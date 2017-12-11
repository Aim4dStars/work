package com.bt.nextgen.service.integration.drawdown;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public enum DrawdownOption {

    PRORATA("prorata", "Pro-rata to all holdings"),
    HIGH_PRICE("asset_with_highest_price", "Asset with highest value (default)");

    private String intlId;
    private String displayName;
    private static Map<String, DrawdownOption> drawdownOptionMap;

    private DrawdownOption(String intlId, String displayName) {
        this.intlId = intlId;
        this.displayName = displayName;
    }

    public String getIntlId() {
        return intlId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static DrawdownOption getDistributionMethod(String intlId) {
        if (drawdownOptionMap == null) {
            initMapping();
        }

        return drawdownOptionMap.get(intlId);
    }

    private static void initMapping() {
        drawdownOptionMap = new HashMap<String, DrawdownOption>();

        for (DrawdownOption distMethod : values()) {
            drawdownOptionMap.put(distMethod.intlId, distMethod);
        }
    }

    public static DrawdownOption forIntlId(String intlId) {
        for (DrawdownOption distMethod : DrawdownOption.values()) {
            if (distMethod.intlId.equals(intlId)) {
                return distMethod;
            }
        }

        return null;
    }

    public static DrawdownOption forDisplayName(String displayName) {
        for (DrawdownOption distMethod : DrawdownOption.values()) {
            if (distMethod.getDisplayName().equals(displayName)) {
                return distMethod;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return intlId;
    }
}
