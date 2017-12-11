package com.bt.nextgen.service.avaloq.termdeposit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;

import org.h2.util.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.bt.nextgen.service.ServiceError;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.gateway.AvaloqException;
import com.bt.nextgen.service.integration.termdeposit.TermDepositAction;
import com.bt.nextgen.service.integration.termdeposit.TermDepositIntegrationService;
import com.bt.nextgen.service.integration.termdeposit.TermDepositTrx;
import com.bt.nextgen.service.integration.termdeposit.TermDepositTrxImpl;
import com.bt.nextgen.service.integration.termdeposit.TermDepositTrxRequest;
import com.bt.nextgen.service.integration.termdeposit.TermDepositTrxRequestImpl;

import java.math.BigDecimal;

@RunWith(MockitoJUnitRunner.class)
public class TermDepositServiceTest 
{
	@Mock
	private TermDepositIntegrationService termDepositService;
	
	private ServiceErrors serviceErrors;
	
	@Before
	public void setup()
	{
		serviceErrors = new ServiceErrorsImpl();
		ServiceError error = new ServiceErrorImpl();
		error.setReason("request parameters not correct");
		serviceErrors.addError(error);
		
		Mockito.when(termDepositService.termDeposit((TermDepositAction)anyObject(), (TermDepositTrxRequest)anyObject(), (ServiceErrors)anyObject())).thenAnswer(new Answer<Boolean>() 
				{
				    @Override
				    public Boolean answer(InvocationOnMock invocation) throws Throwable 
				    {
				        Object[] arguments = invocation.getArguments();
				        if (arguments != null && arguments.length > 0 && arguments[0] != null && arguments[1] != null)
				        {
				        	TermDepositAction action = (TermDepositAction) arguments[0];
				        	TermDepositTrxRequest request = (TermDepositTrxRequest) arguments[1];
				        	
				        	if(action.equals(TermDepositAction.ADD_TERM_DEPOSIT))
				        	{
				        		String amount = request.getAmount();
				        		String asset = request.getAsset();
				        		String currencyCode = request.getCurrencyCode();
				        		String portfolio = request.getPortfolio();
				        		
				        		if(StringUtils.isNullOrEmpty(amount) || StringUtils.isNullOrEmpty(asset) || StringUtils.isNullOrEmpty(currencyCode) || StringUtils.isNullOrEmpty(portfolio))
				        		{
				        			throw new AvaloqException("One of the request parameter is not set for the request", serviceErrors);
				        		}
				        		else
				        		{
				        			return true;
				        		}
				        	}
				        	else if(action.equals(TermDepositAction.BREAK_TERM_DEPOSIT))
				        	{
				        		String asset = request.getAsset();
				        		String portfolio = request.getPortfolio();
				        		
				        		if(StringUtils.isNullOrEmpty(asset)|| StringUtils.isNullOrEmpty(portfolio))
				        		{
				        			throw new AvaloqException("One of the request parameter is not set for the request", serviceErrors);
				        		}
				        		else
				        		{
				        			return true;
				        		}
				        	}
				        	else if(action.equals(TermDepositAction.VALIDATE_ADD_TERM_DEPOSIT))
				        	{
				        		String amount = request.getAmount();
				        		String asset = request.getAsset();
				        		String currencyCode = request.getCurrencyCode();
				        		String portfolio = request.getPortfolio();
				        		
				        		if(StringUtils.isNullOrEmpty(amount) || StringUtils.isNullOrEmpty(asset) || StringUtils.isNullOrEmpty(currencyCode) || StringUtils.isNullOrEmpty(portfolio))
				        		{
				        			throw new AvaloqException("One of the request parameter is not set for the request", serviceErrors);
				        		}
				        		else
				        		{
				        			return true;
				        		}
				        	}
				        	else if(action.equals(TermDepositAction.UPDATE_TERM_DEPOSIT))
				        	{
				        		String asset = request.getAsset();
				        		String renewMode = request.getRenewMode();
				        		String portfolio = request.getPortfolio();
				        		
				        		if(StringUtils.isNullOrEmpty(asset) || StringUtils.isNullOrEmpty(renewMode) || StringUtils.isNullOrEmpty(portfolio))
				        		{
				        			throw new AvaloqException("One of the request parameter is not set for the request", serviceErrors);
				        		}
				        		else
				        		{
				        			return true;
				        		}
				        	}
					        return true;
				        }
				        return true;
				    }
				});
		
		
		Mockito.when(termDepositService.validateBreakTermDeposit((TermDepositTrxRequest)anyObject(), (ServiceErrors)anyObject())).thenAnswer(new Answer<TermDepositTrx>() 
				{
				    @Override
				    public TermDepositTrx answer(InvocationOnMock invocation) throws Throwable 
				    {
				        Object[] arguments = invocation.getArguments();
				        if (arguments != null && arguments.length > 0 && arguments[0] != null)
				        {
				        	TermDepositTrxRequest request = (TermDepositTrxRequest) arguments[0];

				        	String asset = request.getAsset();
				        	String portfolio = request.getPortfolio();

				        	if(StringUtils.isNullOrEmpty(asset)|| StringUtils.isNullOrEmpty(portfolio))
				        	{
				        		throw new AvaloqException("One of the request parameter is not set for the request", serviceErrors);
				        	}
				        	else
				        	{
				        		TermDepositTrx termDepositTrx = new TermDepositTrxImpl();
				        		termDepositTrx.setInterestRate(BigDecimal.valueOf(3.5));
				        		termDepositTrx.setCurrPrpl(BigDecimal.valueOf(5000));
				        		termDepositTrx.setPercentTermElapsed(BigDecimal.valueOf(30));
				        		return termDepositTrx;
				        	}
				        }
				        return null;
				    }
				});
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
		/*request.setPortfolio("");
		boolean addTermDepositFail = termDepositService.termDeposit(TermDepositAction.ADD_TERM_DEPOSIT, request, serviceErrors);
		assertEquals(addTermDepositFail, true);*/
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
		assertNotNull(termDepositTrx.getInterestRate());
		assertNotNull(termDepositTrx.getCurrPrpl());
		assertNotNull(termDepositTrx.getPercentTermElapsed());
	}
}
