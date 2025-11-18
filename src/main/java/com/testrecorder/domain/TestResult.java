package com.testrecorder.domain;

public enum TestResult {
    SUCCESS("Success"),
    FAILURE("Failure");

    private final String displayName;

    TestResult(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TestResult fromString(String value) {
        for (TestResult result : TestResult.values()) {
            if (result.displayName.equalsIgnoreCase(value) || result.name().equalsIgnoreCase(value)) {
                return result;
            }
        }
        throw new IllegalArgumentException("Invalid TestResult: " + value);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
