package korunovacni.dmitri.littlebank.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
@Builder
@Value
public class AccountDto {
    String IBAN;
    String currency;
    int customerId;
    String balance;
}
