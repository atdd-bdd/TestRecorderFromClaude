package com.testrecorder.ui;

import com.testrecorder.domain.*;
import com.testrecorder.service.TestService;
import java.util.List;
import java.util.Optional;

public class TestRecorderController {
    private final TestService testService;
    private TestRecorderFrame frame;
    private Test selectedTest;
    private boolean showActive = true;
    private boolean showInactive = true;
    private boolean showRetired = false;

    public TestRecorderController(TestService testService) {
        this.testService = testService;
    }

    public void setFrame(TestRecorderFrame frame) {
        this.frame = frame;
    }

    public void onTestSelected(String issueId, String subIssueId) {
        try {
            Optional<Test> test = testService.getTest(issueId, subIssueId);
            selectedTest = test.orElse(null);
            updateButtonStates();
            updateStatusBar();
        } catch (Exception e) {
            showError("Error selecting test: " + e.getMessage());
        }
    }

    public void onTestDeselected() {
        selectedTest = null;
        updateButtonStates();
        updateStatusBar();
    }

    public boolean hasSelection() {
        return selectedTest != null;
    }

    public Test getSelectedTest() {
        return selectedTest;
    }

    public void addTest() {
        AddTestDialog dialog = new AddTestDialog(frame);
        if (dialog.showDialog()) {
            try {
                testService.addTest(
                    dialog.getIssueId(),
                    dialog.getSubIssueId(),
                    dialog.getName(),
                    dialog.getFilePath()
                );
                refresh();
            } catch (Exception e) {
                showError("Error adding test: " + e.getMessage());
            }
        }
    }

    public void runSelectedTest() {
        if (selectedTest == null) {
            showError("Please select a test first");
            return;
        }

        RunTestDialog dialog = new RunTestDialog(frame, selectedTest);
        if (dialog.showDialog()) {
            try {
                testService.runTest(
                    selectedTest.getIssueId(),
                    selectedTest.getSubIssueId(),
                    dialog.getResult(),
                    dialog.getComments()
                );
                refresh();
            } catch (Exception e) {
                showError("Error running test: " + e.getMessage());
            }
        }
    }

    public void viewSelectedResults() {
        if (selectedTest == null) {
            showError("Please select a test first");
            return;
        }

        try {
            List<TestRun> testRuns = testService.getTestRuns(
                selectedTest.getIssueId(),
                selectedTest.getSubIssueId()
            );
            ViewResultsDialog dialog = new ViewResultsDialog(frame, selectedTest, testRuns);
            dialog.showDialog();
        } catch (Exception e) {
            showError("Error loading test results: " + e.getMessage());
        }
    }

    public void changeSelectedStatus() {
        if (selectedTest == null) {
            showError("Please select a test first");
            return;
        }

        ChangeStatusDialog dialog = new ChangeStatusDialog(frame, selectedTest);
        if (dialog.showDialog()) {
            try {
                testService.updateTestStatus(
                    selectedTest.getIssueId(),
                    selectedTest.getSubIssueId(),
                    dialog.getNewStatus()
                );
                refresh();
            } catch (Exception e) {
                showError("Error changing status: " + e.getMessage());
            }
        }
    }

    public void refresh() {
        try {
            List<Test> tests = testService.filterTests(showActive, showInactive, showRetired);
            frame.loadTests(tests);
            updateStatusBar();
        } catch (Exception e) {
            showError("Error refreshing tests: " + e.getMessage());
        }
    }

    public void setFilterActive(boolean active) {
        this.showActive = active;
        refresh();
    }

    public void setFilterInactive(boolean inactive) {
        this.showInactive = inactive;
        refresh();
    }

    public void setFilterRetired(boolean retired) {
        this.showRetired = retired;
        refresh();
    }

    public boolean isShowActive() {
        return showActive;
    }

    public boolean isShowInactive() {
        return showInactive;
    }

    public boolean isShowRetired() {
        return showRetired;
    }

    private void updateButtonStates() {
        if (frame != null) {
            frame.updateButtonStates(hasSelection());
        }
    }

    private void updateStatusBar() {
        if (frame != null) {
            frame.updateStatusBar();
        }
    }

    private void showError(String message) {
        if (frame != null) {
            frame.showError(message);
        }
    }

    public int getTestCount() {
        try {
            return testService.filterTests(showActive, showInactive, showRetired).size();
        } catch (Exception e) {
            return 0;
        }
    }
}
