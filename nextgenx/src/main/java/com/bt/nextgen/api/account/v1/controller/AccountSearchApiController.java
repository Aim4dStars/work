/**
 *
 */
package com.bt.nextgen.api.account.v1.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.account.v1.service.AccountSearchJsonDtoService;
import com.bt.nextgen.api.util.ApiConstants;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.web.controller.cash.util.Attribute;

/**
 * @author L095519
 *
 */
@Controller("AccountSearchApiControllerV1")
@RequestMapping(produces = "application/json")
@SuppressWarnings({ "squid:S1142", "squid:MethodCyclomaticComplexity" })
public class AccountSearchApiController {

    @Autowired
    private AccountSearchJsonDtoService accountSearchJsonDtoService;

    /**
     *  /ng/secure/api/accounts/v3_0/quicksearch
     * @param query
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v3.uri.quicksearch}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_intermediary_client_reports')")
    public
    @ResponseBody
    ApiResponse searchAccount(@RequestParam(required = true, value = ApiConstants.QUERY) String query) {
        final List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(new ApiSearchCriteria(Attribute.FILTER_FOR_ACCOUNT, ApiSearchCriteria.SearchOperation.EQUALS, query,
                ApiSearchCriteria.OperationType.STRING));
        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, accountSearchJsonDtoService, criteria).performOperation();

    }

}