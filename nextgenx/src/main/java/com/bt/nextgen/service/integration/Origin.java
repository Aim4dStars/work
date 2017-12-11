package com.bt.nextgen.service.integration;

import java.util.HashMap;
import java.util.Map;

public enum Origin {
    FAX("fax", "Web UI"),
    WEB_UI("btfg$web_ui", "Web UI"),
    BACK_OFFICE("btfg$bo", "Back Office"),
    BACK_OFFICE_DIR("btfg$bo_dir", "Back office direct"),
    REGULAR_INVESTMENT("btfg$rip", "RIP"),
    WEB_UI_RIP("btfg$ui_rip", "Web UI RIP"),
    WEB_UI_RWP("btfg$ui_rwp", "Web UI RWP"),
    DRAWDOWN("btfg$dd", "Drawdown"),
    PANEL_BROKER("btfg$pnl_broker", "Panel broker", true),
    IPO("btfg$ipo", "IPO", true),
    WEB_UI_CASH_SWP("btfg$ui_cashswp", "Auto invest");

    private String code;
    private String name;
    private boolean external;
    private static Map<String, Origin> originMap;

    private Origin(String code, String name, boolean external) {
        this.code = code;
        this.name = name;
        this.external = external;
    }

    private Origin(String code, String name) {
        this(code, name, false);
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public boolean isExternal() {
        return external;
    }

    public static Map<String, Origin> getOriginMap() {
        return originMap;
    }

    public static Origin getOrigin(String code) {
        if (originMap == null) {
            initMapping();
        }

        return originMap.get(code);
    }

    private static void initMapping() {
        originMap = new HashMap<String, Origin>();

        for (Origin origin : values()) {
            originMap.put(origin.code, origin);
        }
    }

    public static Origin forCode(String code) {
        for (Origin origin : Origin.values()) {
            if (origin.code.equals(code)) {
                return origin;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return code;
    }
}