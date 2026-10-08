package com.rwh.tracker;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComponentAdditionTest extends SeleniumTestBase {

    @Test
    void testComponentAddition() {
        // 1. Create a System
        driver.get(getBaseUrl() + "/systems/new");
        String systemName = "SysWithComp " + System.currentTimeMillis();
        driver.findElement(By.name("name")).sendKeys(systemName);
        driver.findElement(By.name("location")).sendKeys("Location A");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(By.cssSelector(".system-card")));

        // 2. Go to system detail page
        driver.findElement(By.cssSelector(".system-card")).click();
        
        // 3. Add Component
        String compName = "Main Filter " + System.currentTimeMillis();
        driver.findElement(By.name("name")).sendKeys(compName);
        new Select(driver.findElement(By.name("type"))).selectByValue("FILTER");
        driver.findElement(By.name("intervalDays")).sendKeys("30");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), '" + compName + "')]")));

        // 4. Verify Component on system page
        assertTrue(driver.getPageSource().contains(compName));
        assertTrue(driver.getPageSource().contains("FILTER"));
    }
}
