package org.motechproject.ebodac.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.ebodac.util.CustomDateDeserializer;
import org.motechproject.ebodac.util.CustomDateSerializer;
import org.motechproject.ebodac.util.CustomEnrollmentStatusSerializer;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Ignore;
import org.motechproject.mds.annotations.NonEditable;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollment;

import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Unique;
import java.util.HashSet;
import java.util.Set;

@Entity(nonEditable = true, maxFetchDepth = 1)
@Unique(name = "externalIdAndCampaignName", members = {"externalId", "campaignName" })
public class Enrollment {

    @Field(required = true)
    private String externalId;

    @Field(required = true)
    private String campaignName;

    @Field
    private EnrollmentStatus status;

    @Field
    private EnrollmentStatus previousStatus;

    @Field
    private LocalDate referenceDate;

    @Field
    private Time deliverTime;

    @NonEditable
    @Field
    private Enrollment parentEnrollment;

    @NonEditable
    @Field
    @Persistent(mappedBy = "parentEnrollment")
    private Set<Enrollment> duplicatedEnrollments = new HashSet<>();

    @NonEditable(display = false)
    @Field
    private String owner;

    private Enrollment() {
    }

    public Enrollment(String externalId, String campaignName) {
        this.externalId = externalId;
        this.campaignName = campaignName;
        this.status = EnrollmentStatus.ENROLLED;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    @JsonSerialize(using = CustomEnrollmentStatusSerializer.class)
    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        if (status != null && !status.equals(this.status)) {
            previousStatus = this.status;
        }
        this.status = status;
    }

    @JsonSerialize(using = CustomEnrollmentStatusSerializer.class)
    public EnrollmentStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(EnrollmentStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
    }

    public Time getDeliverTime() {
        return deliverTime;
    }

    public void setDeliverTime(Time deliverTime) {
        this.deliverTime = deliverTime;
    }

    @JsonIgnore
    public Enrollment getParentEnrollment() {
        return parentEnrollment;
    }

    public void setParentEnrollment(Enrollment parentEnrollment) {
        this.parentEnrollment = parentEnrollment;
    }

    @JsonIgnore
    public Set<Enrollment> getDuplicatedEnrollments() {
        if (duplicatedEnrollments == null) {
            duplicatedEnrollments = new HashSet<>();
        }
        return duplicatedEnrollments;
    }

    public void setDuplicatedEnrollments(Set<Enrollment> duplicatedEnrollments) {
        this.duplicatedEnrollments = duplicatedEnrollments;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Enrollment)) {
            return false;
        }

        Enrollment that = (Enrollment) o;

        return externalId.equals(that.externalId) && campaignName.equals(that.campaignName);

    }

    @Override
    public int hashCode() {
        int result = externalId.hashCode();
        result = 31 * result + campaignName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return externalId + " - " + campaignName + " - " + status;
    }

    public CampaignEnrollment toCampaignEnrollment() {
        CampaignEnrollment enrollment = new CampaignEnrollment(externalId, campaignName);
        enrollment.setDeliverTime(deliverTime);
        enrollment.setReferenceDate(referenceDate);

        return enrollment;
    }

    @Ignore
    public void addDuplicatedEnrollment(Enrollment enrollment) {
        getDuplicatedEnrollments().add(enrollment);
    }

    @Ignore
    public boolean hasDuplicatedEnrollments() {
        return !getDuplicatedEnrollments().isEmpty();
    }
}
