package com.bt.nextgen.api.draftaccount.util;

import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.onboarding.btesb.BtesbErrorHandler;
import ns.btfin_com.sharedservices.integration.common.response.errorresponsetype.v3.ErrorResponseType;
import org.apache.commons.collections.IteratorUtils;

import java.util.List;

public class ServiceErrorsUtil {
    public static void updateResponseServiceErrors(ServiceErrors serviceErrors, List<? extends ErrorResponseType> errorResponse) {
        ServiceErrors responseServiceErrors = BtesbErrorHandler.parseErrorResponse(errorResponse);
        if (responseServiceErrors != null && responseServiceErrors.getErrorList() != null) {
            List<ServiceError> errorList = IteratorUtils.toList(responseServiceErrors.getErrorList().iterator());
            serviceErrors.addErrors(errorList);
        }
    }
}
