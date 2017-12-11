package com.bt.nextgen.service.integration.externalasset.builder;


import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.btfin.panorama.core.security.avaloq.Constants;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ContainerKey;
import com.bt.nextgen.service.integration.externalasset.model.ExternalAsset;
import com.bt.nextgen.service.integration.externalasset.model.OffPlatformExternalAsset;
import com.bt.nextgen.service.integration.externalasset.model.OnPlatformExternalAsset;
import com.btfin.abs.common.v1_0.Hdr;
import com.btfin.abs.trxservice.base.v1_0.Action;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqExec;
import com.btfin.abs.trxservice.extlhold.v1_0.Asset;
import com.btfin.abs.trxservice.extlhold.v1_0.AssetList;
import com.btfin.abs.trxservice.extlhold.v1_0.Data;
import com.btfin.abs.trxservice.extlhold.v1_0.ExtlHoldReq;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;

import java.util.List;

/**
 * Converts external asset list into avaloq request payload.<br>
 * Converts avaloq <code>EXTL_HOLD_REQ</code> transaction repsonse back into UI bean
 */
@SuppressWarnings({"squid:S1172", "squid:S1068", "squid:S1067", "findbugs:URF_UNREAD_FIELD", "squid:MethodCyclomaticComplexity",
                    "checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck"})
public final class ExternalAssetConverter
{
	

    //private static final Logger logger = LoggerFactory.getLogger(com.bt.nextgen.service.integration.externalasset.builder.ExternalAssetConverter.class);

    private ExternalAssetConverter() {

    }

    /**
     *
     * @param externalAssets
     * @param accountKey
     * @param containerKey
     * @param bankDate Current avaloq bank date. This acts as override for non-prod environments where calendar/bank dates are not always in sync
     * @return
     */
    public static ExtlHoldReq toExternalAssetRequest(List<ExternalAsset> externalAssets, AccountKey accountKey, ContainerKey containerKey, DateTime bankDate)
    {
        ExtlHoldReq externalHoldingRequest = AvaloqObjectFactory.getExternalHoldingObjectFactory().createExtlHoldReq();

        Hdr header = AvaloqGatewayUtil.createHdr();
        externalHoldingRequest.setHdr(header);

        Data data = AvaloqObjectFactory.getExternalHoldingObjectFactory().createData();
        data.setBp(AvaloqGatewayUtil.createExtlId(accountKey.getId(), "bp_nr"));
        data.setCont(AvaloqGatewayUtil.createIdVal(containerKey.getId()));

        AssetList assetList = AvaloqObjectFactory.getExternalHoldingObjectFactory().createAssetList();

        for (ExternalAsset extAsset : externalAssets)
        {
            assetList.getAsset().add(toExternalAsset(extAsset, bankDate));
        }

        data.setAssetList(assetList);
        externalHoldingRequest.setData(data);

        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        ReqExec reqExec = new ReqExec();
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(Constants.DO);
        reqExec.setAction(action);
        req.setExec(reqExec);

        externalHoldingRequest.setReq(req);
        return externalHoldingRequest;
    }


    /**
     * Convert an external asset bean into corresponding jaxb bean.
     * <p>To be used to populate external asset list in the <code>EXTL_HOLD_REQ</code> request payload</p>
     * @param externalAsset asset to build a request bean for
     * @return jaxb external asset
     */
    private static Asset toExternalAsset(ExternalAsset externalAsset, DateTime bankDate)
    {
        String positionId = "";
        String positionName = "";
        String positionCode = "";
        String propertyType = "";
        String assetId = "";

        positionName = externalAsset.getPositionName();
        positionCode = externalAsset.getPositionCode(); //PositionCode should be null for IN Panorama asset

        if (externalAsset instanceof OffPlatformExternalAsset)
        {
            OffPlatformExternalAsset offPlatformAsset = (OffPlatformExternalAsset) externalAsset;

            if (offPlatformAsset.getPropertyType() != null) {
                propertyType = offPlatformAsset.getPropertyType().getCode();
            }
        }

        if (externalAsset instanceof OnPlatformExternalAsset)
        {
            OnPlatformExternalAsset onPlatformPanoramaExternalAsset = (OnPlatformExternalAsset) externalAsset;
            assetId = onPlatformPanoramaExternalAsset.getKey().getId();
        }

        if (externalAsset.getPositionIdentifier() != null) {
            positionId = externalAsset.getPositionIdentifier().getPositionId();
        }

        // Check whether the supplied value date is after current avaloq bank date
        // (This can happen in non-prod environments where the avaloq date < calendar date).
        // Use the bank date if the avaloq date is ahead of supplied value date.
        // This will ensure that meaningful data is available when fetching external assets
        // valuation date is mandatory to process transaction in avaloq so defaulting to bankdate if null
        if ( bankDate != null) {
            if (externalAsset.getValueDate() == null) {
                externalAsset.setValueDate(bankDate);
            } else {
                DateTimeComparator dtComparator = DateTimeComparator.getDateOnlyInstance();
                if (dtComparator.compare(externalAsset.getValueDate(), bankDate) > 0) {
                    externalAsset.setValueDate(bankDate);
                }
            }
        }

        Asset asset = new ExternalAssetRequestBuilder()
                .withAssetType(externalAsset.getAssetType() !=null ? externalAsset.getAssetType().getCode() : null)
                .withAssetClass( externalAsset.getAssetClass()!=null ? externalAsset.getAssetClass().getCode() : null)
                .withAssetKey(assetId)
                .withPosId(positionId)
                .withPosName(positionName)
                .withPosCode(positionCode)
                .withPosQty(externalAsset.getQuantity())
                .withPosValue(externalAsset.getMarketValue())
                .withSource(externalAsset.getSource())
                .withValueDate(externalAsset.getValueDate()!=null ? externalAsset.getValueDate().toDate() : null)
                .withMaturityDate(externalAsset.getMaturityDate() != null ? externalAsset.getMaturityDate().toDate() : null)
                .withPropertyType(StringUtils.isNotBlank(propertyType)? propertyType:null)
                .build();

        return asset;
    }
}
