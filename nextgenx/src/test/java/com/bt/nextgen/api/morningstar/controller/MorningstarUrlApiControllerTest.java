package com.bt.nextgen.api.morningstar.controller;

import com.bt.nextgen.core.api.exception.BadRequestException;
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

import com.bt.nextgen.api.morningstar.model.MorningstarUrlDto;
import com.bt.nextgen.api.morningstar.model.MorningstarUrlKey;
import com.bt.nextgen.api.morningstar.service.MorningstarUrlDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;


@RunWith(MockitoJUnitRunner.class)
public class MorningstarUrlApiControllerTest {
	@InjectMocks
	private MorningstarUrlApiController morningstarUrlApiController;
	
	@Mock
	private MorningstarUrlDtoService morningstarUrlDtoService;
	
	@Mock
	private AssetIntegrationService assetIntegrationService;
	
	@Mock
	private static AnnotationMethodHandlerAdapter annotationMethodHandler;
	private MockHttpServletRequest mockHttpServletRequest;
	private MockHttpServletResponse mockHttpServletResponse;
	
	@Before
	public void setup() {
		mockHttpServletRequest = new MockHttpServletRequest(RequestMethod.GET.name(), "/");
		mockHttpServletResponse = new MockHttpServletResponse();
		annotationMethodHandler = new AnnotationMethodHandlerAdapter();
		HttpMessageConverter[] messageConverters =
		{
			new MappingJackson2HttpMessageConverter()
		};
		annotationMethodHandler.setMessageConverters(messageConverters);
	}
	
	@Test
	public void testMorningstarUrl() throws Exception{
		MorningstarUrlDto dto = new MorningstarUrlDto("http://ltqa.morningstar.com/l5k0kjmbi9/snapshot/snapshot.aspx?token=oe5EurclL%2FhdBHlWGBBgXWmZdg5EEv3vODQNCnKkyQY%3D&externalidtype=APIR&externalid=SGP");
		Mockito.when(morningstarUrlDtoService.find(Mockito.any(MorningstarUrlKey.class), Mockito.any(ServiceErrors.class))).thenReturn(dto);

		ApiResponse api = morningstarUrlApiController.getFundProfileUrl("12345");
		
		mockHttpServletRequest.setRequestURI("/secure/api/v1_0/morningstarfundprofileurl/12345/");
		mockHttpServletRequest.setMethod("GET");
		annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, morningstarUrlApiController);
	}

	@Test
	public void testMorningstarUrl_whenInvalidAssetId_thenThrowException() throws Exception{
		MorningstarUrlDto dto = new MorningstarUrlDto("http://ltqa.morningstar.com/l5k0kjmbi9/snapshot/snapshot.aspx?token=oe5EurclL%2FhdBHlWGBBgXWmZdg5EEv3vODQNCnKkyQY%3D&externalidtype=APIR&externalid=SGP");
		Mockito.when(morningstarUrlDtoService.find(Mockito.any(MorningstarUrlKey.class), Mockito.any(ServiceErrors.class))).thenReturn(dto);

		ApiResponse api = null;

		try {
			 api = morningstarUrlApiController.getFundProfileUrl("xx^2345");
		} catch (BadRequestException ex) {
		}

		Assert.assertNull(api);
	}

	@Test
	public void testMorningstarUrl_whenEmptyAssetId_thenThrowException() throws Exception{
		MorningstarUrlDto dto = new MorningstarUrlDto("http://ltqa.morningstar.com/l5k0kjmbi9/snapshot/snapshot.aspx?token=oe5EurclL%2FhdBHlWGBBgXWmZdg5EEv3vODQNCnKkyQY%3D&externalidtype=APIR&externalid=SGP");
		Mockito.when(morningstarUrlDtoService.find(Mockito.any(MorningstarUrlKey.class), Mockito.any(ServiceErrors.class))).thenReturn(dto);

		ApiResponse api = null;

		try {
			api = morningstarUrlApiController.getFundProfileUrl("");
		} catch (BadRequestException ex) {
		}

		Assert.assertNull(api);
	}
}
