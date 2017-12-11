package com.bt.nextgen.api.tracking.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bt.nextgen.core.repository.OnboardingCommunication;
import com.bt.nextgen.core.repository.OnboardingCommunicationRepository;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.bt.nextgen.core.repository.OnboardingPartyDisplayStatus;
import com.bt.nextgen.core.repository.OnboardingPartyRepository;
import com.bt.nextgen.core.repository.OnboardingPartyStatus;
import com.bt.nextgen.core.repository.OnboardingStatusImpl;
import com.bt.nextgen.core.repository.OnboardingStatusInterface;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.service.DateTimeService;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationRepository;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;

@Service
@Transactional
public class InvestorStatusServiceForTechnicalSupportImpl implements InvestorStatusServiceForTechnicalSupport {
    @Autowired
    private OnboardingPartyRepository partyRepository;

    @Autowired
    private EmailStatusService emailStatusService;

    @Autowired
    private OnboardingCommunicationRepository onboardingCommunicationRepository;

    @Autowired
    private DateTimeService dateTimeService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ClientApplicationRepository clientApplicationRepository;

    private static final Logger logger = LoggerFactory.getLogger(InvestorStatusServiceForTechnicalSupportImpl.class);

    @Override
    public OnboardingParty getInvestorOnboardingPartyDetails(String gcmId) {
        try {
            List<OnboardingParty> onboardingParties = partyRepository
                    .findByGCMId(gcmId);
            if (onboardingParties != null && !onboardingParties.isEmpty()) {
                sortByLastModified(onboardingParties);

                return onboardingParties.get(0);
            }

        } catch (javax.persistence.NoResultException e) {
            logger.error("Couldn't find onboarding party details", e);
        }
        return null;
    }

    private void sortByLastModified(List<OnboardingParty> onboardingParties) {
        Collections.sort(onboardingParties,
                new Comparator<OnboardingParty>() {
                    @Override
                    public int compare(OnboardingParty o1,
                                       OnboardingParty o2) {
                        return compareLastModified(o1, o2);
                    }

                    private int compareLastModified(OnboardingParty o1, OnboardingParty o2) {
                        if (o1 != null && o2 != null) {
                            return o2.getLastModifiedDate().compareTo(o1.getLastModifiedDate());
                        } else {
                            return 0;
                        }
                    }
                });
    }

    /**
     * Method to set the onboardingStatus and failure reason for a new investor.
     * @see com.bt.nextgen.api.tracking.service.InvestorStatusServiceForTechnicalSupport#getOnboardingStatusAndFailureMsg(...)
     */
    public OnboardingStatusInterface getOnboardingStatusAndFailureMsg(final String gcmId) {
        OnboardingStatusInterface onboardingStatusInterface = new OnboardingStatusImpl();
        OnboardingParty onboardingParty = getInvestorOnboardingPartyDetails(gcmId);
        if (onboardingParty == null) {
            return onboardingStatusInterface;
        }

        OnboardingPartyStatus onboardingPartyStatus = onboardingParty.getStatus();
        OnboardingPartyDisplayStatus onboardingPartyDisplayStatus = OnboardingPartyDisplayStatus.convertFromOnboardingPartyStatus(onboardingPartyStatus);
        String failureMessage = onboardingParty.getFailureMessage();
        
        if (onboardingPartyStatus == OnboardingPartyStatus.ExistingPanoramaOnlineUser) {
            failureMessage = null;
        } else if (onboardingPartyStatus == OnboardingPartyStatus.NotificationSent) {
            if (emailStatusService.isCommunicationSuccessfulForTheParty(gcmId)) {
                onboardingPartyDisplayStatus = OnboardingPartyDisplayStatus.NOTIFICATION_SENT;
                failureMessage = null;
            } else {
                onboardingPartyDisplayStatus = OnboardingPartyDisplayStatus.FAILED_EMAIL;
                failureMessage = getStatusForFailedCommunication(gcmId); // yes we use the status as a failure message
            }
        }
        onboardingStatusInterface.setStatus(onboardingPartyDisplayStatus);
        if (failureMessage != null) {
            onboardingStatusInterface.setFailureMsg(failureMessage);
        }

        return onboardingStatusInterface;
    }

    private @Nonnull String getStatusForFailedCommunication(final String gcmId) {
        try {
            List<OnboardingCommunication> communications = onboardingCommunicationRepository.findCommunicationsByGcmId(gcmId);
            if (communications != null && communications.size() > 0) {
                Collections.sort(communications, new Comparator<OnboardingCommunication>() {
                    @Override
                    public int compare(OnboardingCommunication o1, OnboardingCommunication o2) {
                        try {
                            return o2.getLastModifiedDate().compareTo(o1.getLastModifiedDate());
                        } catch (Exception e) {
                            return 0;
                        }
                    }
                });
                String status = communications.get(0).getStatus();
                if (status != null) {
                    return status;
                }
            }

        } catch (Exception e) {
            logger.error("Getting exception : {} while fetching communication details for client : {}", e, gcmId);
        }

        return Constants.EMPTY_STRING;
    }

    @Override
    public void updatePartyStatusWhenResendRegistrationCodeSuccess(OnboardingParty onboardingParty) {
        try {
            onboardingParty.setStatus(OnboardingPartyStatus.NotificationSent);
            onboardingParty.setFailureMessage(Constants.EMPTY_STRING);
            onboardingParty.setLastModifiedDate(dateTimeService.getCurrentDateTime().toDate());
            onboardingParty.setLastModifiedId(userProfileService.getGcmId());
            partyRepository.update(onboardingParty);
        } catch (Exception e) {
            logger.error("Getting exception : {}  while updating party details for : {}", e, onboardingParty.getGcmPan());
        }
    }

    @Override
    public ClientApplication getClientApplicationDetailsForOnboardingApplicationId(OnboardingApplicationKey key,
                                                                                   Collection<BrokerIdentifier> adviserIds) {
        try {
            return clientApplicationRepository.findByOnboardingApplicationKey(key, adviserIds);
        } catch (javax.persistence.NoResultException e) {
            logger.error("Couldn't find client application details", e);
            return null;
        }
    }

}
