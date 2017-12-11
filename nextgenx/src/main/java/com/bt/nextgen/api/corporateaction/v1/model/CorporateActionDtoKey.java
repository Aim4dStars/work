package com.bt.nextgen.api.corporateaction.v1.model;

import com.bt.nextgen.core.domain.key.StringIdKey;

/**
 * This is the key object to retrieve specific corporate action.  It is passed to Avaloq as doc_id param.
 */
public class CorporateActionDtoKey extends StringIdKey {
    private String accountId;
    private String ipsId;
    private Boolean summaryOnly;

    public CorporateActionDtoKey(String id) {
        super(id);
    }

    public CorporateActionDtoKey(String id, String accountId, String ipsId, Boolean summaryOnly) {
        super(id);
        this.accountId = accountId;
        this.ipsId = ipsId == null ? "" : ipsId;
        this.summaryOnly = summaryOnly;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getIpsId() {
        return ipsId;
    }

    public Boolean getSummaryOnly() {
        return summaryOnly;
    }
}
