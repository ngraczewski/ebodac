package org.motechproject.ebodac.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.NonEditable;

@Entity
public class VotoLanguage {

    @Field
    private String votoId;

    @Field
    private Language language;

    @NonEditable(display = false)
    @Field
    private String owner;

    public String getVotoId() {
        return votoId;
    }

    public void setVotoId(String votoId) {
        this.votoId = votoId;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
