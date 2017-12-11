package com.bt.nextgen.api.portfolio.v3.service.valuation;

import com.bt.nextgen.api.portfolio.v3.model.DatedAccountKey;
import com.bt.nextgen.api.portfolio.v3.model.valuation.CashManagementValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.WrapAccountValuationImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
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
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ValuationAggregatorTest {

    @InjectMocks
    public ValuationAggregator valuationAggregator;

    @Mock
    public CashValuationAggregator cashValuationAggregator;

    @Mock
    public TDValuationAggregator tdValuationAggregator;

    @Mock
    public MFValuationAggregator mfValuationAggregator;

    @Mock
    public MPValuationAggregator mpValuationAggregator;

    @Mock
    public ShareValuationAggregator shareValuationAggregator;

    @Mock
    public OtherValuationAggregator otherValuationAggregator;

    private static final String CATEGORY_BT_CASH = "BT Cash";
    private static final String CATEGORY_TERM_DEPOSIT = "Term deposits";
    private static final String CATEGORY_MANAGED_FUND = "Managed funds";
    private static final String CATEGORY_MANAGED_PORTFOLIO = "Managed portfolios";
    private static final String CATEGORY_SHARE = "Listed securities";
    private static final String CATEGORY_OTHER = "Other assets";

    private AccountKey accountKey;
    private WrapAccountValuationImpl valuation;
    private DatedAccountKey valuationKey;
    private String cashCategoryName;
    private CashManagementValuationDto cashDto;

    @Before
    public void setup() {

        accountKey = AccountKey.valueOf("plaintext");
        valuationKey = new DatedAccountKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0", new DateTime());
        cashCategoryName = "BT Cash";

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

        List<SubAccountValuation> subAccountValuations = new ArrayList<>();
        subAccountValuations.add(cashAccount);

        valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf(valuationKey.getAccountId()));
        valuation.setSubAccountValuations(subAccountValuations);

        cashDto = new CashManagementValuationDto(accountKey.toString(), cashHolding, BigDecimal.valueOf(1),
                BigDecimal.valueOf(1), false);
        List<InvestmentValuationDto> dtoList = new ArrayList<>();
        dtoList.add(cashDto);

        Mockito.when(
cashValuationAggregator.getCashValuationDtos(Mockito.any(List.class),
                        Mockito.any(BigDecimal.class))).thenReturn(dtoList);

        Mockito.when(cashValuationAggregator.getSubAccountCategory()).thenReturn(cashCategoryName);

    }

    @Test
    public void testBuildValuationDto_whenSubAccountsPassed_thenMatchingCategoryMapCreated() {

        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        Map<AssetType, List<InvestmentValuationDto>> dtoMap = valuationAggregator.getValuationsByCategory(valuation,
                serviceErrors);

        Assert.assertNotNull(dtoMap);
        Assert.assertNotNull(dtoMap.entrySet());

        Assert.assertEquals(1, dtoMap.entrySet().size());

        for (Map.Entry<AssetType, List<InvestmentValuationDto>> entry : dtoMap.entrySet()) {

            Assert.assertEquals("Cash", entry.getKey().getGroupDescription());

            List<InvestmentValuationDto> valuations = entry.getValue();

            Assert.assertEquals(1, valuations.size());
        }
    }
}
