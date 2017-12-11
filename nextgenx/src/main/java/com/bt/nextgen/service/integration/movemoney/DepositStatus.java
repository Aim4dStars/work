package com.bt.nextgen.service.integration.movemoney;

import java.util.HashMap;
import java.util.Map;

public enum DepositStatus {
    NOT_SUBMITTED("nsubm", "Not submitted"),
    ACTIVE("activ", "Active");

    private String code;
    private String displayName;
    private static Map<String, DepositStatus> depositStatusMap;

    private DepositStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static DepositStatus getDepositStatus(String code) {
        if (depositStatusMap == null) {
            initMapping();
        }

        return depositStatusMap.get(code);
    }

    private static void initMapping() {
        depositStatusMap = new HashMap<String, DepositStatus>();

        for (DepositStatus depositStatus : values()) {
            depositStatusMap.put(depositStatus.code, depositStatus);
        }
    }

    @Override
    public String toString() {
        return code;
    }
}
