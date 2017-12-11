package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.transactionhistory.model.TransactionHistoryDto;
import com.bt.nextgen.api.transactionhistory.service.TransactionHistoryDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.BeanFilter;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.Sort;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.core.api.UriMappingConstants.*;

@Report("transactionHistoryCsvReport")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
public class TransactionHistoryCsvReport
{
	//TODO switch over to transactiondtoservice/transactiondto when issues around these two classes have been fixed
	@Autowired
	private TransactionHistoryDtoService transactionService;

	@ReportBean("transactions")
	@SuppressWarnings("unchecked")
	public Collection <TransactionHistoryDto> getTransactions(Map <String, String> params)
	{
		List <ApiSearchCriteria> criteria = getApiSearchCriteria(params);

		String queryString = params.get(BeanFilter.QUERY_PARAMETER);
		String sortString = params.get(Sort.SORT_PARAMETER);
		ApiResponse response = new Sort <>(new BeanFilter(ApiVersion.CURRENT_VERSION,
			new SearchByCriteria <>(ApiVersion.CURRENT_VERSION, transactionService, criteria),
			queryString), sortString).performOperation();
		return ((ResultListDto <TransactionHistoryDto>)response.getData()).getResultList();
	}

	private List <ApiSearchCriteria> getApiSearchCriteria(Map <String, String> params)
	{
		List <ApiSearchCriteria> criteria = new ArrayList <ApiSearchCriteria>();
		ApiSearchCriteria portfolioCriteria = null;
		ApiSearchCriteria assetCriteria = null;
		ApiSearchCriteria startDateCriteria = null;
		ApiSearchCriteria endDateCriteria = null;
		String startDate = params.get(START_DATE_PARAMETER_MAPPING);
		String endDate = params.get(END_DATE_PARAMETER_MAPPING);
		String assetCode = params.get(ASSET_CODE);

		portfolioCriteria = new ApiSearchCriteria(Attribute.ACCOUNT_ID,
			SearchOperation.EQUALS,
			params.get(ACCOUNT_ID_URI_MAPPING),
			OperationType.STRING);
		criteria.add(portfolioCriteria);

		if (assetCode != null && !assetCode.equals(Constants.EMPTY_STRING))
		{
			assetCriteria = new ApiSearchCriteria(Attribute.ASSET_CODE, SearchOperation.EQUALS, assetCode, OperationType.STRING);
			criteria.add(assetCriteria);
		}
		startDateCriteria = new ApiSearchCriteria(Attribute.START_DATE, SearchOperation.EQUALS, startDate, OperationType.DATE);
		criteria.add(startDateCriteria);
		endDateCriteria = new ApiSearchCriteria(Attribute.END_DATE, SearchOperation.EQUALS, endDate, OperationType.DATE);
		criteria.add(endDateCriteria);
		return criteria;
	}
}
