package com.bt.nextgen.service.avaloq.taxinvoice;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqTemplate;

import java.util.ArrayList;
import java.util.List;

/* Suppress warning added as the fields are not supposed to be serialized and the valid parameters list provide information about
 * the parameters that can be supplied to a particular template.
 */
@SuppressWarnings({ "squid:S1171", "squid:S1948" })
public enum TaxInvoiceTemplate implements AvaloqTemplate {

    TAX_INVOICE_PMF("BTFG$UI_BOOK_LIST.BP#FEE_IM_EVT#GST_LAST_MONTH", new ArrayList<AvaloqParameter>() {
        {
            add(TaxInvoiceParams.PARAM_ACCOUNT_ID);
            add(TaxInvoiceParams.PARAM_VERI_START_DATE);
            add(TaxInvoiceParams.PARAM_VERI_END_DATE);
        }
    });

    private List<AvaloqParameter> validParams;
    private String templateName;

    TaxInvoiceTemplate(String templateName, List<AvaloqParameter> validParams) {
        this.templateName = templateName;
        this.validParams = validParams;
    }

    TaxInvoiceTemplate(String templateName) {

        this.templateName = templateName;
    }

    @Override
    public String getTemplateName() {
        return this.templateName;
    }

    @Override
    public List<AvaloqParameter> getValidParamters() {
        return this.validParams;
    }
}
