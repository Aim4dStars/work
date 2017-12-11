package com.bt.nextgen.serviceops.service;

import com.bt.nextgen.api.draftaccount.model.SendEmailDto;
import com.bt.nextgen.api.draftaccount.service.SendEmailService;
import com.bt.nextgen.api.tracking.service.InvestorStatusServiceForTechnicalSupport;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.group.customer.BankingCustomerIdentifier;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import ns.btfin_com.product.panorama.onboardingservice.onboardingresponse.v3_0.StatusTypeCode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResendRegistrationEmailServiceImplTest
{
	@InjectMocks
	private ResendRegistrationEmailServiceImpl resendRegistrationEmailService;

	@Mock
	private InvestorStatusServiceForTechnicalSupport investorStatusServiceForTechnicalSupport;

	@Mock
	private SendEmailService sendEmailService;

	@Mock
	private BrokerHelperService brokerHelperService;

	private final String clientId = "clientId";
	private final String gcmId = "gcmId";

	
	@Test
	public void shouldGetStatusAsErrorWhenNoClientApplicationExistForClient()
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		OnboardingParty party = new OnboardingParty(1, (long)12345, gcmId);
		Mockito.when(investorStatusServiceForTechnicalSupport.getInvestorOnboardingPartyDetails(gcmId)).thenReturn(party);
		String status = resendRegistrationEmailService.resendRegistrationEmailForInvestor(clientId, gcmId, Attribute.INVESTOR, serviceErrors);
		assertEquals(status, Attribute.ERROR_MESSAGE);
		assertTrue(serviceErrors.hasErrors());
	}

	@Test
	public void shouldGetErrorStatusWhenSendEmailStatusIsError()
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		OnboardingParty party = new OnboardingParty(1, (long)12345, gcmId);
		Mockito.when(investorStatusServiceForTechnicalSupport.getInvestorOnboardingPartyDetails(gcmId)).thenReturn(party);
        Broker broker = getBroker("id1");
		Mockito.when(brokerHelperService.getAdviserForInvestor(any(BankingCustomerIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
		ClientApplication clientApplication = new ClientApplication();
		Mockito.when(investorStatusServiceForTechnicalSupport.getClientApplicationDetailsForOnboardingApplicationId(any(OnboardingApplicationKey.class),
			anyCollection()))
			.thenReturn(clientApplication);
		SendEmailDto sendEmailDto = new SendEmailDto(party.getOnboardingApplicationId(), clientId);
		Mockito.when(sendEmailService.submit(any(SendEmailDto.class), any(ServiceErrors.class))).thenReturn(sendEmailDto);
		String status = resendRegistrationEmailService.resendRegistrationEmailForInvestor(clientId, gcmId, Attribute.INVESTOR, serviceErrors);
		assertEquals(status, Attribute.ERROR_MESSAGE);
	}


	@Test
	public void shouldGetSuccessMessageIfUpdateIsSuccefullyDone()
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		OnboardingParty party = new OnboardingParty(1, (long)12345, gcmId);
		Mockito.when(investorStatusServiceForTechnicalSupport.getInvestorOnboardingPartyDetails(gcmId)).thenReturn(party);

        
		ClientApplication clientApplication = new ClientApplication();
		Mockito.when(investorStatusServiceForTechnicalSupport.getClientApplicationDetailsForOnboardingApplicationId(any(OnboardingApplicationKey.class),
			anyCollection()))
			.thenReturn(clientApplication);
		
		Broker broker = getBroker("id1");
		Mockito.when(brokerHelperService.getAdviserForInvestor(any(BankingCustomerIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
		
		Mockito.when(sendEmailService.sendEmailFromServiceOpsDesktopForInvestor(clientId, broker.getKey().getId(), Attribute.INVESTOR, serviceErrors)).thenReturn(StatusTypeCode.SUCCESS.value());
		 doAnswer(new Answer<Object>() {
	            @Override
                public Object answer(InvocationOnMock invocation) {
	                Object[] args = invocation.getArguments();
				return "called with arguments: " + args;
	            }
		}).when(investorStatusServiceForTechnicalSupport).updatePartyStatusWhenResendRegistrationCodeSuccess(any(OnboardingParty.class));
		String status = resendRegistrationEmailService.resendRegistrationEmailForInvestor(clientId, gcmId, Attribute.INVESTOR, serviceErrors);
		assertEquals(status, Attribute.SUCCESS_MESSAGE);
	}
	
	@Test
	public void shouldCallMethodWithoutClientApplicationIdWhenNoPartyExistForClient()
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Broker broker = getBroker("id1");
		Mockito.when(brokerHelperService.getAdviserForInvestor(any(BankingCustomerIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(broker);
		resendRegistrationEmailService.resendRegistrationEmailForInvestor(clientId, gcmId, Attribute.INVESTOR, serviceErrors);
		verify(sendEmailService, times(1)).sendEmailFromServiceOpsDesktopForInvestor(anyString(), anyString(), anyString(), any(ServiceErrors.class));
	}
	
	@Test
	public void shouldReturnSuccessWhensendEmailFromServiceOpsDesktopReturnSuccess()
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		Broker broker = getBroker("id1");
		Mockito.when(brokerHelperService.getAdviserForInvestor(any(BankingCustomerIdentifier.class), any(ServiceErrors.class)))
		.thenReturn(broker);
		Mockito.when(sendEmailService.sendEmailFromServiceOpsDesktopForInvestor(clientId, broker.getKey().getId(), Attribute.INVESTOR, serviceErrors)).thenReturn(StatusTypeCode.SUCCESS.value());
		String status = resendRegistrationEmailService.resendRegistrationEmailForInvestor(clientId, gcmId, Attribute.INVESTOR, serviceErrors);
		assertEquals(status, Attribute.SUCCESS_MESSAGE);
	}
	
	@Test
	public void shouldReturnErrorWhensendEmailFromServiceOpsDesktopResultNotSuccess()
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		Broker broker = getBroker("id1");
		Mockito.when(brokerHelperService.getAdviserForInvestor(any(BankingCustomerIdentifier.class), any(ServiceErrors.class)))
		.thenReturn(broker);
		Mockito.when(sendEmailService.sendEmailFromServiceOpsDesktopForInvestor(clientId, broker.getKey().getId(), Attribute.INVESTOR, serviceErrors)).thenReturn(StatusTypeCode.ERROR.value());
		String status = resendRegistrationEmailService.resendRegistrationEmailForInvestor(clientId, gcmId, Attribute.INVESTOR, serviceErrors);
		assertEquals(status, Attribute.ERROR_MESSAGE);
	}

	private Broker getBroker(final String id)
	{
        Broker broker = Mockito.mock(Broker.class);
        when(broker.getKey()).thenReturn(BrokerKey.valueOf(id));
        return broker;
	}
	
	@Test
	public void shouldReturnSuccessWhensendEmailForAdviserReturnSuccess()
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		Mockito.when(sendEmailService.sendEmailFromServiceOpsDesktopForAdviser(gcmId, Attribute.INVESTOR, serviceErrors)).thenReturn(StatusTypeCode.SUCCESS.value());
		String status = resendRegistrationEmailService.resendRegistrationEmailForAdviser(clientId, gcmId, Attribute.INVESTOR, serviceErrors);
		assertEquals(status, Attribute.SUCCESS_MESSAGE);
	}
	
	@Test
	public void shouldNotReturnSuccessWhensendEmailForAdviserNotReturnSuccess()
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		Mockito.when(sendEmailService.sendEmailFromServiceOpsDesktopForAdviser(gcmId, Attribute.INVESTOR, serviceErrors)).thenReturn(StatusTypeCode.ERROR.value());
		String status = resendRegistrationEmailService.resendRegistrationEmailForAdviser(clientId, gcmId, Attribute.INVESTOR, serviceErrors);
		assertEquals(status, StatusTypeCode.ERROR.value());
		
		Mockito.when(sendEmailService.sendEmailFromServiceOpsDesktopForAdviser(gcmId, Attribute.INVESTOR, serviceErrors)).thenReturn(StatusTypeCode.WARNING.value());
		status = resendRegistrationEmailService.resendRegistrationEmailForAdviser(clientId, gcmId, Attribute.INVESTOR, serviceErrors);
		assertEquals(status, StatusTypeCode.WARNING.value());
	}
}
