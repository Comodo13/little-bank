package korunovacni.dmitri.littlebank.service.impl;

import korunovacni.dmitri.littlebank.domain.Account;
import korunovacni.dmitri.littlebank.domain.dto.AccountDto;
import korunovacni.dmitri.littlebank.domain.dto.converter.AccountConverter;
import korunovacni.dmitri.littlebank.exception.RequestFormatException;
import korunovacni.dmitri.littlebank.exception.ResourceNotFoundException;
import korunovacni.dmitri.littlebank.repo.AccountRepository;
import korunovacni.dmitri.littlebank.service.AccountService;
import korunovacni.dmitri.littlebank.service.CustomerService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    @Autowired
    private final AccountRepository accountRepository;
    @Autowired
    private final CustomerService customerService;

    private final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final static Set<Currency> currencies;
    private final static BigDecimal startingAmount = BigDecimal.valueOf(1000);

    static {
        currencies = new HashSet<>();
        currencies.add(Currency.getInstance("EUR"));
        currencies.add(Currency.getInstance("CAD"));
        currencies.add(Currency.getInstance("CZK"));
        currencies.add(Currency.getInstance("HKD"));
        currencies.add(Currency.getInstance("LVL"));
        currencies.add(Currency.getInstance("PLN"));
        currencies.add(Currency.getInstance("USD"));
        currencies.add(Currency.getInstance("UAH"));
        currencies.add(Currency.getInstance("JPY"));
        currencies.add(Currency.getInstance("RUB"));
        currencies.add(Currency.getInstance("GBP"));
    }

    /**
     * Validate if account with given IBAN is unique (doesn't already exist)
     *
     * @param IBAN inbound String
     * @return boolean true, if IBAN is unique.
     */
    public boolean isUnique(String IBAN) {
        Optional<Account> creditorOpt = accountRepository.findByIBAN(IBAN);
        return creditorOpt.isEmpty();
    }

    /**
     * Validate IBAN
     *
     * @param request inbound dto
     * @return IBAN in String
     * @throws RequestFormatException
     */
    public String validateIBAN(AccountDto request) throws RequestFormatException {
        if (request.getIBAN() == null || request.getIBAN().length() == 0) {
            throw new RequestFormatException("IBAN can't be null");
        }
        String iban = request.getIBAN();
        if (iban.length() < 8 || iban.length() > 32) {
            throw new RequestFormatException("IBAN length can be only from 8 to 32");
        }
        return iban;
    }

    /**
     * Validate and convert currency as a text value
     *
     * @param request inbound dto
     * @return Currency representation of inbound currency
     * @throws RequestFormatException
     */
    public Currency validateCurrency(AccountDto request) throws RequestFormatException {
        if (request.getCurrency() == null || request.getCurrency().length() == 0) {
            throw new RequestFormatException("Enter currency");
        }
        try {
            return Currency.getInstance(request.getCurrency().toUpperCase());
        } catch (IllegalArgumentException iae) {
            throw new RequestFormatException("Currency doesn't exist");
        }
    }

    /**
     * Validate currency, customer, IBAN and create new Account with starting balance of 1000
     *
     * @param request inbound dto
     * @return AccountDto representation of created Account, wrapped in ResponseEntity
     * @throws RequestFormatException
     */
    @Override
    public ResponseEntity<AccountDto> createAccount(AccountDto request)
            throws RequestFormatException, ResourceNotFoundException {
        logger.info("AccountServiceImpl:createAccount: invoked for customer with id: " + request.getCustomerId());
        Account account = new Account();
        account.setBalance(startingAmount);
        Currency currency = validateCurrency(request);
        if (!currencies.contains(currency)) {
            throw new RequestFormatException("This currency is not supported on our little bank");
        }
        account.setCurrency(currency);
        String IBAN = validateIBAN(request);

        if (isUnique(IBAN)) {
            account.setIBAN(IBAN);
        } else {
            throw new RequestFormatException("Account with IBAN: " + IBAN + " already exists");
        }

        account.setCustomer(customerService.getById((long) request.getCustomerId()));
        accountRepository.save(account);
        AccountDto response = AccountConverter.toDto(account);
        logger.info("AccountServiceImpl:createAccount: account with IBAN: " + IBAN +
                " successfully created for customer with id: " + request.getCustomerId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Check all fields of inbound dto and update account with given only
     *
     * @param id  inbound Long
     * @param dto inbound dto
     * @return AccountDto representation of modified Account, wrapped in ResponseEntity
     * @throws RequestFormatException
     */
    @Override
    public ResponseEntity<AccountDto> editAccountById(Long id, AccountDto dto)
            throws RequestFormatException, ResourceNotFoundException {
        if (id == null || id == 0L) {
            throw new RequestFormatException("Account Id can't be null or 0");
        }
        Optional<Account> accountOpt = accountRepository.findById(id);
        if (accountOpt.isEmpty()) {
            throw new ResourceNotFoundException("Account with Id: " + id + " was not found");
        }
        Account account = accountOpt.get();
        if (dto.getCurrency() != null) {
            account.setCurrency(validateCurrency(dto));
        }
        if (dto.getBalance() != null) {
            account.setBalance(BigDecimal.valueOf(Double.parseDouble(dto.getBalance())));
        }
        if (dto.getIBAN() != null) {
            account.setIBAN(validateIBAN(dto));
        }
        accountRepository.save(account);
        AccountDto response = AccountConverter.toDto(account);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    /**
     * Find and delete account by inbound id
     *
     * @param id inbound Long
     * @throws RequestFormatException
     */
    @Override
    public void deleteAccountById(Long id) throws RequestFormatException, ResourceNotFoundException {
        if (id == null || id == 0L) {
            throw new RequestFormatException("Account Id can't be null or 0");
        }
        if (accountRepository.existsById(id)) {
            accountRepository.deleteById(id);       //not the best option to call repository 2 times to do 1 action
        } else {
            throw new ResourceNotFoundException("Account with Id: " + id + " was not found");
        }
        logger.info("AccountServiceImpl:deleteAccountById: error can't find account with id: " + id);
    }

    /**
     * Find Account by id
     *
     * @param id inbound Long
     * @return Dto representation of Account wrapped in ResponseEntity
     * @throws RequestFormatException
     */
    @Override
    public ResponseEntity<AccountDto> viewSummary(Long id) throws RequestFormatException, ResourceNotFoundException {
        if (id == null || id == 0L) {
            throw new RequestFormatException("Account Id can't be null or zero");
        }
        Optional<Account> accountOpt = accountRepository.findById(id);
        if (accountOpt.isEmpty()) {
            throw new ResourceNotFoundException("Account with Id: " + id + " was not found");
        }
        AccountDto dto = AccountConverter.toDto(accountOpt.get());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}