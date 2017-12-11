package com.bt.nextgen.api.portfolio.v3.model.cashmovements;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.income.CashIncome;
import com.bt.nextgen.service.integration.income.DistributionIncome;
import com.bt.nextgen.service.integration.income.DividendIncome;
import com.bt.nextgen.service.integration.income.HoldingIncomeDetails;
import com.bt.nextgen.service.integration.income.Income;
import com.bt.nextgen.service.integration.income.TermDepositIncome;

public class OutstandingIncomeDto extends AbstractOutstandingCashDto {
    private List<OutstandingCash> outstanding = new ArrayList<>();

    public OutstandingIncomeDto(Map<AssetKey, TermDepositPresentation> tds,
            List<HoldingIncomeDetails> income) {
        for (HoldingIncomeDetails holdingIncome : income) {
            addHoldingIncome(tds, holdingIncome);
        }
    }

    public List<OutstandingCash> getOutstanding() {
        return outstanding;
    }

    private void addHoldingIncome(Map<AssetKey, TermDepositPresentation> tds,
            HoldingIncomeDetails holdingIncome) {
        Asset asset = holdingIncome.getAsset();
        AssetKey assetKey = AssetKey.valueOf(asset.getAssetId());
        for (Income incomeItem : holdingIncome.getIncomes()) {
            if (incomeItem instanceof DividendIncome) {
                outstanding.add(new OutstandingCash(asset, (DividendIncome) incomeItem));
            } else if (incomeItem instanceof DistributionIncome) {
                outstanding.add(new OutstandingCash(asset, (DistributionIncome) incomeItem));
            } else if (incomeItem instanceof CashIncome) {
                addOutstandingCashIncome(asset, incomeItem);
            } else if (incomeItem instanceof TermDepositIncome) {
                addOutstandingTermDepositIncome(asset, tds.get(assetKey), incomeItem);
            }

        }
    }

    private void addOutstandingCashIncome(Asset asset, Income incomeItem) {
        CashIncome cashIncome = (CashIncome) incomeItem;
        if (cashIncome.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            outstanding.add(new OutstandingCash(asset, cashIncome));
        }
    }

    private void addOutstandingTermDepositIncome(Asset asset, TermDepositPresentation tdPresentation, Income incomeItem) {
        TermDepositIncome termDepositIncome = (TermDepositIncome) incomeItem;
        if (termDepositIncome.getInterest().compareTo(BigDecimal.ZERO) > 0) {
            outstanding.add(new OutstandingCash(asset, tdPresentation, termDepositIncome));
        }
    }
}
