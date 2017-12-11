package com.bt.nextgen.api.account.v2.service.valuation;

import com.bt.nextgen.api.account.v2.model.CashManagementValuationDto;
import com.bt.nextgen.api.account.v2.model.DatedValuationKey;
import com.bt.nextgen.api.account.v2.model.InvestmentValuationDto;
import com.bt.nextgen.api.account.v2.model.ValuationDto;
import com.bt.nextgen.api.account.v2.model.ValuationSummaryDto;
import com.bt.nextgen.clients.domain.AccountType;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.WrapAccountValuationImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

        List<InvestmentValuationDto> cashInvestmentsList = new ArrayList<>();
        cashInvestmentsList.add(cashDto);

        categoryMap = new LinkedHashMap<>();
        categoryMap.put(AssetType.CASH, cashInvestmentsList);
    }

    @Test
    public void testGetValuationsFromPortfolio_whenAPortfolioHasSubAccounts_thenValuationHasCategories() {

        // Mock account type
        WrapAccountImpl account = new WrapAccountImpl();
        account.setAccountStructureType(AccountStructureType.fromAvaloqStaticCode("S"));

        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers(Mockito.any(AccountKey.class),
                Mockito.any(ServiceErrors.class)))
                .thenReturn(account);

        // Mock account valuation
        WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));
        valuation.setSubAccountValuations(subAccounts);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.anyBoolean(), Mockito.any(ServiceErrors.class))).thenReturn(valuation);

        // Mock valuation aggregator
        Mockito.when(valuationAggregator.getValuationsByCategory(Mockito.any(WrapAccountValuation.class),
                Mockito.any(ServiceErrors.class))).thenReturn(categoryMap);

        ValuationDto valuationDto = valuationDtoServiceImpl.find(valuationKey, new ServiceErrorsImpl());

        Assert.assertNotNull(valuationDto);
        Assert.assertNotNull(valuationDto.getCategories());

        Assert.assertEquals(1, valuationDto.getCategories().size());

        ValuationSummaryDto category = valuationDto.getCategories().get(0);

        Assert.assertNotNull(category.getInvestments());
        Assert.assertEquals(1, category.getInvestments().size());
        Assert.assertEquals(cashCategoryName, category.getCategoryName());
        Assert.assertEquals(BigDecimal.valueOf(5), category.getBalance());
    }

    @Test
    public void testGetValuationsFromPortfolio_whenAPortfolioHasNoSubAccounts_thenValuationHasNoCategories() {

        WrapAccountImpl account = new WrapAccountImpl();
        account.setAccountStructureType(AccountStructureType.fromAvaloqStaticCode("S"));

        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers(Mockito.any(AccountKey.class),
                Mockito.any(ServiceErrors.class)))
                .thenReturn(null);

        WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));
        List<SubAccountValuation> subaccounts = new ArrayList<>();
        valuation.setSubAccountValuations(subaccounts);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);

        Mockito.when(valuationAggregator.getValuationsByCategory(valuation, new ServiceErrorsImpl())).thenReturn(categoryMap);

        ValuationDto valuationDto = valuationDtoServiceImpl.find(valuationKey, new ServiceErrorsImpl());

        Assert.assertNotNull(valuationDto);
        Assert.assertNotNull(valuationDto.getCategories());
        Assert.assertEquals(0, valuationDto.getCategories().size());
    }

    @Test
    public void testGetValuationFromPortfolio_whenAnAccountHasAccountTypeNull_thenValuationDtoReturnsAccountTypeNull() {
        WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));
        List<SubAccountValuation> subaccounts = new ArrayList<>();
        valuation.setSubAccountValuations(subaccounts);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);

        Mockito.when(valuationAggregator.getValuationsByCategory(valuation, new ServiceErrorsImpl())).thenReturn(categoryMap);

        ValuationDto valuationDto = valuationDtoServiceImpl.find(valuationKey, new ServiceErrorsImpl());

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
                Mockito.any(ServiceErrors.class)))
                .thenReturn(account);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);

        Mockito.when(valuationAggregator.getValuationsByCategory(valuation, new ServiceErrorsImpl())).thenReturn(categoryMap);

        ValuationDto valuationDto = valuationDtoServiceImpl.find(valuationKey, new ServiceErrorsImpl());

    }
}
