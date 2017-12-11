package com.bt.nextgen.api.corporateaction.v1.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import ch.lambdaj.function.convert.Converter;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
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
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionPersistenceDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionPortfolioModelDto;
import com.bt.nextgen.api.corporateaction.v1.permission.CorporateActionPermissionService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionCommonService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionElectionDtoService;
import com.bt.nextgen.api.corporateaction.v1.validation.CorporateActionAccountDetailsDtoErrorMapper;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.exception.AccessDeniedException;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.on;

/**
 * This is the API to retrieve a list of Corporate Actions.
 * <p/>
 * start and end dates are optional, however, if one is provided the other must also provided.
 * <p/>
 * Currently does not require adviser ID, but this is to be considered in the future.
 */

@Controller
@RequestMapping(produces = "application/json")
@Api("This API enables submission of elected options for corporate actions")
public class CorporateActionElectionApiController {
    private static final Logger logger = LoggerFactory.getLogger(CorporateActionElectionApiController.class);

    private static final String CURRENT_VERSION = "v1_0";
    private static final String CA_ID = "ca-id";
    private static final String OPTIONS = "options";
    private static final String ACCOUNTS = "accounts";
    private static final String PORTFOLIO_MODELS = "portfolioModels";
    private static final String IPS_ID = "ipsId";

    @Autowired
    @Qualifier("corporateActionElectionDtoService")
    private CorporateActionElectionDtoService corporateActionElectionDtoService;

    @Autowired
    @Qualifier("imCorporateActionElectionDtoService")
    private CorporateActionElectionDtoService imCorporateActionElectionDtoService;

    @Autowired
    private CorporateActionAccountDetailsDtoErrorMapper corporateActionAccountDetailsDtoErrorMapper;

    @Autowired
    private CorporateActionCommonService commonService;

    @Autowired
    private CorporateActionPermissionService permissionService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    @Qualifier("SecureJsonObjectMapper")
    private ObjectMapper objectMapper;

    /**
     * Submit corporate action account election details.
     * <p/>
     *
     * @param corporateActionIdString the corporate action ID
     * @return Api response with data.
     */
    @PreAuthorize("isAuthenticated() and (hasPermission(null, 'Transact_security_events') OR hasPermission(null, " +
            "'Submit_trade_to_executed') OR hasPermission(null, 'Start_upload_model_file') OR hasPermission(null, " +
            "'Create_model_portfolios') OR @corporateActionPermissions.checkPermissionForUser())")
    @ApiOperation(value = "Submit corporate action elections.  Variety of DTO type will be returned depending on corporate action type.  " +
            "NOTE: This API does not support election of over 2kB on Internet Explorer.  Use submitElections instead.", response =
            CorporateActionElectionDetailsBaseDto.class)
    @RequestMapping(method = RequestMethod.POST, value = "${api.corporateaction.v1.uri.corporateActionSubmit}")
    @ResponseBody
    public KeyedApiResponse<CorporateActionDtoKey> submitCorporateActionElections(
            @PathVariable(CA_ID) @ApiParam(value = "The encrypted corporate action order number", required = true) String
                    corporateActionIdString,
            @RequestParam(value = OPTIONS, required = true) @ApiParam(value = "The available options in json notation", required = true)
                    String options,
            @RequestParam(value = ACCOUNTS, required = false) @ApiParam(value = "The accounts' election details in json notation") String
                    accounts,
            @RequestParam(value = PORTFOLIO_MODELS, required = false) @ApiParam(value = "The portfolio models' election details json " +
                    "notation") String portfolioModels,
            @RequestParam(value = IPS_ID, required = false) @ApiParam(value = "The IPS ID") String ipsId) {

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

        return submitCommon(corporateActionIdString,
                new CorporateActionPersistenceDto(corporateActionIdString, null, optionDtoList, accountDetailsDtoList,
                        portfolioModelDtoList, ipsId, false));
    }

    /**
     * Submit corporate action account election details - this one uses proper POST method.  The one before this is to be deprecated.
     * It is not quite worth creating a new version all together as it is a minor change.
     * <p/>
     *
     * @param corporateActionIdString        the corporate action ID
     * @param corporateActionPersistenceJson the mapped persistence dto string
     * @return Api response with data.
     */
    @PreAuthorize("isAuthenticated() and (hasPermission(null, 'Transact_security_events') OR hasPermission(null, " +
            "'Submit_trade_to_executed') OR hasPermission(null, 'Start_upload_model_file') OR hasPermission(null, " +
            "'Create_model_portfolios') OR @corporateActionPermissions.checkPermissionForUser())")
    @ApiOperation(value = "Submit corporate action elections.  Variety of DTO type will be returned depending on corporate action type.",
            response = CorporateActionElectionDetailsBaseDto.class)
    @RequestMapping(method = RequestMethod.POST, value = "${api.corporateaction.v1.uri.corporateActionSubmitElections}")
    @ResponseBody
    public KeyedApiResponse<CorporateActionDtoKey> submitCorporateActionElectionsByPost(
            @PathVariable(CA_ID) @ApiParam(value = "The encrypted corporate action order number", required = true) String
                    corporateActionIdString,
            @RequestBody @ApiParam(value = "The persistence DTO json string", required = true) String corporateActionPersistenceJson)
            throws IOException {

        String sanitizedPersistenceJson = JsonSanitizer.sanitize(corporateActionPersistenceJson);

        CorporateActionPersistenceDto corporateActionPersistenceDto = objectMapper.readerWithView(JsonViews.Write.class).forType(
                new TypeReference<CorporateActionPersistenceDto>() {
                }).readValue(sanitizedPersistenceJson);

        return submitCommon(corporateActionIdString, corporateActionPersistenceDto);
    }

    /**
     * Common submit
     *
     * @param corporateActionIdString       the encrypted ID string
     * @param corporateActionPersistenceDto the persistent dto object
     * @return keyed api response object
     */
    private KeyedApiResponse<CorporateActionDtoKey> submitCommon(String corporateActionIdString,
                                                                 CorporateActionPersistenceDto corporateActionPersistenceDto) {
        final String corporateActionId = EncodedString.toPlainText(corporateActionIdString);

        if (!StringUtils.isNumeric(corporateActionId)) {
            throw new BadRequestException("Invalid corporate action order number");
        }

        boolean isDg = commonService.getUserProfileService().isDealerGroup();
        boolean isIm = commonService.getUserProfileService().isInvestmentManager();
        boolean isPm = commonService.getUserProfileService().isPortfolioManager();

        if (isDg || isPm) {
            if (singlePortfolioPresent(corporateActionPersistenceDto.getPortfolioModels())) {
                // Submit model and accounts
                return new Submit<>(CURRENT_VERSION, imCorporateActionElectionDtoService, corporateActionAccountDetailsDtoErrorMapper,
                        new ImCorporateActionElectionDetailsDto(corporateActionId, corporateActionPersistenceDto.getOptions(),
                                corporateActionPersistenceDto.getPortfolioModels(),
                                corporateActionPersistenceDto.getAccounts())).performOperation();
            } else {
                // Submit accounts
                return new Submit<>(CURRENT_VERSION, corporateActionElectionDtoService, corporateActionAccountDetailsDtoErrorMapper,
                        new CorporateActionElectionDetailsDto(corporateActionId, corporateActionPersistenceDto.getOptions(),
                                corporateActionPersistenceDto.getAccounts(), corporateActionPersistenceDto.getIpsId()))
                        .performOperation();
            }
        }

        if (isIm) {
            return new Submit<>(CURRENT_VERSION, imCorporateActionElectionDtoService, corporateActionAccountDetailsDtoErrorMapper,
                    new ImCorporateActionElectionDetailsDto(corporateActionId, corporateActionPersistenceDto.getOptions(),
                            corporateActionPersistenceDto.getPortfolioModels(), corporateActionPersistenceDto.getAccounts()))
                    .performOperation();
        }

        // Important Notice: This condition is added to make sure if PreAuthorize is passed and If it's investor
        // all the corporate action are belongs to only direct account(for advised account investor has not rights do election).
        List<String> accountIds = Collections.emptyList();
        if (userProfileService.isInvestor()) {
            accountIds = convert(collect(corporateActionPersistenceDto.getAccounts(),
                    on(CorporateActionAccountDetailsDto.class).getAccountKey()),
                    new Converter<String, String>() {
                        @Override
                        public String convert(String s) {
                            return EncodedString.toPlainText(s);
                        }
                    });
        }

        if (permissionService.checkSubmitPermission(accountIds)) {
            return new Submit<>(CURRENT_VERSION, corporateActionElectionDtoService, corporateActionAccountDetailsDtoErrorMapper,
                    new CorporateActionElectionDetailsDto(corporateActionId, corporateActionPersistenceDto.getOptions(),
                            corporateActionPersistenceDto.getAccounts(), corporateActionPersistenceDto.getIpsId())).performOperation();
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    private boolean singlePortfolioPresent(List<ImCorporateActionPortfolioModelDto> portfolioModelDtoList) {
        if (portfolioModelDtoList != null && portfolioModelDtoList.size() == 1
                && portfolioModelDtoList.get(0).getSelectedElections() != null) {
            return true;
        }
        return false;
    }
}
