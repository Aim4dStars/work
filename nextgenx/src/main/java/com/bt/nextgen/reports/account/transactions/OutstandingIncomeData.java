package com.bt.nextgen.reports.account.transactions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.builder.CompareToBuilder;

import com.bt.nextgen.api.portfolio.v3.model.cashmovements.OutstandingCash;
import com.bt.nextgen.api.portfolio.v3.model.cashmovements.OutstandingIncomeDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

public class OutstandingIncomeData {

    private OutstandingIncomeDto outstandingIncomeDto;

    public OutstandingIncomeData(OutstandingIncomeDto outstandingIncomeDto) {
        this.outstandingIncomeDto = outstandingIncomeDto;
    }

    public List<OutstandingCashItemData> getChildren() {
        List<OutstandingCash> outstandingCash = outstandingIncomeDto.getOutstanding();

        List<OutstandingCash> outstandingCashDtos = new ArrayList<>(outstandingCash);
        Collections.sort(outstandingCashDtos, new Comparator<OutstandingCash>() {
            @Override
            public int compare(OutstandingCash o1, OutstandingCash o2) {
                return new CompareToBuilder().append(o1.getAssetType().getSortOrder(), o2.getAssetType().getSortOrder())
                        .append(o1.getSettlementDate(), o2.getSettlementDate()).append(o1.getAssetCode(), o2.getAssetCode())
                        .toComparison();
            }
        });

        List<OutstandingCashItemData> outstandingCashItems = new ArrayList<>();
        for (OutstandingCash outstandingCashDto : outstandingCashDtos) {
            outstandingCashItems.add(new OutstandingCashItemData(outstandingCashDto));
        }

        return outstandingCashItems;

    }

    public String getAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, outstandingIncomeDto.getAmount());
    }
}
