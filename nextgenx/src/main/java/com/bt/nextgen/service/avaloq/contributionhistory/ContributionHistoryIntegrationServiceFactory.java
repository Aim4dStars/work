package com.bt.nextgen.service.avaloq.contributionhistory;

/**
 * Created by L067218 on 12/08/2016.
 */
public interface ContributionHistoryIntegrationServiceFactory {

    /**
     * This method retrieves instance of ContributionHistoryIntegrationService based on cache value
     *
     * @param type
     * @return {@link ContributionHistoryIntegrationService}
     */
    public ContributionHistoryIntegrationService getInstance(String type);
}
