package com.testrecorder;

import com.testrecorder.domain.Configuration;
import com.testrecorder.repository.DatabaseTestRepository;
import com.testrecorder.repository.InMemoryTestRepository;
import com.testrecorder.repository.TestRepository;
import com.testrecorder.service.*;
import com.testrecorder.ui.TestRecorderFrame;
import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Main application entry point for Test Recorder
 */
public class TestRecorderApplication {

    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }

        SwingUtilities.invokeLater(() -> {
            try {
                startApplication();
            } catch (Exception e) {
                System.err.println("Failed to start Test Recorder Application: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Failed to start application: " + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }

    private static void startApplication() {
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

        // Initialize repository - try database first, fall back to in-memory
        TestRepository repository;
        boolean databaseConnected = false;

        try {
            repository = new DatabaseTestRepository(config);
            databaseConnected = true;
            System.out.println("Connected to database: " + config.getDatabaseURL());
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            System.out.println("Falling back to in-memory repository");
            repository = new InMemoryTestRepository();
        }

        // Create service
        TestService testService = new TestService(repository, dateTimeProvider, runnerProvider);

        // Create and show UI
        TestRecorderFrame frame = new TestRecorderFrame(testService, databaseConnected, config.getRootFilePath());
        frame.showAndRefresh();

        System.out.println("Test Recorder Application started successfully");
        System.out.println("Root file path: " + config.getRootFilePath());
        System.out.println("Database URL: " + config.getDatabaseURL());
        System.out.println("Database connected: " + databaseConnected);
    }
}
