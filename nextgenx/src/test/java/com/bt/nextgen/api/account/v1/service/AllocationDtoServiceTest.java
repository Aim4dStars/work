package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.DatedAccountKey;
import com.bt.nextgen.api.account.v2.service.TermDepositPresentationService;
import com.bt.nextgen.api.allocation.model.AllocationByAssetSectorDto;
import com.bt.nextgen.api.allocation.model.HoldingAllocationDto;
import com.bt.nextgen.api.allocation.model.TermDepositAllocationDto;
import com.bt.nextgen.api.allocation.service.AssetAllocationDtoServiceImpl;
import com.bt.nextgen.cms.service.CmsService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedFundAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedFundHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedPortfolioAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.WrapAccountValuationImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.HoldingKey;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
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
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class AllocationDtoServiceTest
{
	@InjectMocks
	private AssetAllocationDtoServiceImpl allocationDtoServiceImpl;

	@Mock
    private PortfolioIntegrationService portfolioIntegrationService;

	@Mock
    private StaticIntegrationService staticIntegrationService;

	@Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private TermDepositPresentationService termDepositPresentationService;

    @Mock
    private CmsService cmsService;

    private DatedAccountKey valuationKey = new DatedAccountKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0",
            new DateTime());

	private BrokerImpl broker = null;

	@Before
	public void setup() throws Exception
	{
		broker = new BrokerImpl(BrokerKey.valueOf("56789"), BrokerType.ADVISER);
		broker.setDealerKey(BrokerKey.valueOf("45677"));
	}

	@Test
	public void testGetAllocationFromPortfolio_whenAPortfolioHasNoAcccounts_thenAllocationIsEmpty()
	{
		WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
		valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));
		List <SubAccountValuation> subaccounts = new ArrayList <>();
		valuation.setSubAccountValuations(subaccounts);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(valuation);

		Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(broker);

		AllocationByAssetSectorDto allocationDto = (AllocationByAssetSectorDto)allocationDtoServiceImpl.find(valuationKey,
			new ServiceErrorsImpl());
		Assert.assertTrue(allocationDto.getSecurities().isEmpty());
	}

	@Test
	public void testGetAllocationFromPortfolio_whenAPortfolioHasACashManagementAccount_thenAllocationMatchCashManagement()
	{
		WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
		valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));

		List <SubAccountValuation> subAccounts = new ArrayList <>();
        CashAccountValuationImpl cashAccount = new CashAccountValuationImpl();
        CashHoldingImpl cashHolding = new CashHoldingImpl();
        cashHolding.setAccountName("accountName");
        cashHolding.setAvailableBalance(BigDecimal.valueOf(1));
        cashHolding.setMarketValue(BigDecimal.valueOf(2));
        cashHolding.setMarketValue(BigDecimal.valueOf(2));
        cashHolding.setAccruedIncome(BigDecimal.valueOf(3));
        cashHolding.setYield(BigDecimal.valueOf(4));
        List<AccountHolding> cashList = new ArrayList<>();
        cashList.add(cashHolding);
        cashAccount.addHoldings(cashList);
        subAccounts.add(cashAccount);

		valuation.setSubAccountValuations(subAccounts);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(valuation);

		Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(broker);

		AllocationByAssetSectorDto allocationDto = (AllocationByAssetSectorDto)allocationDtoServiceImpl.find(valuationKey,
			new ServiceErrorsImpl());
		Assert.assertTrue(allocationDto.getSecurities().size() == 1);
		HoldingAllocationDto details = allocationDto.getSecurities().get(0);
        Assert.assertEquals(details.getMarketValue().doubleValue(), 5.0, 0.05);
        Assert.assertEquals(details.getAllocationPercent().doubleValue(), 1.00, 0.05);
        Assert.assertEquals(details.getAssetSector(), "Cash");
		Assert.assertNotNull(details);
	}

	@Test
	public void testGetAllocationFromPortfolio_whenAPortfolioWithTermDeposits_thenAllocationMatchTermDeposits()
	{
		WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
		valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));
		List <SubAccountValuation> subAccounts = new ArrayList <>();
		TermDepositAccountValuationImpl tdAccount = new TermDepositAccountValuationImpl();

		TermDepositHoldingImpl tdHolding = new TermDepositHoldingImpl();
		tdHolding.setHoldingKey(HoldingKey.valueOf("accountId", "accountName"));
		tdHolding.setMarketValue(BigDecimal.valueOf(2));
        tdHolding.setAccruedIncome(BigDecimal.valueOf(3));
        tdHolding.setYield(BigDecimal.valueOf(4));
		tdHolding.setMaturityDate(new DateTime());
		tdHolding.setMaturityInstruction("7");
		AssetImpl asset = new AssetImpl();
		asset.setAssetId("20168");
		tdHolding.setAsset(asset);

        tdAccount.addHoldings(Collections.singletonList((AccountHolding) tdHolding));
		subAccounts.add(tdAccount);

		TermDepositAssetImpl termDepositAsset = new TermDepositAssetImpl();
		termDepositAsset.setAssetId("20168");
		termDepositAsset.setAssetName("BT");
		termDepositAsset.setGenericAssetId("20169");
		termDepositAsset.setBrand("80000064");

		valuation.setSubAccountValuations(subAccounts);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(valuation);

        TermDepositPresentation tdPres = new TermDepositPresentation();
        tdPres.setBrandName("BT Term Deposit");
        tdPres.setBrandClass("BT");
        tdPres.setTerm("6 months");
        tdPres.setPaymentFrequency(PaymentFrequency.AT_MATURITY.getDisplayName().toLowerCase());

        Mockito.when(
                termDepositPresentationService.getTermDepositPresentation(Mockito.any(AccountKey.class),
                        Mockito.any(String.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(tdPres);

		CodeImpl renewCode = new CodeImpl("7", "CONTR_AMOUNT", "Rollover Principal");

		Mockito.when(staticIntegrationService.loadCode(Mockito.eq(CodeCategory.TD_RENEW_MODE),
			Mockito.anyString(),
			Mockito.any(ServiceErrors.class))).thenReturn(renewCode);

		AllocationByAssetSectorDto allocationDto = (AllocationByAssetSectorDto)allocationDtoServiceImpl.find(valuationKey,
			new ServiceErrorsImpl());

		Assert.assertTrue(allocationDto.getSecurities().size() == 1);
		TermDepositAllocationDto termDepositAllocationDto = (TermDepositAllocationDto)allocationDto.getSecurities().get(0);
		Assert.assertNotNull(termDepositAllocationDto);
		Assert.assertEquals("BT Term Deposit", termDepositAllocationDto.getAssetName());
		Assert.assertEquals("6 months", termDepositAllocationDto.getTerm().toString());
		Assert.assertEquals(PaymentFrequency.AT_MATURITY.getDisplayName().toLowerCase(),
			termDepositAllocationDto.getPaymentFrequency());
	}

	@Test
	public void testGetManagedPortfoliosValuationFromPortfolio_whenAPortfolioWithManagedPortfolios_thenValuationHasMatchingManagedPortfolios()
	{
		WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
		valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));

		List <SubAccountValuation> subAccounts = new ArrayList <>();
		ManagedPortfolioAccountValuationImpl mpAccount = new ManagedPortfolioAccountValuationImpl();
        mpAccount.setSubAccountKey(SubAccountKey.valueOf("accountId"));
		List <AccountHolding> holdings = new ArrayList <>();

        AssetImpl mpAsset = getDefaultAsset(AssetType.MANAGED_PORTFOLIO);

		AssetImpl asset = getDefaultAsset(AssetType.SHARE);
        ManagedFundHoldingImpl holding = new ManagedFundHoldingImpl();
		holding.setAsset(asset);
		holding.setAvailableUnits(BigDecimal.valueOf(1));
		holding.setCost(BigDecimal.valueOf(2));
		holding.setUnitPrice(BigDecimal.valueOf(3));
		holding.setUnitPriceDate(new DateTime());
		holding.setUnits(BigDecimal.valueOf(4));
		holding.setYield(BigDecimal.valueOf(5));
		holding.setMarketValue(BigDecimal.valueOf(11.99));
        holding.setAccruedIncome(BigDecimal.valueOf(0.01));
		holdings.add(holding);

        mpAccount.setAsset(mpAsset);
        mpAccount.addHoldings(holdings);
		subAccounts.add(mpAccount);

		valuation.setSubAccountValuations(subAccounts);

		Mockito.when(assetIntegrationService.loadAsset(Mockito.any(String.class), Mockito.any(ServiceErrors.class)))
			.thenReturn(asset);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(valuation);
		Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		AllocationByAssetSectorDto allocationDto = (AllocationByAssetSectorDto)allocationDtoServiceImpl.find(valuationKey,
			new ServiceErrorsImpl());

		Assert.assertTrue(allocationDto.getSecurities().size() == 2);
		HoldingAllocationDto holdingDto = allocationDto.getSecurities().get(1);
		Assert.assertTrue(holdingDto.getAllocationPercent().doubleValue() == 0.999167);
		Assert.assertEquals(holdingDto.getAssetName(), asset.getAssetName());
		Assert.assertEquals(holdingDto.getAssetType(), asset.getAssetType().name());
		Assert.assertEquals(holdingDto.getMarketValue(), holding.getMarketValue());
	}

	@Test
	public void testGetManagedFundAllocation_NoError()
	{
		WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
		valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));

		List <SubAccountValuation> subAccounts = new ArrayList <>();
		ManagedFundAccountValuationImpl mfAcc = new ManagedFundAccountValuationImpl();
		List <AccountHolding> holdings = new ArrayList <>();

		AssetImpl asset = getDefaultAsset(AssetType.MANAGED_FUND);
        ManagedFundHoldingImpl holding = new ManagedFundHoldingImpl();
		holding.setHoldingKey(HoldingKey.valueOf("111", "Test holding"));
		holding.setAsset(asset);
		holding.setAvailableUnits(BigDecimal.valueOf(1));
		holding.setCost(BigDecimal.valueOf(2));
		holding.setUnitPrice(BigDecimal.valueOf(3));
		holding.setUnitPriceDate(new DateTime());
		holding.setUnits(BigDecimal.valueOf(4));
		holding.setYield(BigDecimal.valueOf(5));
		holding.setMarketValue(BigDecimal.valueOf(11.99));
		holdings.add(holding);

        mfAcc.addHoldings(holdings);
		subAccounts.add(mfAcc);

		valuation.setSubAccountValuations(subAccounts);

		Mockito.when(assetIntegrationService.loadAsset(Mockito.any(String.class), Mockito.any(ServiceErrors.class)))
			.thenReturn(asset);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(valuation);
		Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		AllocationByAssetSectorDto allocationDto = (AllocationByAssetSectorDto)allocationDtoServiceImpl.find(valuationKey,
			new ServiceErrorsImpl());

		Assert.assertTrue(allocationDto.getSecurities().size() == 1);
		HoldingAllocationDto holdingDto = allocationDto.getSecurities().get(0);
		Assert.assertTrue(holdingDto.getAllocationPercent().doubleValue() == 1.0d);
		// For IMF, the assetName should be set to the holding-name (i.e. underlying ManagedFund's name 
		// instead of the Asset name. 
		Assert.assertEquals(holdingDto.getAssetName(), holding.getHoldingKey().getName());
		Assert.assertEquals(holdingDto.getAssetType(), asset.getAssetType().name());
		Assert.assertEquals(holdingDto.getMarketValue(), holding.getMarketValue());
	}

	@Test
	public void testManagedFundAllocationWithManagedPortfolio_NoError()
	{
		WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
		valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));

		AssetImpl asset = getDefaultAsset(AssetType.MANAGED_FUND);
        ManagedFundHoldingImpl holding = new ManagedFundHoldingImpl();

		holding.setHoldingKey(HoldingKey.valueOf("111", "Test holding"));
		holding.setAsset(asset);
		holding.setAvailableUnits(BigDecimal.valueOf(1));
		holding.setCost(BigDecimal.valueOf(2));
		holding.setUnitPrice(BigDecimal.valueOf(3));
		holding.setUnitPriceDate(new DateTime());
		holding.setUnits(BigDecimal.valueOf(4));
		holding.setYield(BigDecimal.valueOf(5));
		holding.setMarketValue(BigDecimal.valueOf(11.99));

		List <SubAccountValuation> subAccounts = new ArrayList <>();
		ManagedFundAccountValuationImpl mfAcc = new ManagedFundAccountValuationImpl();
		List <AccountHolding> holdings = new ArrayList <>();
		holdings.add(holding);
        mfAcc.addHoldings(holdings);
		subAccounts.add(mfAcc);

		// MP setup
		ManagedPortfolioAccountValuationImpl mpAccount = new ManagedPortfolioAccountValuationImpl();
        mpAccount.setSubAccountKey(SubAccountKey.valueOf("MPaccountId"));
		mpAccount.setAsset(asset);
        mpAccount.addHoldings(Collections.singletonList((AccountHolding) holding));
		subAccounts.add(mpAccount);

		valuation.setSubAccountValuations(subAccounts);

		Mockito.when(assetIntegrationService.loadAsset(Mockito.any(String.class), Mockito.any(ServiceErrors.class)))
			.thenReturn(asset);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(valuation);
		Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		AllocationByAssetSectorDto allocationDto = (AllocationByAssetSectorDto)allocationDtoServiceImpl.find(valuationKey,
			new ServiceErrorsImpl());

		Assert.assertTrue(allocationDto.getSecurities().size() == 1);
		HoldingAllocationDto details = allocationDto.getSecurities().get(0);
		// 2 investments hold this managed fund
		Assert.assertTrue(details.getInvestments().size() == 2);

		// When grouped, the dto-name should be the investment-asset name.
		Assert.assertEquals(details.getAssetName(), asset.getAssetName());
		// For Underlying investment,  investment-name should be the holding-name.		
		Assert.assertEquals(details.getInvestments().get(1).getAssetName(), holding.getHoldingKey().getName());

		// Allocation values on the security needs to be summation of each underlying holdings.
		Assert.assertTrue(details.getAllocationPercent().doubleValue() == 1.0d);
		Assert.assertTrue(details.getMarketValue().doubleValue() == holding.getMarketValue().doubleValue() * 2);
		Assert.assertTrue(details.getQuantity().doubleValue() == 4 * 2);
	}

	private AssetImpl getDefaultAsset(AssetType assetType)
	{
		AssetImpl asset = new AssetImpl();
        asset.setAssetClass(AssetClass.CASH);
		asset.setAssetCode("assetCode");
		asset.setAssetId("assetId");
		asset.setAssetName("assetName");
		asset.setAssetType(assetType);
		asset.setBrand("brand");
		asset.setIndustrySector("industrySector");
		asset.setIndustryType("industryType");
		return asset;
	}
}
