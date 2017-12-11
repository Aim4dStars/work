package com.bt.nextgen.api.tracking.controller;

import com.bt.nextgen.api.tracking.service.TrackingDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static com.bt.nextgen.core.api.ApiVersion.CURRENT_VERSION;
import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType.DATE;
import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.GREATER_THAN;
import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.LESS_THAN;
import static com.bt.nextgen.core.api.UriMappingConstants.CURRENT_VERSION_API;
import static com.bt.nextgen.core.api.UriMappingConstants.CURRENT_DIRECT_ONBOARDING_VERSION_API;
import static java.util.Arrays.asList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@RequestMapping(value = { CURRENT_VERSION_API, CURRENT_DIRECT_ONBOARDING_VERSION_API}, produces = APPLICATION_JSON_VALUE)
public class TrackingApiController {

    @Autowired
    private TrackingDtoService trackingAccountDtoService;

    @RequestMapping(method = RequestMethod.GET, value =  "tracking/search")
    @PreAuthorize("isAuthenticated() and hasPermission(#adviserId, 'isValidAdviser')")
    @ResponseBody
    public ApiResponse getDraftAccountsInInterval(@RequestParam String fromdate, @RequestParam String todate, @RequestParam(required = false) String adviser) {
        final List<ApiSearchCriteria> searchCriterias = asList(
            new ApiSearchCriteria("fromdate", GREATER_THAN, fromdate, DATE),
            new ApiSearchCriteria("todate", LESS_THAN, todate, DATE),
            new ApiSearchCriteria("adviserPositionId", adviser));
        return new SearchByCriteria<>(CURRENT_VERSION, trackingAccountDtoService, searchCriterias).performOperation();
    }
}
