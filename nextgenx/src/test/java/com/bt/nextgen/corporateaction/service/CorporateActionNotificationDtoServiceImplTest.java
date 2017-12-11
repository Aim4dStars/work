package com.bt.nextgen.corporateaction.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.access.AccessDeniedException;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionNotificationDto;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionDirectAccountService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionNotificationDtoServiceImpl;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionSecurityExchangeType;
import com.bt.nextgen.web.controller.cash.util.Attribute;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionNotificationDtoServiceImplTest {
	@InjectMocks
	private CorporateActionNotificationDtoServiceImpl actionNotificationDtoService;

	@Mock
	private CorporateActionIntegrationService corporateActionIntegrationService;

	@Mock
	private AccountIntegrationService accountIntegrationService;

	@Mock
	private BrokerHelperService brokerHelperService;

	@Mock
	private CorporateActionDirectAccountService corporateActionDirectAccountService;

	@Test
	public void testSearch_whenThereAreAccountIds_thenReturnResults() {
		List<ApiSearchCriteria> criteria = new ArrayList<>();
		criteria.add(new ApiSearchCriteria(Attribute.START_DATE, ApiSearchCriteria.SearchOperation.EQUALS, "2015-03-22",
				ApiSearchCriteria.OperationType.DATE));
		criteria.add(new ApiSearchCriteria(Attribute.END_DATE, ApiSearchCriteria.SearchOperation.EQUALS, "2017-03-22",
				ApiSearchCriteria.OperationType.DATE));

		CorporateActionImpl corporateAction = new CorporateActionImpl();
		corporateAction.setNotificationCnt(new BigInteger("5"));
		corporateAction.setCorporateActionSecurityExchangeType(CorporateActionSecurityExchangeType.DE_STAPLE);
		Mockito.when(corporateActionIntegrationService.getCountForPendingCorporateEvents
				(Matchers.anyList(), (DateTime) Matchers.anyObject(), (DateTime) Matchers.anyObject(),
						(ServiceErrors) Matchers.anyObject())).thenReturn(corporateAction);
		Mockito.when(brokerHelperService.isDirectInvestor((WrapAccount) Matchers.anyObject(),
				(ServiceErrors) Matchers.anyObject())).thenReturn(true);
		List<WrapAccount> accounts = new ArrayList<>();
		WrapAccountImpl wrapAccount = new WrapAccountImpl();
		accounts.add(wrapAccount);
		Mockito.when(corporateActionDirectAccountService.getDirectAccounts()).thenReturn(accounts);
		List<CorporateActionNotificationDto> resultDto = actionNotificationDtoService.search(criteria, new ServiceErrorsImpl());
		CorporateActionNotificationDto notificationDto = resultDto.get(0);
		Assert.assertNotNull(notificationDto);
		Assert.assertEquals(corporateAction.getNotificationCnt(), notificationDto.getNotificationCount());
	}

	@Test(expected = AccessDeniedException.class)
	public void testSearch_whenThereAreNoAccountIds_thenThrowException() {
		List<ApiSearchCriteria> criteria = new ArrayList<>();
		Mockito.when(corporateActionDirectAccountService.getDirectAccounts()).thenReturn(new ArrayList<WrapAccount>());
		actionNotificationDtoService.search(criteria, new ServiceErrorsImpl());
	}
}