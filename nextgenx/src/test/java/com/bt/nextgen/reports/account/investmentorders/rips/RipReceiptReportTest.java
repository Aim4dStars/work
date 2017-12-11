package com.bt.nextgen.reports.account.investmentorders.rips;

import com.bt.nextgen.api.account.v2.model.BankAccountDto;
import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.bt.nextgen.api.order.model.OrderItemDto;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.bt.nextgen.api.regularinvestment.v2.service.RegularInvestmentDtoService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.joda.time.DateTime;
import org.junit.Assert;
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
public class RipReceiptReportTest {

    @InjectMocks
    private RipReceiptReport ripReceiptReport;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private RegularInvestmentDtoService regularInvestmentService;

    @Mock
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    @Test
    public void testGetGenericText() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();

        Assert.assertEquals("Receipt - regular investment plan", ripReceiptReport.getReportType(params, dataCollections));
    }

    @Test
    public void testGetReportTitle_whenTitleRequsted_thenItMatchesTheCurrentStatus() {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", EncodedString.fromPlainText("accountId").toString());
        params.put("rip_id", "12345");

        Map<String, Object> dataCollections = new HashMap<>();

        RegularInvestmentDto ripDetailDto = Mockito.mock(RegularInvestmentDto.class);
        Mockito.when(regularInvestmentService.find(Mockito.any(OrderGroupKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(ripDetailDto);

        String title = ripReceiptReport.getReportTitle(params, dataCollections);
        Assert.assertEquals("Your regular investment plan was successfully submitted", title);

        Mockito.when(ripDetailDto.getRipStatus()).thenReturn("Cancelled");
        Mockito.when(regularInvestmentService.find(Mockito.any(OrderGroupKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(ripDetailDto);

        title = ripReceiptReport.getReportTitle(params, dataCollections);
        Assert.assertEquals("Your regular investment plan was successfully cancelled", title);

        Mockito.when(ripDetailDto.getRipStatus()).thenReturn("Suspended");
        Mockito.when(regularInvestmentService.find(Mockito.any(OrderGroupKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(ripDetailDto);

        title = ripReceiptReport.getReportTitle(params, dataCollections);
        Assert.assertEquals("Your regular investment plan was successfully suspended", title);

        Mockito.when(ripDetailDto.getRipStatus()).thenReturn("Not a real status");
        Mockito.when(regularInvestmentService.find(Mockito.any(OrderGroupKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(ripDetailDto);

        title = ripReceiptReport.getReportTitle(params, dataCollections);
        Assert.assertEquals("Your regular investment plan was successfully submitted", title);
    }

    @Test
    public void testGetData_whenRipDetailsLoaded_thenReportDataCreated() {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", EncodedString.fromPlainText("accountId").toString());
        params.put("rip_id", "12345");

        Map<String, Object> dataCollections = new HashMap<>();

        BankAccountDto cashAccountDto = Mockito.mock(BankAccountDto.class);
        Mockito.when(cashAccountDto.getBsb()).thenReturn("123456");
        Mockito.when(cashAccountDto.getAccountNumber()).thenReturn("78901234");

        RegularInvestmentDto ripDetailDto = Mockito.mock(RegularInvestmentDto.class);
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

        Mockito.when(accountIntegrationServiceFactory.getInstance(anyString())).thenReturn(accountIntegrationService);

        Collection<?> data = ripReceiptReport.getData(params, dataCollections);
        Assert.assertEquals(1, data.size());

        RegularInvestmentReportData reportData = (RegularInvestmentReportData) data.iterator().next();
        Assert.assertFalse(reportData.getShowDepositDetails());
        Assert.assertTrue(reportData.getShowDateRange());
        Assert.assertEquals("From 01 Jan 2017 to 02 Feb 2017", reportData.getInvestmentDateRange());
        Assert.assertEquals("BSB 123-456 Account no. 78901234", reportData.getInvestmentCashAccount());
        Assert.assertEquals("Invest <b>$1,000.00</b> starting <b>01 Jan 2017</b>, monthly", reportData.getInvestmentDetails());
        Assert.assertEquals("$1,000.00", reportData.getInvestmentAmount());
        Assert.assertNotNull(reportData.getOrders());
    }

}
