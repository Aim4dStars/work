package com.bt.nextgen.service.integration.movemoney;

import java.util.HashMap;
import java.util.Map;

public enum ContributionType {
    SPOUSE("spouse", "spouse", "Spouse"),
    PERSONAL("prsnl_sav_nclaim", "personal", "Personal");

    private String intlId;
    private String name;
    private String displayName;
    private static Map<String, ContributionType> contributionTypeMap;

    private ContributionType(String intlId, String name, String displayName) {
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

    public static ContributionType getContributionType(String intlId) {
        if (contributionTypeMap == null) {
            initMapping();
        }

        return contributionTypeMap.get(intlId);
    }

    private static void initMapping() {
        contributionTypeMap = new HashMap<String, ContributionType>();

        for (ContributionType contributionType : values()) {
            contributionTypeMap.put(contributionType.intlId, contributionType);
        }
    }

    public static ContributionType forIntlId(String intlId) {
        for (ContributionType contributionType : ContributionType.values()) {
            if (contributionType.intlId.equals(intlId)) {
                return contributionType;
            }
        }

        return null;
    }

    public static ContributionType forName(String name) {
        for (ContributionType contributionType : ContributionType.values()) {
            if (contributionType.getName().equals(name)) {
                return contributionType;
            }
        }

        return null;
    }

    public static ContributionType forDisplayName(String displayName) {
        for (ContributionType contributionType : ContributionType.values()) {
            if (contributionType.getDisplayName().equals(displayName)) {
                return contributionType;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return intlId;
    }
}