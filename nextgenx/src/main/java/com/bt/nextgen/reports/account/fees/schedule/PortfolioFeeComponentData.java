package com.bt.nextgen.reports.account.fees.schedule;

import com.bt.nextgen.api.fees.model.IpsFeesTypeTrxnDto;
import com.bt.nextgen.api.fees.model.SlidingScaleFeeTierDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PortfolioFeeComponentData extends AbstractFeeComponentData {

    private IpsFeesTypeTrxnDto dto;

    public PortfolioFeeComponentData(IpsFeesTypeTrxnDto pmf) {
        super("Portfolio management fee");
        this.dto = pmf;
    }

    @Override 
    public String getName() {
        String name = null;
        if (dto.getPercentage() != null) {
            name= "Percentage fee component";
        } else if (dto.getSlidingScaleFeeTier() !=null){
            name = "Sliding scale fee component";
        }
        return name;
    }

    @Override
    public List<? extends Object> getChildren() {
        if (dto.getSlidingScaleFeeTier() == null) {
            return Collections.emptyList();
        }
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

    public String getPercentage() {
        return ReportFormatter.format(ReportFormat.PERCENTAGE, fixPercentage(dto.getPercentage()));
    }

    public String getAsset() {
        String assetCode = dto.getApirCode() == null ? dto.getCode() : dto.getApirCode();
        String assetName = dto.getInvestmentName();
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(assetCode)) {
            builder.append("<b>");
            builder.append(assetCode);
            builder.append(" &#183 ");
            builder.append("</b> ");
        }
        builder.append(assetName);
        return builder.toString();
    }

    // Key and value accessors added to allow reuse of percentage reports.
    public String getKey() {
        return getAsset();
    }

    public String getValue() {
        return getPercentage();
    }

}
