package com.bt.nextgen.service.integration.cashcategorisation.model;

/**
 * Classification of an smsf contribution: Concessional or Non-Concessional. <p>
 * Corresponds to the avaloq code category <code>btfg$conc_type</code>
 */
public enum ContributionClassification {
    CONCESSIONAL("conc", "concessional", 1),
    NON_CONCESSIONAL("nconc", "non-concessional", 2),
    OTHER("na", "other", 3);

    private String avaloqInternalId;
    private String name;
    private int order;


    public static ContributionClassification forAvaloqInternalId(final String id) {
        for (ContributionClassification classification : ContributionClassification.values()) {
            if (classification.getAvaloqInternalId().equalsIgnoreCase(id)) {
                return classification;
            }
        }

        return null;
    }

    public static ContributionClassification forName(final String name) {
        for (ContributionClassification classification : ContributionClassification.values()) {
            if (classification.getName().equalsIgnoreCase(name)) {
                return classification;
            }
        }
        return null;
    }

    ContributionClassification(String avaloqInternalId, String name, int order) {
        this.avaloqInternalId = avaloqInternalId;
        this.name = name;
        this.order = order;
    }

    public String getAvaloqInternalId() {
        return avaloqInternalId;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getAvaloqInternalId();
    }
}