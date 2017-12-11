package com.bt.nextgen.api.beneficiary.controller;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.beneficiary.model.BeneficiaryDto;
import com.bt.nextgen.api.beneficiary.model.BeneficiaryTrxnDto;
import com.bt.nextgen.api.beneficiary.service.BeneficiaryDtoService;
import com.bt.nextgen.api.beneficiary.service.SaveBeneficiariesDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.Submit;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.apache.commons.collections.CollectionUtils;
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
 * This is the controller class for retrieving and saving beneficiary details.
 * Created by M035995 on 11/07/2016.
 */
@Controller
@RequestMapping(produces = "application/json")
public class BeneficiaryApiController {

    private static final String BROKER_ID = "brokerid";
    private static final String CACHE = "cache";

    @Autowired
    private BeneficiaryDtoService beneficiaryDtoService;

    @Autowired
    private SaveBeneficiariesDtoService saveBeneficiariesDtoService;

    /**
     * This method retrieves all beneficiary details types for a particular account Id.
     *
     * @param accountId Account Id for which Beneficiary Details are required.
     *
     * @return instance of {@link ApiResponse}
     */


    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.super.beneficiaries.view')")
    @RequestMapping(method = RequestMethod.GET, value = "${api.beneficiary.v1.uri.super.beneficiaries}")
    @ResponseBody
    public ApiResponse getBeneficiaryDetails(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
                                             @RequestParam(value = CACHE, defaultValue = "false") String useCache) {

        if (StringUtils.isEmpty(accountId)) {
            throw new IllegalArgumentException("Account id is not valid");
        }

        final List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS,
                EncodedString.toPlainText(accountId), ApiSearchCriteria.OperationType.STRING));

        if (StringUtils.isNotEmpty(useCache) && "true".equalsIgnoreCase(useCache)) {
            ApiSearchCriteria useCacheCriteria = new ApiSearchCriteria("useCache", ApiSearchCriteria.SearchOperation.EQUALS, "true", ApiSearchCriteria.OperationType.STRING);
            criteria.add(useCacheCriteria);
        }

        ApiResponse response = new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, beneficiaryDtoService, criteria).performOperation();
        ResultListDto<BeneficiaryDto> resultList = (ResultListDto<BeneficiaryDto>) response.getData();
        BeneficiaryDto beneficiaryDto = new BeneficiaryDto();
        if (resultList != null && CollectionUtils.isNotEmpty(resultList.getResultList())) {
            beneficiaryDto = resultList.getResultList().get(0);
        }
        return new ApiResponse(ApiVersion.CURRENT_VERSION, beneficiaryDto);
    }


    /**
     * This method retrieves all beneficiary details types for all the accounts
     *
     * @return instance of {@link ApiResponse}
     */
    @PreAuthorize("isAuthenticated() and @permissionBaseService.hasBasicPermission('business.beneficiaries.view')")
    @RequestMapping(method = RequestMethod.GET, value = "${api.beneficiary.v3.uri.business.super.beneficiaries}")
    @ResponseBody
    public ApiResponse getBeneficiaryDetailsForAccountList(@RequestParam(value = CACHE, defaultValue = "false") String useCache,
                                                           @RequestParam(value = BROKER_ID, required = false) String brokerId) {
        final List<ApiSearchCriteria> criteria = new ArrayList<>();

        if (StringUtils.isNotEmpty(useCache) && "true".equalsIgnoreCase(useCache)) {
            ApiSearchCriteria useCacheCriteria = new ApiSearchCriteria("useCache", ApiSearchCriteria.SearchOperation.EQUALS, "true", ApiSearchCriteria.OperationType.STRING);
            criteria.add(useCacheCriteria);
        }
        if (StringUtils.isNotEmpty(brokerId)) {
            criteria.add(new ApiSearchCriteria(Attribute.BROKER_ID, brokerId));
        }

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, beneficiaryDtoService, criteria).performOperation();
    }

    /**
     * This method saves/updates beneficiary details for a particular account Id.
     *
     * @param accId              Account Id
     * @param beneficiaryTrxnDto BeneficiaryTrxnDto beneficiary details to be updated
     *
     * @return instance of {@link ApiResponse}
     */
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accId, 'account.super.beneficiaries.update')")
    @RequestMapping(method = RequestMethod.POST, value = "${api.beneficiary.v1.uri.super.beneficiaries}")
    public
    @ResponseBody
    KeyedApiResponse<AccountKey> saveBeneficiaries(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accId,
                                                   @RequestBody BeneficiaryTrxnDto beneficiaryTrxnDto) {
        if (StringUtils.isEmpty(accId)) {
            throw new IllegalArgumentException("Account id is not valid");
        }
        AccountKey key = new AccountKey(accId);
        beneficiaryTrxnDto.setKey(key);
        return new Submit<AccountKey, BeneficiaryTrxnDto>(ApiVersion.CURRENT_VERSION, saveBeneficiariesDtoService, null, beneficiaryTrxnDto).performOperation();
    }

}

