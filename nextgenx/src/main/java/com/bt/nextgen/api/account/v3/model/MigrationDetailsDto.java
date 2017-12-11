package com.bt.nextgen.api.account.v3.model;

import com.bt.nextgen.core.api.model.BaseDto;
import org.joda.time.DateTime;

/**
 * Created by L067218 on 21/08/2017.

 */
public class MigrationDetailsDto extends BaseDto {

    private String sourceId;
    private String accountId;
    private DateTime migrationDate;

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public DateTime getMigrationDate() {
        return migrationDate;
    }

    public void setMigrationDate(DateTime migrationDate) {
        this.migrationDate = migrationDate;
    }
}
