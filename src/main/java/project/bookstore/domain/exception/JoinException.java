package project.bookstore.domain.exception;

public class JoinException extends RuntimeException {
    public JoinException() {
        super();
    }

    public JoinException(String message) {
        super(message);
    }

    public JoinException(String message, Throwable cause) {
        super(message, cause);
    }

    public JoinException(Throwable cause) {
        super(cause);
    }
}
