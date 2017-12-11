package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.model.form.FeesFormFactory;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IFeesForm;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ns.btfin_com.party.intermediary.v1_1.FeesType;
import ns.btfin_com.sharedservices.common.fee.v1_2.FeeClassificationType;
import ns.btfin_com.sharedservices.common.fee.v1_2.FeeInfoType;
import ns.btfin_com.sharedservices.common.fee.v1_2.FlatFeeType;
import ns.btfin_com.sharedservices.common.fee.v1_2.SlidingFeeType;
import ns.btfin_com.sharedservices.common.fee.v1_2.TierType;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.btfin.panorama.onboarding.helper.FeeHelper.UPPER_BOUND;
import static com.btfin.panorama.onboarding.helper.FeeHelper.fees;
import static ns.btfin_com.product.common.investmentproduct.v1_1.ProductType.CASH_FUNDS;
import static ns.btfin_com.product.common.investmentproduct.v1_1.ProductType.MANAGED_FUND;
import static ns.btfin_com.product.common.investmentproduct.v1_1.ProductType.MANAGED_PORTFOLIO;
import static ns.btfin_com.product.common.investmentproduct.v1_1.ProductType.SECURITIES_LISTED;
import static ns.btfin_com.product.common.investmentproduct.v1_1.ProductType.TERM_DEPOSIT;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class FeesBuilderTest {

    private FeesBuilder feesBuilder = new FeesBuilder();

    @Test
    public void shouldReturnFeesTypeWithTheOngoingFees() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String feesWithJustOngoingFees = "{\n" +
                "        \"ongoingFees\": {\n" +
                "            \"feesComponent\": [\n" +
                "                {\n" +
                "                    \"cpiindex\": true,\n" +
                "                    \"amount\": \"1234.00\",\n" +
                "                    \"label\": \"Dollar fee component\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"managedFund\": \"0.50\",\n" +
                "                    \"listedSecurities\": \"0.75\",\n" +
                "                    \"managedPortfolio\": \"1.00\",\n" +
                "                    \"termDeposit\": \"2.00\",\n" +
                "                    \"cash\": \"3.00\",\n" +
                "                    \"label\": \"Percentage fee component\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"type\": \"Ongoing advice fee\"\n" +
                "        }," +
                "\"estamount\": \"100.00\"" +
                "}";
        Map<String, Object> feesMap = mapper.readValue(feesWithJustOngoingFees, new TypeReference<Map<String, Object>>() {
        });

        IFeesForm feesForm = FeesFormFactory.getNewFeesForm(feesMap);
        FeesType feesType = fees(feesBuilder.getFees(feesForm, IClientApplicationForm.AccountType.INDIVIDUAL ));
        assertNotNull(feesType);
        assertThat(feesType.getFee(), hasSize(2));
        FeeInfoType ongoingFees = feesType.getFee().get(1);
        assertEquals(feesType.getFee().get(0).getFeeClassification(), FeeClassificationType.ESTABLISHMENT);
        assertEquals(ongoingFees.getFeeClassification(), FeeClassificationType.ONGOING_ADVICE);
        assertThat(ongoingFees.getFeeMethod(), hasSize(6));
        assertEquals(ongoingFees.getFeeMethod().get(0).getFlatFee().getAppliedFeeAmount(), new BigDecimal("1234.00"));
        assertTrue(ongoingFees.getFeeMethod().get(0).getFlatFee().isIndexFactor());

        assertEquals(ongoingFees.getFeeMethod().get(1).getPercentageFee().getAppliedFeeRate(), new BigDecimal("1.00"));
        assertEquals(ongoingFees.getFeeMethod().get(1).getPercentageFee().getInvestmentProduct(), MANAGED_PORTFOLIO);

        assertEquals(ongoingFees.getFeeMethod().get(2).getPercentageFee().getAppliedFeeRate(), new BigDecimal("2.00"));
        assertEquals(ongoingFees.getFeeMethod().get(2).getPercentageFee().getInvestmentProduct(), TERM_DEPOSIT);

        assertEquals(ongoingFees.getFeeMethod().get(3).getPercentageFee().getAppliedFeeRate(), new BigDecimal("3.00"));
        assertEquals(ongoingFees.getFeeMethod().get(3).getPercentageFee().getInvestmentProduct(), CASH_FUNDS);

        assertEquals(ongoingFees.getFeeMethod().get(4).getPercentageFee().getAppliedFeeRate(), new BigDecimal("0.50"));
        assertEquals(ongoingFees.getFeeMethod().get(4).getPercentageFee().getInvestmentProduct(), MANAGED_FUND);

        assertEquals(ongoingFees.getFeeMethod().get(5).getPercentageFee().getAppliedFeeRate(), new BigDecimal("0.75"));
        assertEquals(ongoingFees.getFeeMethod().get(5).getPercentageFee().getInvestmentProduct(), SECURITIES_LISTED);
    }

    @Test
    public void shouldReturnFeesTypeWithLicenseeFee() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String feesWithJustLicenseeFees = "{\n" +
                "        \"licenseeFees\": {\n" +
                "            \"feesComponent\": [\n" +
                "                {\n" +
                "                    \"cpiindex\": false,\n" +
                "                    \"amount\": \"999.00\",\n" +
                "                    \"label\": \"Dollar fee component\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"managedFund\": \"true\",\n" +
                "                    \"managedPortfolio\": \"true\",\n" +
                "                    \"listedSecurities\": \"true\",\n" +
                "                    \"cash\": \"true\",\n" +
                "                    \"termDeposit\": \"true\",\n" +
                "                    \"slidingScaleFeeTier\": [\n" +
                "                        {\n" +
                "                            \"lowerBound\": \"0.00\",\n" +
                "                            \"upperBound\": \"600.00\",\n" +
                "                            \"percentage\": \"12.00\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"lowerBound\": \"600.00\",\n" +
                "                            \"upperBound\": \"800.00\",\n" +
                "                            \"percentage\": \"24.00\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"lowerBound\": \"800.00\",\n" +
                "                            \"percentage\": \"33.00\"\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"label\": \"Sliding scale fee component\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"type\": \"Licensee advice fee\"\n" +
                "        },\n" +
                "        \"estamount\": \"100.00\"\n" +
                "    }";
        Map<String, Object> feesMap = mapper.readValue(feesWithJustLicenseeFees, new TypeReference<Map<String, Object>>() {
        });

        IFeesForm feesForm = FeesFormFactory.getNewFeesForm(feesMap);
        FeesType feesType = fees(feesBuilder.getFees(feesForm, IClientApplicationForm.AccountType.INDIVIDUAL));
        assertNotNull(feesType);
        assertThat(feesType.getFee(), hasSize(2));
        FeeInfoType licenseeFees = feesType.getFee().get(1);
        assertEquals(feesType.getFee().get(0).getFeeClassification(), FeeClassificationType.ESTABLISHMENT);
        assertEquals(licenseeFees.getFeeClassification(), FeeClassificationType.LICENSEE_ADVICE);
        assertThat(licenseeFees.getFeeMethod(), hasSize(2));
        assertFalse(licenseeFees.getFeeMethod().get(0).getFlatFee().isIndexFactor());
        SlidingFeeType slidingFeeType = licenseeFees.getFeeMethod().get(1).getSlidingFee();
        List<TierType> tiers = slidingFeeType.getTier();

        assertThat(slidingFeeType.getInvestmentProduct(),
                containsInAnyOrder(MANAGED_FUND, MANAGED_PORTFOLIO, CASH_FUNDS, TERM_DEPOSIT, SECURITIES_LISTED));

        assertThat(tiers, hasSize(3));
        assertEquals(tiers.get(0).getAppliedFeeRate(), new BigDecimal("12.00"));
        assertEquals(tiers.get(0).getFromAmount(), new BigDecimal("0.00"));
        assertEquals(tiers.get(0).getToAmount(), new BigDecimal("600.00"));

        assertEquals(tiers.get(1).getAppliedFeeRate(), new BigDecimal("24.00"));
        assertEquals(tiers.get(1).getFromAmount(), new BigDecimal("600.00"));
        assertEquals(tiers.get(1).getToAmount(), new BigDecimal("800.00"));

        assertEquals(tiers.get(2).getAppliedFeeRate(), new BigDecimal("33.00"));
        assertEquals(tiers.get(2).getFromAmount(), new BigDecimal("800.00"));
        assertEquals(tiers.get(2).getToAmount(), UPPER_BOUND);
    }

    @Test
    public void shouldNotReturnEmptyFees() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String feesNotAllFilled = "{\n" +
                "        \"estamount\": \"100.00\"\n" +
                "    }";
        Map<String, Object> feesMap = mapper.readValue(feesNotAllFilled, new TypeReference<Map<String, Object>>() {
        });

        IFeesForm feesForm = FeesFormFactory.getNewFeesForm(feesMap);
        FeesType feesType = fees(feesBuilder.getFees(feesForm, IClientApplicationForm.AccountType.INDIVIDUAL));
        assertNotNull(feesType);
        assertThat(feesType.getFee(), hasSize(1));
        assertEquals(feesType.getFee().get(0).getFeeClassification(), FeeClassificationType.ESTABLISHMENT);
        FlatFeeType flatFee = feesType.getFee().get(0).getFeeMethod().get(0).getFlatFee();
        assertNotNull(flatFee);
        assertEquals(false, flatFee.isIndexFactor());
        assertEquals(new BigDecimal("100.00"), flatFee.getAppliedFeeAmount());
    }
}
