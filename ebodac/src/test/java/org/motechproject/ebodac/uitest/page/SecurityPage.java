package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class SecurityPage extends AbstractBasePage {

    public static final String URL_PATH = "/home";
    static final By MANAGE_USERS = By.linkText("Manage Users");
    public SecurityPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return URL_ROOT;
    }

    public void openUserManagement() throws InterruptedException {
        clickWhenVisible(MANAGE_USERS);
    }

}
