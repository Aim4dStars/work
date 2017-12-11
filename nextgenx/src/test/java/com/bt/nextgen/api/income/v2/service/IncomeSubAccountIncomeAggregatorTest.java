package com.bt.nextgen.api.income.v2.service;

import com.bt.nextgen.api.income.v2.model.AbstractIncomeDto;
import com.bt.nextgen.api.income.v2.model.CashIncomeDto;
import com.bt.nextgen.api.income.v2.model.DistributionIncomeDto;
import com.bt.nextgen.api.income.v2.model.DividendIncomeDto;
import com.bt.nextgen.api.income.v2.model.FeeRebateIncomeDto;
import com.bt.nextgen.api.income.v2.model.InterestIncomeDto;
import com.bt.nextgen.api.income.v2.model.TermDepositIncomeDto;
import com.bt.nextgen.api.portfolio.v3.service.TermDepositPresentationService;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.avaloq.income.CashIncomeImpl;
import com.bt.nextgen.service.avaloq.income.DistributionIncomeImpl;
import com.bt.nextgen.service.avaloq.income.DividendIncomeImpl;
import com.bt.nextgen.service.avaloq.income.FeeRebateIncomeImpl;
import com.bt.nextgen.service.avaloq.income.HoldingIncomeDetailsImpl;
import com.bt.nextgen.service.avaloq.income.InterestIncomeImpl;
import com.bt.nextgen.service.avaloq.income.SubAccountIncomeDetailsImpl;
import com.bt.nextgen.service.avaloq.income.TermDepositIncomeImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.income.HoldingIncomeDetails;
import com.bt.nextgen.service.integration.income.Income;
import com.bt.nextgen.service.integration.income.IncomeType;
import com.bt.nextgen.service.integration.income.SubAccountIncomeDetails;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
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
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IncomeSubAccountIncomeAggregatorTest {
    @InjectMocks
    private IncomeSubAccountIncomeAggregator subAccountAggregator;

    @Mock
    private IncomeDtoFromIncomeValuationBuilder incomeDtoIncomeBuilder;

    @Mock
    private TermDepositPresentationService termDepositPresentationService;

    private Asset cashAsset;
    private Asset tdAsset;
    private Asset mfAsset;

    private DistributionIncomeDto dist = mock(DistributionIncomeDto.class);
    private TermDepositIncomeDto td = mock(TermDepositIncomeDto.class);
    private DividendIncomeDto div = mock(DividendIncomeDto.class);
    private InterestIncomeDto intr = mock(InterestIncomeDto.class);
    private FeeRebateIncomeDto rebate = mock(FeeRebateIncomeDto.class);
    private CashIncomeDto cash = mock(CashIncomeDto.class);


    @Before
    public void setup() {
        cashAsset = Mockito.mock(Asset.class);
        Mockito.when(cashAsset.getAssetType()).thenReturn(AssetType.CASH);
        tdAsset = Mockito.mock(Asset.class);
        Mockito.when(tdAsset.getAssetType()).thenReturn(AssetType.TERM_DEPOSIT);
        mfAsset = Mockito.mock(Asset.class);
        Mockito.when(mfAsset.getAssetType()).thenReturn(AssetType.MANAGED_FUND);

        when(incomeDtoIncomeBuilder.toDistributionIncomeDto(any(HoldingIncomeDetails.class), any(Income.class)))
                .thenReturn(dist);
        when(incomeDtoIncomeBuilder.toDividendIncomeDto(any(HoldingIncomeDetails.class), any(Income.class))).thenReturn(div);
        when(incomeDtoIncomeBuilder.toFeeRebateIncomeDto(any(HoldingIncomeDetails.class), any(Income.class))).thenReturn(rebate);
        when(incomeDtoIncomeBuilder.toInterestIncomeDto(any(HoldingIncomeDetails.class), any(Income.class))).thenReturn(intr);
        when(incomeDtoIncomeBuilder.toTermDepositIncomeDto(any(HoldingIncomeDetails.class), any(Income.class),
                any(TermDepositPresentation.class), anyBoolean())).thenReturn(td);
        when(incomeDtoIncomeBuilder.toCashIncomeDto(any(HoldingIncomeDetails.class), any(Income.class))).thenReturn(cash);
    }

    @Test
    public void testSubAccountAggregator_whenNoSubAccounts_thenEmptyMap() {
        AccountKey accountKey = AccountKey.valueOf("accountId");
        List<SubAccountIncomeDetails> incomes = new ArrayList<>();
        Map<AssetType, Map<IncomeType, List<AbstractIncomeDto>>> result = subAccountAggregator
                .buildInvestmentMapFromSubAccount(incomes, accountKey, new FailFastErrorsImpl());
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testSubAccountAggregator_whenIncomeListHasDistribution_thenDistributionDtoIsproduced() {
        // Distribution income
        DistributionIncomeImpl distIncome = new DistributionIncomeImpl();
        distIncome.setAmount(BigDecimal.valueOf(100d));
        distIncome.setQuantity(BigDecimal.valueOf(20));
        distIncome.setExecutionDate(new DateTime().minusMonths(2));
        distIncome.setPaymentDate(new DateTime().minusMonths(1));
        List<Income> distIncomeList = new ArrayList<>();
        distIncomeList.add(distIncome);

        HoldingIncomeDetailsImpl distHoldingIncome = new HoldingIncomeDetailsImpl();
        distHoldingIncome.addIncomes(distIncomeList);
        AssetImpl asset = new AssetImpl();
        asset.setAssetName("Test asset");
        asset.setAssetCode("0001");
        asset.setAssetType(AssetType.MANAGED_FUND);
        distHoldingIncome.setAsset(asset);

        List<HoldingIncomeDetails> holdingIncomes = new ArrayList<>();
        holdingIncomes.add(distHoldingIncome);

        SubAccountIncomeDetailsImpl mfDetails = new SubAccountIncomeDetailsImpl();
        mfDetails.setAssetType(AssetType.MANAGED_FUND);
        mfDetails.addIncomes(holdingIncomes);
        List<SubAccountIncomeDetails> accountIncomes = new ArrayList<>();
        accountIncomes.add(mfDetails);
        
        Map<AssetType, Map<IncomeType, List<AbstractIncomeDto>>> result = subAccountAggregator
                .buildInvestmentMapFromSubAccount(accountIncomes, AccountKey.valueOf("accountId"), new FailFastErrorsImpl());

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(AssetType.MANAGED_FUND, result.keySet().iterator().next());
        Assert.assertEquals(1, result.get(AssetType.MANAGED_FUND).size());
        AbstractIncomeDto incomeDto = result.get(AssetType.MANAGED_FUND).get(IncomeType.DISTRIBUTION).get(0);
        Assert.assertEquals(dist, incomeDto);
    }


    @Test
    public void testSubAccountAggregator_whenIncomeListHasFeeRebate_thenFeeRebateDtoIsproduced() {
        FeeRebateIncomeImpl rebateIncome = new FeeRebateIncomeImpl();
        rebateIncome.setAmount(BigDecimal.valueOf(100d));
        rebateIncome.setPaymentDate(new DateTime().minusMonths(1));
        List<Income> rebateIncomeList = new ArrayList<>();
        rebateIncomeList.add(rebateIncome);

        HoldingIncomeDetailsImpl rebateHoldingIncome = new HoldingIncomeDetailsImpl();
        rebateHoldingIncome.addIncomes(rebateIncomeList);
        AssetImpl asset = new AssetImpl();
        asset.setAssetName("Test asset");
        asset.setAssetCode("0001");
        asset.setAssetType(AssetType.MANAGED_FUND);
        rebateHoldingIncome.setAsset(asset);

        List<HoldingIncomeDetails> holdingIncomes = new ArrayList<>();
        holdingIncomes.add(rebateHoldingIncome);

        SubAccountIncomeDetailsImpl mfDetails = new SubAccountIncomeDetailsImpl();
        mfDetails.setAssetType(AssetType.MANAGED_FUND);
        mfDetails.addIncomes(holdingIncomes);
        List<SubAccountIncomeDetails> accountIncomes = new ArrayList<>();
        accountIncomes.add(mfDetails);

        Map<AssetType, Map<IncomeType, List<AbstractIncomeDto>>> result = subAccountAggregator
                .buildInvestmentMapFromSubAccount(accountIncomes, AccountKey.valueOf("accountId"), new FailFastErrorsImpl());

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(AssetType.MANAGED_FUND, result.keySet().iterator().next());
        Assert.assertEquals(1, result.get(AssetType.MANAGED_FUND).size());
        AbstractIncomeDto incomeDto = result.get(AssetType.MANAGED_FUND).get(IncomeType.FEE_REBATE).get(0);
        Assert.assertEquals(rebate, incomeDto);
    }

    @Test
    public void testSubAccountAggregator_whenIncomeListHasDividend_thenTDDtoIsproduced() {
        DividendIncomeImpl dividend = new DividendIncomeImpl();
        dividend.setAmount(BigDecimal.valueOf(100d));
        dividend.setPaymentDate(new DateTime().minusMonths(1));
        List<Income> dividendIncomeList = new ArrayList<>();
        dividendIncomeList.add(dividend);

        HoldingIncomeDetailsImpl dividendHoldingIncome = new HoldingIncomeDetailsImpl();
        dividendHoldingIncome.addIncomes(dividendIncomeList);
        AssetImpl asset = new AssetImpl();
        asset.setAssetName("Test asset");
        asset.setAssetCode("0001");
        asset.setAssetType(AssetType.SHARE);
        dividendHoldingIncome.setAsset(asset);

        List<HoldingIncomeDetails> holdingIncomes = new ArrayList<>();
        holdingIncomes.add(dividendHoldingIncome);

        SubAccountIncomeDetailsImpl mfDetails = new SubAccountIncomeDetailsImpl();
        mfDetails.setAssetType(AssetType.SHARE);
        mfDetails.addIncomes(holdingIncomes);
        List<SubAccountIncomeDetails> accountIncomes = new ArrayList<>();
        accountIncomes.add(mfDetails);

        Map<AssetType, Map<IncomeType, List<AbstractIncomeDto>>> result = subAccountAggregator
                .buildInvestmentMapFromSubAccount(accountIncomes, AccountKey.valueOf("accountId"), new FailFastErrorsImpl());

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(AssetType.SHARE, result.keySet().iterator().next());
        Assert.assertEquals(1, result.get(AssetType.SHARE).size());
        AbstractIncomeDto incomeDto = result.get(AssetType.SHARE).get(IncomeType.DIVIDEND).get(0);
        Assert.assertEquals(div, incomeDto);
    }

    @Test
    public void testSubAccountAggregator_whenIncomeListHasInterst_thenTDDtoIsproduced() {
        InterestIncomeImpl intrIncome = new InterestIncomeImpl();
        intrIncome.setAmount(BigDecimal.valueOf(100d));
        intrIncome.setPaymentDate(new DateTime().minusMonths(1));
        List<Income> intrIncomeList = new ArrayList<>();
        intrIncomeList.add(intrIncome);

        HoldingIncomeDetailsImpl intrHoldingIncome = new HoldingIncomeDetailsImpl();
        intrHoldingIncome.addIncomes(intrIncomeList);
        AssetImpl asset = new AssetImpl();
        asset.setAssetName("Test asset");
        asset.setAssetCode("0001");
        asset.setAssetType(AssetType.BOND);
        intrHoldingIncome.setAsset(asset);

        List<HoldingIncomeDetails> holdingIncomes = new ArrayList<>();
        holdingIncomes.add(intrHoldingIncome);

        SubAccountIncomeDetailsImpl mfDetails = new SubAccountIncomeDetailsImpl();
        mfDetails.setAssetType(AssetType.SHARE);
        mfDetails.addIncomes(holdingIncomes);
        List<SubAccountIncomeDetails> accountIncomes = new ArrayList<>();
        accountIncomes.add(mfDetails);

        Map<AssetType, Map<IncomeType, List<AbstractIncomeDto>>> result = subAccountAggregator
                .buildInvestmentMapFromSubAccount(accountIncomes, AccountKey.valueOf("accountId"), new FailFastErrorsImpl());

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(AssetType.SHARE, result.keySet().iterator().next());
        Assert.assertEquals(1, result.get(AssetType.SHARE).size());
        AbstractIncomeDto incomeDto = result.get(AssetType.SHARE).get(IncomeType.INTEREST).get(0);
        Assert.assertEquals(intr, incomeDto);
    }

    @Test
    public void testSubAccountAggregator_whenIncomeListHasCash_thenCashDtoIsproduced() {
        CashIncomeImpl cashIncome = new CashIncomeImpl();
        cashIncome.setAmount(BigDecimal.valueOf(100d));
        cashIncome.setPaymentDate(new DateTime().minusMonths(1));
        List<Income> cashIncomeList = new ArrayList<>();
        cashIncomeList.add(cashIncome);

        HoldingIncomeDetailsImpl cashHoldingIncome = new HoldingIncomeDetailsImpl();
        cashHoldingIncome.addIncomes(cashIncomeList);
        AssetImpl asset = new AssetImpl();
        asset.setAssetName("Test asset");
        asset.setAssetCode("0001");
        asset.setAssetType(AssetType.CASH);
        cashHoldingIncome.setAsset(asset);

        List<HoldingIncomeDetails> holdingIncomes = new ArrayList<>();
        holdingIncomes.add(cashHoldingIncome);

        SubAccountIncomeDetailsImpl mfDetails = new SubAccountIncomeDetailsImpl();
        mfDetails.setAssetType(AssetType.CASH);
        mfDetails.addIncomes(holdingIncomes);
        List<SubAccountIncomeDetails> accountIncomes = new ArrayList<>();
        accountIncomes.add(mfDetails);

        Map<AssetType, Map<IncomeType, List<AbstractIncomeDto>>> result = subAccountAggregator
                .buildInvestmentMapFromSubAccount(accountIncomes, AccountKey.valueOf("accountId"), new FailFastErrorsImpl());

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(AssetType.CASH, result.keySet().iterator().next());
        Assert.assertEquals(1, result.get(AssetType.CASH).size());
        AbstractIncomeDto incomeDto = result.get(AssetType.CASH).get(IncomeType.CASH).get(0);
        Assert.assertEquals(cash, incomeDto);
    }

    @Test
    public void testSubAccountAggregator_whenIncomeListHasTermDeposit_thenTDDtoIsproduced() {
        TermDepositIncomeImpl tdIncome = new TermDepositIncomeImpl();
        tdIncome.setInterest(BigDecimal.valueOf(100d));
        tdIncome.setPaymentDate(new DateTime().minusMonths(1));
        List<Income> tdIncomeList = new ArrayList<>();
        tdIncomeList.add(tdIncome);

        HoldingIncomeDetailsImpl tdHoldingIncome = new HoldingIncomeDetailsImpl();
        tdHoldingIncome.addIncomes(tdIncomeList);
        AssetImpl asset = new AssetImpl();
        asset.setAssetId("A12345");
        asset.setAssetName("Test asset");
        asset.setAssetCode("0001");
        asset.setAssetType(AssetType.TERM_DEPOSIT);
        tdHoldingIncome.setAsset(asset);

        List<HoldingIncomeDetails> holdingIncomes = new ArrayList<>();
        holdingIncomes.add(tdHoldingIncome);

        SubAccountIncomeDetailsImpl mfDetails = new SubAccountIncomeDetailsImpl();
        mfDetails.setAssetType(AssetType.TERM_DEPOSIT);
        mfDetails.addIncomes(holdingIncomes);
        List<SubAccountIncomeDetails> accountIncomes = new ArrayList<>();
        accountIncomes.add(mfDetails);

        Mockito.when(incomeDtoIncomeBuilder.toTermDepositIncomeDto(Mockito.any(HoldingIncomeDetails.class), Mockito.any(Income.class),
                Mockito.any(TermDepositPresentation.class), Mockito.any(Boolean.class)))
                .thenAnswer(new Answer<TermDepositIncomeDto>() {

                    @Override
                    public TermDepositIncomeDto answer(InvocationOnMock invocation) throws Throwable {
                        Object args[] = invocation.getArguments();
                        //isWrapTermDeposit is null
                        Assert.assertEquals(null, args[3]);
                        return td;
                    }
                });

        Map<AssetType, Map<IncomeType, List<AbstractIncomeDto>>> result = subAccountAggregator
                .buildInvestmentMapFromSubAccount(accountIncomes, AccountKey.valueOf("accountId"), new FailFastErrorsImpl());

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(AssetType.TERM_DEPOSIT, result.keySet().iterator().next());
        Assert.assertEquals(1, result.get(AssetType.TERM_DEPOSIT).size());
        AbstractIncomeDto incomeDto = result.get(AssetType.TERM_DEPOSIT).get(IncomeType.TERM_DEPOSIT).get(0);
        Assert.assertEquals(td, incomeDto);
    }

    @Test
    public void testSubAccountAggregator_whenIncomeListHasWrapTermDeposit_thenTDDtoIsproduced() {
        TermDepositIncomeImpl tdIncome = new TermDepositIncomeImpl();
        tdIncome.setInterest(BigDecimal.valueOf(100d));
        tdIncome.setPaymentDate(new DateTime().minusMonths(1));
        List<Income> tdIncomeList = new ArrayList<>();
        tdIncomeList.add(tdIncome);

        HoldingIncomeDetailsImpl tdHoldingIncome = new HoldingIncomeDetailsImpl();
        tdHoldingIncome.addIncomes(tdIncomeList);
        AssetImpl asset = new AssetImpl();
        asset.setAssetId("WBC12345TD");
        asset.setAssetName("Test asset");
        asset.setAssetCode("0001");
        asset.setAssetType(AssetType.TERM_DEPOSIT);
        tdHoldingIncome.setAsset(asset);

        List<HoldingIncomeDetails> holdingIncomes = new ArrayList<>();
        holdingIncomes.add(tdHoldingIncome);

        SubAccountIncomeDetailsImpl mfDetails = new SubAccountIncomeDetailsImpl();
        mfDetails.setAssetType(AssetType.TERM_DEPOSIT);
        mfDetails.addIncomes(holdingIncomes);
        List<SubAccountIncomeDetails> accountIncomes = new ArrayList<>();
        accountIncomes.add(mfDetails);

        Mockito.when(incomeDtoIncomeBuilder.toTermDepositIncomeDto(Mockito.any(HoldingIncomeDetails.class), Mockito.any(Income.class),
                Mockito.any(TermDepositPresentation.class), Mockito.any(Boolean.class)))
                .thenAnswer(new Answer<TermDepositIncomeDto>() {

                    @Override
                    public TermDepositIncomeDto answer(InvocationOnMock invocation) throws Throwable {
                        Object args[] = invocation.getArguments();
                        //isWrapTermDeposit is truthy
                        Assert.assertEquals(true, args[3]);
                        return td;
                    }
                });

        Map<AssetType, Map<IncomeType, List<AbstractIncomeDto>>> result = subAccountAggregator
                .buildInvestmentMapFromSubAccount(accountIncomes, AccountKey.valueOf("accountId"), new FailFastErrorsImpl());

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(AssetType.TERM_DEPOSIT, result.keySet().iterator().next());
        Assert.assertEquals(1, result.get(AssetType.TERM_DEPOSIT).size());
        AbstractIncomeDto incomeDto = result.get(AssetType.TERM_DEPOSIT).get(IncomeType.TERM_DEPOSIT).get(0);
        Assert.assertEquals(td, incomeDto);
    }
}
