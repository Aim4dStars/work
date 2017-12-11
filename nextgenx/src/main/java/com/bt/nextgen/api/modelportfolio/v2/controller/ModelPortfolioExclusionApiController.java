package com.bt.nextgen.api.modelportfolio.v2.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioExclusionDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioExclusionsDto;
import com.bt.nextgen.api.modelportfolio.v2.service.rebalance.ModelPortfolioExclusionDtoService;
import com.bt.nextgen.api.modelportfolio.v2.validation.ModelPortfolioDtoErrorMapper;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.Submit;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ModelPortfolioApiController is used to retrieve and upload the model portfolio details for an investment manager
 */
@Controller("ModelPortfolioExclusionApiControllerV2")
@RequestMapping(produces = "application/json")
public class ModelPortfolioExclusionApiController {
    @Autowired
    private ModelPortfolioExclusionDtoService exclusionDtoService;

    @Autowired
    private ModelPortfolioDtoErrorMapper modelPortfolioErrorMapper;

    private static ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(method = RequestMethod.POST, value = "${api.modelportfolio.v2.uri.exclusions}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Rebalance_model_portfolios')")
    public @ResponseBody ApiResponse submitModelRebalance(@PathVariable("model-id") String modelId,
            @RequestBody String exclusionJson) throws IOException {

        List<ModelPortfolioExclusionDto> exclusions = mapper.readerWithView(JsonViews.Write.class)
                .forType(new TypeReference<List<ModelPortfolioExclusionDto>>() {
                }).readValue(exclusionJson);

        ModelPortfolioExclusionsDto exclusionsDto = new ModelPortfolioExclusionsDto(new ModelPortfolioKey(modelId), exclusions);

        return new Submit<>(ApiVersion.CURRENT_VERSION, exclusionDtoService, modelPortfolioErrorMapper, exclusionsDto)
                .performOperation();

    }

}
