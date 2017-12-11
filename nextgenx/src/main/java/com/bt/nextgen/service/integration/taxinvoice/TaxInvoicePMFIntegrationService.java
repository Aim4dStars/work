package com.bt.nextgen.service.integration.taxinvoice;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import org.joda.time.DateTime;

public interface TaxInvoicePMFIntegrationService {

    TaxInvoicePMF generateTaxInvoicePMF(AccountKey accountKey, DateTime startDate, DateTime endDate, ServiceErrors serviceErrors);

}
