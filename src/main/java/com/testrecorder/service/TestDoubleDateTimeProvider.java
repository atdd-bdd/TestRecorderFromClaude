package com.testrecorder.service;

import com.testrecorder.domain.TestDate;

public class TestDoubleDateTimeProvider implements DateTimeProvider {
    private TestDate fixedDateTime;

    public TestDoubleDateTimeProvider(TestDate fixedDateTime) {
        this.fixedDateTime = fixedDateTime;
    }

    public void setCurrentDateTime(TestDate dateTime) {
        this.fixedDateTime = dateTime;
    }

    @Override
    public TestDate getCurrentDateTime() {
        return fixedDateTime;
    }
}
