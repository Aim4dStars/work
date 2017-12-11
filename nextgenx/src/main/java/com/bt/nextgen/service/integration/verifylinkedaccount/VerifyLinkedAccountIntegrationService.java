package com.bt.nextgen.service.integration.verifylinkedaccount;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.LinkedAccountVerification;

/**
 * Created by l078480 on 17/08/2017.
 */
public interface VerifyLinkedAccountIntegrationService {

    public VerifyLinkedAccountStatus getVerifyLinkedAccount(LinkedAccountVerification request,ServiceErrors serviceErrors);

    public VerifyLinkedAccountStatus generateCodeForLinkedAccount(LinkedAccountVerification request,ServiceErrors serviceErrors);
}
