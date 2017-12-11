package com.bt.nextgen.service.group.customer;

/**
 * Created by m035652 on 13/03/14.
 */
public interface CustomerUnblockRequest extends com.btfin.panorama.core.security.integration.customer.CredentialIdentifier {

    boolean requiresPasswordUpdate();

}
