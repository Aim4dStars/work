package com.bt.nextgen.smsf.service;

import com.bt.nextgen.api.smsf.service.AssetClassDtoService;
import com.bt.nextgen.api.smsf.service.AssetClassDtoServiceImpl;
import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.api.staticdata.service.StaticDataDtoService;
import com.bt.nextgen.api.staticdata.service.StaticDataDtoServiceImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.api.smsf.model.AssetClassDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssetClassDtoServiceImplTest
{
    @Mock
    private StaticDataDtoService staticDataDtoService = new StaticDataDtoServiceImpl();

    @InjectMocks
    private AssetClassDtoService assetClassDtoService = new AssetClassDtoServiceImpl();


    @Test
    public void testGetOrderedAssetClassList()
    {
        when(staticDataDtoService.search(any(List.class), any(ServiceErrorsImpl.class))).thenReturn(getAllStaticCodesForAssetClasses());

        List<AssetClassDto> orderedAssetTypeList = assetClassDtoService.findAll(new ServiceErrorsImpl());

        assertEquals(orderedAssetTypeList.size(), 10);
        assertEquals(orderedAssetTypeList.get(0).getAssetName(), "Cash");
        assertEquals(orderedAssetTypeList.get(1).getAssetName(), "Australian fixed interest");
        assertEquals(orderedAssetTypeList.get(2).getAssetName(), "International fixed interest");
        assertEquals(orderedAssetTypeList.get(3).getAssetName(), "Australian shares");
        assertEquals(orderedAssetTypeList.get(4).getAssetName(), "International shares");
        assertEquals(orderedAssetTypeList.get(5).getAssetName(), "Australian real estate");
        assertEquals(orderedAssetTypeList.get(6).getAssetName(), "International real estate");
        assertEquals(orderedAssetTypeList.get(7).getAssetName(), "Alternatives");
        assertEquals(orderedAssetTypeList.get(8).getAssetName(), "Diversified");
        assertEquals(orderedAssetTypeList.get(9).getAssetName(), "Other");
    }


    private List<StaticCodeDto> getAllStaticCodesForAssetClasses()
    {
        List<StaticCodeDto> assetClasses = new ArrayList<>();

        StaticCodeDto type1 = new StaticCodeDto();
        type1.setId("60687");
        type1.setIntlId("alt_invst");
        type1.setLabel("Alternatives");
        type1.setListName("btfg@asset_class_grp");
        type1.setValue("Alternatives");

        StaticCodeDto type2 = new StaticCodeDto();
        type2.setId("60683");
        type2.setIntlId("fi_au");
        type2.setLabel("Australian Fixed Interest");
        type2.setListName("btfg@asset_class_grp");
        type2.setValue("Australian Fixed Interest");

        StaticCodeDto type3 = new StaticCodeDto();
        type3.setId("60685");
        type3.setIntlId("realest_au");
        type3.setLabel("Australian Real Estate");
        type3.setListName("btfg@asset_class_grp");
        type3.setValue("Australian Real Estate");

        StaticCodeDto type4 = new StaticCodeDto();
        type4.setId("60681");
        type4.setIntlId("eq_au");
        type4.setLabel("Australian Shares");
        type4.setListName("btfg@asset_class_grp");
        type4.setValue("Australian Shares");

        StaticCodeDto type5 = new StaticCodeDto();
        type5.setId("60690");
        type5.setIntlId("cash");
        type5.setLabel("Cash");
        type5.setListName("btfg@asset_class_grp");
        type5.setValue("Cash");

        StaticCodeDto type6 = new StaticCodeDto();
        type6.setId("60689");
        type6.setIntlId("dived");
        type6.setLabel("Diversified");
        type6.setListName("btfg@asset_class_grp");
        type6.setValue("Diversified");

        StaticCodeDto type7 = new StaticCodeDto();
        type7.setId("60684");
        type7.setIntlId("fi_intnl");
        type7.setLabel("International Fixed Interest");
        type7.setListName("btfg@asset_class_grp");
        type7.setValue("International Fixed Interest");

        StaticCodeDto type8 = new StaticCodeDto();
        type8.setId("60690");
        type8.setIntlId("realest_intnl");
        type8.setLabel("International Real Estate");
        type8.setListName("btfg@asset_class_grp");
        type8.setValue("International Real Estate");

        StaticCodeDto type9 = new StaticCodeDto();
        type9.setId("60682");
        type9.setIntlId("eq_intnl");
        type9.setLabel("International Shares");
        type9.setListName("btfg@asset_class_grp");
        type9.setValue("International Shares");

        StaticCodeDto type10 = new StaticCodeDto();
        type10.setId("60688");
        type10.setIntlId("oth_invst");
        type10.setLabel("Other");
        type10.setListName("btfg@asset_class_grp");
        type10.setValue("Other");


        assetClasses.add(type1);
        assetClasses.add(type2);
        assetClasses.add(type3);
        assetClasses.add(type4);
        assetClasses.add(type5);
        assetClasses.add(type6);
        assetClasses.add(type7);
        assetClasses.add(type8);
        assetClasses.add(type9);
        assetClasses.add(type10);

        return assetClasses;
    }
}
