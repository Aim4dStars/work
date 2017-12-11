package com.bt.nextgen.api.tracking.service;

import java.util.Collection;

import com.bt.nextgen.core.repository.OnboardingStatusInterface;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;

public interface InvestorStatusServiceForTechnicalSupport
{

	OnboardingParty getInvestorOnboardingPartyDetails(String gcmId);

	OnboardingStatusInterface getOnboardingStatusAndFailureMsg(String gcmId);

	void updatePartyStatusWhenResendRegistrationCodeSuccess(OnboardingParty onboardingParty);
	
	ClientApplication getClientApplicationDetailsForOnboardingApplicationId(OnboardingApplicationKey key,
		Collection <BrokerIdentifier> adviserIds);
}