package com.bt.nextgen.api.smsf.service;

import com.bt.nextgen.api.smsf.constants.AssetType;
import com.bt.nextgen.api.smsf.constants.AssetClass;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;


/**
 * Representation of the asset type to asset class relationship (cardinality M:M)
 */
public class AssetClassMapping
{
    private static Map<AssetType, List<AssetClass>> assetClassMappings = new EnumMap<>(AssetType.class);

    static
    {
        assetClassMappings.put(AssetType.CASH, getClassesForCashAssetType());
        assetClassMappings.put(AssetType.TERM_DEPOSIT, getClassesForTermDepositAssetType());
        assetClassMappings.put(AssetType.AUSTRALIAN_LISTED_SECURITIES, getClassesForListedSecurityAssetType());
        assetClassMappings.put(AssetType.INTERNATIONAL_LISTED_SECURITIES, getClassesForInternationalSecurityAssetType());
        assetClassMappings.put(AssetType.MANAGED_FUND, getClassesForManagedFundAssetType());
        assetClassMappings.put(AssetType.MANAGED_PORTFOLIO, getClassesForManagedPortfolioAssetType());
        assetClassMappings.put(AssetType.DIRECT_PROPERTY, getClassesForDirectPropertyAssetType());
        assetClassMappings.put(AssetType.OTHER_ASSET, getClassesForOtherAssetType());
    }


    /**
     * Return all asset classifications for a given asset type
     * @param assetType asset type
     * @return all asset classifications for that type
     */
    public static List<AssetClass> getClassificationsForAssetType(AssetType assetType)
    {
        return assetClassMappings.get(assetType);
    }


    /**
     * Asset Classifications related to (@link AssetType#CASH)
     *
     * @return List of classifications that fall under asset type (@link AssetType#CASH)
     */
    private static List<AssetClass> getClassesForCashAssetType() {
        List<AssetClass> classes = new ArrayList<>();
        classes.add(AssetClass.CASH);
        return classes;
    }

    /**
     * Asset Classifications related to (@link AssetType#TERM_DEPOSIT)
     *
     * @return List of classifications that fall under asset type (@link AssetType#TERM_DEPOSIT)
     */
    private static List<AssetClass> getClassesForTermDepositAssetType() {
        List<AssetClass> classes = new ArrayList<>();
        classes.add(AssetClass.CASH);
        return classes;
    }

    /**
     * Asset Classifications related to (@link AssetType#AUSTRALIAN_LISTED_SECURITY)
     *
     * @return List of classifications that fall under asset type (@link AssetType#AUSTRALIAN_LISTED_SECURITY)
     */
    private static List<AssetClass> getClassesForListedSecurityAssetType() {
        List<AssetClass> classes = new ArrayList<>();
        classes.add(AssetClass.CASH);
        classes.add(AssetClass.AUSTRALIAN_FIXED_INTEREST);
        classes.add(AssetClass.INTERNATIONAL_FIXED_INTEREST);
        classes.add(AssetClass.AUSTRALIAN_FLOATING_RATE_INTEREST);
        classes.add(AssetClass.AUSTRALIAN_LISTED_SECURITIES);
        classes.add(AssetClass.INTERNATIONAL_LISTED_SECURITIES);
        classes.add(AssetClass.AUSTRALIAN_REAL_ESTATE);
        classes.add(AssetClass.INTERNATIONAL_REAL_ESTATE);
        classes.add(AssetClass.ALTERNATIVES);
        classes.add(AssetClass.DIVERSIFIED);
        classes.add(AssetClass.OTHER_ASSET);
        return classes;
    }

    /**
     * Asset Classifications related to (@link AssetType#INTERNATIONAL_LISTED_SECURITY)
     *
     * @return List of classifications that fall under asset type (@link AssetType#INTERNATIONAL_LISTED_SECURITY)
     */
    private static List<AssetClass> getClassesForInternationalSecurityAssetType() {
        List<AssetClass> classes = new ArrayList<>();
        classes.add(AssetClass.CASH);
        classes.add(AssetClass.AUSTRALIAN_FIXED_INTEREST);
        classes.add(AssetClass.INTERNATIONAL_FIXED_INTEREST);
        classes.add(AssetClass.AUSTRALIAN_FLOATING_RATE_INTEREST);
        classes.add(AssetClass.AUSTRALIAN_LISTED_SECURITIES);
        classes.add(AssetClass.INTERNATIONAL_LISTED_SECURITIES);
        classes.add(AssetClass.AUSTRALIAN_REAL_ESTATE);
        classes.add(AssetClass.INTERNATIONAL_REAL_ESTATE);
        classes.add(AssetClass.ALTERNATIVES);
        classes.add(AssetClass.DIVERSIFIED);
        classes.add(AssetClass.OTHER_ASSET);
        return classes;
    }

    /**
     * Asset Classifications related to (@link AssetType#MANAGED_FUND)
     *
     * @return List of classifications that fall under asset type (@link AssetType#MANAGED_FUND)
     */
    private static List<AssetClass> getClassesForManagedFundAssetType()
    {
        List<AssetClass> classes = new ArrayList<>();
        classes.add(AssetClass.CASH);
        classes.add(AssetClass.AUSTRALIAN_FIXED_INTEREST);
        classes.add(AssetClass.INTERNATIONAL_FIXED_INTEREST);
        classes.add(AssetClass.AUSTRALIAN_FLOATING_RATE_INTEREST);
        classes.add(AssetClass.AUSTRALIAN_LISTED_SECURITIES);
        classes.add(AssetClass.INTERNATIONAL_LISTED_SECURITIES);
        classes.add(AssetClass.AUSTRALIAN_REAL_ESTATE);
        classes.add(AssetClass.INTERNATIONAL_REAL_ESTATE);
        classes.add(AssetClass.ALTERNATIVES);
        classes.add(AssetClass.DIVERSIFIED);
        classes.add(AssetClass.OTHER_ASSET);
        return classes;
    }

    /**
     * Asset Classifications related to (@link AssetType#MANAGED_PORTFOLIO)
     *
     * @return List of classifications that fall under asset type (@link AssetType#MANAGED_PORTFOLIO)
     */
    private static List<AssetClass> getClassesForManagedPortfolioAssetType()
    {
        List<AssetClass> classes = new ArrayList<>();
        classes.add(AssetClass.CASH);
        classes.add(AssetClass.AUSTRALIAN_FIXED_INTEREST);
        classes.add(AssetClass.INTERNATIONAL_FIXED_INTEREST);
        classes.add(AssetClass.AUSTRALIAN_FLOATING_RATE_INTEREST);
        classes.add(AssetClass.AUSTRALIAN_LISTED_SECURITIES);
        classes.add(AssetClass.INTERNATIONAL_LISTED_SECURITIES);
        classes.add(AssetClass.AUSTRALIAN_REAL_ESTATE);
        classes.add(AssetClass.INTERNATIONAL_REAL_ESTATE);
        classes.add(AssetClass.ALTERNATIVES);
        classes.add(AssetClass.DIVERSIFIED);
        classes.add(AssetClass.OTHER_ASSET);
        return classes;
    }

    /**
     * Asset Classifications related to (@link AssetType#DIRECT_PROPERTY)
     *
     * @return List of classifications that fall under asset type (@link AssetType#DIRECT_PROPERTY)
     */
    private static List<AssetClass> getClassesForDirectPropertyAssetType() {
        List<AssetClass> classes = new ArrayList<>();
        classes.add(AssetClass.AUSTRALIAN_REAL_ESTATE);
        classes.add(AssetClass.INTERNATIONAL_REAL_ESTATE);
        return classes;
    }

    /**
     * Asset Classifications related to (@link AssetType#OTHER_ASSET)
     *
     * @return List of classifications that fall under asset type (@link AssetType#OTHER_ASSET)
     */
    private static List<AssetClass> getClassesForOtherAssetType()
    {
        List<AssetClass> classes = new ArrayList<>();
        classes.add(AssetClass.CASH);
        classes.add(AssetClass.AUSTRALIAN_FIXED_INTEREST);
        classes.add(AssetClass.INTERNATIONAL_FIXED_INTEREST);
        classes.add(AssetClass.AUSTRALIAN_FLOATING_RATE_INTEREST);
        classes.add(AssetClass.AUSTRALIAN_LISTED_SECURITIES);
        classes.add(AssetClass.INTERNATIONAL_LISTED_SECURITIES);
        classes.add(AssetClass.AUSTRALIAN_REAL_ESTATE);
        classes.add(AssetClass.INTERNATIONAL_REAL_ESTATE);
        classes.add(AssetClass.ALTERNATIVES);
        classes.add(AssetClass.DIVERSIFIED);
        classes.add(AssetClass.OTHER_ASSET);
        return classes;
    }

    public Map<AssetType, List<AssetClass>> getAssetClassMapping() {
        return assetClassMappings;
    }
}
