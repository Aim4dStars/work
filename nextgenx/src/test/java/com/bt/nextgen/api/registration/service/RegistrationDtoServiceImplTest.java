package com.bt.nextgen.api.registration.service;

import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.registration.model.InvestorDto;
import com.bt.nextgen.api.registration.model.RegistrationDto;
import com.bt.nextgen.core.repository.UserRepository;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.bt.nextgen.core.security.profile.UserProfileServiceSpringImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationDocumentImpl;
import com.bt.nextgen.service.avaloq.accountactivation.AssociatedPersonImpl;
import com.bt.nextgen.service.avaloq.client.ClientDetailImpl;
import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.avaloq.domain.PhoneImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.accountactivation.AccActivationIntegrationService;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerRole;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditions;
import com.bt.nextgen.service.integration.registration.repository.UserRoleTermsAndConditionsRepository;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import com.btfin.panorama.core.security.avaloq.userinformation.PersonRelationship;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationDtoServiceImplTest {

    @InjectMocks
    private RegistrationDtoServiceImpl registrationDtoServiceImpl;

    @Mock
    private AccActivationIntegrationService accActivationIntegrationService;

    @Mock
    private ClientIntegrationService clientIntegrationService;

    @Mock
    private UserProfileServiceSpringImpl userProfileServiceSpringImpl;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private BrokerUser brokerUser;

    @Mock
    private JobProfile currentJobProfile;

	@Mock
	private UserRoleTermsAndConditionsRepository userRoleTermsAndConditionsRepository;

    @Mock
    private ClientDetailImpl clientDetail;


    UserProfile activeProfile;

    RegistrationDto registrationDtoKeyedObj;

    List <RegistrationDto> lstRegistrationDto;

    String accountId;

    String clientId;

    ApplicationDocumentImpl applicationDocumentImpl = null;

    WrapAccount wrapAccount = null;

    @Before
    public void setup() throws Exception
    {
        when(currentJobProfile.getJobRole()).thenReturn(JobRole.INVESTOR);
        when(currentJobProfile.getJob()).thenReturn(JobKey.valueOf("job id 1"));
        activeProfile = getProfile("client1");
        accountId = "235648";
        registrationDtoKeyedObj = new RegistrationDto(new AccountKey("123456"));
        clientId = "123456";
        lstRegistrationDto = new ArrayList <RegistrationDto>();
        lstRegistrationDto.add(registrationDtoKeyedObj);

        List<ApplicationDocument> lstApplicationDocument = new ArrayList<ApplicationDocument>();
        this.prepareApplicationDocument();
        lstApplicationDocument.add(applicationDocumentImpl);
        Mockito.when(accActivationIntegrationService.loadAccApplicationForPortfolio(Mockito.any(ArrayList.class),Mockito.any(JobRole.class),Mockito.any(ClientKey.class),
                Mockito.any(ServiceErrorsImpl.class))).thenReturn(lstApplicationDocument);

        wrapAccount = Mockito.mock(WrapAccountImpl.class);
        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers(
                Mockito.any(com.bt.nextgen.service.integration.account.AccountKey.class),
                Mockito.any(ServiceErrorsImpl.class))).thenReturn( wrapAccount );
        Mockito.when(wrapAccount.getAdviserPositionId()).thenReturn( BrokerKey.valueOf("3333"));
        Mockito.when(wrapAccount.getAccountKey()).thenReturn(com.bt.nextgen.service.integration.account.AccountKey.valueOf(accountId));

        BrokerUser brokerUser = new BrokerUser()
        {
            @Override
            public UserKey getBankReferenceKey()
            {
                return UserKey.valueOf("3333");
            }

            @Override
            public JobKey getJob()
            {
                return JobKey.valueOf("testJob");
            }

            @Override
            public String getProfileId()
            {
                return "";
            }

            @Override
            public String getFirstName()
            {
                return "Bob";
            }

            @Override
            public String getMiddleName()
            {
                return null;
            }

            @Override
            public String getLastName()
            {
                return "Gilby";
            }

            @Override
            public ClientKey getClientKey()
            {
                return ClientKey.valueOf("testClient");
            }

            @Override
            public void setClientKey(ClientKey personId)
            {

            }

            @Override
            public Collection<BrokerRole> getRoles()
            {
                return null;
            }

            @Override
            public boolean isRegisteredOnline()
            {
                return false;
            }

            @Override
            public String getPracticeName()
            {
                return null;
            }

            @Override
            public String getEntityId()
            {
                return null;
            }

            @Override
            public String getBankReferenceId()
            {
                return null;
            }

            @Override
            public String getFullName()
            {
                return null;
            }

            @Override
            public Collection <com.bt.nextgen.service.integration.account.AccountKey> getWrapAccounts()
            {
                return null;
            }

            @Override
            public ClientType getClientType()
            {
                return null;
            }

            @Override
            public Collection <ClientDetail> getRelatedPersons()
            {
                return null;
            }

            @Override
            public List <Address> getAddresses()
            {
                return null;
            }

            @Override
            public List <Email> getEmails()
            {
                return null;
            }

            @Override
            public List <Phone> getPhones()
            {
                return null;
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
            public DateTime getCloseDate()
            {
                return null;
            }

            @Override
            public DateTime getReferenceStartDate() {
                return null;
            }

            @Override
            public com.bt.nextgen.service.integration.domain.InvestorType getLegalForm() {
                return null;
            }

            @Override
            public CISKey getCISKey() {
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
                // TODO Auto-generated method stub
                return null;
            }
        };
        when(brokerIntegrationService.getAdviserBrokerUser(
                any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(brokerUser);
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        Mockito.when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class),
                Mockito.any(ServiceErrorsImpl.class))).thenReturn(clientDetail);
        when(clientDetail.getFullName()).thenReturn("Jim Jhonson");

        List<Phone> lstPhones = new ArrayList<Phone>();
        PhoneImpl phone = new PhoneImpl();
        phone.setPreferred(true);
        phone.setNumber("1234567890");
        lstPhones.add(phone);
        when(clientDetail.getPhones()).thenReturn(lstPhones);

        List<Email> lstEmail = new ArrayList<Email>();
        EmailImpl email = new EmailImpl();
        email.setPreferred(true);
        email.setEmail("jim.jhonson@mail.com");
        lstEmail.add(email);
        when(clientDetail.getEmails()).thenReturn(lstEmail);
    }


    @Test
    public void shouldUpdateTnCForNonApprovers() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        when(userProfileService.getGcmId()).thenReturn("1111111");
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(brokerUser.getBankReferenceId()).thenReturn("2222222");
        BrokerIdentifier brokerIdentifier = new BrokerIdentifier() {
            @Override
            public BrokerKey getKey() {
                return BrokerKey.valueOf("9999");
            }
        };

        List<BrokerIdentifier> positionKeys = new ArrayList<>();
        positionKeys.add(brokerIdentifier);
        when(brokerIntegrationService
                .getAdvisersForUser(userProfileService.getActiveProfile(), serviceErrors)).thenReturn(positionKeys);
        when(brokerIntegrationService.getAdviserBrokerUser(positionKeys.get(0).getKey()
                , serviceErrors)).thenReturn(brokerUser);
		doNothing().when(userRoleTermsAndConditionsRepository).save(any(UserRoleTermsAndConditions.class));
        assertTrue(registrationDtoServiceImpl.updateTnCForNonAprrover());
    }

    public UserProfile getProfile(final String customerId)
    {
        UserInformation user = new UserInformationImpl();
        user.setClientKey(ClientKey.valueOf(customerId));
        UserProfile profile = new UserProfileAdapterImpl(user, currentJobProfile);
        return profile;
    }

    public ClientKey getKey(UserProfile profile)
    {
        ClientKey clientKey = profile.getClientKey();
        return clientKey;
    }

    private void prepareApplicationDocument() {
        applicationDocumentImpl = new ApplicationDocumentImpl();
        applicationDocumentImpl.setAppNumber("123456");
        List<AssociatedPerson> lstAssociatedPerson = new ArrayList<AssociatedPerson>();
        AssociatedPerson associatedPerson = new AssociatedPersonImpl();
        associatedPerson.setHasToAcceptTnC(true);
        associatedPerson.setClientKey(ClientKey.valueOf("234568"));
        associatedPerson.setRegisteredOnline(true);
        associatedPerson.setHasApprovedTnC(true);
        associatedPerson.setPersonRel(PersonRelationship.AO);
        associatedPerson.setRegisteredOnline(true);
        associatedPerson.setHasApprovedTnC(true);
        lstAssociatedPerson.add(associatedPerson);
        applicationDocumentImpl.setPersonDetails(lstAssociatedPerson);
        String str_date="15-Nov-14";
        DateFormat formatter ;
        Date date = null;
        formatter = new SimpleDateFormat("dd-MMM-yy");
        try {
            date = formatter.parse(str_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        applicationDocumentImpl.setAppSubmitDate(date);
    }

    @Test
    public void testGetAccountApplicationStatus()
    {
        List <RegistrationDto> registrationDtoList = registrationDtoServiceImpl.getAccountApplicationStatus(accountId, new ServiceErrorsImpl());
        assertNotNull(registrationDtoList);
        for(RegistrationDto registrationDto : registrationDtoList)
        {
            assertNotNull(registrationDto);
            assertEquals(AccountStructureType.SMSF, registrationDto.getApplicationType());
            assertEquals("123456", registrationDto.getApplicationReferenceNo());

            DateFormat df = new SimpleDateFormat("dd-MMM-yy");
            Date today = registrationDto.getAppSubmitDate();
            String reportDate = df.format(today);
            assertEquals("15-Nov-14", reportDate);

            assertEquals("Jim Jhonson", registrationDto.getAdviserFullName());
            assertEquals("1234567890", registrationDto.getAdviserPhoneNumber());
            assertEquals("jim.jhonson@mail.com", registrationDto.getAdviserEmail());

            assertNotNull(registrationDto.getLstInvestors());
            List<InvestorDto> lstInvestor = registrationDto.getLstInvestors();
            for(InvestorDto investorDto : lstInvestor)
            {
                assertEquals("Jim Jhonson", investorDto.getInvestorName());
                assertEquals("1234567890", investorDto.getInvestorMobile());
                assertEquals("jim.jhonson@mail.com", investorDto.getInvestorEmail());
                assertEquals(true, investorDto.isPrimary());
                assertEquals(true, investorDto.isRegistered());
                assertEquals(true, investorDto.isApproved());
            }
        }
    }
}
