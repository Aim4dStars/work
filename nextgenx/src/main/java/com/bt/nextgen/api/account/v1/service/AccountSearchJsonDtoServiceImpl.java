/**
 *
 */
package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.client.model.JsonItemDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.json.AccountJsonStreamStrategy;
import com.bt.nextgen.service.json.JsonStreamProcessor;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author L095519 created on 07.07.2017
 *
 */
@Service("AccountSearchDtoService")
public class AccountSearchJsonDtoServiceImpl implements AccountSearchJsonDtoService {

	@Autowired
	@Qualifier("avaloqAccountIntegrationService")
	private AccountIntegrationService accountIntegrationService;

	@Autowired
	private BrokerIntegrationService brokerIntegrationService;

	@Autowired
	private UserProfileService userProfileService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bt.nextgen.api.account.v3.service.AccountSearchJsonDtoService#
	 * searchAccount(java.lang.String, java.lang.String)
	 */

	private List<JsonItemDto> searchAccount(String query) {
		List<JsonItemDto> list = new ArrayList<>();
		List<BrokerKey> brokerKeys = new ArrayList<>();
		if (query.length() >= 2) {
			final Collection<BrokerIdentifier> brokers = brokerIntegrationService
					.getAdvisersForUser(userProfileService.getActiveProfile(), new FailFastErrorsImpl());
			for (BrokerIdentifier broker : brokers) {
				brokerKeys.add(broker.getKey());
			}
			List<String> response = accountIntegrationService.searchAccount(query, brokerKeys,
					new FailFastErrorsImpl());

			JsonStreamProcessor accountJsonProcessor = new JsonStreamProcessor(new AccountJsonStreamStrategy());

			for (String json : response) {
				list.add(new JsonItemDto(accountJsonProcessor.processJson(json)));
			}

		}
		return list;
	}

    public List<JsonItemDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        ApiSearchCriteria criteria = criteriaList.get(0);
        String query = criteria.getValue();
        return searchAccount(query);
    }

}