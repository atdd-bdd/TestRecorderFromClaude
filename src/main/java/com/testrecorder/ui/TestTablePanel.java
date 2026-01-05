package com.testrecorder.ui;

import com.testrecorder.domain.Test;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TestTablePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private TestRecorderController controller;

    public TestTablePanel() {
        setLayout(new BorderLayout());
        initializeTable();
    }

    public void setController(TestRecorderController controller) {
        this.controller = controller;
        setupSelectionListener();
        setupDoubleClickListener();
    }

    private void initializeTable() {
        String[] columnNames = {"Issue ID", "SubIssue ID", "Name", "Runner", "Last Result",
                                "Date Last Run", "Date Previous Result", "File Path", "Comments", "Test Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(70);  // Issue ID
        table.getColumnModel().getColumn(1).setPreferredWidth(70);  // SubIssue ID
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Name
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Runner
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Last Result
        table.getColumnModel().getColumn(5).setPreferredWidth(150); // Date Last Run
        table.getColumnModel().getColumn(6).setPreferredWidth(150); // Date Previous Result
        table.getColumnModel().getColumn(7).setPreferredWidth(150); // File Path
        table.getColumnModel().getColumn(8).setPreferredWidth(150); // Comments
        table.getColumnModel().getColumn(9).setPreferredWidth(80);  // Test Status

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupSelectionListener() {
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && controller != null) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0) {
                        int modelRow = table.convertRowIndexToModel(selectedRow);
                        String issueId = (String) tableModel.getValueAt(modelRow, 0);
                        String subIssueId = (String) tableModel.getValueAt(modelRow, 1);
                        controller.onTestSelected(issueId, subIssueId);
                    } else {
                        controller.onTestDeselected();
                    }
                }
            }
        });
    }

    private void setupDoubleClickListener() {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && controller != null && controller.hasSelection()) {
                    controller.runSelectedTest();
                }
            }
        });
    }

    public void loadTests(List<Test> tests) {
        tableModel.setRowCount(0); // Clear existing data

        for (Test test : tests) {
            Object[] row = {
                test.getIssueId(),
                test.getSubIssueId(),
                test.getName(),
                test.getRunner(),
                test.getLastResult().toString(),
                test.getDateLastRun().toString(),
                test.getDatePreviousResult().toString(),
                test.getFilePath(),
                test.getComments(),
                test.getTestStatus().toString()
            };
            tableModel.addRow(row);
        }
    }

    public void show() {
        JFrame frame = new JFrame("Test Recorder");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(this);
        frame.setSize(1200, 600);
        frame.setVisible(true);
    }

    public List<Test> getDisplayedTests() {
        // This would extract tests from the table - placeholder for testing
        return null;
    }

    public void clearSelection() {
        table.clearSelection();
    }

    public int getRowCount() {
        return tableModel.getRowCount();
    }
}
