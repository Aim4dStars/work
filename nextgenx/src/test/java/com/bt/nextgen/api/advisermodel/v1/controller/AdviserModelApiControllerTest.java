package com.bt.nextgen.api.advisermodel.v1.controller;

import com.bt.nextgen.api.advisermodel.v1.service.AdviserModelDtoService;
import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AdviserModelApiControllerTest {

    @InjectMocks
    private AdviserModelApiController adviserModelApiController;

    @Mock
    private AdviserModelDtoService adviserModelDtoService;

    @Test
    public void testGetTmpDefaultParameters() {
        Mockito.when(adviserModelDtoService.findOne(Mockito.any(ServiceErrors.class))).thenReturn(Mockito.mock(AssetDto.class));

        ApiResponse response = adviserModelApiController.getCashAsset();

        Assert.assertNotNull(response.getData());
    }
}
