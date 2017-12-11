package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.AbstractJsonReaderTest;
import com.bt.nextgen.api.draftaccount.FormDataConstants;
import com.bt.nextgen.api.draftaccount.model.form.ClientApplicationFormFactory;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.api.draftaccount.FormDataConstants.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientApplicationFormDataConverterServiceTest extends AbstractJsonReaderTest {

    @InjectMocks
    ClientApplicationFormDataConverterService clientApplicationFormDataConverterService;

    @Mock
    UserProfileService userProfileService;
    @Mock
    ObjectMapper mapper;

    Map<String, Object> convertedFormData;

    @Before
    public void setUp() throws IOException, JSONException {
        Map<String, Object> formData = readJsonFromFile("individual_direct.json");
        convertedFormData = (Map<String, Object>) clientApplicationFormDataConverterService.convertFormDataForDirect(formData);
    }

    @Test
    public void verifyInvestorsSectionIsConvertedCorrectly() {
        List<Map<String, Object>> investors = (List<Map<String, Object>>) convertedFormData.get(FormDataConstants.FIELD_INVESTORS);
        Map<String, Object> investor = investors.get(0);

        assertThat((String) investor.get(FIELD_TITLE), is("MR"));
        assertThat((String) investor.get(FIELD_FIRSTNAME), is("HOMER"));
        assertThat((String) investor.get(FIELD_LASTNAME), is("SIMPSON"));
        assertThat((String) investor.get(FIELD_DATEOFBIRTH), is("01/01/1989"));
        assertThat((String) investor.get(FIELD_PREFERREDNAME), is("Foo"));
        assertThat((String) investor.get(FIELD_CIS_ID), is("1234567890"));
        assertThat((String) investor.get(FIELD_GENDER), is("male"));
        assertThat((String) investor.get(FIELD_TAXCOUNTRY), is("AU"));
        assertThat((String) investor.get(FIELD_TAXOPTION), is("Will provide Tax File Number later"));
        assertThat((String) investor.get(FIELD_PREFERREDCONTACT), is("mobile"));
        assertThat((String) investor.get(FIELD_USER_NAME), is("21142019"));
        assertTrue(((Map) investor.get(FIELD_MOBILE)).containsValue("0470528886"));
    }

    @Test
    public void verifyInvestorsResidentialAddressIsConvertedCorrectly() {
        List<Map<String, Object>> investors = (List<Map<String, Object>>) convertedFormData.get(FormDataConstants.FIELD_INVESTORS);
        Map<String, Object> investor = investors.get(0);
        Map<String, Object> resAddress = (Map<String, Object>) investor.get(FIELD_RESADDRESS);

        assertThat((Boolean) resAddress.get(FIELD_COMPONENTISED), is(false));
        assertThat((String) resAddress.get(FIELD_ADDRESS_LINE_1), is("55 market street"));
        assertThat((String) resAddress.get(FIELD_ADDRESS_LINE_2), is("Panorama Plaza"));
        assertThat((String) resAddress.get(FIELD_COUNTRY), is("AU"));
        assertThat((String) resAddress.get(FIELD_CITY), is("Sydney"));
        assertThat((String) resAddress.get(FIELD_PIN), is("2000"));
        assertThat((String) resAddress.get(FIELD_STATE), is("NSW"));
    }

    @Test
    public void verifyInvestorsPostalAddressIsConvertedCorrectly() {
        List<Map<String, Object>> investors = (List<Map<String, Object>>) convertedFormData.get(FormDataConstants.FIELD_INVESTORS);
        Map<String, Object> investor = investors.get(0);
        Map<String, Object> postalAddress = (Map<String, Object>) investor.get(FIELD_POSTALADDRESS);

        assertThat((Boolean) postalAddress.get(FIELD_COMPONENTISED), is(false));
        assertThat((String) postalAddress.get(FIELD_ADDRESS_LINE_1), is("55 market street"));
        assertThat((String) postalAddress.get(FIELD_ADDRESS_LINE_2), is("Panorama Plaza"));
        assertThat((String) postalAddress.get(FIELD_COUNTRY), is("AU"));
        assertThat((String) postalAddress.get(FIELD_CITY), is("Sydney"));
        assertThat((String) postalAddress.get(FIELD_PIN), is("2000"));
        assertThat((String) postalAddress.get(FIELD_STATE), is("NSW"));
    }

    @Test
    public void verifyAccountSettingsIsConvertedCorrectly() {
        Map<String, Object> accountSettings = (Map<String, Object>) convertedFormData.get(FormDataConstants.FIELD_ACCOUNTSETTINGS);
        List<Map<String, Object>> investorAccountSettings = (List<Map<String, Object>>) accountSettings.get(FormDataConstants.FIELD_INVESTOR_ACCOUNT_SETTINGS);
        Map<String, Object> investorAccountSetting = investorAccountSettings.get(0);

        assertThat((String) investorAccountSetting.get(FormDataConstants.FIELD_PAYMENT_SETTING), is("allpayments"));
        assertThat((String) accountSettings.get(FormDataConstants.FIELD_PROFESSIONALSPAYMENT), is("nopayments"));

    }

    @Test
    public void verifyLinkedAccountsIsConvertedCorrectly() {
        Map<String, Object> linkedAccount = (Map<String, Object>) convertedFormData.get(FormDataConstants.FIELD_LINKEDACCOUNTS);
        Map<String, Object> primaryLinkedAccount = (Map<String, Object>) linkedAccount.get(FormDataConstants.FIELD_PRIMARY_LINKED_ACCOUNT);

        assertThat((String) primaryLinkedAccount.get("accountname"), is("Cash Account 1"));
        assertThat((String) primaryLinkedAccount.get("bsb"), is("732095"));
        assertThat((String) primaryLinkedAccount.get("accountnumber"), is("596137"));
    }

    @Test
    public void verifyAccountTypeForFormData() {
        assertThat((String) convertedFormData.get(FormDataConstants.FIELD_ACCOUNT_TYPE), is("individual"));
        assertThat((String) convertedFormData.get(FormDataConstants.FIELD_APPLICATION_ORIGIN), is("WestpacLive"));
        assertThat((String) convertedFormData.get(FormDataConstants.FIELD_ADVICE_TYPE), is("NoAdvice"));
    }

    @Test
    public void verifyInvestmentChoiceInConvertedFormData() {
        assertNotNull(convertedFormData.get(FormDataConstants.FIELD_INVESTMENTOPTIONS));
    }

    @Test
    public void verifyStandardAddressData() throws IOException {


        Map<String, Object> formData = readJsonFromFile("individual_direct_with_standardAddress.json");
        convertedFormData = (Map<String, Object>) clientApplicationFormDataConverterService.convertFormDataForDirect(formData);


        List<Map<String, Object>> investors = (List<Map<String, Object>>) convertedFormData.get(FormDataConstants.FIELD_INVESTORS);
        Map<String, Object> investor = investors.get(0);
        Map<String, Object> postalAddress = (Map<String, Object>) investor.get(FIELD_POSTALADDRESS);

        assertThat((Boolean) postalAddress.get(FIELD_COMPONENTISED), is(true));
        assertThat((String) postalAddress.get(FIELD_UNIT_NUMBER), is("505"));
        assertThat((String) postalAddress.get(FIELD_STREET_TYPE), is("St"));
        assertThat((String) postalAddress.get(FIELD_COUNTRY), is("AU"));
        assertThat((String) postalAddress.get(FIELD_CITY), is("Sydney"));
        assertThat((String) postalAddress.get(FIELD_POSTCODE), is("2000"));
        assertThat((String) postalAddress.get(FIELD_STATE), is("NSW"));

    }

    @Test
    public void verifyClientKeyForExistingPanoramaInvestor() throws IOException {
        Map<String, Object> formData = readJsonFromFile("existing_individual_direct.json");
        when(userProfileService.getGcmId()).thenReturn("GCMID");
        convertedFormData = (Map<String, Object>) clientApplicationFormDataConverterService.convertFormDataForDirect(formData);

        IClientApplicationForm form  = ClientApplicationFormFactory.getNewClientApplicationForm(convertedFormData);
        final List<IPersonDetailsForm> existingPersonDetails = form.getExistingPersonDetails();
        final IPersonDetailsForm personDetailsForm = existingPersonDetails.get(0);
        assertThat(personDetailsForm.getClientKey(), is("F9C8CDF34248363B9B59D59FC7109A00C1DA20DDF0528501"));
        assertThat(personDetailsForm.getPanoramaNumber(), is("GCMID"));
    }
}
