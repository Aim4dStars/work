package com.bt.nextgen.service.integration.movemoney;

import java.util.HashMap;
import java.util.Map;

public enum OrderType {
    SUPER_ONE_OFF_CONTRIBUTION("inpay#super_contri", "oneoff", "Super Contribution"),
    SUPER_RECURRING_CONTRIBUTION("lsv_at#stord_new_sa_contri_in", "recurring", "Super Regular Contribution");

    private String intlId;
    private String name;
    private String displayName;
    private static Map<String, OrderType> orderTypeMap;

    private OrderType(String intlId, String name, String displayName) {
        this.intlId = intlId;
        this.name = name;
        this.displayName = displayName;
    }

    public String getIntlId() {
        return intlId;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static OrderType getOrderType(String intlId) {
        if (orderTypeMap == null) {
            initMapping();
        }

        return orderTypeMap.get(intlId);
    }

    private static void initMapping() {
        orderTypeMap = new HashMap<String, OrderType>();

        for (OrderType orderType : values()) {
            orderTypeMap.put(orderType.intlId, orderType);
        }
    }

    @Override
    public String toString() {
        return intlId;
    }
}