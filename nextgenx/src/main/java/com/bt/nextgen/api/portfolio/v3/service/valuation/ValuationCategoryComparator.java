package com.bt.nextgen.api.portfolio.v3.service.valuation;

import com.bt.nextgen.api.portfolio.v3.model.valuation.ValuationSummaryDto;

import java.util.Comparator;

public class ValuationCategoryComparator implements Comparator<ValuationSummaryDto> {
    @Override
    public int compare(ValuationSummaryDto o1, ValuationSummaryDto o2) {
        Integer o1SortOrder = o1.getAssetType().getSortOrder();
        Integer o2SortOrder = o2.getAssetType().getSortOrder();
        return o1SortOrder.compareTo(o2SortOrder);
    }
}
