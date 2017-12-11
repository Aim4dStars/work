package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.AbstractJsonObjectMapperTest;
import com.bt.nextgen.api.draftaccount.model.form.IFeeComponentForm;
import com.bt.nextgen.api.draftaccount.model.form.IFeeComponentTier;
import com.bt.nextgen.api.draftaccount.model.form.IFeesForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.fees.Fees;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

/**
 * Tests for the {@code FeesForm} implementation class.
 */
public class FeesFormTest extends AbstractJsonObjectMapperTest<Fees> {

    private IFeesForm feesForm;

    public FeesFormTest() {
        super(Fees.class);
    }

    @Before
    public void initFormWithFeesComponent() throws IOException {
        initFees("company-new-1", "fees");
    }

    @Test
    public void getEstablishmentFee() {
        assertEquals(new BigDecimal("77803.40"), feesForm.getEstablishmentFee());
    }

    @Test
    public void hasOngoingFees() {
        assertTrue(feesForm.hasOngoingFees());
    }

    @Test
    public void hasLicenseeFees() {
        assertTrue(feesForm.hasLicenseeFees());
    }

    @Test
    public void getOngoingFees() {
        final List<IFeeComponentForm> components = feesForm.getOngoingFeesComponent().getElements();
        assertThat(components, hasSize(2));

        final IFeeComponentForm flatFee = components.get(0);
        assertTrue(flatFee.isCpiIndexed());
        assertEquals("95844.12", flatFee.getAmount());
        assertEquals("Dollar fee component", flatFee.getLabel());

        final IFeeComponentForm percentage = components.get(1);
        assertEquals("2.00", percentage.getListedSecurities());
        assertEquals("1.95", percentage.getManagedFund());
        assertEquals("1.75", percentage.getManagedPortfolio());
        assertEquals("1.50", percentage.getTermDeposit());
        assertEquals("1.00", percentage.getCashFunds());
        assertEquals("Percentage fee component", percentage.getLabel());
    }

    @Test
    public void getLicenceFees() {
        final List<IFeeComponentForm> components = feesForm.getLicenseeFeesComponent().getElements();
        assertThat(components, hasSize(2));

        final IFeeComponentForm flatFee = components.get(0);
        assertFalse(flatFee.isCpiIndexed());
        assertEquals("96552.01", flatFee.getAmount());
        assertEquals("Dollar fee component", flatFee.getLabel());

        final IFeeComponentForm tiered = components.get(1);
        assertTrue(tiered.isForListedSecurities());
        assertFalse(tiered.isForManagedFund());
        assertTrue(tiered.isForManagedPortfolio());
        assertFalse(tiered.isForTermDeposit());
        assertTrue(tiered.isForCash());

        final List<IFeeComponentTier> tiers = tiered.getSlidingScaleFeeTiers();
        assertThat(tiers, hasSize(5));
        assertTier(tiers.get(0), "0.00", "1000000.00", "5.00");
        assertTier(tiers.get(1), "1000000.00", "2000000.00", "4.00");
        assertTier(tiers.get(2), "2000000.00", "5000000.00", "3.00");
        assertTier(tiers.get(3), "5000000.00", "10000000.00", "2.00");
        assertTier(tiers.get(4), "10000000.00", "", "1.00");
        assertEquals("Sliding scale fee component", tiered.getLabel());
    }

    @Test
    public void noFees() throws IOException {
        initFees("joint-empty", "fees");
        assertEquals(0, BigDecimal.ZERO.compareTo(feesForm.getEstablishmentFee()));
        assertFalse(feesForm.hasOngoingFees());
        assertFalse(feesForm.hasLicenseeFees());
        assertThat(feesForm.getOngoingFeesComponent().getElements(), empty());
        assertThat(feesForm.getLicenseeFeesComponent().getElements(), empty());
    }

    private void initFees(String resourceName, String jsonPath) throws IOException {
        final Fees fees = readJsonResource(resourceName, jsonPath);
        feesForm = new FeesForm(fees);
    }

    private static void assertTier(IFeeComponentTier tier, String lower, String upper, String percentage) {
        assertEquals(lower, tier.getLowerBound());
        assertEquals(upper, tier.getUpperBound());
        assertEquals(percentage, tier.getPercentage());
    }
}
