package com.bt.nextgen.service.integration.movemoney;

import java.util.HashMap;
import java.util.Map;

public enum IndexationType {

    NONE("none", "None"),
    CPI("cpi", "Cpi"),
    PERCENTAGE("fixed_pct", "Percentage"),
    DOLLAR("fixed_amount", "Dollar");

    private String intlId;
    private String label;

    IndexationType(String intlId, String label) {
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
    private static Map<String, IndexationType> labelIndexationType = new HashMap<>();
    private static Map<String, IndexationType> intlIdIndexationType = new HashMap<>();
    static {
        for (IndexationType indexationType : IndexationType.values()) {
            labelIndexationType.put(indexationType.label.toLowerCase(), indexationType);
            intlIdIndexationType.put(indexationType.intlId, indexationType);
        }
    }

    /**
     * Returns the IndexationType that pertains to the label.
     * 
     * The lookup is case-insensitive.
     * 
     * @param label
     *            to search with
     * @return the IndexationType that matches the label or null.
     */
    public static IndexationType fromLabel(String label) {
        return label == null ? null : labelIndexationType.get(label.toLowerCase());
    }

    /**
     * Returns the IndexationType that pertains to the intlId.
     * 
     * @param intlId
     *            to search with
     * @return the IndexationType that matches the intlId or null.
     */
    public static IndexationType fromIntlId(String intlId) {
        return intlId == null ? null : intlIdIndexationType.get(intlId);
    }

    @Override
    public String toString() {
        return intlId;
    }
}
