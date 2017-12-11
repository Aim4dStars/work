package com.bt.nextgen.api.rollover.v1.controller;

import com.bt.nextgen.api.rollover.v1.model.ReceivedContributionDto;
import com.bt.nextgen.api.rollover.v1.model.ReceivedRolloverFundDto;
import com.bt.nextgen.api.rollover.v1.model.RolloverHistoryDto;
import com.bt.nextgen.api.rollover.v1.model.RolloverInDto;
import com.bt.nextgen.api.rollover.v1.model.RolloverInDtoImpl;
import com.bt.nextgen.api.rollover.v1.model.RolloverKey;
import com.bt.nextgen.api.rollover.v1.service.ReceivedContributionDtoService;
import com.bt.nextgen.api.rollover.v1.service.ReceivedRolloverDtoService;
import com.bt.nextgen.api.rollover.v1.service.RolloverHistoryDtoService;
import com.bt.nextgen.api.rollover.v1.service.RolloverInDtoService;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.api.validation.ErrorMapper;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.google.json.JsonSanitizer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(produces = "application/json")
@Api(description = "Submit rollovers in or view history of rollovers requested")
public class RolloverInApiController {

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private RolloverInDtoService rolloverService;

    @Autowired
    private ReceivedRolloverDtoService recRolloverService;

    @Autowired
    private RolloverHistoryDtoService rolloverHistoryService;

    @Autowired
    private ReceivedContributionDtoService contributionService;

    @Autowired
    @Qualifier("RolloverDtoErrorMapperImplV1")
    private ErrorMapper errorMapper;

    @Autowired
    private SecureJsonObjectMapper mapper;

    @RequestMapping(method = RequestMethod.POST, value = "${api.rollover.v1.uri.rolloverin}", produces = "application/json")
    @ApiOperation(value = "Submit a new rollover in", response = RolloverInDto.class)
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Make_a_payment_linked_accounts')")
    public @ResponseBody
    KeyedApiResponse<RolloverKey> create(
            @PathVariable("account-id") @ApiParam(value = "ID of account to rollover into", required = true) String accountId,
            @RequestParam(value = "rollInData", required = true) @ApiParam(value = "RolloverInDtoV1 in JSON format", required = true) String rolloverDetails)
            throws IOException {

        if (!profileService.isEmulating()) {
            RolloverInDto rollIn = mapper.readerWithView(JsonViews.Write.class).forType(RolloverInDtoImpl.class)
                    .readValue(JsonSanitizer.sanitize(rolloverDetails));
            if (accountId.equals(rollIn.getKey().getAccountId())) {
                return new Submit<>(ApiVersion.CURRENT_VERSION, rolloverService, errorMapper, rollIn).performOperation();
            }
        }
        throw new AccessDeniedException("Access Denied");
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.rollover.v1.uri.saverollover}", produces = "application/json")
    @ApiOperation(value = "Save a rollover for submission later", response = RolloverInDto.class)
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Make_a_payment_linked_accounts')")
    public @ResponseBody
    KeyedApiResponse<RolloverKey> save(
            @PathVariable("account-id") @ApiParam(value = "ID of account to rollover into", required = true) String accountId,
            @RequestParam(value = "rollInData", required = true) @ApiParam(value = "RolloverInDtoV1 in json format", required = true) String rolloverDetails)
            throws IOException {

        if (!profileService.isEmulating()) {
            RolloverInDto rollIn = mapper.readerWithView(JsonViews.Write.class).forType(RolloverInDtoImpl.class)
                    .readValue(JsonSanitizer.sanitize(rolloverDetails));
            if (accountId.equals(rollIn.getKey().getAccountId())) {
                RolloverInDto responseDto = rolloverService.save(rollIn, new ServiceErrorsImpl());
                return new KeyedApiResponse<RolloverKey>(ApiVersion.CURRENT_VERSION, responseDto.getKey(), responseDto);
            }
        }
        throw new AccessDeniedException("Access Denied");
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.rollover.v1.uri.discardrollover}", produces = "application/json")
    @ApiOperation(value = "Discard an existing saved rollover", response = RolloverInDto.class)
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Make_a_payment_linked_accounts')")
    public @ResponseBody
    KeyedApiResponse<RolloverKey> discard(
            @PathVariable("account-id") @ApiParam(value = "ID of account which owns this rollover", required = true) String accountId,
            @PathVariable("rollover-id") @ApiParam(value = "ID of rollover to discard", required = true) String rolloverId)
            throws IOException {
        if (!profileService.isEmulating()) {
            RolloverKey key = new RolloverKey(accountId, rolloverId);
            RolloverInDto responseDto = rolloverService.discard(key, new ServiceErrorsImpl());
            return new KeyedApiResponse<RolloverKey>(ApiVersion.CURRENT_VERSION, responseDto.getKey(), responseDto);
        }
        throw new AccessDeniedException("Access Denied");
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.rollover.v1.uri.loadrollover}", produces = "application/json")
    @ApiOperation(value = "Load an existing rollover", response = RolloverInDto.class)
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Make_a_payment_linked_accounts')")
    public @ResponseBody
    KeyedApiResponse<RolloverKey> load(
            @PathVariable("account-id") @ApiParam(value = "ID of account which owns this rollover", required = true) String accountId,
            @PathVariable("rollover-id") @ApiParam(value = "ID of rollover to load details for", required = true) String rolloverId)
            throws IOException {

        RolloverKey key = new RolloverKey(accountId, rolloverId);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, rolloverService, key).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.rollover.v1.uri.rolloverhistory}", produces = "application/json")
    @ApiOperation(value = "Retrieve a history of submitted rollovers", response = RolloverHistoryDto.class, responseContainer = "List")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Make_a_payment_linked_accounts')")
    public @ResponseBody
    ApiResponse getRolloverHistory(
            @PathVariable("account-id") @ApiParam(value = "Encoded ID of account to load request history for", required = true) String accountId,
            @RequestParam(value = "start-date", required = false) @ApiParam(value = "Optional - date range start date", required = false) String startDate,
            @RequestParam(value = "end-date", required = false) @ApiParam(value = "Optional - date range end date", required = false) String endDate)
            throws IOException {

            List<ApiSearchCriteria> criteria = new ArrayList<>();
            criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accountId, OperationType.STRING));
            criteria.add(new ApiSearchCriteria(Attribute.START_DATE, SearchOperation.EQUALS, startDate, OperationType.DATE));
            criteria.add(new ApiSearchCriteria(Attribute.END_DATE, SearchOperation.EQUALS, endDate, OperationType.DATE));

            return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, rolloverHistoryService, criteria).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.rollover.v1.uri.receivedrollover}", produces = "application/json")
    @ApiOperation(value = "Retrieve rollovers that have been received by the specified account.", response = ReceivedRolloverFundDto.class, responseContainer = "List")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Make_a_payment_linked_accounts')")
    public @ResponseBody
    ApiResponse getReceivedRollover(
            @PathVariable("account-id") @ApiParam(value = "Encoded ID of account to load received rollovers for", required = true) String accountId)
            throws IOException {
        List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accountId, OperationType.STRING));
        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, recRolloverService, criteria).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.rollover.v1.uri.receivedcontribution}", produces = "application/json")
    @ApiOperation(value = "Retrieve contributions that have been received by the specified account.", response = ReceivedContributionDto.class, responseContainer = "List")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Make_a_payment_linked_accounts')")
    public @ResponseBody
    ApiResponse getReceivedContribution(
            @PathVariable("account-id") @ApiParam(value = "Encoded ID of account to load received contributions for", required = true) String accountId)
            throws IOException {
        List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accountId, OperationType.STRING));
        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, contributionService, criteria).performOperation();
    }

}
