package com.bt.nextgen.reports.account.transactions;

import com.bt.nextgen.api.transactionhistory.model.TransactionHistoryDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransactionData {
    private DateTime tradeDate;
    private DateTime settlementDate;
    private String investmentCode;
    private String investmentName;
    private BigDecimal quantity;
    private String description;
    private String transactionType;
    private BigDecimal netAmount;
    private String assetCode;
    private String assetName;
    private String assetType;
    private List<TransactionData> children = new ArrayList<>();

    public TransactionData(TransactionHistoryDto transactionHistoryDto) {
        this.tradeDate = transactionHistoryDto.getTradeDate();
        this.settlementDate = transactionHistoryDto.getSettlementDate();
        this.description = transactionHistoryDto.getDescription();
        this.transactionType = transactionHistoryDto.getTransactionType();
        this.netAmount = transactionHistoryDto.getNetAmount();
        this.investmentCode = transactionHistoryDto.getInvestmentCode();
        this.investmentName = transactionHistoryDto.getInvestmentName();
        this.assetCode = transactionHistoryDto.getAssetCode();
        this.assetName = transactionHistoryDto.getAssetName();
        this.assetType = transactionHistoryDto.getAssetType();
        this.quantity = transactionHistoryDto.getQuantity();
    }

    public TransactionData(List<TransactionHistoryDto> transactions) {
        for (TransactionHistoryDto transaction : transactions) {
            this.children.add(new TransactionData(transaction));
        }
    }

    public List<TransactionData> getChildren() {
        return children;
    }

    public String getTradeDate() {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, tradeDate);
    }

    public String getSettlementDate() {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, settlementDate);
    }

    public String getInvestmentType() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(investmentCode)) {
            builder.append("<b>");
            builder.append(investmentCode);
            builder.append(" &#183 ");
            builder.append("</b> ");
        }
        builder.append(investmentName);
        return builder.toString();
    }

    public String getSecurity() {
        if (assetCode == null && assetName == null) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder();
            if (StringUtils.isNotBlank(assetCode)) {
                builder.append("<b>");
                builder.append(assetCode);
                builder.append(" &#183 ");
                builder.append("</b> ");
            }
            builder.append(assetName);
            return builder.toString();
        }
    }

    public String getDescription() {
        return description;
    }

    public String getUnits() {
        return quantity != null ? assetType != null && ("MANAGED_FUND").equals(assetType) ? ReportFormatter.format(
                ReportFormat.MANAGED_FUND_UNIT, quantity) : ReportFormatter.format(ReportFormat.UNITS, quantity) : "";
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getNetAmount() {
        return netAmount != null ? ReportFormatter.format(ReportFormat.CURRENCY, netAmount) : "";
    }

}
