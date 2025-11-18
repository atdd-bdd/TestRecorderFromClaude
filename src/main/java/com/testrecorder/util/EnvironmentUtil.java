package com.testrecorder.util;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentUtil {
    private static final Map<String, String> environmentVariables = new HashMap<>();

    public static void setEnvironmentVariable(String key, String value) {
        environmentVariables.put(key, value);
    }

    public static String getEnvironmentVariable(String key) {
        // First check our custom map, then fall back to system env
        if (environmentVariables.containsKey(key)) {
            return environmentVariables.get(key);
        }
        return System.getenv(key);
    }

    public static void clearEnvironmentVariables() {
        environmentVariables.clear();
    }
}
