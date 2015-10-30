package org.motechproject.ebodac.service;

import org.motechproject.ebodac.domain.Subject;

import java.util.Set;

public interface ReportUpdateService {

    Set<String> getPrimerVaccinationReportsToUpdate();

    Set<String> getBoosterVaccinationReportsToUpdate();

    void addReportsToUpdateIfNeeded(Subject oldSubject, Subject newSubject);

    void setConfigService(ConfigService configService);
}
