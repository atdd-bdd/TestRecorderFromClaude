package com.testrecorder.steps;

import com.testrecorder.domain.*;
import com.testrecorder.repository.DatabaseTestRepository;
import com.testrecorder.repository.InMemoryTestRepository;
import com.testrecorder.service.*;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import java.sql.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CommonStepDefinitions {

    public CommonStepDefinitions() {
    }

    private void ensureRepository() throws SQLException {
        if (SharedState.repository instanceof InMemoryTestRepository) {
            SharedState.repository = new DatabaseTestRepository(SharedState.configuration);
            SharedState.initializeProviders();
        }
    }

    @Given("configuration values are:")
    public void configuration_values_are(DataTable dataTable) throws SQLException {
        List<Map<String, String>> rows = dataTable.asMaps();

        for (Map<String, String> row : rows) {
            String variable = row.get("Variable");
            String value = row.get("Value");

            switch (variable) {
                case "rootFilePath":
                    SharedState.configuration.setRootFilePath(value);
                    break;
                case "useTestDoubleForDateTime":
                    SharedState.configuration.setUseTestDoubleForDateTime(Boolean.parseBoolean(value));
                    break;
                case "useTestDoubleForRunner":
                    SharedState.configuration.setUseTestDoubleForRunner(Boolean.parseBoolean(value));
                    break;
                case "valueTestDoubleForDateTime":
                    SharedState.configuration.setValueTestDoubleForDateTime(value);
                    break;
                case "valueTestDoubleForRunner":
                    SharedState.configuration.setValueTestDoubleForRunner(value);
                    break;
                case "formNotCloseOnExit":
                    SharedState.configuration.setFormNotCloseOnExit(Boolean.parseBoolean(value));
                    break;
                case "databaseURL":
                    SharedState.configuration.setDatabaseURL(value);
                    break;
                case "databaseJDBCDriver":
                    SharedState.configuration.setDatabaseJDBCDriver(value);
                    break;
                case "databasePassword":
                    SharedState.configuration.setDatabasePassword(value);
                    break;
                case "databaseUserID":
                    SharedState.configuration.setDatabaseUserID(value);
                    break;
            }
        }

        // Re-initialize with new configuration
        SharedState.repository = new DatabaseTestRepository(SharedState.configuration);
        SharedState.initializeProviders();
    }

    @Given("file exists")
    public void file_exists(DataTable dataTable) throws SQLException {
        ensureRepository();
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            String filePath = row.get("File Path");
            String contents = row.get("Contents");

            String fullPath = SharedState.configuration.getRootFilePath() + filePath;
            SharedState.testService.createTestFile(fullPath, contents);
        }
    }

    @Given("value for runner is")
    public void value_for_runner_is(DataTable dataTable) throws SQLException {
        ensureRepository();
        List<List<String>> rows = dataTable.asLists();
        String runner = rows.get(0).get(0);

        if (SharedState.runnerProvider instanceof TestDoubleRunnerProvider) {
            ((TestDoubleRunnerProvider) SharedState.runnerProvider).setCurrentRunner(runner);
        }
    }

    @Given("value for current date is")
    public void value_for_current_date_is(DataTable dataTable) throws SQLException {
        ensureRepository();
        List<List<String>> rows = dataTable.asLists();
        String dateStr = rows.get(0).get(0);
        TestDate testDate = TestDate.parse(dateStr);

        if (SharedState.dateTimeProvider instanceof TestDoubleDateTimeProvider) {
            ((TestDoubleDateTimeProvider) SharedState.dateTimeProvider).setCurrentDateTime(testDate);
        }
    }

    @When("test is selected")
    public void test_is_selected(DataTable dataTable) throws SQLException {
        ensureRepository();
        Map<String, String> data = dataTable.asMap();
        String issueId = data.get("Issue ID");
        if (issueId == null) {
            issueId = data.get("IssueID");
        }
        if (issueId == null) {
            issueId = data.get("Issue ID   ");
        }

        String subIssueId = data.get("Sub Issue ID");
        if (subIssueId == null) {
            subIssueId = data.get("SubIssue ID");
        }
        if (subIssueId == null) {
            subIssueId = data.get("SubIssueID");
        }

        var test = SharedState.testService.getTest(issueId, subIssueId);
        assertTrue(test.isPresent(), "Test not found: " + issueId + "/" + subIssueId);
        // Store selected test in database as a special record or use a simple approach
        // For now we'll store it in a dedicated table
        storeSelectedTest(issueId, subIssueId);
    }

    private void storeSelectedTest(String issueId, String subIssueId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(
                SharedState.configuration.getDatabaseURL(),
                SharedState.configuration.getDatabaseUserID(),
                SharedState.configuration.getDatabasePassword())) {

            // Create table if not exists
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS selected_test (issue_id VARCHAR(5), sub_issue_id VARCHAR(3))");
                stmt.execute("DELETE FROM selected_test");
            }

            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO selected_test VALUES (?, ?)")) {
                pstmt.setString(1, issueId);
                pstmt.setString(2, subIssueId);
                pstmt.executeUpdate();
            }
        }
    }
}
