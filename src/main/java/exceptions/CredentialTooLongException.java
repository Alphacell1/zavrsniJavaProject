package exceptions;

public class CredentialTooLongException extends RuntimeException{
    public CredentialTooLongException(String message) {
        super(message);
    }

    public CredentialTooLongException(String message, Throwable cause) {
        super(message, cause);
    }

    public CredentialTooLongException(Throwable cause) {
        super(cause);
    }
}
