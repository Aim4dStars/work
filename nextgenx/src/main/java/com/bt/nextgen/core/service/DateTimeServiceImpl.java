package com.bt.nextgen.core.service;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Service
public class DateTimeServiceImpl implements DateTimeService {
    @Override
    public DateTime getCurrentDateTime() {
        return new DateTime();
    }
}
