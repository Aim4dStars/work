package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.btfin.panorama.core.validation.Validator;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioAsset;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioAssetSummary;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShadowPortfolioImplIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	private Validator validator;

	@Before
	public void setup()
	{}

	@Test
	public void testValidation_whenAsAtDateIsNull_thenServiceErrors()
	{
		ShadowPortfolioImpl shadowPortfolio = new ShadowPortfolioImpl();
		ShadowPortfolioDetailImpl detail = new ShadowPortfolioDetailImpl();
		detail.setLastUpdatedTargetPercent(new BigDecimal("11"));
		detail.setFloatingTargetPercent(new BigDecimal("11"));
		detail.setUnits(new BigDecimal("11"));
		detail.setMarketValue(new BigDecimal("11"));
		detail.setShadowPercent(new BigDecimal("11"));
		detail.setDifferencePercent(new BigDecimal("11"));
		List <ShadowPortfolioAssetSummary> assetSummaries = new ArrayList <ShadowPortfolioAssetSummary>();
		ShadowPortfolioAssetSummaryImpl assetSummary = new ShadowPortfolioAssetSummaryImpl();
		assetSummary.setAssetClass("Cash");
		assetSummary.setTotal(detail);
		List <ShadowPortfolioAsset> assets = new ArrayList <>();
		ShadowPortfolioAssetImpl asset = new ShadowPortfolioAssetImpl();
		asset.setAssetId("1234");
		asset.setShadowDetail(detail);
		assets.add(asset);
		assetSummary.setAssets(assets);
		shadowPortfolio.setTotal(detail);
		assetSummaries.add(assetSummary);
		shadowPortfolio.setAssetSummaries(assetSummaries);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(shadowPortfolio, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("asAtDate may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenAssetSummariesIsNull_thenServiceErrors()
	{
		ShadowPortfolioImpl shadowPortfolio = new ShadowPortfolioImpl();
		shadowPortfolio.setAsAtDate(new DateTime());
		ShadowPortfolioDetailImpl detail = new ShadowPortfolioDetailImpl();
		detail.setLastUpdatedTargetPercent(new BigDecimal("11"));
		detail.setFloatingTargetPercent(new BigDecimal("11"));
		detail.setUnits(new BigDecimal("11"));
		detail.setMarketValue(new BigDecimal("11"));
		detail.setShadowPercent(new BigDecimal("11"));
		detail.setDifferencePercent(new BigDecimal("11"));
		shadowPortfolio.setTotal(detail);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(shadowPortfolio, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("assetSummaries may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenTotalIsNull_thenServiceErrors()
	{
		ShadowPortfolioImpl shadowPortfolio = new ShadowPortfolioImpl();
		shadowPortfolio.setAsAtDate(new DateTime());
		ShadowPortfolioDetailImpl detail = new ShadowPortfolioDetailImpl();
		detail.setLastUpdatedTargetPercent(new BigDecimal("11"));
		detail.setFloatingTargetPercent(new BigDecimal("11"));
		detail.setUnits(new BigDecimal("11"));
		detail.setMarketValue(new BigDecimal("11"));
		detail.setShadowPercent(new BigDecimal("11"));
		detail.setDifferencePercent(new BigDecimal("11"));
		List <ShadowPortfolioAssetSummary> assetSummaries = new ArrayList <ShadowPortfolioAssetSummary>();
		ShadowPortfolioAssetSummaryImpl assetSummary = new ShadowPortfolioAssetSummaryImpl();
		assetSummary.setAssetClass("Cash");
		assetSummary.setTotal(detail);
		List <ShadowPortfolioAsset> assets = new ArrayList <>();
		ShadowPortfolioAssetImpl asset = new ShadowPortfolioAssetImpl();
		asset.setAssetId("1234");
		asset.setShadowDetail(detail);
		assets.add(asset);
		assetSummary.setAssets(assets);
		assetSummaries.add(assetSummary);
		shadowPortfolio.setAssetSummaries(assetSummaries);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(shadowPortfolio, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("total may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenSummaryAssetsIsNull_thenServiceErrors()
	{
		ShadowPortfolioImpl shadowPortfolio = new ShadowPortfolioImpl();
		shadowPortfolio.setAsAtDate(new DateTime());
		ShadowPortfolioDetailImpl detail = new ShadowPortfolioDetailImpl();
		detail.setLastUpdatedTargetPercent(new BigDecimal("11"));
		detail.setFloatingTargetPercent(new BigDecimal("11"));
		detail.setUnits(new BigDecimal("11"));
		detail.setMarketValue(new BigDecimal("11"));
		detail.setShadowPercent(new BigDecimal("11"));
		detail.setDifferencePercent(new BigDecimal("11"));
		List <ShadowPortfolioAssetSummary> assetSummaries = new ArrayList <ShadowPortfolioAssetSummary>();
		ShadowPortfolioAssetSummaryImpl assetSummary = new ShadowPortfolioAssetSummaryImpl();
		assetSummary.setAssetClass("Cash");
		assetSummary.setTotal(detail);
		shadowPortfolio.setTotal(detail);
		assetSummaries.add(assetSummary);
		shadowPortfolio.setAssetSummaries(assetSummaries);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(shadowPortfolio, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("assetSummaries[0].assets may not be null", serviceErrors.getErrorList()
			.iterator()
			.next()
			.getReason());
	}

	@Test
	public void testValidation_whenSummaryTotalIsNull_thenServiceErrors()
	{
		ShadowPortfolioImpl shadowPortfolio = new ShadowPortfolioImpl();
		shadowPortfolio.setAsAtDate(new DateTime());
		ShadowPortfolioDetailImpl detail = new ShadowPortfolioDetailImpl();
		detail.setLastUpdatedTargetPercent(new BigDecimal("11"));
		detail.setFloatingTargetPercent(new BigDecimal("11"));
		detail.setUnits(new BigDecimal("11"));
		detail.setMarketValue(new BigDecimal("11"));
		detail.setShadowPercent(new BigDecimal("11"));
		detail.setDifferencePercent(new BigDecimal("11"));
		List <ShadowPortfolioAssetSummary> assetSummaries = new ArrayList <ShadowPortfolioAssetSummary>();
		ShadowPortfolioAssetSummaryImpl assetSummary = new ShadowPortfolioAssetSummaryImpl();
		assetSummary.setAssetClass("Cash");
		List <ShadowPortfolioAsset> assets = new ArrayList <>();
		ShadowPortfolioAssetImpl asset = new ShadowPortfolioAssetImpl();
		asset.setAssetId("1234");
		asset.setShadowDetail(detail);
		assets.add(asset);
		assetSummary.setAssets(assets);
		shadowPortfolio.setTotal(detail);
		assetSummaries.add(assetSummary);
		shadowPortfolio.setAssetSummaries(assetSummaries);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(shadowPortfolio, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("assetSummaries[0].total may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenAssetIdIsNull_thenServiceErrors()
	{
		ShadowPortfolioImpl shadowPortfolio = new ShadowPortfolioImpl();
		shadowPortfolio.setAsAtDate(new DateTime());
		ShadowPortfolioDetailImpl detail = new ShadowPortfolioDetailImpl();
		detail.setLastUpdatedTargetPercent(new BigDecimal("11"));
		detail.setFloatingTargetPercent(new BigDecimal("11"));
		detail.setUnits(new BigDecimal("11"));
		detail.setMarketValue(new BigDecimal("11"));
		detail.setShadowPercent(new BigDecimal("11"));
		detail.setDifferencePercent(new BigDecimal("11"));
		List <ShadowPortfolioAssetSummary> assetSummaries = new ArrayList <ShadowPortfolioAssetSummary>();
		ShadowPortfolioAssetSummaryImpl assetSummary = new ShadowPortfolioAssetSummaryImpl();
		assetSummary.setAssetClass("Cash");
		assetSummary.setTotal(detail);
		List <ShadowPortfolioAsset> assets = new ArrayList <>();
		ShadowPortfolioAssetImpl asset = new ShadowPortfolioAssetImpl();
		asset.setShadowDetail(detail);
		assets.add(asset);
		assetSummary.setAssets(assets);
		shadowPortfolio.setTotal(detail);
		assetSummaries.add(assetSummary);
		shadowPortfolio.setAssetSummaries(assetSummaries);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(shadowPortfolio, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("assetSummaries[0].assets[0].assetId may not be null", serviceErrors.getErrorList()
			.iterator()
			.next()
			.getReason());
	}

	@Test
	public void testValidation_whenShadowDetailIsNull_thenServiceErrors()
	{
		ShadowPortfolioImpl shadowPortfolio = new ShadowPortfolioImpl();
		shadowPortfolio.setAsAtDate(new DateTime());
		ShadowPortfolioDetailImpl detail = new ShadowPortfolioDetailImpl();
		detail.setLastUpdatedTargetPercent(new BigDecimal("11"));
		detail.setFloatingTargetPercent(new BigDecimal("11"));
		detail.setUnits(new BigDecimal("11"));
		detail.setMarketValue(new BigDecimal("11"));
		detail.setShadowPercent(new BigDecimal("11"));
		detail.setDifferencePercent(new BigDecimal("11"));
		List <ShadowPortfolioAssetSummary> assetSummaries = new ArrayList <ShadowPortfolioAssetSummary>();
		ShadowPortfolioAssetSummaryImpl assetSummary = new ShadowPortfolioAssetSummaryImpl();
		assetSummary.setAssetClass("Cash");
		assetSummary.setTotal(detail);
		List <ShadowPortfolioAsset> assets = new ArrayList <>();
		ShadowPortfolioAssetImpl asset = new ShadowPortfolioAssetImpl();
		asset.setAssetId("1234");
		assets.add(asset);
		assetSummary.setAssets(assets);
		shadowPortfolio.setTotal(detail);
		assetSummaries.add(assetSummary);
		shadowPortfolio.setAssetSummaries(assetSummaries);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(shadowPortfolio, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("assetSummaries[0].assets[0].shadowDetail may not be null", serviceErrors.getErrorList()
			.iterator()
			.next()
			.getReason());
	}

	@Test
	public void testValidation_whenLastUpdatedTargetPercentIsNull_thenServiceErrors()
	{
		ShadowPortfolioImpl shadowPortfolio = new ShadowPortfolioImpl();
		shadowPortfolio.setAsAtDate(new DateTime());
		ShadowPortfolioDetailImpl detail = new ShadowPortfolioDetailImpl();
		detail.setFloatingTargetPercent(new BigDecimal("11"));
		detail.setUnits(new BigDecimal("11"));
		detail.setMarketValue(new BigDecimal("11"));
		detail.setShadowPercent(new BigDecimal("11"));
		detail.setDifferencePercent(new BigDecimal("11"));
		List <ShadowPortfolioAssetSummary> assetSummaries = new ArrayList <ShadowPortfolioAssetSummary>();
		ShadowPortfolioAssetSummaryImpl assetSummary = new ShadowPortfolioAssetSummaryImpl();
		assetSummary.setAssetClass("Cash");
		assetSummary.setTotal(detail);
		List <ShadowPortfolioAsset> assets = new ArrayList <>();
		ShadowPortfolioAssetImpl asset = new ShadowPortfolioAssetImpl();
		asset.setAssetId("1234");
		asset.setShadowDetail(detail);
		assets.add(asset);
		assetSummary.setAssets(assets);
		shadowPortfolio.setTotal(detail);
		assetSummaries.add(assetSummary);
		shadowPortfolio.setAssetSummaries(assetSummaries);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(shadowPortfolio, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("assetSummaries[0].total.lastUpdatedTargetPercent may not be null", serviceErrors.getErrorList()
			.iterator()
			.next()
			.getReason());
	}

	@Test
	public void testValidation_whenFloatingTargetPercentIsNull_thenServiceErrors()
	{
		ShadowPortfolioImpl shadowPortfolio = new ShadowPortfolioImpl();
		shadowPortfolio.setAsAtDate(new DateTime());
		ShadowPortfolioDetailImpl detail = new ShadowPortfolioDetailImpl();
		detail.setLastUpdatedTargetPercent(new BigDecimal("11"));
		detail.setUnits(new BigDecimal("11"));
		detail.setMarketValue(new BigDecimal("11"));
		detail.setShadowPercent(new BigDecimal("11"));
		detail.setDifferencePercent(new BigDecimal("11"));
		List <ShadowPortfolioAssetSummary> assetSummaries = new ArrayList <ShadowPortfolioAssetSummary>();
		ShadowPortfolioAssetSummaryImpl assetSummary = new ShadowPortfolioAssetSummaryImpl();
		assetSummary.setAssetClass("Cash");
		assetSummary.setTotal(detail);
		List <ShadowPortfolioAsset> assets = new ArrayList <>();
		ShadowPortfolioAssetImpl asset = new ShadowPortfolioAssetImpl();
		asset.setAssetId("1234");
		asset.setShadowDetail(detail);
		assets.add(asset);
		assetSummary.setAssets(assets);
		shadowPortfolio.setTotal(detail);
		assetSummaries.add(assetSummary);
		shadowPortfolio.setAssetSummaries(assetSummaries);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(shadowPortfolio, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("assetSummaries[0].total.floatingTargetPercent may not be null", serviceErrors.getErrorList()
			.iterator()
			.next()
			.getReason());
	}

	@Test
	public void testValidation_whenObjectIsNull_thenServiceErrors()
	{
		ShadowPortfolioImpl shadowPortfolio = null;
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(shadowPortfolio, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("Failed to validate - object is null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenValid_thenNoServiceError()
	{
		ShadowPortfolioImpl shadowPortfolio = new ShadowPortfolioImpl();
		shadowPortfolio.setAsAtDate(new DateTime());
		ShadowPortfolioDetailImpl detail = new ShadowPortfolioDetailImpl();
		detail.setLastUpdatedTargetPercent(new BigDecimal("11"));
		detail.setFloatingTargetPercent(new BigDecimal("11"));
		detail.setUnits(new BigDecimal("11"));
		detail.setMarketValue(new BigDecimal("11"));
		detail.setShadowPercent(new BigDecimal("11"));
		detail.setDifferencePercent(new BigDecimal("11"));
		List <ShadowPortfolioAssetSummary> assetSummaries = new ArrayList <ShadowPortfolioAssetSummary>();
		ShadowPortfolioAssetSummaryImpl assetSummary = new ShadowPortfolioAssetSummaryImpl();
		assetSummary.setAssetClass("Cash");
		assetSummary.setTotal(detail);
		List <ShadowPortfolioAsset> assets = new ArrayList <>();
		ShadowPortfolioAssetImpl asset = new ShadowPortfolioAssetImpl();
		asset.setAssetId("1234");
		asset.setShadowDetail(detail);
		assets.add(asset);
		assetSummary.setAssets(assets);
		shadowPortfolio.setTotal(detail);
		assetSummaries.add(assetSummary);
		shadowPortfolio.setAssetSummaries(assetSummaries);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(shadowPortfolio, serviceErrors);
		Assert.assertFalse(serviceErrors.hasErrors());
	}
}
