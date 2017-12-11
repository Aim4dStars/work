package com.bt.nextgen.api.product.service;

import com.bt.nextgen.api.product.model.ProductFeeComponentDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductFeeComponent;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static ch.lambdaj.Lambda.convert;

@Deprecated
@Service
public class ProductFeeComponentServiceImpl implements ProductFeeComponentService {

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private ProductFeeComponentDtoConverter feeComponentDtoConverter;

    @Override
    public List<ProductFeeComponentDto> getProductFeeComponents(Product product, ServiceErrors serviceErrors) {
        List<ProductFeeComponent> feeComponents = getFeeComponent(product, serviceErrors);
        return convert(feeComponents, feeComponentDtoConverter);
    }

    private List<ProductFeeComponent> getFeeComponent(Product product, ServiceErrors serviceErrors) {
        return productIntegrationService.getProductFeeComponents(product, serviceErrors);
    }
}
