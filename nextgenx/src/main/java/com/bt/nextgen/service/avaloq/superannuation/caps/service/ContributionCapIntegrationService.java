package com.bt.nextgen.service.avaloq.superannuation.caps.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.superannuation.caps.model.ContributionCaps;
import com.bt.nextgen.service.integration.account.AccountKey;
import org.joda.time.DateTime;

public interface ContributionCapIntegrationService {
    /**
     * Retrieve superannuation contribution caps for a given account and financial year
     *
     * @param accountKey        account key of account to retrieve caps for
     * @param financialYearDate starting date of the financial year to return caps for
     * @param serviceErrors     errors object
     *
     * @return ContributionCaps for the given account and financial year
     */
    public ContributionCaps getContributionCaps(final AccountKey accountKey, DateTime financialYearDate, ServiceErrors serviceErrors);
}