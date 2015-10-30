package org.motechproject.ebodac.helper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.domain.Enrollment;
import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.VotoLanguage;
import org.motechproject.ebodac.domain.VotoMessage;
import org.motechproject.ebodac.repository.EnrollmentDataService;
import org.motechproject.ebodac.repository.VotoLanguageDataService;
import org.motechproject.ebodac.repository.VotoMessageDataService;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.ivr.service.OutboundCallService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(IvrCallHelper.class)
public class IvrCallHelperTest {

    @InjectMocks
    private IvrCallHelper ivrCallHelper = new IvrCallHelper();

    @Mock
    private ConfigService configService;

    @Mock
    private VotoMessageDataService votoMessageDataService;

    @Mock
    private VotoLanguageDataService votoLanguageDataService;

    @Mock
    private SubjectService subjectService;

    @Mock
    private EnrollmentDataService enrollmentDataService;

    @Mock
    private OutboundCallService outboundCallService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldSetOnlyParentEnrollmentProviderIdIfThereAreNoDuplicatedEnrollments() {
        String campaignName = "campaign";
        String messageKey = "message";
        String externalId = "1";

        String subjectPhoneNumber = "123";
        String votoMessageId = "456";
        String votoLanguageId = "789";

        String subscribers = "[{\"phone\":\"123\",\"language\":\"789\"}]";
        String subjectIds = "1";

        Subject subject = new Subject();
        subject.setSubjectId(externalId);
        subject.setPhoneNumber(subjectPhoneNumber);
        subject.setLanguage(Language.English);
        when(subjectService.findSubjectBySubjectId(externalId)).thenReturn(subject);

        VotoLanguage votoLanguage = new VotoLanguage();
        votoLanguage.setLanguage(Language.English);
        votoLanguage.setVotoId(votoLanguageId);
        when(votoLanguageDataService.findVotoLanguageByLanguage(subject.getLanguage())).thenReturn(votoLanguage);

        VotoMessage votoMessage = new VotoMessage();
        votoMessage.setMessageKey(messageKey);
        votoMessage.setVotoIvrId(votoMessageId);
        when(votoMessageDataService.findVotoMessageByMessageKey(messageKey)).thenReturn(votoMessage);

        Enrollment enrollment = new Enrollment(externalId, campaignName);
        when(enrollmentDataService.findEnrollmentBySubjectIdAndCampaignName(externalId, campaignName)).thenReturn(enrollment);

        Config config = new Config();
        config.setSendIvrCalls(true);
        config.setIvrSettingsName("Voto");
        config.setApiKey("apiKey");
        config.setStatusCallbackUrl("url");
        config.setSendSmsIfVoiceFails(true);
        config.setDetectVoiceMail(true);
        config.setRetryAttempts(3);
        config.setRetryDelay(15);
        when(configService.getConfig()).thenReturn(config);

        Map<String, String> callParams = new HashMap<>();
        callParams.put(EbodacConstants.API_KEY, config.getApiKey());
        callParams.put(EbodacConstants.MESSAGE_ID, votoMessageId);
        callParams.put(EbodacConstants.STATUS_CALLBACK_URL, config.getStatusCallbackUrl());
        callParams.put(EbodacConstants.SUBSCRIBERS, subscribers);
        callParams.put(EbodacConstants.SEND_SMS_IF_VOICE_FAILS, "1");
        callParams.put(EbodacConstants.DETECT_VOICEMAIL, "1");
        callParams.put(EbodacConstants.RETRY_ATTEMPTS_SHORT, config.getRetryAttempts().toString());
        callParams.put(EbodacConstants.RETRY_DELAY_SHORT, config.getRetryDelay().toString());
        callParams.put(EbodacConstants.RETRY_ATTEMPTS_LONG, EbodacConstants.RETRY_ATTEMPTS_LONG_DEFAULT);
        callParams.put(EbodacConstants.SUBJECT_IDS, subjectIds);
        callParams.put(EbodacConstants.SUBJECT_PHONE_NUMBER, subjectPhoneNumber);

        ivrCallHelper.initiateIvrCall(campaignName, messageKey, externalId);

        verify(outboundCallService, times(1)).initiateCall(config.getIvrSettingsName(), callParams);
    }

    @Test
    public void shouldAddProvidersIdsForAllDuplicatedEnrollments() {
        String campaignName = "campaign";
        String messageKey = "message";
        String externalId = "1";

        String subjectPhoneNumber = "123";
        String votoMessageId = "456";
        String votoLanguageId = "789";

        String subscribers = "[{\"phone\":\"123\",\"language\":\"789\"}]";
        String subjectIds = "1,2,3,4,5";

        Subject subject = new Subject();
        subject.setSubjectId(externalId);
        subject.setPhoneNumber(subjectPhoneNumber);
        subject.setLanguage(Language.English);
        when(subjectService.findSubjectBySubjectId(externalId)).thenReturn(subject);

        VotoLanguage votoLanguage = new VotoLanguage();
        votoLanguage.setLanguage(Language.English);
        votoLanguage.setVotoId(votoLanguageId);
        when(votoLanguageDataService.findVotoLanguageByLanguage(subject.getLanguage())).thenReturn(votoLanguage);

        VotoMessage votoMessage = new VotoMessage();
        votoMessage.setMessageKey(messageKey);
        votoMessage.setVotoIvrId(votoMessageId);
        when(votoMessageDataService.findVotoMessageByMessageKey(messageKey)).thenReturn(votoMessage);

        Enrollment parent = new Enrollment(externalId, campaignName);

        Set<Enrollment> enrollments = new LinkedHashSet<>();

        Enrollment enrollment = new Enrollment("2", campaignName);
        enrollment.setParentEnrollment(parent);
        enrollments.add(enrollment);

        enrollment = new Enrollment("3", campaignName);
        enrollment.setParentEnrollment(parent);
        enrollments.add(enrollment);

        enrollment = new Enrollment("4", campaignName);
        enrollment.setParentEnrollment(parent);
        enrollments.add(enrollment);

        enrollment = new Enrollment("5", campaignName);
        enrollment.setParentEnrollment(parent);
        enrollments.add(enrollment);

        parent.setDuplicatedEnrollments(enrollments);

        when(enrollmentDataService.findEnrollmentBySubjectIdAndCampaignName(externalId, campaignName)).thenReturn(parent);

        Config config = new Config();
        config.setSendIvrCalls(true);
        config.setIvrSettingsName("Voto");
        config.setApiKey("apiKey");
        config.setStatusCallbackUrl("url");
        config.setSendSmsIfVoiceFails(true);
        config.setDetectVoiceMail(true);
        config.setRetryAttempts(3);
        config.setRetryDelay(15);
        when(configService.getConfig()).thenReturn(config);

        Map<String, String> callParams = new HashMap<>();
        callParams.put(EbodacConstants.API_KEY, config.getApiKey());
        callParams.put(EbodacConstants.MESSAGE_ID, votoMessageId);
        callParams.put(EbodacConstants.STATUS_CALLBACK_URL, config.getStatusCallbackUrl());
        callParams.put(EbodacConstants.SUBSCRIBERS, subscribers);
        callParams.put(EbodacConstants.SEND_SMS_IF_VOICE_FAILS, "1");
        callParams.put(EbodacConstants.DETECT_VOICEMAIL, "1");
        callParams.put(EbodacConstants.RETRY_ATTEMPTS_SHORT, config.getRetryAttempts().toString());
        callParams.put(EbodacConstants.RETRY_DELAY_SHORT, config.getRetryDelay().toString());
        callParams.put(EbodacConstants.RETRY_ATTEMPTS_LONG, EbodacConstants.RETRY_ATTEMPTS_LONG_DEFAULT);
        callParams.put(EbodacConstants.SUBJECT_IDS, subjectIds);
        callParams.put(EbodacConstants.SUBJECT_PHONE_NUMBER, subjectPhoneNumber);

        ivrCallHelper.initiateIvrCall(campaignName, messageKey, externalId);

        verify(outboundCallService, times(1)).initiateCall(config.getIvrSettingsName(), callParams);
    }

    @Test
    public void shouldSetSendSmsIfVoiceFailsAndDetectVoiceMailParams() {
        String campaignName = "campaign";
        String messageKey = "message";
        String externalId = "1";

        String subjectPhoneNumber = "123";
        String votoMessageId = "456";
        String votoLanguageId = "789";

        String subscribers = "[{\"phone\":\"123\",\"language\":\"789\"}]";
        String subjectIds = "1";

        Subject subject = new Subject();
        subject.setSubjectId(externalId);
        subject.setPhoneNumber(subjectPhoneNumber);
        subject.setLanguage(Language.English);
        when(subjectService.findSubjectBySubjectId(externalId)).thenReturn(subject);

        VotoLanguage votoLanguage = new VotoLanguage();
        votoLanguage.setLanguage(Language.English);
        votoLanguage.setVotoId(votoLanguageId);
        when(votoLanguageDataService.findVotoLanguageByLanguage(subject.getLanguage())).thenReturn(votoLanguage);

        VotoMessage votoMessage = new VotoMessage();
        votoMessage.setMessageKey(messageKey);
        votoMessage.setVotoIvrId(votoMessageId);
        when(votoMessageDataService.findVotoMessageByMessageKey(messageKey)).thenReturn(votoMessage);

        Enrollment enrollment = new Enrollment(externalId, campaignName);
        when(enrollmentDataService.findEnrollmentBySubjectIdAndCampaignName(externalId, campaignName)).thenReturn(enrollment);

        Config config = new Config();
        config.setSendIvrCalls(true);
        config.setIvrSettingsName("Voto");
        config.setApiKey("apiKey");
        config.setStatusCallbackUrl("url");
        config.setSendSmsIfVoiceFails(true);
        config.setDetectVoiceMail(true);
        config.setRetryAttempts(3);
        config.setRetryDelay(15);
        when(configService.getConfig()).thenReturn(config);

        Map<String, String> callParams = new HashMap<>();
        callParams.put(EbodacConstants.API_KEY, config.getApiKey());
        callParams.put(EbodacConstants.MESSAGE_ID, votoMessageId);
        callParams.put(EbodacConstants.STATUS_CALLBACK_URL, config.getStatusCallbackUrl());
        callParams.put(EbodacConstants.SUBSCRIBERS, subscribers);
        callParams.put(EbodacConstants.SEND_SMS_IF_VOICE_FAILS, "1");
        callParams.put(EbodacConstants.DETECT_VOICEMAIL, "1");
        callParams.put(EbodacConstants.RETRY_ATTEMPTS_SHORT, config.getRetryAttempts().toString());
        callParams.put(EbodacConstants.RETRY_DELAY_SHORT, config.getRetryDelay().toString());
        callParams.put(EbodacConstants.RETRY_ATTEMPTS_LONG, EbodacConstants.RETRY_ATTEMPTS_LONG_DEFAULT);
        callParams.put(EbodacConstants.SUBJECT_IDS, subjectIds);
        callParams.put(EbodacConstants.SUBJECT_PHONE_NUMBER, subjectPhoneNumber);

        ivrCallHelper.initiateIvrCall(campaignName, messageKey, externalId);
        verify(outboundCallService, times(1)).initiateCall(config.getIvrSettingsName(), callParams);

        config.setSendSmsIfVoiceFails(false);
        callParams.put(EbodacConstants.SEND_SMS_IF_VOICE_FAILS, "0");

        ivrCallHelper.initiateIvrCall(campaignName, messageKey, externalId);
        verify(outboundCallService, times(1)).initiateCall(config.getIvrSettingsName(), callParams);

        config.setDetectVoiceMail(false);
        callParams.put(EbodacConstants.DETECT_VOICEMAIL, "0");

        ivrCallHelper.initiateIvrCall(campaignName, messageKey, externalId);
        verify(outboundCallService, times(1)).initiateCall(config.getIvrSettingsName(), callParams);
    }

    @Test
    public void shouldNotSendIvrCallWhenSendIvrCallsIsFalse() {
        String campaignName = "campaign";
        String messageKey = "message";
        String externalId = "1";

        String subjectPhoneNumber = "123";
        String votoMessageId = "456";
        String votoLanguageId = "789";

        String subscribers = "[{\"phone\":\"123\",\"language\":\"789\"}]";
        String subjectIds = "1";

        Subject subject = new Subject();
        subject.setSubjectId(externalId);
        subject.setPhoneNumber(subjectPhoneNumber);
        subject.setLanguage(Language.English);
        when(subjectService.findSubjectBySubjectId(externalId)).thenReturn(subject);

        VotoLanguage votoLanguage = new VotoLanguage();
        votoLanguage.setLanguage(Language.English);
        votoLanguage.setVotoId(votoLanguageId);
        when(votoLanguageDataService.findVotoLanguageByLanguage(subject.getLanguage())).thenReturn(votoLanguage);

        VotoMessage votoMessage = new VotoMessage();
        votoMessage.setMessageKey(messageKey);
        votoMessage.setVotoIvrId(votoMessageId);
        when(votoMessageDataService.findVotoMessageByMessageKey(messageKey)).thenReturn(votoMessage);

        Enrollment enrollment = new Enrollment(externalId, campaignName);
        when(enrollmentDataService.findEnrollmentBySubjectIdAndCampaignName(externalId, campaignName)).thenReturn(enrollment);

        Config config = new Config();
        config.setSendIvrCalls(false);
        config.setIvrSettingsName("Voto");
        config.setApiKey("apiKey");
        config.setStatusCallbackUrl("url");
        config.setSendSmsIfVoiceFails(true);
        config.setDetectVoiceMail(true);
        config.setRetryAttempts(3);
        config.setRetryDelay(15);
        when(configService.getConfig()).thenReturn(config);

        Map<String, String> callParams = new HashMap<>();
        callParams.put(EbodacConstants.API_KEY, config.getApiKey());
        callParams.put(EbodacConstants.MESSAGE_ID, votoMessageId);
        callParams.put(EbodacConstants.STATUS_CALLBACK_URL, config.getStatusCallbackUrl());
        callParams.put(EbodacConstants.SUBSCRIBERS, subscribers);
        callParams.put(EbodacConstants.SEND_SMS_IF_VOICE_FAILS, "1");
        callParams.put(EbodacConstants.DETECT_VOICEMAIL, "1");
        callParams.put(EbodacConstants.RETRY_ATTEMPTS_SHORT, config.getRetryAttempts().toString());
        callParams.put(EbodacConstants.RETRY_DELAY_SHORT, config.getRetryDelay().toString());
        callParams.put(EbodacConstants.RETRY_ATTEMPTS_LONG, EbodacConstants.RETRY_ATTEMPTS_LONG_DEFAULT);
        callParams.put(EbodacConstants.SUBJECT_IDS, subjectIds);
        callParams.put(EbodacConstants.SUBJECT_PHONE_NUMBER, subjectPhoneNumber);

        ivrCallHelper.initiateIvrCall(campaignName, messageKey, externalId);

        verify(outboundCallService, never()).initiateCall(config.getIvrSettingsName(), callParams);
    }
}
