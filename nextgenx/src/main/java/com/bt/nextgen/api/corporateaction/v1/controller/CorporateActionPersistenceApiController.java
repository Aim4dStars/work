package com.bt.nextgen.api.corporateaction.v1.controller;

import java.io.IOException;
import java.util.List;

import com.btfin.panorama.core.security.encryption.EncodedString;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDtoKey;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionPersistenceDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionPortfolioModelDto;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionPersistenceDtoService;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
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
@Api("This API enables saving of elected options for corporate actions")
public class CorporateActionPersistenceApiController {
    private static final Logger logger = LoggerFactory.getLogger(CorporateActionPersistenceApiController.class);

    private static final String CURRENT_VERSION = "v1_0";
    private static final String CA_ID = "ca-id";
    private static final String CLOSE_DATE = "closeDate";
    private static final String OPTIONS = "options";
    private static final String ACCOUNTS = "accounts";
    private static final String PORTFOLIO_MODELS = "portfolioModels";

    @Autowired
    private CorporateActionPersistenceDtoService corporateActionPersistenceDtoService;

    @Autowired
    @Qualifier("SecureJsonObjectMapper")
    private ObjectMapper objectMapper;

    /**
     * Save corporate action elections.
     *
     * @param corporateActionIdString the corporate action ID
     * @param closeDate               the panorama close date
     * @param options                 the list of options
     * @param accounts                the list of accounts
     * @return CorporateActionPersistenceDto with status
     */
    @PreAuthorize("isAuthenticated() and (hasPermission(null, 'Transact_security_events') OR hasPermission(null, " +
            "'Submit_trade_to_executed') OR hasPermission(null, 'Start_upload_model_file') OR hasPermission(null, " +
            "'Create_model_portfolios') OR @corporateActionPermissions.checkPermissionForUser())")
    @ApiOperation(value = "Save corporate action elections.  NOTE: This API does not support election of over 2kB on Internet Explorer.  " +
            "Use saveElections instead.", response = CorporateActionPersistenceDto.class)
    @RequestMapping(method = RequestMethod.POST, value = "${api.corporateaction.v1.uri.corporateActionSave}")
    @ResponseBody
    public KeyedApiResponse<CorporateActionDtoKey> saveCorporateActionElections(
            @PathVariable(CA_ID) @ApiParam(value = "The encrypted corporate action order number", required = true) String
                    corporateActionIdString,
            @RequestParam(value = CLOSE_DATE, required = true) @ApiParam(value = "The close date of corporate action", required = true)
                    String closeDate,
            @RequestParam(value = OPTIONS, required = true) @ApiParam(value = "The available options in json notation", required = true)
                    String options,
            @RequestParam(value = ACCOUNTS, required = true) @ApiParam(value = "The accounts' elections in json notation", required =
                    true) String accounts,
            @RequestParam(value = PORTFOLIO_MODELS, required = false) @ApiParam(value = "The portfolio models' elections in json " +
                    "notation") String portfolioModels) {

        final CorporateActionPersistenceDto persistenceDto = createCorporateActionPersistenceDto(corporateActionIdString,
                closeDate, options, accounts, portfolioModels, true);

        return new Submit<>(CURRENT_VERSION, corporateActionPersistenceDtoService, null, persistenceDto).performOperation();
    }

    /**
     * Save corporate action elections.  This one does proper POST.  The one before is to be deprecated.
     *
     * @param corporateActionIdString       the corporate action ID
     * @param corporateActionPersistenceJson the mapped persistence dto string
     * @return CorporateActionPersistenceDto with status
     */
    @PreAuthorize("isAuthenticated() and (hasPermission(null, 'Transact_security_events') OR hasPermission(null, " +
            "'Submit_trade_to_executed') OR hasPermission(null, 'Start_upload_model_file') OR hasPermission(null, " +
            "'Create_model_portfolios') OR @corporateActionPermissions.checkPermissionForUser())")
    @ApiOperation(value = "Save corporate action elections.", response = CorporateActionPersistenceDto.class)
    @RequestMapping(method = RequestMethod.POST, value = "${api.corporateaction.v1.uri.corporateActionSaveElections}")
    @ResponseBody
    public KeyedApiResponse<CorporateActionDtoKey> saveCorporateActionElectionsByPost(
            @PathVariable(CA_ID) @ApiParam(value = "The encrypted corporate action order number", required = true) String
                    corporateActionIdString,
            @RequestBody @ApiParam(value = "The persistence DTO json string", required = true) String
                    corporateActionPersistenceJson) throws IOException {
        final String corporateActionId = decodeAndVerifyCorporateActionId(corporateActionIdString);

        String sanitizedPersistenceJson = JsonSanitizer.sanitize(corporateActionPersistenceJson);

        CorporateActionPersistenceDto corporateActionPersistenceDto = objectMapper.readerWithView(JsonViews.Write.class).forType(
                new TypeReference<CorporateActionPersistenceDto>() {
                }).readValue(sanitizedPersistenceJson);

        CorporateActionPersistenceDto persistenceDto =
                new CorporateActionPersistenceDto(corporateActionId, corporateActionPersistenceDto.getCloseDate(),
                        corporateActionPersistenceDto.getOptions(), corporateActionPersistenceDto.getAccounts(),
                        corporateActionPersistenceDto.getPortfolioModels(), corporateActionPersistenceDto.getIpsId(),
                        corporateActionPersistenceDto.isBulkSave());

        return new Submit<>(CURRENT_VERSION, corporateActionPersistenceDtoService, null, persistenceDto).performOperation();
    }

    /**
     * Common method to create CorporateActionPersistenceDto object
     *
     * @param corporateActionIdString the corporate action ID
     * @param closeDate               the panorama close date
     * @param options                 the list of options
     * @param accounts                the list of accounts
     * @param updateMode              set to true if update instead of delete-insert
     * @return CorporateActionPersistenceDto with status
     */
    private CorporateActionPersistenceDto createCorporateActionPersistenceDto(String corporateActionIdString, String closeDate,
                                                                              String options, String accounts, String portfolioModels,
                                                                              boolean updateMode) {
        final String corporateActionId = decodeAndVerifyCorporateActionId(corporateActionIdString);

        List<CorporateActionOptionDto> optionDtoList;
        List<CorporateActionAccountDetailsDto> accountDetailsDtoList = null;
        List<ImCorporateActionPortfolioModelDto> portfolioModelDtoList = null;

        try {
            String sanitizeOptions = JsonSanitizer.sanitize(options);
            optionDtoList = objectMapper.readerWithView(JsonViews.Write.class).forType(new TypeReference<List<CorporateActionOptionDto>>() {
            }).readValue(sanitizeOptions);

            if (portfolioModels != null) {
                String sanitizedPortfolioModels = JsonSanitizer.sanitize(portfolioModels);
                portfolioModelDtoList = objectMapper.readerWithView(JsonViews.Write.class)
                                                    .forType(new TypeReference<List<ImCorporateActionPortfolioModelDto>>() {
                                                    }).readValue(sanitizedPortfolioModels);
            }

            if (accounts != null) {
                String sanitizedAccounts = JsonSanitizer.sanitize(accounts);
                accountDetailsDtoList = objectMapper.readerWithView(JsonViews.Write.class)
                                                    .forType(new TypeReference<List<CorporateActionAccountDetailsDto>>() {
                                                    }).readValue(sanitizedAccounts);
            }
        } catch (IOException e) {
            logger.error("IOException", e);
            throw new IllegalArgumentException("Unable to map corporate action fields: " + e);
        }

        return new CorporateActionPersistenceDto(corporateActionId, new DateTime(closeDate), optionDtoList,
                accountDetailsDtoList, portfolioModelDtoList, null, updateMode);
    }

    private String decodeAndVerifyCorporateActionId(String corporateActionIdString) {
        String corporateActionId = EncodedString.toPlainText(corporateActionIdString);

        if (!StringUtils.isNumeric(corporateActionId)) {
            throw new BadRequestException("Invalid corporate action order number");
        }

        return corporateActionId;
    }
}
