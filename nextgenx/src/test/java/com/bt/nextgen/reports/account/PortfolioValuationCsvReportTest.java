package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.portfolio.v3.model.InvestmentAssetDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.AbstractInvestmentValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.CashManagementValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ManagedPortfolioValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ValuationSummaryDto;
import com.bt.nextgen.api.portfolio.v3.service.valuation.ValuationDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.base.SystemType;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PortfolioValuationCsvReportTest {

    private static final String CATEGORY_BT_CASH = "BT Cash";
    private static final String CATEGORY_TERM_DEPOSITS = "Term deposits";
    private static final String CATEGORY_MANAGED_FUNDS = "Managed funds";
    private static final String CATEGORY_MANAGED_PORTFOLIO = "Managed portfolios";
    private static final String CATEGORY_TAILORED_PORTFOLIO = "Tailored portfolios";
    private static final String CATEGORY_LISTED_SECURITIES = "Listed securities";

    private String accountId;
    private String effectiveDate;
    private DatedValuationKey key;
    private ValuationDto valuationDto;

    @InjectMocks
    private PortfolioValuationCsvReport portfolioValuationCsvReport;

    @Mock
    private ValuationDtoService valuationDtoService;

    @Before
    public void setup() {

        accountId = "975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0";
        effectiveDate = new LocalDate().toString();
        key = new DatedValuationKey(accountId, new DateTime(effectiveDate), false);

        valuationDto = mockValuationDtoService();
        when(valuationDtoService.find((Matchers.any(DatedValuationKey.class)), any(ServiceErrorsImpl.class)))
                .thenReturn(valuationDto);
    }

    private ValuationDto mockValuationDtoService() {
        List<ValuationSummaryDto> categories = new ArrayList<>();
        List<InvestmentValuationDto> cashInvestments = new ArrayList<>();
        List<InvestmentValuationDto> termDepositInvestments = new ArrayList<>();
        List<InvestmentValuationDto> managedFundInvestments = new ArrayList<>();
        List<InvestmentValuationDto> managedInvestments = new ArrayList<>();
        List<InvestmentValuationDto> shareInvestments = new ArrayList<>();

        CashManagementValuationDto cashInvestment = Mockito.mock(CashManagementValuationDto.class);
        Mockito.when(cashInvestment.getBalance()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(cashInvestment.getAvailableBalance()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(cashInvestment.getIncome()).thenReturn(BigDecimal.valueOf(100));
        Mockito.when(cashInvestment.getPortfolioPercent()).thenReturn(BigDecimal.valueOf(0.1));
        Mockito.when(cashInvestment.getValueDateBalance()).thenReturn(BigDecimal.valueOf(2000));
        Mockito.when(cashInvestment.getValueDatePercent()).thenReturn(BigDecimal.valueOf(0.1));
        Mockito.when(cashInvestment.getOutstandingCash()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(cashInvestment.getOutstandingCashPercent()).thenReturn(BigDecimal.valueOf(0.1));
        Mockito.when(cashInvestment.getInterestRate()).thenReturn(BigDecimal.valueOf(0.1));
        Mockito.when(cashInvestment.getName()).thenReturn(CATEGORY_BT_CASH);
        Mockito.when(cashInvestment.getCategoryName()).thenReturn(AssetType.CASH.getGroupDescription());
      

        InvestmentValuationDto termDepositInvestment = new AbstractInvestmentValuationDto(null, new BigDecimal(1000),
                new BigDecimal(1000), new BigDecimal(100), new BigDecimal(100), null, false, false) {

            @Override
            public String getName() {
                return CATEGORY_TERM_DEPOSITS;
            }

            @Override
            public String getCategoryName() {
                return AssetType.TERM_DEPOSIT.getGroupDescription();
            }
        };

        InvestmentValuationDto managedFundInvestment = new AbstractInvestmentValuationDto(null, new BigDecimal(1000),
                new BigDecimal(1000), new BigDecimal(100), new BigDecimal(100), null, false, false) {

            @Override
            public String getName() {
                return CATEGORY_MANAGED_FUNDS;
            }

            @Override
            public String getCategoryName() {
                return AssetType.MANAGED_FUND.getGroupDescription();
            }
        };

        InvestmentAssetDto investmentAsset = Mockito.mock(InvestmentAssetDto.class);
        Mockito.when(investmentAsset.getAllocationPercent()).thenReturn(BigDecimal.ONE);
        Mockito.when(investmentAsset.getAssetCode()).thenReturn("assetCode");

        InvestmentAssetDto cashInvestmentAsset = Mockito.mock(InvestmentAssetDto.class);
        Mockito.when(cashInvestmentAsset.getAllocationPercent()).thenReturn(BigDecimal.ONE);
        Mockito.when(cashInvestmentAsset.getAssetType()).thenReturn(AssetType.CASH.name());
        
        ManagedPortfolioValuationDto managedInvestment = Mockito.mock(ManagedPortfolioValuationDto.class);
        Mockito.when(managedInvestment.getName()).thenReturn("MP1");
        Mockito.when(managedInvestment.getCategoryName()).thenReturn(AssetType.MANAGED_PORTFOLIO.getGroupDescription());
        Mockito.when(managedInvestment.getBalance()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(managedInvestment.getAvailableBalance()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(managedInvestment.getIncome()).thenReturn(BigDecimal.valueOf(100));
        Mockito.when(managedInvestment.getExternalAsset()).thenReturn(Boolean.FALSE);
        Mockito.when(managedInvestment.getIncomeOnly()).thenReturn(Boolean.FALSE);
        Mockito.when(managedInvestment.getIncomePreference()).thenReturn(IncomePreference.REINVEST);
        Mockito.when(managedInvestment.getIncomePercent()).thenReturn(BigDecimal.ONE);
        Mockito.when(managedInvestment.getInvestmentAssets()).thenReturn(Arrays.asList(cashInvestmentAsset, investmentAsset));
        Mockito.when(managedInvestment.getPortfolioPercent()).thenReturn(BigDecimal.valueOf(0.1));

        ManagedPortfolioValuationDto managedInvestment2 = Mockito.mock(ManagedPortfolioValuationDto.class);
        Mockito.when(managedInvestment2.getName()).thenReturn("MP2");
        Mockito.when(managedInvestment2.getCategoryName()).thenReturn(AssetType.MANAGED_PORTFOLIO.getGroupDescription());
        Mockito.when(managedInvestment2.getBalance()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(managedInvestment2.getAvailableBalance()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(managedInvestment2.getIncome()).thenReturn(BigDecimal.valueOf(100));
        Mockito.when(managedInvestment2.getExternalAsset()).thenReturn(Boolean.FALSE);
        Mockito.when(managedInvestment2.getIncomeOnly()).thenReturn(Boolean.FALSE);
        Mockito.when(managedInvestment2.getIncomePercent()).thenReturn(BigDecimal.ONE);
        Mockito.when(managedInvestment2.getInvestmentAssets()).thenReturn(Arrays.asList(cashInvestmentAsset, investmentAsset));
        Mockito.when(managedInvestment2.getPortfolioPercent()).thenReturn(BigDecimal.valueOf(0.1));
        
        ManagedPortfolioValuationDto tailoredInvestment = Mockito.mock(ManagedPortfolioValuationDto.class);
        Mockito.when(tailoredInvestment.getName()).thenReturn("TP1");
        Mockito.when(tailoredInvestment.getCategoryName()).thenReturn(AssetType.TAILORED_PORTFOLIO.getGroupDescription());
        Mockito.when(tailoredInvestment.getBalance()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(tailoredInvestment.getAvailableBalance()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(tailoredInvestment.getIncome()).thenReturn(BigDecimal.valueOf(100));
        Mockito.when(tailoredInvestment.getExternalAsset()).thenReturn(Boolean.FALSE);
        Mockito.when(tailoredInvestment.getIncomeOnly()).thenReturn(Boolean.FALSE);
        Mockito.when(tailoredInvestment.getTailorMade()).thenReturn(Boolean.TRUE);
        Mockito.when(tailoredInvestment.getIncomePreference()).thenReturn(IncomePreference.TRANSFER);
        Mockito.when(tailoredInvestment.getIncomePercent()).thenReturn(BigDecimal.ONE);
        Mockito.when(tailoredInvestment.getInvestmentAssets()).thenReturn(Arrays.asList(cashInvestmentAsset, investmentAsset));
        Mockito.when(tailoredInvestment.getPortfolioPercent()).thenReturn(BigDecimal.valueOf(0.1));
        
        ManagedPortfolioValuationDto externalInvestment = Mockito.mock(ManagedPortfolioValuationDto.class);
        Mockito.when(externalInvestment.getName()).thenReturn("ExternalMP");
        Mockito.when(externalInvestment.getCategoryName()).thenReturn(AssetType.MANAGED_PORTFOLIO.getGroupDescription());
        Mockito.when(externalInvestment.getBalance()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(externalInvestment.getExternalAsset()).thenReturn(Boolean.TRUE);
        Mockito.when(externalInvestment.getInvestmentAssets()).thenReturn(Arrays.asList(cashInvestmentAsset, investmentAsset));
        Mockito.when(externalInvestment.getPortfolioPercent()).thenReturn(BigDecimal.valueOf(0.1));

        InvestmentValuationDto shareInvestment = new AbstractInvestmentValuationDto(null, new BigDecimal(1000),
                new BigDecimal(1000), new BigDecimal(100), new BigDecimal(100), null, false, false) {

            @Override
            public String getName() {
                return CATEGORY_LISTED_SECURITIES;
            }

            @Override
            public String getCategoryName() {
                return AssetType.SHARE.getGroupDescription();
            }
        };
        cashInvestments.add(cashInvestment);
        termDepositInvestments.add(termDepositInvestment);
        managedFundInvestments.add(managedFundInvestment);
        managedInvestments.add(managedInvestment);
        managedInvestments.add(managedInvestment2);
        managedInvestments.add(tailoredInvestment);
        managedInvestments.add(externalInvestment);
        shareInvestments.add(shareInvestment);
        ValuationSummaryDto cashValuationSummary = new ValuationSummaryDto(AssetType.CASH, BigDecimal.valueOf(1000),
                cashInvestments);

        ValuationSummaryDto termDepositValuationSummaryWithWrap = new ValuationSummaryDto(AssetType.TERM_DEPOSIT,
                BigDecimal.valueOf(1000), termDepositInvestments);
        termDepositValuationSummaryWithWrap.setThirdPartySource(SystemType.WRAP.name());

        ValuationSummaryDto termDepositValuationSummaryWithoutWrap = new ValuationSummaryDto(AssetType.TERM_DEPOSIT,
                BigDecimal.valueOf(1000), termDepositInvestments);

        ValuationSummaryDto managedFundValuationSummary = new ValuationSummaryDto(AssetType.MANAGED_FUND,
                BigDecimal.valueOf(1000), managedFundInvestments);

        ValuationSummaryDto managedPortfolioValuationSummary = new ValuationSummaryDto(AssetType.MANAGED_PORTFOLIO,
                BigDecimal.valueOf(1000), managedInvestments);

        ValuationSummaryDto shareValuationSummary = new ValuationSummaryDto(AssetType.SHARE, BigDecimal.valueOf(1000),
                shareInvestments);

        categories.add(cashValuationSummary);
        categories.add(termDepositValuationSummaryWithWrap);
        categories.add(termDepositValuationSummaryWithoutWrap);
        categories.add(managedFundValuationSummary);
        categories.add(managedPortfolioValuationSummary);
        categories.add(shareValuationSummary);
        return new ValuationDto(key, new BigDecimal(100), false, categories);
    }

    @Test
    public void testPortfolioValuationCsvReport_CashValuation() {

        Map<String, String> params = new HashMap<>();
        params.put(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, accountId);
        params.put(UriMappingConstants.EFFECTIVE_DATE_PARAMETER_MAPPING, effectiveDate);

        List<InvestmentDetailDto> resultInvestments = portfolioValuationCsvReport.getInvestmentDetails(params);
        Assert.assertNotNull(resultInvestments);
        Assert.assertFalse(resultInvestments.isEmpty());
        Assert.assertEquals("Interest accrued", resultInvestments.get(3).getAssetName());
        Assert.assertEquals(16, resultInvestments.size());
        Boolean resultShowCurrentDateView = portfolioValuationCsvReport.showCurrentDateView(params);
        assertEquals(true, resultShowCurrentDateView.booleanValue());
    }

    @Test
    public void testPortfolioValuationCsvReport_WithoutCashValuation() {

        Map<String, String> params = new HashMap<>();
        params.put(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, accountId);

        List<ValuationSummaryDto> categories = new ArrayList<>();
        List<InvestmentValuationDto> termDepositInvestments = new ArrayList<>();
        InvestmentValuationDto termDepositInvestment = new AbstractInvestmentValuationDto(null, new BigDecimal(1000),
                new BigDecimal(1000), new BigDecimal(100), new BigDecimal(100), null, false, false) {

            @Override
            public String getName() {
                return CATEGORY_TERM_DEPOSITS;
            }

            @Override
            public String getCategoryName() {
                return AssetType.TERM_DEPOSIT.getGroupDescription();
            }
        };
        termDepositInvestments.add(termDepositInvestment);
        ValuationSummaryDto termDepositValuationSummary = new ValuationSummaryDto(AssetType.TERM_DEPOSIT,
                BigDecimal.valueOf(1000), termDepositInvestments);
        categories.add(termDepositValuationSummary);

        valuationDto = new ValuationDto(key, new BigDecimal(100), false, categories);
        when(valuationDtoService.find((Matchers.any(DatedValuationKey.class)), any(ServiceErrorsImpl.class)))
                .thenReturn(valuationDto);

        List<InvestmentDetailDto> resultInvestments = portfolioValuationCsvReport.getInvestmentDetails(params);
        Assert.assertNotNull(resultInvestments);
        Assert.assertFalse(resultInvestments.isEmpty());
        Assert.assertEquals("Interest accrued", resultInvestments.get(0).getAssetName());
        Assert.assertEquals(1, resultInvestments.size());
        Boolean resultShowCurrentDateView = portfolioValuationCsvReport.showCurrentDateView(params);
        assertEquals(true, resultShowCurrentDateView.booleanValue());
    }

    @Test
    public void testCsvReportData_whenIncomePreferenceSetOnPortfolio_thenIncomeElectionForNonCashAssetsContainsPreference() {
        Map<String, String> params = new HashMap<>();
        params.put(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, accountId);
        params.put(UriMappingConstants.EFFECTIVE_DATE_PARAMETER_MAPPING, effectiveDate);

        List<InvestmentDetailDto> resultInvestments = portfolioValuationCsvReport.getInvestmentDetails(params);

        for (InvestmentDetailDto resultInvestment : resultInvestments) {
            if (resultInvestment.getAssetCode() == null) {
                Assert.assertNull(resultInvestment.getIncomeElection());
            } else if ("Managed portfolios-MP1".equals(resultInvestment.getCategoryName())) {
                Assert.assertEquals("Reinvest into model", resultInvestment.getIncomeElection());
            } else if ("Tailored portfolios-TP1".equals(resultInvestment.getCategoryName())) {
                Assert.assertEquals("Transfer to BT Cash", resultInvestment.getIncomeElection());
            } else if ("Managed portfolios-MP2".equals(resultInvestment.getCategoryName())) {
                Assert.assertEquals("Reinvest into model", resultInvestment.getIncomeElection());
            }
        }
    }
}
