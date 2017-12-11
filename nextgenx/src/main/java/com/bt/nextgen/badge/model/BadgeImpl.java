package com.bt.nextgen.badge.model;

public class BadgeImpl implements Badge {
    private final String badgeName;
    private final String logo;
    private final String reportLogo;
    private final String reportLogoV2;

    public BadgeImpl(String badgeName, String logo, String reportLogo, String reportLogoV2) {
        super();
        this.badgeName = badgeName;
        this.logo = logo;
        this.reportLogo = reportLogo;
        this.reportLogoV2 = reportLogoV2;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public String getLogo() {
        return logo;
    }

    public String getReportLogo() {
        return reportLogo;
    }

    public String getReportLogoV2() {
        return reportLogoV2;
    }

}
