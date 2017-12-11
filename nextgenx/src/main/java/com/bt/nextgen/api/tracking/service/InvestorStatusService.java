package com.bt.nextgen.api.tracking.service;

import com.bt.nextgen.api.draftaccount.model.ApplicationClientStatus;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;

public interface InvestorStatusService {

    ApplicationClientStatus getInvestorStatus(Long applicationId, AssociatedPerson associatedPerson, OnboardingParty onboardingParty);

}
