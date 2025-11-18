package com.testrecorder.steps;

import com.testrecorder.domain.*;
import com.testrecorder.repository.DatabaseTestRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

public class EntitiesStepDefinitions {
    private final TestContext context;
    private static final String CONFIG_FILENAME = "target/test-config.properties";

    public EntitiesStepDefinitions(TestContext context) {
        this.context = context;
    }

    @When("configuration is saved")
    public void configuration_is_saved() throws IOException {
        File file = new File(CONFIG_FILENAME);
        file.getParentFile().mkdirs();
        context.getConfiguration().save(CONFIG_FILENAME);
    }

    @When("configuration is loaded")
    public void configuration_is_loaded() throws IOException {
        context.getConfiguration().load(CONFIG_FILENAME);
    }

    @Then("configuration values now are:")
    public void configuration_values_now_are(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        Configuration config = context.getConfiguration();

        for (Map<String, String> row : rows) {
            String variable = row.get("Variable");
            String expectedValue = row.get("Value");

            String actualValue = switch (variable) {
                case "rootFilePath" -> config.getRootFilePath();
                case "useTestDoubleForDateTime" -> String.valueOf(config.isUseTestDoubleForDateTime());
                case "useTestDoubleForRunner" -> String.valueOf(config.isUseTestDoubleForRunner());
                case "valueTestDoubleForDateTime" -> config.getValueTestDoubleForDateTime();
                case "valueTestDoubleForRunner" -> config.getValueTestDoubleForRunner();
                case "formNotCloseOnExit" -> String.valueOf(config.isFormNotCloseOnExit());
                case "databaseURL" -> config.getDatabaseURL();
                case "databaseJDBCDriver" -> config.getDatabaseJDBCDriver();
                case "databasePassword" -> config.getDatabasePassword();
                case "databaseUserID" -> config.getDatabaseUserID();
                default -> throw new IllegalArgumentException("Unknown variable: " + variable);
            };

            assertEquals(expectedValue, actualValue, "Configuration mismatch for: " + variable);
        }
    }

    @Given("database is setup")
    public void database_is_setup() throws Exception {
        // Switch to database repository
        DatabaseTestRepository dbRepo = new DatabaseTestRepository(context.getConfiguration());
        context.setRepository(dbRepo);
    }

    @When("test is stored")
    public void test_is_stored(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, String> row = rows.get(0);
        
        Test test = new Test(
            row.get("Issue ID"),
            "678", // Default sub issue ID
            row.get("Name"),
            row.get("File Path")
        );
        test.setRunner(row.get("Runner"));
        test.setLastResult(TestResult.fromString(row.get("Last Result")));
        test.setDateLastRun(TestDate.parse(row.get("Date Last Run")));
        test.setDatePreviousResult(TestDate.parse(row.get("Date Previous Result")));
        test.setComments(row.get("Comments"));
        
        context.getRepository().save(test);
    }

    @Then("test can be loaded")
    public void test_can_be_loaded(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, String> expected = rows.get(0);
        
        Optional<Test> testOpt = context.getRepository().findByKey(expected.get("Issue ID"), "678");
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
    public void test_is_equal_when_selectively_compared_to(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, String> expectedData = rows.get(0);
        
        List<Test> tests = context.getTestService().getAllTests();
        assertFalse(tests.isEmpty(), "No tests found");
        
        Test actual = tests.get(0);
        Test expected = new Test();
        expected.setName(expectedData.get("Name"));
        
        String[] fields = expectedData.keySet().toArray(new String[0]);
        assertTrue(actual.selectiveEquals(expected, fields), 
                  "Selective comparison failed");
    }
}
