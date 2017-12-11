package com.bt.nextgen.reports.account.valuation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.portfolio.v3.model.InvestmentAssetDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.CashManagementValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ManagedFundValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ManagedPortfolioValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ShareValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.TermDepositValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ValuationSummaryDto;
import com.bt.nextgen.api.portfolio.v3.service.valuation.ValuationDtoService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedFundHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedPortfolioAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ShareHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositHoldingImpl;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.base.SystemType;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.bt.nextgen.service.integration.portfolio.valuation.HinType;
import com.bt.nextgen.service.integration.portfolio.valuation.HoldingKey;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioStatus;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.asset.AssetType;

@RunWith(MockitoJUnitRunner.class)
public class PortfolioValuationReportTest {
    @InjectMocks
    private PortfolioValuationReport portfolioValuationReport;

    @Mock
    private ValuationDtoService valuationDtoService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private CmsService cmsService;

    @Mock
    private BrokerHelperService brokerHelperService;

    @Mock
    private OptionsService optionsService;

    @Mock
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    private String accountId;
    private String effectiveDate;
    private DatedValuationKey key;
    private ValuationDto valuationDto;
    private Map<String, Object> params;

    @Before
    public void setup() {
        accountId = "975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0";
        effectiveDate = "2014-09-09";
        key = new DatedValuationKey(accountId, new DateTime(effectiveDate), false);

        params = new HashMap<>();
        params.put("account-id", accountId);

        // Mock content service
        when(cmsService.getContent("DS-IP-0001")).thenReturn("MockString");
        when(cmsService.getContent("DS-IP-0200")).thenReturn("MockStringDirect");

        valuationDto = mockValuationDtoService();
        when(valuationDtoService.find((Matchers.any(DatedValuationKey.class)), any(ServiceErrorsImpl.class)))
                .thenReturn(valuationDto);

        when(optionsService.hasFeature(any(OptionKey.class), any(AccountKey.class), any(ServiceErrors.class))).thenReturn(Boolean.FALSE);
        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(mock(WrapAccountDetail.class));
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(ServiceErrors.class)))
                .thenReturn(UserExperience.ADVISED);
        when(accountIntegrationServiceFactory.getInstance(anyString())).thenReturn(accountIntegrationService);
    }

    private ValuationDto mockValuationDtoService() {
        List<ValuationSummaryDto> categories = new ArrayList<>();
        List<InvestmentValuationDto> cashInvestments = new ArrayList<>();
        List<InvestmentValuationDto> termDepositInvestments = new ArrayList<>();
        List<InvestmentValuationDto> managedFundInvestments = new ArrayList<>();
        List<InvestmentValuationDto> shareInvestments = new ArrayList<>();
        List<InvestmentValuationDto> managedPortfolioInvestments = new ArrayList<>();

        CashHoldingImpl cashHolding1 = new CashHoldingImpl();
        cashHolding1.setMarketValue(new BigDecimal(1000));
        cashHolding1.setAccountName("Cash account");
        cashHolding1.setCost(new BigDecimal(1000));
        cashHolding1.setYield(new BigDecimal(0.01));
        cashHolding1.setExternal(false);
        cashHolding1.setHoldingKey(HoldingKey.valueOf("CA001", "Cash account"));
        cashHolding1.setAccruedIncome(new BigDecimal(10));
        cashHolding1.setAvailableBalance(new BigDecimal(100));
        cashHolding1.setValueDateBalance(new BigDecimal(1000));

        CashHoldingImpl cashHolding2 = new CashHoldingImpl();
        cashHolding2.setMarketValue(new BigDecimal(1000));
        cashHolding2.setAccountName("External Cash account");
        cashHolding2.setCost(new BigDecimal(1000));
        cashHolding2.setYield(new BigDecimal(0.01));
        cashHolding2.setExternal(true);
        cashHolding2.setHoldingKey(HoldingKey.valueOf("CA001", "External Cash account"));
        cashHolding2.setAccruedIncome(new BigDecimal(10));
        cashHolding2.setAvailableBalance(new BigDecimal(100));
        cashHolding2.setValueDateBalance(new BigDecimal(1000));

        InvestmentValuationDto cashInvestment1 = new CashManagementValuationDto(null, cashHolding1, new BigDecimal(1000),
                new BigDecimal(1000), false);

        InvestmentValuationDto cashInvestment2 = new CashManagementValuationDto(null, cashHolding2, new BigDecimal(1000),
                new BigDecimal(1000), true);

        AssetImpl asset = new AssetImpl();
        asset.setAssetId("1234");
        asset.setAssetName("BHP Billiton");
        asset.setAssetCode("BHP001");
        asset.setAssetType(AssetType.SHARE);

        AssetImpl asset1 = new AssetImpl();
        asset1.setAssetId("1234");
        asset1.setAssetName("BT Balanced fund");
        asset1.setAssetCode("BLA001");
        asset1.setAssetType(AssetType.SHARE);

        TermDepositPresentation termDepositPresentation = new TermDepositPresentation();
        termDepositPresentation.setBrandClass("brandClass");
        termDepositPresentation.setBrandName("brandName");
        termDepositPresentation.setPaymentFrequency("Monthly");
        termDepositPresentation.setTerm("1 year");

        TermDepositHoldingImpl termDepositHolding = new TermDepositHoldingImpl();
        termDepositHolding.setMarketValue(new BigDecimal(1000));
        termDepositHolding.setCost(new BigDecimal(1000));
        termDepositHolding.setYield(new BigDecimal(0.01));
        termDepositHolding.setExternal(false);
        termDepositHolding.setMaturityDate(new DateTime("2016-08-17"));
        termDepositHolding.setHoldingKey(HoldingKey.valueOf("BT001", "BT Term deposit"));
        termDepositHolding.setAccruedIncome(new BigDecimal(10));
        termDepositHolding.setAvailableBalance(new BigDecimal(100));
        termDepositHolding.setSource("source1");

        InvestmentValuationDto termDeposit = new TermDepositValuationDto(termDepositPresentation, termDepositHolding,
                new BigDecimal(5000), "Reinvest", false);

        ManagedFundHoldingImpl managedFundHolding = new ManagedFundHoldingImpl();
        managedFundHolding.setMarketValue(new BigDecimal(1000));
        managedFundHolding.setCost(new BigDecimal(900));
        managedFundHolding.setExternal(false);
        managedFundHolding.setAvailableUnits(BigDecimal.ZERO);
        managedFundHolding.setAsset(asset1);
        managedFundHolding.setMarketValue(new BigDecimal(1000));
        managedFundHolding.setUnitPrice(new BigDecimal(900));
        managedFundHolding.setUnits(new BigDecimal(10));
        managedFundHolding.setEstdGainDollar(new BigDecimal(100));
        managedFundHolding.setEstdGainPercent(new BigDecimal(11.11));
        managedFundHolding.setUnitPriceDate(new DateTime("2015-06-15"));
        managedFundHolding.setAccruedIncome(BigDecimal.ZERO);
        managedFundHolding.setHoldingKey(HoldingKey.valueOf("BT002", "BT Balanced fund"));

        InvestmentAssetDto investmentAsset = new InvestmentAssetDto(managedFundHolding, new BigDecimal(1000));

        List<String> availableMethods = new ArrayList<>();
        availableMethods.add(DistributionMethod.CASH.getDisplayName());
        availableMethods.add(DistributionMethod.REINVEST.getDisplayName());
        InvestmentValuationDto managedFundDto = new ManagedFundValuationDto(managedFundHolding, new BigDecimal(20),
                investmentAsset, DistributionMethod.REINVEST.getDisplayName(), availableMethods, false);

        ShareHoldingImpl shareHolding = new ShareHoldingImpl();
        shareHolding.setMarketValue(new BigDecimal(1000));
        shareHolding.setCost(new BigDecimal(1000));
        shareHolding.setExternal(false);
        shareHolding.setAvailableUnits(BigDecimal.ZERO);
        shareHolding.setAsset(asset);
        shareHolding.setHinType(HinType.INDIVIDUAL);
        shareHolding.setHoldingKey(HoldingKey.valueOf("H001", "hname"));

        ShareHoldingImpl shareHolding1 = new ShareHoldingImpl();
        shareHolding1.setMarketValue(new BigDecimal(1000));
        shareHolding1.setCost(new BigDecimal(1000));
        shareHolding1.setExternal(true);
        shareHolding1.setAvailableUnits(BigDecimal.ZERO);
        shareHolding1.setAsset(asset);
        shareHolding1.setSource("source");
        shareHolding1.setHinType(HinType.INDIVIDUAL);
        shareHolding1.setHoldingKey(HoldingKey.valueOf("H001", "hname"));

        InvestmentAssetDto shareAsset = new InvestmentAssetDto(shareHolding, new BigDecimal(1000));

        List<String> shareAvailableMethods = new ArrayList<>();
        shareAvailableMethods.add(DistributionMethod.CASH.getDisplayName());
        shareAvailableMethods.add(DistributionMethod.REINVEST.getDisplayName());
        InvestmentValuationDto shareValuationDto = new ShareValuationDto(shareHolding, new BigDecimal(20), shareAsset,
                shareAvailableMethods, false);
        InvestmentValuationDto shareValuationDto1 = new ShareValuationDto(shareHolding1, new BigDecimal(20), shareAsset,
                shareAvailableMethods, true);

        ManagedPortfolioAccountValuationImpl managedPortfolioAccountValuation = new ManagedPortfolioAccountValuationImpl();
        managedPortfolioAccountValuation.setSubAccountKey(SubAccountKey.valueOf(accountId));
        managedPortfolioAccountValuation.setAsset(asset);
        managedPortfolioAccountValuation.setStatus(ManagedPortfolioStatus.OPEN);

        List<InvestmentAssetDto> investments = new ArrayList<>();
        investments.add(new InvestmentAssetDto(managedFundHolding, new BigDecimal(1000)));

        Map<String, BigDecimal> balances = new HashMap<>();
        balances.put("balance", new BigDecimal(1000));
        balances.put("estimatedGain", new BigDecimal(4));
        balances.put("averageCost", new BigDecimal(20));

        ManagedPortfolioValuationDto managedPortfolioValuationDto = new ManagedPortfolioValuationDto(
                managedPortfolioAccountValuation, balances, new BigDecimal(5000), false, investments, false);

        cashInvestments.add(cashInvestment1);
        cashInvestments.add(cashInvestment2);
        termDepositInvestments.add(termDeposit);
        managedFundInvestments.add(managedFundDto);
        shareInvestments.add(shareValuationDto);
        shareInvestments.add(shareValuationDto1);
        managedPortfolioInvestments.add(managedPortfolioValuationDto);

        ValuationSummaryDto cashValuationSummary = new ValuationSummaryDto(AssetType.CASH, new BigDecimal(10000),
                cashInvestments);
        ValuationSummaryDto termDepositValuationSummary = new ValuationSummaryDto(AssetType.TERM_DEPOSIT,
                new BigDecimal(10000), termDepositInvestments);
        ValuationSummaryDto managedFundSummary = new ValuationSummaryDto(AssetType.MANAGED_FUND, new BigDecimal(10000),
                managedFundInvestments);
        ValuationSummaryDto shareSummary = new ValuationSummaryDto(AssetType.SHARE, new BigDecimal(10000),
                shareInvestments);

        ValuationSummaryDto managedPortfolioSummary = new ValuationSummaryDto(AssetType.MANAGED_PORTFOLIO,
                new BigDecimal(10000), managedPortfolioInvestments);

        categories.add(cashValuationSummary);
        categories.add(termDepositValuationSummary);
        categories.add(managedFundSummary);
        categories.add(shareSummary);
        categories.add(managedPortfolioSummary);
        return new ValuationDto(key, new BigDecimal(100), false, categories);
    }

    @Test
    public void testPortfolioValuationReport_whenTodayThenValueDateBalanceIsUsed() {

        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();
        params.put(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, accountId);
        params.put(UriMappingConstants.EFFECTIVE_DATE_PARAMETER_MAPPING, new LocalDate().toString());

        Collection<?> result = portfolioValuationReport.getData(params, dataCollections);
        Assert.assertNotNull(result);
        PortfolioValuationReportData portfolioValuationReportData = (PortfolioValuationReportData) result.iterator().next();
        Assert.assertEquals(portfolioValuationReportData.getChildren().size(), 5);

        ValuationCategoryReportData cashSummaryReportData = (ValuationCategoryReportData) portfolioValuationReportData
                .getChildren().get(0);

        Assert.assertEquals("Cash", cashSummaryReportData.getCategoryName());
        Assert.assertEquals("20.20%", cashSummaryReportData.getCategoryPercent());

        CashValuationReportData cashValuationReportData = (CashValuationReportData) cashSummaryReportData.getChildren().get(0);
        Assert.assertEquals("$1,000.00", cashValuationReportData.getBalance());
        Assert.assertEquals("100.00%", cashValuationReportData.getAllocationPercent());
    }

    @Test
    public void testPortfolioValuationReport() {

        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();
        params.put(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, accountId);
        params.put(UriMappingConstants.EFFECTIVE_DATE_PARAMETER_MAPPING, effectiveDate);
        params.put("nothing", null);
        params.put("integer", Integer.valueOf(10));

        Collection<?> result = portfolioValuationReport.getData(params, dataCollections);
        Assert.assertNotNull(result);
        PortfolioValuationReportData portfolioValuationReportData = (PortfolioValuationReportData) result.iterator().next();
        Assert.assertEquals(portfolioValuationReportData.getChildren().size(), 5);

        ValuationCategoryReportData cashSummaryReportData = (ValuationCategoryReportData) portfolioValuationReportData
                .getChildren().get(0);


        Assert.assertEquals("Cash", cashSummaryReportData.getCategoryName());
        Assert.assertEquals("20.20%", cashSummaryReportData.getCategoryPercent());

        CashValuationReportData cashValuationReportData1 = (CashValuationReportData) cashSummaryReportData.getChildren()
                                                                                                         .get(0);
        Assert.assertEquals("$1,000.00", cashValuationReportData1.getBalance());
        Assert.assertEquals("100.00%", cashValuationReportData1.getAllocationPercent());
        Assert.assertEquals("0.01%",cashValuationReportData1.getRate());

        CashValuationReportData cashValuationReportData2 = (CashValuationReportData) cashSummaryReportData.getChildren()
                .get(1);
        Assert.assertEquals("$1,000.00", cashValuationReportData2.getBalance());
        Assert.assertEquals("100.00%", cashValuationReportData2.getAllocationPercent());
        Assert.assertEquals("-",cashValuationReportData2.getRate());


        ValuationCategoryReportData termDepositSummaryReportData = (ValuationCategoryReportData) portfolioValuationReportData
                .getChildren().get(1);

        TermDepositValuationReportData termDepositValuationReportData = (TermDepositValuationReportData) termDepositSummaryReportData
                .getChildren().get(0);

        Assert.assertEquals("$1,000.00", termDepositValuationReportData.getBalance());
        Assert.assertEquals("Matures on 17 Aug 2016", termDepositValuationReportData.getMaturityDetail());
        Assert.assertEquals("TermDepositValuationReportData", termDepositValuationReportData.getType());
        Assert.assertEquals("1 year term interest Monthly", termDepositValuationReportData.getTermDetail());
        Assert.assertEquals("Reinvest", termDepositValuationReportData.getMaturityInstruction());
        Assert.assertEquals("0.01%", termDepositValuationReportData.getRate());
        Assert.assertEquals("20.00%", termDepositValuationReportData.getAllocationPercent());
        Assert.assertEquals("BT Term deposit", termDepositValuationReportData.getName());
        Assert.assertEquals(false, termDepositValuationReportData.getExternalAsset());

        SimpleValuationReportData interestAccruedReportData = (SimpleValuationReportData) termDepositSummaryReportData
                .getChildren().get(1);

        Assert.assertEquals("Interest accrued", interestAccruedReportData.getName());

        ValuationCategoryReportData managedFundSummaryReportData = (ValuationCategoryReportData) portfolioValuationReportData
                .getChildren().get(2);

        InvestmentValuationReportData investmentValuationReportData = (InvestmentValuationReportData) managedFundSummaryReportData
                .getChildren().get(0);

        Assert.assertEquals("$900.00", investmentValuationReportData.getAverageCost());
        Assert.assertEquals("BLA001", investmentValuationReportData.getCode());
        Assert.assertEquals("<b>BLA001</b> &#183 BT Balanced fund", investmentValuationReportData.getName());
        Assert.assertEquals(false, investmentValuationReportData.getExternalAsset());
        Assert.assertEquals("$100.00", investmentValuationReportData.getDollarGain());
        Assert.assertEquals("11.11%", investmentValuationReportData.getPercentGain());
        Assert.assertEquals("$900.00", investmentValuationReportData.getLastPrice());
        Assert.assertEquals("15 Jun 2015", investmentValuationReportData.getLastPriceDate());
        Assert.assertEquals("10", investmentValuationReportData.getQuantity());

        ValuationCategoryReportData managedPortfolioCategoryReportData = (ValuationCategoryReportData) portfolioValuationReportData
                .getChildren().get(4);

        CompositeValuationReportData managedPortfolioValuationReportData = (CompositeValuationReportData) managedPortfolioCategoryReportData
                .getChildren().get(0);

        List<ValuationReportData> investments = managedPortfolioValuationReportData.getChildren();

        Assert.assertEquals("20.00%", managedPortfolioValuationReportData.getAllocationPercent());
        Assert.assertEquals("$1,000.00", managedPortfolioValuationReportData.getBalance());
        Assert.assertEquals("BHP001", managedPortfolioValuationReportData.getCode());
        Assert.assertEquals("BHP Billiton", managedPortfolioValuationReportData.getName());
        Assert.assertEquals("$4.00", managedPortfolioValuationReportData.getDollarGain());
        Assert.assertEquals("20.00%", managedPortfolioValuationReportData.getPercentGain());

        Assert.assertEquals(2, investments.size());

        ValuationCategoryReportData shareSummaryReportData = (ValuationCategoryReportData) portfolioValuationReportData
                .getChildren().get(3);
        InvestmentValuationReportData shareValuationReportData = (InvestmentValuationReportData) shareSummaryReportData
                .getChildren().get(0);
        InvestmentValuationReportData shareValuationReportData1 = (InvestmentValuationReportData) shareSummaryReportData
                .getChildren().get(1);
        assertNotNull(shareValuationReportData);
        Assert.assertEquals(HinType.INDIVIDUAL.name(), shareValuationReportData.getHinType());
        assertNotNull(shareValuationReportData1);
        assertEquals("source<br/><b>BHP001</b> &#183 BHP Billiton", shareValuationReportData1.getName());
    }

    @Test
    public void testPortfolioValuationReport_forTD() {

        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();
        params.put(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, accountId);
        params.put(UriMappingConstants.EFFECTIVE_DATE_PARAMETER_MAPPING, effectiveDate);
        params.put("nothing", null);
        params.put("integer", Integer.valueOf(10));

        TermDepositHoldingImpl termDepositHolding = new TermDepositHoldingImpl();
        termDepositHolding.setMarketValue(new BigDecimal(1000));
        termDepositHolding.setCost(new BigDecimal(1000));
        termDepositHolding.setYield(new BigDecimal(0.01));
        termDepositHolding.setExternal(false);
        termDepositHolding.setMaturityDate(new DateTime("2016-08-17"));
        termDepositHolding.setHoldingKey(HoldingKey.valueOf("BT001", "BT Term deposit"));
        termDepositHolding.setAccruedIncome(new BigDecimal(10));
        termDepositHolding.setAvailableBalance(new BigDecimal(100));
        termDepositHolding.setSource("source1");

        List<InvestmentValuationDto> termDepositInvestments = new ArrayList<>();
        InvestmentValuationDto termDeposit = new TermDepositValuationDto(new TermDepositPresentation(), termDepositHolding,
                new BigDecimal(5000), "Reinvest", false);

        termDepositInvestments.add(termDeposit);

        ValuationSummaryDto termDepositValuationSummary = new ValuationSummaryDto(AssetType.TERM_DEPOSIT,
                new BigDecimal(10000), termDepositInvestments);
        termDepositValuationSummary.setThirdPartySource(SystemType.WRAP.name());

        List<ValuationSummaryDto> categories = new ArrayList<>();
        categories.add(termDepositValuationSummary);
        ValuationDto valuationDto = new ValuationDto(key, new BigDecimal(100), false, categories);
        when(valuationDtoService.find((Matchers.any(DatedValuationKey.class)), any(ServiceErrorsImpl.class)))
                .thenReturn(valuationDto);

        Collection<?> result = portfolioValuationReport.getData(params, dataCollections);
        Assert.assertNotNull(result);
        PortfolioValuationReportData portfolioValuationReportData = (PortfolioValuationReportData) result.iterator().next();
        Assert.assertEquals(portfolioValuationReportData.getChildren().size(), 1);


        ValuationCategoryReportData termDepositSummaryReportData = (ValuationCategoryReportData) portfolioValuationReportData
                .getChildren().get(0);

        TermDepositValuationReportData termDepositValuationReportData = (TermDepositValuationReportData) termDepositSummaryReportData
                .getChildren().get(0);

        Assert.assertEquals("$1,000.00", termDepositValuationReportData.getBalance());
        Assert.assertNull(termDepositValuationReportData.getMaturityDetail());
        Assert.assertEquals("TermDepositValuationReportData", termDepositValuationReportData.getType());
        Assert.assertNull(termDepositValuationReportData.getTermDetail());
        Assert.assertEquals("Reinvest", termDepositValuationReportData.getMaturityInstruction());
        Assert.assertEquals("-", termDepositValuationReportData.getRate());
        Assert.assertEquals("20.00%", termDepositValuationReportData.getAllocationPercent());
        Assert.assertEquals("BT Term deposit", termDepositValuationReportData.getName());
        Assert.assertEquals(false, termDepositValuationReportData.getExternalAsset());
    }

    @Test
    public void testGetDisclaimer() {
        assertEquals("MockString", portfolioValuationReport.getDisclaimer(params, new HashMap<String, Object>()));
        verify(cmsService, times(1)).getContent("DS-IP-0001");
    }

    @Test
    public void testGetDisclaimer_forDirect() {
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(ServiceErrors.class)))
                .thenReturn(UserExperience.DIRECT);
        assertEquals("MockStringDirect", portfolioValuationReport.getDisclaimer(params, new HashMap<String, Object>()));
        verify(cmsService, times(1)).getContent("DS-IP-0200");
    }

    @Test
    public void testMfGainLossParam() {
        Map<String, String> params = new HashMap<>();
        String gainLossParam = portfolioValuationReport.getMFGainLossParam(params);
        assertEquals("$", gainLossParam);

        params.put("mfGainLossParam", "100");
        assertEquals("100", portfolioValuationReport.getMFGainLossParam(params));
    }

    @Test
    public void testMpGainLossParam() {
        Map<String, String> params = new HashMap<>();
        String gainLossParam = portfolioValuationReport.getMPGainLossParam(params);
        assertEquals("$", gainLossParam);

        params.put("mpGainLossParam", "100");
        assertEquals("100", portfolioValuationReport.getMPGainLossParam(params));
    }

    @Test
    public void testTMPGainLossParam() {
        Map<String, String> params = new HashMap<>();
        String gainLossParam = portfolioValuationReport.getTMPGainLossParam(params);
        assertEquals("$", gainLossParam);

        params.put("tmpGainLossParam", "100");
        assertEquals("100", portfolioValuationReport.getTMPGainLossParam(params));
    }

    @Test
    public void testLsGainLossParam() {
        Map<String, String> params = new HashMap<>();
        String gainLossParam = portfolioValuationReport.getLSGainLossParam(params);
        assertEquals("$", gainLossParam);

        params.put("lsGainLossParam", "100");
        assertEquals("100", portfolioValuationReport.getLSGainLossParam(params));
    }

    @Test
    public void testGetReportTitle() {
        assertEquals("Portfolio valuation", portfolioValuationReport.getReportTitle());
    }

    @Test
    public void testGetReportSubTitle() {
        Map<String, String> params = new HashMap<>();
        params.put("effective-date", "2017-06-01");

        assertEquals("As at 01 Jun 2017", portfolioValuationReport.getReportSubTitle(params));
    }

    @Test
    public void testGetEffectiveDate() {
        Map<String, String> params = new HashMap<>();
        params.put("effective-date", "2017-06-01");

        assertEquals("As at 01 Jun 2017", portfolioValuationReport.getReportSubTitle(params));
    }

    @Test
    public void testGetCashLogoOption() {
        Map<String, String> params = new HashMap<>();
        params.put("account-id", accountId);

        assertFalse(portfolioValuationReport.getCashLogoOption(params));
    }

    @Test
    public void testGetSummaryDescription() {
        assertEquals("Portfolio valuation", portfolioValuationReport.getSummaryDescription(null, null));
    }

    @Test
    public void testGetReportType() {
        assertEquals("Portfolio valuation", portfolioValuationReport.getReportType(null, null));
    }

    @Test
    public void getSummaryValue() {
        Map<String, Object> params = new HashMap<>();
        params.put("effective-date", "2017-06-01");

        assertEquals("$7,030.00", portfolioValuationReport.getSummaryValue(params, null));
    }
}

