package com.rwh.tracker;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class SeleniumTestBase {

    @LocalServerPort
    protected int port;

    protected WebDriver driver;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @RegisterExtension
    ScreenshotExtension screenshotExtension = new ScreenshotExtension();

    class ScreenshotExtension implements TestWatcher {
        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            takeScreenshot(context);
            quitDriver();
        }
        @Override
        public void testSuccessful(ExtensionContext context) {
            quitDriver();
        }
        @Override
        public void testAborted(ExtensionContext context, Throwable cause) {
            quitDriver();
        }
        @Override
        public void testDisabled(ExtensionContext context, Optional<String> reason) {
            quitDriver();
        }
        
        private void takeScreenshot(ExtensionContext context) {
            if (driver instanceof TakesScreenshot) {
                File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                String testName = context.getTestMethod().map(m -> m.getName()).orElse("unknown_test");
                Path destPath = Paths.get("target", "screenshots", testName + "-failure.png");
                Path htmlPath = Paths.get("target", "screenshots", testName + "-failure.html");
                try {
                    Files.createDirectories(destPath.getParent());
                    Files.copy(srcFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Screenshot saved to: " + destPath.toAbsolutePath());
                    Files.writeString(htmlPath, driver.getPageSource());
                    System.out.println("Page source saved to: " + htmlPath.toAbsolutePath());
                } catch (IOException e) {
                    System.err.println("Failed to save screenshot or page source: " + e.getMessage());
                }
            }
        }
        
        private void quitDriver() {
            if (driver != null) {
                driver.quit();
            }
        }
    }
}
