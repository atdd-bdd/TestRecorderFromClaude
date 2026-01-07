package com.testrecorder.steps;

import com.testrecorder.domain.*;
import com.testrecorder.repository.DatabaseTestRepository;
import com.testrecorder.service.*;
import com.testrecorder.ui.TestTablePanel;
import com.testrecorder.ui.TestRecorderFrame;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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

    @When("the application is started")
    public void the_application_is_started() throws Exception {
        ensureRepository();

        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }

        // Create and show the frame on the EDT
        final String rootPath = configuration.getRootFilePath();
        SwingUtilities.invokeAndWait(() -> {
            testRecorderFrame = new TestRecorderFrame(testService, true, rootPath);
            testRecorderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            testRecorderFrame.showAndRefresh();
        });
    }

    @When("the user adds a test")
    public void the_user_adds_a_test(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap();

        StringBuilder message = new StringBuilder();
        message.append("Please add a test with the following data:\n\n");
        message.append("Issue ID: ").append(data.get("Issue ID")).append("\n");
        message.append("Sub Issue ID: ").append(data.get("Sub Issue ID")).append("\n");
        message.append("Name: ").append(data.get("Name")).append("\n");
        message.append("File Path: ").append(data.get("File Path")).append("\n\n");
        message.append("1. Click 'Add Test' button\n");
        message.append("2. Enter the data above\n");
        message.append("3. Click 'Add' to save\n");
        message.append("4. Click 'OK - Done' when complete");

        showModelessInstructions("Manual Test - Add Test", message.toString());
    }

    @Then("tests displayed are")
    public void tests_displayed_are(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();

        StringBuilder message = new StringBuilder();
        message.append("Please verify the tests displayed match:\n\n");

        for (int i = 0; i < rows.size(); i++) {
            Map<String, String> row = rows.get(i);
            message.append("Test ").append(i + 1).append(":\n");
            for (Map.Entry<String, String> entry : row.entrySet()) {
                message.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            message.append("\n");
        }

        message.append("Click 'OK - Done' when verification is complete.");

        showModelessInstructions("Manual Verification - Tests Displayed", message.toString());
    }

    @When("user selects a test")
    public void user_selects_a_test(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap();

        StringBuilder message = new StringBuilder();
        message.append("Please select the test with:\n\n");
        message.append("Issue ID: ").append(data.get("Issue ID")).append("\n");
        message.append("SubIssue ID: ").append(data.get("SubIssueID")).append("\n\n");
        message.append("Click on the test row in the table to select it.\n");
        message.append("Click 'OK - Done' when selected.");

        showModelessInstructions("Manual Test - Select Test", message.toString());
    }

    @When("the user enters the test run")
    public void the_user_enters_the_test_run(DataTable dataTable) throws Exception {
        Map<String, String> data = dataTable.asMap();

        String result = data.get("Result");
        String comments = data.get("Comments");

        StringBuilder message = new StringBuilder();
        message.append("Please enter the test run data:\n\n");
        message.append("Result: ").append(result).append("\n");
        message.append("Comments: ").append(comments).append("\n\n");
        message.append("1. In the Run Test dialog, select '").append(result).append("'\n");
        message.append("2. Enter comments: ").append(comments).append("\n");
        message.append("3. Click 'Run' to save\n");
        message.append("4. Click 'OK - Done' when complete");

        showModelessInstructions("Manual Test - Enter Test Run", message.toString());
    }

    private JDialog currentInstructionDialog = null;

    private void showModelessInstructions(String title, String message) {
        // Close any existing instruction dialog
        if (currentInstructionDialog != null && currentInstructionDialog.isVisible()) {
            currentInstructionDialog.dispose();
        }

        // Create modeless dialog
        currentInstructionDialog = new JDialog(testRecorderFrame, title, false);
        currentInstructionDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(panel.getBackground());
        textArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(400, 250));

        JButton okButton = new JButton("OK - Done");
        okButton.addActionListener(e -> currentInstructionDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(okButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        currentInstructionDialog.setContentPane(panel);
        currentInstructionDialog.pack();
        currentInstructionDialog.setLocationRelativeTo(testRecorderFrame);
        currentInstructionDialog.setVisible(true);
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
