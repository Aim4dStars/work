package com.bt.nextgen.api.superpersonaltaxdeduction.controller;

/**
 * Created by L067218 on 6/10/2016.
 */

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.superpersonaltaxdeduction.model.PersonalTaxDeductionNoticeTrxnDto;
import com.bt.nextgen.api.superpersonaltaxdeduction.service.PersonalTaxDeductionDtoService;
import com.bt.nextgen.api.superpersonaltaxdeduction.service.PersonalTaxDeductionNoticeValidator;
import com.bt.nextgen.api.superpersonaltaxdeduction.service.SavePersonalTaxDeductionDtoService;
import com.bt.nextgen.api.superpersonaltaxdeduction.validation.PersonalTaxDeductionNoticeErrorMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.SearchOneByCriteria;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.api.operation.Validate;
import com.btfin.panorama.core.security.encryption.EncodedString;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the controller class for Personal Tax Deduction
 * Created by L067218 on 06/10/2016.
 */
@Controller
@RequestMapping(produces = "application/json")
public class PersonalTaxDeductionApiController {
    private static final String FINANCIAL_YEAR_DATE_PARAM = "financialYearDate";
    private static final String ACCOUNT_ID_PARAM = "accountId";
    private static final String DATE_PARAM = "date";


    @Autowired
    private PersonalTaxDeductionDtoService personalTaxDeductionDtoService;

    @Autowired
    private SavePersonalTaxDeductionDtoService savePersonalTaxDeductionDtoService;

    @Autowired
    private PersonalTaxDeductionNoticeValidator validator;

    @Autowired
    private PersonalTaxDeductionNoticeErrorMapper errorMapper;


    /**
     * This method shows the personal tax deduction notice for a particular account Id.
     *
     * @param accountId Account Id
     *
     * @return instance of {@link com.bt.nextgen.core.api.model.ApiResponse}
     */

    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.super.personaltaxdeductionnotice.view')")
    @RequestMapping(method = RequestMethod.GET, value = "${api.superpersonaltaxdeduction.v1.uri.notices}")
    @ResponseBody
    public ApiResponse viewPersonalTaxDeduction(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) final String accountId,
                                                @RequestParam(value = DATE_PARAM) final String financialYearStartDate) {
        if (StringUtils.isEmpty(accountId)) {
            throw new IllegalArgumentException("Account id is not valid");
        }

        if (StringUtils.isEmpty(financialYearStartDate)) {
            throw new IllegalArgumentException("Date is not valid");
        }

        final ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria(ACCOUNT_ID_PARAM,
                ApiSearchCriteria.SearchOperation.EQUALS, EncodedString.toPlainText(accountId),
                ApiSearchCriteria.OperationType.STRING);
        final ApiSearchCriteria dateCriteria = new ApiSearchCriteria(FINANCIAL_YEAR_DATE_PARAM,
                ApiSearchCriteria.SearchOperation.EQUALS, financialYearStartDate,
                ApiSearchCriteria.OperationType.STRING);

        final List<ApiSearchCriteria> searchCriteriaList = new ArrayList<>();

        searchCriteriaList.add(accountIdCriteria);
        searchCriteriaList.add(dateCriteria);

        return new SearchOneByCriteria<>(ApiVersion.CURRENT_VERSION, personalTaxDeductionDtoService, searchCriteriaList).performOperation();
    }

    /**
     * This method creates new personal super contributions deduction notice
     *
     * @param accId Account Id
     *
     * @return instance of {@link com.bt.nextgen.core.api.model.ApiResponse}
     */
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accId, 'account.super.personaltaxdeductionnotice.update')")
    @RequestMapping(method = RequestMethod.POST, value = "${api.superpersonaltaxdeduction.v1.uri.notices}")
    @ResponseBody
    public KeyedApiResponse<AccountKey> createOrVaryDeductionNotice(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accId,
            @RequestParam(value = DATE_PARAM) final String financialYearStartDate,
            @RequestBody PersonalTaxDeductionNoticeTrxnDto taxTrxnDto) {
        if (StringUtils.isEmpty(accId)) {
            throw new IllegalArgumentException("Account id is not valid");
        }

        final AccountKey key = new AccountKey(accId);

        taxTrxnDto.setKey(key);
        taxTrxnDto.setDate(financialYearStartDate);

        final KeyedApiResponse<AccountKey> validationResponse = new Validate<>(ApiVersion.CURRENT_VERSION, validator, errorMapper, taxTrxnDto)
                .performOperation();

        if (validationResponse.getError() != null) {
            return validationResponse;
        }

        final PersonalTaxDeductionNoticeTrxnDto dto = (PersonalTaxDeductionNoticeTrxnDto) validationResponse.getData();


        return new Submit<>(ApiVersion.CURRENT_VERSION, savePersonalTaxDeductionDtoService, null, dto).performOperation();
    }
}
