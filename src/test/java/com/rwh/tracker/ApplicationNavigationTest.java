package com.rwh.tracker;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Selenium UI test verifying application loading, title, dashboard visibility,
 * and navigation across core navbar links and pages.
 */
public class ApplicationNavigationTest extends SeleniumTestBase {

    @Test
    void testApplicationRootAndDashboardLoading() {
        driver.get(getBaseUrl() + "/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("navbar-brand")));

        // Verify root redirects to dashboard
        assertTrue(driver.getCurrentUrl().contains("/dashboard"),
                "Root URL / should redirect to /dashboard");

        // Verify page title
        assertTrue(driver.getTitle().contains("RWH Tracker"),
                "Page title should contain RWH Tracker");

        // Verify dashboard header and summary cards are visible
        WebElement header = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h2")));
        assertTrue(header.getText().contains("Dashboard"),
                "Dashboard page header should contain 'Dashboard'");

        assertTrue(driver.getPageSource().contains("Healthy"),
                "Dashboard should display Healthy status card");
        assertTrue(driver.getPageSource().contains("Due Soon"),
                "Dashboard should display Due Soon status card");
        assertTrue(driver.getPageSource().contains("Overdue"),
                "Dashboard should display Overdue status card");
    }

    @Test
    void testNavbarNavigationAcrossMainPages() {
        driver.get(getBaseUrl() + "/dashboard");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // 1. From Dashboard, navigate to Systems page via navbar
        WebElement systemsNavLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@class, 'nav-link') and contains(@href, '/systems')]")));
        systemsNavLink.click();

        wait.until(ExpectedConditions.urlContains("/systems"));
        assertTrue(driver.getPageSource().contains("Registered Systems") || driver.getPageSource().contains("Systems"),
                "Systems page should be loaded");

        // 2. From Systems, navigate to New System form via navbar button
        WebElement newSystemNavLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/systems/new')]")));
        newSystemNavLink.click();

        wait.until(ExpectedConditions.urlContains("/systems/new"));
        assertTrue(driver.findElement(By.name("name")).isDisplayed(),
                "System name input field should be visible on registration form");
        assertTrue(driver.findElement(By.name("location")).isDisplayed(),
                "System location input field should be visible on registration form");

        // 3. From Dashboard, navigate to Alerts page via navbar
        driver.get(getBaseUrl() + "/dashboard");
        WebElement alertsNavLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@class, 'nav-link') and contains(@href, '/alerts')]")));
        alertsNavLink.click();

        wait.until(ExpectedConditions.urlContains("/alerts"));
        assertTrue(driver.getPageSource().contains("Alerts"),
                "Alerts page should be loaded");

        // 4. Return to home/dashboard by clicking brand logo
        WebElement brandLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.className("navbar-brand")));
        brandLink.click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));
        assertTrue(driver.getCurrentUrl().contains("/dashboard"),
                "Clicking navbar brand should navigate back to /dashboard");
    }
}
