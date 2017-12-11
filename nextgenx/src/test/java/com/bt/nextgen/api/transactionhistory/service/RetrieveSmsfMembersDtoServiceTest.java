package com.bt.nextgen.api.transactionhistory.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import com.bt.nextgen.core.web.ApiFormatter;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.transactionhistory.model.SmsfMembersDto;
import com.bt.nextgen.core.web.Format;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;

@RunWith(MockitoJUnitRunner.class)
public class RetrieveSmsfMembersDtoServiceTest
{
	@InjectMocks
	private RetrieveSmsfMembersDtoServiceImpl retrieveSmsfMembersDtoService;

	@Mock
	private AccountIntegrationService accountService;

	private List <SmsfMembersDto> smsfMembers;
	private List <Client> allAssociatedPersons;
	WrapAccountDetailImpl accountDetail;

	@Before
	public void setup()
	{
		smsfMembers = new ArrayList <SmsfMembersDto>();
		accountDetail = new WrapAccountDetailImpl();
		allAssociatedPersons = new ArrayList <Client>();

		Client client1 = new IndividualDetailImpl();
		{}
		;
		((IndividualDetailImpl)client1).setMember(true);
		((IndividualDetailImpl)client1).setFirstName("Greg");
		((IndividualDetailImpl)client1).setLastName("Twist");
		((IndividualDetailImpl)client1).setClientKey(ClientKey.valueOf("plaintext"));
		((IndividualDetailImpl)client1).setDateOfBirth(new DateTime(2005, 3, 26, 12, 0, 0, 0));

		Client client2 = new IndividualDetailImpl();

		((IndividualDetailImpl)client2).setMember(true);
		((IndividualDetailImpl)client2).setFirstName("Don");
		((IndividualDetailImpl)client2).setLastName("Bosco");
		((IndividualDetailImpl)client2).setClientKey(ClientKey.valueOf("plaintext"));
		((IndividualDetailImpl)client2).setDateOfBirth(new DateTime(1987, 4, 14, 12, 0, 0, 0));

		Client client3 = new IndividualDetailImpl();

		((IndividualDetailImpl)client3).setMember(false);
		((IndividualDetailImpl)client3).setFirstName("Sharon");
		((IndividualDetailImpl)client3).setLastName("Cork");
		((IndividualDetailImpl)client3).setClientKey(ClientKey.valueOf("plaintext"));
		((IndividualDetailImpl)client3).setDateOfBirth(new DateTime(1978, 6, 18, 12, 0, 0, 0));

		Client client4 = new IndividualDetailImpl();

		((IndividualDetailImpl)client4).setMember(true);
		((IndividualDetailImpl)client4).setFirstName("Dennis");
		((IndividualDetailImpl)client4).setLastName("Beecham");
		((IndividualDetailImpl)client4).setClientKey(ClientKey.valueOf("plaintext"));
		((IndividualDetailImpl)client4).setDateOfBirth(new DateTime(1990, 3, 25, 12, 0, 0, 0));

		allAssociatedPersons.add(client1);
		allAssociatedPersons.add(client2);
		allAssociatedPersons.add(client3);
		allAssociatedPersons.add(client4);
		accountDetail.setAllAssociatedPersons(allAssociatedPersons);

	}

	@Test
	public void getSmsfMembers()
	{
		Mockito.when(accountService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
			.thenReturn(accountDetail);

		smsfMembers = retrieveSmsfMembersDtoService.toMembersList(accountDetail);
		assertNotNull(smsfMembers);
		Assert.assertEquals(smsfMembers.size(), 3);
		
		Assert.assertEquals(smsfMembers.get(2).getFirstName(), "Greg");
		Assert.assertEquals(smsfMembers.get(2).getLastName(), "Twist");
		Assert.assertEquals(smsfMembers.get(2).getDateOfBirth(), ApiFormatter.asShortDate(new DateTime(2005, 3, 26, 12, 0, 0, 0)));

		Assert.assertEquals(smsfMembers.get(0).getFirstName(), "Dennis");
		Assert.assertEquals(smsfMembers.get(0).getLastName(), "Beecham");
		Assert.assertEquals(smsfMembers.get(0).getDateOfBirth(), ApiFormatter.asShortDate(new DateTime(1990, 3, 25, 12, 0, 0, 0)));
	}

}
