package korunovacni.dmitri.littlebank.controller;

import korunovacni.dmitri.littlebank.domain.dto.AccountDto;
import korunovacni.dmitri.littlebank.exception.RequestFormatException;
import korunovacni.dmitri.littlebank.exception.ResourceNotFoundException;
import korunovacni.dmitri.littlebank.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/accounts")
@RestController
@AllArgsConstructor
public class AccountController {

    @Autowired
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto dto) throws RequestFormatException, ResourceNotFoundException {
        return accountService.createAccount(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> editAccountById(@PathVariable Long id, @RequestBody AccountDto dto)
            throws RequestFormatException, ResourceNotFoundException {
        return accountService.editAccountById(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteAccountById(@PathVariable Long id) throws RequestFormatException, ResourceNotFoundException {
        accountService.deleteAccountById(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> viewSummary(@PathVariable Long id) throws RequestFormatException, ResourceNotFoundException {
        return accountService.viewSummary(id);
    }
}
