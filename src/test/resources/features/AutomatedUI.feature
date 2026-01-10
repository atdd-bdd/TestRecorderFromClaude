Feature: Automated UI Testing

  Background:
    Given configuration values are:
      | Variable                   | Value                                            |
      | rootFilePath               | target\                                          |
      | useTestDoubleForDateTime   | true                                             |
      | useTestDoubleForRunner     | true                                             |
      | valueTestDoubleForDateTime | Oct 1, 2022, 12:30:01 AM                         |
      | valueTestDoubleForRunner   | Sam                                              |
      | formNotCloseOnExit         | true                                             |
      | databaseURL                | jdbc:hsqldb:hsql://localhost                     |
      | databaseJDBCDriver         | org.hsqldb.jdbcDriver                            |
      | databasePassword           |                                                  |
      | databaseUserID             | SA                                               |
    Given file exists
      | File Path               | Contents                              |
      | EnterTestResult.feature | Select test \n Run it \n Check result |

  @automated-ui
  Scenario: Automated - Add a test
    Given tests are empty
      | Issue ID | Sub Issue ID | Name | Last Result | Runner | Date Last Run | Date Previous Result | File Path | Comments |
    And automated application is started
    When automated user adds a test
      | Issue ID     | 12345                   |
      | Name         | Enter test result       |
      | Sub Issue ID | 678                     |
      | File Path    | EnterTestResult.feature |
    Then automated tests displayed are
      | Issue ID | Sub Issue ID | Name              | Last Result |
      | 12345    | 678          | Enter test result | Failure     |
    And automated application is closed

  @automated-ui
  Scenario: Automated - Run a test successfully
    Given tests are
      | Issue ID | SubIssueID | Name              | Runner  | Last Result | Date Last Run | Date Previous Result | File Path               |
      | 12345    | 678        | Enter test result | No Name | Failure     | Never         | Never                | EnterTestResult.feature |
    And automated application is started
    When automated user selects test
      | Issue ID   | 12345 |
      | SubIssueID | 678   |
    Then automated verify test run display contains
      | Test Script | Select test \n Run it \n Check result |
    When automated user selects test
      | Issue ID   | 12345 |
      | SubIssueID | 678   |
    And automated user enters test run
      | Result   | Success     |
      | Comments | Works great |
    Then automated tests displayed are
      | Issue ID | SubIssueID | Name              | Last Result |
      | 12345    | 678        | Enter test result | Success     |
    And automated application is closed

  @automated-ui
  Scenario: Automated - Display multiple tests with filters
    Given tests are
      | Issue ID | SubIssueID | Name   | Runner | Last Result | Date Last Run            | Date Previous Result      | File Path               | Comments    | Test Status |
      | 12345    | 123        | Name a | Sam    | Success     | Oct 1, 2022, 12:30:00 AM | Sep 30, 2022, 12:30:00 AM | EnterTestResult.feature | Works great | Active      |
      | 12348    | 123        | Name B | Bill   | Failure     | Oct 1, 2022, 12:30:01 AM | Sep 30, 2022, 12:30:04 AM | EnterTestResult.feature | Works great | Inactive    |
      | 12347    | 234        | Mame c | Jane   | Success     | Oct 1, 2022, 12:30:02 AM | Sep 30, 2022, 12:30:02 AM | EnterTestResult.feature | Works great | Active      |
      | 12344    | 456        | Name D | Wanda  | Failure     | Oct 1, 2022, 12:30:03 AM | Sep 30, 2022, 12:30:03 AM | EnterTestResult.feature | Works great | Retired     |
    And automated application is started
    Then automated tests displayed are
      | Issue ID | SubIssueID | Name   | Last Result |
      | 12345    | 123        | Name a | Success     |
      | 12347    | 234        | Mame c | Success     |
      | 12348    | 123        | Name B | Failure     |
    And automated application is closed
