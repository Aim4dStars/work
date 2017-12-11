package com.bt.nextgen.service.integration.base;

/**
 * Created by M044576 on 3/10/2017.
 */
public enum MigrationAttribute {

    WRAP_CONTRIBUTION_HISTORY("WrapContributionHistory");

    private String name;

    MigrationAttribute(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
