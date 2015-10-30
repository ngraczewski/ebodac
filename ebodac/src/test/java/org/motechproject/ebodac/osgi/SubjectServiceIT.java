package org.motechproject.ebodac.osgi;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Gender;
import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.SubjectEnrollmentsDataService;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class SubjectServiceIT extends BasePaxIT {

    @Inject
    private SubjectService subjectService;

    @Inject
    private SubjectDataService subjectDataService;

    @Inject
    private SubjectEnrollmentsDataService subjectEnrollmentsDataService;

    @Inject
    private VisitDataService visitDataService;

    private Subject firstSubject;

    private Subject secondSubject;

    @Before
    public void cleanBefore() {
        visitDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
        subjectDataService.deleteAll();
        resetSubjects();
    }

    @After
    public void cleanAfter() {
        visitDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
        subjectDataService.deleteAll();
    }

    private void resetSubjects() {
        firstSubject = new Subject("1000000161", "Michal", "Abacki", "Cabacki",
                "729402018364", "address", Language.English, "community", "B05-SL10001", "chiefdom", "section", "district");

        secondSubject = new Subject("1000000162", "Rafal", "Dabacki", "Ebacki",
                "44443333222", "address", Language.Susu, "community", "B05-SL10001", "chiefdom", "section", "district");
    }

    @Test
    public void shouldCreateOrUpdateForZetes() {
        assertEquals(0, subjectDataService.retrieveAll().size());
        assertEquals(0, visitDataService.retrieveAll().size());

        subjectService.create(firstSubject);
        List<Subject> subjects = subjectDataService.retrieveAll();
        assertEquals(1, subjects.size());

        checkZetesFields(firstSubject, subjects.get(0));
        checkRaveFields(firstSubject, subjects.get(0));

        firstSubject.setAddress("newAddress");
        firstSubject.setLanguage(Language.Limba);
        firstSubject.setPhoneNumber("600700800");

        subjectService.createOrUpdateForZetes(firstSubject); // should update Subject
        subjects = subjectDataService.retrieveAll();
        assertEquals(1, subjects.size());

        checkZetesFields(firstSubject, subjects.get(0));
        checkRaveFields(firstSubject, subjects.get(0));

        subjectService.createOrUpdateForZetes(secondSubject); // should create new Subject
        subjects = subjectDataService.retrieveAll();
        assertEquals(2, subjects.size());

        Subject subjectFromDataService = subjectDataService.findSubjectBySubjectId("1000000162");
        assertNotNull(subjectFromDataService);

        checkZetesFields(secondSubject, subjectFromDataService);
        checkRaveFields(secondSubject, subjectFromDataService);

        secondSubject.setName("Jedrzej");
        secondSubject.setHouseholdName("Gabacki");
        secondSubject.setHeadOfHousehold("Ibacki");
        secondSubject.setCommunity("newCommunity");

        subjectDataService.update(secondSubject);
        subjects = subjectDataService.retrieveAll();
        assertEquals(2, subjects.size());

        subjectFromDataService = subjectDataService.findSubjectBySubjectId("1000000162");
        assertNotNull(subjectFromDataService);

        checkZetesFields(secondSubject, subjectFromDataService);
        checkRaveFields(secondSubject, subjectFromDataService);
    }

    @Test
    public void shouldCreateOrUpdateForRave() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);
        assertEquals(0, subjectDataService.retrieveAll().size());
        assertEquals(0, visitDataService.retrieveAll().size());

        subjectService.create(firstSubject);
        List<Subject> subjects = subjectDataService.retrieveAll();
        assertEquals(1, subjects.size());

        checkZetesFields(firstSubject, subjects.get(0));

        firstSubject.setDateOfDisconStd(LocalDate.parse("2014-10-17", formatter));
        firstSubject.setGender(Gender.Male);
        firstSubject.setStageId((long) 1);

        subjectService.createOrUpdateForRave(firstSubject); // should update Subject
        subjects = subjectDataService.retrieveAll();
        assertEquals(1, subjects.size());

        checkZetesFields(firstSubject, subjects.get(0));
        checkRaveFields(firstSubject, subjects.get(0));

        subjectService.createOrUpdateForRave(secondSubject); // should create new Subject
        subjects = subjectDataService.retrieveAll();
        assertEquals(2, subjects.size());

        Subject subjectFromDataService = subjectDataService.findSubjectBySubjectId("1000000162");
        assertNotNull(subjectFromDataService);

        checkZetesFields(secondSubject, subjectFromDataService);
        checkRaveFields(secondSubject, subjectFromDataService);

        secondSubject.setDateOfBirth(LocalDate.parse("1967-05-02", formatter));
        secondSubject.setPrimerVaccinationDate(LocalDate.parse("2014-10-12", formatter));
        secondSubject.setBoosterVaccinationDate(LocalDate.parse("2014-10-17", formatter));

        subjectDataService.update(secondSubject);
        subjects = subjectDataService.retrieveAll();
        assertEquals(2, subjects.size());

        subjectFromDataService = subjectDataService.findSubjectBySubjectId("1000000162");
        assertNotNull(subjectFromDataService);

        checkZetesFields(secondSubject, subjectFromDataService);
        checkRaveFields(secondSubject, subjectFromDataService);
    }

    @Test
    public void shouldFindSubjectBySubjectId() {
        assertEquals(0, subjectDataService.retrieveAll().size());
        assertEquals(0, visitDataService.retrieveAll().size());

        subjectService.create(firstSubject);
        subjectService.create(secondSubject);

        List<Subject> subjects = subjectDataService.retrieveAll();
        assertEquals(2, subjects.size());

        subjects = subjectDataService.retrieveAll();
        assertEquals(2, subjects.size());

        Subject subject = subjectService.findSubjectBySubjectId("1000000161");
        assertNotNull(subject);
        assertEquals("1000000161", subject.getSubjectId());
    }

    @Test
    public void shouldFindModifiedSubjects() {
        assertEquals(0, subjectDataService.retrieveAll().size());
        assertEquals(0, visitDataService.retrieveAll().size());

        subjectService.create(firstSubject);
        subjectService.create(secondSubject);

        List<Subject> subjects = subjectDataService.retrieveAll();
        assertEquals(2, subjects.size());

        firstSubject.setChanged(true);

        subjectService.update(firstSubject);

        subjects = subjectDataService.retrieveAll();
        assertEquals(2, subjects.size());

        subjects = subjectService.findModifiedSubjects();
        assertEquals(1, subjects.size());

        assertEquals("1000000161", subjects.get(0).getSubjectId());
    }

    @Test
    public void shouldFindSubjectByName() {
        assertEquals(0, subjectDataService.retrieveAll().size());
        assertEquals(0, visitDataService.retrieveAll().size());

        subjectService.create(firstSubject);
        subjectService.create(secondSubject);

        List<Subject> subjects = subjectDataService.retrieveAll();
        assertEquals(2, subjects.size());

        subjects = subjectService.findSubjectByName("Michal");
        assertEquals(1, subjects.size());

        assertEquals("Michal", subjects.get(0).getName());
    }

    @Test
    public void shouldFindSubjectsBoosterVaccinatedAtDay() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);
        assertEquals(0, subjectDataService.retrieveAll().size());
        assertEquals(0, visitDataService.retrieveAll().size());

        firstSubject.setBoosterVaccinationDate(LocalDate.parse("2015-02-28", formatter));
        secondSubject.setBoosterVaccinationDate(LocalDate.parse("2015-03-18", formatter));

        subjectService.create(firstSubject);
        subjectService.create(secondSubject);

        List<Subject> subjects = subjectDataService.retrieveAll();
        assertEquals(2, subjects.size());

        subjects = subjectService.findSubjectsBoosterVaccinatedAtDay(LocalDate.parse("2015-02-28", formatter));
        assertEquals(1, subjects.size());

        assertEquals(LocalDate.parse("2015-02-28", formatter), subjects.get(0).getBoosterVaccinationDate());
    }

    @Test
    public void shouldFindSubjectsPrimerVaccinatedAtDay() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);
        assertEquals(0, subjectDataService.retrieveAll().size());
        assertEquals(0, visitDataService.retrieveAll().size());

        firstSubject.setPrimerVaccinationDate(LocalDate.parse("2015-02-28", formatter));
        secondSubject.setPrimerVaccinationDate(LocalDate.parse("2015-03-18", formatter));

        subjectService.create(firstSubject);
        subjectService.create(secondSubject);

        List<Subject> subjects = subjectDataService.retrieveAll();
        assertEquals(2, subjects.size());

        subjects = subjectService.findSubjectsPrimerVaccinatedAtDay(LocalDate.parse("2015-02-28", formatter));
        assertEquals(1, subjects.size());

        assertEquals(LocalDate.parse("2015-02-28", formatter), subjects.get(0).getPrimerVaccinationDate());
    }

    @Test
    public void shouldFindOldestPrimerVaccinationDate() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);
        assertEquals(0, subjectDataService.retrieveAll().size());
        assertEquals(0, visitDataService.retrieveAll().size());

        firstSubject.setPrimerVaccinationDate(LocalDate.parse("2015-02-28", formatter));
        secondSubject.setPrimerVaccinationDate(LocalDate.parse("2015-03-18", formatter));

        subjectService.create(firstSubject);
        subjectService.create(secondSubject);

        List<Subject> subjects = subjectDataService.retrieveAll();
        assertEquals(2, subjects.size());

        LocalDate oldest = subjectService.findOldestPrimerVaccinationDate();
        assertEquals(LocalDate.parse("2015-02-28", formatter), oldest);
    }

    private void checkZetesFields(Subject expected, Subject actual) {
        assertEquals(expected.getSubjectId(), actual.getSubjectId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getHouseholdName(), actual.getHouseholdName());
        assertEquals(expected.getHeadOfHousehold(), actual.getHeadOfHousehold());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getLanguageCode(), actual.getLanguageCode());
        assertEquals(expected.getCommunity(),        actual.getCommunity());
        assertEquals(expected.getSiteId(), actual.getSiteId());
        assertEquals(expected.getChiefdom(), actual.getChiefdom());
        assertEquals(expected.getSection(),        actual.getSection());
        assertEquals(expected.getDistrict(), actual.getDistrict());
    }

    private void checkRaveFields(Subject expected, Subject actual) {
        assertEquals(expected.getSiteId(), actual.getSiteId());
        assertEquals(expected.getSubjectId(), actual.getSubjectId());
        assertEquals(expected.getGender(), actual.getGender());
        assertEquals(expected.getStageId(), actual.getStageId());
        assertEquals(expected.getDateOfBirth(), actual.getDateOfBirth());
        assertEquals(expected.getPrimerVaccinationDate(), actual.getPrimerVaccinationDate());
        assertEquals(expected.getBoosterVaccinationDate(), actual.getBoosterVaccinationDate());
        assertEquals(expected.getDateOfDisconVac(), actual.getDateOfDisconVac());
        assertEquals(expected.getDateOfDisconStd(), actual.getDateOfDisconStd());
    }
}
