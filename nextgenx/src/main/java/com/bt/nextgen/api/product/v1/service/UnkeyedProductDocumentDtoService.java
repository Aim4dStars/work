package com.bt.nextgen.api.product.v1.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.product.v1.model.ProductDto;
import com.bt.nextgen.api.product.v1.model.UnkeyedProductDocumentDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.selectDistinct;

/**
 * The UnkeyedProductDocumentDtoService, provides functionality to produce the UnkeyedProductDocumentDto which can then be keyed
 * by implemented subclasses.
 */
public class UnkeyedProductDocumentDtoService {

    /** The asset integration service. */
    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    protected AssetIntegrationService assetIntegrationService;

    /** The product integration service. */
    @Autowired
    protected ProductIntegrationService productIntegrationService;

    /** The user profile service. */
    @Autowired
    protected UserProfileService userProfileService;

    /** The broker helper service. */
    @Autowired
    protected BrokerHelperService brokerHelperService;

    @Autowired
    private ProductDtoConverter productDtoConverter;

    /**
     * Returns the unkeyed list of products and brands for provided dealer group and products.
     *
     * @param dealerGroupKey    - ID of the dealer group.
     * @param products          - list of products (whitelabel and offers for dealer group/account)
     * @param availableProducts - list of products from BTFG$UI_APL_LIST.ALL#PROD
     */
    UnkeyedProductDocumentDto getDocumentList(final BrokerKey dealerGroupKey, final List<Product> products,
                                              final Collection<Product> availableProducts, final ServiceErrors serviceErrors) {
        final List<ProductDto> productList = new ArrayList<>();
        final List<String> brandList = new ArrayList<>();
        final List<String> documentTags = new ArrayList<>();
        boolean managedFundAvailable = false;
        if (CollectionUtils.isNotEmpty(products)) {
            productList.addAll(Lambda.convert(products, productDtoConverter));
            final List<Product> directModelProducts = getDirectModelProducts(products, availableProducts);
            if (CollectionUtils.isNotEmpty(directModelProducts)) {
                for (Product directModelProduct : directModelProducts) {
                    final List<Asset> assetList = getAssetList(dealerGroupKey, directModelProduct, serviceErrors);
                    if (CollectionUtils.isNotEmpty(assetList)) {
                        brandList.addAll(getBrandList(assetList));
                        managedFundAvailable = isManagedFundAvailable(assetList);
                    }
                }
            }
            documentTags.addAll(collect(products, on(Product.class).getShortName()));
        }
        documentTags.addAll(brandList);
        documentTags.add(userProfileService.getActiveProfile().getJobRole().name());
        return new UnkeyedProductDocumentDto(productList, brandList, documentTags, managedFundAvailable);
    }

    private List<Asset> getAssetList(BrokerKey dealerGroupKey, Product directModelProduct, ServiceErrors serviceErrors) {
        if (directModelProduct != null) {
            return assetIntegrationService.loadAvailableAssets(dealerGroupKey, directModelProduct.getProductKey(), serviceErrors);
        }
        return Collections.emptyList();
    }

    /**
     * Returns the list of brands provided products.
     *
     * @param assetList - list of available assets
     * @return boolean : is Managed Fund available
     */
    private List<String> getBrandList(Collection<Asset> assetList) {
            final List<Asset> termDeposits = select(assetList, having(on(Asset.class).getAssetType(), Matchers.equalTo(AssetType.TERM_DEPOSIT)));
            return collect(selectDistinct(termDeposits, "brand"), on(Asset.class).getBrand());
    }

    /**
     * Find the direct model products related to the input parent product.
     *
     * @param dgProducts        - dealer group products
     * @param availableProducts - list of products from BTFG$UI_APL_LIST.ALL#PROD
     * @return Product : associated Direct (Model) Product based on Product level
     */
    private List<Product> getDirectModelProducts(final List<Product> dgProducts, final Collection<Product> availableProducts) {
        final List<ProductKey> whiteLabelProductKeys = collect(select(dgProducts, having(on(Product.class).getProductLevel(),
                Matchers.equalTo(ProductLevel.WHITE_LABEL))), on(Product.class).getProductKey());
        final List<Product> directModelProducts = new ArrayList<>();
        for (ProductKey whiteLabelProductKey : whiteLabelProductKeys) {
            final Product modelProduct = getDirectModelProductFromWhiteLabel(whiteLabelProductKey, availableProducts);
            if (modelProduct != null) {
                directModelProducts.add(modelProduct);
            }
        }
        return directModelProducts;
    }

    private Product getDirectModelProductFromWhiteLabel(ProductKey wlProductKey, Collection<Product> availableProducts) {
        final Product directOfferProduct = Lambda.selectFirst(availableProducts, Matchers.allOf(
                having(on(Product.class).isDirect(), Matchers.is(true)),
                having(on(Product.class).getParentProductKey(), Matchers.is(wlProductKey))));
        if (directOfferProduct != null) {
            return Lambda.selectFirst(availableProducts, having(on(Product.class).getParentProductKey(), Matchers.is(directOfferProduct.getProductKey())));
        }
        return null;
    }

    /**
     * Utility method to convert the list products in to a map having product parent ID(prod_parent_id) as key and list of
     * products as value. Saves the multiple iteration to the whole list to retrieve the child of a product.
     *
     * @param products
     *            - list of products from BTFG$UI_PROD_LIST.ALL#PROD_DET
     * @return Map: key-parent product key, value- list of associated products.
     */
    protected Map<ProductKey, List<Product>> getParentKeyProductMap(final Collection<Product> products) {
        final Map<ProductKey, List<Product>> parentKeyProducts = new HashMap<>();
        List<Product> productList;

        if (CollectionUtils.isNotEmpty(products)) {
            for (final Product product : products) {
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
     * Returns if Managed Funds are available for the provided products.
     *
     * @param assetList - list of available assets
     * @return boolean : is Managed Fund available
     */
    private boolean isManagedFundAvailable(final List<Asset> assetList) {
        final List<Asset> managedFunds = select(assetList, having(on(Asset.class).getAssetType(), Matchers.equalTo(AssetType.MANAGED_FUND)));
        return CollectionUtils.isNotEmpty(managedFunds);
    }
}
