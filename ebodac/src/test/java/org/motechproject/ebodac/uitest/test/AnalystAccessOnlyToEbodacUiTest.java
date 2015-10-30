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

public class AnalystAccessOnlyToEbodacUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private String L1analystUser;
    private String L1analystpassword;
    private UserPropertiesHelper userPropertiesHelper;
    private UITestHttpClientHelper httpClientHelper;
    private CreateUsersHelper createUsersHelper;
    private String url;

    @Before
    public void setUp() throws  Exception {
        url = properties.getWebAppUrl();
        if(url.contains("localhost")) {
            createUsersHelper = new CreateUsersHelper(driver);
            createUsersHelper.createUsersWithLogin(properties);
        }
        userPropertiesHelper = new UserPropertiesHelper();
        L1analystUser = userPropertiesHelper.getAnalystUserName();
        L1analystpassword = userPropertiesHelper.getAnalystPassword();
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        if(url.contains("localhost")) {
            httpClientHelper = new UITestHttpClientHelper(url);
            httpClientHelper.addParticipant(new TestParticipant());
        }
        if(homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.login(L1analystUser, L1analystpassword);
        }
    }

    @Test //Test fo EBODAC-528
    public void analystAccessOnlyToEbodacUITest() throws Exception {
        homePage.clickModules();
        Assert.assertFalse(homePage.isElementPresent(homePage.DATA_SERVICES));
    }

    @After
    public void tearDown() throws Exception {
        loginPage.logOut();
    }
}
