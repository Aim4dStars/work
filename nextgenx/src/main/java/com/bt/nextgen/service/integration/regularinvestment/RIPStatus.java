package com.bt.nextgen.service.integration.regularinvestment;

import java.util.HashMap;
import java.util.Map;

public enum RIPStatus {
    CANCELLED("cancel", "Cancelled"),
    EXPIRED("expir", "Expired"),
    FAILED("fail", "Failed"),
    IN_PROGRESS("in_progress", "In progress"),
    ACTIVE("activ", "Active"),
    SUSPENDED("susp", "Suspended"),
    COMPLETED("compl", "Completed"),
    COMPLETED_PARTIALLY("compl_part", "Failed"),
    HOLD("hold","Hold"),
    NOT_EXECUTED("not_exec", "Not executed");

    private String code;
    private String displayName;
    private static Map<String, RIPStatus> ripStatusMap;

    private RIPStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RIPStatus getRIPStatus(String code) {
        if (ripStatusMap == null) {
            initMapping();
        }

        return ripStatusMap.get(code);
    }

    private static void initMapping() {
        ripStatusMap = new HashMap<String, RIPStatus>();

        for (RIPStatus ripStatus : values()) {
            ripStatusMap.put(ripStatus.code, ripStatus);
        }
    }

    public static RIPStatus forCode(String code) {
        for (RIPStatus status : RIPStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }

        return null;
    }

    public static RIPStatus forDisplay(String display) {
        for (RIPStatus status : RIPStatus.values()) {
            if (status.displayName.equals(display)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code;
    }
}
