package com.bt.nextgen.service.avaloq.taxinvoice;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.integration.taxinvoice.IpsInvoice;
import com.bt.nextgen.service.integration.taxinvoice.TaxInvoiceData;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@ServiceBean(xpath = "ips")
public class IpsInvoiceImpl implements IpsInvoice {

    public static final String PATH_IPS_HEAD = "ips_head_list/ips_head/";
    public static final String PATH_IPS_ID = PATH_IPS_HEAD + "ips_name/annot/ctx/id";
    public static final String PATH_IPS_APIR = PATH_IPS_HEAD + "ips_apir/val";
    public static final String PATH_IPS_NAME = PATH_IPS_HEAD + "ips_name/val";
    public static final String PATH_IPS_AMT_NO_GST = PATH_IPS_HEAD + "amt_wo_gst/val";
    public static final String PATH_IPS_GST = PATH_IPS_HEAD + "gst/val";
    public static final String PATH_IPS_AMT_W_GST = PATH_IPS_HEAD + "amt_w_gst/val";

    @ServiceElement(xpath = PATH_IPS_ID)
    private String ipsId;

    @ServiceElement(xpath = PATH_IPS_APIR)
    private String ipsApirCode;

    @ServiceElement(xpath = PATH_IPS_NAME)
    private String ipsName;

    @ServiceElement(xpath = PATH_IPS_AMT_NO_GST, converter = BigDecimalConverter.class)
    private BigDecimal ipsFeeExcludingGST;

    @ServiceElement(xpath = PATH_IPS_GST, converter = BigDecimalConverter.class)
    private BigDecimal ipsGST;

    @ServiceElement(xpath = PATH_IPS_AMT_W_GST, converter = BigDecimalConverter.class)
    private BigDecimal ipsFeeIncludingGST;

    @ServiceElementList(xpath = "evt_list/evt", type = TaxInvoiceDataImpl.class)
    private List<TaxInvoiceData> taxInvoiceDetails;

    @Override
    public String getIpsId() {
        return ipsId;
    }

    public void setIpsId(String ipsId) {
        this.ipsId = ipsId;
    }

    public void setIpsApirCode(String ipsApirCode) {
        this.ipsApirCode = ipsApirCode;
    }

    public void setIpsName(String ipsName) {
        this.ipsName = ipsName;
    }

    public void setIpsFeeExcludingGST(BigDecimal ipsFeeExcludingGST) {
        this.ipsFeeExcludingGST = ipsFeeExcludingGST;
    }

    public void setIpsGST(BigDecimal ipsGST) {
        this.ipsGST = ipsGST;
    }

    public void setIpsFeeIncludingGST(BigDecimal ipsFeeIncludingGST) {
        this.ipsFeeIncludingGST = ipsFeeIncludingGST;
    }

    public void setTaxInvoiceDetails(List<TaxInvoiceData> taxInvoiceDetails) {
        this.taxInvoiceDetails = taxInvoiceDetails;
    }

    @Override
    public String getIpsApirCode() {
        return ipsApirCode;
    }

    @Override
    public String getIpsName() {
        return ipsName;
    }

    @Override
    public BigDecimal getIpsFeeExcludingGST() {
        return ipsFeeExcludingGST;
    }

    @Override
    public BigDecimal getIpsGST() {
        return ipsGST;
    }

    @Override
    public BigDecimal getIpsFeeIncludingGST() {
        return ipsFeeIncludingGST;
    }

    @Override
    public List<TaxInvoiceData> getTaxInvoiceDetails() {
        if (taxInvoiceDetails == null) {
            return Collections.emptyList();
        }
        return taxInvoiceDetails;
    }

}
