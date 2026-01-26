package pl.bankapp.exception;

public class OutgoingTransactionFailedException extends RuntimeException {
    public OutgoingTransactionFailedException(String message) {
        super(message);
    }
}
