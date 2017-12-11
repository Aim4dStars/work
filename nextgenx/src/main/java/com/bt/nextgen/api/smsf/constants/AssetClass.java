package com.bt.nextgen.api.smsf.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Representation of asset classes.</p>
 * Static code category <code>btfg$asset_class_grp</code>
 *
 * @deprecated merged with service.integration.asset.AssetClass
 */
@SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.whitespace.NoLineWrapCheck")
@Deprecated
public enum AssetClass {
    CASH("Cash", "cash", 1),
    AUSTRALIAN_FIXED_INTEREST("Australian Fixed Interest", "fi_au", 2),
    INTERNATIONAL_FIXED_INTEREST("International Fixed Interest", "fi_intnl", 3),
    AUSTRALIAN_FLOATING_RATE_INTEREST("Australian Floating Rate Interest", "fri_au", 4),
    AUSTRALIAN_LISTED_SECURITIES("Australian Shares", "eq_au", 5),
    INTERNATIONAL_LISTED_SECURITIES("International Shares", "eq_intnl", 6),
    AUSTRALIAN_REAL_ESTATE("Australian Real Estate", "realest_au", 7),
    INTERNATIONAL_REAL_ESTATE("International Real Estate", "realest_intnl", 8),
    ALTERNATIVES("Alternatives", "alt_invst", 9),
    DIVERSIFIED("Diversified", "dived", 10),
    OTHER_ASSET("Other", "oth_invst", 11);


    private static final Map<String, AssetClass> lookup = new HashMap<>();

    static {
        for (AssetClass assetClass : AssetClass.values()) {
            lookup.put(assetClass.getCode(), assetClass);
        }
    }

    private String description = "";
    private String code = "";
    private int order = 999;


    AssetClass(String description, String code, int order) {
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

    public static AssetClass getByCode(String code) {
        return lookup.get(code);
    }

    @Override
    public String toString() {
        return code;
    }
}
