package com.testrecorder.repository;

import com.testrecorder.domain.Test;
import com.testrecorder.domain.TestRun;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class InMemoryTestRepository implements TestRepository {
    private final List<Test> tests = new CopyOnWriteArrayList<>();
    private final List<TestRun> testRuns = new CopyOnWriteArrayList<>();

    @Override
    public void save(Test test) {
        Optional<Test> existing = findByKey(test.getIssueId(), test.getSubIssueId());
        if (existing.isPresent()) {
            tests.remove(existing.get());
        }
        tests.add(test);
    }

    @Override
    public Optional<Test> findByKey(String issueId, String subIssueId) {
        return tests.stream()
                .filter(t -> t.matchesKey(issueId, subIssueId))
                .findFirst();
    }

    @Override
    public List<Test> findAll() {
        return new ArrayList<>(tests);
    }

    @Override
    public void deleteAll() {
        tests.clear();
    }

    @Override
    public void saveTestRun(TestRun testRun) {
        testRuns.add(testRun);
    }

    @Override
    public List<TestRun> findTestRunsByKey(String issueId, String subIssueId) {
        return testRuns.stream()
                .filter(tr -> tr.getIssueId().equals(issueId) && tr.getSubIssueId().equals(subIssueId))
                .collect(Collectors.toList());
    }

    @Override
    public List<TestRun> findAllTestRuns() {
        return new ArrayList<>(testRuns);
    }

    @Override
    public void deleteAllTestRuns() {
        testRuns.clear();
    }
}
