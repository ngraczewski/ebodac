package org.motechproject.ebodac.uitest.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

public class UserPropertiesHelper {

    private static org.motech.page.TestProperties SINGLETON;



    public static final String L1ADMIN_PASSWORD_PROPERTY = "admin.password";

    public static final String L1ANALYST_PASSWORD_PROPERTY = "analyst.password";

    public static final String DEFAULT_L1ADMIN_PASSWORD = "testadmin";

    public static final String DEFAULT_L1ANALYST_PASSWORD = "testanalyst";

    public static final String L1ADMIN_USERNAME_PROPERTY = "admin.username";

    public static final String L1ANALYST_USERNAME_PROPERTY = "analyst.username";

    public static final String DEFAULT_L1ADMIN_USERNAME = "admin";

    public static final String DEFAULT_L1ANALYST_USERNAME = "analyst";

    public static org.motech.page.TestProperties instance() {
        if (SINGLETON == null) {
            SINGLETON = new org.motech.page.TestProperties();
        }
        return SINGLETON;
    }

    private Properties properties;

    public UserPropertiesHelper() {
        properties = new Properties();
        try {
            URL resource = Thread.currentThread().getContextClassLoader()
                    .getResource("org/motech/uitestframework/test.properties");
            if (resource != null) {
                System.out.println("test.properties found: " + resource.toExternalForm());
                InputStream input = resource.openStream();
                properties.load(new InputStreamReader(input, "UTF-8"));
                System.out.println("test.properties:");
                System.out.println(properties);
            }
        }
        catch (IOException ioException) {
            throw new RuntimeException("test.properties not found. Error: ", ioException);
        }
        System.out.println(L1ADMIN_USERNAME_PROPERTY + ": " + getAdminUserName());
        System.out.println(L1ADMIN_PASSWORD_PROPERTY + ": " + getAdminPassword());
        System.out.println(L1ANALYST_USERNAME_PROPERTY + ": " + getAnalystUserName());
        System.out.println(L1ANALYST_PASSWORD_PROPERTY + ": " + getAnalystPassword());
    }


    public String getAdminUserName() {
        return getProperty(L1ADMIN_USERNAME_PROPERTY, DEFAULT_L1ADMIN_USERNAME);
    }

    public String getAdminPassword() {
        return getProperty(L1ADMIN_PASSWORD_PROPERTY, DEFAULT_L1ADMIN_PASSWORD);
    }

    public String getAnalystUserName() {
        return getProperty(L1ANALYST_USERNAME_PROPERTY, DEFAULT_L1ANALYST_USERNAME);
    }

    public String getAnalystPassword() {
        return getProperty(L1ANALYST_PASSWORD_PROPERTY, DEFAULT_L1ANALYST_PASSWORD);
    }


    public String getProperty(String property, String defaultValue) {
        String value = System.getProperty(property);
        if (value == null) {
            value = System.getenv(property);
        }
        if (value == null) {
            value = properties.getProperty(property);
        }
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }
}
