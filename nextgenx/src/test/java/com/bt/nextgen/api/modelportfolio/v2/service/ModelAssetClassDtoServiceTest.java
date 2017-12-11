package com.bt.nextgen.api.modelportfolio.v2.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.smsf.model.AssetClassDto;
import com.bt.nextgen.api.staticdata.service.StaticDataDtoService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@RunWith(MockitoJUnitRunner.class)
public class ModelAssetClassDtoServiceTest {

    @InjectMocks
    private ModelAssetClassDtoServiceImpl modelAssetClassDtoService;

    @Mock
    private StaticDataDtoService staticDataDtoService;

    @Mock
    private ModelAssetClassConverter modelAssetClassConverter;

    @Test
    public void testFindAll() {
        
        Mockito.when(staticDataDtoService.search(Mockito.any(List.class), Mockito.any(ServiceErrorsImpl.class)))
                .thenReturn(Mockito.mock(List.class));

        Mockito.when(modelAssetClassConverter.convertToModelAssetClasses(Mockito.any(List.class))).thenReturn(getAllAssetClasses());

        List<AssetClassDto> modelAssetClasses = modelAssetClassDtoService.findAll(new ServiceErrorsImpl());

        Assert.assertNotNull(modelAssetClasses);
        Assert.assertFalse(modelAssetClasses.isEmpty());

        Assert.assertEquals("eq_au", modelAssetClasses.get(0).getKey());
        Assert.assertEquals("Australian shares", modelAssetClasses.get(0).getAssetName());
        
        Assert.assertEquals("cash", modelAssetClasses.get(modelAssetClasses.size() -1).getKey());
        Assert.assertEquals("Cash", modelAssetClasses.get(modelAssetClasses.size() -1).getAssetName());
        
    }

    private List<AssetClassDto> getAllAssetClasses() {
        List<AssetClassDto> assetClasses = new ArrayList<>();

        assetClasses.add(new AssetClassDto("Australian shares", "eq_au"));
        assetClasses.add(new AssetClassDto("International shares", "eq_intnl"));
        assetClasses.add(new AssetClassDto("Australian property", "realest_au"));
        assetClasses.add(new AssetClassDto("International property", "realest_intnl"));
        assetClasses.add(new AssetClassDto("Australian fixed interest", "fi_au"));
        assetClasses.add(new AssetClassDto("International fixed interest", "fi_intnl"));        
        assetClasses.add(new AssetClassDto("Alternatives", "alt_invst"));
        assetClasses.add(new AssetClassDto("Diversified", "dived"));
        assetClasses.add(new AssetClassDto("Liquid", "liquid"));
        assetClasses.add(new AssetClassDto("Cash", "cash"));                

        return assetClasses;
    }

}
