package com.bt.nextgen.reports.account.allocation.sector;

public enum AllocationGroupType {
    INDUSTRY_SUB_SECTOR("industrySubSector"),
    ASSET_SUB_CLASS("assetSubclass");

    private String code = null;

    AllocationGroupType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static AllocationGroupType forCode(String code) {
        for (AllocationGroupType allocationGroupType : AllocationGroupType.values()) {
            if (allocationGroupType.getCode().equals(code)) {
                return allocationGroupType;
            }
        }
        return null;
    }
}
