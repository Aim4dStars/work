package com.bt.nextgen.api.draftaccount.model.form.v1;

import java.io.IOException;
import java.util.List;

import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.client.model.TaxResidenceCountriesDto;
import com.bt.nextgen.api.draftaccount.builder.v3.TINExemptionEnum;
import com.bt.nextgen.api.draftaccount.model.form.IOverseasTaxDetailsForm;
import org.apache.commons.collections.CollectionUtils;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import org.hamcrest.core.Is;
import org.junit.Test;

import com.bt.nextgen.api.draftaccount.model.AbstractJsonObjectMapperTest;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType;
import com.bt.nextgen.api.draftaccount.model.form.ICompanyForm;
import com.bt.nextgen.api.draftaccount.model.form.IExtendedPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm;
import com.bt.nextgen.api.draftaccount.model.form.IPensionEligibilityForm;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.ISmsfForm;
import com.bt.nextgen.api.draftaccount.model.form.ITaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.ITrustForm;
import com.bt.nextgen.api.draftaccount.model.form.ITrustForm.SettlorofTrustType;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;
import com.bt.nextgen.service.integration.domain.Gender;

import static com.bt.nextgen.api.draftaccount.model.form.v1.AddressFormTest.assertStandardAddress;
import static com.bt.nextgen.api.draftaccount.model.form.v1.ClientApplicationFormFactoryV1.getNewClientApplicationForm;
import static com.bt.nextgen.service.integration.domain.Gender.FEMALE;
import static com.bt.nextgen.service.integration.domain.Gender.MALE;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

/**
 * Tests for loading complete client application forms from scratch.
 */
public class ClientApplicationFormTest extends AbstractJsonObjectMapperTest<OnboardingApplicationFormData> {

    private IClientApplicationForm form;

    public ClientApplicationFormTest() {
        super(OnboardingApplicationFormData.class);
    }

    @Test
    public void testAdditionalMembersDualRoles() throws IOException {
        initForm("corporate-trust-benfnbenfowner", AccountType.CORPORATE_TRUST);
        final List<IExtendedPersonDetailsForm> additionalShareholdersAndMembers = form.getAdditionalShareholdersAndMembers();
        assertThat(additionalShareholdersAndMembers.get(0).isShareholder(),is(true));
        assertThat(additionalShareholdersAndMembers.get(0).isMember(),is(true));
        assertThat(additionalShareholdersAndMembers.get(1).isShareholder(),is(true));
        assertThat(additionalShareholdersAndMembers.get(1).isBeneficiary(),is(true));
        assertThat(additionalShareholdersAndMembers.get(2).isBeneficialOwner(),is(true));
        assertThat(additionalShareholdersAndMembers.get(2).isBeneficiary(),is(true));
    }

    @Test
    public void testTrustIndivAdditionalMembersDualRoles() throws IOException {
        initForm("client_application_trust_individual_aml", AccountType.INDIVIDUAL_TRUST);
        final List<IExtendedPersonDetailsForm> additionalShareholdersAndMembers = form.getAdditionalShareholdersAndMembers();
        assertThat(additionalShareholdersAndMembers.get(0).isBeneficiary(),is(true));
        assertThat(additionalShareholdersAndMembers.get(0).isControllerOfTrust(),is(true));
    }

    @Test
    public void company() throws IOException {
        initForm("company-new-1", AccountType.COMPANY);

        // TODO: more tests!
        final List<IExtendedPersonDetailsForm> directors = form.getDirectors();
        assertThat(directors, hasSize(3));
        assertPersonDetails(directors.get(0), "REV", "Billy", "Bob", "Bastard", MALE);
        assertExtendedPersonDetails(directors.get(0), true, "allpayments", true, false);
        assertExtendedPersonRoleFlags(directors.get(0), false, true, false, false);
        assertPersonDetails(directors.get(1), "SISTER", "Belinda", "Breaking", "Bastard", FEMALE);
        assertExtendedPersonDetails(directors.get(1), false, "linkedaccountsonly", true, false);
        assertExtendedPersonRoleFlags(directors.get(1), false, true, false, false);
        assertPersonDetails(directors.get(2), "DR", "Beverly", "Bodacious", "Bastard", FEMALE);
        assertExtendedPersonDetails(directors.get(2), false, "nopayments", false, false);
        assertExtendedPersonRoleFlags(directors.get(2), false, true, false, false);

        final List<IExtendedPersonDetailsForm> shareholders = form.getAdditionalShareholdersAndMembers();
        assertThat(shareholders, hasSize(1));
        assertPersonDetails(shareholders.get(0), "EOTL", "Benjamin", "Bunting", "Bastard", MALE);
        assertExtendedPersonDetails(shareholders.get(0), false, false, false);
        assertExtendedPersonRoleFlags(shareholders.get(0), false, true, false, false);

        final ICompanyForm company = form.getCompanyDetails();
        assertOrganisation(company, "Bastards Inc.", "83914571673", "000000019", "9700", true);
        assertThat(company.getOccupierName(), is("Bunton Jones"));
        assertThat(company.getAsicName(), is("Bastards Incorporated"));
        assertStandardAddress(company.getPlaceOfBusinessAddress(), "34", "George", "STREET", "SYDNEY", "NSW", "2000", "AU");
        final ITaxDetailsForm taxDetails = company.getTaxDetails();
        assertThat(taxDetails.getExemptionReason(), is(nullValue()));
        assertThat(taxDetails.getTaxFileNumber(), is("123456782"));

        assertThat(form.getInvestors(), empty());
        assertThat(form.getFees().getEstablishmentFee(), isBD("77803.40"));
    }

    @Test
    public void existingIndividual() throws IOException {
        initForm("individual-gcm-1", AccountType.INDIVIDUAL);

        final List<IExtendedPersonDetailsForm> investors = form.getInvestors();
        assertThat(investors, hasSize(1));
        final IExtendedPersonDetailsForm investor = investors.get(0);
        assertPersonDetails(investor, "MR", "Florin", null, "Test", MALE);
        assertExtendedPersonDetails(investor, true, "nopayments", true, false);

        assertThat(form.getDirectors(), empty());
        assertThat(form.getFees().getEstablishmentFee(), isBD("3.00"));
    }

    @Test
    public void individualTrustOther() throws IOException {
        initForm("trust-individual-other-2", AccountType.INDIVIDUAL_TRUST, "other");
        final ITrustForm trust = form.getTrust();
        assertThat(trust.getSettlorOfTrust(), is(SettlorofTrustType.ORGANISATION));
        assertThat(trust.getName(),is("wewewew"));
    }

    @Test
    public void adviceTypeAndApplicationOrigin() throws IOException {
        initForm("individual-gcm-1", AccountType.INDIVIDUAL);
        assertThat(form.getAdviceType(), is(IClientApplicationForm.AdviceType.PERSONAL_ADVICE.value()));
        assertThat(form.getApplicationOrigin(), is(IClientApplicationForm.ApplicationOriginType.BT_PANORAMA.value()));
    }

    @Test
    public void forAdvisedApplications_ApplicationApprovalTypeShouldBeOnlineByDefault() throws IOException {
        initForm("individual-gcm-1", AccountType.INDIVIDUAL);
        assertThat(form.getApplicationApprovalType(),is(IClientApplicationForm.ApprovalType.ONLINE));
    }

    @Test
    public void corporateSmsfNew() throws IOException {
        initForm("smsf-new-corporate-2", AccountType.NEW_CORPORATE_SMSF);

        final ISmsfForm smsf = form.getSmsf();
        assertOrganisation(smsf, "The New Corporate SMSF", "", null, "7412", true);
        assertThat(smsf.getSourceOfWealth(), is("Additional Sources"));
        assertThat(smsf.getAdditionalSourceOfWealth(), is("Money Laundering"));
        assertStandardAddress(smsf.getRegisteredAddress(), "28", "West", "STREET", "NORTH SYDNEY", "NSW", "2060", "AU");

        final ICompanyForm company = form.getCompanyTrustee();
        assertOrganisation(company, "The Trustee Company", null, null, "7412", false);
        assertStandardAddress(company.getRegisteredAddress(), "28", "West", "STREET", "NORTH SYDNEY", "NSW", "2060", "AU");
        assertStandardAddress(company.getPlaceOfBusinessAddress(), "28", "West", "STREET", "NORTH SYDNEY", "NSW", "2060", "AU");

        final List<IExtendedPersonDetailsForm> directors = form.getDirectors();
        assertThat(directors, hasSize(2));
        IExtendedPersonDetailsForm director = directors.get(0);
        assertPersonDetails(director, "Mr", "Testcoresmsffivetwosixteen", null, "First", MALE);
        assertExtendedPersonDetails(director, true, "nopayments", true, "4E9F4BF67C010872107B51D427BACE10B33A0FA404360F2D");
        assertExtendedPersonRoleFlags(director, false, true, false, false);
        director = directors.get(1);
        assertPersonDetails(director, "Mr", "Testoneuser", null, "Last", MALE);
        assertExtendedPersonDetails(director, false, "linkedaccountsonly", true, "C4C7EB6CB26186561F4A21ED048C69B6B0E61BFD17B480F3");
        assertExtendedPersonRoleFlags(director, true, true, false, false);
    }

    @Test
    public void superPension_withEligibilityCriteria() throws IOException {
        initForm("superpension-with-eligibility", AccountType.SUPER_PENSION);
        final IPensionEligibilityForm pensionEligibility = form.getPensionEligibility();
        assertThat(pensionEligibility.getEligibilityCriteria(), is("overSixtyFive"));
        assertNull(pensionEligibility.getConditionRelease());
    }

    @Test
    public void superPension_withEligibilityCriteria_andConditionRelease() throws IOException {
        initForm("superpension-with-condition-release", AccountType.SUPER_PENSION);
        final IPensionEligibilityForm pensionEligibility = form.getPensionEligibility();
        assertThat(pensionEligibility.getEligibilityCriteria(), is("UNPSV"));
        assertThat(pensionEligibility.getConditionRelease(), is("PERM_INCAP"));
    }

    @Test
    public void verifyCorelationSequenceForMemberAndExistingInvestor() throws IOException {
        initForm("corporateSmsfWithExistingInvestorAndShareholder", AccountType.CORPORATE_SMSF);
        List<IExtendedPersonDetailsForm> directors = form.getDirectors();
        assertThat(directors.get(0).getCorrelationSequenceNumber(), is(1));
        assertThat(directors.get(1).getCorrelationSequenceNumber(), is(2));
        assertThat(directors.get(2).getCorrelationSequenceNumber(), is(3));

        List<IExtendedPersonDetailsForm> additionalShareholdersAndMembers = form.getAdditionalShareholdersAndMembers();
        assertThat(additionalShareholdersAndMembers.get(0).getCorrelationSequenceNumber(), is(4));
        assertThat(additionalShareholdersAndMembers.get(1).getCorrelationSequenceNumber(), is(5));

        List<IPersonDetailsForm> existingPersonDetails = form.getExistingPersonDetails();
        assertThat(existingPersonDetails.get(0).getCorrelationSequenceNumber(), is(6));
        assertThat(existingPersonDetails.get(1).getCorrelationSequenceNumber(), is(7));
    }

    @Test
    public void testCompany_withNoAccountSettings() throws IOException {
        initForm("company-no-account-settings", AccountType.COMPANY);

        assertEquals(form.getAccountSettings().getRoleSettingForInvestor(0), null);
        assertEquals(form.getAccountSettings().getPaymentSettingForInvestor(0), null);
        assertEquals(form.getAccountSettings().getApproverSettingForInvestor(0), false);
    }

    @Test
      public void test_CRSDetails_forSMSF() throws IOException {
        initForm("client_application_corpsmsf_crs", AccountType.CORPORATE_SMSF);
        ISmsfForm smsfForm = form.getSmsf();
        assertNotNull(smsfForm.getCrsTaxDetails());
        assertNotNull(smsfForm.getCrsTaxDetails().getAustralianTaxDetails());
        assertTrue(smsfForm.getCrsTaxDetails().getAustralianTaxDetails().isTaxCountryAustralia());
        assertThat(smsfForm.getCrsTaxDetails().getOverseasTaxCountry(), isEmptyOrNullString());
        verifyOverseasCountryValuesForOrganization(smsfForm);

    }

    @Test
    public void test_CRSDetails_forTrust() throws IOException {
        initForm("client_application_corptrust_crs", AccountType.CORPORATE_TRUST);
        ITrustForm trustForm = form.getTrust();
        assertNotNull(trustForm.getCrsTaxDetails());
        assertNotNull(trustForm.getCrsTaxDetails().getAustralianTaxDetails());
        assertTrue(trustForm.getCrsTaxDetails().getAustralianTaxDetails().isTaxCountryAustralia());
        assertThat(trustForm.getCrsTaxDetails().getOverseasTaxCountry(), isEmptyOrNullString());
        verifyOverseasCountryValuesForOrganization(trustForm);

    }

    @Test
    public void test_CRSDetails_forCompany() throws IOException {
        initForm("client_application_company_crs", AccountType.COMPANY);
        ICompanyForm companyForm = form.getCompanyDetails();
        assertNotNull(companyForm.getCrsTaxDetails());
        assertNotNull(companyForm.getCrsTaxDetails().getAustralianTaxDetails());
        assertTrue(companyForm.getCrsTaxDetails().getAustralianTaxDetails().isTaxCountryAustralia());
        assertThat(companyForm.getCrsTaxDetails().getOverseasTaxCountry(), isEmptyOrNullString());
        verifyOverseasCountryValuesForOrganization(companyForm);

    }

    @Test
    public void test_CRSDetails_forCompany_NonOverseasResident() throws IOException {
        initForm("client_application_company_crs_non_overseas", AccountType.COMPANY);
        ICompanyForm companyForm = form.getCompanyDetails();
        assertNotNull(companyForm.getCrsTaxDetails());
        assertNotNull(companyForm.getCrsTaxDetails().getAustralianTaxDetails());
        assertTrue(companyForm.getCrsTaxDetails().getAustralianTaxDetails().isTaxCountryAustralia());
    }



    @Test
    public void test_CRSDetails_forIndividual() throws IOException {
        initForm("client_application_individual_crs", AccountType.INDIVIDUAL);
        IPersonDetailsForm personDetailsForm = form.getGenericPersonDetails().get(0);
        assertNotNull(personDetailsForm.getAustralianTaxDetails());
        assertTrue(personDetailsForm.getAustralianTaxDetails().isTaxCountryAustralia());
        assertThat(personDetailsForm.getOverseasTaxCountry(), isEmptyOrNullString());
        verifyOverseasCountryValuesForPerson(personDetailsForm);

    }

    @Test
    public void test_CRSDetails_forExistingInvestor_With_TINExemptionAsUnderAge() throws IOException {
        initForm("client_application_form_data_CorporateSMSF_UnderAge", AccountType.CORPORATE_SMSF);
        IPersonDetailsForm personDetailsForm = form.getGenericPersonDetails().get(0);
        assertNotNull(personDetailsForm.getOverseasTaxDetails());

        if (CollectionUtils.isNotEmpty(personDetailsForm.getOverseasTaxDetails())) {
            List<IOverseasTaxDetailsForm> taxResidenceCountries = personDetailsForm.getOverseasTaxDetails();
            assertThat(taxResidenceCountries.get(0).getTINExemptionReason(), is("btfg$tin_pend"));
            assertThat(taxResidenceCountries.get(0).getOverseasTaxCountry(), Is.is("Albania"));
            assertThat(taxResidenceCountries.get(1).getTINExemptionReason(), is("btfg$under_aged"));
            assertThat(taxResidenceCountries.get(1).getOverseasTaxCountry(), Is.is("India"));
            assertThat(taxResidenceCountries.get(2).getTINExemptionReason(), is("btfg$under_aged"));
            assertThat(taxResidenceCountries.get(2).getOverseasTaxCountry(), Is.is("Italy"));
        }
    }

    @Test
    public void test_CRSDetails_forIndividual_NonOverseasResident() throws IOException {
        initForm("client_application_individual_crs_non_overseas", AccountType.INDIVIDUAL);
        IPersonDetailsForm personDetailsForm = form.getGenericPersonDetails().get(0);
        assertNotNull(personDetailsForm.getAustralianTaxDetails());
        assertTrue(personDetailsForm.getAustralianTaxDetails().isTaxCountryAustralia());
    }

    @Test
    public void test_ParentProductName()  throws IOException {
        initForm("clientapplication_parentproductname", AccountType.INDIVIDUAL);
        assertThat(form.getParentProductName(),is("Cash Management Account"));
    }

    private void verifyOverseasCountryValuesForOrganization(IOrganisationForm organisationForm) {

        if (CollectionUtils.isNotEmpty(organisationForm.getCrsTaxDetails().getOverseasTaxDetails())) {
            List<IOverseasTaxDetailsForm> taxResidenceCountries = organisationForm.getCrsTaxDetails().getOverseasTaxDetails();
            assertThat(taxResidenceCountries.get(0).getTINExemptionReason(), is("btfg$tin_pend"));
            assertThat(taxResidenceCountries.get(0).getOverseasTaxCountry(), Is.is("AL"));
            assertThat(taxResidenceCountries.get(1).getTINExemptionReason(), is("btfg$tin_never_iss"));
            assertThat(taxResidenceCountries.get(1).getOverseasTaxCountry(), Is.is("DZ"));
        }
    }

    private void verifyOverseasCountryValuesForPerson(IPersonDetailsForm personDetailsFormForm) {

        if (CollectionUtils.isNotEmpty(personDetailsFormForm.getOverseasTaxDetails())) {
            List<IOverseasTaxDetailsForm> taxResidenceCountries = personDetailsFormForm.getOverseasTaxDetails();
            assertThat(taxResidenceCountries.get(0).getTINExemptionReason(), is("btfg$tin_pend"));
            assertThat(taxResidenceCountries.get(0).getOverseasTaxCountry(), Is.is("AL"));
            assertThat(taxResidenceCountries.get(1).getTINExemptionReason(), is("btfg$tin_never_iss"));
            assertThat(taxResidenceCountries.get(1).getOverseasTaxCountry(), Is.is("DZ"));
        }
    }


    private void initForm(String resourceName, AccountType accountType) throws IOException {
        this.form = getNewClientApplicationForm(readJsonResource(resourceName));
        assertThat(form.getAccountType(), is(accountType));
    }

    private void initForm(String resourceName, AccountType accountType, String trustType) throws IOException {
        initForm(resourceName, accountType);
        assertTrue(form.hasTrust());
        assertThat(form.getTrustType(), is(trustType));
    }

    static void assertPersonDetails(IPersonDetailsForm person, String title, String firstName, String middleName, String lastName, Gender gender) {
        assertThat(person.getTitle(), is(title));
        assertThat(person.getFirstName(), is(firstName));
        assertThat(person.getMiddleName(), is(middleName));
        assertThat(person.getLastName(), is(lastName));
        assertThat(person.getGender(), is(gender));
    }

    static void assertExtendedPersonDetails(IExtendedPersonDetailsForm person, boolean primary, boolean approver, boolean existing) {
        assertThat(person.isPrimaryContact(), is(primary));
        assertThat(person.isApprover(), is(approver));
        assertThat(person.isExistingPerson(), is(existing));
    }

    static void assertExtendedPersonDetails(IExtendedPersonDetailsForm person, boolean primary, String paymentSetting, boolean approver, boolean existing) {
        assertExtendedPersonDetails(person, primary, approver, existing);
        assertThat(person.getPaymentSetting(), is(PaymentAuthorityEnum.fromValue(paymentSetting)));
    }

    static void assertExtendedPersonDetails(IExtendedPersonDetailsForm person, boolean primary, String paymentSetting, boolean approver, String clientKey) {
        assertExtendedPersonDetails(person, primary, paymentSetting, approver, true);
        assertThat(person.getClientKey(), is(clientKey));
    }

    static void assertExtendedPersonRoleFlags(IExtendedPersonDetailsForm person, boolean member, boolean shareholder, boolean beneficiary, boolean benOwner) {
        assertThat(person.isMember(), is(member));
        assertThat(person.isShareholder(), is(shareholder));
        assertThat(person.isBeneficiary(), is(beneficiary));
        assertThat(person.isBeneficialOwner(), is(benOwner));
    }

    static void assertOrganisation(IOrganisationForm organisation, String name, String abn, String acn, String anzsicCode, Boolean gstRegistered) {
        assertThat(organisation.getName(), is(name));
        assertThat(organisation.getABN(), is(abn));
        assertThat(organisation.getACN(), is(acn));
        assertThat(organisation.getAnzsicCode(), is(anzsicCode));
        assertThat(organisation.getRegisteredForGST(), is(gstRegistered));
    }
}