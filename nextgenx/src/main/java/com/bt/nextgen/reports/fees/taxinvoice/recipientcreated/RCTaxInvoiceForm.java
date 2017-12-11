package com.bt.nextgen.reports.fees.taxinvoice.recipientcreated;

import ch.lambdaj.Lambda;
import com.bt.nextgen.core.reporting.BaseReportV2;
import com.bt.nextgen.core.reporting.HeaderReportData;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.fees.RCTInvoices;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.fees.RCTInvoicesFee;
import com.bt.nextgen.service.integration.fees.RCTInvoicesIntegrationService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import net.sf.jasperreports.engine.Renderable;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Report("rcTaxInvoice")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_dealergroup_account_reports')")
public class RCTaxInvoiceForm extends BaseReportV2 {

    private static final String RCTI_REPORT_NAME = "Recipient created tax invoice";
    private static final String ADJUSTMENT_NOTE_REPORT_NAME = "Adjustment note";

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private RCTInvoicesIntegrationService taxInvoiceIntegrationService;

    @ReportBean("header")
    public HeaderReportData getReportHeader(Map<String, Object> params, Map<String, Object> dataCollections) {
        HeaderReportData header = new HeaderReportData(getReportLogo(params));
        return header;
    }

    @ReportBean("footer")
    public FooterReportData getReportFooter(Map<String, Object> params, Map<String, Object> dataCollections) {
        String imageLocation = getContent("iconAdviserV2");
        Renderable iconContact =  getVectorImage(imageLocation);
        String imagePortraitLocation = getContent("reportFatFooterPortraitImageV2");
        Renderable footerBackgroundPortrait = getRasterImage(imagePortraitLocation);
        String businessSupportContact = getContent("Ins-IP-0309");
        String businessSupportName = getContent("Ins-IP-0308");
        String reportInformation = getContent("Ins-IP-0307");
        String reportTitle = getReportType(params, dataCollections);
        FooterReportData footer = new FooterReportData(iconContact, footerBackgroundPortrait, businessSupportContact,
                businessSupportName,
 reportTitle, reportInformation);
        return footer;
    }

    @ReportBean("startDate")
    public String getStartDate(Map<String, Object> params) {
        return ReportFormatter.format(ReportFormat.MEDIUM_DATE, new DateTime(params.get("start-date")));
    }

    @ReportBean("endDate")
    public String getEndDate(Map<String, Object> params) {
        return ReportFormatter.format(ReportFormat.MEDIUM_DATE, new DateTime(params.get("end-date")));
    }

    @ReportBean("disclaimer")
    public String getDisclaimer() {
        return getContent("DS-IP-0180");
    }

    public List<Object> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        TaxInvoiceReportData taxInvoiceReportData = null;
        List<Object> result = new ArrayList<>();
        ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        String startDate = (String) params.get("start-date");
        String endDate = (String) params.get("end-date");
        Collection<BrokerKey> brokerKeys = getBrokers(serviceErrors);
        taxInvoiceReportData = getTaxInvoiceData(brokerKeys, new DateTime(startDate), new DateTime(endDate), serviceErrors,
                dataCollections);
        result.add(taxInvoiceReportData);
        return result;
    }

    private Collection<BrokerKey> getBrokers(ServiceErrors serviceErrors) {
        Collection<Broker> brokers = brokerIntegrationService.getBrokersForJob(userProfileService.getActiveProfile(),
                serviceErrors);
        Collection<BrokerKey> brokerKeys = new ArrayList<>();
        for (Broker broker : brokers) {
            brokerKeys.add(broker.getKey());
        }
        return brokerKeys;
    }

    private TaxInvoiceReportData getTaxInvoiceData(Collection<BrokerKey> brokerKeys, DateTime startDate, DateTime endDate,
            ServiceErrors serviceErrors, Map<String, Object> dataCollections) {
        TaxInvoiceReportData taxInvoiceReportData = null;
        List<RCTInvoices> rctInvoices = getRecipientCreatedTaxInvoices(brokerKeys, startDate, endDate, serviceErrors,
                dataCollections);
        List<RCTInvoicesFee> rctInvoiceFees = getRecipientCreatedTaxInvoiceFees(rctInvoices);
        if (!rctInvoices.isEmpty()) {
            taxInvoiceReportData = new TaxInvoiceReportData(rctInvoices, rctInvoiceFees);
        }
        return taxInvoiceReportData;
    }

    private List<RCTInvoices> getRecipientCreatedTaxInvoices(Collection<BrokerKey> brokerKeys, DateTime startDate,
            DateTime endDate,
            ServiceErrors serviceErrors, Map<String, Object> dataCollections) {
        String cacheKey = "RCTaxInvoiceform.rctiInvoices";
        List<RCTInvoices> rctInvoices = null;
        synchronized (dataCollections) {
            rctInvoices = (List<RCTInvoices>) dataCollections.get(cacheKey);
            if (rctInvoices == null) {
                rctInvoices = new ArrayList<>();
                for (BrokerKey brokerKey : brokerKeys) {
                    RCTInvoices rctiInvoicesForBroker = taxInvoiceIntegrationService.getRecipientCreatedTaxInvoice(brokerKey,
                            startDate, endDate, serviceErrors);
                    if (rctiInvoicesForBroker != null) {
                        rctInvoices.add(rctiInvoicesForBroker);
                    }
                }
                dataCollections.put(cacheKey, rctInvoices);
            }
        }
        return rctInvoices;
    }

    private List<RCTInvoicesFee> getRecipientCreatedTaxInvoiceFees(List<RCTInvoices> rctInvoices) {
        List<RCTInvoicesFee> rctInvoiceFees = new ArrayList<>();
        for (RCTInvoices rctInvoice : rctInvoices) {
            rctInvoiceFees.addAll(rctInvoice.getRCTInvoicesFees());
        }
        return rctInvoiceFees;
    }

    @ReportBean("reportFileName")
    public String getReportFileName(Map<String, Object> params, Map<String, Object> dataCollections) {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Collection<Broker> brokers = brokerIntegrationService.getBrokersForJob(userProfileService.getActiveProfile(),
                serviceErrors);
        Broker broker = (Broker) brokers.iterator().next();
        StringBuilder filename = new StringBuilder(broker.getExternalBrokerKey().getId());
        filename.append(" - ");
        filename.append(getReportType(params, dataCollections));
        return filename.toString();
    }

    @ReportBean("reportName")
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        String reportName = null;
        BigDecimal totalFeeIncludingTax = BigDecimal.ZERO;
        ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        String startDate = (String) params.get("start-date");
        String endDate = (String) params.get("end-date");
        Collection<BrokerKey> brokerKeys = getBrokers(serviceErrors);
        List<RCTInvoices> rctInvoices = getRecipientCreatedTaxInvoices(brokerKeys, new DateTime(startDate),
                new DateTime(endDate), serviceErrors,
                dataCollections);
        if (!rctInvoices.isEmpty()) {
            totalFeeIncludingTax = Lambda.sum(rctInvoices, Lambda.on(RCTInvoices.class).getFeeIncludingGST());
        }
        if (totalFeeIncludingTax != null && totalFeeIncludingTax.compareTo(BigDecimal.ZERO) < 0) {
            reportName = ADJUSTMENT_NOTE_REPORT_NAME;
        }else{
            reportName = RCTI_REPORT_NAME;
        }
        return reportName;
    }

}
