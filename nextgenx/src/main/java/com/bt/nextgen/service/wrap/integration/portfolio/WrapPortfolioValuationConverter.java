package com.bt.nextgen.service.wrap.integration.portfolio;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.AbstractBaseAccountHolding;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedFundAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedFundHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ShareAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ShareHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositAccountValuationImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.base.SystemType;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.HinType;
import com.bt.nextgen.service.integration.portfolio.valuation.HoldingKey;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.wrap.model.PortfolioPosition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateMidnight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.btfin.panorama.core.security.avaloq.Constants.HYPHEN;

public class WrapPortfolioValuationConverter {

    private static final String IS_EXTERNAL_ASSET_YES = "Y";
    private static final String WRAP_ASSET_SECTOR_CASH = "Cash Account";
    private static final String WRAP_SEC_CLASS_UNIT_TRUST = "Unit Trust";
    private static final String WRAP_SEC_CLASS_EQUITY = "Equity";
    private static final BigDecimal ONE_HUNDRED_PERCENT = new BigDecimal("100.00");
    private static final int DECIMAL_PLACES = 2;
    private static Logger logger = LoggerFactory.getLogger(WrapPortfolioValuationConverter.class);

    private WrapPortfolioValuationConverter() {
    }

    public static WrapAccountValuation convert(List<PortfolioPosition> portfolioPositions,
                                               AssetIntegrationService assetIntegrationService, ServiceErrors serviceErrors) {
        // query ABS to classify the securities
        Map<AssetType, List<PortfolioPosition>> wrapPortfolioPositionsByAssetTypes = queryABSAndClassifySecurities(
                portfolioPositions, assetIntegrationService, serviceErrors);
        List<SubAccountValuation> subAccountValuations = new ArrayList<>();
        for (AssetType assetType : wrapPortfolioPositionsByAssetTypes.keySet()) {
            SubAccountValuation subAccountValuation = addAssetType(wrapPortfolioPositionsByAssetTypes.get(assetType), assetType);
            if (subAccountValuation != null) {
                subAccountValuations.add(subAccountValuation);
            }
        }
        ThirdPartyValuation wrapAccountValuation = new ThirdPartyValuation();
        wrapAccountValuation.setThirdPartySource(SystemType.WRAP.name());
        wrapAccountValuation.setSubAccountValuations(subAccountValuations);
        return wrapAccountValuation;
    }

    private static Map<AssetType, List<PortfolioPosition>> queryABSAndClassifySecurities(final List<PortfolioPosition> wrapPortfolioPositions,
                                                                                         AssetIntegrationService assetIntegrationService,
                                                                                         ServiceErrors serviceErrors) {
        Map<AssetType, List<PortfolioPosition>> wrapPortfolioPositionsByAssetTypes = new HashMap<AssetType, List<PortfolioPosition>>();
        for (PortfolioPosition wrapPortfolioPosition : wrapPortfolioPositions) {
            AssetType assetType = AssetType.OTHER;
            String wrapSecurityCode = getSecurityCode(wrapPortfolioPosition);
            if (WRAP_ASSET_SECTOR_CASH.equalsIgnoreCase(wrapSecurityCode.trim())) {
                // don't look up cash in ABS, it has various Wrap names ie "Cash account" that can't lookup in ABS.
                // Take Wrap's word for it from the assetSector.
                assetType = AssetType.CASH;
            }
            else {
                Asset asset = getABSAssetWithAssetCode(wrapSecurityCode, assetIntegrationService, serviceErrors);
                if (asset == null) {
                    assetType = getAssetTypeFromWrap(wrapSecurityCode, wrapPortfolioPosition.getSecurityClass());
                }
                else {
                    assetType = asset.getAssetType();
                    wrapPortfolioPosition.setSecurityName(asset.getAssetName());
                }
            }
            // add to map
            List<PortfolioPosition> sameAssetTypeWrapPortfolioPositions = wrapPortfolioPositionsByAssetTypes.get(assetType);
            if (sameAssetTypeWrapPortfolioPositions == null) {
                sameAssetTypeWrapPortfolioPositions = new ArrayList<>();
                wrapPortfolioPositionsByAssetTypes.put(assetType, sameAssetTypeWrapPortfolioPositions);
            }
            sameAssetTypeWrapPortfolioPositions.add(wrapPortfolioPosition);
        }
        return wrapPortfolioPositionsByAssetTypes;
    }

    private static SubAccountValuation addAssetType(List<PortfolioPosition> wrapSectorPortfolioPositions, AssetType assetType) {
        SubAccountValuation subAccountValuation = null;
        switch (assetType) {
            case CASH:
                subAccountValuation = createCashManagementValuation(wrapSectorPortfolioPositions, assetType);
                break;
            case SHARE:
            case INTERNATIONAL_SHARE:
                subAccountValuation = createShareValuation(wrapSectorPortfolioPositions, assetType);
                break;
            case MANAGED_FUND:
                subAccountValuation = createManagedFundValuation(wrapSectorPortfolioPositions, assetType);
                break;
            case TERM_DEPOSIT:
                subAccountValuation = createTermDepositValuation(wrapSectorPortfolioPositions, assetType);
                break;
            default:
                logger.info("Asset type not supported: " + assetType);
                break;
        }
        return subAccountValuation;
    }

    private static SubAccountValuation createCashManagementValuation(List<PortfolioPosition> wrapSectorPortfolioPositions, AssetType assetType) {
        List<AccountHolding> cashHoldings = new ArrayList<>();
        for (PortfolioPosition cashPortfolioPosition : wrapSectorPortfolioPositions) {
            WrapCashHoldingImpl cashHolding = new WrapCashHoldingImpl();
            BigDecimal value = safeBigDecimal(cashPortfolioPosition.getValue());
            cashHolding.setAccountName(cashPortfolioPosition.getSecurityName()); // important for sorting
            cashHolding.setHoldingKey(getHoldingKey(assetType, cashPortfolioPosition.getSecurityName()));
            cashHolding.setValueDateBalance(value);
            cashHolding.setNextInterestDate(new DateMidnight().toDateTime());
            cashHolding.setThirdPartySource(SystemType.WRAP.getName());
            cashHolding.setAsset(getAssetType(assetType, cashPortfolioPosition.getSecurityCode(), cashPortfolioPosition.getSecurityName()));
            mapBaseAccountHolding(cashPortfolioPosition, cashHolding, assetType, cashPortfolioPosition.getSecurityName());
            cashHoldings.add(cashHolding);
        }
        CashAccountValuationImpl cashAccountValuation = new CashAccountValuationImpl();
        cashAccountValuation.addHoldings(cashHoldings);
        return cashAccountValuation;
    }

    private static SubAccountValuation createShareValuation(List<PortfolioPosition> wrapSectorPortfolioPositions, AssetType assetType) {
        List<AccountHolding> shareHoldings = new ArrayList<>();
        for (PortfolioPosition sharePortfolioPosition : wrapSectorPortfolioPositions) {
            ShareHoldingImpl shareHolding = new ShareHoldingImpl();
            String securityCode = getSecurityCode(sharePortfolioPosition);
            AssetImpl asset = getAssetType(assetType, securityCode, sharePortfolioPosition.getSecurityName());
            shareHolding.setAsset(asset);
            shareHolding.setHoldingKey(getHoldingKey(assetType, securityCode)); // important for sorting
            shareHolding.setDistributionMethod(null);
            BigDecimal change = safeBigDecimal(sharePortfolioPosition.getChange());
            shareHolding.setEstdGainDollar(change);
            BigDecimal cost = safeBigDecimal(sharePortfolioPosition.getCost());
            BigDecimal estdGainPercent = getEstimatedGainPercentage(change, cost);
            shareHolding.setEstdGainPercent(estdGainPercent);
            shareHolding.setHinType(HinType.CUSTODIAL);
            //shareHolding.setSource(WRAP_SOURCE);
            shareHolding.setMarketValue(safeBigDecimal(sharePortfolioPosition.getValue()));
            shareHolding.setAsset(getAssetType(assetType, sharePortfolioPosition.getSecurityCode(), sharePortfolioPosition.getSecurityName()));
            mapBaseAccountHolding(sharePortfolioPosition, shareHolding, assetType, sharePortfolioPosition.getSecurityName());
            shareHoldings.add(shareHolding);
        }
        ShareAccountValuationImpl shareAccountValuation = new ShareAccountValuationImpl(assetType);
        shareAccountValuation.addHoldings(shareHoldings);
        return shareAccountValuation;
    }

    private static SubAccountValuation createManagedFundValuation(List<PortfolioPosition> wrapSectorPortfolioPositions, AssetType assetType) {
        List<AccountHolding> managedFundHoldings = new ArrayList<>();
        for (PortfolioPosition managedFundPortfolioPosition : wrapSectorPortfolioPositions) {
            ManagedFundHoldingImpl managedFundHolding = new ManagedFundHoldingImpl();
            String securityCode = getSecurityCode(managedFundPortfolioPosition);
            AssetImpl asset = getAssetType(assetType, securityCode, managedFundPortfolioPosition.getSecurityName());
            managedFundHolding.setAsset(asset);
            managedFundHolding.setHoldingKey(getHoldingKey(assetType, securityCode)); // important for sorting
            BigDecimal change = safeBigDecimal(managedFundPortfolioPosition.getChange());
            managedFundHolding.setEstdGainDollar(change);
            BigDecimal cost = safeBigDecimal(managedFundPortfolioPosition.getCost());
            BigDecimal estdGainPercent = getEstimatedGainPercentage(change, cost);
            managedFundHolding.setEstdGainPercent(estdGainPercent);
            managedFundHolding.setEstdGainDollar(change);
            managedFundHolding.setUnitPriceDate(new DateMidnight().toDateTime());
            managedFundHolding.setAsset(getAssetType(assetType, managedFundPortfolioPosition.getSecurityCode(), managedFundPortfolioPosition.getSecurityName()));
            //managedFundHolding.setSource(WRAP_SOURCE);
            BigDecimal value = safeBigDecimal(managedFundPortfolioPosition.getValue());
            managedFundHolding.setMarketValue(value);
            managedFundHolding.setYield(BigDecimal.ZERO); // don't have it
            mapBaseAccountHolding(managedFundPortfolioPosition, managedFundHolding, assetType, managedFundPortfolioPosition.getSecurityName());
            managedFundHoldings.add(managedFundHolding);
        }
        ManagedFundAccountValuationImpl managedFundAccountValuation = new ManagedFundAccountValuationImpl();
        managedFundAccountValuation.addHoldings(managedFundHoldings);
        return managedFundAccountValuation;
    }

    private static SubAccountValuation createTermDepositValuation(List<PortfolioPosition> wrapSectorPortfolioPositions, AssetType assetType) {
        List<AccountHolding> termDepositHoldings = new ArrayList<>();
        for (PortfolioPosition termDepositPortfolioPosition : wrapSectorPortfolioPositions) {
            WrapTermDepositHoldingImpl termDepositHolding = new WrapTermDepositHoldingImpl();
            String securityCode = getSecurityCode(termDepositPortfolioPosition);
            termDepositHolding.setHoldingKey(getHoldingKey(assetType, securityCode)); // important for sorting
            // overrides
            termDepositHolding.setYield(null); // TODO: parse this, value in %.
            termDepositHolding.setMaturityDate(null); // TODO: parse this
            termDepositHolding.setSource(SystemType.WRAP.getName());
            termDepositHolding.setMaturityInstruction(HYPHEN);
            termDepositHolding.setAsset(getAssetType(assetType, securityCode, termDepositPortfolioPosition.getSecurityName()));
            mapBaseAccountHolding(termDepositPortfolioPosition, termDepositHolding, assetType, termDepositPortfolioPosition.getSecurityName());
            termDepositHolding.setThirdPartySource(SystemType.WRAP.getName());
            termDepositHoldings.add(termDepositHolding);
        }
        TermDepositAccountValuationImpl termDepositAccountValuation = new TermDepositAccountValuationImpl();
        termDepositAccountValuation.addHoldings(termDepositHoldings);
        return termDepositAccountValuation;
    }

    private static AssetImpl getAssetType(final AssetType assetType, final String securityCode, final String securityName) {
        AssetImpl asset = new AssetImpl(); // we don't need to contstruct from the subclasses
        asset.setAssetId(securityCode.toLowerCase()); // lowercase or the UI fails to paint sections because of changeddistributionSECURITY
        asset.setAssetName(securityName); // no name available from wrap for shares
        asset.setAssetCode(securityCode);
        asset.setAssetType(assetType);
        return asset;
    }

    private static HoldingKey getHoldingKey(final AssetType assetType, final String securityName) {
        return HoldingKey.valueOf(assetType.toString(), securityName);
    }

    /**
     * Map the common parts adjusting sectorHolding.
     *
     * @param wrapSectorPortfolioPosition
     * @param sectorHolding
     * @param assetType
     * @param wrapSecurityName
     */
    private static void mapBaseAccountHolding(PortfolioPosition wrapSectorPortfolioPosition,
                                              AbstractBaseAccountHolding sectorHolding, AssetType assetType, String wrapSecurityName) {
        BigDecimal value = safeBigDecimal(wrapSectorPortfolioPosition.getValue());
        sectorHolding.setExternal(IS_EXTERNAL_ASSET_YES.equalsIgnoreCase(wrapSectorPortfolioPosition.getIsExternalAsset()));
        sectorHolding.setMarketValue(value);
        sectorHolding.setCost(safeBigDecimal(wrapSectorPortfolioPosition.getCost()));
        sectorHolding.setUnits(safeBigDecimal(wrapSectorPortfolioPosition.getQuantity()));
        sectorHolding.setUnitPrice(safeBigDecimal(wrapSectorPortfolioPosition.getPrice()));
        sectorHolding.setYield(BigDecimal.ZERO);
        sectorHolding.setHoldingKey(getHoldingKey(assetType, wrapSecurityName));
        sectorHolding.setAvailableBalance(value);
    }

    /**
     * Get a code that the market knows this security by.
     * Wrap hides this in different columns for different assetSectors.
     */
    @Nonnull
    private static String getSecurityCode(final PortfolioPosition wrapPortfolioPosition) {
        String securityCode = wrapPortfolioPosition.getSecurityCode();
        String securityName = wrapPortfolioPosition.getSecurityName();
        if (StringUtils.isBlank(securityCode)) {
            securityCode = securityName; // Australian shares
            if (securityCode == null) {
                securityCode = "noSecurityName";
            }
        }
        return securityCode;
    }

    @Nullable
    private static Asset getABSAssetWithAssetCode(String assetCode, AssetIntegrationService assetIntegrationService, ServiceErrors serviceErrors) {
        Asset asset = null;
        List<Asset> assets = assetIntegrationService.loadAssetsForAssetCodes(Collections.singletonList(assetCode), serviceErrors);
        if (CollectionUtils.isNotEmpty(assets)) {
            asset = assets.get(0);
        }
        return asset;
    }

    @Nonnull
    private static AssetType getAssetTypeFromWrap(final String wrapSecurityCode, @Nullable final String wrapSecurityClass) {
        String securityClass = null;
        if (wrapSecurityClass != null) {
            securityClass = wrapSecurityClass.trim();
        }
        AssetType returnValue = AssetType.OTHER; // currently ignored
        if (isWrapTermDeposit(wrapSecurityCode)) {
            returnValue = AssetType.TERM_DEPOSIT;
        }
        else if (WRAP_SEC_CLASS_UNIT_TRUST.equals(securityClass)) {
            returnValue = AssetType.MANAGED_FUND;
        }
        else if (WRAP_SEC_CLASS_EQUITY.equals(securityClass)) {
            returnValue = AssetType.SHARE;
        }
        return returnValue;
    }

    private static boolean isWrapTermDeposit(String securityCode) {
        if (org.apache.commons.lang.StringUtils.isNotBlank(securityCode)) {
            return securityCode.startsWith("WBC") && securityCode.endsWith("TD");
        }
        return false;
    }

    private static
    @Nonnull
    BigDecimal safeBigDecimal(@Nullable final String string) {
        BigDecimal returnValue = BigDecimal.ZERO;
        try {
            if (string != null) {
                returnValue = new BigDecimal(string);
            }
        }
        catch (NumberFormatException e) {
            logger.info("NumberFormatException for: " + string + ": " + e);
        }
        return returnValue;
    }

    /**
     * eg $12345 change to a security that cost $41311 returns 129.88 (%)
     */
    private static BigDecimal getEstimatedGainPercentage(@Nonnull final BigDecimal change, @Nonnull final BigDecimal cost) {
        BigDecimal estdGainPercent = BigDecimal.ZERO;
        if (cost.compareTo(BigDecimal.ZERO) != 0) { // avoid dividing by zero
            estdGainPercent = ONE_HUNDRED_PERCENT.multiply(change.divide(cost, MathContext.DECIMAL64));
        }
        return estdGainPercent.setScale(DECIMAL_PLACES, RoundingMode.HALF_EVEN);
    }
}
