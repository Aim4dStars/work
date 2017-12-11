package com.bt.nextgen.reports.account.performance;

import com.bt.nextgen.api.portfolio.v3.model.performance.ManagedPortfolioPerformanceDto;
import com.bt.nextgen.api.portfolio.v3.model.performance.PeriodPerformanceDto;
import com.bt.nextgen.api.portfolio.v3.model.performance.TermDepositPerformanceDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AccountAssetPerformanceReportData {

    private List<AccountAssetPerformanceReportData> children = new ArrayList<>();
    private String icon;
    private String description;
    private boolean investmentReturnAvailable;
    private boolean cmaMigratedAccount;
    private boolean isChild = false;
    private PeriodPerformanceDto dto;
    private static final String NOT_AVAILABLE = "-";

    public AccountAssetPerformanceReportData(PeriodPerformanceDto dto, boolean investmentReturnAvailable,
                                             boolean cmaMigratedAccount) {
        this.dto = dto;
        this.investmentReturnAvailable = investmentReturnAvailable;
        this.cmaMigratedAccount = cmaMigratedAccount;
        if (dto instanceof ManagedPortfolioPerformanceDto) {
            ManagedPortfolioPerformanceDto mpDto = (ManagedPortfolioPerformanceDto) dto;
            for (PeriodPerformanceDto child : mpDto.getAssetPerformance()) {
                this.addChild(new AccountAssetPerformanceReportData(child, true,
                        investmentReturnAvailable, cmaMigratedAccount));
            }
        }
        if (dto instanceof TermDepositPerformanceDto) {
            icon = ((TermDepositPerformanceDto) dto).getBrand();
        }
    }

    public AccountAssetPerformanceReportData(PeriodPerformanceDto dto, boolean isChild,
                                             boolean investmentReturnAvailable,
                                             boolean cmaMigratedAccount) {
        this(dto, investmentReturnAvailable, cmaMigratedAccount);
        this.isChild = isChild;
    }

    public AccountAssetPerformanceReportData(String description, PeriodPerformanceDto dto,
                                             boolean investmentReturnAvailable,
                                             boolean cmaMigratedAccount) {
        this(dto, investmentReturnAvailable, cmaMigratedAccount);
        this.description = description;
    }

    public AccountAssetPerformanceReportData(String description, boolean investmentReturnAvailable,
                                             boolean cmaMigratedAccount) {
        this.description = description;
        this.investmentReturnAvailable = investmentReturnAvailable;
        this.cmaMigratedAccount = cmaMigratedAccount;
    }

    protected void addChild(AccountAssetPerformanceReportData data) {
        this.children.add(data);
    }

    public List<AccountAssetPerformanceReportData> getChildren() {
        return children;
    }

    public String getDescription() {
        if (description == null) {
            StringBuilder builder = new StringBuilder();
            boolean isWrapTermDeposit = StringUtils.isNotBlank(dto.getAssetCode())
                                        && dto.getAssetCode().startsWith("WBC")
                                        && dto.getAssetCode().endsWith("TD");
            if (StringUtils.isNotBlank(dto.getAssetCode()) && !(dto instanceof TermDepositPerformanceDto)
                    && !isWrapTermDeposit) {
                builder.append("<b>");
                builder.append(dto.getAssetCode());
                builder.append(" &#183 ");
                builder.append("</b> ");
            }
            builder.append(dto.getName());
            if (dto.getName().contains(AssetType.CASH.getDisplayName()) && children.isEmpty() &&
                    (!investmentReturnAvailable || cmaMigratedAccount)) {
                builder.append(" *");
            }
            description = builder.toString();
        }
        return description;
    }

    public Boolean getHasCashFootnote() {
        if (children.isEmpty()) {
            return dto.getName().contains(AssetType.CASH.getDisplayName()) &&
                    (!investmentReturnAvailable || cmaMigratedAccount);
        }
        for (AccountAssetPerformanceReportData child : children) {
            if (child.getHasCashFootnote()) {
                return true;
            }
        }
        return false;
    }

    public String getSubDescription() {
        if (dto instanceof TermDepositPerformanceDto) {
            StringBuilder builder = new StringBuilder();

            builder.append("Matures on ");
            builder.append(ReportFormatter.format(ReportFormat.SHORT_DATE, ((TermDepositPerformanceDto) dto).getMaturityDate()));
            builder.append("<br/>");
            builder.append(((TermDepositPerformanceDto) dto).getTerm());
            builder.append(" term interest payment ");
            builder.append(((TermDepositPerformanceDto) dto).getPaymentFrequency());
            return builder.toString();
        }
        return null;
    }

    public String getOpeningBalance() {
        return ReportFormatter.format(ReportFormat.CURRENCY, isChild ? null : dto.getOpeningBalance());
    }

    public String getInflows() {
        String inFlows = ReportFormatter.format(ReportFormat.CURRENCY, isChild ? null : dto.getPurchase());
        return isMigratedCash() ? NOT_AVAILABLE : inFlows;
    }

    public String getOutflows() {
        String outFlows = ReportFormatter.format(ReportFormat.CURRENCY, isChild ? null : dto.getSales());
        return isMigratedCash() ? NOT_AVAILABLE : outFlows;
    }

    public String getMovement() {
        return isMigratedCash() ? NOT_AVAILABLE : ReportFormatter.format(ReportFormat.CURRENCY, dto.getMovement());
    }

    public String getClosingBalance() {
        return ReportFormatter.format(ReportFormat.CURRENCY, isChild ? null : dto.getClosingBalance());
    }

    public String getIncome() {
        return ReportFormatter.format(ReportFormat.CURRENCY, dto.getNetIncome());
    }

    public String getPerformanceDollar() {
        return isMigratedCash() ? NOT_AVAILABLE : ReportFormatter.format(ReportFormat.CURRENCY, dto.getPerformanceDollar());
    }

    public String getPerformancePercent() {
        return ReportFormatter.format(ReportFormat.PERCENTAGE, dto.getPerformancePercentage());
    }

    public String getPeriod() {
        return ReportFormatter.format(ReportFormat.INTEGER, isChild ? null : dto.getPeriodHeld());
    }

    public String getIcon() {
        return icon;
    }

    private boolean isMigratedCash() {
        String cashName = description.replace(" *", "");
        return (cashName.equals(AssetType.CASH.getDisplayName()) ||
                 cashName.equals("Cash Account")) && !investmentReturnAvailable;
    }
}