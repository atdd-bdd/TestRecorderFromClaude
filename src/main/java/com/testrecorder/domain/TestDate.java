package com.testrecorder.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class TestDate implements Comparable<TestDate> {
    private static final String DATE_FORMAT = "MMM d, yyyy, h:mm:ss a";
    private static final String NEVER_STRING = "Never";
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);
    
    private final Date date;
    private final boolean isNever;

    private TestDate(Date date, boolean isNever) {
        this.date = date;
        this.isNever = isNever;
    }

    public static TestDate never() {
        return new TestDate(new Date(0), true); // Jan 1, 1970
    }

    public static TestDate of(Date date) {
        return new TestDate(date, false);
    }

    public static TestDate parse(String value) {
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase(NEVER_STRING)) {
            return never();
        }

        try {
            Date parsed = DATE_FORMATTER.parse(value.trim());
            return of(parsed);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + value + ". Expected format: " + DATE_FORMAT, e);
        }
    }

    public boolean isNever() {
        return isNever;
    }

    public Date getDate() {
        return date;
    }

    public boolean isAfter(TestDate other) {
        if (this.isNever) return false;
        if (other.isNever) return true;
        return this.date.after(other.date);
    }

    public boolean isBefore(TestDate other) {
        if (this.isNever) return !other.isNever;
        if (other.isNever) return false;
        return this.date.before(other.date);
    }

    @Override
    public int compareTo(TestDate other) {
        if (this.isNever && other.isNever) return 0;
        if (this.isNever) return -1;
        if (other.isNever) return 1;
        return this.date.compareTo(other.date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestDate testDate = (TestDate) o;
        if (isNever && testDate.isNever) return true;
        return isNever == testDate.isNever && Objects.equals(date, testDate.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, isNever);
    }

    @Override
    public String toString() {
        if (isNever) {
            return NEVER_STRING;
        }
        return DATE_FORMATTER.format(date);
    }

    public static boolean isValid(String value) {
        try {
            parse(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
