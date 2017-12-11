package com.bt.nextgen.api.draftaccount.service;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.NonStandardPostalAddress;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.StandardPostalAddress;
import com.bt.nextgen.api.account.v2.model.LinkedAccountDto;
import com.bt.nextgen.api.broker.model.BrokerDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationApprovalDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDetailsDto;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingAccount;
import com.bt.nextgen.core.repository.OnboardingAccountRepository;
import com.bt.nextgen.core.repository.OnboardingApplicationRepository;
import com.bt.nextgen.core.security.profile.InvestorProfileService;
import com.bt.nextgen.core.service.TestDateTimeService;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationRepository;
import com.bt.nextgen.login.util.SamlUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.AvaloqCacheManagedAccountIntegrationService;
import com.bt.nextgen.service.avaloq.account.BankAccountImpl;
import com.bt.nextgen.service.avaloq.accountactivation.AccountActivationRequest;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.client.CacheAvaloqClientIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.avaloq.domain.PhoneImpl;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.bt.nextgen.service.group.customer.groupesb.*;
import com.bt.nextgen.service.group.customer.groupesb.address.AddressAdapter;
import com.bt.nextgen.service.group.customer.groupesb.address.v10.AddressAdapterV10;
import com.bt.nextgen.service.group.customer.groupesb.address.v10.InternationalAddressV10Adapter;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.btfin.panorama.service.integration.account.BankAccount;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.accountactivation.ActivationAccountIntegrationService;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditions;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditionsKey;
import com.bt.nextgen.service.integration.registration.repository.UserRoleTermsAndConditionsRepository;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.test.AttributeMatcher;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import org.hamcrest.Matcher;
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
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyList;

@RunWith(MockitoJUnitRunner.class)
public class ClientApplicationApprovalDtoServiceImplTest {

    @InjectMocks
    private ClientApplicationApprovalDtoServiceImpl activateAccountDtoService;

    @Mock
    private ClientApplicationDetailsDtoServiceImpl clientApplicationDetailsDtoService;

    @Mock
    private ActivationAccountIntegrationService activationService;

    @Mock
    private UserInformationIntegrationService userInformationIntegrationService;

    @Mock
    private InvestorProfileService userProfileService;

    @Mock
    private OnboardingAccountRepository onboardingAccountRepository;

    @Mock
    private OnboardingApplicationRepository onboardingApplicationRepository;

    @Mock
    private TestDateTimeService dateTimeService;

    @Mock
    private ClientIntegrationService clientIntegrationService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    ClientApplicationRepository clientApplicationRepository;

    @Mock
    private AvaloqCacheManagedAccountIntegrationService cacheManagedAccountIntegrationService;

    @Mock
    private CacheAvaloqClientIntegrationServiceImpl cacheClientIntegrationService;

    @Mock
    private BrokerHelperService brokerHelperService;

    @Mock
    private UserRoleTermsAndConditionsRepository userTncRepository;

    @Mock
    private Profile userprofile;

    private ServiceErrors errors;
    private UserProfile profile;

    @Mock
    private UserRoleTermsAndConditions userTnc;

    @Mock
    private AccountsPendingApprovalService accountsPendingApprovalService;

    @Mock
    private ViewClientApplicationDetailsService viewClientApplicationDetailsService;

    @Mock
    private ClientApplicationRepository clientApplicationRepositoryWithoutPermissions;

    @Mock
    private PdsService pdsService;

    @Mock
    private CustomerDataManagementIntegrationService customerDataManagementIntegrationService;

    private OnboardingAccount onboardingAccount;

    @Before
    public void initProfileServiceAndUserRepository() throws Exception {
        final String gcmId = "SOME_GSM_ID";
        when(userProfileService.getGcmId()).thenReturn(gcmId);

        errors = new ServiceErrorsImpl();
        profile = mock(UserProfile.class);
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(profile.getBankReferenceId()).thenReturn("123");
        when(profile.getProfileId()).thenReturn("456");
        when(profile.getCISKey()).thenReturn(CISKey.valueOf("123456789"));
    }

    @Test
    public void shouldMakeAccountActiveAndInvalidateCacheWhenAccountBecomesActiveInAvaloq() throws Exception {
        OnboardingApplicationKey onboardingApplicationKey = OnboardingApplicationKey.valueOf(123);
        String avaloqOrderId = "some avaloq order id";
        OnBoardingApplication onBoardingApplication = createOnboardingApplication(onboardingApplicationKey, avaloqOrderId);
        when(onBoardingApplication.getApplicationType()).thenReturn("xyz");
        when(activationService.submitAccountActivation(argThat(hasAttributeValue("orderId", avaloqOrderId)), eq(errors)))
            .thenReturn(Boolean.TRUE);

        Broker broker = mock(Broker.class);
        when(broker.getKey()).thenReturn(BrokerKey.valueOf("broker-id"));
        List<Broker> brokers = singletonList(broker);

        when(brokerHelperService.getAdviserListForInvestor(eq(profile), eq(errors))).thenReturn(brokers);
        ClientApplication clientApplication = mock(ClientApplication.class);
        when(clientApplicationRepository.findByOnboardingApplicationKey(eq(onboardingApplicationKey), eq(brokers)))
            .thenReturn(clientApplication);

        ClientApplicationApprovalDto response = activateAccountDtoService
            .submit(new ClientApplicationApprovalDto(onboardingApplicationKey, Boolean.FALSE), errors);
        assertThat(response.getActive(), equalTo(Boolean.TRUE));
        verify(clientApplication).markActive();
        verify(cacheManagedAccountIntegrationService).clearContainerListCache();
        verify(cacheManagedAccountIntegrationService).clearAccountListCache();
        verify(cacheManagedAccountIntegrationService).clearOnlineAccountListCache();
        verify(cacheClientIntegrationService).clearClientListCache();
    }

    @Test
    public void shouldNotMakeAccountActiveWhenAccountNotActiveInAvaloq() throws Exception {
        OnboardingApplicationKey onboardingApplicationKey = OnboardingApplicationKey.valueOf(123);
        String avaloqOrderId = "some avaloq order id";
        OnBoardingApplication onBoardingApplication = createOnboardingApplication(onboardingApplicationKey, avaloqOrderId);
        when(onBoardingApplication.getApplicationType()).thenReturn("xyz");
        when(activationService.submitAccountActivation(argThat(hasAttributeValue("orderId", avaloqOrderId)), eq(errors)))
            .thenReturn(Boolean.FALSE);

        ClientApplicationApprovalDto response = activateAccountDtoService
            .submit(new ClientApplicationApprovalDto(onboardingApplicationKey, Boolean.FALSE), errors);

        assertThat(response.getActive(), equalTo(Boolean.FALSE));
    }

    private OnBoardingApplication createOnboardingApplication(OnboardingApplicationKey onboardingApplicationKey, String avaloqOrderId) {
        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        when(onBoardingApplication.getKey()).thenReturn(onboardingApplicationKey);
        when(onBoardingApplication.getAvaloqOrderId()).thenReturn(avaloqOrderId);
        when(onboardingApplicationRepository.find(onboardingApplicationKey)).thenReturn(onBoardingApplication);
        return onBoardingApplication;
    }

    @Test
    public void shouldNotMakeAccountActiveWhenNotFullyApproved() throws Exception {
        OnboardingApplicationKey onboardingApplicationKey = OnboardingApplicationKey.valueOf(123);
        ClientApplicationApprovalDto dto = new ClientApplicationApprovalDto(onboardingApplicationKey, Boolean.FALSE);

        String avaloqOrderId = "some avaloq order id";
        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        when(onBoardingApplication.getAvaloqOrderId()).thenReturn(avaloqOrderId);
        when(onBoardingApplication.getApplicationType()).thenReturn("xyz");
        when(onboardingApplicationRepository.find(onboardingApplicationKey)).thenReturn(onBoardingApplication);
        when(activationService.submitAccountActivation(argThat(hasAttributeValue("orderId", avaloqOrderId)), eq(errors)))
            .thenReturn(Boolean.FALSE);

        activateAccountDtoService.submit(dto, errors);
        assertEquals(dto.getActive(), Boolean.FALSE);
    }

    @Test
    public void shouldApproveAccountUsingNull() throws Exception {
        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        when(onBoardingApplication.getAvaloqOrderId()).thenReturn("some id");
        when(onBoardingApplication.getApplicationType()).thenReturn("xyz");
        when(onboardingApplicationRepository.find(any(OnboardingApplicationKey.class))).thenReturn(onBoardingApplication);

        activateAccountDtoService.submit(mock(ClientApplicationApprovalDto.class), errors);
        verify(activationService).submitAccountActivation(argThat(hasAttributeValue("signDate", null)), eq(errors));
    }

    @Test
    public void shouldApproveAccountUsingGcmId() throws Exception {
        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        when(onBoardingApplication.getAvaloqOrderId()).thenReturn("some id");
        when(onBoardingApplication.getApplicationType()).thenReturn("xyz");
        when(onboardingApplicationRepository.find(any(OnboardingApplicationKey.class))).thenReturn(onBoardingApplication);

        activateAccountDtoService.submit(mock(ClientApplicationApprovalDto.class), errors);
        verify(activationService).submitAccountActivation(argThat(hasAttributeValue("gcmId", "SOME_GSM_ID")), eq(errors));
    }

    @Test
    public void shouldSetTnCAcceptedFlagWhenAUserApprovesAnAccount_withExitingUserTncRecord() throws Exception {
        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        when(onBoardingApplication.getAvaloqOrderId()).thenReturn("some id");
        when(onBoardingApplication.getApplicationType()).thenReturn("xyz");
        when(onboardingApplicationRepository.find(any(OnboardingApplicationKey.class))).thenReturn(onBoardingApplication);
        when(userTncRepository.find(any(UserRoleTermsAndConditionsKey.class))).thenReturn(userTnc);

        activateAccountDtoService.submit(mock(ClientApplicationApprovalDto.class), errors);
        verify(userTnc, Mockito.times(1)).setTncAccepted("Y");
        verify(userTncRepository, Mockito.times(1)).save(any(UserRoleTermsAndConditions.class));
    }

    @Test
    public void shouldSetTnCAcceptedFlagWhenAUserApprovesAnAccount_withNoUserTncRecord() throws Exception {
        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        when(onBoardingApplication.getAvaloqOrderId()).thenReturn("some id");
        when(onBoardingApplication.getApplicationType()).thenReturn("xyz");
        when(onboardingApplicationRepository.find(any(OnboardingApplicationKey.class))).thenReturn(onBoardingApplication);
        when(userTncRepository.find(any(UserRoleTermsAndConditionsKey.class))).thenReturn(null);
        activateAccountDtoService.submit(mock(ClientApplicationApprovalDto.class), errors);
        verify(userTncRepository, Mockito.times(1)).save(any(UserRoleTermsAndConditions.class));
    }

    @Test
    public void approvalWhenLinkedAccountsNeedVerification() throws Exception {
        OnboardingApplicationKey onboardingApplicationKey = OnboardingApplicationKey.valueOf(123);
        String avaloqOrderId = "some avaloq order id";
        OnBoardingApplication onBoardingApplication = createOnboardingApplication(onboardingApplicationKey, avaloqOrderId);

        Long id = 1l;
        ClientApplication clientApplication = createClientApplication(id);
        when(clientApplication.getOnboardingApplication()).thenReturn(onBoardingApplication);
        when(onBoardingApplication.getApplicationType()).thenReturn("Individual");
        when(activationService.submitAccountActivation(argThat(hasAttributeValue("orderId", avaloqOrderId)), eq(errors)))
                .thenReturn(Boolean.TRUE);

        Broker broker = mock(Broker.class);
        when(broker.getKey()).thenReturn(BrokerKey.valueOf("broker-id"));
        List<Broker> brokers = singletonList(broker);
        when(brokerHelperService.getAdviserListForInvestor(eq(profile), eq(errors))).thenReturn(brokers);

        when(clientApplicationRepository.findByOnboardingApplicationKey(eq(onboardingApplicationKey), eq(brokers)))
                .thenReturn(clientApplication);

        WrapAccount wrapAccount = mock(WrapAccount.class);
        when(wrapAccount.getAccountNumber()).thenReturn("account-number");
        when(accountsPendingApprovalService.getUserAccountsPendingApprovals(any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(wrapAccount));
        when(viewClientApplicationDetailsService.viewClientApplicationByAccountNumber(eq("account-number"), any(ServiceErrors.class))).thenReturn(getClientApplicationDetailsDto(AccountStatus.PEND_OPN, true, true));
        onboardingAccount = new OnboardingAccount(123L, onboardingApplicationKey);
        onboardingAccount.setAccountNumber("account-number");
        when(clientApplicationRepositoryWithoutPermissions.findByOnboardingApplicationKey(any(OnboardingApplicationKey.class), any(Collection.class))).thenReturn(clientApplication);
        when(onboardingAccountRepository.findByAccountNumber(any(String.class))).thenReturn(onboardingAccount);
        String expectedPdsUrl = "http://some.url";
        when(pdsService.getUrl(eq(ProductKey.valueOf("MY_PRODUCT_ID")), eq(BrokerKey.valueOf("MY_ADVISER_ID")), any(ServiceErrors.class))).thenReturn(expectedPdsUrl);
        when(clientApplicationDetailsDtoService.findOne(any(ServiceErrors.class))).thenReturn(getClientApplicationDetailsDto(AccountStatus.PEND_OPN, true, true));
        CustomerManagementRequest customerManagementRequest = new CustomerManagementRequestImpl();
        customerManagementRequest.setCISKey(CISKey.valueOf("123456789"));
        customerManagementRequest.setOperationTypes(Arrays.asList(CustomerManagementOperation.ARRANGEMENTS));
        customerManagementRequest.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(getCustomerData());
        SamlToken samlToken = new SamlToken(SamlUtil.loadSaml());
        when(userProfileService.getEffectiveProfile()).thenReturn(userprofile);
        when(userprofile.getToken()).thenReturn(samlToken);
        ClientApplicationApprovalDto response = activateAccountDtoService
                .submit(new ClientApplicationApprovalDto(onboardingApplicationKey, Boolean.FALSE), errors);

        assertThat(response.getActive(), equalTo(Boolean.TRUE));
        verify(clientApplication).markActive();
        verify(cacheManagedAccountIntegrationService).clearContainerListCache();
        verify(cacheManagedAccountIntegrationService).clearAccountListCache();
        verify(cacheManagedAccountIntegrationService).clearOnlineAccountListCache();
        verify(cacheClientIntegrationService).clearClientListCache();
    }

    private CustomerData getCustomerData() {
        IndividualDetails individualDetails = getIndividualDetails("Homer", "Simpson", "Male", "01 Jan 1980", true);
        AddressAdapter address = getAddressAdapter("151", "Clarence", "St", true);
        List<Email> emails = Arrays.asList(getEmail("homer@simpson.com"));
        List<Phone> phones = Arrays.asList(getPhone("61", "02", "9123 1231"));
        List<BankAccount> accounts = getBankAccountList();
        // List<BankAccount> accounts = Arrays.asList(getAccount("062100", "1122334455", "Homer Simpson", "Homie's Account"));
        return getCustomerDataResponse(individualDetails, address, emails, phones, accounts, null);
    }

    private ClientApplicationDetailsDto getClientApplicationDetailsDto(AccountStatus accountStatus, boolean isOnboardingKeyRequired, boolean isLinkedAccountsRequired) {
        ClientApplicationDetailsDto clientApplicationDetailsDto = new ClientApplicationDetailsDto();
        clientApplicationDetailsDto.withAccountAvaloqStatus(accountStatus.getStatus());
        BrokerDto brokerDto = new BrokerDto();
        com.bt.nextgen.api.broker.model.BrokerKey brokerKey = new com.bt.nextgen.api.broker.model.BrokerKey(
                EncodedString.fromPlainText("adviser").toString());
        brokerDto.setKey(brokerKey);
        if(isOnboardingKeyRequired) {
            clientApplicationDetailsDto.withOnboardingApplicationKey(EncodedString.fromPlainText("666").toString());
        }
        if(isLinkedAccountsRequired) {
            clientApplicationDetailsDto.withLinkedAccounts(getLinkedAccountDtos());
        }
        clientApplicationDetailsDto.withAdviser(brokerDto);
        return clientApplicationDetailsDto;
    }

    private List<LinkedAccountDto> getLinkedAccountDtos() {
        List<LinkedAccountDto> linkedAccounts = new ArrayList<LinkedAccountDto>();
        LinkedAccountDto linkedAccountDto1 = new LinkedAccountDto();
        LinkedAccountDto linkedAccountDto2 = new LinkedAccountDto();
        LinkedAccountDto linkedAccountDto3 = new LinkedAccountDto();
        linkedAccountDto1.setBsb("012-345");
        linkedAccountDto1.setAccountNumber("789456123");
        linkedAccountDto1.setName("Linked Account1");
        linkedAccounts.add(linkedAccountDto1);

        linkedAccountDto2.setBsb("756-123");
        linkedAccountDto2.setAccountNumber("759842651");
        linkedAccountDto2.setName("Linked Account 2");
        linkedAccounts.add(linkedAccountDto2);

        linkedAccountDto3.setBsb("456-123");
        linkedAccountDto3.setAccountNumber("753951456");
        linkedAccountDto3.setName("Linked Accont 3");
        linkedAccounts.add(linkedAccountDto3);
        return linkedAccounts;
    }

    private List<BankAccount> getBankAccountList() {
        List<BankAccount>bankAccountList = new ArrayList<>();
        BankAccountImpl bankAccount1 = new BankAccountImpl();
        BankAccountImpl bankAccount2 = new BankAccountImpl();
        bankAccount1.setBsb("012345");
        bankAccount1.setAccountNumber("789456123");
        bankAccount1.setName("Westpac Account 1");
        bankAccountList.add(bankAccount1);

        bankAccount2.setBsb("456123");
        bankAccount2.setAccountNumber("753951456");
        bankAccount2.setName("Westpac Account 2");
        bankAccountList.add(bankAccount2);

        return bankAccountList;
    }

    private ClientApplication createClientApplication(long id) {
        ClientApplication clientApplication = mock(ClientApplication.class);
        when(clientApplication.getProductId()).thenReturn("MY_PRODUCT_ID");
        when(clientApplication.getAdviserPositionId()).thenReturn("MYADVISER_ID");
        when(clientApplication.getId()).thenReturn(id);

        return clientApplication;
    }

    private static Matcher<AccountActivationRequest> hasAttributeValue(String attributeName, Object attributeValue) {
        return new AttributeMatcher(attributeName, attributeValue);
    }

    private AddressAdapter getAddressAdapter(String streetNumber, String streetName, String streetType,
                                             boolean standardAddressFormat) {
        AddressAdapter addressAdapter;
        if (standardAddressFormat) {
            StandardPostalAddress address = new StandardPostalAddress();
            address.setStreetNumber(streetNumber);
            address.setStreetName(streetName);
            address.setStreetType(streetType);
            addressAdapter = new AddressAdapterV10(address);
        } else {
            NonStandardPostalAddress address = new NonStandardPostalAddress();
            address.setAddressLine1(streetNumber + " " + streetName + " " + streetType);
            addressAdapter = new InternationalAddressV10Adapter(address);
        }
        addressAdapter.setStandardAddressFormat(standardAddressFormat);
        return addressAdapter;
    }


    private BankAccount getAccount(String bsb, String number, String name, String nickName) {
        BankAccountImpl bankAccount = new BankAccountImpl();
        bankAccount.setBsb(bsb);
        bankAccount.setAccountNumber(number);
        bankAccount.setName(name);
        bankAccount.setNickName(nickName);
        return bankAccount;
    }

    private Phone getPhone(String countryCode, String areaCode, String number) {
        PhoneImpl phone = new PhoneImpl();
        phone.setAreaCode(areaCode);
        phone.setCountryCode(countryCode);
        phone.setNumber(number);
        phone.setType(AddressMedium.BUSINESS_TELEPHONE);
        return phone;
    }

    private Email getEmail(String emailAddress) {
        EmailImpl email = new EmailImpl();
        email.setEmail(emailAddress);
        email.setType(AddressMedium.EMAIL_PRIMARY);
        return email;
    }

    private IndividualDetails getIndividualDetails(String firstName, String surname, String gender, String dob, boolean idv) {
        IndividualDetails individualDetails = new IndividualDetails();
        individualDetails.setFirstName(firstName);
        individualDetails.setLastName(surname);
        individualDetails.setGender(gender);
        individualDetails.setDateOfBirth(dob);
        individualDetails.setIdVerified(idv);
        return individualDetails;
    }

    private CustomerData getCustomerDataResponse(IndividualDetails individualDetails, Address address, List<Email> emails, List<Phone> phones, List<BankAccount> accounts, List<TaxResidenceCountry> taxResidenceCountries) {
        CustomerData response = new CustomerDataImpl();
        response.setIndividualDetails(individualDetails);
        response.setAddress(address);
        response.setEmails(emails);
        response.setPhoneNumbers(phones);
        response.setBankAccounts(accounts);
        response.setTaxResidenceCountries(taxResidenceCountries);
        return response;
    }
}
