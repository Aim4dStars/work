package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.CashManagementValuationDto;
import com.bt.nextgen.api.account.v1.model.DatedAccountKey;
import com.bt.nextgen.api.account.v1.model.InvestmentAssetDto;
import com.bt.nextgen.api.account.v1.model.ManagedFundValuationDto;
import com.bt.nextgen.api.account.v1.model.ManagedPortfolioValuationDto;
import com.bt.nextgen.api.account.v1.model.TermDepositValuationDto;
import com.bt.nextgen.api.account.v1.model.ValuationDto;
import com.bt.nextgen.api.account.v1.model.ValuationSummaryDto;
import com.bt.nextgen.api.account.v2.service.TermDepositPresentationService;
import com.bt.nextgen.clients.domain.AccountType;
import com.bt.nextgen.cms.service.CmsService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AvaloqAccountIntegrationServiceFactory;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetDetailImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.portfolio.PortfolioIntegrationServiceFactory;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedFundAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedFundHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedPortfolioAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ShareHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.WrapAccountValuationImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.asset.Term;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.HoldingKey;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedFundHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class ValuationDtoServiceTest {
    @InjectMocks
    private ValuationDtoServiceImpl valuationDTOServiceImpl;

    @Mock
    private AvaloqAccountIntegrationServiceFactory avaloqAccountIntegrationServiceFactory;

    @Mock
    private PortfolioIntegrationServiceFactory avaloqPortfolioIntegrationServiceFactory;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private PortfolioIntegrationService portfolioIntegrationService;


    @Mock
    private StaticIntegrationService staticIntegrationService;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ManagedFundAccountDtoService mfaDtoService;

    @Mock
    private CmsService cmsService;

    @Mock
    private TermDepositPresentationService termDepositPresentationService;

    private final DatedAccountKey valuationKey = new DatedAccountKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0",
            new DateTime());

    private WrapAccountDetail accountDetail;
    private WrapAccountValuationImpl cashValuation;
    private WrapAccountValuationImpl valuation;
    private CashAccountValuationImpl cashAccount;
    private ManagedFundAccountValuationImpl mfValuation1;
    private ManagedFundAccountValuationImpl mfValuation2;
    private ManagedFundHoldingImpl mfHolding1;
    private ManagedFundHoldingImpl mfHolding2;
    private AssetImpl mfAsset1;
    private AssetImpl mfAsset2;
    private BigDecimal accountBalance;
    private BrokerImpl broker = null;

    @Before
    public void setup() throws Exception {
        accountBalance = new BigDecimal("123456789.49");

        cashAccount = new CashAccountValuationImpl();
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

        List<AccountHolding> mfList = new ArrayList<>();
        mfAsset1 = new AssetImpl();
        mfAsset1.setAssetType(AssetType.MANAGED_FUND);
        mfHolding1 = new ManagedFundHoldingImpl();
        mfAsset1.setAssetName("BlackRock Scientific Diversified Stable Fund");
        mfAsset1.setAssetCode("BAR0811AU");
        mfAsset1.setStatus(AssetStatus.OPEN);
        mfHolding1.setAsset(mfAsset1);
        mfHolding1.setAccruedIncome(BigDecimal.valueOf(99.90d));
        mfHolding1.setAvailableUnits(BigDecimal.valueOf(11));
        mfHolding1.setCost(BigDecimal.valueOf(111));
        mfHolding1.setUnitPrice(BigDecimal.valueOf(1111));
        mfHolding1.setUnitPriceDate(new DateTime());
        mfHolding1.setUnits(BigDecimal.valueOf(11111));
        mfHolding1.setYield(BigDecimal.valueOf(111111));
        mfHolding1.setMarketValue(BigDecimal.valueOf(94000));
        mfHolding1.setDistributionMethod(DistributionMethod.REINVEST);

        mfHolding1.setHoldingKey(HoldingKey.valueOf(valuationKey.getAccountId(), mfAsset1.getAssetName()));
        mfList.add(mfHolding1);

        mfValuation1 = new ManagedFundAccountValuationImpl();
        // mfValuation1.setHolding(mfHolding1);
        // mfValuation1.setAccountName(mfAsset1.getAssetName());
        // mfValuation1.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));

        mfAsset2 = new AssetImpl();
        mfAsset2.setAssetType(AssetType.MANAGED_FUND);
        mfHolding2 = new ManagedFundHoldingImpl();
        mfAsset2.setAssetName("AMP Capital Investors International Bond");
        mfAsset2.setAssetCode("AMP0254AU");
        mfHolding2.setAsset(mfAsset2);
        mfHolding2.setAccruedIncome(BigDecimal.valueOf(99.90d));
        mfHolding2.setAvailableUnits(BigDecimal.valueOf(22));
        mfHolding2.setCost(BigDecimal.valueOf(222));
        mfHolding2.setUnitPrice(BigDecimal.valueOf(2222));
        mfHolding2.setUnitPriceDate(new DateTime());
        mfHolding2.setUnits(BigDecimal.valueOf(22222));
        mfHolding2.setYield(BigDecimal.valueOf(22222));
        mfHolding2.setMarketValue(BigDecimal.valueOf(34000));
        mfHolding2.setDistributionMethod(DistributionMethod.CASH);

        mfHolding2.setHoldingKey(HoldingKey.valueOf(valuationKey.getAccountId(), mfAsset1.getAssetName()));
        mfList.add(mfHolding2);
        mfValuation1.addHoldings(mfList);
        // mfValuation2 = new ManagedFundAccountValuationImpl();
        // mfValuation2.setHolding(mfHolding2);
        // mfValuation2.setAccountName(mfAsset1.getAssetName());
        // mfValuation2.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));

        List<SubAccountValuation> subAccountValuations = new ArrayList<>();
        subAccountValuations.add(mfValuation1);
        subAccountValuations.add(cashAccount);
        // subAccountValuations.add(mfValuation2);
        valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));
        valuation.setSubAccountValuations(subAccountValuations);

        List<SubAccountValuation> subAccountValuationsCash = new ArrayList<>();
        subAccountValuationsCash.add(cashAccount);
        cashValuation = new WrapAccountValuationImpl();
        cashValuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));
        cashValuation.setSubAccountValuations(subAccountValuationsCash);

        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers(Mockito.any(AccountKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(null);
        broker = new BrokerImpl(BrokerKey.valueOf("56789"), BrokerType.ADVISER);
        broker.setDealerKey(BrokerKey.valueOf("45677"));

        accountDetail = Mockito.mock(WrapAccountDetail.class);

        Mockito.when(avaloqAccountIntegrationServiceFactory.getInstance(anyString())).thenReturn(accountIntegrationService);
        Mockito.when(avaloqPortfolioIntegrationServiceFactory.getInstance(anyString())).thenReturn(portfolioIntegrationService);
        Mockito.when(accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class),Mockito.any(ServiceErrors.class))).thenReturn(accountDetail);
    }

    @Test
    public void testGetCashManagmentValuationFromPortfolio_whenAPortfolioHasNoCashManagmentAcccounts_thenValuationHasNullCashManagementAccount() {
        WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));
        List<SubAccountValuation> subaccounts = new ArrayList<>();
        valuation.setSubAccountValuations(subaccounts);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);

        ValuationDto valuationDto = valuationDTOServiceImpl.find(valuationKey, new ServiceErrorsImpl());
        Assert.assertNull(valuationDto.getCashManagement());

    }

    @Test
    public void testGetTermDepositsValuationFromPortfolio_whenAPortfolioWithNoTermDeposits_thenValuationHasNullTermDeposits() {
        WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));
        List<SubAccountValuation> subaccounts = new ArrayList<>();
        valuation.setSubAccountValuations(subaccounts);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);
        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
        ValuationDto valuationDto = valuationDTOServiceImpl.find(valuationKey, new ServiceErrorsImpl());
        Assert.assertNull(valuationDto.getTermDeposits());
    }

    @Test
    public void testGetTermDepositsValuationFromPortfolio_whenAPortfolioWithNoManagedPortfolios_thenValuationHasNullManagedPortfolios() {
        WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));
        List<SubAccountValuation> subaccounts = new ArrayList<>();
        valuation.setSubAccountValuations(subaccounts);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);
        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
        ValuationDto valuationDto = valuationDTOServiceImpl.find(valuationKey, new ServiceErrorsImpl());
        Assert.assertNull(valuationDto.getManagedPortfolios());
    }

    @Test
    public void testGetCashManagementValuationFromPortfolio_whenAPortfolioHasACashManagementAccount_thenValuationHasMatchingCashManagement() {
        WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));

        List<SubAccountValuation> subAccounts = new ArrayList<>();
        CashAccountValuationImpl cashAccount = new CashAccountValuationImpl();

        cashAccount = new CashAccountValuationImpl();
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
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);
        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
        ValuationDto valuationDto = valuationDTOServiceImpl.find(valuationKey, new ServiceErrorsImpl());
        ValuationSummaryDto summary = valuationDto.getCashManagement();
        Assert.assertNotNull(summary);
        Assert.assertEquals(3, summary.getIncome().doubleValue(), 0.005);
        Assert.assertEquals(0.6, summary.getIncomePercent().doubleValue(), 0.005);
        Assert.assertEquals(1, summary.getPortfolioPercent().doubleValue(), 0.005);
        CashManagementValuationDto cash = (CashManagementValuationDto) summary.getInvestments().get(0);
        Assert.assertNotNull(cash);

        cashHolding = (CashHoldingImpl) cashAccount.getHoldings().get(0);
        Assert.assertEquals(cashHolding.getAvailableBalance().doubleValue(), cash.getAvailableBalance().doubleValue(), 0.005);
        Assert.assertEquals(cashHolding.getBalance().doubleValue(),
                cash.getBalance().doubleValue() + cash.getIncome().doubleValue(), 0.005);
        Assert.assertEquals(cashHolding.getMarketValue().doubleValue(), cash.getBalance().doubleValue(), 0.005);
        Assert.assertEquals(cashHolding.getAccruedIncome().doubleValue(), cash.getIncome().doubleValue(), 0.005);
        Assert.assertEquals(cashHolding.getYield().doubleValue() / 100, cash.getInterestRate().doubleValue(), 0.005);
        Assert.assertEquals(cashHolding.getAccountName(), cash.getName());
        Assert.assertEquals(0.4, cash.getPortfolioPercent().doubleValue(), 0.005);

    }

    @Test
    public void testGetCashManagementValuationFromPortfolio_whenAPortfolioHasACashManagementAccountWithNegativeAvlBalance_thenValuationHasZeroAvlBalance() {
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
        List<AccountHolding> cashList = new ArrayList<>();
        cashList.add(cashHolding);
        cashAccount.addHoldings(cashList);
        subAccounts.add(cashAccount);

        valuation.setSubAccountValuations(subAccounts);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);
        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
        ValuationDto valuationDto = valuationDTOServiceImpl.find(valuationKey, new ServiceErrorsImpl());
        ValuationSummaryDto summary = valuationDto.getCashManagement();
        Assert.assertNotNull(summary);
        Assert.assertEquals(3, summary.getIncome().doubleValue(), 0.005);
        Assert.assertEquals(0.6, summary.getIncomePercent().doubleValue(), 0.005);
        Assert.assertEquals(1, summary.getPortfolioPercent().doubleValue(), 0.005);
        CashManagementValuationDto cash = (CashManagementValuationDto) summary.getInvestments().get(0);
        Assert.assertNotNull(cash);

        cashHolding = (CashHoldingImpl) cashAccount.getHoldings().get(0);
        Assert.assertEquals(BigDecimal.ONE, cash.getAvailableBalance());
        Assert.assertEquals(cashHolding.getMarketValue().doubleValue(), cash.getBalance().doubleValue(), 0.005);
        Assert.assertEquals(cashHolding.getAccruedIncome().doubleValue(), cash.getIncome().doubleValue(), 0.005);
        Assert.assertEquals(cashHolding.getYield().doubleValue() / 100, cash.getInterestRate().doubleValue(), 0.005);
        Assert.assertEquals(cashHolding.getAccountName(), cash.getName());
        Assert.assertEquals(0.4, cash.getPortfolioPercent().doubleValue(), 0.005);
    }

    @Test
    public void testGetTermDepositsValuationFromPortfolio_whenAPortfolioWithTermDeposits_thenValuationHasMatchingTermDeposits() {
        WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));
        List<SubAccountValuation> subAccounts = new ArrayList<>();
        TermDepositAccountValuationImpl tdAccount = new TermDepositAccountValuationImpl();

        TermDepositHoldingImpl tdHolding = new TermDepositHoldingImpl();
        tdHolding.setMarketValue(BigDecimal.valueOf(2));
        tdHolding.setAccruedIncome(BigDecimal.valueOf(3));
        tdHolding.setYield(BigDecimal.valueOf(4));
        tdHolding.setMaturityDate(new DateTime());
        tdHolding.setMaturityInstruction("7");
        tdHolding.setHoldingKey(HoldingKey.valueOf("accountId", "BT Term Deposit"));

        AssetImpl asset = new AssetImpl();
        asset.setAssetId("20168");
        tdHolding.setAsset(asset);
        tdAccount.addHoldings(Collections.singletonList((AccountHolding) tdHolding));

        subAccounts.add(tdAccount);

        valuation.setSubAccountValuations(subAccounts);
        TermDepositAssetDetailImpl termDepositAssetDetail = new TermDepositAssetDetailImpl();
        termDepositAssetDetail.setTerm(new Term("6M"));
        termDepositAssetDetail.setPaymentFrequency(PaymentFrequency.AT_MATURITY);
        Map<String, TermDepositAssetDetail> termDepositAssetMap = new HashMap<String, TermDepositAssetDetail>();
        termDepositAssetMap.put("20169", termDepositAssetDetail);

        TermDepositAssetImpl termDepositAsset = new TermDepositAssetImpl();
        termDepositAsset.setAssetId("20168");
        termDepositAsset.setAssetName("BT");
        termDepositAsset.setGenericAssetId("20169");
        termDepositAsset.setBrand("80000064");
        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);
        Mockito.when(assetIntegrationService.loadAsset(Mockito.any(String.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(termDepositAsset);
        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
        Mockito.when(cmsService.getContent(Mockito.any(String.class))).thenReturn("BT");
        CodeImpl renewCode = new CodeImpl("7", "CONTR_AMOUNT", "Rollover Principal");
        Mockito.when(staticIntegrationService.loadCode(Mockito.eq(CodeCategory.TD_RENEW_MODE), anyString(),
                Mockito.any(ServiceErrors.class))).thenReturn(renewCode);
        TermDepositPresentation tdPres = new TermDepositPresentation();
        tdPres.setBrandName("BT Term Deposit");
        tdPres.setBrandClass("BT");
        tdPres.setTerm("6 months");
        tdPres.setPaymentFrequency(PaymentFrequency.AT_MATURITY.getDisplayName().toLowerCase());
        Mockito.when(termDepositPresentationService.getTermDepositPresentation(Mockito.any(AccountKey.class),
                Mockito.any(String.class), Mockito.any(ServiceErrors.class))).thenReturn(tdPres);

        ValuationDto valuationDto = valuationDTOServiceImpl.find(valuationKey, new ServiceErrorsImpl());
        ValuationSummaryDto summary = valuationDto.getTermDeposits();
        Assert.assertNotNull(summary);
        Assert.assertEquals(3, summary.getIncome().doubleValue(), 0.005);
        Assert.assertEquals(0.6, summary.getIncomePercent().doubleValue(), 0.005);
        Assert.assertEquals(1.0, summary.getPortfolioPercent().doubleValue(), 0.005);
        TermDepositValuationDto td = (TermDepositValuationDto) summary.getInvestments().get(0);
        Assert.assertNotNull(td);
        Assert.assertEquals(tdHolding.getMarketValue().doubleValue(), td.getBalance().doubleValue(), 0.005);
        Assert.assertEquals(tdHolding.getAccruedIncome().doubleValue(), td.getIncome().doubleValue(), 0.005);
        Assert.assertEquals(tdHolding.getInterestRate().doubleValue(), td.getInterestRate().doubleValue(), 0.005);
        Assert.assertEquals(tdHolding.getHoldingKey().getName(), td.getName());
        Assert.assertEquals(tdHolding.getMaturityInstruction(), td.getMaturityInstructionId());
        Assert.assertEquals(0.4, td.getPortfolioPercent().doubleValue(), 0.005);
        Assert.assertEquals(tdHolding.getHoldingKey().getHid().getId(), EncodedString.toPlainText(td.getSubAccountId()));
        Assert.assertEquals(td.getMaturityInstruction(), renewCode.getName());
        Assert.assertEquals("6 months", td.getTerm().toString());
        Assert.assertEquals(PaymentFrequency.AT_MATURITY.getDisplayName().toLowerCase(), td.getPaymentFrequency());

    }

    @Test
    public void testGetManagedPortfoliosValuationFromPortfolio_whenAPortfolioWithManagedPortfolios_thenValuationHasMatchingManagedPortfolios() {
        WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));

        List<SubAccountValuation> subAccounts = new ArrayList<>();
        ManagedPortfolioAccountValuationImpl mpAccount = new ManagedPortfolioAccountValuationImpl();
        mpAccount.setSubAccountKey(SubAccountKey.valueOf("accountId"));

        AssetImpl mpAsset = new AssetImpl();
        mpAsset.setAssetClass(AssetClass.AUSTRALIAN_SHARES);
        mpAsset.setAssetType(AssetType.MANAGED_PORTFOLIO);
        mpAccount.setAsset(mpAsset);

        List<AccountHolding> holdings = new ArrayList<>();

        AssetImpl asset = new AssetImpl();
        asset.setAssetClass(AssetClass.AUSTRALIAN_SHARES);
        asset.setAssetCode("assetCode");
        asset.setAssetId("assetId");
        asset.setAssetName("assetName");
        asset.setAssetType(AssetType.SHARE);
        asset.setBrand("brand");
        asset.setIndustrySector("industrySector");
        asset.setIndustryType("industryType");

        AssetImpl prepaymentAsset = new AssetImpl();
        prepaymentAsset.setAssetClass(AssetClass.AUSTRALIAN_SHARES);
        prepaymentAsset.setAssetCode("prepayAssetCode");
        prepaymentAsset.setAssetId("assetId1");
        prepaymentAsset.setAssetName("assetName1");
        prepaymentAsset.setAssetType(AssetType.SHARE);
        prepaymentAsset.setBrand("brand1");
        prepaymentAsset.setIndustrySector("industrySector");
        prepaymentAsset.setIndustryType("industryType");
        prepaymentAsset.setMoneyAccountType("Cash Claim Account");

        ShareHoldingImpl holding = new ShareHoldingImpl();

        holding.setAsset(asset);
        holding.setAvailableUnits(BigDecimal.valueOf(1));
        holding.setCost(BigDecimal.valueOf(2));
        holding.setUnitPrice(BigDecimal.valueOf(3));
        holding.setUnitPriceDate(new DateTime());
        holding.setUnits(BigDecimal.valueOf(4));
        holding.setYield(BigDecimal.valueOf(5));
        holding.setMarketValue(BigDecimal.valueOf(11.99));
        holding.setAccruedIncome(BigDecimal.valueOf(0.01));
        holding.setRefAsset(prepaymentAsset);
        holdings.add(holding);

        mpAccount.addHoldings(holdings);
        subAccounts.add(mpAccount);

        valuation.setSubAccountValuations(subAccounts);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);
        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
        ValuationDto valuationDto = valuationDTOServiceImpl.find(valuationKey, new ServiceErrorsImpl());
        ValuationSummaryDto summary = valuationDto.getManagedPortfolios();
        Assert.assertNotNull(summary);
        Assert.assertEquals(0.01, summary.getIncome().doubleValue(), 0.005);
        Assert.assertEquals(0, summary.getIncomePercent().doubleValue(), 0.005);
        Assert.assertEquals(1, summary.getPortfolioPercent().doubleValue(), 0.005);
        ManagedPortfolioValuationDto mp = (ManagedPortfolioValuationDto) summary.getInvestments().get(0);
        Assert.assertNotNull(mp);
        Assert.assertEquals(mpAccount.getMarketValue().add(mpAccount.getAccruedIncome()).doubleValue(),
                mp.getBalance().doubleValue(), 0.005);
        // Assert.assertEquals(mpAccount.getAccountName(), mp.getName());
        Assert.assertEquals(1, mp.getPortfolioPercent().doubleValue(), 0.005);
        Assert.assertEquals(mpAccount.getAvailableBalance().doubleValue(), mp.getAvailableBalance().doubleValue(), 0.005);
        Assert.assertEquals(mpAccount.getSubAccountKey().getId(), EncodedString.toPlainText(mp.getSubAccountId()));

        InvestmentAssetDto investment = mp.getInvestmentAssets().get(0);
        // Assert.assertEquals(asset.getAssetId(), investment.getAssetId());
        Assert.assertEquals(asset.getAssetName(), investment.getAssetName());
        Assert.assertEquals(asset.getAssetType().name(), investment.getAssetType());
        Assert.assertEquals(holding.getCost().doubleValue(), investment.getAverageCost().doubleValue(), 0.005);
        Assert.assertEquals((11.99 - 2), investment.getDollarGain().doubleValue(), 0.005);
        Assert.assertEquals(holding.getUnitPriceDate(), investment.getEffectiveDate());
        Assert.assertEquals(holding.getHasPending(), investment.getHasPending());
        Assert.assertEquals(5, investment.getPercentGain().doubleValue(), 0.005);
        Assert.assertEquals(holding.getUnits().doubleValue(), investment.getQuantity().doubleValue(), 0.005);
        Assert.assertEquals(holding.getUnitPrice().doubleValue(), investment.getUnitPrice().doubleValue(), 0.005);
        Assert.assertEquals(Boolean.TRUE, investment.isPrepaymentAsset());
    }

    @Test
    public void testGetManagedFundValuations_whenNoManagedFundsThenEmptyList() {
        ValuationSummaryDto mfValuationDto = valuationDTOServiceImpl.getManagedFundValuations(accountDetail, cashValuation,
                accountBalance);
        Assert.assertNull(mfValuationDto);
    }

    @Test
    public void testGetManagedFundValuations_whenObject_thenValuesMatch() {
        ValuationSummaryDto mfValuationDto = valuationDTOServiceImpl.getManagedFundValuations(accountDetail, valuation,
                accountBalance);
        Assert.assertEquals(2, mfValuationDto.getInvestments().size());
        Assert.assertEquals(mfValuation1.getHoldings().get(0).getAsset().getAssetName(),
                ((ManagedFundValuationDto) mfValuationDto.getInvestments().get(0)).getInvestmentAsset().getAssetName());
        Assert.assertEquals(mfValuation1.getHoldings().get(1).getAsset().getAssetName(),
                ((ManagedFundValuationDto) mfValuationDto.getInvestments().get(1)).getInvestmentAsset().getAssetName());
    }

    @Test
    public void testBuildManagedFundValuationDto_whenObject_thenValuesMatch() {
        List<ManagedFundValuationDto> mfDtoList = valuationDTOServiceImpl.buildManagedFundValuationDto(accountDetail,
                mfValuation1, accountBalance);
        ManagedFundValuationDto mfValuationDto = mfDtoList.get(0);
        ManagedFundHolding holding = (ManagedFundHolding) mfValuation1.getHoldings().get(0);

        Assert.assertEquals(holding.getHoldingKey().getHid().getId(),
                EncodedString.toPlainText(mfValuationDto.getSubAccountId()));
        Assert.assertEquals(holding.getHoldingKey().getName(), mfValuationDto.getName());
        Assert.assertEquals(holding.getAccruedIncome(), mfValuationDto.getIncome());
        Assert.assertEquals(holding.getAsset().getAssetId(), mfValuationDto.getInvestmentAsset().getAssetId());
        Assert.assertEquals(holding.getAsset().getStatus().getDisplayName(), mfValuationDto.getInvestmentAsset().getStatus());
        Assert.assertEquals(holding.getDistributionMethod().getDisplayName(), mfValuationDto.getDistributionMethod());
    }

    public void testGetValuationFromPortfolio_whenAnAccountHasAccountTypeNull_thenValuationDtoReturnsAccountTypeNull() {
        WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));
        List<SubAccountValuation> subaccounts = new ArrayList<>();
        valuation.setSubAccountValuations(subaccounts);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);

        ValuationDto valuationDto = valuationDTOServiceImpl.find(valuationKey, new ServiceErrorsImpl());

        Assert.assertNull(valuationDto.getCashManagement());
        Assert.assertNull(valuationDto.getAccountType());
    }

    @Test
    public void testGetValuationFromPortfolio_whenAnAccountHasAccountTypeSMSF_thenValuationDtoReturnsAccountTypeSMSF() {
        WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));
        List<SubAccountValuation> subaccounts = new ArrayList<>();
        valuation.setSubAccountValuations(subaccounts);

        WrapAccountImpl account = new WrapAccountImpl();
        account.setAccountStructureType(AccountStructureType.fromAvaloqStaticCode("S"));

        Assert.assertEquals(account.getAccountStructureType().name(), AccountType.SMSF.getName());

        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers(Mockito.any(AccountKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(account);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);

        ValuationDto valuationDto = valuationDTOServiceImpl.find(valuationKey, new ServiceErrorsImpl());

        Assert.assertNull(valuationDto.getCashManagement());
        Assert.assertEquals(valuationDto.getAccountType(), AccountType.SMSF.getName());
    }
}
