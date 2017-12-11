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
import com.bt.nextgen.service.wrap.integration.income.ThirdPartyDividendIncomeImpl;
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
public class IncomeReceivedReportV2Test {

    @InjectMocks
    private IncomeReceivedReportV2 incomeReceivedReport;

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
        params.put(UriMappingConstants.END_DATE_PARAMETER_MAPPING, "2015-07-15");
        params.put(PARAM_INCOME_TYPE, "RECEIVED");

        paramsObj.put(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "114129E83B02BCA4D4B3245CEFB3778405F178638F9731DD");
        paramsObj.put(UriMappingConstants.START_DATE_PARAMETER_MAPPING, "2015-07-14");
        paramsObj.put(UriMappingConstants.END_DATE_PARAMETER_MAPPING, "2015-07-15");
        paramsObj.put(PARAM_INCOME_TYPE, "RECEIVED");

        ContentDto contentDto = Mockito.mock(ContentDto.class);
        Mockito.when(contentDto.getContent()).thenReturn("disclaimervalue");
        Mockito.when(contentService.find(Mockito.any(ContentKey.class), Mockito.any(ServiceErrors.class))).thenReturn(contentDto);
        
        Mockito.when(
                optionsService.hasFeature(Mockito.any(OptionKey.class), Mockito.any(AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(Boolean.TRUE);

        CashIncomeDto cashIncomeDto = new CashIncomeDto("MP Cash", "", new DateTime("2015-07-14"), new BigDecimal(2000),
                IncomeType.INTEREST);

        DividendIncomeImpl dividendIncome1 = new DividendIncomeImpl();
        ThirdPartyDividendIncomeImpl dividendIncome2 = new ThirdPartyDividendIncomeImpl();
        DividendIncomeImpl dividendIncome3 = new DividendIncomeImpl();

        dividendIncome1.setQuantity(new BigDecimal(20));
        dividendIncome1.setAmount(new BigDecimal(40));
        dividendIncome1.setPaymentDate(new DateTime("2015-07-14"));
        dividendIncome1.setExecutionDate(new DateTime("2015-07-15"));
        dividendIncome1.setIncomeRate(new BigDecimal(10));
        dividendIncome1.setFrankedDividend(new BigDecimal(4));
        dividendIncome1.setUnfrankedDividend(new BigDecimal(2));
        dividendIncome1.setFrankingCredit(new BigDecimal(2));

        dividendIncome2.setQuantity(new BigDecimal(20));
        dividendIncome2.setAmount(new BigDecimal(40));
        dividendIncome2.setPaymentDate(new DateTime("2015-07-14"));
        dividendIncome2.setExecutionDate(new DateTime("2015-07-15"));
        dividendIncome2.setIncomeRate(new BigDecimal(10));
        dividendIncome2.setFrankedDividend(new BigDecimal(4));
        dividendIncome2.setUnfrankedDividend(new BigDecimal(2));

        dividendIncome3.setQuantity(new BigDecimal(20));
        dividendIncome3.setAmount(new BigDecimal(40));
        dividendIncome3.setPaymentDate(new DateTime("2015-07-14"));
        dividendIncome3.setExecutionDate(new DateTime("2015-07-15"));
        dividendIncome3.setIncomeRate(new BigDecimal(10));
        dividendIncome3.setFrankedDividend(new BigDecimal(4));
        dividendIncome3.setUnfrankedDividend(new BigDecimal(2));

        IncomeDto incomeDto = new IncomeValueDto(cashIncomeDto);

        DividendIncomeDto dividendIncomeDto1 = new DividendIncomeDto("BHP Billiton1", "BHP", dividendIncome1);
        DividendIncomeDto dividendIncomeDto2 = new DividendIncomeDto("Wrap BHP Billiton", "BHP", dividendIncome2);
        DividendIncomeDto dividendIncomeDto3 = new DividendIncomeDto("BHP Billiton2", "BHP", dividendIncome3);

        dividendIncomeDto2.setWrapIncome(true);
        IncomeDto mpDividendIncomeDto1 = new IncomeValueDto(dividendIncomeDto1);
        IncomeDto wrapDividendIncomeDto = new IncomeValueDto(dividendIncomeDto2);
        IncomeDto mpDividendIncomeDto2 = new IncomeValueDto(dividendIncomeDto3);

        List<IncomeDto> incomeDtos = new ArrayList<>();
        incomeDtos.add(mpDividendIncomeDto1);
        incomeDtos.add(mpDividendIncomeDto2);
        incomeDtos.add(wrapDividendIncomeDto);

        IncomeDto cashIncomeTypeDto = new InvestmentIncomeTypeDto(IncomeType.CASH, Collections.singletonList(incomeDto),
                new BigDecimal(2000));

        IncomeDto dividendIncomeTypeDto = new InvestmentIncomeTypeDto(IncomeType.DIVIDEND,
                incomeDtos, new BigDecimal(2000));

        List<IncomeDto> incomes = new ArrayList<>();
        incomes.add(cashIncomeTypeDto);
        incomes.add(dividendIncomeTypeDto);

        IncomeDto investmentTypeDto = new InvestmentTypeDto(AssetType.MANAGED_PORTFOLIO, incomes, new BigDecimal(2000));
        income = new IncomeValuesDto(new IncomeDetailsKey("114129E83B02BCA4D4B3245CEFB3778405F178638F9731DD",
                IncomeDetailsType.ACCRUED, new DateTime("2015-07-14"), new DateTime("2015-07-15")),
                Collections.singletonList(investmentTypeDto));
    }

    @Test
    public final void testGetDisclaimer() {
        Assert.assertEquals("disclaimervalue", incomeReceivedReport.getDisclaimer());
    }

    @Test
    public final void testGetStartDate() {
        Assert.assertEquals(ReportFormatter.format(ReportFormat.SHORT_DATE, new DateTime("2015-07-14")),
                incomeReceivedReport.getStartDate(params));
    }

    @Test
    public final void testGetEndDate() {
        Assert.assertEquals(ReportFormatter.format(ReportFormat.SHORT_DATE, new DateTime("2015-07-15")),
                incomeReceivedReport.getEndDate(params));
    }

    @Test
    public final void testReportTitle() {
        Assert.assertEquals("Income received", incomeReceivedReport.getReportTitle());
    }

    @Test
    public final void testReportSubTitle() {
        Assert.assertEquals("From 14 Jul 2015 to 15 Jul 2015", incomeReceivedReport.getReportSubTitle(params));
    }

    @Test
    public final void testSummaryDescription() {
        Assert.assertEquals("Total income received", incomeReceivedReport.getSummaryDescription(params, dataCollections));
    }

    @Test
    public final void testData_whenGetDataCalled_reportProducesValuesMatchingDto() {

        Mockito.when(incomeDtoService.find(Mockito.any(IncomeDetailsKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                income);
        Collection<?> result = incomeReceivedReport.getData(paramsObj, dataCollections);

        Assert.assertEquals(true, incomeReceivedReport.getIsIncomeBreakDown(params, dataCollections));
        IncomeReportData incomeReportData = (IncomeReportData) result.iterator().next();
        IncomeSummaryReportData summaryReportData = incomeReportData.getIncomeSummaryReportData();
        Assert.assertEquals(incomeReportData.getIncomeCategories().size(), income.getChildren().size());
        Assert.assertEquals(summaryReportData.getInterestTotal(), "$2,000.00");
        Assert.assertEquals(summaryReportData.getDividendTotal(), "$120.00");
        Assert.assertEquals(summaryReportData.getDistributionTotal(), "$0.00");
        Assert.assertEquals(summaryReportData.getIncomeTotal(), "$2,120.00");
        Assert.assertEquals(summaryReportData.getFrankedDividendTotal(), "$12.00");
        Assert.assertEquals(summaryReportData.getUnfrankedDividendTotal(), "$6.00");

        List<IncomeValueReportData> investmentTypes = incomeReportData.getIncomeCategories();
        List<IncomeValueReportData> investmentIncomeTypes = investmentTypes.get(0).getIncomeValuesReportData();

        IncomeValueReportData cashIncomeValueReportData = investmentIncomeTypes.get(0).getIncomeValuesReportData().get(0);
        Assert.assertEquals("$2,000.00", cashIncomeValueReportData.getAmount());
        Assert.assertEquals("MP Cash", cashIncomeValueReportData.getName());
        Assert.assertEquals("14 Jul 2015", cashIncomeValueReportData.getPaymentDate());

        IncomeValueReportData dividendIncomeValueReportData1 = investmentIncomeTypes.get(1).getIncomeValuesReportData().get(0);
        Assert.assertEquals("$40.00", dividendIncomeValueReportData1.getAmount());
        Assert.assertEquals("20", dividendIncomeValueReportData1.getQuantity());
        Assert.assertEquals("14 Jul 2015", dividendIncomeValueReportData1.getPaymentDate());
        Assert.assertEquals("15 Jul 2015", dividendIncomeValueReportData1.getExecutionDate());
        Assert.assertEquals("$10.00", dividendIncomeValueReportData1.getIncomeRate());
        Assert.assertEquals("$4.00", dividendIncomeValueReportData1.getFrankedDividend());
        Assert.assertEquals("$2.00", dividendIncomeValueReportData1.getUnfrankedDividend());
        Assert.assertEquals("$2.00", dividendIncomeValueReportData1.getFrankingCredit());

        IncomeValueReportData dividendIncomeValueReportData2 = investmentIncomeTypes.get(1).getIncomeValuesReportData().get(1);
        Assert.assertEquals("$40.00", dividendIncomeValueReportData2.getAmount());
        Assert.assertEquals("20", dividendIncomeValueReportData2.getQuantity());
        Assert.assertEquals("14 Jul 2015", dividendIncomeValueReportData2.getPaymentDate());
        Assert.assertEquals("15 Jul 2015", dividendIncomeValueReportData2.getExecutionDate());
        Assert.assertEquals("$10.00", dividendIncomeValueReportData2.getIncomeRate());
        Assert.assertEquals("$4.00", dividendIncomeValueReportData2.getFrankedDividend());
        Assert.assertEquals("$2.00", dividendIncomeValueReportData2.getUnfrankedDividend());
        Assert.assertEquals("$0.00", dividendIncomeValueReportData2.getFrankingCredit());

        IncomeValueReportData dividendIncomeValueReportData3 = investmentIncomeTypes.get(1).getIncomeValuesReportData().get(2);
        Assert.assertEquals("$40.00", dividendIncomeValueReportData3.getAmount());
        Assert.assertEquals("20", dividendIncomeValueReportData3.getQuantity());
        Assert.assertEquals("14 Jul 2015", dividendIncomeValueReportData3.getPaymentDate());
        Assert.assertEquals("15 Jul 2015", dividendIncomeValueReportData3.getExecutionDate());
        Assert.assertEquals("$10.00", dividendIncomeValueReportData3.getIncomeRate());
        Assert.assertEquals("$4.00", dividendIncomeValueReportData3.getFrankedDividend());
        Assert.assertEquals("$2.00", dividendIncomeValueReportData3.getUnfrankedDividend());
        Assert.assertEquals("-", dividendIncomeValueReportData3.getFrankingCredit());
    }

    private IncomeValuesDto getTDData() {
        IncomeDetailsKey key = new IncomeDetailsKey("accountId", IncomeDetailsType.RECEIVED, new DateTime("2015-01-01"),
                new DateTime("2018-01-01"));

        List<IncomeDto> incomeValues = new ArrayList<>();
        TermDepositIncomeDto wrapTermDepositIncome = new TermDepositIncomeDto("Wrap Termdeposit", "BT", new DateTime("2017-02-20"),
                new DateTime("2018-02-20"), BigDecimal.valueOf(1000), "1 year", PaymentFrequency.AT_MATURITY.getDisplayName());
        wrapTermDepositIncome.setWrapTermDeposit(true);

        TermDepositIncomeDto termDepositIncome = new TermDepositIncomeDto("BT Termdeposit", "BT", new DateTime("2017-02-20"),
                new DateTime("2018-02-20"), BigDecimal.valueOf(1000), "1 year", PaymentFrequency.AT_MATURITY.getDisplayName());

        incomeValues.add( new IncomeValueDto(wrapTermDepositIncome));
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
        Collection<?> result = incomeReceivedReport.getData(paramsObj, dataCollections);

        Assert.assertEquals(true, incomeReceivedReport.getIsIncomeBreakDown(params, dataCollections));
        IncomeReportData incomeReportData = (IncomeReportData) result.iterator().next();


        Assert.assertNotNull(incomeReportData);
        Assert.assertEquals(1,  incomeReportData.getIncomeCategories().size());
        List<IncomeValueReportData> investmentIncomeTypes = incomeReportData.getIncomeCategories()
                .get(0).getIncomeValuesReportData();
        IncomeValueReportData wrapTD = investmentIncomeTypes.get(0).getIncomeValuesReportData().get(0);
        IncomeValueReportData panoTD = investmentIncomeTypes.get(0).getIncomeValuesReportData().get(1);
        Assert.assertEquals("Interest received", wrapTD.getDescription());
        Assert.assertEquals("Interest received: Maturing 20 Feb 2018", panoTD.getDescription());
    }
}
