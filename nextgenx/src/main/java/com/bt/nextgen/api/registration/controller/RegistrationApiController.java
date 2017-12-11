package com.bt.nextgen.api.registration.controller;

import com.bt.nextgen.api.registration.service.RegistrationDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class RegistrationApiController {

    @Autowired
    RegistrationDtoService registrationDtoService;


    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ACCOUNT_APPLICATION_STATUS)
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody
    ApiResponse getAccountApplicationStatus(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String portfolioId)
            throws Exception
    {
        String portfolioIdTest = EncodedString.toPlainText(portfolioId);
        List <ApiSearchCriteria> criteria = new ArrayList<ApiSearchCriteria>();
        criteria.add(new ApiSearchCriteria(Attribute.PORTFOLIO_ID, ApiSearchCriteria.SearchOperation.EQUALS, portfolioIdTest, ApiSearchCriteria.OperationType.STRING));

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, registrationDtoService, criteria).performOperation();
    }


    @RequestMapping(method = RequestMethod.POST,value = UriMappingConstants.ACCOUNT_NON_APPROVER_ACCEPT_TNC)
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public AjaxResponse acceptTnCNonApprovers1() {
        if(registrationDtoService.updateTnCForNonAprrover()) {
            return new AjaxResponse(true, null);
        }
        return new AjaxResponse(false, "Error while updating term and conditions.");
    }


}
