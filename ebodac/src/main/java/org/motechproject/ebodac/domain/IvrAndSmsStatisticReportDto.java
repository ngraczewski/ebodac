package org.motechproject.ebodac.domain;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.motechproject.ebodac.util.CustomDateTimeSerializer;
import org.motechproject.ebodac.util.CustomSubjectSerializer;

@JsonAutoDetect
public class IvrAndSmsStatisticReportDto {

    @JsonProperty
    private Subject subject;

    @JsonProperty
    private int age;

    @JsonProperty
    private String messageId;

    @JsonProperty
    private DateTime sendDate;

    @JsonProperty
    private double expectedDuration;

    @JsonProperty
    private double timeListenedTo;

    @JsonProperty
    private DateTime receivedDate;

    @JsonProperty
    private int numberOfAttempts;

    @JsonProperty
    private String sms;

    @JsonProperty
    private DateTime smsReceivedDate;

    public IvrAndSmsStatisticReportDto(IvrAndSmsStatisticReport ivrAndSmsStatisticReport) {
        subject = ivrAndSmsStatisticReport.getSubject();
        if (subject.getDateOfBirth() == null) {
            age = 0;
        } else {
            age = Years.yearsBetween(subject.getDateOfBirth(), LocalDate.now()).getYears();
        }
        messageId = ivrAndSmsStatisticReport.getMessageId();
        sendDate = ivrAndSmsStatisticReport.getSendDate();
        expectedDuration = ivrAndSmsStatisticReport.getExpectedDuration();
        timeListenedTo = ivrAndSmsStatisticReport.getTimeListenedTo();
        receivedDate = ivrAndSmsStatisticReport.getReceivedDate();
        numberOfAttempts = ivrAndSmsStatisticReport.getNumberOfAttempts();
        if (ivrAndSmsStatisticReport.getSms()) {
            sms = "YES";
        } else {
            sms = "NO";
        }
        smsReceivedDate = ivrAndSmsStatisticReport.getSmsReceivedDate();
    }

    @JsonSerialize(using = CustomSubjectSerializer.class)
    public Subject getSubject() {
        return subject;
    }

    public int getAge() {
        return age;
    }

    public String getMessageId() {
        return messageId;
    }

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    public DateTime getSendDate() {
        return sendDate;
    }

    public double getExpectedDuration() {
        return expectedDuration;
    }

    public double getTimeListenedTo() {
        return timeListenedTo;
    }

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    public DateTime getReceivedDate() {
        return receivedDate;
    }

    public int getNumberOfAttempts() {
        return numberOfAttempts;
    }

    public String getSms() {
        return sms;
    }

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    public DateTime getSmsReceivedDate() {
        return smsReceivedDate;
    }
}
