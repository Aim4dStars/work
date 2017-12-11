package com.bt.nextgen.service.avaloq.termdeposit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.termdeposit.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

import java.util.ArrayList;
import java.util.List;

public class TermDepositServiceIntegrationTest  extends BaseSecureIntegrationTest
{
	@Autowired
	private TermDepositIntegrationService termDepositService;
	
	private ServiceErrors serviceErrors;
	
	@Before
	public void setup()
	{
		serviceErrors = new ServiceErrorsImpl();
	}
	
	@Test
	public void testTermDeposit()
	{
		TermDepositTrxRequest request = new TermDepositTrxRequestImpl();
		request.setAmount("5000");
		request.setAsset("28124");
		request.setCurrencyCode("1009");
		request.setPortfolio("55627");
		boolean addTermDeposit = termDepositService.termDeposit(TermDepositAction.ADD_TERM_DEPOSIT, request, serviceErrors);
		assertEquals(addTermDeposit, true);
		boolean breakTermDeposit = termDepositService.termDeposit(TermDepositAction.BREAK_TERM_DEPOSIT, request, serviceErrors);
		assertEquals(breakTermDeposit, true);
		boolean validAddTermDeposit = termDepositService.termDeposit(TermDepositAction.VALIDATE_ADD_TERM_DEPOSIT, request, serviceErrors);
		assertEquals(validAddTermDeposit, true);
		request.setRenewMode("true");
		boolean updateTermDeposit = termDepositService.termDeposit(TermDepositAction.UPDATE_TERM_DEPOSIT, request, serviceErrors);
		assertEquals(updateTermDeposit, true);
	}

	@Test
	public void testValidateBreakTermDeposit()
	{
		TermDepositTrxRequest request = new TermDepositTrxRequestImpl();
		request.setAsset("28124");
		request.setPortfolio("55627");
		TermDepositTrx termDepositTrx = termDepositService.validateBreakTermDeposit(request, serviceErrors);
		assertNotNull(termDepositTrx.getWithdrawInterestPaid());
		assertNotNull(termDepositTrx.getCurrPrpl());
		assertNotNull(termDepositTrx.getPercentTermElapsed());
	}

    @SecureTestContext(username = "adviser", customerId = "201101101")
    @Test
    public void testLoadTermDeposit_withOeId()
    {
        BrokerKey brokerKey = BrokerKey.valueOf("29955");
        List<TermDeposit> termDeposits = termDepositService.loadTermDeposit(brokerKey, serviceErrors);
        assertNotNull(termDeposits);
        assertTrue(termDeposits.size()>0);
    }

    @SecureTestContext(username = "adviser", customerId = "201101101")
    @Test
    public void testLoadTermDeposit_withOeIds()
    {
        List<BrokerKey> brokerKeys = new ArrayList<>();
        BrokerKey brokerKey1 = BrokerKey.valueOf("29955");
        BrokerKey brokerKey2 = BrokerKey.valueOf("35989");
        brokerKeys.add(brokerKey1);
        brokerKeys.add(brokerKey2);

        List<TermDeposit> termDeposits = termDepositService.loadTermDeposit(brokerKeys, serviceErrors);
        assertNotNull(termDeposits);
        assertTrue(termDeposits.size()>0);
    }
}
