package com.bt.nextgen.service.group.customer;

import com.btfin.panorama.core.security.Roles;
import com.bt.nextgen.service.ServiceErrors;

import java.util.List;

/**
 * Created by m035652 on 22/01/14.
 */

public interface CustomerLoginManagementIntegrationService 
{
    com.btfin.panorama.core.security.integration.customer.CustomerCredentialInformation getCustomerInformation(CredentialRequest credentialRequest, ServiceErrors errors);

    /**
     * Method accepts customer id return username from SAFI/EAM
     * @param customerId
     * @param errors
     * @return user name of this customer
     */
    String getCustomerUserName(String customerId, ServiceErrors errors);

    com.btfin.panorama.core.security.integration.customer.CustomerCredentialInformation getDirectCustomerInformation(CredentialRequest credentialRequest, ServiceErrors serviceErrors);

    List<Roles> getCredentialGroups(CredentialRequest credentialRequest,ServiceErrors serviceErrors);

    String getPPID(CredentialRequest credentialRequest ,ServiceErrors serviceErrors);

}
