package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.AbstractJsonObjectMapperTest;
import com.bt.nextgen.api.draftaccount.model.form.ICompanyForm;
import com.bt.nextgen.api.draftaccount.model.form.ITaxDetailsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.company.CompanyDetails;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.bt.nextgen.api.draftaccount.model.form.v1.AddressFormTest.assertStandardAddress;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@code CompanyForm} class.
 */
public class CompanyFormTest extends AbstractJsonObjectMapperTest<CompanyDetails> {

    private ICompanyForm companyForm;

    public CompanyFormTest() {
        super(CompanyDetails.class);
    }

    @Before
    public void initFormWithCompanyComponent() throws IOException {
        initCompanyForm("company-new-1");
    }

    private void initCompanyForm(String resourceName) throws IOException {
        final CompanyDetails companyDetails = readJsonResource(resourceName, "companydetails");
        companyForm = new CompanyForm(111, companyDetails);
    }

    @Test
    public void companyDetails() {
        assertThat(companyForm.getName(), is("Bastards Inc."));
        assertThat(companyForm.getAsicName(), is("Bastards Incorporated"));
        assertThat(companyForm.getACN(), is("000000019"));
        assertThat(companyForm.getABN(), is("83914571673"));
        assertThat(companyForm.getSourceOfWealth(), is("Additional Sources"));
        assertThat(companyForm.getAdditionalSourceOfWealth(), is("Profiteering"));
    }

    @Test
    public void taxDetails(){
        final ITaxDetailsForm taxDetails = companyForm.getTaxDetails();
        assertTrue(taxDetails.hasTaxFileNumber());
        assertFalse(taxDetails.hasExemptionReason());
        assertThat(taxDetails.getTaxFileNumber(),is("123456782"));
        assertThat(taxDetails.getTaxCountryCode(),is("AU"));
    }

    @Test
    public void registeredAddress() {
        assertStandardAddress(companyForm.getRegisteredAddress(), "34", "George", "STREET", "SYDNEY", "NSW", "2000", "AU");
    }

    @Test
    public void placeofBusiness(){
        assertStandardAddress(companyForm.getPlaceOfBusinessAddress(), "34", "George", "STREET", "SYDNEY", "NSW", "2000", "AU");
    }
}
