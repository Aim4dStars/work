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
public class UnrealisedCgtDtoServiceTest
{
	@InjectMocks
	private UnrealisedCgtDtoServiceImpl unrealisedCgtDtoServiceImpl;

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

	AssetImpl asset;
	AssetImpl invAsset;
	CgtDataImpl cgtData;

	@Before
	public void setup() throws Exception
	{
		asset = new AssetImpl();
		asset.setAssetType(AssetType.MANAGED_PORTFOLIO);
		asset.setAssetName("Test asset");
		asset.setAssetId("1111");
		asset.setAssetCode("MP0001");

		cgtData = new CgtDataImpl();
		cgtData.setQuantity(BigDecimal.TEN);
		cgtData.setNetProceed(BigDecimal.ONE);
		cgtData.setTaxCost(BigDecimal.ONE);
		cgtData.setHoldingPeriod(BigDecimal.valueOf(111d));

		invAsset = new AssetImpl();
		invAsset.setAssetId("BTA0564AU");
		invAsset.setAssetCode("BTA0564AU");
		invAsset.setAssetType(AssetType.MANAGED_FUND);
		invAsset.setAssetName("Blackrock Wholesale Plus Australian Equity Opportunities Fund");
	}

	@Test
	public void testGetUnrealisedCgtBySecurity_whenWrapCgtDataIsNull()
	{
		Mockito.when(cgtIntegrationService.loadUnrealisedCgtDetails(Mockito.any(String.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(null);
		CgtDto cgtDto = unrealisedCgtDtoServiceImpl.find(cgtGroupBySecurityKey, new ServiceErrorsImpl());
        Assert.assertNotNull(cgtDto);
        Assert.assertEquals(0, cgtDto.getCgtGroupDtoList().size());
	}

	@Test
	public void testGetUnrealisedCgtByAsset_whenWrapCgtDataIsNotNull()
	{
		WrapCgtDataImpl response = new WrapCgtDataImpl();

		InvestmentCgtImpl invCgt = createCgtInvestment(invAsset, cgtData);
		ManagedPortfolioCgtImpl mpCgt = createMpCgtInvestment(asset, invCgt);

		List <InvestmentCgt> responseList = new ArrayList <>();
		responseList.add(mpCgt);
		response.setUnrealisedCgtData(responseList);

		Mockito.when(cgtIntegrationService.loadUnrealisedCgtDetails(Mockito.any(String.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(response);

		CgtDto cgtDto = unrealisedCgtDtoServiceImpl.find(cgtGroupByAssetKey, new ServiceErrorsImpl());

		Assert.assertNotNull(cgtDto);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().size(), 1);

		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getGroupId(), asset.getAssetId());
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getGroupCode(), asset.getAssetCode());
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getGroupName(), asset.getAssetName());

		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getAmount(), BigDecimal.ONE);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getQuantity(), new Integer(10));
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getTaxAmount(), BigDecimal.ONE);

		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().size(), 1);
		Assert.assertEquals(((CgtMpSecurityDto)cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().get(0)).getParentInvId(),
			asset.getAssetId());
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().get(0).getSecurityCode(),
			invAsset.getAssetId());
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().get(0).getSecurityName(),
			invAsset.getAssetName());
	}

	@Test
	public void testGetUnrealisedCgtBySecurity_whenWrapCgtDataIsNotNull()
	{
		WrapCgtDataImpl response = new WrapCgtDataImpl();

		InvestmentCgtImpl invCgt = createCgtInvestment(invAsset, cgtData);

		ManagedPortfolioCgtImpl mpCgt = createMpCgtInvestment(asset, invCgt);
		List <InvestmentCgt> responseList = new ArrayList <>();
		responseList.add(mpCgt);
		response.setUnrealisedCgtData(responseList);

		Mockito.when(cgtIntegrationService.loadUnrealisedCgtDetails(Mockito.any(String.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(response);

		CgtDto cgtDto = unrealisedCgtDtoServiceImpl.find(cgtGroupBySecurityKey, new ServiceErrorsImpl());

		Assert.assertNotNull(cgtDto);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().size(), 1);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().size(), 1);

		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getGroupId(), invAsset.getAssetId());
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getGroupCode(), invAsset.getAssetCode());
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getGroupName(), invAsset.getAssetName());

		Assert.assertEquals(((CgtMpSecurityDto)cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().get(0)).getParentInvId(),
			asset.getAssetId());
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().get(0).getSecurityCode(),
			invAsset.getAssetId());
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().get(0).getSecurityName(),
			invAsset.getAssetName());
	}

	@Test
	public void testGetUnrealisedCgtBySecurity_whenWrapCgtDataInvestmentIdIsNull()
	{
		WrapCgtDataImpl response = new WrapCgtDataImpl();

		InvestmentCgtImpl invCgt = createCgtInvestment(invAsset, cgtData);

		List <InvestmentCgt> responseList = new ArrayList <>();
		responseList.add(invCgt);
		response.setUnrealisedCgtData(responseList);

		Mockito.when(cgtIntegrationService.loadUnrealisedCgtDetails(Mockito.any(String.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(response);

		CgtDto cgtDto = unrealisedCgtDtoServiceImpl.find(cgtGroupBySecurityKey, new ServiceErrorsImpl());

		Assert.assertNotNull(cgtDto);
		Assert.assertEquals(cgtDto.getCgtGroupDtoList().size(), 1);

		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getGroupId(), invAsset.getAssetCode());

		Assert.assertEquals(cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().size(), 1);
		Assert.assertEquals((cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().get(0)).getSecurityCode(),
			invAsset.getAssetCode());
		Assert.assertEquals((cgtDto.getCgtGroupDtoList().get(0).getCgtSecurities().get(0)).getSecurityName(),
			invAsset.getAssetName());
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
