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

public class FilterStepDefinitions {
    private List<Test> filteredTests;

    public FilterStepDefinitions() {
        this.filteredTests = new ArrayList<>();
    }

    private void ensureRepository() throws SQLException {
        if (SharedState.repository instanceof InMemoryTestRepository) {
            SharedState.repository = new DatabaseTestRepository(SharedState.configuration);
            SharedState.initializeProviders();
        }
    }

    @Given("unfiltered tests are")
    public void unfiltered_tests_are(DataTable dataTable) throws SQLException {
        ensureRepository();
        // Clear existing data first
        SharedState.testService.deleteAllTests();

        List<Map<String, String>> rows = dataTable.asMaps();

        for (Map<String, String> row : rows) {
            Test test = createTestFromRow(row);
            SharedState.repository.save(test);
        }
    }

    @When("filtered by")
    public void filtered_by(DataTable dataTable) throws SQLException {
        ensureRepository();
        Map<String, String> filters = dataTable.asMap();

        boolean includeActive = Boolean.parseBoolean(filters.getOrDefault("Active", "true"));
        boolean includeInactive = Boolean.parseBoolean(filters.getOrDefault("Inactive", "true"));
        boolean includeRetired = Boolean.parseBoolean(filters.getOrDefault("Retired", "true"));

        filteredTests = SharedState.testService.filterTests(includeActive, includeInactive, includeRetired);
    }

    @Then("filtered tests are")
    public void filtered_tests_are(DataTable dataTable) {
        List<Map<String, String>> expectedRows = dataTable.asMaps();

        assertEquals(expectedRows.size(), filteredTests.size(),
                "Expected " + expectedRows.size() + " tests but found " + filteredTests.size());

        boolean isSelectiveComparison = expectedRows.get(0).size() < 10;

        for (Map<String, String> expectedRow : expectedRows) {
            String issueId = expectedRow.get("Issue ID");
            String subIssueId = getSubIssueId(expectedRow);

            Optional<Test> testOpt = filteredTests.stream()
                    .filter(t -> t.matchesKey(issueId, subIssueId))
                    .findFirst();

            assertTrue(testOpt.isPresent(), "Test not found: " + issueId + "/" + subIssueId);

            Test actualTest = testOpt.get();

            if (isSelectiveComparison) {
                String[] fields = expectedRow.keySet().toArray(new String[0]);
                Test expectedTest = createTestFromRow(expectedRow);
                assertTrue(actualTest.selectiveEquals(expectedTest, fields),
                          "Selective comparison failed for test: " + issueId + "/" + subIssueId);
            } else {
                assertTestMatches(expectedRow, actualTest);
            }
        }
    }

    private Test createTestFromRow(Map<String, String> row) {
        String issueId = row.get("Issue ID");
        String subIssueId = getSubIssueId(row);
        String name = row.getOrDefault("Name", "");
        String filePath = row.getOrDefault("File Path", "");

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

        if (expected.containsKey("Name")) {
            assertEquals(expected.get("Name"), actual.getName());
        }
        if (expected.containsKey("Runner")) {
            assertEquals(expected.get("Runner"), actual.getRunner());
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
            assertEquals(expected.get("Comments"), actual.getComments());
        }
        if (expected.containsKey("Test Status")) {
            assertEquals(TestStatus.fromString(expected.get("Test Status")), actual.getTestStatus());
        }
    }

    private String getSubIssueId(Map<String, String> row) {
        if (row.containsKey("Sub Issue ID")) return row.get("Sub Issue ID");
        if (row.containsKey("SubIssueID")) return row.get("SubIssueID");
        if (row.containsKey("SubIssue ID")) return row.get("SubIssue ID");
        return null;
    }
}
