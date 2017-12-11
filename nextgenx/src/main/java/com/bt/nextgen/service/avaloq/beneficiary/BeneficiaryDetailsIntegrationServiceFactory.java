package com.bt.nextgen.service.avaloq.beneficiary;

import com.bt.nextgen.service.integration.cashcategorisation.service.CashCategorisationIntegrationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by L067218 on 11/08/2016.
 */

public interface BeneficiaryDetailsIntegrationServiceFactory {

    /**
     * This method retrieves instance of BeneficiaryDetailsIntegrationService based on cache value
     *
     * @param type
     * @return {@link BeneficiaryDetailsIntegrationService}
     */
    public BeneficiaryDetailsIntegrationService getInstance(String type);
}
