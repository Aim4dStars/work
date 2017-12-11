package com.bt.nextgen.service.integration.modelportfolio.common;

// Static code from btfg$ips_status
public enum IpsStatus {
    CLOSED_TO_NEW("closed_to_new", "Closed to New"),
    NEW("new", "New"),
    OPEN("opn", "Open"),
    PENDING("pend", "Pending"),
    SUSPENDED("susp", "Suspended"),
    TERMINATED("ter", "Terminated");
    
    private String intlId;
    private String name;

    private IpsStatus(String intlId, String name) {
        this.intlId = intlId;
        this.name = name;
    }

    public String toString() {
        return intlId;
    }

    public static IpsStatus forIntlId(String intlId) {
        for (IpsStatus status : values()) {
            if (status.intlId.equals(intlId)) {
                return status;
            }
        }
        return null;
    }

    public static IpsStatus forName(String name) {
        for (IpsStatus status : values()) {
            if (status.name().equalsIgnoreCase(name)) {
                return status;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }
}
