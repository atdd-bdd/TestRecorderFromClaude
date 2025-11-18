package com.testrecorder.steps;

import com.testrecorder.domain.*;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestRecorderStepDefinitions {
    private final TestContext context;

    public TestRecorderStepDefinitions(TestContext context) {
        this.context = context;
    }

    @Given("tests are empty")
    public void tests_are_empty(DataTable dataTable) {
        context.getTestService().deleteAllTests();
        context.getTestService().deleteAllTestRuns();
    }

    @Given("test exists")
    @Given("tests are")
    public void test_exists(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            Test test = createTestFromRow(row);
            context.getRepository().save(test);
        }
    }

    @When("adding a test")
    public void adding_a_test(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap();
        String issueId = data.get("Issue ID");
        String subIssueId = data.get("Sub Issue ID");
        String name = data.get("Name");
        String filePath = data.get("File Path");

        context.getTestService().addTest(issueId, subIssueId, name, filePath);
    }

    @When("adding a test that already exists")
    public void adding_a_test_that_already_exists(DataTable dataTable) {
        adding_a_test(dataTable);
    }

    @When("test is run")
    @And("test is run")
    public void test_is_run(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap();
        TestResult result = TestResult.fromString(data.get("Result"));
        String comments = data.getOrDefault("Comments", "");

        Test selected = context.getSelectedTest();
        context.getTestService().runTest(selected.getIssueId(), selected.getSubIssueId(), result, comments);
        
        // Reload the selected test to get updated values
        var updatedTest = context.getTestService().getTest(selected.getIssueId(), selected.getSubIssueId());
        context.setSelectedTest(updatedTest.get());
    }

    @When("test is changed")
    public void test_is_changed(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap();
        Test selected = context.getSelectedTest();
        
        if (data.containsKey("Test Status")) {
            TestStatus newStatus = TestStatus.fromString(data.get("Test Status"));
            context.getTestService().updateTestStatus(selected.getIssueId(), selected.getSubIssueId(), newStatus);
        }
    }

    @Then("tests now are")
    @And("test is now")
    public void tests_now_are(DataTable dataTable) {
        List<Map<String, String>> expectedRows = dataTable.asMaps();
        List<Test> actualTests = context.getTestService().getAllTests();

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
    public void test_run_display_contains(DataTable dataTable) {
        Map<String, String> expected = dataTable.asMap();
        String expectedScript = expected.get("Test Script");
        
        Test selected = context.getSelectedTest();
        String fullPath = context.getConfiguration().getRootFilePath() + selected.getFilePath();
        String actualScript = context.getTestService().readTestScript(fullPath);
        
        // Compare with \n converted from literal
        String normalizedExpected = expectedScript.replace("\\n", "\n").trim();
        String normalizedActual = actualScript.trim();
        
        assertEquals(normalizedExpected, normalizedActual, "Test script doesn't match");
        context.setTestScriptDisplay(actualScript);
    }

    @Then("test run records exist")
    public void test_run_records_exist(DataTable dataTable) {
        List<Map<String, String>> expectedRows = dataTable.asMaps();
        List<TestRun> actualTestRuns = context.getTestService().getAllTestRuns();

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
