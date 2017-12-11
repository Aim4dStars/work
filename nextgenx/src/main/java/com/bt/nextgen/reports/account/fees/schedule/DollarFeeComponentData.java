package com.bt.nextgen.reports.account.fees.schedule;

import com.bt.nextgen.api.fees.model.DollarFeeDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

import java.util.Collections;
import java.util.List;

public class DollarFeeComponentData extends AbstractFeeComponentData {

    private DollarFeeDto dto;

    public DollarFeeComponentData(DollarFeeDto dto) {
        super("Dollar fee component");
        this.dto = dto;
    }

    public String getFeeAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, dto.getAmount());
    }

    public String getCpiDate() {
        // DTO is storing a string instead of a date. We have to rely on it to manage our formatting.
        return dto.getDate();
    }

    public Boolean getCpiIndexed() {
        return dto.isCpiindex();
    }

    @Override
    public List<? extends Object> getChildren() {
        return Collections.emptyList();
    }

}
