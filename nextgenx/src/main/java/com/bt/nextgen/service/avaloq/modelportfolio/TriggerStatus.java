package com.bt.nextgen.service.avaloq.modelportfolio;

import java.util.HashMap;
import java.util.Map;

public enum TriggerStatus {

    NEW("New", "btfg$rdy"),
    PROCESSING("In progress", "btfg$prc"),
    ORDERS_READY("Orders ready", "btfg$compl");

    private String description;
    private String code;

    TriggerStatus(String description, String code) {
        this.description = description;
        this.code = code;
    }

    private static final Map<String, TriggerStatus> lookup = new HashMap<>();

    static {
        for (TriggerStatus triggerStatus : TriggerStatus.values()) {
            lookup.put(triggerStatus.code, triggerStatus);
        }
    }

    public static TriggerStatus getByCode(String code) {
        return lookup.get(code);
    }

    @Override
    public String toString() {
        return code;
    }
    
    public String getDescription(){
        return description;
    }

}
