package com.bt.nextgen.api.allocation.model;

/**
 * @deprecated use account.v2.mode.allocation
 */
@Deprecated
public enum AssetSectorEnum
{
    CASH("Cash", "cash", 1), 
    AUSTRALIAN_FIXED_INTEREST("Australian Fixed Interest", "fi_au", 2),
    INTERNATIONAL_FIXED_INTEREST("International Fixed Interest", "fi_intnl", 3),
    DIVERSIFIED_FIXED_INTEREST("Diversified fixed interest", "dived_fi", 4),    
    AUSTRALIAN_SHARES("Australian Shares", "eq_au", 5), 
    INTERNATIONAL_SHARES("International Shares", "eq_intnl", 6),
    AUSTRALIAN_PROPERTY("Australian Property", "realest_au", 7),
    INTERNATIONAL_PROPERTY("International Property", "realest_intnl", 8),
    INFRASTRUCTURE("Infrastructure", "infra", 9),
    ALTERNATIVES("Alternatives", "alt_invst", 10),
    DIVERSIFIED("Diversified", "dived", 11),
    OTHER("Other", "oth_invst", 12);
    
    // TODO - Should these be pulled from the avaloq code interface?
    private final String desc;
    private final String code;
    private int sortOrder;

    AssetSectorEnum(String desc, String code, int sortOrder) {
        this.desc = desc;
        this.code = code;
        this.sortOrder = sortOrder;
    }

    public String getDesc() {
        return desc;
    }

    public String getCode() {
        return code;
    }

    public static AssetSectorEnum fromDesc(String desc) {
        for (AssetSectorEnum assetSector : AssetSectorEnum.values()) {
            if (assetSector.desc.equalsIgnoreCase(desc)) {
                return assetSector;
            }
        }
        return AssetSectorEnum.OTHER;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}