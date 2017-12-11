package com.bt.nextgen.service.wrap.integration.income;

import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.income.IncomeType;
import com.btfin.panorama.wrap.model.Income;

/**
 * Retrieve Income type for Wrap Investment Income
 * Refer com.bt.nextgen.service.avaloq.income.IncomeConverterModelBuilder
 * Created by L067221 on 4/08/2017.
 */
public final class WrapIncomeConverterModelBuilder {
    private WrapIncomeConverterModelBuilder() {
    }

    /**
     * returns IncomeType for Investment Income based on Asset
     * @param asset
     * @param income
     * @return
     */
    static IncomeType getIncomeType(Asset asset, Income income) {
        IncomeType incomeType = IncomeType.UNCATEGORISED;
        switch (asset.getAssetType()) {
            case CASH:
                incomeType = IncomeType.CASH;
                break;
            case TERM_DEPOSIT:
                incomeType = IncomeType.TERM_DEPOSIT;
                break;
            case MANAGED_FUND:
                incomeType = IncomeType.DISTRIBUTION;
                break;
            case SHARE:
            case BOND:
            case OPTION:
                incomeType = getShareIncomeType(asset, income);
                break;
            default:
                break;
        }
        return incomeType;
    }

    /**
     * returns IncomeType INTEREST, DIVIDEND and DISTRIBUTION
     * @param asset
     * @param income
     * @return
     */
    static private IncomeType getShareIncomeType(Asset asset, Income income) {
        IncomeType incomeType;
        if (asset.getRevenueAssetIndicator() != null) {
            incomeType = IncomeType.INTEREST;
        }
        else if (income.getFrankAmount() != null || income.getUnFrankAmount() != null) {
            incomeType = IncomeType.DIVIDEND;
        }
        else {
            incomeType = IncomeType.DISTRIBUTION;
        }
        return incomeType;
    }
}
