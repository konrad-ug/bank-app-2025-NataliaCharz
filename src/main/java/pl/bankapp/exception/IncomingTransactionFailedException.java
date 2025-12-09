package pl.bankapp.exception;

public class IncomingTransactionFailedException extends RuntimeException {
    public IncomingTransactionFailedException(String message) {
        super(message);
    }
}
