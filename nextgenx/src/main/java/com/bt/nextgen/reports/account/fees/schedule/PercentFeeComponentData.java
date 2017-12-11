package com.bt.nextgen.reports.account.fees.schedule;

import com.bt.nextgen.api.fees.model.PercentageFeeDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;

public class PercentFeeComponentData extends AbstractFeeComponentData {

    private PercentageFeeDto dto;

    public PercentFeeComponentData(PercentageFeeDto dto) {
        super("Percentage fee component");
        this.dto = dto;
    }

    public List<ImmutablePair<String, String>> getChildren() {
        List<ImmutablePair<String, String>> fees = new ArrayList<>();

        if (dto.getShare() != null)
            fees.add(new ImmutablePair<>(AssetType.SHARE.getGroupDescription(),
                    ReportFormatter.format(ReportFormat.PERCENTAGE, fixPercentage(dto.getShare()))));

        if (dto.getManagedFund() != null)
            fees.add(new ImmutablePair<>(AssetType.MANAGED_FUND.getGroupDescription(),
                    ReportFormatter.format(ReportFormat.PERCENTAGE, fixPercentage(dto.getManagedFund()))));

        if (dto.getManagedPortfolio() != null)
            fees.add(new ImmutablePair<>(AssetType.MANAGED_PORTFOLIO.getGroupDescription(),
                    ReportFormatter.format(ReportFormat.PERCENTAGE, fixPercentage(dto.getManagedPortfolio()))));

        if (dto.getTermDeposit() != null)
            fees.add(new ImmutablePair<>(AssetType.TERM_DEPOSIT.getGroupDescription(),
                    ReportFormatter.format(ReportFormat.PERCENTAGE, fixPercentage(dto.getTermDeposit()))));

        if (dto.getCash() != null)
            fees.add(new ImmutablePair<>(AssetType.CASH.getGroupDescription(),
                    ReportFormatter.format(ReportFormat.PERCENTAGE, fixPercentage(dto.getCash()))));
        return fees;
    }

}
