package com.bt.nextgen.service.btesb.financialmarketinstrument;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.avaloq.asset.ShareAssetImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetPrice;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.financialmarketinstrument.FinancialMarketInstrumentIntegrationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

public class ICCFinancialMarketInstrumentIntegrationServiceImplTest extends BaseSecureIntegrationTest {
	@Autowired
	private FinancialMarketInstrumentIntegrationService financialMarketInstrumentIntegrationService;

	@Before
	public void setup() {
	}

	// TODO: Investigate why it fails sometimes.  Seems to require ICC to be up.
	@Ignore
	@Test
	public void testShareAssetPrices() {
		Asset asset = new ShareAssetImpl();
		((ShareAssetImpl) asset).setAssetId("1");
		((ShareAssetImpl) asset).setAssetCode("BHP");
		((ShareAssetImpl) asset).setAssetType(AssetType.SHARE);

		List<AssetPrice> assetPrices =
				financialMarketInstrumentIntegrationService.loadAssetPrices("201603884", Arrays.asList(asset), true, false);

		Assert.assertNotNull(assetPrices);
		Assert.assertTrue(assetPrices.size() == 1);
	}
}
