package korunovacni.dmitri.littlebank.service.impl;

import korunovacni.dmitri.littlebank.domain.Account;
import korunovacni.dmitri.littlebank.domain.Transfer;
import korunovacni.dmitri.littlebank.domain.dto.TransferDto;
import korunovacni.dmitri.littlebank.domain.dto.converter.TransferConverter;
import korunovacni.dmitri.littlebank.exception.CreditorsBalanceIsTooLowException;
import korunovacni.dmitri.littlebank.exception.RequestFormatException;
import korunovacni.dmitri.littlebank.exception.ResourceNotFoundException;
import korunovacni.dmitri.littlebank.repo.AccountRepository;
import korunovacni.dmitri.littlebank.repo.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @InjectMocks
    TransferServiceImpl transferServiceImpl;
    @Mock
    AccountRepository accountRepository;
    @Mock
    TransferRepository transferRepository;

    TransferDto transferDto;

    @BeforeEach
    public void setUp() {
        transferDto = TransferDto
                .builder()
                .creditorIBAN("NL62INGB26564673499")
                .debtorIBAN("NL62INGB2650400811")
                .amount("100")
                .message("to friend")
                .build();
    }
    @Test
    void processTransaction() throws RequestFormatException, CreditorsBalanceIsTooLowException, ResourceNotFoundException {

        Account creditorAccount = new Account();
        creditorAccount.setId(2L);
        creditorAccount.setIBAN("NL62INGB26564673499");
        creditorAccount.setCurrency(Currency.getInstance("EUR"));
        creditorAccount.setBalance(BigDecimal.valueOf(1000));

        Account debtorAccount = new Account();
        debtorAccount.setId(1L);
        debtorAccount.setIBAN("NL62INGB2650400811");
        debtorAccount.setCurrency(Currency.getInstance("USD"));
        debtorAccount.setBalance(BigDecimal.valueOf(1000));

        Mockito.when(accountRepository.findByIBAN("NL62INGB26564673499")).thenReturn(Optional.of(creditorAccount));
        Mockito.when(accountRepository.findByIBAN("NL62INGB2650400811")).thenReturn(Optional.of(debtorAccount));

        assertEquals(Objects.requireNonNull(transferServiceImpl.processTransaction(transferDto).getBody()).getMessage(),"to friend");
        assertEquals(Objects.requireNonNull(transferServiceImpl.processTransaction(transferDto).getBody()).getCreditorIBAN(),"NL62INGB26564673499");
        assertEquals(Objects.requireNonNull(transferServiceImpl.processTransaction(transferDto).getBody()).getDebtorIBAN(),"NL62INGB2650400811");

    }

    @Test
    void exeptionThrownIfBalanceIsLowerThanAmount(){

        Account creditorAccount = new Account();
        creditorAccount.setId(2L);
        creditorAccount.setIBAN("NL62INGB26564673499");
        creditorAccount.setCurrency(Currency.getInstance("EUR"));
        creditorAccount.setBalance(BigDecimal.valueOf(50));

        Account debtorAccount = new Account();
        debtorAccount.setId(1L);
        debtorAccount.setIBAN("NL62INGB2650400811");
        debtorAccount.setCurrency(Currency.getInstance("USD"));
        debtorAccount.setBalance(BigDecimal.valueOf(1000));

        Mockito.when(accountRepository.findByIBAN("NL62INGB26564673499")).thenReturn(Optional.of(creditorAccount));
        Mockito.when(accountRepository.findByIBAN("NL62INGB2650400811")).thenReturn(Optional.of(debtorAccount));

        CreditorsBalanceIsTooLowException thrown = assertThrows(CreditorsBalanceIsTooLowException.class,
                ()->transferServiceImpl.processTransaction(transferDto));

        assertTrue(thrown.getMessage().contains("Insufficient Funds"));
    }

    @Test
    void validateAmount() {
    }

    @Test
    void exchangeCurrency() {



//
//        public BigDecimal exchangeCurrency(Currency from, Currency to, BigDecimal amount) throws RequestFormatException {
//            if (rateToEur.get(from) == null || rateToEur.get(to) == null) {
//                logger.info("TransferServiceImpl:exchangeCurrency:error:this currency doesn't present in currencyRate list");
//                throw new RequestFormatException("Unacceptable currency");
//            }
//            BigDecimal toRate = rateToEur.get(to);
//            BigDecimal fromRate = rateToEur.get(from);
//
//            BigDecimal multiplyRate = toRate.divide(fromRate, 3, RoundingMode.CEILING);
//            BigDecimal finalAmount = amount.multiply(multiplyRate);
//            logger.info("TransferServiceImpl:exchangeCurrency: Successfully converted: "+ amount+" "+from.getDisplayName()+" to "+finalAmount+" "+to);
//            return finalAmount;
    }
}