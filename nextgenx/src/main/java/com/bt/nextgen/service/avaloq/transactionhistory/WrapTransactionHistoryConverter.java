package com.bt.nextgen.service.avaloq.transactionhistory;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.transactionhistory.BTOrderType;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.bt.nextgen.service.wrap.integration.util.WrapAssetUtil.isWrapCash;
import static com.bt.nextgen.service.wrap.integration.util.WrapAssetUtil.isWrapTermDeposit;

@Component
public class WrapTransactionHistoryConverter {

    private static final String WRAP_SEC_CLASS_UNIT_TRUEST = "Unit Trust";
    private static final String WRAP_SEC_CLASS_EQUITY = "Equity";

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    /**
     * Convert Wrap transactions
     *
     * @param wrapTransactionHistories
     * @param serviceErrors
     *
     * @return
     */
    public List<TransactionHistory> convertWrapTransactionsToPanorama(List<com.btfin.panorama.wrap.model.TransactionHistory> wrapTransactionHistories, ServiceErrors serviceErrors) {
        List<TransactionHistory> transactionHistories = new ArrayList<>();
        for (com.btfin.panorama.wrap.model.TransactionHistory wrapTransactionHistory : wrapTransactionHistories) {
            WrapTransactionHistoryImpl wrapTransactionHistoryImpl = new WrapTransactionHistoryImpl();

            setAssetDetails(wrapTransactionHistory, wrapTransactionHistoryImpl, serviceErrors);
            setAmountAndQuantityDetails(wrapTransactionHistory, wrapTransactionHistoryImpl);

            wrapTransactionHistoryImpl.setBookingText(wrapTransactionHistory.getBookingText());
            wrapTransactionHistoryImpl.setTransactionType(BTOrderType.getBTOrderTypeFromInternalId(wrapTransactionHistory.getTransactionType()).getDisplayName());
            wrapTransactionHistoryImpl.setDocId(wrapTransactionHistory.getMovementId());
            wrapTransactionHistoryImpl.setEffectiveDate(formatDateTime(wrapTransactionHistory.getTradeDate()));
            wrapTransactionHistoryImpl.setValDate(formatDateTime(wrapTransactionHistory.getSettlementDate()));
            transactionHistories.add(wrapTransactionHistoryImpl);
        }
        return transactionHistories;
    }

    private DateTime formatDateTime(String inputDate) {
        DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC();
        String updatedDate = inputDate.substring(0, inputDate.lastIndexOf("T"));
        return df.parseDateTime(updatedDate);
    }

    /**
     * Set asset details for the transaction
     *
     * @param wrapTransactionHistory
     * @param wrapTransactionHistoryImpl
     * @param serviceErrors
     */
    private void setAssetDetails(com.btfin.panorama.wrap.model.TransactionHistory wrapTransactionHistory,
                                 WrapTransactionHistoryImpl wrapTransactionHistoryImpl, ServiceErrors serviceErrors) {
        String securityCode = wrapTransactionHistory.getSecurityCode().trim();
        Asset asset = getAssetWithAssetCode(securityCode, serviceErrors);
        // If asset found in Avaloq
        if (asset != null) {
            wrapTransactionHistoryImpl.setAsset(asset);
            wrapTransactionHistoryImpl.setAssetCode(asset.getAssetCode());
            wrapTransactionHistoryImpl.setAssetName(asset.getAssetName());
            wrapTransactionHistoryImpl.setAssetType(asset.getAssetType());
        }
        // Cash asset code not available in Wrap so explicitly set the asset type
        else if (isWrapCash(securityCode)) {
            wrapTransactionHistoryImpl.setAsset(
                    getAsset(AssetType.CASH, securityCode, wrapTransactionHistory.getSecurityName()));
            wrapTransactionHistoryImpl.setAssetType(AssetType.CASH);
        }
        else {
            // If asset not available in Panorama
            if (isWrapTermDeposit(securityCode)) {
                // in case of term deposit
                wrapTransactionHistoryImpl.setAssetType(AssetType.TERM_DEPOSIT);
            }
            else if (WRAP_SEC_CLASS_UNIT_TRUEST.equals(wrapTransactionHistory.getSecurityClass().trim())) {
                wrapTransactionHistoryImpl.setAssetType(AssetType.MANAGED_FUND);
            }
            else if (WRAP_SEC_CLASS_EQUITY.equals(wrapTransactionHistory.getSecurityClass().trim())) {
                wrapTransactionHistoryImpl.setAssetType(AssetType.SHARE);
            }
            wrapTransactionHistoryImpl.setAssetCode(securityCode);
            wrapTransactionHistoryImpl.setAssetName(wrapTransactionHistory.getSecurityName());
            wrapTransactionHistoryImpl.setAsset(
                    getAsset(wrapTransactionHistoryImpl.getAssetType(), securityCode, wrapTransactionHistory.getSecurityName()));
        }
    }

    /**
     * Set amount and quantity details
     *
     * @param wrapTransactionHistory
     * @param wrapTransactionHistoryImpl
     */
    private void setAmountAndQuantityDetails(com.btfin.panorama.wrap.model.TransactionHistory wrapTransactionHistory, WrapTransactionHistoryImpl wrapTransactionHistoryImpl) {
        String securityCode = wrapTransactionHistory.getSecurityCode().trim();
        if (("Corporate Action".equals(wrapTransactionHistory.getServiceType()) &&
                "Call".equals(wrapTransactionHistory.getServiceSubType())) ||
                isWrapCash(securityCode)) {
            wrapTransactionHistoryImpl.setAmount(wrapTransactionHistory.getAmount());
        }
        else {
            wrapTransactionHistoryImpl.setQuantity(wrapTransactionHistory.getQuantity());
        }
    }

    private Asset getAssetWithAssetCode(String assetCode, ServiceErrors serviceErrors) {
        Asset asset = null;
        List<Asset> assets = assetIntegrationService.loadAssetsForAssetCodes(Collections.singletonList(assetCode), serviceErrors);
        if (CollectionUtils.isNotEmpty(assets)) {
            asset = assets.get(0);
        }
        return asset;
    }

    private AssetImpl getAsset(final AssetType assetType, final String securityCode, final String securityName) {
        AssetImpl asset = new AssetImpl();
        asset.setAssetId(securityCode.toLowerCase());
        asset.setAssetName(securityName);
        asset.setAssetCode(securityCode);
        asset.setAssetType(assetType);
        return asset;
    }
}
