package com.testrecorder.service;

import com.testrecorder.domain.TestDate;

public interface DateTimeProvider {
    TestDate getCurrentDateTime();
}
