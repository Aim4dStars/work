package com.bt.nextgen.service.avaloq.taxinvoice;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AdviserIdentifierImpl;
import com.bt.nextgen.service.integration.AdviserIdentifier;
import com.bt.nextgen.service.integration.taxinvoice.TaxAdviserDetails;
import com.bt.nextgen.service.integration.taxinvoice.TaxInvoiceData;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@ServiceBean(xpath = "avsr")
public class TaxAdviserDetailsImpl implements TaxAdviserDetails {
    public static final String PATH_ADVISER_ID = "avsr_head_list/avsr_head/avsr_person/annot/ctx/id";
    public static final String PATH_ADVISER_NAME = "avsr_head_list/avsr_head/avsr_person/val";
    public static final String PATH_ADVISER_PHONE_NUMBER = "avsr_head_list/avsr_head/avsr_person_phone/val";
    public static final String PATH_ADVISER_FEE_EXCLUDING_GST = "avsr_head_list/avsr_head/amt_wo_gst/val";
    public static final String PATH_ADVISER_GST = "avsr_head_list/avsr_head/gst/val";
    public static final String PATH_ADVISER_FEE_INCLUDING_GST = "avsr_head_list/avsr_head/amt_w_gst/val";

    @ServiceElement(xpath = PATH_ADVISER_ID)
    private String adviserId;
    @ServiceElement(xpath = PATH_ADVISER_NAME)
    private String adviserName;
    @ServiceElement(xpath = PATH_ADVISER_PHONE_NUMBER)
    private String adviserPhoneNumber;
    @ServiceElement(xpath = PATH_ADVISER_FEE_EXCLUDING_GST, converter = BigDecimalConverter.class)
    private BigDecimal TotalfeeExcludingGST;
    @ServiceElement(xpath = PATH_ADVISER_GST, converter = BigDecimalConverter.class)
    private BigDecimal TotalGST;
    @ServiceElement(xpath = PATH_ADVISER_FEE_INCLUDING_GST, converter = BigDecimalConverter.class)
    private BigDecimal TotalfeeIncludingGST;

    @ServiceElementList(xpath = "evt_list/evt", type = TaxInvoiceDataImpl.class)
    private List<TaxInvoiceData> taxInvoice;

    @Override
    public AdviserIdentifier getAdviserIdentifier() {
        AdviserIdentifier adviserIdentifier = new AdviserIdentifierImpl();
        adviserIdentifier.setAdviserId(adviserId);
        return adviserIdentifier;
    }

    @Override
    public String getAdviserPhoneNumber() {
        return adviserPhoneNumber;
    }

    @Override
    public List<TaxInvoiceData> getTaxInvoice() {
        if (taxInvoice == null) {
            return Collections.emptyList();
        }
        return taxInvoice;
    }

    @Override
    public BigDecimal getTotalfeeExcludingGST() {
        return TotalfeeExcludingGST;
    }

    public void setTotalfeeExcludingGST(BigDecimal totalfeeExcludingGST) {
        TotalfeeExcludingGST = totalfeeExcludingGST;
    }

    @Override
    public BigDecimal getTotalGST() {
        return TotalGST;
    }

    public void setTotalGST(BigDecimal totalGST) {
        TotalGST = totalGST;
    }

    @Override
    public BigDecimal getTotalfeeIncludingGST() {
        return TotalfeeIncludingGST;
    }

    public void setTotalfeeIncludingGST(BigDecimal totalfeeIncludingGST) {
        TotalfeeIncludingGST = totalfeeIncludingGST;
    }

    @Override
    public String getAdviserName() {
        return adviserName;
    }

    public void setTaxInvoice(List<TaxInvoiceData> taxInvoice) {
        this.taxInvoice = taxInvoice;
    }
}