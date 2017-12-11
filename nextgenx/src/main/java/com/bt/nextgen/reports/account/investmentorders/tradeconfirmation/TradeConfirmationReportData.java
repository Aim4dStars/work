package com.bt.nextgen.reports.account.investmentorders.tradeconfirmation;

import com.bt.nextgen.api.order.model.OrderTransactionDto;
import com.bt.nextgen.api.order.model.TradeOrderDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class TradeConfirmationReportData {
    private final TradeOrderDto tradeOrderDto;
    private final List<TradeConfirmationItemData> tradeConfirmationItems = new ArrayList<>();

    public TradeConfirmationReportData(TradeOrderDto tradeOrderDto) {
        this.tradeOrderDto = tradeOrderDto;
        for (OrderTransactionDto transaction : tradeOrderDto.getOrderTransactions()) {
            tradeConfirmationItems.add(new TradeConfirmationItemData(transaction, getAssetCode()));
        }
    }

    public List<TradeConfirmationItemData> getChildren() {
        return tradeConfirmationItems;
    }

    public String getAssetCode() {
        return tradeOrderDto.getAsset().getAssetType();
    }

    public String getAssetName() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(tradeOrderDto.getAsset().getAssetCode())) {
            builder.append(tradeOrderDto.getAsset().getAssetCode());
            builder.append(" &#183 ");
        }
        return builder.append(tradeOrderDto.getAsset().getAssetName()).toString();
    }

    public String getOrderType() {
        return tradeOrderDto.getOrderType();
    }

    public String getOrderItemNumber() {
        return tradeOrderDto.getKey().getOrderId();
    }

    public String getPriceOption() {
        return "Market".equals(tradeOrderDto.getPriceType()) ? "Market"
                : "Limit - " + ReportFormatter.format(ReportFormat.LS_PRICE, tradeOrderDto.getLimitPrice());
    }

    public String getOriginalQuantity() {
            return ReportFormatter.format(ReportFormat.INTEGER, tradeOrderDto.getOriginalQuantity());
    }

    public String getTotalFilledUnits() {
        return ReportFormatter.format(ReportFormat.INTEGER, tradeOrderDto.getTotalFilledUnits());
    }

    public String getOutstandingUnits() {
        return ReportFormatter.format(ReportFormat.INTEGER, tradeOrderDto.getOutstandingUnits());
    }

    public String getSumTotalConsideration() {
        return ReportFormatter.format(ReportFormat.CURRENCY, tradeOrderDto.getSumTotalConsideration());
    }
}
