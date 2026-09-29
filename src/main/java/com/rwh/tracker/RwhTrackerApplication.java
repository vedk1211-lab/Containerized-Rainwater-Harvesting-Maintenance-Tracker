package com.rwh.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Main entry point for the RWH Tracker application.
 * Extends {@link SpringBootServletInitializer} for WAR deployment to standalone Tomcat.
 */
@SpringBootApplication
public class RwhTrackerApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(RwhTrackerApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(RwhTrackerApplication.class, args);
    }
}
