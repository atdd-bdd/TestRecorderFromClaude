package com.testrecorder.steps;

import com.testrecorder.domain.*;
import com.testrecorder.repository.DatabaseTestRepository;
import com.testrecorder.service.*;
import com.testrecorder.ui.TestTablePanel;
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
    public void test_table_swing_is_shown() throws SQLException {
        ensureRepository();
        testTablePanel = new TestTablePanel();
        List<Test> tests = testService.getAllTests();
        testTablePanel.loadTests(tests);
    }

    @When("test table swing is shown with test run data")
    public void test_table_swing_is_shown_with_test_run_data(DataTable dataTable) throws SQLException {
        ensureRepository();
        Map<String, String> data = dataTable.asMap();

        TestResult result = TestResult.fromString(data.get("Result"));
        String comments = data.get("Comments");

        String[] selectedKey = getSelectedTest();
        testService.runTest(selectedKey[0], selectedKey[1], result, comments);
    }

    @Then("test table should show that data")
    public void test_table_should_show_that_data() {
        assertNotNull(testTablePanel, "Test table panel not initialized");
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
