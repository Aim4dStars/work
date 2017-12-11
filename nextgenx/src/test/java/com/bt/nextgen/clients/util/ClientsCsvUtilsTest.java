package com.bt.nextgen.clients.util;

import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bt.nextgen.core.web.model.FatHeaderModel;
import com.bt.nextgen.payments.domain.PaymentStatus;
import com.bt.nextgen.portfolio.web.model.PortfolioInterface;
import com.bt.nextgen.portfolio.web.model.PortfolioModel;
import com.bt.nextgen.transactions.web.model.TransactionModel;
import com.bt.nextgen.transactions.web.model.TransactionInterface;
import com.bt.nextgen.web.controller.cash.util.Attribute;

public class ClientsCsvUtilsTest {

	
		
	@Test
    public void testGetCsvTransactionsList()
	 {
		FatHeaderModel fatHeaderModel= new FatHeaderModel();
	    PortfolioInterface portfolioModel = new PortfolioModel();
        portfolioModel.setAccountId("10000005");
		portfolioModel.setAccountType("Individual");
		portfolioModel.setAdviser("Salminen Monica");
		portfolioModel.setAccountName("Taylor Martin");
		
		fatHeaderModel.setPortfolioModel(portfolioModel);
		fatHeaderModel.setPrimaryContactName("Taylor Martin");
		TransactionInterface transactionModel = new TransactionModel();
		
		transactionModel.setPaymentStatus(PaymentStatus.CLEARED);
		transactionModel.setTransactionDate(new Date());
		transactionModel.setTransactionType("Maturity");
		transactionModel.setTdPrincipal("$9999.00");
		transactionModel.setDescriptionMain("Term Deposit at 4.05% pa");
		transactionModel.setDescriptionMinor("2 years, monthly interest payments");
		transactionModel.setBrandName("St. George");
				
		List<TransactionInterface> transactions = new ArrayList<>();
		transactions.add(transactionModel);
		
		String disclaimer="testdesclaimer";
		Map<String, String[]> searchParametersMap = new HashMap<>();
		String[] fromDateArray = new String[]{"01 Jul 2013"};
		String[] toDateArray = new String[]{"01 Dec 2013"};
		searchParametersMap.put(Attribute.FROM_TERM_DATE, fromDateArray);
		searchParametersMap.put(Attribute.TO_TERM_DATE, toDateArray);
		
		String st = ClientsCsvUtils.getCsvTermDepositTransactionsReport(fatHeaderModel, transactions,searchParametersMap,disclaimer);
	
		assertThat(st.contains("Account ID"), Is.is(true));
		assertThat(st.contains("10000005"), Is.is(true));
		assertThat(st.contains("Account name"), Is.is(true));
		assertThat(st.contains("Taylor Martin"), Is.is(true));
		assertThat(st.contains("Account type"), Is.is(true));
		assertThat(st.contains("Individual"), Is.is(true));
		assertThat(st.contains("Adviser name"), Is.is(true));
		assertThat(st.contains("Salminen Monica"), Is.is(true));
		assertThat(st.contains("Primary contact"), Is.is(true));
		assertThat(st.contains("Taylor Martin"), Is.is(true));
		
		assertThat(st.contains("Date"), Is.is(true));
		assertThat(st.contains("Transaction type"), Is.is(true));
		assertThat(st.contains("Description"), Is.is(true));
		assertThat(st.contains("Amount"), Is.is(true));
	}
}
