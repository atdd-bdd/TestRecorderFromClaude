package com.testrecorder.steps;

import com.testrecorder.domain.*;
import com.testrecorder.repository.DatabaseTestRepository;
import com.testrecorder.service.*;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import java.sql.*;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class BusinessRulesStepDefinitions {
    private Configuration configuration;
    private DatabaseTestRepository repository;
    private TestService testService;
    private DateTimeProvider dateTimeProvider;
    private RunnerProvider runnerProvider;
    private Test workingTest;

    public BusinessRulesStepDefinitions() {
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

    @Given("test exists with")
    public void test_exists_with(DataTable dataTable) throws SQLException {
        ensureRepository();
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, String> row = rows.get(0);

        workingTest = new Test("12345", "678", "Test", "test.feature");
        workingTest.setLastResult(TestResult.fromString(row.get("Last Result")));
        workingTest.setDateLastRun(TestDate.parse(row.get("Date Last Run")));
        workingTest.setDatePreviousResult(TestDate.parse(row.get("Date Previous Result")));
    }

    @When("Update Test from Test Run")
    public void update_test_from_test_run(DataTable dataTable) throws SQLException {
        ensureRepository();
        List<Map<String, String>> rows = dataTable.asMaps();

        for (Map<String, String> row : rows) {
            Test test = new Test("12345", "678", "Test", "test.feature");

            TestResult oldLastResult = TestResult.fromString(row.get("Old Last Result"));
            TestDate oldDateLastRun = TestDate.parse(row.get("Old Date Last Run"));
            TestDate oldDatePreviousResult = TestDate.parse(row.get("Old Date Previous Result"));
            TestResult result = TestResult.fromString(row.get("Result"));
            TestDate dateTime = TestDate.parse(row.get("Date Time"));

            testService.updateTestFromTestRun(test, oldLastResult, oldDateLastRun,
                                              oldDatePreviousResult, result, dateTime);

            TestResult newLastResult = TestResult.fromString(row.get("New Last Result"));
            TestDate newDateLastRun = TestDate.parse(row.get("New Date Last Run"));
            TestDate newDatePreviousResult = TestDate.parse(row.get("New Date Previous Result"));

            assertEquals(newLastResult, test.getLastResult(),
                        "Last result mismatch for row: " + row);
            assertEquals(newDateLastRun, test.getDateLastRun(),
                        "Date last run mismatch for row: " + row);
            assertEquals(newDatePreviousResult, test.getDatePreviousResult(),
                        "Date previous result mismatch for row: " + row);
        }
    }

    @When("Update Test from Test Run Sequence")
    public void update_test_from_test_run_sequence(DataTable dataTable) throws SQLException {
        ensureRepository();
        List<Map<String, String>> rows = dataTable.asMaps();

        for (Map<String, String> row : rows) {
            TestResult result = TestResult.fromString(row.get("Result"));
            TestDate dateTime = TestDate.parse(row.get("Date Time"));

            TestRun testRun = new TestRun(workingTest.getIssueId(), workingTest.getSubIssueId(),
                                         dateTime, result, "", "");
            workingTest.updateFromTestRun(testRun);

            TestResult newLastResult = TestResult.fromString(row.get("New Last Result"));
            TestDate newDateLastRun = TestDate.parse(row.get("New Date Last Run"));
            TestDate newDatePreviousResult = TestDate.parse(row.get("New Date Previous Result"));

            assertEquals(newLastResult, workingTest.getLastResult(),
                        "Last result mismatch for sequence row: " + row);
            assertEquals(newDateLastRun, workingTest.getDateLastRun(),
                        "Date last run mismatch for sequence row: " + row);
            assertEquals(newDatePreviousResult, workingTest.getDatePreviousResult(),
                        "Date previous result mismatch for sequence row: " + row);
        }
    }
}
