package com.testrecorder.steps;

import com.testrecorder.domain.*;
import com.testrecorder.repository.DatabaseTestRepository;
import com.testrecorder.repository.InMemoryTestRepository;
import com.testrecorder.service.*;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

public class EntitiesStepDefinitions {
    private static final String CONFIG_FILENAME = "target/test-config.properties";

    public EntitiesStepDefinitions() {
    }

    private void ensureRepository() throws SQLException {
        if (SharedState.repository instanceof InMemoryTestRepository) {
            SharedState.repository = new DatabaseTestRepository(SharedState.configuration);
            SharedState.initializeProviders();
        }
    }

    @When("configuration is saved")
    public void configuration_is_saved() throws IOException {
        File file = new File(CONFIG_FILENAME);
        file.getParentFile().mkdirs();
        SharedState.configuration.save(CONFIG_FILENAME);
    }

    @When("configuration is loaded")
    public void configuration_is_loaded() throws IOException {
        SharedState.configuration.load(CONFIG_FILENAME);
    }

    @Then("configuration values now are:")
    public void configuration_values_now_are(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();

        for (Map<String, String> row : rows) {
            String variable = row.get("Variable");
            String expectedValue = row.get("Value");

            String actualValue;
            switch (variable) {
                case "rootFilePath":
                    actualValue = SharedState.configuration.getRootFilePath();
                    break;
                case "useTestDoubleForDateTime":
                    actualValue = String.valueOf(SharedState.configuration.isUseTestDoubleForDateTime());
                    break;
                case "useTestDoubleForRunner":
                    actualValue = String.valueOf(SharedState.configuration.isUseTestDoubleForRunner());
                    break;
                case "valueTestDoubleForDateTime":
                    actualValue = SharedState.configuration.getValueTestDoubleForDateTime();
                    break;
                case "valueTestDoubleForRunner":
                    actualValue = SharedState.configuration.getValueTestDoubleForRunner();
                    break;
                case "formNotCloseOnExit":
                    actualValue = String.valueOf(SharedState.configuration.isFormNotCloseOnExit());
                    break;
                case "databaseURL":
                    actualValue = SharedState.configuration.getDatabaseURL();
                    break;
                case "databaseJDBCDriver":
                    actualValue = SharedState.configuration.getDatabaseJDBCDriver();
                    break;
                case "databasePassword":
                    actualValue = SharedState.configuration.getDatabasePassword();
                    break;
                case "databaseUserID":
                    actualValue = SharedState.configuration.getDatabaseUserID();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown variable: " + variable);
            }

            assertEquals(normalizeEmpty(expectedValue), normalizeEmpty(actualValue), "Configuration mismatch for: " + variable);
        }
    }

    private String normalizeEmpty(String value) {
        return (value == null || value.isEmpty()) ? "" : value;
    }

    @Given("database is setup")
    public void database_is_setup() throws SQLException {
        ensureRepository();
    }

    @When("test is stored")
    public void test_is_stored(DataTable dataTable) throws SQLException {
        ensureRepository();
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, String> row = rows.get(0);

        Test test = new Test(
            row.get("Issue ID"),
            "678",
            row.get("Name"),
            row.get("File Path")
        );
        test.setRunner(row.get("Runner"));
        test.setLastResult(TestResult.fromString(row.get("Last Result")));
        test.setDateLastRun(TestDate.parse(row.get("Date Last Run")));
        test.setDatePreviousResult(TestDate.parse(row.get("Date Previous Result")));
        test.setComments(row.get("Comments"));

        SharedState.repository.save(test);
    }

    @Then("test can be loaded")
    public void test_can_be_loaded(DataTable dataTable) throws SQLException {
        ensureRepository();
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, String> expected = rows.get(0);

        Optional<Test> testOpt = SharedState.repository.findByKey(expected.get("Issue ID"), "678");
        assertTrue(testOpt.isPresent(), "Test not found in database");

        Test actual = testOpt.get();
        assertEquals(expected.get("Name"), actual.getName());
        assertEquals(expected.get("Runner"), actual.getRunner());
        assertEquals(TestResult.fromString(expected.get("Last Result")), actual.getLastResult());
        assertEquals(TestDate.parse(expected.get("Date Last Run")), actual.getDateLastRun());
        assertEquals(TestDate.parse(expected.get("Date Previous Result")), actual.getDatePreviousResult());
        assertEquals(expected.get("File Path"), actual.getFilePath());
        assertEquals(expected.get("Comments"), actual.getComments());
    }

    @Then("test is equal when selectively compared to")
    public void test_is_equal_when_selectively_compared_to(DataTable dataTable) throws SQLException {
        ensureRepository();
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, String> expectedData = rows.get(0);

        List<Test> tests = SharedState.testService.getAllTests();
        assertFalse(tests.isEmpty(), "No tests found");

        Test actual = tests.get(0);
        Test expected = new Test();
        expected.setName(expectedData.get("Name"));

        String[] fields = expectedData.keySet().toArray(new String[0]);
        assertTrue(actual.selectiveEquals(expected, fields),
                  "Selective comparison failed");
    }
}
