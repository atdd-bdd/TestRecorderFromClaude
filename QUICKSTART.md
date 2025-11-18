# Quick Start Guide

Get started with Test Recorder in 5 minutes!

## Prerequisites

Ensure you have:
- Java 11+ installed (`java -version`)
- Maven 3.6+ installed (`mvn -version`)

## Quick Setup

```bash
# 1. Navigate to project directory
cd test-recorder

# 2. Build the project
mvn clean compile

# 3. Run tests
mvn test

# 4. View test reports
open target/cucumber-reports.html
# Or on Linux:
xdg-open target/cucumber-reports.html
```

## Alternative: Use Build Script

```bash
# Make script executable (if not already)
chmod +x build.sh

# Run build and test
./build.sh
```

## What Gets Tested

When you run `mvn test`, Cucumber executes all feature files:

✓ **TestRecorder.feature** - Add, run, and manage tests
✓ **BusinessRules.feature** - Test update logic
✓ **DomainTerms.feature** - Validation rules (IssueID, SubIssueID, dates, etc.)
✓ **Entities.feature** - Configuration and database operations
✓ **Flow.feature** - End-to-end test scenarios
✓ **OS.feature** - Environment variable handling
✓ **SortAndFilter.feature** - Filter tests by status

*Note: UI tests tagged with @manual are skipped in automated runs*

## Running Specific Features

```bash
# Run only domain validation tests
mvn test -Dcucumber.features=src/test/resources/features/DomainTerms.feature

# Run all except manual tests
mvn test -Dcucumber.filter.tags="not @manual"
```

## Understanding the Output

### Success
```
Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### View Results
- **Console**: See step-by-step execution in terminal
- **HTML Report**: `target/cucumber-reports.html` - Beautiful visual report
- **JSON Report**: `target/cucumber.json` - For CI/CD integration

## Example Test Execution

When you run tests, you'll see output like:

```
Scenario: Add a test
  Given configuration values are:
  Given tests are empty
  When adding a test
  Then tests now are
  ✓ Passed

Scenario: Run a test successfully  
  Given test exists
  And value for runner is
  And value for current date is
  When test is selected
  When test is run
  Then test run display contains
  And test is now
  And test run records exist
  ✓ Passed
```

## Common Issues

### Issue: Maven not found
**Solution**: Install Maven
```bash
# Ubuntu/Debian
sudo apt-get install maven

# macOS
brew install maven
```

### Issue: Java version too old
**Solution**: Install Java 11 or higher
```bash
# Ubuntu/Debian
sudo apt-get install openjdk-11-jdk

# macOS
brew install openjdk@11
```

### Issue: Port already in use (for database tests)
**Solution**: The database tests create an embedded database. If you see port conflicts:
```bash
# Check what's using port 9001 (HSQLDB default)
lsof -i :9001

# Kill the process if needed
kill <PID>
```

## Project Structure Overview

```
test-recorder/
├── src/
│   ├── main/java/          ← Production code
│   │   └── com/testrecorder/
│   │       ├── domain/     ← Business entities
│   │       ├── repository/ ← Data access
│   │       ├── service/    ← Business logic
│   │       ├── ui/         ← User interface
│   │       └── util/       ← Utilities
│   └── test/
│       ├── java/           ← Step definitions
│       └── resources/
│           └── features/   ← Cucumber feature files
├── pom.xml                 ← Maven configuration
├── README.md               ← Full documentation
└── PROJECT_SUMMARY.md      ← Project overview
```

## Next Steps

1. **Explore the Code**: Browse `src/main/java/com/testrecorder/`
2. **Read Feature Files**: Check `src/test/resources/features/`
3. **Run Specific Tests**: Try different features
4. **Modify Configuration**: Edit configuration in step definitions
5. **Run the Application**: Launch the UI application

## Running the Application UI

```bash
# Compile and package
mvn package

# Run the application
java -cp target/test-recorder-1.0.0.jar com.testrecorder.TestRecorderApplication
```

The Swing UI will appear showing the test table.

## Getting Help

- **README.md**: Full documentation with all details
- **PROJECT_SUMMARY.md**: Architecture and design overview
- **Feature Files**: Examples of how to use each feature
- **Step Definitions**: Implementation details

## Success Indicators

You'll know everything is working when:

✓ `mvn test` completes successfully
✓ HTML report shows all scenarios passing
✓ No compilation errors
✓ Test execution is deterministic (same results every time)

That's it! You now have a fully functional test management system with BDD testing.
