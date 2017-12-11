package com.bt.nextgen.api.asset.model;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.asset.Asset;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = AssetDto.class, name = "Asset"),
        @JsonSubTypes.Type(value = ShareAssetDto.class, name = "ShareAsset"),
        @JsonSubTypes.Type(value = ManagedFundAssetDto.class, name = "ManagedFundAsset"),
        @JsonSubTypes.Type(value = ManagedPortfolioAssetDto.class, name = "ManagedPortfolioAsset"),
        @JsonSubTypes.Type(value = TermDepositAssetDto.class, name = "TermDepositAsset"),
        @JsonSubTypes.Type(value = TermDepositAssetDtoV2.class, name = "TermDepositAssetDtoV2")})
// TODO remove asset type, work out how to make Jackson deserialise to the correct subtype
public class AssetDto extends BaseDto implements KeyedDto<String> {
    @JsonView(JsonViews.Write.class)
    private String assetId;

    @JsonView(JsonViews.Write.class)
    private String assetType;

    private String assetName;
    private String assetCluster;
    private String status;
    private String isin;
    private String assetCode;
    private String ipsId;
    private String assetClass;
    private String groupClass;
    private List<DistributionMethod> distributionMethods;
    private String riskMeasure;
    private String issuerId;
    private String issuerName;
    private boolean prePensionRestricted;

    public AssetDto() {
    }

    public AssetDto(Asset asset, String assetName, String assetType) {
        super();
        this.assetId = asset.getAssetId();
        this.assetName = assetName;
        this.assetType = assetType;
        this.assetCluster = asset.getAssetCluster() != null ? asset.getAssetCluster().getIntlId() : null;
        this.isin = asset.getIsin();
        this.status = asset.getStatus() != null ? asset.getStatus().getDisplayName() : null;
        this.assetCode = asset.getAssetCode();
        this.ipsId = asset.getIpsId();
        this.assetClass = asset.getAssetClass() == null ? null : asset.getAssetClass().getDescription();
        this.riskMeasure = asset.getRiskMeasure();
        this.issuerId = asset.getIssuerId();
        this.issuerName = asset.getIssuerName();
        this.groupClass = asset.getModelAssetClass() == null ? null : asset.getModelAssetClass().getDescription();
        this.prePensionRestricted = asset.isPrePensionRestricted();
    }

    public AssetDto(Asset asset, String assetType, List<DistributionMethod> distributionMethods) {
        this(asset, asset.getAssetName(), assetType);
        this.distributionMethods = distributionMethods;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public String getAssetType() {
        return assetType;
    }

    public String getAssetCluster() {
        return assetCluster;
    }

    public String getStatus() {
        return status;
    }

    public String getIsin() {
        return isin;
    }

    public String getIpsId() {
        return ipsId;
    }

    public String getIssuerId() {
        return issuerId;
    }

    public String getIssuerName() {
        return issuerName;
    }

    @Override
    public String getKey() {
        return getAssetId();
    }

    /**
     * @return the distributionMethods
     */
    public List<DistributionMethod> getDistributionMethods() {
        return distributionMethods;
    }

    /**
     * @param distributionMethods
     *            the distributionMethods to set
     */
    public void setDistributionMethods(List<DistributionMethod> distributionMethods) {
        this.distributionMethods = distributionMethods;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    /**
     * @return the assetClass
     */
    public String getAssetClass() {
        return assetClass;
    }

    /**
     * @param assetClass
     *            the assetClass to set
     */
    public void setAssetClass(String assetClass) {
        this.assetClass = assetClass;
    }

    public String getRiskMeasure() {
        return riskMeasure;
    }

    public void setRiskMeasure(String riskMeasure) {
        this.riskMeasure = riskMeasure;
    }

    public String getGroupClass() {
        return groupClass;
    }

    public void setGroupClass(String groupClass) {
        this.groupClass = groupClass;
    }

    public boolean isPrePensionRestricted() {
        return prePensionRestricted;
    }
}
