package com.bt.nextgen.api.corporateaction.v1.model;

import java.util.List;

import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;

public class CorporateActionContext {
    private CorporateActionDetails corporateActionDetails;
    private List<CorporateActionAccount> corporateActionAccountList;
    private boolean investmentManager;
    private boolean dealerGroup;
    private String brokerPositionId;
    private String ipsId;
    private String accountId;

    public CorporateActionDetails getCorporateActionDetails() {
        return corporateActionDetails;
    }

    public void setCorporateActionDetails(CorporateActionDetails corporateActionDetails) {
        this.corporateActionDetails = corporateActionDetails;
    }

    public List<CorporateActionAccount> getCorporateActionAccountList() {
        return corporateActionAccountList;
    }

    public void setCorporateActionAccountList(List<CorporateActionAccount> corporateActionAccountList) {
        this.corporateActionAccountList = corporateActionAccountList;
    }

    public boolean isInvestmentManager() {
        return investmentManager;
    }

    public void setInvestmentManager(boolean investmentManager) {
        this.investmentManager = investmentManager;
    }

    public boolean isDealerGroup() {
        return dealerGroup;
    }

    public void setDealerGroup(boolean dealerGroup) {
        this.dealerGroup = dealerGroup;
    }

    public String getBrokerPositionId() {
        return brokerPositionId;
    }

    public void setBrokerPositionId(String brokerPositionId) {
        this.brokerPositionId = brokerPositionId;
    }

    public String getIpsId() {
        return ipsId;
    }

    public void setIpsId(String ipsId) {
        this.ipsId = ipsId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public boolean isDealerGroupOrInvestmentManager() {
        return dealerGroup || investmentManager;
    }
}
