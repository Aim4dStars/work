package com.bt.nextgen.api.portfolio.v3.service.allocation;

import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.AggregateAllocationByExposureDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.KeyedAllocByExposureDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
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
public class AllocationByExposureDtoServiceTest {
    @InjectMocks
    private AllocationByExposureDtoServiceImpl allocationDtoServiceImpl;

    @Mock
    private PortfolioIntegrationService portfolioIntegrationService;

    @Mock
    private ExposureAggregator exposureAggregator;

    @Mock
    private CmsService cmsService;

    private DatedValuationKey valuationKey = new DatedValuationKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0",
            new DateTime(),
            false);

    @Before
    public void setup() throws Exception {
    }

    @Test
    public void testGetAllocationFromPortfolio_whenAPortfolioHasNoAcccounts_thenAllocationIsEmpty() {
        WrapAccountValuationImpl valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));
        List<SubAccountValuation> subaccounts = new ArrayList<>();
        valuation.setSubAccountValuations(subaccounts);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);

        KeyedAllocByExposureDto allocationDto = allocationDtoServiceImpl.find(valuationKey, new ServiceErrorsImpl());
        Assert.assertNull(allocationDto.getName());
        Assert.assertTrue(allocationDto.getAllocations().isEmpty());
    }

    @Test
    public void testGetAllocationFromPortfolio_whenAPortfolioHasAcccounts_thenAccountsArePassedToAggregator() {
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
        AssetImpl cashAsset = new AssetImpl();
        cashAsset.setAssetType(AssetType.CASH);
        cashHolding.setAsset(cashAsset);
        List<AccountHolding> cashList = new ArrayList<>();
        cashList.add(cashHolding);
        cashAccount.addHoldings(cashList);

        subAccounts.add(cashAccount);
        valuation.setSubAccountValuations(subAccounts);

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(Boolean.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(valuation);

        Mockito.when(
                exposureAggregator.aggregateAllocations(Mockito.any(AccountKey.class),
                        Mockito.anyListOf(SubAccountValuation.class), Mockito.anyMap(), Mockito.any(BigDecimal.class),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<AggregateAllocationByExposureDto>() {
            @Override
            public AggregateAllocationByExposureDto answer(InvocationOnMock invocation) throws Throwable {
                List<SubAccountValuation> val = (List<SubAccountValuation>) invocation.getArguments()[1];
                Assert.assertEquals(1, val.size());
                Assert.assertEquals(AssetType.CASH, val.get(0).getAssetType());
                Assert.assertEquals(BigDecimal.valueOf(5), invocation.getArguments()[3]);
                return new AggregateAllocationByExposureDto("Name", null);
            }
        });
        KeyedAllocByExposureDto allocationDto = allocationDtoServiceImpl.find(valuationKey, new ServiceErrorsImpl());
        Assert.assertFalse(allocationDto.getHasExternal());
    }

}
