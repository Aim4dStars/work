package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.AbstractJsonObjectMapperTest;
import com.bt.nextgen.api.draftaccount.schemas.v1.smsf.SmsfDetails;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for the {@code SmsfForm} class.
 */
public class SmsfFormTest extends AbstractJsonObjectMapperTest<SmsfDetails> {

    private SmsfForm smsfForm;

    public SmsfFormTest() {
        super(SmsfDetails.class);
    }

    @Before
    public void initFormWithSmsfComponents() throws IOException {
        initSmsfForm("smsf-existing-corporate-2");
    }

    private void initSmsfForm(String resourceName) throws IOException {
        final SmsfDetails smsfDetails = readJsonResource(resourceName, "smsfdetails");
        smsfForm = new SmsfForm(111, smsfDetails);
    }

    @Test
    public void testSmsfResidentialAddress(){
        assertThat(smsfForm.getRegisteredAddress().getStreetName(),is("West"));
        assertThat(smsfForm.getRegisteredAddress().getStreetType(),is("STREET"));
        assertThat(smsfForm.getRegisteredAddress().getState(),is("NSW"));
        assertThat(smsfForm.getRegisteredAddress().getPostcode(),is("2060"));
        assertThat(smsfForm.getRegisteredAddress().getSuburb(),is("NORTH SYDNEY"));
        assertThat(smsfForm.getRegisteredAddress().getCountry(),is("AU"));
    }

    @Test
    public void testSmsfDetails(){
        assertThat(smsfForm.getABN(),is("34234423424242"));
        assertThat(smsfForm.getRegistrationState(),is("NSW"));
        assertThat(smsfForm.getRegisteredForGST(),is(true));
        assertThat(smsfForm.getSourceOfWealth(),is("Additional Sources"));
        assertThat(smsfForm.getAdditionalSourceOfWealth(),is("dsdsds"));
        assertThat(smsfForm.getName(),is("cvxvv"));
        assertThat(smsfForm.getRegistrationState(),is("NSW"));
        assertThat(smsfForm.getIDVDocIssuer(),is("www.superfundlookup.gov.au"));
    }

    @Test
    public void testSmsfTaxDetails(){
        assertThat(smsfForm.getTaxDetails().getExemptionReason(),nullValue());
        assertThat(smsfForm.getTaxDetails().getTaxFileNumber(),nullValue());
    }

    @Test
    public void testSmsfIndustryDetails(){
        assertThat(smsfForm.getAnzsicCode(),is("7340"));
        assertThat(smsfForm.getIndustryUcmCode(),is("SIC 0358"));
    }
}
