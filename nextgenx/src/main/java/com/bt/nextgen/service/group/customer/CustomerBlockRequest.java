package com.bt.nextgen.service.group.customer;

import com.btfin.panorama.core.security.UserAccountStatus;

/**
 * Created by m035652 on 13/03/14.
 */
public interface CustomerBlockRequest extends com.btfin.panorama.core.security.integration.customer.CredentialIdentifier {

    UserAccountStatus getRequiredStatus();


}
