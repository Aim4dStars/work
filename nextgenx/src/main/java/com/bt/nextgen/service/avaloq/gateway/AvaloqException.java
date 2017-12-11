package com.bt.nextgen.service.avaloq.gateway;

import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.error.IntegrationException;

/**
 * Created with IntelliJ IDEA.
 * User: l053474
 * Date: 27/08/13
 * Time: 1:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class AvaloqException extends IntegrationException{
	private ServiceErrors serviceErrors;


    public AvaloqException(String transactionId, String errorCode, String description)
    {
    	super("", transactionId, errorCode, description);
    }
    
    public AvaloqException(ServiceErrors serviceErrors) {
    	super(serviceErrors.getFirstError());
        this.serviceErrors = serviceErrors;
    }

    public AvaloqException(String message, ServiceErrors serviceErrors) {
        super(message);
        this.serviceErrors = serviceErrors;
    }

    public AvaloqException(String message, Throwable cause, ServiceErrors serviceErrors) {
        super(message, cause);
        this.serviceErrors = serviceErrors;
    }

    public AvaloqException(Throwable cause, ServiceErrors serviceErrors) {
        super(cause);
        this.serviceErrors = serviceErrors;
    }

    public AvaloqException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ServiceErrors serviceErrors) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.serviceErrors = serviceErrors;
    }

    public ServiceErrors getServiceErrors() {
        return serviceErrors;
    }

}
