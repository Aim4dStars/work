package com.bt.nextgen.api.account.v2.service.valuation;

import com.bt.nextgen.api.account.v2.model.InvestmentValuationDto;
import com.bt.nextgen.api.account.v2.model.TermDepositValuationDto;
import com.bt.nextgen.api.account.v2.service.TermDepositPresentationService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositHoldingImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.HoldingKey;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TDValuationAggregatorTest {

    @InjectMocks
    public TDValuationAggregator termDepositValuationAggregator;

    @Mock
    public StaticIntegrationService staticIntegrationService;

    @Mock
    public TermDepositPresentationService termDepositPresentationService;

    @Qualifier("avaloqAssetIntegrationService")
    @Mock
    public AssetIntegrationService assetIntegrationService;

    private AccountKey accountKey;
    private TermDepositAccountValuationImpl subAccount;
    private BigDecimal accountBalance;

    @Before
    public void setup() {

        accountKey = AccountKey.valueOf("accountKey");

        TermDepositHoldingImpl tdHolding = new TermDepositHoldingImpl();

        tdHolding.setMarketValue(BigDecimal.valueOf(1000));
        tdHolding.setAccruedIncome(BigDecimal.valueOf(300));
        tdHolding.setYield(BigDecimal.valueOf(0.5));
        tdHolding.setMaturityDate(new DateTime());
        tdHolding.setMaturityInstruction("7");
        tdHolding.setHoldingKey(HoldingKey.valueOf("accountId", "BT Term Deposit"));

        AssetImpl asset = new AssetImpl();
        asset.setAssetId("20168");
        tdHolding.setAsset(asset);

        List<AccountHolding> holdingsList = new ArrayList<>();
        holdingsList.add(tdHolding);

        subAccount = new TermDepositAccountValuationImpl();
        subAccount.addHoldings(holdingsList);

        accountBalance = BigDecimal.valueOf(20000);

        CodeImpl renewCode = new CodeImpl("7", "CONTR_AMOUNT", "Rollover Principal");
        Mockito.when(staticIntegrationService.loadCode(Mockito.eq(CodeCategory.TD_RENEW_MODE), Mockito.anyString(),
                Mockito.any(ServiceErrors.class))).thenReturn(renewCode);

        TermDepositPresentation tdPres = new TermDepositPresentation();
        tdPres.setBrandName("BT Term Deposit");
        tdPres.setBrandClass("BT");
        tdPres.setTerm("6 months");
        tdPres.setPaymentFrequency(PaymentFrequency.AT_MATURITY.getDisplayName().toLowerCase());

        Mockito.when(termDepositPresentationService.getTermDepositPresentation(Mockito.any(AccountKey.class),
                Mockito.any(String.class), Mockito.any(ServiceErrors.class))).thenReturn(tdPres);

    }

    @Test
    public void testBuildValuationDto_whenTDTypeSubAccountPassed_thenTDValuationDtosCreated() {

        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        List<InvestmentValuationDto> dtoList = termDepositValuationAggregator.getTermDepositValuationDtos(accountKey, subAccount,
                accountBalance, serviceErrors);

        Assert.assertNotNull(dtoList);
        Assert.assertEquals(1, dtoList.size());

        TermDepositValuationDto dto = (TermDepositValuationDto) dtoList.get(0);

        Assert.assertNotNull(dto);

        Assert.assertEquals(BigDecimal.valueOf(1000), dto.getBalance());
        Assert.assertEquals(BigDecimal.valueOf(300), dto.getIncome());
        Assert.assertEquals(BigDecimal.valueOf(0.005), dto.getInterestRate());
        Assert.assertEquals(0.05, dto.getPortfolioPercent().doubleValue(), 0.005);
        Assert.assertEquals("7", dto.getMaturityInstructionId());
        Assert.assertEquals("Rollover Principal", dto.getMaturityInstruction());
        Assert.assertEquals("6 months", dto.getTerm().toString());
        Assert.assertEquals(PaymentFrequency.AT_MATURITY.getDisplayName().toLowerCase(), dto.getPaymentFrequency());
        Assert.assertEquals("Term deposits", dto.getCategoryName());
    }

}
