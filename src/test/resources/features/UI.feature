Feature: UI

  Background:
    Given configuration values are:
      | Variable                   | Value                                            |
      | rootFilePath               | target\ |
      | useTestDoubleForDateTime   | true                                             |
      | useTestDoubleForRunner     | true                                             |
      | valueTestDoubleForDateTime | Oct 1, 2022, 12:30:01 AM                         |
      | valueTestDoubleForRunner   | Sam                                              |
      | formNotCloseOnExit         | true                                             |
      | databaseURL                | jdbc:hsqldb:hsql://localhost/testrecorder                     |
      | databaseJDBCDriver         | org.hsqldb.jdbcDriver                            |
      | databasePassword           |                                                  |
      | databaseUserID             | SA                                               |
    Given file exists
      | File Path               | Contents                              |
      | EnterTestResult.feature | Select test \n Run it \n Check result |

  @manual
  Scenario: Add a test manually
    Given tests are empty
      | Issue ID | Sub Issue ID | Name | Last Result | Runner | Date Last Run | Date Previous Result | File Path | Comments |
    And the application is started
    When the user adds a test
      | Issue ID     | 12345                   |
      | Name         | Enter test result       |
      | Sub Issue ID | 678                     |
      | File Path    | EnterTestResult.feature |
    Then tests displayed are
      | Issue ID | Sub Issue ID | Name              | Runner | Last Result | Date Last Run | Date Previous Result | File Path               | Comments |
      | 12345    | 678          | Enter test result |        | Failure     | Never         | Never                | EnterTestResult.feature |          |
    And user closes application
  @manual
  Scenario: Run a test successfully
    Given tests are
      | Issue ID | SubIssueID | Name              | Runner  | Last Result | Date Last Run | Date Previous Result | File Path               |
      | 12345    | 678        | Enter test result | No Name | Failure     | Never         | Never                | EnterTestResult.feature |
    And the application is started
    When user selects a test
      | Issue ID   | 12345 |
      | SubIssueID | 678   |
   Then user verifies test run display contains
      | Test Script | Select test \n Run it \n Check result |
   When the user enters the test run
      | Result   | Success     |
      | Comments | Works great |
   Then tests displayed are
      | Issue ID | SubIssueID | Name              | Runner | Last Result | Date Last Run            | Date Previous Result | File Path               | Comments    |
      | 12345    | 678        | Enter test result | Sam    | Success     | Oct 1, 2022, 12:30:01 AM | Never                | EnterTestResult.feature | Works great |
    And user closes application

  @manual
  Scenario: Run Manual with Test Doubles
    Given tests are
      | Issue ID | SubIssueID | Name   | Runner | Last Result | Date Last Run            | Date Previous Result       | File Path               | Comments    | Test Status |
      | 12345    | 123        | Name a | Sam    | Success     | Oct 1, 2022, 12:30:00 AM | Sep 30, 2022, 12:30:00 AM | EnterTestResult.feature | Works great | Active      |
      | 12348    | 123        | Name B | Bill   | Failure     | Oct 1, 2022, 12:30:01 AM | Sep 30, 2022, 12:30:04 AM | EnterTestResult.feature | Works great | Inactive    |
      | 12347    | 234        | Mame c | Jane   | Success     | Oct 1, 2022, 12:30:02 AM | Sep 30, 2022, 12:30:02 AM | EnterTestResult.feature | Works great | Active      |
      | 12344    | 456        | Name D | Wanda  | Failure     | Oct 1, 2022, 12:30:03 AM | Sep 30, 2022, 12:30:03 AM | EnterTestResult.feature | Works great | Retired     |
   And the application is started
   When Test Status filter includes
      | Active   |
      | Inactive |
   Then tests displayed are
      | Issue ID | SubIssueID | Name   | Runner | Last Result | Date Last Run            | Date Previous Result       | File Path               | Comments    | Test Status |
      | 12345    | 123        | Name a | Sam    | Success     | Oct 1, 2022, 12:30:00 AM | Sep 30, 2022, 12:30:00 AM | EnterTestResult.feature | Works great | Active      |
      | 12348    | 123        | Name B | Bill   | Failure     | Oct 1, 2022, 12:30:01 AM | Sep 30, 2022, 12:30:04 AM | EnterTestResult.feature | Works great | Inactive    |
      | 12347    | 234        | Mame c | Jane   | Success     | Oct 1, 2022, 12:30:02 AM | Sep 30, 2022, 12:30:02 AM | EnterTestResult.feature | Works great | Active      |
    And user closes application

  @manual @set_up_run
  Scenario: Run manual with no test doubles
    Given tests are
      | Issue ID | SubIssueID | Name              | Runner  | Last Result | Date Last Run | Date Previous Result | File Path               |
      | 12345    | 678        | Enter test result | No Name | Failure     | Never         | Never                | EnterTestResult.feature |
    And  configuration values are:
      | Variable                 | Value                                            |
      | useTestDoubleForDateTime | false                                            |
      | useTestDoubleForRunner   | false                                            |
      | rootFilePath             | target\ |
      | formNotCloseOnExit       | false                                            |
      | databaseURL              | jdbc:hsqldb:hsql://localhost/testrecorder                     |
      | databaseJDBCDriver       | org.hsqldb.jdbcDriver                            |
      | databasePassword         |                                                  |
      | databaseUserID           | SA                                               |
   And the application is started
   When user selects a test
      | Issue ID   | 12345 |
      | SubIssueID | 678   |
   Then user verifies test run display contains
      | Test Script | Select test \n Run it \n Check result |
   When the user enters the test run
      | Result   | Success     |
      | Comments | Works great |
   Then tests displayed are
      | Issue ID | SubIssueID | Name              |  Last Result | Date Previous Result | File Path               |
      | 12345    | 678        | Enter test result |  Success     | Never                | EnterTestResult.feature |
    And user closes application
    
