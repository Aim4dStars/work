package com.bt.nextgen.service.avaloq.contributionhistory;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.base.MigrationAttribute;

/**
 * Created by M044576 on 3/10/2017.
 */
public interface ThirdPartyIntegrationServiceFactory {

 <T> T getInstance(Class<T> clazz, MigrationAttribute migrationAttribute, String type, AccountKey accountKey);

}
