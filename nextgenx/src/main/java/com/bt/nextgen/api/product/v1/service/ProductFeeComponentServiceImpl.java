package com.bt.nextgen.api.product.v1.service;

import com.bt.nextgen.api.product.v1.model.ProductFeeComponentDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductFeeComponent;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static ch.lambdaj.Lambda.convert;

@Service("ProductFeeComponentServiceV1")
public class ProductFeeComponentServiceImpl implements ProductFeeComponentService {

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private ProductFeeComponentDtoConverter feeComponentDtoConverter;

    @Override
    public List<ProductFeeComponentDto> getProductFeeComponents(final Product product, final ServiceErrors serviceErrors) {
        final List<ProductFeeComponent> feeComponents = getFeeComponent(product, serviceErrors);
        return convert(feeComponents, feeComponentDtoConverter);
    }

    private List<ProductFeeComponent> getFeeComponent(final Product product, final ServiceErrors serviceErrors) {
        return productIntegrationService.getProductFeeComponents(product, serviceErrors);
    }
}
