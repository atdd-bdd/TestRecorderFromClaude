package com.testrecorder.domain;

public class Name {
    
    public static String validate(String value) {
        if (value == null) {
            return "";
        }
        
        // Remove invalid characters (keep only alphanumerics and spaces)
        return value.replaceAll("[^a-zA-Z0-9 ]", "");
    }
}
