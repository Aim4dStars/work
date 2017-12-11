package com.bt.nextgen.core.exception;

import com.bt.nextgen.config.TestConfig;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.exception.ApiValidationException;
import com.bt.nextgen.core.api.exception.NotFoundException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.ServiceError;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerInProductionEnvironmentTest
{
	private static final String BLANK = "";
	private GlobalExceptionHandler handler = new GlobalExceptionHandler();
	private ApiException ex;
	private WebRequest request;
    private static String currentEnvironment;

    @BeforeClass
    public static void init() {
        currentEnvironment = Properties.getString("environment");
        Properties.all().put("environment", "PROD");
    }

	@Before
	public void setup() throws Exception
	{
		ex = new ApiException(ApiVersion.CURRENT_VERSION, "message", new Throwable("cause"));
		request = mock(WebRequest.class);
		when(request.getDescription(Mockito.anyBoolean())).thenReturn("mock description");
	}

	@Test
	public void testHandleBadRequest_whenException_thenResponseHasVersionAndBlankMessage()
	{

		ResponseEntity <ApiResponse> response = handler.handleBadRequest(ex, request);
		assertEquals(ex.getApiVersion(), response.getBody().getApiVersion());
		assertThat(response.getBody().getError().getMessage(), is(BLANK));
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
	public void testHandleValidationException_whenException_thenResponseHasVersionAndBlankMessage()
	{
		ResponseEntity <ApiResponse> response = handler.handleValidationException(new ApiValidationException(ex.getApiVersion(),
                new ArrayList<DomainApiErrorDto>(),
                ex.getMessage()), request);
		assertEquals(ex.getApiVersion(), response.getBody().getApiVersion());
		assertThat(response.getBody().getError().getMessage(), is(BLANK));
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
	public void testHandleServiceException_whenException_thenResponseHasVersionAndBlankMessage()
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
		assertThat(response.getBody().getError().getErrors().get(0).getMessage().trim(),is(""));
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
	public void testHandleApiException_whenException_thenResponseHasVersionAndBlankMessage()
	{
		ResponseEntity <ApiResponse> response = handler.handleApiExceptionRequest(ex, request);
		assertEquals(ex.getApiVersion(), response.getBody().getApiVersion());
		assertThat(response.getBody().getError().getMessage().trim(), is(BLANK));
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
	public void testHandleNotFound_whenException_thenResponseHasVersionAndBlankMessage()
	{
		ResponseEntity <ApiResponse> response = handler.handleNotFound(new NotFoundException(ex.getApiVersion(), ex.getMessage()),
                request);
		assertEquals(ex.getApiVersion(), response.getBody().getApiVersion());
		assertThat(response.getBody().getError().getMessage(), is(BLANK));
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
	public void testHandleTypeMismatch_whenException_thenReturnBadRequestAndBlankMessage()
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

    @AfterClass
    public static void tearDown() {
        Properties.all().put("environment", currentEnvironment);
    }

}
