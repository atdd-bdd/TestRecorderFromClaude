package com.testrecorder.ui;

import com.testrecorder.domain.Test;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

public class StatusBar extends JPanel {
    private JLabel testCountLabel;
    private JLabel selectedTestLabel;
    private JLabel databaseStatusLabel;

    public StatusBar() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        initializeComponents();
    }

    private void initializeComponents() {
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));

        testCountLabel = new JLabel("Tests: 0");
        leftPanel.add(testCountLabel);

        leftPanel.add(new JSeparator(SwingConstants.VERTICAL));

        selectedTestLabel = new JLabel("Selected: None");
        leftPanel.add(selectedTestLabel);

        add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 2));
        databaseStatusLabel = new JLabel("Database: Connected");
        databaseStatusLabel.setForeground(new Color(0, 128, 0));
        rightPanel.add(databaseStatusLabel);

        add(rightPanel, BorderLayout.EAST);
    }

    public void updateTestCount(int count) {
        testCountLabel.setText("Tests: " + count);
    }

    public void updateSelectedTest(Test test) {
        if (test != null) {
            selectedTestLabel.setText("Selected: " + test.getIssueId() + "/" + test.getSubIssueId() + " - " + test.getName());
        } else {
            selectedTestLabel.setText("Selected: None");
        }
    }

    public void setDatabaseConnected(boolean connected) {
        if (connected) {
            databaseStatusLabel.setText("Database: Connected");
            databaseStatusLabel.setForeground(new Color(0, 128, 0));
        } else {
            databaseStatusLabel.setText("Database: Disconnected (In-Memory)");
            databaseStatusLabel.setForeground(Color.RED);
        }
    }
}
