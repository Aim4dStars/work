package com.bt.nextgen.api.morningstar.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.api.morningstar.model.MorningstarUrlDto;
import com.bt.nextgen.api.morningstar.model.MorningstarUrlKey;
import com.btfin.panorama.core.security.profile.UserProfileService;

@RunWith(MockitoJUnitRunner.class)
public class MorningstarUrlDtoServiceTest {
	
	@InjectMocks
	private MorningstarUrlDtoServiceImpl morningstarUrlDtoService;
	@Mock
	@Qualifier("avaloqAssetIntegrationService")
	private AssetIntegrationService assetIntegrationService;
	
	@Mock
	private UserProfileService userProfileService;
	
	@Before
	public void setup() throws Exception {
	}
	
	@Test
	public void testGeneratedProfileFundUrl(){
		
		MorningstarUrlKey morningstarUrlKey = new MorningstarUrlKey("assetId"); 
		
		final AssetImpl asset = new AssetImpl();
		asset.setAssetCode("assetCode");
		asset.setAssetName("assetName");
		asset.setAssetId("assetId");
		
		Mockito.when(assetIntegrationService.loadAsset(Mockito.any(String.class), Mockito.any(ServiceErrors.class)))
		.thenReturn(asset);
		Mockito.when(userProfileService.getUserId()).thenReturn("1234");
		
		 MorningstarUrlDto morningstarUrlDto = morningstarUrlDtoService.find(morningstarUrlKey, new ServiceErrorsImpl());
		 
		 Assert.assertNotNull(morningstarUrlDto);
		Assert.assertNotNull(morningstarUrlDto.getUrl());
		
		String urlvalue = morningstarUrlDto.getUrl().substring(0,morningstarUrlDto.getUrl().indexOf("="));
		
		Assert.assertEquals(urlvalue,"http://eultrc.morningstar.com/l5k0kjmbi9/snapshot/snapshot.aspx?token");
	}
}
