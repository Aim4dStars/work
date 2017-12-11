package com.bt.nextgen.service.integration.movemoney;

import java.util.HashMap;
import java.util.Map;

public enum PensionPaymentType {

    MINIMUM_AMOUNT("pens_annu_min", "Minimum amount"),
    MAXIMUM_AMOUNT("pens_annu_max", "Maximum amount"),
    SPECIFIC_AMOUNT("", "Specific amount");

    private String intlId;
    private String label;

    PensionPaymentType(String intlId, String label) {
        this.intlId = intlId;
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public String getIntlId() {
        return intlId;
    }

    /**
     * Case insensitive map to lookup by label.
     */
    private static Map<String, PensionPaymentType> labelPensionPaymentType = new HashMap<>();
    private static Map<String, PensionPaymentType> intlIdPensionPaymentType = new HashMap<>();
    static {
        for (PensionPaymentType pensionPaymentType : PensionPaymentType.values()) {
            labelPensionPaymentType.put(pensionPaymentType.label.toLowerCase(), pensionPaymentType);
            intlIdPensionPaymentType.put(pensionPaymentType.intlId, pensionPaymentType);
        }
    }

    /**
     * Returns the PensionPaymentType that pertains to the label.
     * 
     * The lookup is case-insensitive.
     * 
     * @param label
     *            to search with
     * @return the PensionPaymentType that matches the label or null.
     */
    public static PensionPaymentType fromLabel(String label) {
        return label == null ? null : labelPensionPaymentType.get(label.toLowerCase());
    }

    /**
     * Returns the PensionPaymentType that pertains to the intlId.
     * 
     * @param intlId
     *            to search with
     * @return the PensionPaymentType that matches the intlId or null.
     */
    public static PensionPaymentType fromIntlId(String intlId) {
        return intlId == null ? null : intlIdPensionPaymentType.get(intlId);
    }

    @Override
    public String toString() {
        return intlId;
    }
}
