package com.bt.nextgen.api.portfolio.v3.model.cashmovements;

import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.integration.asset.Asset;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import com.bt.nextgen.service.integration.income.CashIncome;
import com.bt.nextgen.service.integration.income.DistributionIncome;
import com.bt.nextgen.service.integration.income.DividendIncome;
import com.bt.nextgen.service.integration.income.TermDepositIncome;
import com.bt.nextgen.service.integration.portfolio.cashmovements.CashMovement;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class OutstandingCash {
    private final Asset asset;
    private final BigDecimal marketPrice;
    private final BigDecimal quantity;
    private final BigDecimal amount;
    private final DateTime transactionDate;
    private final DateTime settlementDate;
    private final TermDepositPresentation termDepositDetails;

    public OutstandingCash(Asset asset, CashIncome income) {
        this.asset = asset;
        this.marketPrice = BigDecimal.ONE;
        this.quantity = income.getAmount();
        this.amount = income.getAmount();
        this.transactionDate = null;
        this.settlementDate = income.getPaymentDate();
        this.termDepositDetails = null;
    }

    public OutstandingCash(Asset asset, TermDepositPresentation td, CashMovement movement) {
        this.asset = asset;
        this.marketPrice = movement.getPrice();
        this.quantity = movement.getQuantity();
        this.amount = movement.getMarketValue();
        this.transactionDate = movement.getTransactionDate();
        this.settlementDate = movement.getSettlementDate();
        this.termDepositDetails = td;
    }


    public OutstandingCash(Asset asset, TermDepositPresentation td, TermDepositIncome income) {
        this.asset = asset;
        this.marketPrice = BigDecimal.ONE;
        this.quantity = income.getInterest();
        this.amount = income.getInterest();
        this.transactionDate = null;
        this.settlementDate = income.getPaymentDate();
        this.termDepositDetails = td;
    }

    public OutstandingCash(Asset asset, DividendIncome income) {
        this.asset = asset;
        this.marketPrice = BigDecimal.ONE;
        this.quantity = income.getQuantity();
        this.amount = income.getAmount();
        this.transactionDate = income.getExecutionDate();
        this.settlementDate = income.getPaymentDate();
        this.termDepositDetails = null;
    }

    public OutstandingCash(Asset asset, DistributionIncome income) {
        this.asset = asset;
        this.marketPrice = BigDecimal.ONE;
        this.quantity = income.getQuantity();
        this.amount = income.getAmount();
        this.transactionDate = income.getExecutionDate();
        this.settlementDate = income.getPaymentDate();
        this.termDepositDetails = null;
    }

    public DateTime getTransactionDate() {
        return transactionDate;
    }

    public AssetType getAssetType() {
        return asset.getAssetType();
    }

    public String getAssetName() {
        return asset.getAssetName();
    }

    public String getAssetCode() {
        return asset.getAssetCode();
    }

    public TermDepositPresentation getTermDepositDetails() {
        return termDepositDetails;
    }

    public BigDecimal getMarketPrice() {
        return marketPrice;
    }

    public BigDecimal getQuantity() {
        return quantity != null ? quantity.abs() : null;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public DateTime getSettlementDate() {
        return settlementDate;
    }

    public DateTime getMaturityDate() {
        if (AssetType.TERM_DEPOSIT == asset.getAssetType() && asset instanceof TermDepositAsset) {
            return ((TermDepositAsset) asset).getMaturityDate();
        }
        return null;
    }
}
