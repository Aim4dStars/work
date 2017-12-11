package com.bt.nextgen.reports.account.movemoney;

import com.bt.nextgen.api.account.v3.util.AccountProductsHelper;
import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.api.movemoney.v2.model.PayeeDto;
import com.bt.nextgen.api.movemoney.v2.model.PaymentDto;
import com.bt.nextgen.api.movemoney.v2.util.TransactionReceiptHelper;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import net.sf.jasperreports.engine.Renderable;
import org.apache.commons.configuration.Configuration;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionReceiptReportTest {

    @InjectMocks
    private TransactionReceiptReport transactionReceiptReport;

    @Mock
    private CmsService cmsService;

    @Mock
    private TransactionReceiptHelper transactionReceiptHelper;

    @Mock
    private Configuration configuration;

    @Mock
    private AccountProductsHelper accountProductsHelper;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    private Map<String, Object> params;
    private Map<String, Object> dataCollections;

    @Before
    public void setUp() throws Exception {
        params = new HashMap<>();
        dataCollections = new HashMap<>();
        params.put("receipt-id", EncodedString.fromPlainText("123456").toString());
        params.put("account-id", EncodedString.fromPlainText("123456").toString());

        PaymentDto payment = mock(PaymentDto.class);
        PayeeDto fromPayer = mock(PayeeDto.class);
        PayeeDto toPayee = mock(PayeeDto.class);
        when(fromPayer.getAccountName()).thenReturn("From account");
        when(payment.getFromPayDto()).thenReturn(fromPayer);
        when(payment.getToPayeeDto()).thenReturn(toPayee);
        when(payment.getAmount()).thenReturn(BigDecimal.TEN);

        TransactionReceiptReportData data = new TransactionReceiptReportData(payment);
        when(data.getTransactionType()).thenReturn("Payment");

        when(transactionReceiptHelper.getReceiptData(anyString())).thenReturn(data);
        dataCollections.put("cashTransaction123456", data);

        WrapAccountDetail account = Mockito.mock(WrapAccountDetail.class);
        Mockito.when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        Mockito.when(accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class))).thenReturn(account);
        when(accountIntegrationServiceFactory.getInstance(anyString())).thenReturn(accountIntegrationService);
    }

    @Test
    public void getData_payment() throws Exception {
        List<TransactionReceiptReportData> data = (List<TransactionReceiptReportData>) transactionReceiptReport.getData(params, dataCollections);
        assertThat(data, is(notNullValue()));
        assertThat(data.get(0).getAmount(), is("$10.00"));
        assertThat(data.get(0).getFromPayerName(), is("From account"));
    }

    @Test
    public void getData_deposit() throws Exception {
        DepositDto deposit = mock(DepositDto.class);
        PayeeDto fromPayer = mock(PayeeDto.class);
        PayeeDto toPayee = mock(PayeeDto.class);
        when(fromPayer.getAccountName()).thenReturn("From account");
        when(deposit.getFromPayDto()).thenReturn(fromPayer);
        when(deposit.getToPayeeDto()).thenReturn(toPayee);
        when(deposit.getAmount()).thenReturn(BigDecimal.TEN);
        when(deposit.getEndRepeatNumber()).thenReturn("10");
        TransactionReceiptReportData receiptReportData = new TransactionReceiptReportData(deposit);
        dataCollections.put("cashTransaction", receiptReportData);
        when(transactionReceiptHelper.getReceiptData(anyString())).thenReturn(receiptReportData);

        List<TransactionReceiptReportData> data = (List<TransactionReceiptReportData>) transactionReceiptReport.getData(params, dataCollections);
        assertThat(data, is(notNullValue()));
        assertThat(data.get(0).getAmount(), is("$10.00"));
        assertThat(data.get(0).getFromPayerName(), is("From account"));
        assertThat(data.get(0).getEndRepeatNumber(), is(BigInteger.TEN));
        assertThat(data.get(0).getTransactionType(), is("Deposit"));
    }

    @Test
    public void getData_depositForDirectSimple() throws Exception {
        DepositDto deposit = mock(DepositDto.class);
        PayeeDto fromPayer = mock(PayeeDto.class);
        PayeeDto toPayee = mock(PayeeDto.class);
        when(fromPayer.getAccountName()).thenReturn("From account");
        when(deposit.getFromPayDto()).thenReturn(fromPayer);
        when(deposit.getToPayeeDto()).thenReturn(toPayee);
        when(deposit.getAmount()).thenReturn(BigDecimal.TEN);
        TransactionReceiptReportData receiptReportData = new TransactionReceiptReportData(deposit);
        dataCollections.put("cashTransaction", receiptReportData);
        when(transactionReceiptHelper.getReceiptData(anyString())).thenReturn(receiptReportData);
        when(accountProductsHelper.getSubscriptionType(Mockito.any(WrapAccountDetail.class), Mockito.any(ServiceErrors.class))).thenReturn("simple");

        List<TransactionReceiptReportData> data = (List<TransactionReceiptReportData>) transactionReceiptReport.getData(params, dataCollections);
        assertThat(data, is(notNullValue()));
        assertThat(data.get(0).getAmount(), is("$10.00"));
        assertThat(data.get(0).getFromPayerName(), is("From account"));
        assertThat(data.get(0).getTransactionType(), is("Investment"));
    }

    @Test
    public void getData_noTransaction() throws Exception {
        when(transactionReceiptHelper.getReceiptData(anyString())).thenReturn(null);
        List<TransactionReceiptReportData> result = (List<TransactionReceiptReportData>) transactionReceiptReport.getData(params, dataCollections);
        assertThat(result, is(notNullValue()));
        assertTrue(result.isEmpty());
    }

    @Test
    public void getReportType() throws Exception {
        assertThat(transactionReceiptReport.getReportType(params, dataCollections), is("Payment receipt"));
    }

    @Test
    public void getReportType_defaultValue() throws Exception {
        dataCollections.remove("cashTransaction123456");
        assertThat(transactionReceiptReport.getReportType(params, dataCollections), is("Transaction receipt"));
    }

    @Test
    public void getReportTitle() throws Exception {
        assertThat(transactionReceiptReport.getReportTitle(params, dataCollections), is("Payment receipt"));
    }

    @Test
    public void getFromToIcon() throws Exception {
        when(cmsService.getContent(anyString())).thenReturn("cms/rasterImage.png");
        when(configuration.getString(Mockito.anyString())).thenReturn("classpath:/");
        Renderable icon = transactionReceiptReport.getFromToIcon(null);
        Mockito.verify(cmsService).getContent(Mockito.eq("paymentFromToIcon"));
        MatcherAssert.assertThat(icon, notNullValue());
    }
}