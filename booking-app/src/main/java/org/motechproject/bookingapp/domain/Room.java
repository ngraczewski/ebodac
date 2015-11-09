package org.motechproject.bookingapp.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsEntity;

@Entity
public class Room extends MdsEntity {

    @Field(required = true)
    private String number;

    @Field(defaultValue = "1")
    private Integer maxPatients;

}
