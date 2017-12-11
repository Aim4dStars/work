package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.FormDataConstants;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ClientApplicationFormTest {
    @Test
    public void getDirectors_itShouldReturnTheDirectors() throws Exception {
        Map formData = new HashMap();
        Map director = new HashMap<>();
        director.put("firstname", "Sam");
        formData.put("directors", Arrays.asList(director));

        formData.put("accountsettings", getAccountSettingsForDirectors(1));
        formData.put("shareholderandmembers", getRolesForDirectors(1, "yes", "no", null));
        formData.put("accountType", "corporateSMSF");
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);

        assertThat(form.getDirectors().size(), is(1));
        assertThat(form.getDirectors().get(0).getFirstName(), is("Sam"));
        assertThat(((DirectorDetailsForm) form.getDirectors().get(0)).getRole(), is(IOrganisationForm.OrganisationRole.DIRECTOR));
    }

    @Test
    public void getDirectors_ForCorporateSmsf_ShouldReturnTheDirectorsWithAdditionalRoles() throws Exception {
        Map formData = new HashMap();
        Map director = new HashMap<>();
        director.put("firstname", "Sam");
        formData.put("directors", Arrays.asList(director));

        formData.put("accountsettings", getAccountSettingsFoIndividualOrCorporateSmsf(1, "nopayments", "yes"));
        formData.put("shareholderandmembers", getRolesForDirectors(1, "yes", "yes", null));
        formData.put("accountType", "corporateSMSF");
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);

        DirectorDetailsForm directorDetailsForm = (DirectorDetailsForm) form.getDirectors().get(0);
        assertThat(form.getDirectors().size(), is(1));
        assertThat(directorDetailsForm.getFirstName(), is("Sam"));
        assertThat(directorDetailsForm.getRole(), is(IOrganisationForm.OrganisationRole.DIRECTOR));
        assertThat(directorDetailsForm.isMember(), is(true));
        assertThat(directorDetailsForm.isShareholder(), is(true));
        assertThat(directorDetailsForm.isBeneficiary(), is(false));
    }

    @Test
    public void getTrustees_ForIndividualSmsf_ShouldReturnTheTrusteesWithAdditionalRoles() throws Exception {
        Map formData = new HashMap();
        Map director = new HashMap<>();
        director.put("firstname", "Sam");
        formData.put("trustees", Arrays.asList(director));

        formData.put("accountsettings", getAccountSettingsFoIndividualOrCorporateSmsf(1, "nopayments", "yes"));
        formData.put("shareholderandmembers", getRolesForDirectors(1, null, "yes", null));
        formData.put("accountType", "IndividualSMSF");
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);

        IExtendedPersonDetailsForm extendedPersonDetailsForm = form.getTrustees().get(0);
        assertThat(form.getTrustees().size(), is(1));
        assertThat(extendedPersonDetailsForm.getFirstName(), is("Sam"));
        assertThat(extendedPersonDetailsForm.isMember(), is(true));
        assertThat(extendedPersonDetailsForm.isShareholder(), is(false));
        assertThat(extendedPersonDetailsForm.isBeneficiary(), is(false));
    }

    @Test
    public void getDirectors_ForCorporateTrust_ShouldReturnTheDirectorsWithAdditionalRoles() throws Exception {
        Map formData = new HashMap();
        Map director = new HashMap<>();
        director.put("firstname", "Sam");
        formData.put("directors", Arrays.asList(director));

        formData.put("accountsettings", getAccountSettingsFoIndividualOrCorporateSmsf(1, "nopayments", "yes"));
        formData.put("shareholderandmembers", getRolesForDirectors(1, "yes", null, "yes"));

        formData.put("accountType", "corporateTrust");
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);

        DirectorDetailsForm directorDetailsForm = (DirectorDetailsForm) form.getDirectors().get(0);
        assertThat(form.getDirectors().size(), is(1));
        assertThat(directorDetailsForm.getFirstName(), is("Sam"));
        assertThat(directorDetailsForm.getRole(), is(IOrganisationForm.OrganisationRole.DIRECTOR));
        assertThat(directorDetailsForm.isShareholder(), is(true));
        assertThat(directorDetailsForm.isBeneficiary(), is(true));

    }

    @Test
    public void getDirectors_ForCorporateTrust_ShouldReturnCMADetails() throws Exception {
        Map formData = new HashMap();
        Map director = new HashMap<>();
        director.put("firstname", "Sam");
        formData.put("directors", Arrays.asList(director));

        formData.put("accountsettings", getAccountSettingsFoIndividualOrCorporateSmsf(1, "nopayments", "yes"));

        Map trust = new HashMap<>();
        trust.put("personalinvestmententity",true);
        trust.put("trusttype","family");
        formData.put("trustdetails",trust);

        formData.put("accountType", "corporateTrust");
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        assertThat(form.getTrust().getPersonalInvestmentEntity(), is(true));

    }

    @Test
    public void getDirectorsSecretariesSignatories_itShouldReturnAllPeople() throws Exception {
        Map formData = new HashMap();
        Map director = new HashMap<>();
        director.put("firstname", "Sam");
        formData.put("directors", Arrays.asList(director));

        formData.put("accountsettings", getAccountSettingsForDirectors(1));
        formData.put("shareholderandmembers", getRolesForDirectors(1, "yes", "no", null));
        formData.put("accountType", "company");
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);

        List<IExtendedPersonDetailsForm> directorsSecretiariesSignatories = form.getDirectorsSecretariesSignatories();
        assertThat(directorsSecretiariesSignatories.size(), is(1));
        assertThat(directorsSecretiariesSignatories.get(0).getFirstName(), is("Sam"));
        assertThat(((DirectorDetailsForm) directorsSecretiariesSignatories.get(0)).getRole(), is(IOrganisationForm.OrganisationRole.SIGNATORY));
    }

    private Map<String, Object> getAccountSettingsForInvestors(int noOfInvestors) {
        Map<String, Object> accountSettings = new HashMap<>();
        List investorsSettingsList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < noOfInvestors; i++) {
            Map investorSetting = new HashMap<>();
            investorSetting.put("paymentSetting", "nopayments");
            investorSetting.put("isApprover", "false");

            investorsSettingsList.add(investorSetting);
        }
        accountSettings.put("primarycontact", "0");
        accountSettings.put("investorAccountSettings", investorsSettingsList);
        return accountSettings;
    }

    private Map<String, Object> getAccountSettingsForDirectors(int noOfDirectors) {
        Map<String, Object> accountSettings = new HashMap<>();
        List investorsSettingsList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < noOfDirectors; i++) {
            Map investorSetting = new HashMap<>();
            investorSetting.put("paymentSetting", "nopayments");
            investorSetting.put("isApprover", "true");
            investorSetting.put("role", "signatory");

            investorsSettingsList.add(investorSetting);
        }
        accountSettings.put("investorAccountSettings", investorsSettingsList);
        return accountSettings;
    }

    private Map<String, Object> getAccountSettingsFoIndividualOrCorporateSmsf(int noOfDirectors, String paymentSettings, String isApprover) {
        Map<String, Object> accountSettings = new HashMap<>();
        List investorsSettingsList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < noOfDirectors; i++) {
            Map investorSetting = new HashMap<>();
            investorSetting.put("paymentSetting", paymentSettings);
            investorSetting.put("isApprover", isApprover);
            investorsSettingsList.add(investorSetting);
        }
        accountSettings.put("investorAccountSettings", investorsSettingsList);
        return accountSettings;
    }

    private Map<String, Object> getRolesForDirectors(int noOfDirectors, String isShareHolder, String isMember, String isBeneficiary) {
        Map<String, Object> shareholderAndMembers = new HashMap<>();
        List<Map<String, Object>> investorsWithRoles = new ArrayList<>();
        for (int i = 0; i < noOfDirectors; i++) {
            Map<String, Object> investorWithRoles = new HashMap<>();

            if (isShareHolder != null) {
                investorWithRoles.put("isShareholder", isShareHolder);
            }

            if (isMember != null) {
                investorWithRoles.put("isMember", isMember);
            }

            if (isBeneficiary != null) {
                investorWithRoles.put("isBeneficiary", isBeneficiary);
            }

            investorsWithRoles.add(investorWithRoles);
        }
        shareholderAndMembers.put("investorsWithRoles", investorsWithRoles);
        return shareholderAndMembers;
    }

    @Test
    public void getTrustees_returnsListOfTrustees() {
        Map formData = new HashMap();
        Map trustees = new HashMap<>();
        formData.put("accountType", "individualSMSF");
        trustees.put("firstname", "Serious Sam");
        formData.put("trustees", Arrays.asList(trustees));
        formData.put("accountsettings", getAccountSettingsForDirectors(1));

        HashMap<String, Object> investorWithRole = new HashMap<>();
        investorWithRole.put("isMember", true);

        HashMap<String, Object> members = new HashMap<>();
        members.put("investorsWithRoles", Arrays.asList(investorWithRole));
        formData.put("shareholderandmembers", members);
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);

        assertThat(form.getTrustees().size(), is(1));
        assertThat(form.getTrustees().get(0).getFirstName(), is("Serious Sam"));
    }

    @Test
    public void getInvestors_WhenThereIsOnlyOneInvestor_ThenItShouldReturnAListWithOnlyThatInvestor() throws Exception {
        Map formData = new HashMap();
        Map individualInvestorDetails = new HashMap<>();
        individualInvestorDetails.put("firstname", "Sam");
        formData.put("investors", Arrays.asList(individualInvestorDetails));
        formData.put("accountType", "individual");
        formData.put("accountsettings", getAccountSettingsForInvestors(1));
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);

        assertThat(form.getInvestors().size(), is(1));
        assertThat(form.getInvestors().get(0).getFirstName(), is("Sam"));
    }

    @Test
    public void getInvestorsShouldSetPrimaryContactFlagOnInvestorDetailsForm() throws Exception {
        Map formData = new HashMap();

        formData.put("accountType", "joint");
        formData.put("investors", Arrays.asList(new HashMap<>(), new HashMap<>()));
        Map<String, Object> accountSettings = getAccountSettingsForInvestors(2);

        accountSettings.put("primarycontact", "1");
        formData.put("accountsettings", accountSettings);

        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);

        List<IExtendedPersonDetailsForm> investors = form.getInvestors();
        assertThat(investors.size(), is(2));
        assertThat(investors.get(0).isPrimaryContact(), is(false));
        assertThat(investors.get(1).isPrimaryContact(), is(true));

    }

    @Test
    public void getInvestorsShouldSetPaymentSettingsOnInvestorDetailsForm() throws Exception {
        Map formData = new HashMap();

        formData.put("accountType", "joint");
        formData.put("investors", Arrays.asList(new HashMap<>(), new HashMap<>()));

        Map<String, Object> accountSettings = new HashMap<>();
        List investorsSettingsList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 2; i++) {
            Map investorSetting = new HashMap<>();
            investorSetting.put("paymentSetting", "nopayments");
            investorSetting.put("isApprover", "false");

            investorsSettingsList.add(investorSetting);
        }
        accountSettings.put("investorAccountSettings", investorsSettingsList);
        formData.put("accountsettings", accountSettings);
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);

        List<IExtendedPersonDetailsForm> investors = form.getInvestors();
        assertThat(investors.size(), is(2));
        assertThat(investors.get(0).getPaymentSetting(), is(PaymentAuthorityEnum.NOPAYMENTS));
        assertThat(investors.get(1).getPaymentSetting(), is(PaymentAuthorityEnum.NOPAYMENTS));
    }

    @Test
    public void getDirectorsShouldSetPrimaryContactFlagOnInvestorDetailsForm() throws Exception {
        Map formData = new HashMap();

        formData.put("accountType", "corporateSMSF");
        formData.put("directors", Arrays.asList(new HashMap<>(), new HashMap<>()));
        Map<String, Object> accountSettings = getAccountSettingsForDirectors(2);

        accountSettings.put("primarycontact", "1");
        formData.put("accountsettings", accountSettings);
        formData.put("shareholderandmembers", getRolesForDirectors(2, "yes", "no", null));
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);

        List<IExtendedPersonDetailsForm> directors = form.getDirectors();
        assertThat(directors.size(), is(2));
        assertThat(directors.get(0).isPrimaryContact(), is(false));
        assertThat(directors.get(1).isPrimaryContact(), is(true));
    }

    @Test
    public void getDirectorsSecretariesSignatoriesShouldSetPrimaryContactFlagOnInvestorDetailsForm() throws Exception {
        Map formData = new HashMap();

        formData.put("accountType", "company");
        formData.put("directors", Arrays.asList(new HashMap<>(), new HashMap<>()));
        Map<String, Object> accountSettings = getAccountSettingsForDirectors(2);

        accountSettings.put("primarycontact", "1");
        formData.put("accountsettings", accountSettings);
        formData.put("shareholderandmembers", getRolesForDirectors(2, "yes", "no", null));
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);

        List<IExtendedPersonDetailsForm> directors = form.getDirectorsSecretariesSignatories();
        assertThat(directors.size(), is(2));
        assertThat(directors.get(0).isPrimaryContact(), is(false));
        assertThat(directors.get(1).isPrimaryContact(), is(true));
    }

    @Test
    public void getDirectorsShouldSetPaymentSettingsOnInvestorDetailsForm() throws Exception {
        Map formData = new HashMap();

        formData.put("accountType", "corporateSMSF");
        formData.put("directors", Arrays.asList(new HashMap<>(), new HashMap<>()));

        Map<String, Object> accountSettings = new HashMap<>();
        List investorsSettingsList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 2; i++) {
            Map investorSetting = new HashMap<>();
            if (i == 1) {
                investorSetting.put("paymentSetting", "allpayments");
            } else {
                investorSetting.put("paymentSetting", "nopayments");
            }
            investorSetting.put("isApprover", "false");

            investorsSettingsList.add(investorSetting);
        }
        accountSettings.put("investorAccountSettings", investorsSettingsList);

        formData.put("accountsettings", accountSettings);
        formData.put("shareholderandmembers", getRolesForDirectors(2, "yes", "no", null));
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);

        List<IExtendedPersonDetailsForm> directors = form.getDirectors();
        assertThat(directors.size(), is(2));
        assertThat(directors.get(0).getPaymentSetting(), is(PaymentAuthorityEnum.NOPAYMENTS));
        assertThat(directors.get(1).getPaymentSetting(), is(PaymentAuthorityEnum.ALLPAYMENTS));
    }

    @Test
    public void getDirectorsSecretariesSignatoriesShouldSetPaymentSettingsOnInvestorDetailsForm() throws Exception {
        Map formData = new HashMap();

        formData.put("accountType", "company");
        formData.put("directors", Arrays.asList(new HashMap<>(), new HashMap<>()));

        Map<String, Object> accountSettings = new HashMap<>();
        List investorsSettingsList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 2; i++) {
            Map investorSetting = new HashMap<>();
            investorSetting.put("paymentSetting", "nopayments");
            investorSetting.put("isApprover", "false");
            investorSetting.put("role", "secretary");

            investorsSettingsList.add(investorSetting);
        }
        accountSettings.put("investorAccountSettings", investorsSettingsList);

        formData.put("accountsettings", accountSettings);
        formData.put("shareholderandmembers", getRolesForDirectors(2, "yes", "no", null));
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);

        List<IExtendedPersonDetailsForm> directors = form.getDirectorsSecretariesSignatories();
        assertThat(directors.size(), is(2));
        assertThat(directors.get(0).getPaymentSetting(), is(PaymentAuthorityEnum.NOPAYMENTS));
        assertThat(directors.get(1).getPaymentSetting(), is(PaymentAuthorityEnum.NOPAYMENTS));
    }

    @Test
    public void getDirectorsShouldSetRoleDirectorOnInvestorDetailsForm() throws Exception {
        Map formData = new HashMap();

        formData.put("accountType", "corporateSMSF");
        formData.put("directors", Arrays.asList(new HashMap<>(), new HashMap<>()));

        Map<String, Object> accountSettings = new HashMap<>();
        List investorsSettingsList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 2; i++) {
            Map investorSetting = new HashMap<>();
            investorSetting.put("paymentSetting", "nopayments");
            investorSetting.put("isApprover", "false");
            investorSetting.put("role", "signatory");

            investorsSettingsList.add(investorSetting);
        }
        accountSettings.put("investorAccountSettings", investorsSettingsList);

        formData.put("accountsettings", accountSettings);
        formData.put("shareholderandmembers", getRolesForDirectors(2, "yes", "no", null));
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);

        List<IExtendedPersonDetailsForm> directors = form.getDirectors();
        assertThat(directors.size(), is(2));
        assertThat(((DirectorDetailsForm) directors.get(0)).getRole(), is(IOrganisationForm.OrganisationRole.DIRECTOR));
        assertThat(((DirectorDetailsForm) directors.get(1)).getRole(), is(IOrganisationForm.OrganisationRole.DIRECTOR));
    }

    @Test
    public void getDirectorsSecretariesSignatoriesShouldSetRolesOnInvestorDetailsForm() throws Exception {
        Map formData = new HashMap();

        formData.put("accountType", "company");
        formData.put("directors", Arrays.asList(new HashMap<>(), new HashMap<>(), new HashMap<>()));

        Map<String, Object> accountSettings = new HashMap<>();
        List investorsSettingsList = new ArrayList<Map<String, Object>>();
        Map investorSetting = new HashMap<>();
        investorSetting.put("paymentSetting", "nopayments");
        investorSetting.put("isApprover", "false");
        investorSetting.put("role", "signatory");
        investorsSettingsList.add(investorSetting);
        Map investorSetting2 = new HashMap<>();
        investorSetting2.put("paymentSetting", "nopayments");
        investorSetting2.put("isApprover", "false");
        investorSetting2.put("role", "secretary");
        investorsSettingsList.add(investorSetting2);
        Map investorSetting3 = new HashMap<>();
        investorSetting3.put("paymentSetting", "nopayments");
        investorSetting3.put("isApprover", "false");
        investorSetting3.put("role", "director");
        investorsSettingsList.add(investorSetting3);
        accountSettings.put("investorAccountSettings", investorsSettingsList);

        formData.put("accountsettings", accountSettings);
        formData.put("shareholderandmembers", getRolesForDirectors(3, "yes", "no", null));
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);

        List<IExtendedPersonDetailsForm> directors = form.getDirectorsSecretariesSignatories();
        assertThat(directors.size(), is(3));
        assertThat(((DirectorDetailsForm) directors.get(0)).getRole(), is(IOrganisationForm.OrganisationRole.SIGNATORY));
        assertThat(((DirectorDetailsForm) directors.get(1)).getRole(), is(IOrganisationForm.OrganisationRole.SECRETARY));
        assertThat(((DirectorDetailsForm) directors.get(2)).getRole(), is(IOrganisationForm.OrganisationRole.DIRECTOR));
    }

    @Test
    public void getGenericPersonDetails_WhenIndividuals() {
        Map formData = new HashMap();
        formData.put("accountType", "individual");
        Map<String, Object> investor = new HashMap<>();
        investor.put("firstname", "Pikachu");
        formData.put("investors", Arrays.asList(investor));
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        List<IPersonDetailsForm> persons = form.getGenericPersonDetails();
        assertThat(persons, hasSize(1));
        assertThat(persons.get(0).getFirstName(), is("Pikachu"));
    }

    @Test
    public void getGenericPersonDetails_WhenJoint() {
        Map formData = new HashMap();
        formData.put("accountType", "joint");
        Map<String, Object> investor1 = new HashMap<>();
        investor1.put("firstname", "Slowpoke");
        Map<String, Object> investor2 = new HashMap<>();
        investor2.put("firstname", "Slowbro");
        formData.put("investors", Arrays.asList(investor1, investor2));
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        List<IPersonDetailsForm> persons = form.getGenericPersonDetails();
        assertThat(persons, hasSize(2));
        assertThat(persons.get(0).getFirstName(), is("Slowpoke"));
        assertThat(persons.get(1).getFirstName(), is("Slowbro"));
    }

    @Test
    public void getGenericPersonDetails_WhenCorporateSMSF() {
        Map formData = new HashMap();
        formData.put("accountType", "corporateSMSF");
        Map<String, Object> investor1 = new HashMap<>();
        investor1.put("firstname", "Pidgey");
        Map<String, Object> investor2 = new HashMap<>();
        investor2.put("firstname", "Pidgeotto");
        formData.put("directors", Arrays.asList(investor1, investor2));
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        List<IPersonDetailsForm> persons = form.getGenericPersonDetails();
        assertThat(persons, hasSize(2));
        assertThat(persons.get(0).getFirstName(), is("Pidgey"));
        assertThat(persons.get(1).getFirstName(), is("Pidgeotto"));
    }

    @Test
    public void getGenericPersonDetails_WhenIndividualSMSF() {
        Map formData = new HashMap();
        formData.put("accountType", "individualSMSF");
        Map<String, Object> investor1 = new HashMap<>();
        investor1.put("firstname", "Pikachu");
        Map<String, Object> investor2 = new HashMap<>();
        investor2.put("firstname", "Raichu");
        formData.put("trustees", Arrays.asList(investor1, investor2));
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        List<IPersonDetailsForm> persons = form.getGenericPersonDetails();
        assertThat(persons, hasSize(2));
        assertThat(persons.get(0).getFirstName(), is("Pikachu"));
        assertThat(persons.get(1).getFirstName(), is("Raichu"));
    }

    @Test
    public void getGenericPersonDetails_WhenCorporateTrust() {
        Map formData = new HashMap();
        formData.put("accountType", "corporateTrust");
        Map<String, Object> investor1 = new HashMap<>();
        investor1.put("firstname", "Jigglypuff");
        Map<String, Object> investor2 = new HashMap<>();
        investor2.put("firstname", "Wigglytuff");
        formData.put("directors", Arrays.asList(investor1, investor2));
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        List<IPersonDetailsForm> persons = form.getGenericPersonDetails();
        assertThat(persons, hasSize(2));
        assertThat(persons.get(0).getFirstName(), is("Jigglypuff"));
        assertThat(persons.get(1).getFirstName(), is("Wigglytuff"));
    }

    @Test
    public void getGenericPersonDetails_WhenIndividualTrust() {
        Map formData = new HashMap();
        formData.put("accountType", "individualTrust");
        Map<String, Object> investor1 = new HashMap<>();
        investor1.put("firstname", "Zubat");
        Map<String, Object> investor2 = new HashMap<>();
        investor2.put("firstname", "Golbat");
        formData.put("trustees", Arrays.asList(investor1, investor2));
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        List<IPersonDetailsForm> persons = form.getGenericPersonDetails();
        assertThat(persons, hasSize(2));
        assertThat(persons.get(0).getFirstName(), is("Zubat"));
        assertThat(persons.get(1).getFirstName(), is("Golbat"));
    }

    @Test
    public void getGenericPersonDetails_WhenCompany() {
        Map formData = new HashMap();
        formData.put("accountType", "company");
        Map<String, Object> investor1 = new HashMap<>();
        investor1.put("firstname", "Zubat");
        formData.put("directors", Arrays.asList(investor1));
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        List<IPersonDetailsForm> persons = form.getGenericPersonDetails();
        assertThat(persons, hasSize(1));
        assertThat(persons.get(0).getFirstName(), is("Zubat"));
    }

    @Test
    public void getAccountType_WhenAccountTypeIsJoint_ThenItShouldReturnTheJointEnum() throws Exception {
        Map formData = new HashMap();
        formData.put("accountType", "joint");

        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);

        assertThat(form.getAccountType(), is(IClientApplicationForm.AccountType.JOINT));
    }

    @Test
    public void getAccountType_WhenAccountTypeIsNewIndividualSMSF_ThenItShouldReturnTheNewIndividualSMSFEnum() throws Exception {
        Map formData = new HashMap();
        formData.put("accountType", "newIndividualSMSF");

        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);

        assertThat(form.getAccountType(), is(IClientApplicationForm.AccountType.NEW_INDIVIDUAL_SMSF));
    }

    @Test
    public void getAccountName_WhenCorporateSMSF_returnsSMSFName() {
        Map formData = new HashMap();
        Map smsfDetails = new HashMap();
        smsfDetails.put("smsfname", "Poor SMSF");
        formData.put("accountType", "corporateSMSF");
        formData.put("smsfdetails", smsfDetails);
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        assertThat(form.getAccountName(), is("Poor SMSF"));
    }

    @Test
    public void getAccountName_WhenIndividualSMSF_returnsSMSFName() {
        Map formData = new HashMap();
        Map smsfDetails = new HashMap();
        formData.put("accountType", "individualSMSF");
        smsfDetails.put("smsfname", "Santa SMSF");
        formData.put("smsfdetails", smsfDetails);
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        assertThat(form.getAccountName(), is("Santa SMSF"));
    }

    @Test
    public void getAccountName_WhenIndividualTrust_returnsTrustName() {
        Map formData = new HashMap();
        Map trustDetails = new HashMap();
        formData.put("accountType", "individualTrust");
        trustDetails.put("trustname", "Trustworthy Tapir");
        formData.put("trustType", "family");
        formData.put("trustdetails", trustDetails);
        trustDetails.put("trusttype", "family");
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        assertThat(form.getAccountName(), is("Trustworthy Tapir"));
    }

    @Test
    public void getAccountName_WhenCorporateTrust_returnsTrustName() {
        Map formData = new HashMap();
        Map trustDetails = new HashMap();
        formData.put("accountType", "corporateTrust");
        formData.put("trustType", "family");
        trustDetails.put("trustname", "TRUST ME!");
        formData.put("trustdetails", trustDetails);
        trustDetails.put("trusttype", "family");
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        assertThat(form.getAccountName(), is("TRUST ME!"));
    }
    @Test
    public void getAccountName_WhenNewIndividualSMSF_returnsNewIndividualSMSF() {
        Map formData = new HashMap();
        Map smsfDetails = new HashMap();
        formData.put("accountType", "newIndividualSMSF");
        smsfDetails.put("smsfname", "New Santa SMSF");
        formData.put("smsfdetails", smsfDetails);
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        assertThat(form.getAccountName(), is("New Santa SMSF"));
    }


    @Test
    public void getAccountName_WhenCompany_returnsCompanyName() throws Exception {
        Map formData = new HashMap();
        Map companyDetails = new HashMap();
        formData.put("accountType", "company");
        companyDetails.put("companyname", "My Company");
        formData.put("companydetails", companyDetails);
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        assertThat(form.getAccountName(), is("My Company"));

    }

    @Test
    public void getAccountName_whenIndividual_whenInvestorNameIsEmpty_returnsEmptyInvestorName() throws Exception {
        Map formData = new HashMap();
        formData.put("accountType", "individual");
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        assertThat(form.getAccountName(), is(""));
    }

    @Test
    public void getAccountType_WhenAccountTypeIsIndividual_ThenItShouldReturnTheIndividualEnum() throws Exception {
        Map formData = new HashMap();
        formData.put("accountType", "individual");

        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);

        assertThat(form.getAccountType(), is(IClientApplicationForm.AccountType.INDIVIDUAL));
    }

    @Test
    public void getInvestors_WhenThereIsMoreThanOneInvestor_ThenItShouldReturnAListWithAllInvestors() throws Exception {
        Map formData = new HashMap();
        Map sam = new HashMap<>();
        sam.put("firstname", "Sam");

        Map irina = new HashMap<>();
        irina.put("firstname", "Irina");

        formData.put("investors", Arrays.asList(sam, irina));

        formData.put("accountType", "joint");

        formData.put("accountsettings", getAccountSettingsForInvestors(2));
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);

        assertThat(form.getInvestors().size(), is(2));
        assertThat(form.getInvestors().get(0).getFirstName(), is("Sam"));
        assertThat(form.getInvestors().get(1).getFirstName(), is("Irina"));
    }

    @Test
    public void getAccountType_WhenAccountTypeIsCorporateSMSF_ThenItShouldReturnTheCorporateSMSFEnum() throws Exception {
        Map formData = new HashMap();
        formData.put("accountType", "corporateSMSF");

        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        assertThat(form.getAccountType(), is(IClientApplicationForm.AccountType.CORPORATE_SMSF));
    }

    @Test
    public void getAccountType_WhenAccountTypeIsIndividualSMSF_ThenItShouldReturnTheIndividualSMSFEnum() throws Exception {
        Map formData = new HashMap();
        formData.put("accountType", "corporateSMSF");

        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        assertThat(form.getAccountType(), is(IClientApplicationForm.AccountType.CORPORATE_SMSF));
    }

    @Test
    public void getAccountType_WhenAccountTypeIsCorporateTrust_ThenItShouldReturnTheCorporateTrustEnum() throws Exception {
        Map formData = new HashMap();
        formData.put("accountType", "individualSMSF");

        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        assertThat(form.getAccountType(), is(IClientApplicationForm.AccountType.INDIVIDUAL_SMSF));
    }

    @Test
    public void getAccountType_WhenAccountTypeIsIndividualTrust_ThenItShouldReturnTheIndividualTrustEnum() throws Exception {
        Map formData = new HashMap();
        formData.put("accountType", "corporateTrust");

        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        assertThat(form.getAccountType(), is(IClientApplicationForm.AccountType.CORPORATE_TRUST));
    }

    @Test
    public void getAccountType_WhenAccountTypeIsCompany_ThenItShouldReturnTheCompanyEnum() throws Exception {
        Map formData = new HashMap();
        formData.put("accountType", "company");

        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        assertThat(form.getAccountType(), is(IClientApplicationForm.AccountType.COMPANY));
    }

    @Test
    public void getAccountName_WhenTheAccountHasExistingInvestors_ThenTheAccountNameContainsTheNamesOfTheExistingInvestors() throws Exception {
        Map formData = new HashMap();
        formData.put("accountType", "joint");
        formData.put("accountsettings", getAccountSettingsForInvestors(2));

        Map existingInvestor = new HashMap<>();
        existingInvestor.put("firstname", "Sam");
        existingInvestor.put("lastname", "G");
        existingInvestor.put("key", "existing-investor-key");

        Map newInvestor = new HashMap();
        newInvestor.put("firstname", "Shaheedha");
        newInvestor.put("lastname", "M");

        formData.put("investors", Arrays.asList(existingInvestor, newInvestor));
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        assertThat(form.getAccountName(), is("Sam G, Shaheedha M"));
    }

    @Test
    public void getDirectorsSecretariesSignatories_itShouldSetInvestorRoles() {
        Map formData = new HashMap();

        formData.put("accountType", "company");
        formData.put("directors", Arrays.asList(new HashMap<>(), new HashMap<>()));

        Map<String, Object> accountSettings = new HashMap<>();
        List investorsSettingsList = new ArrayList<Map<String, Object>>();
        Map investorSetting = new HashMap<>();
        investorSetting.put("paymentSetting", "nopayments");
        investorSetting.put("isApprover", "false");
        investorSetting.put("role", "signatory");
        investorsSettingsList.add(investorSetting);
        Map investorSetting2 = new HashMap<>();
        investorSetting2.put("paymentSetting", "nopayments");
        investorSetting2.put("isApprover", "true");
        investorSetting2.put("role", "secretary");
        investorsSettingsList.add(investorSetting2);
        accountSettings.put("investorAccountSettings", investorsSettingsList);
        formData.put("accountsettings", accountSettings);


        Map<String, Object> shareholderAndMembers = new HashMap<>();
        List<Map<String, Object>> investorsWithRoles = new ArrayList<>();

        Map<String, Object> investorWithRoles = new HashMap<>();
        investorWithRoles.put("isShareholder", "yes");
        investorWithRoles.put("isMember", "no");
        investorWithRoles.put("isBeneficiary", "yes");
        investorsWithRoles.add(investorWithRoles);

        Map<String, Object> investorWithRoles2 = new HashMap<>();
        investorWithRoles2.put("isShareholder", "no");
        investorWithRoles2.put("isMember", "yes");
        investorWithRoles2.put("isBeneficiary", "no");
        investorsWithRoles.add(investorWithRoles2);
        shareholderAndMembers.put("investorsWithRoles", investorsWithRoles);

        formData.put("shareholderandmembers", shareholderAndMembers);
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);

        List<IExtendedPersonDetailsForm> directors = form.getDirectorsSecretariesSignatories();
        assertThat(directors.size(), is(2));

        DirectorDetailsForm firstDirectorForm = (DirectorDetailsForm) directors.get(0);
        assertThat(firstDirectorForm.getRole(), is(IOrganisationForm.OrganisationRole.SIGNATORY));
        assertThat(firstDirectorForm.isBeneficiary(), is(true));
        assertThat(firstDirectorForm.isShareholder(), is(true));
        assertThat(firstDirectorForm.isMember(), is(false));
        assertThat(firstDirectorForm.isApprover(), is(false));


        DirectorDetailsForm secondDirectorForm = (DirectorDetailsForm) directors.get(1);
        assertThat(secondDirectorForm.getRole(), is(IOrganisationForm.OrganisationRole.SECRETARY));
        assertThat(secondDirectorForm.isBeneficiary(), is(false));
        assertThat(secondDirectorForm.isShareholder(), is(false));
        assertThat(secondDirectorForm.isMember(), is(true));
        assertThat(secondDirectorForm.isApprover(), is(true));
    }

    @Test
    public void getAccountName_WhenNewCorporateSMSF_returnsNewCorporateSMSF() {
        Map formData = new HashMap();
        Map smsfDetails = new HashMap();
        formData.put("accountType", "newCorporateSMSF");
        smsfDetails.put("smsfname", "New Santa SMSF");
        formData.put("smsfdetails", smsfDetails);
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        assertThat(form.getAccountName(), is("New Santa SMSF"));
    }

    @Test
    public void isDirectAccount_WhenAdvised_returnsFalse() {
        Map formData = new HashMap();
        formData.put("accountType", "individual");
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        assertThat(form.isDirectAccount(), is(false));
    }

    @Test
    public void isDirectAccount_WhenDirect_returnsTrue() {
        Map formData = new HashMap();
        formData.put("accountType", "individual");
        formData.put("applicationOrigin", FormDataConstants.VALUE_APPLICATION_ORIGIN_DIRECT);
        formData.put("adviceType", "NoAdvice");
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        assertThat(form.isDirectAccount(), is(true));
    }
}
