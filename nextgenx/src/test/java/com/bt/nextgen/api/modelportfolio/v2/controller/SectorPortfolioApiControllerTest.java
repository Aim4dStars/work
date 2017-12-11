package com.bt.nextgen.api.modelportfolio.v2.controller;

import com.bt.nextgen.api.modelportfolio.v2.model.sector.SectorPortfolioDto;
import com.bt.nextgen.api.modelportfolio.v2.service.sectorportfolio.SectorPortfolioDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class SectorPortfolioApiControllerTest {

    @InjectMocks
    private SectorPortfolioApiController sectorPortfolioApiController;

    @Mock
    private SectorPortfolioDtoService sectorPortfolioDtoService;

    @Test
    public void testGetSectorPortfolios() {

        List<SectorPortfolioDto> mockDtos = new ArrayList<>();

        Mockito.when(sectorPortfolioDtoService.findAll(Mockito.any(ServiceErrors.class))).thenReturn(mockDtos);

        ApiResponse response = sectorPortfolioApiController.getSectorPortfolios();

        List<SectorPortfolioDto> dtos = ((ResultListDto) response.getData()).getResultList();

        Assert.assertNotNull(dtos);
    }


}
