package com.bt.nextgen.api.ips.controller;

import com.bt.nextgen.api.ips.model.InvestmentPolicyStatementKey;
import com.bt.nextgen.api.ips.service.InvestmentPolicyStatementDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * This is the API to retrieve a list of investment portfolios.
 * 
 */
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class InvestmentPolicyStatementApiController {
    @Autowired
    private InvestmentPolicyStatementDtoService investmentPolicyStatementDtoService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.INVESTMENT_POLICY_STATEMENTS)
    @ResponseBody
    public ApiResponse getInvestmentPolicyStatements() {

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, investmentPolicyStatementDtoService,
                (List<ApiSearchCriteria>) null).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.GET_INVESTMENT_POLICY_STATEMENT)
    @ResponseBody
    public ApiResponse getInvestmentPolicyStatement(@PathVariable(UriMappingConstants.IPS_ID_URI_MAPPING) String ipsId) {
        InvestmentPolicyStatementKey key = new InvestmentPolicyStatementKey(ipsId);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, investmentPolicyStatementDtoService, key).performOperation();

    }
}
