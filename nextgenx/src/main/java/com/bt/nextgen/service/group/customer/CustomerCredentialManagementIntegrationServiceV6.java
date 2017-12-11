package com.bt.nextgen.service.group.customer;

import com.bt.nextgen.service.ServiceErrors;

/**
 * Created by L075208 on 18/07/2016.
 */
public interface CustomerCredentialManagementIntegrationServiceV6 {

    boolean updatePPID(String PPID ,String credentialId ,ServiceErrors serviceErrors);
}
