package com.bt.nextgen.api.portfolio.v3.service.performance;

import com.bt.nextgen.api.portfolio.v3.model.performance.PeriodPerformanceDto;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.service.integration.asset.AssetType;

import java.io.Serializable;
import java.util.Comparator;

public class PerformanceComparator implements Comparator<PeriodPerformanceDto>, Serializable {

    private static final long serialVersionUID = -7392589603199256240L;

    @Override
    public int compare(PeriodPerformanceDto o1, PeriodPerformanceDto o2) {
        return compareAsset(getName(o1), getName(o2), getAssetType(o1), getAssetType(o2));
    }

    private String getName(PeriodPerformanceDto o) {
        return o.getName() == null ? Constants.EMPTY_STRING : o.getName().toLowerCase();
    }

    private AssetType getAssetType(PeriodPerformanceDto o) {
        return AssetType.forDisplay(o.getAssetType());
    }

    private int compareAsset(String name1, String name2, AssetType assetType1, AssetType assetType2) {
        // Sort by asset type, then alphabetically
        if (assetType1.getSortOrder() < (assetType2.getSortOrder())) {
            return -1;
        } else if (assetType1.getSortOrder() > assetType2.getSortOrder()) {
            return 1;
        } else {
            return name1.compareTo(name2);
        }
    }
}
