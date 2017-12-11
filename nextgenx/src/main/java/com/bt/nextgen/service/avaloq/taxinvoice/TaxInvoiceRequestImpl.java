package com.bt.nextgen.service.avaloq.taxinvoice;

import com.bt.nextgen.service.integration.taxinvoice.TaxInvoiceRequest;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.joda.time.DateTime;

public class TaxInvoiceRequestImpl implements TaxInvoiceRequest {
    private WrapAccountIdentifier wrapAccountIdentifier;
    private DateTime startDate;
    private DateTime endDate;

    @Override
    public WrapAccountIdentifier getWrapAccountIdentifier() {
        return wrapAccountIdentifier;

    }

    @Override
    public void setWrapAccountIdentifier(WrapAccountIdentifier wrapAccountIdentifier) {
        this.wrapAccountIdentifier = wrapAccountIdentifier;
    }

    @Override
    public DateTime getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;

    }

    @Override
    public DateTime getEndDate() {
        // TODO Auto-generated method stub
        return endDate;
    }

    @Override
    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;

    }

}
