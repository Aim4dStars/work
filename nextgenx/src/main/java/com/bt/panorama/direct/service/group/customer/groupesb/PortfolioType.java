package com.bt.panorama.direct.service.group.customer.groupesb;

/**
 * Created by L072457 on 10/07/2015.
 */
public enum PortfolioType {

    CONSERVATIVE("Conservative", "Conservative"),
    DEFENSIVE("Defensive", "Defensive"),
    MODERATE("Moderate", "Moderate"),
    BALANCED("Balanced", "Balanced"),
    GROWTH("Growth", "Growth"),
    HIGH_GROWTH("High Growth", "High Growth"),
    UNKNOWN("Unknown", "");

    private String description;
    private String code;

    private PortfolioType(String description, String code)
    {
        this.description = description;
        this.code = code;
    }

    public String getDescription()
    {
        return description;
    }

    public String getCode()
    {
        return code;
    }

    public static PortfolioType forCode(String code)
    {
        for (PortfolioType type : PortfolioType.values())
        {
            if (type.getCode().equals(code))
            {
                return type;
            }
        }
        return PortfolioType.UNKNOWN;
    }
}
