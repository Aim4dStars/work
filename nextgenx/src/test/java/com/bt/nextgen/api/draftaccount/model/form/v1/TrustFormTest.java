package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.AbstractJsonObjectMapperTest;
import com.bt.nextgen.api.draftaccount.model.form.ITrustForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.TrustIdentityDocTypeEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.trust.TrustDetails;
import com.bt.nextgen.api.draftaccount.schemas.v1.trust.TrustVerification;
import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.bt.nextgen.api.draftaccount.model.form.v1.AddressFormTest.assertStandardAddress;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for the {@code TrustForm} class.
 */
public class TrustFormTest extends AbstractJsonObjectMapperTest<TrustDetails> {

    private ITrustForm trustForm;
    private ITrustForm corporateTrustForm;

    public TrustFormTest() {
        super(TrustDetails.class);
    }

    @Before
    public void initFormWithTrustComponents() throws IOException {
        initTrustForm("trust-individual-family-2");
        initCorporateTrustForm("trust-corporate-other");
    }

    private void initTrustForm(String resourceName) throws IOException {
        final TrustDetails trustDetails = readJsonResource(resourceName, "trustdetails");
        trustForm = new TrustForm(111, trustDetails);
    }

    private void initCorporateTrustForm(String resourceName) throws IOException {
        final TrustDetails trustDetails = readJsonResource(resourceName, "trustdetails");
        corporateTrustForm = new TrustForm(123, trustDetails);
    }

    @Test
    public void testCorporateTrustDetails(){
        assertThat(corporateTrustForm.getName(),is("UnregisteredTrust"));
        assertThat(corporateTrustForm.getSourceOfWealth(),is("Business profits"));
        assertThat(corporateTrustForm.getDescription(),is("unregmanagedinv"));
        assertThat(corporateTrustForm.getRegisteredForGST(),is(true));
        assertThat(corporateTrustForm.getIdentityDocument(),notNullValue());
        assertThat(corporateTrustForm.getIdentityDocument().getIdvDocument().getDocumentNumber(), is("4445566"));
        assertThat(corporateTrustForm.getIdentityDocument().getIdvDocument().getName(), is("dsdddd"));
        assertThat(corporateTrustForm.getIdentityDocument().getIdvDocument().getDocumentDate(), Matchers.is(XMLGregorianCalendarUtil.getXMLGregorianCalendar("10/10/2015", "dd/MM/yyyy")));
        assertThat(corporateTrustForm.getIdentityDocument().getIdvDocument().getVerifiedFrom(), is("original"));
        assertThat(corporateTrustForm.getIdentityDocument().getIdvDocument().getDocumentType(), is(TrustIdentityDocTypeEnum.LETTERIDV.toString()));
    }

    @Test
    public void testTrustDetails(){
        assertThat(trustForm.getTrustType().value(), is("family"));
        assertThat(trustForm.getDateOfRegistration(),notNullValue());
        assertThat(trustForm.getBusinessName(),is("asaasa"));
        assertThat(trustForm.getSourceOfWealth(),is("Additional Sources"));
        assertThat(trustForm.getABN(),is("33051775556"));
        assertThat(trustForm.getDescription(),nullValue());
        assertThat(trustForm.getRegulatoryBody(),nullValue());
        assertThat(trustForm.getRegisteredForGST(),is(true));
        assertThat(trustForm.getRegistrationState(),is("NSW"));
        assertThat(trustForm.getAdditionalSourceOfWealth(),is("sdsd"));
        assertThat(trustForm.getIdentityDocument(),notNullValue());
        assertThat(trustForm.getIdentityDocument().getIdvDocument().getDocumentNumber(), is("DOC12345"));
        assertThat(trustForm.getIdentityDocument().getIdvDocument().getName(), is("Trust Document"));
        assertThat(trustForm.getIdentityDocument().getIdvDocument().getDocumentDate(), Matchers.is(XMLGregorianCalendarUtil.getXMLGregorianCalendar("03/03/2010", "dd/MM/yyyy")));
        assertThat(trustForm.getIdentityDocument().getIdvDocument().getVerifiedFrom(), is("original"));
        assertThat(trustForm.getIdentityDocument().getIdvDocument().getDocumentType(), is(TrustIdentityDocTypeEnum.TRUSTDEED.toString()));

    }

    @Test
    public void testTrustTaxDetails(){
        assertThat(trustForm.getTaxDetails().getTaxFileNumber(),nullValue());
        assertThat(trustForm.getTaxDetails().getExemptionReason(),is("norfolk_island_res"));
    }

    @Test
    public void testTrustResidentialAddressDetails(){
        assertStandardAddress(trustForm.getRegisteredAddress(), "28", "West", "STREET", "NORTH SYDNEY", "NSW", "2060", "AU");
    }

    @Test
    public void testTrustIndustryDetails(){
        assertThat(trustForm.getAnzsicCode(),is("7111"));
        assertThat(trustForm.getIndustryUcmCode(), is("SIC 0348"));
    }

    @Test
    public void testOtherTypeTrust() throws IOException{
        initTrustForm("trust-corporate-other-2");
        assertThat(trustForm.getTrustType().value(), is("other"));
        assertThat(trustForm.getDescription(), is("testamentary"));
    }
}
