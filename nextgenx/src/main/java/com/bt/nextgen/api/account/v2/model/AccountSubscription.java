package com.bt.nextgen.api.account.v2.model;

@Deprecated
public enum AccountSubscription {

    ACTIVE("active", "DIRE.BTPI.ACTIVE", 2),
    SIMPLE("simple", "DIRE.BTPI.SIMPLE", 1),
    UNDECIDED("undecided", "", 0);

    private final String subscriptionType;
    private final String subscriptionProduct;
    private final int priority;

    AccountSubscription(String subscriptionType, String product, int priority) {
        this.subscriptionType = subscriptionType;
        this.subscriptionProduct = product;
        this.priority = priority;
    }

    public String getSubscriptionProduct() {
        return subscriptionProduct;
    }

    public int getPriority() {
        return priority;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public static AccountSubscription forProduct(String subscription) {
        for (AccountSubscription accountSubscription : AccountSubscription.values()) {
            if (accountSubscription.getSubscriptionProduct().equals(subscription)) {
                return accountSubscription;
            }
        }
        return UNDECIDED;
    }

    public static AccountSubscription forType(String type) {
        for (AccountSubscription accountSubscription : AccountSubscription.values()) {
            if (accountSubscription.getSubscriptionType().equals(type)) {
                return accountSubscription;
            }
        }
        return UNDECIDED;
    }
}
