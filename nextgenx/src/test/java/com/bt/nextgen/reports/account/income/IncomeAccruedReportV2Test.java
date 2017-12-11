package com.bt.nextgen.reports.account.income;

import com.bt.nextgen.api.income.v2.model.CashIncomeDto;
import com.bt.nextgen.api.income.v2.model.DividendIncomeDto;
import com.bt.nextgen.api.income.v2.model.IncomeDetailsKey;
import com.bt.nextgen.api.income.v2.model.IncomeDetailsType;
import com.bt.nextgen.api.income.v2.model.IncomeDto;
import com.bt.nextgen.api.income.v2.model.IncomeValueDto;
import com.bt.nextgen.api.income.v2.model.IncomeValuesDto;
import com.bt.nextgen.api.income.v2.model.InvestmentIncomeTypeDto;
import com.bt.nextgen.api.income.v2.model.InvestmentTypeDto;
import com.bt.nextgen.api.income.v2.model.TermDepositIncomeDto;
import com.bt.nextgen.api.income.v2.service.IncomeDetailsDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.income.DividendIncomeImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.income.IncomeType;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.btfin.panorama.service.integration.asset.AssetType;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IncomeAccruedReportV2Test {

    @InjectMocks
    private IncomeAccruedReportV2 incomeAccruedReport;

    @Mock
    private ContentDtoService contentService;

    @Mock
    private IncomeDetailsDtoService incomeDtoService;

    @Mock
    private OptionsService optionsService;

    private final Map<String, Object> params = new HashMap<>();
    private final Map<String, Object> paramsObj = new HashMap<>();
    private final Map<String, Object> dataCollections = new HashMap<>();
    private static final String PARAM_INCOME_TYPE = "income-type";
    private IncomeValuesDto income = null;

    @Before
    public void setUp() throws Exception {
        params.put(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "114129E83B02BCA4D4B3245CEFB3778405F178638F9731DD");
        params.put(UriMappingConstants.START_DATE_PARAMETER_MAPPING, "2015-07-14");
        params.put(UriMappingConstants.END_DATE_PARAMETER_MAPPING, "2015-07-14");
        params.put(PARAM_INCOME_TYPE, "ACCRUED");

        paramsObj.put(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "114129E83B02BCA4D4B3245CEFB3778405F178638F9731DD");
        paramsObj.put(UriMappingConstants.START_DATE_PARAMETER_MAPPING, "2015-07-14");
        paramsObj.put(UriMappingConstants.END_DATE_PARAMETER_MAPPING, "2015-07-14");
        paramsObj.put(PARAM_INCOME_TYPE, "ACCRUED");

        ContentDto contentDto = Mockito.mock(ContentDto.class);
        Mockito.when(contentDto.getContent()).thenReturn("disclaimervalue");
        Mockito.when(contentService.find(Mockito.any(ContentKey.class), Mockito.any(ServiceErrors.class))).thenReturn(contentDto);

        Mockito.when(
                optionsService.hasFeature(Mockito.any(OptionKey.class), Mockito.any(AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(Boolean.TRUE);

        CashIncomeDto cashIncomeDto = new CashIncomeDto("MP Cash", "", new DateTime("2015-07-14"), new BigDecimal(2000),
                IncomeType.DISTRIBUTION);
        CashIncomeDto cashIncomeDto1 = new CashIncomeDto("BT Cash", "", new DateTime("2015-07-14"), new BigDecimal(2000),
                IncomeType.INTEREST);
        CashIncomeDto cashIncomeDto2 = new CashIncomeDto("MP Cash", "", new DateTime("2015-07-14"), new BigDecimal(2000),
                IncomeType.DISTRIBUTION);
        TermDepositIncomeDto cashIncomeDto3 = new TermDepositIncomeDto("BT Term deposit", "BT", new DateTime("2015-07-14"),
                new DateTime("2015-07-14"), new BigDecimal(2000), "2 years", "At maturity");

        IncomeDto incomeDto = new IncomeValueDto(cashIncomeDto);
        IncomeDto incomeDto1 = new IncomeValueDto(cashIncomeDto1);
        IncomeDto incomeDto2 = new IncomeValueDto(cashIncomeDto2);
        IncomeDto incomeDto3 = new IncomeValueDto(cashIncomeDto3);

        DividendIncomeImpl dividendIncome = new DividendIncomeImpl();

        dividendIncome.setQuantity(new BigDecimal(20));
        dividendIncome.setAmount(new BigDecimal(40));
        dividendIncome.setPaymentDate(new DateTime("2015-07-14"));
        dividendIncome.setExecutionDate(new DateTime("2015-07-15"));
        dividendIncome.setIncomeRate(new BigDecimal(10));
        dividendIncome.setFrankedDividend(new BigDecimal(4));
        dividendIncome.setUnfrankedDividend(new BigDecimal(2));
        dividendIncome.setFrankingCredit(new BigDecimal(2));

        DividendIncomeDto dividendIncomeDto = new DividendIncomeDto("BHP Billiton", "BHP", dividendIncome);
        IncomeDto mpDividendIncomeDto = new IncomeValueDto(dividendIncomeDto);

        IncomeDto cashIncomeTypeDto = new InvestmentIncomeTypeDto(IncomeType.CASH, Collections.singletonList(incomeDto),
                new BigDecimal(2000));
        IncomeDto cashIncomeTypeDto1 = new InvestmentIncomeTypeDto(IncomeType.CASH, Collections.singletonList(incomeDto1),
                new BigDecimal(2000));
        IncomeDto tdCashIncomeTypeDto1 = new InvestmentIncomeTypeDto(IncomeType.TERM_DEPOSIT,
                Collections.singletonList(incomeDto3), new BigDecimal(3000));
        IncomeDto cashIncomeTypeDto2 = new InvestmentIncomeTypeDto(IncomeType.CASH, Collections.singletonList(incomeDto2),
                new BigDecimal(2000));
        IncomeDto dividendIncomeTypeDto = new InvestmentIncomeTypeDto(IncomeType.DIVIDEND,
                Collections.singletonList(mpDividendIncomeDto), new BigDecimal(2000));

        List<IncomeDto> incomes = new ArrayList<>();
        incomes.add(cashIncomeTypeDto);
        incomes.add(dividendIncomeTypeDto);
        
        List<IncomeDto> incomes1 = new ArrayList<>();
        incomes1.add(cashIncomeTypeDto1);

        List<IncomeDto> incomes2 = new ArrayList<>();
        incomes2.add(cashIncomeTypeDto2);

        List<IncomeDto> incomes3 = new ArrayList<>();
        incomes3.add(tdCashIncomeTypeDto1);

        IncomeDto investmentTypeDto = new InvestmentTypeDto(AssetType.MANAGED_PORTFOLIO, incomes, new BigDecimal(2000));
        IncomeDto cashInvestmentTypeDto = new InvestmentTypeDto(AssetType.CASH, incomes1, new BigDecimal(2000));
        IncomeDto tdCashInvestmentTypeDto = new InvestmentTypeDto(AssetType.TERM_DEPOSIT, incomes1, new BigDecimal(5000));
        IncomeDto tmpInvestmentTypeDto = new InvestmentTypeDto(AssetType.TAILORED_PORTFOLIO, incomes2, new BigDecimal(3000));

        List<IncomeDto> investments = new ArrayList<>();
        investments.add(investmentTypeDto);
        investments.add(cashInvestmentTypeDto);
        investments.add(tmpInvestmentTypeDto);
        investments.add(tdCashInvestmentTypeDto);

        income = new IncomeValuesDto(new IncomeDetailsKey("114129E83B02BCA4D4B3245CEFB3778405F178638F9731DD",
                IncomeDetailsType.ACCRUED, new DateTime("2015-07-14"), new DateTime("2015-07-14")),
 investments);
    }

    @Test
    public final void testGetDisclaimer() {
        Assert.assertEquals("disclaimervalue", incomeAccruedReport.getDisclaimer());
    }

    @Test
    public final void testGetStartDate() {
        Assert.assertEquals(ReportFormatter.format(ReportFormat.SHORT_DATE, new DateTime("2015-07-14")),
                incomeAccruedReport.getStartDate(params));
    }

    @Test
    public final void testGetEndDate() {
        Assert.assertEquals(ReportFormatter.format(ReportFormat.SHORT_DATE, new DateTime("2015-07-14")),
                incomeAccruedReport.getEndDate(params));
    }

    @Test
    public final void testReportTitle() {
        Assert.assertEquals("Income accrued", incomeAccruedReport.getReportTitle());
    }

    @Test
    public final void testReportSubTitle() {
        Assert.assertEquals("As at 14 Jul 2015", incomeAccruedReport.getReportSubTitle(params));
    }

    @Test
    public final void testSummaryDescription() {
        Assert.assertEquals("Total income accrued", incomeAccruedReport.getSummaryDescription(params, dataCollections));
    }

    @Test
    public final void testData_whenGetDataCalled_reportProducesValuesMatchingDto() {

        Mockito.when(incomeDtoService.find(Mockito.any(IncomeDetailsKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                income);
        Collection<?> result = incomeAccruedReport.getData(paramsObj, dataCollections);
        Assert.assertEquals(true, incomeAccruedReport.getIsIncomeBreakDown(params, dataCollections));
        IncomeReportData incomeReportData = (IncomeReportData) result.iterator().next();
        IncomeSummaryReportData summaryReportData = incomeReportData.getIncomeSummaryReportData();
        Assert.assertEquals(incomeReportData.getIncomeCategories().size(), income.getChildren().size());
        Assert.assertEquals(summaryReportData.getInterestTotal(), "$4,000.00");
        Assert.assertEquals(summaryReportData.getDividendTotal(), "$40.00");
        Assert.assertEquals(summaryReportData.getDistributionTotal(), "$4,000.00");
        Assert.assertEquals(summaryReportData.getIncomeTotal(), "$8,040.00");
        Assert.assertEquals(summaryReportData.getFrankedDividendTotal(), "$4.00");
        Assert.assertEquals(summaryReportData.getUnfrankedDividendTotal(), "$2.00");

        List<IncomeValueReportData> investmentTypes = incomeReportData.getIncomeCategories();
        List<IncomeValueReportData> investmentIncomeTypes = investmentTypes.get(0).getIncomeValuesReportData();

        IncomeValueReportData cashIncomeValueReportData = investmentIncomeTypes.get(0).getIncomeValuesReportData().get(0);
        Assert.assertEquals("$2,000.00", cashIncomeValueReportData.getAmount());
        Assert.assertEquals("MP Cash", cashIncomeValueReportData.getName());
        Assert.assertEquals("Managed portfolio cash income accrued", cashIncomeValueReportData.getDescription());
        Assert.assertEquals("14 Jul 2015", cashIncomeValueReportData.getPaymentDate());

        IncomeValueReportData dividendIncomeValueReportData = investmentIncomeTypes.get(1).getIncomeValuesReportData().get(0);
        Assert.assertEquals("$40.00", dividendIncomeValueReportData.getAmount());
        Assert.assertEquals("20", dividendIncomeValueReportData.getQuantity());
        Assert.assertEquals("14 Jul 2015", dividendIncomeValueReportData.getPaymentDate());
        Assert.assertEquals("15 Jul 2015", dividendIncomeValueReportData.getExecutionDate());
        Assert.assertEquals("$10.00", dividendIncomeValueReportData.getIncomeRate());
        Assert.assertEquals("$4.00", dividendIncomeValueReportData.getFrankedDividend());
        Assert.assertEquals("$2.00", dividendIncomeValueReportData.getUnfrankedDividend());
        Assert.assertEquals("$2.00", dividendIncomeValueReportData.getFrankingCredit());

        List<IncomeValueReportData> cashIncomeTypes = investmentTypes.get(1).getIncomeValuesReportData();

        IncomeValueReportData cashIncomeValueReportData1 = cashIncomeTypes.get(0).getIncomeValuesReportData().get(0);
        Assert.assertEquals("Cash interest accrued", cashIncomeValueReportData1.getDescription());

        List<IncomeValueReportData> tdCashIncomeTypes = investmentTypes.get(3).getIncomeValuesReportData();

        IncomeValueReportData tdCashIncomeValueReportData1 = tdCashIncomeTypes.get(0).getIncomeValuesReportData().get(0);
        Assert.assertTrue(tdCashIncomeValueReportData1.getDescription().contains("Interest accrued"));

        List<IncomeValueReportData> tmpCashIncomeTypes = investmentTypes.get(2).getIncomeValuesReportData();

        IncomeValueReportData tmpCashIncomeValueReportData = tmpCashIncomeTypes.get(0).getIncomeValuesReportData().get(0);
        Assert.assertEquals("Tailored portfolio cash income accrued", tmpCashIncomeValueReportData.getDescription());
    }

    private IncomeValuesDto getTDData() {
        IncomeDetailsKey key = new IncomeDetailsKey("accountId", IncomeDetailsType.RECEIVED, new DateTime("2015-01-01"),
                new DateTime("2018-01-01"));

        List<IncomeDto> incomeValues = new ArrayList<>();

        TermDepositIncomeDto termDepositIncome = new TermDepositIncomeDto("BT Termdeposit", "BT", new DateTime("2017-02-20"),
                new DateTime("2018-02-20"), BigDecimal.valueOf(1000), "1 year", PaymentFrequency.AT_MATURITY.getDisplayName());

        incomeValues.add( new IncomeValueDto(termDepositIncome));

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
    public void testData_whenGetDataCalledForTD_reportProducesValuesMatchingDto() {
        IncomeValuesDto incomeValuesDto = getTDData();
        when(incomeDtoService.find(any(IncomeDetailsKey.class), any(ServiceErrors.class))).thenReturn(incomeValuesDto);
        Collection<?> result = incomeAccruedReport.getData(paramsObj, dataCollections);
        IncomeReportData incomeReportData = (IncomeReportData) result.iterator().next();
        Assert.assertNotNull(incomeReportData);
        Assert.assertEquals(1,  incomeReportData.getIncomeCategories().size());
        List<IncomeValueReportData> investmentIncomeTypes = incomeReportData.getIncomeCategories()
                .get(0).getIncomeValuesReportData();
        IncomeValueReportData panoTD = investmentIncomeTypes.get(0).getIncomeValuesReportData().get(0);
        Assert.assertEquals("Interest accrued: Maturing 20 Feb 2018", panoTD.getDescription());
    }
}
