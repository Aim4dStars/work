package com.bt.nextgen.api.profile.v1.service;

import java.util.Map;

import com.bt.nextgen.api.profile.v1.model.ProfileDetailsDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;

/**
 * Created by F030695 on 13/07/2016.
 */
public interface InvestorHomepageEvaluator {

    void setInvestorHomepageDetails(ProfileDetailsDto profile, Map<AccountKey, WrapAccount> accountMap, ServiceErrors serviceErrors);
}

