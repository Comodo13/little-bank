package korunovacni.dmitri.littlebank.service;


import korunovacni.dmitri.littlebank.domain.dto.AccountDto;
import korunovacni.dmitri.littlebank.exception.RequestFormatException;
import korunovacni.dmitri.littlebank.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;


public interface AccountService {
    ResponseEntity<AccountDto> createAccount(AccountDto dto) throws RequestFormatException, ResourceNotFoundException;

    ResponseEntity<AccountDto> editAccountById(Long id, AccountDto dto) throws RequestFormatException, ResourceNotFoundException;

    void deleteAccountById(Long id) throws RequestFormatException, ResourceNotFoundException;

    ResponseEntity<AccountDto> viewSummary(Long id) throws RequestFormatException, ResourceNotFoundException;
}
