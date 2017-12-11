package com.bt.nextgen.service.group.customer.groupesb;

/**
 * Created by F057654 on 24/07/2015.
 */
public enum RoleType {
    INDIVIDUAL("Individual"),
    ORGANISATION("Organisation");

    private String description;

    private RoleType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

}
