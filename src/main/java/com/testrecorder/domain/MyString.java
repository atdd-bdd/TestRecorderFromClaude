package com.testrecorder.domain;

public class MyString {
    
    public static String validate(String value) {
        if (value == null) {
            return "";
        }
        
        // Remove invalid characters (parentheses, dollar sign, question mark)
        return value.replaceAll("[()$?]", "");
    }
}
