package com.bt.nextgen.api.modelportfolio.v2.controller;

import com.bt.nextgen.api.modelportfolio.v2.service.sectorportfolio.SectorPortfolioDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("SectorPortfolioApiControllerV2")
@RequestMapping(produces = "application/json")
public class SectorPortfolioApiController {
    @Autowired
    private SectorPortfolioDtoService sectorPortfolioDtoService;

    @RequestMapping(method = RequestMethod.GET, value = "${api.modelportfolio.v2.uri.sectorPortfolios}")
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody ApiResponse getSectorPortfolios() {

        return new FindAll<>(ApiVersion.CURRENT_VERSION, sectorPortfolioDtoService).performOperation();

    }

}
