package com.bt.nextgen.api.portfolio.v3.service.allocation;

import com.bt.nextgen.api.portfolio.v3.model.allocation.AllocationDto;
import com.bt.nextgen.service.integration.asset.AssetClass;

import java.io.Serializable;
import java.util.Comparator;

class SectorComparator implements Comparator<AllocationDto>, Serializable {
    private static final long serialVersionUID = -7392589603199256240L;

    @Override
    public int compare(AllocationDto o1, AllocationDto o2) {
        Integer o1SortOrder = AssetClass.forDescription(o1.getName()).getOrder();
        Integer o2SortOrder = AssetClass.forDescription(o2.getName()).getOrder();
        return o1SortOrder.compareTo(o2SortOrder);
    }
}