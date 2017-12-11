package com.bt.nextgen.service.integration.jms;

import org.h2.util.StringUtils;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.ServiceError;

/**
 * Created by L070589 on 16/01/2015.
 * Annotated Impl class to capture the error details from avaloq for any request.
 */
@ServiceBean(xpath="err", type = ServiceBeanType.CONCRETE)
public class AnnotatedServiceErrorResponseImpl implements ServiceError{

    @ServiceElement(xpath="type", type= String.class)
    private String errorType;
    @ServiceElement(xpath="id", type= String.class)
    private String errorId;
    @ServiceElement(xpath="err_msg", type= String.class)
    private String errorMessage;

	@ServiceElement(xpath="log_id", type= String.class)
	private String avaloqLogId;

	private StringBuffer reason = null;

	private String errorCode;
	private String service;
	private String correlationId;

	public static final String AVALOQ_SYSTEM_ID = "AVALOQ";


    @Override
    public String getMessage() {
        return errorMessage;
    }

    @Override
    public void setMessage(String message) {

    }

    @Override
    public String getReason() {
		if(reason==null)
		{
			reason = new StringBuffer();
			if(!StringUtils.isNullOrEmpty(avaloqLogId))
				reason.append(avaloqLogId).append(":");
			if(!StringUtils.isNullOrEmpty(errorMessage))
				reason.append(errorMessage);
		}
        return reason.toString();
    }

    @Override
    public void setReason(String reason) {
		this.reason = new StringBuffer(reason);
    }

    @Override
    public String getType() {
        return errorType;
    }

    @Override
    public void setType(String type) {
		this.errorType = type;
    }

    @Override
    public String getId() {
        return errorId;
    }

    @Override
    public void setId(String id) {
		this.errorId = id;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public void setErrorCode(String id) {
		this.errorCode = id;
    }

    @Override
    public Throwable getException() {
        return null;
    }

    @Override
    public void setException(Throwable exception) {

    }

    @Override
    public String getCorrelationId() {
      return this.correlationId;
    }

    @Override
    public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
    }

    @Override
    public void setService(String service) {
		this.service = service;
    }

    @Override
    public String getService() {
        return this.service;
    }

    @Override
    public void setOriginatingSystem(String originatingSystem) {

    }

    @Override
    public String getOriginatingSystem() {
        return AVALOQ_SYSTEM_ID;
    }

    @Override
    public String getErrorMessageForScreenDisplay() {
        return null;
    }
}
