package korunovacni.dmitri.littlebank.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.util.Date;

@Getter
@Builder
@Value
public class CustomerDto {
    String name;
    String surname;
    String sex;
    String nationality;
    Date dateOfBirth;
    Long cardNumber;
    Date cardDateOfIssue;
    Date cardDateOfExpiry;
}
