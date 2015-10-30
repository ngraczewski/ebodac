package org.motechproject.ebodac.service;

import org.joda.time.DateTime;

/**
 * Simple example of a service interface.
 */
public interface EbodacService {

    void sendUpdatedSubjects(String zetesUrl, String username, String password);

    void fetchCSVUpdates();

    void fetchCSVUpdates(DateTime startDate);
}
