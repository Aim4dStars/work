package com.bt.nextgen.service.integration.base;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;

/**
 * Created by M035995 on 14/06/2017.
 */
public interface AvaloqAccountIntegrationService {

    /**
     * This method retrives the System & Key identifier of that system from Avaloq service
     * Eg: WRAP - M02356423 ; ASGARD - F3839300
     * @param accountKey
     * @param serviceErrors
     * @return
     */
    ThirdPartyDetails getThirdPartySystemDetails(AccountKey accountKey, ServiceErrors serviceErrors);
}