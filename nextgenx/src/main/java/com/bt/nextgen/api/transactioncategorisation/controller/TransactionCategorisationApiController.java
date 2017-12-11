package com.bt.nextgen.api.transactioncategorisation.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.transactioncategorisation.service.TransactionCategoryDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.SearchByCriteria;

@SuppressWarnings("squid:UnusedProtectedMethod")
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class TransactionCategorisationApiController
{

	@Autowired
	private TransactionCategoryDtoService transactionCategoryDtoService;

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.TRANSACTION_CATEGORY)
	@PreAuthorize("isAuthenticated()")
	public @ResponseBody
	ApiResponse getTransactionCategories()
	{
		List <ApiSearchCriteria> searchCriteriaList = new ArrayList <>();
		return new SearchByCriteria <>(ApiVersion.CURRENT_VERSION, transactionCategoryDtoService, searchCriteriaList).performOperation();
	}

}
