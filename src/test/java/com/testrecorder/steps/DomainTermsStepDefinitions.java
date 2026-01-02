package com.testrecorder.steps;

import com.testrecorder.domain.*;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class DomainTermsStepDefinitions {

    public DomainTermsStepDefinitions() {
    }

    @When("Test Results are")
    public void test_results_are(DataTable dataTable) {
        List<List<String>> rows = dataTable.asLists();
        for (List<String> row : rows) {
            String value = row.get(0);
            TestResult result = TestResult.fromString(value);
            assertNotNull(result, "Invalid test result: " + value);
        }
    }

    @When("IssueID must be five characters and digits without spaces")
    public void issue_id_validation(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            String value = row.get("Value");
            boolean expectedValid = row.get("Valid").equalsIgnoreCase("Yes");
            boolean actualValid = IssueId.isValid(value);

            assertEquals(expectedValid, actualValid,
                        "IssueID validation failed for: " + value + " (" + row.get("Notes") + ")");
        }
    }

    @When("IssueID must be three characters and digits without spaces")
    public void sub_issue_id_validation(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            String value = row.get("Value");
            boolean expectedValid = row.get("Valid").equalsIgnoreCase("Yes");
            boolean actualValid = SubIssueId.isValid(value);

            assertEquals(expectedValid, actualValid,
                        "SubIssueID validation failed for: " + value + " (" + row.get("Notes") + ")");
        }
    }

    @When("TestDate must have valid format")
    public void test_date_validation(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            String value = row.get("Value");
            boolean expectedValid = row.get("Valid").equalsIgnoreCase("Yes");
            boolean actualValid = TestDate.isValid(value);

            assertEquals(expectedValid, actualValid,
                        "TestDate validation failed for: " + value + " (" + row.get("Notes") + ")");
        }
    }

    @When("Test Statuses are")
    public void test_statuses_are(DataTable dataTable) {
        List<List<String>> rows = dataTable.asLists();
        for (List<String> row : rows) {
            String value = row.get(0);
            TestStatus status = TestStatus.fromString(value);
            assertNotNull(status, "Invalid test status: " + value);
        }
    }

    @When("Name changes are")
    public void name_changes_are(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            String value = row.get("Value");
            String expectedNewValue = row.get("New Value");
            String actualNewValue = Name.validate(value);

            assertEquals(expectedNewValue, actualNewValue,
                        "Name validation failed for: " + value + " (" + row.get("Notes") + ")");
        }
    }

    @When("MyString changes are")
    public void my_string_changes_are(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            String value = row.get("Value");
            String expectedNewValue = row.get("New Value");
            String actualNewValue = MyString.validate(value);

            assertEquals(expectedNewValue, actualNewValue,
                        "MyString validation failed for: " + value + " (" + row.get("Notes") + ")");
        }
    }
}
