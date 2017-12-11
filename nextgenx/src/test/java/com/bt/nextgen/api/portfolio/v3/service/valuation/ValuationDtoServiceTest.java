package com.bt.nextgen.api.portfolio.v3.service.valuation;

import com.bt.nextgen.api.portfolio.v3.model.valuation.CashManagementValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ValuationSummaryDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.portfolio.PortfolioIntegrationServiceFactory;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.WrapAccountValuationImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.portfolio.CachePortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValuationDtoServiceTest {

    @InjectMocks
    public ValuationDtoServiceImpl valuationDtoServiceImpl;

    @Mock
    public ValuationAggregator valuationAggregator;

    @Mock
    public AccountIntegrationService accountIntegrationService;

    @Mock
    public PortfolioIntegrationService portfolioIntegrationService;

    @Mock
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    @Mock
    private PortfolioIntegrationServiceFactory portfolioIntegrationServiceFactory;

    @Mock
    private CachePortfolioIntegrationService cachedPortfolioIntegrationService;

    private DatedValuationKey valuationKey;
    private AccountKey accountKey;
    private List<SubAccountValuation> subAccounts;
    private String cashCategoryName;
    private Map<AssetType, List<InvestmentValuationDto>> categoryMap;

    @Before
    public void setup() {
        valuationKey = new DatedValuationKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0", new DateTime(), false);
        accountKey = AccountKey.valueOf("plaintext");

        CashAccountValuationImpl cashAccount = new CashAccountValuationImpl();
        CashHoldingImpl cashHolding = new CashHoldingImpl();
        cashHolding.setAccountName("accountName");
        cashHolding.setAvailableBalance(BigDecimal.valueOf(1));
        cashHolding.setValueDateBalance(BigDecimal.valueOf(4));
        cashHolding.setMarketValue(BigDecimal.valueOf(2));
        cashHolding.setMarketValue(BigDecimal.valueOf(2));
        cashHolding.setAccruedIncome(BigDecimal.valueOf(3));
        cashHolding.setYield(BigDecimal.valueOf(4));
        List<AccountHolding> cashList = new ArrayList<>();
        cashList.add(cashHolding);
        cashAccount.addHoldings(cashList);

        subAccounts = new ArrayList<>();
        subAccounts.add(cashAccount);

        cashCategoryName = "Cash";
        CashManagementValuationDto cashDto = new CashManagementValuationDto(accountKey.toString(), cashHolding,
                BigDecimal.valueOf(1), BigDecimal.valueOf(1), false);
        CashManagementValuationDto externalCashDto = new CashManagementValuationDto(accountKey.toString(), cashHolding,
                BigDecimal.valueOf(1), BigDecimal.valueOf(1), true);

        List<InvestmentValuationDto> cashInvestmentsList = new ArrayList<>();
        cashInvestmentsList.add(cashDto);
        cashInvestmentsList.add(externalCashDto);

        categoryMap = new LinkedHashMap<>();
        categoryMap.put(AssetType.CASH, cashInvestmentsList);
        categoryMap.put(AssetType.MANAGED_FUND, new ArrayList<InvestmentValuationDto>());

        when(accountIntegrationServiceFactory.getInstance(anyString())).thenReturn(accountIntegrationService);
        when(portfolioIntegrationServiceFactory.getInstance(anyString())).thenReturn(portfolioIntegrationService);
    }

    @Test
    public void testGetValuationsFromPortfolio_whenAPortfolioHasSubAccounts_thenValuationHasCategories() {
        // Mock account type
        WrapAccountImpl account = new WrapAccountImpl();
        account.setAccountStructureType(AccountStructureType.fromAvaloqStaticCode("S"));

        when(accountIntegrationService.loadWrapAccountWithoutContainers(Mockito.any(AccountKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(account);

        // Mock account valuation
        WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));
        valuation.setSubAccountValuations(subAccounts);

        when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.anyBoolean(), Mockito.any(ServiceErrors.class))).thenReturn(valuation);

        // Mock valuation aggregator
        when(valuationAggregator.getValuationsByCategory(Mockito.any(WrapAccountValuation.class),
                Mockito.any(ServiceErrors.class))).thenReturn(categoryMap);

        ValuationDto valuationDto = valuationDtoServiceImpl.find(valuationKey, new ServiceErrorsImpl());

        Assert.assertNotNull(valuationDto);
        Assert.assertNotNull(valuationDto.getCategories());

        Assert.assertEquals(1, valuationDto.getCategories().size());

        ValuationSummaryDto category = valuationDto.getCategories().get(0);

        Assert.assertNotNull(category.getInvestments());
        Assert.assertEquals(2, category.getInvestments().size());
        Assert.assertEquals(cashCategoryName, category.getCategoryName());
        Assert.assertEquals(BigDecimal.valueOf(10), category.getBalance());
        Assert.assertEquals(BigDecimal.valueOf(5), category.getInternalBalance());
        Assert.assertEquals(BigDecimal.valueOf(2), category.getExternalBalance());
        Assert.assertEquals(BigDecimal.valueOf(-2), category.getOutstandingCash());
        Assert.assertEquals(0, category.getOutstandingCashPercent().compareTo(BigDecimal.valueOf(-0.4)));
        Assert.assertEquals(false, category.getAllAssetsExternal());
    }



    @Test
    public void testGetValuationsFromPortfolio_whenAPortfolioHasNoSubAccounts_thenValuationHasNoCategories() {

        WrapAccountImpl account = new WrapAccountImpl();
        account.setAccountStructureType(AccountStructureType.fromAvaloqStaticCode("S"));

        when(accountIntegrationService.loadWrapAccountWithoutContainers(Mockito.any(AccountKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(null);

        WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));
        List<SubAccountValuation> subaccounts = new ArrayList<>();
        valuation.setSubAccountValuations(subaccounts);

        when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);

        when(valuationAggregator.getValuationsByCategory(valuation, new ServiceErrorsImpl())).thenReturn(categoryMap);

        ValuationDto valuationDto = valuationDtoServiceImpl.find(valuationKey, new ServiceErrorsImpl());

        Assert.assertNotNull(valuationDto);
        Assert.assertNotNull(valuationDto.getCategories());
        Assert.assertEquals(0, valuationDto.getCategories().size());
    }

    @Test
    public void testfindFromCache() {
        // Mock account type
        WrapAccountImpl account = new WrapAccountImpl();
        account.setAccountStructureType(AccountStructureType.fromAvaloqStaticCode("S"));

        when(accountIntegrationService.loadWrapAccountWithoutContainers(Mockito.any(AccountKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(account);

        // Mock account valuation
        WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));
        valuation.setSubAccountValuations(subAccounts);

        when(cachedPortfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);

        // Mock valuation aggregator
        when(valuationAggregator.getValuationsByCategory(Mockito.any(WrapAccountValuation.class),
                Mockito.any(ServiceErrors.class))).thenReturn(categoryMap);

        ValuationDto valuationDto = valuationDtoServiceImpl.findFromCache(valuationKey, true, new ServiceErrorsImpl());

        Assert.assertNotNull(valuationDto);
        Assert.assertNotNull(valuationDto.getCategories());

        Assert.assertEquals(1, valuationDto.getCategories().size());

        ValuationSummaryDto category = valuationDto.getCategories().get(0);

        Assert.assertNotNull(category.getInvestments());
        Assert.assertEquals(2, category.getInvestments().size());
        Assert.assertEquals(cashCategoryName, category.getCategoryName());
        Assert.assertEquals(BigDecimal.valueOf(10), category.getBalance());
        Assert.assertEquals(BigDecimal.valueOf(5), category.getInternalBalance());
        Assert.assertEquals(BigDecimal.valueOf(2), category.getExternalBalance());
        Assert.assertEquals(BigDecimal.valueOf(-2), category.getOutstandingCash());
        Assert.assertEquals(0, category.getOutstandingCashPercent().compareTo(BigDecimal.valueOf(-0.4)));
        Assert.assertEquals(false, category.getAllAssetsExternal());
    }

    @Test
    public void testFind_withNullResponse() {
        DatedValuationKey datedValuationKey = new DatedValuationKey("577201EB150388ED0B4156C9973428663AE53E0147433630", new DateTime(), Boolean.TRUE);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(Boolean.class), Mockito.any(ServiceErrors.class))).thenReturn(null);

        ValuationDto valuationDto = valuationDtoServiceImpl.find(datedValuationKey, new ServiceErrorsImpl());
        Assert.assertNotNull(valuationDto);
        Assert.assertEquals(BigDecimal.ZERO, valuationDto.getIncome());
    }
}
