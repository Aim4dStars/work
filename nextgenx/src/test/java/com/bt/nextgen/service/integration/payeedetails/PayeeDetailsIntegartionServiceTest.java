package com.bt.nextgen.service.integration.payeedetails;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.AvaloqExecuteImpl;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.payeedetails.AvaloqPayeeDetailsIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.payeedetails.PayeeDetailsImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;

public class PayeeDetailsIntegartionServiceTest
{

	@InjectMocks
	PayeeDetailsIntegrationService service = new AvaloqPayeeDetailsIntegrationServiceImpl();
	
	@Mock
	MoneyAccountIdentifierImpl identifier = new MoneyAccountIdentifierImpl();
	
	@Mock
	WrapAccountIdentifierImpl wrapidentifier = new WrapAccountIdentifierImpl();
	
	@Mock
	ServiceErrors errors = new ServiceErrorsImpl();
	
	@InjectMocks
	AvaloqExecute execute = new AvaloqExecuteImpl();
	
	@Mock
	ParsingContext context;
	
	@Before
	public void setUp()
	{
		PayeeDetailsImpl payee = new PayeeDetailsImpl();
		payee.setMaxDailyLimit("200000");
		
		identifier.setMoneyAccountId("76697");
		payee.setMoneyAccountIdentifier(identifier);
		
		wrapidentifier.setBpId("1234");
		
		/*Mockito.when(service.loadPayeeDetails(wrapidentifier, errors)).thenReturn(payee);*/
	}

	//TODO: fix this test
	@Ignore
	@Test
	public void testPayeeDetails() throws Exception
	{
		PayeeDetails details = service.loadPayeeDetails(wrapidentifier, errors);
		assertNotNull(details);
		assertEquals(wrapidentifier.getAccountIdentifier(), details.getMoneyAccountIdentifier().getMoneyAccountId());
	}
}
