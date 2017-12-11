package com.bt.nextgen.service.avaloq.contributionhistory;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.service.integration.base.AvaloqAccountIntegrationService;
import com.bt.nextgen.service.integration.base.ThirdPartyDetails;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.wrap.model.Contribution;
import com.btfin.panorama.wrap.service.ContributionHistoryService;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Wrap service for contribution history.
 */
@Service("WrapContributionHistoryIntegrationServiceImpl")
@Profile("WrapOffThreadImplementation")
public class WrapContributionHistoryIntegrationServiceImpl implements ContributionHistoryIntegrationService {


    private static Logger logger = LoggerFactory.getLogger(WrapContributionHistoryIntegrationServiceImpl.class);

    @Autowired
    @Qualifier("ContributionHistoryServiceRestClient")
    private ContributionHistoryService contributionHistoryService;

    /*This service will return third party details (WRAP, ASGARD) from Avaloq*/
    @Autowired
    @Qualifier("ThirdPartyAvaloqAccountIntegrationService")
    private AvaloqAccountIntegrationService avaloqAccountIntegrationService;

    @Autowired
    private WrapContributionTypeConverter converter;

    @Autowired
    private StaticIntegrationService staticService;


    @Override
    public ContributionHistory getContributionHistory(AccountKey accountKey, DateTime financialYearStartDate,
                                                      DateTime financialYearEndDate) {
        // Get the M# and other details from ThirdPartyAvaloqAccountIntegrationService
        ThirdPartyDetails thirdPartyDetails =
                avaloqAccountIntegrationService.getThirdPartySystemDetails(com.bt.nextgen.service.integration.account.AccountKey.valueOf(accountKey.getAccountId()), new FailFastErrorsImpl());
        List<Contribution> wrapContributions = contributionHistoryService.getContributionsHistoryForClient(thirdPartyDetails.getMigrationKey(), financialYearStartDate.toDate(),
                financialYearEndDate.toDate(), "DetailedReport", new ServiceErrorsImpl());
        logger.info("WrapContributionHistoryIntegrationServiceImpl::getContributionHistory: wrap request {} count", CollectionUtils.isNotEmpty(wrapContributions)?wrapContributions.size():"empty collection");
        // Mapped Wrap records to Panorama records
        return converter.toContributionHistory(wrapContributions, staticService);
    }


}
