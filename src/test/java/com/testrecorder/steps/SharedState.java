package com.testrecorder.steps;

import com.testrecorder.domain.*;
import com.testrecorder.repository.*;
import com.testrecorder.service.*;
import com.testrecorder.ui.TestTablePanel;
import java.util.*;

public class SharedState {
    public static Configuration configuration = new Configuration();
    public static TestRepository repository = new InMemoryTestRepository();
    public static TestService testService;
    public static DateTimeProvider dateTimeProvider;
    public static RunnerProvider runnerProvider;
    public static Test selectedTest;
    public static List<Test> unfilteredTests = new ArrayList<>();
    public static List<Test> filteredTests = new ArrayList<>();
    public static String testScriptDisplay;
    public static TestTablePanel testTablePanel;
    public static Map<String, String> testFileContents = new HashMap<>();

    static {
        initializeProviders();
    }

    public static void initializeProviders() {
        if (configuration.isUseTestDoubleForDateTime()) {
            TestDate testDate = TestDate.parse(configuration.getValueTestDoubleForDateTime());
            dateTimeProvider = new TestDoubleDateTimeProvider(testDate);
        } else {
            dateTimeProvider = new SystemDateTimeProvider();
        }

        if (configuration.isUseTestDoubleForRunner()) {
            runnerProvider = new TestDoubleRunnerProvider(configuration.getValueTestDoubleForRunner());
        } else {
            runnerProvider = new SystemRunnerProvider();
        }

        testService = new TestService(repository, dateTimeProvider, runnerProvider);
    }

    public static void reset() {
        configuration = new Configuration();
        repository = new InMemoryTestRepository();
        unfilteredTests = new ArrayList<>();
        filteredTests = new ArrayList<>();
        testFileContents = new HashMap<>();
        selectedTest = null;
        testScriptDisplay = null;
        testTablePanel = null;
        initializeProviders();
    }
}
