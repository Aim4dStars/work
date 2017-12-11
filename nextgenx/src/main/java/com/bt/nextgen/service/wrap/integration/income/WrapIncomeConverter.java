package com.bt.nextgen.service.wrap.integration.income;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.avaloq.income.HoldingIncomeDetailsImpl;
import com.bt.nextgen.service.avaloq.income.SubAccountIncomeDetailsImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.income.HoldingIncomeDetails;
import com.bt.nextgen.service.integration.income.IncomeType;
import com.bt.nextgen.service.integration.income.SubAccountIncomeDetails;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.wrap.model.Income;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.service.wrap.integration.income.WrapIncomeConverterModelBuilder.getIncomeType;
import static com.bt.nextgen.service.wrap.integration.income.WrapIncomeFactory.buildIncomeEntryModel;
import static com.bt.nextgen.service.wrap.integration.util.WrapAssetUtil.isWrapCash;
import static com.bt.nextgen.service.wrap.integration.util.WrapAssetUtil.isWrapTermDeposit;

/**
 * Converts Wrap Investment income into Panorama service layer objects
 * Created by L067221 on 1/08/2017.
 */

@Component
public class WrapIncomeConverter {

    private static final String WRAP_SEC_CLASS_UNIT_TRUST = "Unit Trust";
    private static final String WRAP_SEC_CLASS_EQUITY = "Equity";

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    /**
     * Converts Wrap Investment income to  service layer objects
     *
     * @param incomes       wrap incomes
     * @param serviceErrors
     *
     * @return
     */
    public List<SubAccountIncomeDetails> convert(List<Income> incomes, ServiceErrors serviceErrors) {
        final Map<String, Asset> assetMap = buildAssetMap(incomes, serviceErrors);
        final Map<AssetType, List<HoldingIncomeDetails>> holdingIncomeMap = buildHoldingIncomesMap(incomes, assetMap);

        final List<SubAccountIncomeDetails> subAccountIncomeDetailsList = new ArrayList<>();
        for (Map.Entry<AssetType, List<HoldingIncomeDetails>> holdingIncomeDetailsItem : holdingIncomeMap.entrySet()) {
            if (!holdingIncomeDetailsItem.getValue().isEmpty()) {
                final SubAccountIncomeDetailsImpl account = new SubAccountIncomeDetailsImpl();
                account.setAssetType(holdingIncomeDetailsItem.getKey());
                account.addIncomes(holdingIncomeDetailsItem.getValue());
                subAccountIncomeDetailsList.add(account);
            }
        }
        return subAccountIncomeDetailsList;
    }

    //Returns Map of Asset Type and HoldingIncomeDetails
    private Map<AssetType, List<HoldingIncomeDetails>> buildHoldingIncomesMap(List<Income> incomes, Map<String, Asset> assetMap) {
        final Map<Asset, List<com.bt.nextgen.service.integration.income.Income>> incomeDetailsMap = new HashMap<Asset, List<com.bt.nextgen.service.integration.income.Income>>();
        for (Income wrapIncome : incomes) {
            final Asset asset = assetMap.get(wrapIncome.getSecurityCode());
            if (asset != null) {
                final IncomeType incomeType = getIncomeType(asset, wrapIncome);
                final com.bt.nextgen.service.integration.income.Income income = buildIncomeEntryModel(incomeType, wrapIncome);

                if (incomeDetailsMap.get(asset) == null) {
                    incomeDetailsMap.put(asset, new ArrayList<com.bt.nextgen.service.integration.income.Income>());
                }
                incomeDetailsMap.get(asset).add(income);
            }
        }
        return buildAssetIncomeDetailsMap(incomeDetailsMap);
    }

    //Returns HoldingIncomeDetails with Asset and Income List
    private HoldingIncomeDetailsImpl buildHoldingIncomeDetails(Asset asset, List<com.bt.nextgen.service.integration.income.Income> incomeList) {
        final HoldingIncomeDetailsImpl holdingIncomeDetails = new HoldingIncomeDetailsImpl();
        holdingIncomeDetails.setAsset(asset);
        holdingIncomeDetails.addIncomes(incomeList);
        return holdingIncomeDetails;
    }

    //Returns Map of Asset Type and HoldingIncomeDetails
    private Map<AssetType, List<HoldingIncomeDetails>> buildAssetIncomeDetailsMap(Map<Asset, List<com.bt.nextgen.service.integration.income.Income>> incomeDetailsMap) {
        final Map<AssetType, List<HoldingIncomeDetails>> holdingIncomesMap = new HashMap<AssetType, List<HoldingIncomeDetails>>();
        holdingIncomesMap.put(AssetType.CASH, new ArrayList<HoldingIncomeDetails>());
        holdingIncomesMap.put(AssetType.TERM_DEPOSIT, new ArrayList<HoldingIncomeDetails>());
        holdingIncomesMap.put(AssetType.MANAGED_FUND, new ArrayList<HoldingIncomeDetails>());
        holdingIncomesMap.put(AssetType.SHARE, new ArrayList<HoldingIncomeDetails>());
        for (Map.Entry<Asset, List<com.bt.nextgen.service.integration.income.Income>> incomeList : incomeDetailsMap.entrySet()) {
            AssetType assetType = incomeList.getKey().getAssetType();
            if (assetType == AssetType.BOND || assetType == AssetType.OPTION) {
                assetType = AssetType.SHARE;
            }

            final HoldingIncomeDetailsImpl holdingIncomeDetails = buildHoldingIncomeDetails(incomeList.getKey(),
                    incomeList.getValue());
            if (holdingIncomesMap.get(assetType) != null) {
                holdingIncomesMap.get(assetType).add(holdingIncomeDetails);
            }
        }

        return holdingIncomesMap;
    }

    /**
     * Builds asset map for all assets in Wrap Income
     *
     * @param incomes
     * @param serviceErrors
     *
     * @return
     */
    private Map<String, Asset> buildAssetMap(List<Income> incomes,
                                             ServiceErrors serviceErrors) {
        final Map<String, Asset> assetMap = new HashMap<>();
        for (Income income : incomes) {
            addAssetToMap(assetMap, income, serviceErrors);
        }
        return assetMap;
    }


    @Nullable
    private Asset getABSAssetWithAssetCode(String assetCode, ServiceErrors serviceErrors) {
        Asset asset = null;
        List<Asset> assets = assetIntegrationService.loadAssetsForAssetCodes(Collections.singletonList(assetCode), serviceErrors);
        if (CollectionUtils.isNotEmpty(assets)) {
            asset = assets.get(0);
        }
        return asset;
    }

    @Nonnull
    private AssetType getAssetTypeFromWrap(@Nullable final String wrapSecurityClass) {
        String securityClass = null;
        if (wrapSecurityClass != null) {
            securityClass = wrapSecurityClass.trim();
        }
        AssetType returnValue = AssetType.OTHER; // currently ignored
        if (WRAP_SEC_CLASS_UNIT_TRUST.equals(securityClass)) {
            returnValue = AssetType.MANAGED_FUND;
        }
        else if (WRAP_SEC_CLASS_EQUITY.equals(securityClass)) {
            returnValue = AssetType.SHARE;
        }
        return returnValue;
    }

    /**
     * Updates assetSet and assetMap by evaluating securityCode
     * If securityCode equals WRAPWCA than Income is classified as Cash
     * If securityCode starts with WBC and ends TD than Income is classified as TDs
     *
     * @param assetMap
     * @param income
     */
    private void addAssetToMap(Map<String, Asset> assetMap, Income income, ServiceErrors serviceErrors) {
        if (StringUtils.isEmpty(income.getSecurityCode())) {
            return;
        }

        final String securityCode = income.getSecurityCode().trim();
        if (isWrapCash(securityCode)) {
            assetMap.put(securityCode, getCashAsset(income));
        }
        else if (isWrapTermDeposit(securityCode)) {
            assetMap.put(securityCode, getTermDepositAsset(securityCode));
        }
        else {
            AssetType assetType = AssetType.OTHER;
            Asset asset = getABSAssetWithAssetCode(securityCode, serviceErrors);
            if (asset == null) {
                assetType = getAssetTypeFromWrap(income.getSecurityClass());
                asset = getMappedAsset(assetType, income);
            }
            assetMap.put(asset.getAssetCode(), asset);
        }
    }

    /**
     * Returns Wrap Term Deposit Asset
     *
     * @param termDepositAsset
     *
     * @return
     */
    private Asset getTermDepositAsset(String termDepositAsset) {
        final AssetImpl asset = new TermDepositAssetImpl();
        asset.setAssetType(AssetType.TERM_DEPOSIT);
        asset.setAssetId(termDepositAsset);
        asset.setAssetCode(termDepositAsset);
        return asset;
    }

    /**
     * Returns Wrap Cash Asset
     *
     * @param income
     *
     * @return
     */
    private Asset getCashAsset(Income income) {
        final AssetImpl asset = new AssetImpl();
        asset.setAssetType(AssetType.CASH);
        asset.setAssetId(income.getSecurityCode());
        asset.setAssetName(income.getSecurityName());
        return asset;
    }

    /**
     * Returns Asset - created with the mapped assettype
     *
     * @param income
     *
     * @return
     */
    private Asset getMappedAsset(AssetType assetType, Income income) {
        final AssetImpl asset = new AssetImpl();
        asset.setAssetType(assetType);
        asset.setAssetId(income.getSecurityCode());
        asset.setAssetCode(income.getSecurityCode());
        asset.setAssetName(income.getSecurityName());
        return asset;
    }
}
