package com.bt.nextgen.api.product.service;

import com.bt.nextgen.api.product.model.ProductDocumentDto;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.on;

@Deprecated
@Service
public class ProductDocumentsDtoServiceImpl implements ProductDocumentsDtoService {
    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    private static final Logger logger = LoggerFactory.getLogger(ProductDocumentsDtoServiceImpl.class);

    @Override
    public ProductDocumentDto findOne(ServiceErrors serviceErrors) {
        //TODO - UPS REFACTOR1 - We should be passing in an account or dealer group for the users at this point
        Broker dealerGroup = userProfileService.getDealerGroupBroker();
        if (dealerGroup != null) {
            BrokerKey dealerGroupKey = dealerGroup.getDealerKey();
            List<Product> products = productIntegrationService.getDealerGroupProductList(dealerGroupKey, serviceErrors);
            return getDocumentList(dealerGroupKey, products, serviceErrors);
        }
        return null;
    }

    /**
     * Returns the list of products and brands for logged in user
     *
     * @param dealerGroupKey - ID of the dealer group, the logged in user is attached to.
     * @param products       - list of products from BTFG$UI_APL_LIST.ALL#PROD
     * @return ProductDocumentDto
     */
    private ProductDocumentDto getDocumentList(BrokerKey dealerGroupKey, List<Product> products,
                                               ServiceErrors serviceErrors) {
        List<String> productList = new ArrayList<>();
        List<String> brandList = new ArrayList<>();
        boolean managedFundAvailable = false;
        if (products != null) {
            productList.addAll(collect(products, on(Product.class).getShortName()));
            List<Product> directModelProducts = getDirectModelProducts(products, null, serviceErrors);
            brandList.addAll(getBrandList(dealerGroupKey, directModelProducts, serviceErrors));
            managedFundAvailable = isManagedFundAvailable(dealerGroupKey, directModelProducts, serviceErrors);
        }

        String encodedBrokerId = EncodedString.fromPlainText(dealerGroupKey.getId()).toString();
        ProductDocumentDto productDocumentDto = new ProductDocumentDto(new com.bt.nextgen.api.broker.model.BrokerKey(
                encodedBrokerId));
        productDocumentDto.setProductList(productList);
        productDocumentDto.setBrandList(brandList);
        productDocumentDto.setManagedFundAvailable(managedFundAvailable);
        return productDocumentDto;
    }

    /**
     * Returns the list of brands for logged in user
     *
     * @param dealerGroupKey      - ID of the dealer group, the logged in user is attached to.
     * @param directModelProducts - list of direct model products
     * @return List : String
     */
    private List<String> getBrandList(BrokerKey dealerGroupKey, List<Product> directModelProducts, ServiceErrors serviceErrors) {
        Set<String> brandList = new HashSet<>();
        List<Asset> assetList;
        if (directModelProducts != null) {
            for (Product directModelProduct : directModelProducts) {
                assetList = assetIntegrationService.loadAvailableAssets(dealerGroupKey,
                        directModelProduct.getProductKey(),
                        serviceErrors);

                if (assetList != null) {
                    logger.info("Number of assets found: {} for dealer-group: {} and Product: {}",
                            assetList.size(),
                            dealerGroupKey,
                            directModelProduct.getProductKey());

                    for (Asset asset : assetList) {
                        if (AssetType.TERM_DEPOSIT.equals(asset.getAssetType())) {
                            brandList.add(asset.getBrand());
                        }
                    }
                }
            }
        }
        return new ArrayList<>(brandList);
    }


    /**
     * Recursively find the direct model products related to the input parent product
     *
     * @param products            - list of products from BTFG$UI_APL_LIST.ALL#PROD
     * @param parentKeyProductMap - Map: key-parent product key, value- list of associated products.
     * @return Product : associated Direct (Model) Product based on Product level
     */
    private List<Product> getDirectModelProducts(final List<Product> products,
            final Map<ProductKey, List<Product>> parentKeyProductMap, final ServiceErrors serviceErrors) {
        if (products != null) {
            Map<ProductKey, List<Product>> productMap;
            final List<Product> directModelProducts = new ArrayList<>();
            for (final Product product : products) {
                if (ProductLevel.MODEL.equals(product.getProductLevel()) && product.isDirect()) {
                    directModelProducts.add(product);
                } else {
                    productMap = MapUtils.isNotEmpty(parentKeyProductMap) ? parentKeyProductMap
                            : getParentKeyProductMap(productIntegrationService.loadProducts(serviceErrors));
                    directModelProducts.addAll(
                            getDirectModelProducts(productMap.get(product.getProductKey()), parentKeyProductMap, serviceErrors));
                }
            }
            return directModelProducts;
        }
        return Collections.emptyList();
    }

    /**
     * Utility method to convert the list products in to a map having product parent ID(prod_parent_id)
     * as key and list of products as value.
     * Saves the multiple iteration to the whole list to retrieve the child of a product.
     *
     * @param products - list of products from BTFG$UI_PROD_LIST.ALL#PROD_DET
     * @return Map: key-parent product key, value- list of associated products.
     */
    private Map<ProductKey, List<Product>> getParentKeyProductMap(Collection<Product> products) {
        Map<ProductKey, List<Product>> parentKeyProducts = new HashMap<>();
        List<Product> productList;

        if (CollectionUtils.isNotEmpty(products)) {
            for (Product product : products) {
                final ProductKey parentProductKey = product.getParentProductKey();
                if (parentProductKey != null) {
                    productList = parentKeyProducts.get(parentProductKey);
                    if (productList == null) {
                        productList = new ArrayList<>();
                        parentKeyProducts.put(parentProductKey, productList);
                    }
                    productList.add(product);
                }
            }
        }
        return parentKeyProducts;
    }

    /**
     * Returns if Managed Fund is available for logged in user
     *
     * @param dealerGroupKey    - ID of the dealer group, the logged in user is attached to.
     * @param directModelProducts          - list of products from BTFG$UI_APL_LIST.ALL#PROD
     * @return boolean : is Managed Fund available
     */
    private boolean isManagedFundAvailable(BrokerKey dealerGroupKey, List<Product> directModelProducts, ServiceErrors serviceErrors) {
        List<Asset> assetList;
        if (directModelProducts != null) {
            for (Product directModelProduct : directModelProducts) {
                assetList = assetIntegrationService.loadAvailableAssets(dealerGroupKey,
                        directModelProduct.getProductKey(),
                        serviceErrors);

                if (assetList != null) {
                    logger.info("Number of assets found: {} for dealer-group: {} and Product: {}",
                            assetList.size(),
                            dealerGroupKey,
                            directModelProduct.getProductKey());

                    for (Asset asset : assetList) {
                        if (AssetType.MANAGED_FUND.equals(asset.getAssetType())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}