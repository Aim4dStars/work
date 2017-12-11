package com.bt.nextgen.service.integration.transactionfee;

import java.util.HashMap;
import java.util.Map;

public enum ExecutionType {
    DIRECT_MARKET_ACCESS("dma", "Direct market access"),
    PRICE_INLINE("price", "Price inline"),
    TIME_WEIGHTED_AVERAGE_PRICE("twap", "Time-weighted average price"),
    VOLUME_WEIGHTED_AVERAGE_PRICE("vwap", "Volume-weighted average price"),
    WORKED_ORDER("worked", "Worked order");

    private String intlId;
    private String displayName;
    private static Map<String, ExecutionType> intlIdMap;
    private static Map<String, ExecutionType> displayNameMap;

    static {
        intlIdMap = new HashMap<>();
        displayNameMap = new HashMap<>();
        for (ExecutionType executionType : values()) {
            intlIdMap.put(executionType.intlId, executionType);
            displayNameMap.put(executionType.displayName, executionType);
        }
    }

    private ExecutionType(String intlId, String displayName) {
        this.intlId = intlId;
        this.displayName = displayName;
    }

    public static ExecutionType forIntlId(String internalId) {
        return intlIdMap.get(internalId);
    }

    public static ExecutionType forDisplayName(String displayName) {
        return displayNameMap.get(displayName);
    }

    @Override
    public String toString() {
        return intlId;
    }

    public String getIntlId() {
        return intlId;
    }

    public String getDisplayName() {
        return displayName;
    }
}
