package com.bt.nextgen.service.integration.taxinvoice;

import com.bt.nextgen.service.ServiceErrors;

import java.util.List;

/**
 * Interface to return all the Tax invoices for the specific BP ID and specific period.
 * 
 * @param request
 *            - TaxInvoiceRequest
 * @param serviceErrors
 * @return List <DealerGroupDetails>
 * */
public interface TaxInvoiceIntegrationService {
    List<DealerGroupInvoiceDetails> generateTaxInvoice(TaxInvoiceRequest request, ServiceErrors serviceErrors);
}
