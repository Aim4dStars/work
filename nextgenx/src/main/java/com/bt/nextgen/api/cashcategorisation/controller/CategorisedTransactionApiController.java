package com.bt.nextgen.api.cashcategorisation.controller;

import com.bt.nextgen.api.cashcategorisation.service.CategorisedTransactionValuationDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class CategorisedTransactionApiController
{
	@Autowired
	private CategorisedTransactionValuationDtoService categorisedTransactionValuationDtoService;


	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ACCOUNT_CATEGORISATION_SUMMARY)
	@PreAuthorize("isAuthenticated()")
	public @ResponseBody
	ApiResponse getContributionSummary(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accId,
									   @PathVariable(UriMappingConstants.CATEGORY_ID_MAPPING) String categoryId,
									   @RequestParam(value = "date", required = true) String date,
									   @RequestParam(value = "cache", required = false) String useCache)
	{
		//String categoryNumber = "0";

		if (StringUtils.isEmpty(accId))
		{
			throw new IllegalArgumentException("Account id is not valid");
		}

		if (StringUtils.isEmpty(date))
		{
			throw new IllegalArgumentException("Date is not valid");
		}

		if (StringUtils.isEmpty(categoryId) || CashCategorisationType.getByAvaloqInternalId(categoryId) == null)
		{
			throw new IllegalArgumentException("Category id is not valid");
		}

		ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria("accountId", ApiSearchCriteria.SearchOperation.EQUALS, EncodedString.toPlainText(accId), ApiSearchCriteria.OperationType.STRING);
		ApiSearchCriteria dateCriteria = new ApiSearchCriteria("financialYearDate", ApiSearchCriteria.SearchOperation.EQUALS, date, ApiSearchCriteria.OperationType.STRING);
		ApiSearchCriteria categoryCriteria = new ApiSearchCriteria("category", ApiSearchCriteria.SearchOperation.EQUALS, "21", ApiSearchCriteria.OperationType.STRING);

		List<ApiSearchCriteria> searchCriteriaList = new ArrayList<>();
		searchCriteriaList.add(accountIdCriteria);
		searchCriteriaList.add(dateCriteria);
		searchCriteriaList.add(categoryCriteria);

		if (StringUtils.isNotEmpty(useCache) && "true".equalsIgnoreCase(useCache))
		{
			ApiSearchCriteria useCacheCriteria = new ApiSearchCriteria("useCache", ApiSearchCriteria.SearchOperation.EQUALS, "true", ApiSearchCriteria.OperationType.STRING);
			searchCriteriaList.add(useCacheCriteria);
		}

		return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, categorisedTransactionValuationDtoService, searchCriteriaList).performOperation();
	}
}
