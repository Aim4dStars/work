package com.bt.nextgen.api.portfolio.v3.service.performance;

import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.api.portfolio.v3.model.performance.AccountPerformanceOverallDto;
import com.bt.nextgen.api.portfolio.v3.model.performance.ManagedPortfolioPerformanceDto;
import com.bt.nextgen.api.portfolio.v3.model.performance.PeriodPerformanceDto;
import com.bt.nextgen.api.portfolio.v3.model.performance.TermDepositPerformanceDto;
import com.bt.nextgen.api.portfolio.v3.service.TermDepositPresentationService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.AssetPerformanceImpl;
import com.bt.nextgen.service.avaloq.asset.ManagedFundAssetImpl;
import com.bt.nextgen.service.avaloq.asset.ManagedPortfolioPerformanceImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.avaloq.portfolio.performance.PortfolioPerformanceOverallImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.asset.AssetPerformance;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceOverall;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class AccountPerformanceOverallDtoServiceTest {

    @InjectMocks
    private AccountPerformanceOverallDtoServiceImpl performanceOverallService;

    @Mock
    private AccountPerformanceIntegrationService accountPerformanceService;

    @Mock
    private TermDepositPresentationService termDepositPresentationService;

    private DateTime startDate;
    private DateTime endDate;
    private DateRangeAccountKey key;

    private AccountPerformanceOverall accountPerformanceOverall;
    private AssetPerformanceImpl ap;
    private AssetPerformanceImpl apTD;
    private AssetPerformanceImpl apShare;
    private AssetPerformanceImpl apMF;
    private ManagedPortfolioPerformanceImpl apMP;

    @Before
    public void setup() {
        accountPerformanceOverall = createPerformanceTestData();
        startDate = DateTime.parse("2015-01-01");
        endDate = DateTime.parse("2015-03-03");
        key = new DateRangeAccountKey(EncodedString.fromPlainText("accountKey").toString(), startDate, endDate);

        mockTDPresentationService();
    }

    @Test
    public void testFind_whenMethodInvoked_ThenCorrectArgumentsArePassed() {

        Mockito.when(
                accountPerformanceService.loadAccountOverallPerformance(Mockito.any(AccountKey.class),
                        Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<AccountPerformanceOverall>() {

                    @Override
                    public AccountPerformanceOverall answer(InvocationOnMock invocation) throws Throwable {
                        Assert.assertEquals(AccountKey.valueOf("accountKey"), invocation.getArguments()[0]);
                        Assert.assertEquals(startDate, invocation.getArguments()[1]);
                        Assert.assertEquals(endDate, invocation.getArguments()[2]);
                        return accountPerformanceOverall;
                    }
                });

        performanceOverallService.find(key, new FailFastErrorsImpl());
    }

    @Test
    public void testFind_whenValuesAreFetchedFromIntegration_ThenModelIsConstructed() {

        Mockito.when(
                accountPerformanceService.loadAccountOverallPerformance(Mockito.any(AccountKey.class),
                        Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(
                accountPerformanceOverall);

        AccountPerformanceOverallDto dto = performanceOverallService.find(key, new FailFastErrorsImpl());

        Assert.assertNotNull(dto);
        Assert.assertEquals(3, dto.getInvestmentPerformances().size());

        PeriodPerformanceDto cash = dto.getInvestmentPerformances().get(0);
        testPerformanceDto(cash);

        TermDepositPerformanceDto td = (TermDepositPerformanceDto) dto.getInvestmentPerformances().get(1);
        testTDPerformanceDto(td);

        ManagedPortfolioPerformanceDto mp = (ManagedPortfolioPerformanceDto) dto.getInvestmentPerformances().get(2);
        testMPPerformanceDto(mp);
    }

    @Test
    public void testFind_whenEmptyValuesAreFetchedFromIntegration_ThenEmptyModelIsConstructed() {

        Mockito.when(
                accountPerformanceService.loadAccountOverallPerformance(Mockito.any(AccountKey.class),
                        Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(
                null);

        AccountPerformanceOverallDto dto = performanceOverallService.find(key, new FailFastErrorsImpl());

        Assert.assertNotNull(dto);
        Assert.assertNull(dto.getInvestmentPerformances());
    }

    @Test
    public void testPrepaymentsAreNotAdded() {

        List<AssetPerformance> investmentPerformance = new ArrayList<>();

        // Prepayment filter test
        ManagedFundAssetImpl mFundPrePayment = new ManagedFundAssetImpl();
        mFundPrePayment.setAssetId("1");
        mFundPrePayment.setAssetType(AssetType.MANAGED_FUND);
        mFundPrePayment.setMoneyAccountType(Constants.PREPAYMENT_IDENTIFIER);

        AssetPerformanceImpl apMFAssetPerformancePrePayment = new AssetPerformanceImpl();
        apMFAssetPerformancePrePayment.setName(mFundPrePayment.getAssetName());
        apMFAssetPerformancePrePayment.setAssetType(AssetType.MANAGED_FUND);
        apMFAssetPerformancePrePayment.setContainerType(ContainerType.DIRECT);
        apMFAssetPerformancePrePayment.setAsset(mFundPrePayment);
        apMFAssetPerformancePrePayment.setClosingBalance(BigDecimal.ZERO);
        apMFAssetPerformancePrePayment.setOpeningBalance(new BigDecimal(0.00));
        apMFAssetPerformancePrePayment.setSales(new BigDecimal(10.50));
        apMFAssetPerformancePrePayment.setPurchases(new BigDecimal(-10.500000));

        ManagedFundAssetImpl mFundPrePaymentReference = new ManagedFundAssetImpl();
        mFundPrePaymentReference.setAssetId("2");
        mFundPrePaymentReference.setAssetType(AssetType.MANAGED_FUND);
        mFundPrePaymentReference.setAssetName("Managed fund asset pre");
        mFundPrePaymentReference.setAssetCode("MFA PRE");

        apMFAssetPerformancePrePayment.setReferenceAsset(mFundPrePaymentReference);

        investmentPerformance.add(apMFAssetPerformancePrePayment);
        // Won't be added after evaluation in protected PerformanceDto getPerformanceDto(AssetPerformance assetPerformance)

        PortfolioPerformanceOverallImpl accountPerformance = new PortfolioPerformanceOverallImpl();
        accountPerformance.setInvestmentPerformances(investmentPerformance);

        Mockito.when(accountPerformanceService.loadAccountOverallPerformance(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(accountPerformance);

        AccountPerformanceOverallDto dto = performanceOverallService.find(key, new FailFastErrorsImpl());

        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getInvestmentPerformances());
        Assert.assertTrue(dto.getInvestmentPerformances().isEmpty());
    }

    @Test
    public void testPrepaymentsAreNotAddedManagedPortfolio() {

        AssetImpl assetShare = new AssetImpl();
        assetShare.setAssetId("1");
        assetShare.setAssetName("Share asset");
        assetShare.setAssetType(AssetType.SHARE);
        assetShare.setAssetCode("SHA");
        assetShare.setMoneyAccountType(Constants.PREPAYMENT_IDENTIFIER);
    
        AssetImpl assetMF = new AssetImpl();
        assetMF.setAssetId("2");
        assetMF.setAssetName("Managed fund asset");
        assetMF.setAssetType(AssetType.MANAGED_FUND);
        assetMF.setAssetCode("MFA");
        assetMF.setMoneyAccountType(Constants.PREPAYMENT_IDENTIFIER);

        AssetImpl assetMP = new AssetImpl();
        assetMP.setAssetId("3");
        assetMP.setAssetName("Managed portfolio asset");
        assetMP.setAssetType(AssetType.MANAGED_PORTFOLIO);
        assetMP.setAssetCode("MPA");

        AssetPerformanceImpl assetPerformanceMF = new AssetPerformanceImpl();
        assetPerformanceMF.setName(assetMF.getAssetName());
        assetPerformanceMF.setAssetType(AssetType.MANAGED_FUND);
        assetPerformanceMF.setContainerType(ContainerType.DIRECT);
        assetPerformanceMF.setAsset(assetMF);
        assetPerformanceMF.setOpeningBalance(BigDecimal.ZERO);
        assetPerformanceMF.setClosingBalance(new BigDecimal(0.0));

        AssetPerformanceImpl assetPerformanceShare = new AssetPerformanceImpl();
        assetPerformanceShare.setName(assetShare.getAssetName());
        assetPerformanceShare.setAssetType(AssetType.SHARE);
        assetPerformanceShare.setContainerType(ContainerType.DIRECT);
        assetPerformanceShare.setAsset(assetShare);
        assetPerformanceShare.setOpeningBalance(new BigDecimal(0));
        assetPerformanceShare.setClosingBalance(new BigDecimal(0.00));        

        List<AssetPerformance> mpInvestments = new ArrayList<>();
        mpInvestments.add(assetPerformanceMF);
        mpInvestments.add(assetPerformanceShare);
        ManagedPortfolioPerformanceImpl mpPerformance = new ManagedPortfolioPerformanceImpl();
        mpPerformance.setName(assetMP.getAssetName());
        mpPerformance.setAssetType(AssetType.MANAGED_PORTFOLIO);
        mpPerformance.setContainerType(ContainerType.MANAGED_PORTFOLIO);
        mpPerformance.setAsset(assetMP);
        mpPerformance.setAssetPerformances(mpInvestments);
        mpPerformance.setId("MP ID");      
        
        List<AssetPerformance> investmentPerformance = new ArrayList<>();
        investmentPerformance.add(mpPerformance);

        PortfolioPerformanceOverallImpl accountPerformance = new PortfolioPerformanceOverallImpl();
        accountPerformance.setInvestmentPerformances(investmentPerformance);

        Mockito.when(accountPerformanceService.loadAccountOverallPerformance(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(accountPerformance);

        AccountPerformanceOverallDto dto = performanceOverallService.find(key, new FailFastErrorsImpl());

        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getInvestmentPerformances());
        ManagedPortfolioPerformanceDto mpPerformanceDto = (ManagedPortfolioPerformanceDto) dto.getInvestmentPerformances().get(0);       
        Assert.assertTrue(mpPerformanceDto.getAssetPerformance().isEmpty());        
       
    }
    
    @Test
    public void testNonPrepaymentsAreAdded() {

        List<AssetPerformance> investmentPerformance = new ArrayList<>();

        // Balance not zero
        ManagedFundAssetImpl mFundPrePayment = new ManagedFundAssetImpl();
        mFundPrePayment.setAssetId("1");
        mFundPrePayment.setAssetType(AssetType.MANAGED_FUND);
        mFundPrePayment.setMoneyAccountType(Constants.PREPAYMENT_IDENTIFIER);

        AssetPerformanceImpl apMFAssetPerformancePrePayment1 = new AssetPerformanceImpl();
        apMFAssetPerformancePrePayment1.setName(mFundPrePayment.getAssetName());
        apMFAssetPerformancePrePayment1.setAssetType(AssetType.MANAGED_FUND);
        apMFAssetPerformancePrePayment1.setContainerType(ContainerType.DIRECT);
        apMFAssetPerformancePrePayment1.setAsset(mFundPrePayment);
        apMFAssetPerformancePrePayment1.setClosingBalance(BigDecimal.ZERO);
        apMFAssetPerformancePrePayment1.setOpeningBalance(new BigDecimal(10.00));
        apMFAssetPerformancePrePayment1.setSales(new BigDecimal(10.50));
        apMFAssetPerformancePrePayment1.setPurchases(new BigDecimal(-10.500000));

        ManagedFundAssetImpl mFundPrePaymentReference1 = new ManagedFundAssetImpl();
        mFundPrePaymentReference1.setAssetId("11");
        mFundPrePaymentReference1.setAssetType(AssetType.MANAGED_FUND);
        mFundPrePaymentReference1.setAssetName("Managed fund asset pre");
        mFundPrePaymentReference1.setAssetCode("MFA PRE1");

        apMFAssetPerformancePrePayment1.setReferenceAsset(mFundPrePaymentReference1);

        investmentPerformance.add(apMFAssetPerformancePrePayment1);

        // is not Pre payment (setMoneyAccountType not used)
        ManagedFundAssetImpl mFundPrePayment2 = new ManagedFundAssetImpl();
        mFundPrePayment2.setAssetId("2");
        mFundPrePayment2.setAssetType(AssetType.MANAGED_FUND);

        AssetPerformanceImpl apMFAssetPerformancePrePayment2 = new AssetPerformanceImpl();
        apMFAssetPerformancePrePayment2.setName(mFundPrePayment2.getAssetName());
        apMFAssetPerformancePrePayment2.setAssetType(AssetType.MANAGED_FUND);
        apMFAssetPerformancePrePayment2.setContainerType(ContainerType.DIRECT);
        apMFAssetPerformancePrePayment2.setAsset(mFundPrePayment2);

        ManagedFundAssetImpl mFundPrePaymentReference2 = new ManagedFundAssetImpl();
        mFundPrePaymentReference2.setAssetId("22");
        mFundPrePaymentReference2.setAssetType(AssetType.MANAGED_FUND);
        mFundPrePaymentReference2.setAssetName("Managed fund asset");
        mFundPrePaymentReference2.setAssetCode("MFA");

        apMFAssetPerformancePrePayment2.setReferenceAsset(mFundPrePaymentReference2);

        investmentPerformance.add(apMFAssetPerformancePrePayment2);        

        PortfolioPerformanceOverallImpl accountPerformance = new PortfolioPerformanceOverallImpl();
        accountPerformance.setInvestmentPerformances(investmentPerformance);

        Mockito.when(accountPerformanceService.loadAccountOverallPerformance(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(accountPerformance);

        AccountPerformanceOverallDto dto = performanceOverallService.find(key, new FailFastErrorsImpl());

        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getInvestmentPerformances());
        Assert.assertEquals(accountPerformance.getInvestmentPerformances().size(), dto.getInvestmentPerformances().size());
    }    
    
    private AccountPerformanceOverall createPerformanceTestData() {

        AssetImpl cash = new AssetImpl();
        cash.setAssetId("1");
        cash.setAssetName("Cash asset");
        cash.setAssetType(AssetType.CASH);

        AssetImpl termDepositAsset = new TermDepositAssetImpl();
        termDepositAsset.setAssetId("2");
        termDepositAsset.setAssetType(AssetType.TERM_DEPOSIT);
        ((TermDepositAssetImpl) termDepositAsset).setMaturityDate(new DateTime("2015-03-03"));

        AssetImpl share = new AssetImpl();
        share.setAssetId("3");
        share.setAssetName("Share asset");
        share.setAssetType(AssetType.SHARE);
        share.setAssetCode("SHA");

        AssetImpl mfund = new AssetImpl();
        mfund.setAssetId("4");
        mfund.setAssetName("Managed fund asset");
        mfund.setAssetType(AssetType.MANAGED_FUND);
        mfund.setAssetCode("MFA");

        AssetImpl mPortfolio = new AssetImpl();
        mPortfolio.setAssetId("5");
        mPortfolio.setAssetName("Managed portfolio asset");
        mPortfolio.setAssetType(AssetType.MANAGED_PORTFOLIO);
        mPortfolio.setAssetCode("MPA");

        List<AssetPerformance> investmentPerformance = new ArrayList<>();

        ap = new AssetPerformanceImpl();
        ap.setAssetType(AssetType.CASH);
        ap.setName("BT Cash");
        ap.setContainerType(ContainerType.DIRECT);
        ap.setOpeningBalance(BigDecimal.valueOf(100));
        ap.setClosingBalance(BigDecimal.valueOf(101));
        ap.setCapitalReturn(BigDecimal.valueOf(102));
        ap.setIncome(BigDecimal.valueOf(103));
        ap.setPerformanceDollar(BigDecimal.valueOf(104));
        ap.setPerformancePercent(BigDecimal.valueOf(2));
        ap.setPeriodOfDays(Integer.valueOf(105));
        ap.setPurchases(BigDecimal.valueOf(106));
        ap.setSales(BigDecimal.valueOf(107));
        ap.setAsset(cash);

        apTD = new AssetPerformanceImpl();
        apTD.setAssetType(AssetType.TERM_DEPOSIT);
        apTD.setContainerType(ContainerType.DIRECT);
        apTD.setAsset(termDepositAsset);

        apMF = new AssetPerformanceImpl();
        apMF.setName(mfund.getAssetName());
        apMF.setAssetType(AssetType.MANAGED_FUND);
        apMF.setContainerType(ContainerType.DIRECT);
        apMF.setAsset(mfund);

        apShare = new AssetPerformanceImpl();
        apShare.setName(share.getAssetName());
        apShare.setAssetType(AssetType.SHARE);
        apShare.setContainerType(ContainerType.DIRECT);
        apShare.setAsset(share);

        List<AssetPerformance> mpInvestments = new ArrayList<>();
        mpInvestments.add(apMF);
        mpInvestments.add(apShare);
        apMP = new ManagedPortfolioPerformanceImpl();
        apMP.setName(mPortfolio.getAssetName());
        apMP.setAssetType(AssetType.MANAGED_PORTFOLIO);
        apMP.setContainerType(ContainerType.MANAGED_PORTFOLIO);
        apMP.setAsset(mPortfolio);
        apMP.setAssetPerformances(mpInvestments);
        apMP.setId("MP ID");

        investmentPerformance.add(apMP);
        investmentPerformance.add(apTD);
        investmentPerformance.add(ap);

        PortfolioPerformanceOverallImpl accountPerformance = new PortfolioPerformanceOverallImpl();
        accountPerformance.setInvestmentPerformances(investmentPerformance);

        return accountPerformance;
    }

    private void mockTDPresentationService() {
        TermDepositPresentation tdPres = new TermDepositPresentation();
        tdPres.setBrandName("BT Term Deposit");
        tdPres.setBrandClass("BT");
        tdPres.setTerm("6 months");
        tdPres.setPaymentFrequency(PaymentFrequency.AT_MATURITY.getDisplayName().toLowerCase());

        Mockito.when(
                termDepositPresentationService.getTermDepositPresentation(Mockito.any(AccountKey.class),
                        Mockito.any(String.class), Mockito.any(ServiceErrors.class))).thenReturn(tdPres);
    }

    private void testPerformanceDto(PeriodPerformanceDto cash) {
        Assert.assertEquals(ap.getName(), cash.getName());
        Assert.assertEquals("", cash.getAssetCode());
        Assert.assertEquals(ap.getOpeningBalance(), cash.getOpeningBalance());
        Assert.assertEquals(ap.getClosingBalance(), cash.getClosingBalance());
        Assert.assertEquals(ap.getPurchases(), cash.getPurchase());
        Assert.assertEquals(ap.getSales(), cash.getSales());
        Assert.assertEquals(ap.getMarketMovement(), cash.getMovement());
        Assert.assertEquals(ap.getIncome(), cash.getNetIncome());
        Assert.assertEquals(ap.getPerformanceDollar(), cash.getPerformanceDollar());
        Assert.assertEquals(BigDecimal.valueOf(0.02), cash.getPerformancePercentage());
        Assert.assertEquals(ap.getPeriodOfDays(), cash.getPeriodHeld());
        Assert.assertEquals(BigDecimal.ZERO, cash.getPerformanceIncomePercentage());
        Assert.assertEquals(BigDecimal.valueOf(1.02), cash.getPerformanceGrowthPercentage());
        Assert.assertEquals(ap.getAssetType().getDisplayName(), cash.getAssetType());
        Assert.assertEquals(ap.getContainerType().toString(), cash.getContainerType());
    }

    private void testTDPerformanceDto(TermDepositPerformanceDto td) {
        Assert.assertEquals("BT Term Deposit", td.getName());
        Assert.assertEquals(new DateTime("2015-03-03"), td.getMaturityDate());
        Assert.assertEquals("6 months", td.getTerm());
        Assert.assertEquals("BT", td.getBrand());
        Assert.assertEquals(PaymentFrequency.AT_MATURITY.getDisplayName().toLowerCase(), td.getPaymentFrequency());
    }

    private void testMPPerformanceDto(ManagedPortfolioPerformanceDto mp) {

        Assert.assertEquals(apMP.getId(), EncodedString.toPlainText(mp.getInvestmentId()));

        Assert.assertEquals(2, mp.getAssetPerformance().size());

        Assert.assertEquals(AssetType.SHARE.getDisplayName(), mp.getAssetPerformance().get(0).getAssetType());
        Assert.assertEquals(AssetType.MANAGED_FUND.getDisplayName(), mp.getAssetPerformance().get(1).getAssetType());
    }
}
