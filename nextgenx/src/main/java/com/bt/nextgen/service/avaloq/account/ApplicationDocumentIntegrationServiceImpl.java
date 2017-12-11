package com.bt.nextgen.service.avaloq.account;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.account.ApplicationDocumentDetail;
import com.bt.nextgen.service.integration.account.ApplicationDocumentDetailResponse;
import com.bt.nextgen.service.integration.account.ApplicationDocumentIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationDocumentIntegrationServiceImpl implements ApplicationDocumentIntegrationService {
    @Autowired
    private AvaloqExecute avaloqExecute;

    public List<ApplicationDocumentDetail> loadApplicationDocumentsDirect(List<String> cisKeys, ServiceErrors serviceErrors) {
        return loadApplicationDocuments(cisKeys, serviceErrors, true);
    }

    @Override
    public List<ApplicationDocumentDetail> loadApplicationDocuments(List<String> accountNumbers, ServiceErrors serviceErrors) {
        return loadApplicationDocuments(accountNumbers, serviceErrors, false);
    }

    private List<ApplicationDocumentDetail> loadApplicationDocuments(List<String> customerIDNumbers,
                                                                     ServiceErrors serviceErrors,
                                                                     boolean isDirect) {
        String reportRequestName = "";
        AvaloqReportRequest avaloqReportRequestParams = null;
        if (isDirect) {
            reportRequestName = Template.APPLICATION_DOCUMENT_DETAILS_CUSTOMER.getName();
            AvaloqReportRequest reportRequest = new AvaloqReportRequest(reportRequestName);
            //CIS Keys
            avaloqReportRequestParams = reportRequest.forCustomerList(customerIDNumbers);
        } else {
            reportRequestName = Template.APPLICATION_DOCUMENT_DETAILS.getName();
            AvaloqReportRequest reportRequest = new AvaloqReportRequest(reportRequestName);
            // Account Numbers
            avaloqReportRequestParams = reportRequest.forBpNrList(customerIDNumbers);
        }

        ApplicationDocumentDetailResponse response = avaloqExecute.executeReportRequestToDomain(avaloqReportRequestParams,
                ApplicationDocumentDetailResponseImpl.class, serviceErrors);
        return response.getApplicationDocuments();
    }
}
