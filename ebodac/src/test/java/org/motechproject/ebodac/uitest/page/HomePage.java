package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.lang.Override;
import java.lang.String;


public class HomePage extends AbstractBasePage {

    public static final String URL_PATH = "/home";
    static final By EBODAC = By.linkText("EBODAC");
    static final By SECURITY = By.linkText("Security");
    public static final By DATA_SERVICES = By.linkText("Data Services");
    static final By EMAIL = By.linkText("Email");
    static final By MESSAGE_CAMPAIGN = By.linkText("Message Campaign");
    static final By IVR = By.linkText("IVR");
    static final By SMS = By.linkText("SMS");
    static final By SCHEDULER = By.linkText("Scheduler");
    static final By TASKS = By.linkText("Tasks");
    static final By MODULES = By.linkText("Modules");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }

    public void openEBODACModule() throws InterruptedException {
        clickWhenVisible(EBODAC);
    }
    public boolean isEBODACModulePresent() throws InterruptedException {
        try {
            if(findElement(EBODAC)!=null) {
                return true;
            }
            return false;
        } catch(Exception e) {
            return false;
        }
    }

    public boolean isDataServicesModulePresent() throws InterruptedException {
        try {
            if(findElement(DATA_SERVICES)!=null) {
                return true;
            }
            return false;
        } catch(Exception e) {
            return false;
        }
    }

    public void openSecurity() throws InterruptedException {
        clickWhenVisible(SECURITY);
    }

    public boolean isEmailModulePresent() throws InterruptedException {
        try {
            if(findElement(EMAIL)!=null) {
                return true;
            }
            return false;
        } catch(Exception e) {
            return false;
        }
    }

    public boolean isMessageCampaignModulePresent() throws InterruptedException {
        try {
            if(findElement(MESSAGE_CAMPAIGN)!=null) {
                return true;
            }
            return false;
        } catch(Exception e) {
            return false;
        }
    }

    public boolean isIVRModulePresent() throws InterruptedException {
        try {
            if(findElement(IVR)!=null) {
                return true;
            }
            return false;
        } catch(Exception e) {
            return false;
        }
    }

    public boolean isSMSModulePresent() throws InterruptedException {
        try {
            if(findElement(SMS)!=null) {
                return true;
            }
            return false;
        } catch(Exception e) {
            return false;
        }
    }

    public boolean isSchedulerModulePresent() throws InterruptedException {
        try {
            if(findElement(SCHEDULER)!=null) {
                return true;
            }
            return false;
        } catch(Exception e) {
            return false;
        }
    }

    public boolean isTasksModulePresent() throws InterruptedException {
        try {
            if(findElement(TASKS)!=null) {
                return true;
            }
            return false;
        } catch(Exception e) {
            return false;
        }
    }

    public void clickOnEbodac() throws InterruptedException{
        Thread.sleep(500);
        clickOn(EBODAC);
    }

    public void clickModules() throws InterruptedException{
        clickWhenVisible(MODULES);
    }

    public boolean isElementPresent(By by) {
        try {
            findElement(by);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
