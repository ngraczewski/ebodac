package org.motechproject.ebodac.web;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Enrollment;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.SubjectEnrollments;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.exception.EbodacEnrollmentException;
import org.motechproject.ebodac.exception.EbodacException;
import org.motechproject.ebodac.exception.EbodacLookupException;
import org.motechproject.ebodac.repository.EnrollmentDataService;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.ebodac.service.LookupService;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.ebodac.service.VisitService;
import org.motechproject.ebodac.web.domain.GridSettings;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
public class EbodacEnrollmentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EbodacEnrollmentController.class);

    @Autowired
    private EbodacEnrollmentService ebodacEnrollmentService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private EnrollmentDataService enrollmentDataService;

    @Autowired
    private LookupService lookupService;

    @Autowired
    private SubjectService subjectService;

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/reenrollSubject", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> reenrollVisit(@RequestBody Visit visit) {

        if (visit == null) {
            return new ResponseEntity<>("ebodac.enrollment.error.nullVisit", HttpStatus.BAD_REQUEST);
        }
        if (visit.getSubject() == null) {
            return new ResponseEntity<>("ebodac.enrollment.error.noSubject", HttpStatus.BAD_REQUEST);
        }
        if (visit.getMotechProjectedDate() == null) {
            return new ResponseEntity<>("ebodac.enrollment.error.EmptyPlannedDate", HttpStatus.BAD_REQUEST);
        }

        Visit existingVisit = visitService.findVisitBySubjectIdAndVisitType(visit.getSubject().getSubjectId(), visit.getType());

        if (existingVisit == null) {
            return new ResponseEntity<>("ebodac.enrollment.error.noVisitInDB", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (existingVisit.getDate() != null) {
            return new ResponseEntity<>("ebodac.enrollment.error.visitCompleted", HttpStatus.BAD_REQUEST);
        }
        if (!ebodacEnrollmentService.isEnrolled(visit)) {
            return new ResponseEntity<>("ebodac.enrollment.success.plannedDateChanged", HttpStatus.OK);
        }
        if (visit.getMotechProjectedDate().equals(existingVisit.getMotechProjectedDate())) {
            return new ResponseEntity<>("ebodac.enrollment.error.plannedDateNotChanged", HttpStatus.BAD_REQUEST);
        }
        if (visit.getMotechProjectedDate().isBefore(LocalDate.now())) {
            return new ResponseEntity<>("ebodac.enrollment.error.plannedDateInPast", HttpStatus.BAD_REQUEST);
        }
        try {
            ebodacEnrollmentService.reenrollSubject(visit);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
            return new ResponseEntity<>(getMessageFromException(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("ebodac.reenrollVisit.successmMsg", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/availableCampaigns", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getAvailableCampaigns() {
        return EbodacConstants.AVAILABLE_CAMPAIGNS;
    }

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/getEnrollments", method = RequestMethod.POST)
    @ResponseBody
    public Records<?> getEnrollments(GridSettings settings) throws IOException {
        Order order = null;
        if (!settings.getSortColumn().isEmpty()) {
            order = new Order(settings.getSortColumn(), settings.getSortDirection());
        }

        QueryParams queryParams = new QueryParams(settings.getPage(), settings.getRows(), order);

        try {
            return lookupService.getEntities(SubjectEnrollments.class, settings.getLookup(), settings.getFields(), queryParams);
        } catch (EbodacLookupException e) {
            LOGGER.debug(e.getMessage(), e);
            return null;
        }
    }

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/getLookupsForEnrollments", method = RequestMethod.GET)
    @ResponseBody
    public List<LookupDto> getLookupsForEnrollments() {
        return lookupService.getAvailableLookups(SubjectEnrollments.class.getName());
    }

    @PreAuthorize("hasRole('manageEnrollments')")
    @RequestMapping(value = "/checkAdvancedPermissions", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> checkAdvancedPermissions() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('manageEnrollments')")
    @RequestMapping(value = "/getEnrollmentAdvanced/{subjectId}", method = RequestMethod.POST)
    @ResponseBody
    public Records<?> getEnrollmentAdvanced(@PathVariable String subjectId, GridSettings settings) throws IOException {
        Order order = null;
        if (!settings.getSortColumn().isEmpty()) {
            order = new Order(settings.getSortColumn(), settings.getSortDirection());
        }

        QueryParams queryParams = new QueryParams(null, null, order);

        long recordCount;
        int rowCount;

        recordCount = enrollmentDataService.countFindEnrollmentsBySubjectId(subjectId);
        rowCount = (int) Math.ceil(recordCount / (double) settings.getRows());

        List<Enrollment> enrollments = enrollmentDataService.findEnrollmentsBySubjectId(subjectId, queryParams);

        return new Records<>(settings.getPage(), rowCount, (int) recordCount, enrollments);
    }

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/enrollSubject", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> enrollSubject(@RequestBody String subjectId) {
        if (StringUtils.isBlank(subjectId)) {
            return new ResponseEntity<>("ebodac.enrollment.error.emptySubjectId", HttpStatus.BAD_REQUEST);
        }

        try {
            ebodacEnrollmentService.enrollSubject(subjectId);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
            return new ResponseEntity<>(getMessageFromException(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/unenrollSubject", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> unenrollSubject(@RequestBody String subjectId) {
        if (StringUtils.isBlank(subjectId)) {
            return new ResponseEntity<>("ebodac.enrollment.error.emptySubjectId", HttpStatus.BAD_REQUEST);
        }

        try {
            ebodacEnrollmentService.unenrollSubject(subjectId);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
            return new ResponseEntity<>(getMessageFromException(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('manageEnrollments')")
    @RequestMapping(value = "/enrollCampaign/{subjectId}/{campaignName}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> enrollCampaign(@PathVariable String subjectId, @PathVariable String campaignName) throws IOException {

        if (StringUtils.isBlank(subjectId)) {
            return new ResponseEntity<>("ebodac.enrollment.error.emptySubjectId", HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isBlank(campaignName)) {
            return new ResponseEntity<>("ebodac.enrollment.error.emptyCampaignName", HttpStatus.BAD_REQUEST);
        }

        try {
            ebodacEnrollmentService.enrollSubjectToCampaign(subjectId, campaignName);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
            return new ResponseEntity<>(getMessageFromException(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('manageEnrollments')")
    @RequestMapping(value = "/unenrollCampaign/{subjectId}/{campaignName}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> unenrollCampaign(@PathVariable String subjectId, @PathVariable String campaignName) throws IOException {

        if (StringUtils.isBlank(subjectId)) {
            return new ResponseEntity<>("ebodac.enrollment.error.emptySubjectId", HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isBlank(campaignName)) {
            return new ResponseEntity<>("ebodac.enrollment.error.emptyCampaignName", HttpStatus.BAD_REQUEST);
        }

        try {
            ebodacEnrollmentService.unenrollSubject(subjectId, campaignName);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
            return new ResponseEntity<>(getMessageFromException(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('manageEnrollments')")
    @RequestMapping(value = "/enrollCampaignWithNewDate/{subjectId}/{campaignName}/{date}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> enrollCampaignWithNewDate(@PathVariable String subjectId, @PathVariable String campaignName,
                                                            @PathVariable String date) throws IOException {

        if (StringUtils.isBlank(subjectId)) {
            return new ResponseEntity<>("ebodac.enrollment.error.emptySubjectId", HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isBlank(campaignName)) {
            return new ResponseEntity<>("ebodac.enrollment.error.emptyCampaignName", HttpStatus.BAD_REQUEST);
        }

        if (VisitType.PRIME_VACCINATION_DAY.getValue().equals(campaignName)) {
            return new ResponseEntity<>("ebodac.enrollment.error.primeVaccinationDateChanged", HttpStatus.BAD_REQUEST);
        }

        try {
            LocalDate referenceDate = LocalDate.parse(date, DateTimeFormat.forPattern(EbodacConstants.ENROLLMENT_DATE_FORMAT));
            ebodacEnrollmentService.enrollSubjectToCampaignWithNewDate(subjectId, campaignName, referenceDate);
            updateVisit(subjectId, campaignName, referenceDate);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
            return new ResponseEntity<>(getMessageFromException(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('manageEnrollments')")
    @RequestMapping(value = "/reenrollCampaign/{subjectId}/{campaignName}/{date}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> reenrollCampaign(@PathVariable String subjectId, @PathVariable String campaignName,
                                                   @PathVariable String date) throws IOException {

        if (StringUtils.isBlank(subjectId)) {
            return new ResponseEntity<>("ebodac.enrollment.error.emptySubjectId", HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isBlank(campaignName)) {
            return new ResponseEntity<>("ebodac.enrollment.error.emptyCampaignName", HttpStatus.BAD_REQUEST);
        }

        if (VisitType.PRIME_VACCINATION_DAY.getValue().equals(campaignName)) {
            return new ResponseEntity<>("ebodac.enrollment.error.primeVaccinationDateChanged", HttpStatus.BAD_REQUEST);
        }

        try {
            LocalDate referenceDate = LocalDate.parse(date, DateTimeFormat.forPattern(EbodacConstants.ENROLLMENT_DATE_FORMAT));
            ebodacEnrollmentService.reenrollSubjectWithNewDate(subjectId, campaignName, referenceDate);
            updateVisit(subjectId, campaignName, referenceDate);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
            return new ResponseEntity<>(getMessageFromException(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/subjectDataChanged", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> subjectDataChanged(@RequestBody Subject subject) {
        if (ebodacEnrollmentService.isEnrolled(subject.getSubjectId())) {
            try {
                if (StringUtils.isBlank(subject.getPhoneNumber()) || subject.getLanguage() == null) {
                    ebodacEnrollmentService.unenrollSubject(subject.getSubjectId());
                    return new ResponseEntity<>(HttpStatus.OK);
                }

                Subject oldSubject = subjectService.findSubjectBySubjectId(subject.getSubjectId());

                if (!subject.getPhoneNumber().equals(oldSubject.getPhoneNumber())) {
                    ebodacEnrollmentService.changeDuplicatedEnrollmentsForNewPhoneNumber(subject);
                }
            } catch (EbodacEnrollmentException e) {
                LOGGER.debug(e.getMessage(), e);
                return new ResponseEntity<>(getMessageFromException(e), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            Subject oldSubject = subjectService.findSubjectBySubjectId(subject.getSubjectId());
            if (subject.getVisits() != null && subject.getPrimerVaccinationDate() != null && StringUtils.isNotBlank(subject.getPhoneNumber())
                    && subject.getLanguage() != null && (oldSubject.getLanguage() == null || StringUtils.isBlank(subject.getPhoneNumber()))) {
                ebodacEnrollmentService.createEnrollmentRecordsForSubject(oldSubject);
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void updateVisit(String subjectId, String campaignName, LocalDate date) {
        if (!EbodacConstants.BOOSTER_RELATED_MESSAGES.equals(campaignName)) {
            VisitType visitType = null;
            if (campaignName.startsWith(VisitType.BOOST_VACCINATION_DAY.getValue())) {
                visitType = VisitType.BOOST_VACCINATION_DAY;
            } else {
                visitType = VisitType.getByValue(campaignName);
            }
            if (visitType != null) {
                Visit visit = visitService.findVisitBySubjectIdAndVisitType(subjectId, visitType);
                if (visit != null) {
                    visit.setMotechProjectedDate(date);
                    visitService.update(visit);
                }
            }
        }
    }

    private String getMessageFromException(EbodacException e) {
        return String.format("key:%s\nparams:%s", e.getMessageKey(), e.getParams());
    }
}
