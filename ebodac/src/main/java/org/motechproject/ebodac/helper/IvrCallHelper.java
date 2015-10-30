package org.motechproject.ebodac.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.domain.Enrollment;
import org.motechproject.ebodac.domain.EnrollmentStatus;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.repository.EnrollmentDataService;
import org.motechproject.ebodac.repository.VotoLanguageDataService;
import org.motechproject.ebodac.repository.VotoMessageDataService;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.ivr.service.OutboundCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class IvrCallHelper {

    @Autowired
    private ConfigService configService;

    @Autowired
    private VotoMessageDataService votoMessageDataService;

    @Autowired
    private VotoLanguageDataService votoLanguageDataService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private EnrollmentDataService enrollmentDataService;

    @Autowired
    private OutboundCallService outboundCallService;

    public void initiateIvrCall(String campaignName, String messageKey, String externalId) {
        Config config = configService.getConfig();

        if (config.getSendIvrCalls() != null && config.getSendIvrCalls()) {
            Subject subject = subjectService.findSubjectBySubjectId(externalId);
            String votoLanguageId = votoLanguageDataService.findVotoLanguageByLanguage(subject.getLanguage()).getVotoId();
            String votoMessageId = votoMessageDataService.findVotoMessageByMessageKey(messageKey).getVotoIvrId();

            JsonObject subscriber = new JsonObject();
            subscriber.addProperty(EbodacConstants.PHONE, subject.getPhoneNumber());
            subscriber.addProperty(EbodacConstants.LANGUAGE, votoLanguageId);

            JsonArray subscriberArray = new JsonArray();
            subscriberArray.add(subscriber);

            Gson gson = new GsonBuilder().serializeNulls().create();
            String subscribers = gson.toJson(subscriberArray);

            Enrollment enrollment = enrollmentDataService.findEnrollmentBySubjectIdAndCampaignName(externalId, campaignName);
            StringBuilder subjectIds = new StringBuilder(enrollment.getExternalId());

            if (enrollment.getDuplicatedEnrollments() != null) {
                for (Enrollment e : enrollment.getDuplicatedEnrollments()) {
                    if (EnrollmentStatus.ENROLLED.equals(e.getStatus())) {
                        subjectIds.append(",");
                        subjectIds.append(e.getExternalId());
                    }
                }
            }

            Map<String, String> callParams = new HashMap<>();
            callParams.put(EbodacConstants.API_KEY, config.getApiKey());
            callParams.put(EbodacConstants.MESSAGE_ID, votoMessageId);
            callParams.put(EbodacConstants.STATUS_CALLBACK_URL, config.getStatusCallbackUrl());
            callParams.put(EbodacConstants.SUBSCRIBERS, subscribers);
            callParams.put(EbodacConstants.SEND_SMS_IF_VOICE_FAILS, config.getSendSmsIfVoiceFails() ? "1" : "0");
            callParams.put(EbodacConstants.DETECT_VOICEMAIL, config.getDetectVoiceMail() ? "1" : "0");
            callParams.put(EbodacConstants.RETRY_ATTEMPTS_SHORT, config.getRetryAttempts().toString());
            callParams.put(EbodacConstants.RETRY_DELAY_SHORT, config.getRetryDelay().toString());
            callParams.put(EbodacConstants.RETRY_ATTEMPTS_LONG, EbodacConstants.RETRY_ATTEMPTS_LONG_DEFAULT);
            callParams.put(EbodacConstants.SUBJECT_IDS, subjectIds.toString());
            callParams.put(EbodacConstants.SUBJECT_PHONE_NUMBER, subject.getPhoneNumber());

            outboundCallService.initiateCall(config.getIvrSettingsName(), callParams);
        }
    }
}
