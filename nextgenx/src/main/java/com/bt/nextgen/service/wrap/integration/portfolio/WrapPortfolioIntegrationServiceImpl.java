package com.bt.nextgen.service.wrap.integration.portfolio;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.base.AvaloqAccountIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.bt.nextgen.service.wrap.integration.WrapLoggingConstants;
import com.btfin.panorama.wrap.model.PortfolioPosition;
import com.btfin.panorama.wrap.service.PortfolioService;
import org.apache.commons.lang3.time.StopWatch;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service("WrapPortfolioIntegrationServiceImpl")
@Profile({"WrapOffThreadImplementation"})
public class WrapPortfolioIntegrationServiceImpl implements WrapPortfolioIntegrationService {

    private static final String SUBTOTAL_2 = "ZZZZZZZZZ";
    private static final String SUBTOTAL_1 = "00000000";
    private static final List<String> WRAP_SUBTOTALS_TO_IGNORE = Arrays.asList(SUBTOTAL_1, SUBTOTAL_2);
    private static Logger logger = LoggerFactory.getLogger(WrapPortfolioIntegrationServiceImpl.class);

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    /*This service will return third party details (WRAP, ASGARD) from Avaloq*/
    @Autowired
    @Qualifier("ThirdPartyAvaloqAccountIntegrationService")
    private AvaloqAccountIntegrationService avaloqAccountIntegrationService;

    public WrapAccountValuation loadWrapAccountValuation(String migrationKey, DateTime effectiveDate, boolean includeExternalAssets, ServiceErrors serviceErrors) {
        List<PortfolioPosition> wrapPortfolioPositions = new ArrayList<>();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Date asAtDate = effectiveDate.toGregorianCalendar().getTime();
        List<PortfolioPosition> portfolioPositions = portfolioService.getPortfolioPositionForClient(migrationKey, asAtDate,
                includeExternalAssets, serviceErrors);
        stopWatch.stop();
        logger.info(
                WrapLoggingConstants.WRAP_INTEGRATION_TIMING
                        + " WrapTransactionServiceIntegration::getPortfolioPositionForClient: wrap request timetaken= {} ms , {} count",
                stopWatch.getTime(), portfolioPositions.size());

        // filter subtotal rows
        for (PortfolioPosition portfolioPosition : portfolioPositions) {
            if (WRAP_SUBTOTALS_TO_IGNORE.contains(portfolioPosition.getSecurityName())) {
                logger.info(WrapLoggingConstants.WRAP_INTEGRATION_MAPPING + " ignoring " + portfolioPosition.getSecurityName());
            }
            else {
                wrapPortfolioPositions.add(portfolioPosition);
            }
        }
        return WrapPortfolioValuationConverter.convert(wrapPortfolioPositions, assetIntegrationService, serviceErrors);
    }
}