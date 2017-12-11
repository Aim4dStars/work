package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.LoggingConstants;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationWithdrawalDto;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.PermittedClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.accountactivation.AccountActivationRequest;
import com.bt.nextgen.service.avaloq.accountactivation.AccountActivationRequestImpl;
import com.bt.nextgen.service.integration.accountactivation.ActivationAccountIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(value = "springJpaTransactionManager")
public class ClientApplicationWithdrawalDtoServiceImpl implements ClientApplicationWithdrawalDtoService {

    @Autowired
    ActivationAccountIntegrationService activationAccountIntegrationService;

    @Autowired
    private PermittedClientApplicationRepository permittedClientApplicationRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientApplicationWithdrawalDtoServiceImpl.class);

    @Override
    public ClientApplicationWithdrawalDto submit(ClientApplicationWithdrawalDto keyedObject, ServiceErrors serviceErrors) {
        LOGGER.info(LoggingConstants.ONBOARDING_WITHDRAWAL + "begin");
        Long clientApplicationKey = keyedObject.getKey().getClientApplicationKey();
        LOGGER.info(LoggingConstants.ONBOARDING_WITHDRAWAL + "clientApplicationKey=" + clientApplicationKey);
        ClientApplication clientApplication = permittedClientApplicationRepository.find(clientApplicationKey);
        OnBoardingApplication onboardingApplication = clientApplication.getOnboardingApplication();
        String avaloqOrderId = onboardingApplication.getAvaloqOrderId();
        //TODO : Weird to send one of the investor gcm ids for withdrawal. Only avaloqId should be enough.
        LOGGER.info(LoggingConstants.ONBOARDING_WITHDRAWAL + "avaloqOrderId=" + avaloqOrderId);
        String investorGcmId = onboardingApplication.getParties().get(0).getGcmPan();
        LOGGER.info(LoggingConstants.ONBOARDING_WITHDRAWAL + "investorGcmId=" + investorGcmId);
        AccountActivationRequest accountActivationRequest = new AccountActivationRequestImpl(investorGcmId, avaloqOrderId, null);
        boolean status = activationAccountIntegrationService.withdrawAccount(accountActivationRequest, serviceErrors);
        LOGGER.info(LoggingConstants.ONBOARDING_WITHDRAWAL + "status=" + status);
        keyedObject.setWithdrawn(status);
        LOGGER.info(LoggingConstants.ONBOARDING_WITHDRAWAL + "end");
        return keyedObject;
    }
}
