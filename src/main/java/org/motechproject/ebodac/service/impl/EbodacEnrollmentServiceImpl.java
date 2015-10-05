package org.motechproject.ebodac.service.impl;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Enrollment;
import org.motechproject.ebodac.domain.EnrollmentStatus;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.SubjectEnrollments;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.exception.EbodacEnrollmentException;
import org.motechproject.ebodac.repository.EnrollmentDataService;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.SubjectEnrollmentsDataService;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.messagecampaign.exception.CampaignNotFoundException;
import org.motechproject.messagecampaign.service.MessageCampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Service("ebodacEnrollmentService")
public class EbodacEnrollmentServiceImpl implements EbodacEnrollmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EbodacEnrollmentServiceImpl.class);

    private SubjectEnrollmentsDataService subjectEnrollmentsDataService;

    private EnrollmentDataService enrollmentDataService;

    private SubjectDataService subjectDataService;

    private MessageCampaignService messageCampaignService;

    private ConfigService configService;

    @Override
    public void enrollScreening(Subject subject) {
        try {
            enrollNew(subject, VisitType.SCREENING.getValue(), DateUtil.now().toLocalDate(), new Time(DateUtil.now().toLocalTime()));
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
        }
    }

    @Override
    public void enrollSubject(Subject subject) {
        List<Visit> visits = subject.getVisits();

        if (visits != null) {
            for (Visit visit : visits) {
                if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
                    if (visit.getDate() != null) {
                        enrollSubject(visit);
                    }
                } else if (visit.getDate() == null) {
                    enrollSubject(visit);
                }
            }
        }
    }

    @Override
    public void enrollSubject(String subjectId) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subjectId);
        if (subjectEnrollments == null || !EnrollmentStatus.UNENROLLED.equals(subjectEnrollments.getStatus())) {
            throw new EbodacEnrollmentException("Cannot enroll Participant, because no unenrolled Participant exist with id: %s",
                    "ebodac.enroll.error.noUnenrolledSubject", subjectId);
        }

        Subject subject = subjectEnrollments.getSubject();

        boolean disconVac = checkSubjectRequiredDataAndDisconVacDate(subject);

        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            if (!EnrollmentStatus.UNENROLLED.equals(enrollment.getStatus())) {
                continue;
            }
            if (enrollment.getReferenceDate() == null) {
                throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because reference date is empty",
                        "ebodac.enroll.error.emptyReferenceDate", subject.getSubjectId(), enrollment.getCampaignName());
            } else if (disconVac) {
                if (checkIfCampaignInDisconVacList(enrollment.getCampaignName())) {
                    enrollment.setStatus(EnrollmentStatus.COMPLETED);
                    continue;
                }
            }

            enrollment.setStatus(EnrollmentStatus.ENROLLED);
            if (!checkDuplicatedEnrollments(subject, enrollment, false)) {
                scheduleJobsForEnrollment(enrollment, true);
            }
            enrollmentDataService.update(enrollment);
        }
        updateSubjectEnrollments(subjectEnrollments);
    }

    @Override
    public void enrollSubjectToCampaign(String subjectId, String campaignName) {
        enrollUnenrolled(subjectId, campaignName, null);
    }

    @Override
    public void enrollSubjectToCampaignWithNewDate(String subjectId, String campaignName, LocalDate date) {
        enrollUnenrolled(subjectId, campaignName, date);
    }

    @Override
    public void enrollOrCompleteCampaignForSubject(Visit visit) {
        if (visit.getDate() != null) {
            if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
                enrollSubject(visit);
            } else {
                completeCampaignForSubject(visit);
            }
        } else if (!VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
            enrollSubject(visit);
        }
    }

    @Override
    public void unenrollSubject(String subjectId) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subjectId);
        if (subjectEnrollments == null || !EnrollmentStatus.ENROLLED.equals(subjectEnrollments.getStatus())) {
            throw new EbodacEnrollmentException("Cannot unenroll Participant, because Participant with id: %s is not enrolled in any campaign",
                    "ebodac.unenroll.error.noEnrolledSubject", subjectId);
        }
        for (Enrollment enrollment: subjectEnrollments.getEnrollments()) {
            if (EnrollmentStatus.ENROLLED.equals(enrollment.getStatus())) {
                unscheduleJobsForEnrollmentAndChangeParent(enrollment);

                enrollment.setStatus(EnrollmentStatus.UNENROLLED);
                enrollmentDataService.update(enrollment);
            }
        }
        updateSubjectEnrollments(subjectEnrollments);
    }

    @Override
    public void unenrollSubject(String subjectId, String campaignName) {
        if (!unenrollAndSetStatus(subjectId, campaignName, EnrollmentStatus.UNENROLLED)) {
            throw new EbodacEnrollmentException("Cannot unenroll Participant, because no Participant with id: %s registered in Campaign with name: %s",
                    "ebodac.unenroll.error.subjectNotEnrolledInCampaign", subjectId, campaignName);
        }
    }

    @Override
    public void reenrollSubject(Visit visit) {
        if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
            throw new EbodacEnrollmentException("Prime Vaccination Day Visit date cannot be changed", "ebodac.reenroll.error.primeVaccinationDateChanged");
        } else if (!VisitType.UNSCHEDULED_VISIT.equals(visit.getType()) && !VisitType.SCREENING.equals(visit.getType())) {
            reenrollSubjectWithNewDate(visit.getSubject().getSubjectId(), visit.getType().getValue(), visit.getMotechProjectedDate());
        }
    }

    @Override
    public void reenrollSubjectWithNewDate(String subjectId, String campaignName, LocalDate date) {
        unenrollSubject(subjectId, campaignName);
        enrollUnenrolled(subjectId, campaignName, date);
    }

    @Override
    public void withdrawalOrEnrollSubject(Subject oldSubject, Subject newSubject) {
        if (newSubject.getPrimerVaccinationDate() != null && oldSubject.getPrimerVaccinationDate() == null) {
            enrollSubject(newSubject);
        }
        if (newSubject.getDateOfDisconStd() != null && oldSubject.getDateOfDisconStd() == null) {
            if (newSubject.getVisits() != null) {
                for (Visit visit: newSubject.getVisits()) {
                    completeCampaignForSubject(visit);
                }
            }
        } else if (newSubject.getDateOfDisconVac() != null && oldSubject.getDateOfDisconVac() == null) {
            for (String campaignName: configService.getConfig().getDisconVacCampaignsList()) {
                completeCampaignForSubject(newSubject, campaignName);
            }
        }
    }

    @Override
    public void completeCampaign(String subjectId, String campaignName) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subjectId);
        if (subjectEnrollments != null) {
            Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(campaignName);
            if (enrollment != null) {
                enrollment.setStatus(EnrollmentStatus.COMPLETED);
                updateSubjectEnrollments(subjectEnrollments);
            }
        }
    }

    @Override
    public boolean isEnrolled(Visit visit) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(visit.getSubject().getSubjectId());
        String campaignName = visit.getType().getValue();

        if (null == subjectEnrollments) {
            return false;
        }

        Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(campaignName);

        return enrollment != null && EnrollmentStatus.ENROLLED.equals(enrollment.getStatus());
    }

    @Override
    public boolean isEnrolled(String subjectId) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subjectId);
        return subjectEnrollments != null && EnrollmentStatus.ENROLLED.equals(subjectEnrollments.getStatus());
    }

    @Override
    public void changeDuplicatedEnrollmentsForNewPhoneNumber(Subject subject) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());

        try {
            for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
                if (enrollment.getParentEnrollment() == null && enrollment.hasDuplicatedEnrollments()) {
                    changeParentForDuplicatedEnrollments(enrollment, false);
                    if (checkDuplicatedEnrollments(subject, enrollment, true)) {
                        unscheduleJobsForEnrollment(enrollment);
                    }
                    enrollmentDataService.update(enrollment);
                } else if (enrollment.getParentEnrollment() != null && EnrollmentStatus.ENROLLED.equals(enrollment.getParentEnrollment().getStatus())) {
                    Enrollment parentEnrollment = enrollment.getParentEnrollment();
                    parentEnrollment.getDuplicatedEnrollments().remove(enrollment);
                    enrollment.setParentEnrollment(null);
                    if (!checkDuplicatedEnrollments(subject, enrollment, true)) {
                        scheduleJobsForEnrollment(enrollment, false);
                    }
                    enrollmentDataService.update(parentEnrollment);
                    enrollmentDataService.update(enrollment);
                }
            }
        } catch (EbodacEnrollmentException e) {
            throw new EbodacEnrollmentException("Cannot change Participant phone number, because error occurred during changing parent for duplicated enrolments",
                    e, "ebodac.updateSubject.cannotChangeParent");
        }
    }

    @Override
    public void createEnrollmentRecordsForSubject(Subject subject) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());

        if (subjectEnrollments == null) {
            subjectEnrollments = new SubjectEnrollments(subject);
        }

        for (Visit visit : subject.getVisits()) {
            if (!VisitType.UNSCHEDULED_VISIT.equals(visit.getType()) && !VisitType.SCREENING.equals(visit.getType())) {
                String campaignName = visit.getType().getValue();
                Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(campaignName);

                if (enrollment == null) {
                    LocalDate referenceDate = visit.getMotechProjectedDate();
                    if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
                        referenceDate = visit.getDate();
                    }

                    if (referenceDate != null) {
                        if (VisitType.BOOST_VACCINATION_DAY.equals(visit.getType())) {
                            String dayOfWeek = referenceDate.dayOfWeek().getAsText(Locale.ENGLISH);
                            campaignName = VisitType.BOOST_VACCINATION_DAY.getValue() + " " + dayOfWeek;
                        }

                        enrollment = new Enrollment(subject.getSubjectId(), campaignName);
                        enrollment.setReferenceDate(referenceDate);
                        enrollment.setStatus(EnrollmentStatus.UNENROLLED);

                        subjectEnrollments.addEnrolment(enrollment);
                    }
                }
            }
        }

        updateSubjectEnrollments(subjectEnrollments);
    }

    private void enrollSubject(Visit visit) {
        try {
            if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
                enrollNew(visit.getSubject(), visit.getType().getValue(), visit.getDate());
                enrollNew(visit.getSubject(), EbodacConstants.BOOSTER_RELATED_MESSAGES, visit.getDate());
            } else if (!VisitType.UNSCHEDULED_VISIT.equals(visit.getType()) && !VisitType.SCREENING.equals(visit.getType())) {
                enrollNew(visit.getSubject(), visit.getType().getValue(), visit.getMotechProjectedDate());
            }
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
        }
    }

    private void enrollNew(Subject subject, String campaignName, LocalDate referenceDate) {
        enrollNew(subject, campaignName, referenceDate, null);
    }

    private void enrollNew(Subject subject, String campaignName, LocalDate referenceDate, Time deliverTime) {
        if (subject == null) {
            throw new EbodacEnrollmentException("Cannot enroll Participant to Campaign with name: %s, because participant is null",
                    "ebodac.enroll.error.subjectNull", campaignName);
        }
        if (referenceDate == null) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because reference date is empty",
                    "ebodac.enroll.error.emptyReferenceDate", subject.getSubjectId(), campaignName);
        }

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());

        if (subjectEnrollments == null) {
            subjectEnrollments = new SubjectEnrollments(subject);
        }

        checkSubjectRequiredDataAndDisconVacDate(subject, campaignName);

        Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(campaignName);

        if (VisitType.BOOST_VACCINATION_DAY.getValue().equals(campaignName)) {
            String dayOfWeek = referenceDate.dayOfWeek().getAsText(Locale.ENGLISH);
            campaignName = VisitType.BOOST_VACCINATION_DAY.getValue() + " " + dayOfWeek;
        }

        if (enrollment != null) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because enrollment already exists",
                    "ebodac.enroll.error.enrolmentAlreadyExist", subject.getSubjectId(), campaignName);
        } else {
            enrollment = new Enrollment(subject.getSubjectId(), campaignName);
            subjectEnrollments.addEnrolment(enrollment);
        }

        enrollment.setReferenceDate(referenceDate);
        enrollment.setDeliverTime(deliverTime);

        if (!checkDuplicatedEnrollments(subject, enrollment, false)) {
            scheduleJobsForEnrollment(enrollment, false);
        }

        updateSubjectEnrollments(subjectEnrollments);
    }

    private void enrollUnenrolled(String subjectId, String campaignName, LocalDate referenceDate) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subjectId);
        if (subjectEnrollments == null) {
            throw new EbodacEnrollmentException("Cannot enroll Participant, because not found unenrolled Participant with id: %s in campaign with name: %s",
                    "ebodac.enroll.error.noUnenrolledSubjectInCampaign", subjectId, campaignName);
        }

        Subject subject = subjectEnrollments.getSubject();

        checkSubjectRequiredDataAndDisconVacDate(subject, campaignName);

        Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(campaignName);

        if (enrollment == null) {
            throw new EbodacEnrollmentException("Cannot enroll Participant, because not found unenrolled Participant with id: %s in campaign with name: %s",
                    "ebodac.enroll.error.noUnenrolledSubjectInCampaign", subjectId, campaignName);
        }
        if (EnrollmentStatus.ENROLLED.equals(enrollment.getStatus())) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because participant is already enrolled in this campaign",
                    "ebodac.enroll.error.subjectAlreadyEnrolled", subjectId, enrollment.getCampaignName());
        }
        if (EnrollmentStatus.COMPLETED.equals(enrollment.getStatus())) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because this campaign is completed",
                    "ebodac.enroll.error.campaignCompleted", subjectId, enrollment.getCampaignName());
        }

        if (referenceDate != null && campaignName.startsWith(VisitType.BOOST_VACCINATION_DAY.getValue())) {
            subjectEnrollments.removeEnrolment(enrollment);
            subjectEnrollmentsDataService.update(subjectEnrollments);
            enrollmentDataService.delete(enrollment);
            String dayOfWeek = referenceDate.dayOfWeek().getAsText(Locale.ENGLISH);
            campaignName = VisitType.BOOST_VACCINATION_DAY.getValue() + " " + dayOfWeek;
            enrollment = new Enrollment(subjectId, campaignName);
            subjectEnrollments.addEnrolment(enrollment);
        }

        if (referenceDate != null) {
            if (VisitType.PRIME_VACCINATION_DAY.getValue().equals(campaignName) && !referenceDate.equals(enrollment.getReferenceDate())) {
                throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because reference date cannot be changed for Prime Vaccination Day Campaign",
                        "ebodac.enroll.error.primeVaccinationDateChanged", subjectId, enrollment.getCampaignName());
            }
            enrollment.setReferenceDate(referenceDate);
            enrollment.setParentEnrollment(null);
        } else if (enrollment.getReferenceDate() == null) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because reference date is empty",
                    "ebodac.enroll.error.emptyReferenceDate", subjectId, enrollment.getCampaignName());
        }

        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        if (!checkDuplicatedEnrollments(subject, enrollment, false)) {
            scheduleJobsForEnrollment(enrollment, false);
        }

        updateSubjectEnrollments(subjectEnrollments);
    }

    private void completeCampaignForSubject(Visit visit) {
        if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
            completeCampaignForSubject(visit.getSubject(), visit.getType().getValue());
            completeCampaignForSubject(visit.getSubject(), EbodacConstants.BOOSTER_RELATED_MESSAGES);
        } else if (!VisitType.UNSCHEDULED_VISIT.equals(visit.getType()) && !VisitType.SCREENING.equals(visit.getType())) {
            completeCampaignForSubject(visit.getSubject(), visit.getType().getValue());
        }
    }

    private void completeCampaignForSubject(Subject subject, String campaignName) {
        try {
            unenrollAndSetStatus(subject.getSubjectId(), campaignName, EnrollmentStatus.COMPLETED);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
        }
    }

    private boolean unenrollAndSetStatus(String subjectId, String campaignName, EnrollmentStatus status) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subjectId);
        if (subjectEnrollments == null) {
            return false;
        }
        Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(campaignName);

        if (enrollment == null) {
            return false;
        } else if (EnrollmentStatus.ENROLLED.equals(enrollment.getStatus())) {
            unscheduleJobsForEnrollmentAndChangeParent(enrollment);

            enrollment.setStatus(status);
            updateSubjectEnrollments(subjectEnrollments);

            return true;
        } else if (EnrollmentStatus.UNENROLLED.equals(enrollment.getStatus()) && !status.equals(enrollment.getStatus())) {
            enrollment.setStatus(status);
            updateSubjectEnrollments(subjectEnrollments);
            return true;
        }

        return false;
    }

    private void scheduleJobsForEnrollment(Enrollment enrollment, boolean completeIfLastMessageInThePast) {
        try {
            messageCampaignService.scheduleJobsForEnrollment(enrollment.toCampaignEnrollment());
        } catch (CampaignNotFoundException e) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s because Campaign with name: %s doesn't exist",
                    e, "ebodac.enroll.error.campaignNotExist", enrollment.getExternalId(), enrollment.getCampaignName());
        } catch (IllegalArgumentException e) {
            if (completeIfLastMessageInThePast) {
                LOGGER.debug("Cannot enroll Participant with id: {} for Campaign with name: {}, because last message date is in the past. Changing enrollment status to Completed",
                        enrollment.getExternalId(), enrollment.getCampaignName(), e);
                enrollment.setStatus(EnrollmentStatus.COMPLETED);
            } else {
                throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because last message date is in the past",
                        e, "ebodac.enroll.error.lastMessageInPast", enrollment.getExternalId(), enrollment.getCampaignName());
            }
        } catch (Exception e) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because of unknown exception",
                    e, "ebodac.enroll.error.unknownException", enrollment.getExternalId(), enrollment.getCampaignName());
        }
    }

    private void unscheduleJobsForEnrollmentAndChangeParent(Enrollment enrollment) {
        changeParentForDuplicatedEnrollments(enrollment, true);
        unscheduleJobsForEnrollment(enrollment);
    }

    private void unscheduleJobsForEnrollment(Enrollment enrollment) {
        try {
            messageCampaignService.unscheduleJobsForEnrollment(enrollment.toCampaignEnrollment());
        } catch (CampaignNotFoundException e) {
            throw new EbodacEnrollmentException("Cannot unenroll Participant with id: %s because campaign with name: %s doesn't exist",
                    e, "ebodac.unenroll.error.campaignNotExist", enrollment.getExternalId(), enrollment.getCampaignName());
        } catch (Exception e) {
            throw new EbodacEnrollmentException("Cannot unenroll Participant with id: %s from campaign with name: %s, because of unknown exception",
                    e, "ebodac.unenroll.error.unknownException", enrollment.getExternalId(), enrollment.getCampaignName());
        }
    }

    private void updateSubjectEnrollments(SubjectEnrollments subjectEnrollments) {
        EnrollmentStatus status = EnrollmentStatus.COMPLETED;

        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            if (EnrollmentStatus.ENROLLED.equals(enrollment.getStatus())) {
                status = EnrollmentStatus.ENROLLED;
                break;
            } else if (EnrollmentStatus.UNENROLLED.equals(enrollment.getStatus())) {
                status = EnrollmentStatus.UNENROLLED;
            }
        }

        subjectEnrollments.setStatus(status);
        subjectEnrollmentsDataService.update(subjectEnrollments);
    }

    private void checkSubjectRequiredDataAndDisconVacDate(Subject subject, String campaignName) {
        if (checkSubjectRequiredDataAndDisconVacDate(subject) && checkIfCampaignInDisconVacList(campaignName)) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because participant resigned form booster vaccination",
                    "ebodac.enroll.error.subjectResignFromBooster", subject.getSubjectId(), campaignName);
        }
    }

    private boolean checkSubjectRequiredDataAndDisconVacDate(Subject subject) {
        if (subject.getDateOfDisconStd() != null) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s, because participant is withdrawn from study",
                    "ebodac.enroll.error.subjectWithdrawn", subject.getSubjectId());
        }
        if (subject.getLanguage() == null) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s, because participant language is empty",
                    "ebodac.enroll.error.emptyLanguage", subject.getSubjectId());
        }
        if (StringUtils.isBlank(subject.getPhoneNumber())) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s, because participant phone number is empty",
                    "ebodac.enroll.error.emptyPhoneNumber", subject.getSubjectId());
        }
        if (subject.getPrimerVaccinationDate() == null) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s, because participant Prime Vaccination Date is empty",
                    "ebodac.enroll.error.emptyPrimeVaccinationDate", subject.getSubjectId());
        }

        return subject.getDateOfDisconVac() != null;
    }

    private boolean checkIfCampaignInDisconVacList(String campaignName) {
        if (campaignName.startsWith(VisitType.BOOST_VACCINATION_DAY.getValue())) {
            campaignName = VisitType.BOOST_VACCINATION_DAY.getValue();
        }
        return configService.getConfig().getDisconVacCampaignsList().contains(campaignName);
    }

    private void changeParentForDuplicatedEnrollments(Enrollment oldParent, boolean parentUnenrolled) {
        if (oldParent.hasDuplicatedEnrollments()) {
            Set<Enrollment> duplicatedEnrollments = oldParent.getDuplicatedEnrollments();
            for (Enrollment e : duplicatedEnrollments) {
                if (EnrollmentStatus.ENROLLED.equals(e.getStatus())) {
                    try {
                        scheduleJobsForEnrollment(e, false);
                    } catch (EbodacEnrollmentException ex) {
                        throw new EbodacEnrollmentException("Cannot unenroll Participant with id: %s from campaign with name: %s, because error occurred during enrolling new parent for duplicated enrolments",
                                ex, "ebodac.unenroll.error.cannotEnrollNewParent", oldParent.getExternalId(), oldParent.getCampaignName());
                    }

                    e.setParentEnrollment(null);
                    duplicatedEnrollments.remove(e);

                    oldParent.setDuplicatedEnrollments(null);
                    e.setDuplicatedEnrollments(duplicatedEnrollments);

                    if (parentUnenrolled) {
                        oldParent.setParentEnrollment(e);
                        duplicatedEnrollments.add(oldParent);
                    }

                    enrollmentDataService.update(oldParent);
                    enrollmentDataService.update(e);
                    break;
                }
            }
        }
    }

    private boolean checkDuplicatedEnrollments(Subject subject, Enrollment enrollment, boolean phoneNumberChanged) {
        enrollment.setDuplicatedEnrollments(null);

        if (enrollment.getParentEnrollment() != null && EnrollmentStatus.ENROLLED.equals(enrollment.getParentEnrollment().getStatus())) {
            return true;
        }

        String phoneNumber = subject.getPhoneNumber();
        LocalDate referenceDate = enrollment.getReferenceDate();
        String campaignName = enrollment.getCampaignName();

        List<Subject> subjects = subjectDataService.findSubjectsByPhoneNumber(phoneNumber);

        if ((!phoneNumberChanged && subjects.size() == 1) || subjects.isEmpty()) {
            return false;
        }

        Set<String> subjectIds = new HashSet<>();

        for (Subject s : subjects) {
            if (!s.getSubjectId().equals(subject.getSubjectId())) {
                subjectIds.add(s.getSubjectId());
            }
        }

        if (Pattern.compile(EbodacConstants.LONG_TERM_FOLLOW_UP_CAMPAIGN).matcher(campaignName).matches()) {
            campaignName = EbodacConstants.LONG_TERM_FOLLOW_UP_CAMPAIGN;
        } else if (Pattern.compile(EbodacConstants.FOLLOW_UP_CAMPAIGN).matcher(campaignName).matches()) {
            campaignName = EbodacConstants.FOLLOW_UP_CAMPAIGN;
        }

        List<Enrollment> enrollments = enrollmentDataService.findEnrollmentsByStatusReferenceDateCampaignNameAndSubjectIds(
                EnrollmentStatus.ENROLLED, referenceDate, campaignName, subjectIds);

        if (!enrollments.isEmpty()) {
            Enrollment parentEnrolment = null;
            if (enrollments.size() > 1) {
                for (Enrollment e: enrollments) {
                    if (e.getParentEnrollment() == null && e.hasDuplicatedEnrollments()) {
                        parentEnrolment = e;
                        break;
                    }
                }
            } else {
                parentEnrolment = enrollments.get(0);
            }

            if (parentEnrolment == null) {
                throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because cannot find parent for duplicated enrollments",
                        "ebodac.enroll.error.cannotFindParent", subject.getSubjectId(), campaignName);
            }

            enrollment.setParentEnrollment(parentEnrolment);
            parentEnrolment.addDuplicatedEnrollment(enrollment);

            enrollmentDataService.update(parentEnrolment);
            return true;
        }

        return false;
    }

    @Autowired
    public void setSubjectEnrollmentsDataService(SubjectEnrollmentsDataService subjectEnrollmentsDataService) {
        this.subjectEnrollmentsDataService = subjectEnrollmentsDataService;
    }

    @Autowired
    public void setEnrollmentDataService(EnrollmentDataService enrollmentDataService) {
        this.enrollmentDataService = enrollmentDataService;
    }

    @Autowired
    public void setSubjectDataService(SubjectDataService subjectDataService) {
        this.subjectDataService = subjectDataService;
    }

    @Autowired
    public void setMessageCampaignService(MessageCampaignService messageCampaignService) {
        this.messageCampaignService = messageCampaignService;
    }

    @Autowired
    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }
}
