package com.bt.nextgen.api.termdeposit.service;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.asset.Term;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRateImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by M044020 on 2/08/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class TermDepositRateCalculatorCsvUtilsTest {

    @InjectMocks
    private TermDepositRateCalculatorCsvUtils utils;

    @Mock
    private CmsService cmsService;

    private List<TermDepositInterestRate> interestRateList;

    @Before
    public void setUp() throws Exception {
        interestRateList = new ArrayList<>();
        TermDepositInterestRateImpl.TermDepositInterestRateBuilder rateBuilder = new TermDepositInterestRateImpl
                .TermDepositInterestRateBuilder()
                .make().withAssetKey(AssetKey.valueOf("879485")).withIssuerId("10602").withIssuerName("St. George")
                .withWhiteLabelProductKey(ProductKey.valueOf("209457")).withDealerGroupKey(BrokerKey.valueOf("312314"))
                .withAccountStructureType(AccountStructureType.Individual).withTerm(new Term("3M"))
                .withPaymentFrequency(PaymentFrequency.AT_MATURITY).withRate(new BigDecimal("2.25"))
                .withRateAsPercentage(new BigDecimal("2.25")).withLowerLimit(new BigDecimal("0.00"))
                .withUpperLimit(new BigDecimal("20000")).withStartDate(DateTime.now())
                .withEndDate(DateTime.now().plusYears(2));
        interestRateList.add(rateBuilder.buildTermDepositRate());
        rateBuilder = rateBuilder.withRate(new BigDecimal("3.35")).withTerm(new Term("6M"));
        interestRateList.add(rateBuilder.buildTermDepositRate());
        rateBuilder = rateBuilder.withRate(new BigDecimal("2.35")).withLowerLimit(new BigDecimal("20000"))
                .withUpperLimit(new BigDecimal("50000")).withTerm(new Term("3M"));
        interestRateList.add(rateBuilder.buildTermDepositRate());
        rateBuilder = rateBuilder.withRate(new BigDecimal("4.15")).withLowerLimit(new BigDecimal("0"))
                .withUpperLimit(new BigDecimal("20000")).withTerm(new Term("3Y"))
                .withPaymentFrequency(PaymentFrequency.MONTHLY);
        interestRateList.add(rateBuilder.buildTermDepositRate());
        when(cmsService.getContent("10602_name")).thenReturn("St.George");
        when(cmsService.getContent("10602" + "_tdDisclaimer")).thenReturn("St.George Disclaimer\n");
        when(cmsService.getContent("10602" + "_tdFooter")).thenReturn("St.George Footer\n");
    }

    private Map<Term, Map<String, String>> getMonthlyDataMap() {
        Map<Term, Map<String, String>> dataMap = new HashMap<>();
        Map<String, String> rowMap = new HashMap<>();
        rowMap.put("5000-500000", "2.25");
        rowMap.put("500000-1000000", "2.25");
        rowMap.put("1000000-25000000", "2.35");
        dataMap.put(new Term("12M"), rowMap);
        rowMap = new HashMap<>();
        rowMap.put("5000-500000", "2.85");
        rowMap.put("500000-1000000", "2.85");
        rowMap.put("1000000-25000000", "2.85");
        dataMap.put(new Term("36M"), rowMap);
        rowMap = new HashMap<>();
        rowMap.put("5000-500000", "2.90");
        rowMap.put("500000-1000000", "2.90");
        rowMap.put("1000000-25000000", "2.95");
        dataMap.put(new Term("60M"), rowMap);
        return dataMap;
    }

    private Map<Term, Map<String, String>> getMaturityDataMap() {
        Map<Term, Map<String, String>> dataMap = new HashMap<>();
        Map<String, String> rowMap = new HashMap<>();
        rowMap.put("5000-500000", "2.10");
        rowMap.put("500000-1000000", "2.25");
        rowMap.put("1000000-25000000", "2.35");
        dataMap.put(new Term("3M"), rowMap);
        rowMap = new HashMap<>();
        rowMap.put("5000-500000", "2.12");
        rowMap.put("500000-1000000", "2.30");
        rowMap.put("1000000-25000000", "2.35");
        dataMap.put(new Term("3M"), rowMap);
        rowMap = new HashMap<>();
        rowMap.put("5000-500000", "2.15");
        rowMap.put("500000-1000000", "2.28");
        rowMap.put("1000000-25000000", "2.40");
        dataMap.put(new Term("6M"), rowMap);
        rowMap = new HashMap<>();
        rowMap.put("5000-500000", "2.20");
        rowMap.put("500000-1000000", "2.28");
        rowMap.put("1000000-25000000", "2.45");
        dataMap.put(new Term("12M"), rowMap);
        rowMap = new HashMap<>();
        rowMap.put("5000-500000", "3.00");
        rowMap.put("500000-1000000", "3.20");
        rowMap.put("1000000-25000000", "3.20");
        dataMap.put(new Term("36M"), rowMap);
        rowMap = new HashMap<>();
        rowMap.put("5000-500000", "2.90");
        rowMap.put("500000-1000000", "3.00");
        rowMap.put("1000000-25000000", "3.00");
        dataMap.put(new Term("60M"), rowMap);
        return dataMap;
    }

    @Test
    public void getTermDepositRatesCsv() throws Exception {
        String expected = "St.George Disclaimer\n" + "Maturity/Yearly rates\n" + "Bank,Term,20000-50000,0-20000,\n" +
                "St.George,3,2.35%,2.25%\n" + "St.George,6,,3.35%\n" + "\n" + "Monthly rates\n" + "Bank,Term,0-20000," +
                "\n" + "St.George,36,4.15%\n" + "\n" + "\n" + "St.George Footer\n";
        String result = utils.getTermDepositRatesCsv("10602", interestRateList);
        assertThat(result, is(expected));
    }
    @Test
    public void addRateForNullInterestRate() throws Exception {
        Map<Term, Map<String, String>> emptyMap = new HashMap<>();
        Map<Term, Map<String, String>> result = utils.addRate(emptyMap, null);
        assertThat(result, is(sameInstance(emptyMap)));
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void addRateForNullMap() throws Exception {
        Map<Term, Map<String, String>> result = utils.addRate(null, interestRateList.get(0));
        assertThat(result.size(), is(1));
        Map<String, String> rangeMap = result.get(new Term("3M"));
        assertThat(rangeMap.size(), is(1));
        assertThat(rangeMap.get("0-20000"), is("2.25%"));
    }

    @Test
    public void addRateForNewMap() throws Exception {
        Map<Term, Map<String, String>> result = utils
                .addRate(new HashMap<Term, Map<String, String>>(), interestRateList.get(0));
        assertThat(result.size(), is(1));
        Map<String, String> rangeMap = result.get(new Term("3M"));
        assertThat(rangeMap.size(), is(1));
        assertThat(rangeMap.get("0-20000"), is("2.25%"));
    }

    @Test
    public void addRateForMultiple() throws Exception {
        Map<Term, Map<String, String>> result = utils.addRate(null, interestRateList.get(0));
        result = utils.addRate(result, interestRateList.get(1));
        result = utils.addRate(result, interestRateList.get(2));
        assertThat(result.size(), is(2));
        Map<String, String> rangeMap = result.get(new Term("3M"));
        assertThat(rangeMap.size(), is(2));
        assertThat(rangeMap.get("0-20000"), is("2.25%"));
        assertThat(rangeMap.get("20000-50000"), is("2.35%"));
        rangeMap = result.get(new Term("6M"));
        assertThat(rangeMap.size(), is(1));
        assertThat(rangeMap.get("0-20000"), is("3.35%"));
    }

    @Test
    public void addRangeAndRatesForNullInterestRate() throws Exception {
        Map<String, String> emptyMap = new HashMap<>();
        Map<String, String> result = utils.addRangeAndRates(emptyMap, null);
        assertThat(result, is(sameInstance(emptyMap)));
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void addRangeAndRatesForNullMap() throws Exception {
        Map<String, String> result = utils.addRangeAndRates(null, interestRateList.get(0));
        assertThat(result.size(), is(1));
        assertThat(result.get("0-20000"), is("2.25%"));
    }

    @Test
    public void addRangeAndRatesForNewMap() throws Exception {
        Map<String, String> result = utils.addRangeAndRates(new HashMap<String, String>(), interestRateList.get(0));
        assertThat(result.size(), is(1));
        assertThat(result.get("0-20000"), is("2.25%"));
    }

    @Test
    public void addRangeAndRatesForMultiple() throws Exception {
        Map<String, String> result = utils.addRangeAndRates(new HashMap<String, String>(), interestRateList.get(0));
        result = utils.addRangeAndRates(result, interestRateList.get(2));
        assertThat(result.size(), is(2));
        assertThat(result.get("0-20000"), is("2.25%"));
        assertThat(result.get("20000-50000"), is("2.35%"));
    }

    @Test
    public void getRatesCsvForPaymentAtMaturity() throws Exception {
        String expected = "Bank,Term,5000-500000,500000-1000000,1000000-25000000,\n" + "St.George,3,2.12,2.30,2.35\n"
                + "St.George,36,3.00,3.20,3.20\n" + "St.George,6,2.15,2.28,2.40\n" + "St.George,12,2.20,2.28,2.45\n"
                + "St.George,60,2.90,3.00,3.00\n";
        Map<Term, Map<String, String>> dataMap = getMaturityDataMap();
        String result = utils.getRatesCsv("10602", dataMap);
        assertThat(result, is(expected));
    }

    @Test
    public void getRatesCsvForPaymentMonthly() throws Exception {
        String expected = "Bank,Term,5000-500000,500000-1000000,1000000-25000000,\n" + "St.George,36,2.85,2.85," +
                "2.85\n" + "St.George,12,2.25,2.25,2.35\n" + "St.George,60,2.90,2.90,2.95\n";
        Map<Term, Map<String, String>> dataMap = getMonthlyDataMap();
        String result = utils.getRatesCsv("10602", dataMap);
        assertThat(result, is(expected));
    }
}