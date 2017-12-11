package com.bt.nextgen.api.draftaccount.builder.v3;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ns.btfin_com.party.v3_0.ForeignCountryForTaxationType;
import ns.btfin_com.party.v3_0.ReasonForTaxIdentificationNumberExemptionType;
import ns.btfin_com.party.v3_0.TFNRegistrationExemptionType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.InvolvedPartyDetailsType;
import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import com.bt.nextgen.api.draftaccount.AbstractJsonReaderTest;
import com.bt.nextgen.api.draftaccount.model.form.ExtendedPersonDetailsFormFactory;
import com.bt.nextgen.api.draftaccount.model.form.IAusTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.ICrsTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IExtendedPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.ITaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.v1.ClientApplicationFormFactoryV1;
import com.bt.nextgen.api.draftaccount.model.form.v1.CrsTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;
import com.bt.nextgen.config.JsonObjectMapper;

import static com.bt.nextgen.api.draftaccount.schemas.v1.base.TaxOptionTypeEnum.PENSIONER;
import static com.bt.nextgen.api.draftaccount.schemas.v1.base.TaxOptionTypeEnum.TAX_FILE_NUMBER_OR_EXEMPTION_NOT_PROVIDED;
import static com.bt.nextgen.api.draftaccount.schemas.v1.base.TaxOptionTypeEnum.TAX_FILE_NUMBER_PROVIDED;
import static ns.btfin_com.party.v3_0.ReasonForTaxIdentificationNumberExemptionType.TIN_PENDING;
import static ns.btfin_com.party.v3_0.ReasonForTaxIdentificationNumberExemptionType.TIN_NEVER_ISSUED;
import static ns.btfin_com.party.v3_0.TFNRegistrationExemptionType.NORFOLK_ISLAND_RESIDENT;
import static ns.btfin_com.party.v3_0.TFNRegistrationExemptionType.TFN_NOT_QUOTED;
import static ns.btfin_com.party.v3_0.TFNRegistrationType.EXEMPT;
import static ns.btfin_com.party.v3_0.TFNRegistrationType.NONE;
import static ns.btfin_com.party.v3_0.TFNRegistrationType.ONE;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class TaxFieldsBuilderTest extends AbstractJsonReaderTest{

    private TaxFieldsBuilder taxFieldsBuilder = new TaxFieldsBuilder();

    private static final String JSON_SCHEMA_PACKAGE= "com/bt/nextgen/api/draftaccount/builder/v3_JsonSchema/";

    private ICrsTaxDetailsForm crsTaxDetailsForm = mock(CrsTaxDetailsForm.class);
    private IAusTaxDetailsForm ausTaxDetailsForm = mock(IAusTaxDetailsForm.class);
    private Boolean existingInvestor = true;
    private ns.btfin_com.product.common.investmentaccount.v2_0.InvolvedPartyDetailsType investorDetails = new InvolvedPartyDetailsType();


    @Test
    public void shouldPopulateInvestorDetailsWithTFNInformation() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String investorDetailsJson = "{\n" +
                "        \"taxcountry\": \"Ita\",\n" +
                "        \"tfn\": \"123456782\"\n" +
                "    }";

        Map<String, Object> individualInvestorDetailsMap = mapper.readValue(investorDetailsJson, new TypeReference<Map<String, Object>>() {
        });

        InvolvedPartyDetailsType investorDetails = new InvolvedPartyDetailsType();
        taxFieldsBuilder.populateTaxRelatedFieldsForNewInvestor(investorDetails, ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(individualInvestorDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true));

        assertEquals(investorDetails.getTFN().get(0), "123456782");
        assertNull(investorDetails.getTFNRegistrationExemption());
        assertEquals(investorDetails.getTFNRegistration(), ONE);
        assertEquals(investorDetails.getCountryOfResidenceForTax(), "ITA");
    }

    @Test
    public void shouldDefaultTaxDetailsForTrustIndividualAccountNotHavingCRS() throws IOException {
        IClientApplicationForm appForm = getClientApplicationForm("trust_indiv_with_beneficiary_CRS.json");
        InvolvedPartyDetailsType partyDetails = new InvolvedPartyDetailsType();
        IExtendedPersonDetailsForm beneficiaryPerson = appForm.getAdditionalShareholdersAndMembers().get(2);
        taxFieldsBuilder.populateTax(partyDetails, beneficiaryPerson, IClientApplicationForm.AccountType.INDIVIDUAL_TRUST);
        assertThat(partyDetails.getTFNRegistration(), is(NONE));
        assertThat(partyDetails.getCountryOfResidenceForTax(), is("AU"));
    }

    @Test
    public void shouldPopulateTaxDetailsForIndividualAccountHavingCRS() throws IOException {

        IClientApplicationForm clientApplicationForm = getClientApplicationForm("client_application_individual_CRS.json");
        InvolvedPartyDetailsType partyDetails = new InvolvedPartyDetailsType();
        IExtendedPersonDetailsForm investor = clientApplicationForm.getInvestors().get(0);
        taxFieldsBuilder.populateCrsTax(partyDetails, investor, false);

        assertThat(partyDetails.getTFN().size(), Matchers.is(0));
        assertThat(partyDetails.getTFNRegistrationExemption(), is(TFNRegistrationExemptionType.NON_RESIDENT));
        assertThat(partyDetails.getTFNRegistration(), is(EXEMPT));
        assertThat(partyDetails.getCountryOfResidenceForTax(), is("AX"));
        assertNotNull(partyDetails.getForeignCountriesForTaxation().getForeignCountryForTaxation());

        ForeignCountryForTaxationType foreignCountryForTaxationType = partyDetails.getForeignCountriesForTaxation().getForeignCountryForTaxation().get(0);
        assertThat(foreignCountryForTaxationType.getCountryCode(), is("AX"));
        assertNull(foreignCountryForTaxationType.getTIN());
        assertThat(foreignCountryForTaxationType.getReasonForTaxIdentificationNumberExemption(), is(TIN_NEVER_ISSUED));
    }

    @Test
    public void shouldPopulateTaxDetailsForSuperAccountHavingCRS() throws IOException {

        IClientApplicationForm clientApplicationForm = getClientApplicationForm("client_application_superAccumulation_CRS.json");
        InvolvedPartyDetailsType partyDetails = new InvolvedPartyDetailsType();
        IExtendedPersonDetailsForm investor = clientApplicationForm.getInvestors().get(0);
        taxFieldsBuilder.populateCrsTax(partyDetails, investor, false);

        assertThat(partyDetails.getTFN().size(), is(1));
        assertThat(partyDetails.getTFN().get(0), is("123456782"));
        assertNull(partyDetails.getTFNRegistrationExemption());
        assertThat(partyDetails.getTFNRegistration(), is(ONE));
        assertThat(partyDetails.getCountryOfResidenceForTax(), is("AU"));
        assertNotNull(partyDetails.getForeignCountriesForTaxation().getForeignCountryForTaxation());

        ForeignCountryForTaxationType foreignCountryForTaxationType = partyDetails.getForeignCountriesForTaxation().getForeignCountryForTaxation().get(0);
        assertThat(foreignCountryForTaxationType.getCountryCode(), is("AF"));
        assertThat(foreignCountryForTaxationType.getTIN(), is("OVERSEASTIN"));
        assertNull(foreignCountryForTaxationType.getReasonForTaxIdentificationNumberExemption());
    }
    @Test
    public void shouldPopulateTaxDetailsForExistingCorporateSMSFAccountHavingCRS() throws IOException {

        IClientApplicationForm clientApplicationForm = getClientApplicationForm("client_application_existingCorporateSMSF_CRS.json");

        // ************** - DIRECTOR CRS TAX Details ***************
        InvolvedPartyDetailsType directorPartyDetails = new InvolvedPartyDetailsType();
        IExtendedPersonDetailsForm director = clientApplicationForm.getDirectors().get(0);
        taxFieldsBuilder.populateCrsTax(directorPartyDetails, director, false);

        assertTrue(CollectionUtils.isEmpty(directorPartyDetails.getTFN()));
        assertNull(directorPartyDetails.getTFNRegistrationExemption());
        assertThat(directorPartyDetails.getTFNRegistration(), is(NONE)); // NO TFN Drop down present for director
        assertThat(directorPartyDetails.getCountryOfResidenceForTax(), is("AU")); //Australian resident for tax purposes selected as YES
        assertNotNull(directorPartyDetails.getForeignCountriesForTaxation().getForeignCountryForTaxation());

        ForeignCountryForTaxationType firstOverseasCountry = directorPartyDetails.getForeignCountriesForTaxation().getForeignCountryForTaxation().get(0);
        assertThat(firstOverseasCountry.getCountryCode(), is("CV"));
        assertThat(firstOverseasCountry.getTIN(), is("DIRTIN"));
        assertNull(firstOverseasCountry.getReasonForTaxIdentificationNumberExemption());

        ForeignCountryForTaxationType secondOverseasCountry = directorPartyDetails.getForeignCountriesForTaxation().getForeignCountryForTaxation().get(1);
        assertThat(secondOverseasCountry.getCountryCode(), is("KH"));
        assertThat(secondOverseasCountry.getTIN(), is("DIRTINTWO"));
        assertNull(secondOverseasCountry.getReasonForTaxIdentificationNumberExemption());

        // ************** - SMSF CRS TAX Details ***************
        InvolvedPartyDetailsType smsfPartyDetails = new InvolvedPartyDetailsType();
        ICrsTaxDetailsForm smsfCRStaxdetails = clientApplicationForm.getSmsf().getCrsTaxDetails();
        taxFieldsBuilder.populateCrsTaxRelatedFieldsForNewInvestor(smsfPartyDetails, smsfCRStaxdetails);

        assertThat(smsfPartyDetails.getTFN().size(), is(1));
        assertThat(smsfPartyDetails.getTFN().get(0), is("123456782"));
        assertNull(smsfPartyDetails.getTFNRegistrationExemption());
        assertThat(smsfPartyDetails.getTFNRegistration(), is(ONE)); // TFN Drop Down is present and TFN is entered
        assertThat(smsfPartyDetails.getCountryOfResidenceForTax(), is("AU")); // NO radio button present but tax country is defaulted for SMSF
        assertNotNull(smsfPartyDetails.getForeignCountriesForTaxation());

        ForeignCountryForTaxationType overseasCountrySMSF = smsfPartyDetails.getForeignCountriesForTaxation().getForeignCountryForTaxation().get(0);
        assertThat(overseasCountrySMSF.getCountryCode(), is("AX"));
        assertThat(overseasCountrySMSF.getTIN(), is("SMSFTIN"));
        assertNull(overseasCountrySMSF.getReasonForTaxIdentificationNumberExemption());

        // ************** - COMPANY ACTING AS TRUSTEE CRS TAX Details ***************

        InvolvedPartyDetailsType companyPartyDetails = new InvolvedPartyDetailsType();
        ICrsTaxDetailsForm companyTrusteeCRS = clientApplicationForm.getCompanyTrustee().getCrsTaxDetails();
        taxFieldsBuilder.populateCrsTaxRelatedFieldsForNewInvestor(companyPartyDetails, companyTrusteeCRS);

        assertTrue(CollectionUtils.isEmpty(companyPartyDetails.getTFN()));
        assertNull(companyPartyDetails.getTFNRegistrationExemption());
        assertThat(companyPartyDetails.getTFNRegistration(), is(NONE)); // NO Tax details Radio button NOR TFN Drop down present
        assertThat(companyPartyDetails.getCountryOfResidenceForTax(), is("AU")); //Australian resident for tax purposes defaulted


        assertThat(CollectionUtils.size(companyPartyDetails.getForeignCountriesForTaxation().getForeignCountryForTaxation()), is(3));
        ForeignCountryForTaxationType countryOne = companyPartyDetails.getForeignCountriesForTaxation().getForeignCountryForTaxation().get(0);
        assertThat(countryOne.getCountryCode(), is("AX"));
        assertNull(countryOne.getTIN());
        assertThat(countryOne.getReasonForTaxIdentificationNumberExemption(), is(TIN_NEVER_ISSUED));

        ForeignCountryForTaxationType countryTwo = companyPartyDetails.getForeignCountriesForTaxation().getForeignCountryForTaxation().get(1);
        assertThat(countryTwo.getCountryCode(), is("AT"));
        assertNull(countryTwo.getTIN());
        assertThat(countryTwo.getReasonForTaxIdentificationNumberExemption(), is(TIN_PENDING));

        ForeignCountryForTaxationType countryThree = companyPartyDetails.getForeignCountriesForTaxation().getForeignCountryForTaxation().get(2);
        assertThat(countryThree.getCountryCode(), is("BE"));
        assertNull(countryThree.getTIN());
        assertThat(countryThree.getReasonForTaxIdentificationNumberExemption(), is(TIN_PENDING));


        // ************** - SHAREHOLDER ONE CRS TAX Details ***************

        InvolvedPartyDetailsType shareHolderOnePartyDetails = new InvolvedPartyDetailsType();
        assertThat(CollectionUtils.size(clientApplicationForm.getAdditionalShareholdersAndMembers()), is(3));

        ICrsTaxDetailsForm shareHolderOneCRS = clientApplicationForm.getAdditionalShareholdersAndMembers().get(0);
        taxFieldsBuilder.populateCrsTaxRelatedFieldsForNewInvestor(shareHolderOnePartyDetails, shareHolderOneCRS);

        assertTrue(CollectionUtils.isEmpty(shareHolderOnePartyDetails.getTFN()));
        assertNull(shareHolderOnePartyDetails.getTFNRegistrationExemption());
        assertThat(shareHolderOnePartyDetails.getTFNRegistration(), is(NONE)); // No TFN Drop down present
        assertThat(shareHolderOnePartyDetails.getCountryOfResidenceForTax(), is("AF")); //Australian resident for tax purposes? selected as NO with one Overseas


        assertThat(CollectionUtils.size(shareHolderOnePartyDetails.getForeignCountriesForTaxation().getForeignCountryForTaxation()), is(1));
        ForeignCountryForTaxationType foreignCountry = shareHolderOnePartyDetails.getForeignCountriesForTaxation().getForeignCountryForTaxation().get(0);
        assertThat(foreignCountry.getCountryCode(), is("AF"));
        assertThat(foreignCountry.getTIN(), is("AFGTIN"));
        assertNull(foreignCountry.getReasonForTaxIdentificationNumberExemption());

        // ************** - SHAREHOLDER TWO CRS TAX Details ***************

        ICrsTaxDetailsForm shareHolderTwoCRS = clientApplicationForm.getAdditionalShareholdersAndMembers().get(1);
        InvolvedPartyDetailsType shareHolderTwoPartyDetails = new InvolvedPartyDetailsType();
        taxFieldsBuilder.populateCrsTaxRelatedFieldsForNewInvestor(shareHolderTwoPartyDetails, shareHolderTwoCRS);

        assertTrue(CollectionUtils.isEmpty(shareHolderTwoPartyDetails.getTFN()));
        assertNull(shareHolderTwoPartyDetails.getTFNRegistrationExemption());
        assertThat(shareHolderTwoPartyDetails.getTFNRegistration(), is(NONE)); // No TFN Drop down present
        assertThat(shareHolderTwoPartyDetails.getCountryOfResidenceForTax(), is("VI")); //Australian resident for tax purposes? selected as NO with TWO Overseas


        assertThat(CollectionUtils.size(shareHolderTwoPartyDetails.getForeignCountriesForTaxation().getForeignCountryForTaxation()), is(2));
        ForeignCountryForTaxationType countryShareOne = shareHolderTwoPartyDetails.getForeignCountriesForTaxation().getForeignCountryForTaxation().get(0);
        assertThat(countryShareOne.getCountryCode(), is("DZ"));
        assertThat(countryShareOne.getTIN(), is("ALGTIN"));
        assertNull(countryShareOne.getReasonForTaxIdentificationNumberExemption());

        ForeignCountryForTaxationType countryShareTwo = shareHolderTwoPartyDetails.getForeignCountriesForTaxation().getForeignCountryForTaxation().get(1);
        assertThat(countryShareTwo.getCountryCode(), is("VI"));
        assertNull(countryShareTwo.getTIN());
        assertThat(countryShareTwo.getReasonForTaxIdentificationNumberExemption(), is(TIN_PENDING));

        // ************** - SHAREHOLDER THREE CRS TAX Details ***************

        ICrsTaxDetailsForm shareHolderThreeCRS = clientApplicationForm.getAdditionalShareholdersAndMembers().get(2);
        InvolvedPartyDetailsType shareHolderThreePartyDetails = new InvolvedPartyDetailsType();
        taxFieldsBuilder.populateCrsTaxRelatedFieldsForNewInvestor(shareHolderThreePartyDetails, shareHolderThreeCRS);

        assertTrue(CollectionUtils.isEmpty(shareHolderThreePartyDetails.getTFN()));
        assertNull(shareHolderThreePartyDetails.getTFNRegistrationExemption());
        assertThat(shareHolderThreePartyDetails.getTFNRegistration(), is(NONE)); // No TFN Drop down present
        assertThat(shareHolderThreePartyDetails.getCountryOfResidenceForTax(), is("AU")); //Australian resident for tax purposes? selected as YES with NO Overseas
        assertNull(shareHolderThreePartyDetails.getForeignCountriesForTaxation());
    }

    @Test
    public void shouldPopulateTaxDetailsForIndividualAccountNotHavingCRS() throws IOException {

        IClientApplicationForm clientApplicationForm = getClientApplicationForm("individual.json");
        InvolvedPartyDetailsType partyDetails = new InvolvedPartyDetailsType();
        IExtendedPersonDetailsForm investor = clientApplicationForm.getInvestors().get(0);
        taxFieldsBuilder.populateTax(partyDetails, investor, IClientApplicationForm.AccountType.INDIVIDUAL);

        assertThat(partyDetails.getTFN().size(), is(1));
        assertThat(partyDetails.getTFN().get(0), is("123456782"));
        assertNull(partyDetails.getTFNRegistrationExemption());
        assertThat(partyDetails.getTFNRegistration(), is(ONE));
        assertThat(partyDetails.getCountryOfResidenceForTax(), is("AU"));
        assertNull(partyDetails.getForeignCountriesForTaxation());
    }

    @Test
    public void shouldPopulateTaxDetailsForIndividualAccountNotHavingCRS_withOverridenOverseasTaxCountry() throws IOException {
        IClientApplicationForm clientApplicationForm = getClientApplicationForm("individual_overseas_tax_country.json");
        InvolvedPartyDetailsType partyDetails = new InvolvedPartyDetailsType();
        IExtendedPersonDetailsForm investor = clientApplicationForm.getInvestors().get(0);
        taxFieldsBuilder.populateTax(partyDetails, investor, IClientApplicationForm.AccountType.INDIVIDUAL);

        assertThat(partyDetails.getTFN().size(), is(1));
        assertThat(partyDetails.getTFN().get(0), is("123456782"));
        assertNull(partyDetails.getTFNRegistrationExemption());
        assertThat(partyDetails.getTFNRegistration(), is(ONE));
        assertThat(partyDetails.getCountryOfResidenceForTax(), is("AU"));
        assertNull(partyDetails.getForeignCountriesForTaxation());
    }

    @Test
    public void shouldPopulateTaxDetailsForSuperPensionAccountNotHavingCRS() throws IOException {

        IClientApplicationForm clientApplicationForm = getClientApplicationForm("superpension.json");
        InvolvedPartyDetailsType partyDetails = new InvolvedPartyDetailsType();
        IExtendedPersonDetailsForm investor = clientApplicationForm.getInvestors().get(0);

        taxFieldsBuilder.populateTax(partyDetails, investor, IClientApplicationForm.AccountType.SUPER_PENSION);

        assertThat(partyDetails.getTFN().size(), Matchers.is(1));
        assertThat(partyDetails.getTFN().get(0), is("123456782"));
        assertNull(partyDetails.getTFNRegistrationExemption());
        assertThat(partyDetails.getTFNRegistration(), is(ONE));
        assertThat(partyDetails.getCountryOfResidenceForTax(), is("AU"));
        assertNull(partyDetails.getForeignCountriesForTaxation());
    }

    @Test
    public void shouldPopulateTaxDetailsForSuperPensionAccountNotHavingCRS_withOverseasTaxCountry() throws IOException {
        IClientApplicationForm clientApplicationForm = getClientApplicationForm("superpension_overseas_tax_country.json");
        InvolvedPartyDetailsType partyDetails = new InvolvedPartyDetailsType();
        IExtendedPersonDetailsForm investor = clientApplicationForm.getInvestors().get(0);

        taxFieldsBuilder.populateTax(partyDetails, investor, IClientApplicationForm.AccountType.SUPER_PENSION);

        assertEquals(partyDetails.getTFNRegistrationExemption(), TFNRegistrationExemptionType.PENSIONER_FOR_SUPER);
        assertThat(partyDetails.getTFNRegistration(), is(EXEMPT));
        assertThat(partyDetails.getCountryOfResidenceForTax(), is("CA"));
        assertNull(partyDetails.getForeignCountriesForTaxation());
    }

    @Test
    public void shouldHaveTaxCountryAsAUSWithOneOverSeas() throws IOException {

        IClientApplicationForm clientApplicationForm = getClientApplicationForm("Individual_TaxCountryAusOneOverseasCRS.json");
        InvolvedPartyDetailsType partyDetails = new InvolvedPartyDetailsType();
        IExtendedPersonDetailsForm investor = clientApplicationForm.getInvestors().get(0);

        taxFieldsBuilder.populateTax(partyDetails, investor, IClientApplicationForm.AccountType.INDIVIDUAL);

        assertThat(partyDetails.getCountryOfResidenceForTax(), is("AU"));
        assertNotNull(partyDetails.getForeignCountriesForTaxation());
        ForeignCountryForTaxationType foreignCountryForTaxationType = partyDetails.getForeignCountriesForTaxation().getForeignCountryForTaxation().get(0);
        assertThat(foreignCountryForTaxationType.getCountryCode(), is("HR"));
        assertThat(foreignCountryForTaxationType.getTIN(), is("CROATIAN"));
        assertNull(foreignCountryForTaxationType.getReasonForTaxIdentificationNumberExemption());
    }

    @Test
    public void shouldHaveTaxCountryAsHRWithOneOverSeas() throws IOException {

        IClientApplicationForm clientApplicationForm = getClientApplicationForm("Individual_TaxCountryNonAusOneOverseasCRS.json");
        InvolvedPartyDetailsType partyDetails = new InvolvedPartyDetailsType();
        IExtendedPersonDetailsForm investor = clientApplicationForm.getInvestors().get(0);

        taxFieldsBuilder.populateTax(partyDetails, investor, IClientApplicationForm.AccountType.INDIVIDUAL);

        assertThat(partyDetails.getCountryOfResidenceForTax(), is("HR"));
        assertNotNull(partyDetails.getForeignCountriesForTaxation());
        ForeignCountryForTaxationType foreignCountryForTaxationType = partyDetails.getForeignCountriesForTaxation().getForeignCountryForTaxation().get(0);
        assertThat(foreignCountryForTaxationType.getCountryCode(), is("HR"));
        assertThat(foreignCountryForTaxationType.getTIN(), is("CROATIAN"));
        assertNull(foreignCountryForTaxationType.getReasonForTaxIdentificationNumberExemption());
    }

    @Test
    public void verifyMethodCallsForAccountHavingCRS() throws IOException{
        IClientApplicationForm clientApplicationForm = getClientApplicationForm("client_application_individual_CRS.json");
        InvolvedPartyDetailsType partyDetails = new InvolvedPartyDetailsType();
        IExtendedPersonDetailsForm investor = clientApplicationForm.getInvestors().get(0);

        TaxFieldsBuilder taxBuilder = Mockito.spy(new TaxFieldsBuilder());
        taxBuilder.populateCrsTax(partyDetails, investor, false);
        verify(taxBuilder, times(1)).populateCrsTaxRelatedFieldsForNewInvestor(partyDetails, investor);
        verify(taxBuilder, times(0)).populateTaxRelatedFieldsForNewInvestor(partyDetails, investor);
    }

    @Test
    public void verifyMethodCallsForAccountNotHavingCRS() throws IOException{
        IClientApplicationForm clientApplicationForm = getClientApplicationForm("individual.json");
        InvolvedPartyDetailsType partyDetails = new InvolvedPartyDetailsType();
        IExtendedPersonDetailsForm investor = clientApplicationForm.getInvestors().get(0);

        TaxFieldsBuilder taxBuilder = Mockito.spy(new TaxFieldsBuilder());
        taxBuilder.populateTax(partyDetails, investor, IClientApplicationForm.AccountType.INDIVIDUAL);
        verify(taxBuilder, times(0)).populateCrsTaxRelatedFieldsForNewInvestor(partyDetails, investor);
        verify(taxBuilder, times(1)).populateTaxRelatedFieldsForNewInvestor(partyDetails, investor);
    }


    private IClientApplicationForm getClientApplicationForm(String jsonFile) throws IOException {
        ObjectMapper jsonObjectMapper = new JsonObjectMapper();
        Object formData = jsonObjectMapper.readValue(readJsonStringFromFile(JSON_SCHEMA_PACKAGE + jsonFile), OnboardingApplicationFormData.class);
        return ClientApplicationFormFactoryV1.getNewClientApplicationForm((OnboardingApplicationFormData) formData);
    }

    @Test
    public void shouldPopulateInvestorDetailsWithTaxExemption_CentrelinkBenefit() throws IOException {
        validateTaxExemption("social_ben", TFNRegistrationExemptionType.OTHER_ELIGIBLE_BENEFIT);
    }

    @Test
    public void shouldPopulateInvestorDetailsWithTaxExemption_NotRequiredToLodgeTax() throws IOException {
        validateTaxExemption("tax_exempt", TFNRegistrationExemptionType.NOT_REQUIRED);
    }

    @Test
    public void shouldPopulateInvestorDetailsWithTaxExemption_InvestorsInFinanceBusiness() throws IOException {
        validateTaxExemption("fin_busi_provid", TFNRegistrationExemptionType.FINANCE_PROVIDER);
    }

    @Test
    public void shouldPopulateInvestorDetailsWithTaxExemption_NorfolkResidents() throws IOException {
        validateTaxExemption("norfolk_island_res", NORFOLK_ISLAND_RESIDENT);
    }

    @Test
    public void shouldPopulateInvestorDetailsWithTaxExemption_AlphaNumericTFN() throws IOException {
        validateTaxExemption("alpha_char_tfn", TFNRegistrationExemptionType.ALPHA_TFN_QUOTED);
    }

    @Test
    public void shouldPopulateInvestorDetailsWithTaxExemption_NonResidents() throws IOException {
        validateTaxExemption("non_au_resi", TFNRegistrationExemptionType.NON_RESIDENT);
    }

    @Test
    public void shouldPopulateInvestorDetailsWithTaxExemption_NotRequiredToLodgeTax_afterConvertingUIValue() throws IOException {
        validateTaxExemption("Tax Return Not Needed", TFNRegistrationExemptionType.NOT_REQUIRED);
    }

    @Test
    public void shouldPopulateInvestorDetailsWithTaxExemption_NorfolkResidents_afterConvertingUIValue() throws IOException {
        validateTaxExemption("Norfolk Island resident", NORFOLK_ISLAND_RESIDENT);
    }

    @Test
    public void shouldPopulateInvestorDetailsWithTaxExemption_AlphaNumericTFN_afterConvertingUIValue() throws IOException {
        validateTaxExemption("Alphanumeric TFN", TFNRegistrationExemptionType.ALPHA_TFN_QUOTED);
    }

    @Test
    public void shouldThrowExceptionIfReasonIsNotMappable() throws Exception {
        ITaxDetailsForm form = mock(ITaxDetailsForm.class);
        when(form.hasExemptionReason()).thenReturn(true);
        when(form.getExemptionReason()).thenReturn("illegal_value");
        InvolvedPartyDetailsType investorDetails = new InvolvedPartyDetailsType();
        try {
            taxFieldsBuilder.populateTaxRelatedFieldsForNewInvestor(investorDetails, form);
            fail("should throw exception");
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage(), containsString("Can not map TFN Exemption Reason"));
        }
    }

    @Test
    public void testCorporateTrust_withAdditionalMembers_withOverseasTaxCountry() throws IOException {
        IClientApplicationForm clientApplicationForm = getClientApplicationForm("corporatetrust_family_with_additional_members.json");
        InvolvedPartyDetailsType partyDetails = new InvolvedPartyDetailsType();
        IExtendedPersonDetailsForm investor = clientApplicationForm.getAdditionalShareholdersAndMembers().get(0);

        taxFieldsBuilder.populateCrsTax(partyDetails, investor, false);

        assertThat(partyDetails.getCountryOfResidenceForTax(), is("BZ"));
        assertNotNull(partyDetails.getForeignCountriesForTaxation());
        ForeignCountryForTaxationType foreignCountryForTaxationType = partyDetails.getForeignCountriesForTaxation().getForeignCountryForTaxation().get(0);
        assertThat(foreignCountryForTaxationType.getCountryCode(), is("BZ"));
        assertThat(foreignCountryForTaxationType.getReasonForTaxIdentificationNumberExemption(), is(
            ReasonForTaxIdentificationNumberExemptionType.UNDER_AGE));
        assertNull(foreignCountryForTaxationType.getTIN());
    }


    private void validateTaxExemption(String exemption, TFNRegistrationExemptionType type) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String investorDetailsJson = "{\n" +
                "        \"taxcountry\": \"AU\",\n" +
                "        \"exemptionreason\": \"" + exemption + "\"\n" +
                "    }";

        Map<String, Object> individualInvestorDetailsMap = mapper.readValue(investorDetailsJson, new TypeReference<Map<String, Object>>() {
        });

        InvolvedPartyDetailsType investorDetails = new InvolvedPartyDetailsType();
        taxFieldsBuilder.populateTaxRelatedFieldsForNewInvestor(investorDetails, ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(individualInvestorDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true));

        assertThat(investorDetails.getTFN(), hasSize(0));
        assertEquals(investorDetails.getTFNRegistrationExemption(), type);
        assertEquals(investorDetails.getCountryOfResidenceForTax(), "AU");
    }

    @Test
    public void validateTaxEemptionForSuperPension() throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        String investorDetailsJson = "{\n" +
                "        \"taxoption\": \"pensioner\",\n" +
                "        \"taxcountry\": \"AU\"\n" +
                "    }";

        Map<String, Object> individualInvestorDetailsMap = mapper.readValue(investorDetailsJson, new TypeReference<Map<String, Object>>() {});
        InvolvedPartyDetailsType investorDetails = new InvolvedPartyDetailsType();
        taxFieldsBuilder.populateTaxRelatedFieldsForNewInvestor(investorDetails, ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(individualInvestorDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true));
        assertThat(investorDetails.getTFN(), hasSize(0));
        assertEquals(investorDetails.getTFNRegistrationExemption(), TFNRegistrationExemptionType.PENSIONER_FOR_SUPER);
        assertEquals(investorDetails.getCountryOfResidenceForTax(), "AU");
    }

    @Test
    public void validatateTaxDetailsForExistingIDPSInvestor_SuperPension() throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        String investorDetailsJson = "{\n" +
                "   \"taxoption\": \"pensioner\",\n" +
                "   \"tfnExemptId\": \"99\",\n" +
                "   \"exemptionReason\": \"No exemption\"\n" +
                "    }";

        Map<String, Object> individualInvestorDetailsMap = mapper.readValue(investorDetailsJson, new TypeReference<Map<String, Object>>() {});
        InvolvedPartyDetailsType investorDetails = new InvolvedPartyDetailsType();
        taxFieldsBuilder.populateTaxRelatedFields(investorDetails, ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(individualInvestorDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true),true);
        assertThat(investorDetails.getTFN(), hasSize(0));
        assertEquals(investorDetails.getTFNRegistrationExemption(), TFNRegistrationExemptionType.PENSIONER_FOR_SUPER);
        assertNull(investorDetails.getCountryOfResidenceForTax());
    }

    @Test
    public void validatateTaxDetailsForExistingSuperInvestor_SuperPension() throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        String investorDetailsJson = "{\n" +
                "   \"taxoption\": \"Tax File Number or exemption not provided\",\n" +
                "   \"tfnExemptId\": \"99\",\n" +
                "   \"exemptionReason\": \"No exemption\"\n" +
                "    }";

        Map<String, Object> individualInvestorDetailsMap = mapper.readValue(investorDetailsJson, new TypeReference<Map<String, Object>>() {});
        InvolvedPartyDetailsType investorDetails = new InvolvedPartyDetailsType();
        taxFieldsBuilder.populateTaxRelatedFields(investorDetails, ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(individualInvestorDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true), true);
        assertThat(investorDetails.getTFN(), hasSize(0));
        assertNull(investorDetails.getTFNRegistrationExemption());
        assertThat(investorDetails.getTFNRegistration().value(),is(NONE.value()));
        assertNull(investorDetails.getCountryOfResidenceForTax());
    }

    @Test
    public void validatePopulateCRSTaxRelatedFieldsWithTFN(){

        when(crsTaxDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        when(ausTaxDetailsForm.getTaxOption()).thenReturn(TAX_FILE_NUMBER_PROVIDED);
        when(ausTaxDetailsForm.getTFN()).thenReturn("AUSTFN");

        taxFieldsBuilder.populateCRSTaxRelatedFields(investorDetails, crsTaxDetailsForm, existingInvestor);
        assertThat(investorDetails.getTFN().get(0), is("AUSTFN"));
        assertThat(investorDetails.getTFNRegistration(), is(ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.TFNRegistrationType.ONE.value()));
        assertNull(investorDetails.getTFNRegistrationExemption());
    }

    @Test
    public void validatePopulateCRSTaxRelatedFieldsWithNoTFN(){
        when(crsTaxDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        when(ausTaxDetailsForm.getTaxOption()).thenReturn(TAX_FILE_NUMBER_PROVIDED);
        when(ausTaxDetailsForm.getTFN()).thenReturn(null);
        when(ausTaxDetailsForm.isTaxCountryAustralia()).thenReturn(true);


        taxFieldsBuilder.populateCRSTaxRelatedFields(investorDetails, crsTaxDetailsForm, existingInvestor);

        assertThat(investorDetails.getTFN().size(), is(0));
        assertThat(investorDetails.getTFNRegistration(), is(NONE));
        assertThat(investorDetails.getTFNRegistrationExemption().value(),is(TFN_NOT_QUOTED.value()));
    }

    @Test
    public void validatePopulateCRSTaxRelatedFieldsWithTFN_ExemptionAsPensioner(){
        when(crsTaxDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        when(ausTaxDetailsForm.getTaxOption()).thenReturn(PENSIONER);
        when(ausTaxDetailsForm.getTFN()).thenReturn(null);

        taxFieldsBuilder.populateCRSTaxRelatedFields(investorDetails, crsTaxDetailsForm, existingInvestor);
        assertTrue(CollectionUtils.isEmpty(investorDetails.getTFN()));
        assertThat(investorDetails.getTFNRegistration(), is(EXEMPT));
        assertThat(investorDetails.getTFNRegistrationExemption(), is(TFNRegistrationExemptionType.PENSIONER_FOR_SUPER));
    }

    @Test
    public void validatePopulateCRSTaxRelatedFieldsWith_NoExemption(){
        when(crsTaxDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        when(ausTaxDetailsForm.getTFN()).thenReturn(null);
        when(ausTaxDetailsForm.getExemptionReason()).thenReturn("norfolk_island_res");
        when(ausTaxDetailsForm.hasExemptionReason()).thenReturn(true);


        taxFieldsBuilder.populateCRSTaxRelatedFields(investorDetails, crsTaxDetailsForm, existingInvestor);
        assertTrue(CollectionUtils.isEmpty(investorDetails.getTFN()));
        assertThat(investorDetails.getTFNRegistration(), is(EXEMPT));
        assertThat(investorDetails.getTFNRegistrationExemption(), is(TFNRegistrationExemptionType.NORFOLK_ISLAND_RESIDENT));
    }

    @Test
    public void validatePopulateCRSTaxRelatedFieldsWithNoTFNExemption(){
        when(crsTaxDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        when(ausTaxDetailsForm.getTaxOption()).thenReturn(TAX_FILE_NUMBER_OR_EXEMPTION_NOT_PROVIDED);
        when(ausTaxDetailsForm.isTaxCountryAustralia()).thenReturn(true);

        taxFieldsBuilder.populateCRSTaxRelatedFields(investorDetails, crsTaxDetailsForm, existingInvestor);
        assertTrue(CollectionUtils.isEmpty(investorDetails.getTFN()));
        assertThat(investorDetails.getTFNRegistration(), is(NONE));
        assertThat(investorDetails.getTFNRegistrationExemption().value(),is(TFN_NOT_QUOTED.value()));
    }

    @Test
    public void validatePopulateCRSTaxRelatedFieldsWithNoTFNExemptionAndOverseasCountry(){
        when(crsTaxDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        when(ausTaxDetailsForm.getTaxOption()).thenReturn(TAX_FILE_NUMBER_OR_EXEMPTION_NOT_PROVIDED);
        when(ausTaxDetailsForm.isTaxCountryAustralia()).thenReturn(false);

        taxFieldsBuilder.populateCRSTaxRelatedFields(investorDetails, crsTaxDetailsForm, existingInvestor);
        assertTrue(CollectionUtils.isEmpty(investorDetails.getTFN()));
        assertThat(investorDetails.getTFNRegistration(), is(NONE));
        assertNull(investorDetails.getTFNRegistrationExemption());
    }

    @Test
    public void validatePopulateCRSTaxRelatedFieldsForLegalEntityWithAus(){
        when(crsTaxDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        when(ausTaxDetailsForm.getTaxOption()).thenReturn(null);
        when(ausTaxDetailsForm.isTaxCountryAustralia()).thenReturn(true);

        taxFieldsBuilder.populateCRSTaxRelatedFields(investorDetails, crsTaxDetailsForm, existingInvestor);
        assertTrue(CollectionUtils.isEmpty(investorDetails.getTFN()));
        assertThat(investorDetails.getTFNRegistration(), is(NONE));
        assertNull(investorDetails.getTFNRegistrationExemption());
    }

    @Test
    public void validatePopulateCRSTaxRelatedFieldsWithExistingInvestorFalse(){
        when(crsTaxDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        existingInvestor = false;

        taxFieldsBuilder.populateCRSTaxRelatedFields(investorDetails, crsTaxDetailsForm, existingInvestor);
        assertTrue(CollectionUtils.isEmpty(investorDetails.getTFN()));
        assertThat(investorDetails.getTFNRegistration(), is(NONE));
    }


}
