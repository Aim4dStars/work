package com.bt.nextgen.service.avaloq.taxinvoice;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.CurrencyTypeConverter;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.bt.nextgen.service.integration.taxinvoice.TaxInvoiceData;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.joda.time.DateTime;

import java.math.BigDecimal;

@ServiceBean(xpath = "evt")
public class TaxInvoiceDataImpl implements TaxInvoiceData {
    private static final String PATH_PORTFOLIO_ID = "evt_head_list/evt_head/bp_id/val";
    private static final String PATH_FEE_DATE = "evt_head_list/evt_head/veri_date/val";
    private static final String PATH_DESCRIPTION_OF_SUPPLY = "evt_head_list/evt_head/book_text/val";
    private static final String PATH_FEE_EXCLUDING_GST = "evt_head_list/evt_head/amt_wo_gst/val";
    private static final String PATH_GST = "evt_head_list/evt_head/gst/val";
    private static final String PATH_FEE_INCLUDING_GST = "evt_head_list/evt_head/amt_w_gst/val";
    private static final String PATH_CURR_TYPE = "evt_head_list/evt_head/pos_curry_id/val";
    private static final String PATH_REVERSAL_FLAG = "if(evt_head_list/evt_head/order_type_id/val<'0') then true() else false()";

    @ServiceElement(xpath = PATH_PORTFOLIO_ID)
    private String accountId;

    @ServiceElement(xpath = PATH_FEE_DATE, converter = DateTimeTypeConverter.class)
    private DateTime feeDate;

    @ServiceElement(xpath = PATH_DESCRIPTION_OF_SUPPLY)
    private String descriptionOfSupply;

    @ServiceElement(xpath = PATH_FEE_EXCLUDING_GST, converter = BigDecimalConverter.class)
    private BigDecimal feeExcludingGST;

    @ServiceElement(xpath = PATH_GST, converter = BigDecimalConverter.class)
    private BigDecimal GST;

    @ServiceElement(xpath = PATH_FEE_INCLUDING_GST, converter = BigDecimalConverter.class)
    private BigDecimal feeIncludingGST;

    @ServiceElement(xpath = PATH_CURR_TYPE, converter = CurrencyTypeConverter.class)
    private CurrencyType currency;

    @ServiceElement(xpath = PATH_REVERSAL_FLAG)
    private boolean reversalFlag;

    @Override
    public WrapAccountIdentifier getWrapAccountIdentifier() {
        WrapAccountIdentifier wrapAccountIdentifier = new WrapAccountIdentifierImpl();
        wrapAccountIdentifier.setBpId(accountId);
        return wrapAccountIdentifier;
    }

    @Override
    public DateTime getFeeDate() {
        return feeDate;
    }

    @Override
    public String getDescriptionOfSupply() {
        if (reversalFlag) {
            descriptionOfSupply = "Reversal - " + descriptionOfSupply;
        }
        return descriptionOfSupply;
    }

    @Override
    public BigDecimal getFeeExcludingGST() {
        return feeExcludingGST;
    }

    @Override
    public BigDecimal getGST() {
        return GST;
    }

    @Override
    public BigDecimal getFeeIncludingGST() {
        return feeIncludingGST;
    }

    @Override
    public CurrencyType getCurrency() {
        return currency;
    }

    @Override
    public boolean getReversalFlag() {
        return reversalFlag;
    }

    public void setFeeDate(DateTime feeDate) {
        this.feeDate = feeDate;
    }

    public void setDescriptionOfSupply(String descriptionOfSupply) {
        this.descriptionOfSupply = descriptionOfSupply;
    }

    public void setFeeExcludingGST(BigDecimal feeExcludingGST) {
        this.feeExcludingGST = feeExcludingGST;
    }

    public void setGST(BigDecimal GST) {
        this.GST = GST;
    }

    public void setFeeIncludingGST(BigDecimal feeIncludingGST) {
        this.feeIncludingGST = feeIncludingGST;
    }
}