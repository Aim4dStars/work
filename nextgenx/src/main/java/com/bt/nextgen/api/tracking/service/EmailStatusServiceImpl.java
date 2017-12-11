package com.bt.nextgen.api.tracking.service;

import com.bt.nextgen.api.draftaccount.model.ApplicationClientStatus;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.NotFoundException;
import com.bt.nextgen.core.repository.OnboardingCommunication;
import com.bt.nextgen.core.repository.OnboardingCommunicationRepository;
import com.bt.nextgen.core.repository.OnboardingCommunicationStatus;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class EmailStatusServiceImpl implements EmailStatusService {

    @Autowired
    private OnboardingCommunicationRepository communicationRepository;

    private static final Logger logger = LoggerFactory.getLogger(EmailStatusServiceImpl.class);

    @Override
    public ApplicationClientStatus getApplicationClientStatus(Long applicationId, AssociatedPerson person, String gcmId) {

        try {
            List<OnboardingCommunication> communications = getCommunications(applicationId, gcmId);
            if (communications.isEmpty()) {
                return ApplicationClientStatus.PROCESSING;
            }
            return hasSendEmailFailed(communications) && !person.isRegisteredOnline() ? ApplicationClientStatus.FAILED_EMAIL :
                    ApplicationClientStatus.getStatus(person.isHasToAcceptTnC(), person.isRegisteredOnline(), person.isHasApprovedTnC());
        } catch (NotFoundException exception) {
            return ApplicationClientStatus.TECHNICAL_ERROR;
        }
    }

    private List<OnboardingCommunication> getCommunications(Long applicationId, String gcmId) {
        try {
            return communicationRepository.findByApplicationIdAndGCMId(applicationId, gcmId);
        } catch (Exception exception) {
            logger.error("Error while fetching investor's status from OnboardingCommunication for the onboardingApplication " + applicationId);
            throw new NotFoundException(ApiVersion.CURRENT_VERSION, "Error while fetching investor's status from OnboardingCommunication for the onboardingApplication " + applicationId);
        }
    }

    private boolean hasSendEmailFailed(List<OnboardingCommunication> communications) {
        final List<String> emailFailureStatuses = Arrays.asList(OnboardingCommunicationStatus.FORMAT_ERROR, OnboardingCommunicationStatus.ERROR, OnboardingCommunicationStatus.HARD_BOUNCE);
        boolean hasEmailSent = Iterables.any(communications, new Predicate<OnboardingCommunication>() {
            @Override
            public boolean apply(OnboardingCommunication onboardingCommunication) {
                return !emailFailureStatuses.contains(onboardingCommunication.getStatus());
            }
        });
        return !hasEmailSent;
    }

	@Override
	public boolean isCommunicationSuccessfulForTheParty(String gcmId)
	{
		try
		{
			List <OnboardingCommunication> communications = communicationRepository.findCommunicationsByGcmId(gcmId);
			return !hasSendEmailFailed(communications);
		}
		catch (Exception exception)
		{
			logger.error("Getitng exception {} while fetching investor's status from OnboardingCommunication for  gcmID : {}",
				exception,
				gcmId);
			throw new NotFoundException(ApiVersion.CURRENT_VERSION,
				"Error while fetching investor's status from OnboardingCommunication for the gcmId " + gcmId);
		}
	}
}
