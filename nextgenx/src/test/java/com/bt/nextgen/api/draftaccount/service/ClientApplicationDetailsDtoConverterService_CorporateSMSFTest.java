package com.bt.nextgen.api.draftaccount.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.account.v3.model.PersonRelationDto;
import com.bt.nextgen.api.draftaccount.model.AccountSettingsDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.CorporateSmsfApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountAuthoriser;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.account.AlternateNameImpl;
import com.bt.nextgen.service.avaloq.account.AlternateNameType;
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
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ClientApplicationDetailsDtoConverterService_CorporateSMSFTest {

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
    private OrganisationMapper organisationMapper;

    @Mock
    FeatureTogglesService featureTogglesService;

    @Mock
    private CRSTaxDetailHelperService crsTaxDetailHelperService;

    @Mock
    private ClientApplicationDetailsDtoHelperService clientApplicationDetailsDtoHelperService;


    private ServiceErrors serviceErrors;
    private BrokerKey adviserKey;
    List<PersonDetail> mockPersonList;
    List<Organisation> mockOrganisations;
    List<PersonDetail> mockAddresses;
    List<PersonDetail> mockInvestorAccountSettingsList;
    List<AlternateNameImpl> mockAlternateNameList;
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

        mockPersonList = createMockPersonListForCorporateSMSF();
        mockOrganisations = Arrays.asList(createMockOrganization1(), createMockOrganization2());
        mockAddresses = createMockAddresses();
        mockInvestorAccountSettingsList = Arrays.asList(createMockInvestorAccountSettingsList("116165", TransactionPermission.Account_Maintenance),
            createMockInvestorAccountSettingsList("116692", TransactionPermission.Payments_Deposits_To_Linked_Accounts));
        mockAdviserAccountSettingsList = createMockAdviserAccountSettingsList(Arrays.asList(TransactionPermission.Payments_Deposits_To_Linked_Accounts));
        mockPortfolio = createMockPortfolio();

        doCallRealMethod().when(investorDtoConverterForPersonDetail).convertFromPersonDetail(any(PersonDetail.class),any(AccountSubType.class),anyMap());
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
    public void convertFromApplicationDocumentDetail_forCorporateSMSF() throws Exception {

        ApplicationDocumentDetail applicationDocumentDetail = createApplicationDocumentDetailOfCorporateSMSFType(OrderType.ExistingSMSF, null, false);

        Organisation smsfMock = mock(Organisation.class);
        when (smsfMock.getFullName()).thenReturn("Account name");
        when(organizationDtoConverterForApplicationDocument.getOrganisation(applicationDocumentDetail.getOrganisations(),
                IClientApplicationForm.AccountType.CORPORATE_SMSF)).thenReturn(smsfMock);
        ClientApplicationDetailsDto detailsDto = clientApplicationDetailsDtoConverterService.convert(applicationDocumentDetail, serviceErrors, UserExperience.ADVISED);

        assertThat(detailsDto, instanceOf(CorporateSmsfApplicationDetailsDto.class));
        CorporateSmsfApplicationDetailsDto smsfDetailDto = (CorporateSmsfApplicationDetailsDto) detailsDto;
        assertThat(smsfDetailDto.getDirectors().size(), is(2));

        assertThat(smsfDetailDto.getShareholdersAndMembers().size(), is(3));
        verify(personAccountSettingsMapperService, times(1)).mapPersonAccountSettings(mockPersonList, mockInvestorAccountSettingsList);
        assertThat(smsfDetailDto.getAccountAvaloqStatus(), is(AccountStatus.ACTIVE.getStatus()));
        verify(clientApplicationDetailsDtoHelperService,times(1)).getExistingPersonsByCISKey(anyString(),any(List.class), any(ServiceErrors.class));

        assertThat(smsfDetailDto.getLinkedAccounts().size(), is(1));
        assertThat(smsfDetailDto.getLinkedAccounts().get(0).isPrimary(), is(true));
    }

    @Test
    public void convertFromApplicationDocumentDetail_forCorporateSMSF_WithNoLinkedAccounts() throws Exception {

        ApplicationDocumentDetail applicationDocumentDetail = createApplicationDocumentDetailOfCorporateSMSFType(OrderType.ExistingSMSF, null, true);

        Organisation smsfMock = mock(Organisation.class);
        when (smsfMock.getFullName()).thenReturn("Account name");
        when(organizationDtoConverterForApplicationDocument.getOrganisation(applicationDocumentDetail.getOrganisations(),
            IClientApplicationForm.AccountType.CORPORATE_SMSF)).thenReturn(smsfMock);
        ClientApplicationDetailsDto detailsDto = clientApplicationDetailsDtoConverterService.convert(applicationDocumentDetail, serviceErrors, UserExperience.ADVISED);

        assertThat(detailsDto, instanceOf(CorporateSmsfApplicationDetailsDto.class));
        CorporateSmsfApplicationDetailsDto smsfDetailDto = (CorporateSmsfApplicationDetailsDto) detailsDto;
        assertThat(smsfDetailDto.getDirectors().size(), is(2));

        assertThat(smsfDetailDto.getShareholdersAndMembers().size(), is(3));
        verify(personAccountSettingsMapperService, times(1)).mapPersonAccountSettings(mockPersonList, mockInvestorAccountSettingsList);
        assertThat(smsfDetailDto.getAccountAvaloqStatus(), is(AccountStatus.ACTIVE.getStatus()));
        verify(clientApplicationDetailsDtoHelperService,times(1)).getExistingPersonsByCISKey(anyString(),any(List.class), any(ServiceErrors.class));

        assertThat(smsfDetailDto.getLinkedAccounts().size(), is(0));
    }

    @Test
    public void convertFromApplicationDocumentDetail_forNewCorporateSMSF() throws Exception {

        ApplicationDocumentDetail applicationDocumentDetail = createApplicationDocumentDetailOfCorporateSMSFType(OrderType.NewCorporateSMSF, AccountStatus.FUND_ESTABLISHMENT_PENDING, false);

        Organisation smsfMock = mock(Organisation.class);
        when (smsfMock.getFullName()).thenReturn("Account name");
        when(organizationDtoConverterForApplicationDocument.getOrganisation(applicationDocumentDetail.getOrganisations(),
                IClientApplicationForm.AccountType.NEW_CORPORATE_SMSF)).thenReturn(smsfMock);
        ClientApplicationDetailsDto detailsDto = clientApplicationDetailsDtoConverterService.convert(applicationDocumentDetail, serviceErrors, UserExperience.ADVISED);

        assertThat(detailsDto, instanceOf(CorporateSmsfApplicationDetailsDto.class));
        CorporateSmsfApplicationDetailsDto smsfDetailDto = (CorporateSmsfApplicationDetailsDto) detailsDto;
        assertThat(smsfDetailDto.getDirectors().size(), is(2));

        assertThat(smsfDetailDto.getShareholdersAndMembers().size(), is(3));
        assertThat(smsfDetailDto.getAccountAvaloqStatus(), is(AccountStatus.FUND_ESTABLISHMENT_PENDING.getStatus()));
        verify(personAccountSettingsMapperService, times(1)).mapPersonAccountSettings(mockPersonList, mockInvestorAccountSettingsList);
        verify(personAccountSettingsMapperService, times(1)).mapPersonAlternateNames(mockPersonList, mockAlternateNameList);
        verify(clientApplicationDetailsDtoHelperService,times(1)).getExistingPersonsByCISKey(anyString(),any(List.class), any(ServiceErrors.class));
    }

    @Test
    public void convertFromApplicationDocumentDetail_forNewCorporateSMSF_verifyPersonRoles() throws Exception {
        ApplicationDocumentDetail applicationDocumentDetail = createApplicationDocumentDetailOfCorporateSMSFType(OrderType.NewCorporateSMSF, AccountStatus.FUND_ESTABLISHMENT_PENDING, false);

        Organisation smsfMock = mock(Organisation.class);
        when (smsfMock.getFullName()).thenReturn("Account name");
        when(organizationDtoConverterForApplicationDocument.getOrganisation(applicationDocumentDetail.getOrganisations(),
                IClientApplicationForm.AccountType.NEW_CORPORATE_SMSF)).thenReturn(smsfMock);

        ClientApplicationDetailsDto detailsDto = clientApplicationDetailsDtoConverterService.convert(applicationDocumentDetail, serviceErrors, UserExperience.ADVISED);

        assertThat(detailsDto, instanceOf(CorporateSmsfApplicationDetailsDto.class));
        CorporateSmsfApplicationDetailsDto smsfDetailDto = (CorporateSmsfApplicationDetailsDto) detailsDto;
        assertThat(smsfDetailDto.getDirectors().size(), is(2));

        AccountSettingsDto accountSettingsDto = smsfDetailDto.getAccountSettings();
        List<PersonRelationDto> investorAccountSettings = Lambda.filter(new LambdaMatcher<PersonRelationDto>() {
            @Override
            protected boolean matchesSafely(PersonRelationDto personRelationDto) {
                return !personRelationDto.isAdviser();
            }
        }, accountSettingsDto.getPersonRelations());
        assertThat(investorAccountSettings.get(0).getPersonRoles().size(), is(1));
        verify(clientApplicationDetailsDtoHelperService,times(1)).getExistingPersonsByCISKey(anyString(),any(List.class), any(ServiceErrors.class));
    }

    @Test
    public void convertFromApplicationDocumentDetail_forNewCorporateSMSF_wherePermissionIsCompanyRegistration() throws Exception {
        mockAdviserAccountSettingsList = createMockAdviserAccountSettingsList(Arrays.asList(TransactionPermission.Company_Registration));
        ApplicationDocumentDetail applicationDocumentDetail = createApplicationDocumentDetailOfCorporateSMSFType(OrderType.NewCorporateSMSF, AccountStatus.FUND_ESTABLISHMENT_PENDING, false);

        Organisation smsfMock = mock(Organisation.class);
        when (smsfMock.getFullName()).thenReturn("Account name");
        when(organizationDtoConverterForApplicationDocument.getOrganisation(applicationDocumentDetail.getOrganisations(),
            IClientApplicationForm.AccountType.NEW_CORPORATE_SMSF)).thenReturn(smsfMock);
        ClientApplicationDetailsDto detailsDto = clientApplicationDetailsDtoConverterService.convert(applicationDocumentDetail, serviceErrors, UserExperience.ADVISED);

        assertThat(detailsDto, instanceOf(CorporateSmsfApplicationDetailsDto.class));
        CorporateSmsfApplicationDetailsDto smsfDetailDto = (CorporateSmsfApplicationDetailsDto) detailsDto;
        assertThat(smsfDetailDto.getDirectors().size(), is(2));

        assertThat(smsfDetailDto.getShareholdersAndMembers().size(), is(3));
        assertThat(smsfDetailDto.getAccountAvaloqStatus(), is(AccountStatus.FUND_ESTABLISHMENT_PENDING.getStatus()));
        verify(personAccountSettingsMapperService, times(1)).mapPersonAccountSettings(mockPersonList, mockInvestorAccountSettingsList);
        verify(personAccountSettingsMapperService, times(1)).mapPersonAlternateNames(mockPersonList, mockAlternateNameList);
        assertThat(detailsDto.getAccountSettings().getPersonRelations().get(2).getPermissions(), is("No Payments"));
        verify(clientApplicationDetailsDtoHelperService,times(1)).getExistingPersonsByCISKey(anyString(),any(List.class), any(ServiceErrors.class));
    }

    @Test
    public void convertFromApplicationDocumentDetail_forNewCorporateSMSF_whereMoreThanOnePermission() throws Exception {
        mockAdviserAccountSettingsList = createMockAdviserAccountSettingsList(Arrays.asList(TransactionPermission.Company_Registration, TransactionPermission.Payments_Deposits));
        ApplicationDocumentDetail applicationDocumentDetail = createApplicationDocumentDetailOfCorporateSMSFType(OrderType.NewCorporateSMSF, AccountStatus.FUND_ESTABLISHMENT_PENDING, false);

        Organisation smsfMock = mock(Organisation.class);
        when (smsfMock.getFullName()).thenReturn("Account name");
        when(organizationDtoConverterForApplicationDocument.getOrganisation(applicationDocumentDetail.getOrganisations(),
            IClientApplicationForm.AccountType.NEW_CORPORATE_SMSF)).thenReturn(smsfMock);
        ClientApplicationDetailsDto detailsDto = clientApplicationDetailsDtoConverterService.convert(applicationDocumentDetail, serviceErrors, UserExperience.ADVISED);

        assertThat(((CorporateSmsfApplicationDetailsDto) detailsDto).getShareholdersAndMembers().size(), is(3));
        assertThat(detailsDto.getAccountSettings().getPersonRelations().get(2).getPermissions(), is("All payments (linked accounts, BPAY and Pay Anyone)"));
        verify(clientApplicationDetailsDtoHelperService,times(1)).getExistingPersonsByCISKey(anyString(),any(List.class), any(ServiceErrors.class));
    }

    private BrokerUser createMockAdviser() {
        BrokerUser adviser = mock(BrokerUser.class);
        when(adviser.getFirstName()).thenReturn("First_Name");
        when(adviser.getMiddleName()).thenReturn("Middle_Name");
        when(adviser.getLastName()).thenReturn("Last_Name");
        return adviser;
    }

    private ApplicationDocumentDetail createApplicationDocumentDetailOfCorporateSMSFType(OrderType order, AccountStatus accountStatus, boolean emptyLinkedAccounts) {
        ApplicationDocumentDetail applicationDocument = mock(ApplicationDocumentDetail.class);
        List<RegisteredAccountImpl> mockRegisteredAccounts = emptyLinkedAccounts ? createMockEmptyRegisteredAccounts(true): createMockRegisteredAccounts();
        if (emptyLinkedAccounts) {
            mockRegisteredAccounts.addAll(createMockEmptyRegisteredAccounts(false));
        }
        when(applicationDocument.getLinkedAccounts()).thenReturn(mockRegisteredAccounts);
        when(applicationDocument.getOrderType()).thenReturn(order.getOrderType());

        when(applicationDocument.getAdviserKey()).thenReturn(adviserKey);
        Date applicationOpenDate = new Date();
        when(applicationDocument.getApplicationOpenDate()).thenReturn(applicationOpenDate);

        when(applicationDocument.getPersons()).thenReturn(mockPersonList);
        when(applicationDocument.getOrganisations()).thenReturn(mockOrganisations);
        when(applicationDocument.getAccountSettingsForAllPersons()).thenReturn(mockInvestorAccountSettingsList);
        when(applicationDocument.getAlternateNames()).thenReturn(mockAlternateNameList);
        when(applicationDocument.getAdviserAccountSettings()).thenReturn(mockAdviserAccountSettingsList);
        when(applicationDocument.getPortfolio()).thenReturn(mockPortfolio);
        when(applicationDocument.getAccountStatus()).thenReturn(accountStatus);
        when(applicationDocument.getAccountNumber()).thenReturn("123");
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

    private List<AccountAuthoriser> createMockAdviserAccountSettingsList(List<TransactionPermission> permission) {
        List<AccountAuthoriser> accountAuthoriserList = new ArrayList<>();
        for (TransactionPermission transactionPermission : permission) {
            AccountAuthoriser accountAuthoriser = mock(AccountAuthoriser.class);
            when(accountAuthoriser.getTxnType()).thenReturn(transactionPermission);
            when(accountAuthoriser.getAuthPersonId()).thenReturn(ClientKey.valueOf("43186"));
            accountAuthoriserList.add(accountAuthoriser);
        }
        return accountAuthoriserList;
    }

    private PersonDetail createMockInvestorAccountSettingsList(String clientKey, TransactionPermission permission) {
        AccountAuthoriser mockAccountAuthorisation1 = createMockAccountAuthorisation(permission);
        PersonDetail personDetail1 = MockPersonDetailBuilder.make()
                .withClientKey(ClientKey.valueOf(clientKey))
                .withAccountAuthorisationList(Arrays.asList(mockAccountAuthorisation1)).collect();
        return personDetail1;
    }

    private List<AlternateNameImpl> createMockAlternateNamesList() {
        AlternateNameImpl mockAlternateName1 = createMockAlternateName(AlternateNameType.FormerName, "116165", "ABCD_FORMER");
        AlternateNameImpl mockAlternateName2 = createMockAlternateName(AlternateNameType.FormerName, "116692", "FIRST_FORMER");
        AlternateNameImpl mockAlternateName3 = createMockAlternateName(AlternateNameType.FormerName, "116692", "SECOND_FORMER");

        return Arrays.asList(mockAlternateName1, mockAlternateName2, mockAlternateName3);
    }

    private AlternateNameImpl createMockAlternateName(AlternateNameType alternateNameType, String clientKey, String fullname) {
        AlternateNameImpl alternateName = new AlternateNameImpl();
        alternateName.setAlternateNameType(alternateNameType);
        alternateName.setClientKey(ClientKey.valueOf(clientKey));
        alternateName.setFullName(fullname);
        return alternateName;
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

    private List<RegisteredAccountImpl> createMockEmptyRegisteredAccounts(boolean isPrimary) {
        ArrayList<RegisteredAccountImpl> linkedAccounts = new ArrayList<>();
        RegisteredAccountImpl mockRegistredAccount = mock(RegisteredAccountImpl.class);
        when(mockRegistredAccount.getInitialDeposit()).thenReturn(new BigDecimal(0));
        when(mockRegistredAccount.isPrimary()).thenReturn(isPrimary);
        when(mockRegistredAccount.getCurrencyId()).thenReturn("");
        when(mockRegistredAccount.getAccountNumber()).thenReturn("");
        when(mockRegistredAccount.getNickName()).thenReturn("");
        when(mockRegistredAccount.getBsb()).thenReturn("000-000");
        linkedAccounts.add(mockRegistredAccount);
        return linkedAccounts;
    }

    private List<PersonDetail> createMockPersonListForCorporateSMSF() {

        AccountAuthoriser accountAuthoriser = mock(AccountAuthoriser.class);
        when(accountAuthoriser.getTxnType()).thenReturn(TransactionPermission.No_Transaction);

        PersonDetail mockPerson1 = MockPersonDetailBuilder.make()
            .withPrimaryRole(PersonRelationship.DIRECTOR)
            .withTitle("Mr")
            .withFirstName("PersonOne_FirstName")
            .withLastName("PersonOne_LastName")
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
            .withAccountAuthorisationList(Arrays.asList(accountAuthoriser))
            .withPersonAssociation(InvestorRole.OTHER).collect();

        PersonDetail mockPerson2 = MockPersonDetailBuilder.make()
            .withPrimaryRole(PersonRelationship.DIRECTOR)
            .withTitle("Mr")
            .withFirstName("PersonTwo_FirstName")
            .withLastName("PersonTwo_LastName")
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

        PersonDetail mockPerson3 = MockPersonDetailBuilder.make()
            .withPrimaryRole(PersonRelationship.SHAREHLD)
            .withTitle("Mr")
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
            .withTitle("Mr")
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
            .withTitle("Mr")
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

        return Arrays.asList(mockPerson1,mockPerson2, mockPerson3, mockPerson4, mockPerson5);
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

