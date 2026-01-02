package com.testrecorder.steps;

import com.testrecorder.domain.*;
import com.testrecorder.repository.DatabaseTestRepository;
import com.testrecorder.repository.InMemoryTestRepository;
import com.testrecorder.service.*;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import java.sql.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestRecorderStepDefinitions {

    public TestRecorderStepDefinitions() {
    }

    private void ensureRepository() throws SQLException {
        if (SharedState.repository instanceof InMemoryTestRepository) {
            SharedState.repository = new DatabaseTestRepository(SharedState.configuration);
            SharedState.initializeProviders();
        }
    }

    @Given("tests are empty")
    public void tests_are_empty(DataTable dataTable) throws SQLException {
        ensureRepository();
        SharedState.testService.deleteAllTests();
        SharedState.testService.deleteAllTestRuns();
    }

    @Given("^(?:test exists|tests are)$")
    public void test_exists(DataTable dataTable) throws SQLException {
        ensureRepository();
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            Test test = createTestFromRow(row);
            SharedState.repository.save(test);
        }
    }

    @When("adding a test")
    public void adding_a_test(DataTable dataTable) throws SQLException {
        ensureRepository();
        Map<String, String> data = dataTable.asMap();
        String issueId = data.get("Issue ID");
        String subIssueId = data.get("Sub Issue ID");
        String name = data.get("Name");
        String filePath = data.get("File Path");

        SharedState.testService.addTest(issueId, subIssueId, name, filePath);
    }

    @When("adding a test that already exists")
    public void adding_a_test_that_already_exists(DataTable dataTable) throws SQLException {
        adding_a_test(dataTable);
    }

    @When("test is run")
    public void test_is_run(DataTable dataTable) throws SQLException {
        ensureRepository();
        Map<String, String> data = dataTable.asMap();
        TestResult result = TestResult.fromString(data.get("Result"));
        String comments = data.getOrDefault("Comments", "");

        String[] selectedKey = getSelectedTest();
        SharedState.testService.runTest(selectedKey[0], selectedKey[1], result, comments);
    }

    @When("test is changed")
    public void test_is_changed(DataTable dataTable) throws SQLException {
        ensureRepository();
        Map<String, String> data = dataTable.asMap();
        String[] selectedKey = getSelectedTest();

        if (data.containsKey("Test Status")) {
            TestStatus newStatus = TestStatus.fromString(data.get("Test Status"));
            SharedState.testService.updateTestStatus(selectedKey[0], selectedKey[1], newStatus);
        }
    }

    @Then("^(?:tests now are|test is now)$")
    public void tests_now_are(DataTable dataTable) throws SQLException {
        ensureRepository();
        List<Map<String, String>> expectedRows = dataTable.asMaps();
        List<Test> actualTests = SharedState.testService.getAllTests();

        assertEquals(expectedRows.size(), actualTests.size(),
                "Expected " + expectedRows.size() + " tests but found " + actualTests.size());

        for (Map<String, String> expectedRow : expectedRows) {
            String issueId = expectedRow.get("Issue ID");
            String subIssueId = getSubIssueId(expectedRow);

            Optional<Test> testOpt = actualTests.stream()
                    .filter(t -> t.matchesKey(issueId, subIssueId))
                    .findFirst();

            assertTrue(testOpt.isPresent(), "Test not found: " + issueId + "/" + subIssueId);

            Test actualTest = testOpt.get();
            assertTestMatches(expectedRow, actualTest);
        }
    }

    @Then("test run display contains")
    public void test_run_display_contains(DataTable dataTable) throws SQLException {
        ensureRepository();
        Map<String, String> expected = dataTable.asMap();
        String expectedScript = expected.get("Test Script");

        String[] selectedKey = getSelectedTest();
        var testOpt = SharedState.testService.getTest(selectedKey[0], selectedKey[1]);
        assertTrue(testOpt.isPresent());
        Test selected = testOpt.get();

        String fullPath = SharedState.configuration.getRootFilePath() + selected.getFilePath();
        String actualScript = SharedState.testService.readTestScript(fullPath);

        String normalizedExpected = expectedScript.replace("\\n", "\n").trim();
        String normalizedActual = actualScript.trim();

        assertEquals(normalizedExpected, normalizedActual, "Test script doesn't match");
    }

    @Then("test run records exist")
    public void test_run_records_exist(DataTable dataTable) throws SQLException {
        ensureRepository();
        List<Map<String, String>> expectedRows = dataTable.asMaps();
        List<TestRun> actualTestRuns = SharedState.testService.getAllTestRuns();

        assertEquals(expectedRows.size(), actualTestRuns.size(),
                "Expected " + expectedRows.size() + " test runs but found " + actualTestRuns.size());

        for (Map<String, String> expectedRow : expectedRows) {
            String issueId = expectedRow.get("Issue ID");
            String subIssueId = getSubIssueId(expectedRow);
            TestDate dateTime = TestDate.parse(expectedRow.get("Date Time"));

            Optional<TestRun> testRunOpt = actualTestRuns.stream()
                    .filter(tr -> tr.getIssueId().equals(issueId) &&
                                  tr.getSubIssueId().equals(subIssueId) &&
                                  tr.getDateTime().equals(dateTime))
                    .findFirst();

            assertTrue(testRunOpt.isPresent(), "Test run not found");

            TestRun actualTestRun = testRunOpt.get();
            assertEquals(TestResult.fromString(expectedRow.get("Result")), actualTestRun.getResult());
            assertEquals(expectedRow.get("Comments"), actualTestRun.getComments());
            assertEquals(expectedRow.get("Runner"), actualTestRun.getRunner());
        }
    }

    private String[] getSelectedTest() throws SQLException {
        try (Connection conn = DriverManager.getConnection(
                SharedState.configuration.getDatabaseURL(),
                SharedState.configuration.getDatabaseUserID(),
                SharedState.configuration.getDatabasePassword());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT issue_id, sub_issue_id FROM selected_test")) {
            if (rs.next()) {
                return new String[]{rs.getString("issue_id"), rs.getString("sub_issue_id")};
            }
        }
        throw new RuntimeException("No test selected");
    }

    private Test createTestFromRow(Map<String, String> row) {
        String issueId = row.get("Issue ID");
        String subIssueId = getSubIssueId(row);
        String name = row.get("Name");
        String filePath = row.get("File Path");

        Test test = new Test(issueId, subIssueId, name, filePath);

        if (row.containsKey("Runner")) {
            test.setRunner(row.get("Runner"));
        }
        if (row.containsKey("Last Result")) {
            test.setLastResult(TestResult.fromString(row.get("Last Result")));
        }
        if (row.containsKey("Date Last Run")) {
            test.setDateLastRun(TestDate.parse(row.get("Date Last Run")));
        }
        if (row.containsKey("Date Previous Result")) {
            test.setDatePreviousResult(TestDate.parse(row.get("Date Previous Result")));
        }
        if (row.containsKey("Comments")) {
            test.setComments(row.get("Comments"));
        }
        if (row.containsKey("Test Status")) {
            test.setTestStatus(TestStatus.fromString(row.get("Test Status")));
        }

        return test;
    }

    private void assertTestMatches(Map<String, String> expected, Test actual) {
        assertEquals(expected.get("Issue ID"), actual.getIssueId());
        assertEquals(getSubIssueId(expected), actual.getSubIssueId());
        assertEquals(expected.get("Name"), actual.getName());

        if (expected.containsKey("Runner")) {
            assertEquals(normalizeEmpty(expected.get("Runner")), actual.getRunner());
        }
        if (expected.containsKey("Last Result")) {
            assertEquals(TestResult.fromString(expected.get("Last Result")), actual.getLastResult());
        }
        if (expected.containsKey("Date Last Run")) {
            assertEquals(TestDate.parse(expected.get("Date Last Run")), actual.getDateLastRun());
        }
        if (expected.containsKey("Date Previous Result")) {
            assertEquals(TestDate.parse(expected.get("Date Previous Result")), actual.getDatePreviousResult());
        }
        if (expected.containsKey("File Path")) {
            assertEquals(expected.get("File Path"), actual.getFilePath());
        }
        if (expected.containsKey("Comments")) {
            assertEquals(normalizeEmpty(expected.get("Comments")), actual.getComments());
        }
        if (expected.containsKey("Test Status")) {
            assertEquals(TestStatus.fromString(expected.get("Test Status")), actual.getTestStatus());
        }
    }

    private String normalizeEmpty(String value) {
        return (value == null || value.isEmpty()) ? "" : value;
    }

    private String getSubIssueId(Map<String, String> row) {
        if (row.containsKey("Sub Issue ID")) return row.get("Sub Issue ID");
        if (row.containsKey("SubIssueID")) return row.get("SubIssueID");
        if (row.containsKey("SubIssue ID")) return row.get("SubIssue ID");
        return null;
    }
}
