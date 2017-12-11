package com.bt.nextgen.reports.account.fees.schedule;

import com.bt.nextgen.api.fees.model.FlatPercentageFeeDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;

public class FlatPercentFeeComponentData extends AbstractFeeComponentData {

    private FlatPercentageFeeDto dto;

    public FlatPercentFeeComponentData(FlatPercentageFeeDto dto) {
        super("Flat Percentage fee component");
        this.dto = dto;
    }

    public List<ImmutablePair<String, String>> getChildren() {
        List<ImmutablePair<String, String>> fees = new ArrayList<>();

        if (StringUtils.isNotEmpty(dto.getName())) {
            fees.add(new ImmutablePair<>(dto.getLabel(),
                    ReportFormatter.format(ReportFormat.PERCENTAGE, fixPercentage(dto.getRate()))));
        }

        return fees;
    }

}
