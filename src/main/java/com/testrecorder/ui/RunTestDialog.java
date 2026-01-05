package com.testrecorder.ui;

import com.testrecorder.domain.Test;
import com.testrecorder.domain.TestResult;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class RunTestDialog extends JDialog {
    private JRadioButton successRadio;
    private JRadioButton failureRadio;
    private JTextArea commentsArea;
    private JButton runButton;
    private JButton cancelButton;
    private boolean confirmed = false;

    public RunTestDialog(Frame owner, Test test) {
        super(owner, "Run Test: " + test.getIssueId() + "/" + test.getSubIssueId(), true);
        initializeComponents(test);
        layoutComponents(test);
        setupListeners();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initializeComponents(Test test) {
        successRadio = new JRadioButton("Success");
        failureRadio = new JRadioButton("Failure");
        ButtonGroup resultGroup = new ButtonGroup();
        resultGroup.add(successRadio);
        resultGroup.add(failureRadio);
        successRadio.setSelected(true);

        commentsArea = new JTextArea(4, 30);
        commentsArea.setLineWrap(true);
        commentsArea.setWrapStyleWord(true);

        runButton = new JButton("Run");
        cancelButton = new JButton("Cancel");
    }

    private void layoutComponents(Test test) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Test info panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBorder(new TitledBorder("Test Information"));
        infoPanel.add(new JLabel("Name: " + test.getName()));
        infoPanel.add(new JLabel("Current Status: " + test.getLastResult()));
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Center panel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        // Result selection
        JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        resultPanel.setBorder(new TitledBorder("Result"));
        resultPanel.add(successRadio);
        resultPanel.add(failureRadio);
        centerPanel.add(resultPanel, BorderLayout.NORTH);

        // Comments
        JPanel commentsPanel = new JPanel(new BorderLayout());
        commentsPanel.setBorder(new TitledBorder("Comments"));
        commentsPanel.add(new JScrollPane(commentsArea), BorderLayout.CENTER);
        centerPanel.add(commentsPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(runButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void setupListeners() {
        runButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());
    }

    public boolean showDialog() {
        setVisible(true);
        return confirmed;
    }

    public TestResult getResult() {
        return successRadio.isSelected() ? TestResult.SUCCESS : TestResult.FAILURE;
    }

    public String getComments() {
        return commentsArea.getText().trim();
    }
}
