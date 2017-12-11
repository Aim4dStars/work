package com.bt.nextgen.service.integration.options.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.integration.options.model.CategoryKey;
import com.bt.nextgen.service.integration.options.model.CategoryType;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductHierarchyFactory implements HierarchyFactory<ProductKey> {
    @Autowired
    private ProductIntegrationService productService;

    @Override
    public List<CategoryKey> buildHierarchy(ProductKey productKey, ServiceErrors serviceErrors) {
        List<CategoryKey> hierarchy = new ArrayList<>();
        ProductKey thisProductKey = productKey;
        while (thisProductKey != null) {
            Product product = productService.getProductDetail(thisProductKey, serviceErrors);
            CategoryType type = getCategoryForProduct(product);
            if (type != null) {
                hierarchy.add(CategoryKey.valueOf(type, product.getShortName()));
            }
            thisProductKey = product.getParentProductKey();
        }
        return hierarchy;
    }

    private CategoryType getCategoryForProduct(Product product) {
        ProductLevel level = product.getProductLevel();
        CategoryType result = null;
        switch (level) {
            case CATEGORY:
                result = CategoryType.PRODUCT;
                break;
            case PRIVATE_LABEL:
                result = CategoryType.PRIVATE_LABEL;
                break;
            case WHITE_LABEL:
                result = CategoryType.WHITE_LABEL;
                break;
            case OFFER:
                result = CategoryType.OFFER;
                break;
            default:
                break;
        }
        return result;
    }

}
