package org.motechproject.ebodac.web;

import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.ebodac.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jdo.JDOException;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

/**
 * Web API for Subject Registration
 */
@RequestMapping("/registration")
@Controller
public class ZetesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZetesController.class);

    private SubjectService subjectService;

    @PreAuthorize("hasAnyRole('mdsDataAccess', 'registrationSubmission')")
    @RequestMapping(value = "/submit", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> submitSubjectRequest(@RequestBody SubmitSubjectRequest submitSubjectRequest) {

        List<ValidationError> errorList;

        errorList = submitSubjectRequest.validate();

        if (!errorList.isEmpty()) {
            List<String> validationMessages = extract(errorList, on(ValidationError.class).getMessage());
            LOGGER.warn("Subject : {} - {}", submitSubjectRequest.getSubjectId(), validationMessages.toString());
        }

        try {
            Subject subject = new Subject(submitSubjectRequest.getSubjectId(), submitSubjectRequest.getName(),
                    submitSubjectRequest.getHouseholdName(), submitSubjectRequest.getHeadOfHousehold(),
                    submitSubjectRequest.getPhoneNumber(), submitSubjectRequest.getAddress(),
                    Language.getByCode(submitSubjectRequest.getLanguage()),
                    submitSubjectRequest.getCommunity(),
                    submitSubjectRequest.getSiteId(),
                    submitSubjectRequest.getChiefdom(),
                    submitSubjectRequest.getSection(),
                    submitSubjectRequest.getDistrict());

            subjectService.createOrUpdateForZetes(subject);
        } catch (JDOException ex) {
            LOGGER.warn("Error raised during creating subject: " + ex.getMessage(), ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            LOGGER.error("Fatal error raised during creating subject: " + ex.getMessage(), ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public SubjectService getSubjectService() {
        return subjectService;
    }

    @Autowired
    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
    }
}
