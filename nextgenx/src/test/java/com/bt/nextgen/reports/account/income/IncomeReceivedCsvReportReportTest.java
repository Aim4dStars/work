package com.bt.nextgen.reports.account.income;

import com.bt.nextgen.api.income.v2.model.CashIncomeDto;
import com.bt.nextgen.api.income.v2.model.IncomeDetailsKey;
import com.bt.nextgen.api.income.v2.model.IncomeDetailsType;
import com.bt.nextgen.api.income.v2.model.IncomeDto;
import com.bt.nextgen.api.income.v2.model.IncomeValueDto;
import com.bt.nextgen.api.income.v2.model.IncomeValueFlatDto;
import com.bt.nextgen.api.income.v2.model.IncomeValuesDto;
import com.bt.nextgen.api.income.v2.model.InvestmentIncomeTypeDto;
import com.bt.nextgen.api.income.v2.model.InvestmentTypeDto;
import com.bt.nextgen.api.income.v2.model.TermDepositIncomeDto;
import com.bt.nextgen.api.income.v2.service.IncomeDetailsDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.income.IncomeType;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IncomeReceivedCsvReportReportTest {

    @InjectMocks
    private IncomeReceivedCSVReport incomeReceivedReport;

    @Mock
    private IncomeDetailsDtoService incomeDtoService;

    private Map<String, String> params = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        params = new HashMap<>();
        params.put("account-id", "accountId");
        params.put("start-date", "2015-01-01");
        params.put("end-date", "2018-01-01");
        params.put("income-type", "RECEIVED");
    }

    @Test
    public void testGetIncomes_whenNoData_thenNoResults() {
        IncomeDetailsKey key = new IncomeDetailsKey("accountId", IncomeDetailsType.RECEIVED, new DateTime("2015-01-01"),
                new DateTime("2018-01-01"));
        List<IncomeDto> incomeDtos = Collections.emptyList();
        IncomeValuesDto noData = new IncomeValuesDto(key, incomeDtos);
        when(incomeDtoService.find(any(IncomeDetailsKey.class), any(ServiceErrors.class))).thenReturn(noData);
        List<IncomeValueFlatDto> incomes = incomeReceivedReport.getIncomes(params);
        Assert.assertNotNull(incomes);
        Assert.assertEquals(0, incomes.size());
    }

    @Test
    public void testGetIncomes_whenDataSupplied_thenCsvRowsMatch() {
        IncomeDetailsKey key = new IncomeDetailsKey("accountId", IncomeDetailsType.RECEIVED, new DateTime("2015-01-01"),
                new DateTime("2018-01-01"));

        List<IncomeDto> incomeValues = new ArrayList<>();
        List<IncomeDto> incomeValues1 = new ArrayList<>();

        CashIncomeDto cashInterestDto = new CashIncomeDto("BT CMA", null, null, BigDecimal.valueOf(400), IncomeType.INTEREST);
        IncomeValueDto cashInterest = new IncomeValueDto(cashInterestDto);
        TermDepositIncomeDto termDepositIncome = new TermDepositIncomeDto("BT Termdeposit", "BT", new DateTime("2017-02-20"),
                new DateTime("2018-02-20"), BigDecimal.valueOf(1000), "1 year", PaymentFrequency.AT_MATURITY.getDisplayName());
        IncomeValueDto termDepositInterest = new IncomeValueDto(termDepositIncome);
        incomeValues.add(cashInterest);
        incomeValues1.add(termDepositInterest);

        CashIncomeDto cashDistributionDto = new CashIncomeDto("BT CMA", null, null, BigDecimal.valueOf(400),
                IncomeType.DISTRIBUTION);
        IncomeValueDto cashDistribution = new IncomeValueDto(cashDistributionDto);
        incomeValues.add(cashDistribution);
        List<IncomeDto> investmentIncomes = new ArrayList<>();
        InvestmentIncomeTypeDto btCma = new InvestmentIncomeTypeDto(IncomeType.CASH, incomeValues, BigDecimal.valueOf(1000));
        investmentIncomes.add(btCma);

        List<IncomeDto> investmentIncomes1 = new ArrayList<>();
        InvestmentIncomeTypeDto tdIncomeType = new InvestmentIncomeTypeDto(IncomeType.TERM_DEPOSIT, incomeValues1,
                BigDecimal.valueOf(1000));
        investmentIncomes1.add(tdIncomeType);

        List<IncomeDto> investmentTypeDtos = new ArrayList<>();
        InvestmentTypeDto cash = new InvestmentTypeDto(AssetType.CASH, investmentIncomes, BigDecimal.valueOf(1000));
        InvestmentTypeDto termDeposit = new InvestmentTypeDto(AssetType.TERM_DEPOSIT, investmentIncomes1,
                BigDecimal.valueOf(1000));
        investmentTypeDtos.add(cash);
        investmentTypeDtos.add(termDeposit);

        IncomeValuesDto noData = new IncomeValuesDto(key, investmentTypeDtos);
        when(incomeDtoService.find(any(IncomeDetailsKey.class), any(ServiceErrors.class))).thenReturn(noData);
        List<IncomeValueFlatDto> incomes = incomeReceivedReport.getIncomes(params);
        Assert.assertNotNull(incomes);
        Assert.assertEquals(3, incomes.size());

        IncomeValueFlatDto interest = incomes.get(0);
        Assert.assertEquals("Cash interest payment", interest.getDescription());

        IncomeValueFlatDto distribution = incomes.get(1);
        Assert.assertEquals("Cash income distributed", distribution.getDescription());

        IncomeValueFlatDto incomeValue1 = incomes.get(2);
        Assert.assertEquals("Interest received: Maturing 20 Feb 2018", incomeValue1.getDescription());

    }

    private IncomeValuesDto getWrapTDData() {
        IncomeDetailsKey key = new IncomeDetailsKey("accountId", IncomeDetailsType.RECEIVED, new DateTime("2015-01-01"),
                new DateTime("2018-01-01"));

        List<IncomeDto> incomeValues = new ArrayList<>();
        TermDepositIncomeDto termDepositIncome = new TermDepositIncomeDto("Wrap Termdeposit", "BT", new DateTime("2017-02-20"),
                new DateTime("2018-02-20"), BigDecimal.valueOf(1000), "1 year", PaymentFrequency.AT_MATURITY.getDisplayName());
        termDepositIncome.setWrapTermDeposit(true);
        IncomeValueDto termDepositInterest = new IncomeValueDto(termDepositIncome);
        incomeValues.add(termDepositInterest);


        List<IncomeDto> investmentIncomes = new ArrayList<>();
        InvestmentIncomeTypeDto tdIncomeType = new InvestmentIncomeTypeDto(IncomeType.TERM_DEPOSIT, incomeValues,
                BigDecimal.valueOf(1000));
        investmentIncomes.add(tdIncomeType);

        List<IncomeDto> investmentTypeDtos = new ArrayList<>();
        InvestmentTypeDto termDeposit = new InvestmentTypeDto(AssetType.TERM_DEPOSIT, investmentIncomes,
                BigDecimal.valueOf(1000));
        investmentTypeDtos.add(termDeposit);

        return new IncomeValuesDto(key, investmentTypeDtos);
    }

    @Test
    public void testGetIncomes_whenDataSupplied_forWrapTD_thenCsvRowsMatch() {
        IncomeValuesDto incomeValuesDto = getWrapTDData();
        when(incomeDtoService.find(any(IncomeDetailsKey.class), any(ServiceErrors.class))).thenReturn(incomeValuesDto);

        List<IncomeValueFlatDto> incomes = incomeReceivedReport.getIncomes(params);
        Assert.assertNotNull(incomes);
        Assert.assertEquals(1, incomes.size());

        IncomeValueFlatDto incomeValue1 = incomes.get(0);
        Assert.assertEquals("Interest received", incomeValue1.getDescription());
    }


}
