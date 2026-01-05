package com.testrecorder.ui;

import com.testrecorder.domain.Test;
import com.testrecorder.service.TestService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TestRecorderFrame extends JFrame {
    private final TestRecorderController controller;
    private final TestTablePanel tablePanel;
    private final ToolbarPanel toolbarPanel;
    private final StatusBar statusBar;
    private boolean databaseConnected;

    public TestRecorderFrame(TestService testService, boolean databaseConnected) {
        super("Test Recorder");
        this.databaseConnected = databaseConnected;

        // Create controller
        this.controller = new TestRecorderController(testService);

        // Create components
        this.tablePanel = new TestTablePanel();
        this.tablePanel.setController(controller);
        this.toolbarPanel = new ToolbarPanel(controller);
        this.statusBar = new StatusBar();

        // Link controller to frame
        controller.setFrame(this);

        // Setup UI
        initializeFrame();
        setupKeyboardShortcuts();

        // Update status bar
        statusBar.setDatabaseConnected(databaseConnected);
    }

    private void initializeFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLocationRelativeTo(null);

        // Layout
        setLayout(new BorderLayout());
        add(toolbarPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        // Menu bar
        setJMenuBar(createMenuBar());
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem addTestItem = new JMenuItem("Add Test...");
        addTestItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        addTestItem.addActionListener(e -> controller.addTest());
        fileMenu.add(addTestItem);

        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(e -> {
            dispose();
            System.exit(0);
        });
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);

        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);

        JMenuItem runTestItem = new JMenuItem("Run Selected Test...");
        runTestItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        runTestItem.addActionListener(e -> controller.runSelectedTest());
        editMenu.add(runTestItem);

        JMenuItem changeStatusItem = new JMenuItem("Change Status...");
        changeStatusItem.addActionListener(e -> controller.changeSelectedStatus());
        editMenu.add(changeStatusItem);

        menuBar.add(editMenu);

        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);

        JMenuItem viewResultsItem = new JMenuItem("View Results...");
        viewResultsItem.addActionListener(e -> controller.viewSelectedResults());
        viewMenu.add(viewResultsItem);

        viewMenu.addSeparator();

        JMenuItem refreshItem = new JMenuItem("Refresh");
        refreshItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        refreshItem.addActionListener(e -> controller.refresh());
        viewMenu.add(refreshItem);

        menuBar.add(viewMenu);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(helpMenu);

        return menuBar;
    }

    private void setupKeyboardShortcuts() {
        // F5 for refresh (already in menu accelerator, but adding global binding)
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refresh");
        getRootPane().getActionMap().put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.refresh();
            }
        });
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "Test Recorder Application\n\n" +
            "A BDD-tested test management system.\n\n" +
            "Version 1.0",
            "About Test Recorder",
            JOptionPane.INFORMATION_MESSAGE);
    }

    public void loadTests(List<Test> tests) {
        tablePanel.loadTests(tests);
        updateStatusBar();
    }

    public void updateButtonStates(boolean hasSelection) {
        toolbarPanel.updateButtonStates(hasSelection);
    }

    public void updateStatusBar() {
        statusBar.updateTestCount(tablePanel.getRowCount());
        statusBar.updateSelectedTest(controller.getSelectedTest());
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showAndRefresh() {
        setVisible(true);
        controller.refresh();
    }
}
