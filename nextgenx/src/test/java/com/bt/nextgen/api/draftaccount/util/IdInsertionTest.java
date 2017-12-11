package com.bt.nextgen.api.draftaccount.util;

import com.bt.nextgen.api.draftaccount.FormDataConstants;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class IdInsertionTest {
    @Test
    public void mergeIds_WhenThereIsMoreThanOneInvestor_ThenItShouldInsertIdsForEveryInvestor() throws Exception {
        Map<String, Object> formData = new HashMap<>();
        formData.put("accountType", "joint");

        Map<String, Object> investor1 = buildPerson();
        Map<String, Object> investor2 = buildPerson();

        List<Map<String, Object>> investors = Arrays.asList(investor1, investor2);

        formData.put("investors", investors);

        IdInsertion.mergeIds(formData);

        assertIdsInsertedStartingAtSequence(investor1, 1);
        assertIdsInsertedStartingAtSequence(investor2, 10);
    }
    @Test
    public void mergeIds_WhenThereIsADirector_ThenItShouldInsertIdsForTheDirector() throws Exception {
        Map<String, Object> formData = new HashMap<>();
        formData.put("accountType", "corporateSMSF");

        Map<String, Object> director = buildPerson();

        List<Map<String, Object>> directors = Arrays.asList(director);

        formData.put("directors", directors);

        IdInsertion.mergeIds(formData);

        assertIdsInsertedStartingAtSequence(director, 1);
    }

    @Test
    public void mergeIds_WhenThereAreSMSFDetails_ThenItShouldInsertACorrelationIDOnTheSMSFDetails() throws Exception {
        Map<String, Object> formData = new HashMap<>();
        formData.put("smsfdetails", new HashMap<>());
        IdInsertion.mergeIds(formData);
        assertThat((Map<String, Object>) formData.get("smsfdetails"), hasKey("correlationId"));
    }

    @Test
    public void mergeIds_WhenThereIsACompanyTrustee_ThenItShouldInsertACorrelationIDForAddressFieldsOnTheCompanyTrustee() throws Exception {
        Map<String, Object> formData = new HashMap<>();
        HashMap<Object, Object> companyTrustee = new HashMap<>();
        companyTrustee.put("companyoffice", new HashMap<>());
        companyTrustee.put("placeofbusiness", new HashMap<>());
        formData.put("companytrustee", companyTrustee);
        IdInsertion.mergeIds(formData);
        Map<String, Object> companyTrusteeResult = (Map<String, Object>) formData.get("companytrustee");
        assertThat(companyTrusteeResult, hasKey("correlationId"));
        assertThat((Map<String, Object>) companyTrusteeResult.get("companyoffice"), hasKey("correlationId"));
        assertThat((Map<String, Object>) companyTrusteeResult.get("placeofbusiness"), hasKey("correlationId"));
    }

    @Test
    public void mergeIds_WhenThereIsACompanyTrustee_ThenItShouldInsertACorrelationIDOnTheCompanyTrustee() throws Exception {
        Map<String, Object> formData = new HashMap<>();
        formData.put("companytrustee", new HashMap<>());
        IdInsertion.mergeIds(formData);
        assertThat((Map<String, Object>) formData.get("companytrustee"), hasKey("correlationId"));
    }

    @Test
    public void mergeIds_ShouldInsertCorrelationIDsSequentially() throws Exception {
        Map<String, Object> formData = new HashMap<>();
        formData.put("directors", Arrays.asList(buildPerson()));
        formData.put("smsfdetails", new HashMap<>());
        IdInsertion.mergeIds(formData);
        assertThat(((Map<String, Object>) formData.get("smsfdetails")).get("correlationId"), Is.<Object>is(IdInsertion.FIELDS.length + 1 + 1));
    }

    private void assertIdsInsertedStartingAtSequence(Map<String, Object> investor, int sequenceId) {
        assertThat((int) investor.get(FormDataConstants.FIELD_CORRELATION_ID), is(sequenceId));
        for (int i=0; i < IdInsertion.FIELDS.length; i++) {
            String field = IdInsertion.FIELDS[i];
            assertThat(getIdString(investor, field), is(sequenceId + 1 + i));
        }
    }

    @Test
    public void getJsonWith_WhenThereIsASingleInvestor_ThenItShouldInsertIdsIntoJson() throws IOException{
        Map<String,Object> formData = new HashMap<>();
        Map<String, Object> individualinvestordetailsMap = buildPerson();
        formData.put("investors", Arrays.asList(individualinvestordetailsMap));

        IdInsertion.mergeIds(formData);

        assertIdsInsertedStartingAtSequence(individualinvestordetailsMap, 1);
    }

    @Test
    public void mergeIds_WhenThereIsATrust_ThenItShouldInsertIdsForTheTrust() throws Exception {
        Map<String, Object> formData = new HashMap<>();
        formData.put("accountType", "corporateTrust");

        Map<String, Object> trustDetails = new HashMap <String, Object>();

        formData.put("trustdetails", trustDetails);

        IdInsertion.mergeIds(formData);

        assertNotNull(trustDetails.get(FormDataConstants.FIELD_CORRELATION_ID));
    }

    @Test
    public void mergeIds_WhenThereIsACompany_ThenItShouldInsertIdsForTheCompany() throws Exception {
        Map<String, Object> formData = new HashMap<>();
        formData.put("accountType", "company");

        Map<String, Object> companyDetails = new HashMap <String, Object>();

        formData.put("companydetails", companyDetails);

        IdInsertion.mergeIds(formData);

        assertNotNull(companyDetails.get(FormDataConstants.FIELD_CORRELATION_ID));
    }


    @Test
    public void mergeIds_WhenThereIsATrust_ThenItShouldInsertIdsForTheTrustAddress() throws Exception {
        Map<String, Object> formData = new HashMap<>();
        formData.put("accountType", "corporateTrust");

        Map<String, Object> trustDetails = new HashMap <String, Object>();
        trustDetails.put("address", new HashMap <String, Object>());
        formData.put("trustdetails", trustDetails);

        IdInsertion.mergeIds(formData);

        assertNotNull(((Map <String, Object>)trustDetails.get("address")).get(FormDataConstants.FIELD_CORRELATION_ID));
    }

    @Test
    public void mergeIds_WhenThereIsACompany_ThenItShouldInsertIdsForTheCompanyOffice() throws Exception {
        Map<String, Object> formData = new HashMap<>();
        formData.put("accountType", "company");

        Map<String, Object> companyDetails = new HashMap <String, Object>();
        companyDetails.put("companyoffice", new HashMap<String, Object>());
        formData.put("companydetails", companyDetails);

        IdInsertion.mergeIds(formData);

        assertNotNull(((Map<String, Object>) companyDetails.get("companyoffice")).get(FormDataConstants.FIELD_CORRELATION_ID));
    }

    @Test
    public void mergeIds_WhenThereIsACompany_ThenItShouldInsertIdsForTheCompanyPlaceOfBusiness() throws Exception {
        Map<String, Object> formData = new HashMap<>();
        formData.put("accountType", "company");

        Map<String, Object> companyDetails = new HashMap <String, Object>();
        companyDetails.put("placeofbusiness", new HashMap<String, Object>());
        formData.put("companydetails", companyDetails);

        IdInsertion.mergeIds(formData);

        assertNotNull(((Map<String, Object>) companyDetails.get("placeofbusiness")).get(FormDataConstants.FIELD_CORRELATION_ID));
    }



    private Map<String, Object> buildPerson() {
        Map<String,Object> investor = new HashMap<>();

        for (String field : IdInsertion.FIELDS) {
            investor.put(field, new HashMap<>());
        }

        return investor;
    }

    private int getIdString(Map<String, Object> map, String key) {
        return (int)((Map<String, Object>) map.get(key)).get(FormDataConstants.FIELD_CORRELATION_ID);
    }
}
