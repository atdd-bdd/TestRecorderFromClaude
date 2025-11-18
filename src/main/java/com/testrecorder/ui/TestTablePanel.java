package com.testrecorder.ui;

import com.testrecorder.domain.Test;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class TestTablePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    public TestTablePanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        initializeTable();
    }

    private void initializeTable() {
        String[] columnNames = {"Issue ID", "SubIssue ID", "Name", "Runner", "Last Result", 
                                "Date Last Run", "Date Previous Result", "File Path", "Comments", "Test Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
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
}
