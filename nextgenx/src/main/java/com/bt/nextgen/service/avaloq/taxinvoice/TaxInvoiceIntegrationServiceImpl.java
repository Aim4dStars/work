package com.bt.nextgen.service.avaloq.taxinvoice;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.taxinvoice.DealerGroupInvoiceDetails;
import com.bt.nextgen.service.integration.taxinvoice.TaxInvoiceIntegrationService;
import com.bt.nextgen.service.integration.taxinvoice.TaxInvoiceRequest;
import com.bt.nextgen.service.integration.taxinvoice.TaxInvoiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of TaxInvoiceIntegrationService.
 * 
 * @param request
 *            - TaxInvoiceRequest
 * @param serviceErrors
 * @return List <DealerGroupDetails>
 * */
@Service
public class TaxInvoiceIntegrationServiceImpl implements TaxInvoiceIntegrationService {
    @Autowired
    private AvaloqExecute avaloqExecute;

    @Override
    public List<DealerGroupInvoiceDetails> generateTaxInvoice(TaxInvoiceRequest request, ServiceErrors serviceErrors) {

        TaxInvoiceResponse response = avaloqExecute
                .executeReportRequestToDomain(
                        new AvaloqReportRequest(Template.TAX_INVOICE.getName())
                                .forAccount(request.getWrapAccountIdentifier().getAccountIdentifier())
                                .forDateTime(Constants.REPORT_FROM_DATE, request.getStartDate())
                                .forDateTime(Constants.REPORT_TO_DATE, request.getEndDate()), TaxInvoiceResponseImpl.class,
                        serviceErrors);
        return response != null ? response.getDealerGroupDetails() : new ArrayList<DealerGroupInvoiceDetails>();
    }

}