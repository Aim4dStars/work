package com.bt.nextgen.reports.account.movemoney.deposit;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.movemoney.v3.model.DepositDto;
import com.bt.nextgen.api.movemoney.v3.model.DepositKey;
import com.bt.nextgen.api.movemoney.v3.model.PayeeDto;
import com.bt.nextgen.api.movemoney.v3.service.DepositDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.reports.account.movemoney.ReceiptReportData;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DepositReceiptReportTest {

    @InjectMocks
    private DepositReceiptReport depositReceiptReport;

    @Mock
    private DepositDtoService depositDtoService;

    @Mock
    private ContentDtoService contentService;

    private DepositDto depositDto = new DepositDto();
    private String accountId = "975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0";
    private DepositKey depositKey = new DepositKey("depositId");

    private Map<String, Object> params = new HashMap<>();
    private Map<String, Object> dataCollections = new HashMap<>();

    @Before
    public void setup() {
        depositDto = mockDepositDtoService();
        when(depositDtoService.find((Matchers.any(DepositKey.class)), any(ServiceErrorsImpl.class))).thenReturn(depositDto);
    }

    private DepositDto mockDepositDtoService() {
        PayeeDto payeeDto = new PayeeDto();
        payeeDto.setAccountId("123456");
        payeeDto.setCode("012345");
        payeeDto.setAccountName("name");

        depositDto.setKey(depositKey);
        depositDto.setAccountKey(new AccountKey(accountId));
        depositDto.setAmount(new BigDecimal(1000));
        depositDto.setDepositType("Personal");
        depositDto.setTransactionDate(new DateTime("2016-08-17"));
        depositDto.setFromPayDto(payeeDto);

        return depositDto;
    }

    @Test
    public void testDepositReceiptReport() {
        params.put("deposit-id", "depositId");
        Collection<?> result = depositReceiptReport.getData(params, dataCollections);
        Assert.assertNotNull(result);

        ReceiptReportData depositReceiptReportData = (ReceiptReportData) result.iterator().next();
        Assert.assertEquals(depositReceiptReportData.getDepositId(), depositDto.getKey().getDepositId());
        Assert.assertEquals(depositReceiptReportData.getDepositType(), depositDto.getDepositType());
        Assert.assertEquals(depositReceiptReportData.getFromPayerName(), depositDto.getFromPayDto().getAccountName());
        Assert.assertEquals(depositReceiptReportData.getFromPayerAccount(), depositDto.getFromPayDto().getAccountId());
        Assert.assertEquals(depositReceiptReportData.getFromPayerBsb(), "012-345");
        Assert.assertEquals(depositReceiptReportData.getAmount(), "$1,000.00");
        Assert.assertEquals(depositReceiptReportData.getTransactionDate(), "17 Aug 2016");
    }

    @Test
    public void testDepositReceiptReport_whenRegularEndDate_thenRegular() {
        params.put("deposit-id", "depositId");
        params.put("report-type", "regular");
        depositDto.setRepeatEndDate(new DateTime("2017-08-17"));
        depositDto.setFrequency("Monthly");

        Collection<?> result = depositReceiptReport.getData(params, dataCollections);
        Assert.assertNotNull(result);

        ReceiptReportData depositReceiptReportData = (ReceiptReportData) result.iterator().next();
        Assert.assertEquals(depositReceiptReportData.getDepositId(), depositDto.getKey().getDepositId());
        Assert.assertEquals(depositReceiptReportData.getDepositType(), depositDto.getDepositType());
        Assert.assertEquals(depositReceiptReportData.getFromPayerName(), depositDto.getFromPayDto().getAccountName());
        Assert.assertEquals(depositReceiptReportData.getFromPayerAccount(), depositDto.getFromPayDto().getAccountId());
        Assert.assertEquals(depositReceiptReportData.getFrequency(), depositDto.getFrequency());
        Assert.assertEquals(depositReceiptReportData.getFromPayerBsb(), "012-345");
        Assert.assertEquals(depositReceiptReportData.getAmount(), "$1,000.00");
        Assert.assertEquals(depositReceiptReportData.getTransactionDate(), "17 Aug 2016");
        Assert.assertEquals(depositReceiptReportData.getRepeatEndDate(), "17 Aug 2017");
    }

    @Test
    public void testDepositReceiptReport_whenRegularAndNoEndDate_thenRegular() {
        params.put("deposit-id", "depositId");
        params.put("report-type", "regular");
        depositDto.setRepeatEndDate(null);
        depositDto.setFrequency("Monthly");
        Collection<?> result = depositReceiptReport.getData(params, dataCollections);

        Assert.assertNotNull(result);
        ReceiptReportData depositReceiptReportData = (ReceiptReportData) result.iterator().next();
        Assert.assertEquals(depositReceiptReportData.getRepeatEndDate(), "No end date");
    }

    @Test
    public void testGetReportType_whenNoReportType_thenReportTypeOneOff() {
        String reportType = depositReceiptReport.getReportType(params, dataCollections);
        assertNotNull(reportType);
        assertEquals(reportType, "One-off contribution");
    }

    @Test
    public void testGetReportType_whenReportTypeIsNotEmpty_thenReportTypeRegular() {
        params.put("report-type", "regular");
        String reportType = depositReceiptReport.getReportType(params, dataCollections);
        assertNotNull(reportType);
        assertEquals(reportType, "Regular contribution");
    }

    @Test
    public void testGetReportType_whenReportTypeIsEmpty_thenReportTypeOneOff() {
        params.put("report-type", "");
        String reportType = depositReceiptReport.getReportType(params, dataCollections);
        assertNotNull(reportType);
        assertEquals(reportType, "One-off contribution");
    }

    @Test
    public void testGetReportType_whenReportTypeIsNotRegular_thenReportTypeOneOff() {
        params.put("report-type", "test");
        String reportType = depositReceiptReport.getReportType(params, dataCollections);
        assertNotNull(reportType);
        assertEquals(reportType, "One-off contribution");
    }

    @Test
    public void testGetPaymentDueLabel_whenNoReportType_thenPaymentLabel() {
        String reportType = depositReceiptReport.getPaymentDueLabel(params);
        assertNotNull(reportType);
        assertEquals(reportType, "Payment due");
    }

    @Test
    public void testGetPaymentDueLabel_whenReportTypeIsNotEmpty_thenRegularLabel() {
        params.put("report-type", "regular");
        String reportType = depositReceiptReport.getPaymentDueLabel(params);
        assertNotNull(reportType);
        assertEquals(reportType, "Regular payment due");
    }

    @Test
    public void testGetPaymentDueLabel_whenReportTypeIsEmpty_thenPaymentLabel() {
        params.put("report-type", "");
        String reportType = depositReceiptReport.getPaymentDueLabel(params);
        assertNotNull(reportType);
        assertEquals(reportType, "Payment due");
    }

    @Test
    public void testGetPaymentDueLabel_whenReportTypeIsNotRegular_thenPaymentLabel() {
        params.put("report-type", "test");
        String reportType = depositReceiptReport.getPaymentDueLabel(params);
        assertNotNull(reportType);
        assertEquals(reportType, "Payment due");
    }

    @Test
    public void testGetDeclaration() {
        ContentDto content = new ContentDto(new ContentKey("MockKey"), "MockString");
        when(contentService.find((any(ContentKey.class)), any(ServiceErrorsImpl.class))).thenReturn(content);
        String declaration = depositReceiptReport.getDeclaration();
        assertEquals("MockString", declaration);
    }

    @Test
    public void testGetInfoMessage() {
        ContentDto content = new ContentDto(new ContentKey("InfoKey"), "InfoMessage");
        when(contentService.find((any(ContentKey.class)), any(ServiceErrorsImpl.class))).thenReturn(content);
        String infoMessage = depositReceiptReport.getInfoMessage();
        assertEquals("InfoMessage", infoMessage);
    }
}
