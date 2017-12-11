package com.bt.nextgen.api.fees.v2.model;

import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

public class TaxInvoiceDto extends BaseDto implements KeyedDto<DateRangeAccountKey> {

    private DateRangeAccountKey key;

    private List<TaxInvoiceDetailsDto> taxInvoiceDetails;

    public TaxInvoiceDto(DateRangeAccountKey key, List<TaxInvoiceDetailsDto> taxInvoiceDetails) {
        super();
        this.key = key;
        this.taxInvoiceDetails = taxInvoiceDetails;
    }

    public List<TaxInvoiceDetailsDto> getTaxInvoiceDetails() {
        return taxInvoiceDetails;
    }

    @Override
    public DateRangeAccountKey getKey() {
        return key;
    }

}
