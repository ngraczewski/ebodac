package org.motechproject.ebodac.exception;

public class EbodacLookupException extends RuntimeException {

    public EbodacLookupException(String message, Throwable cause) {
        super(message, cause);
    }

    public EbodacLookupException(String message) {
        super(message);
    }
}
