package pl.bankapp.exception;

public class OutgoingTransactionFailedException extends RuntimeException {
    /**
     * Feature 17 - przelewy przez API
     */
    public OutgoingTransactionFailedException(String message) {
        super(message);
    }
}
