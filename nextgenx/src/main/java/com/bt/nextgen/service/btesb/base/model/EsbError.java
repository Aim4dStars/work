package com.bt.nextgen.service.btesb.base.model;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;

@ServiceBean(xpath = "ErrorResponse", type = ServiceBeanType.CONCRETE)
public class EsbError {

    @ServiceElement(xpath = "Code")
    private String code;

    @ServiceElement(xpath = "SubCode")
    private String subCode;

    @ServiceElement(xpath = "Description")
    private String description;

    @ServiceElement(xpath = "Reason")
    private String reason;


    public String getCode() {
        return code;
    }

    public String getSubCode() {
        return subCode;
    }

    public String getDescription() {
        return description;
    }

    public String getReason() {
        return reason;
    }
}
