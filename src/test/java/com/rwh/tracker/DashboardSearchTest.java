package com.rwh.tracker;

import com.rwh.tracker.model.Component;
import com.rwh.tracker.model.ComponentType;
import com.rwh.tracker.model.RwhSystem;
import com.rwh.tracker.service.SystemService;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DashboardSearchTest extends SeleniumTestBase {

    @Autowired
    private SystemService systemService;

    @Test
    void testDashboardSearch() throws Exception {
        // Create test data programmatically to isolate the search test
        long ts = System.currentTimeMillis();
        String comp1Name = "AlphaFilter" + ts;
        String comp2Name = "BetaTank" + ts;

        RwhSystem system = new RwhSystem();
        system.setName("DashSys" + ts);
        system.setLocation("Location C");
        system = systemService.createSystem(system);

        Component c1 = new Component();
        c1.setName(comp1Name);
        c1.setType(ComponentType.FILTER);
        c1.setIntervalDays(30);
        systemService.addComponent(system.getId(), c1);

        Component c2 = new Component();
        c2.setName(comp2Name);
        c2.setType(ComponentType.TANK);
        c2.setIntervalDays(60);
        systemService.addComponent(system.getId(), c2);

        // Verify both components appear on unfiltered dashboard
        driver.get(getBaseUrl() + "/dashboard");
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table")));

        String unfilteredSource = driver.getPageSource();
        assertTrue(unfilteredSource.contains(comp1Name),
                "comp1 should exist on unfiltered dashboard");
        assertTrue(unfilteredSource.contains(comp2Name),
                "comp2 should exist on unfiltered dashboard");

        // Search for comp1 only
        driver.get(getBaseUrl() + "/dashboard?q=" + comp1Name);
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table")));

        String filteredSource = driver.getPageSource();
        assertTrue(filteredSource.contains(comp1Name),
                "comp1 should appear in filtered results");
        assertFalse(filteredSource.contains(comp2Name),
                "comp2 should NOT appear in filtered results");
    }
}
