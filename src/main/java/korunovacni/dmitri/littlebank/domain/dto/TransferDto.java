package korunovacni.dmitri.littlebank.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;


@Getter
@AllArgsConstructor
@Builder
@Value
public class TransferDto {

    String amount;
    String debtorIBAN;
    String creditorIBAN;
    String message;
}
