package com.bt.nextgen.api.account.v1.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.client.model.JsonItemDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.avaloq.broker.BrokerIdentifierImpl;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
/**
 * @author L096395 created on 07.07.2017
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountSearchJsonDtoServiceImplTest {
	@Mock
	AccountIntegrationService accountIntegrationService;

	@Mock
	private BrokerIntegrationService brokerIntegrationService;

	@Mock
	private UserProfileService profileService;

	@InjectMocks
	AccountSearchJsonDtoServiceImpl accountSearchJsonDtoServiceImpl;

	private List<ApiSearchCriteria> filters = new ArrayList<>();
	private ServiceErrors serviceErrors = new FailFastErrorsImpl();
	
	@Test
	public void test_search_withValidLengthQuery() {
		String response1 = "{\"position_id\" : 101861,\"client_id\" : 1188818,\"account_id\" : 1188821,\"account_bsb\" : \"262786\"}";
		Collection<BrokerIdentifier> value = new ArrayList<>();
		value.add(new BrokerIdentifierImpl("1234"));
		List<String> listOfString = new ArrayList<>();
		listOfString.add(response1);
		Mockito.when(brokerIntegrationService.getAdvisersForUser(Mockito.any(UserProfile.class),
				Mockito.any(ServiceErrors.class))).thenReturn(value);
		Mockito.when(accountIntegrationService.searchAccount(Mockito.anyString(), Mockito.anyListOf(BrokerKey.class),
				Mockito.any(ServiceErrors.class))).thenReturn(listOfString);
		filters.add(new ApiSearchCriteria("filterForAccount", SearchOperation.EQUALS, "per", OperationType.STRING));
		List<JsonItemDto> jsonItemDtoList = accountSearchJsonDtoServiceImpl.search(filters, serviceErrors);
		Mockito.verify(brokerIntegrationService).getAdvisersForUser(Mockito.any(UserProfile.class),
				Mockito.any(ServiceErrors.class));
		assertEquals(1, jsonItemDtoList.size());
	}

	@Test
	public void test_search_withInValidLengthQuery() {
		Collection<BrokerIdentifier> value = new ArrayList<>();
		value.add(new BrokerIdentifierImpl("1234"));
		Mockito.when(brokerIntegrationService.getAdvisersForUser(Mockito.any(UserProfile.class),
				Mockito.any(ServiceErrors.class))).thenReturn(value);
		Mockito.when(accountIntegrationService.searchAccount(Mockito.anyString(), Mockito.anyListOf(BrokerKey.class),
				Mockito.any(ServiceErrors.class))).thenReturn(Collections.<String>emptyList());
		filters.add(new ApiSearchCriteria("filterForAccount", SearchOperation.EQUALS, "p", OperationType.STRING));
		List<JsonItemDto> jsonItemDtoList = accountSearchJsonDtoServiceImpl.search(filters, serviceErrors);
		assertTrue(jsonItemDtoList.isEmpty());
	}
}
