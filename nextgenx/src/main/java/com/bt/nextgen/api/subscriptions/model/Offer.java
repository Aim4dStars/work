package com.bt.nextgen.api.subscriptions.model;

import com.bt.nextgen.api.subscriptions.service.Subscriptions;
import com.google.common.base.Objects;

/**
 * Bean to represent offer
 */
public class Offer {

    private Subscriptions type;

    public Offer(Subscriptions type) {
        this.type = type;
    }

    public String getName() {
        return type.getName();
    }

    public Subscriptions getType() {
        return type;
    }

    public String workFlowName() {
        return type.getWorkFlowType();
    }

    public String productType() {
        return type.getProductType();
    }

    public String getOderType() {
        return type.getOrderType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Offer))
            return false;
        Offer that = (Offer) o;
        return Objects.equal(this.getType().getName(), that.getType().getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.type.getName());
    }

}