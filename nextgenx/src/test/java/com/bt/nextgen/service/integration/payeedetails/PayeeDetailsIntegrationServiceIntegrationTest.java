package com.bt.nextgen.service.integration.payeedetails;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.pasttransaction.TransactionOrderType;
import com.bt.nextgen.service.avaloq.pasttransaction.TransactionType;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PayeeDetailsIntegrationServiceIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	PayeeDetailsIntegrationService service;
	
	@Test
    @SecureTestContext
	public void testPayeeDetailsService() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		WrapAccountIdentifier identifier = new WrapAccountIdentifierImpl();
		identifier.setBpId("80217");
		
		service.clearCache(identifier);
		PayeeDetails payee = service.loadPayeeDetails(identifier, serviceErrors);
		assertNotNull(payee);
		
		assertEquals("200000", payee.getMaxDailyLimit());
		
		//Check cashAccount details
		assertEquals("120012232", payee.getCashAccount().getAccountNumber());
		assertEquals("Martin Demo Taylor", payee.getCashAccount().getAccountName());
		assertEquals("220186", payee.getCashAccount().getBillerCode());
		assertEquals("262786", payee.getCashAccount().getBsb());
		
		//Check payanyone details
		assertEquals("345345345", payee.getPayanyonePayeeList().get(0).getAccountNumber());
		assertEquals("12003", payee.getPayanyonePayeeList().get(0).getBsb());
		assertEquals("deepshikha payanyone", payee.getPayanyonePayeeList().get(0).getName());
		assertEquals("deepshikha", payee.getPayanyonePayeeList().get(0).getNickName());
		
		//Check Linkedaccount details
		assertEquals("123456789", payee.getLinkedAccountList().get(0).getAccountNumber());
		assertEquals("36154", payee.getLinkedAccountList().get(0).getBsb());
		assertEquals("Linked Account Name 05", payee.getLinkedAccountList().get(0).getName());
		assertEquals("Linked Account Nickname 05", payee.getLinkedAccountList().get(0).getNickName());
		assertEquals("3000", payee.getLinkedAccountList().get(0).getLimit().toString());
		assertEquals("6",payee.getLinkedAccountList().get(0).getLinkedAccountStatus());
		assertEquals(true, payee.getLinkedAccountList().get(0).isPrimary());
		assertEquals("1000", payee.getLinkedAccountList().get(0).getRemainingLimit().toString());
		assertEquals("8000", payee.getLinkedAccountList().get(1).getRemainingLimit().toString());
		
		//Check bpay details
		assertEquals("1008", payee.getBpayBillerPayeeList().get(0).getBillerCode());
		assertEquals("283947934223423", payee.getBpayBillerPayeeList().get(0).getCRN());
		assertEquals("deepshikha bpay", payee.getBpayBillerPayeeList().get(0).getName());
		assertEquals("singh", payee.getBpayBillerPayeeList().get(1).getNickName());
		
		assertEquals("76690", payee.getPayeeAuthorityList().get(0).getPersonId());

		//Check PayeeLimits
		assertEquals(TransactionType.PAY, payee.getPayeeLimits().get(0).getMetaType());
        assertEquals(TransactionOrderType.BPAY, payee.getPayeeLimits().get(0).getOrderType());
        assertEquals("aud", payee.getPayeeLimits().get(0).getCurrency());
        
        assertEquals(TransactionType.PAY, payee.getPayeeLimits().get(1).getMetaType());
        assertEquals(TransactionOrderType.PAY_ANYONE, payee.getPayeeLimits().get(1).getOrderType());
        assertEquals("aud", payee.getPayeeLimits().get(1).getCurrency());

		assertEquals("40000", payee.getPayeeLimits().get(0).getLimitAmount());
		assertEquals("70000", payee.getPayeeLimits().get(0).getRemainingLimit());
		assertEquals("78000", payee.getPayeeLimits().get(1).getRemainingLimit());
		
		//check money account id
		assertEquals("76697", payee.getMoneyAccountIdentifier().getMoneyAccountId());
		
		//check modifier sequence number
		assertEquals("1200", payee.getModifierSequenceNumber().getModificationIdentifier().toString());
	}

    @Test
	
    @SecureTestContext(username = "explode")
    public void testPayeeDetailsServiceError() throws Exception
    {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        WrapAccountIdentifier identifier = new WrapAccountIdentifierImpl();
        identifier.setBpId("80217");
        PayeeDetails payee = service.loadPayeeDetails(identifier, serviceErrors);
        assertNotNull(payee);
     //   assertEquals("200000", payee.getMaxDailyLimit());
        assertThat(serviceErrors.hasErrors(), Is.is(true));

    }
}
