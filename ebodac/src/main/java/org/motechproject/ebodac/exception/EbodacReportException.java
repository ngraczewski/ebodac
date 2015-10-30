package org.motechproject.ebodac.exception;

public class EbodacReportException extends EbodacException {

    public EbodacReportException(String message, Throwable cause, String messageKey, String... params) {
        super(message, cause, messageKey, params);
    }

    public EbodacReportException(String message, String messageKey, String... params) {
        super(message, messageKey, params);
    }
}
