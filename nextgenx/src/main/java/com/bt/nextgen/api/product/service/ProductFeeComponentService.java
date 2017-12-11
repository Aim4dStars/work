package com.bt.nextgen.api.product.service;

import com.bt.nextgen.api.product.model.ProductFeeComponentDto;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.avaloq.product.FeeType;
import com.bt.nextgen.service.integration.product.Product;

import java.util.List;

@Deprecated
public interface ProductFeeComponentService {

    List<ProductFeeComponentDto> getProductFeeComponents(Product product, ServiceErrors serviceErrors);
}
