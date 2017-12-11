package com.bt.nextgen.service.avaloq.transaction;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.transaction.TransactionErrorDetails;

@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class TransactionErrorDetailsImpl implements TransactionErrorDetails {

    // Path exists when environment error such as query timeout occurs
    public static final String XML_ENV_ERROR_DETAILS_PATH = "//ResponseDetails/ErrorResponses/ErrorResponse/Reason";

    // Path exists when a request error such as incomplete mandatory fields exists
    public static final String XML_REQ_ERROR_DETAILS_PATH = "//req_msg/../../err";

    @ServiceElement(xpath = XML_ENV_ERROR_DETAILS_PATH + " | " + XML_REQ_ERROR_DETAILS_PATH)
    private String errorMessage;

    public boolean isErrorResponse() {
        return errorMessage != null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public TransactionErrorDetailsImpl() {
        super();
    }
}
