package com.testrecorder.repository;

import com.testrecorder.domain.Test;
import com.testrecorder.domain.TestRun;
import java.util.List;
import java.util.Optional;

public interface TestRepository {
    void save(Test test);
    Optional<Test> findByKey(String issueId, String subIssueId);
    List<Test> findAll();
    void deleteAll();
    void saveTestRun(TestRun testRun);
    List<TestRun> findTestRunsByKey(String issueId, String subIssueId);
    List<TestRun> findAllTestRuns();
    void deleteAllTestRuns();
}
