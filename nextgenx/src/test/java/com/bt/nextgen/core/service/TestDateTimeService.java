package com.bt.nextgen.core.service;

import org.joda.time.DateTime;

public class TestDateTimeService implements DateTimeService {

    private DateTime now;

    @Override
    public DateTime getCurrentDateTime() {
        return now != null ? now : DateTime.now();
    }

    public void freezeTime(DateTime dateTime) {
        now = dateTime;
    }

    public void freezeTime() {
        now = DateTime.now();
    }

    public void unfreezeTime() {
        now = null;
    }
}