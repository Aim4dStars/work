package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.IndividualSmsfApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountAuthoriser;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.account.TransactionPermission;
import com.bt.nextgen.service.avaloq.accountactivation.AccountStructure;
import com.bt.nextgen.service.avaloq.accountactivation.LinkedPortfolioDetails;
import com.bt.nextgen.service.avaloq.accountactivation.RegisteredAccountImpl;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.ApplicationDocumentDetail;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.AddressType;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.ExemptionReason;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.domain.IdentityVerificationStatus;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.Organisation;
import com.bt.nextgen.service.integration.domain.PersonDetail;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.btfin.panorama.core.security.avaloq.userinformation.PersonRelationship;
import com.btfin.panorama.service.integration.broker.Broker;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ClientApplicationDetailsDtoConverterService_IndividualSMSFTest {

    @InjectMocks
    private ClientApplicationDetailsDtoConverterService clientApplicationDetailsDtoConverterService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private InvestorDtoConverterForPersonDetail investorDtoConverterForPersonDetail;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private OrganizationDtoConverterForApplicationDocument organizationDtoConverterForApplicationDocument;

    @Mock
    private PersonMapperService personAccountSettingsMapperService;

    @Mock
    OrganisationMapper organisationMapper;

    @Mock
    FeatureTogglesService featureTogglesService;

    @Mock
    private CRSTaxDetailHelperService crsTaxDetailHelperService;

    @Mock
    ClientApplicationDetailsDtoHelperService clientApplicationDetailsDtoHelperService;

    private ServiceErrors serviceErrors;
    private BrokerKey adviserKey;
    List<PersonDetail> mockPersonList;
    List<Organisation> mockOrganisations;
    List<PersonDetail> mockAddresses;
    List<PersonDetail> mockInvestorAccountSettingsList;
    List<AccountAuthoriser> mockAdviserAccountSettingsList;
    List<LinkedPortfolioDetails> mockPortfolio;

    @Before
    public void setUp() throws Exception {
        adviserKey= BrokerKey.valueOf("95794");
        serviceErrors = mock(ServiceErrors.class);
        BrokerUser mockmockAdviser = createMockAdviser();
        when(brokerIntegrationService.getAdviserBrokerUser(adviserKey,serviceErrors)).thenReturn(mockmockAdviser);
        Product mockProduct = mock(Product.class);
        when(mockProduct.getProductName()).thenReturn("Test Product Name");
        when(productIntegrationService.getProductDetail(any(ProductKey.class), any(ServiceErrors.class))).thenReturn(mockProduct);

        mockOrganisations = Arrays.asList(createMockOrganization1(), createMockOrganization2());
        mockAddresses = createMockAddresses();
        mockInvestorAccountSettingsList = createMockInvestorAccountSettingsList();
        mockAdviserAccountSettingsList = createMockAdviserAccountSettingsList();
        mockPortfolio = createMockPortfolio();

        doCallRealMethod().when(investorDtoConverterForPersonDetail).convertFromPersonDetail(any(PersonDetail.class),any(AccountSubType.class),anyMap()) ;
        doCallRealMethod().when(investorDtoConverterForPersonDetail).getPersonRoles(any(PersonDetail.class));
        doCallRealMethod().when(investorDtoConverterForPersonDetail).setCrsTaxDetailHelperService(any(CRSTaxDetailHelperService.class));
        investorDtoConverterForPersonDetail.setCrsTaxDetailHelperService(crsTaxDetailHelperService);
        Broker dealerGroupBroker = getDealerGroupBroker(true);
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(dealerGroupBroker);
    }

    private Broker getDealerGroupBroker(boolean offlineApprovalAccess) {
        Broker dealerGroupBroker = mock(Broker.class);
        when(dealerGroupBroker.getParentKey()).thenReturn(BrokerKey.valueOf("123"));
        when(dealerGroupBroker.isOfflineApproval()).thenReturn(offlineApprovalAccess);
        return dealerGroupBroker;
    }

    @Test
       public void convertFromApplicationDocumentDetail_forNewIndividualSMSF() throws Exception {
        mockPersonList = createMockPersonListForIndividualeSMSF();
        ApplicationDocumentDetail applicationDocumentDetail = createApplicationDocumentDetailNewIndividualSMSFType(serviceErrors);

        Organisation smsfMock = mock(Organisation.class);
        when (smsfMock.getFullName()).thenReturn("Account name");
        when(organizationDtoConverterForApplicationDocument.getOrganisation(applicationDocumentDetail.getOrganisations(),
                IClientApplicationForm.AccountType.NEW_INDIVIDUAL_SMSF)).thenReturn(smsfMock);
        ClientApplicationDetailsDto detailsDto = clientApplicationDetailsDtoConverterService.convert(applicationDocumentDetail, serviceErrors, UserExperience.ADVISED );
        assertThat(detailsDto.getInvestorAccountType(), is("newIndividualSMSF"));
        IndividualSmsfApplicationDetailsDto smsfDetailDto = (IndividualSmsfApplicationDetailsDto) detailsDto;
        assertThat(smsfDetailDto.getTrustees().size(), is(1));
        assertThat(smsfDetailDto.getMembers().size(), is(4));
        verify(clientApplicationDetailsDtoHelperService,times(1)).getExistingPersonsByCISKey(anyString(),any(List.class), any(ServiceErrors.class));
    }

    @Test
    public void convertFromApplicationDocumentDetail_forExistingIndividualSMSF() throws Exception {
        mockPersonList = createMockPersonListForIndividualeSMSF();
        ApplicationDocumentDetail applicationDocumentDetail = createApplicationDocumentDetailExistingIndividualSMSFType(serviceErrors);

        Organisation smsfMock = mock(Organisation.class);
        when (smsfMock.getFullName()).thenReturn("Account name");
        when(organizationDtoConverterForApplicationDocument.getOrganisation(applicationDocumentDetail.getOrganisations(),
                IClientApplicationForm.AccountType.INDIVIDUAL_SMSF)).thenReturn(smsfMock);
        ClientApplicationDetailsDto detailsDto = clientApplicationDetailsDtoConverterService.convert(applicationDocumentDetail, serviceErrors, UserExperience.ADVISED );
        assertThat(detailsDto.getInvestorAccountType(), is("individualSMSF"));
        IndividualSmsfApplicationDetailsDto smsfDetailDto = (IndividualSmsfApplicationDetailsDto) detailsDto;
        assertThat(smsfDetailDto.getTrustees().size(), is(1));
        assertThat(smsfDetailDto.getMembers().size(), is(4));
        verify(clientApplicationDetailsDtoHelperService,times(1)).getExistingPersonsByCISKey(anyString(),any(List.class), any(ServiceErrors.class));

    }

    private BrokerUser createMockAdviser() {
        BrokerUser adviser = mock(BrokerUser.class);
        when(adviser.getFirstName()).thenReturn("First_Name");
        when(adviser.getMiddleName()).thenReturn("Middle_Name");
        when(adviser.getLastName()).thenReturn("Last_Name");
        return adviser;
    }

    private ApplicationDocumentDetail createApplicationDocumentDetailNewIndividualSMSFType(ServiceErrors serviceErrors) {
        ApplicationDocumentDetail applicationDocument = mock(ApplicationDocumentDetail.class);
        List<RegisteredAccountImpl> mockRegisteredAccounts = createMockRegisteredAccounts();
        when(applicationDocument.getLinkedAccounts()).thenReturn(mockRegisteredAccounts);

        when(applicationDocument.getAdviserKey()).thenReturn(adviserKey);
        Date applicationOpenDate = new Date();
        when(applicationDocument.getApplicationOpenDate()).thenReturn(applicationOpenDate);

        when(applicationDocument.getPersons()).thenReturn(mockPersonList);
        when(applicationDocument.getOrganisations()).thenReturn(mockOrganisations);
        when(applicationDocument.getAccountSettingsForAllPersons()).thenReturn(mockInvestorAccountSettingsList);
        when(applicationDocument.getAdviserAccountSettings()).thenReturn(mockAdviserAccountSettingsList);
        when(applicationDocument.getPortfolio()).thenReturn(mockPortfolio);
        when(applicationDocument.getOrderType()).thenReturn(OrderType.NewIndividualSMSF.getOrderType());
        when(applicationDocument.getAccountNumber()).thenReturn("12345");
        return applicationDocument;
    }

    private ApplicationDocumentDetail createApplicationDocumentDetailExistingIndividualSMSFType(ServiceErrors serviceErrors) {
        ApplicationDocumentDetail applicationDocument = mock(ApplicationDocumentDetail.class);
        List<RegisteredAccountImpl> mockRegisteredAccounts = createMockRegisteredAccounts();
        when(applicationDocument.getLinkedAccounts()).thenReturn(mockRegisteredAccounts);

        when(applicationDocument.getAdviserKey()).thenReturn(adviserKey);
        Date applicationOpenDate = new Date();
        when(applicationDocument.getApplicationOpenDate()).thenReturn(applicationOpenDate);

        when(applicationDocument.getPersons()).thenReturn(mockPersonList);
        when(applicationDocument.getOrganisations()).thenReturn(mockOrganisations);
        when(applicationDocument.getAccountSettingsForAllPersons()).thenReturn(mockInvestorAccountSettingsList);
        when(applicationDocument.getAdviserAccountSettings()).thenReturn(mockAdviserAccountSettingsList);
        when(applicationDocument.getPortfolio()).thenReturn(mockPortfolio);
        when(applicationDocument.getOrderType()).thenReturn(OrderType.ExistingSMSF.getOrderType());
        when(applicationDocument.getAccountNumber()).thenReturn("12345");
        return applicationDocument;
    }

    private ApplicationDocumentDetail createApplicationDocumentDetailOfCorporateSMSFType(ServiceErrors serviceErrors) {
        ApplicationDocumentDetail applicationDocument = mock(ApplicationDocumentDetail.class);
        List<RegisteredAccountImpl> mockRegisteredAccounts = createMockRegisteredAccounts();
        when(applicationDocument.getLinkedAccounts()).thenReturn(mockRegisteredAccounts);

        when(applicationDocument.getAdviserKey()).thenReturn(adviserKey);
        Date applicationOpenDate = new Date();
        when(applicationDocument.getApplicationOpenDate()).thenReturn(applicationOpenDate);

        when(applicationDocument.getPersons()).thenReturn(mockPersonList);
        when(applicationDocument.getOrganisations()).thenReturn(mockOrganisations);
        when(applicationDocument.getAccountSettingsForAllPersons()).thenReturn(mockInvestorAccountSettingsList);
        when(applicationDocument.getAdviserAccountSettings()).thenReturn(mockAdviserAccountSettingsList);
        when(applicationDocument.getPortfolio()).thenReturn(mockPortfolio);
        return applicationDocument;
    }

    private List<LinkedPortfolioDetails> createMockPortfolio() {
        LinkedPortfolioDetails mockPortfolio = mock(LinkedPortfolioDetails.class);
        when(mockPortfolio.getProductId()).thenReturn("116702");
        when(mockPortfolio.getAccountType()).thenReturn(AccountStructure.S);
        when(mockPortfolio.getAccountNumber()).thenReturn("120027917");
        when(mockPortfolio.getProductId()).thenReturn("103125");
        return Arrays.asList(mockPortfolio);
    }

    private List<AccountAuthoriser> createMockAdviserAccountSettingsList() {
        AccountAuthoriser accountAuthoriser = mock(AccountAuthoriser.class);
        when(accountAuthoriser.getTxnType()).thenReturn(TransactionPermission.Payments_Deposits_To_Linked_Accounts);
        when(accountAuthoriser.getAuthPersonId()).thenReturn(ClientKey.valueOf("43186"));
        return Arrays.asList(accountAuthoriser);
    }

    private List<PersonDetail> createMockInvestorAccountSettingsList() {
        AccountAuthoriser mockAccountAuthorisation1 = createMockAccountAuthorisation(TransactionPermission.Account_Maintenance);
        PersonDetail personDetail1 = MockPersonDetailBuilder.make()
                .withClientKey(ClientKey.valueOf("116165"))
                .withAccountAuthorisationList(Arrays.asList(mockAccountAuthorisation1)).collect();

        AccountAuthoriser mockAccountAuthorisation2 = createMockAccountAuthorisation(TransactionPermission.Payments_Deposits_To_Linked_Accounts);
        PersonDetail personDetail2 = MockPersonDetailBuilder.make()
                .withClientKey(ClientKey.valueOf("116692"))
                .withAccountAuthorisationList(Arrays.asList(mockAccountAuthorisation2)).collect();


        return Arrays.asList(personDetail1, personDetail2);
    }

    private AccountAuthoriser createMockAccountAuthorisation(TransactionPermission transactionPermission) {
        AccountAuthoriser authoriser = mock(AccountAuthoriser.class);
        when(authoriser.getTxnType()).thenReturn(transactionPermission);
        return authoriser;
    }

    private List<PersonDetail> createMockAddresses() {

        Address address1WithDomicile = MockAddressBuilder.make()
                .withStreetNumber("51-57")
                .withStreetName("Pitt")
                .withStreetTypeId("33")
                .withStreetType("Street")
                .withSuburb("SYDNEY")
                .withState("New South Wales")
                .withStateAbbr("NSW")
                .withStateCode("btfg$au_nsw")
                .withPostCode("2000")
                .withCountryCode("au")
                .withCountry("Australia")
                .withIsDomicile(true)
                .withIsMailingAddress(false)
                .withAddressType(AddressMedium.POSTAL)
                .withPostAddress(AddressType.POSTAL)
                .withStateOther("New South Wales")
                .collect();

        Address addressWithMailing = MockAddressBuilder.make()
                .withStreetNumber("51-57")
                .withStreetName("Pitt")
                .withStreetTypeId("33")
                .withStreetType("Street")
                .withSuburb("SYDNEY")
                .withState("New South Wales")
                .withStateAbbr("NSW")
                .withStateCode("btfg$au_nsw")
                .withPostCode("2000")
                .withCountryCode("au")
                .withCountry("Australia")
                .withIsDomicile(false)
                .withIsMailingAddress(true)
                .withAddressType(AddressMedium.POSTAL)
                .withPostAddress(AddressType.POSTAL)
                .withStateOther("New South Wales")
                .collect();

        PersonDetail person1 = MockPersonDetailBuilder.make()
                .withClientKey(ClientKey.valueOf("116697"))
                .withAddresses(Arrays.asList(address1WithDomicile)).collect();
        PersonDetail person2 = MockPersonDetailBuilder.make()
                .withClientKey(ClientKey.valueOf("116696"))
                .withAddresses(Arrays.asList(addressWithMailing, address1WithDomicile)).collect();
        PersonDetail person3 = MockPersonDetailBuilder.make()
                .withClientKey(ClientKey.valueOf("116165"))
                .withAddresses(Arrays.asList(address1WithDomicile, addressWithMailing))
                .withPhones(Arrays.asList(createMockPhone("116163", AddressMedium.MOBILE_PHONE_PRIMARY, "0456456456", true, AddressType.ELECTRONIC)))
                .withEmails(Arrays.asList(createMockEmail("116164", AddressMedium.EMAIL_PRIMARY, "test@email.com", false, AddressType.ELECTRONIC)))
                .collect();
        PersonDetail person4 = MockPersonDetailBuilder.make()
                .withClientKey(ClientKey.valueOf("116692"))
                .withAddresses(Arrays.asList(addressWithMailing, address1WithDomicile)).collect();
        PersonDetail person5 = MockPersonDetailBuilder.make()
                .withClientKey(ClientKey.valueOf("116693"))
                .withAddresses(Arrays.asList(addressWithMailing, address1WithDomicile)).collect();
        PersonDetail person6 = MockPersonDetailBuilder.make()
                .withClientKey(ClientKey.valueOf("116695"))
                .withAddresses(Arrays.asList(addressWithMailing, address1WithDomicile)).collect();

        return Arrays.asList(person1, person2, person3, person4, person5, person6);
    }

    private Email createMockEmail(String emailKey, AddressMedium type, String emailAddress, boolean isPreferred, AddressType category) {
        Email email = mock(Email.class);
        when(email.getEmailKey()).thenReturn(AddressKey.valueOf(emailKey));
        when(email.getType()).thenReturn(type);
        when(email.getEmail()).thenReturn(emailAddress);
        when(email.isPreferred()).thenReturn(isPreferred);
        when(email.getCategory()).thenReturn(category);
        return email;
    }

    private Phone createMockPhone(String phoneKey, AddressMedium type, String number, boolean isPreferred, AddressType category) {
        Phone phone = mock(Phone.class);
        when(phone.getPhoneKey()).thenReturn(AddressKey.valueOf(phoneKey));
        when(phone.getType()).thenReturn(type);
        when(phone.getNumber()).thenReturn(number);
        when(phone.isPreferred()).thenReturn(isPreferred);
        when(phone.getCategory()).thenReturn(category);
        return phone;
    }


    private List<RegisteredAccountImpl> createMockRegisteredAccounts() {
        ArrayList<RegisteredAccountImpl> linkedAccounts = new ArrayList<>();
        RegisteredAccountImpl mockRegistredAccount = mock(RegisteredAccountImpl.class);
        when(mockRegistredAccount.getInitialDeposit()).thenReturn(new BigDecimal(99999));
        when(mockRegistredAccount.isPrimary()).thenReturn(true);
        when(mockRegistredAccount.getCurrencyId()).thenReturn("1009");
        when(mockRegistredAccount.getAccountNumber()).thenReturn("654654");
        when(mockRegistredAccount.getNickName()).thenReturn("Linked Account");
        linkedAccounts.add(mockRegistredAccount);
        return linkedAccounts;
    }


    private List<PersonDetail> createMockPersonListForIndividualeSMSF() {

        AccountAuthoriser accountAuthoriser = mock(AccountAuthoriser.class);
        when(accountAuthoriser.getTxnType()).thenReturn(TransactionPermission.No_Transaction);

        PersonDetail mockPerson3 = MockPersonDetailBuilder.make()
            .withPrimaryRole(PersonRelationship.SHAREHLD)
            .withTitle( "Mr")
            .withFirstName("ShareHolderOne_FirstName")
            .withLastName("ShareHolderOne_LastName")
            .withDateOfBirth(new DateTime(356169651))
//            .withGender(Gender.MALE)
            .withResiCountryForTax("Australia")
            .withResiCountryCodeForTax("2061")
            .withExemptionReason(ExemptionReason.NO_EXEMPTION)
            .withIdentityVerificationStatus(IdentityVerificationStatus.Pending)
            .withGcmId("217187533")
            .withInvestorType(InvestorType.INDIVIDUAL)
            .withClientKey(ClientKey.valueOf("116693"))
            .withIsPrimaryContact(false)
            .withIsApprover(false)
            .withIsMember(false)
            .withIsBeneficiary(false)
            .withIsShareHolder(false)
            .withClientType(ClientType.N)
            .withPersonAssociation(InvestorRole.OTHER).collect();

        PersonDetail mockPerson4 = MockPersonDetailBuilder.make()
            .withPrimaryRole(PersonRelationship.SHAREHLD)
            .withTitle( "Mr")
            .withFirstName("ShareHolderTwo_FirstName")
            .withLastName("ShareHolderTwo_LastName")
            .withDateOfBirth(new DateTime(356169651))
//            .withGender(Gender.MALE)
            .withResiCountryForTax("Australia")
            .withResiCountryCodeForTax("2061")
            .withExemptionReason(ExemptionReason.NO_EXEMPTION)
            .withIdentityVerificationStatus(IdentityVerificationStatus.Pending)
            .withGcmId("217187535")
            .withInvestorType(InvestorType.INDIVIDUAL)
            .withClientKey(ClientKey.valueOf("116165"))
            .withIsPrimaryContact(false)
            .withIsApprover(false)
            .withIsMember(false)
            .withIsBeneficiary(false)
            .withIsShareHolder(false)
            .withClientType(ClientType.N)
            .withPersonAssociation(InvestorRole.OTHER).collect();

        PersonDetail mockPerson5 = MockPersonDetailBuilder.make()
            .withPrimaryRole(PersonRelationship.SHAREHLD)
            .withTitle( "Mr")
            .withFirstName("ShareHolderThree_Under18")
            .withLastName("ShareHolderThree_LastName")
            .withDateOfBirth(new DateTime(356169651))
//            .withGender(Gender.MALE)
            .withResiCountryForTax("Australia")
            .withResiCountryCodeForTax("2061")
            .withExemptionReason(ExemptionReason.NO_EXEMPTION)
            .withIdentityVerificationStatus(IdentityVerificationStatus.Pending)
            .withGcmId("217187534")
            .withInvestorType(InvestorType.INDIVIDUAL)
            .withClientKey(ClientKey.valueOf("116165"))
            .withIsPrimaryContact(false)
            .withIsApprover(false)
            .withIsMember(false)
            .withIsBeneficiary(false)
            .withIsShareHolder(false)
            .withClientType(ClientType.N)
            .withPersonAssociation(InvestorRole.OTHER).collect();

        PersonDetail mockPerson6 = MockPersonDetailBuilder.make()
                .withPrimaryRole(PersonRelationship.SHAREHLD)
                .withTitle( "Mr")
                .withFirstName("PersonOneTrustee_FirstName")
                .withLastName("PersonOneTrustee_LastName")
                .withDateOfBirth(new DateTime(356169651))
                .withGender(Gender.MALE)
                .withResiCountryForTax("Australia")
                .withResiCountryCodeForTax("2061")
                .withExemptionReason(ExemptionReason.NO_EXEMPTION)
                .withIdentityVerificationStatus(IdentityVerificationStatus.Completed)
                .withGcmId("217187449")
                .withInvestorType(InvestorType.INDIVIDUAL)
                .withClientKey(ClientKey.valueOf("116165"))
                .withIsPrimaryContact(true)
                .withIsApprover(false)
                .withIsMember(false)
                .withIsBeneficiary(false)
                .withIsShareHolder(false)
                .withClientType(ClientType.N)
                .withPersonAssociation(InvestorRole.OTHER).collect();

        PersonDetail mockPerson7 = MockPersonDetailBuilder.make()
                .withPrimaryRole(PersonRelationship.TRUSTEE)
                .withTitle( "Mr")
                .withFirstName("PersonTwoTrustee_FirstName")
                .withLastName("PersonTwoTrustee_LastName")
                .withDateOfBirth(new DateTime(356169651))
                .withGender(Gender.MALE)
                .withResiCountryForTax("Australia")
                .withResiCountryCodeForTax("2061")
                .withExemptionReason(ExemptionReason.NO_EXEMPTION)
                .withIdentityVerificationStatus(IdentityVerificationStatus.Completed)
                .withGcmId("217187532")
                .withInvestorType(InvestorType.INDIVIDUAL)
                .withClientKey(ClientKey.valueOf("116692"))
                .withIsPrimaryContact(false)
                .withIsApprover(false)
                .withIsMember(false)
                .withIsBeneficiary(false)
                .withIsShareHolder(true)
                .withClientType(ClientType.N)
                .withAccountAuthorisationList(Arrays.asList(accountAuthoriser))
                .withPersonAssociation(InvestorRole.OTHER).collect();

        return Arrays.asList(mockPerson6,mockPerson7, mockPerson3, mockPerson4, mockPerson5);
    }

    private Organisation createMockOrganization1() {
        Address address = MockAddressBuilder.make()
                .withStreetNumber("51-57")
                .withStreetName("Pitt")
                .withStreetTypeId("33")
                .withStreetType("Street")
                .withSuburb("SYDNEY")
                .withState("New South Wales")
                .withStateAbbr("NSW")
                .withStateCode("btfg$au_nsw")
                .withPostCode("2000")
                .withCountryCode("au")
                .withCountry("Australia")
                .withAddressType(AddressMedium.POSTAL)
                .withPostAddress(AddressType.POSTAL)
                .withStateOther("New South Wales")
                .collect();
        return MockOrganizationBuilder.make()
                .withResiCountryForTax("Australia")
                .withResiCountryCodeForTax("2061")
                .withABN("15068383737")
                .withIsRegistrationForGst(false)
                .withRegistrationDate(new DateTime(1412859600000L))
                .withRegistrationState("NSW")
                .withRegistrationStateCode("5004")
                .withIsRegistrationOnline(false)
                .withIsTfnProvided(false)
                .withExceptionReason(ExemptionReason.NO_EXEMPTION)
                .withIdVerificationStatus(IdentityVerificationStatus.Pending)
                .withAnzsicId("7340")
                .withIndustry("FINANCIAL ASSET INVESTORS (7340)")
                .withGcmId("217187537")
                .withInvestorType(InvestorType.SMSF)
                .withTfnExemptId("99")
                .withClientKey(ClientKey.valueOf("116697"))
                .withFullName("Avengers Corporate SMSF")
                .withAddresses(Arrays.asList(address))
                .withClientType(ClientType.L)
                .withLegalForm(InvestorType.SMSF)
                .withAssociatedRole(InvestorRole.OTHER)
                .collect();
    }

    private Organisation createMockOrganization2() {
        Address address1 = MockAddressBuilder.make()
                .withStreetNumber("51-57")
                .withStreetName("Pitt")
                .withStreetTypeId("33")
                .withStreetType("Street")
                .withSuburb("SYDNEY")
                .withState("New South Wales")
                .withStateAbbr("NSW")
                .withStateCode("btfg$au_nsw")
                .withPostCode("2000")
                .withCountryCode("au")
                .withCountry("Australia")
                .withIsDomicile(false)
                .withIsMailingAddress(true)
                .withAddressType(AddressMedium.POSTAL)
                .withPostAddress(AddressType.POSTAL)
                .withStateOther("New South Wales")
                .collect();
        Address address2 = MockAddressBuilder.make()
                .withStreetNumber("51-57")
                .withStreetName("Pitt")
                .withStreetTypeId("33")
                .withStreetType("Street")
                .withSuburb("SYDNEY")
                .withState("New South Wales")
                .withStateAbbr("NSW")
                .withStateCode("btfg$au_nsw")
                .withPostCode("2000")
                .withCountryCode("au")
                .withCountry("Australia")
                .withIsDomicile(true)
                .withIsMailingAddress(false)
                .withAddressType(AddressMedium.POSTAL)
                .withPostAddress(AddressType.POSTAL)
                .withStateOther("New South Wales")
                .collect();
        return MockOrganizationBuilder.make()
                .withResiCountryForTax("Australia")
                .withResiCountryCodeForTax("2061")
                .withACN("068383737")
                .withIsRegistrationForGst(false)
                .withIsRegistrationOnline(false)
                .withAsicName("The Avengers")
                .withIsTfnProvided(false)
                .withExceptionReason(ExemptionReason.NO_EXEMPTION)
                .withIdVerificationStatus(IdentityVerificationStatus.Pending)
                .withAnzsicId("7340")
                .withIndustry("FINANCIAL ASSET INVESTORS (7340)")
                .withGcmId("217187536")
                .withInvestorType(InvestorType.COMPANY)
                .withTfnExemptId("99")
                .withClientKey(ClientKey.valueOf("116696"))
                .withFullName("The Avengers")
                .withAddresses(Arrays.asList(address1, address2))
                .withClientType(ClientType.L)
                .withLegalForm(InvestorType.COMPANY)
                .withAssociatedRole(InvestorRole.OTHER)
                .collect();
    }
}

