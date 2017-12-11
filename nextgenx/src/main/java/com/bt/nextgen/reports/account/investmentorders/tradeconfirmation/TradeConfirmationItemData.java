package com.bt.nextgen.reports.account.investmentorders.tradeconfirmation;

import com.bt.nextgen.api.order.model.OrderTransactionDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class TradeConfirmationItemData {
    private final DateTime tradeDate;
    private final BigDecimal units;
    private final BigDecimal price;
    private final BigDecimal transactionFee;
    private final BigDecimal consideration;
    private final DateTime settlementDate;
    private final String assetType;

    public TradeConfirmationItemData(OrderTransactionDto transaction, String assetType) {
        this.tradeDate = transaction.getTradeDate();
        this.units = transaction.getUnits();
        this.price = transaction.getPrice();
        this.transactionFee = transaction.getTransactionFee();
        this.consideration = transaction.getConsideration();
        this.settlementDate = transaction.getSettlementDate();
        this.assetType = assetType;
    }

    public String getTradeDate() {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, tradeDate);
    }

    public String getUnits() {
        if (AssetType.MANAGED_FUND.getDisplayName().equals(assetType)) {
            return ReportFormatter.format(ReportFormat.MANAGED_FUND_UNIT, units);
        } else {
            return ReportFormatter.format(ReportFormat.INTEGER, units);
        }
    }

    public String getPrice() {
        if (AssetType.MANAGED_FUND.getDisplayName().equals(assetType)) {
            return ReportFormatter.format(ReportFormat.MANAGED_FUND_PRICE, price);
        } else {
            return ReportFormatter.format(ReportFormat.LS_PRICE, price);
        }
    }

    public String getTransactionFee() {
        return ReportFormatter.format(ReportFormat.CURRENCY, transactionFee);
    }

    public String getConsideration() {
        return ReportFormatter.format(ReportFormat.CURRENCY, consideration);
    }

    public String getSettlementDate() {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, settlementDate);
    }
}
