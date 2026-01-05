package com.testrecorder.ui;

import com.testrecorder.domain.Test;
import com.testrecorder.domain.TestStatus;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ChangeStatusDialog extends JDialog {
    private JComboBox<TestStatus> statusComboBox;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean confirmed = false;

    public ChangeStatusDialog(Frame owner, Test test) {
        super(owner, "Change Test Status", true);
        initializeComponents(test);
        layoutComponents(test);
        setupListeners();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initializeComponents(Test test) {
        statusComboBox = new JComboBox<>(TestStatus.values());
        statusComboBox.setSelectedItem(test.getTestStatus());

        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
    }

    private void layoutComponents(Test test) {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Test info
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(new JLabel("Test: " + test.getIssueId() + "/" + test.getSubIssueId() + " - " + test.getName()), gbc);

        // Current status
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Current Status: " + test.getTestStatus()), gbc);

        // New status
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("New Status:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(statusComboBox, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);

        setContentPane(mainPanel);
    }

    private void setupListeners() {
        saveButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());
    }

    public boolean showDialog() {
        setVisible(true);
        return confirmed;
    }

    public TestStatus getNewStatus() {
        return (TestStatus) statusComboBox.getSelectedItem();
    }
}
