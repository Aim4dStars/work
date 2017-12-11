package com.bt.nextgen.api.account.v3.model;

/**
 * Enumeration for Direct Product Offers (SIMPLE/ACTIVE/UNDECIDED)
 */
public enum DirectOffer {

    ACTIVE("active", "DIRE.BTPI.ACTIVE", 2),
    SIMPLE("simple", "DIRE.BTPI.SIMPLE", 1),
    UNDECIDED("undecided", "", 0);

    private final String subscriptionType;
    private final String subscriptionProduct;
    private final int priority;

    /**
     * Constructs the Direct Offer based on following inputs:
     *
     * @param subscriptionType - subscription type name
     * @param product          - Direct offer short name
     * @param priority         - priority of the offer if account has multiples
     */
    DirectOffer(String subscriptionType, String product, int priority) {
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

    /**
     * @param productShortName - Short name of the direct offer
     * @return - The Offer based on the input product short name
     */
    public static DirectOffer forProduct(String productShortName) {
        for (DirectOffer directOffer : DirectOffer.values()) {
            if (directOffer.getSubscriptionProduct().equals(productShortName)) {
                return directOffer;
            }
        }
        return UNDECIDED;
    }

    /**
     * @param subscriptionType - Subscription type (simple/active/undecided)
     * @return - The Offer based on the input subscription type
     */
    public static DirectOffer forType(String subscriptionType) {
        for (DirectOffer directOffer : DirectOffer.values()) {
            if (directOffer.getSubscriptionType().equals(subscriptionType)) {
                return directOffer;
            }
        }
        return UNDECIDED;
    }
}
