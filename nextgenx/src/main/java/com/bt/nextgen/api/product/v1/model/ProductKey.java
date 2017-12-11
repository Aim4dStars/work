package com.bt.nextgen.api.product.v1.model;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class ProductKey {
    private String productId;

    public ProductKey() {
        // No-arg constructor
    }

    public ProductKey(final String productId) {
        super();
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((productId == null) ? 0 : productId.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }
}
