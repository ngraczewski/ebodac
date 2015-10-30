package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motech.page.LoginPage;
import org.motech.test.TestBase;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ParticipantEditPage;
import org.motechproject.ebodac.uitest.page.ParticipantPage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;


public class ChangeLanguageUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private ParticipantPage participantPage;
    private ParticipantEditPage participantEditPage;
    private String L1adminUser;
    private String L1adminpassword;
    private String testLanguage;
    private String changedLanguage;
    private UITestHttpClientHelper httpClientHelper;
    private String url;

    @Before
    public void setUp() {
        L1adminUser = properties.getUserName();
        L1adminpassword = properties.getPassword();
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        ebodacPage = new EBODACPage(driver);
        participantPage = new ParticipantPage(driver);
        participantEditPage = new ParticipantEditPage(driver);
        url = properties.getWebAppUrl();
        if(url.contains("localhost")) {
            httpClientHelper = new UITestHttpClientHelper(url);
            httpClientHelper.addParticipant(new TestParticipant());
        }
        if(homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.login(L1adminUser, L1adminpassword);
        }
    }

    @Test
    public void changeLanguageTest() throws Exception {
        homePage.openEBODACModule();
        ebodacPage.showParticipants();
        participantPage.openFirstParticipant();
        testLanguage = participantEditPage.changeLanguage("1");
        changedLanguage = participantPage.getFirstParticipantLanguage();
        assertEquals(changedLanguage, testLanguage);
    }

    @After
    public void tearDown() throws Exception {
        loginPage.logOut();
    }
}
