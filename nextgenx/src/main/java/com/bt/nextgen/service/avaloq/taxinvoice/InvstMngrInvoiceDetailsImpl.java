package com.bt.nextgen.service.avaloq.taxinvoice;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.integration.taxinvoice.InvstMngrInvoiceDetails;
import com.bt.nextgen.service.integration.taxinvoice.IpsInvoice;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@ServiceBean(xpath = "invst_mngr", type = ServiceBeanType.CONCRETE)
public class InvstMngrInvoiceDetailsImpl implements InvstMngrInvoiceDetails {
    public static final String PATH_INV_MNGR_HEAD = "invst_mngr_head_list/invst_mngr_head/";
    public static final String PATH_INV_MNGR_NAME = PATH_INV_MNGR_HEAD + "im_oe_owner/val";
    public static final String PATH_INV_MNGR_ABN = PATH_INV_MNGR_HEAD + "im_oe_owner_abn/val";
    public static final String PATH_IM_AMT_NO_GST = PATH_INV_MNGR_HEAD + "amt_wo_gst/val";
    public static final String PATH_IM_GST = PATH_INV_MNGR_HEAD + "gst/val";
    public static final String PATH_IM_AMT_GST = PATH_INV_MNGR_HEAD + "amt_w_gst/val";

    @ServiceElement(xpath = PATH_INV_MNGR_NAME)
    private String invstMngrName;

    @ServiceElement(xpath = PATH_INV_MNGR_ABN)
    private String invstMngrABN;

    @ServiceElement(xpath = PATH_IM_AMT_NO_GST, converter = BigDecimalConverter.class)
    private BigDecimal imFeeExcludingGST;

    @ServiceElement(xpath = PATH_IM_GST, converter = BigDecimalConverter.class)
    private BigDecimal imGST;

    @ServiceElement(xpath = PATH_IM_AMT_GST, converter = BigDecimalConverter.class)
    private BigDecimal imFeeIncludingGST;

    @ServiceElementList(xpath = "ips_list/ips", type = IpsInvoiceImpl.class)
    private List<IpsInvoice> ipsInvoiceList;

    @Override
    public String getInvstMngrName() {
        return invstMngrName;
    }

    public void setInvstMngrName(String invstMngrName) {
        this.invstMngrName = invstMngrName;
    }

    public void setInvstMngrABN(String invstMngrABN) {
        this.invstMngrABN = invstMngrABN;
    }

    public void setImFeeExcludingGST(BigDecimal imFeeExcludingGST) {
        this.imFeeExcludingGST = imFeeExcludingGST;
    }

    public void setImGST(BigDecimal imGST) {
        this.imGST = imGST;
    }

    public void setImFeeIncludingGST(BigDecimal imFeeIncludingGST) {
        this.imFeeIncludingGST = imFeeIncludingGST;
    }

    public void setIpsInvoiceList(List<IpsInvoice> ipsInvoiceList) {
        this.ipsInvoiceList = ipsInvoiceList;
    }

    @Override
    public String getInvstMngrABN() {
        return invstMngrABN;
    }

    @Override
    public BigDecimal getImFeeExcludingGST() {
        return imFeeExcludingGST;
    }

    @Override
    public BigDecimal getImGST() {
        return imGST;
    }

    @Override
    public BigDecimal getImFeeIncludingGST() {
        return imFeeIncludingGST;
    }

    @Override
    public List<IpsInvoice> getIpsInvoiceList() {
        if (ipsInvoiceList == null) {
            return Collections.emptyList();
        }
        return ipsInvoiceList;
    }

}
