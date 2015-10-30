package org.motechproject.ebodac.uitest.test;

import org.junit.Before;
import org.junit.Test;
import org.motech.page.LoginPage;
import org.motech.test.TestBase;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.HomePage;

import static junit.framework.Assert.assertTrue;

public class MotechAccessToAllModuelsUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private String user;
    private String password;
    private UITestHttpClientHelper httpClientHelper;
    private String url;

    @Before
    public void setUp() {
        user = properties.getUserName();
        password = properties.getPassword();
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        url = properties.getWebAppUrl();
        if(url.contains("localhost")) {
            httpClientHelper = new UITestHttpClientHelper(url);
            httpClientHelper.addParticipant(new TestParticipant());
        }
        if(homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.login(user, password);
        }
    }

    @Test
    public void motechAccessToAllModulesUiTest() throws Exception {
        assertTrue(homePage.isDataServicesModulePresent());
        assertTrue(homePage.isEBODACModulePresent());
        assertTrue(homePage.isEmailModulePresent());
        assertTrue(homePage.isMessageCampaignModulePresent());
        assertTrue(homePage.isIVRModulePresent());
        assertTrue(homePage.isSMSModulePresent());
        assertTrue(homePage.isSchedulerModulePresent());
        assertTrue(homePage.isTasksModulePresent());
    }

    public void tearDown() throws Exception {
        loginPage.logOut();
    }
}
