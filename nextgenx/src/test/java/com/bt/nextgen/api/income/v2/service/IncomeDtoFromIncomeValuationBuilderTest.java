package com.bt.nextgen.api.income.v2.service;

import com.bt.nextgen.api.income.v2.model.DividendIncomeDto;
import com.bt.nextgen.api.income.v2.model.TermDepositIncomeDto;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.avaloq.income.DividendIncomeImpl;
import com.bt.nextgen.service.avaloq.income.HoldingIncomeDetailsImpl;
import com.bt.nextgen.service.avaloq.income.TermDepositIncomeImpl;
import com.bt.nextgen.service.integration.base.SystemType;
import com.bt.nextgen.service.integration.income.Income;
import com.bt.nextgen.service.wrap.integration.income.ThirdPartyDividendIncomeImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by L067221 on 18/09/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class IncomeDtoFromIncomeValuationBuilderTest {

    @InjectMocks
    private IncomeDtoFromIncomeValuationBuilder builder;

    @Test
    public void testToTermDepositIncomeDto() {
        DateTime paymentDate = new DateTime();
        TermDepositIncomeImpl termDepositIncome = new TermDepositIncomeImpl();
        termDepositIncome.setPaymentDate(paymentDate);
        List<Income> incomeList = new ArrayList<>();
        incomeList.add(termDepositIncome);

        HoldingIncomeDetailsImpl holdingIncomeDetails = new HoldingIncomeDetailsImpl();
        TermDepositPresentation termDepositPresentation = new TermDepositPresentation();
        termDepositPresentation.setBrandName("WRAP");

        holdingIncomeDetails.addIncomes(incomeList);
        AssetImpl asset = new TermDepositAssetImpl();
        asset.setAssetType(AssetType.TERM_DEPOSIT);
        asset.setAssetName("Test asset");
        asset.setAssetCode("0001");
        holdingIncomeDetails.setAsset(asset);

        TermDepositIncomeDto incomeDto =  builder.toTermDepositIncomeDto(holdingIncomeDetails, termDepositIncome, termDepositPresentation, true );
        assertNotNull(incomeDto);
        assertEquals(true, incomeDto.isWrapTermDeposit());
        assertEquals("WRAP", incomeDto.getName());
        assertEquals(paymentDate, incomeDto.getPaymentDate());
    }

    @Test
    public void testToDividendIncomeDto() {
        List<Income> incomes = new ArrayList<>();

        DividendIncomeImpl dividendIncome1 = new DividendIncomeImpl();
        ThirdPartyDividendIncomeImpl dividendIncome2 = new ThirdPartyDividendIncomeImpl();
        ThirdPartyDividendIncomeImpl dividendIncome3 = new ThirdPartyDividendIncomeImpl();
        dividendIncome2.setThirdPartySource(SystemType.WRAP.name());

        incomes.add(dividendIncome1);
        incomes.add(dividendIncome2);
        incomes.add(dividendIncome3);

        HoldingIncomeDetailsImpl holdingIncomeDetails = new HoldingIncomeDetailsImpl();

        holdingIncomeDetails.addIncomes(incomes);
        AssetImpl asset = new TermDepositAssetImpl();
        asset.setAssetType(AssetType.SHARE);
        asset.setAssetName("Test asset");
        asset.setAssetCode("0001");
        holdingIncomeDetails.setAsset(asset);

        DividendIncomeDto incomeDto =  builder.toDividendIncomeDto(holdingIncomeDetails, dividendIncome1);
        assertNotNull(incomeDto);
        assertFalse(incomeDto.isWrapIncome());

        incomeDto =  builder.toDividendIncomeDto(holdingIncomeDetails, dividendIncome2);
        assertNotNull(incomeDto);
        assertTrue(incomeDto.isWrapIncome());

        incomeDto =  builder.toDividendIncomeDto(holdingIncomeDetails, dividendIncome3);
        assertNotNull(incomeDto);
        assertFalse(incomeDto.isWrapIncome());
    }
}
