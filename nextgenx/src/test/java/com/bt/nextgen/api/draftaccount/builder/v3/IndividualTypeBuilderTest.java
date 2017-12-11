package com.bt.nextgen.api.draftaccount.builder.v3;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ns.btfin_com.identityverification.v1_1.PartyIdentificationStatusType;
import ns.btfin_com.party.v3_0.GenderTypeCode;
import ns.btfin_com.party.v3_0.IDVPerformedByIntermediaryType;
import ns.btfin_com.party.v3_0.IndividualType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.PartyIdentificationInformationsIndType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.RegisteredResidentialAddressDetailType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberTypeCode;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberUsageTypeCode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.draftaccount.model.form.ExtendedPersonDetailsFormFactory;
import com.bt.nextgen.api.draftaccount.model.form.IAccountSettingsForm;
import com.bt.nextgen.api.draftaccount.model.form.IAddressForm;
import com.bt.nextgen.api.draftaccount.model.form.IExtendedPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.gesb.locationmanagement.PostalAddress;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.Field;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertEquals;
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
public class IndividualTypeBuilderTest {

    @InjectMocks
    private IndividualTypeBuilder individualTypeBuilder;

    @Mock
    private AddressTypeBuilder addressTypeBuilder;

    @Mock
    private PartyIdentificationInformationBuilder partyIdentificationInformationBuilder;

    @Mock
    private IDVPerformedByIntermediaryTypeBuilder idvPerformedByIntermediaryTypeBuilder;

    @Mock
    private PurposeOfBusinessRelationshipTypeBuilder purposeOfBusinessRelationshipTypeBuilder;

    @Mock
    private BrokerUser adviser;

    @Mock
    private Broker dealer;

    @Mock
    private IAccountSettingsForm accountSettingsForm;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    @Mock
    private AddressV2CacheService addressV2CacheService;

    @Mock
    private ServiceErrors serviceErrors;

    @Mock
    private ns.btfin_com.sharedservices.common.address.v3_0.RegisteredResidentialAddressDetailType residentialAddressDetailType;

    @Before
    public void setUp(){
        when(addressTypeBuilder.getAddressType(any(IAddressForm.class), any(RegisteredResidentialAddressDetailType.class), eq(serviceErrors))).thenReturn(new
            RegisteredResidentialAddressDetailType());
        when(partyIdentificationInformationBuilder.getPartyIdentificationInformation(any(IExtendedPersonDetailsForm.class)))
                .thenReturn(new PartyIdentificationInformationsIndType());
        when(idvPerformedByIntermediaryTypeBuilder.intermediary(adviser, dealer)).thenReturn(new IDVPerformedByIntermediaryType());
        when(accountSettingsForm.hasSourceOfFunds()).thenReturn(true);
        when(accountSettingsForm.getSourceOfFunds()).thenReturn("Compensation payment");
        mockStaticIntegrationServiceForTitle("MR");
    }

    @Test
    public void shouldBuildIndividualTypeWithAppropriateInfo() throws IOException {
        String json = "{" +
                "       \"title\": \"mr\",\n" +
                "        \"firstname\": \"John$##$\",\n" +
                "        \"middlename\": \"Doe\",\n" +
                "        \"lastname\": \"Smith\",\n" +
                "        \"preferredname\": \"Joe\",\n" +
                "        \"alternatename\": \"Joe Alternate\",\n" +
                "        \"dateofbirth\": \"01/01/1980\",\n" +
                "        \"gender\": \"male\"," +
                "        \"wealthsource\": \"Gift/Donation\"," +
                "        \"preferredcontact\": \"homenumber\",\n" +
                "        \"homenumber\": {\n" +
                "            \"label\": \"homenumber\",\n" +
                "            \"areacode\": \"04\",\n" +
                "            \"countrycode\": \"61\",\n" +
                "            \"value\": \"87654321\"\n" +
                "        },\n" +
                "        \"worknumber\": {\n" +
                "            \"label\": \"worknumber\",\n" +
                "            \"areacode\": \"02\",\n" +
                "            \"countrycode\": \"61\",\n" +
                "            \"value\": \"12345678\"\n" +
                "        }," +
                "\"resaddress\": {}\n}";
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> individualDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        IndividualType individual = individualTypeBuilder.getIndividualType(
            ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(individualDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true), adviser, dealer,
            accountSettingsForm, serviceErrors);
        assertThat(individual.getTitlePrefix(), is("MR"));
        assertThat(individual.getGivenName(), is("John$##$"));
        assertThat(individual.getLastName(), is("Smith"));
        assertThat(individual.getMiddleName().get(0), is("Doe"));
        assertThat(individual.getAlternateName().get(0).getName(), is("Joe"));
        assertTrue(individual.getAlternateName().get(0).isPreferred());
        assertThat(individual.getAlternateName().get(1).getName(), is("Joe Alternate"));
        assertThat(individual.getGender(), is(GenderTypeCode.MALE));
        assertThat(individual.getDateOfBirth(), is(XMLGregorianCalendarUtil.getXMLGregorianCalendar("01/01/1980", "dd/MM/yyyy")));


        assertThat(individual.getHomePhoneNumber().getContactNumberType(), is(ContactNumberTypeCode.PHONE));
        assertThat(individual.getHomePhoneNumber().getContactNumber().getStandardContactNumber().getSubscriberNumber(), is("87654321"));
        assertThat(individual.getHomePhoneNumber().getContactNumberUsage(), hasItems(ContactNumberUsageTypeCode.PREFERRED, ContactNumberUsageTypeCode.HOME));


        assertThat(individual.getWorkPhoneNumber().getContactNumberType(), is(ContactNumberTypeCode.PHONE));
        assertThat(individual.getWorkPhoneNumber().getContactNumber().getStandardContactNumber().getAreaCode(), is("2"));
        assertThat(individual.getWorkPhoneNumber().getContactNumber().getStandardContactNumber().getSubscriberNumber(), is("12345678"));
        assertThat(individual.getWorkPhoneNumber().getContactNumberUsage(), hasItems(ContactNumberUsageTypeCode.WORK));
    }

    @Test
    public void shouldBuildIndividualWithCanonicalCodeAsTitle() throws IOException {
        String json = "{" +
                "       \"title\": \"MR\",\n" +
                "        \"dateofbirth\": \"01/01/1980\",\n" +
                "        \"firstname\": \"John$##$\",\n" +
                "        \"middlename\": \"Doe\",\n" +
                "        \"lastname\": \"Smith\",\n" +
                "        \"gender\": \"male\"}";
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> individualDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });

        Code codeImpl = Mockito.mock(CodeImpl.class);
        Field field1 = mock(Field.class);
        when(codeImpl.getField(anyString())).thenReturn(field1);
        when(field1.getValue()).thenReturn("MR");
        when(staticIntegrationService.loadCodes(eq(CodeCategory.PERSON_TITLE), any(ServiceErrors.class))).thenReturn(Arrays.asList(codeImpl));

        IndividualType individual = individualTypeBuilder.getIndividualType(
            ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(individualDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true), adviser,
            dealer, accountSettingsForm, serviceErrors);
        assertThat(individual.getTitlePrefix(), is("MR"));
    }

    @Test
    public void shouldBuildIndividualWithDrTitle() throws IOException {
        String json = "{" +
                "       \"title\": \"DR\",\n" +
                "        \"dateofbirth\": \"01/01/1980\",\n" +
                "        \"firstname\": \"John$##$\",\n" +
                "        \"middlename\": \"Doe\",\n" +
                "        \"lastname\": \"Smith\",\n" +
                "        \"gender\": \"male\"}";
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> individualDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });

        IndividualType individual = individualTypeBuilder.getIndividualType(
            ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(individualDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true), adviser,
            dealer, accountSettingsForm, serviceErrors);
        assertThat(individual.getTitlePrefix(), is("Dr."));
    }

    @Test
    public void shouldBuildIndividualTypeWithGenderAsFemale() throws IOException {
        String json = "{\"gender\": \"female\"," +
                "\"firstname\": \"John$##$\"," +
                "\"lastname\": \"Smith\"," +
                "\"wealthsource\": \"Gift/Donation\"," +
                "\"resaddress\": {}\n" +
                "}";
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> individualDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        IndividualType individual = individualTypeBuilder.getIndividualType(
            ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(individualDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true), adviser,
            dealer, accountSettingsForm, serviceErrors);
        assertThat(individual.getGender(), is(GenderTypeCode.FEMALE));
    }

    @Test
    public void shouldBuildIndividualTypeWithGenderAsOther() throws IOException {
        String json = "{\"gender\": \"other\"," +
                "\"firstname\": \"John$##$\"," +
                "\"lastname\": \"Smith\"," +
                "\"wealthsource\": \"Gift/Donation\"," +
                "\"resaddress\": {}\n" +
                "}";
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> individualDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        IndividualType individual = individualTypeBuilder.getIndividualType(
            ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(individualDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true), adviser,
            dealer, accountSettingsForm, serviceErrors);
        assertThat(individual.getGender(), is(GenderTypeCode.OTHER_RESPONSE));
    }

    @Test
    public void shouldBuildIndividualTypeWithYesIdvStatus() throws IOException {
        String json = "{\"idVerified\": true," +
                "\"gender\": \"male\"," +
                "\"firstname\": \"John$##$\"," +
                "\"lastname\": \"Smith\"," +
                "\"wealthsource\": \"Gift/Donation\"," +
                "\"resaddress\": {}\n" +
                "}";
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> individualDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        IndividualType individual = individualTypeBuilder.getIndividualType(
            ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(individualDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true), adviser,
            dealer, accountSettingsForm, serviceErrors);
        assertThat(individual.getPartyIdentificationStatus(), is(PartyIdentificationStatusType.YES));
    }

    @Test
    public void shouldBuildIndividualTypeWithYesIdVerified() throws IOException {
        String json = "{\"idVerified\": true," +
                "\"gender\": \"male\"," +
                "\"firstname\": \"John$##$\"," +
                "\"lastname\": \"Smith\"," +
                "\"wealthsource\": \"Gift/Donation\"," +
                "\"resaddress\": {}\n" +
                "}";
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> individualDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        IndividualType individual = individualTypeBuilder.getIndividualType(
            ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(individualDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true), adviser,
            dealer, accountSettingsForm, serviceErrors);
        assertThat(individual.getPartyIdentificationStatus(), is(PartyIdentificationStatusType.YES));
    }

    @Test
    public void shouldBuildIndividualTypeWithoutAnyIdvStatus() throws IOException {
        String json = "{\"gender\": \"male\"," +
                "\"firstname\": \"John$##$\"," +
                "\"lastname\": \"Smith\"," +
                "\"wealthsource\": \"Gift/Donation\"," +
                "\"resaddress\": {}\n" +
                "}";
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> individualDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        IndividualType individual = individualTypeBuilder.getIndividualType(
            ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(individualDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true), adviser,
            dealer, accountSettingsForm, serviceErrors);
        assertNull(individual.getPartyIdentificationStatus());
    }

    @Test
    public void shouldBuildResidentialAddress_forGcmAddress() throws IOException {
        IPersonDetailsForm person = mock(IPersonDetailsForm.class);
        when(person.isGcmRetrievedPerson()).thenReturn(true);
        IAddressForm address = mock(IAddressForm.class);
        when(address.getStreetName()).thenReturn("Pitt");
        when(person.getGCMRetAddresses()).thenReturn(address);
        individualTypeBuilder.getIndividualType(person, adviser, dealer, accountSettingsForm, serviceErrors);
        ArgumentCaptor<IAddressForm> argument1 = ArgumentCaptor.forClass(IAddressForm.class);
        ArgumentCaptor<ns.btfin_com.sharedservices.common.address.v3_0.RegisteredResidentialAddressDetailType> argument2 = ArgumentCaptor.forClass(ns.btfin_com.sharedservices.common.address.v3_0.RegisteredResidentialAddressDetailType.class);
        ArgumentCaptor<Boolean> argument3 = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<ServiceErrors> argument4 = ArgumentCaptor.forClass(ServiceErrors.class);
        verify(addressTypeBuilder, times(1)).getAddressType(argument1.capture(), argument2.capture(), argument3.capture(), argument4.capture());
        assertEquals(argument1.getValue().getStreetName(), "Pitt");
        assertEquals(argument3.getValue(), true);
    }

    @Test
    public void shouldBuildResidentialAddress_forAddress() throws IOException {
        IPersonDetailsForm person = mock(IPersonDetailsForm.class);
        when(person.isGcmRetrievedPerson()).thenReturn(false);
        IAddressForm address = mock(IAddressForm.class);
        when(address.getStreetName()).thenReturn("Pitt");
        when(person.hasResidentialAddress()).thenReturn(true);
        when(person.getResidentialAddress()).thenReturn(address);
        individualTypeBuilder.getIndividualType(person, adviser, dealer, accountSettingsForm, serviceErrors);
        ArgumentCaptor<IAddressForm> argument1 = ArgumentCaptor.forClass(IAddressForm.class);
        ArgumentCaptor<ns.btfin_com.sharedservices.common.address.v3_0.RegisteredResidentialAddressDetailType> argument2 = ArgumentCaptor.forClass(ns.btfin_com.sharedservices.common.address.v3_0.RegisteredResidentialAddressDetailType.class);
        ArgumentCaptor<ServiceErrors> argument3 = ArgumentCaptor.forClass(ServiceErrors.class);
        verify(addressTypeBuilder, times(1)).getAddressType(argument1.capture(), argument2.capture(), argument3.capture());
        assertEquals(argument1.getValue().getStreetName(), "Pitt");
    }

    private void mockStaticIntegrationServiceForTitle(String title){
        CodeImpl codeImpl = Mockito.mock(CodeImpl.class);
        Field field = mock(Field.class);
        when(codeImpl.getField(anyString())).thenReturn(field);
        when(field.getValue()).thenReturn(title);
        when(staticIntegrationService.loadCodeByUserId(eq(CodeCategory.PERSON_TITLE), eq(title.toLowerCase()), any(ServiceErrors.class))).thenReturn(codeImpl);
    }
}
