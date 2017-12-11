package com.bt.nextgen.api.product.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;

public class BrokerProductKey extends BaseDto {
    /**
     * The dealer group broker id.
     */
    private String brokerId;

    /**
     * The white label product id.
     */
    private String productId;

    /**
     * Constructor for the key
     *
     * @param brokerId  - The dealer group broker id.
     * @param productId - The white label product id.
     */
    public BrokerProductKey(String brokerId, String productId) {
        this.brokerId = brokerId;
        this.productId = productId;
    }

    public String getBrokerId() {
        return brokerId;
    }

    public String getProductId() {
        return productId;
    }
}
