package com.bt.nextgen.service.onboarding;

import com.bt.nextgen.service.ServiceErrors;

/**
 * Base level response interface.
 */
public interface Response {

    /**
     * Service errors that were encountered during request processing.
     * @return the service errors.
     */
    ServiceErrors getServiceErrors();

    /**
     * Set the service errors.
     * @param serviceErrors the service errors to set.
     */
    void setServiceErrors(ServiceErrors serviceErrors);
}
