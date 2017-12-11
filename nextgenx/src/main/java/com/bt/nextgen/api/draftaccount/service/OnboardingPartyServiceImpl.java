package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.bt.nextgen.core.repository.OnboardingPartyRepository;
import com.bt.nextgen.core.repository.OnboardingPartyStatus;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.service.DateTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OnboardingPartyServiceImpl implements OnboardingPartyService {

    @Autowired
    private OnboardingPartyRepository onboardingPartyRepository;

    @Autowired
    private DateTimeService dateTimeService;

    @Autowired
    private UserProfileService userProfileService;

    @Override
    public void createOnboardingPartyForExistingUsers(IClientApplicationForm clientApplicationForm, Long onboardingApplicationId) {
        for (IPersonDetailsForm personDetailsForm : clientApplicationForm.getExistingPersonDetails()) {
            OnboardingParty onboardingParty = new OnboardingParty(personDetailsForm.getCorrelationSequenceNumber(), onboardingApplicationId, personDetailsForm.getPanoramaNumber());
            onboardingParty.setStatus(OnboardingPartyStatus.ExistingPanoramaOnlineUser);
            onboardingParty.setLastModifiedDate(dateTimeService.getCurrentDateTime().toDate());
            onboardingParty.setLastModifiedId(userProfileService.getGcmId());
            onboardingPartyRepository.save(onboardingParty);
        }
    }
}
