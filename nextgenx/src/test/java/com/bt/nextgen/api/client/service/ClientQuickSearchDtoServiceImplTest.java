package com.bt.nextgen.api.client.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
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
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.avaloq.broker.BrokerIdentifierImpl;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
/**
 * 
 * @author L096395
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientQuickSearchDtoServiceImplTest {

	@Mock
	private ClientIntegrationService clientIntegrationService;

	@Mock
	private BrokerIntegrationService brokerIntegrationService;

	@Mock
	private UserProfileService profileService;

	@InjectMocks
	ClientQuickSearchDtoServiceImpl clientQuickSearchDtoServiceImpl;

	private List<ApiSearchCriteria> filters = new ArrayList<>();
	private ServiceErrors serviceErrors = new FailFastErrorsImpl();

	@Test
	public void test_search_withValidLengthQuery() {
		String response1 = "{\"position_id\":101861,\"client_id\":1188818,\"account_id\":1188821,\"account_bsb\":\"262786\",\"account_name\":\"bp--1_4431\",\"account_number\":\"120132030\",\"account_open_date\":null,\"account_status\":\"\",\"account_structure_type\":\"SMSF\",\"account_type\":\"Customer\",\"adviser_id\":29561,\"adviser_name\":\"N Craig Leibbrandt\",\"checksum\":\"101861\",\"client_first_name\":\"person-121_10645\",\"client_full_name\":\"person-121_10645person-121_10645person-121_10645\",\"client_last_name\":\"person-121_10645\",\"client_middle_name\":\"person-121_10645\",\"object_hierarchy\":[{\"oe_id\":99780,\"oetype_id\":660307,\"oetype\":\"Issuer\"}]}";
		Collection<BrokerIdentifier> value = new ArrayList<>();
		value.add(new BrokerIdentifierImpl("1234"));
		List<String> listOfString = new ArrayList<>();
		listOfString.add(response1);
		when(brokerIntegrationService.getAdvisersForUser(Mockito.any(UserProfile.class),
				Mockito.any(ServiceErrors.class))).thenReturn(value);
		when(clientIntegrationService.performClientSearch(Mockito.anyString(), Mockito.anyListOf(BrokerKey.class),
				Mockito.any(ServiceErrors.class))).thenReturn(listOfString);
		filters.add(new ApiSearchCriteria("filterForAccount", SearchOperation.EQUALS, "per", OperationType.STRING));
		List<JsonItemDto> jsonItemDtoList = clientQuickSearchDtoServiceImpl.search(filters, serviceErrors);
		Mockito.verify(brokerIntegrationService).getAdvisersForUser(Mockito.any(UserProfile.class),
				Mockito.any(ServiceErrors.class));
		Assert.assertEquals(1, jsonItemDtoList.size());
	}

	@Test
	public void test_search_withInValidLengthQuery() {
		Collection<BrokerIdentifier> value = new ArrayList<>();
		value.add(new BrokerIdentifierImpl("1234"));
		when(brokerIntegrationService.getAdvisersForUser(Mockito.any(UserProfile.class),
				Mockito.any(ServiceErrors.class))).thenReturn(value);
		when(clientIntegrationService.performClientSearch(Mockito.anyString(), Mockito.anyListOf(BrokerKey.class),
				Mockito.any(ServiceErrors.class))).thenReturn(Collections.<String>emptyList());
		filters.add(new ApiSearchCriteria("filterForAccount", SearchOperation.EQUALS, "p", OperationType.STRING));
		List<JsonItemDto> jsonItemDtoList = clientQuickSearchDtoServiceImpl.search(filters, serviceErrors);
		assertTrue(jsonItemDtoList.isEmpty());
	}

}