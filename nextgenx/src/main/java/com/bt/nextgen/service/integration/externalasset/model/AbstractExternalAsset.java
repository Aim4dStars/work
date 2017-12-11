package com.bt.nextgen.service.integration.externalasset.model;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.PositionIdentifier;
import com.bt.nextgen.service.integration.externalasset.builder.DateTimeConverter;
import com.bt.nextgen.service.integration.externalasset.builder.PositionIdentifierConverter;
import com.bt.nextgen.api.smsf.constants.AssetClass;
import com.bt.nextgen.api.smsf.constants.AssetType;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;


@ServiceBean(xpath = "pos_head", type = ServiceBeanType.ABSTRACT, lazyBeanClasses =
{
    OnPlatformExternalAssetImpl.class, OffPlatformExternalAssetImpl.class
})
public abstract class AbstractExternalAsset implements ExternalAsset
{
    private static final Logger logger = LoggerFactory.getLogger(AbstractExternalAsset.class);

    @ServiceElement(xpath = "ext_hld_name/val")
    private String assetName;

    @ServiceElement(xpath = "ext_hld_type/val", staticCodeCategory = "ASSET_CLUSTER")
    private AssetType assetType;

    @ServiceElement(xpath = "ext_hld_class/val", staticCodeCategory = "ASSET_CLASS_GRP")
    private AssetClass assetClass;

    @ServiceElement(xpath = "qty/val")
    private BigDecimal quantity;

    @ServiceElement(xpath = "curr_val_ref/val")
    private BigDecimal marketValue;

    @ServiceElement(xpath = "ext_hld_src/val")
    private String source;

    @ServiceElement(xpath = "curr_price_date/val", converter = DateTimeConverter.class)
    private DateTime valueDate;

    @ServiceElement(xpath = "ext_hld_mat/val", converter = DateTimeConverter.class)
    private DateTime maturityDate;

    @ServiceElement(xpath = "pos_name/annot/ctx/id", converter = PositionIdentifierConverter.class)
    private PositionIdentifier positionIdentifier;

    @ServiceElement(xpath = "ext_hld_name/val")
    private String positionName;

    @ServiceElement(xpath = "ext_hld_code/val")
    private String positionCode;


    @Override
    public PositionIdentifier getPositionIdentifier()
    {
        return positionIdentifier;
    }

    @Override
    public void setPositionIdentifier(PositionIdentifier positionIndentifier)
    {
        this.positionIdentifier = positionIndentifier;
    }

    @Override
    public String getAssetName() {
        return assetName;
    }

    @Override
    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    @Override
    public AssetType getAssetType() {
        return assetType;
    }

    @Override
    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    @Override
    public AssetClass getAssetClass() {
        return assetClass;
    }

    @Override
    public void setAssetClass(AssetClass assetClass)
    {
        this.assetClass = assetClass;
    }

    @Override
    public BigDecimal getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Override
    public BigDecimal getMarketValue() {
        return marketValue;
    }

    @Override
    public void setMarketValue(BigDecimal value) {
        this.marketValue = value;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public DateTime getValueDate() {
        return valueDate;
    }

    @Override
    public void setValueDate(DateTime valueDate) {
        this.valueDate = valueDate;
    }

    @Override
    public DateTime getMaturityDate() {
        return maturityDate;
    }

    @Override
    public void setMaturityDate(DateTime maturityDate) {
        this.maturityDate = maturityDate;
    }

    @Override
    public String getPositionName() {
        return positionName;
    }

    @Override
    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    @Override
    public String getPositionCode() {
        return positionCode;
    }

    @Override
    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    @Override
    public BigDecimal getPercentageTotal(BigDecimal totalPortfolioMarketValue)
    {
        BigDecimal percentageTotal = BigDecimal.ZERO;

        try
        {
            if (totalPortfolioMarketValue.compareTo(BigDecimal.ZERO) >= 0 && getMarketValue() != null)
            {
                percentageTotal = getMarketValue().divide(totalPortfolioMarketValue, 4, RoundingMode.HALF_UP);
            }
        }
        catch (ArithmeticException ae)
        {
            logger.warn("Unable to calculate percentage total for asset classification. Defaulting to zero.", ae);
        }

        return percentageTotal;
    }
}