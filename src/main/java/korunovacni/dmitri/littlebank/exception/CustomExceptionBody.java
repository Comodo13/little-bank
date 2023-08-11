package korunovacni.dmitri.littlebank.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.time.ZonedDateTime;

/**
 * Custom exception payload
 */
@AllArgsConstructor
@Getter
@Setter
@Value
public class CustomExceptionBody {
    String message;
    ZonedDateTime time;
}
