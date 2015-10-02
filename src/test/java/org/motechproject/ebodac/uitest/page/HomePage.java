package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.lang.Override;
import java.lang.String;


public class HomePage extends AbstractBasePage {

    public static final String URL_PATH = "/home";
    static final By EBODAC = By.linkText("EBODAC");
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


}
