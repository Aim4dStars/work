package com.bt.nextgen.api.notification.service;

import com.bt.nextgen.api.notification.model.NotificationDto;
import com.bt.nextgen.api.notification.model.NotificationDtoKey;
import com.bt.nextgen.api.notification.model.NotificationListDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.NotificationImpl;
import com.bt.nextgen.service.avaloq.NotificationResponseImpl;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerRole;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.broker.BrokerWrapper;
import com.bt.nextgen.service.integration.broker.BrokerWrapperImpl;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.messages.NotificationIntegrationService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.integration.messages.Notification;
import com.btfin.panorama.core.security.integration.messages.NotificationCategory;
import com.btfin.panorama.core.security.integration.messages.NotificationOwnerAccountType;
import com.btfin.panorama.core.security.integration.messages.NotificationResponse;
import com.btfin.panorama.core.security.integration.messages.NotificationStatus;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.avaloq.broker.BrokerIdentifierImpl;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationSearchDtoServiceTest {
    @InjectMocks
    NotificationSearchDtoServiceImpl dtoService;

    @Mock
    NotificationIntegrationService notificationService;

    @Mock
    AccountIntegrationService accountService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    private ProductIntegrationService productService;

    public static final String DATE_FORMAT = "dd MMM yyyy";
    NotificationListDto notificationDto;
    ServiceErrors serviceErrors;
    List<ApiSearchCriteria> criteriaList;
    NotificationResponse notificationResponse;
    List<Notification> notification;
    NotificationDtoKey key;
    Map<AccountKey, WrapAccount> accountResponse;
    Product productResponse;
    BrokerUser brokerResponse;
    UserProfile activeProfile;
    AccountStatus accountStatus;

    @Before
    public void setup() {
        key = new NotificationDtoKey(true);
        activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1");
        notificationResponse = new NotificationResponseImpl();
        notification = new ArrayList<>();
        criteriaList = new ArrayList<>();
        criteriaList.add(new ApiSearchCriteria("startDate", SearchOperation.EQUALS, "01 Jan 2013",
            OperationType.STRING));
        criteriaList.add(new ApiSearchCriteria("endDate", SearchOperation.EQUALS, "30 Aug 2014", OperationType.STRING));
        productResponse = getProduct();
        brokerResponse = getAdviser();
        accountResponse = new HashMap<>();
        accountResponse.put(getWrapAccount(AccountStatus.ACTIVE).getAccountKey(), getWrapAccount(AccountStatus.ACTIVE));

        Map<BrokerKey, BrokerWrapper> brokerWrapperMap = new HashMap<>();
        BrokerWrapper brokerWrapper = new BrokerWrapperImpl(BrokerKey.valueOf("12345"), brokerResponse, true, null);
        brokerWrapperMap.put(BrokerKey.valueOf("12345"), brokerWrapper);
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(accountService.loadWrapAccountWithoutContainers(any(ServiceErrors.class)))
            .thenReturn(accountResponse);
        when(productService.getProductDetail(any(ProductKey.class), any(ServiceErrors.class)))
            .thenReturn(productResponse);
        when(brokerService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class)))
            .thenReturn(
                brokerResponse);
        when(brokerService.getAdviserBrokerUser(anyListOf(BrokerKey.class), any(ServiceErrors.class)))
                .thenReturn(brokerWrapperMap);
    }

    @Test
    public void testFindAllSuccessAdviser() {
        notification.add(getNotification("123", "01 Jul 2013", false, NotificationCategory.CHANGES_TO_ACCOUNTS,
            NotificationStatus.READ, 1));
        notification.add(getNotification("456", "15 Jan 2013", false, NotificationCategory.CLIENT_ACTIONS,
            NotificationStatus.DELETED, 0));
        notification.add(getNotification("456", "15 Jan 2013", false, NotificationCategory.CLIENT_ACTIONS,
            NotificationStatus.UNKNOWN, 0));
        notification.add(getNotification("789", "30 Jan 2014", false, NotificationCategory.FAILED_PAYMENTS,
            NotificationStatus.UNREAD, 0));
        notification.add(getNotification("789", "30 Jan 2014", false, NotificationCategory.FAILED_PAYMENTS,
            NotificationStatus.UNREAD, 1));
        notification.add(getNotification("456", "15 Jan 2013", false, NotificationCategory.CLIENT_ACTIONS,
            NotificationStatus.UNKNOWN, 0));
        notificationResponse.setNotification(notification);

        when(notificationService.loadNotifications(any(ArrayList.class), any(DateTime.class),
            any(DateTime.class), any(ServiceErrors.class)))
            .thenReturn(notificationResponse.getNotification());
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        List<NotificationDto> result = dtoService.search(key, criteriaList, serviceErrors);
        assertEquals(result.size(), 4);
        assertEquals(result.get(0).getId(), notificationResponse.getNotification().get(0).getNotificationId());
        assertEquals(result.get(0).getAccountName(), "Account ABC");
        assertEquals(result.get(0).getAccountNumber(), "12345");
        assertEquals(result.get(0).getProductName(), "product1");
        assertNull(result.get(0).getAdviserName());
    }

    @Test
    public void testFindAllSuccessInvestor() {
        UserProfile activeProfile = getProfile(JobRole.INVESTOR, "job id 1", "client1");
        notification.add(getNotification("123", "01 Jul 2013", false, NotificationCategory.CHANGES_TO_ACCOUNTS,
            NotificationStatus.READ, 0));
        notification.add(getNotification("456", "15 Jan 2013", false, NotificationCategory.CLIENT_ACTIONS,
            NotificationStatus.READ, 0));
        notification.add(getNotification("789", "30 Jan 2014", false, NotificationCategory.FAILED_PAYMENTS,
            NotificationStatus.READ, 0));
        notificationResponse.setNotification(notification);
        when(notificationService.loadNotifications(any(ArrayList.class), any(DateTime.class),
            any(DateTime.class), any(ServiceErrors.class)))
            .thenReturn(notificationResponse.getNotification());
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        List<NotificationDto> result = dtoService.search(key, criteriaList, serviceErrors);
        assertEquals(result.size(), 3);
        assertEquals(result.get(0).getId(), notificationResponse.getNotification().get(0).getNotificationId());
        assertEquals(EncodedString.toPlainText(result.get(0).getAccountId()), "123");
        assertEquals(result.get(0).getAccountName(), "Account ABC");
        assertEquals(result.get(0).getAccountNumber(), "12345");
        assertEquals(result.get(0).getProductName(), "product1");
        assertEquals(result.get(0).getAdviserName(), "Demo Adviser");
    }

    @Test
    public void testFindAllFilterResults() {
        key = new NotificationDtoKey(false);
        criteriaList.add(new ApiSearchCriteria("categories", SearchOperation.EQUALS, "fail_pay", OperationType.STRING));
        notification.add(getNotification("123", "01 Jul 2013", true, NotificationCategory.FAILED_PAYMENTS,
            NotificationStatus.READ, 0));
        notification.add(getNotification("456", "15 Jan 2013", false, NotificationCategory.CLIENT_ACTIONS,
            NotificationStatus.READ, 0));
        notification.add(getNotification("789", "30 Jan 2011", false, NotificationCategory.FAILED_PAYMENTS,
            NotificationStatus.READ, 0));
        notification.add(getNotification("011", "30 Jan 2014", false, NotificationCategory.FAILED_PAYMENTS,
            NotificationStatus.READ, 0));
        notificationResponse.setNotification(notification);

        when(accountService.loadWrapAccountWithoutContainers(any(ServiceErrors.class)))
            .thenReturn(accountResponse);
        when(notificationService.loadNotifications(any(ArrayList.class), any(DateTime.class),
            any(DateTime.class), any(ServiceErrors.class)))
            .thenReturn(notificationResponse.getNotification());
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        List<NotificationDto> result = dtoService.search(key, criteriaList, serviceErrors);
        assertEquals(result.size(), 1);
    }

    @Test
    public void testSearchForAccount() {
        key = new NotificationDtoKey(true);
        criteriaList.add(new ApiSearchCriteria("account",
            SearchOperation.EQUALS,
            EncodedString.fromPlainText("123").toString(),
            OperationType.STRING));
        notification.add(getNotification("123", "01 Jul 2013", false, NotificationCategory.FAILED_PAYMENTS,
            NotificationStatus.READ, 0));
        notificationResponse.setNotification(notification);
        when(accountService.loadWrapAccountWithoutContainers(any(ServiceErrors.class)))
            .thenReturn(accountResponse);
        when(notificationService.loadNotifications(any(ArrayList.class), any(DateTime.class),
            any(DateTime.class), any(ServiceErrors.class)))
            .thenReturn(notificationResponse.getNotification());

        List<NotificationDto> result = dtoService.search(key, criteriaList, serviceErrors);
        assertEquals(result.size(), 1);
    }

    @Test
    public void testReturnNullPendingOpenAccounts()
    {
        accountResponse = new HashMap<>();
        accountResponse.put(getWrapAccount(AccountStatus.PEND_OPN).getAccountKey(), getWrapAccount(AccountStatus.PEND_OPN));
        when(accountService.loadWrapAccountWithoutContainers(any(ServiceErrors.class)))
                .thenReturn(accountResponse);

        notification.add(getNotification("123", "01 Jul 2013", false, NotificationCategory.FAILED_PAYMENTS,
                NotificationStatus.READ, 0));
        notificationResponse.setNotification(notification);

        when(notificationService.loadNotifications(any(ArrayList.class), any(DateTime.class), any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(notificationResponse.getNotification());
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        List<NotificationDto> result = dtoService.search(key, criteriaList, serviceErrors);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), notificationResponse.getNotification().get(0).getNotificationId());
        assertEquals(result.get(0).getAccountName(), "Account ABC");
        assertEquals(result.get(0).getAccountNumber(), null);
    }

    private Notification getNotification(String id, String date, boolean clientMessage, NotificationCategory
        category, NotificationStatus status, int priority) {
        NotificationImpl notification = new NotificationImpl();
        notification.setNotificationId(id);
        notification.setMyMessage(clientMessage);
        notification.setNotificationTimeStamp(DateTime.parse(date, DateTimeFormat.forPattern(DATE_FORMAT)));
        notification.setNotificationCategoryId(category);
        notification.setBpId(id);
        notification.setNotificationMessage("Notification message");
        notification.setOwnerAccountType(NotificationOwnerAccountType.INDIVIDUAL);
        notification.setStatus(status);
        notification.setEventPriority(priority);
        notification.setRecipientId(7242);
        notification.setDocumentUrl("testUrl");
        return notification;
    }

    @Test
    public void testNoDateInCriteria() {
        criteriaList = new ArrayList<>();
        notification.add(getNotification("123", "01 Jul 2013", false, NotificationCategory.CHANGES_TO_ACCOUNTS,
            NotificationStatus.READ, 0));
        notification.add(getNotification("456", "15 Jan 2013", false, NotificationCategory.CLIENT_ACTIONS,
            NotificationStatus.READ, 0));
        notification.add(getNotification("789", "30 Jan 2014", false, NotificationCategory.FAILED_PAYMENTS,
            NotificationStatus.READ, 0));
        notificationResponse.setNotification(notification);

        when(notificationService.loadNotifications(any(ArrayList.class), any(DateTime.class),
            any(DateTime.class), any(ServiceErrors.class)))
            .thenReturn(notificationResponse.getNotification());
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        List<NotificationDto> result = dtoService.search(key, criteriaList, serviceErrors);
        assertEquals(result.size(), 3);
        assertEquals(result.get(0).getId(), notificationResponse.getNotification().get(0).getNotificationId());
        assertEquals(result.get(0).getAccountName(), "Account ABC");
        assertEquals(result.get(0).getAccountNumber(), "12345");
        assertEquals(result.get(0).getProductName(), "product1");
        assertNull(result.get(0).getAdviserName());
    }

    @Test
    public void testDocumentUrl() {
        criteriaList = new ArrayList<>();
        notification.add(getNotification("123", "01 Jul 2013", false, NotificationCategory.NEW_STATEMENTS,
                NotificationStatus.READ, 0));
        notificationResponse.setNotification(notification);

        when(notificationService.loadNotifications(any(ArrayList.class), any(DateTime.class),
                any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(notificationResponse.getNotification());
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        List<NotificationDto> result = dtoService.search(key, criteriaList, serviceErrors);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), notificationResponse.getNotification().get(0).getNotificationId());
        assertEquals(result.get(0).getAccountName(), "Account ABC");
        assertEquals(result.get(0).getAccountNumber(), "12345");
        assertEquals(result.get(0).getProductName(), "product1");
        assertNull(result.get(0).getAdviserName());
        assertEquals(result.get(0).getDocumentUrl(), "testUrl");
    }

    @Test
    public void testFindAllSuccessParaplanner() {
        UserProfile activeProfile = getProfile(JobRole.PARAPLANNER, "job id 1", "client1");
        notification.add(getNotification("123", "01 Jul 2013", false, NotificationCategory.CHANGES_TO_ACCOUNTS,
            NotificationStatus.READ, 0));
        notification.add(getNotification("456", "15 Jan 2013", false, NotificationCategory.CLIENT_ACTIONS,
            NotificationStatus.READ, 0));
        notification.add(getNotification("789", "30 Jan 2014", false, NotificationCategory.FAILED_PAYMENTS,
            NotificationStatus.READ, 0));
        notificationResponse.setNotification(notification);
        when(notificationService.loadNotifications(any(ArrayList.class), any(DateTime.class),
            any(DateTime.class), any(ServiceErrors.class)))
            .thenReturn(notificationResponse.getNotification());
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(brokerService.getAdvisersForUser(any(JobProfile.class), any(ServiceErrors.class)))
            .thenReturn(asList(brokerIdentifier("broker1"), brokerIdentifier("broker2")));
        when(brokerService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class)))
            .thenReturn(getAdviser());
        List<NotificationDto> result = dtoService.search(key, criteriaList, serviceErrors);
        assertEquals(result.size(), 3);
        assertEquals(result.get(0).getId(), notificationResponse.getNotification().get(0).getNotificationId());
        assertEquals(EncodedString.toPlainText(result.get(0).getAccountId()), "123");
        assertEquals(result.get(0).getAccountName(), "Account ABC");
        assertEquals(result.get(0).getAccountNumber(), "12345");
        assertEquals(result.get(0).getProductName(), "product1");
        assertEquals(result.get(0).getAdviserName(), "Demo Adviser");
    }

    @Test
    public void testFindNoResults() {
        notificationResponse.setNotification(new ArrayList<Notification>());
        when(notificationService.loadNotifications(any(ArrayList.class), any(DateTime.class),
            any(DateTime.class), any(ServiceErrors.class)))
            .thenReturn(notificationResponse.getNotification());
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        List<NotificationDto> result = dtoService.search(key, criteriaList, serviceErrors);
        assertEquals(result.size(), 0);
    }

    private static BrokerIdentifier brokerIdentifier(String brokerId) {
        return new BrokerIdentifierImpl(brokerId);
    }

    private UserProfile getProfile(final JobRole role, final String jobId, final String customerId) {
        UserInformationImpl user = new UserInformationImpl();
        user.setClientKey(ClientKey.valueOf(customerId));

        JobProfileImpl job = new JobProfileImpl();
        job.setJobRole(role);
        job.setJob(JobKey.valueOf(jobId));
        user.setProfileId("7242");
        UserProfile profile = new UserProfileAdapterImpl(user, job);
        return profile;
    }

    @Test
    public void findAllSuccessAccountantSupport() {
        notification.add(getNotification("123", "01 Jul 2013", false, NotificationCategory.CHANGES_TO_ACCOUNTS,
                NotificationStatus.READ, 1));
        notification.add(getNotification("456", "15 Jan 2013", false, NotificationCategory.CLIENT_ACTIONS,
                NotificationStatus.DELETED, 0));
        notification.add(getNotification("456", "15 Jan 2013", false, NotificationCategory.CLIENT_ACTIONS,
                NotificationStatus.UNKNOWN, 0));
        notification.add(getNotification("789", "30 Jan 2014", false, NotificationCategory.FAILED_PAYMENTS,
                NotificationStatus.UNREAD, 0));
        notification.add(getNotification("789", "30 Jan 2014", false, NotificationCategory.FAILED_PAYMENTS,
                NotificationStatus.UNREAD, 1));
        notification.add(getNotification("456", "15 Jan 2013", false, NotificationCategory.CLIENT_ACTIONS,
                NotificationStatus.UNKNOWN, 0));
        notificationResponse.setNotification(notification);
        activeProfile = getProfile(JobRole.ACCOUNTANT_SUPPORT_STAFF, "job id 1", "client1");

        when(notificationService.loadNotifications(any(ArrayList.class), any(DateTime.class),
                any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(notificationResponse.getNotification());
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(brokerService.getAdvisersForUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(getBrokerIdentifiers());
        when(brokerService.getAccountantBrokerUsers(anyListOf(BrokerKey.class), any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(getAdviser(), getAdviser()));
        List<NotificationDto> result = dtoService.search(key, criteriaList, serviceErrors);
        assertEquals(result.size(), 4);
        assertEquals(result.get(0).getId(), notificationResponse.getNotification().get(0).getNotificationId());
        assertEquals(result.get(0).getAccountName(), "Account ABC");
        assertEquals(result.get(0).getAccountNumber(), "12345");
        assertEquals(result.get(0).getProductName(), "product1");
    }

    @Test
    public void findAllSuccessParaPlanner() {
        notification.add(getNotification("123", "01 Jul 2013", false, NotificationCategory.CHANGES_TO_ACCOUNTS,
                NotificationStatus.READ, 1));
        notification.add(getNotification("456", "15 Jan 2013", false, NotificationCategory.CLIENT_ACTIONS,
                NotificationStatus.DELETED, 0));
        notification.add(getNotification("456", "15 Jan 2013", false, NotificationCategory.CLIENT_ACTIONS,
                NotificationStatus.UNKNOWN, 0));
        notification.add(getNotification("789", "30 Jan 2014", false, NotificationCategory.FAILED_PAYMENTS,
                NotificationStatus.UNREAD, 0));
        notification.add(getNotification("789", "30 Jan 2014", false, NotificationCategory.FAILED_PAYMENTS,
                NotificationStatus.UNREAD, 1));
        notification.add(getNotification("456", "15 Jan 2013", false, NotificationCategory.CLIENT_ACTIONS,
                NotificationStatus.UNKNOWN, 0));
        notificationResponse.setNotification(notification);
        activeProfile = getProfile(JobRole.PARAPLANNER, "job id 1", "client1");

        when(notificationService.loadNotifications(any(ArrayList.class), any(DateTime.class),
                any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(notificationResponse.getNotification());
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(brokerService.getAdvisersForUser(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(getBrokerIdentifiers());
        when(brokerService.getAdviserBrokerUser(anyListOf(BrokerKey.class), any(ServiceErrors.class)))
                .thenReturn(getBrokerWrapperMap());
        List<NotificationDto> result = dtoService.search(key, criteriaList, serviceErrors);
        assertEquals(result.size(), 4);
        assertEquals(result.get(0).getId(), notificationResponse.getNotification().get(0).getNotificationId());
        assertEquals(result.get(0).getAccountName(), "Account ABC");
        assertEquals(result.get(0).getAccountNumber(), "12345");
        assertEquals(result.get(0).getProductName(), "product1");
      }

    private WrapAccount getWrapAccount(AccountStatus accountStatus) {
        WrapAccountImpl wrapAccount = new WrapAccountImpl();
        wrapAccount.setAccountKey(AccountKey.valueOf("123"));
        wrapAccount.setAccountNumber("12345");
        wrapAccount.setAccountName("Account ABC");
        wrapAccount.setAccountStatus(accountStatus);
        wrapAccount.setProductKey(productResponse.getProductKey());
        wrapAccount.setAdviserPositionId(BrokerKey.valueOf(brokerResponse.getBankReferenceId()));
        return wrapAccount;
    }

    private Product getProduct() {
        ProductImpl product = new ProductImpl();
        product.setProductKey(ProductKey.valueOf("12345"));
        product.setProductName("product1");
        return product;
    }

    private BrokerUser getAdviser() {
        return new BrokerUser() {
            @Override public Collection<BrokerRole> getRoles() {
                return null;
            }

            @Override public boolean isRegisteredOnline() {
                return false;
            }

            @Override public String getPracticeName() {
                return null;
            }

            @Override public String getEntityId() {
                return null;
            }

            @Override public DateTime getReferenceStartDate() {
                return null;
            }

            @Override public String getFirstName() {
                return "Demo";
            }

            @Override public String getMiddleName() {
                return null;
            }

            @Override public String getLastName() {
                return "Adviser";
            }

            @Override public Collection<AccountKey> getWrapAccounts() {
                return null;
            }

            @Override public Collection<ClientDetail> getRelatedPersons() {
                return null;
            }

            @Override public List<Email> getEmails() {
                return null;
            }

            @Override public List<Phone> getPhones() {
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

            @Override public DateTime getCloseDate() {
                return null;
            }

            @Override public String getFullName() {
                return null;
            }

            @Override public ClientType getClientType() {
                return null;
            }

            @Override public List<Address> getAddresses() {
                return null;
            }

            @Override public InvestorType getLegalForm() {
                return null;
            }

            @Override public ClientKey getClientKey() {
                return null;
            }

            @Override public void setClientKey(ClientKey personId) {

            }

            @Override public JobKey getJob() {
                return null;
            }

            @Override public String getProfileId() {
                return "id1";
            }

            @Override public String getBankReferenceId() {
                return "12345";
            }

            @Override public UserKey getBankReferenceKey() {
                return UserKey.valueOf(getBankReferenceId());
            }

            @Override public CISKey getCISKey() {
                return null;
            }

            @Override
            public List<TaxResidenceCountry> getTaxResidenceCountries() {
                // TODO Auto-generated method stub
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
    }

    private List<BrokerIdentifier> getBrokerIdentifiers() {

        BrokerIdentifierImpl brokerIdentifier1 = new BrokerIdentifierImpl();
        brokerIdentifier1.setKey(BrokerKey.valueOf("1234"));
        BrokerIdentifierImpl brokerIdentifier2 = new BrokerIdentifierImpl();
        brokerIdentifier2.setKey(BrokerKey.valueOf("12345"));
        List<BrokerIdentifier> brokerIdentifiers = Arrays.asList((BrokerIdentifier)brokerIdentifier1,
                (BrokerIdentifier)brokerIdentifier2);
        return brokerIdentifiers;
    }

    private Map<BrokerKey, BrokerWrapper> getBrokerWrapperMap() {
        Map<BrokerKey, BrokerWrapper> brokerMap = new HashMap<>();
        BrokerWrapper brokerWrapper1 =  new BrokerWrapperImpl(BrokerKey.valueOf("1234"), getAdviser(), true, "Sample DealerGrp");
        BrokerWrapper brokerWrapper2 =  new BrokerWrapperImpl(BrokerKey.valueOf("12345"), getAdviser(), true, "Sample DealerGrp 2");
        brokerMap.put(BrokerKey.valueOf("1234"), brokerWrapper1);
        brokerMap.put(BrokerKey.valueOf("12345"), brokerWrapper2);
        return brokerMap;
    }

    private BrokerWrapper getBrokerWrapper() {
        Broker broker = mock(Broker.class);
        BrokerWrapper brokerWrapper = mock(BrokerWrapper.class);
        when(brokerWrapper.getBrokerUser()).thenReturn(null);
        when(brokerWrapper.isDirectInvestment()).thenReturn(false);
        return brokerWrapper;
    }
}

