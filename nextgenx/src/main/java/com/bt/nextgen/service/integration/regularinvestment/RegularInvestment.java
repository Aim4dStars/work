package com.bt.nextgen.service.integration.regularinvestment;

import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;
import com.bt.nextgen.service.integration.order.OrderGroup;

public interface RegularInvestment extends OrderGroup {

    public RIPSchedule getRIPSchedule();

    public RecurringDepositDetails getDirectDebitDetails();

    public void setDirectDebitDetails(RecurringDepositDetails directDebit);

    public RIPStatus getRIPStatus();

    public String getFundSourceId();

}
