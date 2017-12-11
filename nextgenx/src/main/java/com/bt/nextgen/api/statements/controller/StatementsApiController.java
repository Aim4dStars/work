package com.bt.nextgen.api.statements.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.statements.service.StatementDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.Group;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.Sort;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.web.controller.cash.util.Attribute;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class StatementsApiController
{
	public static final String DOC_TYPES_PARAMETER_MAPPING = "doc-types";

	@Autowired
	private StatementDtoService statementsService;

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ACCOUNT_STATEMENTS)
	@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
	public @ResponseBody
	ApiResponse getStatements(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
		@RequestParam(value = UriMappingConstants.START_DATE_PARAMETER_MAPPING, required = false) String startDate,
		@RequestParam(value = UriMappingConstants.END_DATE_PARAMETER_MAPPING, required = false) String endDate,
		@RequestParam(value = DOC_TYPES_PARAMETER_MAPPING, required = false) List <String> documentTypes)
	{
		List <ApiSearchCriteria> criteriaList = getBaseSearchCriteriaList(startDate, endDate, documentTypes);

		AccountKey.valueOf(accountId);
		criteriaList.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accountId, OperationType.STRING));
		return new SearchByCriteria <>(ApiVersion.CURRENT_VERSION, statementsService, criteriaList).performOperation();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/statements")
	@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
	public @ResponseBody
	ApiResponse getStatements(
		@RequestParam(value = UriMappingConstants.START_DATE_PARAMETER_MAPPING, required = false) String startDate,
		@RequestParam(value = UriMappingConstants.END_DATE_PARAMETER_MAPPING, required = false) String endDate,
		@RequestParam(value = DOC_TYPES_PARAMETER_MAPPING, required = false) List <String> documentTypes)
	{
		List <ApiSearchCriteria> criteriaList = getBaseSearchCriteriaList(startDate, endDate, documentTypes);
		return new SearchByCriteria <>(ApiVersion.CURRENT_VERSION, statementsService, criteriaList).performOperation();
	}

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.STATEMENTS)
	@PreAuthorize("isAuthenticated()")
	public @ResponseBody
	ApiResponse getFeeRevenueStatements(
		@RequestParam(value = UriMappingConstants.START_DATE_PARAMETER_MAPPING, required = false) String startDate,
		@RequestParam(value = UriMappingConstants.END_DATE_PARAMETER_MAPPING, required = false) String endDate,
		@RequestParam(value = DOC_TYPES_PARAMETER_MAPPING, required = false) List <String> documentTypes,
		@RequestParam(value = Sort.SORT_PARAMETER, required = false) String orderBy,
		@RequestParam(value = Group.GROUP_PARAMETER, required = false) String groupBy)
	{
		List <ApiSearchCriteria> criteriaList = getBaseSearchCriteriaList(startDate, endDate, documentTypes);

		return new Sort <>(new SearchByCriteria <>(ApiVersion.CURRENT_VERSION, statementsService, criteriaList), orderBy).performOperation();
	}

	private List <ApiSearchCriteria> getBaseSearchCriteriaList(String startDate, String endDate, List <String> documentTypes)
	{
		List <ApiSearchCriteria> criteriaList = new ArrayList <>();
		criteriaList.add(new ApiSearchCriteria(Attribute.START_DATE, SearchOperation.EQUALS, startDate, OperationType.DATE));
		criteriaList.add(new ApiSearchCriteria(Attribute.END_DATE, SearchOperation.EQUALS, endDate, OperationType.DATE));
		if (!CollectionUtils.isEmpty(documentTypes))
		{
			String docTypes = StringUtils.join(documentTypes, ',');
			criteriaList.add(new ApiSearchCriteria(Attribute.DOCUMENT_TYPES,
				SearchOperation.EQUALS,
				docTypes,
				OperationType.STRING));
		}
		return criteriaList;
	}
}
