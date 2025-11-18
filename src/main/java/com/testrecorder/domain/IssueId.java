package com.testrecorder.domain;

public class IssueId {
    
    public static String validate(String value) {
        if (value == null) {
            throw new IllegalArgumentException("IssueID cannot be null");
        }
        
        String trimmed = value.trim();
        
        // Check for spaces
        if (trimmed.contains(" ")) {
            throw new IllegalArgumentException("IssueID cannot contain spaces");
        }
        
        // Check length (must be 5 characters)
        if (trimmed.length() != 5) {
            throw new IllegalArgumentException("IssueID must be exactly 5 characters");
        }
        
        // Must be alphanumeric
        if (!trimmed.matches("[A-Za-z0-9]{5}")) {
            throw new IllegalArgumentException("IssueID must contain only letters and digits");
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
