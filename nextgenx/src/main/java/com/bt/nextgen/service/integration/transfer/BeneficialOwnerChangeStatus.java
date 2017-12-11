package com.bt.nextgen.service.integration.transfer;

import java.util.HashMap;
import java.util.Map;

/**
 * Change of beneficial owner status for an in-specie transfer asset.
 * 
 * @author m028796
 * 
 */
public enum BeneficialOwnerChangeStatus {

    NO("no", "No - No Change of Beneficial Owner"), 
    YES("yes", "Yes - Change of Beneficial Owner"), 
    UNKNOWN("unknown", "Unknown - Unable to determine change of Beneficial Owner");

    private String code;
    private String displayName;
    private static Map<String, BeneficialOwnerChangeStatus> statusMap;

    static {
        statusMap = new HashMap<>();
        for (BeneficialOwnerChangeStatus type : values()) {
            statusMap.put(type.code, type);
        }
    }

    public static BeneficialOwnerChangeStatus getStatus(String id) {
        return statusMap.get(id);
    }

    BeneficialOwnerChangeStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public static BeneficialOwnerChangeStatus forCode(String code) {
        for (BeneficialOwnerChangeStatus change : BeneficialOwnerChangeStatus.values()) {
            if (change.code.equals(code)) {
                return change;
            }
        }

        return null;
    }

    public static BeneficialOwnerChangeStatus forDisplay(String display) {
        for (BeneficialOwnerChangeStatus change : BeneficialOwnerChangeStatus.values()) {
            if (change.displayName.equals(display)) {
                return change;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return code;
    }
}
