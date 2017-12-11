package com.bt.nextgen.api.modelportfolio.v2.service;

import com.bt.nextgen.api.smsf.model.AssetClassDto;
import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ModelAssetClassConverterTest {

    @InjectMocks
    private ModelAssetClassConverter modelAssetClassConverter;

    @Test
    public void testOrderAndLabel() {
        List<StaticCodeDto> assetStaticCodes = new ArrayList<>();

        assetStaticCodes.add(new StaticCodeDto("9656", "ASSET_CLASS", "ALT_INVST", "alt_invst", "Alternative Investment"));
        assetStaticCodes.add(new StaticCodeDto("9665", "ASSET_CLASS", "CASH", "cash", "Cash"));
        assetStaticCodes.add(new StaticCodeDto("9658", "ASSET_CLASS", "DIVED", "dived", "Diversified"));
        assetStaticCodes.add(new StaticCodeDto("9653", "ASSET_CLASS", "FI_INTNL", "fi_intnl", "International Fixed Interest"));
        assetStaticCodes.add(new StaticCodeDto("9664", "ASSET_CLASS", "LIQUID", "liquid", "Liquid"));
        assetStaticCodes.add(new StaticCodeDto("9652", "ASSET_CLASS", "FI_AU", "fi_au", "Australian Fixed Interest"));
        assetStaticCodes.add(new StaticCodeDto("9651", "ASSET_CLASS", "EQ_INTNL", "eq_intnl", "International Shares"));
        assetStaticCodes.add(new StaticCodeDto("9650", "ASSET_CLASS", "EQ_AU", "eq_au", "Australian Shares"));
        assetStaticCodes
                .add(new StaticCodeDto("9777", "ASSET_CLASS", "FRI_INTNL", "fri_au", "Australian Floating Rate Interest"));
        assetStaticCodes.add(new StaticCodeDto("9778", "ASSET_CLASS", "DIVED_FI", "dived_fi", "Diversified fixed interest"));
        assetStaticCodes.add(new StaticCodeDto("9779", "ASSET_CLASS", "INFRA", "infrastructure", "Listed infrastructure"));
        assetStaticCodes.add(new StaticCodeDto("9780", "ASSET_CLASS", "OTH_INVST", "oth_invst", "Other"));

        List<AssetClassDto> modelAssetClasses = modelAssetClassConverter.convertToModelAssetClasses(assetStaticCodes);

        Assert.assertEquals(modelAssetClasses.get(0).getAssetName(), "Australian shares");
        Assert.assertEquals(modelAssetClasses.get(1).getAssetName(), "International shares");
        Assert.assertEquals(modelAssetClasses.get(2).getAssetName(), "Australian fixed interest");
        Assert.assertEquals(modelAssetClasses.get(3).getAssetName(), "Australian floating rate interest");
        Assert.assertEquals(modelAssetClasses.get(4).getAssetName(), "International fixed interest");
        Assert.assertEquals(modelAssetClasses.get(5).getAssetName(), "Diversified fixed interest");
        Assert.assertEquals(modelAssetClasses.get(6).getAssetName(), "Listed infrastructure");
        Assert.assertEquals(modelAssetClasses.get(7).getAssetName(), "Alternative investment");
        Assert.assertEquals(modelAssetClasses.get(8).getAssetName(), "Diversified");
        Assert.assertEquals(modelAssetClasses.get(9).getAssetName(), "Other");
        Assert.assertEquals(modelAssetClasses.get(10).getAssetName(), "Liquid");
        Assert.assertEquals(modelAssetClasses.get(11).getAssetName(), "Cash");
    }

}
