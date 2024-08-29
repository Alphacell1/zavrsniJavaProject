package exceptions;

public class NotAFileException extends Exception{
    public NotAFileException(String message) {
        super(message);
    }

    public NotAFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAFileException(Throwable cause) {
        super(cause);
    }
}
