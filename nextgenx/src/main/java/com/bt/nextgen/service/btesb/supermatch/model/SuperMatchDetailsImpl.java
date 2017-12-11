package com.bt.nextgen.service.btesb.supermatch.model;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.integration.supermatch.Member;
import com.bt.nextgen.service.integration.supermatch.StatusSummary;
import com.bt.nextgen.service.integration.supermatch.SuperFundAccount;
import com.bt.nextgen.service.integration.supermatch.SuperMatchDetails;

import java.util.List;

/**
 * Super match details
 */
@ServiceBean(xpath = "SuperannuationMatch", type = ServiceBeanType.CONCRETE)
public class SuperMatchDetailsImpl implements SuperMatchDetails {

    @ServiceElement(xpath = "StatusSummary", type = StatusSummaryImpl.class)
    private StatusSummary statusSummary;

    @ServiceElementList(xpath = "ATOMonies/*", type = AtoMoney.class)
    private List<AtoMoney> atoMonies;

    @ServiceElementList(xpath = "FundDetails/SuperFundAccount", type = SuperFundAccountImpl.class)
    private List<SuperFundAccount> superFundAccounts;

    @Override
    public StatusSummary getStatusSummary() {
        return statusSummary;
    }

    @Override
    public List<AtoMoney> getAtoMonies() {
        return atoMonies;
    }

    @Override
    public List<SuperFundAccount> getSuperFundAccounts() {
        return superFundAccounts;
    }
}
