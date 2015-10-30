package org.motechproject.ebodac.exception;

public class EbodacEnrollmentException extends EbodacException {

    public EbodacEnrollmentException(String message, Throwable cause, String messageKey, String... params) {
        super(message, cause, messageKey, params);
    }

    public EbodacEnrollmentException(String message, String messageKey, String... params) {
        super(message, messageKey, params);
    }
}
