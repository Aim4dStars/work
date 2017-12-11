package com.bt.nextgen.service.wrap.integration.asset.performance;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.AssetPerformanceImpl;
import com.bt.nextgen.service.avaloq.asset.ManagedPortfolioPerformanceImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.avaloq.portfolio.performance.PortfolioPerformanceOverallImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceOverall;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.wrap.model.AssetPerformance;
import com.btfin.panorama.wrap.rest.client.AssetPerformanceServiceRestClient;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WrapAssetPerformanceIntegrationServiceImplTest {

    @Mock
    private AssetPerformanceServiceRestClient assetPerformanceService;
    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private WrapPerformanceOverallConverter wrapPerformanceOverallConverter;

    @InjectMocks
    private WrapAssetPerformanceIntegrationServiceImpl wrapAssetPerformanceIntegrationService;

    private DateTime startDate;
    private DateTime endDate;
    private AccountPerformanceOverall accountPerformanceOverall;
    private AssetPerformanceImpl ap;
    private AssetPerformanceImpl apTD;
    private AssetPerformanceImpl apShare;
    private AssetPerformanceImpl apMF;
    private ManagedPortfolioPerformanceImpl apMP;
    private AccountKey accountKey;
    private WrapAccountDetailImpl accountDetail = new WrapAccountDetailImpl();
    List <SubAccount> list = new ArrayList <>();

    @Before
    public void init() throws Exception {
        accountKey = AccountKey.valueOf("accountId");
        accountDetail.setAccountStructureType(AccountStructureType.SUPER);

        accountPerformanceOverall = createPerformanceTestData();
        startDate = DateTime.parse("2015-01-01");
        endDate = DateTime.parse("2015-03-03");
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetail);
        when(assetPerformanceService.getAssetPerformanceForClient(any(String.class), any(DateTime.class), any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(createWrapPerformanceTestData());
        when(wrapPerformanceOverallConverter.toModel(any(List.class), any(AccountStructureType.class), any(ServiceErrors.class)))
                .thenReturn(createPerformanceTestData());
    }
    @Test
    public void loadAccountOverallPerformance() {
        DateTime fromDate = DateTime.parse("01/07/2008 00:00:00", DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss"));
        DateTime toDate = DateTime.parse("16/05/2011 00:00:00", DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss"));
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        String clientId = "M01234567";
        AccountPerformanceOverall assetPerf = wrapAssetPerformanceIntegrationService.loadAccountOverallPerformance(accountDetail, fromDate, toDate);

        assertThat(assetPerf, notNullValue());
        assertThat(assetPerf.getInvestmentPerformances().size(), is(3));
    }
    @Test
    public void combineAssetPerformance() {
        AssetImpl cashAsset = new AssetImpl();
        cashAsset.setAssetId("1");
        cashAsset.setAssetName("Cash asset");
        cashAsset.setAssetType(AssetType.CASH);

        AccountPerformanceOverall acpo = new PortfolioPerformanceOverallImpl();
        AssetPerformanceImpl mpPerf = new AssetPerformanceImpl();

        mpPerf.setCode("BHP");
        mpPerf.setName("BHP limited");
        mpPerf.setAssetType(AssetType.forDisplay("Managed fund"));
        mpPerf.setOpeningBalance(new BigDecimal("100"));
        mpPerf.setClosingBalance(new BigDecimal("50"));
        mpPerf.setSales(new BigDecimal("50"));
        mpPerf.setPurchases(new BigDecimal("50"));
        mpPerf.setMarketMovement(new BigDecimal("50"));
        mpPerf.setIncome(new BigDecimal("50"));
        mpPerf.setPerformanceDollar(new BigDecimal("50"));
        acpo.getInvestmentPerformances().add(mpPerf);

        AssetPerformanceImpl cash = new AssetPerformanceImpl();
        cash.setAssetType(AssetType.forDisplay("Cash"));
        cash.setCode("11111111");
        cash.setName("Cash Account Management");
        cash.setOpeningBalance(new BigDecimal("10"));
        cash.setClosingBalance(new BigDecimal("15"));
        cash.setSales(new BigDecimal("15"));
        cash.setPurchases(new BigDecimal("5"));
        cash.setMarketMovement(new BigDecimal("60"));
        cash.setIncome(new BigDecimal("50"));
        cash.setPerformanceDollar(new BigDecimal("60"));
        cash.setAsset(cashAsset);
        acpo.getInvestmentPerformances().add(cash);

        AssetPerformanceImpl apCashPending;
        apCashPending = new AssetPerformanceImpl();
        apCashPending.setAssetType(AssetType.CASH);
        apCashPending.setName("Pending");
        apCashPending.setCode(null);
        apCashPending.setContainerType(ContainerType.DIRECT);
        apCashPending.setOpeningBalance(BigDecimal.valueOf(0));
        apCashPending.setClosingBalance(BigDecimal.valueOf(0));
        apCashPending.setCapitalReturn(BigDecimal.valueOf(0));
        apCashPending.setIncome(BigDecimal.valueOf(0));
        apCashPending.setPerformanceDollar(BigDecimal.valueOf(0));
        apCashPending.setPerformancePercent(BigDecimal.valueOf(0));
        apCashPending.setPeriodOfDays(Integer.valueOf(0));
        apCashPending.setPurchases(BigDecimal.valueOf(0));
        apCashPending.setSales(BigDecimal.valueOf(0));
        apCashPending.setAsset(cashAsset);
        acpo.getInvestmentPerformances().add(apCashPending);

        AccountPerformanceOverall wrapAssPerfOv = new PortfolioPerformanceOverallImpl();

        AssetPerformanceImpl wrapPerf = new AssetPerformanceImpl();
        wrapPerf.setAssetType(AssetType.forDisplay("Managed fund"));
        wrapPerf.setCode("BHP");
        wrapPerf.setName("BHP limited");
        wrapPerf.setOpeningBalance(new BigDecimal("10"));
        wrapPerf.setClosingBalance(new BigDecimal("15"));
        wrapPerf.setSales(new BigDecimal("15"));
        wrapPerf.setPurchases(new BigDecimal("5"));
        wrapPerf.setMarketMovement(new BigDecimal("50"));
        wrapPerf.setIncome(new BigDecimal("50"));
        wrapPerf.setPerformanceDollar(new BigDecimal("50"));
        wrapAssPerfOv.getInvestmentPerformances().add(wrapPerf);

        AssetPerformanceImpl wrapPerf1 = new AssetPerformanceImpl();
        wrapPerf1.setAssetType(AssetType.forDisplay("Listed security"));
        wrapPerf1.setCode("CBA");
        wrapPerf1.setName("Commonwealth Bank");
        wrapPerf1.setOpeningBalance(new BigDecimal("10"));
        wrapPerf1.setClosingBalance(new BigDecimal("15"));
        wrapPerf1.setSales(new BigDecimal("15"));
        wrapPerf1.setPurchases(new BigDecimal("5"));
        wrapPerf1.setMarketMovement(new BigDecimal("50"));
        wrapPerf1.setIncome(new BigDecimal("50"));
        wrapPerf1.setPerformanceDollar(new BigDecimal("50"));
        wrapAssPerfOv.getInvestmentPerformances().add(wrapPerf1);

        AssetPerformanceImpl wrapPerf2 = new AssetPerformanceImpl();
        wrapPerf2.setAssetType(AssetType.forDisplay("Cash"));
        wrapPerf2.setCode("WRAPWCA");
        wrapPerf2.setName("Cash");
        wrapPerf2.setOpeningBalance(new BigDecimal("10"));
        wrapPerf2.setClosingBalance(new BigDecimal("40"));
        wrapPerf2.setSales(new BigDecimal("15"));
        wrapPerf2.setPurchases(new BigDecimal("5"));
        wrapPerf2.setMarketMovement(new BigDecimal("50"));
        wrapPerf2.setIncome(new BigDecimal("50"));
        wrapPerf2.setPerformanceDollar(new BigDecimal("50"));
        wrapAssPerfOv.getInvestmentPerformances().add(wrapPerf2);

        AccountPerformanceOverall comAssPerf = wrapAssetPerformanceIntegrationService.combineAssetPerformance(acpo, wrapAssPerfOv, accountDetail);

        assertThat(comAssPerf, notNullValue());
        assertThat(comAssPerf.getInvestmentPerformances().size(), is(4));
        assertThat(comAssPerf.getInvestmentPerformances().get(3).getName(), is("Cash Account"));
        assertThat(comAssPerf.getInvestmentPerformances().get(3).getClosingBalance(), is(BigDecimal.valueOf(15)));
    }
    private AccountPerformanceOverall createPerformanceTestData() {

        AssetImpl cash = new AssetImpl();
        cash.setAssetId("1");
        cash.setAssetName("Cash asset");
        cash.setAssetType(AssetType.CASH);

        TermDepositAssetImpl termDepositAsset = new TermDepositAssetImpl();
        termDepositAsset.setAssetId("2");
        termDepositAsset.setAssetType(AssetType.TERM_DEPOSIT);
        termDepositAsset.setMaturityDate(new DateTime("2015-03-03"));

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

        List<com.bt.nextgen.service.integration.asset.AssetPerformance> investmentPerformance = new ArrayList<>();

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

        List<com.bt.nextgen.service.integration.asset.AssetPerformance> mpInvestments = new ArrayList<>();
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

    private List<AssetPerformance> createWrapPerformanceTestData() {
        List<AssetPerformance> perfList = new ArrayList<>();
        AssetPerformance ap = new AssetPerformance();
        ap.setAssetCluster("Cash");
        ap.setSecurityCode("WRAPWCA");
        ap.setSecurityName("Cash");
        ap.setOpeningBalance(BigDecimal.valueOf(100));
        ap.setClosingBalance(BigDecimal.valueOf(101));
        ap.setIncome(BigDecimal.valueOf(103));
        ap.setPurchases(BigDecimal.valueOf(106));
        ap.setSales(BigDecimal.valueOf(107));
        ap.setPerformanceAmount(BigDecimal.valueOf(108));
        perfList.add(ap);

        AssetPerformance ap1 = new AssetPerformance();
        ap1.setAssetCluster("Listed security");
        ap1.setSecurityCode("BHP");
        ap1.setSecurityName("BHP Billiton Limited");
        ap1.setOpeningBalance(BigDecimal.valueOf(100));
        ap1.setClosingBalance(BigDecimal.valueOf(101));
        ap1.setIncome(BigDecimal.valueOf(103));
        ap1.setPurchases(BigDecimal.valueOf(106));
        ap1.setSales(BigDecimal.valueOf(107));
        ap.setPerformanceAmount(BigDecimal.valueOf(108));
        perfList.add(ap1);

        return perfList;
    }
}
