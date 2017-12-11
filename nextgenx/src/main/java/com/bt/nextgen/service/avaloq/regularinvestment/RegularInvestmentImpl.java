package com.bt.nextgen.service.avaloq.regularinvestment;

import com.bt.nextgen.service.avaloq.order.OrderGroupImpl;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;
import com.bt.nextgen.service.integration.order.OrderGroup;
import com.bt.nextgen.service.integration.regularinvestment.RIPSchedule;
import com.bt.nextgen.service.integration.regularinvestment.RIPStatus;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestment;

public class RegularInvestmentImpl extends OrderGroupImpl implements RegularInvestment {

    private RIPSchedule ripSchedule;
    private RecurringDepositDetails directDebitDetails;
    private RIPStatus ripStatus;
    private String firstNotification;
    private String fundSourceId;

    public RegularInvestmentImpl() {
    }

    public RegularInvestmentImpl(OrderGroup orderGroup) {
        super(orderGroup.getAccountKey(), orderGroup.getOrderGroupId(), orderGroup.getOwner(), orderGroup.getLastUpdateDate(),
                orderGroup.getTransactionSeq(), orderGroup.getOrders(), orderGroup.getWarnings(), orderGroup.getReference());
        this.firstNotification = orderGroup.getFirstNotification();
    }

    @Override
    public RecurringDepositDetails getDirectDebitDetails() {
        return directDebitDetails;
    }

    public void setDirectDebitDetails(RecurringDepositDetails directDebitDetails) {
        this.directDebitDetails = directDebitDetails;
    }

    @Override
    public RIPSchedule getRIPSchedule() {
        return ripSchedule;
    }

    public void setRIPSchedule(RIPSchedule ripSchedule) {
        this.ripSchedule = ripSchedule;
    }

    @Override
    public RIPStatus getRIPStatus() {
        return ripStatus;
    }

    public void setRIPStatus(RIPStatus ripStatus) {
        this.ripStatus = ripStatus;
    }

    public String getFirstNotification() {
        return firstNotification;
    }

    public void setFirstNotification(String firstNotification) {
        this.firstNotification = firstNotification;
    }

    public String getFundSourceId() {
        return fundSourceId;
    }

    public void setFundSourceId(String fundSourceId) {
        this.fundSourceId = fundSourceId;
    }

}
