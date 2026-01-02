package com.testrecorder.repository;

import com.testrecorder.domain.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseTestRepository implements TestRepository {
    private final Connection connection;

    public DatabaseTestRepository(Configuration config) throws SQLException {
        try {
            Class.forName(config.getDatabaseJDBCDriver());
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found", e);
        }
        
        this.connection = DriverManager.getConnection(
                config.getDatabaseURL(),
                config.getDatabaseUserID(),
                config.getDatabasePassword()
        );
        
        initializeTables();
    }

    private void initializeTables() throws SQLException {
        String createTestsTable =
            "CREATE TABLE IF NOT EXISTS tests (" +
                "issue_id VARCHAR(5) NOT NULL, " +
                "sub_issue_id VARCHAR(3) NOT NULL, " +
                "name VARCHAR(255), " +
                "runner VARCHAR(255), " +
                "last_result VARCHAR(50), " +
                "date_last_run VARCHAR(50), " +
                "date_previous_result VARCHAR(50), " +
                "file_path VARCHAR(500), " +
                "comments VARCHAR(1000), " +
                "test_status VARCHAR(50), " +
                "PRIMARY KEY (issue_id, sub_issue_id)" +
            ")";

        String createTestRunsTable =
            "CREATE TABLE IF NOT EXISTS test_runs (" +
                "issue_id VARCHAR(5) NOT NULL, " +
                "sub_issue_id VARCHAR(3) NOT NULL, " +
                "date_time VARCHAR(50) NOT NULL, " +
                "result VARCHAR(50), " +
                "comments VARCHAR(1000), " +
                "runner VARCHAR(255), " +
                "PRIMARY KEY (issue_id, sub_issue_id, date_time)" +
            ")";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTestsTable);
            stmt.execute(createTestRunsTable);
        }
    }

    @Override
    public void save(Test test) {
        // First try to update, if no rows affected then insert
        String updateSql =
            "UPDATE tests SET name = ?, runner = ?, last_result = ?, " +
                "date_last_run = ?, date_previous_result = ?, file_path = ?, " +
                "comments = ?, test_status = ? " +
            "WHERE issue_id = ? AND sub_issue_id = ?";

        String insertSql =
            "INSERT INTO tests (issue_id, sub_issue_id, name, runner, last_result, " +
                "date_last_run, date_previous_result, file_path, comments, test_status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            // Try update first
            try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
                pstmt.setString(1, test.getName());
                pstmt.setString(2, test.getRunner());
                pstmt.setString(3, test.getLastResult().toString());
                pstmt.setString(4, test.getDateLastRun().toString());
                pstmt.setString(5, test.getDatePreviousResult().toString());
                pstmt.setString(6, test.getFilePath());
                pstmt.setString(7, test.getComments());
                pstmt.setString(8, test.getTestStatus().toString());
                pstmt.setString(9, test.getIssueId());
                pstmt.setString(10, test.getSubIssueId());
                int rowsUpdated = pstmt.executeUpdate();

                if (rowsUpdated == 0) {
                    // No existing row, do insert
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                        insertStmt.setString(1, test.getIssueId());
                        insertStmt.setString(2, test.getSubIssueId());
                        insertStmt.setString(3, test.getName());
                        insertStmt.setString(4, test.getRunner());
                        insertStmt.setString(5, test.getLastResult().toString());
                        insertStmt.setString(6, test.getDateLastRun().toString());
                        insertStmt.setString(7, test.getDatePreviousResult().toString());
                        insertStmt.setString(8, test.getFilePath());
                        insertStmt.setString(9, test.getComments());
                        insertStmt.setString(10, test.getTestStatus().toString());
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save test", e);
        }
    }

    @Override
    public Optional<Test> findByKey(String issueId, String subIssueId) {
        String sql = "SELECT * FROM tests WHERE issue_id = ? AND sub_issue_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, issueId);
            pstmt.setString(2, subIssueId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTest(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find test", e);
        }
        
        return Optional.empty();
    }

    @Override
    public List<Test> findAll() {
        List<Test> tests = new ArrayList<>();
        String sql = "SELECT * FROM tests";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                tests.add(mapResultSetToTest(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all tests", e);
        }

        return tests;
    }

    @Override
    public void deleteAll() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM tests");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete all tests", e);
        }
    }

    @Override
    public void saveTestRun(TestRun testRun) {
        String sql =
            "INSERT INTO test_runs (issue_id, sub_issue_id, date_time, result, comments, runner) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, testRun.getIssueId());
            pstmt.setString(2, testRun.getSubIssueId());
            pstmt.setString(3, testRun.getDateTime().toString());
            pstmt.setString(4, testRun.getResult().toString());
            pstmt.setString(5, testRun.getComments());
            pstmt.setString(6, testRun.getRunner());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save test run", e);
        }
    }

    @Override
    public List<TestRun> findTestRunsByKey(String issueId, String subIssueId) {
        List<TestRun> testRuns = new ArrayList<>();
        String sql = "SELECT * FROM test_runs WHERE issue_id = ? AND sub_issue_id = ? ORDER BY date_time";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, issueId);
            pstmt.setString(2, subIssueId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    testRuns.add(mapResultSetToTestRun(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find test runs", e);
        }

        return testRuns;
    }

    @Override
    public List<TestRun> findAllTestRuns() {
        List<TestRun> testRuns = new ArrayList<>();
        String sql = "SELECT * FROM test_runs ORDER BY date_time";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                testRuns.add(mapResultSetToTestRun(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all test runs", e);
        }

        return testRuns;
    }

    @Override
    public void deleteAllTestRuns() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM test_runs");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete all test runs", e);
        }
    }

    private Test mapResultSetToTest(ResultSet rs) throws SQLException {
        Test test = new Test();
        test.setIssueId(rs.getString("issue_id"));
        test.setSubIssueId(rs.getString("sub_issue_id"));
        test.setName(rs.getString("name"));
        test.setRunner(rs.getString("runner"));
        test.setLastResult(TestResult.fromString(rs.getString("last_result")));
        test.setDateLastRun(TestDate.parse(rs.getString("date_last_run")));
        test.setDatePreviousResult(TestDate.parse(rs.getString("date_previous_result")));
        test.setFilePath(rs.getString("file_path"));
        test.setComments(rs.getString("comments"));
        test.setTestStatus(TestStatus.fromString(rs.getString("test_status")));
        return test;
    }

    private TestRun mapResultSetToTestRun(ResultSet rs) throws SQLException {
        return new TestRun(
                rs.getString("issue_id"),
                rs.getString("sub_issue_id"),
                TestDate.parse(rs.getString("date_time")),
                TestResult.fromString(rs.getString("result")),
                rs.getString("comments"),
                rs.getString("runner")
        );
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
