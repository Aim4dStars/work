package com.bt.nextgen.api.income.v2.model;

import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.income.IncomeType;

public class IncomeValueFlatDto extends IncomeValueDto {
    private final AssetType investmentType;
    private final IncomeType incomeType;
    private final IncomeType incomeSubtype;
    private final String reportType;

    public IncomeValueFlatDto(IncomeValueDto incomeValue, AssetType investmentType, IncomeType incomeType, String reportType) {
        super(incomeValue);
        this.investmentType = investmentType;
        this.incomeType = incomeType;
        this.reportType = reportType;
        this.incomeSubtype = incomeValue.getIncomeType();
    }

    public String getInvestmentType() {
        return investmentType.getGroupDescription();
    }

    public IncomeType getIncomeType() {
        return incomeType;
    }

    public String getIncomeTypeDescription() {
        return incomeType.getDisplayName();
    }


    public String getDescription() {
        String description = null;

        if (investmentType == AssetType.TERM_DEPOSIT) {
            description = getTermDepositDescription();
        } else if (incomeType == IncomeType.CASH) {
            description = getCashDescription();
        }
        return description;
    }

    private String getTermDepositDescription() {
        String description = "";
        if (("Income Received").equals(reportType)) {
            description =  Boolean.TRUE.equals(getWrapIncome()) ? "Interest received" : "Interest received: Maturing "
                    + ReportFormatter.format(ReportFormat.SHORT_DATE, getMaturityDate());
        } else {
            description = "Interest accrued: Maturing "
                    + ReportFormatter.format(ReportFormat.SHORT_DATE, getMaturityDate());
        }
        return description;
    }

    private String getCashDescription() {
        String description = "";
        if (investmentType == AssetType.CASH) {
            description = "Cash ";
        } else if (investmentType == AssetType.MANAGED_PORTFOLIO) {
            description = "Managed portfolio cash ";
        } else if (investmentType == AssetType.TAILORED_PORTFOLIO) {
            description = "Tailored portfolio cash ";
        }
        if (incomeSubtype == IncomeType.INTEREST) {
            description += "interest ";
        } else {
            description += "income ";
        }

        if ("Income Received".equals(reportType)) {
            if (incomeSubtype == IncomeType.INTEREST) {
                description += "payment";
            } else {
                description += "distributed";
            }
        } else {
            description += "accrued";
        }
        return description;
    }
}
