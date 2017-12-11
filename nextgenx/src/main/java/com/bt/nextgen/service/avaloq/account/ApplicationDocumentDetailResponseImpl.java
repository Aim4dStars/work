package com.bt.nextgen.service.avaloq.account;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.account.ApplicationDocumentDetail;
import com.bt.nextgen.service.integration.account.ApplicationDocumentDetailResponse;

import java.util.List;

@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class ApplicationDocumentDetailResponseImpl extends AvaloqBaseResponseImpl implements ApplicationDocumentDetailResponse {

    @ServiceElementList(xpath = "//data/doc_list/doc", type = ApplicationDocumentDetailImpl.class)
    private List<ApplicationDocumentDetail> applicationDocumentDetails;

    @Override
    public List<ApplicationDocumentDetail> getApplicationDocuments() {
        return applicationDocumentDetails;
    }

    @Override
    public void setApplicationDocumentDetails(List<ApplicationDocumentDetail> applicationDocumentDetails) {
        this.applicationDocumentDetails = applicationDocumentDetails;
    }
}
