package com.bt.nextgen.reports.account.valuation;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ValuationDto;
import com.bt.nextgen.api.portfolio.v3.service.valuation.ValuationDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionNames;
import com.bt.nextgen.service.integration.options.service.OptionsService;

@Report(value = "portfolioValuationReportV2", filename = "Portfolio valuation")
public class PortfolioValuationReport extends AccountReportV2 {

    private static final String DISCLAIMER_KEY = "DS-IP-0001";
    private static final String DISCLAIMER_KEY_DIRECT = "DS-IP-0200";
    private static final String ACCOUNT_ID = "account-id";
    private static final String EFFECTIVE_DATE = "effective-date";
    private static final String INCLUDE_EXTERNAL = "include-external";
    private static final String REPORT_NAME = "Portfolio valuation";
    private static final String REPORT_SUB_TITLE = "As at %s";

    @Autowired
    @Qualifier("ValuationDtoServiceV3")
    private ValuationDtoService valuationService;

    @Autowired
    private OptionsService optionsService;

    @ReportBean("reportTitle")
    public String getReportTitle() {
        return REPORT_NAME;
    }

    @ReportBean("reportSubTitle")
    public String getReportSubTitle(Map<String, String> params) {
        String effectiveDate = getEffectiveDate(params);
        return String.format(REPORT_SUB_TITLE, effectiveDate);
    }

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        String accountId = (String) params.get(ACCOUNT_ID);
        String effectiveDate = (String) params.get(EFFECTIVE_DATE);
        Boolean includeExternal = Boolean.valueOf((String) params.get(INCLUDE_EXTERNAL));
        DatedValuationKey key = createParameterisedDatedValuationKey(accountId, new DateTime(effectiveDate), includeExternal, params);
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ValuationDto valuationDto = valuationService.find(key, serviceErrors);
        PortfolioValuationReportData valuationData = new PortfolioValuationReportData(valuationDto);
        return Collections.singletonList(valuationData);
    }

    @ReportBean("effectiveDate")
    public String getEffectiveDate(Map<String, String> params) {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, new DateTime(params.get(EFFECTIVE_DATE)));
    }

    @ReportBean("cashLogoOption")
    public Boolean getCashLogoOption(Map<String, String> params) {
        String accountId = params.get(UriMappingConstants.ACCOUNT_ID_URI_MAPPING);
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(accountId));
        Boolean cashLogoOption = optionsService
                .hasFeature(OptionKey.valueOf(OptionNames.CASH_BRANDED), accountKey, serviceErrors);
        return cashLogoOption;
    }

    @ReportBean("mfGainLossParam")
    public String getMFGainLossParam(Map<String, String> params) {
        String gainLossParam = params.get("mfGainLossParam");
        if (gainLossParam != null) {
            return gainLossParam;
        } else {
            return Constants.DOLLAR;
        }
    }

    @ReportBean("mpGainLossParam")
    public String getMPGainLossParam(Map<String, String> params) {
        String gainLossParam = params.get("mpGainLossParam");
        if (gainLossParam != null) {
            return gainLossParam;
        } else {
            return Constants.DOLLAR;
        }
    }

    @ReportBean("tmpGainLossParam")
    public String getTMPGainLossParam(Map<String, String> params) {
        String gainLossParam = params.get("tmpGainLossParam");
        if (gainLossParam != null) {
            return gainLossParam;
        } else {
            return Constants.DOLLAR;
        }
    }

    @ReportBean("lsGainLossParam")
    public String getLSGainLossParam(Map<String, String> params) {
        String gainLossParam = params.get("lsGainLossParam");
        if (gainLossParam != null) {
            return gainLossParam;
        } else {
            return Constants.DOLLAR;
        }
    }

    @Override
    public String getSummaryDescription(Map<String, Object> params, Map<String, Object> dataCollections) {
        return REPORT_NAME;
    }

    @Override
    public String getSummaryValue(Map<String, Object> params, Map<String, Object> dataCollections) {
        String accountId = (String) params.get(ACCOUNT_ID);
        String effectiveDate = (String) params.get(EFFECTIVE_DATE);
        Boolean includeExternal = true;
        DatedValuationKey key = createParameterisedDatedValuationKey(accountId, new DateTime(effectiveDate), includeExternal, params);
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ValuationDto data = valuationService.find(key, serviceErrors);
        return ReportFormatter.format(ReportFormat.CURRENCY, data.getBalance());
    }

    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return REPORT_NAME;
    }

    @ReportBean("disclaimer")
    public String getDisclaimer(Map<String, Object> params, Map<String, Object> dataCollections) {
        final UserExperience userExperience = getUserExperience(params, dataCollections);
        return UserExperience.DIRECT.equals(userExperience) ? getContent(DISCLAIMER_KEY_DIRECT) : getContent(DISCLAIMER_KEY);
    }
}
