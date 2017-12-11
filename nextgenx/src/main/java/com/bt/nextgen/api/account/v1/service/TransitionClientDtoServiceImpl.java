package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.transitions.TransitionAccountDto;
import com.bt.nextgen.api.account.v1.util.TransitionBrokerServiceFilterUtil;
import com.bt.nextgen.api.account.v1.util.TransitionFilterUtil;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.TransitionAccountIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L069552 on 16/09/2015.
 */
@Deprecated
@Service("TransitionClientService")
public class TransitionClientDtoServiceImpl implements TransitionClientDtoService {


    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private TransitionAccountIntegrationService transitionAccountIntegrationService;


    @Override
    public List<TransitionAccountDto> findAll(ServiceErrors serviceErrors) {

        List<ApiSearchCriteria> criterias = new ArrayList<ApiSearchCriteria>();
        TransitionFilterUtil transitionFilterUtil = new TransitionFilterUtil(accountService,productIntegrationService,transitionAccountIntegrationService);
        TransitionBrokerServiceFilterUtil transitionBrokerServiceFilterUtil = new TransitionBrokerServiceFilterUtil(brokerIntegrationService);
        List<TransitionAccountDto> transitionAccountDtos = transitionFilterUtil.findAll(criterias, serviceErrors);
        if (!transitionAccountDtos.isEmpty() && !accountService.loadWrapAccountWithoutContainers(serviceErrors).isEmpty())
            transitionAccountDtos.get(0).setAdviserList(transitionBrokerServiceFilterUtil
                    .getAdviserList(accountService.loadWrapAccountWithoutContainers(serviceErrors), serviceErrors));

        return transitionAccountDtos;
    }

    @Override
    public List<TransitionAccountDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        TransitionFilterUtil transitionFilterUtil = new TransitionFilterUtil(accountService,productIntegrationService,transitionAccountIntegrationService);
        TransitionBrokerServiceFilterUtil transitionBrokerServiceFilterUtil = new TransitionBrokerServiceFilterUtil(brokerIntegrationService);
        List<TransitionAccountDto> transitionAccountDtos = transitionFilterUtil.findAll(criteriaList,serviceErrors);
        if (!transitionAccountDtos.isEmpty() && !accountService.loadWrapAccountWithoutContainers(serviceErrors).isEmpty())
            transitionAccountDtos.get(0).setAdviserList(transitionBrokerServiceFilterUtil
                    .getAdviserList(accountService.loadWrapAccountWithoutContainers(serviceErrors), serviceErrors));

        return transitionAccountDtos;
    }
}
