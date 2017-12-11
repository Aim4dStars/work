package com.bt.nextgen.api.asset.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.asset.controller.AssetApiController;
import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.termdeposit.service.ProductToAccountType;
import com.bt.nextgen.termdeposit.service.TermDepositAssetRateSearchKey;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.core.IsEqual;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.selectFirst;
import static com.bt.nextgen.api.asset.util.AssetConstants.FILTER_AAL;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;

@Service
@SuppressWarnings("squid:S1200")
public class AvailableAssetDtoServiceImpl implements AvailableAssetDtoService {
    private final Logger logger = LoggerFactory.getLogger(AvailableAssetDtoServiceImpl.class);

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private BrokerIntegrationService brokerService;

    @Autowired
    private AssetDtoConverter assetDtoConverter;

    @Autowired
    private AssetDtoConverterV2 assetDtoConverterV2;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private BankDateIntegrationService bankDateIntegrationService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private FeatureTogglesService featureTogglesService;


    protected List<AssetDto> toAssetDto(List<Asset> assets, Map<String, TermDepositAssetDetail> termDepositAssetDetails) {
        final List<AssetDto> assetDtos = assetDtoConverter.toAssetDto(assets, termDepositAssetDetails);

        Collections.sort(assetDtos, new Comparator<AssetDto>() {
            @Override
            public int compare(AssetDto a1, AssetDto a2) {
                try {
                    return a1.getAssetName().compareToIgnoreCase(a2.getAssetName());
                } catch (final Exception err) {
                    logger.warn("Problem with asset a1 {} or a2 {}", a1, a2, err);
                    if (a1 != null) {
                        logger.warn("a1 assetName is {}", a1.getAssetName());
                    }
                    if (a2 != null) {
                        logger.warn("a2 assetName is {}", a2.getAssetName());
                    }
                }

                return 0;
            }
        });

        return assetDtos;
    }


    protected List<AssetDto> toAssetDto(List<Asset> assets, List<TermDepositInterestRate> termDepositAssetDetails) {
        final List<AssetDto> assetDtos = assetDtoConverterV2.toAssetDto(assets, termDepositAssetDetails);

        Collections.sort(assetDtos, new Comparator<AssetDto>() {
            @Override
            public int compare(AssetDto a1, AssetDto a2) {
                try {
                    return a1.getAssetName().compareToIgnoreCase(a2.getAssetName());
                } catch (final Exception err) {
                    logger.warn("Problem with asset a1 {} or a2 {}", a1, a2, err);
                    if (a1 != null) {
                        logger.warn("a1 assetName is {}", a1.getAssetName());
                    }
                    if (a2 != null) {
                        logger.warn("a2 assetName is {}", a2.getAssetName());
                    }
                }

                return 0;
            }
        });

        return assetDtos;
    }
    @Override
    public List<AssetDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        EncodedString accountId = null;
        final DateTime bankDate = bankDateIntegrationService.getBankDate(serviceErrors);
        boolean termDepositToggle = featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle("termDepositToggle");
        if (!criteriaList.isEmpty()) {
            for (final ApiSearchCriteria parameter : criteriaList) {
                if (Attribute.ACCOUNT_ID.equals(parameter.getProperty())) {
                    accountId = new EncodedString(parameter.getValue());
                } else {
                    throw new IllegalArgumentException("Unsupported search");
                }
            }
            if (accountId == null) {
                throw new IllegalArgumentException("Unsupported search");
            }

            final WrapAccount account = accountService.loadWrapAccountDetail(AccountKey.valueOf(accountId.plainText()),
                    serviceErrors);

            final Broker adviser = brokerService.getBroker(account.getAdviserPositionId(), serviceErrors);

            SubAccount directProductSubAccount = null;
            for (final SubAccount subAccount : account.getSubAccounts()) {
                if (ContainerType.DIRECT.equals(subAccount.getSubAccountType())) {
                    directProductSubAccount = subAccount;
                    break;
                }
            }

            final List<Asset> assets = assetService.loadAvailableAssets(adviser.getDealerKey(),
                    directProductSubAccount.getProductIdentifier().getProductKey(), serviceErrors);
            final List<String> assetIds = Lambda.collect(assets,on(Asset.class).getAssetId());

            if(!termDepositToggle){
                return toAssetDto(assets, assetService.loadTermDepositRates(adviser.getDealerKey(), bankDate, assets, serviceErrors));
            }else{
                TermDepositAssetRateSearchKey termDepositAssetRateSearchKey = new TermDepositAssetRateSearchKey(account.getProductKey(),adviser.getDealerKey(),null,account.getAccountStructureType(),bankDate,assetIds);
                return toAssetDto(assets, assetService.loadTermDepositRates(termDepositAssetRateSearchKey, serviceErrors));
            }
        } else {
            // TODO - UPS REFACTOR1 - This should be account specific, we should warn if there are multiple accounts.
            final List<Asset> assets = assetService.loadAvailableAssets(userProfileService.getDealerGroupBroker().getDealerKey(),
                    serviceErrors);

            return toAssetDto(assets, assetService.loadTermDepositRates(userProfileService.getDealerGroupBroker().getDealerKey(),
                    bankDate, assets, serviceErrors));
        }

    }

    /**
     * Get all available assets for the dealer. If asset type key is provided, then only matching asset types and/or asset status
     * are returned.
     *
     * @param queryString
     *            To filter the results based on the query string
     * @param filterCriteria
     *            the filter criteria. Intercepts asset type and asset status, each is optional.
     * @param serviceErrors
     *            the service errors
     * @return list of sorted assets by asset name in ascending order, filtered by asset type, asset status or both
     */
    @Override
    public List<AssetDto> getFilteredValue(final String queryString, final List<ApiSearchCriteria> filterCriteria,
            final ServiceErrors serviceErrors) {
        String assetType = null;
        AssetStatus assetStatus = null;
        Boolean filterAal = true;
        final Map<String, TermDepositAssetDetail> termDepositRates = new HashMap<>();
        final List<TermDepositInterestRate> termDepositAssetRates = new ArrayList<>();
        boolean termDepositToggle = featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle("termDepositToggle");

        for (final ApiSearchCriteria parameter : filterCriteria) {
            if (Attribute.ASSET_TYPE.equals(parameter.getProperty())) {
                assetType = parameter.getValue();
            } else if (Attribute.ASSET_STATUS.equals(parameter.getProperty())) {
                assetStatus = AssetStatus.forName(parameter.getValue());
            } else if (FILTER_AAL.equals(parameter.getProperty())) {
                filterAal = Boolean.valueOf(parameter.getValue());
            }
        }

        final ApiSearchCriteria productIdCriteria = Lambda.selectFirst(filterCriteria,
                having(on(ApiSearchCriteria.class).getProperty(), IsEqual.equalTo(AssetApiController.PRODUCT_ID)));

        // TODO - UPS REFACTOR1 - Need to work out if these assets should be in the context of a specific account, which may
        // require interface changes at the JSON layer
        final BrokerKey dealerKey = userProfileService.getDealerGroupBroker().getDealerKey();

        final Collection<AssetType> assetTypes = assetType == null ? Collections.<AssetType> emptyList()
                : getAssetTypes(assetType);

        // Filter assets by query and asset types
        final Map<String, Asset> filteredAssetsMap = assetService.loadAssetsForCriteria(Collections.<String> emptyList(),
                queryString, assetTypes, serviceErrors);

        List<Asset> filteredAssets = loadAndFilterAssets(filterAal, dealerKey, productIdCriteria, filteredAssetsMap,
                serviceErrors);

        // Filter by asset status before passing to existing function
        if (assetStatus != null) {
            filteredAssets = select(filteredAssets, having(on(Asset.class).getStatus(), IsEqual.equalTo(assetStatus)));
        }
        DateTime bankDate = bankDateIntegrationService.getBankDate(serviceErrors);

        // Fetch TD rates details
        if (isEmpty(assetTypes) || assetTypes.contains(AssetType.TERM_DEPOSIT)) {
            final List<Asset> termDepositAssets = filter(having(on(Asset.class).getAssetType(), is(AssetType.TERM_DEPOSIT)),
                    filteredAssets);
            final List<String> assetIds = Lambda.collect(termDepositAssets,on(Asset.class).getAssetId());
            if (termDepositToggle) {
                ProductKey productKey = ProductKey.valueOf(EncodedString.toPlainText(productIdCriteria.getValue()));
                TermDepositAssetRateSearchKey termDepositAssetRateSearchKey = new TermDepositAssetRateSearchKey(productKey, dealerKey,
                        null, getAccountStructureType(productKey, serviceErrors), bankDate, assetIds);
                termDepositAssetRates.addAll(assetService.loadTermDepositRates(termDepositAssetRateSearchKey, serviceErrors));
                return toAssetDto(filteredAssets, termDepositAssetRates);
            } else {

                termDepositRates.putAll(assetService.loadTermDepositRates(dealerKey,
                        bankDateIntegrationService.getBankDate(serviceErrors), termDepositAssets, serviceErrors));
            }

        }

        return toAssetDto(filteredAssets, termDepositRates);
    }

    private AccountStructureType getAccountStructureType(ProductKey productKey, ServiceErrors serviceErrors) {
        Product product = productIntegrationService.getProductDetail(productKey, serviceErrors);
        return AccountStructureType.valueOf(ProductToAccountType.getDefaultAccountTypeForProduct(product.getProductName()));
    }

    private List<Asset> loadAndFilterAssets(final boolean filterAal, final BrokerKey dealerKey,
            final ApiSearchCriteria productIdCriteria, final Map<String, Asset> filteredAssetsMap,
            final ServiceErrors serviceErrors) {
        if (filterAal) {
            if (productIdCriteria == null) {
                return filterAssets(assetService.loadAvailableAssets(dealerKey, serviceErrors), filteredAssetsMap);
            }
            return filterAssets(assetService.loadAvailableAssets(dealerKey,
                    getDirectModelProductFromWhiteLabel(
                            ProductKey.valueOf(EncodedString.toPlainText(productIdCriteria.getValue())), serviceErrors),
                    serviceErrors), filteredAssetsMap);
        }
        return new ArrayList<>(filteredAssetsMap.values());

    }

    /**
     * Gets the direct model product from the white label product id, this method assumes that the provided product key is a White
     * Label product key.
     *
     * This is done so that the AAL can be looked up with correct product id.
     *
     * @param wlProductKey
     *            the white label product key
     * @param serviceErrors
     *            the service errors
     * @return the direct model product which is the descendant of the white label product.
     */
    private ProductKey getDirectModelProductFromWhiteLabel(ProductKey wlProductKey, ServiceErrors serviceErrors) {
        final Collection<Product> availableProducts = productIntegrationService.loadProducts(serviceErrors);
        final Product directOfferProduct = selectFirst(availableProducts, allOf(having(on(Product.class).isDirect(), is(true)),
                having(on(Product.class).getParentProductKey(), is(wlProductKey))));
        final Product directModelProduct = selectFirst(availableProducts,
                having(on(Product.class).getParentProductKey(), is(directOfferProduct.getProductKey())));
        return directModelProduct.getProductKey();
    }

    private List<Asset> filterAssets(List<Asset> availableAssets, Map<String, Asset> filteredAssets) {
        final List<Asset> filteredAvailableAssets = new ArrayList<>();
        if (isNotEmpty(availableAssets)) {
            for (final Asset asset : availableAssets) {
                if (filteredAssets.containsKey(asset.getAssetId())) {
                    filteredAvailableAssets.add(asset);
                }
            }
        }
        return filteredAvailableAssets;
    }

    private Collection<AssetType> getAssetTypes(String assetTypeValue) {
        final Collection<AssetType> assetTypes = new ArrayList<>();
        final String[] types = StringUtils.split(assetTypeValue, ",");
        AssetType assetType;
        for (final String type : types) {
            assetType = AssetType.forName(type);
            if (assetType != null) {
                assetTypes.add(assetType);
                if (AssetType.SHARE == assetType) {
                    assetTypes.add(AssetType.OPTION);
                    assetTypes.add(AssetType.BOND);
                }
            }
        }
        return assetTypes;
    }
}