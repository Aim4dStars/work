package com.bt.nextgen.api.smsf.service;

import com.bt.nextgen.api.smsf.constants.AssetClass;
import com.bt.nextgen.api.smsf.model.AssetClassDto;
import com.bt.nextgen.api.smsf.model.AssetClassOrderedDto;
import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.btfin.panorama.core.util.StringUtil;


public class AssetClassConverter
{
    public AssetClassDto toAssetClassDto(StaticCodeDto staticCode)
    {
        AssetClassDto assetTypeDto = new AssetClassDto();
        assetTypeDto.setAssetCode(staticCode.getIntlId());
        assetTypeDto.setAssetName(StringUtil.toProperCase(staticCode.getLabel()));
        //assetTypeDto.setOrder(9999);                        // Default order to end of list

        return assetTypeDto;
    }

    public AssetClassOrderedDto toAssetClassOrderedDto(StaticCodeDto staticCode)
    {
        AssetClassOrderedDto assetTypeDto = new AssetClassOrderedDto(new AssetClassDto());
        assetTypeDto.setAssetCode(staticCode.getIntlId());
        assetTypeDto.setAssetName(StringUtil.toProperCase(staticCode.getLabel()));
        assetTypeDto.setOrder(9999);                        // Default order to end of list

        return assetTypeDto;
    }

    public AssetClassOrderedDto toAssetClassOrderedDto(StaticCodeDto staticCode, AssetClass assetClass)
    {
        AssetClassOrderedDto assetClassDto = toAssetClassOrderedDto(staticCode);

        if (assetClass != null && assetClass.getOrder() > 0)
            assetClassDto.setOrder(assetClass.getOrder());

        return assetClassDto;
    }
}
