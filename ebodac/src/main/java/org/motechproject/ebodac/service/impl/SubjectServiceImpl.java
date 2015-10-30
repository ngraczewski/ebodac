package org.motechproject.ebodac.service.impl;

import org.joda.time.LocalDate;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.ebodac.service.ReportUpdateService;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the {@link org.motechproject.ebodac.service.SubjectService} interface. Uses
 * {@link org.motechproject.ebodac.repository.SubjectDataService} in order to retrieve and persist records.
 */
@Service("subjectService")
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectDataService subjectDataService;

    @Autowired
    private ReportUpdateService reportUpdateService;

    @Autowired
    private EbodacEnrollmentService ebodacEnrollmentService;

    @Override
    public Subject createOrUpdateForZetes(Subject newSubject) {

        Subject subjectInDb = findSubjectBySubjectId(newSubject.getSubjectId());

        reportUpdateService.addReportsToUpdateIfNeeded(subjectInDb, newSubject);

        if (subjectInDb != null) {
            if (subjectInDb.equalsForZetes(newSubject)) {
                return subjectInDb;
            }
            subjectInDb.setName(newSubject.getName());
            subjectInDb.setHouseholdName(newSubject.getHouseholdName());
            subjectInDb.setPhoneNumber(newSubject.getPhoneNumber());
            subjectInDb.setHeadOfHousehold(newSubject.getHeadOfHousehold());
            subjectInDb.setAddress(newSubject.getAddress());
            subjectInDb.setLanguage(newSubject.getLanguage());
            subjectInDb.setCommunity(newSubject.getCommunity());
            subjectInDb.setSiteId(newSubject.getSiteId());
            subjectInDb.setChiefdom(newSubject.getChiefdom());
            subjectInDb.setSection(newSubject.getSection());
            subjectInDb.setDistrict(newSubject.getDistrict());

            ebodacEnrollmentService.enrollSubject(subjectInDb);

            return update(subjectInDb);
        } else {
            return create(newSubject);
        }
    }

    @Override
    public Subject createOrUpdateForRave(Subject newSubject) {

        Subject subjectInDb = findSubjectBySubjectId(newSubject.getSubjectId());

        reportUpdateService.addReportsToUpdateIfNeeded(subjectInDb, newSubject);

        if (subjectInDb != null) {
            if (subjectInDb.equalsForRave(newSubject)) {
                return subjectInDb;
            }

            ebodacEnrollmentService.withdrawalOrEnrollSubject(subjectInDb, newSubject);

            subjectInDb.setGender(newSubject.getGender());
            subjectInDb.setStageId(newSubject.getStageId());
            subjectInDb.setDateOfBirth(newSubject.getDateOfBirth());
            subjectInDb.setPrimerVaccinationDate(newSubject.getPrimerVaccinationDate());
            subjectInDb.setBoosterVaccinationDate(newSubject.getBoosterVaccinationDate());
            subjectInDb.setDateOfDisconVac(newSubject.getDateOfDisconVac());
            subjectInDb.setDateOfDisconStd(newSubject.getDateOfDisconStd());

            return update(subjectInDb);
        } else {
            return create(newSubject);
        }
    }

    @Override
    public List<Subject> findSubjectByName(String name) {
        return subjectDataService.findSubjectsByName(name);
    }

    @Override
    public Subject findSubjectBySubjectId(String subjectId) {
        return subjectDataService.findSubjectBySubjectId(subjectId);
    }

    @Override
    public Subject findSubjectById(Long id) {
        return subjectDataService.findById(id);
    }

    @Override
    public List<Subject> findModifiedSubjects() {
        return subjectDataService.findSubjectsByModified(true);
    }

    @Override
    public List<Subject> getAll() {
        return subjectDataService.retrieveAll();
    }

    @Override
    public Subject create(Subject record) {
        return subjectDataService.create(record);
    }

    @Override
    public Subject update(Subject record) {
        return subjectDataService.update(record);
    }

    @Override
    public List<Subject> findSubjectsPrimerVaccinatedAtDay(LocalDate date) {
        return subjectDataService.findSubjectsByPrimerVaccinationDate(date);
    }

    @Override
    public List<Subject> findSubjectsBoosterVaccinatedAtDay(LocalDate date) {
        return subjectDataService.findSubjectsByBoosterVaccinationDate(date);
    }

    @Override
    public LocalDate findOldestPrimerVaccinationDate() {
        QueryParams queryParams = new QueryParams(new Order("primerVaccinationDate", Order.Direction.ASC));
        List<Subject> subjects = subjectDataService.retrieveAll(queryParams);
        if (subjects != null && !subjects.isEmpty()) {
            for(Subject subject : subjects) {
                if (subject.getPrimerVaccinationDate() != null) {
                    return subject.getPrimerVaccinationDate();
                }
            }
        }
        return LocalDate.now().minusDays(1);
    }

    @Override
    public void delete(Subject record) {
        subjectDataService.delete(record);
    }

    @Override
    public void deleteAll(){ subjectDataService.deleteAll(); }
}
