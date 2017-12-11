package com.bt.nextgen.service.integration.externalasset.model;

import com.bt.nextgen.core.conversion.AssetKeyConverter;
import com.bt.nextgen.integration.xml.annotation.LazyServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.asset.AssetKey;

/**
 * External Asset that is registered on the Panorama platform.
 * <p>
 * Typically these are standardised assets that are tradeable (i.e. listed securities, managed funds).
 * </p>
 */
@LazyServiceBean(expression = "if (count(asset_id/val) >0) then true() else false()")
public class OnPlatformExternalAssetImpl extends AbstractExternalAsset implements OnPlatformExternalAsset
{

    @ServiceElement(xpath="asset_id/val", converter = AssetKeyConverter.class)
    private AssetKey assetKey = null;

    private String assetCode = null;

    public AssetKey getKey()
    {
        return assetKey;
    }

    public AssetKey getAssetKey()
    {
        return assetKey;
    }

    public void setAssetKey(AssetKey assetKey)
    {
        this.assetKey = assetKey;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }
}