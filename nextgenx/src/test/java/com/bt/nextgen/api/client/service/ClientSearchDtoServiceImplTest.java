package com.bt.nextgen.api.client.service;

import com.bt.nextgen.service.avaloq.account.AccountStatus;

import com.bt.nextgen.api.account.v1.model.AccountDto;
import com.bt.nextgen.api.account.v1.model.AccountKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import com.bt.nextgen.api.client.model.ClientDto;
import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualImpl;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientSearchDtoServiceImplTest
{
	@Mock
	private ClientListDtoServiceImpl clientListDtoService;

    @Mock
    private ClientIntegrationService clientIntegrationService;

	@InjectMocks
	private ClientSearchDtoServiceImpl clientSearchService;

	private ServiceErrors serviceErrors = new FailFastErrorsImpl();
	private List <ClientIdentificationDto> clientList = new ArrayList <>();
	private List<ApiSearchCriteria> filters;
	private IndividualImpl individual;

	@Before
	public void setup()
	{
		filters = new ArrayList<>();
		List <AccountDto> accounts = new ArrayList <>();
		ClientDto client1 = new ClientDto();
		ClientDto client2 = new ClientDto();

		AccountDto account = new AccountDto(new AccountKey());
		account.setAccountName("DB Pty ltd");
		account.setAccountId("1234567");
		account.setAccountNumber("1234567");
		account.setAccountStatus(String.valueOf(AccountStatus.PEND_OPN));
		accounts.add(account);

		AccountDto account2 = new AccountDto(new AccountKey());
		account2.setAccountName("ABC corp ltd");
		account2.setAccountId("123456");
		account2.setAccountNumber("123489");
		account2.setAccountStatus(String.valueOf(AccountStatus.ACTIVE));
		accounts.add(account2);

		client1.setAccounts(accounts);
		client1.setDisplayName("A&B corp");

		client2.setAccounts(accounts.subList(0,1));
		client2.setDisplayName("D'ennis Beecham");

		clientList.add(client1);
		clientList.add(client2);

		when(clientListDtoService.getFilteredValue(Mockito.any(String.class),Mockito.anyListOf(ApiSearchCriteria.class),Mockito.any(ServiceErrors.class))).thenReturn(clientList);

        individual = new IndividualImpl();
        individual.setClientKey(ClientKey.valueOf("1234"));
        individual.setFullName("D'ennis Beecham");
        AddressImpl address = new AddressImpl();
        individual.setAddresses(Arrays.asList(new Address[]{address}));
        individual.setFirstName("D'ennis");
        individual.setLastName("Beecham");

        when(clientIntegrationService.loadClients(serviceErrors)).thenReturn(Arrays.asList(new Client[]{individual}));
	}

	@Test
	public void testGetFilteredValueWithoutFilters()
	{
		clientList = clientSearchService.getFilteredValue("Denn", filters, serviceErrors);
		Assert.assertEquals(1, clientList.size());

		clientList = clientSearchService.getFilteredValue("Bee", filters, serviceErrors);
		Assert.assertEquals(1, clientList.size());

		clientList = clientSearchService.getFilteredValue("!@#", filters, serviceErrors);
		Assert.assertEquals(0, clientList.size());

		clientList = clientSearchService.getFilteredValue("D$Be", filters, serviceErrors);
		Assert.assertEquals(0, clientList.size());

		clientList = clientSearchService.getFilteredValue("  ", filters, serviceErrors);
		Assert.assertEquals(0, clientList.size());

		clientList = clientSearchService.getFilteredValue("D'e", filters, serviceErrors);
		Assert.assertEquals(1, clientList.size());

		clientList = clientSearchService.getFilteredValue("D'E", filters, serviceErrors);
		Assert.assertEquals(1, clientList.size());

		clientList = clientSearchService.getFilteredValue("D", filters, serviceErrors);
		Assert.assertEquals(2, clientList.size());

		clientList = clientSearchService.getFilteredValue("A&B", filters, serviceErrors);
		Assert.assertEquals(1, clientList.size());

		clientList = clientSearchService.getFilteredValue("1234", filters, serviceErrors);
		Assert.assertEquals(2, clientList.size());

		clientList = clientSearchService.getFilteredValue("'-'", filters, serviceErrors);
		Assert.assertEquals(0, clientList.size());

		clientList = clientSearchService.getFilteredValue("A_B", filters, serviceErrors);
		Assert.assertEquals(1, clientList.size());

		clientList = clientSearchService.getFilteredValue("AB", filters, serviceErrors);
		Assert.assertEquals(1, clientList.size());

		clientList = clientSearchService.getFilteredValue("A%", filters, serviceErrors);
		Assert.assertEquals(0, clientList.size());

		clientList = clientSearchService.getFilteredValue("A--", filters, serviceErrors);
		Assert.assertEquals(1, clientList.size());

		clientList = clientSearchService.getFilteredValue("A/B", filters, serviceErrors);
		Assert.assertEquals(0, clientList.size());

		clientList = clientSearchService.getFilteredValue("D,B", filters, serviceErrors);
		Assert.assertEquals(1, clientList.size());

		clientList = clientSearchService.getFilteredValue("D.B", filters, serviceErrors);
		Assert.assertEquals(0, clientList.size());

		clientList = clientSearchService.getFilteredValue("D()B", filters, serviceErrors);
		Assert.assertEquals(0, clientList.size());

		clientList = clientSearchService.getFilteredValue("D-B", filters, serviceErrors);
		Assert.assertEquals(1, clientList.size());

		clientList = clientSearchService.getFilteredValue("D-S", filters, serviceErrors);
		Assert.assertEquals(0, clientList.size());

		clientList = clientSearchService.getFilteredValue("D_-B", filters, serviceErrors);
		Assert.assertEquals(1, clientList.size());

		clientList = clientSearchService.getFilteredValue("D   '    e", filters, serviceErrors);
		Assert.assertEquals(1, clientList.size());

		clientList = clientSearchService.getFilteredValue("D   _    e", filters, serviceErrors);
		Assert.assertEquals(0, clientList.size());

		clientList = clientSearchService.getFilteredValue("D   _   B", filters, serviceErrors);
		Assert.assertEquals(1, clientList.size());

		clientList = clientSearchService.getFilteredValue("'De", filters, serviceErrors);
		Assert.assertEquals(1, clientList.size());

		clientList = clientSearchService.getFilteredValue("'D", filters, serviceErrors);
		Assert.assertEquals(2, clientList.size());
	}

	@Test
	public void testGetFilteredValueWithFilters()
	{
		filters.add(new ApiSearchCriteria("accountStatus", SearchOperation.EQUALS,"Active", OperationType.STRING ));

		clientList = clientSearchService.getFilteredValue("Denn", filters, serviceErrors);
		Assert.assertEquals(0, clientList.size());

		clientList = clientSearchService.getFilteredValue("A&B", filters, serviceErrors);
		Assert.assertEquals(1, clientList.size());

		clientList = clientSearchService.getFilteredValue("1234", filters, serviceErrors);
		Assert.assertEquals(1, clientList.size());
		Assert.assertEquals("A&B corp", ((ClientDto)clientList.get(0)).getDisplayName());
	}

}
