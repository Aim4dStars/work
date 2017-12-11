package com.bt.nextgen.api.portfolio.v3.service.allocation;

import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AggregatedAllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.KeyedAllocBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.portfolio.PortfolioIntegrationServiceFactory;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.WrapAccountValuationImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
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
public class AllocationBySectorDtoServiceTest {
    @InjectMocks
    private AllocationBySectorDtoServiceImpl allocationDtoServiceImpl;

    @Mock
    private PortfolioIntegrationServiceFactory portfolioIntegrationServiceFactory;

    @Mock
    private PortfolioIntegrationService portfolioIntegrationService;

    @Mock
    private SectorAggregator sectorAggregator;

    @Mock
    private CmsService cmsService;

    @Before
    public void setup() throws Exception {
    }

    @Test
    public void testGetAllocationFromPortfolio_whenAPortfolioHasNoAcccounts_thenAllocationIsEmpty() {
        DatedValuationKey valuationKey = new DatedValuationKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0",
                new DateTime(), false, true);
        WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));
        List<SubAccountValuation> subaccounts = new ArrayList<>();
        valuation.setSubAccountValuations(subaccounts);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);
        Mockito.when(portfolioIntegrationServiceFactory.getInstance(Mockito.any(String.class)))
                .thenReturn(portfolioIntegrationService);
        KeyedAllocBySectorDto allocationDto = allocationDtoServiceImpl.find(valuationKey, new ServiceErrorsImpl());
        Assert.assertNull(allocationDto.getName());
        Assert.assertTrue(allocationDto.getAllocations().isEmpty());
    }

    @Test
    public void testGetAllocationFromPortfolio_whenAPortfolioHasAcccounts_thenAccountsArePassedToAggregator() {
        DatedValuationKey valuationKey = new DatedValuationKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0",
                new DateTime(), false, false);

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
                Mockito.any(DateTime.class), Mockito.any(Boolean.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);
        Mockito.when(portfolioIntegrationServiceFactory.getInstance(Mockito.any(String.class)))
                .thenReturn(portfolioIntegrationService);
        Mockito.when(
                sectorAggregator.aggregateAllocations(Mockito.any(AccountKey.class),
                        Mockito.anyListOf(SubAccountValuation.class), Mockito.any(BigDecimal.class)))
                .thenAnswer(new Answer<AggregatedAllocationBySectorDto>() {
                    @Override
                    public AggregatedAllocationBySectorDto answer(InvocationOnMock invocation) throws Throwable {
                        List<SubAccountValuation> val = (List<SubAccountValuation>) invocation.getArguments()[1];
                        Assert.assertEquals(1, val.size());
                        Assert.assertEquals(AssetType.CASH, val.get(0).getAssetType());
                        Assert.assertEquals(BigDecimal.valueOf(5), invocation.getArguments()[2]);
                        return new AggregatedAllocationBySectorDto("Name", null);
                    }
                });
        KeyedAllocBySectorDto allocationDto = allocationDtoServiceImpl.find(valuationKey, new ServiceErrorsImpl());
        Assert.assertFalse(allocationDto.getHasExternal());
    }

}
