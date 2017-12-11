package com.bt.nextgen.api.corporateaction.v1.controller;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDtoKey;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDtoKey;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionNotificationDto;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionDetailsDtoService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionListDtoService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionListWithMetadataDtoService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionNotificationDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionGroup;
import com.bt.nextgen.web.controller.cash.util.Attribute;

/**
 * This is the API to retrieve a list of Corporate Actions.
 * <p/>
 * start and end dates are optional, however, if one is provided the other must also provided.
 * <p/>
 * Currently does not require adviser ID, but this is to be considered in the future.
 */

@Controller
@RequestMapping(produces = "application/json")
@Api("Load corporate actions and corporate action details")
public class CorporateActionApiController {
    private static final String CURRENT_VERSION = "v1_0";
    private static final String CA_ID = "ca-id";
    private static final String START_DATE = "startdate";
    private static final String END_DATE = "enddate";
    private static final String CA_TYPE = "type";
    private static final String ACCOUNT_ID = "accountId";
    private static final String IPS_ID = "ipsId";
    private static final String METADATA = "metadata";
    private static final String SUMMARY = "summary";

    @Autowired
    private CorporateActionListDtoService corporateActionListDtoService;

    @Autowired
    private CorporateActionListWithMetadataDtoService corporateActionListWithMetadataDtoService;

    @Autowired
    private CorporateActionDetailsDtoService corporateActionDetailsDtoService;

    @Autowired
    private CorporateActionNotificationDtoService corporateActionNotificationDtoService;

    /**
     * Retrieve a list of corporate actions for a given date.
     * <p/>
     * start and end dates are optional, however, if one is provided the other must also provided.
     * <p/>
     * Currently does not require adviser ID, but this is to be considered in the future. @param startDate the date to search from,
     * inclusive (optional).
     *
     * @param startDate start date - optional
     * @param endDate   end date - required if start date is provided
     * @param type      the corporate action type: voluntary/mandatory
     * @param accountId encoded account ID - optional
     * @param ipsId     ips ID - optional
     * @param metadata  true/false.  If true, an object with metadata and the corporate action list will be returned
     * @return Api response with data.
     */
    @PreAuthorize("isAuthenticated() and (hasPermission(null, 'View_security_events') OR hasPermission(null, 'View_intermediary_reports')"
            + " OR hasPermission(null, 'View_model_portfolios') OR @corporateActionPermissions.checkPermissionForUser())")
    @ApiOperation(value = "Load corporate actions for adviser/IM/DG/ASIM/direct", response = CorporateActionListDto.class)
    @RequestMapping(method = RequestMethod.GET, value = "${api.corporateaction.v1.uri.corporateActions}")
    @ResponseBody
    public ApiResponse getCorporateActions(
            @RequestParam(value = START_DATE, required = false) @ApiParam(value = "The start date to search from") String startDate,
            @RequestParam(value = END_DATE, required = false) @ApiParam(value = "The end date to search to") String endDate,
            @RequestParam(value = CA_TYPE, required = false) @ApiParam(value = "The corporate action group: voluntary or mandatory")
                    String type,
            @RequestParam(value = ACCOUNT_ID, required = false) @ApiParam(value = "The account ID to limit the search to") String accountId,
            @RequestParam(value = IPS_ID, required = false) @ApiParam(value = "The IPS ID - used only by IM and DG") String ipsId,
            @RequestParam(value = METADATA, required = false) @ApiParam(value = "Optional flag to return meta-data about the result")
                    String metadata) {
        if (Boolean.valueOf(metadata)) {
            return new FindByKey<>(CURRENT_VERSION, corporateActionListWithMetadataDtoService,
                    new CorporateActionListDtoKey(startDate, endDate, type, accountId, ipsId)).performOperation();
        }

        final List<ApiSearchCriteria> criteria = new ArrayList<>();

        // Optional criteria
        criteria.add(new ApiSearchCriteria(Attribute.START_DATE, SearchOperation.EQUALS, startDate, OperationType.DATE));
        criteria.add(new ApiSearchCriteria(Attribute.END_DATE, SearchOperation.EQUALS, endDate, OperationType.DATE));
        criteria.add(new ApiSearchCriteria(Attribute.CA_TYPE, SearchOperation.EQUALS, type, OperationType.STRING));
        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accountId, OperationType.STRING));
        criteria.add(new ApiSearchCriteria(Attribute.PORTFOLIO_MODEL, SearchOperation.EQUALS, ipsId, OperationType.STRING));

        return new SearchByCriteria<>(CURRENT_VERSION, corporateActionListDtoService, criteria).performOperation();
    }

    /**
     * Retrieve a list of corporate actions for ROA for a given date
     * <p/>
     * start and end dates are optional, however, if one is provided the other must also provided.
     * <p/>
     * Currently does not require adviser ID, but this is to be considered in the future. @param startDate the date to search from,
     * inclusive (optional).
     *
     * @param endDate the date to search to (optional)
     * @return Api response with data.
     */
    @PreAuthorize("isAuthenticated() and (hasPermission(null, 'View_intermediary_reports') OR hasPermission(null, " +
            "'View_model_portfolios'))")
    @ApiOperation(value = "Load ROA corporate actions.  This is currently unused due to the project being deferred", response =
            CorporateActionDto.class, responseContainer = "List")
    @RequestMapping(method = RequestMethod.GET, value = "${api.corporateaction.v1.uri.roaCorporateActions}")
    @ResponseBody
    public ApiResponse getRoaCorporateActions(
            @RequestParam(value = START_DATE, required = false) @ApiParam(value = "The start date to search from") String startDate,
            @RequestParam(value = END_DATE, required = false) @ApiParam(value = "The start date to search from") String endDate) {
        final List<ApiSearchCriteria> criteria = new ArrayList<>();

        // Optional criteria
        criteria.add(new ApiSearchCriteria(Attribute.START_DATE, SearchOperation.EQUALS, startDate, OperationType.DATE));
        criteria.add(new ApiSearchCriteria(Attribute.END_DATE, SearchOperation.EQUALS, endDate, OperationType.DATE));
        criteria.add(new ApiSearchCriteria(Attribute.CA_TYPE, SearchOperation.EQUALS, CorporateActionGroup.VOLUNTARY.getCode(),
                OperationType.STRING));
        criteria.add(new ApiSearchCriteria(Attribute.CA_ROA, SearchOperation.EQUALS, Boolean.TRUE.toString(), OperationType.BOOLEAN));

        return new SearchByCriteria<>(CURRENT_VERSION, corporateActionListDtoService, criteria).performOperation();
    }

    /**
     * Retrieve corporate action details.
     * <p/>
     *
     * @param corporateActionIdString the corporate action ID
     * @return Api response with data.
     */
    @PreAuthorize("isAuthenticated() and (hasPermission(null, 'View_security_events') OR hasPermission(null, 'View_intermediary_reports')" +
            " OR hasPermission(null, 'View_model_portfolios') OR hasPermission(null, 'View_security_events_approval')" +
            " OR @corporateActionPermissions.checkPermissionForUser())")
    @ApiOperation(value = "Load corporate actions details along with relevant accounts for adviser/IM/DG/ASIM/direct", response =
            CorporateActionDetailsDto.class)
    @RequestMapping(method = RequestMethod.GET, value = "${api.corporateaction.v1.uri.corporateAction}")
    @ResponseBody
    public ApiResponse getCorporateActionDetails(
            @PathVariable(CA_ID) @ApiParam(value = "The encrypted corporate action order number", required = true) String
                    corporateActionIdString,
            @RequestParam(value = ACCOUNT_ID, required = false) @ApiParam(value = "The account ID to limit the result to") String
                    accountIdString,
            @RequestParam(value = IPS_ID, required = false) @ApiParam(value = "The IPS ID to limit the result to") String ipsId,
            @RequestParam(value = SUMMARY, required = false) @ApiParam(value = "The flag to exclude accounts from corporate action " +
                    "details") String summary) {
        final String corporateActionId = EncodedString.toPlainText(corporateActionIdString);

        return new FindByKey<>(CURRENT_VERSION, corporateActionDetailsDtoService,
                new CorporateActionDtoKey(corporateActionId,
                        StringUtils.isEmpty(accountIdString) ? "" : EncodedString.toPlainText(accountIdString), ipsId,
                        Boolean.parseBoolean(summary))).performOperation();
    }

    /**
     * Retrieve count for pending corporate event.
     * <p/>
     *
     * @return Api response with data.
     */
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Load corporate actions events notification count", response = CorporateActionNotificationDto.class)
    @RequestMapping(method = RequestMethod.GET, value = "${api.corporateaction.v1.uri.corporateActionsPendingCount}")
    @ResponseBody
    public ApiResponse getCorporateEventsNotificationCount(
            @RequestParam(value = START_DATE, required = false) @ApiParam(value = "The start date to search from") String startDate,
            @RequestParam(value = END_DATE, required = false) @ApiParam(value = "The end date to search to") String endDate) {
        final List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(new ApiSearchCriteria(Attribute.START_DATE, SearchOperation.EQUALS, startDate, OperationType.DATE));
        criteria.add(new ApiSearchCriteria(Attribute.END_DATE, SearchOperation.EQUALS, endDate, OperationType.DATE));

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, corporateActionNotificationDtoService, criteria).performOperation();
    }
}
