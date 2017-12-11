package com.bt.nextgen.service.integration.order;

import java.util.HashMap;
import java.util.Map;

public enum ExpiryMethod {
    GFD("good_for_day", "Good for day"),
    GTC("good_till_canc", "Good till cancelled");

    private String intlId;
    private String displayName;
    private static Map<String, ExpiryMethod> expiryMethodMap;

    private ExpiryMethod(String intlId, String displayName) {
        this.intlId = intlId;
        this.displayName = displayName;
    }

    public String getIntlId() {
        return intlId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ExpiryMethod getExpiryMethod(String intlId) {
        if (expiryMethodMap == null) {
            initMapping();
        }

        return expiryMethodMap.get(intlId);
    }

    private static void initMapping() {
        expiryMethodMap = new HashMap<String, ExpiryMethod>();

        for (ExpiryMethod expiryMethod : values()) {
            expiryMethodMap.put(expiryMethod.intlId, expiryMethod);
        }
    }

    public static ExpiryMethod forIntlId(String intlId) {
        for (ExpiryMethod expiryMethod : ExpiryMethod.values()) {
            if (expiryMethod.intlId.equals(intlId)) {
                return expiryMethod;
            }
        }

        return null;
    }

    public static ExpiryMethod forDisplayName(String displayName) {
        for (ExpiryMethod expiryMethod : ExpiryMethod.values()) {
            if (expiryMethod.getDisplayName().equals(displayName)) {
                return expiryMethod;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return intlId;
    }

    public static ExpiryMethod forName(String expiryType) {
        for (ExpiryMethod expiryMethod : ExpiryMethod.values()) {
            if (expiryMethod.name().equals(expiryType)) {
                return expiryMethod;
            }
        }
        return null;
    }
}