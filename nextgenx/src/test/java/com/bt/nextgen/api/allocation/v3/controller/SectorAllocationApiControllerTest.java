package com.bt.nextgen.api.allocation.v3.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.bt.nextgen.api.portfolio.v3.controller.SectorAllocationApiController;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.KeyedAllocBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.service.allocation.AllocationBySectorDtoService;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.service.ServiceErrors;

@RunWith(MockitoJUnitRunner.class)
public class SectorAllocationApiControllerTest
{
	@InjectMocks
	private SectorAllocationApiController sectorAllocationApiController;

	@Mock
    private AllocationBySectorDtoService allocationService;

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
        KeyedAllocBySectorDto allocationDto = Mockito.mock(KeyedAllocBySectorDto.class);

        Mockito.when(allocationService.find(Mockito.any(DatedValuationKey.class), Mockito.any(ServiceErrors.class)))
			.thenReturn(allocationDto);

        mockHttpServletRequest.setParameter("include-external", "false");

        mockHttpServletRequest
                .setRequestURI("/secure/api/portfolio/v3_0/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C/allocation-sector");
		mockHttpServletRequest.setMethod("GET");
        KeyedApiResponse<DatedValuationKey> apiResponse = sectorAllocationApiController.getAssetAllocationBySectorDetails(
                "account-id", null,
                Boolean.FALSE, "false",
                mockHttpServletRequest);
        Assert.assertNotNull(apiResponse);

	}

}
