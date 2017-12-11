package com.bt.nextgen.reports.account.income;

import com.bt.nextgen.api.income.v2.model.IncomeDto;
import com.bt.nextgen.api.income.v2.model.IncomeValueDto;
import com.bt.nextgen.api.income.v2.model.InvestmentIncomeTypeDto;
import com.bt.nextgen.api.income.v2.model.InvestmentTypeDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.income.IncomeType;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class IncomeValueReportData {

    private IncomeDto incomeDto;
    private IncomeDto parent;
    private IncomeDto superParent;
    private String reportType;
    private List<IncomeValueReportData> incomeValuesReportData = new ArrayList<>();
    private static final String TOTAL = "Total ";
    private static final String EMPTY_SPACE = " ";
    private static final String INCOME = "income ";
    private static final String INTEREST = "interest ";

    public IncomeValueReportData(IncomeDto incomeDto, IncomeDto parent, IncomeDto superParent, String reportType) {
        this.incomeDto = incomeDto;
        this.parent = parent;
        this.superParent = superParent;
        this.reportType = reportType;
        for (IncomeDto income : incomeDto.getChildren()) {
            incomeValuesReportData.add(new IncomeValueReportData(income, incomeDto, parent, reportType));
        }

    }

    public String getAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, incomeDto.getAmount());
    }

    public String getPaymentDate() {
        DateTime paymentDate = null;
        if (incomeDto instanceof IncomeValueDto) {
            paymentDate = ((IncomeValueDto) incomeDto).getPaymentDate();
            return ReportFormatter.format(ReportFormat.SHORT_DATE, paymentDate);
        }
        return ReportFormatter.format(ReportFormat.SHORT_DATE, paymentDate);
    }

    public String getMaturityDate() {
        DateTime maturityDate = null;
        if (incomeDto instanceof IncomeValueDto) {
            maturityDate = ((IncomeValueDto) incomeDto).getMaturityDate();
            return ReportFormatter.format(ReportFormat.SHORT_DATE, maturityDate);
        }
        return ReportFormatter.format(ReportFormat.SHORT_DATE, maturityDate);
    }

    public String getExecutionDate() {
        DateTime executionDate = null;
        if (incomeDto instanceof IncomeValueDto) {
            executionDate = ((IncomeValueDto) incomeDto).getExecutionDate();
            return ReportFormatter.format(ReportFormat.SHORT_DATE, executionDate);
        }
        return ReportFormatter.format(ReportFormat.SHORT_DATE, executionDate);
    }

    public String getQuantity() {
        BigDecimal quantity = null;
        if (incomeDto instanceof IncomeValueDto) {
            quantity = ((IncomeValueDto) incomeDto).getQuantity();
            return ReportFormatter.format(ReportFormat.UNITS, quantity);
        }
        return ReportFormatter.format(ReportFormat.UNITS, quantity);
    }

    public String getIncomeRate() {
        BigDecimal incomeRate = null;
        if (incomeDto instanceof IncomeValueDto) {
            incomeRate = ((IncomeValueDto) incomeDto).getIncomeRate();
            return ReportFormatter.format(ReportFormat.CURRENCY, incomeRate);
        }
        return ReportFormatter.format(ReportFormat.CURRENCY, incomeRate);
    }

    public String getFrankedDividend() {
        BigDecimal frankedDividend = null;
        if (incomeDto instanceof IncomeValueDto) {
            frankedDividend = ((IncomeValueDto) incomeDto).getFrankedDividend();
            return ReportFormatter.format(ReportFormat.CURRENCY, frankedDividend);
        }
        return ReportFormatter.format(ReportFormat.CURRENCY, frankedDividend);
    }

    public String getUnfrankedDividend() {
        BigDecimal unfrankedDividend = null;
        if (incomeDto instanceof IncomeValueDto) {
            unfrankedDividend = ((IncomeValueDto) incomeDto).getUnfrankedDividend();
            return ReportFormatter.format(ReportFormat.CURRENCY, unfrankedDividend);
        }
        return ReportFormatter.format(ReportFormat.CURRENCY, unfrankedDividend);
    }

    public String getFrankingCredit() {
        BigDecimal frankingCredit = null;
        if (incomeDto instanceof IncomeValueDto) {
            frankingCredit = ((IncomeValueDto) incomeDto).getFrankingCredit();
            return ReportFormatter.format(ReportFormat.CURRENCY, frankingCredit);
        }
        return ReportFormatter.format(ReportFormat.CURRENCY, frankingCredit);
    }

    public List<IncomeValueReportData> getIncomeValuesReportData() {
        return this.incomeValuesReportData;
    }

    public Boolean getShowSubGroup() {
        if (incomeDto instanceof InvestmentTypeDto) {
            InvestmentTypeDto investmentTypeDto = (InvestmentTypeDto) incomeDto;
            if (investmentTypeDto.getAssetType().equals(AssetType.CASH)
                    || investmentTypeDto.getAssetType().equals(AssetType.TERM_DEPOSIT)) {
                return Boolean.FALSE;
            } else {
                return Boolean.TRUE;
            }
        }
        return false;
    }

    public String getName() {
        return incomeDto.getName();
    }

    public String getCode() {
        return incomeDto.getCode();
    }

    public String getDescription() {
        String description = null;
        if (incomeDto instanceof IncomeValueDto) {
            InvestmentIncomeTypeDto investmentIncomeTypeDto = (InvestmentIncomeTypeDto) parent;
            InvestmentTypeDto investmentTypeDto = (InvestmentTypeDto) superParent;
            if (investmentTypeDto.getAssetType() == AssetType.TERM_DEPOSIT) {
                description =  getTermDepositDescription((IncomeValueDto) incomeDto);
            } else if (investmentIncomeTypeDto.getIncomeType() == IncomeType.CASH) {
                description = getCashDescription(investmentTypeDto, (IncomeValueDto) incomeDto);
            }
        }
        return description;
    }

    private String getTermDepositDescription(IncomeValueDto incomeValue) {
        String description = "";
        if (("received").equals(reportType)) {
            description =  Boolean.TRUE.equals(incomeValue.getWrapIncome()) ? "Interest received"
                    : "Interest received: Maturing " + getMaturityDate();
        } else {
            description = "Interest accrued: Maturing " + getMaturityDate();
        }
        return description;
    }

    private String getCashDescription(InvestmentTypeDto investmentTypeDto, IncomeValueDto incomeValue) {
        String description = "";
        if (investmentTypeDto.getAssetType() == AssetType.CASH) {
            description = "Cash ";
        } else if (investmentTypeDto.getAssetType() == AssetType.MANAGED_PORTFOLIO) {
            description = "Managed portfolio cash ";
        } else if (investmentTypeDto.getAssetType() == AssetType.TAILORED_PORTFOLIO) {
            description = "Tailored portfolio cash ";
        }
        IncomeType subtype = incomeValue.getIncomeType();
        if(subtype == IncomeType.INTEREST) {
            description += "interest ";
        } else {
            description += "income ";
        }

        if ("received".equals(reportType)) {
            if(subtype == IncomeType.INTEREST) {
                description += "payment";
            } else {
                description += "distributed";
            }
        } else {
            description += "accrued";
        }
        return description;
    }

    public String getSummaryDescription() {
        if (incomeDto instanceof InvestmentIncomeTypeDto) {
            InvestmentTypeDto investmentTypeDto = (InvestmentTypeDto) parent;
            if (investmentTypeDto.getAssetType().equals(AssetType.TERM_DEPOSIT)) {
                return new StringBuilder(TOTAL).append(investmentTypeDto.getAssetType().getGroupDescription().toLowerCase())
                        .append(EMPTY_SPACE).append(INTEREST).append(reportType.toLowerCase()).toString();
            } else {
                return new StringBuilder(TOTAL).append(investmentTypeDto.getAssetType().getGroupDescription().toLowerCase())
                        .append(EMPTY_SPACE).append(INCOME).append(reportType.toLowerCase()).toString();
            }
        } else if (incomeDto instanceof IncomeValueDto) {
            InvestmentIncomeTypeDto investmentIncomeTypeDto = (InvestmentIncomeTypeDto) parent;
            InvestmentTypeDto investmentTypeDto = (InvestmentTypeDto) superParent;
            return new StringBuilder(TOTAL).append(investmentTypeDto.getAssetType().getGroupDescription().toLowerCase())
                    .append(EMPTY_SPACE).append(investmentIncomeTypeDto.getIncomeType().getGroupDescription().toLowerCase())
                    .append(EMPTY_SPACE).append(reportType.toLowerCase()).toString();
        } else {
            return null;
        }
    }

    public String getReportType() {
        return reportType;
    }
}
