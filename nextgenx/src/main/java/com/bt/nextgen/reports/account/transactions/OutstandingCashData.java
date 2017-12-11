package com.bt.nextgen.reports.account.transactions;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.core.IsEqual;

import ch.lambdaj.Lambda;

import com.bt.nextgen.api.portfolio.v3.model.cashmovements.CashMovementsDto;
import com.bt.nextgen.api.portfolio.v3.model.cashmovements.OutstandingMovementsDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

public class OutstandingCashData {
    private CashMovementsDto cashMovementsDto;

    public OutstandingCashData(CashMovementsDto cashMovementsDto) {
        this.cashMovementsDto = cashMovementsDto;
    }

    public List<OutstandingCashMovementsData> getChildren() {
        List<OutstandingMovementsDto> outstandingCashMovementDtos = cashMovementsDto.getOutstandingCash();
        List<OutstandingCashMovementsData> outstandingCashMovements = new ArrayList<>();
        for (OutstandingMovementsDto outstandingCashMovementDto : outstandingCashMovementDtos) {
            outstandingCashMovements.add(new OutstandingCashMovementsData(outstandingCashMovementDto));
        }
        List<OutstandingCashMovementsData> unsettledBuysList = Lambda.select(outstandingCashMovements,
                having(on(OutstandingCashMovementsData.class).getCategory(), IsEqual.equalTo("Unsettled buys")));
        if (unsettledBuysList.isEmpty()) {
            outstandingCashMovements.add(new OutstandingCashMovementsData("Unsettled buys", BigDecimal.ZERO));
        }
        List<OutstandingCashMovementsData> unsettledSellsList = Lambda.select(outstandingCashMovements,
                having(on(OutstandingCashMovementsData.class).getCategory(), IsEqual.equalTo("Unsettled sells")));
        if (unsettledSellsList.isEmpty()) {
            outstandingCashMovements.add(new OutstandingCashMovementsData("Unsettled sells", BigDecimal.ZERO));
        }
        return outstandingCashMovements;
    }

    public String getOtherAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, cashMovementsDto.getOther());
    }

    public String getAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, cashMovementsDto.getOutstandingTotal());
    }

}
