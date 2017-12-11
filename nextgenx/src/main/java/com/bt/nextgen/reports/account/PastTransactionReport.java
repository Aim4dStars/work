package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.transaction.model.TransactionDto;
import com.bt.nextgen.api.transaction.service.TransactionDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.reporting.BaseReport;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Report("pastTransactionReport")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
public class PastTransactionReport extends BaseReport
{
	//TODO switch over to transactiondtoservice/transactiondto when issues around these two classes have been fixed
	@Autowired
	private TransactionDtoService transactionService;

	@ReportBean("pastTransactions")
	public Collection <TransactionDto> getTransactions(Map <String, String> params)
	{

		String accountId = params.get(UriMappingConstants.ACCOUNT_ID_URI_MAPPING);

		List <ApiSearchCriteria> criteria = new ArrayList <ApiSearchCriteria>();
		criteria.add(new ApiSearchCriteria(Attribute.PORTFOLIO_ID, SearchOperation.EQUALS, accountId, OperationType.STRING));
		criteria.add(new ApiSearchCriteria(Attribute.TRANSACTION_TYPE,
			SearchOperation.EQUALS,
			Attribute.SCHEDULED_TRANSACTIONS,
			OperationType.STRING));

		ServiceErrors serviceErrors = new FailFastErrorsImpl();
		List <TransactionDto> resultList = transactionService.search(criteria, serviceErrors);
		SimpleDateFormat form = new SimpleDateFormat("dd-MMM-yy");

		for (TransactionDto transactionDto : resultList)
		{
			if (transactionDto.getEffectiveDate() != null)
			{
				Date date = transactionDto.getEffectiveDate().toDate();
				transactionDto.setEffectiveDateUpdated((form.format(date)).toString());

			}

			if (transactionDto.getMetaType().equals("Incoming Payment"))
			{
				transactionDto.setCreditAmount(transactionDto.getNetAmount());
			}
			else
			{
				transactionDto.setDebitAmount(transactionDto.getNetAmount());
			}

		}

		return resultList;

	}
}
