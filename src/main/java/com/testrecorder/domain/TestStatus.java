package com.testrecorder.domain;

public enum TestStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    RETIRED("Retired");

    private final String displayName;

    TestStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TestStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return ACTIVE; // Default
        }
        for (TestStatus status : TestStatus.values()) {
            if (status.displayName.equalsIgnoreCase(value) || status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid TestStatus: " + value);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
