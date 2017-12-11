package com.bt.nextgen.draftaccount.repository;

import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public class PermittedClientApplicationRepositoryImpl implements PermittedClientApplicationRepository {

    @Autowired
    ClientApplicationRepository clientApplicationRepository;

    @Autowired
    UserProfileService userProfileService;

    @Autowired
    BrokerIntegrationService brokerService;

    @Override
    public List<ClientApplication> findNonActiveApplicationsBetweenDates(Date from, Date to) {
        return clientApplicationRepository.findNonActiveApplicationsBetweenDates(from, to, getAdvisersForUser());
    }

    @Override
    public ClientApplication find(Long id) {
        return clientApplicationRepository.find(id, getAdvisersForUser());
    }

    @Override
    public Long save(ClientApplication clientApplication) {
        return clientApplicationRepository.save(clientApplication);
    }

    @Override
    public ClientApplication findByOnboardingApplicationKey(OnboardingApplicationKey key) {
        return clientApplicationRepository.findByOnboardingApplicationKey(key, getAdvisersForUser());
    }

    @Override
    public ClientApplication findByOnboardingApplicationKeyWithoutPermissionCheck(OnboardingApplicationKey key) {
        return clientApplicationRepository.findByOnboardingApplicationKey(key);
    }

    @Override
    public List<ClientApplication> findCertainNumberOfLatestDraftAccounts(Integer count) {
        return clientApplicationRepository.findCertainNumberOfMostRecentDraftAccounts(count, getAdvisersForUser());
    }

    @Override
    public Long getNumberOfDraftAccounts() {
        return clientApplicationRepository.getNumberOfDraftAccounts(getAdvisersForUser());
    }

    private Collection<BrokerIdentifier> getAdvisersForUser() {
        UserInformation activeProfile = userProfileService.getActiveProfile();
        return brokerService.getAdvisersForUser(activeProfile, new FailFastErrorsImpl());
    }
    
    @Override
     public ClientApplication findByClientApplicationId(Long id){
    	 return clientApplicationRepository.find(id);
     }
}
