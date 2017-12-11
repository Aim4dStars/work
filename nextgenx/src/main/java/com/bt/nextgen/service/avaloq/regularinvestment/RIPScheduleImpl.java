package com.bt.nextgen.service.avaloq.regularinvestment;

import org.joda.time.DateTime;

import com.bt.nextgen.service.integration.regularinvestment.RIPRecurringFrequency;
import com.bt.nextgen.service.integration.regularinvestment.RIPSchedule;

public class RIPScheduleImpl implements RIPSchedule {

    private DateTime firstExecDate;
    private DateTime lastExecDate;
    private DateTime nextExecDate;
    private RIPRecurringFrequency recurringFrequency;

    public RIPScheduleImpl(DateTime firstExecDate, DateTime lastExecDate, RIPRecurringFrequency recurringFrequency) {
        super();
        this.firstExecDate = firstExecDate;
        this.lastExecDate = lastExecDate;
        this.recurringFrequency = recurringFrequency;
    }

    public RIPScheduleImpl(DateTime firstExecDate, DateTime lastExecDate, DateTime nextExecDate,
            RIPRecurringFrequency recurringFrequency) {
        this(firstExecDate, lastExecDate, recurringFrequency);
        this.nextExecDate = nextExecDate;
    }

    @Override
    public DateTime getFirstExecDate() {
        return firstExecDate;
    }

    @Override
    public DateTime getLastExecDate() {
        return lastExecDate;
    }

    public DateTime getNextExecDate() {
        return nextExecDate;
    }

    @Override
    public RIPRecurringFrequency getRecurringFrequency() {
        return recurringFrequency;
    }

}