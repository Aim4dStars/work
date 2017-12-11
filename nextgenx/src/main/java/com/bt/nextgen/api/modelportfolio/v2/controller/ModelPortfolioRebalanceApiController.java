package com.bt.nextgen.api.modelportfolio.v2.controller;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioRebalanceDto;
import com.bt.nextgen.api.modelportfolio.v2.service.rebalance.ModelPortfolioRebalanceDetailDtoService;
import com.bt.nextgen.api.modelportfolio.v2.service.rebalance.ModelPortfolioRebalanceDtoService;
import com.bt.nextgen.api.modelportfolio.v2.validation.ModelPortfolioDtoErrorMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.BeanFilter;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.Sort;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ModelPortfolioApiController is used to retrieve and upload the model portfolio details for an investment manager
 */
@Controller("ModelPortfolioRebalanceApiControllerV2")
@RequestMapping(produces = "application/json")
public class ModelPortfolioRebalanceApiController {
    @Autowired
    private ModelPortfolioRebalanceDtoService modelPortfolioRebalanceDtoService;

    @Autowired
    private ModelPortfolioRebalanceDetailDtoService accountsRebalanceDtoService;

    @Autowired
    private ModelPortfolioDtoErrorMapper modelPortfolioErrorMapper;

    @Autowired
    private UserProfileService profileService;

    @RequestMapping(method = RequestMethod.GET, value = "${api.modelportfolio.v2.uri.rebalances}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Rebalance_model_portfolios')")
    public @ResponseBody ApiResponse getModelPortfolioRebalances(
            @RequestParam(value = BeanFilter.QUERY_PARAMETER, required = false) String queryString,
            @RequestParam(value = Sort.SORT_PARAMETER, required = false) String sortOrder) {

        return new Sort<>(new BeanFilter(ApiVersion.CURRENT_VERSION,
                new FindAll<>(ApiVersion.CURRENT_VERSION, modelPortfolioRebalanceDtoService), queryString), sortOrder)
                        .performOperation();

    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.modelportfolio.v2.uri.rebalance}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Rebalance_model_portfolios')")
    public @ResponseBody ApiResponse submitModelRebalance(@PathVariable("model-id") String modelId,
            @RequestParam(value = "action") String action) {

        if (!profileService.isEmulating()) {
            ModelPortfolioRebalanceDto rebalRequest = new ModelPortfolioRebalanceDto(new ModelPortfolioKey(modelId),
                    action);

            return new Submit<ModelPortfolioKey, ModelPortfolioRebalanceDto>(ApiVersion.CURRENT_VERSION,
                    modelPortfolioRebalanceDtoService, modelPortfolioErrorMapper, rebalRequest).performOperation();
        } else {
            throw new AccessDeniedException("Access Denied");
        }

    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.modelportfolio.v2.uri.rebalance}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Rebalance_model_portfolios')")
    public @ResponseBody ApiResponse getModelPortfolioRebalance(@PathVariable("model-id") String modelId) {

        ModelPortfolioKey key = new ModelPortfolioKey(modelId);

        return new FindByKey<>(ApiVersion.CURRENT_VERSION, accountsRebalanceDtoService, key).performOperation();

    }
}
