package korunovacni.dmitri.littlebank.service.impl;

import korunovacni.dmitri.littlebank.domain.Customer;
import korunovacni.dmitri.littlebank.domain.dto.AccountDto;
import korunovacni.dmitri.littlebank.exception.RequestFormatException;
import korunovacni.dmitri.littlebank.exception.ResourceNotFoundException;
import korunovacni.dmitri.littlebank.repo.AccountRepository;
import korunovacni.dmitri.littlebank.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Currency;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @InjectMocks
    AccountServiceImpl accountService;

    @Mock
    AccountRepository accountRepository;
    @Mock
    CustomerService customerService;

    AccountDto dto;

    @BeforeEach
    public void setUp() {
        dto = AccountDto
                .builder()
                .customerId(1)
                .IBAN("NL62INGB2650400811")
                .currency("EUR")
                .build();
    }

    @Test
    void validateIBAN() throws RequestFormatException {

        assertEquals(accountService.validateIBAN(dto), dto.getIBAN());
    }
    @Test
    void validateCurrency() throws RequestFormatException {

        assertEquals(accountService.validateCurrency(dto), Currency.getInstance("EUR"));
    }

    @Test
    void createAccount() throws RequestFormatException, ResourceNotFoundException {

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John");
        customer.setSurname("doe");
        customer.setSex("male");
        customer.setCardNumber(124675478383L);

        Mockito.when(customerService.getById((long) dto.getCustomerId())).thenReturn(customer);

        assertEquals(Objects.requireNonNull(accountService.createAccount(dto).getBody()).getCustomerId(), dto.getCustomerId());
        assertEquals(Objects.requireNonNull(accountService.createAccount(dto).getBody()).getBalance(), "1000");
    }

}