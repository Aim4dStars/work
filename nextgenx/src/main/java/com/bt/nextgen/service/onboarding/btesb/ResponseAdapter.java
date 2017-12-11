package com.bt.nextgen.service.onboarding.btesb;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.onboarding.Response;
import ns.btfin_com.sharedservices.integration.common.response.errorresponsetype.v3.ErrorResponseType;

import java.util.List;

import static com.bt.nextgen.service.onboarding.btesb.BtesbErrorHandler.parseErrorResponse;
import static com.bt.nextgen.service.onboarding.btesb.BtesbErrorHandler.parseErrorRsp;

/**
 * Base implementation of all response adapters.
 */
public class ResponseAdapter implements Response{

    private ServiceErrors serviceErrors;

    @Override
    public final ServiceErrors getServiceErrors() {
        return serviceErrors;
    }

    @Override
    public final void setServiceErrors(ServiceErrors serviceErrors) {
        this.serviceErrors = serviceErrors;
    }

    protected void setServiceErrors(List<? extends ErrorResponseType> errors) {
        this.serviceErrors = parseErrorResponse(errors);
    }

    protected void setServiceErrors(ErrorResponseType error) {
        this.serviceErrors = parseErrorRsp(error);
    }
}
