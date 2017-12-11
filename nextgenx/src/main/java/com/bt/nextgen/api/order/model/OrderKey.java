package com.bt.nextgen.api.order.model;

import com.bt.nextgen.config.JsonViews;
import com.fasterxml.jackson.annotation.JsonView;

public class OrderKey implements Comparable<OrderKey> {
    @JsonView(JsonViews.Write.class)
    private String orderId;

    public OrderKey() {
        super();
    }

    public OrderKey(String orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId cannot be null");
        }
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orderId == null) ? 0 : orderId.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("squid:S1142")
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OrderKey other = (OrderKey) obj;
        if (orderId == null) {
            if (other.orderId != null)
                return false;
        } else if (!orderId.equals(other.orderId))
            return false;
        return true;
    }

    @Override
    public int compareTo(OrderKey o) {
        return orderId.compareTo(o.orderId);
    }
}
