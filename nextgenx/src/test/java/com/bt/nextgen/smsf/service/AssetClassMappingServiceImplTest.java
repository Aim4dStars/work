package com.bt.nextgen.smsf.service;

import com.bt.nextgen.api.smsf.service.AssetClassMappingService;
import com.bt.nextgen.api.smsf.service.AssetClassMappingServiceImpl;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class AssetClassMappingServiceImplTest
{
    @Mock
    StaticDataDtoService staticDataDtoService = new StaticDataDtoServiceImpl();

    @InjectMocks
    AssetClassMappingService assetClassMappingService = new AssetClassMappingServiceImpl();


    @Test
    public void getClassificationsForCashAssetType()
    {
        when(staticDataDtoService.search(any(List.class), any(ServiceErrorsImpl.class))).thenReturn(getAllStaticCodesForAssetClasses());

        ArrayList<AssetClassDto> classes = (ArrayList<AssetClassDto>) assetClassMappingService.search("cash", new ServiceErrorsImpl());

        assertEquals(1, classes.size());
        assertEquals("Cash", classes.get(0).getAssetName());
    }

    @Test
    public void getClassificationsForAustralianListedSecuritiesAssetType()
    {
        when(staticDataDtoService.search(any(List.class), any(ServiceErrorsImpl.class))).thenReturn(getAllStaticCodesForAssetClasses());

        ArrayList<AssetClassDto> classes = (ArrayList<AssetClassDto>) assetClassMappingService.search("ls", new ServiceErrorsImpl());

        assertEquals(11, classes.size());
        assertEquals("Cash", classes.get(0).getAssetName());
        assertEquals("Australian fixed interest", classes.get(1).getAssetName());
        assertEquals("International fixed interest", classes.get(2).getAssetName());
        assertEquals("Australian floating rate interest", classes.get(3).getAssetName());
        assertEquals("Australian shares", classes.get(4).getAssetName());
        assertEquals("International shares", classes.get(5).getAssetName());
        assertEquals("Australian real estate", classes.get(6).getAssetName());
        assertEquals("International real estate", classes.get(7).getAssetName());
        assertEquals("Alternatives", classes.get(8).getAssetName());
        assertEquals("Diversified", classes.get(9).getAssetName());
        assertEquals("Other", classes.get(10).getAssetName());
    }

    @Test
    public void getClassificationsForOtherAssetType()
    {
        when(staticDataDtoService.search(any(List.class), any(ServiceErrorsImpl.class))).thenReturn(getAllStaticCodesForAssetClasses());

        ArrayList<AssetClassDto> classes = (ArrayList<AssetClassDto>) assetClassMappingService.search("oth", new ServiceErrorsImpl());

        assertEquals(11, classes.size());
        assertEquals("Cash", classes.get(0).getAssetName());
        assertEquals("Australian fixed interest", classes.get(1).getAssetName());
        assertEquals("International fixed interest", classes.get(2).getAssetName());
        assertEquals("Australian floating rate interest", classes.get(3).getAssetName());
        assertEquals("Australian shares", classes.get(4).getAssetName());
        assertEquals("International shares", classes.get(5).getAssetName());
        assertEquals("Australian real estate", classes.get(6).getAssetName());
        assertEquals("International real estate", classes.get(7).getAssetName());
        assertEquals("Alternatives", classes.get(8).getAssetName());
        assertEquals("Diversified", classes.get(9).getAssetName());
        assertEquals("Other", classes.get(10).getAssetName());
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
        type7.setLabel("International fixed interest");
        type7.setListName("btfg@asset_class_grp");
        type7.setValue("International Fixed Interest");

        StaticCodeDto type8 = new StaticCodeDto();
        type8.setId("60690");
        type8.setIntlId("realest_intnl");
        type8.setLabel("International real estate");
        type8.setListName("btfg@asset_class_grp");
        type8.setValue("International Real Estate");

        StaticCodeDto type9 = new StaticCodeDto();
        type9.setId("60682");
        type9.setIntlId("eq_intnl");
        type9.setLabel("International shares");
        type9.setListName("btfg@asset_class_grp");
        type9.setValue("International Shares");

        StaticCodeDto type10 = new StaticCodeDto();
        type10.setId("60688");
        type10.setIntlId("oth_invst");
        type10.setLabel("Other");
        type10.setListName("btfg@asset_class_grp");
        type10.setValue("Other");


        StaticCodeDto type11 = new StaticCodeDto();
        type11.setId("60693");
        type11.setIntlId("fri_au");
        type11.setLabel("Australian Floating Rate Interest");
        type11.setListName("btfg@asset_class_grp");
        type11.setValue("Australian Floating Rate Interest");


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
        assetClasses.add(type11);

        return assetClasses;
    }
}
