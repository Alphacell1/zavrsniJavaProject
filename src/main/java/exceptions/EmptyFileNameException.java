package exceptions;

public class EmptyFileNameException extends Exception{
    public EmptyFileNameException(String message) {
        super(message);
    }

    public EmptyFileNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyFileNameException(Throwable cause) {
        super(cause);
    }
}
