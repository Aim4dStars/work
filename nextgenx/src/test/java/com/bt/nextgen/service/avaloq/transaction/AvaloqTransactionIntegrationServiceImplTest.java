package com.bt.nextgen.service.avaloq.transaction;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.transaction.Transaction;
import com.bt.nextgen.service.integration.transaction.TransactionIntegrationService;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AvaloqTransactionIntegrationServiceImplTest extends BaseSecureIntegrationTest
{
	@Autowired
	@Qualifier("AvaloqTransactionIntegrationServiceImpl")
	private TransactionIntegrationService transactionIntegrationService;

	@Test
	@SecureTestContext
	public void loadScheduledTransactionsTest()
	{
		WrapAccountIdentifier identifier = new WrapAccountIdentifierImpl();
		identifier.setBpId("11861");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		List <Transaction> transactions = transactionIntegrationService.loadScheduledTransactions(identifier, serviceErrors);
		assertNotNull(transactions);

		assertEquals("217126", transactions.get(0).getPaymentId());
		assertEquals("225727", transactions.get(1).getPaymentId());
		assertEquals("225728", transactions.get(2).getPaymentId());
		assertEquals("225729", transactions.get(3).getPaymentId());
		assertEquals("227608", transactions.get(4).getPaymentId());
		assertEquals("227609", transactions.get(5).getPaymentId());
		assertEquals("227611", transactions.get(6).getPaymentId());
		assertEquals("227612", transactions.get(7).getPaymentId());
		assertEquals("227613", transactions.get(8).getPaymentId());
	}

	@Test
	@SecureTestContext
	public void loadTransactionHistoryTest()
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		List <TransactionHistory> transactions = transactionIntegrationService.loadTransactionHistory("11861",
			new DateTime(),
			new DateTime(),
			serviceErrors);

		assertNotNull(transactions);

		assertEquals(64, transactions.size());
	}

	@Test
	@SecureTestContext
	public void loadRecentCashTransactionsTest()
	{
		WrapAccountIdentifier identifier = new WrapAccountIdentifierImpl();
		identifier.setBpId("69949");

		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		List <TransactionHistory> transactions = transactionIntegrationService.loadRecentCashTransactions(identifier,
			serviceErrors);

		assertNotNull(transactions);

		assertEquals(10, transactions.size());

		// Other details covered by cash transaction history tests
	}
}
