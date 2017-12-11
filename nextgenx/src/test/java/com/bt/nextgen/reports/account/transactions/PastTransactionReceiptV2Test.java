package com.bt.nextgen.reports.account.transactions;

import com.bt.nextgen.api.transactionhistory.model.CashTransactionHistoryDto;
import com.bt.nextgen.api.transactionhistory.service.CashTransactionHistoryReportService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.avaloq.transactionhistory.TransactionHistoryImpl;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.bt.nextgen.reports.account.transactions.PastTransactionReceiptV2.TRANSACTION_RECEIPT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PastTransactionReceiptV2Test
{
	@InjectMocks
	private PastTransactionReceiptV2 pastTransactionReceipt;

	@Mock
    private CmsService cmsService;

	@Mock
	private CashTransactionHistoryReportService cashTransactionHistoryReportService;

	private static final String ACCOUNT_ID = "accountId";

	@Test
	public void testRetrievePastTransaction() throws ParseException
	{
		TransactionHistoryImpl transactionHistory = new TransactionHistoryImpl();
		transactionHistory.setPayerAccount(ACCOUNT_ID);
        CashTransactionHistoryDto transactionDto = new CashTransactionHistoryDto(transactionHistory);
		when(cashTransactionHistoryReportService.retrievePastTransaction(anyString(), anyString(), any(DateTime.class), any(DateTime.class), anyString())).thenReturn(transactionDto);
		Map <String, Object> params = new HashMap <>();
		params.put("account-id", "975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0");
		params.put("startDate", "2014-09-09");
		params.put("endDate", "2014-12-09");
		params.put("receiptNo", "DCA0F79903927AF7D8D1C670428E5E3978F6A1D47D099A10");

		Collection<PastTransactionReceiptData> reportDataList = (Collection<PastTransactionReceiptData>) pastTransactionReceipt.getData(params, null);

		assertNotNull(reportDataList);
		assertEquals(1, reportDataList.size());
		PastTransactionReceiptData reportData = reportDataList.iterator().next();
		assertEquals(ACCOUNT_ID, reportData.getPayerAccount());
	}

	@Test
	public void shouldReturnReportTitle() {
		assertEquals(TRANSACTION_RECEIPT, pastTransactionReceipt.getReportTitle(new HashMap<String, Object>(), new HashMap<String, Object>()));
	}

}
