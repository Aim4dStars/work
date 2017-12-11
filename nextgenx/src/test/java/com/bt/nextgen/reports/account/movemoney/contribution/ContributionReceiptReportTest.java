package com.bt.nextgen.reports.account.movemoney.contribution;

import com.bt.nextgen.api.movemoney.v3.model.PayeeDto;
import com.bt.nextgen.api.transaction.model.TransactionDto;
import com.bt.nextgen.api.transaction.model.TransactionKey;
import com.bt.nextgen.api.transaction.service.TransactionDtoService;
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
public class ContributionReceiptReportTest {

    @InjectMocks
    private ContributionReceiptReport contributionReceiptReport;

    @Mock
    private TransactionDtoService transactionService;

    @Mock
    private ContentDtoService contentService;;

    private TransactionDto transactionDto = new TransactionDto();

    private Map<String, Object> paramsMap = new HashMap<>();
    private Map<String, Object> dataCollections = new HashMap<>();

    @Before
    public void setup() {
        transactionDto = mockTransactionDtoService();
        ContentDto content = new ContentDto(new ContentKey("MockKey"), "MockString");
        when(contentService.find((any(ContentKey.class)), any(ServiceErrorsImpl.class))).thenReturn(content);
        when(transactionService.find((Matchers.any(TransactionKey.class)), any(ServiceErrorsImpl.class)))
                .thenReturn(transactionDto);
    }

    private TransactionDto mockTransactionDtoService() {
        PayeeDto payeeDto = new PayeeDto();
        payeeDto.setAccountId("123456");
        payeeDto.setCode("012345");
        payeeDto.setAccountName("name");

        transactionDto.setStordPosId("078F653237B8A38CC39C2AACD451BE37F611F7B06204C2DA");
        transactionDto.setFirstPayment(new DateTime("2017-01-17"));
        transactionDto.setNetAmount(new BigDecimal(1000));
        transactionDto.setContributionType("Personal");
        transactionDto.setPayer("name");
        transactionDto.setPayerBsb("012345");
        transactionDto.setPayerAccount("123456");
        return transactionDto;
    }

    @Test
    public void testDepositReceiptReport() {
        paramsMap.put("account-id", "676AA77A418C5BC1AB5E2DEBC7E023DA15A6C416331D7421");
        paramsMap.put("transaction-id", "078F653237B8A38CC39C2AACD451BE37F611F7B06204C2DA");
        transactionDto.setNextDueDate(new DateTime("2017-01-17"));

        Collection<?> result = contributionReceiptReport.getData(paramsMap, dataCollections);
        Assert.assertNotNull(result);

        ReceiptReportData receiptReportData = (ReceiptReportData) result.iterator().next();
        Assert.assertEquals(receiptReportData.getFromPayerBsb(), "012-345");
        Assert.assertEquals(receiptReportData.getAmount(), "$1,000.00");
        Assert.assertEquals(receiptReportData.getPaymentDate(), "17 Jan 2017");
    }

    @Test
    public void testContributionReceiptReport_whenRegularEndDate_thenRegular() {
        paramsMap.put("account-id", "676AA77A418C5BC1AB5E2DEBC7E023DA15A6C416331D7421");
        paramsMap.put("transaction-id", "078F653237B8A38CC39C2AACD451BE37F611F7B06204C2DA");
        paramsMap.put("report-type", "regular");
        transactionDto.setNextDueDate(new DateTime("2017-05-17"));
        transactionDto.setLastPayment(new DateTime("2017-08-17"));
        transactionDto.setFrequency("Monthly");

        Collection<?> result = contributionReceiptReport.getData(paramsMap, dataCollections);
        Assert.assertNotNull(result);

        ReceiptReportData receiptReportData = (ReceiptReportData) result.iterator().next();
        Assert.assertEquals(receiptReportData.getDepositType(), transactionDto.getContributionType());
        Assert.assertEquals(receiptReportData.getFromPayerName(), transactionDto.getPayer());
        Assert.assertEquals(receiptReportData.getFromPayerAccount(), transactionDto.getPayerAccount());
        Assert.assertEquals(receiptReportData.getFromPayerBsb(), "012-345");
        Assert.assertEquals(receiptReportData.getAmount(), "$1,000.00");
        Assert.assertEquals(receiptReportData.getPaymentDate(), "17 May 2017");
        Assert.assertEquals(receiptReportData.getTransactionDate(), "17 Jan 2017");
        Assert.assertEquals(receiptReportData.getRepeatEndDate(), "17 Aug 2017");
    }

    @Test
    public void testContributionReceiptReport_whenRegularAndNoEndDate_thenRegular() {
        paramsMap.put("account-id", "676AA77A418C5BC1AB5E2DEBC7E023DA15A6C416331D7421");
        paramsMap.put("transaction-id", "078F653237B8A38CC39C2AACD451BE37F611F7B06204C2DA");
        paramsMap.put("report-type", "regular");
        transactionDto.setRepeatEndDate(null);
        transactionDto.setFrequency("Monthly");

        Collection<?> result = contributionReceiptReport.getData(paramsMap, dataCollections);

        Assert.assertNotNull(result);
        ReceiptReportData receiptReportData = (ReceiptReportData) result.iterator().next();
        Assert.assertEquals(receiptReportData.getRepeatEndDate(), "No end date");
    }

    @Test
    public void testGetReportType_whenNoReportType_thenReportTypeOneOff() {
        String reportType = contributionReceiptReport.getReportType(paramsMap, dataCollections);
        assertNotNull(reportType);
        assertEquals(reportType, "One-off contribution");
    }

    @Test
    public void testGetReportType_whenReportTypeIsNotEmpty_thenReportTypeRegular() {
        paramsMap.put("report-type", "regular");
        String reportType = contributionReceiptReport.getReportType(paramsMap, dataCollections);
        assertNotNull(reportType);
        assertEquals(reportType, "Regular contribution");
    }

    @Test
    public void testGetReportType_whenReportTypeIsEmpty_thenReportTypeOneOff() {
        paramsMap.put("report-type", "");
        String reportType = contributionReceiptReport.getReportType(paramsMap, dataCollections);
        assertNotNull(reportType);
        assertEquals(reportType, "One-off contribution");
    }

    @Test
    public void testGetReportType_whenReportTypeIsNotRegular_thenReportTypeOneOff() {
        paramsMap.put("report-type", "test");
        String reportType = contributionReceiptReport.getReportType(paramsMap, dataCollections);
        assertNotNull(reportType);
        assertEquals(reportType, "One-off contribution");
    }

    @Test
    public void testGetPaymentDueLabel_whenNoReportType_thenPaymentLabel() {
        String reportType = contributionReceiptReport.getPaymentDueLabel(paramsMap);
        assertNotNull(reportType);
        assertEquals(reportType, "Payment due");
    }

    @Test
    public void testGetPaymentDueLabel_whenReportTypeIsNotEmpty_thenRegularLabel() {
        paramsMap.put("report-type", "regular");
        String reportType = contributionReceiptReport.getPaymentDueLabel(paramsMap);
        assertNotNull(reportType);
        assertEquals(reportType, "Regular payment due");
    }

    @Test
    public void testGetPaymentDueLabel_whenReportTypeIsEmpty_thenPaymentLabel() {
        paramsMap.put("report-type", "");
        String reportType = contributionReceiptReport.getPaymentDueLabel(paramsMap);
        assertNotNull(reportType);
        assertEquals(reportType, "Payment due");
    }

    @Test
    public void testGetPaymentDueLabel_whenReportTypeIsNotRegular_thenPaymentLabel() {
        paramsMap.put("report-type", "test");
        String reportType = contributionReceiptReport.getPaymentDueLabel(paramsMap);
        assertNotNull(reportType);
        assertEquals(reportType, "Payment due");
    }

    @Test
    public void testGetDisclaimer() {
        String content = contributionReceiptReport.getDisclaimer();
        assertEquals("MockString", content);
    }
}
