package com.bt.nextgen.api.draftaccount.model.form;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;

import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TrustFormTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void hasIDVDocument_shouldReturnTrue_WhenTrustTypeIsFamily() throws Exception {
        ITrustForm trustForm = getTrustFormForType(ITrustForm.TrustType.FAMILY.value());
        assertTrue(trustForm.hasIDVDocument());
    }

    @Test
    public void hasIDVDocument_shouldReturnTrue_WhenTrustTypeIsOther() throws Exception {
        ITrustForm trustForm = getTrustFormForType(ITrustForm.TrustType.OTHER.value());
        assertTrue(trustForm.hasIDVDocument());
    }

    @Test
    public void hasIDVDocument_shouldReturnFalse_WhenTrustTypeIsRegulated() throws Exception {
        ITrustForm trustForm = getTrustFormForType(ITrustForm.TrustType.REGULATED.value());
        assertFalse(trustForm.hasIDVDocument());
    }

    @Test
    public void hasIDVDocument_shouldReturnFalse_WhenTrustTypeIsRegisteredMIS() throws Exception {
        ITrustForm trustForm = getTrustFormForType(ITrustForm.TrustType.REGISTERED_MIS.value());
        assertFalse(trustForm.hasIDVDocument());
    }

    @Test
    public void hasIDVDocument_shouldReturnFalse_WhenTrustTypeIsGovtSuper() throws Exception {
        ITrustForm trustForm = getTrustFormForType(ITrustForm.TrustType.GOVT_SUPER.value());
        assertFalse(trustForm.hasIDVDocument());
    }

    @Test
    public void getIDVRegulatoryBody_shouldReturnAPRA_WhenTrustTypeIsRegulated() throws Exception {
        ITrustForm trustForm = getTrustFormForType(ITrustForm.TrustType.REGULATED.value());
        assertThat(trustForm.getRegulatoryBody(), is(IOrganisationForm.RegulatoryBody.APRA));
    }

    @Test
    public void getIDVRegulatoryBody_shouldReturnAPRA_WhenTrustTypeIsRegisteredMIS() throws Exception {
        ITrustForm trustForm = getTrustFormForType(ITrustForm.TrustType.REGISTERED_MIS.value());
        assertThat(trustForm.getRegulatoryBody(), is(IOrganisationForm.RegulatoryBody.ASIC));
    }

    @Test
    public void getIDVRegulatoryBody_shouldReturnNull_WhenTrustTypeIsFamily() throws Exception {
        ITrustForm trustForm = getTrustFormForType(ITrustForm.TrustType.FAMILY.value());
        assertThat(trustForm.getRegulatoryBody(), is(nullValue()));
    }

    @Test
    public void getIDVRegulatoryBody_shouldReturnNull_WhenTrustTypeIsOther() throws Exception {
        ITrustForm trustForm = getTrustFormForType(ITrustForm.TrustType.OTHER.value());
        assertThat(trustForm.getRegulatoryBody(), is(nullValue()));
    }

    @Test
    public void getIDVRegulatoryBody_shouldReturnAPRA_WhenTrustTypeIsGovtSuper() throws Exception {
        ITrustForm trustForm = getTrustFormForType(ITrustForm.TrustType.GOVT_SUPER.value());
        assertThat(trustForm.getRegulatoryBody(), is(IOrganisationForm.RegulatoryBody.APRA));
    }

    @Test
    public void getIDVURL_shouldReturnAPRAURL_WhenTrustTypeIsRegulated() {
        ITrustForm trustForm = getTrustFormForType(ITrustForm.TrustType.REGULATED.value());
        assertThat(trustForm.getIdvURL(), is("www.apra.gov.au"));
    }

    @Test
    public void getIDVDocIssuer_shouldThrowUnsupportedException() throws Exception {
        ITrustForm trustForm = getTrustFormForType(ITrustForm.TrustType.REGULATED.value());
        exception.expect(UnsupportedOperationException.class);
        exception.expectMessage("No Doc issuer for the trust form");
        trustForm.getIDVDocIssuer();
    }

    @Test
    public void getIDVURL_shouldReturnASICURL_WhenTrustTypeIsRegisteredMIS() {
        ITrustForm trustForm = getTrustFormForType(ITrustForm.TrustType.REGISTERED_MIS.value());
        assertThat(trustForm.getIdvURL(), is("ASIC"));
    }

    @Test
    public void getIDVURL_shouldReturnNull_WhenTrustTypeIsFamily() throws Exception {
        ITrustForm trustForm = getTrustFormForType(ITrustForm.TrustType.FAMILY.value());
        assertThat(trustForm.getIdvURL(), is(nullValue()));
    }

    @Test
    public void getIDVURL_shouldReturnNull_WhenTrustTypeIsOther() throws Exception {
        ITrustForm trustForm = getTrustFormForType(ITrustForm.TrustType.OTHER.value());
        assertThat(trustForm.getIdvURL(), is(nullValue()));
    }

    @Test
    public void getIDVURL_shouldReturnSearchURLFromForm_WhenTrustTypeIsGovtSuper() throws Exception {
        HashMap<String, Object> trustDetails = new HashMap<>();
        trustDetails.put("trusttype", ITrustForm.TrustType.GOVT_SUPER.value());
        trustDetails.put("nameofwebsite", "www.govsuperlookup.gov.au");

        ITrustForm trustForm = TrustFormFactory.getNewTrustForm(1, trustDetails);
        assertThat(trustForm.getIdvURL(), is("www.govsuperlookup.gov.au"));
    }

    public ITrustForm getTrustFormForType(String trustType) {
        return TrustFormFactory.getNewTrustForm(1, singletonMap("trusttype", (Object) trustType));
    }

    @Test
    public void getSettlorofTrustType_shouldReturnOrgSettTrustEnum() throws Exception{
        HashMap<String, Object> trustDetails = new HashMap<>();
        trustDetails.put("trusttype", ITrustForm.TrustType.OTHER.value());
        trustDetails.put("settloroftrust", "organisation");
        trustDetails.put("organisationName","TestOrgName");
        ITrustForm trustForm = TrustFormFactory.getNewTrustForm(1, trustDetails);
        assertThat(trustForm.getSettlorOfTrust(),is(ITrustForm.SettlorofTrustType.ORGANISATION));

    }

    @Test
    public void getSettlorofTrustType_shouldReturnPsnSettTrustEnum() throws Exception{
        HashMap<String, Object> trustDetails = new HashMap<>();
        trustDetails.put("trusttype", ITrustForm.TrustType.OTHER.value());
        trustDetails.put("settloroftrust", "individual");
        trustDetails.put("title","Mr");
        trustDetails.put("firstname","testFirst");
        trustDetails.put("middlename","testMiddle");
        trustDetails.put("lastname","testLast");
        ITrustForm trustForm = TrustFormFactory.getNewTrustForm(1, trustDetails);
        assertThat(trustForm.getSettlorOfTrust(),is(ITrustForm.SettlorofTrustType.INDIVIDUAL));

    }

    @Test
    public void getTrustTypeAvaloqReferenceName() throws Exception{
        HashMap<String, Object> trustDetails = new HashMap<>();
        trustDetails.put("trusttype", ITrustForm.TrustType.OTHER.value());
        trustDetails.put("trustdescription","family");
        ITrustForm trustForm = TrustFormFactory.getNewTrustForm(1, trustDetails);
        assertThat(ITrustForm.StandardTrustDescription.fromString(trustForm.getDescription()).value(),is("btfg$discrny_trust"));

    }


}
