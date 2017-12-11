package com.bt.nextgen.api.policy.controller;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.broker.model.BrokerKey;
import com.bt.nextgen.api.policy.model.AccountPolicyDto;
import com.bt.nextgen.api.policy.model.PolicyDocumentDto;
import com.bt.nextgen.api.policy.model.PolicyDto;
import com.bt.nextgen.api.policy.model.PolicyKey;
import com.bt.nextgen.api.policy.model.PolicyTrackingDto;
import com.bt.nextgen.api.policy.service.PolicyAccountsDtoService;
import com.bt.nextgen.api.policy.service.PolicyDocumentDtoService;
import com.bt.nextgen.api.policy.service.PolicyDtoService;
import com.bt.nextgen.api.policy.service.PolicySummaryDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.FindByPartialKey;
import com.bt.nextgen.core.api.operation.FindOne;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.SearchByKey;
import com.bt.nextgen.core.api.operation.Sort;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Api(value="Provides services to support insurance policies")
@Controller
@RequestMapping(produces = "application/json")
public class PolicyApiController {
    private static final Logger logger = LoggerFactory.getLogger(PolicyApiController.class);

    @Autowired
    private PolicyDtoService policyDtoService;

    @Autowired
    private PolicySummaryDtoService policySummaryDtoService;

    @Autowired
    private PolicyAccountsDtoService policyAccountsDtoService;

    @Autowired
    private PolicyDocumentDtoService policyDocumentDtoService;


    @PreAuthorize("isAuthenticated() and @acctPermissionService.hasTransactionPermission(#accountId, 'account.super.insurance.view')")
    @ApiOperation(value="Retrieve insurance policies for specific account ID", response = PolicyDto.class, responseContainer = "List")
    @RequestMapping(method = RequestMethod.GET, value = "${api.policy.v1.uri.accounts}")
    public
    @ResponseBody
    ApiResponse getPoliciesForAccount(@PathVariable(value = "account-id") @ApiParam(value="Encoded ID of account to load policies for", required = true) String accountId) {
        PolicyKey key = new PolicyKey();
        key.setAccountId(accountId);

        return new Sort<>(new SearchByKey<>(ApiVersion.CURRENT_VERSION, policyDtoService, key), "status;policyType").performOperation();
    }

    @PreAuthorize("isAuthenticated() and @acctPermissionService.hasTransactionPermission(#accountId, 'account.super.insurance.view')")
    @ApiOperation(value="Retrieve insurance policy details for a specific account ID and policy ID", response = PolicyDto.class)
    @RequestMapping(method = RequestMethod.GET, value = {"${api.policy.v1.uri.policydetails}", "${api.policy.v1.uri.benefits}"})
    public
    @ResponseBody
    ApiResponse getPolicyDetails(@PathVariable(value = "account-id") @ApiParam(value="Encoded ID of account to load policy for", required = true) String accountId,
                                 @PathVariable(value = "policy-id") @ApiParam(value="Encoded ID of policy to load policy for", required = true) String policyId) {
        PolicyKey key = new PolicyKey();
        key.setPolicyId(policyId);
        key.setAccountId(accountId);
        return new FindByPartialKey<>(ApiVersion.CURRENT_VERSION, policyDtoService, key).performOperation();
    }

    @PreAuthorize("isAuthenticated() and @acctPermissionService.hasTransactionPermission(#accountId, 'account.super.insurance.view')")
    @ApiOperation(value="Retrieve account policies for a specific account ID", response = AccountPolicyDto.class, responseContainer = "List")
    @RequestMapping(method = RequestMethod.GET, value = "${api.policy.v1.uri.relatedaccounts}")
    public
    @ResponseBody
    ApiResponse getAccountsForPolicy(@PathVariable(value = "account-id") @ApiParam(value="Encoded ID of account to load account policy for", required = true) String accountId) {
        final AccountKey accountKey = new AccountKey(accountId);

        return new Sort<>(new SearchByKey<>(ApiVersion.CURRENT_VERSION, policyAccountsDtoService, accountKey), "accountName").performOperation();
    }

    @PreAuthorize("isAuthenticated() and (@permissionBaseService.hasBasicPermission('account.insurance.applications.view') or " +
            "@permissionBaseService.hasBasicPermission('account.insurance.businessreport.view'))")
    @ApiOperation(value="Retrieve policies for F number, customer number and/or broker ID", response = PolicyTrackingDto.class, responseContainer = "List", notes = "You must specify at least one of the three parameters")
    @RequestMapping(method = RequestMethod.GET, value = "${api.policy.v1.uri.policies}")
    public
    @ResponseBody
    ApiResponse getPoliciesForFNumber(@RequestParam(value = "fnumber", required = false) @ApiParam(value="F number to load policies for") String fNumber,
                                      @RequestParam(value = "brokerid", required = false) @ApiParam(value="Broker ID to load policies for") String brokerId,
                                      @RequestParam(value = "customer", required = false) @ApiParam(value="Customer number to load policies for") String customerNumber) {
        List<ApiSearchCriteria> apiSearchCriterias = new ArrayList<>();
        apiSearchCriterias.add(new ApiSearchCriteria(Attribute.INSURANCE_FNUMBER, fNumber));
        if (StringUtils.isNotEmpty(customerNumber)) {
            apiSearchCriterias.add(new ApiSearchCriteria(Attribute.INSURANCE_CUSTOMER_NUMBER, customerNumber));
        }
        if (StringUtils.isNotEmpty(brokerId)) {
            apiSearchCriterias.add(new ApiSearchCriteria(Attribute.BROKER_ID, brokerId));
        }
        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, policySummaryDtoService, apiSearchCriterias).performOperation();
    }

    @PreAuthorize("isAuthenticated() and (@permissionBaseService.hasBasicPermission('account.insurance.applications.view') or " +
            "@permissionBaseService.hasBasicPermission('account.insurance.businessreport.view'))")
    @ApiOperation(value="Retrieve F numbers for a specific broker ID", response = PolicyTrackingDto.class)
    @RequestMapping(method = RequestMethod.GET, value = "${api.policy.v1.uri.fnumbers}")
    public
    @ResponseBody
    ApiResponse getFNumbersForIntermediary(@RequestParam(value = "brokerid", required = false) @ApiParam(value="Broker ID to load policies for") String brokerId) {
        if (brokerId != null) {
            BrokerKey brokerKey = new BrokerKey(brokerId);
            return new FindByKey<>(ApiVersion.CURRENT_VERSION, policySummaryDtoService, brokerKey).performOperation();
        }
        else {
            return new FindOne<>(ApiVersion.CURRENT_VERSION, policySummaryDtoService).performOperation();
        }
    }

    @PreAuthorize("isAuthenticated() and @acctPermissionService.hasTransactionPermission(#accountId, 'account.super.insurance.view')")
    @ApiOperation(value="Retrieve policy documents for a specific account ID", response = PolicyDocumentDto.class, responseContainer = "List")
    @RequestMapping(method = RequestMethod.GET, value = "${api.policy.v1.uri.documents}")
    public
    @ResponseBody
    ApiResponse getInsuranceDocuments(@PathVariable(value = "account-id") @ApiParam(value="Encoded account ID to load policy documents for", required = true) String accountId,
                                      @RequestParam(required = false, value = "sortby") @ApiParam(value="Sort by order") String orderby) {
        final AccountKey key = new AccountKey(accountId);
        return new Sort<>(new SearchByKey<>(ApiVersion.CURRENT_VERSION, policyDocumentDtoService, key), orderby).performOperation();
    }
}