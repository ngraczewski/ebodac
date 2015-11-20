package org.motechproject.bookingapp.exception;

public class LimitationExceededException extends IllegalArgumentException {

    public LimitationExceededException(String message) {
        super(message);
    }

    public LimitationExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
