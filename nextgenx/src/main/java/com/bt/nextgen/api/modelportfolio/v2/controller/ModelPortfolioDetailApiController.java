package com.bt.nextgen.api.modelportfolio.v2.controller;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.ModelPortfolioDetailDto;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.ModelPortfolioDetailDtoImpl;
import com.bt.nextgen.api.modelportfolio.v2.service.detail.ModelPortfolioDetailDtoService;
import com.bt.nextgen.api.modelportfolio.v2.validation.ModelPortfolioDtoErrorMapper;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.api.operation.Update;
import com.bt.nextgen.core.api.operation.Validate;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.google.json.JsonSanitizer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * ModelPortfolioDetailApiController is used to create and maintain the model template
 */
@Controller("ModelPortfolioDetailApiControllerV2")
@RequestMapping(produces = "application/json")
@Api(description = "Submit or update details for model portfolios")
public class ModelPortfolioDetailApiController {

    @Autowired
    private ModelPortfolioDtoErrorMapper modelPortfolioErrorMapper;

    @Autowired
    private SecureJsonObjectMapper mapper;

    @Autowired
    private ModelPortfolioDetailDtoService modelPortfolioDetailDtoService;

    @Autowired
    private UserProfileService profileService;

    private static final String MODEL_ID = "model-id";
    private static final String MODEL_OBJECT = "model-object";
    private static final String VALIDATE_ONLY = "x-ro-validate-only";

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Create_model_portfolios')")
    @ApiOperation(value = "Submit details for new model portfolio", response = ModelPortfolioDetailDto.class)
    @RequestMapping(method = RequestMethod.POST, value = "${api.modelportfolio.v2.uri.modeldetails}")
    public @ResponseBody
    ApiResponse submitModel(
            @RequestParam(value = MODEL_OBJECT, required = true) @ApiParam(value = "ModelPortfolioDetailDto in JSON format", required = true) String modelObject,
            @RequestParam(value = VALIDATE_ONLY, required = false) @ApiParam(value = "Details only validated if true", required = false) String validateOnly)
            throws IOException {
        if (!profileService.isEmulating()) {
            ModelPortfolioDetailDto modelDetail = mapper.readerWithView(JsonViews.Write.class)
                    .forType(ModelPortfolioDetailDtoImpl.class).readValue(JsonSanitizer.sanitize(modelObject));

            if ("true".equals(validateOnly)) {
                return new Validate<>(ApiVersion.CURRENT_VERSION, modelPortfolioDetailDtoService, modelPortfolioErrorMapper,
                        modelDetail).performOperation();
            } else {
                return new Submit<>(ApiVersion.CURRENT_VERSION, modelPortfolioDetailDtoService, modelPortfolioErrorMapper,
                        modelDetail).performOperation();
            }
        }
        throw new AccessDeniedException("Access Denied");
    }

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Create_model_portfolios')")
    @ApiOperation(value = "Retrieve details for existing model portfolio", response = ModelPortfolioDetailDto.class)
    @RequestMapping(method = RequestMethod.GET, value = "${api.modelportfolio.v2.uri.modeldetail}")
    public @ResponseBody
    ApiResponse findModel(
            @PathVariable(MODEL_ID) @ApiParam(value = "ID of model portfolio to retrieve", required = true) String modelId) {

        ModelPortfolioKey key = new ModelPortfolioKey(modelId);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, modelPortfolioDetailDtoService, key).performOperation();
    }

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Create_model_portfolios')")
    @ApiOperation(value = "Update details for existing model portfolio", response = ModelPortfolioDetailDto.class)
    @RequestMapping(method = RequestMethod.POST, value = "${api.modelportfolio.v2.uri.modeldetail}")
    public @ResponseBody
    ApiResponse updateModel(
            @PathVariable(MODEL_ID) @ApiParam(value = "ID of model portfolio to update", required = true) String modelId,
            @RequestParam(value = MODEL_OBJECT, required = true) @ApiParam(value = "ModelPortfolioDetailDto in JSON format", required = true) String modelObject)
            throws IOException {
        if (!profileService.isEmulating()) {
            ModelPortfolioDetailDto modelDetail = mapper.readerWithView(JsonViews.Write.class)
                    .forType(ModelPortfolioDetailDtoImpl.class).readValue(JsonSanitizer.sanitize(modelObject));
            ((ModelPortfolioDetailDtoImpl) modelDetail).setKey(new ModelPortfolioKey(modelId));

            return new Update<>(ApiVersion.CURRENT_VERSION, modelPortfolioDetailDtoService, modelPortfolioErrorMapper,
                    modelDetail).performOperation();
        }
        throw new AccessDeniedException("Access Denied");
    }
}