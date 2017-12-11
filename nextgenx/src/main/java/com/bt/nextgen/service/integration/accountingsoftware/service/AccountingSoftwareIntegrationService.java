package com.bt.nextgen.service.integration.accountingsoftware.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.accountingsoftware.model.AccountingSoftware;

/**
 * Created by L062329 on 12/06/2015.
 */
public interface AccountingSoftwareIntegrationService {

    public AccountingSoftware update(AccountingSoftware software, ServiceErrors serviceErrors);

}
