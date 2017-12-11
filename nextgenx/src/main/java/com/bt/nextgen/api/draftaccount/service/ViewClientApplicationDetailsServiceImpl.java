package com.bt.nextgen.api.draftaccount.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDetailsDto;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.PermittedClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.ApplicationDocumentDetailImpl;
import com.bt.nextgen.service.avaloq.accountactivation.ApprovalType;
import com.bt.nextgen.service.avaloq.accountactivation.LinkedPortfolioDetails;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.ApplicationDocumentDetail;
import com.bt.nextgen.service.integration.account.ApplicationDocumentIntegrationService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by F058391 on 10/08/2016.
 */
@Service
public class ViewClientApplicationDetailsServiceImpl implements ViewClientApplicationDetailsService {

    @Autowired
    private PermittedClientApplicationRepository clientApplicationRepository;

    @Autowired
    private ClientApplicationDetailsDtoConverterService clientApplicationDetailsDtoConverterService;

    @Autowired
    private BrokerHelperService brokerHelperService;

    @Autowired
    private ApplicationDocumentIntegrationService applicationDocumentIntegrationService;

    @Override
    public ClientApplicationDetailsDto viewClientApplicationById(Long clientApplicationId, ServiceErrors serviceErrors) {

        ClientApplication clientApplication = clientApplicationRepository.findByClientApplicationId(clientApplicationId);
        if (clientApplication != null && clientApplication.getOnboardingApplication() != null) {
            return clientApplicationDetailsDtoConverterService.convert(clientApplication, serviceErrors);
        }
        return null;
    }

    @Override
    public ClientApplicationDetailsDto viewClientApplicationByAccountNumber(String accountNumber, ServiceErrors serviceErrors) {
        List<ApplicationDocumentDetail> applicationDocumentDetailList = applicationDocumentIntegrationService.loadApplicationDocuments(Lists.newArrayList(accountNumber), serviceErrors);
        return getClientApplicationDetails(applicationDocumentDetailList,serviceErrors);
    }

    @Override
    public ClientApplicationDetailsDto viewOnlineClientApplicationByAccountNumbers(List<String> accountNumbers, ServiceErrors serviceErrors) {
        List<ApplicationDocumentDetail> applicationDocumentDetailList = applicationDocumentIntegrationService.loadApplicationDocuments(accountNumbers, serviceErrors);
        applicationDocumentDetailList = Lambda.filter(new LambdaMatcher<ApplicationDocumentDetail>(){
            @Override
            protected boolean matchesSafely(ApplicationDocumentDetail applicationDocumentDetail) {
                return ((ApplicationDocumentDetailImpl)applicationDocumentDetail).getApprovalType()== ApprovalType.ONLINE;
            }
        },applicationDocumentDetailList);
        return getClientApplicationDetails(applicationDocumentDetailList,serviceErrors);
    }

    private ClientApplicationDetailsDto getClientApplicationDetails(List<ApplicationDocumentDetail> applicationDocumentDetailList, ServiceErrors serviceErrors){
        if (CollectionUtils.isNotEmpty(applicationDocumentDetailList)) {
            UserExperience userExperience = brokerHelperService.getUserExperience(applicationDocumentDetailList.get(0),serviceErrors);
            ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService.convert(applicationDocumentDetailList.get(0), serviceErrors, userExperience);
            LinkedPortfolioDetails linkedPortfolioDetails = applicationDocumentDetailList.get(0).getPortfolio().get(0);
            final String accountNumber = linkedPortfolioDetails.getAccountNumber();
            return clientApplicationDetailsDto.withAccountKey(accountNumber);
        }
        return null;
    }

}
