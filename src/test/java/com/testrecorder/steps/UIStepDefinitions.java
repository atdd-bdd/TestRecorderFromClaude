package com.testrecorder.steps;

import com.testrecorder.domain.*;
import com.testrecorder.repository.DatabaseTestRepository;
import com.testrecorder.service.*;
import com.testrecorder.ui.TestTablePanel;
import com.testrecorder.ui.TestRecorderFrame;
import javax.swing.*;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import java.sql.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class UIStepDefinitions {
    private Configuration configuration;
    private DatabaseTestRepository repository;
    private TestService testService;
    private DateTimeProvider dateTimeProvider;
    private RunnerProvider runnerProvider;
    private TestTablePanel testTablePanel;
    private TestRecorderFrame testRecorderFrame;

    public UIStepDefinitions() {
        this.configuration = new Configuration();
    }

    private void ensureRepository() throws SQLException {
        if (repository == null) {
            repository = new DatabaseTestRepository(configuration);
            initializeProviders();
            testService = new TestService(repository, dateTimeProvider, runnerProvider);
        }
    }

    private void initializeProviders() {
        if (configuration.isUseTestDoubleForDateTime()) {
            TestDate testDate = TestDate.parse(configuration.getValueTestDoubleForDateTime());
            this.dateTimeProvider = new TestDoubleDateTimeProvider(testDate);
        } else {
            this.dateTimeProvider = new SystemDateTimeProvider();
        }

        if (configuration.isUseTestDoubleForRunner()) {
            this.runnerProvider = new TestDoubleRunnerProvider(configuration.getValueTestDoubleForRunner());
        } else {
            this.runnerProvider = new SystemRunnerProvider();
        }
    }

    @When("test table swing is shown")
    public void test_table_swing_is_shown() throws Exception {
        ensureRepository();

        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }

        // Create and show the frame on the EDT
        final Object lock = new Object();
        final String rootPath = configuration.getRootFilePath();
        SwingUtilities.invokeLater(() -> {
            testRecorderFrame = new TestRecorderFrame(testService, true, rootPath);
            testRecorderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            testRecorderFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    synchronized (lock) {
                        lock.notify();
                    }
                }
            });
            testRecorderFrame.showAndRefresh();
        });

        // Wait for the user to close the window
        synchronized (lock) {
            lock.wait();
        }
    }

    @When("test table swing is shown with test run data")
    public void test_table_swing_is_shown_with_test_run_data(DataTable dataTable) throws Exception {
        ensureRepository();
        Map<String, String> data = dataTable.asMap();

        String result = data.get("Result");
        String comments = data.get("Comments");

        // Build instruction message
        StringBuilder message = new StringBuilder();
        message.append("Please enter the following test run data:\n\n");
        message.append("Result: ").append(result).append("\n");
        message.append("Comments: ").append(comments).append("\n\n");
        message.append("1. Select the test in the table\n");
        message.append("2. Click 'Run Test' button\n");
        message.append("3. Enter the data above\n");
        message.append("4. Click 'Run' to save\n");
        message.append("5. Close the application when done");

        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }

        // Show instruction dialog
        JOptionPane.showMessageDialog(null,
            message.toString(),
            "Manual Test Instructions",
            JOptionPane.INFORMATION_MESSAGE);

        // Create and show the frame on the EDT
        final Object lock = new Object();
        final String rootPath = configuration.getRootFilePath();
        SwingUtilities.invokeLater(() -> {
            testRecorderFrame = new TestRecorderFrame(testService, true, rootPath);
            testRecorderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            testRecorderFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    synchronized (lock) {
                        lock.notify();
                    }
                }
            });
            testRecorderFrame.showAndRefresh();
        });

        // Wait for the user to close the window
        synchronized (lock) {
            lock.wait();
        }
    }

    @Then("test table should show that data")
    public void test_table_should_show_that_data() {
        JOptionPane.showMessageDialog(null,
            "Please verify that the test table displays the expected data.\n\n" +
            "Click OK when verification is complete.",
            "Manual Verification Required",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private String[] getSelectedTest() throws SQLException {
        try (Connection conn = DriverManager.getConnection(
                configuration.getDatabaseURL(),
                configuration.getDatabaseUserID(),
                configuration.getDatabasePassword());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT issue_id, sub_issue_id FROM selected_test")) {
            if (rs.next()) {
                return new String[]{rs.getString("issue_id"), rs.getString("sub_issue_id")};
            }
        }
        throw new RuntimeException("No test selected");
    }
}
