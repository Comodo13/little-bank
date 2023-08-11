package korunovacni.dmitri.littlebank.service;

import korunovacni.dmitri.littlebank.domain.dto.TransferDto;
import korunovacni.dmitri.littlebank.domain.dto.TransferHistory;
import korunovacni.dmitri.littlebank.exception.CreditorsBalanceIsTooLowException;
import korunovacni.dmitri.littlebank.exception.RequestFormatException;
import korunovacni.dmitri.littlebank.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TransferService {
    ResponseEntity<TransferDto> processTransaction(TransferDto transferDto) throws RequestFormatException, CreditorsBalanceIsTooLowException, ResourceNotFoundException;

    ResponseEntity<List<TransferDto>> getTransferHistory();

    ResponseEntity<List<TransferDto>> getTransfersByMessage(String message);

    ResponseEntity<TransferHistory> getTransfersByIBAN(String IBAN);

    ResponseEntity<List<TransferDto>> getTransfersByAmount(String amount) throws RequestFormatException;

    ResponseEntity<TransferDto> getTransferById(Long id) throws RequestFormatException, ResourceNotFoundException;
}
