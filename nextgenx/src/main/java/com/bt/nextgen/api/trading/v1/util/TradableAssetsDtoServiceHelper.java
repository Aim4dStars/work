package com.bt.nextgen.api.trading.v1.util;

import com.bt.nextgen.api.account.v2.model.DatedAccountKey;
import com.bt.nextgen.api.account.v2.service.DistributionAccountDtoService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.collection.AvaloqCollectionIntegrationService;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateMidnight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

// suppressed warning about too many dependancies as this is a helper class
@SuppressWarnings({ "squid:S1200" })
@Component
public class TradableAssetsDtoServiceHelper {
    public static final String WHOLESALE_PLUS = "wholesale_plus";
    public static final String WHOLESALE_PLUS_SEARCH = "wholesale plus";
    public static final String WHOLESALE_PLUS_DESC = "Wholesale plus funds";

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Autowired
    public DistributionAccountDtoService distributionAccountDtoService;

    @Qualifier("cacheAvaloqAccountIntegrationService")
    @Autowired
    public AccountIntegrationService accountService;

    @Qualifier("cacheAvaloqPortfolioIntegrationService")
    @Autowired
    public PortfolioIntegrationService portfolioService;

    @Autowired
    public BrokerIntegrationService brokerService;

    @Autowired
    private AvaloqCollectionIntegrationService collectionService;

    private static final Logger logger = LoggerFactory.getLogger(TradableAssetsDtoServiceHelper.class);

    public Map<String, Asset> getFilteredAssets(boolean assetTypeHeld, Collection<String> assetIds, String query,
            Map<String, Asset> valuationAssets, Collection<AssetType> assetTypes, boolean wholesalePlus,
            ServiceErrors serviceErrors) {
        Map<String, Asset> filteredAssets = new HashMap<>();
        if (assetTypeHeld && StringUtils.isEmpty(query)) {
            // just use the assets in the valuation
            filteredAssets.putAll(valuationAssets);
        } else {
            filteredAssets = filterAssets(assetIds, query, assetTypes, wholesalePlus, serviceErrors);
        }
        return filteredAssets;
    }

    protected Map<String, Asset> filterAssets(Collection<String> assetIds, String query, Collection<AssetType> assetTypes,
            boolean wholesalePlus, ServiceErrors serviceErrors) {
        Map<String, Asset> filteredAssets = new HashMap<>();
        // load assets from the matched assets given in the query string
        filteredAssets.putAll(assetIntegrationService.loadAssetsForCriteria(assetIds, query, assetTypes, serviceErrors));

        if (wholesalePlus) {
            filterWholesalePlus(filteredAssets);
        }
        return filteredAssets;
    }

    protected void filterWholesalePlus(Map<String, Asset> filteredAssets) {
        Iterator<Entry<String, Asset>> it = filteredAssets.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Asset> assetEntry = it.next();
            if (assetEntry.getValue().getAssetName() == null
                    || !assetEntry.getValue().getAssetName().toLowerCase().contains(WHOLESALE_PLUS_SEARCH)) {
                it.remove();
            }
        }
    }

    public Map<String, List<DistributionMethod>> loadDistributionMethods(Collection<Asset> assets) {
        final Map<String, List<DistributionMethod>> assetDistributionMethods = new HashMap<String, List<DistributionMethod>>();
        for (Asset asset : assets) {
            assetDistributionMethods.put(asset.getAssetId(), distributionAccountDtoService.getAvailableDistributionMethod(asset));
        }
        return assetDistributionMethods;
    }

    public WrapAccountValuation loadValuation(String accountId, ServiceErrors serviceErrors) {
        final DatedAccountKey key = new DatedAccountKey(accountId, DateMidnight.now().toDateTime());
        final AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        return portfolioService.loadWrapAccountValuation(accountKey, key.getEffectiveDate(), serviceErrors);
    }

    public WrapAccountDetail loadAccount(String accountId, ServiceErrors serviceErrors) {
        final EncodedString encodedAccountId = new EncodedString(accountId);
        final WrapAccountDetail account = accountService.loadWrapAccountDetail(AccountKey.valueOf(encodedAccountId.plainText()),
                serviceErrors);
        if (account == null) {
            logger.error("Account could not be loaded. Usually caused by faulty apl initialisation");
        }
        return account;
    }

    public BrokerKey loadBroker(WrapAccountDetail account, ServiceErrors serviceErrors) {
        BrokerKey brokerKey = null;
        final Broker adviser = brokerService.getBroker(account.getAdviserKey(), serviceErrors);
        if (adviser == null) {
            logger.error("account's adviser could not be loaded. Usually caused by faulty broker hiearchy initialisation");
        } else {
            brokerKey = adviser.getDealerKey();
        }
        return brokerKey;
    }

    public ProductKey loadDirectProductKey(WrapAccountDetail account) {
        ProductKey directProductKey = null;
        for (SubAccount subAccount : account.getSubAccounts()) {
            if (ContainerType.DIRECT.equals(subAccount.getSubAccountType())) {
                directProductKey = subAccount.getProductIdentifier().getProductKey();
                break;
            }
        }
        if (directProductKey == null) {
            logger.error("could not find the product id for the direct container under the account");
        }
        return directProductKey;
    }

    /**
     * get a map of the assets in the valuation
     * 
     */
    public Map<String, Asset> getValuationAssets(WrapAccountValuation valuation) {
        final Map<String, Asset> valuationAssets = new HashMap<>();
        for (SubAccountValuation subAccount : valuation.getSubAccountValuations()) {
            final AssetType subAssetType = subAccount.getAssetType();
            if (AssetType.MANAGED_PORTFOLIO.equals(subAssetType) || AssetType.TAILORED_PORTFOLIO.equals(subAssetType)) {
                final ManagedPortfolioAccountValuation mpValuation = (ManagedPortfolioAccountValuation) subAccount;
                valuationAssets.put(mpValuation.getAsset().getAssetId(), mpValuation.getAsset());
            } else if (AssetType.MANAGED_FUND.equals(subAssetType)) {
                for (AccountHolding holding : subAccount.getHoldings()) {
                    valuationAssets.put(holding.getAsset().getAssetId(), holding.getAsset());
                }
            } else if (AssetType.SHARE.equals(subAssetType)) {
                for (AccountHolding holding : subAccount.getHoldings()) {
                    valuationAssets.put(holding.getAsset().getAssetId(), holding.getAsset());
                }
            }
        }

        return valuationAssets;
    }

    public BigDecimal getCombinedAmount(final BigDecimal value, final BigDecimal augend) {
        if (value == null)
            return augend;
        else if (augend == null)
            return value;
        else
            return value.add(augend);
    }

    /**
     * Returns the list of asset Ids to be excluded for the Adviser (currently only for direct).
     * The assumption for the collections(that contain the list of assets to be excluded) & adviser position is:
     * Adviser OE key == Collection Sym key
     *
     * @param account - The account for which the assets are being loaded
     */
    public List<String> getAssetsToExclude(WrapAccountDetail account, ServiceErrors serviceErrors) {
        final Broker adviser = brokerService.getBroker(account.getAdviserKey(), serviceErrors);
        if (adviser != null && adviser.isDirectInvestment()) {
            logger.info("Loading assets to exclude for the direct Adviser position: {}", adviser.getBrokerOEKey());
            return collectionService.loadAssetsForCollection(adviser.getBrokerOEKey(), serviceErrors);
        }
        return new ArrayList<>();
    }
}
