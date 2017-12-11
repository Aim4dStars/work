package com.bt.nextgen.api.cgt.service;

import com.bt.nextgen.api.cgt.model.CgtDto;
import com.bt.nextgen.api.cgt.model.CgtKey;
import com.bt.nextgen.api.cgt.model.CgtMpSecurityDto;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.cgt.CgtDataImpl;
import com.bt.nextgen.service.avaloq.cgt.InvestmentCgtImpl;
import com.bt.nextgen.service.avaloq.cgt.ManagedPortfolioCgtImpl;
import com.bt.nextgen.service.avaloq.cgt.WrapCgtDataImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.cgt.CgtBaseData;
import com.bt.nextgen.service.integration.cgt.CgtIntegrationService;
import com.bt.nextgen.service.integration.cgt.InvestmentCgt;
import org.joda.time.DateTime;
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
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class RealisedCgtDtoServiceTest
{
	@InjectMocks
	private RealisedCgtDtoServiceImpl realisedCgtDtoServiceImpl;

	@Mock
	private CgtIntegrationService cgtIntegrationService;

	final CgtKey cgtGroupBySecurityKey = new CgtKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0",
		new DateTime(),
		new DateTime().plusDays(10),
		"SECURITY");

	final CgtKey cgtGroupByAssetKey = new CgtKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0",
		new DateTime(),
		new DateTime().plusDays(10),
		"ASSET_TYPE");

	AssetImpl invAsset, invAsset1, invAsset2, asset1, asset2;
	CgtDataImpl cgtData1, cgtData2;

	@Before
	public void setup() throws Exception
	{
		asset1 = new AssetImpl();
		asset1.setAssetType(AssetType.MANAGED_FUND);
		asset1.setAssetName("Test asset");
		asset1.setAssetCode("0001");
		asset1.setAssetId("0001");

		asset2 = new AssetImpl();
		asset2.setAssetType(AssetType.MANAGED_FUND);
		asset2.setAssetName("Test asset 2");
		asset2.setAssetCode("0002");
		asset2.setAssetId("0002");

		cgtData1 = new CgtDataImpl();
		cgtData1.setQuantity(BigDecimal.TEN);
		cgtData1.setNetProceed(BigDecimal.ONE);
		cgtData1.setTaxCost(BigDecimal.ONE);
		cgtData1.setHoldingPeriod(BigDecimal.valueOf(111d));

		invAsset = new AssetImpl();
		invAsset.setAssetId("BTA0564AU");
		invAsset.setAssetCode("BTA0564AU");
		invAsset.setAssetType(AssetType.MANAGED_FUND);
		invAsset.setAssetName("Blackrock Wholesale Plus Australian Equity Opportunities Fund");

		invAsset1 = new AssetImpl();
		invAsset1.setAssetId("INV001");
		invAsset1.setAssetCode("INV001");
		invAsset1.setAssetName("ABC Managed fund");
		invAsset1.setAssetType(AssetType.MANAGED_FUND);

		invAsset2 = new AssetImpl();
		invAsset2.setAssetId("INV002");
		invAsset2.setAssetCode("INV002");
		invAsset2.setAssetName("DEF Managed fund");
		invAsset2.setAssetType(AssetType.MANAGED_FUND);

		cgtData2 = new CgtDataImpl();
		cgtData2.setQuantity(BigDecimal.ONE);
		cgtData2.setTaxCost(BigDecimal.ZERO);
	}

	@Test
	public void testGetRealisedCgtBySecurity_whenWrapCgtDataIsNull()
	{
		Mockito.when(cgtIntegrationService.loadRealisedCgtDetails(Mockito.any(String.class),
			Mockito.any(DateTime.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(null);
		CgtDto cgtDto = realisedCgtDtoServiceImpl.find(cgtGroupBySecurityKey, new ServiceErrorsImpl());

        Assert.assertNotNull(cgtDto);
        Assert.assertEquals(0, cgtDto.getCgtGroupDtoList().size());
	}

	@Test
	public void testGetRealisedCgtBySecurity_whenWrapCgtDataIsNotNull()
	{
		WrapCgtDataImpl response = new WrapCgtDataImpl();
		InvestmentCgtImpl invCgt = createCgtInvestment(invAsset, cgtData1);

		ManagedPortfolioCgtImpl mpCgt = createMpCgtInvestment(asset1, invCgt);
		List <InvestmentCgt> responseList = new ArrayList <>();
		responseList.add(mpCgt);
		response.setCgtData(responseList);

		Mockito.when(cgtIntegrationService.loadRealisedCgtDetails(Mockito.any(String.class),
			Mockito.any(DateTime.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(response);
		CgtDto cgtDto = realisedCgtDtoServiceImpl.find(cgtGroupBySecurityKey, new ServiceErrorsImpl());
		Assert.assertNotNull(cgtDto);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().size(), 1);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().size(), 1);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getGroupId(), invAsset.getAssetCode());
		Assert.assertEquals(((CgtMpSecurityDto)cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().get(0)).getParentInvId(),
			asset1.getAssetCode());
	}

	@Test
	public void testGetRealisedCgtBySecurity_whenWrapCgtDataParentInvestmentIsNull()
	{
		WrapCgtDataImpl response = new WrapCgtDataImpl();

		// Create InvestmentCgt.
		InvestmentCgtImpl invCgt = createCgtInvestment(asset1, cgtData1);

		List <InvestmentCgt> responseList = new ArrayList <>();
		responseList.add(invCgt);
		response.setCgtData(responseList);

		Mockito.when(cgtIntegrationService.loadRealisedCgtDetails(Mockito.any(String.class),
			Mockito.any(DateTime.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(response);
		CgtDto cgtDto = realisedCgtDtoServiceImpl.find(cgtGroupBySecurityKey, new ServiceErrorsImpl());
		Assert.assertNotNull(cgtDto);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().size(), 1);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().size(), 1);

		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getGroupId(), asset1.getAssetId());
	}

	@Test
	public void testGetRealisedCgtBySecurity_whenWrapCgtDataContainsTwoSameManagedFundAssets()
	{
		WrapCgtDataImpl response = new WrapCgtDataImpl();

		InvestmentCgtImpl invCgt1 = createCgtInvestment(asset1, cgtData1);
		InvestmentCgtImpl invCgt2 = createCgtInvestment(asset1, cgtData2);

		List <InvestmentCgt> responseList = new ArrayList <>();
		responseList.add(invCgt1);
		responseList.add(invCgt2);
		response.setCgtData(responseList);

		Mockito.when(cgtIntegrationService.loadRealisedCgtDetails(Mockito.any(String.class),
			Mockito.any(DateTime.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(response);
		CgtDto cgtDto = realisedCgtDtoServiceImpl.find(cgtGroupBySecurityKey, new ServiceErrorsImpl());
		Assert.assertNotNull(cgtDto);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().size(), 1);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().size(), 2);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getGroupId(), asset1.getAssetId());

	}

	@Test
	public void testGetRealisedCgtBySecurity_whenWrapCgtDataContainsTwoDifferentManagedFundAssets()
	{
		WrapCgtDataImpl response = new WrapCgtDataImpl();
		InvestmentCgtImpl invCgt1 = createCgtInvestment(asset1, cgtData1);
		InvestmentCgtImpl invCgt2 = createCgtInvestment(asset2, cgtData2);

		List <InvestmentCgt> responseList = new ArrayList <>();
		responseList.add(invCgt1);
		responseList.add(invCgt2);
		response.setCgtData(responseList);

		Mockito.when(cgtIntegrationService.loadRealisedCgtDetails(Mockito.any(String.class),
			Mockito.any(DateTime.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(response);
		CgtDto cgtDto = realisedCgtDtoServiceImpl.find(cgtGroupBySecurityKey, new ServiceErrorsImpl());
		Assert.assertNotNull(cgtDto);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().size(), 2);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().size(), 1);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(1).getCgtSecurities().size(), 1);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getGroupId(), asset1.getAssetId());
	}

	@Test
	public void testGetRealisedCgtBySecurity_whenWrapCgtDataContainsIMFAndManagedPortfolioWithManagedFund()
	{
		WrapCgtDataImpl response = new WrapCgtDataImpl();

		InvestmentCgt invCgt1 = createCgtInvestment(invAsset1, cgtData1);
		ManagedPortfolioCgtImpl mp1 = createMpCgtInvestment(asset1, invCgt1);

		InvestmentCgt invCgt2 = createCgtInvestment(invAsset2, cgtData2);

		List <InvestmentCgt> responseList = new ArrayList <>();
		responseList.add(mp1);
		responseList.add(invCgt2);
		response.setCgtData(responseList);

		Mockito.when(cgtIntegrationService.loadRealisedCgtDetails(Mockito.any(String.class),
			Mockito.any(DateTime.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(response);
		CgtDto cgtDto = realisedCgtDtoServiceImpl.find(cgtGroupBySecurityKey, new ServiceErrorsImpl());
		Assert.assertNotNull(cgtDto);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().size(), 2);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().size(), 1);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(1).getCgtSecurities().size(), 1);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(1).getGroupId(), invAsset2.getAssetId());
		Assert.assertEquals((cgtDto.getCgtGroupDtoList().get(1).getCgtSecurities().get(0)).getSecurityCode(),
			invAsset2.getAssetCode());
		Assert.assertEquals((cgtDto.getCgtGroupDtoList().get(1).getCgtSecurities().get(0)).getSecurityName(),
			invAsset2.getAssetName());
		Assert.assertEquals((cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().get(0)).getSecurityCode(),
			invAsset1.getAssetCode());
		Assert.assertEquals((cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().get(0)).getSecurityName(),
			invAsset1.getAssetName());
		Assert.assertEquals(((CgtMpSecurityDto)cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().get(0)).getParentInvId(),
			asset1.getAssetCode());
		Assert.assertEquals(((CgtMpSecurityDto)cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().get(0)).getParentInvName(),
			asset1.getAssetName());
	}

	@Test
	public void testGetRealisedCgtByAssetType_whenWrapCgtDataIsNotNull()
	{
		WrapCgtDataImpl response = new WrapCgtDataImpl();

		InvestmentCgtImpl invCgt = createCgtInvestment(invAsset, cgtData1);

		ManagedPortfolioCgtImpl mpCgt = createMpCgtInvestment(asset1, invCgt);
		List <InvestmentCgt> responseList = new ArrayList <>();
		responseList.add(mpCgt);
		response.setCgtData(responseList);

		Mockito.when(cgtIntegrationService.loadRealisedCgtDetails(Mockito.any(String.class),
			Mockito.any(DateTime.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(response);
		CgtDto cgtDto = realisedCgtDtoServiceImpl.find(cgtGroupByAssetKey, new ServiceErrorsImpl());
		Assert.assertNotNull(cgtDto);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().size(), 1);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().size(), 1);

		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getGroupId(), asset1.getAssetId());
		Assert.assertEquals(((CgtMpSecurityDto)cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().get(0)).getParentInvId(),
			asset1.getAssetCode());
	}

	private InvestmentCgtImpl createCgtInvestment(Asset asset, CgtBaseData data)
	{
		List <CgtBaseData> dataList = new ArrayList <>();
		dataList.add(data);

		InvestmentCgtImpl invCgt = new InvestmentCgtImpl();
		invCgt.setHolding(asset);
		invCgt.setCgtData(dataList);

		return invCgt;
	}

	private ManagedPortfolioCgtImpl createMpCgtInvestment(Asset asset, InvestmentCgt invCgt)
	{
		List <InvestmentCgt> dataList = new ArrayList <>();
		dataList.add(invCgt);

		ManagedPortfolioCgtImpl mp = new ManagedPortfolioCgtImpl();
		mp.setInvestment(asset);
		mp.setInvestmentCgtList(dataList);

		return mp;
	}

}
