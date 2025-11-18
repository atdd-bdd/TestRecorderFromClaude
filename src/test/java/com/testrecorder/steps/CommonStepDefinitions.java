package com.testrecorder.steps;

import com.testrecorder.domain.*;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CommonStepDefinitions {
    private final TestContext context;

    public CommonStepDefinitions(TestContext context) {
        this.context = context;
    }

    @Given("configuration values are:")
    public void configuration_values_are(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        Configuration config = context.getConfiguration();

        for (Map<String, String> row : rows) {
            String variable = row.get("Variable");
            String value = row.get("Value");

            switch (variable) {
                case "rootFilePath":
                    config.setRootFilePath(value);
                    break;
                case "useTestDoubleForDateTime":
                    config.setUseTestDoubleForDateTime(Boolean.parseBoolean(value));
                    break;
                case "useTestDoubleForRunner":
                    config.setUseTestDoubleForRunner(Boolean.parseBoolean(value));
                    break;
                case "valueTestDoubleForDateTime":
                    config.setValueTestDoubleForDateTime(value);
                    break;
                case "valueTestDoubleForRunner":
                    config.setValueTestDoubleForRunner(value);
                    break;
                case "formNotCloseOnExit":
                    config.setFormNotCloseOnExit(Boolean.parseBoolean(value));
                    break;
                case "databaseURL":
                    config.setDatabaseURL(value);
                    break;
                case "databaseJDBCDriver":
                    config.setDatabaseJDBCDriver(value);
                    break;
                case "databasePassword":
                    config.setDatabasePassword(value);
                    break;
                case "databaseUserID":
                    config.setDatabaseUserID(value);
                    break;
            }
        }

        context.reinitializeProviders();
    }

    @Given("file exists")
    public void file_exists(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            String filePath = row.get("File Path");
            String contents = row.get("Contents");
            context.addTestFileContent(filePath, contents);
            
            // Create actual file in the filesystem
            String fullPath = context.getConfiguration().getRootFilePath() + filePath;
            context.getTestService().createTestFile(fullPath, contents);
        }
    }

    @Given("value for runner is")
    public void value_for_runner_is(DataTable dataTable) {
        List<List<String>> rows = dataTable.asLists();
        String runner = rows.get(0).get(0);
        
        if (context.getRunnerProvider() instanceof com.testrecorder.service.TestDoubleRunnerProvider) {
            ((com.testrecorder.service.TestDoubleRunnerProvider) context.getRunnerProvider()).setCurrentRunner(runner);
        }
    }

    @Given("value for current date is")
    @When("value for current date is")
    public void value_for_current_date_is(DataTable dataTable) {
        List<List<String>> rows = dataTable.asLists();
        String dateStr = rows.get(0).get(0);
        TestDate testDate = TestDate.parse(dateStr);
        
        if (context.getDateTimeProvider() instanceof com.testrecorder.service.TestDoubleDateTimeProvider) {
            ((com.testrecorder.service.TestDoubleDateTimeProvider) context.getDateTimeProvider()).setCurrentDateTime(testDate);
        }
    }

    @When("test is selected")
    public void test_is_selected(DataTable dataTable) {
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

        var test = context.getTestService().getTest(issueId, subIssueId);
        assertTrue(test.isPresent(), "Test not found: " + issueId + "/" + subIssueId);
        context.setSelectedTest(test.get());
    }
}
