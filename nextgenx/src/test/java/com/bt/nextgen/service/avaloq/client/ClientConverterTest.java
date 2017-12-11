package com.bt.nextgen.service.avaloq.client;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;

@RunWith(MockitoJUnitRunner.class)
public class ClientConverterTest
{
	@InjectMocks
	private ClientConverter clientConverter = new ClientConverter();

	@Mock
	private StaticIntegrationService staticService;
	@Mock
	private ProductIntegrationService productIntegrationService;

	ServiceErrors serviceErrors = new FailFastErrorsImpl();

	@Before
	public void setup()
	{
		Mockito.when(staticService.loadCode(Mockito.any(CodeCategory.class),
			Mockito.anyString(),
			Mockito.any(ServiceErrors.class))).thenAnswer(new Answer <Object>()
		{
			public Object answer(InvocationOnMock invocation)
			{
				Object[] args = invocation.getArguments();
				if (CodeCategory.PERSON_TYPE.equals(args[0]) && "120".equals(args[1]))
				{
					return new CodeImpl("120", "person", Constants.NATURAL_PERSON);
				}
				else if (CodeCategory.PERSON_TYPE.equals(args[0]) && "121".equals(args[1]))
				{
					return new CodeImpl("121", "person", Constants.LEGAL_ENTITY);
				}
				else if (CodeCategory.ACCOUNT_STRUCTURE_TYPE.equals(args[0]) && "20611".equals(args[1]))
				{
					return new CodeImpl("20611", "btfg$indvl", "Individual");
				}
				else if (CodeCategory.COUNTRY.equals(args[0]) && "2061".equals(args[1]))
				{
					return new CodeImpl("2061", "country", "Australia");
				}
				else if (CodeCategory.STATES.equals(args[0]) && "5004".equals(args[1]))
				{
					return new CodeImpl("2061", "state", "New South Wales");
				}
				else
				{
					return "Unknown";
				}
			}
		});
	}

	@Test
	public void testToClient() throws Exception
	{
		com.avaloq.abs.screen_rep.hira.btfg$ui_custr_list_bp_person_cont.Rep report = JaxbUtil.unmarshall("/webservices/response/BTFG$UI_CUSTR_LIST.BP_PERSON_CONT_UT.xml",
			com.avaloq.abs.screen_rep.hira.btfg$ui_custr_list_bp_person_cont.Rep.class);
		Map <ClientKey, ClientDetail> clientMap = clientConverter.toClientMap(report, serviceErrors);
		Assert.assertEquals(1, clientMap.size());

		ClientDetail client = clientMap.values().iterator().next();
		Assert.assertEquals(1, client.getWrapAccounts().size());
		AccountKey account = client.getWrapAccounts().iterator().next();
		Assert.assertEquals("45403", account.getId());

		PersonImpl person = (PersonImpl)client;
		Assert.assertEquals("45401", person.getClientKey().getId());
		Assert.assertEquals("Adrian", person.getFirstName());
		Assert.assertEquals("Smith", person.getLastName());
		Assert.assertEquals("Adrian Demo Smith", person.getFullName());

		Assert.assertEquals(0, person.getRelatedPersons().size());
	}
}
