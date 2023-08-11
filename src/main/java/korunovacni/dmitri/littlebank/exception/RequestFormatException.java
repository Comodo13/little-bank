package korunovacni.dmitri.littlebank.exception;

public class RequestFormatException extends Exception {

    public RequestFormatException(String message) {
        super(message);
    }

    public RequestFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
