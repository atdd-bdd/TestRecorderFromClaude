package com.testrecorder.service;

import com.testrecorder.domain.*;
import com.testrecorder.repository.TestRepository;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TestService {
    private final TestRepository repository;
    private final DateTimeProvider dateTimeProvider;
    private final RunnerProvider runnerProvider;

    public TestService(TestRepository repository, DateTimeProvider dateTimeProvider, RunnerProvider runnerProvider) {
        this.repository = repository;
        this.dateTimeProvider = dateTimeProvider;
        this.runnerProvider = runnerProvider;
    }

    public void addTest(String issueId, String subIssueId, String name, String filePath) {
        // Check if test already exists - if it does, don't add it
        Optional<Test> existing = repository.findByKey(issueId, subIssueId);
        if (existing.isPresent()) {
            return; // Test already exists, don't modify
        }

        Test test = new Test(issueId, subIssueId, name, filePath);
        repository.save(test);
    }

    public Optional<Test> getTest(String issueId, String subIssueId) {
        return repository.findByKey(issueId, subIssueId);
    }

    public List<Test> getAllTests() {
        return repository.findAll();
    }

    public void deleteAllTests() {
        repository.deleteAll();
    }

    public void runTest(String issueId, String subIssueId, TestResult result, String comments) {
        Optional<Test> testOpt = repository.findByKey(issueId, subIssueId);
        if (!testOpt.isPresent()) {
            throw new IllegalArgumentException("Test not found: " + issueId + "/" + subIssueId);
        }

        Test test = testOpt.get();
        TestDate currentDateTime = dateTimeProvider.getCurrentDateTime();
        String runner = runnerProvider.getCurrentRunner();

        TestRun testRun = new TestRun(issueId, subIssueId, currentDateTime, result, comments, runner);
        
        // Update test with test run information
        test.updateFromTestRun(testRun);
        
        // Save both
        repository.save(test);
        repository.saveTestRun(testRun);
    }

    public void updateTestStatus(String issueId, String subIssueId, TestStatus newStatus) {
        Optional<Test> testOpt = repository.findByKey(issueId, subIssueId);
        if (!testOpt.isPresent()) {
            throw new IllegalArgumentException("Test not found: " + issueId + "/" + subIssueId);
        }

        Test test = testOpt.get();
        test.setTestStatus(newStatus);
        repository.save(test);
    }

    public List<TestRun> getTestRuns(String issueId, String subIssueId) {
        return repository.findTestRunsByKey(issueId, subIssueId);
    }

    public List<TestRun> getAllTestRuns() {
        return repository.findAllTestRuns();
    }

    public void deleteAllTestRuns() {
        repository.deleteAllTestRuns();
    }

    public String readTestScript(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return "";
            }
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read test script: " + filePath, e);
        }
    }

    public void createTestFile(String filePath, String contents) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(contents);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create test file: " + filePath, e);
        }
    }

    public boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    public List<Test> filterTests(boolean includeActive, boolean includeInactive, boolean includeRetired) {
        return repository.findAll().stream()
                .filter(test -> {
                    TestStatus status = test.getTestStatus();
                    if (status == TestStatus.ACTIVE && includeActive) return true;
                    if (status == TestStatus.INACTIVE && includeInactive) return true;
                    if (status == TestStatus.RETIRED && includeRetired) return true;
                    return false;
                })
                .collect(Collectors.toList());
    }

    // Business rule: Update test from test run
    public void updateTestFromTestRun(Test test, TestResult oldLastResult, TestDate oldDateLastRun, 
                                      TestDate oldDatePreviousResult, TestResult result, TestDate dateTime) {
        // Set the old values
        test.setLastResult(oldLastResult);
        test.setDateLastRun(oldDateLastRun);
        test.setDatePreviousResult(oldDatePreviousResult);

        // Create a test run and update
        TestRun testRun = new TestRun(
                test.getIssueId(),
                test.getSubIssueId(),
                dateTime,
                result,
                "",
                ""
        );

        test.updateFromTestRun(testRun);
    }
}
