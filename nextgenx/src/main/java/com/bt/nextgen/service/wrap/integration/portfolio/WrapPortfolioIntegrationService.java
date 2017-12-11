package com.bt.nextgen.service.wrap.integration.portfolio;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import org.joda.time.DateTime;

public interface WrapPortfolioIntegrationService {
    WrapAccountValuation loadWrapAccountValuation(String migrationKey, DateTime effectiveDate, boolean includeExternalAssets, ServiceErrors serviceErrors);
}
