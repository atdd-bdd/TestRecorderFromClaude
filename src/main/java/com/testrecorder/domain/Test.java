package com.testrecorder.domain;

import java.util.Date;
import java.util.Objects;

public class Test {
    private String issueId;
    private String subIssueId;
    private String name;
    private String runner;
    private TestResult lastResult;
    private TestDate dateLastRun;
    private TestDate datePreviousResult;
    private String filePath;
    private String comments;
    private TestStatus testStatus;

    public Test() {
        this.lastResult = TestResult.FAILURE;
        this.dateLastRun = TestDate.never();
        this.datePreviousResult = TestDate.never();
        this.runner = "";
        this.comments = "";
        this.testStatus = TestStatus.ACTIVE;
    }

    public Test(String issueId, String subIssueId, String name, String filePath) {
        this();
        this.issueId = IssueId.validate(issueId);
        this.subIssueId = SubIssueId.validate(subIssueId);
        this.name = Name.validate(name);
        this.filePath = filePath;
    }

    // Getters and Setters
    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = IssueId.validate(issueId);
    }

    public String getSubIssueId() {
        return subIssueId;
    }

    public void setSubIssueId(String subIssueId) {
        this.subIssueId = SubIssueId.validate(subIssueId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Name.validate(name);
    }

    public String getRunner() {
        return runner;
    }

    public void setRunner(String runner) {
        this.runner = runner != null ? runner : "";
    }

    public TestResult getLastResult() {
        return lastResult;
    }

    public void setLastResult(TestResult lastResult) {
        this.lastResult = lastResult;
    }

    public TestDate getDateLastRun() {
        return dateLastRun;
    }

    public void setDateLastRun(TestDate dateLastRun) {
        this.dateLastRun = dateLastRun;
    }

    public TestDate getDatePreviousResult() {
        return datePreviousResult;
    }

    public void setDatePreviousResult(TestDate datePreviousResult) {
        this.datePreviousResult = datePreviousResult;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments != null ? comments : "";
    }

    public TestStatus getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(TestStatus testStatus) {
        this.testStatus = testStatus != null ? testStatus : TestStatus.ACTIVE;
    }

    public void updateFromTestRun(TestRun testRun) {
        if (testRun.getDateTime().isAfter(this.dateLastRun)) {
            // Only update datePreviousResult when the result changes
            if (this.lastResult != testRun.getResult()) {
                this.datePreviousResult = this.dateLastRun;
            }
            this.dateLastRun = testRun.getDateTime();
            this.lastResult = testRun.getResult();
            this.runner = testRun.getRunner();
            this.comments = testRun.getComments();
        }
    }

    public boolean matchesKey(String issueId, String subIssueId) {
        return this.issueId.equals(issueId) && this.subIssueId.equals(subIssueId);
    }

    public boolean selectiveEquals(Test other, String... fields) {
        if (other == null) return false;
        
        for (String field : fields) {
            switch (field.toLowerCase()) {
                case "name":
                    if (!Objects.equals(this.name, other.name)) return false;
                    break;
                case "issueid":
                case "issue id":
                    if (!Objects.equals(this.issueId, other.issueId)) return false;
                    break;
                case "subissueid":
                case "sub issue id":
                    if (!Objects.equals(this.subIssueId, other.subIssueId)) return false;
                    break;
                case "teststatus":
                case "test status":
                    if (!Objects.equals(this.testStatus, other.testStatus)) return false;
                    break;
                // Add more fields as needed
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Test test = (Test) o;
        return Objects.equals(issueId, test.issueId) &&
                Objects.equals(subIssueId, test.subIssueId) &&
                Objects.equals(name, test.name) &&
                Objects.equals(runner, test.runner) &&
                lastResult == test.lastResult &&
                Objects.equals(dateLastRun, test.dateLastRun) &&
                Objects.equals(datePreviousResult, test.datePreviousResult) &&
                Objects.equals(filePath, test.filePath) &&
                Objects.equals(comments, test.comments) &&
                testStatus == test.testStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(issueId, subIssueId);
    }

    @Override
    public String toString() {
        return "Test{" +
                "issueId='" + issueId + '\'' +
                ", subIssueId='" + subIssueId + '\'' +
                ", name='" + name + '\'' +
                ", lastResult=" + lastResult +
                ", testStatus=" + testStatus +
                '}';
    }
}
