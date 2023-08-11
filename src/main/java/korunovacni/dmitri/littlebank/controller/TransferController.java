package korunovacni.dmitri.littlebank.controller;

import korunovacni.dmitri.littlebank.domain.dto.TransferDto;
import korunovacni.dmitri.littlebank.domain.dto.TransferHistory;
import korunovacni.dmitri.littlebank.exception.CreditorsBalanceIsTooLowException;
import korunovacni.dmitri.littlebank.exception.RequestFormatException;
import korunovacni.dmitri.littlebank.exception.ResourceNotFoundException;
import korunovacni.dmitri.littlebank.service.TransferService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/transactions")
public class TransferController {

    @Autowired
    private final TransferService transactionService;

    @PostMapping
    public ResponseEntity<TransferDto> processTransaction(@RequestBody final TransferDto dto)
            throws RequestFormatException, CreditorsBalanceIsTooLowException, ResourceNotFoundException {
        return transactionService.processTransaction(dto);
    }

    @GetMapping("/")
    public ResponseEntity<List<TransferDto>> getTransferHistory() {
        return transactionService.getTransferHistory();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransferDto> getTransferById(@PathVariable Long id)
            throws RequestFormatException, ResourceNotFoundException {
        return transactionService.getTransferById(id);
    }

    @GetMapping("/iban/{iban}")
    public ResponseEntity<TransferHistory> getTransfersByIban(@PathVariable String iban) {
        return transactionService.getTransfersByIBAN(iban);
    }

    @GetMapping("/search/{by}")
    public ResponseEntity<List<TransferDto>> getTransfersByAmountOrMessage(@PathVariable String by)
            throws RequestFormatException {
        String[] parts = by.split("=");
        String type = parts[0];
        String value = parts[1];
        switch (type) {
            case "amount":
                return transactionService.getTransfersByAmount(value);
            case "message":
                return transactionService.getTransfersByMessage(value);
        }
        return null;

    }
}
