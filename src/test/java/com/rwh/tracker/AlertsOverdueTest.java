package com.rwh.tracker;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlertsOverdueTest extends SeleniumTestBase {

    @Test
    void testAlertsOverdue() {
        // 1. Create a System
        driver.get(getBaseUrl() + "/systems/new");
        String systemName = "Alert Sys " + System.currentTimeMillis();
        driver.findElement(By.name("name")).sendKeys(systemName);
        driver.findElement(By.name("location")).sendKeys("Location D");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(By.cssSelector(".system-card")));

        // 2. Go to system detail page
        driver.findElement(By.cssSelector(".system-card")).click();
        
        // 3. Add Component
        String compName = "Overdue Sensor " + System.currentTimeMillis();
        driver.findElement(By.name("name")).sendKeys(compName);
        new Select(driver.findElement(By.name("type"))).selectByValue("FILTER");
        driver.findElement(By.name("intervalDays")).sendKeys("1");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), '" + compName + "')]")));

        // 4. Log Maintenance 10 days ago
        WebElement logLink = driver.findElement(By.cssSelector("a[href*='/logs/new']"));
        driver.get(logLink.getAttribute("href"));
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(By.id("performedAt")));
        
        LocalDate tenDaysAgo = LocalDate.now().minusDays(10);
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", driver.findElement(By.id("performedAt")), tenDaysAgo.toString());
        driver.findElement(By.name("notes")).sendKeys("Retroactive log");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), 'successfully')]")));

        // 4. Navigate to Alerts
        driver.get(getBaseUrl() + "/alerts");

        // 5. Verify Component in Alerts
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains(compName), "Overdue component should appear in alerts list");
    }
}
