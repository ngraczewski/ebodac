package org.motechproject.ebodac.service;

import org.joda.time.LocalDate;
import org.motechproject.ebodac.domain.Subject;

import java.util.List;

/**
 * Service interface for CRUD on Subject
 */
public interface SubjectService {

    Subject createOrUpdateForZetes(Subject newSubject);

    Subject createOrUpdateForRave(Subject newSubject);

    List<Subject> findSubjectByName(String name);

    Subject findSubjectBySubjectId(String subjectId);

    Subject findSubjectById(Long id);

    List<Subject> findModifiedSubjects();

    List<Subject> getAll();

    void delete(Subject record);

    void deleteAll();

    Subject create(Subject record);

    Subject update(Subject record);

    List<Subject> findSubjectsPrimerVaccinatedAtDay(LocalDate date);

    List<Subject> findSubjectsBoosterVaccinatedAtDay(LocalDate date);

    LocalDate findOldestPrimerVaccinationDate();
}
