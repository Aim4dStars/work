package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.client.model.ClientDto;
import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.service.ClientListDtoService;
import com.bt.nextgen.api.draftaccount.model.ApplicationClientStatus;
import com.bt.nextgen.api.draftaccount.model.HoldingApplicationClientDto;
import com.bt.nextgen.api.draftaccount.model.HoldingApplicationDto;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationDocumentImpl;
import com.bt.nextgen.service.avaloq.accountactivation.ApprovalType;
import com.bt.nextgen.service.avaloq.accountactivation.AssociatedPersonImpl;
import com.bt.nextgen.service.avaloq.client.ClientIdentifier;
import com.bt.nextgen.service.avaloq.client.ClientIdentifierImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.accountactivation.AccActivationIntegrationService;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerRole;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.AddressType;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import com.btfin.panorama.core.security.avaloq.userinformation.PersonRelationship;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HoldingApplicationDtoServiceImplTest {
    @Mock
    private AccountsPendingApprovalService accountsPendingApprovalService;

    @Mock
    private ClientListDtoService clientListDtoService;

    @Mock
    private AccActivationIntegrationService accActivationIntegrationService;

    @Mock
    private BrokerIntegrationService brokerService;

    @InjectMocks
    private HoldingApplicationDtoServiceImpl service;

    @Mock
    private UserProfileService userProfileService;

    UserProfile activeProfile;


    @Before
    public void setUp() throws Exception {

        activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1");

    }

    private UserProfile getProfile(final JobRole role, final String jobId, final String customerId) {
        UserInformationImpl user = new UserInformationImpl();
        user.setClientKey(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf(customerId));

        JobProfileImpl job = new JobProfileImpl();
        job.setJobRole(role);
        job.setJob(JobKey.valueOf(jobId));

        UserProfile profile = new UserProfileAdapterImpl(user, job);
        return profile;
    }

    @Test
    public void find_shouldReturnDtoWithAppliedOnAccountNameAccountTypeAndClientList_Existing() throws Exception {
        AccountKey accountKey = AccountKey.valueOf("PENDING_ID");
        String accountName = "Existing any account";
        setupPendingAccount(accountKey, accountName, AccountStructureType.Joint);

        Date appSubmitDate = new Date();
        setupApplicationDocument(appSubmitDate);

        ClientDto clientDto1 = createClientDto("John", "Beecham", "a@a.com", "+33333");
        ClientDto clientDto2 = createClientDto("Dennis", "Beecham", "a@a.com", "+33333");
        HoldingApplicationClientDto expectedClient1 = new HoldingApplicationClientDto(clientDto1, true, ApplicationClientStatus.APPROVED);
        HoldingApplicationClientDto expectedClient2 = new HoldingApplicationClientDto(clientDto2, false, ApplicationClientStatus.APPROVED);
        when(clientListDtoService.find(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(clientDto1).thenReturn(clientDto2);
        when(brokerService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(getBrokerUser());
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        HoldingApplicationDto application = service.find(accountKey, new ServiceErrorsImpl());
        assertThat(application.getClients(), hasSize(2));
        assertThat(application.getClients().get(0).getFullName(), is(expectedClient2.getFullName()));
        assertThat(application.getClients().get(0).getEmail(), is(expectedClient2.getEmail()));
        assertThat(application.getClients().get(0).getPhoneNumber(), is(expectedClient2.getPhoneNumber()));
        assertThat(application.getClients().get(0).isApprover(), is(expectedClient2.isApprover()));

        assertThat(application.getClients().get(1).getFullName(), is(expectedClient1.getFullName()));
        assertThat(application.getClients().get(1).isApprover(), is(expectedClient1.isApprover()));

        assertThat(application.getAppliedOn(), is(new DateTime(appSubmitDate)));
        assertThat(application.getAccountName(), is(accountName));
        assertThat(application.getOrderType(), is(nullValue()));

        assertThat(application.getAdviser().getFullName(), is("Homer Simpson"));
        assertThat(application.getAdviser().getCorporateName(), is("Corporate Adviser Name"));
        assertThat(application.getAdviser().getBusinessPhone(), is("11111111"));
        assertThat(application.getAdviser().getEmail(), is("primary@email.com"));
        assertThat(application.getApprovalType(), is(ApprovalType.ONLINE));
    }

    @Test
    public void find_shouldReturnDtoWithFullNameWhenNoCorporateName_Existing() throws Exception {
        AccountKey accountKey = AccountKey.valueOf("PENDING_ID");
        String accountName = "Existing any account";
        setupPendingAccount(accountKey, accountName, AccountStructureType.Joint);

        Date appSubmitDate = new Date();
        setupApplicationDocument(appSubmitDate);

        ClientDto clientDto1 = createClientDto("John", "Beecham", "a@a.com", "+33333");
        ClientDto clientDto2 = createClientDto("Dennis", "Beecham", "a@a.com", "+33333");
        HoldingApplicationClientDto expectedClient1 = new HoldingApplicationClientDto(clientDto1, true,
                ApplicationClientStatus.APPROVED);
        HoldingApplicationClientDto expectedClient2 = new HoldingApplicationClientDto(clientDto2, false,
                ApplicationClientStatus.APPROVED);
        when(clientListDtoService.find(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(clientDto1).thenReturn(
                clientDto2);

        BrokerUser brokerUser = Mockito.mock(BrokerUser.class);
        Mockito.when(brokerUser.getFirstName()).thenReturn("Homer");
        Mockito.when(brokerUser.getLastName()).thenReturn("Simpson");
        when(brokerService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(brokerUser);

        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);

        HoldingApplicationDto application = service.find(accountKey, new ServiceErrorsImpl());
        assertThat(application.getAdviser().getFullName(), is("Homer Simpson"));
        assertThat(application.getAdviser().getCorporateName(), is("Homer Simpson"));
    }

    private BrokerUser getBrokerUser() {
        BrokerUser brokerUser = new BrokerUser() {
            @Override
            public Collection<BrokerRole> getRoles() {
                return null;
            }

            @Override
            public boolean isRegisteredOnline() {
                return false;
            }

            @Override
            public String getPracticeName() {
                return null;
            }

            @Override
            public String getEntityId() {
                return null;
            }

            @Override
            public DateTime getReferenceStartDate() {
                return null;
            }

            @Override
            public CISKey getCISKey() {
                return null;
            }

            @Override
            public JobKey getJob() {
                return null;
            }

            @Override
            public String getFirstName() {
                return "Homer";
            }

            @Override
            public String getMiddleName() {
                return null;
            }

            @Override
            public String getLastName() {
                return "Simpson";
            }

            @Override
            public String getBankReferenceId() {
                return null;
            }

            @Override
            public UserKey getBankReferenceKey() {
                return null;
            }

            @Override
            public Collection<AccountKey> getWrapAccounts() {
                return null;
            }

            @Override
            public Collection<ClientDetail> getRelatedPersons() {
                return null;
            }

            @Override
            public DateTime getCloseDate() {
                return null;
            }

            @Override
            public ClientType getClientType() {
                return null;
            }

            @Override
            public List<Address> getAddresses() {
                return null;
            }

            @Override
            public InvestorType getLegalForm() {
                return null;
            }

            @Override
            public List<Email> getEmails() {
                return Arrays.asList(getEmail(AddressMedium.EMAIL_ADDRESS_SECONDARY, "second@email.com"), getEmail(AddressMedium.EMAIL_PRIMARY, "primary@email.com"));
            }

            @Override
            public List<Phone> getPhones() {
                return Arrays.asList(getPhone(AddressMedium.BUSINESS_TELEPHONE, "11111111"), getPhone(AddressMedium.MOBILE_PHONE_PRIMARY, "2222222222"));
            }

            @Override
            public int getAge() {
                return 0;
            }

            @Override
            public Gender getGender() {
                return null;
            }

            @Override
            public DateTime getDateOfBirth() {
                return null;
            }

            @Override
            public boolean isRegistrationOnline() {
                return false;
            }

            @Override
            public String getTitle() {
                return null;
            }

            @Override
            public String getSafiDeviceId() {
                return null;
            }

            @Override
            public String getModificationSeq() {
                return null;
            }

            @Override
            public String getGcmId() {
                return null;
            }

            @Override
            public DateTime getOpenDate() {
                return null;
            }

            @Override
            public String getFullName() {
                return null;
            }

            @Override
            public com.bt.nextgen.service.integration.userinformation.ClientKey getClientKey() {
                return null;
            }

            @Override
            public void setClientKey(com.bt.nextgen.service.integration.userinformation.ClientKey clientKey) {

            }

            @Override
            public String getProfileId() {
                return null;
            }

            @Override
            public List<TaxResidenceCountry> getTaxResidenceCountries() {
                return null;
            }

            @Override
            public String getBrandSiloId() {
                return null;
            }

            @Override
            public String getCorporateName() {
                return "Corporate Adviser Name";
            }
        };
        return brokerUser;
    }

    private Phone getPhone(final AddressMedium medium, final String phoneVal) {
        Phone phone = new Phone() {
            @Override
            public AddressKey getPhoneKey() {
                return null;
            }

            @Override
            public AddressMedium getType() {
                return medium;
            }

            @Override
            public String getNumber() {
                return phoneVal;
            }

            @Override
            public String getCountryCode() {
                return null;
            }

            @Override
            public String getAreaCode() {
                return null;
            }

            @Override
            public String getModificationSeq() {
                return null;
            }

            @Override
            public boolean isPreferred() {
                return false;
            }

            @Override
            public AddressType getCategory() {
                return null;
            }
        };
        return phone;
    }

    private Email getEmail(final AddressMedium medium, final String emailVal) {
        Email email = new Email() {
            @Override
            public AddressKey getEmailKey() {
                return null;
            }

            @Override
            public AddressMedium getType() {
                return medium;
            }

            @Override
            public String getEmail() {
                return emailVal;
            }

            @Override
            public String getModificationSeq() {
                return null;
            }

            @Override
            public boolean isPreferred() {
                return false;
            }

            @Override
            public AddressType getCategory() {
                return null;
            }
        };
        return email;
    }

    @Test
    public void find_shouldReturnDtoWithAppliedOnAccountNameAccountTypeAndClientList_NewSMSF() throws Exception {
        AccountKey accountKey = AccountKey.valueOf("PENDING_ID");
        String accountName = "New Individual SMSF Account";
        setupPendingAccount(accountKey, accountName, AccountStructureType.Joint);

        Date appSubmitDate = new Date();
        setupApplicationDocumentForNewSMSF(appSubmitDate);

        ClientDto clientDto1 = createClientDto("John", "Beecham", "a@a.com", "+33333");
        ClientDto clientDto2 = createClientDto("Dennis", "Beecham", "a@a.com", "+33333");
        HoldingApplicationClientDto expectedClient1 = new HoldingApplicationClientDto(clientDto1, true, ApplicationClientStatus.APPROVED);
        HoldingApplicationClientDto expectedClient2 = new HoldingApplicationClientDto(clientDto2, false, ApplicationClientStatus.APPROVED);
        when(clientListDtoService.find(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(clientDto1).thenReturn(clientDto2);
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        HoldingApplicationDto application = service.find(accountKey, new ServiceErrorsImpl());
        assertThat(application.getClients(), hasSize(2));
        assertThat(application.getClients().get(0).getFullName(), is(expectedClient2.getFullName()));
        assertThat(application.getClients().get(0).getEmail(), is(expectedClient2.getEmail()));
        assertThat(application.getClients().get(0).getPhoneNumber(), is(expectedClient2.getPhoneNumber()));
        assertThat(application.getClients().get(0).isApprover(), is(expectedClient2.isApprover()));

        assertThat(application.getClients().get(1).getFullName(), is(expectedClient1.getFullName()));
        assertThat(application.getClients().get(1).isApprover(), is(expectedClient1.isApprover()));

        assertThat(application.getAppliedOn(), is(new DateTime(appSubmitDate)));
        assertThat(application.getAccountName(), is(accountName));
        assertThat(application.getOrderType(), is("newIndividualSMSF"));
        assertThat(application.getApprovalType(), is(ApprovalType.OFFLINE));
    }

    @Test
    public void find_shouldIncludeOnlyNaturalPersons() {
        AccountKey accountKey = AccountKey.valueOf("PENDING_ID");
        String accountName = "Investor First";
        setupPendingAccount(accountKey, accountName, AccountStructureType.Company);

        Date appSubmitDate = new Date();
        setupApplicationDocumentForCompanyAccountType(appSubmitDate);

        ClientDto clientDto = createClientDto("John", "Beecham", "a@a.com", "+33333");
        HoldingApplicationClientDto expectedClient = new HoldingApplicationClientDto(clientDto, true, ApplicationClientStatus.APPROVED);

        when(clientListDtoService.find(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(clientDto).thenReturn(null);
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);

        HoldingApplicationDto application = service.find(accountKey, new ServiceErrorsImpl());
        assertThat(application.getClients(), hasSize(1));
        assertThat(application.getClients().get(0).getFullName(), is(expectedClient.getFullName()));
        assertThat(application.getClients().get(0).isApprover(), is(expectedClient.isApprover()));
    }

    @Test
    public void find_IndividualAccountType_shouldIncludeAssociatedPersonsWithPersonRelationshipAccountOwner() throws Exception {
        assertAssociatedWithRelationshipIsIncluded(PersonRelationship.AO, AccountStructureType.Individual);
    }

    @Test
    public void find_TrustAccountType_shouldNotIncludeAssociatedPersonsWithPersonRelationshipAccountOwner() throws Exception {
        assertAssociatedWithRelationshipIsNotIncluded(PersonRelationship.AO, AccountStructureType.Trust);
    }


    @Test
    public void find_TrustAccountType_shouldIncludeAssociatedPersonsWithPersonRelationshipTrustee() throws Exception {
        assertAssociatedWithRelationshipIsIncluded(PersonRelationship.TRUSTEE, AccountStructureType.Trust);
    }

    @Test
    public void find_SmsfAccountType_shouldIncludeAssociatedPersonsWithPersonRelationshipDirector() throws Exception {
        assertAssociatedWithRelationshipIsIncluded(PersonRelationship.DIRECTOR, AccountStructureType.SMSF);
    }

    @Test
    public void find_CompanyAccountType_shouldIncludeAssociatedPersonsWithPersonRelationshipSecretary() throws Exception {
        assertAssociatedWithRelationshipIsIncluded(PersonRelationship.SECRETARY, AccountStructureType.Company);
    }

    @Test
     public void find_CompanyAccountType_shouldIncludeAssociatedPersonsWithPersonRelationshipSignatory() throws Exception {
        assertAssociatedWithRelationshipIsIncluded(PersonRelationship.SIGNATORY, AccountStructureType.Company);
    }

    @Test
    public void find_TrustAccountType_shouldNotIncludeAssociatedPersonsWithPersonRelationshipBeneficiary() throws Exception {
        assertAssociatedWithRelationshipIsNotIncluded(PersonRelationship.BENEFICIARY, AccountStructureType.Trust);
    }

    @Test
    public void find_JointAccountType_shouldNotIncludeAssociatedPersonsWithPersonRelationshipPrimaryContact() throws Exception {
        assertAssociatedWithRelationshipIsNotIncluded(PersonRelationship.PC, AccountStructureType.Joint);
    }

    @Test
    public void find_SmsfAccountType_shouldNotIncludeAssociatedPersonsWithPersonRelationshipMember() throws Exception {
        assertAssociatedWithRelationshipIsNotIncluded(PersonRelationship.MBR, AccountStructureType.SMSF);
    }

    private void assertAssociatedWithRelationshipIsIncluded(PersonRelationship personRelationship, AccountStructureType accountStructureType) {
        AccountKey accountKey = AccountKey.valueOf("PENDING_ID");
        String accountName = "Investor First";
        setupPendingAccount(accountKey, accountName, accountStructureType);

        Date appSubmitDate = new Date();
        String clientId = "1";
        AssociatedPerson person1 = createAssociatedPerson(clientId, true, personRelationship);
        ApplicationDocument applicationDocument = createApplicationDocument(appSubmitDate, person1);
        when(accActivationIntegrationService.loadAccApplicationForPortfolio(anyListOf(WrapAccountIdentifier.class),any(JobRole.class),any(com.bt.nextgen.service.integration.userinformation.ClientKey.class), any(ServiceErrors.class))).thenReturn(Arrays.asList(applicationDocument));
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        ClientDto clientDto = createClientDto("John", "Beecham", "a@a.com", "+33333");
        when(clientListDtoService.find(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(clientDto);

        HoldingApplicationClientDto expectedClient = new HoldingApplicationClientDto(clientDto, true, ApplicationClientStatus.APPROVED);
        HoldingApplicationDto application = service.find(accountKey, new ServiceErrorsImpl());
        assertThat(application.getClients(), hasSize(1));
        assertThat(application.getClients().get(0).getFullName(), is(expectedClient.getFullName()));
        assertThat(application.getClients().get(0).getEmail(), is(expectedClient.getEmail()));
        assertThat(application.getClients().get(0).getPhoneNumber(), is(expectedClient.getPhoneNumber()));
        assertThat(application.getClients().get(0).isApprover(), is(expectedClient.isApprover()));
        assertThat(application.getApprovalType(), is(ApprovalType.ONLINE));
    }

    private void assertAssociatedWithRelationshipIsNotIncluded(PersonRelationship personRelationship, AccountStructureType accountStructureType) {

        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        AccountKey accountKey = AccountKey.valueOf("PENDING_ID");
        String accountName = "Investor First";
        setupPendingAccount(accountKey, accountName, accountStructureType);

        Date appSubmitDate = new Date();
        String clientId = "1";
        AssociatedPerson person = createAssociatedPerson(clientId, true, personRelationship);
        ApplicationDocument applicationDocument = createApplicationDocument(appSubmitDate, person);
        when(accActivationIntegrationService.loadAccApplicationForPortfolio(anyListOf(WrapAccountIdentifier.class), any(JobRole.class), any(com.bt.nextgen.service.integration.userinformation.ClientKey.class), any(ServiceErrors.class))).thenReturn(Arrays.asList(applicationDocument));

        HoldingApplicationDto application = service.find(accountKey, new ServiceErrorsImpl());
        assertThat(application.getClients(), empty());
    }

    private void setupApplicationDocument(Date appSubmitDate) {
        AssociatedPerson person1 = createAssociatedPerson("1", true, PersonRelationship.AO);
        AssociatedPerson person2 = createAssociatedPerson("2", false, PersonRelationship.AO);
        ApplicationDocument applicationDocument = createApplicationDocument(appSubmitDate, person1, person2);
        when(accActivationIntegrationService.loadAccApplicationForPortfolio(anyListOf(WrapAccountIdentifier.class),any(JobRole.class),any(com.bt.nextgen.service.integration.userinformation.ClientKey.class), any(ServiceErrors.class))).thenReturn(Arrays.asList(applicationDocument));
    }

    private void setupApplicationDocumentForNewSMSF(Date appSubmitDate) {
        AssociatedPerson person1 = createAssociatedPerson("1", true, PersonRelationship.AO);
        AssociatedPerson person2 = createAssociatedPerson("2", false, PersonRelationship.AO);
        ApplicationDocument applicationDocument = createApplicationDocumentForNewSMSF(appSubmitDate, person1, person2);
        when(accActivationIntegrationService.loadAccApplicationForPortfolio(anyListOf(WrapAccountIdentifier.class),any(JobRole.class),any(com.bt.nextgen.service.integration.userinformation.ClientKey.class), any(ServiceErrors.class))).thenReturn(Arrays.asList(applicationDocument));
    }

    private void setupApplicationDocumentForCompanyAccountType(Date appSubmitDate) {
        AssociatedPerson person1 = createAssociatedPerson("1", false, PersonRelationship.AO);
        AssociatedPerson person2 = createAssociatedPerson("2", true, PersonRelationship.DIRECTOR);
        ApplicationDocument applicationDocument = createApplicationDocument(appSubmitDate, person1, person2);
        when(accActivationIntegrationService.loadAccApplicationForPortfolio(anyListOf(WrapAccountIdentifier.class),any(JobRole.class),any(com.bt.nextgen.service.integration.userinformation.ClientKey.class), any(ServiceErrors.class))).thenReturn(Arrays.asList(applicationDocument));
    }

    private void setupPendingAccount(AccountKey accountKey, String accountName, AccountStructureType accountStructureType) {
        String ownerId = "1234";
        WrapAccount account = createAccount(accountKey, AccountStatus.PEND_OPN, ownerId, accountName, accountStructureType);
        when(accountsPendingApprovalService.getUserAccountsPendingApprovals(any(ServiceErrors.class))).thenReturn(Arrays.asList(account));
    }


    private ApplicationDocument createApplicationDocument(Date appSubmitDate, AssociatedPerson... associatedPersons) {
        ApplicationDocument applicationDocument = new ApplicationDocumentImpl();
        applicationDocument.setAppSubmitDate(appSubmitDate);
        applicationDocument.setPersonDetails(Arrays.asList(associatedPersons));
        applicationDocument.setOrderType(null);
        applicationDocument.setApprovalType(ApprovalType.ONLINE);
        return applicationDocument;
    }

    private ApplicationDocument createApplicationDocumentForNewSMSF(Date appSubmitDate, AssociatedPerson... associatedPersons) {
        String orderTypeForNewSMSF = OrderType.NewIndividualSMSF.getOrderType();
        ApplicationDocument applicationDocument = new ApplicationDocumentImpl();
        applicationDocument.setAppSubmitDate(appSubmitDate);
        applicationDocument.setPersonDetails(Arrays.asList(associatedPersons));
        applicationDocument.setOrderType(orderTypeForNewSMSF);
        applicationDocument.setApprovalType(ApprovalType.OFFLINE);
        return applicationDocument;
    }

    private AssociatedPerson createAssociatedPerson(String clientId, boolean hasToAcceptTnC, PersonRelationship personRelationship) {
        AssociatedPerson person = new AssociatedPersonImpl();
        person.setClientKey(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf(clientId));
        person.setHasToAcceptTnC(hasToAcceptTnC);
        person.setPersonRel(personRelationship);
        return person;
    }

    private ClientDto createClientDto(String firstName, String lastName, String emailAddress, String number) {
        IndividualDto clientDto = new IndividualDto();
        clientDto.setFirstName(firstName);
        clientDto.setLastName(lastName);

        EmailDto email = new EmailDto();
        email.setEmail(emailAddress);
        email.setEmailType(AddressMedium.EMAIL_PRIMARY.getAddressType());

        PhoneDto phoneNumber = new PhoneDto();
        phoneNumber.setNumber(number);
        phoneNumber.setPhoneType(AddressMedium.MOBILE_PHONE_PRIMARY.getAddressType());

        clientDto.setEmails(Arrays.asList(email));
        clientDto.setPhones(Arrays.asList(phoneNumber));
        return clientDto;
    }

    private WrapAccount createAccount(AccountKey key, AccountStatus status, String ownerId, String name, AccountStructureType accountStructureType) {
        WrapAccountImpl account = new WrapAccountImpl();
        account.setAccountStatus(status);
        account.setAccountKey(key);
        account.setAccountName(name);
        List<ClientIdentifier> list = new ArrayList();
        list.add(new ClientIdentifierImpl(ownerId));
        list.add(new ClientIdentifierImpl(ownerId));
        account.setOwnerClientKeys(list);
        account.setAccountStructureType(accountStructureType);
        return account;
    }
}
