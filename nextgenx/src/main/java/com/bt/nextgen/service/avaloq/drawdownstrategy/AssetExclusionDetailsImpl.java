package com.bt.nextgen.service.avaloq.drawdownstrategy;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.drawdownstrategy.AssetExclusionDetails;

@ServiceBean(xpath = "draw_dwn_excl_pref_item | dd_excl_pref")
public class AssetExclusionDetailsImpl implements AssetExclusionDetails {

    @ServiceElement(xpath = "asset_id/val | asset_excl_id/val")
    private String assetId;

    public AssetExclusionDetailsImpl() {
        super();
    }

    @Override
    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }
}
