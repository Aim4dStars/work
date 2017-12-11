package com.bt.nextgen.service.integration.rollover;

import java.util.HashMap;
import java.util.Map;

public enum RolloverContributionStatus {
    IN_PROGRESS("btfg$in_progress", "In progress"),
    RECEIVED("btfg$rcvd", "Received");

    private String code;
    private String displayName;
    private static Map<String, RolloverContributionStatus> contributionStatusMap;

    private RolloverContributionStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RolloverContributionStatus getRolloverContributionStatus(String code) {
        if (contributionStatusMap == null) {
            initMapping();
        }

        return contributionStatusMap.get(code);
    }

    private static void initMapping() {
        contributionStatusMap = new HashMap<String, RolloverContributionStatus>();

        for (RolloverContributionStatus option : values()) {
            contributionStatusMap.put(option.code, option);
        }
    }

    public static RolloverContributionStatus forCode(String code) {
        for (RolloverContributionStatus option : RolloverContributionStatus.values()) {
            if (option.code.equals(code)) {
                return option;
            }
        }
        return null;
    }

    public static RolloverContributionStatus forDisplay(String display) {
        for (RolloverContributionStatus option : RolloverContributionStatus.values()) {
            if (option.displayName.equals(display)) {
                return option;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code;
    }
}