package org.motechproject.ebodac.service;

import org.motechproject.ebodac.domain.Config;

public interface ConfigService {

    Config getConfig();

    void updateConfig(Config config);
}
