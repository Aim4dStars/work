package com.bt.nextgen.api.account.v1.controller.movemoney;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.account.v1.model.DepositDto;
import com.bt.nextgen.api.account.v1.service.DepositDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.api.operation.Validate;
import com.bt.nextgen.core.api.validation.ErrorMapper;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.web.controller.cash.util.Attribute;

/**
 * @deprecated Use V2
 */
@Deprecated
@Controller
@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class DepositsApiController {
    @Autowired
    @Qualifier("DepositDtoServiceV1")
    private DepositDtoService depositDtoService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.DEPOSITS)
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#portfolioId, 'account.report.view')")
    public @ResponseBody ApiResponse getPayeesForAccount(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String portfolioId) {
        String portfolioIdTest = EncodedString.toPlainText(portfolioId);
        List<ApiSearchCriteria> criteria = new ArrayList<ApiSearchCriteria>();
        criteria.add(
                new ApiSearchCriteria(Attribute.PORTFOLIO_ID, SearchOperation.EQUALS, portfolioIdTest, OperationType.STRING));

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, depositDtoService, criteria).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.CONFIRM_DEPOSITS)
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#portfolioId, 'account.deposit.linked.create')")
    public @ResponseBody KeyedApiResponse<AccountKey> confirmDeposit(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String portfolioId, @ModelAttribute DepositDto depositDto) {
        String portfolioTest = EncodedString.toPlainText(portfolioId);
        AccountKey key = new AccountKey(portfolioTest);
        depositDto.setKey(key);

        return new Validate<>(ApiVersion.CURRENT_VERSION, depositDtoService, new ErrorMapper() {

            @Override
            public List<DomainApiErrorDto> map(List<ValidationError> errors) {
                return new ArrayList<DomainApiErrorDto>();
            }
        }, depositDto).performOperation();

    }

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.SUBMIT_DEPOSITS)
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#portfolioId, 'account.deposit.linked.create')")
    public @ResponseBody KeyedApiResponse<AccountKey> submitDeposit(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String portfolioId, @ModelAttribute DepositDto depositDto) {

        String portfolioTest = EncodedString.toPlainText(portfolioId);
        AccountKey key = new AccountKey(portfolioTest);
        depositDto.setKey(key);

        return new Submit<>(ApiVersion.CURRENT_VERSION, depositDtoService, new ErrorMapper() {

            @Override
            public List<DomainApiErrorDto> map(List<ValidationError> errors) {
                return new ArrayList<DomainApiErrorDto>();
            }
        }, depositDto).performOperation();

    }
}
