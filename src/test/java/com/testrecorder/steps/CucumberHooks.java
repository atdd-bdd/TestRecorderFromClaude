package com.testrecorder.steps;

import com.testrecorder.repository.DatabaseTestRepository;
import io.cucumber.java.Before;
import java.sql.*;

public class CucumberHooks {

    @Before
    public void resetSharedState() {
        // If there's an existing database repository, clean it up first
        if (SharedState.repository instanceof DatabaseTestRepository) {
            try {
                cleanDatabase();
            } catch (SQLException e) {
                // Ignore cleanup errors
            }
        }
        SharedState.reset();
    }

    private void cleanDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(
                SharedState.configuration.getDatabaseURL(),
                SharedState.configuration.getDatabaseUserID(),
                SharedState.configuration.getDatabasePassword());
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM test_runs");
            stmt.execute("DELETE FROM tests");
            stmt.execute("DELETE FROM selected_test");
        } catch (SQLException e) {
            // Tables might not exist yet, ignore
        }
    }
}
