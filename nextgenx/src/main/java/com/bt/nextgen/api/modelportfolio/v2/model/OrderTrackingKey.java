package com.bt.nextgen.api.modelportfolio.v2.model;

public class OrderTrackingKey implements Comparable<OrderTrackingKey> {
    private String ipsOrderId;
    private String modelOrderId;

    public OrderTrackingKey() {
        super();
    }

    public OrderTrackingKey(String ipsOrderId, String modelOrderId) {
        this.ipsOrderId = ipsOrderId;
        this.modelOrderId = modelOrderId;
    }

    public String getIpsOrderId() {
        return ipsOrderId;
    }

    public String getModelOrderId() {
        return modelOrderId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ipsOrderId == null) ? 0 : ipsOrderId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        OrderTrackingKey other = (OrderTrackingKey) obj;
        if ((ipsOrderId == null && other.ipsOrderId != null) || (modelOrderId == null && other.modelOrderId != null)) {
            return false;
        }

        return ipsOrderId.equals(other.ipsOrderId) && modelOrderId.equals(other.modelOrderId);
    }

    @Override
    public int compareTo(OrderTrackingKey o) {
        return ipsOrderId.compareTo(o.ipsOrderId);
    }
}
