package korunovacni.dmitri.littlebank.exception;

public class CreditorsBalanceIsTooLowException extends Exception {

    public CreditorsBalanceIsTooLowException(String message) {
        super(message);
    }

    public CreditorsBalanceIsTooLowException(String message, Throwable cause) {
        super(message, cause);
    }
}
