package com.bt.nextgen.api.portfolio.v3.service.valuation;


import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;

import java.util.Comparator;

public class ValuationComparator implements Comparator<InvestmentValuationDto> {
    @Override
    public int compare(InvestmentValuationDto o1, InvestmentValuationDto o2) {
        if (o1.getExternalAsset() && !o2.getExternalAsset()) {
            return 1;
        }
        if (!o1.getExternalAsset() && o2.getExternalAsset()) {
            return -1;
        }
        String o1Name = o1.getName();
        String o2Name = o2.getName();
        return o1Name.compareToIgnoreCase(o2Name);
    }
}
