package com.bt.nextgen.service.integration.taxinvoice;

import java.math.BigDecimal;
import java.util.List;

public interface IpsInvoice {

    String getIpsId();

    String getIpsApirCode();

    String getIpsName();

    BigDecimal getIpsFeeExcludingGST();

    BigDecimal getIpsGST();

    BigDecimal getIpsFeeIncludingGST();

    List<TaxInvoiceData> getTaxInvoiceDetails();

}
