package org.motechproject.ebodac.exception;

public class EbodacExportException extends RuntimeException {

    public EbodacExportException(String message, Throwable cause) {
        super(message, cause);
    }

    public EbodacExportException(String message) {
        super(message);
    }
}
