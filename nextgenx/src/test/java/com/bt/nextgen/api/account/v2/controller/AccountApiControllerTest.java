package com.bt.nextgen.api.account.v2.controller;

import org.junit.Before;
import org.junit.Ignore;
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

import com.bt.nextgen.api.account.v2.model.AccountBalanceDto;
import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import com.bt.nextgen.api.account.v2.model.PerformanceDto;
import com.bt.nextgen.api.account.v2.model.PerformanceReportDto;
import com.bt.nextgen.api.account.v2.model.WrapAccountDetailDto;
import com.bt.nextgen.api.account.v2.service.AccountBalanceDtoService;
import com.bt.nextgen.api.account.v2.service.AccountPerformanceDtoService;
import com.bt.nextgen.api.account.v2.service.AccountPeriodPerformanceDtoService;
import com.bt.nextgen.api.account.v2.service.WrapAccountDetailDtoService;
import com.bt.nextgen.api.account.v2.service.valuation.ValuationDtoService;
import com.bt.nextgen.api.account.v2.validation.WrapAccountDetailsDtoErrorMapper;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.security.api.service.PermissionAccountDtoService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;

import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
@Ignore("dodgy mock uri method handler doesn't have full spring capabilities")
public class AccountApiControllerTest {
    @InjectMocks
    private AccountApiController accountApiController;

    @Mock
    private ValuationDtoService valuationService;

    @Mock
    private WrapAccountDetailDtoService wrapAccountDetailDtoService;

    @Mock
    private AccountPerformanceDtoService performanceService;

    @Mock
    private AccountPeriodPerformanceDtoService performanceReportDtoService;

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

    @Before
    public void setUp() throws Exception {

        mockHttpServletRequest = new MockHttpServletRequest(RequestMethod.GET.name(), "/");
        mockHttpServletResponse = new MockHttpServletResponse();
        annotationMethodHandler = new AnnotationMethodHandlerAdapter();
        HttpMessageConverter[] messageConverters = { new MappingJackson2HttpMessageConverter() };
        annotationMethodHandler.setMessageConverters(messageConverters);

    }

    @Test
    public final void testGetAccount() throws Exception {
        WrapAccountDetailDto wrapAccountDetailDto = new WrapAccountDetailDto();
        Mockito.when(wrapAccountDetailDtoService.find(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(wrapAccountDetailDto);
        mockHttpServletRequest.setParameter(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "account-id");
        mockHttpServletRequest.setRequestURI("/secure/api/accounts/v2_0/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C");
        mockHttpServletRequest.setMethod("GET");
        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, accountApiController);
    }

    @Test
    public final void testUpdate() throws Exception {
        WrapAccountDetailDto wrapAccountDetailDto = new WrapAccountDetailDto();
        Mockito.when(
                wrapAccountDetailDtoService.update(Mockito.any(WrapAccountDetailDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(wrapAccountDetailDto);
        Mockito.when(profileService.isEmulating()).thenReturn(false);
        mockHttpServletRequest.setParameter(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "account-id");
        mockHttpServletRequest.setRequestURI("/secure/api/accounts/v2_0/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C");
        mockHttpServletRequest.setMethod("POST");
        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, accountApiController);
    }

    @Test
    public final void testUpdateAccessDeniedException() throws Exception {
        WrapAccountDetailDto wrapAccountDetailDto = new WrapAccountDetailDto();
        Mockito.when(
                wrapAccountDetailDtoService.update(Mockito.any(WrapAccountDetailDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(wrapAccountDetailDto);
        Mockito.when(profileService.isEmulating()).thenReturn(true);
        mockHttpServletRequest.setParameter(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "account-id");
        mockHttpServletRequest.setRequestURI("/secure/api/accounts/v2_0/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C");
        mockHttpServletRequest.setMethod("POST");

        try {
            annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, accountApiController);
        } catch (AccessDeniedException exception) {
            assert (true);
            return;
        }
        fail("AccessDeniedException Not Thrown");

    }

    @Test
    public final void testGetPortfolioPerformance() throws Exception {

        PerformanceDto performanceDto = new PerformanceDto();
        Mockito.when(performanceService.find(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                performanceDto);

        mockHttpServletRequest.setParameter(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "account-id");
        mockHttpServletRequest
                .setRequestURI("/secure/api/accounts/v2_0/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C/performance");
        mockHttpServletRequest.setMethod("GET");
        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, accountApiController);

    }

    @Test
    public final void testGetPortfolioPerformanceReport() throws Exception {

        PerformanceReportDto performanceDto = new PerformanceReportDto();
        Mockito.when(performanceReportDtoService.find(Mockito.any(DateRangeAccountKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                performanceDto);

        mockHttpServletRequest.setParameter(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "account-id");
        mockHttpServletRequest.setParameter(UriMappingConstants.START_DATE_PARAMETER_MAPPING, "2015-04-01");
        mockHttpServletRequest.setParameter(UriMappingConstants.END_DATE_PARAMETER_MAPPING, "2015-08-01");

        mockHttpServletRequest
                .setRequestURI("/secure/api/accounts/v2_0/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C/performance-report");
        mockHttpServletRequest.setMethod("GET");
        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, accountApiController);
    }

    @Test
    public final void testGetAccountBalance() throws Exception {

        AccountBalanceDto accountBalanceDto = new AccountBalanceDto();
        Mockito.when(accountBalanceDtoService.find(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                accountBalanceDto);

        mockHttpServletRequest.setParameter(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "account-id");
        mockHttpServletRequest
                .setRequestURI("/secure/api/accounts/v2_0/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C/balance");
        mockHttpServletRequest.setMethod("GET");
        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, accountApiController);

    }


    @Test
    public final void testGetAccountBalances() throws Exception {

        AccountBalanceDto accountBalanceDto = new AccountBalanceDto();
        Mockito.when(accountBalanceDtoService.find(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                accountBalanceDto);

        mockHttpServletRequest.setParameter(UriMappingConstants.ACCOUNT_ID_LIST_URI_MAPPING, "account-id-list");
        mockHttpServletRequest
                .setRequestURI("/secure/api/accounts/v2_0/CC70F19A491DCC514CFDA7CDCFB92E5AEF81407A445CFE9F,0C821C970A8CE7F4C4B3FF60215B2847CF093C666DCD0381/balances");
        mockHttpServletRequest.setMethod("GET");
        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, accountApiController);

    }
}
