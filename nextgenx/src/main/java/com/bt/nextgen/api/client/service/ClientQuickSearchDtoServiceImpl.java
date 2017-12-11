package com.bt.nextgen.api.client.service;

import com.bt.nextgen.api.client.model.JsonItemDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.json.AccountJsonStreamStrategy;
import com.bt.nextgen.service.json.JsonStreamProcessor;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author L096395
 */
@Service("ClientQuickSearchDtoService")
public class ClientQuickSearchDtoServiceImpl implements ClientQuickSearchDtoService {
    
    private static final Logger LOG = LoggerFactory.getLogger(ClientQuickSearchDtoServiceImpl.class);

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private UserProfileService profileService;

    private List<JsonItemDto> performClientSearch(String query) {

        List<JsonItemDto> list = new ArrayList<>();
        LOG.info("Inside performClientSearchByQueryString of ClientQuickSearchDtoServiceImpl: {}", query);

        if (query.length() >= 2) {

            List<BrokerKey> brokerKeys = new ArrayList<>();
            Collection<BrokerIdentifier> brokerIds = brokerIntegrationService.getAdvisersForUser(profileService.getActiveProfile(), new FailFastErrorsImpl());
            for (BrokerIdentifier brokerId: brokerIds) {
                brokerKeys.add(brokerId.getKey());
            }

            List<String> response = clientIntegrationService.performClientSearch(query, brokerKeys, new FailFastErrorsImpl());
            
            JsonStreamProcessor accountJsonProcessor = new JsonStreamProcessor(new AccountJsonStreamStrategy());

			for (String json : response) {
				list.add(new JsonItemDto(accountJsonProcessor.processJson(json)));
			}
        }

        return list;
    }

    @Override
    public List<JsonItemDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        ApiSearchCriteria criteria = criteriaList.get(0);
        String query = criteria.getValue();
        return performClientSearch(query);
    }
}