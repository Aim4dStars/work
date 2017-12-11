package com.bt.nextgen.api.account.v1.controller;

import com.bt.nextgen.account.api.model.InvestmentValuationDto;
import com.bt.nextgen.api.account.v1.model.AccountBalanceDto;
import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.account.v1.model.DatedAccountKey;
import com.bt.nextgen.api.account.v1.model.PerformanceDto;
import com.bt.nextgen.api.account.v1.model.ValuationDto;
import com.bt.nextgen.api.account.v1.model.ValuationSummaryDto;
import com.bt.nextgen.api.account.v1.model.ValuationSummaryListDto;
import com.bt.nextgen.api.account.v1.model.WrapAccountDetailDto;
import com.bt.nextgen.api.account.v1.service.AccountBalanceDtoService;
import com.bt.nextgen.api.account.v1.service.AccountPerformanceDtoService;
import com.bt.nextgen.api.account.v1.service.ValuationDtoService;
import com.bt.nextgen.api.account.v1.service.WrapAccountDetailDtoService;
import com.bt.nextgen.api.account.v1.validation.WrapAccountDetailsDtoErrorMapper;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.security.api.service.PermissionAccountDtoService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class AccountApiControllerTest
{
	@InjectMocks
	private AccountApiController accountApiController;

	@Mock
	private ValuationDtoService valuationService;

	@Mock
	private WrapAccountDetailDtoService wrapAccountDetailService;

	@Mock
	private AccountPerformanceDtoService performanceService;

	@Mock
	private WrapAccountDetailsDtoErrorMapper wrapAccountDetailsDtoErrorMapper;

	@Mock
	private AccountBalanceDtoService accountBalanceDtoService;

	@Mock
	private PermissionAccountDtoService permissionAccountDtoService;

	@Mock
	private UserProfileService profileService;

	private MockHttpServletRequest mockHttpServletRequest;
	private MockHttpServletResponse mockHttpServletResponse;

	@Mock
	private static AnnotationMethodHandlerAdapter annotationMethodHandler;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{

		mockHttpServletRequest = new MockHttpServletRequest(RequestMethod.GET.name(), "/");
		mockHttpServletResponse = new MockHttpServletResponse();
		annotationMethodHandler = new AnnotationMethodHandlerAdapter();
		HttpMessageConverter[] messageConverters =
		{
			new MappingJackson2HttpMessageConverter()
		};
		annotationMethodHandler.setMessageConverters(messageConverters);

	}

	@Test
	public final void testGetValuation() throws Exception
	{

		ValuationSummaryDto cashManagement = new ValuationSummaryDto(new BigDecimal(100),
			new BigDecimal(100),
			new BigDecimal(100),
			new BigDecimal(100),
			new ArrayList <InvestmentValuationDto>());
		ValuationSummaryDto termDeposits = new ValuationSummaryDto(new BigDecimal(100),
			new BigDecimal(100),
			new BigDecimal(100),
			new BigDecimal(100),
			new ArrayList <InvestmentValuationDto>());
		ValuationSummaryDto managedPortfolios = new ValuationSummaryDto(new BigDecimal(100),
			new BigDecimal(100),
			new BigDecimal(100),
			new BigDecimal(100),
			new ArrayList <InvestmentValuationDto>());
		ValuationSummaryDto managedFunds = new ValuationSummaryDto(new BigDecimal(100),
			new BigDecimal(100),
			new BigDecimal(100),
			new BigDecimal(100),
			new ArrayList <InvestmentValuationDto>());

		ValuationSummaryListDto valuationSummaryList = new ValuationSummaryListDto(cashManagement,
			termDeposits,
			managedPortfolios,
			managedFunds);
		ValuationDto valuationDto = new ValuationDto(new DatedAccountKey("accountId", new DateTime()),
			new BigDecimal(100),
			new BigDecimal(100),
			"accountType",
			valuationSummaryList);

		Mockito.when(valuationService.find(Mockito.any(DatedAccountKey.class), Mockito.any(ServiceErrors.class)))
			.thenReturn(valuationDto);

		mockHttpServletRequest.setParameter(UriMappingConstants.EFFECTIVE_DATE_PARAMETER_MAPPING, "2015-03-11");
		mockHttpServletRequest.setParameter(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "account-id");

		mockHttpServletRequest.setRequestURI("/secure/api/v1_0/accounts/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C/valuation");
		mockHttpServletRequest.setMethod("GET");
		annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, accountApiController);

	}

	@Test
	public final void testGetAccount() throws Exception
	{
        WrapAccountDetailDto wrapAccountDetailDto = new WrapAccountDetailDto();
		Mockito.when(wrapAccountDetailService.find(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
.thenReturn(
                wrapAccountDetailDto);
		mockHttpServletRequest.setParameter(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "account-id");
		mockHttpServletRequest.setRequestURI("/secure/api/v1_0/accounts/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C");
		mockHttpServletRequest.setMethod("GET");
		annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, accountApiController);
	}

	@Test
	public final void testUpdate() throws Exception
	{
        WrapAccountDetailDto wrapAccountDetailDto = new WrapAccountDetailDto();
        Mockito.when(wrapAccountDetailService.update(Mockito.any(WrapAccountDetailDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(wrapAccountDetailDto);
		Mockito.when(profileService.isEmulating()).thenReturn(false);
		mockHttpServletRequest.setParameter(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "account-id");
		mockHttpServletRequest.setRequestURI("/secure/api/v1_0/accounts/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C/update");
		mockHttpServletRequest.setMethod("POST");
		annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, accountApiController);
	}

	@Test
	public final void testUpdateAccessDeniedException() throws Exception
	{
        WrapAccountDetailDto wrapAccountDetailDto = new WrapAccountDetailDto();
        Mockito.when(wrapAccountDetailService.update(Mockito.any(WrapAccountDetailDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(wrapAccountDetailDto);
		Mockito.when(profileService.isEmulating()).thenReturn(true);
		mockHttpServletRequest.setParameter(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "account-id");
		mockHttpServletRequest.setRequestURI("/secure/api/v1_0/accounts/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C/update");
		mockHttpServletRequest.setMethod("POST");

		try
		{
			annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, accountApiController);
		}
		catch (AccessDeniedException exception)
		{
			assert (true);
			return;
		}
		fail("AccessDeniedException Not Thrown");

	}

	@Test
	public final void testGetPortfolioPerformance() throws Exception
	{

		PerformanceDto performanceDto = new PerformanceDto();
		Mockito.when(performanceService.find(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
			.thenReturn(performanceDto);

		mockHttpServletRequest.setParameter(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "account-id");
		mockHttpServletRequest.setRequestURI("/secure/api/v1_0/accounts/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C/performance");
		mockHttpServletRequest.setMethod("GET");
		annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, accountApiController);

	}

	@Test
	public final void testGetAccountBalance() throws Exception
	{

		AccountBalanceDto accountBalanceDto = new AccountBalanceDto();
		Mockito.when(accountBalanceDtoService.find(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
			.thenReturn(accountBalanceDto);

		mockHttpServletRequest.setParameter(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "account-id");
		mockHttpServletRequest.setRequestURI("/secure/api/v1_0/accounts/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C/balance");
		mockHttpServletRequest.setMethod("GET");
		annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, accountApiController);

	}

}
