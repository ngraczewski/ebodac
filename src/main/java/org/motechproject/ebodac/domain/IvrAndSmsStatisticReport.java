package org.motechproject.ebodac.domain;

import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.NonEditable;
import org.motechproject.mds.util.SecurityMode;

import javax.jdo.annotations.Unique;

@Access(value = SecurityMode.PERMISSIONS, members = {"manageEbodac"})
@Entity(nonEditable = true)
public class IvrAndSmsStatisticReport {

    @Unique
    @Field
    private String providerCallId;

    @Field(displayName = "Participant")
    private Subject subject;

    @Field
    private String messageId;

    @Field(displayName = "Sent Date")
    private DateTime sendDate;

    @Field
    private double expectedDuration;

    @Field
    private double timeListenedTo;

    @Field
    private double messagePercentListened;

    @Field
    private DateTime receivedDate;

    @Field
    private int numberOfAttempts;

    @Field
    private boolean sms;

    @Field
    private DateTime smsReceivedDate;

    @NonEditable(display = false)
    @Field
    private String owner;

    public IvrAndSmsStatisticReport(String providerCallId, Subject subject, String messageId, DateTime sendDate, double expectedDuration, double timeListenedTo,
                                    double messagePercentListened, DateTime receivedDate, int numberOfAttempts, boolean sms, DateTime smsReceivedDate) {
        this.providerCallId = providerCallId;
        this.subject = subject;
        this.messageId = messageId;
        this.sendDate = sendDate;
        this.expectedDuration = expectedDuration;
        this.timeListenedTo = timeListenedTo;
        this.messagePercentListened = messagePercentListened;
        this.receivedDate = receivedDate;
        this.numberOfAttempts = numberOfAttempts;
        this.sms = sms;
        this.smsReceivedDate = smsReceivedDate;
    }

    public String getProviderCallId() {
        return providerCallId;
    }

    public void setProviderCallId(String providerCallId) {
        this.providerCallId = providerCallId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public DateTime getSendDate() {
        return sendDate;
    }

    public void setSendDate(DateTime sendDate) {
        this.sendDate = sendDate;
    }

    public double getExpectedDuration() {
        return expectedDuration;
    }

    public void setExpectedDuration(double expectedDuration) {
        this.expectedDuration = expectedDuration;
    }

    public double getTimeListenedTo() {
        return timeListenedTo;
    }

    public void setTimeListenedTo(double timeListenedTo) {
        this.timeListenedTo = timeListenedTo;
    }

    public double getMessagePercentListened() {
        return messagePercentListened;
    }

    public void setMessagePercentListened(double messagePercentListened) {
        this.messagePercentListened = messagePercentListened;
    }

    public DateTime getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(DateTime receivedDate) {
        this.receivedDate = receivedDate;
    }

    public int getNumberOfAttempts() {
        return numberOfAttempts;
    }

    public void setNumberOfAttempts(int numberOfAttempts) {
        this.numberOfAttempts = numberOfAttempts;
    }

    public boolean getSms() {
        return sms;
    }

    public void setSms(boolean sms) {
        this.sms = sms;
    }

    public DateTime getSmsReceivedDate() {
        return smsReceivedDate;
    }

    public void setSmsReceivedDate(DateTime smsReceivedDate) {
        this.smsReceivedDate = smsReceivedDate;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void updateReportData(String providerCallId, Subject subject, String messageId, DateTime sendDate, double expectedDuration, double timeListenedTo,
                                 double messagePercentListened, DateTime receivedDate, int numberOfAttempts, boolean sms, DateTime smsReceivedDate) {
        this.providerCallId = providerCallId;
        this.subject = subject;
        this.messageId = messageId;
        this.sendDate = sendDate;
        this.expectedDuration = expectedDuration;
        this.timeListenedTo = timeListenedTo;
        this.messagePercentListened = messagePercentListened;
        this.receivedDate = receivedDate;
        this.numberOfAttempts = numberOfAttempts;
        this.sms = sms;
        this.smsReceivedDate = smsReceivedDate;
    }
}
