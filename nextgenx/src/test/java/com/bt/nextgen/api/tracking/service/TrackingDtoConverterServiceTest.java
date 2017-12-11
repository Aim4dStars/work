package com.bt.nextgen.api.tracking.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.draftaccount.FormDataConstants;
import com.bt.nextgen.api.draftaccount.model.ApplicationClientStatus;
import com.bt.nextgen.api.draftaccount.model.form.ClientApplicationFormFactory;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.ApprovalTypeEnum;
import com.bt.nextgen.api.tracking.model.PersonInfo;
import com.bt.nextgen.api.tracking.model.TrackingDto;
import com.bt.nextgen.config.ApplicationContextProvider;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingAccount;
import com.bt.nextgen.core.repository.OnboardingAccountRepository;
import com.bt.nextgen.core.repository.OnboardingApplicationStatus;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.bt.nextgen.core.repository.UserPreferenceRepositoryImpl;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationDocumentImpl;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import com.bt.nextgen.service.avaloq.accountactivation.AssociatedPersonImpl;
import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.btfin.panorama.core.security.avaloq.userinformation.PersonRelationship;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.btfin.panorama.core.security.integration.domain.Individual;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationContext;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TrackingDtoConverterServiceTest {

    @Mock
    private BrokerIntegrationService brokerIntegrationService;
    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private OnboardingAccountRepository onboardingAccountRepository;

    @Mock
    private InvestorStatusService investorStatusService;

    @Mock
    private AccountStatusService accountStatusService;

    @InjectMocks
    private TrackingDtoConverterService service;

    @Mock
    private UserPreferenceRepositoryImpl userPreferenceRepository;

    @Mock
    private ApplicationContext applicationContext;

    private ObjectMapper mapper;

    private ClientApplication clientApplication;


    private Map<String, ApplicationDocument> applicationDocumentsByOrderId;
    private final static String DUMMY_PRODUCT_ID = "PROD_ID";
    private final static String GCM_ID_ONE = "1";
    private final static String GCM_ID_TWO = "2";
    private final static String PERSON_NAME_JOE = "Joe";
    private final static String PERSON_NAME_SAM = "Sam";
    public static final String LAST_MODIFIED_ID = "M1";


    @Before
    public void setUp() throws Exception {
        //setup jsonObjectMapper for tests
        mapper = new JsonObjectMapper();
        applicationContext = mock(ApplicationContext.class);
        Mockito.when(applicationContext.getBean(eq("jsonObjectMapper"), any(Class.class))).thenReturn(mapper);
        Mockito.when(applicationContext.getBean(eq("jsonObjectMapper"))).thenReturn(mapper);
        ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider(null);
        applicationContextProvider.setApplicationContext(applicationContext);


        applicationDocumentsByOrderId = new HashMap<>();
        clientApplication = createClientApplication();
        clientApplication.setLastModifiedId(LAST_MODIFIED_ID);
        clientApplication.setAdviserPositionId("2");
        clientApplication.setLastModifiedAt(new DateTime());
        clientApplication.setOnboardingApplication(new OnBoardingApplication());
        clientApplication.setProductId(DUMMY_PRODUCT_ID);

        UserKey lastModifiedByUserKey = UserKey.valueOf("M1");
        BrokerUser lastModifiedByUser = mock(BrokerUser.class);
        when(lastModifiedByUser.getFirstName()).thenReturn("Phill");
        when(lastModifiedByUser.getLastName()).thenReturn("NotSoSmart");
        when(brokerIntegrationService.getBrokerUser(eq(lastModifiedByUserKey), any(ServiceErrors.class))).thenReturn(lastModifiedByUser);

        BrokerUser adviser = mock(BrokerUser.class);
        when(adviser.getFirstName()).thenReturn("John");
        when(adviser.getFirstName()).thenReturn("Smart");
        when(brokerIntegrationService.getAdviserBrokerUser(eq(BrokerKey.valueOf("2")), any(ServiceErrors.class))).thenReturn(adviser);
    }

    @Test
    public void convertToDto_shouldIncludeAssociatedPeopleWhoOnlyHaveTheRole_Signatory_InTheListOfInvestors() {
        mockClientApplicationWithRoles(Arrays.asList(InvestorRole.Signatory));
        when(accountStatusService.getStatusForAccountType(any(OnboardingApplicationStatus.class), any(ClientApplication.class),
                anyMap())).thenReturn(OnboardingApplicationStatus.processing); // must return non null

        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(dto.getInvestors().size(), is(2));
    }

    @Test
    public void convertToDto_shouldIncludeAssociatedPeopleWhoOnlyHaveTheRole_Secretary_InTheListOfInvestors() {
        mockClientApplicationWithRoles(Arrays.asList(InvestorRole.Secretary));
        when(accountStatusService.getStatusForAccountType(any(OnboardingApplicationStatus.class), any(ClientApplication.class),
                anyMap())).thenReturn(OnboardingApplicationStatus.processing); // must return non null

        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(dto.getInvestors().size(), is(2));
    }

    @Test
    public void convertToDto_shouldIncludeAssociatedPeopleWhoOnlyHaveTheRole_Trustee_InTheListOfInvestors() {
        mockClientApplicationWithRoles(Arrays.asList(InvestorRole.Trustee));
        when(accountStatusService.getStatusForAccountType(any(OnboardingApplicationStatus.class), any(ClientApplication.class),
                anyMap())).thenReturn(OnboardingApplicationStatus.processing); // must return non null

        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(dto.getInvestors().size(), is(2));
    }

    @Test
    public void convertToDto_shouldNotIncludeInvestorsWithEmptyRoles() {
        String avaloqOrderId = "MY ORDER ID";
        OnboardingApplicationKey onboardingApplicationKey = mockClientApplicationWithoutAssociatedPerson();
        AssociatedPerson joe = createAssociatedPerson("Super Name", "1", true, true, GCM_ID_ONE);
        joe.setPersonRel(null);
        AssociatedPerson sam = createAssociatedPerson("someName2", "2", true, true, GCM_ID_TWO);

        addPersonsToApplication(avaloqOrderId, onboardingApplicationKey, Arrays.asList(joe, sam), Arrays.asList(InvestorRole.Director));
        when(accountStatusService.getStatusForAccountType(any(OnboardingApplicationStatus.class), any(ClientApplication.class),
                anyMap())).thenReturn(OnboardingApplicationStatus.processing); // must return non null
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(dto.getInvestors().size(), is(1));
    }

    @Test
    public void convertToDto_shouldReturnInternalOnboardingApplicationIdIfExists() {
        OnboardingApplicationKey expectedOnboardingApplicationKey = mockClientApplicationWithoutAssociatedPerson();
        when(accountStatusService.getStatusForAccountType(any(OnboardingApplicationStatus.class), any(ClientApplication.class),
                anyMap())).thenReturn(OnboardingApplicationStatus.processing); // must return non null
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(dto.getOnboardingApplicationKey(), is(expectedOnboardingApplicationKey));
    }

    @Test
    public void convertToDto_shouldNotIncludeAssociatedPeopleWhoOnlyHaveTheRole_Member_InTheListOfInvestors() {
        String avaloqOrderId = "MY ORDER ID";
        OnboardingApplicationKey onboardingApplicationKey = mockClientApplicationWithoutAssociatedPerson();

        AssociatedPerson joe = createAssociatedPerson("Super Name", "1", true, true, GCM_ID_ONE);
        joe.setPersonRel(PersonRelationship.MBR);
        AssociatedPerson sam = createAssociatedPerson("someName2", "2", true, true, GCM_ID_TWO);

        addPersonsToApplication(avaloqOrderId, onboardingApplicationKey, Arrays.asList(joe, sam), Arrays.asList(InvestorRole.Director));
        when(accountStatusService.getStatusForAccountType(any(OnboardingApplicationStatus.class), any(ClientApplication.class),
                anyMap())).thenReturn(OnboardingApplicationStatus.processing); // must return non null
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(dto.getInvestors().size(), is(1));
    }

    @Test
    public void convertToDto_shouldNotIncludeAssociatedPeopleWhoOnlyHaveTheRole_Beneficiary_InTheListOfInvestors() {
        String avaloqOrderId = "MY ORDER ID";
        OnboardingApplicationKey onboardingApplicationKey = mockClientApplicationWithoutAssociatedPerson();

        AssociatedPerson joe = createAssociatedPerson("Super Name", "1", true, true, GCM_ID_ONE);
        joe.setPersonRel(PersonRelationship.BENEFICIARY);
        AssociatedPerson sam = createAssociatedPerson("someName2", "2", true, true, GCM_ID_TWO);

        addPersonsToApplication(avaloqOrderId, onboardingApplicationKey, Arrays.asList(joe, sam), Arrays.asList(InvestorRole.Director));
        when(accountStatusService.getStatusForAccountType(any(OnboardingApplicationStatus.class), any(ClientApplication.class),
                anyMap())).thenReturn(OnboardingApplicationStatus.processing); // must return non null
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(dto.getInvestors().size(), is(1));
    }

    @Test
    public void convertToDto_shouldIncludeAssociatedPeopleWhoHaveDirector_InTheListOfInvestors() {
        String avaloqOrderId = "MY ORDER ID";
        OnboardingApplicationKey onboardingApplicationKey = mockClientApplicationWithoutAssociatedPerson();

        PersonRelationship[] personRelationships = {PersonRelationship.DIRECTOR, PersonRelationship.AO, PersonRelationship.SECRETARY, PersonRelationship.SIGNATORY, PersonRelationship.TRUSTEE};
        List<AssociatedPerson> associatedPersons = new ArrayList<>();
        Map<String, OnboardingParty> partyHashMap = new HashMap<>();

        for (int i = 0; i < 5; i++) {
            String id = String.valueOf(i + 1);
            AssociatedPerson person = createAssociatedPerson("Some Name", id, true, true, id);
            person.setPersonRel(personRelationships[i]);
            associatedPersons.add(person);
            partyHashMap.put(id, new OnboardingParty());
        }

        ApplicationDocument appDoc = new ApplicationDocumentImpl();
        appDoc.setPersonDetails(associatedPersons);
        appDoc.setAppState(ApplicationStatus.PEND_ACCEPT);

        applicationDocumentsByOrderId.put(avaloqOrderId, appDoc);

        OnboardingAccount onboardingAccount = new OnboardingAccount();
        onboardingAccount.setAccountNumber("Account Number");
        when(onboardingAccountRepository.findByOnboardingApplicationId(onboardingApplicationKey)).thenReturn(onboardingAccount);

        Map<Long, Map<String, OnboardingParty>> onboardingApplicationIdByGcmPanMap = new HashMap<>();
        onboardingApplicationIdByGcmPanMap.put(1231l, partyHashMap);
        when(accountStatusService.getStatusForAccountType(any(OnboardingApplicationStatus.class), any(ClientApplication.class),
                anyMap())).thenReturn(OnboardingApplicationStatus.processing); // must return non null

        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), onboardingApplicationIdByGcmPanMap, null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(dto.getInvestors().size(), is(5));
    }

    @Test
    public void convertToDto_shouldSet_isApprover_toTrue_forAccountsWhichDoNeedToSignTNCs() {
        mockClientApplicationWithAvaloqApprovers(true, true, true);
        when(accountStatusService.getStatusForAccountType(any(OnboardingApplicationStatus.class), any(ClientApplication.class),
                anyMap())).thenReturn(OnboardingApplicationStatus.processing); // must return non null
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(),
                getTestPartyMap(1231), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        for (TrackingDto.Investor i : dto.getInvestors()) {
            assertThat(i.isApprover(), is(true));
        }
    }

    @Test
    public void convertToDto_shouldBuildAccountStatusBasedOnInvestorsThatAreApprovers() {
        mockClientApplicationWithNonApproverAndApprover();

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        when(accountStatusService.getAccountStatusByInvestorsStatuses(captor.capture())).thenReturn(OnboardingApplicationStatus.processing);
        when(accountStatusService.getStatusForAccountType(any(OnboardingApplicationStatus.class), any(ClientApplication.class),
                anyMap())).thenReturn(OnboardingApplicationStatus.processing); // must return non null

        service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        List<TrackingDto.Investor> capturedInvestor = (List<TrackingDto.Investor>) captor.getValue();
        assertThat(capturedInvestor.size(), is(1));
        assertThat(capturedInvestor.get(0).isApprover(), is(true));
    }

    @Test
    public void convertToDto_shouldSet_isApprover_toFalse_forAccountsWhichDoNotNeedToSignTNCs() {
        mockClientApplicationWithNonApprovers();
        when(accountStatusService.getStatusForAccountType(any(OnboardingApplicationStatus.class), any(ClientApplication.class),
                anyMap())).thenReturn(OnboardingApplicationStatus.processing); // must return non null

        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        for (TrackingDto.Investor i : dto.getInvestors()) {
            assertThat(i.isApprover(), is(false));
        }
    }

    @Test
    public void shouldConvertApplicationDtoIntoTrackingDto() {
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));
        TrackingDto trackingDto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(),
                getTestPartyMap(1231), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertNotNull(trackingDto);
    }

    @Test
    public void shouldProvideInvestorsWhenClientApplicationIsAssociatedWithSome() {
        mockClientApplicationWithAvaloqApprovers(true, true, true);
        when(accountStatusService.getStatusForAccountType(any(OnboardingApplicationStatus.class), any(ClientApplication.class),
                anyMap())).thenReturn(OnboardingApplicationStatus.processing); // must return non null

        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(dto.getInvestors(), hasSize(2));

        List<String> firstNames = Lambda.convert(dto.getInvestors(), new Converter<TrackingDto.Investor, String>() {
            @Override
            public String convert(TrackingDto.Investor s) {
                return s.getFirstName();
            }
        });
        assertThat(firstNames, IsIterableContainingInAnyOrder.containsInAnyOrder("Sam", "Joe"));
    }

    @Test
    public void convertToDto_shouldFilterOutInvestorsWithoutName() {
        AssociatedPerson joe = createAssociatedPerson(null, "1", false, true, GCM_ID_ONE);
        AssociatedPerson sam = createAssociatedPerson(PERSON_NAME_SAM, "2", false, true, GCM_ID_TWO);
        mockClientApplicationWithInvestors(Arrays.asList(joe, sam), Arrays.asList(InvestorRole.Director));
        when(accountStatusService.getStatusForAccountType(any(OnboardingApplicationStatus.class), any(ClientApplication.class),
                anyMap())).thenReturn(OnboardingApplicationStatus.processing); // must return non null

        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(dto.getInvestors(), hasSize(1));
        assertThat(dto.getInvestors().get(0).getFirstName(), is(PERSON_NAME_SAM));
    }


    @Test
    public void shouldProvideTrustTypeIfAccountHasTrust() {
        clientApplication = mock(ClientApplication.class);
        when(clientApplication.getAdviserPositionId()).thenReturn("2");
        when(clientApplication.getOnboardingApplication()).thenReturn(new OnBoardingApplication());
        when(clientApplication.getLastModifiedAt()).thenReturn(new DateTime());
        when(clientApplication.getProductId()).thenReturn(DUMMY_PRODUCT_ID);
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId)).thenReturn(OnboardingApplicationStatus.draft);

        Map<String, Object> formData = new HashMap<>();
        formData.put("trustType", "family");
        HashMap trustForm = new HashMap<String, Object>();
        trustForm.put("registeredAddress", "Some Address");
        trustForm.put("trusttype", "family");
        formData.put("trustdetails", trustForm);
        formData.put("accountType", "individualTrust");
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(formData);
        when(clientApplication.getClientApplicationForm()).thenReturn(form);

        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(dto.getTrustType(), is("family"));
    }

    @Test
    public void convertToDto_setPrimaryContact_whenIndividual() {
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
        .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));

        Map formData = new HashMap();
        Map person = new HashMap<>();
        person.put("firstname", "Lonely");
        person.put("lastname", "Investor");
        formData.put("investors", Arrays.asList(person));
        formData.put("accountType", "individual");
        formData.put("accountsettings", getAccountSettingsForInvestors(1));
        clientApplication.setFormData(formData);
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        final TrackingDto.Contact primaryContact = dto.getPrimaryContact();
        assertThat(primaryContact.getFirstName(), is("Lonely"));
        assertThat(primaryContact.getLastName(), is("Investor"));
    }

    @Test
    public void convertToDto_setPrimaryContact_whenJoint() {
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
        .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));
       Map formData = new HashMap();
        Map person1 = new HashMap<>();
        person1.put("firstname", "First");
        person1.put("lastname", "Joint");
        formData.put("investors", Arrays.asList(person1));
        formData.put("accountType", "joint");
        formData.put("accountsettings", getAccountSettingsForInvestors(1));
        clientApplication.setFormData(formData);
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        final TrackingDto.Contact primaryContact = dto.getPrimaryContact();
        assertThat(primaryContact.getFirstName(), is("First"));
        assertThat(primaryContact.getLastName(), is("Joint"));
    }

    @Test
    public void convertToDto_setPrimaryContact_whenCorporateSmsf() {
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));
        Map formData = new HashMap();
        Map person1 = new HashMap<>();
        person1.put("firstname", "First Corporate");
        person1.put("lastname", "Director");
        formData.put("directors", Arrays.asList(person1));
        formData.put("accountType", "corporateSMSF");
        formData.put("accountsettings", getAccountSettingsForInvestors(1));
        formData.put("shareholderandmembers", getShareholdersAndMembersForOne());
        clientApplication.setFormData(formData);

        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        final TrackingDto.Contact primaryContact = dto.getPrimaryContact();
        assertThat(primaryContact.getFirstName(), is("First Corporate"));
        assertThat(primaryContact.getLastName(), is("Director"));
    }

    @Test
    public void convertToDto_setPrimaryContact_whenCorporateTrust() {
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));
        Map formData = new HashMap();
        Map person1 = new HashMap<>();
        person1.put("firstname", "First Trust");
        person1.put("lastname", "Director");
        formData.put("directors", Arrays.asList(person1));
        formData.put("accountType", "corporateTrust");
        formData.put("accountsettings", getAccountSettingsForInvestors(1));
        formData.put("shareholderandmembers", getShareholdersAndMembersForOne());
        clientApplication.setFormData(formData);

        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        final TrackingDto.Contact primaryContact = dto.getPrimaryContact();
        assertThat(primaryContact.getFirstName(), is("First Trust"));
        assertThat(primaryContact.getLastName(), is("Director"));
    }

    @Test
    public void convertToDto_setPrimaryContact_whenIndividualSmsf() {
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));
        Map formData = new HashMap();
        Map person1 = new HashMap<>();
        person1.put("firstname", "Individual");
        person1.put("lastname", "Individualovich");
        formData.put("trustees", Arrays.asList(person1));
        formData.put("accountType", "IndividualSmsf");


        formData.put("accountsettings", getAccountSettingsForInvestors(1));
        clientApplication.setFormData(formData);

        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        final TrackingDto.Contact primaryContact = dto.getPrimaryContact();
        assertThat(primaryContact.getFirstName(), is("Individual"));
        assertThat(primaryContact.getLastName(), is("Individualovich"));
    }

    @Test
    public void convertToDto_setPrimaryContact_whenNewIndividualSmsf() {
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));
        Map formData = new HashMap();
        Map person1 = new HashMap<>();
        person1.put("firstname", "newSMSF");
        person1.put("lastname", "Individual");
        formData.put("trustees", Arrays.asList(person1));
        formData.put("accountType", "NewIndividualSmsf");


        formData.put("accountsettings", getAccountSettingsForInvestors(1));
        clientApplication.setFormData(formData);

        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        final TrackingDto.Contact primaryContact = dto.getPrimaryContact();
        assertThat(primaryContact.getFirstName(), is("newSMSF"));
        assertThat(primaryContact.getLastName(), is("Individual"));
    }

    @Test
    public void convertToDto_setPrimaryContact_whenIndividualTrust() {
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));
        Map formData = new HashMap();
        Map person1 = new HashMap<>();
        person1.put("firstname", "Person");
        person1.put("lastname", "Personov");
        formData.put("trustees", Arrays.asList(person1));
        formData.put("accountType", "IndividualTrust");
        formData.put("accountsettings", getAccountSettingsForInvestors(1));
        clientApplication.setFormData(formData);

        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        final TrackingDto.Contact primaryContact = dto.getPrimaryContact();
        assertThat(primaryContact.getFirstName(), is("Person"));
        assertThat(primaryContact.getLastName(), is("Personov"));
    }

    @Test
    public void convertToDto_shouldSetPrimaryContactWhenAccountTypeisCompany() throws Exception {
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));
        Map formData = new HashMap();
        Map person1 = new HashMap<>();
        person1.put("firstname", "Person");
        person1.put("lastname", "Personov");
        formData.put("directors", Arrays.asList(person1));
        formData.put("accountType", "company");
        formData.put("accountsettings", getAccountSettingsForInvestors(1));
        clientApplication.setFormData(formData);

        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        final TrackingDto.Contact primaryContact = dto.getPrimaryContact();
        assertThat(primaryContact.getFirstName(), is("Person"));
        assertThat(primaryContact.getLastName(), is("Personov"));
    }

    @Test
    public void convertToDto_setPrimaryContact_whenMoreThenOneContact() {
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));
        Map<String, Object> formData = new HashMap<>();
        Map<String, Object> person1 = new HashMap<>();
        // "mobile":{"value":"002"},"email":{"value":"first@mail.au"}
        Map<String, String> email1 = new HashMap<>();
        email1.put("value", "first@mail.au");
        person1.put("email", email1);

        person1.put("firstname", "First");
        person1.put("lastname", "Joint");

        Map<String, Object> person2 = new HashMap<>();
        person2.put("firstname", "Second");
        Map<String, String> email2 = new HashMap<>();
        email2.put("value", "second@mail.au");
        person2.put("email", email2);
        Map<String, String> mobile2 = new HashMap<>();
        mobile2.put("value", "002");
        person2.put("mobile", mobile2);


        HashMap<String, Object> accountSettings = new HashMap<>();

        List<Map<String, Object>> investorsSettingsList = new ArrayList<Map<String, Object>>();
        Map<String, Object> investorSetting1 = new HashMap<>();
        investorSetting1.put("paymentSetting", "nopayments");
        investorSetting1.put("isApprover", "true");

        Map<String, Object> investorSetting2 = new HashMap<>();
        investorSetting2.put("paymentSetting", "linkedaccountsonly");
        investorSetting2.put("isApprover", "true");

        investorsSettingsList.add(investorSetting1);
        investorsSettingsList.add(investorSetting2);
        accountSettings.put("investorAccountSettings", investorsSettingsList);
        accountSettings.put("primarycontact", "1");
        accountSettings.put("professionalspayment", "linkedaccountsonly");
        formData.put("trustees", Arrays.asList(person1, person2));
        formData.put("accountType", "IndividualTrust");
        formData.put("accountsettings", accountSettings);
        clientApplication.setFormData(formData);

        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);

        final TrackingDto.Contact primaryContact = dto.getPrimaryContact();
        assertThat(primaryContact.getFirstName(), is("Second"));
        assertThat(primaryContact.getContacts().get(0).getMethod(), is(TrackingDto.ContactMethodType.MOBILE));
        assertThat(primaryContact.getContacts().get(1).getMethod(), is(TrackingDto.ContactMethodType.EMAIL));
        assertThat(primaryContact.getContacts().get(0).getValue(), is("002"));
        assertThat(primaryContact.getContacts().get(1).getValue(), is("second@mail.au"));
    }

    private HashMap<String, Object> getShareholdersAndMembersForOne() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("members", Arrays.asList("0"));
        map.put("shareholders", Arrays.asList("0"));
        return map;
    }

    @Test
    public void shouldHaveLastModifiedBy() throws Exception {

        BrokerUser brokerUser = mock(BrokerUser.class);
        when(brokerUser.getFirstName()).thenReturn("Phill");
        when(brokerUser.getLastName()).thenReturn("NotSoSmart");

        clientApplication.setAdviserPositionId("ID1");
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));

        HashMap<String, BrokerUser> lastModifiedIdMap = new HashMap<>();
        lastModifiedIdMap.put(LAST_MODIFIED_ID, brokerUser);
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), lastModifiedIdMap, null);
        assertEquals("Phill", dto.getLastModifiedBy().getFirstName());
        assertEquals("NotSoSmart", dto.getLastModifiedBy().getLastName());
    }

    @Test
    public void shouldHaveBlankLastModifiedByIfBrokerUserIsNull() throws Exception {
        clientApplication.setLastModifiedId("adviser2");
        when(brokerIntegrationService.getBrokerUser(eq(UserKey.valueOf("adviser2")), any(ServiceErrors.class))).thenReturn(null);
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));

        TrackingDto trackingDto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);

        PersonInfo lastModifiedBy = trackingDto.getLastModifiedBy();
        assertThat(lastModifiedBy.getFirstName(), is(""));
        assertThat(lastModifiedBy.getLastName(), is(""));
    }


    @Test
    public void shouldHaveAccountTypeCorrectlySet() {
        Map<String, Object> formData = new HashMap<>();
        formData.put("accountType", "joint");
        formData.put("accountsettings", new HashMap<>());
        clientApplication.setFormData(formData);
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));

        assertEquals(service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null).getAccountType(), "joint");
    }

    @Test
    public void shouldHaveProductNameSet() {
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));
        assertNotNull(service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null).getProductName());
    }

    @Test
    public void shouldHaveMoreDetailsForAdviserIfDoFetchAdviserDetailsFlagSetAsTrue() throws Exception {
        BrokerUser brokerUser = mock(BrokerUser.class);
        Phone businesPhone = createPhone("33336666", "02", AddressMedium.BUSINESS_TELEPHONE);
        Phone primaryMobile = createPhone("0444473322", "", AddressMedium.MOBILE_PHONE_PRIMARY);
        List<Phone> phoneNumbers = Arrays.asList(businesPhone, primaryMobile);
        Email emailPrimary = createEmail("primary@email.com", AddressMedium.EMAIL_PRIMARY);
        List<Email> emails = Arrays.asList(emailPrimary);

        when(brokerIntegrationService.getAdviserBrokerUser(eq(BrokerKey.valueOf("adviser1")), any(ServiceErrors.class))).thenReturn(brokerUser);
        when(brokerUser.getFirstName()).thenReturn("Lolly");
        when(brokerUser.getLastName()).thenReturn("Pop");
        when(brokerUser.getPhones()).thenReturn(phoneNumbers);
        when(brokerUser.getEmails()).thenReturn(emails);

        clientApplication.setAdviserPositionId("adviser1");

        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));

        HashMap<String, PersonInfo> adviserPositionIdMap = new HashMap<>();
        adviserPositionIdMap.put("adviser1", new PersonInfo("Lolly", "Pop"));
        TrackingDto trackingDto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, adviserPositionIdMap, new HashMap<String, BrokerUser>(), null);

        PersonInfo adviser = trackingDto.getAdviser();
        assertThat(adviser.getFirstName(), is("Lolly"));
        assertThat(adviser.getLastName(), is("Pop"));
    }

    @Test
    public void aSubmittedDraftAccountWithServerFailureShouldHaveDtoWithStatusProcessing() {
        clientApplication.markSubmitted();
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));

        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(dto.getStatus(), equalTo(OnboardingApplicationStatus.processing));
    }

    @Test
    public void searchShouldReturnListOfDraftAccountDtoWithFailedStatuses() {
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.failed);
        MatcherAssert.assertThat(
                service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(),
                        getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null).getStatus(),
                equalTo(OnboardingApplicationStatus.failed));
    }

   @Test
    public void convertToDtoShouldSetStatusToProcessingWhenOrderNotYetInAvaloqPostSubmission() {
        // This is a transient condition that occurs after submission, before ICC calls ABS
        clientApplication.markSubmitted();
        OnBoardingApplication onboardingApplication = mock(OnBoardingApplication.class);
        OnboardingApplicationKey onboardingApplicationKey = OnboardingApplicationKey.valueOf(10);
        when(onboardingApplication.getKey()).thenReturn(onboardingApplicationKey);
        clientApplication.setOnboardingApplication(onboardingApplication);

        when(onboardingAccountRepository.findByOnboardingApplicationId(onboardingApplicationKey)).thenThrow(NoResultException.class);
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId)).thenReturn(null);
        when(accountStatusService.getAccountStatusByInvestorsStatuses(any(List.class))).thenReturn(OnboardingApplicationStatus.processing);
        when(accountStatusService.getStatusForAccountType(OnboardingApplicationStatus.processing, clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.processing);


        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);

        assertThat(dto.getStatus(), equalTo(OnboardingApplicationStatus.processing));
        assertThat(dto.getDisplayName(), is("John Smith"));
    }

    @Test
    public void searchShouldReturnListOfDraftAccountDtoWithDraftStatuses() {
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));
        MatcherAssert
                .assertThat(
                        service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(),
                                getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null).getStatus(),
                        equalTo(OnboardingApplicationStatus.draft));
    }

    @Test
    public void convertToDtoShouldSetStatusToAwaitingApprovalAndSetInvestors() {
        mockClientApplicationWithAvaloqApprovers(false, false, true);
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId)).thenReturn(null);
        when(accountStatusService.getAccountStatusByInvestorsStatuses(any(List.class))).thenReturn(OnboardingApplicationStatus.awaitingApproval);
        when(accountStatusService.getStatusForAccountType(OnboardingApplicationStatus.awaitingApproval, clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.awaitingApproval);
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        MatcherAssert.assertThat(dto.getStatus(), equalTo(OnboardingApplicationStatus.awaitingApproval));
        MatcherAssert.assertThat(dto.getInvestors(), hasSize(2));

        verify(accountStatusService, times(1)).getApplicationStatus(clientApplication, applicationDocumentsByOrderId);
        verify(accountStatusService, times(1)).getAccountStatusByInvestorsStatuses(any(List.class));
    }

    @Test
    public void convertToDtoShouldSetStatusToAwaitingApprovalOfflineAndSetInvestors() {
        mockClientApplicationWithAvaloqApprovers(false, false, true);
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId)).thenReturn(null);
        when(accountStatusService.getAccountStatusByInvestorsStatuses(any(List.class))).thenReturn(OnboardingApplicationStatus.awaitingApproval);
        clientApplication.getOnboardingApplication().setOffline(true);
        when(accountStatusService.getStatusForAccountType(OnboardingApplicationStatus.awaitingApproval, clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.awaitingApprovalOffline);

        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        MatcherAssert.assertThat(dto.getStatus(), equalTo(OnboardingApplicationStatus.awaitingApprovalOffline));
        MatcherAssert.assertThat(dto.getInvestors(), hasSize(2));

        verify(accountStatusService, times(1)).getApplicationStatus(clientApplication, applicationDocumentsByOrderId);
        verify(accountStatusService, times(1)).getAccountStatusByInvestorsStatuses(any(List.class));
    }

    @Test
    public void convertToDtoShouldSetStatusToAwaitingApprovalIfAnyOfRegisteredApproverEmailSendIsFailed() {
        mockClientApplicationWithAvaloqApprovers(false, true, true);
        when(investorStatusService.getInvestorStatus(anyLong(), any(AssociatedPerson.class), any(OnboardingParty.class)))
                .thenReturn(ApplicationClientStatus.AWAITING_APPROVAL).thenReturn(ApplicationClientStatus.APPROVED);

        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId)).thenReturn(null);

        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        when(accountStatusService.getAccountStatusByInvestorsStatuses(listArgumentCaptor.capture())).thenReturn(OnboardingApplicationStatus.awaitingApproval);
        when(accountStatusService.getStatusForAccountType(OnboardingApplicationStatus.awaitingApproval, clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.awaitingApproval);

        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);

        List<TrackingDto.Investor> investors = listArgumentCaptor.getValue();
        assertThat(investors.size(), is(2));
        assertThat(investors.get(0).getStatus(), is(ApplicationClientStatus.AWAITING_APPROVAL));
        assertThat(investors.get(1).getStatus(), is(ApplicationClientStatus.APPROVED));
        assertThat(dto.getStatus(), equalTo(OnboardingApplicationStatus.awaitingApproval));
        assertThat(dto.getInvestors().size(), is(2));
    }

    @Test
    public void convertToDtoShouldSetStatusToFailedEmailIfAnyOfRegisteredApproverEmailSendIsFailed() {
        mockClientApplicationWithAvaloqApprovers(false, true, false);
        when(investorStatusService.getInvestorStatus(anyLong(), any(AssociatedPerson.class), any(OnboardingParty.class)))
                .thenReturn(ApplicationClientStatus.FAILED_EMAIL);

        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId)).thenReturn(null);

        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        when(accountStatusService.getAccountStatusByInvestorsStatuses(listArgumentCaptor.capture())).thenReturn(OnboardingApplicationStatus.failedEmail);
        when(accountStatusService.getStatusForAccountType(OnboardingApplicationStatus.failedEmail, clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.failedEmail);

        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);

        List<TrackingDto.Investor> investors = listArgumentCaptor.getValue();
        assertThat(investors.size(), is(2));
        assertThat(investors.get(0).getStatus(), is(ApplicationClientStatus.FAILED_EMAIL));
        assertThat(investors.get(1).getStatus(), is(ApplicationClientStatus.FAILED_EMAIL));
        assertThat(dto.getStatus(), equalTo(OnboardingApplicationStatus.failedEmail));
        assertThat(dto.getInvestors().size(), is(2));
    }

    @Test
    public void convertToDtoShouldReturnAccountDisplayNameForDraftStatus() throws IOException {
        clientApplication.setOnboardingApplication(null);
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        ;

        assertThat(dto.getStatus(), is(OnboardingApplicationStatus.draft));
        assertThat(dto.getDisplayName(), is("John Smith"));
    }

    @Test
    public void shouldHaveProductName() {
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(),
                getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(dto.getProductName(), is("26155861855125eed76c0bc14a54465949551bb3a34ee4217c14ff63fbe1c773"));
        assertThat(dto.getParentProductName(), is("CMA"));
    }

    @Test
    public void shouldSetAccountDisplayNameFromFormDataIfNoInvestorsInAvaloq() {
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId)).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus());
            }
        });
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(dto.getDisplayName(), is("John Smith"));
    }

    @Test
    public void shouldSetDisplayNameFromFormDataForCorporateSMSFWhenDraft() {
        HashMap<String, Object> formDataAsMap = new HashMap<>();
        HashMap<String, Object> smsfDetails = new HashMap<>();
        smsfDetails.put("smsfname", "SMITH SMSF");
        formDataAsMap.put("smsfdetails", smsfDetails);
        formDataAsMap.put("accountType", "corporateSMSF");
        formDataAsMap.put("accountsettings", new HashMap<>());
        clientApplication.setFormData(formDataAsMap);
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(dto.getDisplayName(), is("SMITH SMSF"));
    }

    @Test
    public void shouldSetApprovalTypeAsDefault() {
        when(accountStatusService.getStatusForAccountType(any(OnboardingApplicationStatus.class), any(ClientApplication.class),
                anyMap())).thenReturn(OnboardingApplicationStatus.processing); // must return non null
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(dto.getApprovalType(), is(ApprovalTypeEnum.ONLINE));
    }

    @Test
    public void shouldSetOrderIdWhenPresent() {
        OnboardingApplicationKey onboardingApplicationKey = OnboardingApplicationKey.valueOf(1231l);
        OnBoardingApplication onboardingApplication = createOnBoardingApplication("1234567", onboardingApplicationKey);
        clientApplication.setOnboardingApplication(onboardingApplication);
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(dto.getOrderId(), is("1234567"));
    }
    @Test
    public void shouldSetOrderIdAsNull() {
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(dto.getOrderId(), isEmptyOrNullString());
    }

    @Test
     public void shouldSetApprovalTypeAsOffline() {
        OnBoardingApplication onboardingApplication = mock(OnBoardingApplication.class);
        when(onboardingApplication.isOffline()).thenReturn(true);
        clientApplication.setOnboardingApplication(onboardingApplication);
        when(accountStatusService.getStatusForAccountType(any(OnboardingApplicationStatus.class), any(ClientApplication.class),
                anyMap())).thenReturn(OnboardingApplicationStatus.processing); // must return non null

        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(dto.getApprovalType(), is(ApprovalTypeEnum.OFFLINE));
    }

    @Test
    public void shouldSetPrimaryContact() throws IOException {
        final Map<String, Object> formData = getMinimalFormDataForSingleInvestor("individual");
        clientApplication.setFormData(formData);
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(dto.getPrimaryContact().getFirstName(), is("John"));
        assertThat(dto.getPrimaryContact().getLastName(), is("NotSoSmart"));

        List<TrackingDto.ContactMethod> contacts = dto.getPrimaryContact().getContacts();
        assertThat(contacts.get(1).getMethod(), is(TrackingDto.ContactMethodType.EMAIL));
        assertThat(contacts.get(1).getValue(), is("john.not.so.smart@gmail.com"));
    }

    @Test
    public void shouldSetPreferredContactForIndividualAccount() throws IOException {
        final Map<String, Object> formData = getMinimalFormDataForSingleInvestor("individual");
        clientApplication.setFormData(formData);
        when(accountStatusService.getApplicationStatus(clientApplication, applicationDocumentsByOrderId))
                .thenReturn(OnboardingApplicationStatus.convertFromClientApplicationStatus(clientApplication.getStatus()));
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(dto.getPrimaryContact().getFirstName(), is("John"));
        assertThat(dto.getPrimaryContact().getLastName(), is("NotSoSmart"));

        List<TrackingDto.ContactMethod> contacts = dto.getPrimaryContact().getContacts();
        assertThat(contacts.get(1).getMethod(), is(TrackingDto.ContactMethodType.EMAIL));
        assertThat(contacts.get(1).getValue(), is("john.not.so.smart@gmail.com"));
    }

    @Test
    public void shouldFetchTheLastModifiedAsIs() throws IOException {
        Map<String, Object> formData = new HashMap<>();
        formData.put("accountType", "individual");
        formData.put(FormDataConstants.FIELD_APPLICATION_ORIGIN, "WestpacLive");
        formData.put(FormDataConstants.FIELD_ADVICE_TYPE, "NoAdvice");
        String investorJson = "[\n" +
                "    {\n" +
                "        \"title\": \"mr\",\n" +
                "        \"firstname\": \"John\",\n" +
                "        \"email\": {\n" +
                "            \"value\": \"john.not.so.smart@gmail.com\"\n" +
                "        },\n" +
                "        \"lastname\": \"NotSoSmart\"\n" +
                "    }\n" +
                "]";

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> investors = mapper.readValue(investorJson, new TypeReference<List<Map<String, Object>>>() {
        });
        formData.put("investors", investors);
        clientApplication.setFormData(formData);
        when(accountStatusService.getStatusForAccountType(any(OnboardingApplicationStatus.class), any(ClientApplication.class),
                anyMap())).thenReturn(OnboardingApplicationStatus.processing); // must return non null
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        final PersonInfo lastModifiedBy = dto.getLastModifiedBy();
        assertThat(lastModifiedBy.getFirstName(), is("M1"));
        assertThat(lastModifiedBy.getLastName(), is(""));
    }

    @Test
    public void convertShouldReturnTheBpIdForAnAccountIfSubmittedSuccessfully() throws IOException {
        Map<String, ApplicationDocument> applicationDocuments = new HashMap<>();
        ApplicationDocument applicationDocument = mock(ApplicationDocument.class);
        when(applicationDocument.getBpid()).thenReturn(AccountKey.valueOf("orderId"));
        when(applicationDocument.getPersonDetails()).thenReturn(new ArrayList<AssociatedPerson>());
        applicationDocuments.put("orderId", applicationDocument);

        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        when(onBoardingApplication.getKey()).thenReturn(OnboardingApplicationKey.valueOf(123l));
        when(onBoardingApplication.getAvaloqOrderId()).thenReturn("orderId");

        Map<String, Object> formData = new HashMap<>();
        formData.put("accountType", "individual");
        formData.put(FormDataConstants.FIELD_APPLICATION_ORIGIN, "WestpacLive");
        formData.put(FormDataConstants.FIELD_ADVICE_TYPE, "NoAdvice");
        String investorJson = "[\n" +
                "    {\n" +
                "        \"title\": \"mr\",\n" +
                "        \"firstname\": \"John\",\n" +
                "        \"email\": {\n" +
                "            \"value\": \"john.not.so.smart@gmail.com\"\n" +
                "        },\n" +
                "        \"lastname\": \"NotSoSmart\"\n" +
                "    }\n" +
                "]";

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> investors = mapper.readValue(investorJson, new TypeReference<List<Map<String, Object>>>() {
        });
        formData.put("investors", investors);
        clientApplication.setFormData(formData);
        clientApplication.setOnboardingApplication(onBoardingApplication);
        when(accountStatusService.getStatusForAccountType(any(OnboardingApplicationStatus.class), any(ClientApplication.class),
                anyMap())).thenReturn(OnboardingApplicationStatus.processing); // must return non null

        TrackingDto dto = service.convertToDto(clientApplication, applicationDocuments, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertThat(EncodedString.toPlainText(dto.getEncryptedBpId()), is("orderId"));
    }

    @Test
    public void shouldReturnTrackingDtoForSuperAccumulationAccount() throws IOException {
        final Map<String, Object> formData = getMinimalFormDataForSingleInvestor("superAccumulation");
        clientApplication.setFormData(formData);
        when(accountStatusService.getStatusForAccountType(any(OnboardingApplicationStatus.class), any(ClientApplication.class),
                anyMap())).thenReturn(OnboardingApplicationStatus.processing); // must return non null
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertNotNull(dto);
        assertThat(dto.getAccountType(), is("superAccumulation"));
    }

    @Test
    public void shouldReturnTrackingDtoForSuperPensionAccount() throws IOException {
        final Map<String, Object> formData = getMinimalFormDataForSingleInvestor("superPension");
        clientApplication.setFormData(formData);
        when(accountStatusService.getStatusForAccountType(any(OnboardingApplicationStatus.class), any(ClientApplication.class),
                anyMap())).thenReturn(OnboardingApplicationStatus.processing); // must return non null
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertNotNull(dto);
        assertThat(dto.getAccountType(), is("superPension"));
    }

    @Test
    public void testAccountId_withEncodedId() {
        clientApplication.setOnboardingApplication(getOnboardingApplication(1234, null));
        Map<Long, OnboardingAccount> onboardingAccountByApplicationid = getOnboardingAccounts();
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), onboardingAccountByApplicationid, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertEquals(dto.getAccountId(), "12345678");
        assertEquals(EncodedString.toPlainText(dto.getEncodedAccountId()), "12345678");
    }

    @Test
    public void test_withNoAccountId_withNoEncodedId() {
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertNull(dto.getAccountId());
        assertNull(dto.getEncodedAccountId());
    }

    @Test
    public void testWithEncodedBpId() {
        clientApplication.setOnboardingApplication(getOnboardingApplication(1234, "1234"));
        Map<String, ApplicationDocument> applicationDocumentsByOrderId = getApplicationDocuments();
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertEquals(EncodedString.toPlainText(dto.getEncryptedBpId()), "12345678");
    }

    @Test
    public void testwithNoEncodedBpId() {
        TrackingDto dto = service.convertToDto(clientApplication, applicationDocumentsByOrderId, getTestProductMap(), getTestPartyMap(1231l), null, new HashMap<String, PersonInfo>(), new HashMap<String, BrokerUser>(), null);
        assertNull(dto.getAccountId());
        assertNull(dto.getEncodedAccountId());
    }

    private Map<Long, OnboardingAccount> getOnboardingAccounts() {
        Map<Long, OnboardingAccount> onboardingAccount = new HashMap<>();
        onboardingAccount.put(Long.valueOf(1234), getOnboardingAccount("12345678"));
        return onboardingAccount;
    }

    private OnboardingAccount getOnboardingAccount(String accountNumber) {
        OnboardingAccount onboardingAccount = new OnboardingAccount();
        onboardingAccount.setAccountNumber(accountNumber);
        return onboardingAccount;
    }

    private OnBoardingApplication getOnboardingApplication(long id, String avaloqOrderId) {
        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        when(onBoardingApplication.getKey()).thenReturn(OnboardingApplicationKey.valueOf(id));
        when(onBoardingApplication.getAvaloqOrderId()).thenReturn(avaloqOrderId);
        return onBoardingApplication;
    }

    private Map<String, ApplicationDocument> getApplicationDocuments() {
        Map<String, ApplicationDocument> applicationDocuments = new HashMap<>();
        applicationDocuments.put("1234", getApplicationDocument("12345678"));
        return applicationDocuments;
    }

    private ApplicationDocument getApplicationDocument(String bpId) {
        ApplicationDocument applicationDocument = mock(ApplicationDocument.class);
        when(applicationDocument.getBpid()).thenReturn(AccountKey.valueOf(bpId));
        return applicationDocument;
    }

    private OnboardingApplicationKey mockClientApplicationWithoutAssociatedPerson() {
        String avaloqOrderId = "MY ORDER ID";
        OnboardingApplicationKey onboardingApplicationKey = OnboardingApplicationKey.valueOf(1231l);
        OnBoardingApplication onboardingApplication = createOnBoardingApplication(avaloqOrderId, onboardingApplicationKey);
        clientApplication.setOnboardingApplication(onboardingApplication);

        return onboardingApplicationKey;
    }

    private void mockClientApplicationWithNonApprovers() {
        AssociatedPerson joe = createAssociatedPerson(PERSON_NAME_JOE, "1", false, false, GCM_ID_ONE);
        joe.setHasToAcceptTnC(false);
        AssociatedPerson sam = createAssociatedPerson(PERSON_NAME_SAM, "2", false, false, GCM_ID_TWO);
        sam.setHasToAcceptTnC(false);
        mockClientApplicationWithInvestors(Arrays.asList(joe, sam), Arrays.asList(InvestorRole.Director));
    }

    private void mockClientApplicationWithAvaloqApprovers(boolean hasApprovedTncInvestor1, boolean hasApprovedTncInvestor2, boolean isRegistered) {
        AssociatedPerson joe = createAssociatedPerson(PERSON_NAME_JOE, "1", isRegistered, true, GCM_ID_ONE);
        joe.setHasApprovedTnC(hasApprovedTncInvestor1);
        joe.setPersonRel(PersonRelationship.DIRECTOR);
        AssociatedPerson sam = createAssociatedPerson(PERSON_NAME_SAM, "2", isRegistered, true, GCM_ID_TWO);
        sam.setHasApprovedTnC(hasApprovedTncInvestor2);
        sam.setPersonRel(PersonRelationship.DIRECTOR);

        mockClientApplicationWithInvestors(Arrays.asList(joe, sam), Arrays.asList(InvestorRole.Director));
    }


    private void mockClientApplicationWithNonApproverAndApprover() {
        AssociatedPerson joe = createAssociatedPerson(PERSON_NAME_JOE, "1", false, false, GCM_ID_ONE);
        AssociatedPerson sam = createAssociatedPerson(PERSON_NAME_SAM, "2", false, true, GCM_ID_TWO);
        mockClientApplicationWithInvestors(Arrays.asList(joe, sam), Arrays.asList(InvestorRole.Director));

    }

    private void mockClientApplicationWithRoles(List<InvestorRole> roles) {
        AssociatedPerson joe = createAssociatedPerson(PERSON_NAME_JOE, "1", false, true, GCM_ID_ONE);
        AssociatedPerson sam = createAssociatedPerson(PERSON_NAME_SAM, "2", false, true, GCM_ID_TWO);
        mockClientApplicationWithInvestors(Arrays.asList(joe, sam), roles);
    }

    private Map<Long, Map<String, OnboardingParty>> getTestPartyMap(long onboardingApplicationId) {
        Map<String, OnboardingParty> partyHashMap = new HashMap<>();
        partyHashMap.put(GCM_ID_ONE, new OnboardingParty());
        partyHashMap.put(GCM_ID_TWO, new OnboardingParty());

        Map<Long, Map<String, OnboardingParty>> onboardingApplicationIdByGcmPanMap = new HashMap<>();
        onboardingApplicationIdByGcmPanMap.put(onboardingApplicationId, partyHashMap);
        return onboardingApplicationIdByGcmPanMap;
    }

    private void mockClientApplicationWithInvestors(List<AssociatedPerson> associatedPersons, List<InvestorRole> roles) {
        String avaloqOrderId = "MY ORDER ID";
        OnboardingApplicationKey onboardingApplicationKey = OnboardingApplicationKey.valueOf(1231l);
        OnBoardingApplication onboardingApplication = createOnBoardingApplication(avaloqOrderId, onboardingApplicationKey);
        clientApplication.setOnboardingApplication(onboardingApplication);
        clientApplication.setProductId(DUMMY_PRODUCT_ID);
        addPersonsToApplication(avaloqOrderId, onboardingApplicationKey, associatedPersons, roles);
    }

    private Map<ProductKey, Product> getTestProductMap() {
        Map<ProductKey, Product> partyHashMap = new HashMap<>();
        ProductKey prodKey = ProductKey.valueOf(DUMMY_PRODUCT_ID);
        ProductImpl product = new ProductImpl();
        product.setProductName("26155861855125eed76c0bc14a54465949551bb3a34ee4217c14ff63fbe1c773");
        product.setParentProductName("CMA");
        product.setProductKey(prodKey);
        partyHashMap.put(prodKey, product);
        return partyHashMap;
    }

    private void addPersonsToApplication(String avaloqOrderId, OnboardingApplicationKey onboardingApplicationKey, List<AssociatedPerson> investors, List<InvestorRole> roles) {
        ApplicationDocument appDoc = new ApplicationDocumentImpl();
        appDoc.setPersonDetails(investors);
        appDoc.setAppState(ApplicationStatus.PEND_ACCEPT);

        applicationDocumentsByOrderId.put(avaloqOrderId, appDoc);

        OnboardingAccount onboardingAccount = new OnboardingAccount();
        onboardingAccount.setAccountNumber("Account Number");
        when(onboardingAccountRepository.findByOnboardingApplicationId(onboardingApplicationKey)).thenReturn(onboardingAccount);
    }

    private OnBoardingApplication createOnBoardingApplication(String avaloqOrderId, OnboardingApplicationKey onboardingApplicationKey) {
        OnBoardingApplication onboardingApplication = mock(OnBoardingApplication.class);
        when(onboardingApplication.getKey()).thenReturn(onboardingApplicationKey);
        when(onboardingApplication.getAvaloqOrderId()).thenReturn(avaloqOrderId);
        return onboardingApplication;
    }


    private ClientApplication createClientApplication() throws NoSuchFieldException, IllegalAccessException, IOException {
        ClientApplication clientApplication = new ClientApplication();
        clientApplication.setAdviserPositionId("adviser1");
        clientApplication.setLastModifiedAt(new DateTime());
        Map<String, Object> formData = new HashMap<>();
        formData.put("accountType", "individual");

        String investorJson = "[\n" +
                "    {\n" +
                "        \"title\": \"mr\",\n" +
                "        \"firstname\": \"John\",\n" +
                "        \"middlename\": \"Doe\",\n" +
                "        \"email\": {\n" +
                "            \"value\": \"test@example.com\"\n" +
                "        },\n" +
                "        \"lastname\": \"Smith\"\n" +
                "    }\n" +
                "]";

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> investors = mapper.readValue(investorJson, new TypeReference<List<Map<String, Object>>>() {
        });
        formData.put("investors", investors);
        formData.put("accountsettings", getAccountSettingsForInvestors(1));
        clientApplication.setFormData(formData);

        Field idField = ClientApplication.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(clientApplication, 1L);

        return clientApplication;
    }

    private AssociatedPerson createAssociatedPerson(String name, String id, boolean isRegisteredOnline, boolean isApprover, String gcmId) {
        AssociatedPerson person = new AssociatedPersonImpl();
        person.setFirstName(name);
        person.setClientKey(ClientKey.valueOf(id));
        person.setHasToAcceptTnC(isApprover);
        person.setRegisteredOnline(isRegisteredOnline);
        person.setHasApprovedTnC(true);
        person.setGcmId(gcmId);
        person.setPersonRel(PersonRelationship.AO);
        return person;
    }

    private Individual createIndividualDetail(String lastName, String firstName, String emailAddress, String gcmId, List<InvestorRole> roles) {
        EmailImpl email = new EmailImpl();
        email.setEmail(emailAddress);
        IndividualImpl individual = new IndividualImpl();

        individual.setFirstName(firstName);
        individual.setLastName(lastName);
        individual.setEmails(Arrays.asList((Email) email));
        individual.setGcmId(gcmId);

        return individual;
    }

    private Map<String, Object> getAccountSettingsForInvestors(int noOfInvestors) {
        Map<String, Object> accountSettings = new HashMap<>();
        List investorsSettingsList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < noOfInvestors; i++) {
            Map investorSetting = new HashMap<>();
            investorSetting.put("paymentSetting", "nopayments");
            investorSetting.put("isApprover", "true");

            investorsSettingsList.add(investorSetting);
        }
        accountSettings.put("primarycontact", "0");
        accountSettings.put("investorAccountSettings", investorsSettingsList);
        return accountSettings;
    }

    private Phone createPhone(String phoneNumber, String areaCode, AddressMedium type) {
        Phone phone = mock(Phone.class);
        when(phone.getNumber()).thenReturn(phoneNumber);
        when(phone.getAreaCode()).thenReturn(areaCode);
        when(phone.getType()).thenReturn(type);
        return phone;
    }


    private Email createEmail(String emailAddress, AddressMedium type) {
        Email email = mock(Email.class);
        when(email.getEmail()).thenReturn(emailAddress);
        when(email.getType()).thenReturn(type);
        return email;
    }

    private Map<String,Object> getMinimalFormDataForSingleInvestor(String accountType) throws IOException {
        Map<String, Object> formData = new HashMap<>();
        formData.put("accountType", accountType);

        String investorJson = "[\n" +
                "    {\n" +
                "        \"title\": \"mr\",\n" +
                "        \"firstname\": \"John\",\n" +
                "        \"email\": {\n" +
                "            \"value\": \"john.not.so.smart@gmail.com\"\n" +
                "        },\n" +
                "        \"lastname\": \"NotSoSmart\"\n" +
                "    }\n" +
                "]";

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> investors = mapper.readValue(investorJson, new TypeReference<List<Map<String, Object>>>() {
        });
        formData.put("investors", investors);
        formData.put("accountsettings", getAccountSettingsForInvestors(1));
        return formData;
    }

}
