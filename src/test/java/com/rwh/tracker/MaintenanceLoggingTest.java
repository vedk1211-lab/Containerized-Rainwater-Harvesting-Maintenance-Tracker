package com.rwh.tracker;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MaintenanceLoggingTest extends SeleniumTestBase {

    @Test
    void testMaintenanceLogging() {
        // 1. Create a System
        driver.get(getBaseUrl() + "/systems/new");
        String systemName = "Maint Sys " + System.currentTimeMillis();
        driver.findElement(By.name("name")).sendKeys(systemName);
        driver.findElement(By.name("location")).sendKeys("Location B");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(By.cssSelector(".system-card")));

        // 2. Go to system detail page
        driver.findElement(By.cssSelector(".system-card")).click();
        
        // 3. Add Component
        String compName = "PUMP " + System.currentTimeMillis();
        driver.findElement(By.name("name")).sendKeys(compName);
        new Select(driver.findElement(By.name("type"))).selectByValue("PUMP");
        driver.findElement(By.name("intervalDays")).sendKeys("30");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), '" + compName + "')]")));

        // 4. Log Maintenance
        WebElement logLink = driver.findElement(By.cssSelector("a[href*='/logs/new']"));
        driver.get(logLink.getAttribute("href"));
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(By.id("performedAt")));

        String notes = "Replaced seal";
        // performedAt defaults to today, but we can set it anyway just in case, though it's pre-filled
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", driver.findElement(By.id("performedAt")), LocalDate.now().toString());
        driver.findElement(By.name("notes")).sendKeys(notes);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), 'successfully')]")));

        // 5. Verify log on Component History page
        assertTrue(driver.getCurrentUrl().contains("/systems"));
        assertTrue(driver.getPageSource().contains("successfully"));
    }
}
