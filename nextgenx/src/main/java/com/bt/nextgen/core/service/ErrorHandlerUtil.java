package com.bt.nextgen.core.service;

import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.soap.client.SoapFaultClientException;

import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;

import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.webservice.provider.CorrelationIdWrapper;
import com.bt.nextgen.service.ServiceError;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.web.validator.ValidationErrorCode;
import com.bt.nextgen.cms.service.CmsService;

public class ErrorHandlerUtil
{
	private static final Logger logger = LoggerFactory.getLogger(ErrorHandlerUtil.class);

    public static void parseErrors(ServiceStatus serviceStatus, ServiceErrors serviceErrors, String key, CorrelationIdWrapper correlationIdWrapper)
	{
		ServiceError error;
		StatusInfo statusInfo = serviceStatus.getStatusInfo().get(0);
		if (serviceErrors == null || serviceStatus == null)
		{
			return;
		}
		try
		{
			error = new ServiceErrorImpl();
			error.setErrorCode(statusInfo.getCode());
			error.setReason(statusInfo.getDescription());
			error.setType(statusInfo.getStatusDetail().get(0).getProviderErrorDetail().get(0).getProviderErrorCode());
			error.setCorrelationId(correlationIdWrapper.getCorrelationId());
			error.setService(Properties.get(key));
			serviceErrors.addError(error);
		}
        //CHECKSTYLE:OFF
		catch (Exception e)
		{
			logger.error("Error parsing error:", e);
		}
        //CHECKSTYLE:ON
	}

    public static void parseErrors(ServiceStatus serviceStatus, ServiceErrors serviceErrors, String key, CorrelationIdWrapper correlationIdWrapper, CmsService cmsService)
    {
        ServiceError error;
        if (serviceErrors == null || serviceStatus == null) {
            return;
        }
        StatusInfo statusInfo = serviceStatus.getStatusInfo().get(0);
        try
        {
            error = new ServiceErrorImpl();
            error.setErrorCode(statusInfo.getCode());
            error.setReason(getCustomErrorMessage(statusInfo, cmsService));
            error.setType(statusInfo.getStatusDetail().get(0).getProviderErrorDetail().get(0).getProviderErrorCode());
            error.setCorrelationId(correlationIdWrapper.getCorrelationId());
            error.setService(Properties.get(key));
            serviceErrors.addError(error);
        }
        catch (Exception e)
        {
            logger.error("Error parsing error:", e);
        }
    }

	/**
	 * Method to parse username service response status in case of error and returns the error key to fetch the value corresponds to that key from cms.
	 * @param status ServiceStatus
	 * @return String
	 */
	public static String parseServiceNegativeResponse(ServiceStatus status, ServiceErrors serviceErrors, CorrelationIdWrapper correlationIdWrapper)
	{
		if (status != null && status.getStatusInfo() != null && status.getStatusInfo().size() > 0)
		{
			parseErrors(status, serviceErrors, ServiceConstants.SERVICE_310, correlationIdWrapper);
			Level level = status.getStatusInfo().get(0).getLevel();
			switch (level)
			{
				case ERROR:
					logger.info("ESB Status Code: {}", status.getStatusInfo().get(0).getCode());
					logger.info("ESB Status Description: {}", status.getStatusInfo().get(0).getDescription());
					if(status.getStatusInfo().get(0).getStatusDetail().get(0).getProviderErrorDetail().get(0).getProviderErrorCode().equalsIgnoreCase(ErrorConstants.ALIAS_IN_USE_FAULT))
					{
						return ValidationErrorCode.USER_NAME_NOT_UNIQUE;
					}
					else
					{
						return ValidationErrorCode.ERROR_IN_REGISTRATION;
					}
				case WARNING:
				case INFORMATION:
				default:
					return ValidationErrorCode.ERROR_IN_REGISTRATION;
			}
		}
		return ValidationErrorCode.ERROR_IN_REGISTRATION;
	}

	/**
	 * Method to parse password service response status in case of error and returns the error key to fetch the value corresponds to that key from cms.
	 * @param status ServiceStatus
	 * @return String
	 */
	public static String parsePasswordServiceNegativeResponse(ServiceStatus status, ServiceErrors serviceErrors, CorrelationIdWrapper correlationIdWrapper)
	{
		if (status != null && status.getStatusInfo() != null && status.getStatusInfo().size() > 0)
		{
			parseErrors(status, serviceErrors, ServiceConstants.SERVICE_247, correlationIdWrapper);
			Level level = status.getStatusInfo().get(0).getLevel();
			switch (level)
			{
				case ERROR:
					logger.info("ESB Status Code: {}", status.getStatusInfo().get(0).getCode());
					logger.info("ESB Status Description: {}", status.getStatusInfo().get(0).getDescription());
					switch (status.getStatusInfo().get(0).getStatusDetail().get(0).getProviderErrorDetail().get(0).getProviderErrorCode())
					{
						case ErrorConstants.AUTHENTICATE_USER_FAULT:
							return ValidationErrorCode.INVALID_CURRENT_PASSWORD;

						case ErrorConstants.PWD_POLICY_IN_HISTORY_FAULT:
							return ValidationErrorCode.PASSWORD_NOT_UNIQUE;

						default:
							return ValidationErrorCode.FAILED_FORGET_PASSWORD;
					}
				case WARNING:
				case INFORMATION:
				default:
					return ValidationErrorCode.FAILED_FORGET_PASSWORD;
			}
		}
		return ValidationErrorCode.FAILED_FORGET_PASSWORD;
	}

	/**
	 *
	 */
	public static void parseSoapFaultException(ServiceErrors serviceErrors,SoapFaultClientException sfe,String key)
	{
		try
		{
			ServiceError error = new ServiceErrorImpl();
			error.setErrorCode(sfe.getFaultCode().toString());
			error.setReason(sfe.getFaultStringOrReason());
			error.setType(Constants.SOAP_FAULT_EXCPETION);
			error.setService(Properties.get(key));
			serviceErrors.addError(error);

		}
		catch (Exception e)
		{
			logger.error("Error parsing error:", e);
		}
	}

	/**
	 * Reads a <code>ServiceStatus</code> response object (Group ESB 301 service call) and generates a <code>ServiceError</code>
	 * object if any exist.
	 *
	 * @param serviceStatus
	 * @param serviceErrors
	 * @param key
	 * @param correlationIdWrapper
	 * @param cmsService
	 */
	public static void parseErrorsForUserName(ServiceStatus serviceStatus, ServiceErrors serviceErrors, String key, CorrelationIdWrapper correlationIdWrapper,CmsService cmsService)
	{
		ServiceError error;

		if (serviceErrors == null || serviceStatus == null)
		{
			return;
		}

		StatusInfo statusInfo = serviceStatus.getStatusInfo().get(0);

		error = new ServiceErrorImpl();
		error.setErrorCode(statusInfo.getCode());

		Level level = serviceStatus.getStatusInfo().get(0).getLevel();
		if(cmsService != null) {

			switch (level) {
				case ERROR:
					error.setReason(getErrorReasonForUsernameManagement(statusInfo.getCode(), cmsService));
					break;
				case WARNING:
				case INFORMATION:
				default:
					error.setReason(cmsService.getContent(ValidationErrorCode.ERROR_IN_REGISTRATION));
			}
		}else{

			error.setReason(statusInfo.getDescription());
		}

		error.setType(statusInfo.getStatusDetail().get(0).getProviderErrorDetail().get(0).getProviderErrorCode());
		error.setCorrelationId(correlationIdWrapper.getCorrelationId());
		error.setService(Properties.get(key));
		serviceErrors.addError(error);
	}


	private static String getErrorReasonForUsernameManagement(String errorCode, CmsService cmsService)
	{
		if (errorCode.trim().equalsIgnoreCase(ErrorConstants.ALIAS_IN_USE_FAULT))
		{
			return cmsService.getContent(ValidationErrorCode.USER_NAME_NOT_UNIQUE);
		}
		else
		{
			return cmsService.getContent(ValidationErrorCode.ERROR_IN_REGISTRATION);
		}
	}
	/**
	 * Reads a <code>ServiceStatus</code> response object (Group ESB 247 service call) and generates a <code>ServiceError</code>
	 * object if any exist for Password Management.
	 *
	 * @param status
	 * @param serviceErrors
	 * @param key
	 * @param correlationIdWrapper
	 * @param cmsService
	 */
	public static void parseErrorsForPassword(ServiceStatus status, ServiceErrors serviceErrors,String key, CorrelationIdWrapper correlationIdWrapper,CmsService cmsService)
	{
		ServiceError serviceError = null;

		if (serviceErrors == null || status == null)
		{
			return;
		}

		StatusInfo statusInfo = status.getStatusInfo().get(0);
		StatusDetail statusDetail = statusInfo.getStatusDetail().get(0);
		String providerErrorCode = statusDetail.getProviderErrorDetail().get(0).getProviderErrorCode();


		if(serviceError == null)
			serviceError = new ServiceErrorImpl();

		Level level = status.getStatusInfo().get(0).getLevel();
		switch (level)
		{
			case ERROR:
				serviceError.setReason(getErrorReasonforPasswordManagement(providerErrorCode,cmsService));
				break;

			case WARNING:
			case INFORMATION:
			default:

				serviceError.setReason(cmsService.getContent(ValidationErrorCode.FAILED_FORGET_PASSWORD));

		}

		serviceError.setErrorCode(status.getStatusInfo().get(0).getCode());
		serviceError.setCorrelationId(correlationIdWrapper.getCorrelationId());
		serviceError.setService(Properties.get(key));
		serviceError.setType(providerErrorCode);
		serviceErrors.addError(serviceError);


	}

	private static String getErrorReasonforPasswordManagement(String providerErrorCode,CmsService cmsService){

		switch (providerErrorCode)
		{
			case ErrorConstants.AUTHENTICATE_USER_FAULT:
				return cmsService.getContent(ValidationErrorCode.USER_NAME_NOT_UNIQUE);


			case ErrorConstants.PWD_POLICY_IN_HISTORY_FAULT:
				return cmsService.getContent(ValidationErrorCode.PASSWORD_NOT_UNIQUE);


			case ErrorConstants.PWD_POLICY_MAX_CONSECUTIVE_REPEATED_CHARS_FAULT:
				return cmsService.getContent(ValidationErrorCode.PWD_MAX_ALLOWED_CHARS);

			default:
				return cmsService.getContent(ValidationErrorCode.FAILED_FORGET_PASSWORD);

		}

	}

    public static String getCustomErrorMessage(StatusInfo statusInfo, CmsService cmsService) {
        String errorMessage = statusInfo.getDescription();
        String errorCode = statusInfo.getStatusDetail().get(0).getProviderErrorDetail().get(0).getProviderErrorCode();

        switch (errorCode) {
            case ErrorConstants.ALIAS_IN_USE:
                errorMessage = cmsService.getContent(ValidationErrorCode.USER_NAME_NOT_UNIQUE);
                break;
            case ErrorConstants.PWD_POLICY_IN_HISTORY_FAULT:
                errorMessage = cmsService.getContent(ValidationErrorCode.PASSWORD_NOT_UNIQUE);
                break;
            case ErrorConstants.PWD_POLICY_MAX_CONSECUTIVE_REPEATED_CHARS_FAULT:
                errorMessage =  cmsService.getContent(ValidationErrorCode.PWD_MAX_ALLOWED_CHARS);
                break;
            case ErrorConstants.PWD_POLICY_VALIDATION_FAULT:
                errorMessage =  cmsService.getContent(ValidationErrorCode.PWD_INVALID);
                break;
            default:
                errorMessage = getMessageOnBlank(statusInfo, cmsService);
                break;
        }
        return errorMessage;
    }

    private static String getMessageOnBlank(StatusInfo statusInfo, CmsService cmsService) {
        String errorMessage;
        if (null == statusInfo.getDescription() || statusInfo.getDescription().isEmpty()) {
            errorMessage =  cmsService.getContent(ValidationErrorCode.SYSTEM_UNAVAILABLE);
        } else {
            errorMessage = statusInfo.getDescription();
        }

        return errorMessage;
    }

    /**
	 * Reads a <code>ServiceStatus</code> response object (Group ESB 418 service call) and generates a <code>ServiceError</code>
	 * object if any exist.
	 *
	 * @param serviceStatus
	 * @param serviceErrors
	 * @param key
	 * @param correlationIdWrapper
	 * @param cmsService
	 */
    @SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.IllegalCatchCheck")
	public static void parseGCMErrors(StatusInfo statusInfo, ServiceErrors serviceErrors, String key, CorrelationIdWrapper correlationIdWrapper)
	{
			ServiceError error;
			if (serviceErrors == null) {
				return;
			}

			try
			{
				error = new ServiceErrorImpl();
				error.setErrorCode(statusInfo.getCode());
				error.setReason(statusInfo.getDescription());
				error.setType(statusInfo.getStatusDetail().get(0).getProviderErrorDetail().get(0).getProviderErrorCode());
				error.setCorrelationId(correlationIdWrapper.getCorrelationId());
				error.setService(Properties.get(key));
				serviceErrors.addError(error);
			}
			catch (Exception e)
			{
				logger.error("Error parsing error:", e);
			}
	}
}
