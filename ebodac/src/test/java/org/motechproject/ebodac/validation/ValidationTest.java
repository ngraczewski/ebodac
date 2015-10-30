package org.motechproject.ebodac.validation;

import com.google.common.base.Predicate;
import org.junit.Assert;
import org.junit.Test;
import org.motechproject.ebodac.web.SubmitSubjectRequest;

import java.util.List;

import static com.google.common.collect.Iterables.any;

/**
 * Tests SubmitSubjectRequest validations
 */
public class ValidationTest {

    private Predicate<ValidationError> hasErrorPredicate(final String errorMessage) {
        return new Predicate<ValidationError>() {
            @Override
            public boolean apply(ValidationError err) {
                return err.getMessage().equals(errorMessage);
            }
        };
    }

    @Test
    public void testSubjectIdValidation() {

        SubmitSubjectRequest submitRequestWithCorrectId = new SubmitSubjectRequest();
        SubmitSubjectRequest submitRequestWithWrongId = new SubmitSubjectRequest();

        submitRequestWithCorrectId.setSubjectId("1000000452");
        submitRequestWithWrongId.setSubjectId("1010000173");

        List<ValidationError> request1Errors = submitRequestWithCorrectId.validate();
        List<ValidationError> request2Errors = submitRequestWithWrongId.validate();

        Assert.assertTrue(!any(request1Errors, hasErrorPredicate(ValidationError.SUBJECT_ID_NOT_VERIFIED)));
        Assert.assertFalse(!any(request2Errors, hasErrorPredicate(ValidationError.SUBJECT_ID_NOT_VERIFIED)));
    }

    @Test
    public void testSubmitRequestNullValuesValidation() {
        SubmitSubjectRequest request = new SubmitSubjectRequest();

        List<ValidationError> requestErrors = request.validate();

        Assert.assertTrue(any(requestErrors, hasErrorPredicate(ValidationError.LANGUAGE_NULL)));
        Assert.assertTrue(any(requestErrors, hasErrorPredicate(ValidationError.SUBJECT_ID_NULL)));
        Assert.assertTrue(any(requestErrors, hasErrorPredicate(ValidationError.NAME_NULL)));
        Assert.assertTrue(any(requestErrors, hasErrorPredicate(ValidationError.PHONE_NUMBER_NULL)));
    }

    @Test
    public void testLanguageCodeValidation() {
        SubmitSubjectRequest request = new SubmitSubjectRequest();
        SubmitSubjectRequest request2 = new SubmitSubjectRequest();

        request.setLanguage("not-en");
        request2.setLanguage("kri");

        List<ValidationError> requestErrors = request.validate();
        List<ValidationError> request2Errors = request2.validate();

        Assert.assertTrue(any(requestErrors, hasErrorPredicate(ValidationError.LANGUAGE_NOT_CORRECT)));
        Assert.assertFalse(any(request2Errors, hasErrorPredicate(ValidationError.LANGUAGE_NOT_CORRECT)));
    }

    @Test
    public void testValuesWithNumbers() {
        SubmitSubjectRequest request = new SubmitSubjectRequest();

        request.setSubjectId("1231QWE3463asd45");
        request.setName("King Lion 3rd");
        request.setHouseholdName("Brown34");
        request.setHeadOfHousehold("M4ri0n");

        List<ValidationError> requestErrors = request.validate();

        Assert.assertTrue(any(requestErrors, hasErrorPredicate(ValidationError.SUBJECT_ID_NOT_VERIFIED)));
        Assert.assertTrue(any(requestErrors, hasErrorPredicate(ValidationError.NAME_HAS_DIGITS)));
        Assert.assertTrue(any(requestErrors, hasErrorPredicate(ValidationError.HOUSEHOLD_NAME_HAS_DIGITS)));
        Assert.assertTrue(any(requestErrors, hasErrorPredicate(ValidationError.HEAD_OF_HOUSEHOLD_HAS_DIGITS)));
    }
}
