package com.bt.nextgen.service.avaloq.pension;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;

import javax.annotation.concurrent.Immutable;


/**
 * Implementation for pension commencement status.
 */
@ServiceBean(xpath = "//data/doc_list/doc/doc_head_list/doc_head", type = ServiceBeanType.CONCRETE)
@Immutable
public class PensionCommencementStatusImpl implements PensionCommencementStatus {
    @ServiceElement(xpath = "doc_id/val")
    private Long docId;


    @Override
    public Long getDocId() {
        return docId;
    }
}
