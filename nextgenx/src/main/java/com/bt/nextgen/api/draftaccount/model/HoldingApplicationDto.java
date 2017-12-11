package com.bt.nextgen.api.draftaccount.model;

import java.util.List;

import com.bt.nextgen.service.avaloq.accountactivation.ApprovalType;
import org.joda.time.DateTime;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.account.AccountKey;

public class HoldingApplicationDto extends BaseDto implements KeyedDto<AccountKey> {

    private final AccountKey accountKey;
    private final DateTime appliedOn;
    private final String accountName;
    private final List<HoldingApplicationClientDto> clients;
    private final String orderType;
    private final Adviser adviser;
    private ApprovalType approvalType;

    public HoldingApplicationDto(AccountKey accountKey, DateTime appliedOn, String accountName, String orderType,
                                 List<HoldingApplicationClientDto> clients, Adviser adviser, ApprovalType approvalType) {
        this.accountKey = accountKey;
        this.appliedOn = appliedOn;
        this.accountName = accountName;
        this.clients = clients;
        this.orderType = orderType;
        this.adviser = adviser;
        this.approvalType = approvalType;
    }

    public String getOrderType() {
        return orderType;
    }

    public List<HoldingApplicationClientDto> getClients() {
        return clients;
    }

    public DateTime getAppliedOn() {
        return appliedOn;
    }

    public String getAccountName() {
        return accountName;
    }

    @Override
    public AccountKey getKey() {
        return accountKey;
    }

    public Adviser getAdviser() {
        return adviser;
    }

    public ApprovalType getApprovalType(){
        return approvalType;
    }
}
