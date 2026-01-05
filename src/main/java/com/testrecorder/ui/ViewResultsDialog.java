package com.testrecorder.ui;

import com.testrecorder.domain.Test;
import com.testrecorder.domain.TestRun;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewResultsDialog extends JDialog {
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JTextArea commentsArea;
    private JButton closeButton;
    private List<TestRun> testRuns;

    public ViewResultsDialog(Frame owner, Test test, List<TestRun> testRuns) {
        super(owner, "Test Run History: " + test.getIssueId() + "/" + test.getSubIssueId(), true);
        this.testRuns = testRuns;
        initializeComponents();
        layoutComponents(test);
        loadData();
        setupListeners();
        setSize(600, 400);
        setLocationRelativeTo(owner);
    }

    private void initializeComponents() {
        String[] columnNames = {"Date/Time", "Result", "Runner"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultsTable = new JTable(tableModel);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        commentsArea = new JTextArea(4, 40);
        commentsArea.setEditable(false);
        commentsArea.setLineWrap(true);
        commentsArea.setWrapStyleWord(true);

        closeButton = new JButton("Close");
    }

    private void layoutComponents(Test test) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Test info
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("Test: " + test.getName()));
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Results table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(new TitledBorder("Test Runs"));
        tablePanel.add(new JScrollPane(resultsTable), BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Bottom panel with comments and close button
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

        JPanel commentsPanel = new JPanel(new BorderLayout());
        commentsPanel.setBorder(new TitledBorder("Comments (select a run above)"));
        commentsPanel.add(new JScrollPane(commentsArea), BorderLayout.CENTER);
        bottomPanel.add(commentsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (TestRun run : testRuns) {
            Object[] row = {
                run.getDateTime().toString(),
                run.getResult().toString(),
                run.getRunner()
            };
            tableModel.addRow(row);
        }

        if (testRuns.isEmpty()) {
            commentsArea.setText("No test runs recorded for this test.");
        }
    }

    private void setupListeners() {
        closeButton.addActionListener(e -> dispose());

        resultsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = resultsTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < testRuns.size()) {
                    TestRun selectedRun = testRuns.get(selectedRow);
                    String comments = selectedRun.getComments();
                    commentsArea.setText(comments != null && !comments.isEmpty() ? comments : "(No comments)");
                }
            }
        });
    }

    public void showDialog() {
        setVisible(true);
    }
}
