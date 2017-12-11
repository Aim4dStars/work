package com.bt.nextgen.api.asset.service;

import com.bt.nextgen.api.asset.model.AssetPriceDto;
import com.bt.nextgen.api.asset.model.AssetPriceDtoKey;
import com.bt.nextgen.api.asset.model.SharePriceDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.ManagedFundAssetImpl;
import com.bt.nextgen.service.avaloq.asset.ShareAssetImpl;
import com.bt.nextgen.service.avaloq.asset.SharePriceImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetPrice;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.financialmarketinstrument.FinancialMarketInstrumentIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class AssetPriceDtoServiceImplTest {
	@InjectMocks
	private AssetPriceDtoServiceImpl assetPriceDtoService;

	@Mock
	private AssetIntegrationService assetIntegrationService;

	@Mock
	private FinancialMarketInstrumentIntegrationService financialMarketInstrumentIntegrationService;

	@Mock
	private AssetPriceDtoConverter assetPriceDtoConverter;

	@Mock
	private UserProfileService userProfileService;

	private ShareAssetImpl shareAsset1 = new ShareAssetImpl();
	private ShareAssetImpl shareAsset2 = new ShareAssetImpl();
	private ManagedFundAssetImpl mfAsset = new ManagedFundAssetImpl();

	@Before
	public void setup() {
		shareAsset1.setAssetId("0");
		shareAsset1.setAssetCode("XXX");
		shareAsset1.setAssetName("YYY");
		shareAsset1.setAssetType(AssetType.SHARE);
		shareAsset1.setPrice(BigDecimal.TEN);
		shareAsset2.setAssetId("1");
		shareAsset2.setAssetCode("XXX2");
		shareAsset2.setAssetName("YYY2");
		shareAsset2.setAssetType(AssetType.SHARE);
		shareAsset2.setPrice(BigDecimal.TEN);

		mfAsset.setAssetId("0");
		mfAsset.setAssetCode("XXX");
		mfAsset.setAssetName("YYY");
		mfAsset.setAssetType(AssetType.MANAGED_FUND);
		mfAsset.setPrice(BigDecimal.TEN);

		SharePriceImpl sharePrice1 = new SharePriceImpl();
		sharePrice1.setAsset(shareAsset1);
		sharePrice1.setLastPrice(10.0);
		SharePriceImpl sharePrice2 = new SharePriceImpl();
		sharePrice2.setLastPrice(20.0);
		sharePrice2.setAsset(shareAsset2);

		List<AssetPrice> assetPrices = new ArrayList<>(2);
		assetPrices.add(sharePrice1);
		assetPrices.add(sharePrice2);

		Mockito.when(financialMarketInstrumentIntegrationService.loadAssetPrices(Mockito.anyString(),
				Mockito.anyListOf(Asset.class), Mockito.anyBoolean(), Mockito.anyBoolean())).thenReturn(assetPrices);

		Mockito.when(financialMarketInstrumentIntegrationService.loadAssetPrice(Mockito.anyString(),
				Mockito.any(Asset.class), Mockito.anyBoolean(), Mockito.anyBoolean())).thenReturn(sharePrice1);

		List<AssetPriceDto> assetPriceDtos = new ArrayList<>();

		SharePriceDto sharePriceDto = new SharePriceDto(sharePrice1);

		Mockito.when(assetPriceDtoConverter.toAssetPriceDtos(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(assetPriceDtos);
		Mockito.when(assetPriceDtoConverter.toAssetPriceDto(Mockito.any(AssetPrice.class), Mockito.anyBoolean())).thenReturn(sharePriceDto);

		Mockito.when(userProfileService.getUserId()).thenReturn("201603884");
	}

	@Test
	public void testFind_shareAsset() {
		Mockito.when(assetIntegrationService.loadAsset(Mockito.anyString(), Mockito.any(ServiceErrors.class))).thenReturn(shareAsset1);

		AssetPriceDto assetPriceDto =
				assetPriceDtoService.find(new AssetPriceDtoKey("0", Boolean.FALSE, Boolean.TRUE, Boolean.FALSE), null);
		Assert.assertNotNull(assetPriceDto);
		Assert.assertTrue(assetPriceDto.getLastPrice() == 10.0);
	}

	@Test
	public void testFind_managedFundAsset() {
		Mockito.when(assetIntegrationService.loadAsset(Mockito.anyString(), Mockito.any(ServiceErrors.class))).thenReturn(mfAsset);

		AssetPriceDto assetPriceDto =
				assetPriceDtoService.find(new AssetPriceDtoKey("0", Boolean.FALSE, Boolean.TRUE, Boolean.FALSE), null);
		Assert.assertNotNull(assetPriceDto);
		Assert.assertTrue(assetPriceDto.getLastPrice() == 10.0);
	}

	@Test
	public void testSearch_shareAsset() {
		Map<String, Asset> assetMap = new HashMap<>();
		assetMap.put("0", shareAsset1);
		assetMap.put("1", shareAsset2);

		Mockito.when(assetIntegrationService.loadAssets(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(assetMap);


		List<ApiSearchCriteria> criteria = new ArrayList<>();
		criteria.add(new ApiSearchCriteria(Attribute.LIVE_ASSET_PRICE, ApiSearchCriteria.SearchOperation.EQUALS, "true",
				ApiSearchCriteria.OperationType.STRING));
		criteria.add(new ApiSearchCriteria(Attribute.COMPREHENSIVE_ASSET_PRICE, ApiSearchCriteria.SearchOperation.EQUALS, "false",
				ApiSearchCriteria.OperationType.STRING));
		criteria.add(new ApiSearchCriteria(Attribute.FALLBACK, ApiSearchCriteria.SearchOperation.EQUALS, "false",
				ApiSearchCriteria.OperationType.STRING));
		criteria.add(new ApiSearchCriteria(Attribute.ASSETTYPEINTLID, ApiSearchCriteria.SearchOperation.EQUALS, "0",
				ApiSearchCriteria.OperationType.STRING));
		criteria.add(new ApiSearchCriteria(Attribute.ASSETTYPEINTLID, ApiSearchCriteria.SearchOperation.EQUALS, "1",
				ApiSearchCriteria.OperationType.STRING));

		List<AssetPriceDto> assetPriceDtos = assetPriceDtoService.search(criteria, null);

		Assert.assertNotNull(assetPriceDtos);
	}

	@Test
	public void testSearch_managedFundAsset() {
		Map<String, Asset> assetMap = new HashMap<>();
		assetMap.put("0", mfAsset);

		Mockito.when(assetIntegrationService.loadAssets(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(assetMap);

		List<ApiSearchCriteria> criteria = new ArrayList<>();
		criteria.add(new ApiSearchCriteria(Attribute.LIVE_ASSET_PRICE, ApiSearchCriteria.SearchOperation.EQUALS, "true",
				ApiSearchCriteria.OperationType.STRING));
		criteria.add(new ApiSearchCriteria(Attribute.COMPREHENSIVE_ASSET_PRICE, ApiSearchCriteria.SearchOperation.EQUALS, "false",
				ApiSearchCriteria.OperationType.STRING));
		criteria.add(new ApiSearchCriteria(Attribute.FALLBACK, ApiSearchCriteria.SearchOperation.EQUALS, "false",
				ApiSearchCriteria.OperationType.STRING));
		criteria.add(new ApiSearchCriteria(Attribute.ASSETTYPEINTLID, ApiSearchCriteria.SearchOperation.EQUALS, "0",
				ApiSearchCriteria.OperationType.STRING));
		criteria.add(new ApiSearchCriteria(Attribute.ASSETTYPEINTLID, ApiSearchCriteria.SearchOperation.EQUALS, "1",
				ApiSearchCriteria.OperationType.STRING));

		List<AssetPriceDto> assetPriceDtos = assetPriceDtoService.search(criteria, null);

		Assert.assertNotNull(assetPriceDtos);
	}
}
