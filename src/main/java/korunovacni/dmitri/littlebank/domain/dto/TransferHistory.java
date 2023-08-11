package korunovacni.dmitri.littlebank.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Container for separating outcome and income transfers
 */
@Getter
@Setter
@AllArgsConstructor
public class TransferHistory {
    private List<TransferDto> outcomeTransfers;
    private List<TransferDto> incomeTransfers;
}
