package com.testrecorder.service;

import com.testrecorder.domain.TestDate;
import java.util.Date;

public class SystemDateTimeProvider implements DateTimeProvider {
    @Override
    public TestDate getCurrentDateTime() {
        return TestDate.of(new Date());
    }
}
