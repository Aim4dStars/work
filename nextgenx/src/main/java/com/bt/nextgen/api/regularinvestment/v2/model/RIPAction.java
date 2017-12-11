package com.bt.nextgen.api.regularinvestment.v2.model;

/**
 * Action that can be carried out on an existing regular investment via the front end. Status of the RIP can be modified by
 * Cancelling, Resuming and Suspending. Details of the RIP can be modified via the EDIT action.
 * 
 * @author m028796
 * 
 */
public enum RIPAction {
    CANCELLED("cancel"),
    RESUME("activate"),
    SUSPEND("suspend"),
    EDIT("edit");

    private String action;

    private RIPAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public static RIPAction getRIPAction(String action) {
        for (RIPAction ripAction : RIPAction.values()) {
            if (ripAction.action.equals(action)) {
                return ripAction;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return action;
    }
}
