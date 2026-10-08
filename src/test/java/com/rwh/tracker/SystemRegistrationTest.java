package com.rwh.tracker;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SystemRegistrationTest extends SeleniumTestBase {

    @Test
    void testSystemRegistration() {
        driver.get(getBaseUrl() + "/systems/new");

        WebElement nameInput = driver.findElement(By.name("name"));
        nameInput.sendKeys("Test System " + System.currentTimeMillis());

        WebElement locationInput = driver.findElement(By.name("location"));
        locationInput.sendKeys("Test Location");

        WebElement submitBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        submitBtn.click();
        
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(By.cssSelector(".system-card")));

        assertTrue(driver.getPageSource().contains("Test System"));
    }
}
