package com.bt.nextgen.api.product.v1.service;

import com.bt.nextgen.api.product.v1.model.ProductFeeComponentDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.product.Product;

import java.util.List;

public interface ProductFeeComponentService {

    List<ProductFeeComponentDto> getProductFeeComponents(Product product, ServiceErrors serviceErrors);
}
