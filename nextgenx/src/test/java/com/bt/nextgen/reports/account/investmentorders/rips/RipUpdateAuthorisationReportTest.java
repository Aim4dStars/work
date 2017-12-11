package com.bt.nextgen.reports.account.investmentorders.rips;

import com.bt.nextgen.api.account.v2.model.BankAccountDto;
import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.bt.nextgen.api.order.model.OrderItemDto;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.bt.nextgen.api.regularinvestment.v2.service.RegularInvestmentDtoService;
import com.bt.nextgen.cms.service.CmsService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class RipUpdateAuthorisationReportTest {

    @InjectMocks
    private RipUpdateAuthorisationReport ripUpdateAuthorisationReport;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private CmsService cmsService;

    @Mock
    private OptionsService optionsService;

    @Mock
    private RegularInvestmentDtoService regularInvestmentService;

    @Mock
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    private static final String DECLARATION = "DS-IP-0080";
    private static final String CANCEL_DECLARATION = "DS-IP-0090";
    private static final String SUSPEND_DECLARATION = "DS-IP-0091";
    private static final String RENEW_DECLARATION = "DS-IP-0092";

    private static final String SUPER_DECLARATION = "DS-IP-0181";
    private static final String SUPER_CANCEL_DECLARATION = "DS-IP-0182";
    private static final String SUPER_SUSPEND_DECLARATION = "DS-IP-0183";
    private static final String SUPER_RENEW_DECLARATION = "DS-IP-0184";

    @Before
    public void setup() {
        Mockito.when(cmsService.getContent(DECLARATION)).thenReturn(DECLARATION);
        Mockito.when(cmsService.getContent(CANCEL_DECLARATION)).thenReturn(CANCEL_DECLARATION);
        Mockito.when(cmsService.getContent(SUSPEND_DECLARATION)).thenReturn(SUSPEND_DECLARATION);
        Mockito.when(cmsService.getContent(RENEW_DECLARATION)).thenReturn(RENEW_DECLARATION);
        Mockito.when(cmsService.getContent(SUPER_DECLARATION)).thenReturn(SUPER_DECLARATION);
        Mockito.when(cmsService.getContent(SUPER_CANCEL_DECLARATION)).thenReturn(SUPER_CANCEL_DECLARATION);
        Mockito.when(cmsService.getContent(SUPER_SUSPEND_DECLARATION)).thenReturn(SUPER_SUSPEND_DECLARATION);
        Mockito.when(cmsService.getContent(SUPER_RENEW_DECLARATION)).thenReturn(SUPER_RENEW_DECLARATION);
        Mockito.when(accountIntegrationServiceFactory.getInstance(anyString())).thenReturn(accountIntegrationService);
    }

    @Test
    public void testGetGenericText() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();

        Assert.assertEquals("Client authorisation - regular investment plan",
                ripUpdateAuthorisationReport.getReportType(params, dataCollections));
    }

    @Test
    public void testGetReportTitle_whenTitleRequested_thenItMatchesTheCurrentStatus() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();

        String title = ripUpdateAuthorisationReport.getReportTitle(params, dataCollections);
        Assert.assertEquals("Your client authorisation for a regular investment plan", title);

        params.clear();
        params.put("action", "suspend");
        title = ripUpdateAuthorisationReport.getReportTitle(params, dataCollections);
        Assert.assertEquals("Your client authorisation for a suspension", title);

        params.clear();
        params.put("action", "cancel");
        title = ripUpdateAuthorisationReport.getReportTitle(params, dataCollections);
        Assert.assertEquals("Your client authorisation for a cancellation", title);

        params.clear();
        params.put("action", "activate");
        title = ripUpdateAuthorisationReport.getReportTitle(params, dataCollections);
        Assert.assertEquals("Your client authorisation for a renewal", title);

        params.clear();
        params.put("action", "not a real action");
        title = ripUpdateAuthorisationReport.getReportTitle(params, dataCollections);
        Assert.assertEquals("Your client authorisation for a regular investment plan", title);
    }

    @Test
    public void testGetDeclaration_whenDeclarationRequestedForInvestmentAccount_thenItMatchesTheCurrentAccountTypeAndStatus() {
        Mockito.when(
                optionsService.hasFeature(Mockito.any(OptionKey.class), Mockito.any(AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(true);

        Map<String, Object> params = new HashMap<>();
        params.put("account-id", EncodedString.fromPlainText("accountId").toString());
        String declaration = ripUpdateAuthorisationReport.getDeclaration(params);
        Assert.assertEquals(DECLARATION, declaration);

        params.clear();
        params.put("action", "suspend");
        params.put("account-id", EncodedString.fromPlainText("accountId").toString());
        declaration = ripUpdateAuthorisationReport.getDeclaration(params);
        Assert.assertEquals(SUSPEND_DECLARATION, declaration);

        params.clear();
        params.put("action", "cancel");
        params.put("account-id", EncodedString.fromPlainText("accountId").toString());
        declaration = ripUpdateAuthorisationReport.getDeclaration(params);
        Assert.assertEquals(CANCEL_DECLARATION, declaration);

        params.clear();
        params.put("action", "activate");
        params.put("account-id", EncodedString.fromPlainText("accountId").toString());
        declaration = ripUpdateAuthorisationReport.getDeclaration(params);
        Assert.assertEquals(RENEW_DECLARATION, declaration);

        params.clear();
        params.put("action", "not a real action");
        params.put("account-id", EncodedString.fromPlainText("accountId").toString());
        declaration = ripUpdateAuthorisationReport.getDeclaration(params);
        Assert.assertEquals(DECLARATION, declaration);
    }

    @Test
    public void testGetDeclaration_whenDeclarationRequestedForSuperAccount_thenItMatchesTheCurrentAccountTypeAndStatus() {
        Mockito.when(
                optionsService.hasFeature(Mockito.any(OptionKey.class), Mockito.any(AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(false);

        Map<String, Object> params = new HashMap<>();
        params.put("account-id", EncodedString.fromPlainText("accountId").toString());
        String declaration = ripUpdateAuthorisationReport.getDeclaration(params);
        Assert.assertEquals(SUPER_DECLARATION, declaration);

        params.clear();
        params.put("action", "suspend");
        params.put("account-id", EncodedString.fromPlainText("accountId").toString());
        declaration = ripUpdateAuthorisationReport.getDeclaration(params);
        Assert.assertEquals(SUPER_SUSPEND_DECLARATION, declaration);

        params.clear();
        params.put("action", "cancel");
        params.put("account-id", EncodedString.fromPlainText("accountId").toString());
        declaration = ripUpdateAuthorisationReport.getDeclaration(params);
        Assert.assertEquals(SUPER_CANCEL_DECLARATION, declaration);

        params.clear();
        params.put("action", "activate");
        params.put("account-id", EncodedString.fromPlainText("accountId").toString());
        declaration = ripUpdateAuthorisationReport.getDeclaration(params);
        Assert.assertEquals(SUPER_RENEW_DECLARATION, declaration);

        params.clear();
        params.put("action", "not a real action");
        params.put("account-id", EncodedString.fromPlainText("accountId").toString());
        declaration = ripUpdateAuthorisationReport.getDeclaration(params);
        Assert.assertEquals(SUPER_DECLARATION, declaration);
    }

    @Test
    public void testGetData_whenRipDetailsLoaded_thenReportDataCreated() {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", EncodedString.fromPlainText("accountId").toString());
        params.put("rip_id", "12345");

        Map<String, Object> dataCollections = new HashMap<>();

        BankAccountDto cashAccountDto = Mockito.mock(BankAccountDto.class);
        Mockito.when(cashAccountDto.getBsb()).thenReturn("123-456");
        Mockito.when(cashAccountDto.getAccountNumber()).thenReturn("78901234");

        RegularInvestmentDto ripDetailDto = Mockito.mock(RegularInvestmentDto.class);
        Mockito.when(ripDetailDto.getRipStatus()).thenReturn("Active");
        Mockito.when(ripDetailDto.getInvestmentAmount()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(ripDetailDto.getInvestmentStartDate()).thenReturn(new DateTime("2017-01-01"));
        Mockito.when(ripDetailDto.getInvestmentEndDate()).thenReturn(new DateTime("2017-02-02"));
        Mockito.when(ripDetailDto.getFrequency()).thenReturn("Monthly");
        Mockito.when(ripDetailDto.getCashAccountDto()).thenReturn(cashAccountDto);
        Mockito.when(ripDetailDto.getOrders()).thenReturn(new ArrayList<OrderItemDto>());

        Mockito.when(regularInvestmentService.find(Mockito.any(OrderGroupKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(ripDetailDto);

        WrapAccountDetail accountDetail = Mockito.mock(WrapAccountDetail.class);
        Mockito.when(accountDetail.getAccountName()).thenReturn("accountName");
        Mockito.when(accountDetail.getAccountNumber()).thenReturn("accountNumber");

        Mockito.when(
                accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(accountDetail);

        Collection<?> data = ripUpdateAuthorisationReport.getData(params, dataCollections);
        Assert.assertEquals(1, data.size());

        RegularInvestmentReportData reportData = (RegularInvestmentReportData) data.iterator().next();
        Assert.assertEquals("This regular investment plan is currently active", reportData.getStatus());
        Assert.assertFalse(reportData.getShowDepositDetails());
        Assert.assertTrue(reportData.getShowDateRange());
        Assert.assertEquals("From 01 Jan 2017 to 02 Feb 2017", reportData.getInvestmentDateRange());
        Assert.assertEquals("BSB 123-456 Account no. 78901234", reportData.getInvestmentCashAccount());
        Assert.assertEquals("Invest <b>$1,000.00</b> starting <b>01 Jan 2017</b>, monthly", reportData.getInvestmentDetails());
        Assert.assertEquals("$1,000.00", reportData.getInvestmentAmount());
        Assert.assertNotNull(reportData.getOrders());
    }
}
