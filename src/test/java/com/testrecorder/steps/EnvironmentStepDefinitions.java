package com.testrecorder.steps;

import com.testrecorder.util.EnvironmentUtil;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class EnvironmentStepDefinitions {
    private final TestContext context;

    public EnvironmentStepDefinitions(TestContext context) {
        this.context = context;
    }

    @When("environment variable is set")
    public void environment_variable_is_set(DataTable dataTable) {
        Map<String, String> variables = dataTable.asMap();
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            EnvironmentUtil.setEnvironmentVariable(entry.getKey(), entry.getValue());
        }
    }

    @Then("environment variable is now")
    public void environment_variable_is_now(DataTable dataTable) {
        Map<String, String> expected = dataTable.asMap();
        for (Map.Entry<String, String> entry : expected.entrySet()) {
            String actualValue = EnvironmentUtil.getEnvironmentVariable(entry.getKey());
            assertEquals(entry.getValue(), actualValue, 
                        "Environment variable mismatch for: " + entry.getKey());
        }
    }
}
