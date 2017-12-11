package com.bt.nextgen.service.integration.order;

import java.util.HashMap;
import java.util.Map;

public enum PriceType {
    MARKET("mkt", "Market"), LIMIT("lim", "Limit");

    private String intlId;
    private String displayName;
    private static Map<String, PriceType> priceTypeMap;

    private PriceType(String intlId, String displayName) {
        this.intlId = intlId;
        this.displayName = displayName;
    }

    public String getIntlId() {
        return intlId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PriceType getPriceType(String intlId) {
        if (priceTypeMap == null) {
            initMapping();
        }

        return priceTypeMap.get(intlId);
    }

    private static void initMapping() {
        priceTypeMap = new HashMap<String, PriceType>();

        for (PriceType priceType : values()) {
            priceTypeMap.put(priceType.intlId, priceType);
        }
    }

    public static PriceType forIntlId(String intlId) {
        for (PriceType priceType : PriceType.values()) {
            if (priceType.intlId.equals(intlId)) {
                return priceType;
            }
        }

        return null;
    }

    public static PriceType forDisplayName(String displayName) {
        for (PriceType priceType : PriceType.values()) {
            if (priceType.getDisplayName().equals(displayName)) {
                return priceType;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return intlId;
    }
}