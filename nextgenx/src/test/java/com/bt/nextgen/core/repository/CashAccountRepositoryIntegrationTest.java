package com.bt.nextgen.core.repository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.test.client.MockWebServiceServer;
import org.springframework.ws.test.client.RequestMatchers;
import org.springframework.ws.test.client.ResponseCreators;
import org.springframework.xml.transform.StringSource;
//TODO - Change this test to not be an integration test.
public class CashAccountRepositoryIntegrationTest
{
	//@Autowired //XXX MIXING MOCKS AND SPRING CONTEXT IS BAAAAAAAAD
	//private WebServiceProvider provider = ;

	private MockWebServiceServer mockWebServer;

	@Before
	public void setUp() throws Exception
	{
		WebServiceTemplate template = new WebServiceTemplate();//= provider.getDefaultWebServiceTemplate();
		
		mockWebServer = MockWebServiceServer.createServer(template);
		template.setInterceptors(new ClientInterceptor[0]);
	}

	@Test
	public void testServiceCall() throws Exception
	{
		String xml = "<ns2:CashAccountResponse xmlns:ns2=\"ns://btfin.com/nextgen/services/schemas\">" + "<Status>OK</Status>"
			+ "<AccountName>Dennis Beecham</AccountName>" + "<Bsb>205456</Bsb>" + "<AccountNumber>234568876</AccountNumber>"
			+ "</ns2:CashAccountResponse>";
		mockWebServer.expect(RequestMatchers.anything()).andRespond(ResponseCreators.withPayload(new StringSource(xml)));

		//CashAccount cashAccount = cashAccountRepository.load("234568876");
		//assertEquals(cashAccount.getBsb(), "205456");
		//assertEquals(cashAccount.getAccountNumber(), "234568876");
	}
}
