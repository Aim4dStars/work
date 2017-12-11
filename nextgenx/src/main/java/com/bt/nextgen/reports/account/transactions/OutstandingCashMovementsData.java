package com.bt.nextgen.reports.account.transactions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.bt.nextgen.api.portfolio.v3.model.cashmovements.OutstandingCash;
import com.bt.nextgen.api.portfolio.v3.model.cashmovements.OutstandingMovementsDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

public class OutstandingCashMovementsData {
    private OutstandingMovementsDto outstandingMovementsDto;
    private String category;
    private BigDecimal amount;

    public OutstandingCashMovementsData(OutstandingMovementsDto outstandingMovementsDto) {
        this.outstandingMovementsDto = outstandingMovementsDto;
        this.category = outstandingMovementsDto.getCategory();
        this.amount = outstandingMovementsDto.getAmount();
    }

    public OutstandingCashMovementsData(String category, BigDecimal amount) {
        this.category = category;
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public String getAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, amount);
    }

    public List<OutstandingCashItemData> getChildren() {
        List<OutstandingCashItemData> outstandingCashReportDatas = new ArrayList<>();
        if (outstandingMovementsDto != null) {
            List<OutstandingCash> outstandingCashDtos = outstandingMovementsDto.getOutstanding();
            for (OutstandingCash outstandingCashDto : outstandingCashDtos) {
                outstandingCashReportDatas.add(new OutstandingCashItemData(outstandingCashDto));
            }
        }
        return outstandingCashReportDatas;
    }

    public String getTotalDescription() {
        return "Total " + getCategory();
    }
}
