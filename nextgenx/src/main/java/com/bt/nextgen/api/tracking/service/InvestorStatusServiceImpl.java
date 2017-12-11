package com.bt.nextgen.api.tracking.service;

import com.bt.nextgen.api.draftaccount.model.ApplicationClientStatus;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.NotFoundException;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.bt.nextgen.core.repository.OnboardingPartyStatus;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InvestorStatusServiceImpl implements InvestorStatusService {

    @Autowired
    private EmailStatusService emailStatusService;

    private static final Logger logger = LoggerFactory.getLogger(InvestorStatusServiceImpl.class);

    @Override
    public ApplicationClientStatus getInvestorStatus(Long applicationId, AssociatedPerson person,
            OnboardingParty onboardingParty) {
        try {
            if (onboardingParty == null) {
                throw new NotFoundException(ApiVersion.CURRENT_VERSION);
            }

            if (onboardingParty.getStatus() == null) {
                return ApplicationClientStatus.PROCESSING;
            }
            OnboardingPartyStatus onboardingPartyStatus = onboardingParty.getStatus();
            switch (onboardingPartyStatus) {
                case ExistingPanoramaOnlineUser:
                case NotificationSentToExistingOnlineUser:
                    return ApplicationClientStatus.getStatus(person.isHasToAcceptTnC(), person.isRegisteredOnline(),
                            person.isHasApprovedTnC());
                case NotificationSent:
                    return emailStatusService.getApplicationClientStatus(applicationId, person, onboardingParty.getGcmPan());
                default:
                    return ApplicationClientStatus.PROCESSING;
            }
        } catch (Exception exception) {
            logger.error("Error while fetching investor's status from OnboardingCommunication for the onboardingApplication "
                    + applicationId);
            throw new NotFoundException(ApiVersion.CURRENT_VERSION,
                    "Error while fetching investor's status from OnboardingCommunication for the onboardingApplication "
                            + applicationId);
        }
    }
}
