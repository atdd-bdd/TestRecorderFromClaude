# Test Recorder

A comprehensive test management system built with Java and Cucumber for BDD (Behavior-Driven Development).

## Overview

Test Recorder is a test management application that allows you to:
- Create and manage test cases
- Execute tests and record results
- Track test history with timestamps
- Filter and organize tests by status
- Store test data in memory or database (HSQLDB)
- View test results in a Swing-based UI

## Project Structure

```
test-recorder/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/testrecorder/
│   │           ├── domain/          # Domain model and business entities
│   │           │   ├── Test.java
│   │           │   ├── TestRun.java
│   │           │   ├── Configuration.java
│   │           │   ├── TestResult.java (enum)
│   │           │   ├── TestStatus.java (enum)
│   │           │   ├── TestDate.java
│   │           │   ├── IssueId.java
│   │           │   ├── SubIssueId.java
│   │           │   ├── Name.java
│   │           │   └── MyString.java
│   │           ├── repository/      # Data access layer
│   │           │   ├── TestRepository.java (interface)
│   │           │   ├── InMemoryTestRepository.java
│   │           │   └── DatabaseTestRepository.java
│   │           ├── service/         # Business logic layer
│   │           │   ├── TestService.java
│   │           │   ├── DateTimeProvider.java (interface)
│   │           │   ├── RunnerProvider.java (interface)
│   │           │   ├── SystemDateTimeProvider.java
│   │           │   ├── SystemRunnerProvider.java
│   │           │   ├── TestDoubleDateTimeProvider.java
│   │           │   └── TestDoubleRunnerProvider.java
│   │           ├── ui/              # User interface components
│   │           │   └── TestTablePanel.java
│   │           ├── util/            # Utility classes
│   │           │   └── EnvironmentUtil.java
│   │           └── TestRecorderApplication.java  # Main entry point
│   └── test/
│       ├── java/
│       │   └── com/testrecorder/
│       │       ├── CucumberTestRunner.java
│       │       └── steps/           # Cucumber step definitions
│       │           ├── TestContext.java
│       │           ├── CommonStepDefinitions.java
│       │           ├── TestRecorderStepDefinitions.java
│       │           ├── BusinessRulesStepDefinitions.java
│       │           ├── DomainTermsStepDefinitions.java
│       │           ├── EntitiesStepDefinitions.java
│       │           ├── EnvironmentStepDefinitions.java
│       │           ├── FilterStepDefinitions.java
│       │           └── UIStepDefinitions.java
│       └── resources/
│           └── features/            # Cucumber feature files
│               ├── TestRecorder.feature
│               ├── Entities.feature
│               ├── UI.feature
│               ├── BusinessRules.feature
│               ├── DomainTerms.feature
│               ├── Flow.feature
│               ├── OS.feature
│               └── SortAndFilter.feature
└── pom.xml                          # Maven configuration
```

## Key Features

### Domain Model
- **Test**: Represents a test case with attributes like Issue ID, Sub Issue ID, Name, Runner, Last Result, etc.
- **TestRun**: Represents an execution of a test with result, comments, runner, and timestamp
- **Configuration**: Manages application settings including database connection and test doubles

### Domain Validation
- **IssueId**: Must be exactly 5 alphanumeric characters without spaces
- **SubIssueId**: Must be exactly 3 alphanumeric characters without spaces
- **Name**: Alphanumeric characters and spaces only (invalid characters removed)
- **MyString**: Removes invalid characters like parentheses, dollar signs, question marks
- **TestDate**: Supports date format "MMM d, yyyy, h:mm:ss a" and special "Never" value

### Test Status
- **Active**: Test is currently in use
- **Inactive**: Test is temporarily disabled
- **Retired**: Test is permanently retired

### Test Result
- **Success**: Test passed
- **Failure**: Test failed

### Business Rules
- When a test is run, it updates the test's last result, date last run, and moves the previous date last run to date previous result
- Tests can only be updated if the new test run's date is after the current date last run
- Adding a test that already exists (same Issue ID and Sub Issue ID) does not modify the existing test

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Building the Project

```bash
# Compile the project
mvn clean compile

# Run tests
mvn test

# Package as JAR
mvn package
```

## Running the Application

```bash
# Run the main application
mvn exec:java -Dexec.mainClass="com.testrecorder.TestRecorderApplication"

# Or run the compiled JAR
java -jar target/test-recorder-1.0.0.jar
```

## Running Cucumber Tests

The tests are organized into several feature files:

1. **TestRecorder.feature**: Core test recording functionality
2. **Entities.feature**: Configuration and database operations
3. **UI.feature**: User interface tests (mostly manual)
4. **BusinessRules.feature**: Business logic validation
5. **DomainTerms.feature**: Domain model validation rules
6. **Flow.feature**: End-to-end test flows
7. **OS.feature**: Environment variable management
8. **SortAndFilter.feature**: Filtering and sorting functionality

To run all tests:
```bash
mvn test
```

To run specific feature:
```bash
mvn test -Dcucumber.filter.tags="@tag-name"
```

To exclude manual tests:
```bash
mvn test -Dcucumber.filter.tags="not @manual"
```

## Configuration

The application can be configured using a properties file. Create a `config.properties` file with the following settings:

```properties
rootFilePath=target/
useTestDoubleForDateTime=false
useTestDoubleForRunner=false
valueTestDoubleForDateTime=
valueTestDoubleForRunner=
formNotCloseOnExit=false
databaseURL=jdbc:hsqldb:hsql://localhost
databaseJDBCDriver=org.hsqldb.jdbcDriver
databasePassword=
databaseUserID=SA
```

Load the configuration:
```bash
java -Dconfig.file=config.properties -jar target/test-recorder-1.0.0.jar
```

## Database Setup

The application supports HSQLDB for persistent storage. To use the database:

1. Start HSQLDB server:
```bash
java -cp hsqldb.jar org.hsqldb.server.Server --database.0 file:testrecorder --dbname.0 testrecorder
```

2. Configure the application to use the database in your configuration file:
```properties
databaseURL=jdbc:hsqldb:hsql://localhost/testrecorder
```

## Test Doubles

The application supports test doubles for DateTime and Runner providers, which is useful for testing:

- **DateTimeProvider**: Can be set to return a fixed date/time for testing
- **RunnerProvider**: Can be set to return a fixed runner name for testing

Enable test doubles in configuration:
```properties
useTestDoubleForDateTime=true
valueTestDoubleForDateTime=Oct 1, 2022, 12:30:01 AM
useTestDoubleForRunner=true
valueTestDoubleForRunner=TestRunner
```

## Example Usage

### Adding a Test
```java
TestService testService = new TestService(repository, dateTimeProvider, runnerProvider);
testService.addTest("12345", "678", "My Test", "test.feature");
```

### Running a Test
```java
testService.runTest("12345", "678", TestResult.SUCCESS, "Test passed");
```

### Filtering Tests
```java
List<Test> activeTests = testService.filterTests(true, false, false);
```

## Cucumber Integration

The project uses Cucumber for BDD testing. Step definitions are organized by feature area:

- **CommonStepDefinitions**: Shared steps for configuration and setup
- **TestRecorderStepDefinitions**: Test recording operations
- **BusinessRulesStepDefinitions**: Business rule validation
- **DomainTermsStepDefinitions**: Domain validation
- **EntitiesStepDefinitions**: Configuration and database operations
- **EnvironmentStepDefinitions**: Environment variable management
- **FilterStepDefinitions**: Filtering and sorting
- **UIStepDefinitions**: User interface operations

## Test Reports

After running tests, reports are generated in:
- HTML Report: `target/cucumber-reports.html`
- JSON Report: `target/cucumber.json`

## License

This is a sample project for demonstration purposes.

## Author

Generated for Test Recorder project
