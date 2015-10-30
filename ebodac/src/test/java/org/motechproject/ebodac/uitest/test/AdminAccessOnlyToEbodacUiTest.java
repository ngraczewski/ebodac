package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motech.page.LoginPage;
import org.motech.test.TestBase;
import org.motechproject.ebodac.uitest.helper.CreateUsersHelper;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import org.motechproject.ebodac.uitest.page.HomePage;
public class AdminAccessOnlyToEbodacUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private String L1adminUser;
    private String L1adminpassword;
    private UITestHttpClientHelper httpClientHelper;
    private UserPropertiesHelper userPropertiesHelper;
    private CreateUsersHelper createUsersHelper;
    private String url;

    @Before
    public void setUp() throws Exception{
        url = properties.getWebAppUrl();
        if(url.contains("localhost")){
            createUsersHelper = new CreateUsersHelper(driver);
            createUsersHelper.createUsersWithLogin(properties);
        }
        userPropertiesHelper = new UserPropertiesHelper();
        L1adminUser = userPropertiesHelper.getAdminUserName();
        L1adminpassword = userPropertiesHelper.getAdminPassword();
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        if(url.contains("localhost")) {
            httpClientHelper = new UITestHttpClientHelper(url);
            httpClientHelper.addParticipant(new TestParticipant());
        }
        if(homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.login(L1adminUser, L1adminpassword);
        }
    }

    @Test //Test for EBODAC-531
    public void adminAccessOnlyToEbodacUiTest() throws Exception {
        homePage.clickModules();
        Assert.assertFalse(homePage.isElementPresent(homePage.DATA_SERVICES));
    }

    @After
    public void tearDown() throws Exception {
        loginPage.logOut();
    }
}
