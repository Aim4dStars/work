package com.bt.nextgen.api.product.v1.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.product.v1.model.ProductCategory;
import com.bt.nextgen.api.product.v1.model.ProductDto;
import com.bt.nextgen.api.product.v1.model.ProductKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.integration.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("ProductDtoConverterV1")
public class ProductDtoConverter implements Converter<Product, ProductDto> {

    @Autowired
    private ProductFeeComponentDtoConverter feeComponentDtoConverter;

    @Override
    public ProductDto convert(final Product product) {
        final ProductDto productDto = new ProductDto();
        final ProductKey productKey = new ProductKey(EncodedString.fromPlainText(product.getProductKey().getId()).toString());
        productDto.setKey(productKey);
        productDto.setProductType(product.getProductType());
        productDto.setActive(product.isActive());
        productDto.setProductName(product.getProductName());
        productDto.setParentProductName(product.getParentProductName());
        productDto.setProductLevel(product.getProductLevel() != null ? product.getProductLevel().name() : "");
        productDto.setParentProduct(product.getParentProduct());
        if (product.getParentProductKey() != null) {
            productDto.setParentProductKey(
                    new ProductKey(EncodedString.fromPlainText(product.getParentProductKey().getId()).toString()));
        }
        productDto.setShortName(product.getShortName());
        productDto.setMinIntialInvestment(product.getMinIntialInvestment());
        productDto.setMinContribution(product.getMinContribution());
        productDto.setMinWithdrwal(product.getMinWithdrwal());
        productDto.setFeeComponents(Lambda.convert(product.getFeeComponents(), feeComponentDtoConverter));
        productDto.setProductCategory(product.isSuper() ? ProductCategory.SUPER : ProductCategory.STANDARD);

        return productDto;
    }
}
