package com.bt.nextgen.service.avaloq.modelportfolio;

public enum RebalanceAction {
    SCAN("scan"),
    DISCARD("discard"),
    RECALCULATE("adhoc_ips"),
    SUBMIT("submit");

    private String code;

    private RebalanceAction(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static RebalanceAction forCode(String code) {
        for(RebalanceAction action:values()) {
            if(action.code.equals(code)) {
                return action;
            }
        }
        return null;
    }

}
