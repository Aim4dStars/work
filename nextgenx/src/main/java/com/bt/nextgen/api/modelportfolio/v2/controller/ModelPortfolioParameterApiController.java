package com.bt.nextgen.api.modelportfolio.v2.controller;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.DealerParameterKey;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.ModelPortfolioType;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.PreferredPortfolioDefaultParamsDto;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.TailoredPortfolioDefaultParamsDto;
import com.bt.nextgen.api.modelportfolio.v2.service.ModelAssetClassDtoService;
import com.bt.nextgen.api.modelportfolio.v2.service.TailorMadePortfolioDtoService;
import com.bt.nextgen.api.modelportfolio.v2.service.defaultparams.ModelPortfolioDefaultParamsDtoService;
import com.bt.nextgen.api.modelportfolio.v2.service.defaultparams.TailoredPortfolioOfferDtoService;
import com.bt.nextgen.api.smsf.model.AssetClassDto;
import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.api.staticdata.service.StaticDataDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * ModelPortfolioParameterApiController provides to the UI default parameters and information required when creating model
 * portfolios
 */
@Controller("ModelPortfolioParameterApiControllerV2")
@RequestMapping(produces = "application/json")
@Api(description = "Provide parameters used to create and maintain model portfolios")
public class ModelPortfolioParameterApiController {

    @Autowired
    private ModelAssetClassDtoService modelAssetClassDtoService;

    @Autowired
    private StaticDataDtoService staticDtoService;

    @Autowired
    private TailorMadePortfolioDtoService tmpDtoService;

    @Deprecated
    @Autowired
    private TailoredPortfolioOfferDtoService offerDtoService;

    @Autowired
    private ModelPortfolioDefaultParamsDtoService defaultParamsDtoService;

    @Autowired
    private UserProfileService userProfileService;

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_model_portfolios')")
    @RequestMapping(method = RequestMethod.GET, value = "${api.modelportfolio.v2.uri.tmpcashasset}")
    @ApiOperation(value = "Load TMP cash asset", response = AssetDto.class, responseContainer = "List")
    public @ResponseBody
    ApiResponse getModelCashAsset(
            @RequestParam(value = "ipsModelType", required = true) @ApiParam(value = "Model account type to load asset for", required = true) String ipsModelType) {

        List<ApiSearchCriteria> criteria = new ArrayList<>();
        ApiSearchCriteria searchCriteria = new ApiSearchCriteria("modelType", ApiSearchCriteria.SearchOperation.EQUALS,
                ipsModelType, ApiSearchCriteria.OperationType.STRING);
        criteria.add(searchCriteria);

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, tmpDtoService, criteria).performOperation();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "${api.modelportfolio.v2.uri.investmentstyles}")
    @ApiOperation(value = "Load all investment styles available to describe a TMP", response = StaticCodeDto.class, responseContainer = "List")
    public @ResponseBody
    ApiResponse getInvestmentStyles() {
        return getStaticCodeDetails(CodeCategory.IPS_INVESTMENT_STYLE.name());
    }

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_model_portfolios')")
    @RequestMapping(method = RequestMethod.GET, value = "${api.modelportfolio.v2.uri.ipsassetclass}")
    @ApiOperation(value = "Load all model asset classes available to describe a TMP", response = StaticCodeDto.class, responseContainer = "List")
    public @ResponseBody
    ApiResponse getIpsAssetClass() {
        return getStaticCodeDetails(CodeCategory.IPS_ASSET_CLASS.name());
    }

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_model_portfolios')")
    @RequestMapping(method = RequestMethod.GET, value = "${api.modelportfolio.v2.uri.assetclass}")
    @ApiOperation(value = "Load all asset classes available to be invested in by a TMP", response = AssetClassDto.class, responseContainer = "List")
    public @ResponseBody
    ApiResponse getAssetClass() {
        return new FindAll<>(ApiVersion.CURRENT_VERSION, modelAssetClassDtoService).performOperation();
    }

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_model_portfolios')")
    @RequestMapping(method = RequestMethod.GET, value = "${api.modelportfolio.v2.uri.tmpdefaultparams}")
    @ApiOperation(value = "Load TMP default investment amounts, for use by IM", response = TailoredPortfolioDefaultParamsDto.class)
    public @ResponseBody
    ApiResponse getTmpDefaultParams(
            @RequestParam(value = "accountType", required = true) @ApiParam(value = "Model account type to load defaults for", required = true) String accountType) {

        DealerParameterKey key = new DealerParameterKey(ModelType.forCode(accountType).getId(),
                ModelPortfolioType.TAILORED.getIntlId(), userProfileService.getInvestmentManager(new ServiceErrorsImpl()));

        return new FindByKey<>(ApiVersion.CURRENT_VERSION, defaultParamsDtoService, key).performOperation();
    }

    // TODO: Confirm correct permission to apply for adviser
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_model_portfolios')")
    @RequestMapping(method = RequestMethod.GET, value = "${api.modelportfolio.v2.uri.ppdefaultparams}")
    @ApiOperation(value = "Load PP default investment amounts, for use by adviser", response = PreferredPortfolioDefaultParamsDto.class)
    public @ResponseBody
    ApiResponse getPPDefaultParams(
            @RequestParam(value = "accountType", required = true) @ApiParam(value = "Model account type to load defaults for", required = true) String accountType) {

        DealerParameterKey key = new DealerParameterKey(ModelType.forCode(accountType).getId(),
                ModelPortfolioType.PREFERRED.getIntlId(), userProfileService.getDealerGroupBroker());

        return new FindByKey<>(ApiVersion.CURRENT_VERSION, defaultParamsDtoService, key).performOperation();
    }

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_model_portfolios')")
    @RequestMapping(method = RequestMethod.GET, value = "${api.modelportfolio.v2.uri.modeloffers}")
    @ApiOperation(value = "Load TMP offers", response = TailoredPortfolioDefaultParamsDto.class, responseContainer = "List")
    @Deprecated
    public @ResponseBody
    ApiResponse getModelOffer(
            @RequestParam(value = "ipsModelType", required = true) @ApiParam(value = "Model account type to load offers for", required = true) String ipsModelType) {

        DealerParameterKey key = new DealerParameterKey(ModelType.forCode(ipsModelType).getId(),
                ModelPortfolioType.TAILORED.getIntlId(), userProfileService.getDealerGroupBroker());

        List<ApiSearchCriteria> criteria = new ArrayList<>();
        ApiSearchCriteria modelCriteria = new ApiSearchCriteria("modelType", ApiSearchCriteria.SearchOperation.EQUALS,
                ipsModelType, ApiSearchCriteria.OperationType.STRING);
        criteria.add(modelCriteria);
        String brokerId = key.getBrokerKey() == null ? null : key.getBrokerKey().getId();
        ApiSearchCriteria brokerCriteria = new ApiSearchCriteria("dealerId", ApiSearchCriteria.SearchOperation.EQUALS, brokerId,
                ApiSearchCriteria.OperationType.STRING);
        criteria.add(brokerCriteria);

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, offerDtoService, criteria).performOperation();
    }

    private ApiResponse getStaticCodeDetails(String category) {
        List<ApiSearchCriteria> criteria = new ArrayList<>();
        ApiSearchCriteria searchCriteria = new ApiSearchCriteria("category", ApiSearchCriteria.SearchOperation.EQUALS, category,
                ApiSearchCriteria.OperationType.STRING);
        criteria.add(searchCriteria);

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, staticDtoService, criteria).performOperation();
    }
}
