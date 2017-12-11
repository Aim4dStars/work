package com.bt.nextgen.api.termdeposit.service;

import ch.lambdaj.Lambda;
import static ch.lambdaj.Lambda.filter;
import com.bt.nextgen.api.termdeposit.model.Badge;
import com.bt.nextgen.api.termdeposit.model.Brand;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorDealerKey;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorDto;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorKey;
import com.bt.nextgen.api.termdeposit.model.TermDepositRateDetails;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.CacheManagedTermDepositAssetRateIntegrationService;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.termdeposit.service.ProductToAccountType;
import com.bt.nextgen.termdeposit.service.TermDepositAssetRateSearchKey;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

/**
 * Created by l079353 on 10/07/2017.
 */
@Service
@SuppressWarnings("squid:UnusedProtectedMethod")//Suppressing this as matchSafely method has been defined in anonymous class
public class TermDepositRateCalculatorDtoServiceImpl implements TermDepositRateCalculatorDtoService {

    private static Logger LOGGER = LoggerFactory.getLogger(TermDepositRateCalculatorDtoServiceImpl.class);
    private static final String DIRECT_CHANNEL = "direct";
    private static final String INDIVIDUAL ="Individual";

    @Autowired
    private TermDepositCalculatorUtils termDepositCalculatorUtils;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    private TermDepositCalculatorConverter termDepositCalculatorConverter;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private TermDepositRateCalculatorCsvUtils csvUtils;

    /**
     * Method to get the whole calculator on the basis of TermDepositCalculatorKey(badge, amount, dealer-group) passed
     * along with the list of badge/products.
     *
     * @param key : TermDepositCalculatorKey(badge, amount, dealer-group)
     */
    @Override
    public TermDepositCalculatorDto find(TermDepositCalculatorKey key, ServiceErrors serviceErrors) {
        final Set<Badge> badges = new TreeSet<>(); // keep all badges for the dealer group.
        TermDepositRateDetails termDepositRateDetails = getTermDepositInterestRatesWithBadges(key, badges, serviceErrors);
        //Populate brands from the TermDepositInterestRate list.
        List<Brand> brands = populateBrands(termDepositRateDetails.getTermDepositInterestRates());
        TermDepositCalculatorDto termDepositCalculatorDto = termDepositCalculatorConverter
                .toTermDepositCalculatorDto(brands, termDepositRateDetails.getTermDepositInterestRates(), new BigDecimal(key.getAmount()));
        termDepositCalculatorDto.setBadge(termDepositRateDetails.getSelectedBadge());
        termDepositCalculatorDto.setBadges(new ArrayList<Badge>(badges));
        String productName = termDepositRateDetails.getSelectedBadge().getName();
        termDepositCalculatorDto.setSelectedAccountType(key instanceof TermDepositCalculatorDealerKey && ((TermDepositCalculatorDealerKey)key).getAccountType() != null
        ? ((TermDepositCalculatorDealerKey)key).getAccountType()  : ProductToAccountType.getDefaultAccountTypeForProduct(productName));
        termDepositCalculatorDto.setAccountTypeList(fetchAccountTypeListForBadge(termDepositRateDetails.getSelectedBadge().getProductId(), serviceErrors));
        return termDepositCalculatorDto;
    }

    private List<String> fetchAccountTypeListForBadge(String productId, ServiceErrors serviceErrors) {
        ProductKey productKey = ProductKey.valueOf(ConsistentEncodedString.toPlainText(productId));
        SortedSet accountTypes =  assetService.getAccountTypesByProduct(productKey,serviceErrors);
        return new ArrayList<String>(accountTypes);
    }

    public TermDepositRateDetails getTermDepositInterestRatesWithBadges(TermDepositCalculatorKey key,
                                                                     Set<Badge> badges, ServiceErrors serviceErrors) {
        LOGGER.debug("TermDepositRateCalculatorDtoServiceImpl.find() for key: {}", key);
        AccountStructureType accountStructureType = null;
        Map<ProductKey,List<Asset>> productAssets = new HashMap<>();

        WrapAccount account = termDepositCalculatorUtils.getAccount(key, serviceErrors);
        BrokerKey brokerKey = termDepositCalculatorUtils.getBrokerKey(key, account, serviceErrors);
        Map<ProductKey, Product> productMap = productIntegrationService.loadProductsMap(serviceErrors);
        List<Product> products = termDepositCalculatorUtils.getProducts(key, brokerKey, account, productMap, serviceErrors);
        Badge selectedBadge = selectBadge(products, key, productAssets, brokerKey, badges, productMap, serviceErrors);
        String productName = selectedBadge.getName();
        ProductKey selectedProductKey = ProductKey.valueOf(ConsistentEncodedString.toPlainText(selectedBadge.getProductId()));
        List<Asset> filteredAssets = filter(new LambdaMatcher<Asset>() {
            @Override
            protected boolean matchesSafely(Asset asset) {
                return AssetType.TERM_DEPOSIT.equals(asset.getAssetType());
            }
        }, productAssets.get(selectedProductKey));

        List<String> assetIds = extract(filteredAssets, on(Asset.class).getAssetId());
        DateTime bankDate = termDepositCalculatorUtils.getBankDate();
        BigDecimal amount = key.getAmount() == null ? null : new BigDecimal(key.getAmount());
        if(key instanceof TermDepositCalculatorDealerKey){
             String accountType = ((TermDepositCalculatorDealerKey)key).getAccountType();
             accountStructureType = accountType != null ? AccountStructureType.valueOf(accountType) : AccountStructureType.valueOf(ProductToAccountType.getDefaultAccountTypeForProduct(productName));
        }
        accountStructureType = account == null ? accountStructureType : account.getAccountStructureType();
        TermDepositAssetRateSearchKey searchKey = new TermDepositAssetRateSearchKey(selectedProductKey, brokerKey, amount,
                accountStructureType, bankDate, assetIds);
        List<TermDepositInterestRate> termDepositInterestRates = assetService
                .loadTermDepositRatesForAdviser(searchKey, serviceErrors);
        TermDepositRateDetails termDepositRateDetails = new TermDepositRateDetails();
        termDepositRateDetails.setTermDepositInterestRates(termDepositInterestRates);
        termDepositRateDetails.setSelectedBadge(selectedBadge);
        return termDepositRateDetails;
    }

    public Badge selectBadge(List<Product> products, TermDepositCalculatorKey key, Map<ProductKey, List<Asset>> productAssets, BrokerKey brokerKey, Set<Badge> badges,
                             Map<ProductKey, Product> productMap, ServiceErrors serviceErrors) {
        Badge selectedBadge = null;
        Map<ProductKey, Product> parentProducts = getParentKeyProducts(productMap.values());

        for (Product product : products) {

            Product directModelProduct = getDirectModelProduct(parentProducts, product, productMap, serviceErrors);
            if(directModelProduct != null){
                List<Asset> assets = assetService
                        .loadAvailableAssets(brokerKey, directModelProduct.getProductKey(), serviceErrors);

                if(CollectionUtils.isNotEmpty(assets)){
                    productAssets.put(product.getProductKey(),assets);
                    Badge badge = new Badge(ConsistentEncodedString.fromPlainText(product.getProductKey().getId()).toString(),
                            product.getProductName());
                    badges.add(badge);
                    // To check that badge is supplied from UI or not.
                    if (key.getBadge() != null) {
                        if (key.getBadge().equals(product.getProductKey())) {
                            selectedBadge = badge;
                        }
                    } else {
                        if ((product.getProductName() != null && "BT Panorama Investments"
                                .equals(product.getProductName())) || selectedBadge == null)
                        {
                            // set the default to investment product. not nice but it's the best way for now.
                            selectedBadge = badge;
                        }
                    }
                }
            }
        }
        return selectedBadge;
    }

    public List<Brand> populateBrands(List<TermDepositInterestRate> termDepositInterestRates) {
        Set<Brand> brandSet = new HashSet<>();
        for (TermDepositInterestRate termDepositInterestRate : termDepositInterestRates) {
            brandSet.add(new Brand(termDepositInterestRate.getIssuerId()));
        }
        List<Brand> brandList = (List<Brand>) Lambda.collect(brandSet);
        return brandList;
    }

    public Product getDirectModelProduct(Map<ProductKey, Product> parentProducts, Product baseProduct,
                                         Map<ProductKey, Product> productMap, ServiceErrors serviceErrors) {
        ProductKey productKey = baseProduct.getProductKey();
        while (productKey != null) {
            Product product = productMap.get(productKey);
            if (product != null && product.getProductLevel() == ProductLevel.MODEL && product.isDirect()) {
                return product;
            }
            Product childProduct = parentProducts.get(productKey);
            if (childProduct != null && childProduct.getProductLevel() == ProductLevel.MODEL && childProduct
                    .isDirect())
            {
                return childProduct;
            } else {
                if (childProduct != null) {
                    productKey = childProduct.getProductKey();
                } else {
                    LOGGER.info("Not able to fetch the child product, returning null.");
                    break;
                }
            }
        }
        LOGGER.info("Not able to fetch the child product, returning null.");
        return null;
    }

    public Map<ProductKey, Product> getParentKeyProducts(Collection<Product> products) {
        Map<ProductKey, Product> parentKeyProducts = new HashMap<>();
        for (Product product : products) {
            ProductKey parentProductKey = product.getParentProductKey();
            if (parentProductKey != null && product
                    .isDirect()) // ROOT products are not included & only direct product is
                // included.
                parentKeyProducts.put(parentProductKey, product);
        }
        return parentKeyProducts;
    }
}
