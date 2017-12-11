package com.bt.nextgen.reports.account.transactions;

import java.math.BigDecimal;

import com.bt.nextgen.api.portfolio.v3.model.cashmovements.CashMovementsDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

public class CashAccountBalanceData {
    private CashMovementsDto cashMovementsDto;

    public CashAccountBalanceData(CashMovementsDto cashMovementsDto) {
        this.cashMovementsDto = cashMovementsDto;
    }

    public String getMinimumCashBalance() {
        return ReportFormatter.format(ReportFormat.CURRENCY, cashMovementsDto.getMinCash());
    }

    public String getReservedCashBalance() {
        return ReportFormatter.format(ReportFormat.CURRENCY, cashMovementsDto.getReservedCash());
    }

    public String getCurrentCashAccountBalance() {
        return ReportFormatter.format(ReportFormat.CURRENCY, cashMovementsDto.getValueDateCash());
    }

    public String getAvailableCash() {
        return ReportFormatter.format(ReportFormat.CURRENCY, cashMovementsDto.getAvailableCash());
    }

    public String getTotalAvailableCash() {
        boolean availableCashNegative = getIsAvailableCashNegative();
        if (availableCashNegative) {
            return ReportFormatter.format(ReportFormat.CURRENCY, BigDecimal.ZERO);
        }else{
            return getAvailableCash();
        }
    }

    public boolean getIsAvailableCashNegative() {
        return cashMovementsDto.getAvailableCash().compareTo(BigDecimal.ZERO) < 0;
    }

}
