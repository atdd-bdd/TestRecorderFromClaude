package com.testrecorder.domain;

public class SubIssueId {
    
    public static String validate(String value) {
        if (value == null) {
            throw new IllegalArgumentException("SubIssueID cannot be null");
        }
        
        String trimmed = value.trim();
        
        // Check for spaces
        if (trimmed.contains(" ")) {
            throw new IllegalArgumentException("SubIssueID cannot contain spaces");
        }
        
        // Check length (must be 3 characters)
        if (trimmed.length() != 3) {
            throw new IllegalArgumentException("SubIssueID must be exactly 3 characters");
        }
        
        // Must be alphanumeric
        if (!trimmed.matches("[A-Za-z0-9]{3}")) {
            throw new IllegalArgumentException("SubIssueID must contain only letters and digits");
        }
        
        return trimmed;
    }
    
    public static boolean isValid(String value) {
        try {
            validate(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
