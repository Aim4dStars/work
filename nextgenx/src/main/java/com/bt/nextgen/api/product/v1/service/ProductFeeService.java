package com.bt.nextgen.api.product.v1.service;

import com.bt.nextgen.api.product.v1.model.ProductFeeDto;
import com.bt.nextgen.service.ServiceErrors;

public interface ProductFeeService {

    public ProductFeeDto findProductFee(String adviserPositionId, String productId, ServiceErrors serviceErrors);
}
