package com.bt.nextgen.api.income.v1.service;

import com.bt.nextgen.api.account.v2.model.DatedAccountKey;
import com.bt.nextgen.api.account.v2.service.TermDepositPresentationService;
import com.bt.nextgen.api.income.v1.model.CashIncomeDetailsDto;
import com.bt.nextgen.api.income.v1.model.CashIncomeDto;
import com.bt.nextgen.api.income.v1.model.DistributionIncomeDto;
import com.bt.nextgen.api.income.v1.model.DividendIncomeDto;
import com.bt.nextgen.api.income.v1.model.FeeRebateIncomeDto;
import com.bt.nextgen.api.income.v1.model.IncomeDetailsDto;
import com.bt.nextgen.api.income.v1.model.IncomeDetailsKey;
import com.bt.nextgen.api.income.v1.model.IncomeDetailsType;
import com.bt.nextgen.api.income.v1.model.ManagedPortfolioIncomeDetailsDto;
import com.bt.nextgen.api.income.v1.model.TermDepositIncomeDetailsDto;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.ManagedPortfolioAssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.avaloq.income.CashIncomeImpl;
import com.bt.nextgen.service.avaloq.income.DistributionIncomeImpl;
import com.bt.nextgen.service.avaloq.income.DividendIncomeImpl;
import com.bt.nextgen.service.avaloq.income.HoldingIncomeDetailsImpl;
import com.bt.nextgen.service.avaloq.income.SubAccountIncomeDetailsImpl;
import com.bt.nextgen.service.avaloq.income.TermDepositIncomeImpl;
import com.bt.nextgen.service.avaloq.income.WrapAccountIncomeDetailsImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedPortfolioAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.WrapAccountValuationImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.income.HoldingIncomeDetails;
import com.bt.nextgen.service.integration.income.Income;
import com.bt.nextgen.service.integration.income.IncomeIntegrationService;
import com.bt.nextgen.service.integration.income.SubAccountIncomeDetails;
import com.bt.nextgen.service.integration.income.WrapAccountIncomeDetails;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
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
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class IncomeDetailsDtoServiceTest {
    @InjectMocks
    private IncomeDetailsDtoServiceImpl incomeDTOServiceImpl;

    @Mock
    private PortfolioIntegrationService portfolioIntegrationService;

    @Mock
    private IncomeIntegrationService incomeIntegrationService;

    @Mock
    private TermDepositPresentationService termDepositPresentationService;

    private DatedAccountKey valuationKey = new DatedAccountKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0",
            new DateTime());
    private IncomeDetailsKey incomeReceivedKey = new IncomeDetailsKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0",
            IncomeDetailsType.RECEIVED, new DateTime(), new DateTime().plusDays(1));
    private IncomeDetailsKey incomeAccruedKey = new IncomeDetailsKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0",
            IncomeDetailsType.ACCRUED, new DateTime(), new DateTime().plusDays(1));

    @Before
    public void setup() {

        TermDepositPresentation tdPres = new TermDepositPresentation();
        tdPres.setBrandName("BT Term Deposit");
        tdPres.setBrandClass("BT");
        tdPres.setTerm("6 months");
        tdPres.setPaymentFrequency(PaymentFrequency.AT_MATURITY.getDisplayName().toLowerCase());

        Mockito.when(termDepositPresentationService.getTermDepositPresentation(Mockito.any(AccountKey.class),
                Mockito.any(String.class), Mockito.any(ServiceErrors.class))).thenReturn(tdPres);
    }

    @Test
    public void testGetIncomeAccruedFromValuation_whenCashDividendIsNull() {
        // Valuation with cash account
        WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));
        List<SubAccountValuation> subAccounts = new ArrayList<>();
        CashAccountValuationImpl cashAccount = new CashAccountValuationImpl();
        CashHoldingImpl cashHolding = new CashHoldingImpl();
        cashHolding.setAccountName("accountName");
        cashHolding.setAvailableBalance(BigDecimal.valueOf(1));
        cashHolding.setMarketValue(BigDecimal.valueOf(2));
        cashHolding.setMarketValue(BigDecimal.valueOf(2));
        cashHolding.setAccruedIncome(BigDecimal.valueOf(3));
        cashHolding.setYield(BigDecimal.valueOf(4));
        cashHolding.setNextInterestDate(new DateTime().plusMonths(2));
        List<AccountHolding> cashList = new ArrayList<>();
        cashList.add(cashHolding);
        cashAccount.addHoldings(cashList);

        subAccounts.add(cashAccount);

        valuation.setSubAccountValuations(subAccounts);
        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);

        // Accrued income response service
        List<WrapAccountIncomeDetails> incomeResponse = new ArrayList<>();
        Mockito.when(incomeIntegrationService.loadCashDividendDetails(Mockito.any(AccountKey.class), Mockito.any(DateTime.class),
                Mockito.anyString(), Mockito.any(ServiceErrors.class))).thenReturn(incomeResponse);

        IncomeDetailsDto dto = incomeDTOServiceImpl.find(incomeAccruedKey, new ServiceErrorsImpl());
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getCashIncomeDetails());
        Assert.assertNotNull(dto.getManagedPortfolioIncomeDetails());

        ManagedPortfolioIncomeDetailsDto mpDto = dto.getManagedPortfolioIncomeDetails();
        Assert.assertTrue(BigDecimal.ZERO.equals(mpDto.getManagedPortfolioIncomeTotal()));

        CashIncomeDto cashIncomeDto = dto.getCashIncomeDetails().getIncomes().get(0);
        Assert.assertTrue(cashIncomeDto.getAmount().doubleValue() == 3d);
    }

    @Test
    public void testGetIncomeAccruedFromValuation_whenCashInMPIsNotNull() {
        // Valuation with cash account
        WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));
        List<SubAccountValuation> subAccounts = new ArrayList<>();
        ManagedPortfolioAccountValuationImpl mpAccount = new ManagedPortfolioAccountValuationImpl();

        AssetImpl mpAsset = new ManagedPortfolioAssetImpl();
        mpAsset.setAssetType(AssetType.MANAGED_PORTFOLIO);

        mpAccount.setSubAccountKey(SubAccountKey.valueOf("accountId"));
        mpAccount.setAsset(mpAsset);

        CashHoldingImpl cashHolding = new CashHoldingImpl();
        AssetImpl cashAsset = new AssetImpl();
        cashAsset.setAssetType(AssetType.CASH);
        cashAsset.setAssetName("Mp cash");
        cashAsset.setAssetCode("MP-001");
        cashHolding.setAsset(cashAsset);
        cashHolding.setAccruedIncome(BigDecimal.valueOf(99.90d));
        cashHolding.setNextInterestDate(new DateTime());
        List<AccountHolding> holdings = new ArrayList<>();
        holdings.add(cashHolding);
        mpAccount.addHoldings(holdings);

        subAccounts.add(mpAccount);

        valuation.setSubAccountValuations(subAccounts);
        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);

        // Accrued income response service
        List<WrapAccountIncomeDetails> incomeResponse = new ArrayList<>();
        Mockito.when(incomeIntegrationService.loadCashDividendDetails(Mockito.any(AccountKey.class), Mockito.any(DateTime.class),
                Mockito.anyString(), Mockito.any(ServiceErrors.class))).thenReturn(incomeResponse);

        IncomeDetailsDto dto = incomeDTOServiceImpl.find(incomeAccruedKey, new ServiceErrorsImpl());
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getManagedPortfolioIncomeDetails());

        ManagedPortfolioIncomeDetailsDto mpDto = dto.getManagedPortfolioIncomeDetails();
        Assert.assertTrue(mpDto.getCashIncomes().size() == 1);
        CashIncomeDto cashIncomeDto = mpDto.getCashIncomes().get(0);
        Assert.assertTrue(cashIncomeDto.getAmount().equals(cashHolding.getAccruedIncome()));
    }

    @Test
    public void testGetIncomeAccrued_whenDividendIsNotNull() {
        List<WrapAccountIncomeDetails> incomeResponse = new ArrayList<>();
        WrapAccountIncomeDetailsImpl a = new WrapAccountIncomeDetailsImpl();

        // Dividend income
        DividendIncomeImpl divIncome = new DividendIncomeImpl();
        divIncome.setAmount(BigDecimal.valueOf(100d));
        divIncome.setQuantity(BigDecimal.valueOf(20));
        List<Income> divIncomeList = new ArrayList<>();
        divIncomeList.add(divIncome);

        HoldingIncomeDetailsImpl divHoldingIncome = new HoldingIncomeDetailsImpl();
        divHoldingIncome.addIncomes(divIncomeList);
        AssetImpl asset = new AssetImpl();
        asset.setAssetName("Test asset");
        asset.setAssetCode("0001");
        asset.setAssetType(AssetType.SHARE);
        divHoldingIncome.setAsset(asset);

        List<HoldingIncomeDetails> holdingIncomes = new ArrayList<>();
        holdingIncomes.add(divHoldingIncome);

        SubAccountIncomeDetailsImpl mpDetails = new SubAccountIncomeDetailsImpl();
        mpDetails.setAssetType(AssetType.MANAGED_PORTFOLIO);
        mpDetails.addIncomes(holdingIncomes);

        List<SubAccountIncomeDetails> accounts = new ArrayList<>();
        accounts.add(mpDetails);
        a.setSubAccountIncomeDetailsList(accounts);
        incomeResponse.add(a);

        Mockito.when(incomeIntegrationService.loadCashDividendDetails(Mockito.any(AccountKey.class), Mockito.any(DateTime.class),
                Mockito.anyString(), Mockito.any(ServiceErrors.class))).thenReturn(incomeResponse);

        IncomeDetailsDto dto = incomeDTOServiceImpl.find(incomeAccruedKey, new ServiceErrorsImpl());
        Assert.assertNotNull(dto);
        Assert.assertTrue(dto.getDividendTotal().doubleValue() == 100d);
        Assert.assertTrue(dto.getDistributionTotal().doubleValue() == 0d);
        Assert.assertTrue(dto.getFrankedDividendTotal().doubleValue() == 0d);

        DividendIncomeDto div = dto.getManagedPortfolioIncomeDetails().getDividends().get(0);
        Assert.assertTrue(div.getQuantity().equals(divIncome.getQuantity()));
    }

    @Test
    public void testGetIncomeAccrued_whenDistributionIsNotNull() {
        List<WrapAccountIncomeDetails> incomeResponse = new ArrayList<>();
        WrapAccountIncomeDetailsImpl a = new WrapAccountIncomeDetailsImpl();

        // Distribution income
        DistributionIncomeImpl divIncome = new DistributionIncomeImpl();
        divIncome.setAmount(BigDecimal.valueOf(100d));
        divIncome.setQuantity(BigDecimal.valueOf(20));
        divIncome.setExecutionDate(new DateTime().minusMonths(2));
        divIncome.setPaymentDate(new DateTime().minusMonths(1));
        List<Income> divIncomeList = new ArrayList<>();
        divIncomeList.add(divIncome);

        HoldingIncomeDetailsImpl distHoldingIncome = new HoldingIncomeDetailsImpl();
        distHoldingIncome.addIncomes(divIncomeList);
        AssetImpl asset = new AssetImpl();
        asset.setAssetName("Test asset");
        asset.setAssetCode("0001");
        asset.setAssetType(AssetType.MANAGED_FUND);
        distHoldingIncome.setAsset(asset);

        List<HoldingIncomeDetails> holdingIncomes = new ArrayList<>();
        holdingIncomes.add(distHoldingIncome);

        SubAccountIncomeDetailsImpl mpDetails = new SubAccountIncomeDetailsImpl();
        mpDetails.setAssetType(AssetType.MANAGED_PORTFOLIO);
        mpDetails.addIncomes(holdingIncomes);

        List<SubAccountIncomeDetails> accounts = new ArrayList<>();
        accounts.add(mpDetails);
        a.setSubAccountIncomeDetailsList(accounts);
        incomeResponse.add(a);

        Mockito.when(incomeIntegrationService.loadCashDividendDetails(Mockito.any(AccountKey.class), Mockito.any(DateTime.class),
                Mockito.anyString(), Mockito.any(ServiceErrors.class))).thenReturn(incomeResponse);

        IncomeDetailsDto dto = incomeDTOServiceImpl.find(incomeAccruedKey, new ServiceErrorsImpl());
        Assert.assertNotNull(dto);
        Assert.assertTrue(dto.getDividendTotal().doubleValue() == 0d);
        Assert.assertTrue(dto.getDistributionTotal().doubleValue() == 100d);

        // Verify details in the dto have been mapped correctly.
        DistributionIncomeDto dist = dto.getManagedPortfolioIncomeDetails().getDistributions().get(0);
        DateTime executionDate = dist.getExecutionDate();
        Assert.assertTrue(executionDate.equals(divIncome.getExecutionDate()));
        Assert.assertTrue(dist.getPaymentDate().equals(divIncome.getPaymentDate()));
        Assert.assertTrue(dist.getQuantity().equals(divIncome.getQuantity()));
    }

    @Test
    public void testGetIncomeAccrued_whenManagedFundisNotNull() {

        List<WrapAccountIncomeDetails> incomeResponse = new ArrayList<>();
        WrapAccountIncomeDetailsImpl a = new WrapAccountIncomeDetailsImpl();

        // Managed fund income
        List<Income> disIncomeList = new ArrayList<>();
        DistributionIncomeImpl disIncome = new DistributionIncomeImpl();
        disIncome.setAmount(BigDecimal.valueOf(100d));
        disIncome.setQuantity(BigDecimal.valueOf(20));
        disIncome.setExecutionDate(new DateTime().minusMonths(2));
        disIncome.setPaymentDate(new DateTime().minusMonths(1));
        disIncomeList.add(disIncome);

        HoldingIncomeDetailsImpl disHoldingIncome = new HoldingIncomeDetailsImpl();
        disHoldingIncome.addIncomes(disIncomeList);
        AssetImpl asset = new AssetImpl();
        asset.setAssetName("Test asset");
        asset.setAssetCode("0001");
        asset.setAssetType(AssetType.MANAGED_FUND);
        disHoldingIncome.setAsset(asset);

        List<HoldingIncomeDetails> holdingIncomes = new ArrayList<>();
        holdingIncomes.add(disHoldingIncome);

        SubAccountIncomeDetailsImpl mfDetails = new SubAccountIncomeDetailsImpl();
        mfDetails.setAssetType(AssetType.MANAGED_FUND);
        mfDetails.addIncomes(holdingIncomes);

        List<SubAccountIncomeDetails> accounts = new ArrayList<>();
        accounts.add(mfDetails);
        a.setSubAccountIncomeDetailsList(accounts);
        incomeResponse.add(a);

        Mockito.when(incomeIntegrationService.loadCashDividendDetails(Mockito.any(AccountKey.class), Mockito.any(DateTime.class),
                Mockito.anyString(), Mockito.any(ServiceErrors.class))).thenReturn(incomeResponse);

        IncomeDetailsDto dto = incomeDTOServiceImpl.find(incomeAccruedKey, new ServiceErrorsImpl());
        Assert.assertNotNull(dto);
        Assert.assertTrue(dto.getDividendTotal().doubleValue() == 0d);

        // Verify details in the dto have been mapped correctly.
        DistributionIncomeDto distributionIncomeDto = dto.getManagedFundIncomeDetails().getDistributions().get(0);
        DateTime executionDate = distributionIncomeDto.getExecutionDate();
        Assert.assertTrue(executionDate.equals(disIncome.getExecutionDate()));
        Assert.assertTrue(distributionIncomeDto.getPaymentDate().equals(disIncome.getPaymentDate()));
        Assert.assertTrue(distributionIncomeDto.getQuantity().equals(disIncome.getQuantity()));
        Assert.assertTrue(distributionIncomeDto.getAmount().equals(disIncome.getAmount()));
    }

    @Test
    public void testGetIncomeAccrued_whenSharesisNotNull() {
        List<WrapAccountIncomeDetails> incomeResponse = new ArrayList<>();
        WrapAccountIncomeDetailsImpl a = new WrapAccountIncomeDetailsImpl();

        List<Income> disIncomeList = new ArrayList<>();
        DistributionIncomeImpl disIncome = new DistributionIncomeImpl();
        disIncome.setAmount(BigDecimal.valueOf(50d));
        disIncome.setQuantity(BigDecimal.valueOf(10));
        disIncome.setExecutionDate(new DateTime().minusMonths(2));
        disIncome.setPaymentDate(new DateTime().minusMonths(1));
        disIncomeList.add(disIncome);
        HoldingIncomeDetailsImpl disHoldingIncome = new HoldingIncomeDetailsImpl();
        disHoldingIncome.addIncomes(disIncomeList);
        AssetImpl asset = new AssetImpl();
        asset.setAssetName("Test asset");
        asset.setAssetCode("0001");
        asset.setAssetType(AssetType.SHARE);
        disHoldingIncome.setAsset(asset);

        List<Income> divIncomeList = new ArrayList<>();
        DividendIncomeImpl divIncome = new DividendIncomeImpl();
        divIncome.setAmount(BigDecimal.valueOf(150d));
        divIncome.setFrankedDividend(BigDecimal.valueOf(70d));
        divIncome.setUnfrankedDividend(BigDecimal.valueOf(80d));
        divIncome.setQuantity(BigDecimal.valueOf(20));
        divIncome.setExecutionDate(new DateTime().minusMonths(2));
        divIncome.setPaymentDate(new DateTime().minusMonths(1));
        divIncomeList.add(divIncome);
        HoldingIncomeDetailsImpl divHoldingIncome = new HoldingIncomeDetailsImpl();
        divHoldingIncome.addIncomes(divIncomeList);
        disHoldingIncome.setAsset(asset);

        List<HoldingIncomeDetails> holdingIncomes = new ArrayList<>();
        holdingIncomes.add(disHoldingIncome);
        holdingIncomes.add(divHoldingIncome);

        SubAccountIncomeDetailsImpl shareDetails = new SubAccountIncomeDetailsImpl();
        shareDetails.setAssetType(AssetType.SHARE);
        shareDetails.addIncomes(holdingIncomes);

        List<SubAccountIncomeDetails> accounts = new ArrayList<>();
        accounts.add(shareDetails);
        a.setSubAccountIncomeDetailsList(accounts);
        incomeResponse.add(a);

        Mockito.when(incomeIntegrationService.loadCashDividendDetails(Mockito.any(AccountKey.class), Mockito.any(DateTime.class),
                Mockito.anyString(), Mockito.any(ServiceErrors.class))).thenReturn(incomeResponse);

        IncomeDetailsDto dto = incomeDTOServiceImpl.find(incomeAccruedKey, new ServiceErrorsImpl());
        Assert.assertNotNull(dto);
        Assert.assertEquals(150d, dto.getDividendTotal().doubleValue(), 0.0001);
        Assert.assertEquals(50d, dto.getDistributionTotal().doubleValue(), 0.0001);

        // Verify details in the dto have been mapped correctly.
        DistributionIncomeDto distrIncome = dto.getShareIncomeDetails().getDistributions().get(0);
        Assert.assertEquals(disIncome.getAmount(), distrIncome.getAmount());

        DividendIncomeDto dividIncome = dto.getShareIncomeDetails().getDividends().get(0);
        Assert.assertEquals(70d, dividIncome.getFrankedDividend().doubleValue(), 0.0001);
        Assert.assertEquals(80d, dividIncome.getUnfrankedDividend().doubleValue(), 0.0001);
    }

    @Test
    public void testGetIncomeReceived_whenCashIncomeIsNull() {

        List<WrapAccountIncomeDetails> incomeResponse = new ArrayList<>();
        Mockito.when(incomeIntegrationService.loadIncomeReceivedDetails(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(incomeResponse);

        IncomeDetailsDto dto = incomeDTOServiceImpl.find(incomeReceivedKey, new ServiceErrorsImpl());
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getCashIncomeDetails());
        Assert.assertNotNull(dto.getManagedPortfolioIncomeDetails());

        ManagedPortfolioIncomeDetailsDto mpDto = dto.getManagedPortfolioIncomeDetails();
        Assert.assertTrue(BigDecimal.ZERO.equals(mpDto.getCashIncomesTotal()));
        Assert.assertTrue(dto.getCashIncomeDetails().getIncomes().isEmpty());
    }

    @Test
    public void testGetIncomeReceived_whenCashIncomeIsNotNull() {

        List<WrapAccountIncomeDetails> incomeResponse = new ArrayList<>();
        WrapAccountIncomeDetailsImpl wrapAccountIncomeDetails = new WrapAccountIncomeDetailsImpl();
        Mockito.when(incomeIntegrationService.loadIncomeReceivedDetails(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(incomeResponse);

        List<Income> cashIncomeList = new ArrayList<>();
        CashIncomeImpl cashIncome = new CashIncomeImpl();
        cashIncome.setAmount(BigDecimal.valueOf(100d));
        cashIncomeList.add(cashIncome);

        HoldingIncomeDetailsImpl cashHolding = new HoldingIncomeDetailsImpl();
        cashHolding.addIncomes(cashIncomeList);
        List<HoldingIncomeDetails> cashHoldingList = new ArrayList<>();
        cashHoldingList.add(cashHolding);

        SubAccountIncomeDetailsImpl cashAccount = new SubAccountIncomeDetailsImpl();
        cashAccount.setAssetType(AssetType.CASH);
        cashAccount.addIncomes(cashHoldingList);

        List<SubAccountIncomeDetails> accounts = new ArrayList<>();
        accounts.add(cashAccount);
        wrapAccountIncomeDetails.setSubAccountIncomeDetailsList(accounts);
        incomeResponse.add(wrapAccountIncomeDetails);

        IncomeDetailsDto dto = incomeDTOServiceImpl.find(incomeReceivedKey, new ServiceErrorsImpl());
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getCashIncomeDetails());

        CashIncomeDetailsDto cashIncomeDto = dto.getCashIncomeDetails();
        Assert.assertTrue(cashIncomeDto.getIncomeTotal().doubleValue() == 100d);
        Assert.assertTrue(dto.getCashIncomeDetails().getIncomes().get(0).getAmount().doubleValue() == 100d);
    }

    @Test
    public void testGetIncomeReceived_whenTermDepositIncomeIsNotNull() {
        List<WrapAccountIncomeDetails> incomeResponse = new ArrayList<>();
        WrapAccountIncomeDetailsImpl wrapAccountIncomeDetails = new WrapAccountIncomeDetailsImpl();
        Mockito.when(incomeIntegrationService.loadIncomeReceivedDetails(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(incomeResponse);

        List<Income> tdIncomeList = new ArrayList<>();
        TermDepositIncomeImpl tdIncome = new TermDepositIncomeImpl();
        tdIncome.setInterest(BigDecimal.valueOf(100d));
        tdIncomeList.add(tdIncome);

        HoldingIncomeDetailsImpl tdHolding = new HoldingIncomeDetailsImpl();
        tdHolding.addIncomes(tdIncomeList);
        List<HoldingIncomeDetails> tdHoldingList = new ArrayList<>();
        tdHoldingList.add(tdHolding);

        SubAccountIncomeDetailsImpl tdAccount = new SubAccountIncomeDetailsImpl();
        tdAccount.setAssetType(AssetType.TERM_DEPOSIT);
        tdAccount.addIncomes(tdHoldingList);

        List<SubAccountIncomeDetails> accounts = new ArrayList<>();
        accounts.add((SubAccountIncomeDetails) tdAccount);
        wrapAccountIncomeDetails.setSubAccountIncomeDetailsList(accounts);
        incomeResponse.add(wrapAccountIncomeDetails);

        IncomeDetailsDto dto = incomeDTOServiceImpl.find(incomeReceivedKey, new ServiceErrorsImpl());
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getTermDepositIncomeDetails());

        TermDepositIncomeDetailsDto tdIncomeDto = dto.getTermDepositIncomeDetails();
        Assert.assertTrue(tdIncomeDto.getIncomeTotal().doubleValue() == 100d);
        Assert.assertTrue(dto.getTermDepositIncomeDetails().getIncomes().get(0).getAmount().doubleValue() == 100d);
        Assert.assertTrue(dto.getInterestTotal().doubleValue() == 100d);
    }

    @Test
    public void testGetIncomeReceived_whenManagedFundIsNotNull() {

        List<WrapAccountIncomeDetails> incomeResponse = new ArrayList<>();
        WrapAccountIncomeDetailsImpl a = new WrapAccountIncomeDetailsImpl();

        // Managed fund income
        List<Income> disIncomeList = new ArrayList<>();
        DistributionIncomeImpl disIncome = new DistributionIncomeImpl();
        disIncome.setAmount(BigDecimal.valueOf(100d));
        disIncome.setQuantity(BigDecimal.valueOf(20));
        disIncome.setExecutionDate(new DateTime().minusMonths(2));
        disIncome.setPaymentDate(new DateTime().minusMonths(1));
        disIncomeList.add(disIncome);

        HoldingIncomeDetailsImpl disHoldingIncome = new HoldingIncomeDetailsImpl();
        disHoldingIncome.addIncomes(disIncomeList);
        AssetImpl asset = new AssetImpl();
        asset.setAssetName("Test asset");
        asset.setAssetCode("0001");
        asset.setAssetType(AssetType.MANAGED_FUND);
        disHoldingIncome.setAsset(asset);

        // Managed fund management fee rebate income
        List<Income> disIncomeList2 = new ArrayList<>();
        DistributionIncomeImpl disRebateIncome = new DistributionIncomeImpl();
        disRebateIncome.setAmount(BigDecimal.valueOf(100d));
        disRebateIncome.setQuantity(null);
        disRebateIncome.setExecutionDate(new DateTime().minusMonths(1));
        disRebateIncome.setPaymentDate(new DateTime());
        disIncomeList2.add(disRebateIncome);

        HoldingIncomeDetailsImpl disFeeRebateIncome = new HoldingIncomeDetailsImpl();
        disFeeRebateIncome.addIncomes(disIncomeList2);
        AssetImpl asset2 = new AssetImpl();
        asset2.setAssetName("Ref asset");
        asset2.setAssetCode("0002");
        asset2.setAssetType(AssetType.MANAGED_FUND);
        disFeeRebateIncome.setAsset(asset2);

        List<HoldingIncomeDetails> holdingIncomes = new ArrayList<>();
        holdingIncomes.add(disHoldingIncome);
        holdingIncomes.add(disFeeRebateIncome);

        SubAccountIncomeDetailsImpl mfDetails = new SubAccountIncomeDetailsImpl();
        mfDetails.setAssetType(AssetType.MANAGED_FUND);
        mfDetails.addIncomes(holdingIncomes);

        List<SubAccountIncomeDetails> accounts = new ArrayList<>();
        accounts.add(mfDetails);
        a.setSubAccountIncomeDetailsList(accounts);
        incomeResponse.add(a);

        Mockito.when(incomeIntegrationService.loadIncomeReceivedDetails(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(incomeResponse);
        IncomeDetailsDto dto = incomeDTOServiceImpl.find(incomeReceivedKey, new ServiceErrorsImpl());
        Assert.assertNotNull(dto);
        Assert.assertTrue(dto.getDividendTotal().doubleValue() == 0d);
        Assert.assertTrue(dto.getDistributionTotal().doubleValue() == 200d);

        // Verify details in the dto have been mapped correctly.
        Assert.assertEquals(1, dto.getManagedFundIncomeDetails().getDistributions().size());

        FeeRebateIncomeDto managedFundRebate = dto.getManagedFundIncomeDetails().getFeeRebates().get(0);
        Assert.assertTrue(managedFundRebate.getPaymentDate().equals(disRebateIncome.getPaymentDate()));
        Assert.assertTrue(managedFundRebate.getAmount().equals(disRebateIncome.getAmount()));

        DistributionIncomeDto managedFundIncome = dto.getManagedFundIncomeDetails().getDistributions().get(0);
        DateTime executionDate2 = managedFundIncome.getExecutionDate();
        Assert.assertTrue(executionDate2.equals(disIncome.getExecutionDate()));
        Assert.assertTrue(managedFundIncome.getPaymentDate().equals(disIncome.getPaymentDate()));
        Assert.assertTrue(managedFundIncome.getQuantity().equals(disIncome.getQuantity()));
        Assert.assertTrue(managedFundIncome.getAmount().equals(disIncome.getAmount()));
        Assert.assertEquals(false, managedFundIncome.getIsFeeRebate());
    }

    @Test
    public void testGetIncomeReceived_whenCashInMPIsNotNull() {
        List<WrapAccountIncomeDetails> incomeResponse = new ArrayList<>();
        WrapAccountIncomeDetailsImpl a = new WrapAccountIncomeDetailsImpl();

        CashIncomeImpl cashIncome = new CashIncomeImpl();
        cashIncome.setAmount(BigDecimal.valueOf(100d));
        cashIncome.setDescription("MP Cash");
        List<Income> cashIncomeList = new ArrayList<>();
        cashIncomeList.add(cashIncome);

        HoldingIncomeDetailsImpl cashHoldingIncome = new HoldingIncomeDetailsImpl();
        cashHoldingIncome.addIncomes(cashIncomeList);
        AssetImpl asset = new AssetImpl();
        asset.setAssetName("Test asset");
        asset.setAssetCode("0001");
        asset.setAssetType(AssetType.CASH);
        cashHoldingIncome.setAsset(asset);

        List<HoldingIncomeDetails> holdingIncomes = new ArrayList<>();
        holdingIncomes.add(cashHoldingIncome);

        SubAccountIncomeDetailsImpl mpDetails = new SubAccountIncomeDetailsImpl();
        mpDetails.setAssetType(AssetType.MANAGED_PORTFOLIO);
        mpDetails.addIncomes(holdingIncomes);

        List<SubAccountIncomeDetails> accounts = new ArrayList<>();
        accounts.add(mpDetails);
        a.setSubAccountIncomeDetailsList(accounts);
        incomeResponse.add(a);

        Mockito.when(incomeIntegrationService.loadIncomeReceivedDetails(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(incomeResponse);
        IncomeDetailsDto dto = incomeDTOServiceImpl.find(incomeReceivedKey, new ServiceErrorsImpl());
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getManagedPortfolioIncomeDetails());

        ManagedPortfolioIncomeDetailsDto mpDto = dto.getManagedPortfolioIncomeDetails();
        Assert.assertTrue(mpDto.getCashIncomes().size() == 1);
        CashIncomeDto cashIncomeDto = mpDto.getCashIncomes().get(0);
        Assert.assertTrue(cashIncomeDto.getAmount().equals(cashIncome.getAmount()));
    }

    @Test
    public void testGetIncomeReceived_whenDividendIsNotNull() {
        List<WrapAccountIncomeDetails> incomeResponse = new ArrayList<>();
        WrapAccountIncomeDetailsImpl a = new WrapAccountIncomeDetailsImpl();

        // Dividend income
        DividendIncomeImpl divIncome = new DividendIncomeImpl();
        divIncome.setAmount(BigDecimal.valueOf(100d));
        divIncome.setQuantity(BigDecimal.valueOf(20));
        List<Income> divIncomeList = new ArrayList<>();
        divIncomeList.add(divIncome);

        HoldingIncomeDetailsImpl divHoldingIncome = new HoldingIncomeDetailsImpl();
        divHoldingIncome.addIncomes(divIncomeList);
        AssetImpl asset = new AssetImpl();
        asset.setAssetName("Test asset");
        asset.setAssetCode("0001");
        asset.setAssetType(AssetType.SHARE);
        divHoldingIncome.setAsset(asset);

        List<HoldingIncomeDetails> holdingIncomes = new ArrayList<>();
        holdingIncomes.add(divHoldingIncome);

        SubAccountIncomeDetailsImpl mpDetails = new SubAccountIncomeDetailsImpl();
        mpDetails.setAssetType(AssetType.MANAGED_PORTFOLIO);
        mpDetails.addIncomes(holdingIncomes);

        List<SubAccountIncomeDetails> accounts = new ArrayList<>();
        accounts.add(mpDetails);
        a.setSubAccountIncomeDetailsList(accounts);
        incomeResponse.add(a);

        Mockito.when(incomeIntegrationService.loadIncomeReceivedDetails(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(incomeResponse);
        IncomeDetailsDto dto = incomeDTOServiceImpl.find(incomeReceivedKey, new ServiceErrorsImpl());
        Assert.assertNotNull(dto);
        Assert.assertEquals(100d, dto.getDividendTotal().doubleValue(), 0.001);
        Assert.assertEquals(0d, dto.getDistributionTotal().doubleValue(), 0.001);
        // Assert.assertEquals(0d, dto.getFrankedDividendTotal().doubleValue(),
        // 0.001);

        DividendIncomeDto div = dto.getManagedPortfolioIncomeDetails().getDividends().get(0);
        Assert.assertTrue(div.getQuantity().equals(divIncome.getQuantity()));
    }

    @Test
    public void testGetIncomeReceived_whenDistributionIsNotNull() {
        List<WrapAccountIncomeDetails> incomeResponse = new ArrayList<>();
        WrapAccountIncomeDetailsImpl a = new WrapAccountIncomeDetailsImpl();

        // Distribution income
        List<Income> disIncomeList = new ArrayList<>();
        DistributionIncomeImpl disIncome = new DistributionIncomeImpl();
        disIncome.setAmount(BigDecimal.valueOf(100d));
        disIncome.setQuantity(BigDecimal.valueOf(20));
        disIncome.setExecutionDate(new DateTime().minusMonths(2));
        disIncome.setPaymentDate(new DateTime().minusMonths(1));
        disIncomeList.add(disIncome);

        HoldingIncomeDetailsImpl disHoldingIncome = new HoldingIncomeDetailsImpl();
        disHoldingIncome.addIncomes(disIncomeList);
        AssetImpl asset = new AssetImpl();
        asset.setAssetName("Test asset");
        asset.setAssetCode("0001");
        asset.setAssetType(AssetType.MANAGED_FUND);
        disHoldingIncome.setAsset(asset);

        // Managed fund management fee rebate income
        List<Income> disIncomeList2 = new ArrayList<>();
        DistributionIncomeImpl disRebateIncome = new DistributionIncomeImpl();
        disRebateIncome.setAmount(BigDecimal.valueOf(100d));
        disRebateIncome.setExecutionDate(new DateTime().minusMonths(1));
        disRebateIncome.setPaymentDate(new DateTime());
        disIncomeList2.add(disRebateIncome);

        HoldingIncomeDetailsImpl disFeeRebateIncome = new HoldingIncomeDetailsImpl();
        disFeeRebateIncome.addIncomes(disIncomeList2);
        AssetImpl asset2 = new AssetImpl();
        asset2.setAssetName("Ref asset");
        asset2.setAssetCode("0002");
        asset2.setAssetType(AssetType.MANAGED_FUND);
        disFeeRebateIncome.setAsset(asset2);

        List<HoldingIncomeDetails> holdingIncomes = new ArrayList<>();
        holdingIncomes.add(disHoldingIncome);
        holdingIncomes.add(disFeeRebateIncome);

        SubAccountIncomeDetailsImpl mpDetails = new SubAccountIncomeDetailsImpl();
        mpDetails.setAssetType(AssetType.MANAGED_PORTFOLIO);
        mpDetails.addIncomes(holdingIncomes);

        List<SubAccountIncomeDetails> accounts = new ArrayList<>();
        accounts.add(mpDetails);
        a.setSubAccountIncomeDetailsList(accounts);
        incomeResponse.add(a);

        Mockito.when(incomeIntegrationService.loadIncomeReceivedDetails(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(incomeResponse);
        IncomeDetailsDto dto = incomeDTOServiceImpl.find(incomeReceivedKey, new ServiceErrorsImpl());
        Assert.assertNotNull(dto);
        Assert.assertTrue(dto.getDividendTotal().doubleValue() == 0d);
        Assert.assertTrue(dto.getDistributionTotal().doubleValue() == 200d);

        // Verify details in the dto have been mapped correctly.
        List<DistributionIncomeDto> mpDistIncomes = dto.getManagedPortfolioIncomeDetails().getDistributions();
        List<FeeRebateIncomeDto> mpDistRebates = dto.getManagedPortfolioIncomeDetails().getFeeRebates();

        Assert.assertEquals(1, mpDistIncomes.size());

        FeeRebateIncomeDto dtoRebate = mpDistRebates.get(0);
        Assert.assertTrue(disRebateIncome.getPaymentDate().equals(dtoRebate.getPaymentDate()));
        Assert.assertEquals(disRebateIncome.getAmount(), dtoRebate.getAmount());

        DistributionIncomeDto dtoIncome = mpDistIncomes.get(0);

        Assert.assertEquals(disIncome.getExecutionDate(), dtoIncome.getExecutionDate());
        Assert.assertEquals(disIncome.getPaymentDate(), dtoIncome.getPaymentDate());
        Assert.assertEquals(disIncome.getQuantity(), dtoIncome.getQuantity());
        Assert.assertEquals(disIncome.getAmount(), dtoIncome.getAmount());
        Assert.assertEquals(false, dtoIncome.getIsFeeRebate());

    }

}
