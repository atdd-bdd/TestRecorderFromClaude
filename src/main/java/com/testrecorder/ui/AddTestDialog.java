package com.testrecorder.ui;

import com.testrecorder.domain.IssueId;
import com.testrecorder.domain.SubIssueId;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddTestDialog extends JDialog {
    private JTextField issueIdField;
    private JTextField subIssueIdField;
    private JTextField nameField;
    private JTextField filePathField;
    private JButton browseButton;
    private JButton addButton;
    private JButton cancelButton;
    private boolean confirmed = false;

    public AddTestDialog(Frame owner) {
        super(owner, "Add New Test", true);
        initializeComponents();
        layoutComponents();
        setupListeners();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initializeComponents() {
        issueIdField = new JTextField(10);
        issueIdField.setToolTipText("5 alphanumeric characters");

        subIssueIdField = new JTextField(5);
        subIssueIdField.setToolTipText("3 alphanumeric characters");

        nameField = new JTextField(30);
        nameField.setToolTipText("Test name");

        filePathField = new JTextField(25);
        filePathField.setToolTipText("Path to test file (optional)");

        browseButton = new JButton("Browse...");

        addButton = new JButton("Add");
        cancelButton = new JButton("Cancel");
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Issue ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Issue ID:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(issueIdField, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("(5 chars)"), gbc);

        // SubIssue ID
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("SubIssue ID:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(subIssueIdField, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("(3 chars)"), gbc);

        // Name
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(nameField, gbc);

        // File Path
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("File Path:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(filePathField, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(browseButton, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);

        setContentPane(mainPanel);
    }

    private void setupListeners() {
        addButton.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());

        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Test File");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            // Start from current directory or file path if set
            String currentPath = filePathField.getText().trim();
            if (!currentPath.isEmpty()) {
                fileChooser.setCurrentDirectory(new java.io.File(currentPath).getParentFile());
            }

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                filePathField.setText(fileChooser.getSelectedFile().getPath());
            }
        });

        // Enter key in any field triggers Add
        Action addAction = new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                addButton.doClick();
            }
        };
        issueIdField.addActionListener(e -> subIssueIdField.requestFocus());
        subIssueIdField.addActionListener(e -> nameField.requestFocus());
        nameField.addActionListener(e -> filePathField.requestFocus());
        filePathField.addActionListener(e -> addButton.doClick());
    }

    private boolean validateInput() {
        // Validate Issue ID
        if (!IssueId.isValid(issueIdField.getText())) {
            showError("Issue ID must be exactly 5 alphanumeric characters");
            issueIdField.requestFocus();
            return false;
        }

        // Validate SubIssue ID
        if (!SubIssueId.isValid(subIssueIdField.getText())) {
            showError("SubIssue ID must be exactly 3 alphanumeric characters");
            subIssueIdField.requestFocus();
            return false;
        }

        // Validate Name
        if (nameField.getText().trim().isEmpty()) {
            showError("Name is required");
            nameField.requestFocus();
            return false;
        }

        return true;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean showDialog() {
        setVisible(true);
        return confirmed;
    }

    public String getIssueId() {
        return issueIdField.getText().trim();
    }

    public String getSubIssueId() {
        return subIssueIdField.getText().trim();
    }

    public String getName() {
        return nameField.getText().trim();
    }

    public String getFilePath() {
        return filePathField.getText().trim();
    }
}
