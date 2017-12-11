package com.bt.nextgen.service.integration.taxinvoice;

import java.math.BigDecimal;
import java.util.List;

public interface InvstMngrInvoiceDetails {

    String getInvstMngrName();

    String getInvstMngrABN();

    BigDecimal getImFeeExcludingGST();

    BigDecimal getImGST();

    BigDecimal getImFeeIncludingGST();

    List<IpsInvoice> getIpsInvoiceList();

}
