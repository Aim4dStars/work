package com.bt.nextgen.reports.account.fees.schedule;

import com.bt.nextgen.api.fees.model.SlidingScaleFeeDto;
import com.bt.nextgen.api.fees.model.SlidingScaleFeeTierDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;

public class SlidingFeeComponentData extends AbstractFeeComponentData {
    private SlidingScaleFeeDto dto;

    public SlidingFeeComponentData(SlidingScaleFeeDto dto) {
        super("Sliding scale fee component");
        this.dto = dto;
    }

    @Override
    public List<? extends Object> getChildren() {
        List<ImmutablePair<String, String>> fees = new ArrayList<>();
        for (SlidingScaleFeeTierDto tier : dto.getSlidingScaleFeeTier()) {
            StringBuilder tierDescription = new StringBuilder();
            tierDescription.append(ReportFormatter.format(ReportFormat.LARGE_CURRENCY, tier.getLowerBound()));
            if (tier.getUpperBound() == null) {
                tierDescription.append(" and above");
            } else {
                tierDescription.append(" - ");
                tierDescription.append(ReportFormatter.format(ReportFormat.LARGE_CURRENCY, tier.getUpperBound()));
            }
            fees.add(new ImmutablePair<>(tierDescription.toString(),
                    ReportFormatter.format(ReportFormat.PERCENTAGE, fixPercentage(tier.getPercentage()))));
        }
        return fees;
    }

    public List<String> getAssetTypes() {
        List<String> assetTypes = new ArrayList<>();
        if (dto.isShare())
            assetTypes.add(AssetType.SHARE.getGroupDescription());
        if (dto.isManagedFund())
            assetTypes.add(AssetType.MANAGED_FUND.getGroupDescription());
        if (dto.isManagedPortfolio())
            assetTypes.add(AssetType.MANAGED_PORTFOLIO.getGroupDescription());
        if (dto.isTermDeposit())
            assetTypes.add(AssetType.TERM_DEPOSIT.getGroupDescription());
        if (dto.isCash())
            assetTypes.add(AssetType.CASH.getGroupDescription());
        return assetTypes;
    }
}
