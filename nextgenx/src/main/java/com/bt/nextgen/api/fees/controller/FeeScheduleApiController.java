package com.bt.nextgen.api.fees.controller;

import com.bt.nextgen.api.account.v1.model.AccountKey;

import com.bt.nextgen.api.fees.model.FeeScheduleDto;
import com.bt.nextgen.api.fees.model.FeesScheduleTrxnDto;
import com.bt.nextgen.api.fees.service.FeeScheduleDtoService;
import com.bt.nextgen.api.fees.service.TaxInvoiceDtoService;
import com.bt.nextgen.api.fees.validation.FeesScheduleDtoErrorMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.api.operation.Validate;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class FeeScheduleApiController
{
	@Autowired
	private FeeScheduleDtoService feeScheduleDtoService;

	@Autowired
	private FeesScheduleDtoErrorMapper feesScheduleDtoErrorMapper;

	@Autowired
	private TaxInvoiceDtoService taxInvoiceDtoService;

    @Autowired
    private UserProfileService profileService;

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.FEE_SCHEDULE, produces = "application/json")
	@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
	public @ResponseBody
	ApiResponse getFees(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId)
	{
		AccountKey key = new AccountKey(accountId);
		return new FindByKey <>(ApiVersion.CURRENT_VERSION, feeScheduleDtoService, key).performOperation();

	}

	@RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.VALIDATE_FEE_SCHEDULE)
	@PreAuthorize("isAuthenticated() and @acctPermissionService.hasTransactionPermission(#accId, 'account.fee.advice.update')")
	public @ResponseBody
	KeyedApiResponse <AccountKey> validateFees(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accId,
		@ModelAttribute FeesScheduleTrxnDto feeScheduleTransactionDto)
	{
        AccountKey key = new AccountKey(accId);
        FeeScheduleDto scheduleDto = new FeeScheduleDto();
        scheduleDto.setKey(key);
        scheduleDto.setTransactionDto(feeScheduleTransactionDto);
        return new Validate<>(ApiVersion.CURRENT_VERSION, feeScheduleDtoService, feesScheduleDtoErrorMapper, scheduleDto).performOperation();
	}

	@RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.SUBMIT_FEE_SCHEDULE)
	@PreAuthorize("isAuthenticated() and @acctPermissionService.hasTransactionPermission(#accId, 'account.fee.advice.update')")
	public @ResponseBody
	KeyedApiResponse <AccountKey> submitFees(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accId,
		@ModelAttribute FeesScheduleTrxnDto feeScheduleTransactionDto)
	{
        if (!profileService.isEmulating()) {
            AccountKey key = new AccountKey(accId);
            FeeScheduleDto scheduleDto = new FeeScheduleDto();
            scheduleDto.setKey(key);
            scheduleDto.setTransactionDto(feeScheduleTransactionDto);
            return new Submit<>(ApiVersion.CURRENT_VERSION, feeScheduleDtoService, feesScheduleDtoErrorMapper, scheduleDto).performOperation();
        }
        else {
            throw new AccessDeniedException("Access Denied");
        }
	}

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.TAX_INVOICE, produces = "application/json")
	@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
	public @ResponseBody
	ApiResponse generateTaxInvoicePdf(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
									  @RequestParam(value = Attribute.MONTH, required = true) String month,
									  @RequestParam(value = Attribute.YEAR, required = true) String year)
	{
		List<ApiSearchCriteria> criteria = new ArrayList<ApiSearchCriteria>();
		criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accountId, OperationType.STRING));

		criteria.add(new ApiSearchCriteria(Attribute.MONTH, SearchOperation.EQUALS, month, OperationType.STRING));
		criteria.add(new ApiSearchCriteria(Attribute.YEAR, SearchOperation.EQUALS, year, OperationType.STRING));

		return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, taxInvoiceDtoService, criteria).performOperation();

	}


}
