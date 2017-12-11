package com.bt.nextgen.reports.account.common;

import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ValuationDto;
import com.bt.nextgen.api.portfolio.v3.service.valuation.ValuationDtoService;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;

public abstract class AbstractAccountValuationReport extends AccountReportV2 {

    private static final Logger logger = LoggerFactory.getLogger(AbstractAccountValuationReport.class);
    private static final String INCLUDE_EXTERNAL = "include-external";
    private static final String VALUATION_DATA_KEY = "AccountValuationReport.valuationData.";

    @Autowired
    @Qualifier("ValuationDtoServiceV3")
    private ValuationDtoService valuationService;

    @ReportBean("accountValuation")
    public AccountValuationReportData getAccountValuation(Map<String, Object> params, Map<String, Object> dataCollections) {
        AccountKey accountKey = getAccountKey(params);
        String includeExternal = (String) params.get(INCLUDE_EXTERNAL);

        DatedValuationKey key = createParameterisedDatedValuationKey(EncodedString.fromPlainText(accountKey.getId()).toString(),
                getEffectiveDate(params, dataCollections), Boolean.parseBoolean(includeExternal), params);

        return getValuation(key, dataCollections);
    }

    // allow child classes to access this as it is a common requirement for reports
    protected AccountValuationReportData getValuation(DatedValuationKey key, Map<String, Object> dataCollections) {
        String cacheKey = VALUATION_DATA_KEY + key.getAccountId();
        synchronized (dataCollections) {
            ValuationDto valuation = (ValuationDto) dataCollections.get(cacheKey);
            if (valuation == null) {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                valuation = valuationService.find(key, serviceErrors);
                dataCollections.put(cacheKey, valuation);
                if (serviceErrors.hasErrors()) {
                    logger.error("Errors during creation of base account valuation report: {}", serviceErrors.getErrorList());
                }
            }
            return new AccountValuationReportData(valuation);
        }
    }
    
    protected String getValuationDataKey() {
        return VALUATION_DATA_KEY;
    }

    public abstract DateTime getEffectiveDate(Map<String, Object> params, Map<String, Object> dataCollections);
}
