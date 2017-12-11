package com.bt.nextgen.api.pension.controller;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.pension.model.PensionTrxnDto;
import com.bt.nextgen.api.pension.service.PensionCommencementDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.Submit;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This is the controller class for commencing pension
 * Created by L067218 on 12/09/2016.
 */
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class PensionCommencementApiController {

    @Autowired
    private PensionCommencementDtoService pensionCommencementDtoService;


    /**
     * This method commences pension for a particular account Id.
     *
     * @param accId Account Id
     *
     * @return instance of {@link com.bt.nextgen.core.api.model.ApiResponse}
     */
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accId, 'account.super.pension.commencement.update')")
    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.SUPER_PENSION_COMMENCEMENT)
    @ResponseBody
    public KeyedApiResponse<AccountKey> commencePension(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accId) {
        if (StringUtils.isEmpty(accId)) {
            throw new IllegalArgumentException("Account id is not valid");
        }

        final AccountKey key = new AccountKey(accId);
        final PensionTrxnDto pensionTrxnDto = new PensionTrxnDto();

        pensionTrxnDto.setKey(key);

        return new Submit<>(ApiVersion.CURRENT_VERSION, pensionCommencementDtoService, null, pensionTrxnDto).performOperation();
    }
}
