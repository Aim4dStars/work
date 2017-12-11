package com.bt.nextgen.api.fees.v2.service;

import com.bt.nextgen.api.fees.v2.model.TaxInvoiceDto;
import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface TaxInvoiceDtoService extends FindByKeyDtoService<DateRangeAccountKey, TaxInvoiceDto> {

}
