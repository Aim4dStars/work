package com.bt.nextgen.api.order.model;

import com.bt.nextgen.config.JsonViews;
import com.fasterxml.jackson.annotation.JsonView;

public class OrderGroupKey implements Comparable<OrderGroupKey> {
    @JsonView(JsonViews.Write.class)
    private String orderGroupId;

    @JsonView(JsonViews.Write.class)
    private String accountId;

    public OrderGroupKey() {
        super();
    }

    public OrderGroupKey(String accountId, String orderGroupId) {
        if (accountId == null) {
            throw new IllegalArgumentException("accountId cannot be null");
        }

        if (orderGroupId == null) {
            throw new IllegalArgumentException("orderGroupId cannot be null");
        }
        this.orderGroupId = orderGroupId;
        this.accountId = accountId;
    }

    public String getOrderGroupId() {
        return orderGroupId;
    }

    public void setOrderGroupId(String orderGroupId) {
        this.orderGroupId = orderGroupId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
        result = prime * result + ((orderGroupId == null) ? 0 : orderGroupId.hashCode());
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
        OrderGroupKey other = (OrderGroupKey) obj;
        if (accountId == null) {
            if (other.accountId != null)
                return false;
        } else if (!accountId.equals(other.accountId))
            return false;
        if (orderGroupId == null) {
            if (other.orderGroupId != null)
                return false;
        } else if (!orderGroupId.equals(other.orderGroupId))
            return false;
        return true;
    }

    @Override
    public int compareTo(OrderGroupKey o) {
        return orderGroupId.compareTo(o.orderGroupId);
    }
}
