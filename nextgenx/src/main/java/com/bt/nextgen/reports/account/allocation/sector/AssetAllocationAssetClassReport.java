package com.bt.nextgen.reports.account.allocation.sector;

import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.KeyedAllocBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.service.allocation.AllocationBySectorDtoService;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.reports.account.common.AbstractAccountValuationReport;
import com.bt.nextgen.reports.account.common.AccountValuationReportData;
import com.bt.nextgen.reports.account.common.SummaryReportData;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import net.sf.jasperreports.engine.Renderable;
import net.sf.jasperreports.renderers.JCommonDrawableRenderer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Report(value = "assetAllocationAssetClassV2", filename = "Portfolio asset allocation")
public class AssetAllocationAssetClassReport extends AbstractAccountValuationReport {

    private static final Logger logger = LoggerFactory.getLogger(AssetAllocationAssetClassReport.class);

    @Autowired
    private AllocationBySectorDtoService allocationDtoService;

    @Autowired
    private AssetSectorReportDataConverter reportDataConverter;

    private static final String DISCLAIMER_KEY = "DS-IP-0002";
    private static final String DISCLAIMER_KEY_DIRECT = "DS-IP-0200";
    private static final String ACCOUNT_ID = "account-id";
    private static final String EFFECTIVE_DATE = "effective-date";
    private static final String INCLUDE_EXTERNAL = "include-external";
    private static final String GROUP_BY_NAME = "group-by";
    private static final String ALLOCATION_DATA_KEY = "AssetAllocationAssetClassReport.allocationData.";

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        String accountId = (String) params.get(ACCOUNT_ID);
        String effectiveDate = (String) params.get(EFFECTIVE_DATE);
        String includeExternal = (String) params.get(INCLUDE_EXTERNAL);
        String groupName = (String) params.get(GROUP_BY_NAME);
        AllocationGroupType allocationGroupType = AllocationGroupType.forCode(groupName);

        DatedValuationKey key = createParameterisedDatedValuationKey(accountId, new DateTime(effectiveDate),
                Boolean.parseBoolean(includeExternal), params);

        List<AllocationBySectorDto> data = getAllocationData(key, dataCollections);
        AssetAllocationReportData reportData = reportDataConverter.getReportData(allocationGroupType, data);
        return Collections.singletonList(reportData);
    }

    @Override
    public String getSummaryDescription(Map<String, Object> params, Map<String, Object> dataCollections) {
        return "Portfolio valuation";
    }

    @Override
    public String getSummaryValue(Map<String, Object> params, Map<String, Object> dataCollections) {
        Boolean includeExternal = Boolean.parseBoolean((String) params.get(INCLUDE_EXTERNAL));
        AccountValuationReportData valuation = getAccountValuation(params, dataCollections);

        String summaryValue = includeExternal ? valuation.getTotalBalance() : valuation.getInternalBalance();

        return summaryValue;
    }

    @ReportBean("reportSummary")
    public SummaryReportData getReportSummary(Map<String, Object> params, Map<String, Object> dataCollections) {
        Boolean includeExternal = Boolean.parseBoolean((String) params.get(INCLUDE_EXTERNAL));
        AccountValuationReportData valuation = getAccountValuation(params, dataCollections);

        String summaryText = includeExternal ? "Total assets including external assets" : "Total assets";
        String summaryBalance = includeExternal ? valuation.getTotalBalance() : valuation.getInternalBalance();
        String summaryPercent = ReportFormatter.format(ReportFormat.PERCENTAGE, true, BigDecimal.valueOf(1));

        return new SummaryReportData(summaryText, summaryBalance, summaryPercent, true);
    }

    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return "Portfolio asset allocation";
    }

    @Override
    public DateTime getEffectiveDate(Map<String, Object> params, Map<String, Object> dataCollections) {
        return new DateTime(params.get(EFFECTIVE_DATE));
    }

    @ReportBean("effectiveDate")
    public String getEffectiveDate(Map<String, String> params) {
        DateTime effectiveDate = new DateTime(params.get(EFFECTIVE_DATE));
        return ReportFormatter.format(ReportFormat.SHORT_DATE, effectiveDate);
    }

    @ReportBean("includeExternal")
    public boolean getIncludeExternalParam(Map<String, String> params) {
        String includeExternal = params.get(INCLUDE_EXTERNAL);
        return Boolean.parseBoolean(includeExternal);
    }

    @ReportBean("disclaimer")
    public String getDisclaimer(Map<String, Object> params, Map<String, Object> dataCollections) {
        final UserExperience userExperience = getUserExperience(params, dataCollections);
        return getContent(UserExperience.DIRECT.equals(userExperience) ? DISCLAIMER_KEY_DIRECT : DISCLAIMER_KEY);
    }

    @ReportBean("externalAssetMarker")
    public Renderable getExternalAssetMarker() {
        return getRasterImage(getContent("externalAssetMarker"));
    }

    @ReportBean("allocationPieChart")
    public JCommonDrawableRenderer getAllocationPieChart(Map<String, String> params, Map<String, Object> dataCollections) {
        String accountId = (String) params.get(ACCOUNT_ID);
        String effectiveDate = (String) params.get(EFFECTIVE_DATE);
        String includeExternal = (String) params.get(INCLUDE_EXTERNAL);

        DatedValuationKey key = new DatedValuationKey(accountId, new DateTime(effectiveDate),
                Boolean.parseBoolean(includeExternal));

        List<AllocationBySectorDto> data = getAllocationData(key, dataCollections);
        return new JCommonDrawableRenderer(AssetSectorReportChart.createChart(data));
    }

    private List<AllocationBySectorDto> getAllocationData(DatedValuationKey key, Map<String, Object> dataCollections) {
        String cacheKey = ALLOCATION_DATA_KEY + key.getAccountId();
        synchronized (dataCollections) {
            KeyedAllocBySectorDto allocationData = (KeyedAllocBySectorDto) dataCollections.get(cacheKey);
            if (allocationData == null) {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                allocationData = allocationDtoService.find(key, serviceErrors);
                dataCollections.put(cacheKey, allocationData);
                if (serviceErrors.hasErrors()) {
                    logger.error("Errors during creation of asset allocation asset class report: {}",
                            serviceErrors.getErrorList());
                }
            }
            return allocationData.getAllocations();
        }
    }
}
