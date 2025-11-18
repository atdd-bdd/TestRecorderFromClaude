package com.testrecorder;

import com.testrecorder.domain.Configuration;
import com.testrecorder.repository.InMemoryTestRepository;
import com.testrecorder.service.*;
import com.testrecorder.ui.TestTablePanel;
import java.io.IOException;

/**
 * Main application entry point for Test Recorder
 */
public class TestRecorderApplication {
    
    public static void main(String[] args) {
        try {
            // Load configuration
            Configuration config = new Configuration();
            String configFile = System.getProperty("config.file", "config.properties");
            try {
                config.load(configFile);
                System.out.println("Configuration loaded from: " + configFile);
            } catch (IOException e) {
                System.out.println("Using default configuration");
            }

            // Initialize providers
            DateTimeProvider dateTimeProvider;
            RunnerProvider runnerProvider;

            if (config.isUseTestDoubleForDateTime()) {
                dateTimeProvider = new TestDoubleDateTimeProvider(
                    com.testrecorder.domain.TestDate.parse(config.getValueTestDoubleForDateTime())
                );
            } else {
                dateTimeProvider = new SystemDateTimeProvider();
            }

            if (config.isUseTestDoubleForRunner()) {
                runnerProvider = new TestDoubleRunnerProvider(config.getValueTestDoubleForRunner());
            } else {
                runnerProvider = new SystemRunnerProvider();
            }

            // Initialize repository and service
            InMemoryTestRepository repository = new InMemoryTestRepository();
            TestService testService = new TestService(repository, dateTimeProvider, runnerProvider);

            // Create and show UI
            TestTablePanel panel = new TestTablePanel();
            panel.loadTests(testService.getAllTests());
            panel.show();

            System.out.println("Test Recorder Application started successfully");
            System.out.println("Root file path: " + config.getRootFilePath());
            System.out.println("Database URL: " + config.getDatabaseURL());

        } catch (Exception e) {
            System.err.println("Failed to start Test Recorder Application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
