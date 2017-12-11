package com.bt.nextgen.api.smsf.model;

@SuppressWarnings({"squid:S1068", "findbugs:URF_UNREAD_FIELD"})
public class AssetClassOrderedDto extends AssetClassDto
{

    private AssetClassDto assetClass = null;

    private int order;

    public AssetClassOrderedDto(AssetClassDto assetClass)
    {
        this.assetClass = assetClass;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
