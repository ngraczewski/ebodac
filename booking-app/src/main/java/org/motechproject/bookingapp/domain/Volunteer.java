package org.motechproject.bookingapp.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsEntity;

@Entity
public class Volunteer extends MdsEntity {

    @Field(required = true)
    private String name;

    public Volunteer() {
    }

    public Volunteer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
