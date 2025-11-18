package com.testrecorder.steps;

import com.testrecorder.domain.*;
import com.testrecorder.repository.*;
import com.testrecorder.service.*;
import com.testrecorder.ui.TestTablePanel;
import java.util.*;

public class TestContext {
    private Configuration configuration;
    private TestRepository repository;
    private TestService testService;
    private DateTimeProvider dateTimeProvider;
    private RunnerProvider runnerProvider;
    private Test selectedTest;
    private List<Test> unfilteredTests;
    private List<Test> filteredTests;
    private String testScriptDisplay;
    private TestTablePanel testTablePanel;
    private Map<String, String> testFileContents;

    public TestContext() {
        this.configuration = new Configuration();
        this.repository = new InMemoryTestRepository();
        this.unfilteredTests = new ArrayList<>();
        this.filteredTests = new ArrayList<>();
        this.testFileContents = new HashMap<>();
        initializeProviders();
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

        this.testService = new TestService(repository, dateTimeProvider, runnerProvider);
    }

    public void reinitializeProviders() {
        initializeProviders();
    }

    // Getters and Setters
    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public TestRepository getRepository() {
        return repository;
    }

    public void setRepository(TestRepository repository) {
        this.repository = repository;
        this.testService = new TestService(repository, dateTimeProvider, runnerProvider);
    }

    public TestService getTestService() {
        return testService;
    }

    public DateTimeProvider getDateTimeProvider() {
        return dateTimeProvider;
    }

    public void setDateTimeProvider(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
        this.testService = new TestService(repository, dateTimeProvider, runnerProvider);
    }

    public RunnerProvider getRunnerProvider() {
        return runnerProvider;
    }

    public void setRunnerProvider(RunnerProvider runnerProvider) {
        this.runnerProvider = runnerProvider;
        this.testService = new TestService(repository, dateTimeProvider, runnerProvider);
    }

    public Test getSelectedTest() {
        return selectedTest;
    }

    public void setSelectedTest(Test selectedTest) {
        this.selectedTest = selectedTest;
    }

    public List<Test> getUnfilteredTests() {
        return unfilteredTests;
    }

    public void setUnfilteredTests(List<Test> unfilteredTests) {
        this.unfilteredTests = unfilteredTests;
    }

    public List<Test> getFilteredTests() {
        return filteredTests;
    }

    public void setFilteredTests(List<Test> filteredTests) {
        this.filteredTests = filteredTests;
    }

    public String getTestScriptDisplay() {
        return testScriptDisplay;
    }

    public void setTestScriptDisplay(String testScriptDisplay) {
        this.testScriptDisplay = testScriptDisplay;
    }

    public TestTablePanel getTestTablePanel() {
        return testTablePanel;
    }

    public void setTestTablePanel(TestTablePanel testTablePanel) {
        this.testTablePanel = testTablePanel;
    }

    public Map<String, String> getTestFileContents() {
        return testFileContents;
    }

    public void addTestFileContent(String filePath, String contents) {
        this.testFileContents.put(filePath, contents);
    }

    public String getTestFileContent(String filePath) {
        return testFileContents.get(filePath);
    }
}
