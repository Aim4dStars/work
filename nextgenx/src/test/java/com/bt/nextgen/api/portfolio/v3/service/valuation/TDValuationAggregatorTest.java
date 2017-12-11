package com.bt.nextgen.api.portfolio.v3.service.valuation;

import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.TermDepositValuationDto;
import com.bt.nextgen.api.portfolio.v3.service.TermDepositPresentationService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositHoldingImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.base.SystemType;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.HoldingKey;
import com.bt.nextgen.service.wrap.integration.portfolio.WrapTermDepositHoldingImpl;
import com.btfin.panorama.core.conversion.CodeCategory;
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

    @Mock
    private OptionsService optionsService;

    private AccountKey accountKey;
    private TermDepositAccountValuationImpl subAccount;
    private TermDepositAccountValuationImpl subAccount1;
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

        Mockito.when(
                optionsService.hasFeature(Mockito.any(OptionKey.class), Mockito.any(AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(true);

        TermDepositPresentation tdPres = new TermDepositPresentation();
        tdPres.setBrandName("BT Term Deposit");
        tdPres.setBrandClass("BT");
        tdPres.setTerm("6 months");
        tdPres.setPaymentFrequency(PaymentFrequency.AT_MATURITY.getDisplayName().toLowerCase());

        Mockito.when(
                termDepositPresentationService.getTermDepositPresentation(Mockito.any(AccountKey.class),
                        Mockito.any(String.class), Mockito.any(ServiceErrors.class))).thenReturn(tdPres);

    }

    @Test
    public void testBuildValuationDto_whenTDTypeSubAccountPassed_thenTDValuationDtosCreated() {

        CodeImpl renewCode = new CodeImpl("7", "CONTR_AMOUNT", "Reinvest principal only", "avq$contr_amount");
        Mockito.when(
                staticIntegrationService.loadCode(Mockito.eq(CodeCategory.TD_RENEW_MODE), Mockito.anyString(),
                        Mockito.any(ServiceErrors.class))).thenReturn(renewCode);

        ServiceErrors serviceErrors = new ServiceErrorsImpl();

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

        WrapTermDepositHoldingImpl wrapTDHolding = new WrapTermDepositHoldingImpl();
        wrapTDHolding.setMarketValue(BigDecimal.valueOf(2000));
        wrapTDHolding.setAccruedIncome(BigDecimal.valueOf(400));
        wrapTDHolding.setYield(BigDecimal.valueOf(0.5));
        wrapTDHolding.setMaturityDate(new DateTime());
        wrapTDHolding.setMaturityInstruction("-");
        wrapTDHolding.setHoldingKey(HoldingKey.valueOf("accountId", "WBC Term Deposit 185d 05-MAY-16 2.50%"));
        wrapTDHolding.setThirdPartySource(SystemType.WRAP.getName());
        asset = new AssetImpl();
        asset.setAssetId("20168");
        tdHolding.setAsset(asset);

        List<AccountHolding> holdingsList = new ArrayList<>();
        holdingsList.add(tdHolding);
        holdingsList.add(wrapTDHolding);

        subAccount = new TermDepositAccountValuationImpl();
        subAccount.addHoldings(holdingsList);
        List<InvestmentValuationDto> dtoList = termDepositValuationAggregator.getTermDepositValuationDtos(accountKey,
                subAccount.getHoldings(), accountBalance, serviceErrors);

        Assert.assertNotNull(dtoList);
        Assert.assertEquals(2, dtoList.size());

        TermDepositValuationDto dto1 = (TermDepositValuationDto) dtoList.get(0);
        Assert.assertNotNull(dto1);
        Assert.assertEquals("Term deposits", termDepositValuationAggregator.getSubAccountCategory());
        Assert.assertEquals(BigDecimal.valueOf(1000), dto1.getBalance());
        Assert.assertEquals(BigDecimal.valueOf(300), dto1.getIncome());
        Assert.assertEquals(BigDecimal.valueOf(0.005), dto1.getInterestRate());
        Assert.assertEquals(0.05, dto1.getPortfolioPercent().doubleValue(), 0.005);
        Assert.assertEquals("7", dto1.getMaturityInstructionId());
        Assert.assertEquals("Reinvest principal only", dto1.getMaturityInstruction());
        Assert.assertEquals("6 months", dto1.getTerm().toString());
        Assert.assertEquals(PaymentFrequency.AT_MATURITY.getDisplayName().toLowerCase(), dto1.getPaymentFrequency());
        Assert.assertEquals("Term deposits", dto1.getCategoryName());

        TermDepositValuationDto dto2 = (TermDepositValuationDto) dtoList.get(1);
        Assert.assertNotNull(dto2);
        Assert.assertEquals("Term deposits", termDepositValuationAggregator.getSubAccountCategory());
        Assert.assertEquals(BigDecimal.valueOf(2000), dto2.getBalance());
        Assert.assertEquals(BigDecimal.valueOf(400), dto2.getIncome());
        Assert.assertEquals(BigDecimal.valueOf(0.005), dto2.getInterestRate());
        Assert.assertEquals(0.1, dto2.getPortfolioPercent().doubleValue(), 0.005);
        Assert.assertEquals("-", dto2.getMaturityInstructionId());
        Assert.assertNull(dto2.getTerm());
        Assert.assertNull(dto2.getPaymentFrequency());
        Assert.assertEquals("Term deposits", dto2.getCategoryName());
    }

    @Test
    public void testBuildValuationDto_whenTDTypeWithWrapSubAccountPassed_thenTDValuationDtosCreated() {

        CodeImpl renewCode = new CodeImpl("7", "CONTR_AMOUNT", "Reinvest principal only", "avq$contr_amount");
        Mockito.when(
                staticIntegrationService.loadCode(Mockito.eq(CodeCategory.TD_RENEW_MODE), Mockito.anyString(),
                        Mockito.any(ServiceErrors.class))).thenReturn(renewCode);

        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        List<InvestmentValuationDto> dtoList = termDepositValuationAggregator.getTermDepositValuationDtos(accountKey,
                subAccount.getHoldings(), accountBalance, serviceErrors);

        Assert.assertNotNull(dtoList);
        Assert.assertEquals(1, dtoList.size());

        TermDepositValuationDto dto = (TermDepositValuationDto) dtoList.get(0);

        Assert.assertNotNull(dto);

        Assert.assertEquals("Term deposits", termDepositValuationAggregator.getSubAccountCategory());
        Assert.assertEquals(BigDecimal.valueOf(1000), dto.getBalance());
        Assert.assertEquals(BigDecimal.valueOf(300), dto.getIncome());
        Assert.assertEquals(BigDecimal.valueOf(0.005), dto.getInterestRate());
        Assert.assertEquals(0.05, dto.getPortfolioPercent().doubleValue(), 0.005);
        Assert.assertEquals("7", dto.getMaturityInstructionId());
        Assert.assertEquals("Reinvest principal only", dto.getMaturityInstruction());
        Assert.assertEquals("6 months", dto.getTerm().toString());
        Assert.assertEquals(PaymentFrequency.AT_MATURITY.getDisplayName().toLowerCase(), dto.getPaymentFrequency());
        Assert.assertEquals("Term deposits", dto.getCategoryName());
    }

    @Test
    public void testBuildValuationDto_whenNoRenewCodesPassed_thenTDValuationDtosCreated() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        Mockito.when(
                staticIntegrationService.loadCode(Mockito.eq(CodeCategory.TD_RENEW_MODE), Mockito.anyString(),
                        Mockito.any(ServiceErrors.class))).thenReturn(null);

        List<InvestmentValuationDto> dtoList = termDepositValuationAggregator.getTermDepositValuationDtos(accountKey,
                subAccount.getHoldings(), accountBalance, serviceErrors);

        Assert.assertNotNull(dtoList);
        Assert.assertEquals(1, dtoList.size());

        TermDepositValuationDto dto = (TermDepositValuationDto) dtoList.get(0);

        Assert.assertNotNull(dto);
        Assert.assertEquals("Deposit all money into cash", dto.getMaturityInstruction());
    }

    @Test
    public void testBuildValuationDto_whenNoMaturityInstCodePassed_thenTDValuationDtosCreated() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        CodeImpl renewCode = new CodeImpl("7", "CONTR_AMOUNT", "Reinvest principal only", "avq$contr_amount");

        Mockito.when(
                staticIntegrationService.loadCode(Mockito.eq(CodeCategory.TD_RENEW_MODE), Mockito.anyString(),
                        Mockito.any(ServiceErrors.class))).thenReturn(renewCode);

        TermDepositHoldingImpl tdHolding = new TermDepositHoldingImpl();

        tdHolding.setMarketValue(BigDecimal.valueOf(1000));
        tdHolding.setAccruedIncome(BigDecimal.valueOf(300));
        tdHolding.setYield(BigDecimal.valueOf(0.5));
        tdHolding.setMaturityDate(new DateTime());
        tdHolding.setHoldingKey(HoldingKey.valueOf("accountId", "BT Term Deposit"));

        AssetImpl asset = new AssetImpl();
        asset.setAssetId("20168");
        tdHolding.setAsset(asset);

        List<AccountHolding> holdingsList = new ArrayList<>();
        holdingsList.add(tdHolding);

        subAccount1 = new TermDepositAccountValuationImpl();
        subAccount1.addHoldings(holdingsList);

        List<InvestmentValuationDto> dtoList = termDepositValuationAggregator.getTermDepositValuationDtos(accountKey,
                subAccount1.getHoldings(), accountBalance, serviceErrors);

        Assert.assertNotNull(dtoList);
        Assert.assertEquals(1, dtoList.size());

        TermDepositValuationDto dto = (TermDepositValuationDto) dtoList.get(0);

        Assert.assertNotNull(dto);
        Assert.assertEquals("Deposit all money into cash", dto.getMaturityInstruction());
    }


}
