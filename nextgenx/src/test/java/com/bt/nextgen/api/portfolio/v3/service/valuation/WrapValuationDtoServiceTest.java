package com.bt.nextgen.api.portfolio.v3.service.valuation;

import com.bt.nextgen.api.portfolio.v3.model.valuation.CashManagementValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ValuationDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.WrapAccountValuationImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.bt.nextgen.service.wrap.integration.PortfolioValuationIntegrationServiceImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.joda.time.DateTime;
import org.junit.Assert;
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
public class WrapValuationDtoServiceTest {

    @Mock
    public PortfolioIntegrationService portfolioIntegrationService;
    @Mock
    public ValuationAggregator valuationAggregator;
    @InjectMocks
    private WrapValuationDtoServiceImpl wrapValuationDtoService;
    @Mock
    private PortfolioValuationIntegrationServiceImpl portfolioValuationIntegrationService;

    @Test
    public void testFind() {
        DatedValuationKey datedValuationKey = new DatedValuationKey("577201EB150388ED0B4156C9973428663AE53E0147433630", new DateTime(), Boolean.TRUE);

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

        CashManagementValuationDto cashDto = new CashManagementValuationDto(AccountKey.valueOf("plaintext").toString(), cashHolding,
                BigDecimal.valueOf(1), BigDecimal.valueOf(1), false);
        CashManagementValuationDto externalCashDto = new CashManagementValuationDto(AccountKey.valueOf("plaintext").toString(), cashHolding,
                BigDecimal.valueOf(1), BigDecimal.valueOf(1), true);

        List<InvestmentValuationDto> cashInvestmentsList = new ArrayList<>();
        cashInvestmentsList.add(cashDto);
        cashInvestmentsList.add(externalCashDto);


        Map<AssetType, List<InvestmentValuationDto>> categoryMap = new LinkedHashMap<>();
        categoryMap.put(AssetType.CASH, cashInvestmentsList);
        categoryMap.put(AssetType.MANAGED_FUND, new ArrayList<InvestmentValuationDto>());

        WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf(datedValuationKey.getAccountId()));
        List<SubAccountValuation> subaccounts = new ArrayList<>();
        valuation.setSubAccountValuations(subaccounts);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(Boolean.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);
        Mockito.when(valuationAggregator.getValuationsByCategory(Mockito.any(WrapAccountValuation.class), Mockito.any(ServiceErrors.class))).thenReturn(categoryMap);
        Mockito.when(
                portfolioValuationIntegrationService.loadWrapAccountValuation(
                        Mockito.any(AccountKey.class), Mockito.any(DateTime.class), Mockito.any(Boolean.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(new WrapAccountValuationImpl());

        ValuationDto valuationDto = wrapValuationDtoService.find(datedValuationKey, new ServiceErrorsImpl());

        Assert.assertNotNull(valuationDto);
        Assert.assertNotNull(valuationDto.getCategories());
        Assert.assertEquals(1, valuationDto.getCategories().size());
    }

    @Test
    public void testFind_withNullResponse() {
        DatedValuationKey datedValuationKey = new DatedValuationKey("577201EB150388ED0B4156C9973428663AE53E0147433630", new DateTime(), Boolean.TRUE);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(Boolean.class), Mockito.any(ServiceErrors.class))).thenReturn(null);

        ValuationDto valuationDto = wrapValuationDtoService.find(datedValuationKey, new ServiceErrorsImpl());
        Assert.assertNotNull(valuationDto);
        Assert.assertEquals(BigDecimal.ZERO, valuationDto.getIncome());
    }
}
