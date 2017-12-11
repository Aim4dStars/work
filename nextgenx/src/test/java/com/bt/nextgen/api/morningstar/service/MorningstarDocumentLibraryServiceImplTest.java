package com.bt.nextgen.api.morningstar.service;

import com.bt.nextgen.api.morningstar.model.MorningstarAssetProfileKey;
import com.bt.nextgen.api.morningstar.model.MorningstarDocumentKey;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.web.HttpStreamReturnCode;
import com.bt.nextgen.service.web.HttpStreamService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;

@RunWith(MockitoJUnitRunner.class)
public class MorningstarDocumentLibraryServiceImplTest {

	@InjectMocks
	private MorningstarDocumentLibraryServiceImpl morningstarDocumentLibraryService;

	@Mock
	private HttpStreamService httpStreamService;

	@Mock
	private AssetIntegrationService assetIntegrationService;

	@Before
	public void setup() throws Exception {
		Mockito.when(httpStreamService
				.streamBinaryContentFromUrl(Mockito.anyString(), Mockito.anyString(), Mockito.any(HttpServletResponse.class),
						Mockito.any(ServiceErrors.class))).thenReturn(HttpStreamReturnCode.OK);

		AssetImpl asset = new AssetImpl();
		asset.setIsin("AU60ETL00329");

		Mockito.when(assetIntegrationService.loadAsset(Mockito.anyString(), Mockito.any(ServiceErrors.class))).thenReturn(asset);
	}

	@Test
	public void testGetFundProfileDocument_whenNotRedirectMode_thenShouldReturnOk() {
		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

		HttpStreamReturnCode rc =
				morningstarDocumentLibraryService.getFundProfileDocument(new MorningstarAssetProfileKey("BHP", "PDF", "btpn"), false,
						mockHttpServletResponse, new ServiceErrorsImpl());

		Assert.assertEquals(rc, HttpStreamReturnCode.OK);
	}

	@Test
	public void testGetFundProfileDocument_whenRedirectMode_thenShouldReturnOkAndResponseIsRedirect() {
		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

		HttpStreamReturnCode rc =
				morningstarDocumentLibraryService.getFundProfileDocument(new MorningstarAssetProfileKey("BHP", "PDF", "btpn"), true,
						mockHttpServletResponse, new ServiceErrorsImpl());

		Assert.assertEquals(rc, HttpStreamReturnCode.OK);
		Assert.assertEquals(mockHttpServletResponse.getRedirectedUrl(),
				"http://qa.content.morningstar.com.au/fundpdf/fundprofile-b?symbol=BHP&type=PDF&client=btpn");
	}

	@Test
	public void testGetDocumentFromLibrary_whenRequested_thenShouldBeRedirectedToExternalUrl() {
		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

		morningstarDocumentLibraryService
				.getDocumentFromLibrary(new MorningstarDocumentKey("111231", "PDS"), mockHttpServletResponse, new ServiceErrorsImpl());

		Assert.assertEquals(mockHttpServletResponse.getRedirectedUrl(), "http://www.morningstar.com.au/s/bt/AU60ETL00329.pdf");
	}
}
