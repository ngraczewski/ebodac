package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class ParticipantEditPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final By PHONE_NUMBER_FIELD = By.id("phoneNumberForm");
    static final By SAVE_BUTTON = By.xpath("//div[@id='dataBrowser']/div/div/div/ng-form/div[2]/div/button");
    static final By CONFIRMATION_BUTTON = By.xpath("//div[@id='editSubjectModal']/div[2]/div/div[3]/button");
    static final By LANGUAGE_FIELD = By.xpath("(//button[@type='button'])[2]");
    static final String LANGUAGE_PATH = "//div[@id='dataBrowser']/div/div/div/ng-form/div/form/div[8]/div/ng-form/div/div/ul/li";
    static final String LANGUAGE_PATH_END = "/a/label";
    public ParticipantEditPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }

    public void changePhoneNumber(String number) throws InterruptedException {
        findElement(PHONE_NUMBER_FIELD).clear();
        changeFocus();
        Thread.sleep(500);
        findElement(PHONE_NUMBER_FIELD).sendKeys(number);
        changeFocus();
        Thread.sleep(500);
        clickOn(SAVE_BUTTON);
        clickWhenVisible(CONFIRMATION_BUTTON);
    }

    private void changeFocus() {
        findElement(By.className("form-control")).click();
        findElement(By.className("form-control")).sendKeys("");
    }

    public String changeLanguage(String languagePos) throws InterruptedException  {
        clickWhenVisible(LANGUAGE_FIELD);
        String language = chooseLanguage(languagePos);
        Thread.sleep(500);
        clickOn(SAVE_BUTTON);
        clickWhenVisible(CONFIRMATION_BUTTON);
        return language;
    }

    private String chooseLanguage(String languagePos) {
        try {
            clickOn(By.xpath(LANGUAGE_PATH + "[" + languagePos + "]" + LANGUAGE_PATH_END));
            return findElement(By.xpath(LANGUAGE_PATH + "[" + languagePos + "]" + LANGUAGE_PATH_END + "/input")).getText();
        } catch(Exception e) {
            throw new AssertionError("No language at chosen position");
        }
    }
}
