package com.bt.nextgen.api.smsf.service;

import com.bt.nextgen.api.smsf.constants.AssetType;
import com.bt.nextgen.api.smsf.model.AssetTypeDto;
import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.btfin.panorama.core.util.StringUtil;


public class AssetTypeConverter
{
    public AssetTypeDto toAssetTypeDto(StaticCodeDto staticCode)
    {
        AssetTypeDto assetTypeDto = new AssetTypeDto();
        assetTypeDto.setAssetCode(staticCode.getIntlId());
        assetTypeDto.setAssetName(StringUtil.toProperCase(staticCode.getLabel()));
        assetTypeDto.setOrder(9999);                        // Default order to end of list

        return assetTypeDto;
    }

    public AssetTypeDto toAssetTypeDto(StaticCodeDto staticCode, AssetType assetType)
    {
        AssetTypeDto assetTypeDto = toAssetTypeDto(staticCode);

        if (assetType != null && assetType.getOrder() > 0)
            assetTypeDto.setOrder(assetType.getOrder());

        return assetTypeDto;
    }
}
