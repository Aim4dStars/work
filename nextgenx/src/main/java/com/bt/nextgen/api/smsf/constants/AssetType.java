package com.bt.nextgen.api.smsf.constants;


import java.util.HashMap;
import java.util.Map;

/**
 * @deprecated merged with service.integration.asset.AssetClass
 */
@SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.whitespace.NoLineWrapCheck")
@Deprecated
public enum AssetType {
    CASH("Cash", "cash", 1),
    TERM_DEPOSIT("Term Deposit", "td", 2),
    AUSTRALIAN_LISTED_SECURITIES("Listed security", "ls", 3),
    INTERNATIONAL_LISTED_SECURITIES("International listed security", "ils", 4),
    MANAGED_FUND("Managed fund", "mf", 5),
    MANAGED_PORTFOLIO("Managed portfolio", "mp", 6),
    DIRECT_PROPERTY("Direct Property", "dp", 7),
    OTHER_ASSET("Other", "oth", 8);


    private String description = "";
    private String code = "";
    private int order = 999;
    private static final Map<String, AssetType> lookup = new HashMap<>();

    static {
        for (AssetType type : AssetType.values()) {
            lookup.put(type.getCode(), type);
        }
    }


    AssetType(String description, String code, int order) {
        this.description = description;
        this.code = code;
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public String getDescription() {
        return this.description;
    }

    public String getCode() {
        return code;
    }

    public static AssetType getByCode(String code) {
        return lookup.get(code);
    }


    @Override
    public String toString() {
        return code;
    }
}
