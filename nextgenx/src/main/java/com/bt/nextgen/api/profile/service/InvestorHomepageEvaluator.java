package com.bt.nextgen.api.profile.service;

import java.util.Map;

import com.bt.nextgen.api.profile.model.ProfileDetailsDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;

/**
 * Use package com.bt.nextgen.api.profile.v1.service.InvestorHomepageEvaluator
 */
@Deprecated
public interface InvestorHomepageEvaluator {

    void setInvestorHomepageDetails(ProfileDetailsDto profile, Map<AccountKey, WrapAccount> accountMap, ServiceErrors serviceErrors);
}
