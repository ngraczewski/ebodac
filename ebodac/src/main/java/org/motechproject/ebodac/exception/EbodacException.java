package org.motechproject.ebodac.exception;

import org.apache.commons.lang.StringUtils;

public class EbodacException extends RuntimeException {

    private String messageKey;
    private String params;

    public EbodacException(String message, Throwable cause, String messageKey, String... params) {
        this(String.format(message, params), cause);
        this.messageKey = messageKey;
        this.params = StringUtils.join(params, ',');
    }

    public EbodacException(String message, String messageKey, String... params) {
        this(String.format(message, params));
        this.messageKey = messageKey;
        this.params = StringUtils.join(params, ',');
    }

    public EbodacException(String message, Throwable cause) {
        super(message, cause);
    }

    public EbodacException(String message) {
        super(message);
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
