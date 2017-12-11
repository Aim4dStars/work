package com.bt.nextgen.service.integration.externalasset.builder;

import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.btfin.abs.trxservice.extlhold.v1_0.Asset;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;

@SuppressWarnings({"squid:S1068", "findbugs:URF_UNREAD_FIELD",
                   "squid:MethodCyclomaticComplexity",
                   "findbugs:MS_PKGPROTECT",
                   "checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck",
                   "squid:S00112"})
public class ExternalAssetRequestBuilder
{
    private String assetType;
    private String assetClass;
    private String assetId;
    private String posId;
    private String posName;
    private String posCode;
    private BigDecimal posQty;
    private BigDecimal posValue;
    private String source;
    private Date valueDate;
    private Date maturityDate;
    private String propertyType;

    private static final Logger logger = LoggerFactory.getLogger(com.bt.nextgen.service.integration.externalasset.builder.ExternalAssetRequestBuilder.class);


    public Asset build() {
        ServiceErrors serviceErrors = validate();

        if (serviceErrors.hasErrors()) {
            for (ServiceError serviceError : serviceErrors.getErrorList()) {
                logger.warn(serviceError.getMessage());
            }

            throw new IllegalStateException("One or more mandatory values have not been provided");
        }

        Asset asset = new Asset();

        if (!StringUtils.isEmpty(assetId))
            asset.setAssetId(AvaloqGatewayUtil.createIdVal(assetId));

        if (!StringUtils.isEmpty(assetType))
            asset.setAssetType(AvaloqGatewayUtil.createExtlIdVal(assetType));

        if (!StringUtils.isEmpty(assetClass))
            asset.setAssetClass(AvaloqGatewayUtil.createExtlIdVal(assetClass));

        if (!StringUtils.isEmpty(posId))
            asset.setPosId(AvaloqGatewayUtil.createIdVal(posId));

        if (!StringUtils.isEmpty(posName))
            asset.setPosName(AvaloqGatewayUtil.createTextVal(posName));

        if (posQty != null)
            asset.setPosQty(AvaloqGatewayUtil.createNumberVal(posQty));

        if (posValue != null)
            asset.setPosValue(AvaloqGatewayUtil.createNumberVal(posValue));
//        else
//            asset.setPosValue(AvaloqGatewayUtil.createNumberVal(BigDecimal.ZERO));

        if (!StringUtils.isEmpty(posCode))
            asset.setPosCode(AvaloqGatewayUtil.createTextVal(posCode));

        if (!StringUtils.isEmpty(source))
            asset.setPosSource(AvaloqGatewayUtil.createTextVal(source));

        if (valueDate != null)
        {
            asset.setValDate(AvaloqGatewayUtil.createDateVal(valueDate));
        }

        if (maturityDate != null)
            asset.setMaturityDate(AvaloqGatewayUtil.createDateVal(maturityDate));

        if (!StringUtils.isEmpty(propertyType))
            asset.setPropertyType(AvaloqGatewayUtil.createExtlIdVal(propertyType));

        return asset;
    }


    private ServiceErrors validate()
    {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        // If we not updating an existing external asset
//        if (StringUtils.isEmpty(posId))
//        {
//            // Asset Id is required for Australian Shares and Managed Funds
//            if (StringUtils.isEmpty(assetId) && (assetType.equals(AssetType.AUSTRALIAN_LISTED_SECURITIES.toString())
//                                              || assetType.equals(AssetType.MANAGED_FUND.toString())))
//            {
//                serviceErrors.addError(createServiceError("Asset Id"));
//            }
//        }
//        else
//        {
//            if (StringUtils.isEmpty(posName))
//            {
//                serviceErrors.addError(createServiceError("Position Name"));
//            }
/*
            else if (posQty == null)
            {
                serviceErrors.addError(createServiceError("Position Quantity"));
            }
*/

//        }

        return serviceErrors;
    }

/*    private ServiceError createServiceError(String fieldName)
    {
        return new ServiceErrorImpl("GUI", "MIA", fieldName + " is mandatory", null);
    }*/


    public ExternalAssetRequestBuilder withAssetType(String assetType)
    {
        this.assetType = assetType;
        return this;
    }

    public ExternalAssetRequestBuilder withAssetClass(String assetClass)
    {
        this.assetClass = assetClass;
        return this;
    }

    public ExternalAssetRequestBuilder withPosId(String posId)
    {
        this.posId = posId;
        return this;
    }

    public ExternalAssetRequestBuilder withPosName(String posName)
    {
        this.posName = posName;
        return this;
    }

    public ExternalAssetRequestBuilder withPosCode(String posCode)
    {
        this.posCode = posCode;
        return this;
    }

    public ExternalAssetRequestBuilder withPosQty(BigDecimal posQty)
    {
        this.posQty = posQty;
        return this;
    }

    public ExternalAssetRequestBuilder withPosValue(BigDecimal posValue)
    {
        this.posValue = posValue;
        return this;
    }

    public ExternalAssetRequestBuilder withSource(String source)
    {
        this.source = source;
        return this;
    }

    public ExternalAssetRequestBuilder withValueDate(Date valueDate)
    {
        this.valueDate = (valueDate != null ? new Date(valueDate.getTime()) : null);
        return this;
    }

    public ExternalAssetRequestBuilder withMaturityDate(Date maturityDate)
    {
        this.maturityDate = (maturityDate != null ? new Date(maturityDate.getTime()) : null);
        return this;
    }

    public ExternalAssetRequestBuilder withPropertyType(String propertyType)
    {
        this.propertyType = propertyType;
        return this;
    }

    public ExternalAssetRequestBuilder withAssetKey(String assetId)
    {
        this.assetId = assetId;
        return this;
    }

}
