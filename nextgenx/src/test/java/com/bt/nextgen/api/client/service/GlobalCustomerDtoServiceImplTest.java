package com.bt.nextgen.api.client.service;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.NonStandardPostalAddress;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.StandardPostalAddress;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.ClientDto;
import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.model.ClientUpdateKey;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.model.TaxResidenceCountriesDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.code.FieldImpl;
import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.domain.PhoneImpl;
import com.bt.nextgen.service.avaloq.domain.existingclient.IndividualWithAccountDataImpl;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerDataImpl;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.IndividualDetails;
import com.bt.nextgen.service.group.customer.groupesb.TaxResidenceCountry;
import com.bt.nextgen.service.group.customer.groupesb.address.AddressAdapter;
import com.bt.nextgen.service.group.customer.groupesb.address.v7.AddressAdapterV7;
import com.bt.nextgen.service.group.customer.groupesb.address.v7.InternationalAddressV7Adapter;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.AddressType;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.ExemptionReason;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.domain.IdentityVerificationStatus;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.integration.domain.IndividualDetail;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.api.client.service.GlobalCustomerDtoServiceImpl.BTFG_$_IM_CODE;
import static com.bt.nextgen.api.client.service.GlobalCustomerDtoServiceImpl.fullName;
import static com.bt.nextgen.api.country.service.CountryDtoServiceImpl.UCM_CODE;
import static com.bt.nextgen.service.integration.domain.AddressMedium.BUSINESS_TELEPHONE;
import static com.bt.nextgen.service.integration.domain.AddressMedium.MOBILE_PHONE_PRIMARY;
import static com.btfin.panorama.core.conversion.CodeCategory.ADDRESS_STREET_TYPE;
import static com.btfin.panorama.core.conversion.CodeCategory.COUNTRY;
import static com.btfin.panorama.core.conversion.CodeCategory.PERSON_TITLE;
import static com.btfin.panorama.core.security.encryption.EncodedString.fromPlainTextUsingTL;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test the {@code GlobalCustomerDtoServiceImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class GlobalCustomerDtoServiceImplTest {

    private static final String CIS_KEY = "98765432101";

    private static final String Z_NUMBER = "99887766";

    @Mock
    private CustomerDataManagementIntegrationService customerDataManagementService;

    @Mock
    private ClientIntegrationService clientIntegrationService;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    @Mock
    private DirectInvestorDataDtoService directInvestorDataDtoService;

    @InjectMocks
    private GlobalCustomerDtoServiceImpl service;

    private ClientKey key;

    private ServiceErrors errors;

    @Before
    public void initErrorsAndMockStaticIntegrationService() {
        com.bt.nextgen.service.integration.userinformation.ClientKey clientKey = com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf(CIS_KEY);
        key = new ClientKey(CIS_KEY);
        errors = new ServiceErrorsImpl();
    }

    @Test
    public void findUnknownCustomerReturnsNull() {
        prepareExistingClients(null);
        prepareCustomerInformation(null);
        assertNull(service.find(key, errors));
    }

    @Test
    public void findCustomerFromExistingClientList() {
        CustomerDataDto customerDataDto = new CustomerDataDto();
        customerDataDto.setTaxResidenceCountries(Arrays.asList(getTaxResidenceCountry("us","btfg$tin_never_iss",null),getTaxResidenceCountry("FOREIGN","","Y")));
        when(directInvestorDataDtoService.find(any(ClientUpdateKey.class), any(ServiceErrors.class))).thenReturn(customerDataDto);
        prepareExistingClients(individual(CIS_KEY, "12345"));
        IndividualDetail person = person("Mr", "Robert", "Brogue", DateTime.parse("1982-11-02"), Gender.MALE, IdentityVerificationStatus.Completed);
        prepareClientDetail("12345", person);
        final ClientDto client = service.find(key, errors);
        assertTrue(client instanceof IndividualDto);
        assertEquals(fromPlainTextUsingTL("12345").toString(), client.getKey().getClientId());
        assertEquals("Robert Brogue", client.getFullName());
        assertTrue(client.isRegistered());
        assertTrue(client.isIdVerified());
        assertEquals(((IndividualDto)client).getIsForeignRegistered(),"Y");
        verify(clientIntegrationService, times(1)).loadClientByCISKey(eq(CIS_KEY), any(ServiceErrors.class));
        verify(clientIntegrationService, times(1)).loadClientDetails(eq(person.getClientKey()), any(ServiceErrors.class));
    }

    @Test
    public void findCustomerFromExistingClientListWithForeignRegistered_N() {
        CustomerDataDto customerDataDto = new CustomerDataDto();
        customerDataDto.setTaxResidenceCountries(Arrays.asList(getTaxResidenceCountry("us","btfg$tin_never_iss",null),getTaxResidenceCountry("FOREIGN","","N")));
        when(directInvestorDataDtoService.find(any(ClientUpdateKey.class), any(ServiceErrors.class))).thenReturn(customerDataDto);
        prepareExistingClients(individual(CIS_KEY, "12345"));
        IndividualDetail person = person("Mr", "Robert", "Brogue", DateTime.parse("1982-11-02"), Gender.MALE, IdentityVerificationStatus.Completed);
        prepareClientDetail("12345", person);
        final ClientDto client = service.find(key, errors);
        assertTrue(client instanceof IndividualDto);
        assertEquals(fromPlainTextUsingTL("12345").toString(), client.getKey().getClientId());
        assertEquals("Robert Brogue", client.getFullName());
        assertTrue(client.isRegistered());
        assertTrue(client.isIdVerified());
        assertEquals(((IndividualDto)client).getIsForeignRegistered(),"N");
        verify(clientIntegrationService, times(1)).loadClientByCISKey(eq(CIS_KEY), any(ServiceErrors.class));
        verify(clientIntegrationService, times(1)).loadClientDetails(eq(person.getClientKey()), any(ServiceErrors.class));
    }

    @Test
    public void findCustomerFromGCM() {
        when(clientIntegrationService.loadClientByCISKey(eq(CIS_KEY), any(ServiceErrors.class))).thenReturn(null);
        prepareCustomerInformation(customer("AU", "MR", "ANCG", "in"));
        Code code = createCountryCodeMock();
        when(staticIntegrationService.loadCodeByUserId(eq(COUNTRY), eq("AU"), any(ServiceErrors.class))).thenReturn(code);
        Code codeTitle = createTitleCodeMock("MR", "Mr");
        when(staticIntegrationService.loadCodes(eq(PERSON_TITLE), any(ServiceErrors.class))).thenReturn(singletonList(codeTitle));
        Code codeStreetType = createTitleCodeMock("ANCG", "Anchorage");
        when(staticIntegrationService.loadCodes(eq(ADDRESS_STREET_TYPE), any(ServiceErrors.class))).thenReturn(singletonList(codeStreetType));
        final ClientDto client = service.find(key, errors);
        assertNull(client.getKey());
        assertTrue(client instanceof IndividualDto);
        assertFalse(client.isRegistered());
        assertTrue(client.isIdVerified());
        Map<String, String> expectedResult = new HashMap<String,String>();
        expectedResult.put("investorType","individual");
        expectedResult.put("title","MR");
        expectedResult.put("titleLabel","Mr");
        expectedResult.put("firstName","George");
        expectedResult.put("middleName","Avery");
        expectedResult.put("lastName","Test");
        expectedResult.put("gender","Male");
        expectedResult.put("fullName","George Avery Test");
        expectedResult.put("streetType","ANCG");
        expectedResult.put("gcmStreetType","Anchorage");
        assertIndividual((IndividualDto) client, expectedResult);

        verify(clientIntegrationService, times(1)).loadClientByCISKey(eq(CIS_KEY), any(ServiceErrors.class));
        verify(customerDataManagementService, times(1)).retrieveCustomerInformation(any(CustomerManagementRequest.class),anyList(),any(ServiceErrors.class));
        verify(staticIntegrationService,times(1)).loadCodes(eq(CodeCategory.ADDRESS_STREET_TYPE), any(ServiceErrors.class));
    }

    @Test
    public void findCustomerFromGCMForeignRegistered() {
        when(clientIntegrationService.loadClientByCISKey(eq(CIS_KEY), any(ServiceErrors.class))).thenReturn(null);

        CustomerData customer = customer("AU", "MR", "ANCG", "in");
        IndividualDetails individualDetails = customer.getIndividualDetails();
        individualDetails.setIsForeignRegistered("Y");
        customer.setTaxResidenceCountries(taxResidenceCountries("in"));
        prepareCustomerInformation(customer);

        Code code = createCountryCodeMock();
        when(staticIntegrationService.loadCodeByUserId(eq(COUNTRY), eq("AU"), any(ServiceErrors.class))).thenReturn(code);
        Code codeTitle = createTitleCodeMock("MR", "Mr");
        when(staticIntegrationService.loadCodes(eq(PERSON_TITLE), any(ServiceErrors.class))).thenReturn(singletonList(codeTitle));
        Code codeStreetType = createTitleCodeMock("ANCG", "Anchorage");
        when(staticIntegrationService.loadCodes(eq(ADDRESS_STREET_TYPE), any(ServiceErrors.class))).thenReturn(singletonList(codeStreetType));
        when(staticIntegrationService.loadCodeByAvaloqId(eq(COUNTRY), eq("in"), any(ServiceErrors.class))).thenReturn(new CodeImpl("codeId", "IN", "India", "in"));
        final IndividualDto client = (IndividualDto) service.find(key, errors);
        assertNull(client.getKey());
        assertFalse(client.isRegistered());
        assertTrue(client.isIdVerified());

        assertThat(client.getIsForeignRegistered(), is("Y"));

        List<TaxResidenceCountriesDto> taxResidenceCountries = client.getTaxResidenceCountries();
        assertThat(taxResidenceCountries.size(), is(1));

        verify(clientIntegrationService, times(1)).loadClientByCISKey(eq(CIS_KEY), any(ServiceErrors.class));
        verify(customerDataManagementService, times(1)).retrieveCustomerInformation(any(CustomerManagementRequest.class),anyList(),any(ServiceErrors.class));
        verify(staticIntegrationService,times(1)).loadCodes(eq(CodeCategory.ADDRESS_STREET_TYPE), any(ServiceErrors.class));
    }

    @Test
    public void findCustomerFromGCMNotForeignRegistered() {
        when(clientIntegrationService.loadClientByCISKey(eq(CIS_KEY), any(ServiceErrors.class))).thenReturn(null);

        CustomerData customer = customer("AU", "MR", "ANCG", "in");
        customer.setTaxResidenceCountries(taxResidenceCountries("in"));
        prepareCustomerInformation(customer);

        Code code = createCountryCodeMock();
        when(staticIntegrationService.loadCodeByUserId(eq(COUNTRY), eq("AU"), any(ServiceErrors.class))).thenReturn(code);
        Code codeTitle = createTitleCodeMock("MR", "Mr");
        when(staticIntegrationService.loadCodes(eq(PERSON_TITLE), any(ServiceErrors.class))).thenReturn(singletonList(codeTitle));
        Code codeStreetType = createTitleCodeMock("ANCG", "Anchorage");
        when(staticIntegrationService.loadCodes(eq(ADDRESS_STREET_TYPE), any(ServiceErrors.class))).thenReturn(singletonList(codeStreetType));
        final IndividualDto client = (IndividualDto) service.find(key, errors);
        assertNull(client.getKey());
        assertFalse(client.isRegistered());
        assertTrue(client.isIdVerified());

        assertThat(client.getIsForeignRegistered(), is("N"));
        assertNull(client.getTaxResidenceCountries());

        verify(clientIntegrationService, times(1)).loadClientByCISKey(eq(CIS_KEY), any(ServiceErrors.class));
        verify(customerDataManagementService, times(1)).retrieveCustomerInformation(any(CustomerManagementRequest.class),anyList(),any(ServiceErrors.class));
        verify(staticIntegrationService,times(1)).loadCodes(eq(CodeCategory.ADDRESS_STREET_TYPE), any(ServiceErrors.class));
    }


    @Test
    public void findCustomerWithNonStandardAddressFromGCM() {
        when(clientIntegrationService.loadClientByCISKey(eq(CIS_KEY), any(ServiceErrors.class))).thenReturn(null);
        prepareCustomerInformation(customerWithNonStandardAddress("AU", "MR"));
        Code code = createCountryCodeMock();
        when(staticIntegrationService.loadCodeByUserId(eq(COUNTRY), eq("AU"), any(ServiceErrors.class))).thenReturn(code);
        Code codeTitle = createTitleCodeMock("MR", "Mr");
        when(staticIntegrationService.loadCodes(eq(PERSON_TITLE), any(ServiceErrors.class))).thenReturn(singletonList(codeTitle));
        final ClientDto client = service.find(key, errors);
        assertNull(client.getKey());
        assertTrue(client instanceof IndividualDto);
        AddressDto addressDto = client.getAddresses().get(0);
        assertFalse(addressDto.isStandardAddressFormat());
        assertThat(addressDto.getAddressLine1(), is("UNIT 10"));
        assertThat(addressDto.getAddressLine2(), is("4  CHARLES  STREET"));
        assertThat(addressDto.getCity(), is("PARRAMATTA"));
        assertThat(addressDto.getState(), is("NSW"));
        assertThat(addressDto.getPostcode(), is("2150"));
        assertThat(addressDto.getCountry(), is("Australia"));
        verify(staticIntegrationService,times(0)).loadCodes(eq(CodeCategory.ADDRESS_STREET_TYPE), any(ServiceErrors.class));
    }

    @Test
    public void findCustomerFromGCMWithIMCodeForTitle() {
        when(clientIntegrationService.loadClientByCISKey(eq(CIS_KEY), any(ServiceErrors.class))).thenReturn(null);
        prepareCustomerInformation(customer("AU", "SIS", "ST", "in"));
        Code code = createCountryCodeMock();
        when(staticIntegrationService.loadCodeByUserId(eq(COUNTRY), eq("AU"), any(ServiceErrors.class))).thenReturn(code);
        Code codeTitle = createTitleCodeMock("SIS", "Sister");
        when(staticIntegrationService.loadCodes(eq(PERSON_TITLE), any(ServiceErrors.class))).thenReturn(singletonList(codeTitle));
        Code streetCodeMock = createTitleCodeMock("ST", "Street");
        when(staticIntegrationService.loadCodes(eq(ADDRESS_STREET_TYPE), any(ServiceErrors.class))).thenReturn(singletonList(streetCodeMock));
        final ClientDto client = service.find(key, errors);
        assertNull(client.getKey());
        assertTrue(client instanceof IndividualDto);
        assertFalse(client.isRegistered());
        assertTrue(client.isIdVerified());
        Map<String, String> expectedResult = new HashMap<String,String>();
        expectedResult.put("investorType","individual");
        expectedResult.put("title","SIS");
        expectedResult.put("titleLabel","Sister");
        expectedResult.put("firstName","George");
        expectedResult.put("middleName","Avery");
        expectedResult.put("lastName","Test");
        expectedResult.put("gender","Male");
        expectedResult.put("fullName","George Avery Test");
        expectedResult.put("streetType","ST");
        expectedResult.put("gcmStreetType","Street");
        assertIndividual((IndividualDto) client ,expectedResult);

        verify(clientIntegrationService, times(1)).loadClientByCISKey(eq(CIS_KEY), any(ServiceErrors.class));
        verify(customerDataManagementService, times(1)).retrieveCustomerInformation(any(CustomerManagementRequest.class),anyList(),any(ServiceErrors.class));
    }

    @Test
    public void findCustomerFromGCMWithTitleDr() {
        when(clientIntegrationService.loadClientByCISKey(eq(CIS_KEY), any(ServiceErrors.class))).thenReturn(null);
        prepareCustomerInformation(customer("AU", "Dr.", "ST", "in"));
        Code code = createCountryCodeMock();
        when(staticIntegrationService.loadCodeByUserId(eq(COUNTRY), eq("AU"), any(ServiceErrors.class))).thenReturn(code);
        Code codeTitle = createTitleCodeMock("DR", "DR");
        when(staticIntegrationService.loadCodes(eq(PERSON_TITLE), any(ServiceErrors.class))).thenReturn(singletonList(codeTitle));
        Code streetCodeMock = createTitleCodeMock("ST", "Street");
        when(staticIntegrationService.loadCodes(eq(ADDRESS_STREET_TYPE), any(ServiceErrors.class))).thenReturn(singletonList(streetCodeMock));
        final ClientDto client = service.find(key, errors);
        assertNull(client.getKey());
        assertTrue(client instanceof IndividualDto);
        assertFalse(client.isRegistered());
        assertTrue(client.isIdVerified());
        Map<String, String> expectedResult = new HashMap<String,String>();
        expectedResult.put("investorType","individual");
        expectedResult.put("title","DR");
        expectedResult.put("titleLabel","DR");
        expectedResult.put("firstName","George");
        expectedResult.put("middleName","Avery");
        expectedResult.put("lastName","Test");
        expectedResult.put("gender","Male");
        expectedResult.put("fullName","George Avery Test");
        expectedResult.put("streetType","ST");
        expectedResult.put("gcmStreetType","Street");
        assertIndividual((IndividualDto) client ,expectedResult);

        verify(clientIntegrationService, times(1)).loadClientByCISKey(eq(CIS_KEY), any(ServiceErrors.class));
        verify(customerDataManagementService, times(1)).retrieveCustomerInformation(any(CustomerManagementRequest.class),anyList(),any(ServiceErrors.class));
    }


    @Test
    public void findCustomerFromGCMWithUCMCodeForCountry() {
        when(clientIntegrationService.loadClientByCISKey(eq(CIS_KEY), any(ServiceErrors.class))).thenReturn(null);
        prepareCustomerInformation(customer("AUS", "MISS", "ARC", "in"));

        Code code = createCountryCodeMock();
        when(staticIntegrationService.loadCodes(eq(COUNTRY), any(ServiceErrors.class))).thenReturn(singletonList(code));
        Code codeTitle = createTitleCodeMock("MISS", "Miss");
        when(staticIntegrationService.loadCodes(eq(PERSON_TITLE), any(ServiceErrors.class))).thenReturn(singletonList(codeTitle));
        Code streetCodeMock = createTitleCodeMock("ARC", "Arcade");
        when(staticIntegrationService.loadCodes(eq(ADDRESS_STREET_TYPE), any(ServiceErrors.class))).thenReturn(singletonList(streetCodeMock));
        final ClientDto client = service.find(key, errors);
        assertNull(client.getKey());
        assertTrue(client instanceof IndividualDto);
        assertFalse(client.isRegistered());
        assertTrue(client.isIdVerified());
        Map<String, String> expectedResult = new HashMap<String,String>();
        expectedResult.put("investorType","individual");
        expectedResult.put("title", "MISS");
        expectedResult.put("titleLabel", "Miss");
        expectedResult.put("firstName", "George");
        expectedResult.put("middleName", "Avery");
        expectedResult.put("lastName", "Test");
        expectedResult.put("gender", "Male");
        expectedResult.put("fullName", "George Avery Test");
        expectedResult.put("streetType","ARC");
        expectedResult.put("gcmStreetType","Arcade");
        assertIndividual((IndividualDto) client, expectedResult);

        verify(clientIntegrationService, times(1)).loadClientByCISKey(eq(CIS_KEY), any(ServiceErrors.class));
        verify(customerDataManagementService, times(1)).retrieveCustomerInformation(any(CustomerManagementRequest.class),anyList(),any(ServiceErrors.class));
    }

    @Test
    public void findCustomerFromGCMWithNullCountryAndStreetType() throws Exception {
        when(clientIntegrationService.loadClientByCISKey(eq(CIS_KEY), any(ServiceErrors.class))).thenReturn(null);
        prepareCustomerInformation(customer(null, "MISS", null, "in"));

        Code codeTitle = createTitleCodeMock("MISS", "Miss");
        when(staticIntegrationService.loadCodes(eq(PERSON_TITLE), any(ServiceErrors.class))).thenReturn(singletonList(codeTitle));
        Code streetCodeMock = createTitleCodeMock("ARC", "Arcade");
        when(staticIntegrationService.loadCodes(eq(ADDRESS_STREET_TYPE), any(ServiceErrors.class))).thenReturn(singletonList(streetCodeMock));
        final ClientDto client = service.find(key, errors);
        assertNull(client.getKey());

        assertNull(client.getAddresses().get(0).getGcmStreetType());
        assertNull(client.getAddresses().get(0).getCountry());
    }

    private Code createTitleCodeMock(String userId, String name) {
        Code codeTitle = mock(Code.class);
        when(codeTitle.getUserId()).thenReturn(userId);
        when(codeTitle.getName()).thenReturn(name);
        when(codeTitle.getField(BTFG_$_IM_CODE)).thenReturn(new FieldImpl(BTFG_$_IM_CODE, userId));
        return codeTitle;
    }

    private Code createCountryCodeMock() {
        Code code = mock(Code.class);
        when(code.getUserId()).thenReturn("AU");
        when(code.getName()).thenReturn("Australia");
        when(code.getField(UCM_CODE)).thenReturn(new FieldImpl(UCM_CODE, "AUS"));
        return code;
    }

    private void prepareExistingClients(com.btfin.panorama.service.avaloq.domain.existingclient.Client client) {
        when(clientIntegrationService.loadClientByCISKey(eq(CIS_KEY), any(ServiceErrors.class))).thenReturn(client);
    }

    @SuppressWarnings("unchecked")
    private void prepareCustomerInformation(CustomerData result) {
        when(customerDataManagementService.retrieveCustomerInformation(requestWith(CIS_KEY), anyList(), eq(errors))).thenReturn(result);
    }

    private void prepareClientDetail(String clientId, ClientDetail details) {
        com.bt.nextgen.service.integration.userinformation.ClientKey clientKey = com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf(clientId);
        details.setClientKey(clientKey);
        when(clientIntegrationService.loadClientDetails(clientKey, errors)).thenReturn(details);
    }

    private IndividualWithAccountDataImpl individual(String cisKey, String clientId) {
        final IndividualWithAccountDataImpl individual = mock(IndividualWithAccountDataImpl.class);
        when(individual.getCISKey()).thenReturn(CISKey.valueOf(cisKey));
        when(individual.getClientKey()).thenReturn(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf(clientId));
        when(individual.getLegalForm()).thenReturn(InvestorType.INDIVIDUAL);
        when(individual.getIdentityVerificationStatus()).thenReturn(IdentityVerificationStatus.Completed);
        when(individual.getPrimaryEmail()).thenReturn("primaryEmail@test.com");
        when(individual.getPrimaryMobile()).thenReturn("0411111111");
        return individual;
    }

    private IndividualDetail person(String title, String firstName, String lastName, DateTime dob, Gender gender, IdentityVerificationStatus idvStatus) {
        IndividualDetailImpl person = new IndividualDetailImpl();
        person.setTitle(title);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setFullName(fullName(firstName, lastName));
        person.setDateOfBirth(dob);
        person.setGender(gender);
        person.setExemptionReason(ExemptionReason.NO_EXEMPTION);
        person.setIdVerificationStatus(idvStatus);
        person.setCisId("523698");
        return person;
    }

    private static CustomerManagementRequest requestWith(final String cisKey) {
        return argThat(new BaseMatcher<CustomerManagementRequest>() {
            @Override
            public boolean matches(Object item) {
                final CustomerManagementRequest request = (CustomerManagementRequest) item;
                return cisKey.equals(request.getCISKey().getId());
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("CustomerManagementRequest[CISKey=").appendText(cisKey).appendText("]");
            }
        });
    }
    /**
     * Whip up mock customer data from GCM.
     * @return customer with sufficient data mocked up.
     */
    private static CustomerData customer(String countryCode, String titleCode, String streetType, String countryIntlID) {
        final CustomerData customer = new CustomerDataImpl();
        customer.setIndividualDetails(individualDetails(titleCode));
        customer.setAddress(address(countryCode, streetType));
        customer.setPhoneNumbers(asList(phone("4", "34569090", MOBILE_PHONE_PRIMARY), phone("2", "82542234", BUSINESS_TELEPHONE)));
        customer.setEmails(asList(email("test@google.com"), email("defunct@gdaymate.com.au")));
        customer.setTaxResidenceCountries(taxResidenceCountries(countryIntlID));
        return customer;
    }

    private static List<TaxResidenceCountry> taxResidenceCountries(String countryIntlID) {
        TaxResidenceCountry taxResidenceCountry1 = new TaxResidenceCountry();
        taxResidenceCountry1.setResidenceCountry(countryIntlID);
        taxResidenceCountry1.setExemptionReason("Exempted");

        TaxResidenceCountry taxResidenceCountry2 = new TaxResidenceCountry();
        taxResidenceCountry2.setResidenceCountry("FOREIGN");
        taxResidenceCountry2.setTin("tin");

        return Arrays.asList(taxResidenceCountry1, taxResidenceCountry2);
    }

    private static CustomerData customerWithNonStandardAddress(String countryCode, String titleCode){
        CustomerDataImpl customerData = new CustomerDataImpl();
        customerData.setIndividualDetails(individualDetails(titleCode));
        customerData.setAddress(nonStandardAddress(countryCode));
        customerData.setPhoneNumbers(asList(phone("4", "34569090", MOBILE_PHONE_PRIMARY), phone("2", "82542234", BUSINESS_TELEPHONE)));
        customerData.setEmails(asList(email("test@google.com"), email("defunct@gdaymate.com.au")));
        return customerData;
    }

    private static Address nonStandardAddress(String countryCode) {
        NonStandardPostalAddress nonstandardAddress = new NonStandardPostalAddress();
        nonstandardAddress.setAddressLine1("UNIT 10");
        nonstandardAddress.setAddressLine2("4  CHARLES  STREET");
        nonstandardAddress.setCity("PARRAMATTA");
        nonstandardAddress.setState("NSW");
        nonstandardAddress.setPostCode("2150");
        nonstandardAddress.setCountry(countryCode);
        return new InternationalAddressV7Adapter(nonstandardAddress);
    }

    private static IndividualDetails individualDetails(String titleCode) {
        final IndividualDetails individual = new IndividualDetails();
        individual.setTitle(titleCode);
        individual.setFirstName("George");
        individual.setMiddleNames(asList("Avery", "Bumpkin"));
        individual.setLastName("Test");
        individual.setGender("M");
        individual.setDateOfBirth("12/04/1978");
        individual.setIdVerified(true);
        individual.setUserName(Z_NUMBER);
        individual.setIsForeignRegistered("N");
        return individual;
    }

    private static Address address(String countryCode, String streetType) {
        StandardPostalAddress standardPostalAddress = new StandardPostalAddress();

        standardPostalAddress.setFloorNumber("10");
        standardPostalAddress.setStreetNumber("150");
        standardPostalAddress.setStreetName("Collins");
        standardPostalAddress.setStreetType(streetType);
        standardPostalAddress.setCity("Melbourne");
        standardPostalAddress.setState("Victoria");
        standardPostalAddress.setPostCode("3000");
        // NB. Deliberately set shortened country CODE for the country, rather than the proper name. This reflects
        // what is implemented in the GCM retrieve operation.
        standardPostalAddress.setCountry(countryCode);

        final AddressAdapter address = new AddressAdapterV7(standardPostalAddress);
        address.setStandardAddressFormat(true);
        address.setSuburb("Melbourne");
        address.setStateCode("VIC");
        address.setAddressKey(AddressKey.valueOf("72591882"));
        address.setDomicile(true);
        return address;
    }

    private static Phone phone(String countryCode, String areaCode, String number, AddressMedium type) {
        final PhoneImpl phone = new PhoneImpl();
        phone.setCountryCode(countryCode);
        phone.setAreaCode(areaCode);
        phone.setNumber(number);
        phone.setCategory(AddressType.ELECTRONIC);
        phone.setType(type);
        return phone;
    }

    private static Phone phone(String areaCode, String number, AddressMedium type) {
        return phone("61", areaCode, number, type);
    }

    private static Email email(String emailAddress) {
        final EmailImpl email = new EmailImpl();
        email.setEmail(emailAddress);
        email.setCategory(AddressType.ELECTRONIC);
        return email;
    }

    private static void assertIndividual(IndividualDto individual,  Map<String, String> expectedResult ) {
        assertEquals(CIS_KEY, individual.getCisId());
        assertEquals(Z_NUMBER, individual.getUserName());
        assertEquals(expectedResult.get("investorType"), individual.getInvestorType());
        assertEquals(expectedResult.get("title"), individual.getTitle());
        assertEquals(expectedResult.get("titleLabel"), individual.getGcmTitleLabel());
        assertEquals(expectedResult.get("firstName"), individual.getFirstName());
        assertEquals(expectedResult.get("middleName"), individual.getMiddleName());
        assertEquals(expectedResult.get("lastName"), individual.getLastName());
        assertEquals(expectedResult.get("gender"), individual.getGender());
        assertEquals(expectedResult.get("fullName"), individual.getFullName());
        assertAddresses(individual.getAddresses(), expectedResult.get("streetType"), expectedResult.get("gcmStreetType"));
        assertEmails(individual.getEmails());
        assertPhones(individual.getPhones());
    }

    private static void assertPhones(List<PhoneDto> phones) {
        assertPhone(phones.get(0), "04", "34569090", "Primary");
        assertPhone(phones.get(1), "02", "82542234", "Work");
    }

    private static void assertPhone(PhoneDto phone, String countryCode, String areaCode, String number, String type) {
        assertEquals(countryCode, phone.getCountryCode());
        assertEquals(areaCode, phone.getAreaCode());
        assertEquals(number, phone.getNumber());
        assertEquals(type, phone.getPhoneType());
    }

    private static void assertPhone(PhoneDto phone, String areaCode, String number, String category) {
        assertPhone(phone, "61", areaCode, number, category);
    }

    private static void assertAddresses(List<AddressDto> addresses, String expectedStreetTypeCode, String expectedStreetLable) {
        assertEquals(1, addresses.size());
        final AddressDto address = addresses.get(0);
        assertEquals("10", address.getFloor());
        assertEquals("150", address.getStreetNumber());
        assertEquals("Collins", address.getStreetName());
        assertEquals(expectedStreetLable, address.getGcmStreetType());
        assertEquals(expectedStreetTypeCode, address.getStreetType());
        assertEquals("Melbourne", address.getSuburb());
        assertEquals("3000", address.getPostcode());
        assertEquals("Australia", address.getCountry());
        assertEquals("AU", address.getCountryCode());
        assertTrue(address.isDomicile());
        assertTrue(address.isGcmAddress());
    }

    private static void assertEmails(List<EmailDto> emails) {
        assertEquals(2, emails.size());
        assertEquals("test@google.com", emails.get(0).getEmail());
        assertTrue(emails.get(0).getGcmMastered());
        assertEquals("defunct@gdaymate.com.au", emails.get(1).getEmail());
        assertTrue(emails.get(1).getGcmMastered());
    }

    private static TaxResidenceCountriesDto getTaxResidenceCountry(String residenceCountry, String exemptionReason, String tin) {
        TaxResidenceCountriesDto taxResidenceCountriesDto =  new TaxResidenceCountriesDto();
        taxResidenceCountriesDto.setTaxResidenceCountry(residenceCountry);
        taxResidenceCountriesDto.setTaxExemptionReason(exemptionReason);
        taxResidenceCountriesDto.setTin(tin);

        return taxResidenceCountriesDto;
    }
}
