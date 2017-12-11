package com.bt.nextgen.reports.account;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.transactionhistory.model.TransactionHistoryDto;
import com.bt.nextgen.api.transactionhistory.service.TransactionHistoryDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@RunWith(MockitoJUnitRunner.class)
public class TransactionHistoryCsvReportTest
{
	@InjectMocks
	private TransactionHistoryCsvReport transactionHistoryCsvReport;

	@Mock
	private TransactionHistoryDtoService transactionHistoryDtoService;

	@Before
	public void setup()
	{
		// Mock transaction history dto service
		List <TransactionHistoryDto> transactionDtos = new ArrayList <TransactionHistoryDto>();

		TransactionHistoryDto transactionDto = new TransactionHistoryDto();

		transactionDto.setDescription("MockDto");

		transactionDtos.add(transactionDto);

		when(transactionHistoryDtoService.search((Matchers.anyListOf(ApiSearchCriteria.class)), any(ServiceErrorsImpl.class))).thenReturn(transactionDtos);
	}

	@Test
	public void testGetTransactions()
	{
		Map <String, String> params = new HashMap <>();

		Collection <TransactionHistoryDto> transactionResponse = transactionHistoryCsvReport.getTransactions(params);

		assertNotNull(transactionResponse);

		assertEquals(1, transactionResponse.size());
	}
}
