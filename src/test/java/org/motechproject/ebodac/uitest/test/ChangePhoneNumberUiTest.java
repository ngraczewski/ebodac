package org.motechproject.ebodac.uitest.test;

import org.motech.page.LoginPage;
import org.motech.test.TestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ParticipantEditPage;
import org.motechproject.ebodac.uitest.page.ParticipantPage;

import java.lang.Exception;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;

public class ChangePhoneNumberUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private ParticipantPage participantPage;
    private ParticipantEditPage participantEditPage;
    private String L1adminUser;
    private String L1adminpassword;
    private String testNumber = "55577755";
    private String changedNumber;
    private Properties motechproperties;
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

    @Test //Test for EBODAC-508
    public void changePhoneNumberTest() throws Exception {
        homePage.openEBODACModule();
        ebodacPage.showParticipants();
        participantPage.openFirstParticipant();
        participantEditPage.changePhoneNumber(testNumber);
        changedNumber = participantPage.getFirstParticipantNumber();
        assertEquals(changedNumber, testNumber);
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
