package com.bt.nextgen.api.uar.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by l081361 on 26/07/2016.
 */
public enum UarPermissions {
    CAN_TRAN("1", "Can transact"),
    CAN_UPDATE("2", "Can update"),
    CAN_NOT_TRAN("3", "Cannot transact cash"),
    READ_ONLY("4", "Read only");

    private String label;

    UarPermissions(String code, String label) {
        this.code = code;
        this.label = label;
    }

    private static Map<String, UarPermissions> uarPermissionsMap;

    public static UarPermissions getUarPermissions(String label) {
        if (uarPermissionsMap == null) {
            initMapping();
        }
        return uarPermissionsMap.get(label);
    }

    private static void initMapping() {
        uarPermissionsMap = new HashMap<>();

        for (UarPermissions uarPermissions : values()) {
            uarPermissionsMap.put(uarPermissions.label, uarPermissions);

        }
    }

    public String getCode() {
        return code;
    }

    private String code;

    @Override
    public String toString() {
        return code;
    }

    public String getLabel() {
        return label;
    }
}
