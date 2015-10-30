package org.motechproject.ebodac.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.NonEditable;

@Entity
public class VotoMessage {

    @Field
    private String votoIvrId;

    @Field
    private String votoSmsId;

    @Field
    private String messageKey;

    @NonEditable(display = false)
    @Field
    private String owner;

    public String getVotoIvrId() {
        return votoIvrId;
    }

    public void setVotoIvrId(String votoIvrId) {
        this.votoIvrId = votoIvrId;
    }

    public String getVotoSmsId() {
        return votoSmsId;
    }

    public void setVotoSmsId(String votoSmsId) {
        this.votoSmsId = votoSmsId;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
