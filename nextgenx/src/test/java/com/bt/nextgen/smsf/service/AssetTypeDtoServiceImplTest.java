package com.bt.nextgen.smsf.service;

import com.bt.nextgen.api.smsf.service.AssetTypeDtoService;
import com.bt.nextgen.api.smsf.service.AssetTypeDtoServiceImpl;
import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.api.staticdata.service.StaticDataDtoService;
import com.bt.nextgen.api.staticdata.service.StaticDataDtoServiceImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.api.smsf.model.AssetTypeDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssetTypeDtoServiceImplTest
{
    @Mock
    private StaticDataDtoService staticDataDtoService = new StaticDataDtoServiceImpl();

    @InjectMocks
    private AssetTypeDtoService assetTypeDtoService = new AssetTypeDtoServiceImpl();

    @Mock
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Before
    public void setup()
    {
        ////when(staticDataDtoService.search(any(List.class), any(ServiceErrorsImpl.class))).thenReturn(getAllStaticCodesForAssetTypes());
    }

    @Test
    public void testGetOrderedAssetTypeList()
    {
        when(staticDataDtoService.search(any(List.class), any(ServiceErrorsImpl.class))).thenReturn(getAllStaticCodesForAssetTypes());

        List<AssetTypeDto> orderedAssetTypeList = assetTypeDtoService.findAll(new ServiceErrorsImpl());

        assertEquals(orderedAssetTypeList.size(), 8);
        assertEquals("Cash", orderedAssetTypeList.get(0).getAssetName());
        assertEquals("Term deposit", orderedAssetTypeList.get(1).getAssetName());
        assertEquals("Listed security", orderedAssetTypeList.get(2).getAssetName());
        assertEquals("International listed security", orderedAssetTypeList.get(3).getAssetName());
        assertEquals("Managed fund", orderedAssetTypeList.get(4).getAssetName());
        assertEquals("Managed portfolio", orderedAssetTypeList.get(5).getAssetName());
        assertEquals("Direct property", orderedAssetTypeList.get(6).getAssetName());
        assertEquals("Other", orderedAssetTypeList.get(7).getAssetName());
    }

    @Test
    // Static Code for btfg$asset_cluster in different order to expected display order
    public void testGetOrderedAssetTypeListWithStaticCodesOutOfOrder()
    {
        when(staticDataDtoService.search(any(List.class), any(ServiceErrorsImpl.class))).thenReturn(getAllStaticCodesForAssetTypesInOutOfOrder());

        List<AssetTypeDto> orderedAssetTypeList = assetTypeDtoService.findAll(new ServiceErrorsImpl());

        assertEquals(orderedAssetTypeList.size(), 8);
        assertEquals("Cash", orderedAssetTypeList.get(0).getAssetName());
        assertEquals("Term deposit", orderedAssetTypeList.get(1).getAssetName());
        assertEquals("Listed security", orderedAssetTypeList.get(2).getAssetName());
        assertEquals("International listed security", orderedAssetTypeList.get(3).getAssetName());
        assertEquals("Managed fund", orderedAssetTypeList.get(4).getAssetName());
        assertEquals("Managed portfolio", orderedAssetTypeList.get(5).getAssetName());
        assertEquals("Direct property", orderedAssetTypeList.get(6).getAssetName());
        assertEquals("Other", orderedAssetTypeList.get(7).getAssetName());
    }


    private List<StaticCodeDto> getAllStaticCodesForAssetTypes()
    {
        List<StaticCodeDto> assetTypes = new ArrayList<>();

        StaticCodeDto type1 = new StaticCodeDto();
        type1.setId("505200");
        type1.setIntlId("cash");
        type1.setLabel("Cash");
        type1.setListName("btfg@asset_cluster");
        type1.setValue("Cash");

        StaticCodeDto type2 = new StaticCodeDto();
        type2.setId("505203");
        type2.setIntlId("ls");
        type2.setLabel("Listed Security");
        type2.setListName("btfg@asset_cluster");
        type2.setValue("Listed Security");

        StaticCodeDto type3 = new StaticCodeDto();
        type3.setId("505202");
        type3.setIntlId("mf");
        type3.setLabel("Managed Fund");
        type3.setListName("btfg@asset_cluster");
        type3.setValue("Managed Fund");

        StaticCodeDto type4 = new StaticCodeDto();
        type4.setId("505205");
        type4.setIntlId("oth");
        type4.setLabel("Other");
        type4.setListName("btfg@asset_cluster");
        type4.setValue("Other");

        StaticCodeDto type5 = new StaticCodeDto();
        type5.setId("505201");
        type5.setIntlId("td");
        type5.setLabel("Term Deposit");
        type5.setListName("btfg@asset_cluster");
        type5.setValue("Term Deposit");

        StaticCodeDto type6 = new StaticCodeDto();
        type6.setId("505203");
        type6.setIntlId("ils");
        type6.setLabel("International listed Security");
        type6.setListName("btfg@asset_cluster");
        type6.setValue("International listed Security");

        StaticCodeDto type7 = new StaticCodeDto();
        type7.setId("505206");
        type7.setIntlId("mp");
        type7.setLabel("Managed Portfolio");
        type7.setListName("btfg@asset_cluster");
        type7.setValue("Managed Portfolio");

        StaticCodeDto type8 = new StaticCodeDto();
        type8.setId("505207");
        type8.setIntlId("dp");
        type8.setLabel("Direct Property");
        type8.setListName("btfg@asset_cluster");
        type8.setValue("Direct Property");

        assetTypes.add(type1);
        assetTypes.add(type2);
        assetTypes.add(type3);
        assetTypes.add(type4);
        assetTypes.add(type5);
        assetTypes.add(type6);
        assetTypes.add(type7);
        assetTypes.add(type8);

        return assetTypes;
    }


    private List<StaticCodeDto> getAllStaticCodesForAssetTypesInOutOfOrder()
    {
        List<StaticCodeDto> assetTypes = new ArrayList<>();

        StaticCodeDto type1 = new StaticCodeDto();
        type1.setId("505200");
        type1.setIntlId("cash");
        type1.setLabel("Cash");
        type1.setListName("btfg@asset_cluster");
        type1.setValue("Cash");

        StaticCodeDto type2 = new StaticCodeDto();
        type2.setId("505203");
        type2.setIntlId("ls");
        type2.setLabel("Listed Security");
        type2.setListName("btfg@asset_cluster");
        type2.setValue("Listed Security");

        StaticCodeDto type3 = new StaticCodeDto();
        type3.setId("505202");
        type3.setIntlId("mf");
        type3.setLabel("Managed Fund");
        type3.setListName("btfg@asset_cluster");
        type3.setValue("Managed Fund");

        StaticCodeDto type4 = new StaticCodeDto();
        type4.setId("505205");
        type4.setIntlId("oth");
        type4.setLabel("Other");
        type4.setListName("btfg@asset_cluster");
        type4.setValue("Other");

        StaticCodeDto type5 = new StaticCodeDto();
        type5.setId("505201");
        type5.setIntlId("td");
        type5.setLabel("Term Deposit");
        type5.setListName("btfg@asset_cluster");
        type5.setValue("Term Deposit");

        StaticCodeDto type6 = new StaticCodeDto();
        type6.setId("505203");
        type6.setIntlId("ils");
        type6.setLabel("International listed Security");
        type6.setListName("btfg@asset_cluster");
        type6.setValue("International listed Security");

        StaticCodeDto type7 = new StaticCodeDto();
        type7.setId("505206");
        type7.setIntlId("mp");
        type7.setLabel("Managed Portfolio");
        type7.setListName("btfg@asset_cluster");
        type7.setValue("Managed Portfolio");

        StaticCodeDto type8 = new StaticCodeDto();
        type8.setId("505207");
        type8.setIntlId("dp");
        type8.setLabel("Direct Property");
        type8.setListName("btfg@asset_cluster");
        type8.setValue("Direct Property");

        assetTypes.add(type7);
        assetTypes.add(type2);
        assetTypes.add(type3);
        assetTypes.add(type8);
        assetTypes.add(type6);
        assetTypes.add(type5);
        assetTypes.add(type1);
        assetTypes.add(type4);


        return assetTypes;
    }



}
