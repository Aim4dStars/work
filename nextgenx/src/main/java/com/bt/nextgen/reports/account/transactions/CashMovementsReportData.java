package com.bt.nextgen.reports.account.transactions;

import com.bt.nextgen.api.portfolio.v3.model.cashmovements.CashMovementsDto;
import com.bt.nextgen.api.portfolio.v3.model.cashmovements.OutstandingIncomeDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

public class CashMovementsReportData {

    private CashMovementsDto cashMovementsDto;

    public CashMovementsReportData(CashMovementsDto cashMovementsDto) {
        this.cashMovementsDto = cashMovementsDto;
    }
    
    public OutstandingCashData getOutstandingCashData() {
        return new OutstandingCashData(cashMovementsDto);
    }
   
    public OutstandingIncomeData getOutstandingIncomeData() {
        OutstandingIncomeDto outstandingIncomeDto = (OutstandingIncomeDto) cashMovementsDto.getOutstandingIncome();
        return new OutstandingIncomeData(outstandingIncomeDto);
    }

    public CashAccountBalanceData getCashAccountBalanceData() {
        return new CashAccountBalanceData(cashMovementsDto);
    }

    public String getTotalCashMovements() {
        return ReportFormatter.format(ReportFormat.CURRENCY, cashMovementsDto.getTotalCashMovements());
    }
}
