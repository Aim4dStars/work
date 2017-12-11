package com.bt.nextgen.service.integration.regularinvestment;

import org.joda.time.DateTime;

public interface RIPSchedule {

    public DateTime getFirstExecDate();

    public DateTime getLastExecDate();

    public DateTime getNextExecDate();

    public RIPRecurringFrequency getRecurringFrequency();

}