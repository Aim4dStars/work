package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.client.model.AddressTypeV2;
import com.bt.nextgen.api.client.model.CompanyDto;
import com.bt.nextgen.api.client.model.RegisteredEntityDto;
import com.bt.nextgen.api.client.model.SmsfDto;
import com.bt.nextgen.api.client.model.TrustDto;
import com.bt.nextgen.api.draftaccount.AbstractJsonReaderTest;
import com.bt.nextgen.api.draftaccount.model.form.ClientApplicationFormFactory;
import com.bt.nextgen.api.draftaccount.model.form.IAddressForm;
import com.bt.nextgen.api.draftaccount.model.form.IAusTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.ICompanyForm;
import com.bt.nextgen.api.draftaccount.model.form.ICrsTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm;
import com.bt.nextgen.api.draftaccount.model.form.IOverseasTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IShareholderAndMembersForm;
import com.bt.nextgen.api.draftaccount.model.form.ISmsfForm;
import com.bt.nextgen.api.draftaccount.model.form.ITaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.ITrustForm;
import com.bt.nextgen.api.draftaccount.model.form.v1.ClientApplicationFormFactoryV1;
import com.bt.nextgen.api.draftaccount.model.form.v1.CrsTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.TaxOptionTypeEnum;
import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganizationDtoConverterTest extends AbstractJsonReaderTest {

    @InjectMocks
    OrganizationDtoConverter organizationDtoConverter;

    @Mock
    private AddressDtoConverter addressDtoConverter;

    @Mock
    private CRSTaxDetailHelperService crsTaxDetailHelperService;

    @Mock
    ClientApplicationDetailsDtoHelperService clientApplicationDetailsDtoHelperService;

    @Mock
    private StaticIntegrationService staticService;


    private ICrsTaxDetailsForm crsTaxDetailsForm = mock(CrsTaxDetailsForm.class);
    private IAusTaxDetailsForm ausTaxDetailsForm = mock(IAusTaxDetailsForm.class);

    private Collection<Code> taxExemmptionList;
    private Collection<Code> pensionExemptionReasonList;

    private JsonObjectMapper mapper;

    private String anzicId = "7340";
    private String industryName = "The Industry";
    private String businessName = "The Serious Business";

    @Before
    public void setUp() {
        mapper = new JsonObjectMapper();
        Code code = new CodeImpl("code-id", "user-id", industryName);
        when(staticService.loadCodeByUserId(eq(CodeCategory.ANZSIC_INDUSTRY), eq(anzicId), any(ServiceErrors.class))).thenReturn(code);
        taxExemmptionList = new ArrayList <Code>();
        pensionExemptionReasonList = new ArrayList<>();
        taxExemmptionList.add(new CodeImpl("3", "social_ben", "Recipient of other eligible Centrelink pension or benefit"));
        taxExemmptionList.add(new CodeImpl("6", "norfolk_island_res", "Norfolk Island resident"));
        pensionExemptionReasonList.add(new CodeImpl("2", "pensioner", "Pensionaer"));
        when(staticService.loadCodes(CodeCategory.EXEMPTION_REASON,null)).thenReturn(taxExemmptionList);
        when(staticService.loadCodes(CodeCategory.PENSION_EXEMPTION_REASON,null)).thenReturn(pensionExemptionReasonList);
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertIndividualTrustDetails_AndReturnATrustDto() throws Exception {
        IClientApplicationForm form = individualTrustApplicationForm();

        RegisteredEntityDto trust = organizationDtoConverter.convertFromOrganizationForm(form);

        assertNotNull(trust);

    }

    @Test
    public void convertFromOrganisationForm_ShouldSetAddressV2ForTrust() throws IOException {
        Object formData = mapper.readValue(readJsonStringFromFile("client_application_indtrust_formv2.json"), OnboardingApplicationFormData.class);
        IClientApplicationForm clientApplicationForm = ClientApplicationFormFactoryV1.getNewClientApplicationForm((OnboardingApplicationFormData) formData);
        RegisteredEntityDto trust = organizationDtoConverter.convertFromOrganizationForm(clientApplicationForm);
        assertThat(trust.getAddressesV2().get(0).getAddressDisplayText(),is("Unit 15  1 Clarence Street, STRATHFIELD  NSW  2135"));
        assertThat(trust.getAddressesV2().get(0).getAddressType(),is(AddressTypeV2.REGISTERED));
        assertNull(trust.getTaxResidenceCountries());
        verify(crsTaxDetailHelperService, times(1)).populateCRSTaxDetailsForOrganization(any(IOrganisationForm.class),any(RegisteredEntityDto.class));

    }

    @Test
    public void convertFromOrganisationForm_ShouldSetAddressV2ForSMSF() throws IOException {
        Object formData = mapper.readValue(readJsonStringFromFile("client_application_smsf_addressv2.json"), OnboardingApplicationFormData.class);
        IClientApplicationForm clientApplicationForm = ClientApplicationFormFactoryV1.getNewClientApplicationForm((OnboardingApplicationFormData) formData);
        RegisteredEntityDto smsf = organizationDtoConverter.convertFromOrganizationForm(clientApplicationForm);
        assertThat(smsf.getAddressesV2().get(0).getAddressDisplayText(),is("33 Pitt Town Road, MCGRATHS HILL  NSW  2756"));
        assertThat(smsf.getAddressesV2().get(0).getAddressType(),is(AddressTypeV2.REGISTERED));
        assertNull(smsf.getTaxResidenceCountries());
        verify(crsTaxDetailHelperService, times(0)).populateCRSTaxDetailsForOrganization(any(IOrganisationForm.class),any(RegisteredEntityDto.class));
    }

    @Test
    public void convertFromOrganisationForm_ShouldSetAddressV2ForCompany() throws IOException {
        Object formData = mapper.readValue(readJsonStringFromFile("client_application_company_addressv2.json"), OnboardingApplicationFormData.class);
        IClientApplicationForm clientApplicationForm = ClientApplicationFormFactoryV1.getNewClientApplicationForm((OnboardingApplicationFormData) formData);
        RegisteredEntityDto company = organizationDtoConverter.convertFromOrganizationForm(clientApplicationForm);
        assertThat(company.getAddressesV2().get(0).getAddressDisplayText(),is("5 Lahore Street, CRESTMEAD  QLD  4132"));
        assertThat(company.getAddressesV2().get(0).getAddressType(),is(AddressTypeV2.REGISTERED));
        assertThat(company.getAddressesV2().get(1).getAddressDisplayText(),is("5 Lahore Street, CRESTMEAD  QLD  4132"));
        assertThat(company.getAddressesV2().get(1).getAddressType(),is(AddressTypeV2.PLACEOFBUSINESS));
        verify(crsTaxDetailHelperService, times(0)).populateCRSTaxDetailsForOrganization(any(IOrganisationForm.class),any(RegisteredEntityDto.class));

    }

    @Test
    public void convertFromOrganisationForm_ShouldSetCompanyDetailsCMA() throws IOException{
        Object formData =  mapper.readValue(readJsonStringFromFile("client_application_company_addressv2.json"),OnboardingApplicationFormData.class);
        IClientApplicationForm iClientApplicationForm = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        RegisteredEntityDto registeredEntityDto = organizationDtoConverter.convertFromOrganizationForm(iClientApplicationForm);
        assertTrue(registeredEntityDto instanceof CompanyDto);
        assertThat(((CompanyDto)registeredEntityDto).getPersonalInvestmentEntity(),is("Yes"));
        assertNull(((CompanyDto)registeredEntityDto).getTaxResidenceCountries());
        verify(crsTaxDetailHelperService, times(0)).populateCRSTaxDetailsForOrganization(any(IOrganisationForm.class),any(RegisteredEntityDto.class));

    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertIndividualTrustDetails_AndReturnATrustDtoWithBusinessName() throws Exception {
        IClientApplicationForm form = individualTrustApplicationForm();
        TrustDto trust = (TrustDto)organizationDtoConverter.convertFromOrganizationForm(form);
        assertEquals(trust.getBusinessName(), businessName);

    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertCorporateTrustDetails_AndReturnATrustDto() throws Exception {
        IClientApplicationForm form = corporateTrustApplicationForm();

        RegisteredEntityDto trust = organizationDtoConverter.convertFromOrganizationForm(form);

        assertNotNull(trust);

    }


    @Test
    public void convertFromOrganisationForm_ShouldConvertCorporateTrustDetails_AndReturnATrustDtoWithBusinessName() throws Exception {
        IClientApplicationForm form = corporateTrustApplicationForm();
        TrustDto trust = (TrustDto)organizationDtoConverter.convertFromOrganizationForm(form);
        assertEquals(trust.getBusinessName(), businessName);
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertCorporateTrustDetails_shouldReturnTrustDtoWithOnlyOneAddress() throws Exception {
        IClientApplicationForm form = corporateTrustApplicationForm();

        RegisteredEntityDto trust = organizationDtoConverter.convertFromOrganizationForm(form);

        assertThat(trust.getAddresses().size(), is(1));
    }


    @Test
    public void convertFromOrganisationForm_ShouldConvertCorporateTrustDetails_AndReturnATrustDtoWithCompanyDto() throws Exception {
        String companyName = "Any-Company";
        IClientApplicationForm form = corporateTrustApplicationForm();
        ICompanyForm company = form.getCompanyTrustee();
        when(company.getName()).thenReturn(companyName);

        TrustDto trust = (TrustDto)organizationDtoConverter.convertFromOrganizationForm(form);
        assertThat(trust.getCompany().getFullName(), is(companyName));
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertCorporateTrustDetails_forCMA_Family() throws Exception {
        String companyName = "Any-Company";
        IClientApplicationForm form = corporateTrustApplicationForm_Family();
        TrustDto trust = (TrustDto)organizationDtoConverter.convertFromOrganizationForm(form);
        assertThat(trust.getPersonalInvestmentEntity(),is("Yes"));

    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertCorporateTrustDetails_forCMA_Family_Invalid() throws Exception {
        String companyName = "Any-Company";
        IClientApplicationForm form = corporateTrustApplicationForm_InvalidData();
        TrustDto trust = (TrustDto)organizationDtoConverter.convertFromOrganizationForm(form);
        assertNull(trust.getPersonalInvestmentEntity());

    }

    @Test
    public void convertFromOrganisationForm_ShouldNotPopulatedCMADetails_Regulated() throws Exception {
        String companyName = "Any-Company";
        IClientApplicationForm form = corporateTrustApplicationForm();
        ICompanyForm company = form.getCompanyTrustee();
        when(company.getName()).thenReturn(companyName);
        TrustDto trust = (TrustDto)organizationDtoConverter.convertFromOrganizationForm(form);
        assertNull(trust.getPersonalInvestmentEntity());

    }
    public void convertFromOrganisationForm_ShouldNotPopulateCorporateTrustDetails_forCMA_GovtSuper() throws Exception {
        String companyName = "Any-Company";
        IClientApplicationForm form = corporateTrustApplicationForm_GovtSuper();
        TrustDto trust = (TrustDto)organizationDtoConverter.convertFromOrganizationForm(form);
        assertNull(trust.getPersonalInvestmentEntity());
    }

    private IClientApplicationForm corporateTrustApplicationForm_GovtSuper() {
        XMLGregorianCalendar registeredDate = XMLGregorianCalendarUtil.getXMLGregorianCalendar("10/10/2000", "dd/MM/yyyy");
        IClientApplicationForm form = mock(IClientApplicationForm.class);
        when(form.getAccountType()).thenReturn(IClientApplicationForm.AccountType.CORPORATE_TRUST);
        when(form.getShareholderAndMembers()).thenReturn(mock(IShareholderAndMembersForm.class));
        IAddressForm addressForm = mock(IAddressForm.class);
        when(addressForm.getAddressIdentifier()).thenReturn(null);

        ITrustForm trustForm = mock(ITrustForm.class);
        when(trustForm.getTrustType()).thenReturn(ITrustForm.TrustType.GOVT_SUPER);
        when(trustForm.getAnzsicCode()).thenReturn(anzicId);
        when(trustForm.getDateOfRegistration()).thenReturn(registeredDate);
        when(trustForm.getBusinessName()).thenReturn(businessName);
        when(trustForm.getPersonalInvestmentEntity()).thenReturn(true);
        when(trustForm.getTaxDetails()).thenReturn(mock(ITaxDetailsForm.class));
        when(trustForm.getRegisteredAddress()).thenReturn(addressForm);
        when(form.getTrust()).thenReturn(trustForm);
        ICompanyForm company = mock(ICompanyForm.class);
        when(company.getAnzsicCode()).thenReturn(anzicId);
        when(company.getRegisteredAddress()).thenReturn(addressForm);
        when(company.getPlaceOfBusinessAddress()).thenReturn(addressForm);
        when(form.getCompanyTrustee()).thenReturn(company);
        return form;
    }

    private IClientApplicationForm corporateTrustApplicationForm() {
        XMLGregorianCalendar registeredDate = XMLGregorianCalendarUtil.getXMLGregorianCalendar("10/10/2000", "dd/MM/yyyy");
        IClientApplicationForm form = mock(IClientApplicationForm.class);

        when(form.getAccountType()).thenReturn(IClientApplicationForm.AccountType.CORPORATE_TRUST);
        when(form.getShareholderAndMembers()).thenReturn(mock(IShareholderAndMembersForm.class));

        IAddressForm addressForm = mock(IAddressForm.class);
        when(addressForm.getAddressIdentifier()).thenReturn(null);

        ITrustForm trustForm = mock(ITrustForm.class);
        when(trustForm.getTrustType()).thenReturn(ITrustForm.TrustType.REGULATED);
        when(trustForm.getAnzsicCode()).thenReturn(anzicId);
        when(trustForm.getDateOfRegistration()).thenReturn(registeredDate);
        when(trustForm.getBusinessName()).thenReturn(businessName);
        when(trustForm.getPersonalInvestmentEntity()).thenReturn(true);
        ICompanyForm company = mock(ICompanyForm.class);
        when(company.getAnzsicCode()).thenReturn(anzicId);
        when(company.getRegisteredAddress()).thenReturn(addressForm);
        when(company.getPlaceOfBusinessAddress()).thenReturn(addressForm);
        when(form.getCompanyTrustee()).thenReturn(company);


        ITaxDetailsForm taxDetails = mock(ITaxDetailsForm.class);
        when(trustForm.getTaxDetails()).thenReturn(taxDetails);

        when(trustForm.getRegisteredAddress()).thenReturn(addressForm);
        when(form.getTrust()).thenReturn(trustForm);
        return form;
    }

    private IClientApplicationForm corporateTrustApplicationForm_withCRSData() {
        XMLGregorianCalendar registeredDate = XMLGregorianCalendarUtil.getXMLGregorianCalendar("10/10/2000", "dd/MM/yyyy");
        IClientApplicationForm form = mock(IClientApplicationForm.class);

        when(form.getAccountType()).thenReturn(IClientApplicationForm.AccountType.CORPORATE_TRUST);
        when(form.getShareholderAndMembers()).thenReturn(mock(IShareholderAndMembersForm.class));

        IAddressForm addressForm = mock(IAddressForm.class);
        when(addressForm.getAddressIdentifier()).thenReturn(null);

        ITrustForm trustForm = mock(ITrustForm.class);
        when(trustForm.getTrustType()).thenReturn(ITrustForm.TrustType.REGULATED);
        when(trustForm.getAnzsicCode()).thenReturn(anzicId);
        when(trustForm.getDateOfRegistration()).thenReturn(registeredDate);
        when(trustForm.getBusinessName()).thenReturn(businessName);
        when(trustForm.getPersonalInvestmentEntity()).thenReturn(true);
        ICompanyForm company = mock(ICompanyForm.class);
        when(company.getAnzsicCode()).thenReturn(anzicId);
        when(company.getRegisteredAddress()).thenReturn(addressForm);
        when(company.getPlaceOfBusinessAddress()).thenReturn(addressForm);


        when(crsTaxDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        List<IOverseasTaxDetailsForm> taxDetailsForms = new ArrayList<>();
        when(crsTaxDetailsForm.getOverseasTaxDetails()).thenReturn(taxDetailsForms);
        when(ausTaxDetailsForm.isTaxCountryAustralia()).thenReturn(true);
        when(crsTaxDetailsForm.hasOverseasTaxCountry()).thenReturn(false);
        when(crsTaxDetailsForm.getAustralianTaxDetails().getExemptionReason()).thenReturn("social_ben");
        when(crsTaxDetailsForm.getAustralianTaxDetails().getTaxOption()).thenReturn(TaxOptionTypeEnum.EXEMPTION_REASON_PROVIDED);
        when(trustForm.getCrsTaxDetails()).thenReturn(crsTaxDetailsForm);
        when(company.getCrsTaxDetails()).thenReturn(crsTaxDetailsForm);

        when(trustForm.getRegisteredAddress()).thenReturn(addressForm);
        when(form.getCompanyTrustee()).thenReturn(company);
        when(form.getTrust()).thenReturn(trustForm);
        return form;
    }

    private IClientApplicationForm corporateTrustApplicationForm_Family() {
        XMLGregorianCalendar registeredDate = XMLGregorianCalendarUtil.getXMLGregorianCalendar("10/10/2000", "dd/MM/yyyy");
        IClientApplicationForm form = mock(IClientApplicationForm.class);

        when(form.getAccountType()).thenReturn(IClientApplicationForm.AccountType.CORPORATE_TRUST);
        when(form.getShareholderAndMembers()).thenReturn(mock(IShareholderAndMembersForm.class));

        IAddressForm addressForm = mock(IAddressForm.class);
        when(addressForm.getAddressIdentifier()).thenReturn(null);

        ITrustForm trustForm = mock(ITrustForm.class);
        when(trustForm.getTrustType()).thenReturn(ITrustForm.TrustType.FAMILY);
        when(trustForm.getAnzsicCode()).thenReturn(anzicId);
        when(trustForm.getDateOfRegistration()).thenReturn(registeredDate);
        when(trustForm.getBusinessName()).thenReturn(businessName);
        when(trustForm.getPersonalInvestmentEntity()).thenReturn(true);
        ICompanyForm company = mock(ICompanyForm.class);
        when(company.getAnzsicCode()).thenReturn(anzicId);
        when(company.getRegisteredAddress()).thenReturn(addressForm);
        when(company.getPlaceOfBusinessAddress()).thenReturn(addressForm);
        when(form.getCompanyTrustee()).thenReturn(company);


        ITaxDetailsForm taxDetails = mock(ITaxDetailsForm.class);
        when(trustForm.getTaxDetails()).thenReturn(taxDetails);

        when(trustForm.getRegisteredAddress()).thenReturn(addressForm);
        when(form.getTrust()).thenReturn(trustForm);
        return form;
    }


    private IClientApplicationForm corporateTrustApplicationForm_InvalidData() {
        XMLGregorianCalendar registeredDate = XMLGregorianCalendarUtil.getXMLGregorianCalendar("10/10/2000", "dd/MM/yyyy");
        IClientApplicationForm form = mock(IClientApplicationForm.class);

        when(form.getAccountType()).thenReturn(IClientApplicationForm.AccountType.CORPORATE_TRUST);
        when(form.getShareholderAndMembers()).thenReturn(mock(IShareholderAndMembersForm.class));

        IAddressForm addressForm = mock(IAddressForm.class);
        when(addressForm.getAddressIdentifier()).thenReturn(null);

        ITrustForm trustForm = mock(ITrustForm.class);
        when(trustForm.getTrustType()).thenReturn(ITrustForm.TrustType.FAMILY);
        when(trustForm.getAnzsicCode()).thenReturn(anzicId);
        when(trustForm.getDateOfRegistration()).thenReturn(registeredDate);
        when(trustForm.getBusinessName()).thenReturn(businessName);
        when(trustForm.getPersonalInvestmentEntity()).thenReturn(null);
        ICompanyForm company = mock(ICompanyForm.class);
        when(company.getAnzsicCode()).thenReturn(anzicId);
        when(company.getRegisteredAddress()).thenReturn(addressForm);
        when(company.getPlaceOfBusinessAddress()).thenReturn(addressForm);
        when(form.getCompanyTrustee()).thenReturn(company);


        ITaxDetailsForm taxDetails = mock(ITaxDetailsForm.class);
        when(trustForm.getTaxDetails()).thenReturn(taxDetails);

        when(trustForm.getRegisteredAddress()).thenReturn(addressForm);
        when(form.getTrust()).thenReturn(trustForm);
        return form;
    }


    public void convertFromOrganisationForm_ShouldConvertCompanyDetails_andReturnACompanyDto() throws Exception {
        IClientApplicationForm form = companyApplicationForm();
        CompanyDto company = (CompanyDto) organizationDtoConverter.convertFromOrganizationForm(form);

        assertNotNull(company);
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertCompanyDetails_andReturnACompanyDtoWithCompanyName() throws Exception {
        IClientApplicationForm form = companyApplicationForm();
        String companyName = "crazyCompany";
        ICompanyForm companyDetails = form.getCompanyDetails();
        when(companyDetails.getName()).thenReturn(companyName);

        CompanyDto company = (CompanyDto) organizationDtoConverter.convertFromOrganizationForm(form);

        assertThat(company.getFullName(), is(companyName));
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertCompanyDetails_andReturnACompanyDtoWithAsicName() throws Exception {
        IClientApplicationForm form = companyApplicationForm();
        String asicName = "asicName";
        ICompanyForm companyDetails = form.getCompanyDetails();
        when(companyDetails.getAsicName()).thenReturn(asicName);

        CompanyDto company = (CompanyDto) organizationDtoConverter.convertFromOrganizationForm(form);

        assertThat(company.getAsicName(), is(asicName));
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertCompanyDetails_andReturnACompanyDtoWithAcn() throws Exception {
        IClientApplicationForm form = companyApplicationForm();
        String acn = "000";
        ICompanyForm companyDetails = form.getCompanyDetails();
        when(companyDetails.getACN()).thenReturn(acn);

        CompanyDto company = (CompanyDto) organizationDtoConverter.convertFromOrganizationForm(form);

        assertThat(company.getAcn(), is(acn));
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertCompanyDetails_andReturnACompanyDtoWithAbn() throws Exception {
        IClientApplicationForm form = companyApplicationForm();
        String abn = "000";
        ICompanyForm companyDetails = form.getCompanyDetails();
        when(companyDetails.getABN()).thenReturn(abn);

        CompanyDto company = (CompanyDto) organizationDtoConverter.convertFromOrganizationForm(form);

        assertThat(company.getAbn(), is(abn));
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertCompanyDetails_andReturnACompanyDtoWithIndustry() throws Exception {
        IClientApplicationForm form = companyApplicationForm();

        CompanyDto company = (CompanyDto) organizationDtoConverter.convertFromOrganizationForm(form);

        assertThat(company.getIndustry(), is(industryName));
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertCompanyDetails_andReturnACompanyDtoWithRegisteredForGst() throws Exception {
        IClientApplicationForm form = companyApplicationForm();
        ICompanyForm companyDetails = form.getCompanyDetails();
        when(companyDetails.getRegisteredForGST()).thenReturn(true);
        CompanyDto company = (CompanyDto) organizationDtoConverter.convertFromOrganizationForm(form);

        assertThat(company.isRegistrationForGst(), is(true));
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertCompanyDetails_andReturnACompanyDtoWithDocumentIssuer() throws Exception {
        CompanyDto company = (CompanyDto) organizationDtoConverter.convertFromOrganizationForm(companyApplicationForm());

        assertThat(company.getIdvs(), is("Verified"));
    }


    @Test
    public void convertFromOrganisationForm_ShouldConvertCompanyDetails_andReturnACompanyDtoWithTwoAddresses() throws Exception {
        CompanyDto company = (CompanyDto) organizationDtoConverter.convertFromOrganizationForm(companyApplicationForm());

        assertThat(company.getAddresses().size(), is(2));
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertTrustDetails_AndReturnTrustDtoWithTrustName() throws Exception {
        String trustName = "Any-Trust-Name";
        IClientApplicationForm form = individualTrustApplicationForm();

        ITrustForm trustForm = form.getTrust();
        when(trustForm.getName()).thenReturn(trustName);
        when(form.getTrust()).thenReturn(trustForm);

        RegisteredEntityDto trust = organizationDtoConverter.convertFromOrganizationForm(form);

        assertThat(trust.getFullName(), is(trustName));
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertTrustDetails_AndReturnTrustDtoWithAbn() throws Exception {
        String abn = "Any-Abn";
        IClientApplicationForm form = individualTrustApplicationForm();

        ITrustForm trustForm = form.getTrust();
        when(trustForm.getABN()).thenReturn(abn);
        when(form.getTrust()).thenReturn(trustForm);

        RegisteredEntityDto trust = organizationDtoConverter.convertFromOrganizationForm(form);

        assertThat(trust.getAbn(), is(abn));
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertTrustDetails_AndReturnTrustDtoWithOnlyOneAddress() throws Exception {
        String abn = "Any-Abn";
        IClientApplicationForm form = individualTrustApplicationForm();

        ITrustForm trustForm = form.getTrust();
        when(trustForm.getABN()).thenReturn(abn);
        when(form.getTrust()).thenReturn(trustForm);

        RegisteredEntityDto trust = organizationDtoConverter.convertFromOrganizationForm(form);

        assertThat(trust.getAddresses().size(), is(1));
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertTrustDetails_ForRegisteredManagedInvSch_AndReturnTrustDtoWithArsn() throws Exception {
        String arsn = "Any-Arsn";
        IClientApplicationForm form = individualTrustApplicationForm();

        ITrustForm trustForm = form.getTrust();
        when(trustForm.getTrustType()).thenReturn(ITrustForm.TrustType.REGISTERED_MIS);
        when(trustForm.getArsn()).thenReturn(arsn);
        when(form.getTrust()).thenReturn(trustForm);

        TrustDto trust = (TrustDto)organizationDtoConverter.convertFromOrganizationForm(form);
        assertThat(trust.getTrustType(), is(ITrustForm.TrustType.REGISTERED_MIS.value()));
        assertThat(trust.getArsn(), is(arsn));
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertTrustDetails_ForRegulatedTrust_AndReturnTrustDtoWithRegulatorAndLicesingNumber() throws Exception {
        String regulator = "Any-Regulator";
        String licensingNumber = "Any-Licensing-Number";
        IClientApplicationForm form = individualTrustApplicationForm();

        ITrustForm trustForm = form.getTrust();
        when(trustForm.getTrustType()).thenReturn(ITrustForm.TrustType.REGULATED);
        when(trustForm.getRegulatorName()).thenReturn(regulator);
        when(trustForm.getRegulatorLicenseNumber()).thenReturn(licensingNumber);
        when(form.getTrust()).thenReturn(trustForm);

        TrustDto trust = (TrustDto)organizationDtoConverter.convertFromOrganizationForm(form);
        assertThat(trust.getTrustType(), is(ITrustForm.TrustType.REGULATED.value()));
        assertThat(trust.getTrustReguName(), is(regulator));
        assertThat(trust.getLicencingNumber(), is(licensingNumber));
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertTrustDetails_ForFamilyOrOtherTrust_AndReturnTrustDtoWithTrustDescription() throws Exception {
        String description = "trust-description";
        IClientApplicationForm form = individualTrustApplicationForm();

        ITrustForm trustForm = form.getTrust();
        when(trustForm.getTrustType()).thenReturn(ITrustForm.TrustType.FAMILY);
        when(trustForm.getDescription()).thenReturn(description);
        when(form.getTrust()).thenReturn(trustForm);

        TrustDto trust = (TrustDto)organizationDtoConverter.convertFromOrganizationForm(form);
        assertThat(trust.getTrustType(), is(ITrustForm.TrustType.FAMILY.value()));
        assertThat(trust.getBusinessClassificationDesc(), is(description));
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertTrustDetails_ForGovtSuperFund_AndReturnTrustDtoWithLegislationEstTheFund() throws Exception {
        String legislationEstFund = "Any-Legislation-Name";
        IClientApplicationForm form = individualTrustApplicationForm();

        ITrustForm trustForm = form.getTrust();
        when(trustForm.getTrustType()).thenReturn(ITrustForm.TrustType.GOVT_SUPER);
        when(trustForm.getNameOfLegislation()).thenReturn(legislationEstFund);
        when(form.getTrust()).thenReturn(trustForm);

        TrustDto trust = (TrustDto)organizationDtoConverter.convertFromOrganizationForm(form);
        assertThat(trust.getTrustType(), is(ITrustForm.TrustType.GOVT_SUPER.value()));
        assertThat(trust.getLegEstFund(), is(legislationEstFund));
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertTrustDetails_AndReturnTrustDtoWithBeneficiaryClass() throws Exception {
        String beneficiaryClass = "Any-Beneficiary-Class";
        IClientApplicationForm form = individualTrustApplicationForm();

        IShareholderAndMembersForm shareholderMember = form.getShareholderAndMembers();
        when(shareholderMember.getBeneficiaryClassDetails()).thenReturn(beneficiaryClass);

        TrustDto trust = (TrustDto)organizationDtoConverter.convertFromOrganizationForm(form);

        assertThat(trust.getTrustMemberClass(), is(beneficiaryClass));
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertTrustDetails_AndReturnTrustDtoWithTrustType() throws Exception {
        IClientApplicationForm form = individualTrustApplicationForm();

        ITrustForm trustForm = form.getTrust();
        when(trustForm.getTrustType()).thenReturn(ITrustForm.TrustType.REGISTERED_MIS);

        TrustDto trust = (TrustDto)organizationDtoConverter.convertFromOrganizationForm(form);

        assertThat(trust.getTrustType(), is(ITrustForm.TrustType.REGISTERED_MIS.value()));
    }




    private IClientApplicationForm companyApplicationForm() {
        IClientApplicationForm form = mock(IClientApplicationForm.class);
        when(form.getAccountType()).thenReturn(IClientApplicationForm.AccountType.COMPANY);

        ICompanyForm companyDetails = mock(ICompanyForm.class);
        when(companyDetails.getCisKey()).thenReturn("1");
        when(companyDetails.getAnzsicCode()).thenReturn(anzicId);
        IAddressForm addressForm = mock(IAddressForm.class);
        when(addressForm.getAddressIdentifier()).thenReturn(null);
        when(companyDetails.getRegisteredAddress()).thenReturn(addressForm);
        when(form.getCompanyDetails()).thenReturn(companyDetails);

        return form;
    }


    private IClientApplicationForm companyApplicationForm_withCRSData() {
        IClientApplicationForm form = mock(IClientApplicationForm.class);
        when(form.getAccountType()).thenReturn(IClientApplicationForm.AccountType.COMPANY);

        ICompanyForm companyDetails = mock(ICompanyForm.class);
        when(companyDetails.getCisKey()).thenReturn("1");
        when(companyDetails.getAnzsicCode()).thenReturn(anzicId);
        IAddressForm addressForm = mock(IAddressForm.class);
        when(addressForm.getAddressIdentifier()).thenReturn(null);
        when(companyDetails.getRegisteredAddress()).thenReturn(addressForm);

        when(ausTaxDetailsForm.getTFN()).thenReturn("11111111");
        when(ausTaxDetailsForm.getTFN()).thenReturn("11111111");
        when(crsTaxDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        List<IOverseasTaxDetailsForm> taxDetailsForms = new ArrayList<>();
        when(crsTaxDetailsForm.getOverseasTaxDetails()).thenReturn(taxDetailsForms);
        when(ausTaxDetailsForm.isTaxCountryAustralia()).thenReturn(true);
        when(ausTaxDetailsForm.getTaxOption()).thenReturn(TaxOptionTypeEnum.TAX_FILE_NUMBER_PROVIDED);
        when(crsTaxDetailsForm.getAustralianTaxDetails().hasTaxFileNumber()).thenReturn(true);
        when(crsTaxDetailsForm.hasOverseasTaxCountry()).thenReturn(false);
        when(companyDetails.getCrsTaxDetails()).thenReturn(crsTaxDetailsForm);
        when(form.getCompanyDetails()).thenReturn(companyDetails);

        return form;
    }



    private IClientApplicationForm individualTrustApplicationForm() {
        XMLGregorianCalendar registeredDate = XMLGregorianCalendarUtil.getXMLGregorianCalendar("10/10/2000", "dd/MM/yyyy");

        IClientApplicationForm form = mock(IClientApplicationForm.class);
        when(form.getAccountType()).thenReturn(IClientApplicationForm.AccountType.INDIVIDUAL_TRUST);

        ITrustForm trustForm = mock(ITrustForm.class);
        when(trustForm.getTrustType()).thenReturn(ITrustForm.TrustType.REGULATED);
        when(trustForm.getAnzsicCode()).thenReturn(anzicId);
        when(trustForm.getDateOfRegistration()).thenReturn(registeredDate);
        when(trustForm.getBusinessName()).thenReturn(businessName);
        IAddressForm addressForm = mock(IAddressForm.class);
        when(addressForm.getAddressIdentifier()).thenReturn(null);
        when(trustForm.getRegisteredAddress()).thenReturn(addressForm);

        ITaxDetailsForm taxDetails = mock(ITaxDetailsForm.class);
        when(trustForm.getTaxDetails()).thenReturn(taxDetails);
        when(form.getTrust()).thenReturn(trustForm);

        IShareholderAndMembersForm shareholderAndMembers = mock(IShareholderAndMembersForm.class);
        when(form.getShareholderAndMembers()).thenReturn(shareholderAndMembers);
        return form;
    }

    private IClientApplicationForm smsfApplicationForm_withCRSData() {

        IClientApplicationForm form = mock(IClientApplicationForm.class);
        when(form.getAccountType()).thenReturn(IClientApplicationForm.AccountType.INDIVIDUAL_SMSF);

        ISmsfForm smsfForm = mock(ISmsfForm.class);
        IAddressForm addressForm = mock(IAddressForm.class);
        when(addressForm.getAddressIdentifier()).thenReturn("abc");
        when(smsfForm.getRegisteredAddress()).thenReturn(addressForm);

        when(smsfForm.getABN()).thenReturn("DUMMY ABN");
        when(smsfForm.getName()).thenReturn("DUMMY NAME");
        when(smsfForm.getAnzsicCode()).thenReturn("DUMMY ANZSIC");
        when(smsfForm.getOrganisationType()).thenReturn(com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm.OrganisationType.SMSF);
        when(smsfForm.getIDVDocIssuer()).thenReturn("DUMMY SEARCH LOOKUP");
        when(smsfForm.getRegistrationState()).thenReturn("NSW");
        when(smsfForm.getDateOfRegistration()).thenReturn(XMLGregorianCalendarUtil.getXMLGregorianCalendar("10/10/2000", "dd/MM/yyyy"));

        List<IOverseasTaxDetailsForm> taxDetailsForms = new ArrayList<>();
        when(crsTaxDetailsForm.getOverseasTaxDetails()).thenReturn(taxDetailsForms);
        when(crsTaxDetailsForm.hasOverseasTaxCountry()).thenReturn(false);
        when(ausTaxDetailsForm.isTaxCountryAustralia()).thenReturn(true);
        when(ausTaxDetailsForm.getTaxOption()).thenReturn(TaxOptionTypeEnum.TAX_FILE_NUMBER_OR_EXEMPTION_NOT_PROVIDED);
        when(ausTaxDetailsForm.hasTaxFileNumber()).thenReturn(false);
        when(crsTaxDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        when(smsfForm.getCrsTaxDetails()).thenReturn(crsTaxDetailsForm);
        when(form.getSmsf()).thenReturn(smsfForm);

        return form;
    }

    private IClientApplicationForm CorpSmsfApplicationForm_withCRSData() {

        IClientApplicationForm form = mock(IClientApplicationForm.class);
        when(form.getAccountType()).thenReturn(IClientApplicationForm.AccountType.CORPORATE_SMSF);

        ISmsfForm smsfForm = mock(ISmsfForm.class);
        IAddressForm addressForm = mock(IAddressForm.class);
        when(addressForm.getAddressIdentifier()).thenReturn("abc");
        when(smsfForm.getRegisteredAddress()).thenReturn(addressForm);

        when(smsfForm.getABN()).thenReturn("DUMMY ABN");
        when(smsfForm.getName()).thenReturn("DUMMY NAME");
        when(smsfForm.getAnzsicCode()).thenReturn("DUMMY ANZSIC");
        when(smsfForm.getOrganisationType()).thenReturn(com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm.OrganisationType.SMSF);
        when(smsfForm.getIDVDocIssuer()).thenReturn("DUMMY SEARCH LOOKUP");
        when(smsfForm.getRegistrationState()).thenReturn("NSW");
        when(smsfForm.getDateOfRegistration()).thenReturn(XMLGregorianCalendarUtil.getXMLGregorianCalendar("10/10/2000", "dd/MM/yyyy"));


        ICompanyForm companyForm = mock(ICompanyForm.class);
        when(companyForm.getCisKey()).thenReturn("1");
        when(companyForm.getAnzsicCode()).thenReturn(anzicId);
        IAddressForm addressFormCompany = mock(IAddressForm.class);
        when(addressForm.getAddressIdentifier()).thenReturn(null);

        List<IOverseasTaxDetailsForm> taxDetailsForms = new ArrayList<>();
        when(crsTaxDetailsForm.getOverseasTaxDetails()).thenReturn(taxDetailsForms);
        when(crsTaxDetailsForm.hasOverseasTaxCountry()).thenReturn(false);
        when(ausTaxDetailsForm.isTaxCountryAustralia()).thenReturn(true);
        when(crsTaxDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);


        when(companyForm.getRegisteredAddress()).thenReturn(addressFormCompany);
        when(companyForm.getCrsTaxDetails()).thenReturn(crsTaxDetailsForm);
        when(companyForm.getCrsTaxDetails()).thenReturn(crsTaxDetailsForm);
        when(smsfForm.getCrsTaxDetails()).thenReturn(crsTaxDetailsForm);
        when(form.getCompanyTrustee()).thenReturn(companyForm);
        when(form.getSmsf()).thenReturn(smsfForm);

        return form;
    }



    @Test
    public void convertFromOrganisationForm_ShouldConvertSmsfDetails_FromFormData() throws Exception {
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(readJsonFromFile("client_application_corpsmsf_form_data_with_addl_members.json"));
        RegisteredEntityDto smsf = organizationDtoConverter.convertFromOrganizationForm(form);
        ISmsfForm smsfFormDetails = form.getSmsf();
        assertThat(smsf.getFullName(), is(smsfFormDetails.getName()));
        assertThat(smsf.getAbn(), is(smsfFormDetails.getABN()));
        assertThat(smsf.getRegistrationDate().getTime(),is(smsfFormDetails.getDateOfRegistration().toGregorianCalendar().getTime().getTime()));
        assertThat(smsf.getRegistrationState(),is(smsfFormDetails.getRegistrationState()));
        assertThat(smsf.getIndustry(),is(industryName));
        assertThat(smsf.isRegistrationForGst(),is(smsfFormDetails.getRegisteredForGST()));
        assertThat(smsf.isIdVerified(), is(smsfFormDetails.hasIDVDocument()));
        verify(crsTaxDetailHelperService, times(0)).populateCRSTaxDetailsForOrganization(any(IOrganisationForm.class),any(RegisteredEntityDto.class));
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertCompanyDetails_FromFormData() throws Exception {
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(readJsonFromFile("client_application_corpsmsf_form_data_with_addl_members.json"));
        RegisteredEntityDto smsf = organizationDtoConverter.convertFromOrganizationForm(form);

        CompanyDto company = ((SmsfDto) smsf).getCompany();

        ICompanyForm companyForm = form.getCompanyTrustee();
        assertThat(company.getFullName(), is(companyForm.getName()));
        assertThat(company.getAsicName(), is(companyForm.getAsicName()));
        assertThat(company.getAcn(), is(companyForm.getACN()));
        assertThat(company.getAbn(), is(companyForm.getABN()));
        assertThat(company.getIndustry(), is("The Industry"));
        verify(crsTaxDetailHelperService, times(0)).populateCRSTaxDetailsForOrganization(any(IOrganisationForm.class),any(RegisteredEntityDto.class));
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertSmsfDetails_FromNewCorporateSmsfFormData() throws Exception {
        Code code = new CodeImpl("code-id", "user-id", industryName);
        when(staticService.loadCodeByUserId(eq(CodeCategory.ANZSIC_INDUSTRY), eq("7412"), any(ServiceErrors.class))).thenReturn(code);

        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(readJsonFromFile("client_application_newcorpsmsf_form_data.json"));
        RegisteredEntityDto smsf = organizationDtoConverter.convertFromOrganizationForm(form);

        ISmsfForm smsfFormDetails = form.getSmsf();

        assertThat(smsf.getFullName(), is(smsfFormDetails.getName()));
        assertThat(smsf.getAbn(), is(""));
        assertNull(smsf.getRegistrationDate());
        assertThat(smsf.getRegistrationState(), is(smsfFormDetails.getRegistrationState()));
        assertThat(smsf.getIndustry(),is(industryName));
        assertThat(smsf.isRegistrationForGst(),is(smsfFormDetails.getRegisteredForGST()));

        assertNull(smsf.getTfnExemptId());
        assertNull(smsf.getExemptionReason());
        assertThat(smsf.isTfnProvided(), is(smsfFormDetails.getTaxDetails().hasTaxFileNumber()));

        assertThat(smsf.isIdVerified(), is(smsfFormDetails.hasIDVDocument()));
        verify(crsTaxDetailHelperService, times(0)).populateCRSTaxDetailsForOrganization(any(IOrganisationForm.class),any(RegisteredEntityDto.class));
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertCompanyDetails_FromNewCorporateSmsfFormData() throws Exception {
        Code code = new CodeImpl("code-id", "user-id", industryName);
        when(staticService.loadCodeByUserId(eq(CodeCategory.ANZSIC_INDUSTRY), eq("7412"), any(ServiceErrors.class))).thenReturn(code);

        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(readJsonFromFile("client_application_newcorpsmsf_form_data.json"));
        RegisteredEntityDto smsf = organizationDtoConverter.convertFromOrganizationForm(form);

        CompanyDto company = ((SmsfDto) smsf).getCompany();

        ICompanyForm companyForm = form.getCompanyTrustee();
        assertThat(company.getFullName(), is(companyForm.getName()));
        assertThat(company.getAsicName(), is(companyForm.getAsicName()));
        assertThat(company.getAcn(), is(companyForm.getACN()));
        assertThat(company.getAbn(), is(companyForm.getABN()));
        assertThat(company.getIndustry(), is("The Industry"));
        assertThat(company.getOccupierName(), is(companyForm.getOccupierName()));
    }

    @Test
    public void convertFromOrganisationForm_ShouldCallCRSUtility_returnTrustDto() throws Exception {
        IClientApplicationForm form = corporateTrustApplicationForm_withCRSData();
        final String exemptionReason = "social_ben";
        when(staticService.loadCodeByAvaloqId(eq(CodeCategory.EXEMPTION_REASON), anyString(), any(ServiceErrors.class))).thenReturn(
                new CodeImpl("3", "4444442", "Recipient of other eligible Centrelink pension or benefit", "social_ben"));
        RegisteredEntityDto trust = organizationDtoConverter.convertFromOrganizationForm(form);
        assertNotNull(trust);
        assertFalse(trust.isTfnProvided());
        assertThat(trust.getExemptionReason(),is("Recipient of other eligible Centrelink pension or benefit"));
        verify(crsTaxDetailHelperService, times(2)).populateCRSTaxDetailsForOrganization(any(IOrganisationForm.class), any(RegisteredEntityDto.class));
    }

    @Test
    public void convertFromOrganisationForm_ShouldCallCRSUtility_Company() throws Exception {
        IClientApplicationForm form = companyApplicationForm_withCRSData();
        CompanyDto company = (CompanyDto) organizationDtoConverter.convertFromOrganizationForm(form);
        assertTrue(company.isTfnProvided());
        assertNull(company.getExemptionReason());
        verify(crsTaxDetailHelperService, times(1)).populateCRSTaxDetailsForOrganization(any(IOrganisationForm.class),any(RegisteredEntityDto.class));

    }


    @Test
    public void convertFromOrganisationForm_ShouldConvertCallCRSUtility_SMSF() throws Exception {
        IClientApplicationForm form = smsfApplicationForm_withCRSData();
        SmsfDto smsf = (SmsfDto)organizationDtoConverter.convertFromOrganizationForm(form);
        assertFalse(smsf.isTfnProvided());
        assertNull(smsf.getExemptionReason());
        verify(crsTaxDetailHelperService, times(1)).populateCRSTaxDetailsForOrganization(any(IOrganisationForm.class),any(RegisteredEntityDto.class));

    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertCallCRSUtility_CorpSMSF() throws Exception {
        IClientApplicationForm form = CorpSmsfApplicationForm_withCRSData();
        SmsfDto smsf = (SmsfDto)organizationDtoConverter.convertFromOrganizationForm(form);
        verify(crsTaxDetailHelperService, times(2)).populateCRSTaxDetailsForOrganization(any(IOrganisationForm.class),any(RegisteredEntityDto.class));

    }


    @Test
    public void convertFromOrganisationForm_CRS_InvalidExemptionReason() throws Exception {
        IClientApplicationForm form = CorpSmsfApplicationForm_withCRSData();
        when(form.getCompanyTrustee().getCrsTaxDetails().getAustralianTaxDetails().hasTaxFileNumber()).thenReturn(false);
        when(form.getCompanyTrustee().getCrsTaxDetails().getAustralianTaxDetails().getExemptionReason()).thenReturn("abcdef");
        SmsfDto smsf = (SmsfDto)organizationDtoConverter.convertFromOrganizationForm(form);
        assertNull(smsf.getCompany().getExemptionReason());
        verify(crsTaxDetailHelperService, times(2)).populateCRSTaxDetailsForOrganization(any(IOrganisationForm.class),any(RegisteredEntityDto.class));

    }

    @Test
    public void convertFromOrganisationForm_ShouldNotCallCRSUtility_Trust() throws Exception {
        IClientApplicationForm form = individualTrustApplicationForm();
        RegisteredEntityDto trust = organizationDtoConverter.convertFromOrganizationForm(form);
        assertNull(trust.getTaxResidenceCountries());
        assertFalse(trust.isTfnProvided());
        assertNull(trust.getExemptionReason());
        verify(crsTaxDetailHelperService, times(0)).populateCRSTaxDetailsForOrganization(any(IOrganisationForm.class), any(RegisteredEntityDto.class));
    }


    @Test
    public void convertFromOrganisationForm_ShouldNotCallCRSUtility_Company() throws Exception {
        IClientApplicationForm form = companyApplicationForm();
        RegisteredEntityDto trust = organizationDtoConverter.convertFromOrganizationForm(form);
        assertNull(trust.getTaxResidenceCountries());
        assertFalse(trust.isTfnProvided());
        assertNull(trust.getExemptionReason());
        verify(crsTaxDetailHelperService, times(0)).populateCRSTaxDetailsForOrganization(any(IOrganisationForm.class), any(RegisteredEntityDto.class));
    }

    @Test
    public void convertFromOrganisationForm_ShouldNotCallCRSUtility_Smsf() throws Exception {
        Code code = new CodeImpl("code-id", "user-id", industryName);
        when(staticService.loadCodeByUserId(eq(CodeCategory.ANZSIC_INDUSTRY), eq("7412"), any(ServiceErrors.class))).thenReturn(code);
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(readJsonFromFile("client_application_newcorpsmsf_form_data.json"));
        RegisteredEntityDto smsf = organizationDtoConverter.convertFromOrganizationForm(form);
        assertNull(smsf.getTaxResidenceCountries());
        assertFalse(smsf.isTfnProvided());
        assertNull(smsf.getExemptionReason());
        verify(crsTaxDetailHelperService, times(0)).populateCRSTaxDetailsForOrganization(any(IOrganisationForm.class), any(RegisteredEntityDto.class));

    }


}
