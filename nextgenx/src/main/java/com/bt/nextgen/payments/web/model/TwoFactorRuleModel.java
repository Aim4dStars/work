package com.bt.nextgen.payments.web.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by l069679 on 23/11/2016.
 */
public class TwoFactorRuleModel implements Serializable {

    private static final long serialVersionUID = -5196841093583422938L;

    private Map<TwoFactorAccountVerificationKey, AccountVerificationStatus> accountStatusMap = new HashMap<>();

    public Map<TwoFactorAccountVerificationKey, AccountVerificationStatus> getAccountStatusMap() {
        return accountStatusMap;
    }

    public void addVerificationStatus(TwoFactorAccountVerificationKey accountVerificationKey, AccountVerificationStatus verificationStatus) {
        accountStatusMap.put(accountVerificationKey, verificationStatus);
    }
}
