package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.client.model.TaxResidenceCountriesDto;
import com.bt.nextgen.api.client.model.TrustDto;
import com.bt.nextgen.api.draftaccount.AbstractJsonReaderTest;
import com.bt.nextgen.api.draftaccount.builder.v3.TINExemptionEnum;
import com.bt.nextgen.api.draftaccount.model.form.IAddressForm;
import com.bt.nextgen.api.draftaccount.model.form.IAusTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.ICrsTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm;
import com.bt.nextgen.api.draftaccount.model.form.IOverseasTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.ITaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.ITrustForm;
import com.bt.nextgen.api.draftaccount.model.form.v1.CrsTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.v1.OverseasTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.OverseasTaxDetails;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.TinOptionTypeEnum;
import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.domain.CountryNameConverter;
import com.bt.nextgen.service.avaloq.domain.PersonDetailImpl;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.Organisation;
import com.bt.nextgen.service.integration.domain.OrganisationImpl;
import com.bt.nextgen.service.integration.domain.TrustType;
import com.bt.nextgen.service.integration.domain.TrustTypeDesc;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.domain.PersonDetail;
import com.btfin.panorama.core.conversion.CodeCategory;
import org.apache.commons.collections.CollectionUtils;
import static org.hamcrest.core.Is.is;


import org.joda.time.DateTime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by L069552 on 13/03/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class CRSTaxDetailHelperServiceImplTest extends AbstractJsonReaderTest {

    @InjectMocks
    CRSTaxDetailHelperServiceImpl crsTaxDetailHelperService;

    @Mock
    CountryNameConverter countryNameConverter;

    @Mock
    private StaticIntegrationService staticService;

    @Mock
    ClientApplicationDetailsDtoHelperService clientApplicationDetailsDtoHelperService;

    private ICrsTaxDetailsForm crsTaxDetailsForm = mock(CrsTaxDetailsForm.class);
    private IAusTaxDetailsForm ausTaxDetailsForm = mock(IAusTaxDetailsForm.class);
    private static final String AUSTRALIA = "Australia";
    private static final String INDIA = "India";

    private JsonObjectMapper mapper;

    private String anzicId = "7340";
    private String industryName = "The Industry";
    private String businessName = "The Serious Business";
    private List<Code> tinExemptionReasonList;

    @Before
    public void setUp() {
        mapper = new JsonObjectMapper();
        Code code = new CodeImpl("code-id", "user-id", industryName);
        when(staticService.loadCodeByUserId(eq(CodeCategory.ANZSIC_INDUSTRY), eq(anzicId), any(ServiceErrors.class))).thenReturn(code);
        when(countryNameConverter.convert("2061")).thenReturn("Australia");
        when(countryNameConverter.convert("2007")).thenReturn("Singapore");
        when(countryNameConverter.convert("2058")).thenReturn("Argentina");
        when(countryNameConverter.convert("2027")).thenReturn("India");
        tinExemptionReasonList = new ArrayList <Code>();
        tinExemptionReasonList.add(new CodeImpl("5", "TIN", "Tax identification number","tin"));
        tinExemptionReasonList.add(new CodeImpl("1020", "UNDER_AGED","Under age","btfg$under_aged"));
        tinExemptionReasonList.add(new CodeImpl("1022", "TIN_NEVER_ISS", "TIN not issued","btfg$tin_never_iss"));
        tinExemptionReasonList.add(new CodeImpl("1021", "TIN_PEND", "TIN pending","btfg$tin_pend"));
        when(staticService.loadCodes(eq(CodeCategory.TIN_EXEMPTION_REASONS), any(ServiceErrors.class))).thenReturn(tinExemptionReasonList);
        when(staticService.loadCodeByUserId(eq(CodeCategory.COUNTRY), eq("SG"), any(ServiceErrors.class))).thenReturn(new CodeImpl("2007", "SG", "Singapore"));
        when(staticService.loadCodeByUserId(eq(CodeCategory.COUNTRY), eq("IN"), any(ServiceErrors.class))).thenReturn(new CodeImpl("2027", "IN", "India"));
        when(staticService.loadCodeByUserId(eq(CodeCategory.COUNTRY), eq("DE"), any(ServiceErrors.class))).thenReturn(new CodeImpl("2132", "DE", "Germany"));
        when(staticService.loadCodeByUserId(eq(CodeCategory.COUNTRY), eq("ABC"), any(ServiceErrors.class))).thenReturn(null);
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertCRSDetails_ForAustraliaTaxRes() throws Exception {
        IOrganisationForm organisationForm = fetchTrustOrganizationForm();
        TrustDto trustDto = new TrustDto();
        crsTaxDetailHelperService.populateCRSTaxDetailsForOrganization(organisationForm, trustDto);
        assertNotNull(trustDto.getResiCountryforTax());
        assertThat(trustDto.getResiCountryforTax(), is(AUSTRALIA));
        assertNotNull(trustDto.getTaxResidenceCountries());
        assertEquals(trustDto.getTaxResidenceCountries().size(), 3);
    }

    @Test
    public void convertFromOrganisationForm_ShouldConvertCRSDetails_ForOverseasTaxRes() throws Exception {
        IOrganisationForm organisationForm = fetchTrustOrganizationForm_forOverseasResident();
        TrustDto trustDto = new TrustDto();
        crsTaxDetailHelperService.populateCRSTaxDetailsForOrganization(organisationForm, trustDto);
        assertNotNull(trustDto.getResiCountryforTax());
        assertThat(trustDto.getResiCountryforTax(), is(AUSTRALIA));
        assertNotNull(trustDto.getTaxResidenceCountries());
        assertEquals(trustDto.getTaxResidenceCountries().size(), 3);
        verifyOverseasCountryValues(trustDto);
    }


    @Test
    public void convertFromPersonForm_ShouldConvertCRSDetails_ForAustraliaTaxRes() throws Exception {
        IPersonDetailsForm personDetailsForm = fetchPersonDetailForm();
        InvestorDto investorDto = new InvestorDto();
        crsTaxDetailHelperService.populateCRSTaxDetailsForIndividual(personDetailsForm, investorDto);
        assertNotNull(investorDto.getResiCountryforTax());
        assertThat(investorDto.getResiCountryforTax(), is(AUSTRALIA));
        assertNotNull(investorDto.getTaxResidenceCountries());
        assertEquals(investorDto.getTaxResidenceCountries().size(), 3);
        verifyOverseasCountryValues(investorDto);
    }

    @Test
    public void convertFromPersonForm_ShouldConvertCRSDetails_ForForeignRegisteredNull() throws Exception {
        IPersonDetailsForm personDetailsForm = fetchPersonDetailFormForeignRegisteredNull();
        InvestorDto investorDto = new InvestorDto();
        crsTaxDetailHelperService.populateCRSTaxDetailsForIndividual(personDetailsForm, investorDto);
        assertNotNull(investorDto.getResiCountryforTax());
        assertThat(investorDto.getResiCountryforTax(), is(INDIA));
        assertNull(investorDto.getTaxResidenceCountries());
    }

    @Test
    public void convertFromPersonForm_ShouldConvertCRSDetails_ForOverseasTaxRes() throws Exception {
        IPersonDetailsForm personDetailsForm = fetchPersonDetailForm_forOverseasResident();
        InvestorDto investorDto = new InvestorDto();
        crsTaxDetailHelperService.populateCRSTaxDetailsForIndividual(personDetailsForm, investorDto);
        assertNotNull(investorDto.getResiCountryforTax());
        assertThat(investorDto.getResiCountryforTax(), is(INDIA));
        assertNotNull(investorDto.getTaxResidenceCountries());
        assertEquals(investorDto.getTaxResidenceCountries().size(), 3);
        verifyOverseasCountryValues(investorDto);
    }

    @Test
    public void convertFromPersonForm_ShouldConvertCRSDetails_InvalidCountryCode() throws Exception {
        List<IOverseasTaxDetailsForm> overseasTaxDetailsList = new ArrayList<>();
        XMLGregorianCalendar xmlGregorianCalendar = XMLGregorianCalendarUtil.getXMLGregorianCalendar("10/10/1990", "dd/MM/yyyy");
        IPersonDetailsForm personDetailsForm = mock(IPersonDetailsForm.class);
        when(personDetailsForm.getTitle()).thenReturn("mr");
        when(personDetailsForm.getDateOfBirthAsCalendar()).thenReturn(xmlGregorianCalendar);
        IAddressForm addressForm = mock(IAddressForm.class);
        when(addressForm.getAddressIdentifier()).thenReturn(null);
        when(personDetailsForm.getResidentialAddress()).thenReturn(addressForm);
        when(personDetailsForm.getPostalAddress()).thenReturn(addressForm);
        when(ausTaxDetailsForm.isTaxCountryAustralia()).thenReturn(false);
        when(personDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        when(personDetailsForm.hasOverseasTaxCountry()).thenReturn(true);
        when(personDetailsForm.getOverseasTaxCountry()).thenReturn("ABC");
        IOverseasTaxDetailsForm overseasTaxDetailsForm = MockOverseasFormBuilder.make().withOverseasTaxCountry("ABC").withTIN("11111111").collect();
        overseasTaxDetailsList.add(overseasTaxDetailsForm);
        when(personDetailsForm.getOverseasTaxDetails()).thenReturn(overseasTaxDetailsList);
        InvestorDto investorDto = new InvestorDto();
        crsTaxDetailHelperService.populateCRSTaxDetailsForIndividual(personDetailsForm, investorDto);
        assertThat(investorDto.getResiCountryforTax(),is("ABC"));
        assertNotNull(investorDto.getTaxResidenceCountries());
        assertEquals(investorDto.getTaxResidenceCountries().size(), 1);
        assertThat(investorDto.getTaxResidenceCountries().get(0).getTaxResidenceCountry(), is("ABC"));
    }

    @Test
    public void convertFromPersonForm_ShouldConvertCRSDetails_ForOverseas_Single_Country() throws Exception {
        IPersonDetailsForm personDetailsForm = fetchPersonDetailForm_forOverseas_Single_Country();
        InvestorDto investorDto = new InvestorDto();
        crsTaxDetailHelperService.populateCRSTaxDetailsForIndividual(personDetailsForm, investorDto);
        assertNotNull(investorDto.getResiCountryforTax());
        assertThat(investorDto.getResiCountryforTax(), is("Singapore"));
        assertNotNull(investorDto.getTaxResidenceCountries());
        assertEquals(investorDto.getTaxResidenceCountries().size(), 3);
        verifyOverseasCountryValues(investorDto);
    }

    @Test
    public void convertFromPersonForm_ShouldHaveCRSData_ExistingUser() throws Exception {
        IPersonDetailsForm personDetailsForm = fetchExistingPersonDetailForm_WithoutOverseasResident();
        InvestorDto investorDto = new InvestorDto();
        crsTaxDetailHelperService.populateCRSTaxDetailsForIndividual(personDetailsForm, investorDto);
        assertNotNull(investorDto.getResiCountryforTax());
        assertNull(investorDto.getTaxResidenceCountries());
        assertNotNull(investorDto.getOverseasTaxResident());
        assertFalse(investorDto.getOverseasTaxResident());
    }


    @Test
    public void convertFromPersonForm_ShouldHaveCRSData_NonExistingUser() throws Exception {
        IPersonDetailsForm personDetailsForm = fetchPersonDetailForm_WithoutOverseasResident();
        when(personDetailsForm.isExistingPerson()).thenReturn(false);
        InvestorDto investorDto = new InvestorDto();
        crsTaxDetailHelperService.populateCRSTaxDetailsForIndividual(personDetailsForm, investorDto);
        assertNotNull(investorDto.getResiCountryforTax());
        assertNull(investorDto.getTaxResidenceCountries());
        assertFalse(investorDto.getOverseasTaxResident());
    }

    @Test
    public void convertFromPersonDetails_ShouldConvertCRSDetails_OverseasCountries() throws Exception {
       PersonDetail personDetail = fetchPersonDetails();
       InvestorDto investorDto = new InvestorDto();
       crsTaxDetailHelperService.populateCRSTaxDetailsForIndividual(personDetail,investorDto, false,null,null);
       assertNotNull(investorDto.getTaxResidenceCountries());
       assertEquals(investorDto.getTaxResidenceCountries().size(), 3);
       verifyOverseasCountryValues(investorDto);
       assertTrue(investorDto.getOverseasTaxResident());
    }


    @Test
    public void convertFromPersonDetails_ShouldConvertCRSDetails_Without_OverseasCountries() throws Exception {
        PersonDetail personDetail = mock(PersonDetailImpl.class);
        List<TaxResidenceCountry> taxResidenceCountries = new ArrayList<>();
        when(personDetail.getTaxResidenceCountries()).thenReturn(taxResidenceCountries);
        when(personDetail.getResiCountryForTax()).thenReturn(AUSTRALIA);
        InvestorDto investorDto = new InvestorDto();
        crsTaxDetailHelperService.populateCRSTaxDetailsForIndividual(personDetail,investorDto, false,null,null);
        assertFalse(investorDto.getOverseasTaxResident());
        assertNull(investorDto.getTaxResidenceCountries());
    }

    @Test
    public void convertFromPersonDetails_ShouldConvertCRSDetails_OverseasCountries_ExistingUser() throws Exception {
        PersonDetail personDetail = fetchPersonDetails();
        InvestorDto investorDto = new InvestorDto();
        Map cisKeysToOverseasDetails = new HashMap();
        cisKeysToOverseasDetails.put("1234567",true);
        crsTaxDetailHelperService.populateCRSTaxDetailsForIndividual(personDetail,investorDto, true,cisKeysToOverseasDetails,"1234567");
        assertNotNull(investorDto.getResiCountryforTax());
        assertThat(investorDto.getResiCountryforTax(), is(AUSTRALIA));
        assertNotNull(investorDto.getTaxResidenceCountries());
        assertEquals(investorDto.getTaxResidenceCountries().size(), 3);
        verifyOverseasCountryValues(investorDto);
        assertNull(investorDto.getOverseasTaxResident());
    }

    @Test
    public void person__WithOverseasCountries_ExistingUser_NonOverseasRes() throws Exception {
        PersonDetail personDetail = fetchPersonDetails();
        InvestorDto investorDto = new InvestorDto();
        Map cisKeysToOverseasDetails = new HashMap();
        cisKeysToOverseasDetails.put("1234567",true);
        when(personDetail.getTaxResidenceCountries()).thenReturn(new ArrayList<TaxResidenceCountry>());
        crsTaxDetailHelperService.populateCRSTaxDetailsForIndividual(personDetail,investorDto, true,cisKeysToOverseasDetails,"1234567");
        assertNotNull(investorDto.getResiCountryforTax());
        assertThat(investorDto.getResiCountryforTax(), is(AUSTRALIA));
        verifyOverseasCountryValues(investorDto);
        assertNotNull(investorDto.getOverseasTaxResident());
        assertTrue(investorDto.getOverseasTaxResident());
    }

    @Test
    public void person__WithOverseasCountries_ExistingUser_OverseasNull() throws Exception {
        PersonDetail personDetail = fetchPersonDetails();
        InvestorDto investorDto = new InvestorDto();
        Map cisKeysToOverseasDetails = new HashMap();
        cisKeysToOverseasDetails.put("1234567",null);
        when(personDetail.getTaxResidenceCountries()).thenReturn(new ArrayList<TaxResidenceCountry>());
        crsTaxDetailHelperService.populateCRSTaxDetailsForIndividual(personDetail,investorDto, true,cisKeysToOverseasDetails,"1234567");
        assertNotNull(investorDto.getResiCountryforTax());
        assertThat(investorDto.getResiCountryforTax(), is(AUSTRALIA));
        verifyOverseasCountryValues(investorDto);
        assertNull(investorDto.getOverseasTaxResident());
    }

    @Test
    public void convertFromPersonDetails_ShouldConvertCRSDetails_ExistingUser_Invalid() throws Exception {
        PersonDetail personDetail = fetchPersonDetails();
        InvestorDto investorDto = new InvestorDto();
        Map cisKeysToOverseasDetails = new HashMap();
        cisKeysToOverseasDetails.put("1234567",true);
        crsTaxDetailHelperService.populateCRSTaxDetailsForIndividual(personDetail,investorDto, true,cisKeysToOverseasDetails,null);
        assertNotNull(investorDto.getResiCountryforTax());
        assertThat(investorDto.getResiCountryforTax(), is(AUSTRALIA));
        assertNotNull(investorDto.getTaxResidenceCountries());
        assertEquals(investorDto.getTaxResidenceCountries().size(), 3);
        verifyOverseasCountryValues(investorDto);
        assertNull(investorDto.getOverseasTaxResident());
    }


    private void verifyOverseasCountryValues(InvestorDto investorDto) {

        if (CollectionUtils.isNotEmpty(investorDto.getTaxResidenceCountries())) {
            List<TaxResidenceCountriesDto> taxResidenceCountries = investorDto.getTaxResidenceCountries();
            assertThat(taxResidenceCountries.get(0).getTaxExemptionReason(), is("TIN not issued"));
            assertThat(taxResidenceCountries.get(0).getTaxResidenceCountry(), is("Singapore"));
            assertThat(taxResidenceCountries.get(1).getTaxExemptionReason(), is("TIN pending"));
            assertThat(taxResidenceCountries.get(1).getTaxResidenceCountry(), is("India"));
            assertThat(taxResidenceCountries.get(2).getTaxExemptionReason(), is("Tax identification number"));
            assertThat(taxResidenceCountries.get(2).getTaxResidenceCountry(), is("Germany"));
            assertThat(taxResidenceCountries.get(2).getTin(), is("11111111"));
        }
    }


    private IOrganisationForm fetchTrustOrganizationForm() {
        XMLGregorianCalendar registeredDate = XMLGregorianCalendarUtil.getXMLGregorianCalendar("10/10/2000", "dd/MM/yyyy");

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

        when(crsTaxDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        List<IOverseasTaxDetailsForm> taxDetailsForms = fetchOverSeasCountriesList();
        when(crsTaxDetailsForm.getOverseasTaxDetails()).thenReturn(taxDetailsForms);
        when(ausTaxDetailsForm.isTaxCountryAustralia()).thenReturn(true);
        when(trustForm.getCrsTaxDetails()).thenReturn(crsTaxDetailsForm);
        return trustForm;
    }

    @Test
    public void testGetOrganisationDetailsForTrust_withCRSDetails_OverseasCountries() {
        Organisation trust = mock(OrganisationImpl.class);
        TrustDto trustDto  = new TrustDto();
        when(trust.getInvestorType()).thenReturn(InvestorType.TRUST);
        when(trust.getTrustType()).thenReturn(TrustType.OTHER);
        when(trust.getTrustTypeDesc()).thenReturn(TrustTypeDesc.BTFG$UNREG_MNGD_INVST_SCH);
        when(trust.getResiCountryForTax()).thenReturn(AUSTRALIA);
        List<TaxResidenceCountry> taxResidenceCountries = fetchOverSeasCountriesList_forApplicationDocument();
        when(trust.getTaxResidenceCountries()).thenReturn(taxResidenceCountries);
        crsTaxDetailHelperService.populateCRSTaxDetailsForOrganization(trust,trustDto);
        assertNotNull(trustDto.getTaxResidenceCountries());
        assertEquals(trustDto.getTaxResidenceCountries().size(), 3);
        verifyOverseasCountryValues(trustDto);
        assertTrue(trustDto.getOverseasTaxResident());
    }

    @Test
    public void testGetOrganisationDetailsForTrust_withCRSDetails_Without_OverseasCountries() {
        Organisation trust = mock(OrganisationImpl.class);
        TrustDto trustDto  = new TrustDto();
        when(trust.getInvestorType()).thenReturn(InvestorType.TRUST);
        when(trust.getTrustType()).thenReturn(TrustType.OTHER);
        when(trust.getTrustTypeDesc()).thenReturn(TrustTypeDesc.BTFG$UNREG_MNGD_INVST_SCH);
        when(trust.getResiCountryForTax()).thenReturn(AUSTRALIA);
        List<TaxResidenceCountry> taxResidenceCountries = new ArrayList<>();
        when(trust.getTaxResidenceCountries()).thenReturn(taxResidenceCountries);
        crsTaxDetailHelperService.populateCRSTaxDetailsForOrganization(trust, trustDto);
        assertNull(trustDto.getTaxResidenceCountries());
        assertFalse(trustDto.getOverseasTaxResident());
    }

    @Test
    public void testGetOrganisationDetailsForTrust_withInvalidCRSData_Country() {
        Organisation trust = mock(OrganisationImpl.class);
        TrustDto trustDto  = new TrustDto();
        when(trust.getInvestorType()).thenReturn(InvestorType.TRUST);
        when(trust.getTrustType()).thenReturn(TrustType.OTHER);
        when(trust.getTrustTypeDesc()).thenReturn(TrustTypeDesc.BTFG$UNREG_MNGD_INVST_SCH);
        when(trust.getResiCountryForTax()).thenReturn(null);
        List<TaxResidenceCountry> taxResidenceCountries = new ArrayList<>();
        when(trust.getTaxResidenceCountries()).thenReturn(taxResidenceCountries);
        crsTaxDetailHelperService.populateCRSTaxDetailsForOrganization(trust,trustDto);
        assertNull(trustDto.getResiCountryforTax());
    }


    @Test
    public void testGetOrganisationDetailsForTrust_withCRSDetails_OnlyOverseasResident() {
        Organisation trust = mock(OrganisationImpl.class);
        TrustDto trustDto  = new TrustDto();
        when(trust.getInvestorType()).thenReturn(InvestorType.TRUST);
        when(trust.getTrustType()).thenReturn(TrustType.OTHER);
        when(trust.getTrustTypeDesc()).thenReturn(TrustTypeDesc.BTFG$UNREG_MNGD_INVST_SCH);
        when(trust.getResiCountryForTax()).thenReturn(INDIA);
        List<TaxResidenceCountry> taxResidenceCountries = new ArrayList<>();
        when(trust.getTaxResidenceCountries()).thenReturn(taxResidenceCountries);
        crsTaxDetailHelperService.populateCRSTaxDetailsForOrganization(trust,trustDto);
        assertFalse(trustDto.getOverseasTaxResident());
    }


    private IPersonDetailsForm mockAddressPersonForm(){
        XMLGregorianCalendar xmlGregorianCalendar = XMLGregorianCalendarUtil.getXMLGregorianCalendar("10/10/1990", "dd/MM/yyyy");
        IPersonDetailsForm personDetailsForm = mock(IPersonDetailsForm.class);
        when(personDetailsForm.getTitle()).thenReturn("mr");
        when(personDetailsForm.getDateOfBirthAsCalendar()).thenReturn(xmlGregorianCalendar);

        IAddressForm addressForm = mock(IAddressForm.class);
        when(addressForm.getAddressIdentifier()).thenReturn(null);

        when(personDetailsForm.getResidentialAddress()).thenReturn(addressForm);
        when(personDetailsForm.getPostalAddress()).thenReturn(addressForm);

        return personDetailsForm;
    }


    private IPersonDetailsForm fetchPersonDetailForm() {

        IPersonDetailsForm personDetailsForm = mockAddressPersonForm();

        when(crsTaxDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        List<IOverseasTaxDetailsForm> overseastaxDetailsForms = fetchOverSeasCountriesList();
        when(ausTaxDetailsForm.isTaxCountryAustralia()).thenReturn(true);
        when(personDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        when(personDetailsForm.hasOverseasTaxCountry()).thenReturn(false);
        when(personDetailsForm.getOverseasTaxDetails()).thenReturn(overseastaxDetailsForms);
        return personDetailsForm;
    }

    private IPersonDetailsForm fetchPersonDetailFormForeignRegisteredNull() {
        IPersonDetailsForm personDetailsForm = mockAddressPersonForm();

        when(crsTaxDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        when(ausTaxDetailsForm.isTaxCountryAustralia()).thenReturn(false);
        when(personDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        when(personDetailsForm.hasOverseasTaxCountry()).thenReturn(true);
        when(personDetailsForm.getOverseasTaxCountry()).thenReturn("IN");
        when(personDetailsForm.getOverseasTaxDetails()).thenReturn(null);
        return personDetailsForm;
    }

    private IPersonDetailsForm fetchExistinPersonDetailForm() {
        IPersonDetailsForm personDetailsForm = mockAddressPersonForm();
        when(personDetailsForm.getCisId()).thenReturn("1234567");

        when(crsTaxDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        List<IOverseasTaxDetailsForm> overseastaxDetailsForms = fetchOverSeasCountriesList();
        when(ausTaxDetailsForm.isTaxCountryAustralia()).thenReturn(true);
        when(personDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        when(personDetailsForm.hasOverseasTaxCountry()).thenReturn(false);
        when(personDetailsForm.getOverseasTaxDetails()).thenReturn(overseastaxDetailsForms);
        return personDetailsForm;
    }


    private IPersonDetailsForm fetchPersonDetailForm_forOverseasResident() {
        IPersonDetailsForm personDetailsForm = mockAddressPersonForm();

        List<IOverseasTaxDetailsForm> overseastaxDetailsForms = fetchOverSeasCountriesList();
        when(ausTaxDetailsForm.isTaxCountryAustralia()).thenReturn(false);
        when(personDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        when(personDetailsForm.hasOverseasTaxCountry()).thenReturn(true);
        when(personDetailsForm.getOverseasTaxCountry()).thenReturn("IN");
        when(personDetailsForm.getOverseasTaxDetails()).thenReturn(overseastaxDetailsForms);
        return personDetailsForm;
    }

    private IPersonDetailsForm fetchPersonDetailForm_forOverseas_Single_Country() {
        IPersonDetailsForm personDetailsForm = mockAddressPersonForm();

        List<IOverseasTaxDetailsForm> overseastaxDetailsForms = fetchOverSeasCountriesList();
        when(ausTaxDetailsForm.isTaxCountryAustralia()).thenReturn(false);
        when(personDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        when(personDetailsForm.getOverseasTaxDetails()).thenReturn(overseastaxDetailsForms);
        return personDetailsForm;
    }

    private IPersonDetailsForm fetchPersonDetailForm_WithoutOverseasResident() {
        IPersonDetailsForm personDetailsForm = mockAddressPersonForm();

        when(personDetailsForm.getCisId()).thenReturn(null);
        List<IOverseasTaxDetailsForm> overseastaxDetailsForms = new ArrayList<>();
        when(ausTaxDetailsForm.isTaxCountryAustralia()).thenReturn(true);
        when(personDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        when(personDetailsForm.getOverseasTaxDetails()).thenReturn(overseastaxDetailsForms);
        return personDetailsForm;
    }

    private IPersonDetailsForm fetchExistingPersonDetailForm_WithoutOverseasResident() {
        IPersonDetailsForm personDetailsForm = mockAddressPersonForm();

        when(personDetailsForm.getCisId()).thenReturn("123456");
        List<IOverseasTaxDetailsForm> overseastaxDetailsForms = new ArrayList<>();
        when(ausTaxDetailsForm.isTaxCountryAustralia()).thenReturn(true);
        when(personDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        when(personDetailsForm.getOverseasTaxDetails()).thenReturn(overseastaxDetailsForms);
        when(personDetailsForm.getIsOverseasTaxRes()).thenReturn(false);
        return personDetailsForm;
    }

    private PersonDetail fetchPersonDetails() {

        List<InvestorRole> investorRoles = new ArrayList<InvestorRole>();
        investorRoles.add(InvestorRole.BeneficialOwner);
        PersonDetail personDetail = mock(PersonDetailImpl.class);
        when(personDetail.getResiCountryForTax()).thenReturn(AUSTRALIA);
        List<TaxResidenceCountry> taxResidenceCountries = fetchOverSeasCountriesList_forApplicationDocument();
        when(personDetail.getTaxResidenceCountries()).thenReturn(taxResidenceCountries);
        return personDetail;
    }

    private IOrganisationForm fetchTrustOrganizationForm_forOverseasResident() {
        XMLGregorianCalendar registeredDate = XMLGregorianCalendarUtil.getXMLGregorianCalendar("10/10/2000", "dd/MM/yyyy");

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

        when(crsTaxDetailsForm.getAustralianTaxDetails()).thenReturn(ausTaxDetailsForm);
        List<IOverseasTaxDetailsForm> taxDetailsForms = fetchOverSeasCountriesList();
        when(crsTaxDetailsForm.getOverseasTaxDetails()).thenReturn(taxDetailsForms);
        when(ausTaxDetailsForm.isTaxCountryAustralia()).thenReturn(true);
        when(crsTaxDetailsForm.getOverseasTaxCountry()).thenReturn(AUSTRALIA);
        when(trustForm.getCrsTaxDetails()).thenReturn(crsTaxDetailsForm);
        return trustForm;
    }



    private List<IOverseasTaxDetailsForm> fetchOverSeasCountriesList() {

        List<IOverseasTaxDetailsForm> overseasTaxDetailsForms = new ArrayList<>();

        IOverseasTaxDetailsForm overseasTaxDetailsFormOne = MockOverseasFormBuilder.make().withOverseasTaxCountry("SG").withTINExemptionReason(TINExemptionEnum.TIN_NEVERISSUED.getExemption()).withTINOption(TinOptionTypeEnum.EXEMPTION_REASON_PROVIDED).collect();
        IOverseasTaxDetailsForm overseasTaxDetailsFormTwo = MockOverseasFormBuilder.make().withOverseasTaxCountry("IN").withTINExemptionReason(TINExemptionEnum.TIN_PENDING.getExemption()).withTINOption(TinOptionTypeEnum.EXEMPTION_REASON_PROVIDED).collect();
        IOverseasTaxDetailsForm overseasTaxDetailsFormThree = MockOverseasFormBuilder.make().withOverseasTaxCountry("DE").withTIN("11111111").collect();
        overseasTaxDetailsForms.add(overseasTaxDetailsFormOne);
        overseasTaxDetailsForms.add(overseasTaxDetailsFormTwo);
        overseasTaxDetailsForms.add(overseasTaxDetailsFormThree);

        return overseasTaxDetailsForms;

    }


    private List<TaxResidenceCountry> fetchOverSeasCountriesList_forApplicationDocument() {

        List<TaxResidenceCountry> taxResidenceCountries = new ArrayList<TaxResidenceCountry>();
        TaxResidenceCountry taxResidenceCountry1 = MockTaxResidencyCountryBuilder.make().withCountryOfResidence("Singapore").withTin("").withTinExemptionReason("TIN not issued").collect();
        TaxResidenceCountry taxResidenceCountry2 = MockTaxResidencyCountryBuilder.make().withCountryOfResidence("India").withTin("").withTinExemptionReason("TIN pending").collect();
        TaxResidenceCountry taxResidenceCountry3 = MockTaxResidencyCountryBuilder.make().withCountryOfResidence("Germany").withTin("11111111").withTinExemptionReason("Tax identification number").collect();
        TaxResidenceCountry taxResidenceCountry4 = MockTaxResidencyCountryBuilder.make().withCountryOfResidence("Spain").withTin("11111111").withTinExemptionReason("Tax identification number").withEndDate(new DateTime()).collect();

        taxResidenceCountries.add(taxResidenceCountry1);
        taxResidenceCountries.add(taxResidenceCountry2);
        taxResidenceCountries.add(taxResidenceCountry3);
        taxResidenceCountries.add(taxResidenceCountry4);

        return taxResidenceCountries;
    }

}