package com.bt.nextgen.api.portfolio.v3.service.cashmovements;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.portfolio.v3.model.cashmovements.CashMovementsDto;
import com.bt.nextgen.api.portfolio.v3.model.cashmovements.OutstandingCash;
import com.bt.nextgen.api.portfolio.v3.model.cashmovements.OutstandingMovementsDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ParameterisedDatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.service.TermDepositPresentationService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.avaloq.portfolio.PortfolioIntegrationServiceFactory;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositHoldingImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import com.bt.nextgen.service.integration.income.CashIncome;
import com.bt.nextgen.service.integration.income.DistributionIncome;
import com.bt.nextgen.service.integration.income.DividendIncome;
import com.bt.nextgen.service.integration.income.HoldingIncomeDetails;
import com.bt.nextgen.service.integration.income.Income;
import com.bt.nextgen.service.integration.income.IncomeIntegrationService;
import com.bt.nextgen.service.integration.income.SubAccountIncomeDetails;
import com.bt.nextgen.service.integration.income.TermDepositIncome;
import com.bt.nextgen.service.integration.income.WrapAccountIncomeDetails;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.cashmovements.CashMovement;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.CashAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.TermDepositAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CashMovementsDtoServiceTest {
    @InjectMocks
    private CashMovementsDtoServiceImpl cashMovements;

    @Mock
    private PortfolioIntegrationService portfolioIntegrationService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private IncomeIntegrationService incomeIntegrationService;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private TermDepositPresentationService tdPresService;

    @Mock
    private PortfolioIntegrationServiceFactory portfolioIntegrationServiceFactory;

    @Mock
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    private Map<String, Asset> assetMap = new HashMap<>();

    private TermDepositPresentation tdPres;

    @Before
    public void setup() throws Exception {
        WrapAccountDetail account = mock(WrapAccountDetail.class);
        when(account.getMinCashAmount()).thenReturn(BigDecimal.valueOf(2000));
        when(account.isHasMinCash()).thenReturn(true);
        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(account);

        Asset cashAsset = mock(Asset.class);
        when(cashAsset.getAssetId()).thenReturn("cash asset");
        when(cashAsset.getAssetCode()).thenReturn("cash code");
        when(cashAsset.getAssetType()).thenReturn(AssetType.CASH);
        assetMap.put(cashAsset.getAssetId(), cashAsset);

        TermDepositAsset tdAsset = mock(TermDepositAsset.class);
        when(tdAsset.getAssetId()).thenReturn("td asset");
        when(tdAsset.getAssetCode()).thenReturn("td code");
        when(tdAsset.getMaturityDate()).thenReturn(new DateTime("2000-1-1"));
        when(tdAsset.getAssetType()).thenReturn(AssetType.TERM_DEPOSIT);
        assetMap.put(tdAsset.getAssetId(), tdAsset);

        Asset shareAsset = mock(Asset.class);
        when(shareAsset.getAssetId()).thenReturn("sh asset");
        when(shareAsset.getAssetCode()).thenReturn("sh code");
        when(shareAsset.getAssetType()).thenReturn(AssetType.SHARE);
        assetMap.put(shareAsset.getAssetId(), shareAsset);

        when(assetIntegrationService.loadAssets(Mockito.anyCollection(), any(ServiceErrors.class))).thenReturn(assetMap);
        when(assetIntegrationService.loadAssets(Mockito.anyCollection(), any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(assetMap);

        tdPres = new TermDepositPresentation();
        when(tdPresService.getTermDepositPresentation(any(AccountKey.class), any(String.class), any(ServiceErrors.class)))
                .thenReturn(tdPres);

        when(portfolioIntegrationServiceFactory.getInstance(anyString())).thenReturn(portfolioIntegrationService);
        when(accountIntegrationServiceFactory.getInstance(anyString())).thenReturn(accountIntegrationService);
    }

    @Test
    public void testCashMovements_whenAPortfolioHasNoAcccounts_thenAllocationIsEmpty() {
        when(portfolioIntegrationService.loadCashMovement(any(AccountKey.class), any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(new ArrayList<CashMovement>());
        when(incomeIntegrationService.loadIncomeAccruedDetails(any(AccountKey.class), any(DateTime.class),
                any(ServiceErrors.class))).thenReturn(new ArrayList<WrapAccountIncomeDetails>());

        CashHoldingImpl cashHolding = new CashHoldingImpl();
        cashHolding.setAccruedIncome(BigDecimal.valueOf(0));
        cashHolding.setAsset(assetMap.get("cash asset"));

        List<AccountHolding> holdings = new ArrayList<>();
        holdings.add(cashHolding);

        SubAccountValuation subv = mock(CashAccountValuation.class);
        when(subv.getAssetType()).thenReturn(AssetType.CASH);
        when(subv.getHoldings()).thenReturn(holdings);
        when(subv.getAccruedIncome()).thenReturn(cashHolding.getAccruedIncome());

        WrapAccountValuation valuation = mock(WrapAccountValuation.class);
        when(valuation.getSubAccountValuations()).thenReturn(Collections.singletonList(subv));

        when(portfolioIntegrationService.loadWrapAccountValuation(any(AccountKey.class), any(DateTime.class),
                Mockito.anyBoolean(), any(ServiceErrors.class))).thenReturn(valuation);

        Map<String, String> params = new HashMap<>(1);

        DatedValuationKey valuationKey =
                new ParameterisedDatedValuationKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0", new DateTime(), false, params);

        CashMovementsDto dto = cashMovements.find(valuationKey, new FailFastErrorsImpl());

        assertNotNull(dto);
        assertEquals(null, dto.getReservedCash());
        assertEquals(BigDecimal.ZERO, dto.getAvailableCash());
        assertEquals(BigDecimal.valueOf(2000), dto.getMinCash());
        assertEquals(BigDecimal.ZERO, dto.getTradeDateCash());
        assertEquals(BigDecimal.ZERO, dto.getValueDateCash());
        assertEquals(0, dto.getOutstandingCash().size());
        assertEquals(BigDecimal.ZERO, dto.getOutstandingIncome().getAmount());
        assertEquals(BigDecimal.valueOf(0), dto.getOther());
        assertEquals(BigDecimal.valueOf(0), dto.getOutstandingCashTotal());
        assertEquals(BigDecimal.valueOf(0), dto.getOutstandingTotal());
        assertEquals(BigDecimal.valueOf(0), dto.getTotalCashMovements());
        assertEquals(0, dto.getOutstandingIncome().getOutstanding().size());

    }

    @Test
    public void testCashMovements_whenAPortfolioCashAccrued_thenIncomeMovementsContainsIt() {
        when(portfolioIntegrationService.loadCashMovement(any(AccountKey.class), any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(new ArrayList<CashMovement>());
        CashHoldingImpl cashHolding = new CashHoldingImpl();
        cashHolding.setAsset(assetMap.get("cash asset"));

        List<AccountHolding> holdings = new ArrayList<>();
        holdings.add(cashHolding);

        SubAccountValuation subv = mock(CashAccountValuation.class);
        when(subv.getAssetType()).thenReturn(AssetType.CASH);
        when(subv.getHoldings()).thenReturn(holdings);
        when(subv.getAccruedIncome()).thenReturn(cashHolding.getAccruedIncome());

        WrapAccountValuation valuation = mock(WrapAccountValuation.class);
        when(valuation.getSubAccountValuations()).thenReturn(Collections.singletonList(subv));

        when(portfolioIntegrationService.loadWrapAccountValuation(any(AccountKey.class), any(DateTime.class),
                Mockito.anyBoolean(), any(ServiceErrors.class))).thenReturn(valuation);

        List<Income> incomes = new ArrayList<>();
        CashIncome cashIncome = mock(CashIncome.class);

        when(cashIncome.getAmount()).thenReturn(BigDecimal.valueOf(2));
        when(cashIncome.getPaymentDate()).thenReturn(new DateTime("2002-01-01"));
        incomes.add(cashIncome);

        HoldingIncomeDetails holdingIncome = mock(HoldingIncomeDetails.class);
        when(holdingIncome.getAsset()).thenReturn(assetMap.get("cash asset"));
        when(holdingIncome.getIncomes()).thenReturn(incomes);
        SubAccountIncomeDetails subI = mock(SubAccountIncomeDetails.class);
        when(subI.getAssetType()).thenReturn(AssetType.CASH);
        when(subI.isDirect()).thenReturn(true);
        when(subI.getIncomes()).thenReturn(Collections.singletonList(holdingIncome));

        WrapAccountIncomeDetails wrapIncome = mock(WrapAccountIncomeDetails.class);
        when(wrapIncome.getSubAccountIncomeDetailsList()).thenReturn(Collections.singletonList(subI));

        when(incomeIntegrationService.loadIncomeAccruedDetails(any(AccountKey.class), any(DateTime.class),
                any(ServiceErrors.class))).thenReturn(Collections.singletonList(wrapIncome));

        DatedValuationKey valuationKey = new DatedValuationKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0", new DateTime(),
                false, true);

        CashMovementsDto dto = cashMovements.find(valuationKey, new FailFastErrorsImpl());

        assertNotNull(dto);
        assertEquals(null, dto.getReservedCash());
        assertEquals(BigDecimal.ZERO, dto.getAvailableCash());
        assertEquals(BigDecimal.valueOf(2000), dto.getMinCash());
        assertEquals(BigDecimal.ZERO, dto.getTradeDateCash());
        assertEquals(BigDecimal.ZERO, dto.getValueDateCash());
        assertEquals(0, dto.getOutstandingCash().size());
        assertEquals(BigDecimal.valueOf(2), dto.getOutstandingIncome().getAmount());
        assertEquals(1, dto.getOutstandingIncome().getOutstanding().size());

        OutstandingCash income = dto.getOutstandingIncome().getOutstanding().get(0);
        assertEquals(BigDecimal.valueOf(2), income.getAmount());
        assertEquals("cash code", income.getAssetCode());
        assertEquals(AssetType.CASH, income.getAssetType());
        assertEquals(BigDecimal.valueOf(1), income.getMarketPrice());
        assertEquals(BigDecimal.valueOf(2), income.getQuantity());
        assertEquals(new DateTime("2002-1-1"), income.getSettlementDate());
        assertEquals(null, income.getTransactionDate());
        assertEquals(null, income.getTermDepositDetails());
        assertEquals(null, income.getMaturityDate());
    }

    @Test
    public void testCashMovements_whenAPortfolioTDAccrued_thenIncomeMovementsContainsIt() {
        when(portfolioIntegrationService.loadCashMovement(any(AccountKey.class), any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(new ArrayList<CashMovement>());
        CashHoldingImpl cashHolding = new CashHoldingImpl();
        cashHolding.setAsset(assetMap.get("td asset"));

        List<AccountHolding> holdings = new ArrayList<>();
        holdings.add(cashHolding);

        SubAccountValuation subv = mock(CashAccountValuation.class);
        when(subv.getAssetType()).thenReturn(AssetType.CASH);
        when(subv.getHoldings()).thenReturn(holdings);
        when(subv.getAccruedIncome()).thenReturn(cashHolding.getAccruedIncome());

        WrapAccountValuation valuation = mock(WrapAccountValuation.class);
        when(valuation.getSubAccountValuations()).thenReturn(Collections.singletonList(subv));

        when(portfolioIntegrationService.loadWrapAccountValuation(any(AccountKey.class), any(DateTime.class),
                Mockito.anyBoolean(), any(ServiceErrors.class))).thenReturn(valuation);

        List<Income> incomes = new ArrayList<>();
        TermDepositIncome tdIncome = mock(TermDepositIncome.class);

        when(tdIncome.getInterest()).thenReturn(BigDecimal.valueOf(2));
        when(tdIncome.getPaymentDate()).thenReturn(new DateTime("2002-01-01"));
        incomes.add(tdIncome);

        HoldingIncomeDetails holdingIncome = mock(HoldingIncomeDetails.class);
        when(holdingIncome.getAsset()).thenReturn(assetMap.get("td asset"));
        when(holdingIncome.getIncomes()).thenReturn(incomes);
        SubAccountIncomeDetails subI = mock(SubAccountIncomeDetails.class);
        when(subI.getAssetType()).thenReturn(AssetType.TERM_DEPOSIT);
        when(subI.isDirect()).thenReturn(true);
        when(subI.getIncomes()).thenReturn(Collections.singletonList(holdingIncome));

        WrapAccountIncomeDetails wrapIncome = mock(WrapAccountIncomeDetails.class);
        when(wrapIncome.getSubAccountIncomeDetailsList()).thenReturn(Collections.singletonList(subI));

        when(incomeIntegrationService.loadIncomeAccruedDetails(any(AccountKey.class), any(DateTime.class),
                any(ServiceErrors.class))).thenReturn(Collections.singletonList(wrapIncome));

        DatedValuationKey valuationKey = new DatedValuationKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0", new DateTime(),
                false, true);

        CashMovementsDto dto = cashMovements.find(valuationKey, new FailFastErrorsImpl());

        assertNotNull(dto);
        assertEquals(null, dto.getReservedCash());
        assertEquals(BigDecimal.ZERO, dto.getAvailableCash());
        assertEquals(BigDecimal.valueOf(2000), dto.getMinCash());
        assertEquals(BigDecimal.ZERO, dto.getTradeDateCash());
        assertEquals(BigDecimal.ZERO, dto.getValueDateCash());
        assertEquals(0, dto.getOutstandingCash().size());
        assertEquals(BigDecimal.valueOf(2), dto.getOutstandingIncome().getAmount());
        assertEquals(1, dto.getOutstandingIncome().getOutstanding().size());

        OutstandingCash income = dto.getOutstandingIncome().getOutstanding().get(0);
        assertEquals(BigDecimal.valueOf(2), income.getAmount());
        assertEquals("td code", income.getAssetCode());
        assertEquals(AssetType.TERM_DEPOSIT, income.getAssetType());
        assertEquals(BigDecimal.valueOf(1), income.getMarketPrice());
        assertEquals(BigDecimal.valueOf(2), income.getQuantity());
        assertEquals(new DateTime("2002-1-1"), income.getSettlementDate());
        assertEquals(null, income.getTransactionDate());
        assertEquals(null, income.getTermDepositDetails().getBrandName());
        assertEquals(null, income.getAssetName());
        assertEquals(new DateTime("2000-1-1"), income.getMaturityDate());
    }

    @Test
    public void testCashMovements_whenAPortfolioTDAccruedAndMinCashEnabled_thenIncomeMovementsContainsIt() {
        when(portfolioIntegrationService.loadCashMovement(any(AccountKey.class), any(DateTime.class), any(ServiceErrors.class))).thenReturn(
                new ArrayList<CashMovement>());
        when(incomeIntegrationService.loadIncomeAccruedDetails(any(AccountKey.class), any(DateTime.class),
                any(ServiceErrors.class))).thenReturn(new ArrayList<WrapAccountIncomeDetails>());

        CashHoldingImpl cashHolding = new CashHoldingImpl();
        cashHolding.setAccruedIncome(BigDecimal.ZERO);
        cashHolding.setMarketValue(BigDecimal.ZERO);
        cashHolding.setValueDateBalance(BigDecimal.valueOf(4000));
        cashHolding.setAsset(assetMap.get("cash asset"));
        cashHolding.setNextInterestDate(new DateTime("2000-1-1"));
        List<AccountHolding> holdings = new ArrayList<>();
        holdings.add(cashHolding);
        SubAccountValuation subv = mock(CashAccountValuation.class);
        when(subv.getAssetType()).thenReturn(AssetType.CASH);
        when(subv.getHoldings()).thenReturn(holdings);
        when(subv.getAccruedIncome()).thenReturn(cashHolding.getAccruedIncome());

        TermDepositHoldingImpl tdHolding = new TermDepositHoldingImpl();
        tdHolding.setAccruedIncome(BigDecimal.valueOf(5));
        tdHolding.setMarketValue(BigDecimal.valueOf(10));
        tdHolding.setAsset(assetMap.get("td asset"));
        tdHolding.setNextInterestDate(new DateTime("2001-1-1"));

        List<AccountHolding> tdholdings = new ArrayList<>();
        tdholdings.add(tdHolding);
        SubAccountValuation tdsubv = mock(TermDepositAccountValuation.class);
        when(tdsubv.getAssetType()).thenReturn(AssetType.TERM_DEPOSIT);
        when(tdsubv.getHoldings()).thenReturn(tdholdings);

        List<SubAccountValuation> subvs = new ArrayList<>();
        subvs.add(subv);
        subvs.add(tdsubv);

        WrapAccountValuation valuation = mock(WrapAccountValuation.class);

        when(valuation.getSubAccountValuations()).thenReturn(subvs);

        when(portfolioIntegrationService.loadWrapAccountValuation(any(AccountKey.class), any(DateTime.class),
                Mockito.anyBoolean(), any(ServiceErrors.class))).thenReturn(valuation);
        DatedValuationKey valuationKey = new DatedValuationKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0",
                new DateTime(), false, true);


        WrapAccountDetail account = mock(WrapAccountDetail.class);
        when(account.getMinCashAmount()).thenReturn(BigDecimal.valueOf(2000));
        when(account.isHasMinCash()).thenReturn(false);
        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(account);

        CashMovementsDto dto = cashMovements.find(valuationKey, new FailFastErrorsImpl());
        assertNotNull(dto);
        assertEquals(BigDecimal.valueOf(4000), dto.getReservedCash());
        assertEquals(BigDecimal.valueOf(0), dto.getMinCash());
    }

    @Test
    public void testCashMovements_whenAPortfolioHasShareIncome_thenIncomeMovementsContainsIt() {
        when(portfolioIntegrationService.loadCashMovement(any(AccountKey.class), any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(new ArrayList<CashMovement>());
        CashHoldingImpl cashHolding = new CashHoldingImpl();
        cashHolding.setAsset(assetMap.get("cash asset"));

        List<AccountHolding> holdings = new ArrayList<>();
        holdings.add(cashHolding);

        SubAccountValuation subv = mock(CashAccountValuation.class);
        when(subv.getAssetType()).thenReturn(AssetType.CASH);
        when(subv.getHoldings()).thenReturn(holdings);
        when(subv.getAccruedIncome()).thenReturn(cashHolding.getAccruedIncome());

        WrapAccountValuation valuation = mock(WrapAccountValuation.class);
        when(valuation.getSubAccountValuations()).thenReturn(Collections.singletonList(subv));

        when(portfolioIntegrationService.loadWrapAccountValuation(any(AccountKey.class), any(DateTime.class),
                Mockito.anyBoolean(), any(ServiceErrors.class))).thenReturn(valuation);

        List<Income> incomes = new ArrayList<>();
        DividendIncome dividend = mock(DividendIncome.class);

        when(dividend.getQuantity()).thenReturn(BigDecimal.valueOf(1));
        when(dividend.getAmount()).thenReturn(BigDecimal.valueOf(2));
        when(dividend.getExecutionDate()).thenReturn(new DateTime("2001-01-01"));
        when(dividend.getPaymentDate()).thenReturn(new DateTime("2002-01-01"));
        incomes.add(dividend);

        DistributionIncome distribution = mock(DistributionIncome.class);
        when(distribution.getQuantity()).thenReturn(BigDecimal.valueOf(3));
        when(distribution.getAmount()).thenReturn(BigDecimal.valueOf(4));
        when(distribution.getExecutionDate()).thenReturn(new DateTime("2003-01-01"));
        when(distribution.getPaymentDate()).thenReturn(new DateTime("2004-01-01"));
        incomes.add(distribution);

        Income other = mock(Income.class);
        incomes.add(other);

        HoldingIncomeDetails holdingIncome = mock(HoldingIncomeDetails.class);
        when(holdingIncome.getAsset()).thenReturn(assetMap.get("sh asset"));
        when(holdingIncome.getIncomes()).thenReturn(incomes);
        SubAccountIncomeDetails subI = mock(SubAccountIncomeDetails.class);
        when(subI.getAssetType()).thenReturn(AssetType.SHARE);
        when(subI.isDirect()).thenReturn(true);
        when(subI.getIncomes()).thenReturn(Collections.singletonList(holdingIncome));
        WrapAccountIncomeDetails wrapIncome = mock(WrapAccountIncomeDetails.class);
        when(wrapIncome.getSubAccountIncomeDetailsList()).thenReturn(Collections.singletonList(subI));

        when(incomeIntegrationService.loadIncomeAccruedDetails(any(AccountKey.class), any(DateTime.class),
                any(ServiceErrors.class))).thenReturn(Collections.singletonList(wrapIncome));

        DatedValuationKey valuationKey = new DatedValuationKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0", new DateTime(),
                false, true);

        CashMovementsDto dto = cashMovements.find(valuationKey, new FailFastErrorsImpl());

        assertNotNull(dto);
        assertEquals(null, dto.getReservedCash());
        assertEquals(BigDecimal.ZERO, dto.getAvailableCash());
        assertEquals(BigDecimal.valueOf(2000), dto.getMinCash());
        assertEquals(BigDecimal.ZERO, dto.getTradeDateCash());
        assertEquals(BigDecimal.ZERO, dto.getValueDateCash());
        assertEquals(0, dto.getOutstandingCash().size());
        assertEquals(BigDecimal.valueOf(6), dto.getOutstandingIncome().getAmount());
        assertEquals(2, dto.getOutstandingIncome().getOutstanding().size());

        OutstandingCash income = dto.getOutstandingIncome().getOutstanding().get(0);
        assertEquals(BigDecimal.valueOf(2), income.getAmount());
        assertEquals("sh code", income.getAssetCode());
        assertEquals(AssetType.SHARE, income.getAssetType());
        assertEquals(BigDecimal.valueOf(1), income.getMarketPrice());
        assertEquals(BigDecimal.valueOf(1), income.getQuantity());
        assertEquals(new DateTime("2002-1-1"), income.getSettlementDate());
        assertEquals(new DateTime("2001-1-1"), income.getTransactionDate());
        assertEquals(null, income.getTermDepositDetails());

        income = dto.getOutstandingIncome().getOutstanding().get(1);
        assertEquals(BigDecimal.valueOf(4), income.getAmount());
        assertEquals("sh code", income.getAssetCode());
        assertEquals(AssetType.SHARE, income.getAssetType());
        assertEquals(BigDecimal.valueOf(1), income.getMarketPrice());
        assertEquals(BigDecimal.valueOf(3), income.getQuantity());
        assertEquals(new DateTime("2004-1-1"), income.getSettlementDate());
        assertEquals(new DateTime("2003-1-1"), income.getTransactionDate());
        assertEquals(null, income.getTermDepositDetails());
    }

    @Test
    public void testCashMovements_whenAPortfolioHasMPIncome_thenAllocationDoesNotContainIt() {
        when(portfolioIntegrationService.loadCashMovement(any(AccountKey.class), any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(new ArrayList<CashMovement>());
        when(incomeIntegrationService.loadIncomeAccruedDetails(any(AccountKey.class), any(DateTime.class),
                any(ServiceErrors.class))).thenReturn(new ArrayList<WrapAccountIncomeDetails>());

        CashHoldingImpl cashHolding = new CashHoldingImpl();
        cashHolding.setAccruedIncome(BigDecimal.valueOf(0));
        cashHolding.setAsset(assetMap.get("cash asset"));

        List<AccountHolding> holdings = new ArrayList<>();
        holdings.add(cashHolding);

        SubAccountValuation subv = mock(CashAccountValuation.class);
        when(subv.getAssetType()).thenReturn(AssetType.CASH);
        when(subv.getHoldings()).thenReturn(holdings);
        when(subv.getAccruedIncome()).thenReturn(cashHolding.getAccruedIncome());

        WrapAccountValuation valuation = mock(WrapAccountValuation.class);
        when(valuation.getSubAccountValuations()).thenReturn(Collections.singletonList(subv));

        SubAccountIncomeDetails subI = mock(SubAccountIncomeDetails.class);
        when(subI.getAssetType()).thenReturn(AssetType.MANAGED_PORTFOLIO);
        when(subI.isDirect()).thenReturn(false);
        WrapAccountIncomeDetails wrapIncome = mock(WrapAccountIncomeDetails.class);
        when(wrapIncome.getSubAccountIncomeDetailsList()).thenReturn(Collections.singletonList(subI));

        when(portfolioIntegrationService.loadWrapAccountValuation(any(AccountKey.class), any(DateTime.class),
                Mockito.anyBoolean(), any(ServiceErrors.class))).thenReturn(valuation);
        DatedValuationKey valuationKey = new DatedValuationKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0", new DateTime(),
                false, true);

        CashMovementsDto dto = cashMovements.find(valuationKey, new FailFastErrorsImpl());

        assertNotNull(dto);
        assertEquals(null, dto.getReservedCash());
        assertEquals(BigDecimal.ZERO, dto.getAvailableCash());
        assertEquals(BigDecimal.valueOf(2000), dto.getMinCash());
        assertEquals(BigDecimal.ZERO, dto.getTradeDateCash());
        assertEquals(BigDecimal.ZERO, dto.getValueDateCash());
        assertEquals(0, dto.getOutstandingCash().size());
        assertEquals(BigDecimal.ZERO, dto.getOutstandingIncome().getAmount());
        assertEquals(BigDecimal.valueOf(0), dto.getOther());
        assertEquals(BigDecimal.valueOf(0), dto.getOutstandingCashTotal());
        assertEquals(BigDecimal.valueOf(0), dto.getOutstandingTotal());
        assertEquals(BigDecimal.valueOf(0), dto.getTotalCashMovements());
        assertEquals(0, dto.getOutstandingIncome().getOutstanding().size());

    }

    @Test
    public void testCashMovements_whenAPortfolioHasOutstandingCash_thenTheDtoContainsIt() {
        when(portfolioIntegrationService.loadCashMovement(any(AccountKey.class), any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(new ArrayList<CashMovement>());

        List<Income> incomes = new ArrayList<>();
        DividendIncome dividendIncome = mock(DividendIncome.class);

        when(dividendIncome.getAmount()).thenReturn(BigDecimal.valueOf(2));
        when(dividendIncome.getPaymentDate()).thenReturn(new DateTime("2002-01-01"));
        incomes.add(dividendIncome);

        HoldingIncomeDetails holdingIncome = mock(HoldingIncomeDetails.class);
        when(holdingIncome.getAsset()).thenReturn(assetMap.get("sh asset"));
        when(holdingIncome.getIncomes()).thenReturn(incomes);
        SubAccountIncomeDetails subI = mock(SubAccountIncomeDetails.class);
        when(subI.getAssetType()).thenReturn(AssetType.SHARE);
        when(subI.isDirect()).thenReturn(true);
        when(subI.getIncomes()).thenReturn(Collections.singletonList(holdingIncome));

        WrapAccountIncomeDetails wrapIncome = mock(WrapAccountIncomeDetails.class);
        when(wrapIncome.getSubAccountIncomeDetailsList()).thenReturn(Collections.singletonList(subI));

        when(incomeIntegrationService.loadIncomeAccruedDetails(any(AccountKey.class), any(DateTime.class),
                any(ServiceErrors.class))).thenReturn(Collections.singletonList(wrapIncome));

        CashHoldingImpl cashHolding = new CashHoldingImpl();
        cashHolding.setAccruedIncome(BigDecimal.valueOf(0));
        cashHolding.setAsset(assetMap.get("cash asset"));

        List<AccountHolding> holdings = new ArrayList<>();
        holdings.add(cashHolding);

        SubAccountValuation subv = mock(CashAccountValuation.class);
        when(subv.getAssetType()).thenReturn(AssetType.CASH);
        when(subv.getHoldings()).thenReturn(holdings);
        when(subv.getAccruedIncome()).thenReturn(cashHolding.getAccruedIncome());

        WrapAccountValuation valuation = mock(WrapAccountValuation.class);
        when(valuation.getSubAccountValuations()).thenReturn(Collections.singletonList(subv));

        when(portfolioIntegrationService.loadWrapAccountValuation(any(AccountKey.class), any(DateTime.class),
                Mockito.anyBoolean(), any(ServiceErrors.class))).thenReturn(valuation);
        DatedValuationKey valuationKey = new DatedValuationKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0", new DateTime(),
                false, true);

        CashMovement movement = mock(CashMovement.class);
        when(movement.getAssetKey()).thenReturn(AssetKey.valueOf("sh asset"));
        when(movement.getCategory()).thenReturn("unsettled buys");
        when(movement.getMarketValue()).thenReturn(BigDecimal.valueOf(1));
        when(movement.getPrice()).thenReturn(BigDecimal.valueOf(2));
        when(movement.getQuantity()).thenReturn(null);
        when(movement.getSettlementDate()).thenReturn(new DateTime("2001-01-01"));
        when(movement.getTransactionDate()).thenReturn(new DateTime("2002-01-01"));

        when(portfolioIntegrationService.loadCashMovement(any(AccountKey.class), any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(Collections.singletonList(movement));

        CashMovementsDto dto = cashMovements.find(valuationKey, new FailFastErrorsImpl());

        assertNotNull(dto);
        assertEquals(null, dto.getReservedCash());
        assertEquals(BigDecimal.ZERO, dto.getAvailableCash());
        assertEquals(BigDecimal.valueOf(2000), dto.getMinCash());
        assertEquals(BigDecimal.ZERO, dto.getTradeDateCash());
        assertEquals(BigDecimal.ZERO, dto.getValueDateCash());
        assertEquals(BigDecimal.valueOf(-1), dto.getOther());
        assertEquals(BigDecimal.valueOf(1), dto.getOutstandingCashTotal());
        assertEquals(BigDecimal.valueOf(0), dto.getOutstandingTotal());
        assertEquals(BigDecimal.valueOf(2), dto.getTotalCashMovements());

        assertEquals(1, dto.getOutstandingCash().size());
        assertEquals(BigDecimal.valueOf(2), dto.getOutstandingIncome().getAmount());
        assertEquals(1, dto.getOutstandingIncome().getOutstanding().size());

        OutstandingMovementsDto outstandingDto = dto.getOutstandingCash().get(0);
        assertEquals(BigDecimal.valueOf(1), outstandingDto.getAmount());
        assertEquals("unsettled buys", outstandingDto.getCategory());
        assertEquals(1, outstandingDto.getOutstanding().size());

        OutstandingCash oustandingMovement = outstandingDto.getOutstanding().get(0);
        assertEquals(BigDecimal.valueOf(1), oustandingMovement.getAmount());
        assertEquals("sh code", oustandingMovement.getAssetCode());
        assertEquals(AssetType.SHARE, oustandingMovement.getAssetType());
        assertEquals(BigDecimal.valueOf(2), oustandingMovement.getMarketPrice());
        assertEquals(null, oustandingMovement.getQuantity());
        assertEquals(new DateTime("2001-1-1"), oustandingMovement.getSettlementDate());
        assertEquals(new DateTime("2002-1-1"), oustandingMovement.getTransactionDate());
        assertEquals(null, oustandingMovement.getTermDepositDetails());

    }

    @Test
    public void testCashMovements_whenAPortfolioNoCashAccrued_thenIncomeMovementsDoesnot_ContainsIt() {
        when(portfolioIntegrationService.loadCashMovement(any(AccountKey.class), any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(new ArrayList<CashMovement>());
        CashHoldingImpl cashHolding = new CashHoldingImpl();
        cashHolding.setAsset(assetMap.get("cash asset"));

        List<AccountHolding> holdings = new ArrayList<>();
        holdings.add(cashHolding);

        SubAccountValuation subv = mock(CashAccountValuation.class);
        when(subv.getAssetType()).thenReturn(AssetType.CASH);
        when(subv.getHoldings()).thenReturn(holdings);
        when(subv.getAccruedIncome()).thenReturn(cashHolding.getAccruedIncome());

        WrapAccountValuation valuation = mock(WrapAccountValuation.class);
        when(valuation.getSubAccountValuations()).thenReturn(Collections.singletonList(subv));

        when(portfolioIntegrationService.loadWrapAccountValuation(any(AccountKey.class), any(DateTime.class),
                Mockito.anyBoolean(), any(ServiceErrors.class))).thenReturn(valuation);

        List<Income> incomes = new ArrayList<>();
        CashIncome cashIncome = mock(CashIncome.class);

        when(cashIncome.getAmount()).thenReturn(BigDecimal.valueOf(0));
        when(cashIncome.getPaymentDate()).thenReturn(new DateTime("2002-01-01"));
        incomes.add(cashIncome);

        HoldingIncomeDetails holdingIncome = mock(HoldingIncomeDetails.class);
        when(holdingIncome.getAsset()).thenReturn(assetMap.get("cash asset"));
        when(holdingIncome.getIncomes()).thenReturn(incomes);
        SubAccountIncomeDetails subI = mock(SubAccountIncomeDetails.class);
        when(subI.getAssetType()).thenReturn(AssetType.CASH);
        when(subI.getIncomes()).thenReturn(Collections.singletonList(holdingIncome));

        WrapAccountIncomeDetails wrapIncome = mock(WrapAccountIncomeDetails.class);
        when(wrapIncome.getSubAccountIncomeDetailsList()).thenReturn(Collections.singletonList(subI));

        when(incomeIntegrationService.loadIncomeAccruedDetails(any(AccountKey.class), any(DateTime.class),
                any(ServiceErrors.class))).thenReturn(Collections.singletonList(wrapIncome));

        DatedValuationKey valuationKey = new DatedValuationKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0",
                new DateTime(), false, true);

        CashMovementsDto dto = cashMovements.find(valuationKey, new FailFastErrorsImpl());

        assertNotNull(dto);
        assertEquals(0, dto.getOutstandingIncome().getOutstanding().size());
    }

    @Test
    public void testCashMovements_whenAPortfolioNoTdAccrued_thenIncomeMovementsDoesnot_ContainsIt() {
        when(portfolioIntegrationService.loadCashMovement(any(AccountKey.class), any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(new ArrayList<CashMovement>());
        CashHoldingImpl cashHolding = new CashHoldingImpl();
        cashHolding.setAsset(assetMap.get("cash asset"));

        List<AccountHolding> holdings = new ArrayList<>();
        holdings.add(cashHolding);

        SubAccountValuation subv = mock(CashAccountValuation.class);
        when(subv.getAssetType()).thenReturn(AssetType.CASH);
        when(subv.getHoldings()).thenReturn(holdings);
        when(subv.getAccruedIncome()).thenReturn(cashHolding.getAccruedIncome());

        WrapAccountValuation valuation = mock(WrapAccountValuation.class);
        when(valuation.getSubAccountValuations()).thenReturn(Collections.singletonList(subv));

        when(
                portfolioIntegrationService.loadWrapAccountValuation(any(AccountKey.class), any(DateTime.class),
                        Mockito.anyBoolean(), any(ServiceErrors.class))).thenReturn(valuation);

        List<Income> incomes = new ArrayList<>();
        TermDepositIncome tdIncome = mock(TermDepositIncome.class);

        when(tdIncome.getInterest()).thenReturn(BigDecimal.valueOf(0));
        when(tdIncome.getPaymentDate()).thenReturn(new DateTime("2002-01-01"));
        incomes.add(tdIncome);

        HoldingIncomeDetails holdingIncome = mock(HoldingIncomeDetails.class);
        when(holdingIncome.getAsset()).thenReturn(assetMap.get("td asset"));
        when(holdingIncome.getIncomes()).thenReturn(incomes);
        SubAccountIncomeDetails subI = mock(SubAccountIncomeDetails.class);
        when(subI.getAssetType()).thenReturn(AssetType.TERM_DEPOSIT);
        when(subI.getIncomes()).thenReturn(Collections.singletonList(holdingIncome));

        WrapAccountIncomeDetails wrapIncome = mock(WrapAccountIncomeDetails.class);
        when(wrapIncome.getSubAccountIncomeDetailsList()).thenReturn(Collections.singletonList(subI));

        when(
                incomeIntegrationService.loadIncomeAccruedDetails(any(AccountKey.class), any(DateTime.class),
                        any(ServiceErrors.class))).thenReturn(Collections.singletonList(wrapIncome));

        DatedValuationKey valuationKey = new DatedValuationKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0",
                new DateTime(), false, true);

        CashMovementsDto dto = cashMovements.find(valuationKey, new FailFastErrorsImpl());

        assertNotNull(dto);
        assertEquals(0, dto.getOutstandingIncome().getOutstanding().size());
    }

    @Test
    public void testCashMovements_whenAPortfolioHasOutstandingCash_thenIncomeMovements_ContainsIt() {
        DatedValuationKey valuationKey = new DatedValuationKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0",
                new DateTime(), false, true);

        CashMovement movement1 = mock(CashMovement.class);
        when(movement1.getAssetKey()).thenReturn(AssetKey.valueOf("td asset"));
        when(movement1.getCategory()).thenReturn("unsettled buys");
        when(movement1.getMarketValue()).thenReturn(BigDecimal.valueOf(1));
        when(movement1.getPrice()).thenReturn(BigDecimal.valueOf(2));
        when(movement1.getQuantity()).thenReturn(null);
        when(movement1.getSettlementDate()).thenReturn(new DateTime("2001-01-01"));
        when(movement1.getTransactionDate()).thenReturn(new DateTime("2002-01-01"));

        CashMovement movement2 = mock(CashMovement.class);
        when(movement2.getAssetKey()).thenReturn(AssetKey.valueOf("td asset"));
        when(movement2.getCategory()).thenReturn("unsettled buys");
        when(movement2.getMarketValue()).thenReturn(BigDecimal.valueOf(1));
        when(movement2.getPrice()).thenReturn(BigDecimal.valueOf(2));
        when(movement2.getQuantity()).thenReturn(null);
        when(movement2.getSettlementDate()).thenReturn(new DateTime("2001-01-01"));
        when(movement2.getTransactionDate()).thenReturn(new DateTime("2002-01-08"));

        CashMovement movement3 = mock(CashMovement.class);
        when(movement3.getAssetKey()).thenReturn(AssetKey.valueOf("td asset"));
        when(movement3.getCategory()).thenReturn("unsettled buys");
        when(movement3.getMarketValue()).thenReturn(BigDecimal.valueOf(1));
        when(movement3.getPrice()).thenReturn(BigDecimal.valueOf(2));
        when(movement3.getQuantity()).thenReturn(null);
        when(movement3.getSettlementDate()).thenReturn(new DateTime("2001-01-01"));
        when(movement3.getTransactionDate()).thenReturn(new DateTime("2002-01-01"));

        List<CashMovement> movementsList = new ArrayList<CashMovement>();
        movementsList.add(movement1);
        movementsList.add(movement2);
        movementsList.add(movement3);

        when(portfolioIntegrationService.loadCashMovement(any(AccountKey.class), any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(movementsList);

        CashMovementsDto dto = cashMovements.find(valuationKey, new FailFastErrorsImpl());

        assertNotNull(dto);
        assertEquals(1, dto.getOutstandingCash().size());
    }

}
