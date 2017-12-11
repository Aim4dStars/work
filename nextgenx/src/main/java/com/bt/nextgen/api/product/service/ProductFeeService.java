package com.bt.nextgen.api.product.service;

import com.bt.nextgen.api.product.model.ProductFeeDto;
import com.bt.nextgen.service.ServiceErrors;

@Deprecated
public interface ProductFeeService {

    public ProductFeeDto findProductFee(String adviserPositionId, String productId, ServiceErrors serviceErrors);
}
