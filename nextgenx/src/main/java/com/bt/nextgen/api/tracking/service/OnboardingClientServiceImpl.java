package com.bt.nextgen.api.tracking.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.draftaccount.LoggingConstants;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.tracking.model.OnboardingClientStatusDto;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.bt.nextgen.core.repository.OnboardingPartyRepository;
import com.bt.nextgen.core.repository.OnboardingPartyStatus;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional(value = "springJpaTransactionManager")
public class OnboardingClientServiceImpl implements OnboardingClientService {
    @Autowired
    private ClientApplicationRepository clientApplicationsRepository;
    @Autowired
    private OnboardingPartyRepository onboardingPartyRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(OnboardingClientServiceImpl.class);

    private boolean isProvisionOnlineAccessComplete(ClientApplication application) {
        final OnBoardingApplication onboardingApplication = application.getOnboardingApplication();
        if (onboardingApplication != null && onboardingApplication.getAvaloqOrderId() != null) {
            final List<OnboardingParty> onboardingPartiesByApplicationIds = onboardingPartyRepository.findOnboardingPartiesByApplicationIds(Arrays.asList(onboardingApplication.getKey().getId()));
            final OnboardingParty onboardingParty = Lambda.selectFirst(onboardingPartiesByApplicationIds, new LambdaMatcher<OnboardingParty>() {

                @Override
                protected boolean matchesSafely(OnboardingParty onboardingParty) {
                    return OnboardingPartyStatus.NotificationSentToExistingOnlineUser.equals(onboardingParty.getStatus()) ||
                            OnboardingPartyStatus.ExistingPanoramaOnlineUser.equals(onboardingParty.getStatus());
                }
            });
            if (onboardingParty != null) {
                return true;
            }

        }

        return false;
    }

    @Override
    public OnboardingClientStatusDto find(ClientApplicationKey key, ServiceErrors serviceErrors) {
        LOGGER.info(LoggingConstants.ONBOARDING + " ClientApplication Key " + key);
        Long applicationId = key.getClientApplicationKey();
        ClientApplication clientApplication = clientApplicationsRepository.find(applicationId);
        final boolean provisionOnlineAccessComplete = isProvisionOnlineAccessComplete(clientApplication);
        LOGGER.info(LoggingConstants.ONBOARDING + " OnboardingClientStatus " + provisionOnlineAccessComplete);
        OnboardingClientStatusDto onboardingClientStatusDto = new OnboardingClientStatusDto(provisionOnlineAccessComplete);
        onboardingClientStatusDto.setKey(key);
        return onboardingClientStatusDto;
    }
}
