package com.bt.nextgen.service.wrap.integration;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.portfolio.valuation.WrapAccountValuationImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.base.AvaloqAccountIntegrationService;
import com.bt.nextgen.service.integration.base.ThirdPartyDetails;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.bt.nextgen.service.wrap.integration.portfolio.WrapPortfolioIntegrationService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service("ThirdPartyValuationIntegrationService")
@Profile({"WrapOffThreadImplementation"})
public class PortfolioValuationIntegrationServiceImpl {

    @Autowired
    @Qualifier("avaloqPortfolioIntegrationService")
    private PortfolioIntegrationService portfolioIntegrationService;

    @Autowired
    @Qualifier("WrapPortfolioIntegrationServiceImpl")
    private WrapPortfolioIntegrationService wrapPortfolioIntegrationService;

    /*This service will return third party details (WRAP, ASGARD) from Avaloq*/
    @Autowired
    @Qualifier("ThirdPartyAvaloqAccountIntegrationService")
    private AvaloqAccountIntegrationService avaloqAccountIntegrationService;

    public WrapAccountValuation loadWrapAccountValuation(AccountKey accountKey, DateTime effectiveDate, boolean includeExternal, ServiceErrors serviceErrors) {
        WrapAccountValuationImpl wrapAccountValuation = new WrapAccountValuationImpl();
        wrapAccountValuation.setAccountKey(accountKey);
        wrapAccountValuation.setHasExternal(includeExternal);

        ThirdPartyDetails thirdPartyDetails = avaloqAccountIntegrationService.getThirdPartySystemDetails(accountKey, serviceErrors);
        if (thirdPartyDetails.getMigrationDate() != null && effectiveDate.isBefore(thirdPartyDetails.getMigrationDate())) {
            wrapAccountValuation =
                    (WrapAccountValuationImpl) wrapPortfolioIntegrationService.loadWrapAccountValuation(
                            thirdPartyDetails.getMigrationKey(), effectiveDate, includeExternal, serviceErrors);
        }
        else {
            wrapAccountValuation =
                    (WrapAccountValuationImpl) portfolioIntegrationService.loadWrapAccountValuation(
                            accountKey, effectiveDate, includeExternal, serviceErrors);
        }
        return wrapAccountValuation;
    }
}
