package org.motechproject.ebodac.service.impl.csv;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.mds.service.DefaultCsvImportCustomizer;
import org.motechproject.mds.service.MotechDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SubjectCsvImportCustomizer extends DefaultCsvImportCustomizer {

    private SubjectService subjectService;

    @Override
    public Object findExistingInstance(Map<String, String> row, MotechDataService motechDataService) {
        String subjectId = row.get(EbodacConstants.SUBJECT_ID_FIELD_NAME);

        if (StringUtils.isNotBlank(subjectId)) {
            return subjectService.findSubjectBySubjectId(subjectId);
        }
        return null;
    }

    @Override
    public Object doCreate(Object instance, MotechDataService motechDataService) {
        return subjectService.create((Subject) instance);
    }

    @Override
    public Object doUpdate(Object instance, MotechDataService motechDataService) {
        return subjectService.update((Subject) instance);
    }

    public SubjectService getSubjectService() {
        return subjectService;
    }

    @Autowired
    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
    }
}
