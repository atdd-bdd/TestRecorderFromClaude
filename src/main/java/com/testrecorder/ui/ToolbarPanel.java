package com.testrecorder.ui;

import javax.swing.*;
import java.awt.*;

public class ToolbarPanel extends JPanel {
    private final TestRecorderController controller;
    private JButton addTestButton;
    private JButton runTestButton;
    private JButton viewResultsButton;
    private JButton changeStatusButton;
    private JButton refreshButton;
    private JCheckBox activeCheckBox;
    private JCheckBox inactiveCheckBox;
    private JCheckBox retiredCheckBox;

    public ToolbarPanel(TestRecorderController controller) {
        this.controller = controller;
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        initializeComponents();
        setupListeners();
    }

    private void initializeComponents() {
        addTestButton = new JButton("Add Test");
        addTestButton.setToolTipText("Add a new test (Ctrl+N)");
        add(addTestButton);

        runTestButton = new JButton("Run Test");
        runTestButton.setToolTipText("Run selected test (Ctrl+R)");
        runTestButton.setEnabled(false);
        add(runTestButton);

        viewResultsButton = new JButton("View Results");
        viewResultsButton.setToolTipText("View test run history");
        viewResultsButton.setEnabled(false);
        add(viewResultsButton);

        changeStatusButton = new JButton("Change Status");
        changeStatusButton.setToolTipText("Change test status");
        changeStatusButton.setEnabled(false);
        add(changeStatusButton);

        add(new JSeparator(SwingConstants.VERTICAL));

        refreshButton = new JButton("Refresh");
        refreshButton.setToolTipText("Refresh test list (F5)");
        add(refreshButton);

        add(Box.createHorizontalStrut(20));
        add(new JLabel("Filter:"));

        activeCheckBox = new JCheckBox("Active", controller.isShowActive());
        add(activeCheckBox);

        inactiveCheckBox = new JCheckBox("Inactive", controller.isShowInactive());
        add(inactiveCheckBox);

        retiredCheckBox = new JCheckBox("Retired", controller.isShowRetired());
        add(retiredCheckBox);
    }

    private void setupListeners() {
        addTestButton.addActionListener(e -> controller.addTest());
        runTestButton.addActionListener(e -> controller.runSelectedTest());
        viewResultsButton.addActionListener(e -> controller.viewSelectedResults());
        changeStatusButton.addActionListener(e -> controller.changeSelectedStatus());
        refreshButton.addActionListener(e -> controller.refresh());

        activeCheckBox.addActionListener(e -> controller.setFilterActive(activeCheckBox.isSelected()));
        inactiveCheckBox.addActionListener(e -> controller.setFilterInactive(inactiveCheckBox.isSelected()));
        retiredCheckBox.addActionListener(e -> controller.setFilterRetired(retiredCheckBox.isSelected()));
    }

    public void updateButtonStates(boolean hasSelection) {
        runTestButton.setEnabled(hasSelection);
        viewResultsButton.setEnabled(hasSelection);
        changeStatusButton.setEnabled(hasSelection);
    }
}
