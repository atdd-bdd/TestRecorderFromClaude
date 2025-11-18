package com.testrecorder.domain;

import java.util.Objects;

public class TestRun {
    private String issueId;
    private String subIssueId;
    private TestDate dateTime;
    private TestResult result;
    private String comments;
    private String runner;

    public TestRun() {
        this.comments = "";
        this.runner = "";
    }

    public TestRun(String issueId, String subIssueId, TestDate dateTime, TestResult result, String comments, String runner) {
        this.issueId = issueId;
        this.subIssueId = subIssueId;
        this.dateTime = dateTime;
        this.result = result;
        this.comments = comments != null ? comments : "";
        this.runner = runner != null ? runner : "";
    }

    // Getters and Setters
    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getSubIssueId() {
        return subIssueId;
    }

    public void setSubIssueId(String subIssueId) {
        this.subIssueId = subIssueId;
    }

    public TestDate getDateTime() {
        return dateTime;
    }

    public void setDateTime(TestDate dateTime) {
        this.dateTime = dateTime;
    }

    public TestResult getResult() {
        return result;
    }

    public void setResult(TestResult result) {
        this.result = result;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments != null ? comments : "";
    }

    public String getRunner() {
        return runner;
    }

    public void setRunner(String runner) {
        this.runner = runner != null ? runner : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestRun testRun = (TestRun) o;
        return Objects.equals(issueId, testRun.issueId) &&
                Objects.equals(subIssueId, testRun.subIssueId) &&
                Objects.equals(dateTime, testRun.dateTime) &&
                result == testRun.result &&
                Objects.equals(comments, testRun.comments) &&
                Objects.equals(runner, testRun.runner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(issueId, subIssueId, dateTime);
    }

    @Override
    public String toString() {
        return "TestRun{" +
                "issueId='" + issueId + '\'' +
                ", subIssueId='" + subIssueId + '\'' +
                ", dateTime=" + dateTime +
                ", result=" + result +
                ", runner='" + runner + '\'' +
                '}';
    }
}
