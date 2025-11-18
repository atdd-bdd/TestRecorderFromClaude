package com.testrecorder.service;

public class TestDoubleRunnerProvider implements RunnerProvider {
    private String fixedRunner;

    public TestDoubleRunnerProvider(String fixedRunner) {
        this.fixedRunner = fixedRunner;
    }

    public void setCurrentRunner(String runner) {
        this.fixedRunner = runner;
    }

    @Override
    public String getCurrentRunner() {
        return fixedRunner;
    }
}
