package com.bt.nextgen.api.corporateaction.v1.controller;

import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionApprovalDecisionListDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDtoKey;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDtoKey;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionApprovalDtoService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionApprovalListDtoService;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.Submit;

/**
 * This is the API to retrieve a list of Corporate Actions.
 * <p/>
 * start and end dates are optional, however, if one is provided the other must also provided.
 * <p/>
 * Currently does not require adviser ID, but this is to be considered in the future.
 */

@Controller
@RequestMapping(produces = "application/json")
@Api("Load corporate actions for trustee users")
public class CorporateActionApprovalApiController {
    private static final String CURRENT_VERSION = "v1_0";
    private static final String START_DATE = "startdate";
    private static final String END_DATE = "enddate";

    @Autowired
    private CorporateActionApprovalListDtoService corporateActionApprovalListDtoService;

    @Autowired
    private CorporateActionApprovalDtoService corporateActionApprovalDtoService;

    @Autowired
    @Qualifier("SecureJsonObjectMapper")
    private ObjectMapper objectMapper;

    /**
     * Retrieve a list of corporate actions applicable to trustee and IRG for a given date.
     * <p/>
     * start and end dates are optional, however, if one is provided the other must also provided.
     * <p/>
     *
     * @param startDate start date - optional
     * @param endDate   end date - required if start date is provided
     * @return Api response with data.
     */
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_security_events_approval')")
    @ApiOperation(value = "Load corporate actions for trustee", response = CorporateActionListDto.class)
    @RequestMapping(method = RequestMethod.GET, value = "${api.corporateaction.v1.uri.corporateActionApprovalList}")
    @ResponseBody
    public ApiResponse getCorporateActionApprovalList(
            @RequestParam(value = START_DATE, required = false) @ApiParam(value = "The start date to search from") String startDate,
            @RequestParam(value = END_DATE, required = false) @ApiParam(value = "The end date to search to") String endDate) {
        return new FindByKey<>(CURRENT_VERSION, corporateActionApprovalListDtoService,
                new CorporateActionListDtoKey(startDate, endDate, null, null, null)).performOperation();
    }


    /**
     * Submit trustee/IRG corporate action approval decision.
     * <p/>
     *
     * @param corporateActionApprovalDecisionListJson the corporate action approval list json
     * @return Api response with data.
     */
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Transact_security_events_approval')")
    @ApiOperation(value = "Save the approval status for corporate action(s)", response = CorporateActionApprovalDecisionListDto.class)
    @RequestMapping(method = RequestMethod.POST, value = "${api.corporateaction.v1.uri.corporateActionApprovalSubmit}")
    @ResponseBody
    public KeyedApiResponse<CorporateActionDtoKey> submitCorporateActionApprovalDecisions(
            @RequestBody @ApiParam(value = "The DTO object mapped by Spring", required = true) String
                    corporateActionApprovalDecisionListJson) throws IOException {

        String sanitisedApprovalDecisionListJson = JsonSanitizer.sanitize(corporateActionApprovalDecisionListJson);

        CorporateActionApprovalDecisionListDto corporateActionApprovalDecisionListDto =
                objectMapper.readerWithView(JsonViews.Write.class).forType(new TypeReference<CorporateActionApprovalDecisionListDto>() {
                }).readValue(sanitisedApprovalDecisionListJson);

        return new Submit<>(CURRENT_VERSION, corporateActionApprovalDtoService, null, corporateActionApprovalDecisionListDto)
                .performOperation();
    }
}
