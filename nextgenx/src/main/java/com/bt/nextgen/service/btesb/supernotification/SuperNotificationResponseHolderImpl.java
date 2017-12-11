package com.bt.nextgen.service.btesb.supernotification;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.btesb.base.model.EsbError;
import com.bt.nextgen.service.integration.supernotification.SuperNotificationResponseHolder;

@ServiceBean(xpath = "//RspSNNS:NotifyCustomerResponseMsg", type = ServiceBeanType.CONCRETE)
public class SuperNotificationResponseHolderImpl implements SuperNotificationResponseHolder {

    @ServiceElement(xpath = "Status")
    private String status;

    @ServiceElement(xpath = "ResponseDetails/ResponseDetail/ErrorResponse")
    private EsbError error;

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public EsbError getError() {
        return error;
    }
}
