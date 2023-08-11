package korunovacni.dmitri.littlebank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = RequestFormatException.class)
    public ResponseEntity<Object> handleRequestFormatException(RequestFormatException e) {

        CustomExceptionBody customExceptionBody = new CustomExceptionBody(e.getMessage(), ZonedDateTime.now());
        return new ResponseEntity<>(customExceptionBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = CreditorsBalanceIsTooLowException.class)
    public ResponseEntity<Object> handleCreditorsBalanceIsTooLowException(CreditorsBalanceIsTooLowException e) {

        CustomExceptionBody customExceptionBody = new CustomExceptionBody(e.getMessage(), ZonedDateTime.now());
        return new ResponseEntity<>(customExceptionBody, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException e) {

        CustomExceptionBody customExceptionBody = new CustomExceptionBody(e.getMessage(), ZonedDateTime.now());
        return new ResponseEntity<>(customExceptionBody, HttpStatus.NOT_FOUND);
    }

}
