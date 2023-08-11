package korunovacni.dmitri.littlebank.service.impl;

import korunovacni.dmitri.littlebank.domain.Account;
import korunovacni.dmitri.littlebank.domain.Transfer;
import korunovacni.dmitri.littlebank.domain.dto.TransferDto;
import korunovacni.dmitri.littlebank.domain.dto.TransferHistory;
import korunovacni.dmitri.littlebank.domain.dto.converter.TransferConverter;
import korunovacni.dmitri.littlebank.exception.CreditorsBalanceIsTooLowException;
import korunovacni.dmitri.littlebank.exception.RequestFormatException;
import korunovacni.dmitri.littlebank.exception.ResourceNotFoundException;
import korunovacni.dmitri.littlebank.repo.AccountRepository;
import korunovacni.dmitri.littlebank.repo.TransferRepository;
import korunovacni.dmitri.littlebank.service.TransferService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TransferServiceImpl implements TransferService {

    @Autowired
    private final TransferRepository transferRepository;
    @Autowired
    private final AccountRepository accountRepository;

    private final Logger logger = LoggerFactory.getLogger(TransferServiceImpl.class);

    private final static Map<Currency, BigDecimal> rateToEur;

    /**
     *  Currency rates stored in Map just for convenience of not having external libraries
     */
    static {
        rateToEur = new HashMap<>();
        rateToEur.put(Currency.getInstance("EUR"), BigDecimal.valueOf(1));
        rateToEur.put(Currency.getInstance("CAD"), BigDecimal.valueOf(1.424397));
        rateToEur.put(Currency.getInstance("CZK"), BigDecimal.valueOf(25.430142));
        rateToEur.put(Currency.getInstance("HKD"), BigDecimal.valueOf(8.784294));
        rateToEur.put(Currency.getInstance("LVL"), BigDecimal.valueOf(0.681942));
        rateToEur.put(Currency.getInstance("PLN"), BigDecimal.valueOf(4.658117));
        rateToEur.put(Currency.getInstance("USD"), BigDecimal.valueOf(1.13075));
        rateToEur.put(Currency.getInstance("UAH"), BigDecimal.valueOf(29.886991));
        rateToEur.put(Currency.getInstance("JPY"), BigDecimal.valueOf(129.817495));
        rateToEur.put(Currency.getInstance("RUB"), BigDecimal.valueOf(82.174453));
        rateToEur.put(Currency.getInstance("GBP"), BigDecimal.valueOf(0.841691));
    }

    /**
     * Validate IBANs, creditor's balance and create Transfer object.
     *
     * @param transferDto inbound dto
     * @return Dto representation of Transfer wrapped in ResponseEntity
     * @throws RequestFormatException
     */
    @Override
    @Transactional
    public ResponseEntity<TransferDto> processTransaction(TransferDto transferDto)
            throws RequestFormatException, CreditorsBalanceIsTooLowException, ResourceNotFoundException {
        if (transferDto.getCreditorIBAN() == null ||
                transferDto.getDebtorIBAN() == null ||
                transferDto.getCreditorIBAN().length() == 0 ||
                transferDto.getDebtorIBAN().length() == 0) {
            throw new RequestFormatException("IBAN can't be null");
        }
        logger.info("TransferServiceImpl:processTransaction: invoked");
        String creditorIBAN = transferDto.getCreditorIBAN();
        String debtorIBAN = transferDto.getDebtorIBAN();
        if (creditorIBAN.equals(debtorIBAN)) {
            logger.info("TransferServiceImpl:processTransaction: error IBANs are the same");
            throw new RequestFormatException("Accounts can not be same");
        }
        Optional<Account> creditorOpt = accountRepository.findByIBAN(creditorIBAN);
        Optional<Account> debtorOpt = accountRepository.findByIBAN(debtorIBAN);

        if (creditorOpt.isEmpty()) {
            logger.info("TransferServiceImpl:processTransaction: error couldn't find creditors IBAN: " + debtorIBAN);
            throw new ResourceNotFoundException("Account with IBAN: " + creditorIBAN + " was not found");
        }
        if (debtorOpt.isEmpty()) {
            logger.info("TransferServiceImpl:processTransaction: error couldn't find debtors IBAN: " + debtorIBAN);
            throw new ResourceNotFoundException("Account with IBAN: " + debtorIBAN + " was not found");
        }
        Account creditor = creditorOpt.get();
        Account debtor = debtorOpt.get();

        BigDecimal amount = validateAmount(transferDto);
        if (creditor.getBalance().compareTo(amount) >= 0) {
            creditor.setBalance(creditor.getBalance().subtract(amount));
            debtor.setBalance(debtor.getBalance().add(exchangeCurrency(creditor.getCurrency(), debtor.getCurrency(), amount)));
        } else {
            logger.info("TransferServiceImpl:processTransaction: error: creditor's balance: " + creditor.getBalance() +
                    " is lower than amount: " + amount);
            throw new CreditorsBalanceIsTooLowException("Insufficient Funds");
        }

        Transfer transfer = new Transfer();
        transfer.setAmount(amount);
        transfer.setDate(LocalDateTime.now());
        transfer.setCreditorIBAN(transferDto.getCreditorIBAN());
        transfer.setDebtorIBAN(transferDto.getDebtorIBAN());
        transfer.setMessage(transferDto.getMessage().trim());
        transferRepository.save(transfer);

        logger.info("TransferServiceImpl:processTransaction: transfer was successfully made from IBAN: "
                + creditorIBAN + " to IBAN: " + debtorIBAN);
        TransferDto dto = TransferConverter.toDto(transfer);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Validate, get from dto and convert amount as a text value
     *
     * @param dto inbound dto
     * @return BigDecimal representation of inbound amount
     * @throws RequestFormatException
     */
    public BigDecimal validateAmount(TransferDto dto) throws RequestFormatException {
        BigDecimal big;
        try {
            big = BigDecimal.valueOf(Double.parseDouble(dto.getAmount()));
        } catch (NumberFormatException | NullPointerException nfe) {
            throw new RequestFormatException("Wrong amount format");
        }
        return big;
    }

    @Override
    public ResponseEntity<List<TransferDto>> getTransferHistory() {
        List<Transfer> transactions = transferRepository.findAll();
        List<TransferDto> dtos = transactions
                .stream()
                .map(TransferConverter::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    /**
     * Find Transfers by Amount
     *
     * @param amount inbound text
     * @return List of dto representations of Transfer wrapped in ResponseEntity
     * @throws RequestFormatException
     */
    @Override
    public ResponseEntity<List<TransferDto>> getTransfersByAmount(String amount) throws RequestFormatException {
        if (amount == null || amount.length() == 0) {
            throw new RequestFormatException("Amount field is empty");
        }
        BigDecimal amountBig;
        try {
            amountBig = BigDecimal.valueOf(Double.parseDouble(amount));
        } catch (NumberFormatException | NullPointerException nfe) {
            throw new RequestFormatException("Wrong amount format");
        }
        List<Transfer> transfers = transferRepository.findByAmount(amountBig);
        List<TransferDto> dtos = transfers
                .stream()
                .map(TransferConverter::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    /**
     * Find Transfer by id
     *
     * @param id inbound Long
     * @return dto representation of Transfer wrapped in ResponseEntity
     * @throws RequestFormatException
     */
    @Override
    public ResponseEntity<TransferDto> getTransferById(Long id) throws RequestFormatException, ResourceNotFoundException {
        if (id == null || id == 0L) {
            throw new RequestFormatException("Transfer Id can't be null or zero");
        }
        Optional<Transfer> transferOpt = transferRepository.findById(id);
        if (transferOpt.isEmpty()) {
            throw new ResourceNotFoundException("Transfer with Id: " + id + " was not found");
        }
        TransferDto dto = TransferConverter.toDto(transferOpt.get());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Find Transfers by IBAN
     *
     * @param IBAN inbound text
     * @return TransferHistory container with 2 Lists of dto representations of Transfer wrapped in ResponseEntity
     */
    @Override
    public ResponseEntity<TransferHistory> getTransfersByIBAN(String IBAN) {
        List<TransferDto> debtor = getTransfersByDebtorIBAN(IBAN);
        List<TransferDto> creditor = getTransfersByCreditorIBAN(IBAN);
        TransferHistory history = new TransferHistory(creditor, debtor);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    public List<TransferDto> getTransfersByDebtorIBAN(String IBAN) {
        List<Transfer> transfers = transferRepository.findByDebtorIBAN(IBAN);
        return transfers
                .stream()
                .map(TransferConverter::toDto)
                .collect(Collectors.toList());
    }

    public List<TransferDto> getTransfersByCreditorIBAN(String IBAN) {
        List<Transfer> transfers = transferRepository.findByCreditorIBAN(IBAN);
        return transfers
                .stream()
                .map(TransferConverter::toDto)
                .collect(Collectors.toList());
    }

    /**
     * find Transfers by String message
     *
     * @param message inbound text
     * @return List of dto representations of Transfer wrapped in ResponseEntity
     */
    @Override
    public ResponseEntity<List<TransferDto>> getTransfersByMessage(String message) {
        List<Transfer> transfers = transferRepository.findByMessage(message);
        List<TransferDto> dtos = transfers
                .stream()
                .map(TransferConverter::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    /**
     * Convert currencies through base EUR
     *
     * @param from   Currency of creditors Account
     * @param to     Currency of debtors account
     * @param amount BigDecimal representation of amount
     * @return BigDecimal representation of amount after currency conversion
     * @throws RequestFormatException
     */
    public BigDecimal exchangeCurrency(Currency from, Currency to, BigDecimal amount) throws RequestFormatException {
        if (rateToEur.get(from) == null || rateToEur.get(to) == null) {
            logger.info("TransferServiceImpl:exchangeCurrency:error:this currency doesn't present in currencyRate list");
            throw new RequestFormatException("Unacceptable currency");
        }
        BigDecimal toRate = rateToEur.get(to);
        BigDecimal fromRate = rateToEur.get(from);

        BigDecimal multiplyRate = toRate.divide(fromRate, 3, RoundingMode.CEILING);
        BigDecimal finalAmount = amount.multiply(multiplyRate);
        logger.info("TransferServiceImpl:exchangeCurrency: Successfully converted: " + amount + " " + from.getDisplayName() + " to " + finalAmount + " " + to);
        return finalAmount;
    }
}
