package com.bt.nextgen.service.integration.base;
import com.bt.nextgen.service.integration.base.SystemType;

import org.joda.time.DateTime;

public class ThirdPartyDetails {

    private SystemType systemType;
    private DateTime migrationDate;
    private String migrationKey;

    public SystemType getSystemType() {
        return systemType;
    }

    public void setSystemType(SystemType systemType) {
        this.systemType = systemType;
    }

    public DateTime getMigrationDate() {
        return migrationDate;
    }

    public void setMigrationDate(DateTime migrationDate) {
        this.migrationDate = migrationDate;
    }

    public String getMigrationKey() {
        return migrationKey;
    }

    public void setMigrationKey(String migrationKey) {
        this.migrationKey = migrationKey;
    }

}
