package com.bt.nextgen.api.corporateaction.v1.model;

import com.bt.nextgen.core.domain.key.AbstractKey;

/**
 * This is the key object to retrieve list of corporate action
 */
public class CorporateActionListDtoKey extends AbstractKey {
    private String startDate;
    private String endDate;
    private String corporateActionGroup;
    private String accountId;
    private String ipsId;

    public CorporateActionListDtoKey(String startDate, String endDate, String corporateActionGroup, String accountId, String ipsId) {
        this.startDate = startDate != null ? startDate : "";
        this.endDate = endDate != null ? endDate : "";
        this.corporateActionGroup = corporateActionGroup != null ? corporateActionGroup : "";
        this.accountId = accountId != null ? accountId : "";
        this.ipsId = ipsId != null ? ipsId : "";
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCorporateActionGroup() {
        return corporateActionGroup;
    }

    public void setCorporateActionGroup(String corporateActionGroup) {
        this.corporateActionGroup = corporateActionGroup;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getIpsId() {
        return ipsId;
    }

    public void setIpsId(String ipsId) {
        this.ipsId = ipsId;
    }
}
