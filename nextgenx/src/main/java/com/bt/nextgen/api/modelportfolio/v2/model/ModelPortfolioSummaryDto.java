package com.bt.nextgen.api.modelportfolio.v2.model;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummary;
import com.btfin.panorama.service.integration.asset.AssetType;

public class ModelPortfolioSummaryDto extends ModelDto {

    private final String accountType;
    private final String apirCode;
    private final DateTime lastUpdateDate;
    private final DateTime openDate;
    private final String lastUpdatedBy;
    private final String assetClass;
    private final String investmentStyle;
    private final String modelConstruction;
    private final String modelDescription;
    private final String status;
    private final BigDecimal fum;
    private final Boolean hasVoluntaryCorporateActions;
    private final Boolean hasMandatoryCorporateActions;
    private final OrderTrackingKey orderTrackingKey;
    private final AssetType assetType;
    private final Integer totalAccountsCount;

    public ModelPortfolioSummaryDto(ModelPortfolioSummary summary, AssetType assetType) {
        super(summary);
        this.accountType = summary.getAccountType() == null ? "" : summary.getAccountType().getDisplayValue();
        this.apirCode = summary.getApirCode();
        this.lastUpdateDate = summary.getLastUpdateDate();
        this.lastUpdatedBy = summary.getLastUpdatedBy();
        this.assetClass = summary.getAssetClass();
        this.investmentStyle = summary.getInvestmentStyle();
        this.modelConstruction = summary.getModelConstruction() == null ? null : summary.getModelConstruction().getDisplayValue();
        this.status = StringUtils.capitalize(summary.getStatus().getName().toLowerCase());
        this.fum = summary.getFum();
        this.hasVoluntaryCorporateActions = summary.getHasVoluntaryCorporateActions();
        this.hasMandatoryCorporateActions = summary.getHasMandatoryCorporateActions();
        this.orderTrackingKey = new OrderTrackingKey(summary.getIpsOrderId(), summary.getModelOrderId());
        this.assetType = assetType;
        this.openDate = summary.getOpenDate();
        this.modelDescription = summary.getModelDescription();
        this.totalAccountsCount = summary.getNumAccounts();
    }

    public String getAccountType() {
        return accountType;
    }

    public String getApirCode() {
        return apirCode;
    }

    public DateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public String getAssetClass() {
        return assetClass;
    }

    public String getInvestmentStyle() {
        return investmentStyle;
    }

    public String getModelConstruction() {
        return modelConstruction;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getFum() {
        return fum;
    }

    public Boolean getHasVoluntaryCorporateActions() {
        return hasVoluntaryCorporateActions;
    }

    public Boolean getHasMandatoryCorporateActions() {
        return hasMandatoryCorporateActions;
    }

    public OrderTrackingKey getOrderTrackingKey() {
        return orderTrackingKey;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public DateTime getOpenDate() {
        return openDate;
    }

    public String getModelDescription() {
        return modelDescription;
    }

    public Integer getTotalAccountsCount() {
        return totalAccountsCount;
    }
}
