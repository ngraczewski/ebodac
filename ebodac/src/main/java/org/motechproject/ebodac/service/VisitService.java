package org.motechproject.ebodac.service;

import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;

/**
 * Service interface for CRUD on Visit
 */
public interface VisitService {

    Visit create(Visit visit);

    Visit update(Visit visit);

    Visit createOrUpdate(Visit visit);

    void delete(Visit visit);

    Visit findVisitBySubjectIdAndVisitType(String subjectId, VisitType visitType);
}
