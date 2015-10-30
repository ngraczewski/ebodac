package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class UsersPage extends AbstractBasePage {

    public static final String URL_PATH = "#/webSecurity/users";
    static final By ADD_USER = By.linkText("Add user");
    static final By USERNAME = By.id("userName");
    static final By NEW_PASSWORD = By.name("newPassword");
    static final By CONFIRM_PASSWORD = By.name("confirmPassword");
    static final By CONFIRM = By.cssSelector("input.btn.btn-primary");
    static final By POPUP = By.id("popup_ok");
    public UsersPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }

    public boolean addUser(String userName, String password, String roles) throws InterruptedException{
        clickWhenVisible(ADD_USER);
        waitForElement(USERNAME);
        findElement(USERNAME).clear();
        findElement(USERNAME).sendKeys(userName);
        if(findElement(USERNAME).getAttribute("class").contains("ng-invalid-pwd")) {
            return false;
        }
        waitForElement(NEW_PASSWORD);
        findElement(NEW_PASSWORD).clear();
        findElement(NEW_PASSWORD).sendKeys(password);
        waitForElement(CONFIRM_PASSWORD);
        findElement(CONFIRM_PASSWORD).clear();
        findElement(CONFIRM_PASSWORD).sendKeys(password);
        clickOn(By.xpath("//input[@value='" + roles + "']"));
        clickWhenVisible(CONFIRM);
        clickOn(POPUP);
        return true;
    }
}