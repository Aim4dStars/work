package com.bt.nextgen.api.draftaccount;

import static org.junit.Assert.fail;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationSubmitDto;
import com.bt.nextgen.api.draftaccount.service.ClientApplicationSubmitDtoService;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.bt.nextgen.core.repository.OnboardingPartyStatus;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.PermittedClientApplicationRepository;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;

/**
* Created by L069260 on 22/01/2015.
*/
@Component
public class SubmissionTransacter {

    private static Logger logger = LoggerFactory.getLogger(SubmissionTransacter.class);

    private static final int MAX_TRIES = 180;  //bumped to 120 so that submitting 10 directors won't time out
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ClientApplicationSubmitDtoService applications;

    @Autowired
    private PermittedClientApplicationRepository clientApplicationRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void waitForPartyStatus(ClientApplicationSubmitDto dto, List<String> status) throws Exception {

        int tries = 0;
        OnboardingPartyStatus lastPartyStatus = null;
        while (tries++ < MAX_TRIES){
            List<OnboardingParty> parties = getParties(dto);

            if (parties.isEmpty()) {
                logger.info("Waiting for party/ies to be created...");
            }

            for(OnboardingParty party : parties) {
                lastPartyStatus = party.getStatus();
                if (status.contains(lastPartyStatus)) {
                    logger.info("Success... Status = " + lastPartyStatus);
                    return;
                } else {
                    logger.info("Still trying... Status = " + lastPartyStatus);
                }
            }

            Thread.sleep(1000);
        }

        fail("Party status for application "+dto.getKey().getClientApplicationKey()+
                " did not resolve to "+status+" but instead was "+ lastPartyStatus);

    }

    private List<OnboardingParty> getParties(ClientApplicationSubmitDto dto) {
        entityManager.clear();
        ClientApplication clientApplication = entityManager.find(ClientApplication.class, dto.getKey().getClientApplicationKey());
        OnBoardingApplication onBoardingApplication = clientApplication.getOnboardingApplication();
        return onBoardingApplication.getParties();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void submit(ClientApplicationSubmitDto dto) {
        logger.info("Submitting...");
        applications.submit(dto, new FailFastErrorsImpl());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(ClientApplication application) {
        logger.info("Saving draft...");
        clientApplicationRepository.save(application);
    }
}
