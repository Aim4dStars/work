package com.btfin.panorama.service.integration.product;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductKey;

import java.util.Map;

/**
 * Created by L070589 on 31/03/2016.
 */
public interface ProductDetailService {


    /**
     *
     * @param serviceErrors
     * @return
     */
    Map<ProductKey, Product> loadProductsMap(ServiceErrors serviceErrors);



    /**
     * Returns product based on the product id
     * @param productId
     * @param serviceErrors
     * @return Product
     */
    Product getProductDetail(ProductKey productId, ServiceErrors serviceErrors);
}
