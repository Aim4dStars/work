package com.bt.nextgen.core.exception;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.exception.ApiValidationException;
import com.bt.nextgen.core.api.exception.NotFoundException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.service.ServiceError;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

public class GlobalExceptionHandlerTest
{
	private GlobalExceptionHandler handler = new GlobalExceptionHandler();
	private ApiException ex;
	private WebRequest request;

	@Before
	public void setup() throws Exception
	{
		ex = new ApiException(ApiVersion.CURRENT_VERSION, "message", new Throwable("cause"));
		request = mock(WebRequest.class);
		when(request.getDescription(Mockito.anyBoolean())).thenReturn("mock description");

	}

	@Test
	public void testHandleBadRequest_whenException_thenResponseHasVersionAndMessage()
	{
		ResponseEntity <ApiResponse> response = handler.handleBadRequest(ex, request);
		assertEquals(ex.getApiVersion(), response.getBody().getApiVersion());
		assertEquals(ex.getMessage(), response.getBody().getError().getMessage());
		try
		{
			UUID.fromString(response.getBody().getError().getReference());
		}
		catch (IllegalArgumentException e)
		{
			Assert.fail();
		}
	}

	@Test
	public void testHandleValidationException_whenException_thenResponseHasVersionAndMessage()
	{
		ResponseEntity <ApiResponse> response = handler.handleValidationException(new ApiValidationException(ex.getApiVersion(),
			new ArrayList <DomainApiErrorDto>(),
			ex.getMessage()), request);
		assertEquals(ex.getApiVersion(), response.getBody().getApiVersion());
		assertEquals(ex.getMessage(), response.getBody().getError().getMessage());
		try
		{
			UUID.fromString(response.getBody().getError().getReference());
		}
		catch (IllegalArgumentException e)
		{
			Assert.fail();
		}
	}

	@Test
	public void testHandleServiceException_whenException_thenResponseHasVersionAndMessage()
	{
		ServiceErrors errors = new ServiceErrorsImpl();
		ServiceError error = new ServiceErrorImpl();
		error.setException(ex);
		error.setReason("Test error");
		errors.addError(error);
		com.bt.nextgen.core.api.exception.ServiceException serviceException;
		serviceException = new com.bt.nextgen.core.api.exception.ServiceException(ex.getApiVersion(), errors, ex);

		ResponseEntity <ApiResponse> response = handler.handleServiceExceptionRequest(serviceException, request);

		assertEquals(ex.getApiVersion(), response.getBody().getApiVersion());
		Assert.assertTrue(response.getBody().getError().getErrors().get(0).getMessage().contains("Test error"));
		try
		{
			UUID.fromString(response.getBody().getError().getReference());
		}
		catch (IllegalArgumentException e)
		{
			Assert.fail();
		}
	}

	@Test
	public void testHandleApiException_whenException_thenResponseHasVersionAndMessage()
	{
		ResponseEntity <ApiResponse> response = handler.handleApiExceptionRequest(ex, request);
		assertEquals(ex.getApiVersion(), response.getBody().getApiVersion());
		Assert.assertTrue(response.getBody().getError().getMessage().contains(ex.getMessage()));
		try
		{
			UUID.fromString(response.getBody().getError().getReference());
		}
		catch (IllegalArgumentException e)
		{
			Assert.fail();
		}
	}

	@Test
	public void testHandleNotFound_whenException_thenResponseHasVersionAndMessage()
	{
		ResponseEntity <ApiResponse> response = handler.handleNotFound(new NotFoundException(ex.getApiVersion(), ex.getMessage()),
			request);
		assertEquals(ex.getApiVersion(), response.getBody().getApiVersion());
		assertEquals(ex.getMessage(), response.getBody().getError().getMessage());
		try
		{
			UUID.fromString(response.getBody().getError().getReference());
		}
		catch (IllegalArgumentException e)
		{
			Assert.fail();
		}
	}

	@Test
	public void testHandleTypeMismatch_whenException_thenReturnBadRequest()
	{
		ResponseEntity <Object> response = handler.handleTypeMismatch(new TypeMismatchException("object", null),
			null,
			null,
			request);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void testHandleError_whenException_withXMLHttpRequest_thenReturnInternalServerError() throws Exception
	{
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader("X-Requested-With")).thenReturn("XMLHttpRequest");
		ResponseEntity response = (ResponseEntity)handler.handleError(request, new Exception());
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}

}
