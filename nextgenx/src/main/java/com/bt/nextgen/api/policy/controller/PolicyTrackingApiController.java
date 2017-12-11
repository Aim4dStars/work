package com.bt.nextgen.api.policy.controller;

import com.bt.nextgen.api.policy.model.CustomerKey;
import com.bt.nextgen.api.policy.model.PolicyDto;
import com.bt.nextgen.api.policy.model.PolicyTrackingIdentifier;
import com.bt.nextgen.api.policy.service.PolicyTrackingDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.SearchByKeyedCriteria;
import com.btfin.panorama.core.util.StringUtil;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for policy tracking screen
 */
@Api(value="Provides services to support insurance policy tracking details")
@Controller
@RequestMapping(produces = "application/json")
public class PolicyTrackingApiController {

    @Autowired
    private PolicyTrackingDtoService policyTrackingDtoService;

    @PreAuthorize("isAuthenticated() and @permissionBaseService.hasBasicPermission('account.insurance.applications.view')")
    @ApiOperation(value="Retrieve policy tracking identifiers for a specific F number (with optional broker ID)", response = PolicyTrackingIdentifier.class, responseContainer = "List")
    @RequestMapping(method = RequestMethod.GET, value = "${api.policy.v1.uri.tracking}")
    public @ResponseBody
    ApiResponse getPolicies(@RequestParam(value = "fnumber") @ApiParam(value="F number to fetch policy tracking IDs for", required = true) String fNumbers,
                            @RequestParam(value = "brokerid", required = false) @ApiParam(value="Broker ID to fetch policy tracking IDs for") String brokerId) {
        List<ApiSearchCriteria> apiSearchCriterias = new ArrayList<>();
        apiSearchCriterias.add(new ApiSearchCriteria(Attribute.INSURANCE_FNUMBER, fNumbers));
        if (StringUtil.isNotNullorEmpty(brokerId))
            apiSearchCriterias.add(new ApiSearchCriteria(Attribute.BROKER_ID, brokerId));
        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, policyTrackingDtoService, apiSearchCriterias).performOperation();
    }

    @PreAuthorize("isAuthenticated() and @permissionBaseService.hasBasicPermission('account.insurance.applications.view')")
    @ApiOperation(value="Retrieve underwriting notes for a specific customer ID and F number (with optional broker ID)", response = PolicyTrackingIdentifier.class, responseContainer = "List")
    @RequestMapping(method = RequestMethod.GET, value = "${api.policy.v1.uri.underwritenotes}")
    public @ResponseBody
    ApiResponse getUnderwritingNotes(@PathVariable(value = "customer-id") @ApiParam(value="Customer ID to fetch underwriting notes for", required = true) String customerNumber,
                                     @RequestParam(value = "fnumber") @ApiParam(value="F number to fetch underwriting notes for", required = true) String fNumbers,
                                     @RequestParam(value = "brokerid", required = false) @ApiParam(value="Broker ID to fetch underwriting notes for") String brokerId) {
        CustomerKey customerKey = new CustomerKey(customerNumber);
        List<ApiSearchCriteria> apiSearchCriterias = new ArrayList<>();
        apiSearchCriterias.add(new ApiSearchCriteria(Attribute.INSURANCE_FNUMBER, fNumbers));
        if (StringUtil.isNotNullorEmpty(brokerId))
            apiSearchCriterias.add(new ApiSearchCriteria(Attribute.BROKER_ID, brokerId));
        return new SearchByKeyedCriteria<>(ApiVersion.CURRENT_VERSION, policyTrackingDtoService, customerKey,apiSearchCriterias).performOperation();
    }
}
