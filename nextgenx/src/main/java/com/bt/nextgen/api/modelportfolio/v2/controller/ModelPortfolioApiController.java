package com.bt.nextgen.api.modelportfolio.v2.controller;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioSummaryDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioUploadDto;
import com.bt.nextgen.api.modelportfolio.v2.service.ModelPortfolioSummaryDtoService;
import com.bt.nextgen.api.modelportfolio.v2.service.ModelPortfolioUploadDtoService;
import com.bt.nextgen.api.modelportfolio.v2.validation.ModelPortfolioDtoErrorMapper;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.api.operation.Validate;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.modelportfolio.util.ModelPortfolioUploadUtil;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ModelPortfolioApiController is used to retrieve and update the investment allocations of existing model portfolios
 */
@Controller("ModelPortfolioApiControllerV2")
@RequestMapping(produces = "application/json")
@Api(description = "Load or update allocations for existing models")
public class ModelPortfolioApiController {

    private static final String MODEL_ID = "model-id";

    @Autowired
    private ModelPortfolioSummaryDtoService modelPortfolioSummaryDtoService;

    @Autowired
    private ModelPortfolioUploadDtoService modelPortfolioUploadDtoService;

    @Autowired
    private ModelPortfolioDtoErrorMapper modelPortfolioUploadErrorMapper;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private SecureJsonObjectMapper mapper;

    @Autowired
    private ModelPortfolioUploadUtil modelPortfolioUploadUtil;

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_model_portfolios')")
    @RequestMapping(method = RequestMethod.GET, value = "${api.modelportfolio.v2.uri.modelportfolios}")
    @ApiOperation(value = "Load all models available to the current user", response = ModelPortfolioSummaryDto.class, responseContainer = "List")
    public @ResponseBody
    ApiResponse getModels() {
        return new FindAll<>(ApiVersion.CURRENT_VERSION, modelPortfolioSummaryDtoService).performOperation();
    }

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Create_model_portfolios')")
    @RequestMapping(method = RequestMethod.POST, value = "${api.modelportfolio.v2.uri.modelportfolio}")
    @ApiOperation(value = "Update investment allocations of an existing model portfolio", response = ModelPortfolioUploadDto.class)
    public @ResponseBody
    ApiResponse createModel(
            @PathVariable(MODEL_ID) @ApiParam(value = "Model ID to update allocations for", required = true) String modelId,
            @RequestParam(value = "modelData", required = true) @ApiParam(value = "ModelPortfolioUploadDto containing new allocations in JSON format", required = true) String modelPortfolio,
            @RequestParam(value = "x-ro-validate-only", required = false) @ApiParam(value = "Flag indicating the data is for validation only", required = false) String validateOnly)
            throws IOException {

        if (!userProfileService.isEmulating()) {
            ModelPortfolioUploadDto modelConstruct = mapper.readerWithView(JsonViews.Write.class)
                    .forType(ModelPortfolioUploadDto.class).readValue(JsonSanitizer.sanitize(modelPortfolio));
            modelConstruct.setKey(new ModelPortfolioKey(modelId));

            if ("true".equals(validateOnly)) {
                return new Validate<>(ApiVersion.CURRENT_VERSION, modelPortfolioUploadDtoService,
                        modelPortfolioUploadErrorMapper, modelConstruct).performOperation();
            } else {
                return new Submit<>(ApiVersion.CURRENT_VERSION, modelPortfolioUploadDtoService, modelPortfolioUploadErrorMapper,
                        modelConstruct).performOperation();
            }
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    @PreAuthorize("isAuthenticated() and (hasPermission(null, 'View_intermediary_client_reports') OR (hasPermission(null, 'View_model_portfolios')))")
    @RequestMapping(method = RequestMethod.GET, value = "${api.modelportfolio.v2.uri.allocationdetail}")
    @ApiOperation(value = "Search for the allocations of an existing model portfolio", response = ModelPortfolioUploadDto.class, responseContainer = "List")
    public @ResponseBody
    ApiResponse findModel(
            @PathVariable(MODEL_ID) @ApiParam(value = "Model ID to search for", required = true) String modelId,
            @RequestParam(value = "modelCode", required = false) @ApiParam(value = "Model code to search for", required = false) String modelCode,
            @RequestParam(value = "allocationId", required = false) @ApiParam(value = "Allocation ID to search for", required = false) String allocationId) {

        List<ApiSearchCriteria> criteria = new ArrayList<>();
        ApiSearchCriteria modelCriteria = new ApiSearchCriteria("modelId", ApiSearchCriteria.SearchOperation.EQUALS, modelId,
                ApiSearchCriteria.OperationType.STRING);
        criteria.add(modelCriteria);

        if (modelCode != null) {
            ApiSearchCriteria searchCriteria = new ApiSearchCriteria("modelCode", ApiSearchCriteria.SearchOperation.EQUALS,
                    modelCode, ApiSearchCriteria.OperationType.STRING);
            criteria.add(searchCriteria);
        }
        if (allocationId != null) {
            ApiSearchCriteria allocIdCriteria = new ApiSearchCriteria("allocationId", ApiSearchCriteria.SearchOperation.EQUALS,
                    allocationId, ApiSearchCriteria.OperationType.STRING);
            criteria.add(allocIdCriteria);
        }
        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, modelPortfolioUploadDtoService, criteria).performOperation();
    }

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Start_upload_model_file')")
    @RequestMapping(method = RequestMethod.POST, value = "${api.modelportfolio.v2.uri.upload}")
    public @ResponseBody
    ApiResponse uploadModel(@PathVariable(MODEL_ID) @ApiParam(value = "Model ID to upload for", required = true) String modelId,
            @RequestPart("upload-file") MultipartFile file) {

        if (!userProfileService.isEmulating()) {

            ModelPortfolioUploadDto modelUpload = modelPortfolioUploadUtil.parseFile(modelId, file);

            return new Submit<>(ApiVersion.CURRENT_VERSION, modelPortfolioUploadDtoService, modelPortfolioUploadErrorMapper,
                    modelUpload).performOperation();
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }
}
