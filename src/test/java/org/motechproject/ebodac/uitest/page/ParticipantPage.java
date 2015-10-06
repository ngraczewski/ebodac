package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class ParticipantPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final By PARTICIPANT = By.xpath("//table[@id='instancesTable']/tbody/tr[2]");
    static final By PHONE_NUMBER = By.xpath("//table[@id='instancesTable']/tbody/tr[2]/td[@aria-describedby='instancesTable_phoneNumber']");
    public ParticipantPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }

    public void openFirstParticipant() throws InterruptedException {
        try {
            Thread.sleep(500);
            findElement(PARTICIPANT);
        } catch(Exception e) {
            throw new AssertionError("No participants present");
        }
        clickWhenVisible(PARTICIPANT);
    }


    public String getFirstParticipantNumber() {
        return findElement(PHONE_NUMBER).getText();
    }
}
