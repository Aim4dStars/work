package com.bt.nextgen.api.performance.service;

import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import com.bt.nextgen.api.account.v2.model.WrapAccountDetailDto;
import com.bt.nextgen.api.account.v2.service.TermDepositPresentationService;
import com.bt.nextgen.api.account.v2.service.WrapAccountDetailDtoService;
import com.bt.nextgen.api.account.v2.service.valuation.ValuationDtoService;
import com.bt.nextgen.api.performance.model.PerformanceDto;
import com.bt.nextgen.api.performance.model.PortfolioPerformanceDto;
import com.bt.nextgen.api.performance.model.TermDepositPerformanceDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.AssetPerformanceImpl;
import com.bt.nextgen.service.avaloq.asset.ManagedPortfolioPerformanceImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.avaloq.performance.PerformanceImpl;
import com.bt.nextgen.service.avaloq.portfolio.performance.PortfolioPerformanceOverallImpl;
import com.bt.nextgen.service.avaloq.portfolio.performance.WrapAccountPerformanceImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetPerformance;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;
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
import java.util.HashMap;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PerformanceDtoServiceTest {

    private String accountId = "36846";

    @InjectMocks
    private PerformanceDtoServiceImpl performanceDtoService;

    @Mock
    private AccountPerformanceIntegrationService accountPerformanceService;

    @Mock
    private ValuationDtoService valuationService;

    @Mock
    private WrapAccountDetailDtoService accountService;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private TermDepositPresentationService termDepositPresentationService;

    PerformanceImpl performance = new PerformanceImpl();
    PortfolioPerformanceOverallImpl accountPerformanceOverAll = new PortfolioPerformanceOverallImpl();
    AssetPerformanceImpl assetPerformance = null;
    AssetPerformanceImpl assetPerformanceTd = null;
    AssetPerformanceImpl assetPerformanceMf1 = null;
    AssetPerformanceImpl assetPerformanceMf2 = null;
    ManagedPortfolioPerformanceImpl assetPerformanceMp1 = null;

    DateRangeAccountKey dateRangeAccountKey = null;

    @Before
    public void setup() throws Exception {
        dateRangeAccountKey = new DateRangeAccountKey(EncodedString.fromPlainText(accountId).toString(), new DateTime(),
                new DateTime());

        performance.setActiveRor(BigDecimal.valueOf(10.0));
        performance.setCapitalGrowth(BigDecimal.valueOf(20.0));
        performance.setOpeningBalance(BigDecimal.valueOf(23434343));
        performance.setBalanceBeforeFee(BigDecimal.valueOf(23232));
        performance.setIncome(BigDecimal.valueOf(3434343));
        performance.setTwrrAccum(BigDecimal.valueOf(324343));
        performance.setTwrrGross(BigDecimal.valueOf(34343));
        performance.setIncomeRtn(BigDecimal.valueOf(30.0));
        AssetImpl termDepositAsset = new TermDepositAssetImpl();
        termDepositAsset.setAssetId("20168");
        termDepositAsset.setAssetName("BT");
        ((TermDepositAssetImpl) termDepositAsset).setGenericAssetId("20169");
        termDepositAsset.setBrand("80000064");

        AssetImpl share = new AssetImpl();
        share.setAssetId("93679");
        share.setAssetName("Test Share");
        share.setAssetType(AssetType.SHARE);
        share.setAssetCode("APIR2323");

        AssetImpl mfund1 = new AssetImpl();
        mfund1.setAssetId("93659");
        mfund1.setAssetName("DEF Test Fund");
        mfund1.setAssetType(AssetType.MANAGED_FUND);
        mfund1.setAssetCode("APIR999");

        AssetImpl mfund2 = new AssetImpl();
        mfund2.setAssetId("93669");
        mfund2.setAssetName("ABC Test Fund");
        mfund2.setAssetType(AssetType.MANAGED_FUND);
        mfund2.setAssetCode("APIR912");

        HashMap<String, Asset> assetMap = new HashMap<>();
        assetMap.put("20168", termDepositAsset);
        assetMap.put("93659", mfund1);
        assetMap.put("93669", mfund2);
        assetMap.put("93679", share);

        Mockito.when(assetIntegrationService.loadAssets(Mockito.anyCollectionOf(String.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(assetMap);

        List<AssetPerformance> investmentPerformance = new ArrayList<>();

        assetPerformance = new AssetPerformanceImpl();
        assetPerformance.setAssetType(AssetType.CASH);
        assetPerformance.setContainerType(ContainerType.DIRECT);
        assetPerformance.setOpeningBalance(BigDecimal.valueOf(23232322));
        assetPerformance.setClosingBalance(BigDecimal.valueOf(23232244));
        assetPerformance.setCapitalReturn(BigDecimal.valueOf(3434343));
        assetPerformance.setIncome(BigDecimal.valueOf(343433));
        assetPerformance.setPerformanceDollar(BigDecimal.valueOf(324444));
        assetPerformance.setPerformancePercent(BigDecimal.valueOf(0.2567));
        assetPerformance.setPeriodOfDays(Integer.valueOf(345));
        assetPerformance.setPurchases(BigDecimal.valueOf(34343));
        assetPerformance.setSales(BigDecimal.valueOf(343343));
        assetPerformanceTd = new AssetPerformanceImpl();
        assetPerformanceTd.setAssetType(AssetType.TERM_DEPOSIT);
        assetPerformanceTd.setContainerType(ContainerType.DIRECT);
        assetPerformanceTd.setOpeningBalance(BigDecimal.valueOf(23232322));
        assetPerformanceTd.setClosingBalance(BigDecimal.valueOf(23232244));
        assetPerformanceTd.setCapitalReturn(BigDecimal.valueOf(3434343));
        assetPerformanceTd.setIncome(BigDecimal.valueOf(343433));
        assetPerformanceTd.setPerformanceDollar(BigDecimal.valueOf(324444));
        assetPerformanceTd.setPerformancePercent(BigDecimal.valueOf(0.2567));
        assetPerformanceTd.setPeriodOfDays(Integer.valueOf(345));
        assetPerformanceTd.setPurchases(BigDecimal.valueOf(34343));
        assetPerformanceTd.setSales(BigDecimal.valueOf(343343));
        assetPerformanceTd.setAsset(termDepositAsset);

        assetPerformanceMf1 = new AssetPerformanceImpl();
        assetPerformanceMf1.setName(mfund1.getAssetName());
        assetPerformanceMf1.setAssetType(AssetType.MANAGED_FUND);
        assetPerformanceMf1.setContainerType(ContainerType.DIRECT);
        assetPerformanceMf1.setOpeningBalance(BigDecimal.valueOf(23232322));
        assetPerformanceMf1.setClosingBalance(BigDecimal.valueOf(23232244));
        assetPerformanceMf1.setCapitalReturn(BigDecimal.valueOf(3434343));
        assetPerformanceMf1.setIncome(BigDecimal.valueOf(343433));
        assetPerformanceMf1.setPerformanceDollar(BigDecimal.valueOf(324444));
        assetPerformanceMf1.setPerformancePercent(BigDecimal.valueOf(0.2567));
        assetPerformanceMf1.setPeriodOfDays(Integer.valueOf(345));
        assetPerformanceMf1.setPurchases(BigDecimal.valueOf(34343));
        assetPerformanceMf1.setSales(BigDecimal.valueOf(343343));
        assetPerformanceMf1.setAsset(mfund1);

        assetPerformanceMf2 = new AssetPerformanceImpl();
        assetPerformanceMf2.setName(mfund2.getAssetName());
        assetPerformanceMf2.setAssetType(AssetType.MANAGED_FUND);
        assetPerformanceMf2.setContainerType(ContainerType.DIRECT);
        assetPerformanceMf2.setOpeningBalance(BigDecimal.valueOf(3434343));
        assetPerformanceMf2.setClosingBalance(BigDecimal.valueOf(3434343));
        assetPerformanceMf2.setCapitalReturn(BigDecimal.valueOf(3434343));
        assetPerformanceMf2.setIncome(BigDecimal.valueOf(453433));
        assetPerformanceMf2.setPerformanceDollar(BigDecimal.valueOf(453433));
        assetPerformanceMf2.setPerformancePercent(BigDecimal.valueOf(0.3467));
        assetPerformanceMf2.setPeriodOfDays(Integer.valueOf(123));
        assetPerformanceMf2.setPurchases(BigDecimal.valueOf(23232));
        assetPerformanceMf2.setSales(BigDecimal.valueOf(23232));
        assetPerformanceMf2.setAsset(mfund2);

        investmentPerformance.add(assetPerformanceMf2);
        investmentPerformance.add(assetPerformanceMf1);

        List<AssetPerformance> mpInvestments = new ArrayList<>();
        mpInvestments.addAll(investmentPerformance);

        assetPerformanceMp1 = new ManagedPortfolioPerformanceImpl();
        assetPerformanceMp1.setName(share.getAssetName());
        assetPerformanceMp1.setContainerType(ContainerType.MANAGED_PORTFOLIO);
        assetPerformanceMp1.setOpeningBalance(BigDecimal.valueOf(23232322));
        assetPerformanceMp1.setClosingBalance(BigDecimal.valueOf(23232244));
        assetPerformanceMp1.setCapitalReturn(BigDecimal.valueOf(3434343));
        assetPerformanceMp1.setIncome(BigDecimal.valueOf(343433));
        assetPerformanceMp1.setPerformanceDollar(BigDecimal.valueOf(324444));
        assetPerformanceMp1.setPerformancePercent(BigDecimal.valueOf(0.2567));
        assetPerformanceMp1.setPeriodOfDays(Integer.valueOf(345));
        assetPerformanceMp1.setPurchases(BigDecimal.valueOf(34343));
        assetPerformanceMp1.setSales(BigDecimal.valueOf(343343));
        assetPerformanceMp1.setAsset(share);
        assetPerformanceMp1.setAssetPerformances(mpInvestments);

        investmentPerformance.add(assetPerformanceMp1);
        investmentPerformance.add(assetPerformanceTd);
        investmentPerformance.add(assetPerformance);

        accountPerformanceOverAll.setInvestmentPerformances(investmentPerformance);

        TermDepositPresentation tdPres = new TermDepositPresentation();
        tdPres.setBrandName("BT Term Deposit");
        tdPres.setBrandClass("BT");
        tdPres.setTerm("6 months");
        tdPres.setPaymentFrequency(PaymentFrequency.AT_MATURITY.getDisplayName().toLowerCase());

        Mockito.when(
                termDepositPresentationService.getTermDepositPresentation(Mockito.any(AccountKey.class),
                        Mockito.any(String.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(tdPres);

    }

    @Test
    public void testGetAccountPerformance_When_Performance_Null() {
        Mockito.when(
                accountPerformanceService.loadAccountOverallPerformance(Mockito.any(AccountKey.class),
                        Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(
                null);

        PortfolioPerformanceDto performanceDto = performanceDtoService.find(dateRangeAccountKey, new ServiceErrorsImpl());

        Assert.assertNotNull(performanceDto);
        Assert.assertEquals(BigDecimal.ZERO, performanceDto.getPerformanceBeforeFeesDollars());
        Assert.assertEquals(BigDecimal.ZERO, performanceDto.getPerformanceBeforeFeesPercent());
        Assert.assertEquals(BigDecimal.ZERO, performanceDto.getPerformanceAfterFeesDollars());
        Assert.assertEquals(BigDecimal.ZERO, performanceDto.getPerformanceAfterFeesPercent());
        Assert.assertEquals(BigDecimal.ZERO, performanceDto.getPerformanceGrowthPercent());
        Assert.assertEquals(BigDecimal.ZERO, performanceDto.getPerformanceIncomePercent());
        Assert.assertEquals(BigDecimal.ZERO, performanceDto.getPerformancePercent());
        Assert.assertEquals(null, performanceDto.getInvestmentPerformances());

    }

    @Test
    public void testGetAccountPerformance_When_Performance_Present() {

        WrapAccountPerformanceImpl accountPerformance = new WrapAccountPerformanceImpl();
        accountPerformance.setPeriodPerformanceData(performance);

        Mockito.when(
                accountPerformanceService.loadAccountTotalPerformance(Mockito.any(AccountKey.class), Mockito.any(String.class),
                        Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(
                accountPerformance);

        WrapAccountPerformanceImpl sinceInceptionPerformance = new WrapAccountPerformanceImpl();
        sinceInceptionPerformance.setPeriodPerformanceData(performance);

        Mockito.when(
                accountPerformanceService.loadAccountPerformanceSummarySinceInception(Mockito.any(AccountKey.class),
                        Mockito.any(String.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(
                sinceInceptionPerformance);

        Mockito.when(
                accountPerformanceService.loadAccountOverallPerformance(Mockito.any(AccountKey.class),
                        Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(
                accountPerformanceOverAll);

        WrapAccountDetailDto accountDto = new WrapAccountDetailDto(new com.bt.nextgen.api.account.v2.model.AccountKey(
                EncodedString.fromPlainText(accountId).toString()), new DateTime(), new DateTime());

        Mockito.when(
                accountService.find(Mockito.any(com.bt.nextgen.api.account.v2.model.AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(accountDto);

        PortfolioPerformanceDto performanceDto = performanceDtoService.find(dateRangeAccountKey, new ServiceErrorsImpl());

        List<PerformanceDto> accountPerformances = performanceDto.getInvestmentPerformances();
        Assert.assertNotNull(accountPerformances);
        Assert.assertEquals(5, accountPerformances.size());

        PerformanceDto cashPerfDto = accountPerformances.get(0);
        Assert.assertEquals(assetPerformance.getAssetType().getDisplayName(), cashPerfDto.getAssetType());
        Assert.assertEquals(assetPerformance.getContainerType().getCode(), cashPerfDto.getContainerType());
        Assert.assertEquals(assetPerformance.getOpeningBalance(), cashPerfDto.getOpeningBalance());
        Assert.assertEquals(assetPerformance.getClosingBalance(), cashPerfDto.getClosingBalance());
        Assert.assertEquals(assetPerformance.getPurchases(), cashPerfDto.getPurchase());
        Assert.assertEquals(assetPerformance.getSales(), cashPerfDto.getSales());
        Assert.assertEquals(assetPerformance.getMarketMovement(), cashPerfDto.getMovement());
        Assert.assertEquals(assetPerformance.getPerformanceDollar(), cashPerfDto.getPerformanceDollar());
        Assert.assertEquals(BigDecimal.valueOf(0.002567), cashPerfDto.getPerformancePercentage());
        Assert.assertEquals(assetPerformance.getIncome(), cashPerfDto.getNetIncome());
        Assert.assertEquals(assetPerformance.getPeriodOfDays(), cashPerfDto.getPeriodHeld());

        TermDepositPerformanceDto tdPerfDto = (TermDepositPerformanceDto) accountPerformances.get(1);
        Assert.assertEquals(assetPerformanceTd.getAssetType().getDisplayName(), tdPerfDto.getAssetType());
        Assert.assertEquals(assetPerformanceTd.getContainerType().getCode(), tdPerfDto.getContainerType());
        Assert.assertEquals(assetPerformanceTd.getOpeningBalance(), tdPerfDto.getOpeningBalance());
        Assert.assertEquals(assetPerformanceTd.getClosingBalance(), tdPerfDto.getClosingBalance());
        Assert.assertEquals(assetPerformanceTd.getPurchases(), tdPerfDto.getPurchase());
        Assert.assertEquals(assetPerformanceTd.getSales(), tdPerfDto.getSales());
        Assert.assertEquals(assetPerformanceTd.getMarketMovement(), tdPerfDto.getMovement());
        Assert.assertEquals(assetPerformanceTd.getPerformanceDollar(), tdPerfDto.getPerformanceDollar());
        Assert.assertEquals(BigDecimal.valueOf(0.002567), tdPerfDto.getPerformancePercentage());
        Assert.assertEquals(assetPerformanceTd.getIncome(), tdPerfDto.getNetIncome());
        Assert.assertEquals(assetPerformanceTd.getPeriodOfDays(), tdPerfDto.getPeriodHeld());

        PerformanceDto mfPerfDto2 = accountPerformances.get(2);
        Assert.assertEquals(assetPerformanceMf2.getName(), mfPerfDto2.getName());
        Assert.assertEquals(assetPerformanceMf2.getAssetType().getDisplayName(), mfPerfDto2.getAssetType());
        Assert.assertEquals(assetPerformanceMf2.getContainerType().getCode(), mfPerfDto2.getContainerType());
        Assert.assertEquals(assetPerformanceMf2.getOpeningBalance(), mfPerfDto2.getOpeningBalance());
        Assert.assertEquals(assetPerformanceMf2.getClosingBalance(), mfPerfDto2.getClosingBalance());
        Assert.assertEquals(assetPerformanceMf2.getPurchases(), mfPerfDto2.getPurchase());
        Assert.assertEquals(assetPerformanceMf2.getSales(), mfPerfDto2.getSales());
        Assert.assertEquals(assetPerformanceMf2.getMarketMovement(), mfPerfDto2.getMovement());
        Assert.assertEquals(assetPerformanceMf2.getPerformanceDollar(), mfPerfDto2.getPerformanceDollar());
        Assert.assertEquals(BigDecimal.valueOf(0.003467), mfPerfDto2.getPerformancePercentage());
        Assert.assertEquals(assetPerformanceMf2.getIncome(), mfPerfDto2.getNetIncome());
        Assert.assertEquals(assetPerformanceMf2.getPeriodOfDays(), mfPerfDto2.getPeriodHeld());

        PerformanceDto mfPerfDto1 = accountPerformances.get(3);
        Assert.assertEquals(assetPerformanceMf1.getName(), mfPerfDto1.getName());
        Assert.assertEquals(assetPerformanceMf1.getAssetType().getDisplayName(), mfPerfDto1.getAssetType());
        Assert.assertEquals(assetPerformanceMf1.getContainerType().getCode(), mfPerfDto1.getContainerType());
        Assert.assertEquals(assetPerformanceMf1.getOpeningBalance(), mfPerfDto1.getOpeningBalance());
        Assert.assertEquals(assetPerformanceMf1.getClosingBalance(), mfPerfDto1.getClosingBalance());
        Assert.assertEquals(assetPerformanceMf1.getPurchases(), mfPerfDto1.getPurchase());
        Assert.assertEquals(assetPerformanceMf1.getSales(), mfPerfDto1.getSales());
        Assert.assertEquals(assetPerformanceMf1.getMarketMovement(), mfPerfDto1.getMovement());
        Assert.assertEquals(assetPerformanceMf1.getPerformanceDollar(), mfPerfDto1.getPerformanceDollar());
        Assert.assertEquals(BigDecimal.valueOf(0.002567), mfPerfDto1.getPerformancePercentage());
        Assert.assertEquals(assetPerformanceMf1.getIncome(), mfPerfDto1.getNetIncome());
        Assert.assertEquals(assetPerformanceMf1.getPeriodOfDays(), mfPerfDto1.getPeriodHeld());

        PerformanceDto mpPerfDto1 = accountPerformances.get(4);
        Assert.assertEquals(assetPerformanceMp1.getContainerType().getCode(), mpPerfDto1.getContainerType());
        Assert.assertEquals(assetPerformanceMp1.getAssetPerformances().size(), 2);
        Assert.assertEquals(performance.getPerformanceBeforeFee(), performanceDto.getPerformanceBeforeFeesDollars());
        Assert.assertEquals(performance.getPerformanceAfterFee(), performanceDto.getPerformanceAfterFeesDollars());
        Assert.assertEquals(BigDecimal.valueOf(0.3), performanceDto.getPerformanceIncomePercent());
        Assert.assertEquals(BigDecimal.valueOf(0.2), performanceDto.getPerformanceGrowthPercent());
    }
}
