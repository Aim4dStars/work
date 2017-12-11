package com.bt.nextgen.core.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.exception.ApiValidationException;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.exception.NotFoundException;
import com.bt.nextgen.core.api.model.ApiError;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.core.webservice.exception.WebServiceClientException;
import com.bt.nextgen.core.xml.XmlUtil;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.avaloq.gateway.AvaloqException;
import com.bt.nextgen.service.error.BTEsbException;
import com.bt.nextgen.service.error.GroupEsbException;
import com.bt.nextgen.service.error.IntegrationException;
import com.bt.nextgen.util.Environment;

import static com.bt.nextgen.core.util.SETTINGS.VIEW_EXCEPTION_DETAILS_ENABLED;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler
{
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	private final String TEMPLATE = "subTemplate";
	private String defaultErrorView = "error";
	private String userConfigErrorView = "userConfigError";

	@Autowired
	private UserProfileService profileService;

	//	@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Bad data")
	//	@ExceptionHandler(Exception.class)
	//	public void badData() 
	//	{
	//		logger.error("BadDataException");
	//	}

	@ExceptionHandler(Exception.class)
	public Object handleError(HttpServletRequest req, Exception exception) throws Exception
	{
		UUID ref = UUID.randomUUID();
		logger.error("Request[{}] raised {} [{}]", req.getRequestURI(), ref.toString(), exception.getMessage(), exception);

		if ("XMLHttpRequest".equals(req.getHeader("X-Requested-With")))
		{
			return createResponse(HttpStatus.INTERNAL_SERVER_ERROR, ref.toString());
		}

		if (isUnauthorised() || !canViewExceptionDetails())
		{
			return new ModelAndView("redirect:/public/static/page/maintenance.html");
		}

		ModelAndView model = new ModelAndView(defaultErrorView);
		model.addObject(TEMPLATE, "base");

		if (exception instanceof WebServiceClientException && exception.getCause() instanceof SoapFaultClientException
			&& ((SoapFaultClientException)exception.getCause()).getSoapFault() != null
			&& ((SoapFaultClientException)exception.getCause()).getSoapFault().getSource() != null)
		{
			try
			{
				String xml = XmlUtil.prettyPrint(((SoapFaultClientException)exception.getCause()).getSoapFault().getSource());
				logger.info("Soap fault:\n" + xml);
				xml = xml.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
				model.addObject("soapException", xml);
			}
			catch (TransformerException ex)
			{
				logger.error("Exception caught while transforming xml", ex);
			}
		}

		if (exception instanceof XmlUnmarshallException)
		{
			model.addObject("subTemplate", "unmarshall");
			logger.error("returning the special view, oldViewName was {}", defaultErrorView);
			model.setViewName("error.xmlUnmarshall");
			model.addObject("rootCauseException", exception);
		}

		if (exception instanceof GroupEsbException || exception instanceof BTEsbException || exception instanceof AvaloqException)
		{
			model.addObject("errorcode", ((IntegrationException)exception).getAbbreviatedErrorForDisplay());
			model.setViewName(View.ERROR_FATAL);
			logger.error("Fatal exception occured", exception);
			return model;
		}

		model.addObject("exceptions", produceStackTrace(exception));

		logger.warn("Unhandled exception", exception);

		return model;
	}

	private Object createResponse(HttpStatus status, String ref)
	{
		ApiError error = new ApiError(status.value(), status.getReasonPhrase(), ref);
		ApiResponse response = new ApiResponse("unknown", error);
		return new ResponseEntity <ApiResponse>(response, new HttpHeaders(), status);
	}

	Collection <Throwable> produceStackTrace(Throwable rootCause)
	{
        if (!canViewExceptionDetails()) {
            return Collections.emptyList();
        }

        Collection <Throwable> exceptions = new ArrayList <>();

		Throwable exception = rootCause;
		while (exception != null)
		{
			exceptions.add(exception);
			exception = exception.getCause();
		}
		return exceptions;

	}

	private boolean isUnauthorised()
	{
		return !(profileService.isLoggedIn());

	}

	// 500
	@ExceptionHandler(
	{
		ApiException.class
	})
	public ResponseEntity <ApiResponse> handleApiExceptionRequest(final ApiException ex, final WebRequest request)
	{
		List <DomainApiErrorDto> apiErrors = new ArrayList <>();

		UUID ref = UUID.randomUUID();
		logger.error("ApiException raised handling {} {} ", ref.toString(), request.getDescription(false), ex);

		StringWriter strWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(strWriter);
		writer.println(getExceptionMessage(ex));
		if (canViewExceptionDetails()) {
            ex.printStackTrace(writer);
        }

		ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), strWriter.toString(), ref.toString(), apiErrors);
		ApiResponse response = new ApiResponse(ex.getApiVersion(), error);
		return new ResponseEntity <ApiResponse>(response, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// 500
	@ExceptionHandler(
	{
		com.bt.nextgen.core.api.exception.ServiceException.class
	})
	public ResponseEntity <ApiResponse> handleServiceExceptionRequest(
		final com.bt.nextgen.core.api.exception.ServiceException ex, final WebRequest request)
	{
		List <DomainApiErrorDto> apiErrors = new ArrayList <>();
		UUID ref = UUID.randomUUID();
		logger.error("Api ServiceException raised handling {} {} ", ref.toString(), request.getDescription(false), ex);
		if (ex.getErrors() != null)
		{
			for (ServiceError error : ex.getErrors().getErrorList())
			{
				String reason = canViewExceptionDetails() ? error.getReason() : "";
				StringWriter strWriter = new StringWriter();
				PrintWriter writer = new PrintWriter(strWriter);
				if (error.getException() != null && canViewExceptionDetails())
				{
					error.getException().printStackTrace(writer);
				}
				reason += "\n" + strWriter.toString();
				DomainApiErrorDto apiError = new DomainApiErrorDto(error.getService(),
					error.getErrorCode(),
					reason,
					ErrorType.ERROR);
				apiErrors.add(apiError);
			}
		}

		ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), getExceptionMessage(ex), ref.toString(), apiErrors);
		ApiResponse response = new ApiResponse(ex.getApiVersion(), error);
		return new ResponseEntity <ApiResponse>(response, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// 400
	@ExceptionHandler(
	{
		BadRequestException.class
	})
	public ResponseEntity <ApiResponse> handleBadRequest(final ApiException ex, final WebRequest request)
	{
		UUID ref = UUID.randomUUID();
		logger.warn("BadRequestException raised handling {} {}", ref.toString(), request.getDescription(false), ex);
		ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), getExceptionMessage(ex), ref.toString());
		ApiResponse response = new ApiResponse(ex.getApiVersion(), error);
		return new ResponseEntity <ApiResponse>(response, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	// 400
	@ExceptionHandler(
	{
		ApiValidationException.class
	})
	public ResponseEntity <ApiResponse> handleValidationException(final ApiValidationException ex, final WebRequest request)
	{
		UUID ref = UUID.randomUUID();
		logger.warn("ApiValidationException raised handling {} {}", ref.toString(), request.getDescription(false), ex);
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), getExceptionMessage(ex), ref.toString(), ex.getErrors());
		ApiResponse response = new ApiResponse(ex.getApiVersion(), apiError);
		return new ResponseEntity <>(response, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	// 404
	@ExceptionHandler(
	{
		NotFoundException.class
	})
	public ResponseEntity <ApiResponse> handleNotFound(final NotFoundException ex, final WebRequest request)
	{
		UUID ref = UUID.randomUUID();
		logger.warn("NotFoundException raised handling {} {}", ref.toString(), request.getDescription(false), ex);
		ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), getExceptionMessage(ex), ref.toString());
		ApiResponse response = new ApiResponse(ex.getApiVersion(), error);
		return new ResponseEntity <ApiResponse>(response, new HttpHeaders(), HttpStatus.NOT_FOUND);
	}

	//400
	@Override
	protected ResponseEntity <Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status,
		WebRequest request)
	{
		UUID ref = UUID.randomUUID();
		logger.warn("TypeMismatch raised handling {} {}", ref.toString(), request.getDescription(false), ex);
		ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), getExceptionMessage(ex), ref.toString());
		ApiResponse response = new ApiResponse(ApiVersion.CURRENT_VERSION, error);
		return new ResponseEntity <Object>(response, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	//403
	@ExceptionHandler(
	{
		AccessDeniedException.class
	})
	public Object handleAccessDenied(AccessDeniedException exception, WebRequest request)
	{
		UUID ref = UUID.randomUUID();
		logger.warn("AccessDeniedException raised handling {} {}", ref.toString(), request.getDescription(false), exception);
		if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With")))
		{
			return createResponse(HttpStatus.FORBIDDEN, ref.toString());
		}

		ModelAndView model = new ModelAndView(defaultErrorView);
		model.addObject(TEMPLATE, "base");
		model.addObject("exceptions", produceStackTrace(exception));
		logger.warn("Unhandled exception", exception);
		return model;
	}

	private String getExceptionMessage(Exception ex) {
		return canViewExceptionDetails() ? ex.getMessage() : "";
	}

	private boolean canViewExceptionDetails() {
		return Environment.notProduction() && VIEW_EXCEPTION_DETAILS_ENABLED.isTrue();
	}

}