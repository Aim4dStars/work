package com.bt.nextgen.api.beneficiary.controller;

import com.bt.nextgen.api.beneficiary.service.NominationTypesDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L067218 on 1/07/2016.
 */

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class SuperNominationTypeApiController {
    /**
     * DTO service for nomination types
     */
    @Autowired
    private NominationTypesDtoService nominationTypesDtoService;

    /**
     * Get nomination types for super account
     *
     * @return list of nomination types
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.SUPER_NOMINATION_TYPES)
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ApiResponse getSuperNominationTypes(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accId,
                                               @RequestParam(value = "filter_for_account", defaultValue = "false") String filter) {
        List<ApiSearchCriteria> searchCriteriaList = new ArrayList<>();
        searchCriteriaList.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS, accId, ApiSearchCriteria.OperationType.STRING));
        searchCriteriaList.add(new ApiSearchCriteria(Attribute.FILTER_FOR_ACCOUNT, ApiSearchCriteria.SearchOperation.EQUALS, filter, ApiSearchCriteria.OperationType.STRING));
        return new SearchByCriteria <>(ApiVersion.CURRENT_VERSION, nominationTypesDtoService, searchCriteriaList).performOperation();

    }

}

