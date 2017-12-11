package com.bt.nextgen.api.fees.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.fees.service.RCTInvoicesDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.web.controller.cash.util.Attribute;

/**
 * RCTIApiController is used to display recipient generated tax invoices 
 * 
 * @author M038389
 * 
 */
@Controller("RecipientCreatedTaxInvoicesApiControllerV2")
@RequestMapping(produces = "application/json")
public class RCTInvoicesApiController {
    @Autowired
    private RCTInvoicesDtoService dtoService;

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_dealergroup_account_reports')")
    @RequestMapping(method = RequestMethod.GET, value = "${api.fees.v2.uri.recipientCreatedTaxInvoices}")
    public @ResponseBody ApiResponse getRecipientCreatedTaxInvoices(
    		@RequestParam(value = "start-date") String startDate,
    		@RequestParam(value = "end-date") String endDate) {
    	
        List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(new ApiSearchCriteria(Attribute.START_DATE, SearchOperation.EQUALS, startDate, OperationType.DATE));
        criteria.add(new ApiSearchCriteria(Attribute.END_DATE, SearchOperation.EQUALS, endDate, OperationType.DATE));

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, dtoService, criteria).performOperation();
    }
}
