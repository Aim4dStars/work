package com.bt.nextgen.api.termdeposit.service;

import com.bt.nextgen.api.termdeposit.model.Brand;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorDto;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by M044020 on 31/07/2017.
 */
public class TermDepositCalculatorConverterTest {

    private TermDepositCalculatorConverter converter = new TermDepositCalculatorConverter();
    private List<TermDepositInterestRate> termDepositInterestRates;
    private List<Brand> brands;
    @Before
    public void init() {
        brands = new ArrayList<>();
        brands.add(new Brand("10602"));
        brands.add(new Brand("40179"));
        brands.add(new Brand("10603"));
        brands.add(new Brand("10604"));
        termDepositInterestRates = new ArrayList<>();
        TermDepositInterestRateImpl.TermDepositInterestRateBuilder rateBuilder = new TermDepositInterestRateImpl
                .TermDepositInterestRateBuilder()
                .make().withAssetKey(AssetKey.valueOf("879485")).withIssuerId("10602").withIssuerName("St. George")
                .withWhiteLabelProductKey(ProductKey.valueOf("209457")).withDealerGroupKey(BrokerKey.valueOf("312314"))
                .withAccountStructureType(AccountStructureType.Individual).withTerm(new Term("3M"))
                .withPaymentFrequency(PaymentFrequency.AT_MATURITY).withRate(new BigDecimal("2.25"))
                .withRateAsPercentage(new BigDecimal("2.25")).withLowerLimit(new BigDecimal("0.00"))
                .withUpperLimit(new BigDecimal("20000")).withStartDate(DateTime.now())
                .withEndDate(DateTime.now().plusYears(2));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("6M"));
        rateBuilder.withRate(new BigDecimal("2.50")).withRateAsPercentage(new BigDecimal("2.50"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("1Y"));
        rateBuilder.withRate(new BigDecimal("2.85")).withRateAsPercentage(new BigDecimal("2.85"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.MONTHLY);
        rateBuilder.withRate(new BigDecimal("2.75")).withRateAsPercentage(new BigDecimal("2.75"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("3Y"));
        rateBuilder.withRate(new BigDecimal("3.25")).withRateAsPercentage(new BigDecimal("3.25"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.ANNUALLY);
        rateBuilder.withRate(new BigDecimal("3.35")).withRateAsPercentage(new BigDecimal("3.35"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("5Y"));
        rateBuilder.withRate(new BigDecimal("4.15")).withRateAsPercentage(new BigDecimal("4.15"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.MONTHLY);
        rateBuilder.withRate(new BigDecimal("4.05")).withRateAsPercentage(new BigDecimal("4.05"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withIssuerId("40179").withIssuerName("BT");
        rateBuilder.withTerm(new Term("3M"));
        rateBuilder.withRate(new BigDecimal("2.28")).withRateAsPercentage(new BigDecimal("2.28"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("6M"));
        rateBuilder.withRate(new BigDecimal("2.55")).withRateAsPercentage(new BigDecimal("2.55"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("1Y"));
        rateBuilder.withRate(new BigDecimal("2.95")).withRateAsPercentage(new BigDecimal("2.95"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.MONTHLY);
        rateBuilder.withRate(new BigDecimal("2.65")).withRateAsPercentage(new BigDecimal("2.65"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("3Y"));
        rateBuilder.withRate(new BigDecimal("3.25")).withRateAsPercentage(new BigDecimal("3.25"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.ANNUALLY);
        rateBuilder.withRate(new BigDecimal("3.35")).withRateAsPercentage(new BigDecimal("3.35"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("5Y"));
        rateBuilder.withRate(new BigDecimal("4.15")).withRateAsPercentage(new BigDecimal("4.15"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.MONTHLY);
        rateBuilder.withRate(new BigDecimal("4.05")).withRateAsPercentage(new BigDecimal("4.05"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withIssuerId("10603").withIssuerName("Bank of Melbourne");
        rateBuilder.withTerm(new Term("3M"));
        rateBuilder.withRate(new BigDecimal("2.28")).withRateAsPercentage(new BigDecimal("2.28"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("6M"));
        rateBuilder.withRate(new BigDecimal("2.25")).withRateAsPercentage(new BigDecimal("2.25"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("1Y"));
        rateBuilder.withRate(new BigDecimal("2.95")).withRateAsPercentage(new BigDecimal("2.95"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.MONTHLY);
        rateBuilder.withRate(new BigDecimal("2.65")).withRateAsPercentage(new BigDecimal("2.65"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("3Y"));
        rateBuilder.withRate(new BigDecimal("3.25")).withRateAsPercentage(new BigDecimal("3.25"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.ANNUALLY);
        rateBuilder.withRate(new BigDecimal("3.35")).withRateAsPercentage(new BigDecimal("3.35"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("5Y"));
        rateBuilder.withRate(new BigDecimal("4.35")).withRateAsPercentage(new BigDecimal("4.35"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.MONTHLY);
        rateBuilder.withRate(new BigDecimal("4.05")).withRateAsPercentage(new BigDecimal("4.05"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withIssuerId("10604").withIssuerName("Westpac");
        rateBuilder.withTerm(new Term("3M"));
        rateBuilder.withRate(new BigDecimal("2.30")).withRateAsPercentage(new BigDecimal("2.30"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("6M"));
        rateBuilder.withRate(new BigDecimal("2.55")).withRateAsPercentage(new BigDecimal("2.55"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("1Y"));
        rateBuilder.withRate(new BigDecimal("2.95")).withRateAsPercentage(new BigDecimal("2.95"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.MONTHLY);
        rateBuilder.withRate(new BigDecimal("2.65")).withRateAsPercentage(new BigDecimal("2.65"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("3Y"));
        rateBuilder.withRate(new BigDecimal("3.25")).withRateAsPercentage(new BigDecimal("3.25"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.ANNUALLY);
        rateBuilder.withRate(new BigDecimal("3.35")).withRateAsPercentage(new BigDecimal("3.35"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("5Y"));
        rateBuilder.withRate(new BigDecimal("4.15")).withRateAsPercentage(new BigDecimal("4.15"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.MONTHLY);
        rateBuilder.withRate(new BigDecimal("4.05")).withRateAsPercentage(new BigDecimal("4.05"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
    }

    @Test
    public void convertBrandNull() throws Exception {
        TermDepositCalculatorDto result = converter
                .toTermDepositCalculatorDto(null, termDepositInterestRates, new BigDecimal("10000.00"));
        assertThat(result, is(nullValue()));
    }

    @Test
    public void convertTermDepositRateNull() throws Exception {
        TermDepositCalculatorDto result = converter
                .toTermDepositCalculatorDto(brands, null, new BigDecimal("10000.00"));
        assertThat(result, is(nullValue()));
    }

    @Test
    public void convertAmountNull() throws Exception {
        TermDepositCalculatorDto result = converter.toTermDepositCalculatorDto(brands, termDepositInterestRates, null);
        assertThat(result, is(nullValue()));
    }

    @Test
    public void convertAllNull() throws Exception {
        TermDepositCalculatorDto result = converter.toTermDepositCalculatorDto(null, null, null);
        assertThat(result, is(nullValue()));
    }

    @Test
    public void convertBrandEmpty() throws Exception {
        TermDepositCalculatorDto result = converter
                .toTermDepositCalculatorDto(new ArrayList<Brand>(), termDepositInterestRates,
                        new BigDecimal("10000.00"));
        assertThat(result, is(nullValue()));
    }

    @Test
    public void convertTermDepositRateEmpty() throws Exception {
        TermDepositCalculatorDto result = converter
                .toTermDepositCalculatorDto(brands, new ArrayList<TermDepositInterestRate>(),
                        new BigDecimal("10000.00"));
        assertThat(result, is(nullValue()));
    }

    @Test
    public void convertAll() throws Exception {
        TermDepositCalculatorDto result = converter
                .toTermDepositCalculatorDto(brands, termDepositInterestRates, new BigDecimal("10000.00"));
        assertThat(result.getTermDepositBankRates().size(), is(4));
        assertThat(result.getTermDepositBankRates().get(0).getTermMap().size(), is(5));
        assertThat(result.getTermDepositBankRates().get(1).getTermMap().size(), is(5));
        assertThat(result.getTermDepositBankRates().get(2).getTermMap().size(), is(5));
        assertThat(result.getTermDepositBankRates().get(3).getTermMap().size(), is(5));
        assertThat(result.getTermDepositBankRates().get(2).getTermMap().get(new Term("3M")).isBestRateFlag(), is(false));
        assertThat(result.getTermDepositBankRates().get(2).getTermMap().get(new Term("6M")).isBestRateFlag(), is(false));
        assertThat(result.getTermDepositBankRates().get(3).getTermMap().get(new Term("6M")).isBestRateFlag(), is(false));
    }
}