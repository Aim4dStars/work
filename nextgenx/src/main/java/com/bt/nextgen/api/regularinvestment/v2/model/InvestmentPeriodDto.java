package com.bt.nextgen.api.regularinvestment.v2.model;

import org.joda.time.DateTime;

import com.bt.nextgen.service.integration.regularinvestment.RIPSchedule;

public class InvestmentPeriodDto {

    /**
     * Date when the investment order will be first executed..
     */
    private DateTime investmentStartDate;

    /**
     * The last scheduled transaction date for this regular investment. This could be different from the last deposit/withdrawal
     * from the specified Direct Debit account.
     */
    private DateTime investmentEndDate;

    /**
     * Frequency of the investment
     */
    private String frequency;

    private DateTime nextDueDate;

    public InvestmentPeriodDto(RIPSchedule schedule) {
        if (schedule != null) {
            this.investmentStartDate = schedule.getFirstExecDate();
            this.investmentEndDate = schedule.getLastExecDate();
            this.nextDueDate = schedule.getNextExecDate();
            if (schedule.getRecurringFrequency() != null) {
                frequency = schedule.getRecurringFrequency().name();
            }
        }
    }

    public InvestmentPeriodDto(DateTime startDate, DateTime investmentEndDate, DateTime nextDueDate, String frequency) {

        this.investmentEndDate = investmentEndDate;
        this.investmentStartDate = startDate;
        this.nextDueDate = nextDueDate;
        this.frequency = frequency;

    }

    public DateTime getInvestmentStartDate() {
        return investmentStartDate;
    }

    public DateTime getInvestmentEndDate() {
        return investmentEndDate;
    }

    public String getFrequency() {
        return frequency;
    }

    public DateTime getNextDueDate() {
        return nextDueDate;
    }
}
