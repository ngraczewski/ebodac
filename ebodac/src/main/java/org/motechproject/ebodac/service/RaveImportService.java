package org.motechproject.ebodac.service;

import java.io.Reader;

public interface RaveImportService {

    void importCsv(final Reader reader, String filename);
}
