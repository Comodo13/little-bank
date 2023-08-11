package korunovacni.dmitri.littlebank.domain.dto.converter;

import korunovacni.dmitri.littlebank.domain.Transfer;
import korunovacni.dmitri.littlebank.domain.dto.TransferDto;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class TransferConverter {


    public static TransferDto toDto(Transfer transfer) {
        return TransferDto.builder()
                .amount(transfer.getAmount().toString())
                .creditorIBAN(transfer.getCreditorIBAN())
                .debtorIBAN(transfer.getDebtorIBAN())
                .message(transfer.getMessage())
                .build();
    }
}
