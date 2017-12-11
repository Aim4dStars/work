/**
 * 
 */
package com.bt.nextgen.api.allocation.controller;

import static org.junit.Assert.*;

import com.bt.nextgen.api.account.v1.model.DatedAccountKey;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import com.bt.nextgen.api.allocation.model.AllocationDto;
import com.bt.nextgen.api.allocation.service.AllocationDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.service.ServiceErrors;

/**
 * @author L072463

 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SectorAllocationApiControllerTest
{
	@InjectMocks
	private SectorAllocationApiController sectorAllocationApiController;

	@Mock
	private AllocationDtoService allocationService;

	private MockHttpServletRequest mockHttpServletRequest;
	private MockHttpServletResponse mockHttpServletResponse;

	@Mock
	private static AnnotationMethodHandlerAdapter annotationMethodHandler;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{

		mockHttpServletRequest = new MockHttpServletRequest(RequestMethod.GET.name(), "/");
		mockHttpServletResponse = new MockHttpServletResponse();
		annotationMethodHandler = new AnnotationMethodHandlerAdapter();
		HttpMessageConverter[] messageConverters =
		{
			new MappingJackson2HttpMessageConverter()
		};
		annotationMethodHandler.setMessageConverters(messageConverters);

	}

	/**
	 * Test method for {@link com.bt.nextgen.api.allocation.controller.SectorAllocationApiController#getAssetAllocationDetails(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testGetAssetAllocationDetails() throws Exception
	{
		AllocationDto allocationDto = new AllocationDto()
		{

			@Override
			public String getType()
			{
				// TODO Auto-generated method stub
				return "allocationType";
			}

			@Override
			public DatedAccountKey getKey()
			{
				// TODO Auto-generated method stub
				return new DatedAccountKey("accountId", new DateTime());
			}
		};

		Mockito.when(allocationService.find(Mockito.any(DatedAccountKey.class), Mockito.any(ServiceErrors.class)))
			.thenReturn(allocationDto);

		mockHttpServletRequest.setParameter(UriMappingConstants.EFFECTIVE_DATE_PARAMETER_MAPPING, "2015-03-11");
		mockHttpServletRequest.setParameter(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "account-id");

		mockHttpServletRequest.setRequestURI("/secure/api/v1_0/accounts/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C/allocation-sector");
		mockHttpServletRequest.setMethod("GET");
		annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, sectorAllocationApiController);
	}

}
