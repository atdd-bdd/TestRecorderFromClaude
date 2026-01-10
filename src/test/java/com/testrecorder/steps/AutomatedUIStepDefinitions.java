package com.testrecorder.steps;

import com.testrecorder.domain.*;
import com.testrecorder.repository.DatabaseTestRepository;
import com.testrecorder.service.*;
import com.testrecorder.ui.TestRecorderFrame;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.Robot;
import org.assertj.swing.data.TableCell;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.*;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class AutomatedUIStepDefinitions {
    private final TestContext testContext;
    private DatabaseTestRepository repository;
    private TestService testService;
    private TestRecorderFrame testRecorderFrame;
    private FrameFixture frameFixture;
    private Robot robot;

    public AutomatedUIStepDefinitions() {
        this.testContext = TestContext.getInstance();
    }

    private void ensureRepository() throws SQLException {
        if (repository == null) {
            Configuration configuration = testContext.getConfiguration();
            repository = new DatabaseTestRepository(configuration);
            // Reinitialize providers to pick up test double settings from configuration
            testContext.reinitializeProviders();
            testService = new TestService(repository, testContext.getDateTimeProvider(), testContext.getRunnerProvider());
        }
    }

    @When("automated application is started")
    public void automated_application_is_started() throws Exception {
        ensureRepository();

        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }

        // Create robot for UI interaction
        robot = BasicRobot.robotWithCurrentAwtHierarchy();

        // Create and show the frame on the EDT
        final String rootPath = testContext.getConfiguration().getRootFilePath();
        testRecorderFrame = GuiActionRunner.execute(() -> {
            TestRecorderFrame frame = new TestRecorderFrame(testService, true, rootPath);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            return frame;
        });

        // Create fixture for the frame
        frameFixture = new FrameFixture(robot, testRecorderFrame);
        frameFixture.show();

        // Wait for the frame to be showing
        frameFixture.requireVisible();

        // Trigger refresh
        GuiActionRunner.execute(() -> testRecorderFrame.showAndRefresh());

        // Small delay to allow refresh to complete
        pause(500);
    }

    @When("automated user adds a test")
    public void automated_user_adds_a_test(DataTable dataTable) throws Exception {
        Map<String, String> data = dataTable.asMap();

        String issueId = data.get("Issue ID");
        String subIssueId = data.get("Sub Issue ID");
        String name = data.get("Name");
        String filePath = data.get("File Path");

        // Click Add Test button
        frameFixture.button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(JButton button) {
                return "Add Test".equals(button.getText());
            }
        }).click();

        // Wait for dialog to appear
        DialogFixture dialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
            @Override
            protected boolean isMatching(JDialog dialog) {
                return "Add New Test".equals(dialog.getTitle());
            }
        }).using(robot);

        // Fill in the form
        dialog.textBox(new GenericTypeMatcher<JTextField>(JTextField.class) {
            private int index = 0;
            @Override
            protected boolean isMatching(JTextField field) {
                // First text field is Issue ID
                return index++ == 0;
            }
        }).setText(issueId);

        // Find all text fields and fill them in order
        JTextField[] textFields = findTextFields(dialog.target());
        if (textFields.length >= 1) textFields[0].setText(issueId);
        if (textFields.length >= 2) textFields[1].setText(subIssueId);
        if (textFields.length >= 3) textFields[2].setText(name);
        if (textFields.length >= 4) textFields[3].setText(filePath);

        // Click Add button
        dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(JButton button) {
                return "Add".equals(button.getText());
            }
        }).click();

        // Wait for dialog to close
        pause(300);
    }

    @When("automated user selects test")
    public void automated_user_selects_test(DataTable dataTable) throws Exception {
        Map<String, String> data = dataTable.asMap();
        String issueId = data.get("Issue ID");
        String subIssueId = data.get("SubIssueID");

        // Find the table and select the row
        JTableFixture table = frameFixture.table();

        // Find the row with matching Issue ID and SubIssue ID
        int rowCount = table.rowCount();
        for (int row = 0; row < rowCount; row++) {
            String cellIssueId = table.cell(TableCell.row(row).column(0)).value();
            String cellSubIssueId = table.cell(TableCell.row(row).column(1)).value();

            if (issueId.equals(cellIssueId) && subIssueId.equals(cellSubIssueId)) {
                table.selectRows(row);
                pause(200);
                return;
            }
        }

        fail("Test not found with Issue ID: " + issueId + " and SubIssue ID: " + subIssueId);
    }

    @Then("automated verify test run display contains")
    public void automated_verify_test_run_display_contains(DataTable dataTable) throws Exception {
        Map<String, String> expected = dataTable.asMap();
        String expectedScript = expected.get("Test Script").replace("\\n", "\n");

        // Click Run Test button
        frameFixture.button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(JButton button) {
                return "Run Test".equals(button.getText());
            }
        }).click();

        // Wait for dialog to appear (title includes test ID, e.g., "Run Test: 12345/678")
        DialogFixture dialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
            @Override
            protected boolean isMatching(JDialog dialog) {
                String title = dialog.getTitle();
                return title != null && title.startsWith("Run Test") && dialog.isShowing();
            }
        }).using(robot);

        // Find the text area with the script content
        JTextArea scriptArea = findTextArea(dialog.target());
        assertNotNull(scriptArea, "Script area not found in Run Test dialog");

        String actualScript = scriptArea.getText().trim();
        String normalizedExpected = expectedScript.trim();

        assertEquals(normalizedExpected, actualScript, "Test script content mismatch");

        // Cancel the dialog (we're just verifying, not running)
        dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(JButton button) {
                return "Cancel".equals(button.getText());
            }
        }).click();

        pause(200);
    }

    @When("automated user enters test run")
    public void automated_user_enters_test_run(DataTable dataTable) throws Exception {
        Map<String, String> data = dataTable.asMap();
        String result = data.get("Result");
        String comments = data.get("Comments");

        // Click Run Test button
        frameFixture.button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(JButton button) {
                return "Run Test".equals(button.getText());
            }
        }).click();

        // Wait for dialog to appear (title includes test ID, e.g., "Run Test: 12345/678")
        DialogFixture dialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
            @Override
            protected boolean isMatching(JDialog dialog) {
                String title = dialog.getTitle();
                return title != null && title.startsWith("Run Test") && dialog.isShowing();
            }
        }).using(robot);

        // Select result radio button
        dialog.radioButton(new GenericTypeMatcher<JRadioButton>(JRadioButton.class) {
            @Override
            protected boolean isMatching(JRadioButton radioButton) {
                return result.equals(radioButton.getText());
            }
        }).click();

        // Enter comments in the text area that's for comments (not the script)
        JTextArea[] textAreas = findTextAreas(dialog.target());
        // The comments text area should be the editable one
        for (JTextArea ta : textAreas) {
            if (ta.isEditable()) {
                ta.setText(comments);
                break;
            }
        }

        // Click Run button
        dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(JButton button) {
                return "Run".equals(button.getText());
            }
        }).click();

        pause(500);
    }

    @Then("automated tests displayed are")
    public void automated_tests_displayed_are(DataTable dataTable) throws Exception {
        List<Map<String, String>> expectedRows = dataTable.asMaps();

        // Refresh the table
        frameFixture.button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(JButton button) {
                return "Refresh".equals(button.getText());
            }
        }).click();

        pause(300);

        JTableFixture table = frameFixture.table();
        int actualRowCount = table.rowCount();

        assertEquals(expectedRows.size(), actualRowCount,
            "Expected " + expectedRows.size() + " rows but found " + actualRowCount);

        // Verify each row
        for (int i = 0; i < expectedRows.size(); i++) {
            Map<String, String> expected = expectedRows.get(i);

            if (expected.containsKey("Issue ID")) {
                String actual = table.cell(TableCell.row(i).column(0)).value();
                assertEquals(expected.get("Issue ID"), actual, "Issue ID mismatch at row " + i);
            }
            if (expected.containsKey("SubIssueID") || expected.containsKey("Sub Issue ID")) {
                String expectedSubId = expected.containsKey("SubIssueID") ?
                    expected.get("SubIssueID") : expected.get("Sub Issue ID");
                String actual = table.cell(TableCell.row(i).column(1)).value();
                assertEquals(expectedSubId, actual, "SubIssue ID mismatch at row " + i);
            }
            if (expected.containsKey("Name")) {
                String actual = table.cell(TableCell.row(i).column(2)).value();
                assertEquals(expected.get("Name"), actual, "Name mismatch at row " + i);
            }
            if (expected.containsKey("Last Result")) {
                String actual = table.cell(TableCell.row(i).column(4)).value();
                assertEquals(expected.get("Last Result"), actual, "Last Result mismatch at row " + i);
            }
        }
    }

    @When("automated application is closed")
    public void automated_application_is_closed() {
        if (frameFixture != null) {
            frameFixture.cleanUp();
        }
        if (robot != null) {
            robot.cleanUp();
        }
    }

    // Helper methods
    private JTextField[] findTextFields(Container container) {
        java.util.List<JTextField> fields = new java.util.ArrayList<>();
        findComponents(container, JTextField.class, fields);
        return fields.toArray(new JTextField[0]);
    }

    private JTextArea findTextArea(Container container) {
        java.util.List<JTextArea> areas = new java.util.ArrayList<>();
        findComponents(container, JTextArea.class, areas);
        return areas.isEmpty() ? null : areas.get(0);
    }

    private JTextArea[] findTextAreas(Container container) {
        java.util.List<JTextArea> areas = new java.util.ArrayList<>();
        findComponents(container, JTextArea.class, areas);
        return areas.toArray(new JTextArea[0]);
    }

    @SuppressWarnings("unchecked")
    private <T extends Component> void findComponents(Container container, Class<T> type, java.util.List<T> result) {
        for (Component c : container.getComponents()) {
            if (type.isInstance(c)) {
                result.add((T) c);
            }
            if (c instanceof Container) {
                findComponents((Container) c, type, result);
            }
        }
    }

    private void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
