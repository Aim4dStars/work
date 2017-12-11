package com.bt.nextgen.reports.beneficiary;

import com.bt.nextgen.api.beneficiary.model.Beneficiary;
import com.bt.nextgen.api.beneficiary.model.BeneficiaryDto;
import com.bt.nextgen.api.beneficiary.service.BeneficiaryDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;


@Report("beneficiaryDetailsPdfReport")
public class BeneficiaryDetailsPdfReport extends AccountReportV2 {

    private static final String REPORT_NAME = "Beneficiary details";

    private static final String DISCLAIMER_CONTENT = "DS-IP-0146";

    private static final String INFO_MESSAGE_WHEN_NO_DATA = "Ins-IP-0150";

    private static final String INFO_MESSAGE_WHEN_NO_SECONDARY_DATA = "Ins-IP-0236";

    private static final String  INFO_MESSAGE_AUTO_REV_NOMINATION = "Ins-IP-0232";

    private static final String ACCOUNT_ID = "account-id";

    private static final String CACHE_KEY = "BeneficiaryDetailsPdfReport.beneficiaryDetails";

    @Autowired
    private BeneficiaryDtoService beneficiaryDtoService;

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    /**
     * DTO Service for getting content information
     */
    @Autowired
    private ContentDtoService contentService;

    /**
     * @inheritDoc
     */
    @Override
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#root.this.getAccountEncodedId(#params), 'account.super.beneficiaries.view')")
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        return Collections.singletonList(getBeneficiaryReportData(params, dataCollections));
    }

    /**
     * @inheritDoc
     */
    @Override
    @ReportBean("reportType")
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return REPORT_NAME;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSummaryDescription(Map<String, Object> params, Map<String, Object> dataCollections) {
        final BeneficiaryDetailsReportData beneficiaryDetailsReportData = getBeneficiaryReportData(params, dataCollections);
        return "-".equals(beneficiaryDetailsReportData.getBeneficiariesLastUpdatedTime()) ? "" : "Last updated " +
                beneficiaryDetailsReportData.getBeneficiariesLastUpdatedTime();
    }

    /**
     * This method retrieves the disclaimer text for the content id.
     * Sonar warning represents the issue with params not being used in the method.
     *
     * @param params map of parameters to be passed to jasper report
     *
     * @return disclaimer text
     */
    @SuppressWarnings("squid:S1172")
    @ReportBean("disclaimer")
    public String getDisclaimer(Map<String, String> params) {
        final ServiceErrors serviceErrors = new FailFastErrorsImpl();
        final ContentKey key = new ContentKey(DISCLAIMER_CONTENT);
        final ContentDto content = contentService.find(key, serviceErrors);
        return content != null ? content.getContent() : "";
    }

    /**
     * This method retrieves the disclaimer text for the content id.
     * Sonar warning represents the issue with params not being used in the method.
     *
     * @param params map of parameters to be passed to jasper report
     *
     * @return disclaimer text
     */
    @SuppressWarnings("squid:S1172")
    @ReportBean("infoMessage")
    public String getInfoMessageWhenNoData(Map<String, String> params) {
        return getInfoMessageWhenNoDataSetup(INFO_MESSAGE_WHEN_NO_DATA);
    }

    @SuppressWarnings("squid:S1172")
    @ReportBean("infoMessageSecondary")
    public String getInfoMessageSecondaryWhenNoData(Map<String, String> params) {
        return getInfoMessageWhenNoDataSetup(INFO_MESSAGE_WHEN_NO_SECONDARY_DATA);
    }

    @SuppressWarnings("squid:S1172")
    @ReportBean("infoMessageAutoRevNomination")
    public String getInfoMessageAutoRevNomination(Map<String, String> params) {
        return getInfoMessageWhenNoDataSetup(INFO_MESSAGE_AUTO_REV_NOMINATION);
    }

    private String getInfoMessageWhenNoDataSetup(final String infoMessage) {
        final ServiceErrors serviceErrors = new FailFastErrorsImpl();
        final ContentKey key = new ContentKey(infoMessage);
        final ContentDto content = contentService.find(key, serviceErrors);
        return content != null ? content.getContent() : "";
    }

    /**
     * Converter method for the PDF DataObject model
     *
     * @param beneficiaryList list of type {@link Beneficiary}
     *
     * @return returns the list of {@link BeneficiaryData}
     */
    public List<BeneficiaryData> convertToBeneficiaryDataObject(List<Beneficiary> beneficiaryList) {
        final List<BeneficiaryData> beneficiaryDataList = new ArrayList<>();
        BeneficiaryData beneficiaryData = null;
        for (Beneficiary beneficiary : beneficiaryList) {
            beneficiaryData = new BeneficiaryData(beneficiary, staticIntegrationService);
            beneficiaryDataList.add(beneficiaryData);
        }
        return beneficiaryDataList;
    }

    /**
     * This method retrieves the {@link BeneficiaryDetailsReportData} object for the given accountId and puts it
     * into the dataCollections map for usage by other methods.
     *
     * @param params          map of params to be passed to jasper report.
     * @param dataCollections map of data collection to be passed to jasper report.
     *
     * @return object of {@link BeneficiaryDetailsReportData}
     */
    private BeneficiaryDetailsReportData getBeneficiaryReportData(Map<String, Object> params, Map<String, Object> dataCollections) {
        synchronized (dataCollections) {
            BeneficiaryDetailsReportData beneficiaryDetailsReportData = (BeneficiaryDetailsReportData) dataCollections.get(CACHE_KEY);
            if (beneficiaryDetailsReportData == null) {

                final String accountId = (String) params.get(ACCOUNT_ID);

                final List<ApiSearchCriteria> criteria = new ArrayList<>();
                criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS,
                        EncodedString.toPlainText(accountId), ApiSearchCriteria.OperationType.STRING));
                final List<BeneficiaryDto> beneficiaryDtoList = beneficiaryDtoService.search(criteria, new ServiceErrorsImpl());
                final BeneficiaryDto beneficiaryDto = isNotEmpty(beneficiaryDtoList) ? beneficiaryDtoList.get(0) : new BeneficiaryDto();
                beneficiaryDetailsReportData = new BeneficiaryDetailsReportData(beneficiaryDto.getBeneficiariesLastUpdatedTime(),
                        convertToBeneficiaryDataObject(beneficiaryDto.getBeneficiaries()));
                dataCollections.put(CACHE_KEY, beneficiaryDetailsReportData);
            }
            return beneficiaryDetailsReportData;
        }
    }

}
