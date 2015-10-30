package org.motechproject.ebodac.uitest.helper;

import org.motech.page.LoginPage;
import org.motech.page.TestProperties;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.SecurityPage;
import org.motechproject.ebodac.uitest.page.UsersPage;
import org.openqa.selenium.WebDriver;

public class CreateUsersHelper {
    private WebDriver driver;

    private HomePage homePage;
    private SecurityPage securityPage;
    private UsersPage usersPage;
    private LoginPage loginPage;

    public CreateUsersHelper(WebDriver driver) {
        this.driver = driver;
        homePage = new HomePage(driver);
        securityPage = new SecurityPage(driver);
        usersPage = new UsersPage(driver);
        loginPage = new LoginPage(driver);
    }
    public boolean createUser(String userName, String password, String roles) throws InterruptedException {
        homePage.openSecurity();
        securityPage.openUserManagement();
        return usersPage.addUser(userName,password,roles);
    }

    public boolean createAdminUser() throws InterruptedException {
        return createUser("admin", "testadmin", "EBODAC Administrator");
    }

    public boolean createAnalystUser() throws InterruptedException  {
        return createUser("analyst","testanalyst","EBODAC Site-Analyst");
    }

    public void createUsers() throws InterruptedException {
        if(createAdminUser()) {
            createAnalystUser();
        }
    }

    public void createUsersWithLogin(TestProperties properties) throws InterruptedException {
        loginPage.login(properties.getUserName(), properties.getPassword());
        createUsers();
        loginPage.logOut();
    }
}
