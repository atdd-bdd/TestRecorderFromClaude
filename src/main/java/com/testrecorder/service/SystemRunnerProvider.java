package com.testrecorder.service;

public class SystemRunnerProvider implements RunnerProvider {
    @Override
    public String getCurrentRunner() {
        return System.getProperty("user.name", "Unknown");
    }
}
