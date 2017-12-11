package com.bt.nextgen.service.avaloq.taxinvoice;

import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.taxinvoice.TaxInvoicePMF;
import com.bt.nextgen.service.integration.taxinvoice.TaxInvoicePMFIntegrationService;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaxInvoicePMFIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
        TaxInvoicePMFIntegrationService {

    @Autowired
    private AvaloqReportService avaloqReportService;

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    @Override
    public TaxInvoicePMF generateTaxInvoicePMF(final AccountKey accountKey, final DateTime startDate, final DateTime endDate,
            final ServiceErrors serviceErrors) {

        return new IntegrationSingleOperation<TaxInvoicePMF>("generateTaxInvoicePMF", serviceErrors) {

            @Override
            public TaxInvoicePMF performOperation() {

                AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(TaxInvoiceTemplate.TAX_INVOICE_PMF)
                        .forParam(TaxInvoiceParams.PARAM_ACCOUNT_ID, accountKey.getId())
                        .forParam(TaxInvoiceParams.PARAM_VERI_START_DATE, startDate)
                        .forParam(TaxInvoiceParams.PARAM_VERI_END_DATE, endDate);
                TaxInvoicePMFImpl response = avaloqReportService.executeReportRequestToDomain(avaloqRequest,
                        TaxInvoicePMFImpl.class, serviceErrors);

                return response;

            }
        }.run();

    }
}