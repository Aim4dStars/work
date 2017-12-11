package com.bt.nextgen.api.client.util;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.ClientDto;
import com.bt.nextgen.api.client.model.ClientTxnDto;
import com.bt.nextgen.api.client.model.CompanyDto;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.model.RegisteredEntityDto;
import com.bt.nextgen.api.client.model.SmsfDto;
import com.bt.nextgen.api.client.model.TaxResidenceCountriesDto;
import com.bt.nextgen.api.client.model.TrustDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import com.btfin.panorama.core.security.avaloq.accountactivation.FormerName;
import com.bt.nextgen.service.avaloq.accountactivation.FormerNameImpl;
import com.bt.nextgen.service.avaloq.client.TaxResidenceCountryImpl;
import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.bt.nextgen.service.avaloq.domain.CompanyImpl;
import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.domain.InvestorDetailImpl;
import com.bt.nextgen.service.avaloq.domain.PhoneImpl;
import com.bt.nextgen.service.avaloq.domain.RegisteredEntityImpl;
import com.bt.nextgen.service.avaloq.domain.SmsfImpl;
import com.bt.nextgen.service.avaloq.domain.TrustImpl;
import com.bt.nextgen.service.group.customer.groupesb.TaxResidenceCountry;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.Field;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.ExemptionReason;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.domain.IdentityVerificationStatus;
import com.btfin.panorama.core.security.integration.domain.IndividualDetail;
import com.btfin.panorama.core.security.integration.domain.InvestorDetail;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.PensionExemptionReason;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.domain.TrustType;
import com.bt.nextgen.service.integration.domain.TrustTypeDesc;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ClientDetailDtoConverterTest {

    ServiceErrors serviceErrors;
    List<Address> addresses;
    List<Phone> phones;
    List<Email> emails;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    @Mock
    private Code code1;

    @Mock
    private Code code2;

    @Mock
    private Field countryField1;

    @Mock
    private Field countryField2;

    @Mock
    private Field taxExemptionField2;

    @Before
    public void setup() {
        addresses = new ArrayList<>();
        addresses.add(getAddressModel("900", "Kent", "St", "Sydney", "NSW", "2000", "5678", null));

        phones = new ArrayList<>();
        phones.add(getPhone("123456789", null, "2", "4568", AddressMedium.MOBILE_PHONE_PRIMARY, true));

        emails = new ArrayList<>();
        emails.add(getEmail("2", "1456", "abc@gmail.com", AddressMedium.EMAIL_PRIMARY, false));
    }

    @Test
    public void testToAddressDto() {
        AddressImpl addressModel = (AddressImpl) getAddressModel("275", "Kent", "Amble", "Sydney", "NSW", "2000", "1234", null);
        AddressDto addressDto = new AddressDto();
        ClientDetailDtoConverter.toAddressDto(addressModel, addressDto, serviceErrors);
        assertNotNull(addressDto);
        assertEquals(addressDto.getStreetName(), "Kent");
        assertEquals(addressDto.getStreetNumber(), addressModel.getStreetNumber());
        assertEquals(addressDto.getStreetType(), addressModel.getStreetType());
        assertEquals(addressDto.getSuburb(), "Sydney");
        assertEquals(addressDto.getState(), addressModel.getState());
        assertEquals(addressDto.getPostcode(), addressModel.getPostCode());

    }

    @Test
    public void testToAddressDto_WhenNoStateIsSet() {
        AddressImpl addressModel = new AddressImpl();
        addressModel.setStateOther("Dummy State");
        addressModel.setAddressKey(AddressKey.valueOf("1234"));
        AddressDto addressDto = new AddressDto();
        ClientDetailDtoConverter.toAddressDto(addressModel, addressDto, serviceErrors);
        assertNotNull(addressDto);
        assertEquals(addressDto.getState(), addressModel.getStateOther());
    }

    @Test
    public void testToPhoneDto() {
        PhoneImpl phoneModel = (PhoneImpl) getPhone("12568975", "1256", "1", "5678", AddressMedium.MOBILE_PHONE_PRIMARY, true);
        PhoneDto phoneDto = new PhoneDto();
        ClientDetailDtoConverter.toPhoneDto(phoneModel, phoneDto, serviceErrors);
        assertNotNull(phoneDto);
        assertEquals(phoneDto.getNumber(), phoneModel.getNumber());
        assertEquals(phoneDto.getCountryCode(), phoneModel.getCountryCode());
        assertEquals(phoneDto.isPreferred(), phoneModel.isPreferred());
    }

    @Test
    public void testToEmailDto() {
        EmailImpl emailModel = (EmailImpl) getEmail("4534", "12", "abc@gmail.com", AddressMedium.EMAIL_PRIMARY, false);
        EmailDto emailDto = new EmailDto();
        ClientDetailDtoConverter.toEmailDto(emailModel, emailDto, serviceErrors);
        assertNotNull(emailDto);
        assertEquals(emailDto.getEmail(), emailModel.getEmail());
    }

    @Test
    public void testToInvestorDto() {
        InvestorDetailImpl investorModel = new InvestorDetailImpl();
        investorModel.setInvestorType(InvestorType.TRUST);
        investorModel.setPersonAssociation(InvestorRole.Signatory);
        investorModel.setTfnProvided(true);
        investorModel.setCisId("12312312312");
        investorModel.setExemptionReason(ExemptionReason.SOCIAL_BENEFICIARY);
        investorModel.setResiCountryCodeForTax("2061");
        investorModel.setResiCountryForTax("Australia");
        InvestorDto investorDto = new InvestorDto();
        ClientDetailDtoConverter.toInvestorDto(investorModel, investorDto, serviceErrors);
        assertNotNull(investorDto);
        assertEquals(investorDto.isTfnProvided(), investorModel.getTfnProvided());
        assertEquals(investorDto.getInvestorType(), investorModel.getInvestorType().name());
        assertEquals(investorDto.getExemptionReason(), investorModel.getExemptionReason().getValue());
        assertEquals(investorDto.getCisId(), investorModel.getCISKey().getId());
        assertEquals(investorDto.getResiCountryCodeForTax(), investorModel.getResiCountryCodeForTax());
        assertEquals(investorDto.getResiCountryforTax(), investorModel.getResiCountryForTax());

    }

    @Test
    public void testToSuperPensionInvestorDto() {
        InvestorDetailImpl investorModel = new InvestorDetailImpl();
        investorModel.setInvestorType(InvestorType.INDIVIDUAL);
        investorModel.setTfnProvided(false);
        investorModel.setCisId("12312312312");
        investorModel.setExemptionReason(ExemptionReason.NO_EXEMPTION);
        investorModel.setPensionexemptionReason(PensionExemptionReason.PENSIONER);
        InvestorDto investorDto = new InvestorDto();
        ClientDetailDtoConverter.toInvestorDto(investorModel, investorDto, serviceErrors);
        assertNotNull(investorDto);
        assertEquals(investorDto.isTfnProvided(), investorModel.getTfnProvided());
        assertEquals(investorDto.getInvestorType(), investorModel.getInvestorType().name());
        assertEquals(investorDto.getExemptionReason(), investorModel.getExemptionReason().getValue());
        assertEquals(investorDto.getPensionExemptionReason(), investorModel.getPensionExemptionReason());
        assertEquals(investorDto.getCisId(), investorModel.getCISKey().getId());

    }

    @Test
    public void testToRegisteredEntityDto() {
        RegisteredEntityImpl registeredEntityModel = new RegisteredEntityImpl();
        registeredEntityModel.setAbn("123456");
        registeredEntityModel.setRegistrationState("NSW");
        RegisteredEntityDto registeredEntityDto = new RegisteredEntityDto();
        ClientDetailDtoConverter.toRegisteredEntityDto(registeredEntityModel, registeredEntityDto);
        assertNotNull(registeredEntityDto);
        assertEquals(registeredEntityDto.getRegistrationState(), registeredEntityModel.getRegistrationState());
        assertEquals(registeredEntityDto.getAbn(), registeredEntityModel.getAbn());
    }

    @Test
    public void testToIndividualDto() {
        IndividualDetailImpl individualModel = new IndividualDetailImpl();
        individualModel.setAge(29);
        individualModel.setGender(Gender.MALE);
        individualModel.setFirstName("DAN");
        individualModel.setLastName("LAST");
        individualModel.setMiddleName("MIDDLE");
        individualModel.setFullName("DAN LAST");
        individualModel.setTitle("Mr");
        individualModel.setIdVerificationStatus(IdentityVerificationStatus.Completed);
        IndividualDto individualDto = new IndividualDto();
        ClientDetailDtoConverter.toIndividualDto(individualModel, individualDto, serviceErrors);
        assertNotNull(individualDto);

        String gender = String.valueOf(individualModel.getGender()).toLowerCase();
        assertEquals(individualDto.getGender(), (Character.toUpperCase(gender.charAt(0)) + gender.substring(1)));
        assertEquals(individualDto.getFirstName(), "Dan");
        assertEquals(individualDto.getMiddleName(), "Middle");
        assertEquals(individualDto.getLastName(), "Last");
        assertEquals(individualDto.getAge(), individualModel.getAge());
        assertNotEquals(individualDto.getFullName(), individualModel.getFullName());
        assertEquals(individualDto.getFullName(), "Dan Middle Last");

    }

    @Test
    public void testToIndividualDto_McDonald() {
        IndividualDetailImpl individualModel = new IndividualDetailImpl();
        individualModel.setAge(29);
        individualModel.setGender(Gender.FEMALE);
        individualModel.setFirstName("Michelle");
        individualModel.setLastName("McDonald");
        individualModel.setMiddleName("Ann");
        individualModel.setFullName("Michelle Ann McDonald");
        individualModel.setTitle("Ms");
        individualModel.setIdVerificationStatus(IdentityVerificationStatus.Completed);
        IndividualDto individualDto = new IndividualDto();
        ClientDetailDtoConverter.toIndividualDto(individualModel, individualDto, serviceErrors);
        assertNotNull(individualDto);

        String gender = String.valueOf(individualModel.getGender()).toLowerCase();
        assertEquals(individualDto.getGender(), (Character.toUpperCase(gender.charAt(0)) + gender.substring(1)));
        assertEquals(individualDto.getFirstName(), individualModel.getFirstName());
        assertEquals(individualDto.getAge(), individualModel.getAge());
        assertEquals(individualDto.getFullName(), individualModel.getFullName());
        assertEquals(individualDto.getFullName(), "Michelle Ann McDonald");

    }

    @Test
    public void testFullNameForCompany() {
        CompanyImpl companyModel = new CompanyImpl();
        companyModel.setAcn("123456789");
        companyModel.setResiCountryCodeForTax("Australia");
        companyModel.setFullName("John Smith");
        companyModel.setInvestorType(InvestorType.TRUST);
        companyModel.setPersonAssociation(InvestorRole.Signatory);
        companyModel.setTfnProvided(true);
        companyModel.setCisId("12312312312");
        companyModel.setExemptionReason(ExemptionReason.SOCIAL_BENEFICIARY);
        List<InvestorDetail> linkedClients = new ArrayList<InvestorDetail>();
        IndividualDetailImpl investorModel = new IndividualDetailImpl();
        investorModel.setPersonAssociation(InvestorRole.Secretary);
        investorModel.setClientKey(ClientKey.valueOf("36689"));
        investorModel.setTitle("Mr");
        investorModel.setIdVerificationStatus(IdentityVerificationStatus.Completed);

        investorModel.setAddresses(addresses);
        investorModel.setPhones(phones);
        investorModel.setEmails(emails);

        linkedClients.add(investorModel);
        companyModel.setLinkedClients(linkedClients);
        companyModel.setAbn("123456");
        companyModel.setRegistrationState("NSW");
        companyModel.setClientKey(ClientKey.valueOf("12345678"));
        ClientDto clientDto = ClientDetailDtoConverter.toClientDto(companyModel, null, staticIntegrationService,null);
        assertNotNull(clientDto.getFullName());
    }


    @Test
    public void testToCompanyDto() {
        CompanyImpl companyModel = new CompanyImpl();
        companyModel.setAcn("123456789");
        companyModel.setResiCountryCodeForTax("Australia");
        List<InvestorDetail> linkedClients = new ArrayList<InvestorDetail>();
        IndividualDetailImpl investorModel = new IndividualDetailImpl();
        investorModel.setPersonAssociation(InvestorRole.Secretary);
        investorModel.setClientKey(ClientKey.valueOf("36689"));
        investorModel.setTitle("Mr");
        investorModel.setIdVerificationStatus(IdentityVerificationStatus.Completed);

        investorModel.setAddresses(addresses);
        investorModel.setPhones(phones);
        investorModel.setEmails(emails);

        linkedClients.add(investorModel);
        companyModel.setLinkedClients(linkedClients);
        CompanyDto companyDto = new CompanyDto();
        ClientDetailDtoConverter.toCompanyDto(companyModel, companyDto, staticIntegrationService, serviceErrors);
        assertNotNull(companyDto);
        assertNotNull(companyDto.getLinkedClients());
        assertEquals(companyDto.getAcn(), companyModel.getAcn());
        assertEquals(companyDto.getResiCountryforTax(), companyModel.getResiCountryForTax());
    }

    @Test
    public void testToSMSFDto() {
        SmsfImpl smsfModel = new SmsfImpl();
        List<InvestorDetail> linkedClients = new ArrayList<InvestorDetail>();
        IndividualDetailImpl investorModel1 = createIndividualDetail(InvestorRole.Secretary, "36689", "Mr", "LastA", IdentityVerificationStatus.Completed);
        IndividualDetailImpl investorModel2 = createIndividualDetail(InvestorRole.Secretary, "36690", "Mr", "LastB", IdentityVerificationStatus.Completed);

        linkedClients.add(investorModel1);
        linkedClients.add(investorModel2);

        smsfModel.setLinkedClients(linkedClients);
        smsfModel.setIdVerificationStatus(IdentityVerificationStatus.Completed);
        smsfModel.setTrustees(Arrays.asList(getTrustee()));
        SmsfDto smsfDto = new SmsfDto();
        smsfDto.setCompany(new CompanyDto());
        List<AssociatedPerson> associatedPersons = new ArrayList<AssociatedPerson>();

        AssociatedPerson associatedPerson1 = new AssociatedPersonBuilder().withClientKey("36689")
                .withTFNEntered(true).withFormerNames("name11", "name12", "name13").withBirthCountry("Australia")
                .withBirthStateDomestic("NSW").withBirthSuburb("Bateman's Bay").create();
        associatedPersons.add(associatedPerson1);

        AssociatedPerson associatedPerson2 = new AssociatedPersonBuilder().withClientKey("36690").withTFNExemptionId("2").create();

        // no formerName, placeofbirth
        associatedPersons.add(associatedPerson2);
        ClientDetailDtoConverter.toSmsfDto(smsfModel, smsfDto, associatedPersons, staticIntegrationService,serviceErrors);
        assertNotNull(smsfDto);
        assertNotNull(smsfDto.getLinkedClients());
        assertEquals(IdentityVerificationStatus.Completed.toString(), smsfDto.getIdvs());
        assertTrue(smsfDto.getLinkedClients().get(0).isTfnProvided()); // has been overwritten
        assertTrue(smsfDto.getLinkedClients().get(0).isTfnEntered());
        assertFalse(smsfDto.getLinkedClients().get(1).isTfnProvided());
        assertFalse(smsfDto.getLinkedClients().get(1).isTfnEntered());
        assertThat(smsfDto.getLinkedClients().get(1).getTfnExemptId(), is("2"));
        //
        IndividualDto linkedIndividualClient1 = getLinkedIndividualClientAt(smsfDto, 0);
        assertEquals("name11", linkedIndividualClient1.getFormerName()); // pick the immediately prior formerName if there are many
        assertEquals("Australia", linkedIndividualClient1.getPlaceOfBirthCountry());
        assertEquals("NSW", linkedIndividualClient1.getPlaceOfBirthState());
        assertEquals("Bateman's Bay", linkedIndividualClient1.getPlaceOfBirthSuburb());
        //
        assertEquals(2, smsfDto.getLinkedClients().size());

        assertEquals(smsfDto.getCompany().getOccupierName(), "Lenny Leonard");
    }

    private ClientDetail getTrustee() {
        CompanyImpl trustee = new CompanyImpl();
        trustee.setClientKey(ClientKey.valueOf("123"));
        trustee.setAddresses((Arrays.asList(getAddressModel("900", "Kent", "St", "Sydney", "NSW", "2000", "5678", null),
                getAddressModel("900", "Kent", "St", "Sydney", "NSW", "2000", "5678", "Lenny Leonard"))));
        return trustee;
    }

    private IndividualDetailImpl createIndividualDetail(InvestorRole role, String key, String title, String lastName, IdentityVerificationStatus idvStatus) {
        IndividualDetailImpl investorModel = new IndividualDetailImpl();
        investorModel.setPersonAssociation(role);
        investorModel.setClientKey(ClientKey.valueOf(key));
        investorModel.setTitle(title);
        investorModel.setLastName(lastName);
        investorModel.setIdVerificationStatus(idvStatus);

        investorModel.setAddresses(addresses);
        investorModel.setPhones(phones);
        investorModel.setEmails(emails);
        investorModel.setTfnProvided(false);
        return investorModel;
    }

    private IndividualDto getLinkedIndividualClientAt(final SmsfDto smsfDto, final int position) {
        return (IndividualDto) smsfDto.getLinkedClients().get(position);
    }

    @Test
    public void testToTrustDto() {
        TrustImpl trustModel = new TrustImpl();
        List<InvestorDetail> linkedClients = new ArrayList<InvestorDetail>();
        IndividualDetailImpl investorModel = new IndividualDetailImpl();
        investorModel.setPersonAssociation(InvestorRole.Secretary);
        investorModel.setClientKey(ClientKey.valueOf("36689"));
        investorModel.setTitle("Mr");
        investorModel.setIdVerificationStatus(IdentityVerificationStatus.Completed);
        investorModel.setAddresses(addresses);
        investorModel.setPhones(phones);
        investorModel.setEmails(emails);

        linkedClients.add(investorModel);
        trustModel.setLinkedClients(linkedClients);
        List<InvestorDetail> beneficiaries = new ArrayList<InvestorDetail>();
        IndividualDetailImpl invModel = new IndividualDetailImpl();
        invModel.setPersonAssociation(InvestorRole.Beneficiary);
        invModel.setTitle("Mr");
        invModel.setIdVerificationStatus(IdentityVerificationStatus.Completed);
        invModel.setClientKey(ClientKey.valueOf("36689"));
        invModel.setDateOfBirth(DateTime.parse("1982-11-02"));
        invModel.setGender(Gender.MALE);
        invModel.setAddresses(addresses);
        invModel.setPhones(phones);
        invModel.setEmails(emails);
        List<InvestorRole> personRoles = asList(InvestorRole.Beneficiary);
        invModel.setPersonRoles(personRoles);

        beneficiaries.add(invModel);
        trustModel.setBeneficiaries(beneficiaries);
        List<ClientDetail> trusteeList = new ArrayList<ClientDetail>();
        CompanyImpl company = new CompanyImpl();
        company.setExemptionReason(ExemptionReason.NON_RESIDENT);
        company.setInvestorType(InvestorType.TRUST);
        company.setClientKey(ClientKey.valueOf("36689"));
        List<Address> addressList = new ArrayList<>();
        AddressImpl address = new AddressImpl();
        address.setAddressKey(AddressKey.valueOf("1234"));
        addressList.add(address);
        company.setAddresses(addressList);
        trusteeList.add(company);
        trustModel.setTrustees(trusteeList);
        trustModel.setTrustType(TrustType.REGI_MIS);
        trustModel.setIdVerificationStatus(IdentityVerificationStatus.Restricted);
        trustModel.setAsicName("some business name");
        TrustDto trustDto = new TrustDto();
        ClientDetailDtoConverter.toTrustDto(trustModel, trustDto, staticIntegrationService, serviceErrors);
        assertNotNull(trustDto);
        assertNotNull(trustDto.getLinkedClients());
        assertEquals(trustDto.getBusinessName(), "Some Business Name");
        assertNotNull(trustDto.getBeneficiaries());
        assertNotNull(trustDto.getBeneficiaries().get(0).getAddresses());
        assertEquals(trustDto.getBeneficiaries().get(0).getAddresses().get(0).getStreetName(), "Kent");
        assertEquals(trustDto.getBeneficiaries().get(0).getPersonRoles(), personRoles);
        assertThat(trustDto.getBeneficiaries().get(0).getDateOfBirth(), is("02 Nov 1982"));
        assertThat(trustDto.getBeneficiaries().get(0).getGender(), is("Male"));
        assertThat(trustDto.getBeneficiaries().get(0).getIdvs(), is("compl"));
        assertEquals(trustDto.getIdvs(), IdentityVerificationStatus.Restricted.toString());

    }

    @Test
    public void testToTrustDto_WhenTheTrustTypeIsOtherAndTrustTypeDescIsOther() {
        TrustImpl trustModel = new TrustImpl();
        trustModel.setTrustType(TrustType.OTHER);
        trustModel.setTrustTypeDesc(TrustTypeDesc.BTFG$OTH);
        trustModel.setBusinessClassificationDesc("USER ENTERED");
        TrustDto trustDto = new TrustDto();
        ClientDetailDtoConverter.toTrustDto(trustModel, trustDto, staticIntegrationService,serviceErrors);

        assertThat(trustDto.getTrustType(), is(TrustType.OTHER.getTrustTypeValue()));
        assertThat(trustDto.getTrustTypeDesc(), is(TrustTypeDesc.BTFG$OTH.getTrustTypeDescValue()));
        assertThat(trustDto.getBusinessClassificationDesc(), is(TrustTypeDesc.BTFG$OTH.getTrustTypeDescValue()
                + " - " + trustModel.getBusinessClassificationDesc()));
    }

    @Test
    public void testToTrustDto_WhenTheTrustTypeIsOtherAndTrustTypeDescIsNotOther() {
        TrustImpl trustModel = new TrustImpl();
        trustModel.setTrustType(TrustType.OTHER);
        trustModel.setTrustTypeDesc(TrustTypeDesc.BTFG$TSTMTRY_TRUST);
        TrustDto trustDto = new TrustDto();
        ClientDetailDtoConverter.toTrustDto(trustModel, trustDto, staticIntegrationService,serviceErrors);

        assertThat(trustDto.getTrustType(), is(TrustType.OTHER.getTrustTypeValue()));
        assertThat(trustDto.getTrustTypeDesc(), is(TrustTypeDesc.BTFG$TSTMTRY_TRUST.getTrustTypeDescValue()));
        assertThat(trustDto.getBusinessClassificationDesc(), is(TrustTypeDesc.BTFG$TSTMTRY_TRUST.getTrustTypeDescValue()));
    }

    @Test
    public void testToTrustDto_withShareholder() {
        TrustImpl trustModel = new TrustImpl();
        List<InvestorDetail> linkedClients = new ArrayList<InvestorDetail>();
        IndividualDetailImpl investorModel = new IndividualDetailImpl();
        investorModel.setPersonAssociation(InvestorRole.Secretary);
        investorModel.setClientKey(ClientKey.valueOf("36689"));
        investorModel.setTitle("Mr");
        investorModel.setIdVerificationStatus(IdentityVerificationStatus.Completed);

        investorModel.setAddresses(addresses);
        investorModel.setPhones(phones);
        investorModel.setEmails(emails);

        linkedClients.add(investorModel);
        trustModel.setLinkedClients(linkedClients);
        List<InvestorDetail> beneficiaries = new ArrayList<InvestorDetail>();
        IndividualDetailImpl invModel = new IndividualDetailImpl();
        invModel.setPersonAssociation(InvestorRole.Beneficiary);
        invModel.setTitle("Mr");
        invModel.setIdVerificationStatus(IdentityVerificationStatus.Completed);
        invModel.setClientKey(ClientKey.valueOf("36689"));
        invModel.setDateOfBirth(DateTime.parse("1982-11-02"));
        invModel.setGender(Gender.MALE);
        invModel.setAddresses(addresses);
        invModel.setPhones(phones);
        invModel.setEmails(emails);
        List<InvestorRole> personRoles = new ArrayList<>();
        personRoles.add(InvestorRole.Shareholder);
        personRoles.add(InvestorRole.BeneficialOwner);
        invModel.setPersonRoles(personRoles);

        beneficiaries.add(invModel);
        trustModel.setBeneficiaries(beneficiaries);
        List<ClientDetail> trusteeList = new ArrayList<ClientDetail>();
        CompanyImpl company = new CompanyImpl();
        company.setExemptionReason(ExemptionReason.NON_RESIDENT);
        company.setInvestorType(InvestorType.TRUST);
        company.setClientKey(ClientKey.valueOf("36689"));
        List<Address> addressList = new ArrayList<>();
        AddressImpl address = new AddressImpl();
        address.setAddressKey(AddressKey.valueOf("1234"));
        addressList.add(address);
        company.setAddresses(addressList);
        trusteeList.add(company);
        trustModel.setTrustees(trusteeList);
        trustModel.setTrustType(TrustType.REGI_MIS);
        trustModel.setIdVerificationStatus(IdentityVerificationStatus.Restricted);
        trustModel.setAsicName("some business name");
        TrustDto trustDto = new TrustDto();
        ClientDetailDtoConverter.toTrustDto(trustModel, trustDto,staticIntegrationService, serviceErrors);
        List<InvestorRole> returnedRoles = trustDto.getBeneficiaries().get(0).getPersonRoles();
        assertThat(returnedRoles.size(), is(1));
        assertThat(returnedRoles.get(0), is(InvestorRole.Shareholder));
    }

    @Test
    public void testGetCorporateCompanyDetails() {
        CompanyImpl company = new CompanyImpl();
        CompanyDto companyDto = new CompanyDto();
        company.setExemptionReason(ExemptionReason.NON_RESIDENT);
        company.setInvestorType(InvestorType.TRUST);
        company.setClientKey(ClientKey.valueOf("36689"));
        company.setIdVerificationStatus(IdentityVerificationStatus.Completed);
        company.setFullName("TestCompany");
        company.setAsicName("TestCompanyASIC");
        List<Address> addressList = new ArrayList<>();
        AddressImpl address = new AddressImpl();
        address.setAddressKey(AddressKey.valueOf("1234"));
        address.setBuilding("Kensington");
        addressList.add(address);
        company.setAddresses(addressList);
        ClientDetailDtoConverter.getCorporateCompanyDetails(company, companyDto,staticIntegrationService, serviceErrors);
        assertNotNull(companyDto);
        assertEquals(companyDto.getAddresses().get(0).getBuilding(), address.getBuilding());
        assertEquals(companyDto.getIdvs(), "compl");
        assertEquals(companyDto.getFullName(), "Testcompanyasic");
    }

    @Test
    public void testSetInvestorLinkedClient() {
        IndividualDetailImpl linkedClientModel1 = new IndividualDetailImpl();
        linkedClientModel1.setPersonAssociation(InvestorRole.Secretary);
        linkedClientModel1.setClientKey(ClientKey.valueOf("33698"));
        linkedClientModel1.setTitle("Mr");
        linkedClientModel1.setIdVerificationStatus(IdentityVerificationStatus.Completed);

        linkedClientModel1.setAddresses(addresses);
        linkedClientModel1.setPhones(phones);
        linkedClientModel1.setEmails(emails);

        IndividualDetailImpl linkedClientModel2 = new IndividualDetailImpl();
        linkedClientModel2.setPersonAssociation(InvestorRole.Signatory);
        linkedClientModel2.setClientKey(ClientKey.valueOf("33698"));
        linkedClientModel2.setTitle("Mr");
        linkedClientModel2.setIdVerificationStatus(IdentityVerificationStatus.Completed);

        linkedClientModel2.setAddresses(addresses);
        linkedClientModel2.setPhones(phones);
        linkedClientModel2.setEmails(emails);

        List<InvestorDetail> linkedClientModelList = new ArrayList<InvestorDetail>();
        linkedClientModelList.add(linkedClientModel1);
        linkedClientModelList.add(linkedClientModel2);
        Map<ClientKey, IndividualDto> linkedClientDtoMap = new HashMap<ClientKey, IndividualDto>();

        for (InvestorDetail linkedClientModel : linkedClientModelList) {
            ClientDetailDtoConverter.setInvestorLinkedClient((IndividualDetail) linkedClientModel, linkedClientDtoMap,
                    serviceErrors);
        }
        assertNotNull(linkedClientDtoMap);
        assertEquals(linkedClientDtoMap.size(), 1);
    }

    @Test
    public void testGetBeneficiaries() {
        List<InvestorDetail> investorDetailList = new ArrayList<>();

        IndividualDetailImpl investor1 = new IndividualDetailImpl();
        IndividualDetailImpl investor2 = new IndividualDetailImpl();
        List<Address> addressList1 = new ArrayList<>();
        AddressImpl address1 = new AddressImpl();
        address1.setAddressKey(AddressKey.valueOf("1234"));
        address1.setBuilding("Kensington");
        addressList1.add(address1);
        investor1.setClientKey(ClientKey.valueOf("12345"));
        investor1.setAddresses(addressList1);
        investor1.setLastName("Twist");
        investor1.setTitle("Mr");
        investor1.setDateOfBirth(DateTime.parse("1982-11-02"));
        investor1.setGender(Gender.MALE);
        investor1.setIdVerificationStatus(IdentityVerificationStatus.Completed);

        List<Address> addressList2 = new ArrayList<>();
        AddressImpl address2 = new AddressImpl();
        address2.setAddressKey(AddressKey.valueOf("5678"));
        address2.setBuilding("Ventura");
        addressList2.add(address2);
        investor2.setClientKey(ClientKey.valueOf("123456"));
        investor2.setAddresses(addressList2);
        investor2.setLastName("Dais");
        investor2.setTitle("Mr");
        investor2.setDateOfBirth(DateTime.parse("1982-11-02"));
        investor2.setGender(Gender.MALE);
        investor2.setIdVerificationStatus(IdentityVerificationStatus.Restricted);

        investorDetailList.add(investor1);
        investorDetailList.add(investor2);
        List<IndividualDto> beneficiaryList = ClientDetailDtoConverter.getBeneficiaries(investorDetailList, serviceErrors);
        assertNotNull(beneficiaryList);
        assertEquals(beneficiaryList.get(0).getLastName(), investor2.getLastName());
        assertEquals(beneficiaryList.get(1).getLastName(), investor1.getLastName());
        assertThat(investor1.getIdentityVerificationStatus().getId(), is("compl"));
        assertThat(investor2.getIdentityVerificationStatus().getId(), is("restr"));
    }

    @Test
    public void testSetPhoneListForUpdate() {
        PhoneDto phoneDto1 = new PhoneDto();
        PhoneDto phoneDto2 = new PhoneDto();

        phoneDto1.setPreferred(true);
        phoneDto1.setCountryCode("1256");
        phoneDto1.setNumber("12568975");
        phoneDto1.setPhoneType("Primary");

        phoneDto2.setPreferred(false);
        phoneDto2.setCountryCode("2004");
        phoneDto2.setNumber("659878965");
        phoneDto2.setPhoneType("Home");

        List<PhoneDto> phoneDtoList = asList(phoneDto1, phoneDto2);
        List<Phone> phoneModelList = ClientDetailDtoConverter.setPhoneListForUpdate(phoneDtoList);
        assertNotNull(phoneModelList);
        assertEquals(phoneModelList.size(), phoneDtoList.size());
        assertEquals(phoneModelList.get(0).getNumber(), phoneDtoList.get(0).getNumber());
        assertEquals(phoneModelList.get(1).isPreferred(), phoneDtoList.get(1).isPreferred());
    }

    @Test
    public void testSetEmailListForUpdate() {
        EmailDto emailDto1 = new EmailDto();
        EmailDto emailDto2 = new EmailDto();
        emailDto1.setEmail("abc@gmail.com");
        emailDto2.setEmail("greg@yahoo.com");
        List<EmailDto> emailDtoList = asList(emailDto1, emailDto2);
        List<com.bt.nextgen.service.integration.domain.Email> emailModelList = ClientDetailDtoConverter
                .setEmailListForUpdate(emailDtoList);
        assertNotNull(emailModelList);
        assertEquals(emailModelList.size(), emailDtoList.size());
        assertEquals(emailModelList.get(0).getEmail(), emailDtoList.get(0).getEmail());
        assertEquals(emailModelList.get(1).getEmail(), emailDtoList.get(1).getEmail());
    }

    @Test
    public void testSetAddressDtoList() {

        ClientDetail client = new IndividualDetailImpl();
        List<Address> addresses = new ArrayList<Address>();
        AddressImpl addressModel = new AddressImpl();
        addressModel.setModificationSeq("1");
        addressModel.setAddressKey(AddressKey.valueOf("1234"));
        addresses.add(addressModel);

        ((IndividualDetailImpl) client).setAddresses(addresses);
        List<AddressDto> addressDtos = ClientDetailDtoConverter.getAddressDtoList(client, serviceErrors);
        assertNotNull(addressDtos);
    }

    @Test
    public void testPhoneDeDuplication_test1() {
        /*
         * When we have duplicate phone numbers in primary, secondary, preferred
         * and business telephone
         */
        ClientDetail client = new IndividualDetailImpl();
        List<Phone> phones = new ArrayList<>();
        // Primary, Preferred, Secondary Phone, Business_telephone
        phones.add(getPhone("123456789", null, "2", "4568", AddressMedium.BUSINESS_TELEPHONE, true));
        phones.add(getPhone("123456789", null, "3", "4569", AddressMedium.MOBILE_PHONE_PRIMARY, false));
        phones.add(getPhone("123456789", null, "4", "4567", AddressMedium.MOBILE_PHONE_SECONDARY, false));
        phones.add(getPhone("123456789", null, "2", "4566", AddressMedium.BUSINESS_TELEPHONE, false));

        ((IndividualDetailImpl) client).setPhones(phones);
        List<PhoneDto> phoneDtos = ClientDetailDtoConverter.getPhoneDtoList(client, serviceErrors);

        assertNotNull(phoneDtos);
        assertEquals(phoneDtos.size(), 2);
    }

    @Test
    public void testPhoneDeDuplication_test2() {

        ClientDetail client = new IndividualDetailImpl();
        List<Phone> phones = new ArrayList<>();


        // Primary, Preferred, Secondary Phone, Business_telephone
        phones.add(getPhone("123234356", null, "2", "4568", AddressMedium.BUSINESS_TELEPHONE, true));
        phones.add(getPhone("123456789", null, "3", "4569", AddressMedium.MOBILE_PHONE_PRIMARY, false));

        ((IndividualDetailImpl) client).setPhones(phones);

        List<PhoneDto> phoneDtos = ClientDetailDtoConverter.getPhoneDtoList(client, serviceErrors);

        assertNotNull(phoneDtos);
        assertEquals(phoneDtos.size(), 2);
    }

    @Test
    public void testPrimaryDuplication() {

        ClientDetail client = new IndividualDetailImpl();
        List<Phone> phones = new ArrayList<>();

        // Primary, Preferred, Secondary Phone, Business_telephone
        PhoneImpl phoneModel = new PhoneImpl();
        phoneModel.setNumber("61419123456");
        phoneModel.setModificationSeq("2");
        phoneModel.setPhoneKey(AddressKey.valueOf("4568"));
        phoneModel.setType(AddressMedium.MOBILE_PHONE_PRIMARY);
        phones.add(phoneModel);

        PhoneImpl secPhoneModel = new PhoneImpl();
        secPhoneModel.setNumber("0469009001");
        secPhoneModel.setModificationSeq("3");
        secPhoneModel.setPhoneKey(AddressKey.valueOf("4569"));
        secPhoneModel.setType(AddressMedium.MOBILE_PHONE_SECONDARY);
        phones.add(secPhoneModel);

        PhoneImpl phoneModelDupl = new PhoneImpl();
        phoneModelDupl.setNumber("61419123456");
        phoneModelDupl.setModificationSeq("2");
        phoneModelDupl.setPhoneKey(AddressKey.valueOf("4568"));
        phoneModelDupl.setType(AddressMedium.MOBILE_PHONE_SECONDARY);
        phones.add(phoneModelDupl);

        PhoneImpl otherPhoneModel = new PhoneImpl();
        otherPhoneModel.setNumber("0282534119");
        otherPhoneModel.setModificationSeq("4");
        otherPhoneModel.setPhoneKey(AddressKey.valueOf("4570"));
        otherPhoneModel.setType(AddressMedium.OTHER);
        phones.add(otherPhoneModel);

        PhoneImpl workPhoneModel = new PhoneImpl();
        workPhoneModel.setNumber("0282534119");
        workPhoneModel.setModificationSeq("5");
        workPhoneModel.setPhoneKey(AddressKey.valueOf("4571"));
        workPhoneModel.setType(AddressMedium.BUSINESS_TELEPHONE);
        phones.add(workPhoneModel);

        phones.add(getPhone("0282534119", null, "6", "4572", AddressMedium.PERSONAL_TELEPHONE, true));

        ((IndividualDetailImpl) client).setPhones(phones);

        List<PhoneDto> phoneDtos = ClientDetailDtoConverter.getPhoneDtoList(client, serviceErrors);

        assertNotNull(phoneDtos);
        assertEquals(phoneDtos.size(), 5);
    }

    @Test
    public void testPrimaryDuplicationIntentionally() {

        ClientDetail client = new IndividualDetailImpl();
        List<Phone> phones = new ArrayList<Phone>();


        // Primary, Preferred, Secondary Phone, Business_telephone
        PhoneImpl phoneModel = new PhoneImpl();
        phoneModel.setNumber("61419123456");
        phoneModel.setModificationSeq("2");
        phoneModel.setPhoneKey(AddressKey.valueOf("4568"));
        phoneModel.setType(AddressMedium.MOBILE_PHONE_PRIMARY);
        phones.add(phoneModel);

        PhoneImpl secPhoneModel = new PhoneImpl();
        secPhoneModel.setNumber("0469009001");
        secPhoneModel.setModificationSeq("3");
        secPhoneModel.setPhoneKey(AddressKey.valueOf("4569"));
        secPhoneModel.setType(AddressMedium.MOBILE_PHONE_SECONDARY);
        phones.add(secPhoneModel);

        PhoneImpl phoneModelDupl = new PhoneImpl();
        phoneModelDupl.setNumber("61419123456");
        phoneModelDupl.setModificationSeq("2");
        phoneModelDupl.setPhoneKey(AddressKey.valueOf("4768"));
        phoneModelDupl.setType(AddressMedium.MOBILE_PHONE_SECONDARY);
        phones.add(phoneModelDupl);

        PhoneImpl phoneModelDuplIntentionally = new PhoneImpl();
        phoneModelDuplIntentionally.setNumber("61419123456");
        phoneModelDuplIntentionally.setModificationSeq("2");
        phoneModelDuplIntentionally.setPhoneKey(AddressKey.valueOf("4568"));
        phoneModelDuplIntentionally.setType(AddressMedium.MOBILE_PHONE_SECONDARY);
        phones.add(phoneModelDuplIntentionally);

        PhoneImpl otherPhoneModel = new PhoneImpl();
        otherPhoneModel.setNumber("0282534119");
        otherPhoneModel.setModificationSeq("4");
        otherPhoneModel.setPhoneKey(AddressKey.valueOf("4570"));
        otherPhoneModel.setType(AddressMedium.OTHER);
        phones.add(otherPhoneModel);

        PhoneImpl workPhoneModel = new PhoneImpl();
        workPhoneModel.setNumber("0282534119");
        workPhoneModel.setModificationSeq("5");
        workPhoneModel.setPhoneKey(AddressKey.valueOf("4571"));
        workPhoneModel.setType(AddressMedium.BUSINESS_TELEPHONE);
        phones.add(workPhoneModel);

        phones.add(getPhone("0282534119", null, "6", "4572", AddressMedium.PERSONAL_TELEPHONE, true));

        ((IndividualDetailImpl) client).setPhones(phones);

        List<PhoneDto> phoneDtos = ClientDetailDtoConverter.getPhoneDtoList(client, serviceErrors);

        assertNotNull(phoneDtos);
        assertEquals(phoneDtos.size(), 5);
    }

    @Test
    public void testPreferredDuplication() {

        ClientDetail client = new IndividualDetailImpl();

        List<Phone> phones = new ArrayList<Phone>();

        // Primary, Preferred, Secondary Phone, Business_telephone
        PhoneImpl phoneModel = new PhoneImpl();
        phoneModel.setNumber("61419123456");
        phoneModel.setModificationSeq("2");
        phoneModel.setPhoneKey(AddressKey.valueOf("4568"));
        phoneModel.setType(AddressMedium.MOBILE_PHONE_PRIMARY);
        phones.add(phoneModel);

        PhoneImpl secPhoneModel = new PhoneImpl();
        secPhoneModel.setNumber("0469009001");
        secPhoneModel.setModificationSeq("3");
        secPhoneModel.setPhoneKey(AddressKey.valueOf("4569"));
        secPhoneModel.setType(AddressMedium.MOBILE_PHONE_SECONDARY);
        phones.add(secPhoneModel);

        PhoneImpl phoneModelDupl = new PhoneImpl();
        phoneModelDupl.setNumber("61419123456");
        phoneModelDupl.setModificationSeq("2");
        phoneModelDupl.setPhoneKey(AddressKey.valueOf("4568"));
        phoneModelDupl.setType(AddressMedium.MOBILE_PHONE_SECONDARY);
        phones.add(phoneModelDupl);

        PhoneImpl phoneModelDuplIntentionally = new PhoneImpl();
        phoneModelDuplIntentionally.setNumber("61419123456");
        phoneModelDuplIntentionally.setModificationSeq("2");
        phoneModelDuplIntentionally.setPhoneKey(AddressKey.valueOf("4568"));
        phoneModelDuplIntentionally.setType(AddressMedium.MOBILE_PHONE_SECONDARY);
        phones.add(phoneModelDuplIntentionally);

        PhoneImpl otherPhoneModel = new PhoneImpl();
        otherPhoneModel.setNumber("0282534119");
        otherPhoneModel.setModificationSeq("4");
        otherPhoneModel.setPhoneKey(AddressKey.valueOf("4570"));
        otherPhoneModel.setType(AddressMedium.OTHER);
        phones.add(otherPhoneModel);

        phones.add(getPhone("0282534119", null, "5", "4571", AddressMedium.BUSINESS_TELEPHONE, false));
        phones.add(getPhone("0282534119", null, "6", "4572", AddressMedium.PERSONAL_TELEPHONE, false));
        phones.add(getPhone("0282534119", null, "6", "4572", AddressMedium.PERSONAL_TELEPHONE, true));

        ((IndividualDetailImpl) client).setPhones(phones);

        List<PhoneDto> phoneDtos = ClientDetailDtoConverter.getPhoneDtoList(client, serviceErrors);

        assertNotNull(phoneDtos);
        assertEquals(phoneDtos.size(), 5);

    }

    @Test
    public void testEmailDeDuplication() {

        ClientDetail client = new IndividualDetailImpl();
        List<Email> emails = new ArrayList<>();

        // Primary, Preferred, Secondary Email
        emails.add(getEmail("abc@gmail.com", "2", "1456", AddressMedium.EMAIL_ADDRESS_SECONDARY, true));
        emails.add(getEmail("abc@gmail.com", "2", "2456", AddressMedium.EMAIL_ADDRESS_SECONDARY, false));
        emails.add(getEmail("abcd@gmail.com", "2", "2456", AddressMedium.EMAIL_PRIMARY, false));
        emails.add(getEmail("abcd@gmail.com", "2", "2456", AddressMedium.EMAIL_ADDRESS_SECONDARY, false));

        ((IndividualDetailImpl) client).setEmails(emails);

        List<EmailDto> emailDtos = ClientDetailDtoConverter.getEmailDtoList(client, serviceErrors);

        assertNotNull(emailDtos);
        assertEquals(emailDtos.size(), 2);

    }

    @Test
    public void testPhoneDeDuplication_test4() {
        ClientDetail client = new IndividualDetailImpl();
        List<Phone> phones = new ArrayList<Phone>();

        // Primary, Preferred, Secondary Phone, Business_telephone

        phones.add(getPhone("123234356", null, "2", "4528", AddressMedium.BUSINESS_TELEPHONE, true));
        phones.add(getPhone("123234356", null, "2", "4538", AddressMedium.BUSINESS_TELEPHONE, false));
        phones.add(getPhone("123234356", null, "2", "4338", AddressMedium.PERSONAL_TELEPHONE, false));
        phones.add(getPhone("123234356", null, "2", "4568", AddressMedium.OTHER, false));
        phones.add(getPhone("123234356", null, "2", "4569", AddressMedium.MOBILE_PHONE_PRIMARY, false));

        ((IndividualDetailImpl) client).setPhones(phones);

        List<PhoneDto> phoneDtos = ClientDetailDtoConverter.getPhoneDtoList(client, serviceErrors);

        assertNotNull(phoneDtos);
        assertEquals(phoneDtos.size(), 4);

    }

    @Test
    public void testPhoneDeDuplication() {
        ClientDetail client = new IndividualDetailImpl();
        List<Phone> phones = new ArrayList<>();


        phones.add(getPhone("0282500002", null, "3", "1", AddressMedium.MOBILE_PHONE_SECONDARY, false));
        phones.add(getPhone("0282500003", null, "3", "1", AddressMedium.MOBILE_PHONE_SECONDARY, false));
        phones.add(getPhone("0282500002", null, "2", "2", AddressMedium.PERSONAL_TELEPHONE, false));
        phones.add(getPhone("0282500002", null, "2", "5", AddressMedium.PERSONAL_TELEPHONE, false));
        phones.add(getPhone("0282500002", null, "2", "4", AddressMedium.BUSINESS_TELEPHONE, false));
        phones.add(getPhone("0282500002", null, "2", "3", AddressMedium.OTHER, false));
        phones.add(getPhone("0282500001", null, "2", "6", AddressMedium.OTHER, false));
        phones.add(getPhone("0282500003", null, "2", "3", AddressMedium.BUSINESS_TELEPHONE, false));
        phones.add(getPhone("0282500001", null, "2", "14", AddressMedium.BUSINESS_TELEPHONE, false));
        phones.add(getPhone("0282500001", null, "2", "13", AddressMedium.BUSINESS_TELEPHONE, false));
        phones.add(getPhone("0282500001", null, "2", "9", AddressMedium.MOBILE_PHONE_PRIMARY, false));
        phones.add(getPhone("0282500003", null, "2", "10", AddressMedium.PERSONAL_TELEPHONE, true));
        phones.add(getPhone("0282500003", null, "2", "13", AddressMedium.PERSONAL_TELEPHONE, false));
        phones.add(getPhone("0282500001", null, "2", "17", AddressMedium.OTHER, false));
        phones.add(getPhone("0282500003", null, "2", "17", AddressMedium.PERSONAL_TELEPHONE, false));
        phones.add(getPhone("0282500003", null, "2", "17", AddressMedium.OTHER, false));
        phones.add(getPhone("0282500001", null, "2", "17", AddressMedium.MOBILE_PHONE_SECONDARY, false));

        ((IndividualDetailImpl) client).setPhones(phones);
        List<PhoneDto> phoneDtos = ClientDetailDtoConverter.getPhoneDtoList(client, serviceErrors);

        assertNotNull(phoneDtos);
        assertEquals(phoneDtos.size(), 14);
    }

    @Test
    public void testSecondaryPhoneDeDuplication() {
        ClientDetail client = new IndividualDetailImpl();
        List<Phone> phones = new ArrayList<>();

        phones.add(getPhone("61401872264", null, "3", "1", AddressMedium.MOBILE_PHONE_SECONDARY, false));
        phones.add(getPhone("61401872264", null, "3", "5", AddressMedium.MOBILE_PHONE_SECONDARY, false));
        phones.add(getPhone("61419123456", null, "3", "1", AddressMedium.MOBILE_PHONE_PRIMARY, true));
        phones.add(getPhone("61398987446", null, "2", "3", AddressMedium.OTHER, false));
        phones.add(getPhone("61211111111", null, "2", "3", AddressMedium.OTHER, false));
        phones.add(getPhone("61256789333", null, "2", "5", AddressMedium.OTHER, false));

        ((IndividualDetailImpl) client).setPhones(phones);

        List<PhoneDto> phoneDtos = ClientDetailDtoConverter.getPhoneDtoList(client, serviceErrors);

        assertNotNull(phoneDtos);
        assertEquals(phoneDtos.size(), 6);
    }

    @Test
    public void testParseFullNumberToGCMNumberFormat() {
        List<PhoneDto> phoneDtos = new ArrayList<>();

        PhoneImpl secondaryPhoneModel = (PhoneImpl) getPhone("0282500000", null, "3", "1", AddressMedium.MOBILE_PHONE_SECONDARY, false);
        PhoneDto dto1 = new PhoneDto();
        phones.add(secondaryPhoneModel);
        ClientDetailDtoConverter.toPhoneDto(secondaryPhoneModel, dto1, serviceErrors);

        PhoneImpl homePhone1 = (PhoneImpl) getPhone("61282500000", null, "2", "2", AddressMedium.PERSONAL_TELEPHONE, false);
        PhoneDto dto2 = new PhoneDto();
        phones.add(homePhone1);
        ClientDetailDtoConverter.toPhoneDto(homePhone1, dto2, serviceErrors);

        PhoneImpl otherPhone1 = (PhoneImpl) getPhone("610282500000", null, "2", "3", AddressMedium.OTHER, false);
        PhoneDto dto3 = new PhoneDto();
        phones.add(otherPhone1);
        ClientDetailDtoConverter.toPhoneDto(otherPhone1, dto3, serviceErrors);

        PhoneImpl businessPhone1 = new PhoneImpl();
        PhoneDto dto4 = new PhoneDto();
        businessPhone1.setNumber("610401766762");
        businessPhone1.setModificationSeq("2");
        businessPhone1.setPhoneKey(AddressKey.valueOf("4"));
        businessPhone1.setType(AddressMedium.BUSINESS_TELEPHONE);
        phones.add(businessPhone1);
        ClientDetailDtoConverter.toPhoneDto(businessPhone1, dto4, serviceErrors);

        PhoneImpl businessPhone2 = (PhoneImpl) getPhone("0401872255", null, "2", "5", AddressMedium.MOBILE_PHONE_PRIMARY, true);
        PhoneDto dto5 = new PhoneDto();
        phones.add(businessPhone2);
        ClientDetailDtoConverter.toPhoneDto(businessPhone2, dto5, serviceErrors);

        PhoneImpl businessPhone4 = new PhoneImpl();
        PhoneDto dto6 = new PhoneDto();
        businessPhone4.setNumber("919792288282");
        businessPhone4.setModificationSeq("2");
        businessPhone4.setPhoneKey(AddressKey.valueOf("15"));
        businessPhone4.setType(AddressMedium.BUSINESS_TELEPHONE);
        phones.add(businessPhone4);
        ClientDetailDtoConverter.toPhoneDto(businessPhone4, dto6, serviceErrors);

        PhoneImpl businessPhone3 = new PhoneImpl();
        PhoneDto dto7 = new PhoneDto();
        businessPhone3.setNumber("0282500000");
        businessPhone3.setModificationSeq("2");
        businessPhone3.setPhoneKey(AddressKey.valueOf("11"));
        businessPhone3.setType(AddressMedium.BUSINESS_TELEPHONE);
        phones.add(businessPhone3);
        ClientDetailDtoConverter.toPhoneDto(businessPhone3, dto7, serviceErrors);


        PhoneDto dto9 = new PhoneDto();
        PhoneImpl otherPhone4 = (PhoneImpl) getPhone("911244111111", null, "2", "18", AddressMedium.OTHER, false);
        phones.add(otherPhone4);
        ClientDetailDtoConverter.toPhoneDto(otherPhone4, dto9, serviceErrors);

        PhoneDto dto0 = new PhoneDto();
        PhoneImpl otherPhone5 = (PhoneImpl) getPhone("1800123456", null, "2", "18", AddressMedium.OTHER, false);
        phones.add(otherPhone5);
        ClientDetailDtoConverter.toPhoneDto(otherPhone5, dto0, serviceErrors);


        PhoneDto dto10 = new PhoneDto();
        PhoneImpl otherPhone6 = (PhoneImpl) getPhone("611800123456", null, "2", "18", AddressMedium.OTHER, false);
        phones.add(otherPhone6);
        ClientDetailDtoConverter.toPhoneDto(otherPhone6, dto10, serviceErrors);

        PhoneImpl otherPhone7 = (PhoneImpl) getPhone("1300123456", null, "2", "18", AddressMedium.OTHER, false);
        PhoneDto dto11 = new PhoneDto();
        phones.add(otherPhone5);
        ClientDetailDtoConverter.toPhoneDto(otherPhone7, dto11, serviceErrors);

        PhoneImpl otherPhone8 = (PhoneImpl) getPhone("611300123456", null, "2", "18", AddressMedium.OTHER, false);
        PhoneDto dto12 = new PhoneDto();
        phones.add(otherPhone8);
        ClientDetailDtoConverter.toPhoneDto(otherPhone8, dto12, serviceErrors);

        phoneDtos.add(dto1);
        phoneDtos.add(dto2);
        phoneDtos.add(dto3);
        phoneDtos.add(dto4);
        phoneDtos.add(dto5);
        phoneDtos.add(dto6);
        phoneDtos.add(dto7);
        phoneDtos.add(dto9);
        phoneDtos.add(dto0);
        phoneDtos.add(dto10);
        phoneDtos.add(dto11);
        phoneDtos.add(dto12);

        ClientDetailDtoConverter.parseFullNumberListToGCMNumberFormat(phoneDtos);

        assertThat(phoneDtos.get(0).getCountryCode(), is("61"));
        assertThat(phoneDtos.get(0).getAreaCode(), is("02"));
        assertThat(phoneDtos.get(0).getNumber(), is("82500000"));

        assertThat(phoneDtos.get(1).getCountryCode(), is("61"));
        assertThat(phoneDtos.get(1).getAreaCode(), is("02"));
        assertThat(phoneDtos.get(1).getNumber(), is("82500000"));

        assertThat(phoneDtos.get(2).getCountryCode(), is("61"));
        assertThat(phoneDtos.get(2).getAreaCode(), is("02"));
        assertThat(phoneDtos.get(2).getNumber(), is("82500000"));

        assertThat(phoneDtos.get(3).getCountryCode(), is("61"));
        assertThat(phoneDtos.get(3).getAreaCode(), is("04"));
        assertThat(phoneDtos.get(3).getNumber(), is("01766762"));

        assertThat(phoneDtos.get(4).getCountryCode(), is("61"));
        assertThat(phoneDtos.get(4).getAreaCode(), is("04"));
        assertThat(phoneDtos.get(4).getNumber(), is("01872255"));

        assertThat(phoneDtos.get(5).getCountryCode(), is("91"));
        assertThat(phoneDtos.get(5).getAreaCode(), is("979"));
        assertThat(phoneDtos.get(5).getNumber(), is("2288282"));

        assertThat(phoneDtos.get(6).getCountryCode(), is("61"));
        assertThat(phoneDtos.get(6).getAreaCode(), is("02"));
        assertThat(phoneDtos.get(6).getNumber(), is("82500000"));

        assertThat(phoneDtos.get(7).getCountryCode(), is("91"));
        assertThat(phoneDtos.get(7).getAreaCode(), is("124"));
        assertThat(phoneDtos.get(7).getNumber(), is("4111111"));

        assertThat(phoneDtos.get(8).getCountryCode(), is("61"));
        assertThat(phoneDtos.get(8).getAreaCode(), is("1800"));
        assertThat(phoneDtos.get(8).getNumber(), is("123456"));

        assertThat(phoneDtos.get(9).getCountryCode(), is("61"));
        assertThat(phoneDtos.get(9).getAreaCode(), is("1800"));
        assertThat(phoneDtos.get(9).getNumber(), is("123456"));

        assertThat(phoneDtos.get(10).getCountryCode(), is("61"));
        assertThat(phoneDtos.get(10).getAreaCode(), is("1300"));
        assertThat(phoneDtos.get(10).getNumber(), is("123456"));

        assertThat(phoneDtos.get(11).getCountryCode(), is("61"));
        assertThat(phoneDtos.get(11).getAreaCode(), is("1300"));
        assertThat(phoneDtos.get(11).getNumber(), is("123456"));

    }

    @Test
    public void testCreatePhoneListForAvaloqUpdate() {
        List<PhoneDto> phoneDtos = new ArrayList<>();
        PhoneDto dto1 = new PhoneDto();
        dto1.setFullPhoneNumber("61092304244");
        dto1.setRequestedAction("D");

        PhoneDto dto2 = new PhoneDto();
        dto2.setFullPhoneNumber("61092304244");
        dto2.setRequestedAction("A");

        PhoneDto dto3 = new PhoneDto();
        dto3.setFullPhoneNumber("61092304244");
        dto3.setRequestedAction("A");
        phoneDtos.add(dto1);
        phoneDtos.add(dto2);
        phoneDtos.add(dto3);

        ClientTxnDto dto = new ClientTxnDto();
        dto.setPhones(phoneDtos);

        ClientDetailDtoConverter.createPhoneListForAvaloqUpdate(dto);

        assertThat(dto.getPhones().size(), is(2));
        assertThat(dto.getPhones().get(0).getRequestedAction(), is("A"));
        assertThat(dto.getPhones().get(1).getRequestedAction(), is("A"));
    }

    @Test
    public void testAreaCodeFormat() {
        List<PhoneDto> phoneDtos = new ArrayList<>();

        PhoneImpl secondaryPhoneModel = new PhoneImpl();
        PhoneDto dto1 = new PhoneDto();
        secondaryPhoneModel.setNumber("1800123456");
        secondaryPhoneModel.setModificationSeq("3");
        secondaryPhoneModel.setPhoneKey(AddressKey.valueOf("1"));
        secondaryPhoneModel.setType(AddressMedium.MOBILE_PHONE_SECONDARY);
        phones.add(secondaryPhoneModel);
        ClientDetailDtoConverter.toPhoneDto(secondaryPhoneModel, dto1, serviceErrors);

        PhoneImpl homePhone1 = (PhoneImpl) getPhone("0182500000", null, "2", "22", AddressMedium.PERSONAL_TELEPHONE, false);
        PhoneDto dto2 = new PhoneDto();
        phones.add(homePhone1);
        ClientDetailDtoConverter.toPhoneDto(homePhone1, dto2, serviceErrors);

        phoneDtos.add(dto1);
        phoneDtos.add(dto2);

        ClientDetailDtoConverter.parseFullNumberListToGCMNumberFormat(phoneDtos);

        assertThat(phoneDtos.get(0).getCountryCode(), is("61"));
        assertThat(phoneDtos.get(0).getAreaCode(), is("1800"));
        assertThat(phoneDtos.get(0).getNumber(), is("123456"));

        assertThat(phoneDtos.get(1).getCountryCode(), is("61"));
        assertThat(phoneDtos.get(1).getAreaCode(), is("01"));
        assertThat(phoneDtos.get(1).getNumber(), is("82500000"));
    }

    @Test
    public void testSetTaxResidenceCountriesForUpdate() {
        //Initialising the data
        List<TaxResidenceCountriesDto> taxResidenceCountriesDtoList = new ArrayList<>();
        TaxResidenceCountriesDto countriesDto = new TaxResidenceCountriesDto();
        countriesDto.setTin("8303ARFHN");
        countriesDto.setStartDate(new DateTime().toString());
        countriesDto.setTaxResidenceCountry("US");
        taxResidenceCountriesDtoList.add(countriesDto);
        countriesDto = new TaxResidenceCountriesDto();
        countriesDto.setTaxExemptionReason("TIN never issued");
        countriesDto.setStartDate("2017-02-06T00:00:00.000+11:00");
        countriesDto.setEndDate("2017-02-10T00:00:00.000+11:00");
        countriesDto.setTaxResidenceCountry("UK");
        taxResidenceCountriesDtoList.add(countriesDto);
        countriesDto = new TaxResidenceCountriesDto();
        countriesDto.setStartDate(new DateTime().toString());
        countriesDto.setTaxResidenceCountry("FOREIGN");
        countriesDto.setTin("Y");
        taxResidenceCountriesDtoList.add(countriesDto);

        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.COUNTRY), eq("US"), any(ServiceErrors.class))).thenReturn(code1);
        when(countryField1.getValue()).thenReturn("US");
        when(code1.getField("btfg$im_code")).thenReturn(countryField1);
        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.COUNTRY), eq("UK"), any(ServiceErrors.class))).thenReturn(code2);
        when(countryField2.getValue()).thenReturn("UK");
        when(code2.getField("btfg$im_code")).thenReturn(countryField2);
        when(taxExemptionField2.getValue()).thenReturn("R002");
        when(code2.getField("btfg$ucm_code")).thenReturn(taxExemptionField2);
        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.TIN_EXEMPTION_REASONS), eq("TIN never issued"), any(ServiceErrors.class))).thenReturn(code2);

        List<TaxResidenceCountry> taxResidenceCountryList = ClientDetailDtoConverter.
                setTaxResidenceCountriesForUpdate(taxResidenceCountriesDtoList, staticIntegrationService, new ServiceErrorsImpl());

        assertThat("Size of list is 3", taxResidenceCountryList.size(), is(equalTo(3)));

        assertThat("Country 1 Name is US", taxResidenceCountryList.get(0).getResidenceCountry(), is(equalTo("US")));
        assertThat("Country 1 TIN is not null", taxResidenceCountryList.get(0).getTin(), is(equalTo("8303ARFHN")));
        assertThat("Country 1 Exemption reason is null", taxResidenceCountryList.get(0).getExemptionReason(), equalTo(null));
        assertNotNull("Start Date is not null", taxResidenceCountryList.get(0).getStartDate());
        assertNull("EndDate is null", taxResidenceCountryList.get(0).getEndDate());

        assertThat("Country 2 Name is US", taxResidenceCountryList.get(1).getResidenceCountry(), is(equalTo("UK")));
        assertThat("Country 2 TIN is not null", taxResidenceCountryList.get(1).getTin(), equalTo(null));
        assertNotNull("EndDate is not null", taxResidenceCountryList.get(1).getEndDate());
        assertNotNull("StartDate is not null", taxResidenceCountryList.get(1).getStartDate());
        assertThat("Country 2 Exemption reason is R002", taxResidenceCountryList.get(1).getExemptionReason(), equalTo("R002"));

        assertThat("FOREIGN block Name is FOREIGN", taxResidenceCountryList.get(2).getResidenceCountry(), is(equalTo("FOREIGN")));
        assertThat("FOREIGN block TIN value is Y", taxResidenceCountryList.get(2).getTin(), is(equalTo("Y")));
        assertThat("FOREIGN block exemption reason is null", taxResidenceCountryList.get(2).getExemptionReason(), equalTo(null));

        verify(staticIntegrationService, times(2)).loadCodeByAvaloqId(eq(CodeCategory.COUNTRY), anyString(), any(ServiceErrors.class));
        verify(staticIntegrationService, times(1)).loadCodeByAvaloqId(eq(CodeCategory.TIN_EXEMPTION_REASONS), anyString(), any(ServiceErrors.class));
    }


    private Phone getPhone(String number, String countryCode, String modificationSeq, String phoneKey, AddressMedium businessTelephone, boolean preferred) {
        PhoneImpl phoneModel = new PhoneImpl();
        phoneModel.setNumber(number);
        phoneModel.setCountryCode(countryCode);
        phoneModel.setModificationSeq(modificationSeq);
        phoneModel.setPhoneKey(AddressKey.valueOf(phoneKey));
        phoneModel.setType(businessTelephone);
        phoneModel.setPreferred(preferred);
        return phoneModel;
    }

    private Email getEmail(String modificationSeq, String emailKey, String emailAddress, AddressMedium addressMedium, boolean isPreferred) {
        EmailImpl emailModel = new EmailImpl();
        emailModel.setEmail(emailAddress);
        emailModel.setModificationSeq(modificationSeq);
        emailModel.setEmailKey(AddressKey.valueOf(emailKey));
        emailModel.setType(addressMedium);
        emailModel.setPreferred(isPreferred);
        return emailModel;
    }

    private Address getAddressModel(String streetNumber, String streetName, String streetType, String suburb, String state, String postCode, String addressId, String occupierName) {
        AddressImpl addressModel = new AddressImpl();
        addressModel.setStreetNumber(streetNumber);
        addressModel.setStreetName(streetName);
        addressModel.setStreetType(streetType);
        addressModel.setSuburb(suburb);
        addressModel.setState(state);
        addressModel.setPostCode(postCode);
        addressModel.setAddressKey(AddressKey.valueOf(addressId));
        addressModel.setOccupierName(occupierName);
        return addressModel;
    }

    private class AssociatedPersonBuilder {

        private AssociatedPerson associatedPerson;

        private AssociatedPersonBuilder() {
            associatedPerson = Mockito.mock(AssociatedPerson.class);
        }

        public AssociatedPersonBuilder withClientKey(String clientKey) {
            when(associatedPerson.getClientKey()).thenReturn(ClientKey.valueOf(clientKey));
            return this;
        }

        public AssociatedPersonBuilder withTFNEntered(boolean isTFNEntered) {
            when(associatedPerson.hasTFNEntered()).thenReturn(isTFNEntered);
            return this;
        }

        public AssociatedPersonBuilder withFormerNames(String... formerNames) {
            List<FormerName> names = Lambda.convert(formerNames, new Converter<String, FormerName>() {
                @Override
                public FormerName convert(String formerNameString) {
                    FormerNameImpl formerName = new FormerNameImpl();
                    formerName.setFormerName(formerNameString);
                    return formerName;
                }
            });
            when(associatedPerson.getFormerNames()).thenReturn(names);
            return this;
        }

        public AssociatedPersonBuilder withBirthCountry(String birthCountry) {
            when(associatedPerson.getBirthCountry()).thenReturn(birthCountry);
            return this;
        }

        public AssociatedPersonBuilder withBirthStateDomestic(String birthStateDomestic) {
            when(associatedPerson.getBirthStateDomestic()).thenReturn(birthStateDomestic);
            return this;
        }

        public AssociatedPersonBuilder withBirthSuburb(String birthSuburb) {
            when(associatedPerson.getBirthSuburb()).thenReturn(birthSuburb);
            return this;
        }

        public AssociatedPerson create() {
            return associatedPerson;
        }

        public AssociatedPersonBuilder withTFNExemptionId(String exemptionId) {
            when(associatedPerson.getTfnExemptId()).thenReturn(exemptionId);
            return this;
        }
    }

    @Test
    public void testTaxResidenceCountries() {
        CompanyImpl companyModel = new CompanyImpl();
        companyModel.setAcn("123456789");
        companyModel.setResiCountryCodeForTax("Australia");
        companyModel.setFullName("John Smith");
        companyModel.setInvestorType(InvestorType.TRUST);
        companyModel.setPersonAssociation(InvestorRole.Signatory);
        companyModel.setTfnProvided(true);
        companyModel.setCisId("12312312312");
        companyModel.setExemptionReason(ExemptionReason.SOCIAL_BENEFICIARY);
        List<InvestorDetail> linkedClients = new ArrayList<InvestorDetail>();
        IndividualDetailImpl investorModel = new IndividualDetailImpl();
        investorModel.setPersonAssociation(InvestorRole.Secretary);
        investorModel.setClientKey(ClientKey.valueOf("36689"));
        investorModel.setTitle("Mr");
        investorModel.setIdVerificationStatus(IdentityVerificationStatus.Completed);

        investorModel.setAddresses(addresses);
        investorModel.setPhones(phones);
        investorModel.setEmails(emails);

        linkedClients.add(investorModel);
        companyModel.setLinkedClients(linkedClients);
        companyModel.setAbn("123456");
        companyModel.setRegistrationState("NSW");
        companyModel.setClientKey(ClientKey.valueOf("12345678"));
        List<com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry> countriesDtos = getTaxResidenceCountriesWithAustralia();
        companyModel.setTaxResidenceCountries(countriesDtos);
        ClientDto clientDto = ClientDetailDtoConverter.toClientDto(companyModel, null, staticIntegrationService,null);
        List<TaxResidenceCountriesDto> taxResidenceCountriesDtos = ((InvestorDto)clientDto).getTaxResidenceCountries();
        assertNotNull(taxResidenceCountriesDtos);
        assertEquals("Australia", taxResidenceCountriesDtos.get(0).getTaxResidenceCountry());
        assertEquals("AU",taxResidenceCountriesDtos.get(0).getTaxResidencyCountryCode());
        assertEquals("Afghanistan", taxResidenceCountriesDtos.get(1).getTaxResidenceCountry());
        assertEquals("AF",taxResidenceCountriesDtos.get(1).getTaxResidencyCountryCode());
        assertEquals("12456", taxResidenceCountriesDtos.get(1).getTin());
        assertNull(taxResidenceCountriesDtos.get(1).getTaxExemptionReason());
        assertEquals("Afghanistan", taxResidenceCountriesDtos.get(1).getTaxResidenceCountry());
        assertEquals("Singapore", taxResidenceCountriesDtos.get(2).getTaxResidenceCountry());
        assertEquals("Singapore", taxResidenceCountriesDtos.get(2).getTaxResidenceCountry());
        assertEquals("", taxResidenceCountriesDtos.get(2).getTin());
        assertEquals("TIN never issued", taxResidenceCountriesDtos.get(2).getTaxExemptionReason());
        assertEquals("tin", taxResidenceCountriesDtos.get(2).getTaxExemptionReasonCode());
        assertEquals("United Kingdom", taxResidenceCountriesDtos.get(3).getTaxResidenceCountry());
    }

    private List<com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry> getTaxResidenceCountriesWithAustralia() {
        List<com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry> countriesDtos = new ArrayList<>();

        TaxResidenceCountryImpl taxResidenceCountry1 = new TaxResidenceCountryImpl();
        taxResidenceCountry1.setCountryCode(new Integer("2007"));
        taxResidenceCountry1.setCountryName("Singapore");
        taxResidenceCountry1.setTin("");
        taxResidenceCountry1.setTinExemption("TIN never issued");
        taxResidenceCountry1.setTinExemptionCode(Integer.valueOf("1022"));
        countriesDtos.add(taxResidenceCountry1);
        when(staticIntegrationService.loadCode(eq(CodeCategory.COUNTRY), eq("2007"), any(ServiceErrors.class))).thenReturn(new CodeImpl("2046", "SIN", "Singapore", "sin"));
        when(staticIntegrationService.loadCode(eq(CodeCategory.TIN_EXEMPTION_REASONS), eq("1022"), any(ServiceErrors.class))).thenReturn(new CodeImpl("1022", "tin", "TIN never issued", "tin"));


        TaxResidenceCountryImpl taxResidenceCountry2 = new TaxResidenceCountryImpl();
        taxResidenceCountry2.setCountryCode(new Integer("2061"));
        taxResidenceCountry2.setCountryName("Australia");
        taxResidenceCountry2.setTin("");
        taxResidenceCountry2.setTinExemption("TIN never issued");
        taxResidenceCountry2.setTinExemptionCode(Integer.valueOf("1022"));
        countriesDtos.add(taxResidenceCountry2);
        when(staticIntegrationService.loadCode(eq(CodeCategory.COUNTRY), eq("2061"), any(ServiceErrors.class))).thenReturn(new CodeImpl("2061", "AU", "Australia", "au"));
        when(staticIntegrationService.loadCode(eq(CodeCategory.TIN_EXEMPTION_REASONS), eq("1022"), any(ServiceErrors.class))).thenReturn(new CodeImpl("1022", "tin", "TIN never issued", "tin"));

        TaxResidenceCountryImpl taxResidenceCountry3 = new TaxResidenceCountryImpl();
        taxResidenceCountry3.setCountryCode(new Integer("2170"));
        taxResidenceCountry3.setCountryName("United Kingdom");
        taxResidenceCountry3.setTin("");
        taxResidenceCountry3.setTinExemption("TIN never issued");
        taxResidenceCountry3.setTinExemptionCode(Integer.valueOf("1022"));
        countriesDtos.add(taxResidenceCountry3);
        when(staticIntegrationService.loadCode(eq(CodeCategory.COUNTRY), eq("2170"), any(ServiceErrors.class))).thenReturn(new CodeImpl("2170", "UK", "United Kingdon", "uk"));
        when(staticIntegrationService.loadCode(eq(CodeCategory.TIN_EXEMPTION_REASONS), eq("1022"), any(ServiceErrors.class))).thenReturn(new CodeImpl("1022", "tin", "TIN never issued", "tin"));

        TaxResidenceCountryImpl taxResidenceCountry4 = new TaxResidenceCountryImpl();
        taxResidenceCountry4.setCountryCode(new Integer("2046"));
        taxResidenceCountry4.setCountryName("Afghanistan");
        taxResidenceCountry4.setTin("12456");
        taxResidenceCountry4.setTinExemption(null);
        taxResidenceCountry4.setTinExemptionCode(null);
        countriesDtos.add(taxResidenceCountry4);
        when(staticIntegrationService.loadCode(eq(CodeCategory.COUNTRY), eq("2046"), any(ServiceErrors.class))).thenReturn(new CodeImpl("2046", "AF", "Afghanistan", "af"));
        return countriesDtos;
    }


    @Test
    public void testCorporateCompanyTaxResidenceCountries() {
        SmsfImpl smsfModel = new SmsfImpl();
        smsfModel.setResiCountryCodeForTax("Australia");
        smsfModel.setFullName("John Smith");
        smsfModel.setInvestorType(InvestorType.TRUST);
        smsfModel.setPersonAssociation(InvestorRole.Signatory);
        smsfModel.setTfnProvided(true);
        smsfModel.setCisId("12312312312");
        smsfModel.setExemptionReason(ExemptionReason.SOCIAL_BENEFICIARY);
        List<InvestorDetail> linkedClients = new ArrayList<InvestorDetail>();
        IndividualDetailImpl investorModel = new IndividualDetailImpl();
        investorModel.setPersonAssociation(InvestorRole.Secretary);
        investorModel.setClientKey(ClientKey.valueOf("36689"));
        investorModel.setTitle("Mr");
        investorModel.setIdVerificationStatus(IdentityVerificationStatus.Completed);

        investorModel.setAddresses(addresses);
        investorModel.setPhones(phones);
        investorModel.setEmails(emails);

        linkedClients.add(investorModel);
        smsfModel.setLinkedClients(linkedClients);
        smsfModel.setAbn("123456");
        smsfModel.setRegistrationState("NSW");
        smsfModel.setClientKey(ClientKey.valueOf("12345678"));

        CompanyImpl companyModel = new CompanyImpl();
        companyModel.setAcn("123456789");
        companyModel.setResiCountryCodeForTax("Australia");
        companyModel.setClientKey(ClientKey.valueOf("36689"));

        companyModel.setLinkedClients(null);
        List<com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry> countriesDtos = getTaxResidenceCountriesWithAustralia();
        companyModel.setTaxResidenceCountries(countriesDtos);

        List<ClientDetail> trusteeList = new ArrayList<ClientDetail>();
        trusteeList.add(companyModel);
        smsfModel.setTrustees(trusteeList);

        ClientDto clientDto = ClientDetailDtoConverter.toClientDto(smsfModel, null, staticIntegrationService,null);

        assertNotNull(clientDto);
        assertNotNull(((SmsfDto)clientDto).getCompany());
        assertNotNull(((SmsfDto)clientDto).getCompany().getTaxResidenceCountries());

        List<TaxResidenceCountriesDto> taxResidenceCountriesDtos = ((SmsfDto)clientDto).getCompany().getTaxResidenceCountries();
        assertNotNull(taxResidenceCountriesDtos);
        assertEquals("Australia", taxResidenceCountriesDtos.get(0).getTaxResidenceCountry());
        assertEquals("", taxResidenceCountriesDtos.get(0).getTin());
        assertEquals("TIN never issued", taxResidenceCountriesDtos.get(0).getTaxExemptionReason());

        assertEquals("Afghanistan", taxResidenceCountriesDtos.get(1).getTaxResidenceCountry());
        assertEquals("12456", taxResidenceCountriesDtos.get(1).getTin());
        assertNull(taxResidenceCountriesDtos.get(1).getTaxExemptionReason());

        assertEquals("Singapore", taxResidenceCountriesDtos.get(2).getTaxResidenceCountry());
        assertEquals("", taxResidenceCountriesDtos.get(2).getTin());
        assertEquals("TIN never issued", taxResidenceCountriesDtos.get(2).getTaxExemptionReason());

        assertEquals("United Kingdom", taxResidenceCountriesDtos.get(3).getTaxResidenceCountry());
    }



}
