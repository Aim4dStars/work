package com.bt.nextgen.core.security.profile;

import com.btfin.panorama.service.integration.broker.Broker;

/**
 * This should only be used by the profile details service API.
 */
public interface InvestorProfileService extends com.btfin.panorama.core.security.profile.UserProfileService
{
    Broker getAdviserForLoggedInInvestor();
}
