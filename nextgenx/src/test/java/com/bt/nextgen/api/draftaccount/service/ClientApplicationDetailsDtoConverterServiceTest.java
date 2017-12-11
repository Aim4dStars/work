package com.bt.nextgen.api.draftaccount.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.account.v2.model.AccountPaymentPermission;
import com.bt.nextgen.api.account.v2.model.LinkedAccountDto;
import com.bt.nextgen.api.account.v3.model.PersonRelationDto;
import com.bt.nextgen.api.broker.model.BrokerDto;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.model.CompanyDto;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.client.model.SmsfDto;
import com.bt.nextgen.api.client.model.TaxResidenceCountriesDto;
import com.bt.nextgen.api.client.model.TrustDto;
import com.bt.nextgen.api.client.service.ClientListDtoServiceImpl;
import com.bt.nextgen.api.draftaccount.AbstractJsonReaderTest;
import com.bt.nextgen.api.draftaccount.model.AccountSettingsDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDtoMapImpl;
import com.bt.nextgen.api.draftaccount.model.CompanyDetailsDto;
import com.bt.nextgen.api.draftaccount.model.CorporateSmsfApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.CorporateTrustApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.IndividualOrJointApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.IndividualTrustApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.SuperPensionApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IExtendedPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.v1.ClientApplicationFormFactoryV1;
import com.bt.nextgen.api.draftaccount.schemas.v1.DirectClientApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;
import com.bt.nextgen.config.ApplicationContextProvider;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.core.repository.OnboardingApplicationStatus;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountAuthoriser;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.account.ApplicationDocumentDetailImpl;
import com.bt.nextgen.service.avaloq.account.BPClassListImpl;
import com.bt.nextgen.service.avaloq.account.TransactionPermission;
import com.bt.nextgen.service.avaloq.accountactivation.AccountStructure;
import com.bt.nextgen.service.avaloq.accountactivation.ApprovalType;
import com.bt.nextgen.service.avaloq.accountactivation.LinkedPortfolioDetails;
import com.bt.nextgen.service.avaloq.accountactivation.RegisteredAccountImpl;
import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.bt.nextgen.service.avaloq.fees.AnnotatedPercentageFeesComponent;
import com.bt.nextgen.service.avaloq.fees.DollarFeesComponent;
import com.bt.nextgen.service.avaloq.fees.FeesComponentType;
import com.bt.nextgen.service.avaloq.fees.FeesComponents;
import com.bt.nextgen.service.avaloq.fees.FeesMiscType;
import com.bt.nextgen.service.avaloq.fees.FeesScheduleImpl;
import com.bt.nextgen.service.avaloq.fees.FeesType;
import com.bt.nextgen.service.avaloq.fees.OneOffFeesComponent;
import com.bt.nextgen.service.avaloq.fees.RegularFeesComponent;
import com.bt.nextgen.service.avaloq.fees.SlidingScaleFeesComponent;
import com.bt.nextgen.service.avaloq.fees.SlidingScaleTiers;
import com.bt.nextgen.service.avaloq.pension.ConditionOfRelease;
import com.bt.nextgen.service.avaloq.pension.EligibilityCriteria;
import com.bt.nextgen.service.avaloq.pension.PensionEligibility;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.ApplicationDocumentDetail;
import com.bt.nextgen.service.integration.account.BPClassList;
import com.bt.nextgen.service.integration.account.CashManagementAccountType;
import com.bt.nextgen.service.integration.account.CashManagementAccountValues;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.domain.Organisation;
import com.bt.nextgen.service.integration.domain.PensionExemptionReason;
import com.bt.nextgen.service.integration.domain.PersonDetail;
import com.bt.nextgen.service.integration.fees.FeesSchedule;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.CISKey;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.avaloq.userinformation.PersonRelationship;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.SUPER_ACCUMULATION;
import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.SUPER_PENSION;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
public class ClientApplicationDetailsDtoConverterServiceTest extends AbstractJsonReaderTest {

    private final String JSON_SCHEMA_V3_PACKAGE= "com/bt/nextgen/api/draftaccount/builder/v3_JsonSchema/";

    @Mock
    private ClientApplicationDtoConverterService clientApplicationDtoConverterService;

    @Mock
    private ClientListDtoServiceImpl clientListDtoService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private IndividualDtoConverter individualDtoConverter;

    @Mock
    private InvestorDtoConverterForPersonDetail investorDtoConverterForPersonDetail;

    @Mock
    private OrganizationDtoConverterForApplicationDocument organizationDtoConverterForApplicationDocument;

    @Mock
    private OrganizationDtoConverter organizationDtoConverter;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @InjectMocks
    private ClientApplicationDetailsDtoConverterService clientApplicationDetailsDtoConverterService;


    @Mock
    private PersonMapperService personAccountSettingsMapperService;


    @Mock
    private OrganisationMapper organisationMapper;

    @Mock
    CRSTaxDetailHelperService crsTaxDetailHelperService;


    private ClientApplication clientApplication;

    private ClientApplicationDto clientApplicationDto;

    private BrokerUser brokerUser;

    @Mock
    private StaticIntegrationService staticService;

    @Mock
    private Code eligibilityCriteria;

    @Mock
    private Code conditionOfRelease;

    @Mock
    private ApplicationContext applicationContext;

    private ObjectMapper jsonObjectMapper;

    @Mock
    private ClientApplicationDetailsDtoHelperService clientApplicationDetailsDtoHelperService;

    FeatureToggles featureToggles;

    @Mock
    FeatureTogglesService featureTogglesService;

    private static final String IDPS = "idps";
    private static final String SUPER="super";

    @Before
    public void setUp() throws IOException {

        //setup jsonObjectMapper for tests
        clientApplication = new ClientApplication();
        jsonObjectMapper = new JsonObjectMapper();
        Mockito.when(applicationContext.getBean(eq("jsonObjectMapper"), any(Class.class))).thenReturn(jsonObjectMapper);
        Mockito.when(applicationContext.getBean(eq("jsonObjectMapper"))).thenReturn(jsonObjectMapper);
        ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider(null);
        applicationContextProvider.setApplicationContext(applicationContext);
        clientApplication.setApplicationContext(applicationContext);

        clientApplication.setFormData(readJsonFromFile("client_application_form_data_2.json"));
        clientApplication.setAdviserPositionId("BROKER_ID");

        clientApplicationDto = new ClientApplicationDtoMapImpl();
        clientApplicationDto.setProductName("ProdName");
        clientApplicationDto.setReferenceNumber("Ref001");
        clientApplicationDto.setLastModified(new DateTime());
        clientApplicationDto.setOffline(false);

        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), any(ServiceErrors.class)))
            .thenReturn(clientApplicationDto);
        clientApplicationDetailsDtoConverterService.setJsonObjectMapper(jsonObjectMapper);

        brokerUser = mock(BrokerUser.class);
        when(brokerUser.getFirstName()).thenReturn("John");
        when(brokerUser.getMiddleName()).thenReturn("Alan");
        when(brokerUser.getLastName()).thenReturn("Smith");
        when(brokerUser.getCorporateName()).thenReturn("CorporateName");

        AddressImpl address = new AddressImpl();
        address.setUnit("10");
        address.setStreetName("Pitt");
        address.setSuburb("Sydney");
        address.setState("NSW");
        address.setAddressKey(AddressKey.valueOf("ADDRESS"));
        when(brokerUser.getAddresses()).thenReturn(Arrays.<Address>asList(address));

        when(brokerIntegrationService.getAdviserBrokerUser(BrokerKey.valueOf("BROKER_ID"), null)).thenReturn(brokerUser);

        IndividualDto investorDto = new IndividualDto();
        investorDto.setFirstName("Dennis");
        investorDto.setMiddleName("R");
        investorDto.setLastName("Smith");
        investorDto.setPersonRoles(singletonList(InvestorRole.BeneficialOwner));


        when(individualDtoConverter.convertFromIndividualForm(any(IExtendedPersonDetailsForm.class), any(ServiceErrors.class),
                any(IClientApplicationForm.AccountType.class))).thenReturn(investorDto);

        Broker dealerGroupBroker = getDealerGroupBroker(true, true, null);
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(dealerGroupBroker);

        when(clientApplicationDetailsDtoHelperService
            .getCodeNameByIntlId(any(CodeCategory.class), any(String.class), any(ServiceErrors.class)))
            .thenReturn("Exempt payee as pensioner");
        when(clientApplicationDetailsDtoHelperService.eligibilityCriteria(any(String.class), any(ServiceErrors.class)))
            .thenReturn("eligibility criteria long name for unpsv");
        when(clientApplicationDetailsDtoHelperService.conditionOfRelease(any(String.class), any(ServiceErrors.class)))
            .thenReturn("condition of release long name for oth");
 }

    @Test
    public void testX() throws Exception {
        clientApplication.setFormData(readJsonFromFile("client_application_form_data_individual_multiple_crs.json"));
        IndividualOrJointApplicationDetailsDto dto = (IndividualOrJointApplicationDetailsDto)clientApplicationDetailsDtoConverterService.convert(clientApplication, null);
        assertThat(dto.getInvestors().size(), is(1));
    }

    @Test
    public void convert_shouldReturnClientDetailsDtoWithExistingInvestor() throws Exception {
        clientApplication.setFormData(readJsonFromFile("client_application_form_data_joint_existing.json"));
        ClientKey clientKey = new ClientKey("C760CDDA457EEA4BA402D686CA51D201E611C0480380EB7F");
        InvestorDto existingClientDto = new InvestorDto();
        existingClientDto.setKey(clientKey);
        when(clientListDtoService.find(eq(clientKey), any(ServiceErrors.class))).thenReturn(existingClientDto);
        IndividualOrJointApplicationDetailsDto clientApplicationDetailsDto
            = (IndividualOrJointApplicationDetailsDto) clientApplicationDetailsDtoConverterService.convert(clientApplication, null);
        assertThat(clientApplicationDetailsDto.getInvestors().size(), is(2));
        assertThat(clientApplicationDetailsDto.getInvestors().get(1), is(existingClientDto));
        assertThat(clientApplicationDetailsDto.getInvestors().get(1).getIdvs(), is("Verified"));
        assertEquals("Online", clientApplicationDetailsDto.getApprovalType());
        verify(crsTaxDetailHelperService, times(0)).populateCRSTaxDetailsForIndividual(any(IPersonDetailsForm.class),any(InvestorDto.class));
    }

    @Test
    public void convert_shouldReturnClientDetailsWithParentProductName() throws Exception {
        clientApplication.setFormData(readJsonFromFile("clientapplication_parentproductname.json"));
        IndividualOrJointApplicationDetailsDto clientApplicationDetailsDto
                = (IndividualOrJointApplicationDetailsDto) clientApplicationDetailsDtoConverterService.convert(clientApplication, null);
        assertThat(clientApplicationDetailsDto.getParentProductName(),is("Cash Management Account"));
    }

    @Test
    public void convert_shouldCallCRSUtilityforExistingInvestor() throws Exception {
        clientApplication.setFormData(readJsonFromFile("client_application_form_data_joint_existing.json"));
        ClientKey clientKey = new ClientKey("C760CDDA457EEA4BA402D686CA51D201E611C0480380EB7F");
        InvestorDto existingClientDto = new InvestorDto();
        existingClientDto.setKey(clientKey);
        when(clientListDtoService.find(eq(clientKey), any(ServiceErrors.class))).thenReturn(existingClientDto);
        IndividualOrJointApplicationDetailsDto clientApplicationDetailsDto
                = (IndividualOrJointApplicationDetailsDto) clientApplicationDetailsDtoConverterService.convert(clientApplication, null);
        verify(crsTaxDetailHelperService,times(0)).populateCRSTaxDetailsForIndividual(any(IPersonDetailsForm.class), any(InvestorDto.class));
    }


    @Test
    public void Convert_shouldReturnLastModifiedAtWhenValueIsNotNull() {
        clientApplication.setLastModifiedAt(new DateTime());
        ClientApplicationDetailsDto clientApplicationDto = clientApplicationDetailsDtoConverterService.convert(clientApplication, null);
        assertNotNull(clientApplicationDto.getLastModified());

    }

    @Test
    public void convert_shouldReturnClientDetailsDtoWithExistingInvestorAndRoles() throws Exception {
        clientApplication.setFormData(readJsonFromFile("client_application_form_data_joint_existing.json"));
        ClientKey clientKey = new ClientKey("C760CDDA457EEA4BA402D686CA51D201E611C0480380EB7F");
        List<InvestorRole> personRoles = Arrays.asList(InvestorRole.Owner);

        InvestorDto existingClientDto = new InvestorDto();
        existingClientDto.setKey(clientKey);

        when(clientListDtoService.find(eq(clientKey), any(ServiceErrors.class))).thenReturn(existingClientDto);
        when(individualDtoConverter.getPersonRoles(any(IExtendedPersonDetailsForm.class))).thenReturn(personRoles);

        IndividualOrJointApplicationDetailsDto clientApplicationDetailsDto
            = (IndividualOrJointApplicationDetailsDto) clientApplicationDetailsDtoConverterService.convert(clientApplication, null);
        assertThat(clientApplicationDetailsDto.getInvestors().size(), is(2));
        assertThat(clientApplicationDetailsDto.getInvestors().get(1).getPersonRoles(), is(personRoles));
    }

    @Test
    public void convert_ShouldAddTheOwnerRoleToPersonRelationDtoInAccountSettingsDto_ForAllInvestors() throws Exception {
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(clientApplication, null);
        AccountSettingsDto accountSettings = clientApplicationDetailsDto.getAccountSettings();

        List<PersonRelationDto> owners = Lambda.filter(new LambdaMatcher<PersonRelationDto>() {
            @Override
            protected boolean matchesSafely(PersonRelationDto personRelation) {
                return personRelation.getPersonRoles().contains(InvestorRole.Owner);
            }
        }, accountSettings.getPersonRelations());

        assertThat(owners, is(not(empty())));
    }


    @Test
    public void shouldTestExistingIDPSInvestorWithSuperPension() throws Exception {
        String jsonString = readJsonStringFromFile("superpension_existingidps.json");
        IClientApplicationForm clientApplicationForm = getClientApplicationForm(jsonString, false);
        ClientApplication clientApplication = mock(ClientApplication.class);
        when(clientApplication.getClientApplicationForm()).thenReturn(clientApplicationForm);
        when(clientApplication.getAdviserPositionId()).thenReturn("BROKER_ID");
        ClientKey clientKey = new ClientKey("E2748F14DDB6D9165C37CC6227F8E33A4F4D9ADB1BFDC899");
        List<InvestorRole> personRoles = Arrays.asList(InvestorRole.Owner);
        InvestorDto existingClientDto = new InvestorDto();
        existingClientDto.setKey(clientKey);
        existingClientDto.setPensionExemptionReason(PensionExemptionReason.PENSIONER);
        when(clientListDtoService.find(eq(clientKey), any(ServiceErrors.class))).thenReturn(existingClientDto);
        when(individualDtoConverter.getPersonRoles(any(IExtendedPersonDetailsForm.class))).thenReturn(personRoles);
        IndividualOrJointApplicationDetailsDto clientApplicationDetailsDto
            = (IndividualOrJointApplicationDetailsDto) clientApplicationDetailsDtoConverterService.convert(clientApplication, null);
        assertThat(clientApplicationDetailsDto.getInvestors().size(), is(1));
        assertThat(clientApplicationDetailsDto.getInvestors().get(0).getExemptionReason(), is("Exempt payee as pensioner"));
    }

    @Test
    public void shouldTestExemptionReasonForExistingIDPSInvestorWithSuperPension() throws Exception {
        String jsonString = readJsonStringFromFile("superpension_existing.json");
        IClientApplicationForm clientApplicationForm = getClientApplicationForm(jsonString, false);
        ClientApplication clientApplication = mock(ClientApplication.class);
        when(clientApplication.getClientApplicationForm()).thenReturn(clientApplicationForm);
        when(clientApplication.getAdviserPositionId()).thenReturn("BROKER_ID");
        ClientKey clientKey = new ClientKey("8BF758EB04C1D1129D9E0027AD771AC7B89927FCA2E4EEEF");
        List<InvestorRole> personRoles = Arrays.asList(InvestorRole.Owner);
        InvestorDto existingClientDto = new InvestorDto();
        existingClientDto.setKey(clientKey);
        when(clientListDtoService.find(eq(clientKey), any(ServiceErrors.class))).thenReturn(existingClientDto);
        when(individualDtoConverter.getPersonRoles(any(IExtendedPersonDetailsForm.class))).thenReturn(personRoles);
        IndividualOrJointApplicationDetailsDto clientApplicationDetailsDto
            = (IndividualOrJointApplicationDetailsDto) clientApplicationDetailsDtoConverterService.convert(clientApplication, null);
        assertThat(clientApplicationDetailsDto.getInvestors().size(), is(1));
        assertThat(clientApplicationDetailsDto.getInvestors().get(0).getExemptionReason(), is("Exempt payee as pensioner"));
    }

    private IClientApplicationForm getClientApplicationForm(String jsonAsString, boolean isDirect) throws Exception {
        Object formData;
        IClientApplicationForm clientApplicationForm;
        if (isDirect) {
            formData = jsonObjectMapper.readValue(jsonAsString, DirectClientApplicationFormData.class);
            clientApplicationForm = ClientApplicationFormFactoryV1
                .getNewDirectClientApplicationForm((DirectClientApplicationFormData) formData);
        } else {
            formData = jsonObjectMapper.readValue(jsonAsString, OnboardingApplicationFormData.class);
            clientApplicationForm = ClientApplicationFormFactoryV1.getNewClientApplicationForm((OnboardingApplicationFormData) formData);
        }
        return clientApplicationForm;
    }

    @Test
    public void convertFromApplicationDocumentDetailForAndSuperPension() throws Exception {
        clientApplication.setFormData(readJsonFromFile("client_application_superpension_form.json"));
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
                .convert(clientApplication, null);
        List<LinkedAccountDto> linkedAccounts = clientApplicationDetailsDto.getLinkedAccounts();
        assertThat(linkedAccounts.size(), is(1));
    }

    @Test
    public void convertFromApplicationDocumentDetailForSuperAccumulation() throws Exception {
        clientApplication.setFormData(readJsonFromFile("client_application_superAccumulation_form.json"));
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
                .convert(clientApplication, null);
        List<LinkedAccountDto> linkedAccounts = clientApplicationDetailsDto.getLinkedAccounts();
        assertThat(linkedAccounts.size(), is(1));
    }

    @Test
    public void convertFromApplicationDocumentDetailForDirectAndSuperAccumulation() throws Exception {
        clientApplication.setFormData(readJsonFromFile("direct_superAccumulation.json"));
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
                .convert(clientApplication, null);
        List<LinkedAccountDto> linkedAccounts = clientApplicationDetailsDto.getLinkedAccounts();
        assertThat(linkedAccounts, is(nullValue()));
    }

    @Test
    public void convert_ShouldAddTheDirectorsRoleToPersonRelationDtoInAccountSettingsDto_ForAllDirectors() throws Exception {
        clientApplication.setFormData(readJsonFromFile("client_application_corpsmsf_form_data_with_two_approvers.json"));
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
                .convert(clientApplication, null);
        AccountSettingsDto accountSettingsDto = clientApplicationDetailsDto.getAccountSettings();

        List<PersonRelationDto> directors = Lambda.filter(new LambdaMatcher<PersonRelationDto>() {
            @Override
            protected boolean matchesSafely(PersonRelationDto personRelation) {
                return personRelation.getPersonRoles().contains(InvestorRole.Director);
            }
        }, accountSettingsDto.getPersonRelations());

        assertThat(directors, is(not(empty())));
        assertEquals("Offline", clientApplicationDetailsDto.getApprovalType());
    }

    @Test
    public void convert_ShouldAddTheTrusteesRoleToPersonRelationDtoInAccountSettingsDto_ForAllTrustess() throws Exception {
        clientApplication.setFormData(readJsonFromFile("client_application_individualtrust_family_form_data_minimal.json"));
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(clientApplication, null);
        AccountSettingsDto accountSettingsDto = clientApplicationDetailsDto.getAccountSettings();

        List<PersonRelationDto> trustees = Lambda.filter(new LambdaMatcher<PersonRelationDto>() {
            @Override
            protected boolean matchesSafely(PersonRelationDto personRelation) {
                return personRelation.getPersonRoles().contains(InvestorRole.Trustee);
            }
        }, accountSettingsDto.getPersonRelations());

        assertThat(trustees, is(not(empty())));
    }

    @Test
    public void convert_ShouldHaveMajorShareholder() throws Exception {
        clientApplication.setFormData(readJsonFromFile("client_application_individualtrust_family_form_data_minimal.json"));
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(clientApplication, null);
        assertThat(clientApplicationDetailsDto.getMajorShareholder(), is("yes"));
    }

    @Test
    public void convert_ShouldHaveCMADetailsForIndvTrust_Family() throws Exception {
        clientApplication.setFormData(readJsonFromFile("client_application_individualtrust_family_cma_form_data.json"));
        TrustDto trustDto = new TrustDto();
        trustDto.setPersonalInvestmentEntity("Yes");
        when(organizationDtoConverter.convertFromOrganizationForm(any(IClientApplicationForm.class))).thenReturn(trustDto);
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
                .convert(clientApplication, null);

        assertTrue(clientApplicationDetailsDto instanceof IndividualTrustApplicationDetailsDto);

        IndividualTrustApplicationDetailsDto individualTrustApplicationDetailsDto = (IndividualTrustApplicationDetailsDto)clientApplicationDetailsDto;
        AccountSettingsDto accountSettingsDto = individualTrustApplicationDetailsDto.getAccountSettings();
        assertNotNull(accountSettingsDto.getPowerOfAttorney());
        assertThat(accountSettingsDto.getPowerOfAttorney(), is("No"));
        assertNotNull(((IndividualTrustApplicationDetailsDto) clientApplicationDetailsDto).getTrust().getPersonalInvestmentEntity());
        assertThat(((IndividualTrustApplicationDetailsDto) clientApplicationDetailsDto).getTrust().getPersonalInvestmentEntity(),is("Yes"));
    }

    @Test
    public void convert_ShouldHaveCMADetailsForIndvTrust_Other() throws Exception {
        clientApplication.setFormData(readJsonFromFile("client_application_individualtrust_other_form_data_minimal.json"));
        TrustDto trustDto = new TrustDto();
        trustDto.setPersonalInvestmentEntity("Yes");
        when(organizationDtoConverter.convertFromOrganizationForm(any(IClientApplicationForm.class))).thenReturn(trustDto);
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
                .convert(clientApplication, null);

        assertTrue(clientApplicationDetailsDto instanceof IndividualTrustApplicationDetailsDto);

        IndividualTrustApplicationDetailsDto individualTrustApplicationDetailsDto = (IndividualTrustApplicationDetailsDto)clientApplicationDetailsDto;
        AccountSettingsDto accountSettingsDto = individualTrustApplicationDetailsDto.getAccountSettings();
        assertNotNull(accountSettingsDto.getPowerOfAttorney());
        assertThat(accountSettingsDto.getPowerOfAttorney(), is("Yes"));
        assertNotNull(((IndividualTrustApplicationDetailsDto) clientApplicationDetailsDto).getTrust().getPersonalInvestmentEntity());
        assertThat(((IndividualTrustApplicationDetailsDto) clientApplicationDetailsDto).getTrust().getPersonalInvestmentEntity(),is("Yes"));
    }

    @Test
    public void convert_ShouldHaveCMADetailsForIndvTrust_Govt_Super() throws Exception {
        clientApplication.setFormData(readJsonFromFile("client_application_individualtrust_govtsuper_form_data_minimal.json"));
        TrustDto trustDto = new TrustDto();
        when(organizationDtoConverter.convertFromOrganizationForm(any(IClientApplicationForm.class))).thenReturn(trustDto);
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
                .convert(clientApplication, null);

        assertTrue(clientApplicationDetailsDto instanceof IndividualTrustApplicationDetailsDto);
        IndividualTrustApplicationDetailsDto individualTrustApplicationDetailsDto = (IndividualTrustApplicationDetailsDto)clientApplicationDetailsDto;
        AccountSettingsDto accountSettingsDto = individualTrustApplicationDetailsDto.getAccountSettings();
        assertNotNull(accountSettingsDto.getPowerOfAttorney());
        assertThat(accountSettingsDto.getPowerOfAttorney(), is("Yes"));
        assertNull(((IndividualTrustApplicationDetailsDto) clientApplicationDetailsDto).getTrust().getPersonalInvestmentEntity());
    }

    @Test
    public void convert_ShouldHaveCMADetailsForCorpTrust_Family() throws Exception {
        clientApplication.setFormData(readJsonFromFile("client_application_corporatetrust_family_form_data_minimal.json"));
        TrustDto trustDto = new TrustDto();
        trustDto.setPersonalInvestmentEntity("Yes");
        when(organizationDtoConverter.convertFromOrganizationForm(any(IClientApplicationForm.class))).thenReturn(trustDto);
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
                .convert(clientApplication, null);

        assertTrue(clientApplicationDetailsDto instanceof CorporateTrustApplicationDetailsDto);
        CorporateTrustApplicationDetailsDto corporateTrustApplicationDetailsDto = (CorporateTrustApplicationDetailsDto)clientApplicationDetailsDto;
        AccountSettingsDto accountSettingsDto = corporateTrustApplicationDetailsDto.getAccountSettings();
        assertNotNull(accountSettingsDto.getPowerOfAttorney());
        assertThat(accountSettingsDto.getPowerOfAttorney(), is("Yes"));
        assertNotNull(((CorporateTrustApplicationDetailsDto) clientApplicationDetailsDto).getTrust().getPersonalInvestmentEntity());
        assertThat(((CorporateTrustApplicationDetailsDto) clientApplicationDetailsDto).getTrust().getPersonalInvestmentEntity(),is("Yes"));
    }

    @Test
    public void convert_ShouldHaveCMADetailsForCompany() throws IOException {
        clientApplication.setFormData(readJsonFromFile("client_application_company_form_data_v2.json"));
        CompanyDto companyDto = new CompanyDto();
        companyDto.setPersonalInvestmentEntity("Yes");
        when(organizationDtoConverter.convertFromOrganizationForm(any(IClientApplicationForm.class))).thenReturn(companyDto);
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
                .convert(clientApplication, null);

        assertTrue(clientApplicationDetailsDto instanceof CompanyDetailsDto);
        CompanyDetailsDto companyDetailsDto = (CompanyDetailsDto)clientApplicationDetailsDto;
        AccountSettingsDto accountSettingsDto = companyDetailsDto.getAccountSettings();
        assertNotNull(accountSettingsDto.getPowerOfAttorney());
        assertThat(accountSettingsDto.getPowerOfAttorney(), is("Yes"));
        assertNotNull(((CompanyDetailsDto) clientApplicationDetailsDto).getCompany().getPersonalInvestmentEntity());
        assertThat(((CompanyDetailsDto) clientApplicationDetailsDto).getCompany().getPersonalInvestmentEntity(), is("Yes"));
    }



    @Test
    public void convert_ShouldHaveApproverSettings_InTheAccountSettings() throws Exception {
        clientApplication.setFormData(readJsonFromFile("client_application_corpsmsf_form_data_with_two_approvers.json"));
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(clientApplication, null);
        AccountSettingsDto accountSettingsDto = clientApplicationDetailsDto.getAccountSettings();

        List<PersonRelationDto> approvers = Lambda.filter(new LambdaMatcher<PersonRelationDto>() {
            @Override
            protected boolean matchesSafely(PersonRelationDto personRelationDto) {
                return personRelationDto.isApprover();
            }
        }, accountSettingsDto.getPersonRelations());

        assertThat(approvers, is(not(empty())));
    }

    private void setUpCorporateSMSFClientApplication(boolean emptyLinkedAccounts) throws IOException {
        clientApplication = new ClientApplication();
        //setup Spring context with 'jsonObjectMapper'
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        Mockito.when(applicationContext.getBean("jsonObjectMapper")).thenReturn(new JsonObjectMapper());
        clientApplication.setApplicationContext(applicationContext);
        //
        if(emptyLinkedAccounts) {
            clientApplication.setFormData(readJsonFromFile("client_application_corpsmsf_form_data_withEmptyLinkedAccounts.json"));
        } else {
            clientApplication.setFormData(readJsonFromFile("client_application_corpsmsf_form_data_with_addl_members.json"));
        }
        clientApplication.setAdviserPositionId("BROKER_ID");
        when(organizationDtoConverter.convertFromOrganizationForm(any(IClientApplicationForm.class))).thenReturn(new SmsfDto());
    }

    private void setUpCorporateTrustFamilyApplication() throws IOException {
        clientApplication = new ClientApplication();
        //setup Spring context with 'jsonObjectMapper'
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        JsonObjectMapper mapper = new JsonObjectMapper();
        Mockito.when(applicationContext.getBean("jsonObjectMapper", ObjectMapper.class)).thenReturn(mapper);
        Mockito.when(applicationContext.getBean("jsonObjectMapper")).thenReturn(mapper);
        clientApplication.setApplicationContext(applicationContext);
        clientApplication.setFormData(readJsonFromFile(JSON_SCHEMA_V3_PACKAGE + "corporatetrust_family.json"));
        clientApplication.setAdviserPositionId("BROKER_ID");
    }

    @Test
    public void convert_shouldHaveNomiatedMember() throws IOException {
        clientApplication.setFormData(readJsonFromFile("client_application_company_form_data.json"));
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(clientApplication, null);
        assertThat(clientApplicationDetailsDto.isContainsNominatedInvestors(), is(true));
    }



    @Test
    public void convertFromClientApplication_WhenCorporateSMSF() throws IOException {
        setUpCorporateSMSFClientApplication(false);
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(clientApplication, null);
        assertTrue(clientApplicationDetailsDto instanceof CorporateSmsfApplicationDetailsDto);
        List<InvestorDto> directors = ((CorporateSmsfApplicationDetailsDto) clientApplicationDetailsDto).getDirectors();
        assertThat(directors.size(), is(clientApplication.getClientApplicationForm().getDirectors().size()));


        List<InvestorDto> shareholdersAndMembers = ((CorporateSmsfApplicationDetailsDto) clientApplicationDetailsDto)
            .getShareholdersAndMembers();
        assertThat(shareholdersAndMembers.size(),
            is(clientApplication.getClientApplicationForm().getAdditionalShareholdersAndMembers().size()));

    }

    @Test
    public void convertFromClientApplication_WhenCorporateSMSF_WithoutLinkedAccounts() throws IOException {
        setUpCorporateSMSFClientApplication(true);
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(clientApplication, null);
        assertTrue(clientApplicationDetailsDto instanceof CorporateSmsfApplicationDetailsDto);
        List<InvestorDto> directors = ((CorporateSmsfApplicationDetailsDto) clientApplicationDetailsDto).getDirectors();
        assertThat(directors.size(), is(clientApplication.getClientApplicationForm().getDirectors().size()));


        List<InvestorDto> shareholdersAndMembers = ((CorporateSmsfApplicationDetailsDto) clientApplicationDetailsDto)
            .getShareholdersAndMembers();
        assertThat(shareholdersAndMembers.size(),
            is(clientApplication.getClientApplicationForm().getAdditionalShareholdersAndMembers().size()));
        assertThat(clientApplicationDetailsDto.getLinkedAccounts().size(), is(0));

    }


    @Test
    public void convertFromClientApplication_WhenCorporateTrust() throws IOException {
        setUpCorporateTrustFamilyApplication();

        IndividualDto investorDto = new IndividualDto();
        investorDto.setFirstName("Florin");
        investorDto.setMiddleName("M");
        investorDto.setLastName("Smith");
        investorDto.setPersonRoles(Arrays.asList(InvestorRole.ControllerOfTrust));
        when(individualDtoConverter.convertFromIndividualForm(any(IExtendedPersonDetailsForm.class), any(ServiceErrors.class),
                any(IClientApplicationForm.AccountType.class))).thenReturn(investorDto);

        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService.convert(clientApplication, null);
        assertTrue(clientApplicationDetailsDto instanceof CorporateTrustApplicationDetailsDto);
        CorporateTrustApplicationDetailsDto dto = (CorporateTrustApplicationDetailsDto)clientApplicationDetailsDto;
        List<InvestorDto> shareholdersAndMembers  = dto.getShareholdersAndMembers();
        assertThat(shareholdersAndMembers.size(), is(clientApplication.getClientApplicationForm().getAdditionalShareholdersAndMembers().size()));
        for(InvestorDto investor: shareholdersAndMembers) {
            assertTrue(investor.getPersonRoles().contains(InvestorRole.ControllerOfTrust));
        }
    }

    @Test
    public void convertFromClientApplication_WhenCorporateSMSFWithCompanySecretary() throws IOException {
        setUpCorporateSMSFClientApplication(false);
        IndividualDto investorOneDto = new IndividualDto();
        investorOneDto.setFirstName("Vijay");
        investorOneDto.setLastName("Kumar");
        investorOneDto.setPersonRoles(Arrays.asList(InvestorRole.BeneficialOwner,InvestorRole.Secretary));

        when(individualDtoConverter.convertFromIndividualForm(any(IExtendedPersonDetailsForm.class), any(ServiceErrors.class),
                any(IClientApplicationForm.AccountType.class))).thenReturn(investorOneDto);
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
                .convert(clientApplication, null);
        assertTrue(clientApplicationDetailsDto instanceof CorporateSmsfApplicationDetailsDto);
        List<InvestorDto> directors = ((CorporateSmsfApplicationDetailsDto) clientApplicationDetailsDto).getDirectors();
        assertThat(directors.size(), is(clientApplication.getClientApplicationForm().getDirectors().size()));
        assertThat(directors.get(0).getPersonRoles(), hasItems(InvestorRole.Secretary));

    }

    @Test
    public void convertFromClientApplication_shouldContainInvestorDetailsForTheAccountType() {
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(clientApplication, null);
        List<InvestorDto> investors = ((IndividualOrJointApplicationDetailsDto) clientApplicationDetailsDto).getInvestors();
        assertThat(investors.size(), is(1));
        InvestorDto investorDto = investors.get(0);
        assertThat(investorDto.getFirstName(), is("Dennis"));
        assertThat(investorDto.getLastName(), is("Smith"));
        assertThat(clientApplicationDetailsDto.getInvestorAccountType(), is("individual"));
    }

    @Test
    public void convertFromClientApplication_shouldHaveNullOnboardingApplicationKey() throws Exception {
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(clientApplication, null);
        assertNull(clientApplicationDetailsDto.getOnboardingApplicationKey());
    }

    @Test
    public void convertFromClientApplication_shouldHaveNullAccountKey() throws Exception {
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(clientApplication, null);
        assertNull(clientApplicationDetailsDto.getAccountKey());
    }

    @Test
    public void convertFromClientApplication_shouldHaveNullPdsUrl() throws Exception {
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(clientApplication, null);
        assertNull(clientApplicationDetailsDto.getPdsUrl());
    }

    @Test
    public void convertFromClientApplication_shouldHaveProcessingAsStatus() throws Exception {
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(clientApplication, null);
        assertThat(clientApplicationDetailsDto.getAccountAvaloqStatus(), is(OnboardingApplicationStatus.processing.toString()));
    }

    @Test
    public void convertFromClientApplication_shouldHaveProductNameAndReferenceNumber() throws Exception {
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(clientApplication, null);
        assertThat(clientApplicationDetailsDto.getProductName(), is("ProdName"));
        assertThat(clientApplicationDetailsDto.getReferenceNumber(), is("Ref001"));
    }

    @Test
    public void convertFromClientApplication_shouldAdviserDetails() throws Exception {
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(clientApplication, null);
        BrokerDto adviser = clientApplicationDetailsDto.getAdviser();
        assertThat(adviser.getFirstName(), is("John"));
        assertThat(adviser.getLastName(), is("Smith"));
        assertThat(adviser.getCorporateName(), is("CorporateName"));
        AddressDto addressDto = adviser.getAddresses().get(0);
        assertThat(addressDto.getState(), is("NSW"));
        assertThat(addressDto.getUnitNumber(), is("10"));
        assertThat(addressDto.getStreetName(), is("Pitt"));
        assertThat(addressDto.getSuburb(), is("Sydney"));
    }

    @Test
    public void convertFromClientApplication_shouldMultipleLinkedAccountDetails() throws Exception {
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(clientApplication, null);
        List<LinkedAccountDto> linkedAccounts = clientApplicationDetailsDto.getLinkedAccounts();

        assertThat(linkedAccounts.size(), is(2));

        LinkedAccountDto linkedAccountDto = linkedAccounts.get(0);
        assertThat(linkedAccountDto.getAccountNumber(), is("666000"));
        assertThat(linkedAccountDto.getNickName(), is("MO"));
        assertThat(linkedAccountDto.getName(), is("NEMO"));
        assertThat(linkedAccountDto.getBsb(), is("062-005"));
        assertThat(linkedAccountDto.getDirectDebitAmount(), is(new BigDecimal("756000.00")));
        assertThat(linkedAccountDto.isPrimary(), is(true));

        LinkedAccountDto linkedAccountDto2 = linkedAccounts.get(1);
        assertThat(linkedAccountDto2.getAccountNumber(), is("000000"));
        assertThat(linkedAccountDto2.getNickName(), is("BO"));
        assertThat(linkedAccountDto2.getName(), is("BOB"));
        assertThat(linkedAccountDto2.getBsb(), is("062-005"));
        assertThat(linkedAccountDto2.getDirectDebitAmount(), is(new BigDecimal("12.00")));
        assertThat(linkedAccountDto2.isPrimary(), is(false));
    }

    @Test
    public void convertFromClientApplication_shouldHaveAccountSettingsDetails() throws Exception {
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(clientApplication, null);
        AccountSettingsDto accountSettingsDto = clientApplicationDetailsDto.getAccountSettings();
        assertThat(accountSettingsDto.getPersonRelations().size(), is(2));

        PersonRelationDto personRelationDto1 = accountSettingsDto.getPersonRelations().get(0);
        assertThat(personRelationDto1.getName(), is("NEMO A"));
        assertThat(personRelationDto1.isAdviser(), is(false));
        assertThat(personRelationDto1.isPrimaryContactPerson(), is(true));
        assertThat(personRelationDto1.getPermissions(), is(AccountPaymentPermission.NO_PAYMENTS.getPermissionDesc()));

        PersonRelationDto personRelationDto2 = accountSettingsDto.getPersonRelations().get(1);
        assertThat(personRelationDto2.getName(), is("CorporateName"));
        assertThat(personRelationDto2.isAdviser(), is(true));
        assertThat(personRelationDto2.isPrimaryContactPerson(), is(false));
        assertThat(personRelationDto2.getPermissions(), is(AccountPaymentPermission.PAYMENTS_DEPOSITS_TO_ALL.getPermissionDesc()));
    }

    @Test
    public void convertFromClientApplication_shouldHaveAccountSettingsDetailsForJointAccount() throws Exception {
        clientApplication.setFormData(readJsonFromFile("client_application_form_data_joint.json"));
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(clientApplication, null);

        assertThat(clientApplicationDetailsDto.getInvestorAccountType(), is("joint"));
        AccountSettingsDto accountSettingsDto = clientApplicationDetailsDto.getAccountSettings();
        assertThat(accountSettingsDto.getPersonRelations().size(), is(6));

        PersonRelationDto personRelationDto1 = accountSettingsDto.getPersonRelations().get(0);
        assertThat(personRelationDto1.getName(), is("Sam Oopsy-Doopsy"));
        assertThat(personRelationDto1.isAdviser(), is(false));
        assertThat(personRelationDto1.isPrimaryContactPerson(), is(false));
        assertThat(personRelationDto1.getPermissions(), is(AccountPaymentPermission.NO_PAYMENTS.getPermissionDesc()));

        PersonRelationDto personRelationDto2 = accountSettingsDto.getPersonRelations().get(1);
        assertThat(personRelationDto2.getName(), is("Irina Filimonovich"));
        assertThat(personRelationDto2.isAdviser(), is(false));
        assertThat(personRelationDto2.isPrimaryContactPerson(), is(true));
        assertThat(personRelationDto2.getPermissions(),
            is(AccountPaymentPermission.PAYMENTS_DEPOSITS_TO_LINKED_ACCOUNTS.getPermissionDesc()));

        PersonRelationDto personRelationDto3 = accountSettingsDto.getPersonRelations().get(5);
        assertThat(personRelationDto3.getName(), is("CorporateName"));
        assertThat(personRelationDto3.isAdviser(), is(true));
        assertThat(personRelationDto3.isPrimaryContactPerson(), is(false));
        assertThat(personRelationDto3.getPermissions(),
            is(AccountPaymentPermission.PAYMENTS_DEPOSITS_TO_LINKED_ACCOUNTS.getPermissionDesc()));
    }

    @Test
    public void convertFromClientApplication_shouldHaveAccountSettingsDetailsForCompanyAccount() throws Exception {
        clientApplication.setFormData(readJsonFromFile("client_application_company_form_data_ten_directors.json"));
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(clientApplication, null);

        assertThat(clientApplicationDetailsDto.getInvestorAccountType(), is("company"));
        AccountSettingsDto accountSettingsDto = clientApplicationDetailsDto.getAccountSettings();
        assertThat(accountSettingsDto.getPersonRelations().size(), is(11)); // 10 directors and 1 adviser.

        PersonRelationDto personRelationDto1 = accountSettingsDto.getPersonRelations().get(0);
        assertThat(personRelationDto1.getName(), is("Anthony Flour"));
        assertThat(personRelationDto1.isAdviser(), is(false));
        assertThat(personRelationDto1.isPrimaryContactPerson(), is(false));
        assertThat(personRelationDto1.getPersonRoles(), hasItem(InvestorRole.Director));
        assertThat(personRelationDto1.isApprover(), is(true));
        assertThat(personRelationDto1.getPermissions(), is(AccountPaymentPermission.NO_PAYMENTS.getPermissionDesc()));

        PersonRelationDto personRelationDto2 = accountSettingsDto.getPersonRelations().get(1);
        assertThat(personRelationDto2.getName(), is("Amanda Flour"));
        assertThat(personRelationDto2.isAdviser(), is(false));
        assertThat(personRelationDto2.isPrimaryContactPerson(), is(false));
        assertThat(personRelationDto2.getPersonRoles(), hasItem(InvestorRole.Signatory));
        assertThat(personRelationDto2.isApprover(), is(false));
        assertThat(personRelationDto2.getPermissions(),
            is(AccountPaymentPermission.PAYMENTS_DEPOSITS_TO_LINKED_ACCOUNTS.getPermissionDesc()));

        PersonRelationDto personRelationDto3 = accountSettingsDto.getPersonRelations().get(5);
        assertThat(personRelationDto3.getName(), is("Mark Flour"));
        assertThat(personRelationDto3.isAdviser(), is(false));
        assertThat(personRelationDto3.isPrimaryContactPerson(), is(false));
        assertThat(personRelationDto3.getPersonRoles(), hasItem(InvestorRole.Secretary));
        assertThat(personRelationDto3.isApprover(), is(false));
        assertThat(personRelationDto3.getPermissions(), is(AccountPaymentPermission.PAYMENTS_DEPOSITS_TO_ALL.getPermissionDesc()));

        PersonRelationDto adviser = accountSettingsDto.getPersonRelations().get(10);
        assertThat(adviser.getName(), is("CorporateName"));
        assertThat(adviser.isAdviser(), is(true));
        assertThat(adviser.isPrimaryContactPerson(), is(false));
        assertThat(adviser.getPersonRoles().size(), is(0));
        assertThat(adviser.isApprover(), is(false));
        assertThat(adviser.getPermissions(), is(AccountPaymentPermission.PAYMENTS_DEPOSITS_TO_LINKED_ACCOUNTS.getPermissionDesc()));
    }

    @Test
    public void convertFromClientApplication_shouldHaveAdditionalShareholderDetailsForCompanyAccount() throws Exception {
        clientApplication.setFormData(readJsonFromFile("client_application_company_form_data_ten_directors.json"));
        CompanyDetailsDto clientApplicationDetailsDto = (CompanyDetailsDto) clientApplicationDetailsDtoConverterService
            .convert(clientApplication, null);

        assertThat(clientApplicationDetailsDto.getInvestorAccountType(), is("company"));
        List<InvestorDto> shareholders = clientApplicationDetailsDto.getShareholders();
        assertThat(shareholders.size(), is(1));
    }

    @Test
    public void convertFromClientApplication_shouldHaveFees() throws Exception {
        clientApplication.setFormData(readJsonFromFile("client_application_form_data_joint.json"));
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(clientApplication, null);

        Map<String, Object> fees = (Map<String, Object>) clientApplicationDetailsDto.getFees();
        assertThat(fees, hasKey("licenseeFees"));
        assertThat(fees, hasKey("ongoingFees"));
        assertThat((String) fees.get("estamount"), is("500.00"));
    }

    @Test
    public void convertFromApplicationDocumentDetail_shouldHaveFees() throws Exception {

        ServiceErrors serviceErrors = mock(ServiceErrors.class);
        ApplicationDocumentDetail applicationDocument = createApplicationDocumentDetailWithFees(serviceErrors, AccountStructure.I,
            ApprovalType.ONLINE);

        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(applicationDocument, serviceErrors, UserExperience.ADVISED);
        Map<String, Object> feesMap = (Map<String, Object>) clientApplicationDetailsDto.getFees();
        assertThat(feesMap.size(), is(4));

        verifyLicenseeFees(feesMap);
        verifyOngoingFees(feesMap);
        assertThat(feesMap, hasKey("ongoingFees"));
        assertThat((String) feesMap.get("estamount"), is("500.00"));
        verify(personAccountSettingsMapperService, times(1))
            .mapPersonAccountSettings(applicationDocument.getPersons(), applicationDocument.getAccountSettingsForAllPersons());
        assertThat(clientApplicationDetailsDto.getParentProductName(), is(""));
    }

    @Test
    public void convertFromApplicationDocumentDetail_shouldAdviserDetails() throws Exception {

        ServiceErrors serviceErrors = mock(ServiceErrors.class);
        ApplicationDocumentDetail applicationDocument = createApplicationDocumentDetailWithFees(serviceErrors, AccountStructure.I,
                ApprovalType.ONLINE);

        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
                .convert(applicationDocument, serviceErrors, UserExperience.ADVISED);
        BrokerDto adviser = clientApplicationDetailsDto.getAdviser();
        assertNotNull(adviser.getKey());
        assertThat(EncodedString.toPlainText(adviser.getKey().getBrokerId()), is("adviserKey"));
    }

    private void verifyOngoingFees(Map<String, Object> feesMap) {
        Map<String, Object> ongoingFees = (Map<String, Object>) feesMap.get("ongoingFees");
        assertThat(ongoingFees, hasKey("type"));
        assertThat(ongoingFees.get("type").toString(), is("Ongoing advice fee"));

        assertThat(ongoingFees, hasKey("feesComponent"));
        List feesComponents = (List) ongoingFees.get("feesComponent");
        assertThat(feesComponents.size(), is(2));
        //Dollar Fee component
        Map<String, String> dollarFeeComponent = (Map<String, String>) feesComponents.get(0);
        assertThat(dollarFeeComponent, hasKey("amount"));
        assertThat(dollarFeeComponent, hasKey("cpiindex"));
        assertThat(dollarFeeComponent, hasKey("label"));
        assertThat(dollarFeeComponent.get("amount"), is("17.00"));
//        assertThat(Boolean.valueOf(dollarFeeComponent.get("cpiindex")),is(true));
        assertThat(dollarFeeComponent.get("label"), is("Dollar fee component"));
        //Sliding Fee component
        Map<String, List<Map<String, String>>> slidingFeeComponent = (Map<String, List<Map<String, String>>>) feesComponents.get(1);
        assertThat(slidingFeeComponent, hasKey("slidingScaleFeeTier"));
        assertThat(slidingFeeComponent, hasKey("managedFund"));
        assertThat(slidingFeeComponent, hasKey("managedPortfolio"));

        List<Map<String, String>> slidingScaleFeeTiers = slidingFeeComponent.get("slidingScaleFeeTier");
        Map<String, String> slidingScaleFeeTier1 = slidingScaleFeeTiers.get(0);
        assertThat(slidingScaleFeeTier1.get("lowerBound"), is("0.00"));
        assertThat(slidingScaleFeeTier1.get("upperBound"), is("13.00"));
        assertThat(slidingScaleFeeTier1.get("percentage"), is("0.50"));
        Map<String, String> slidingScaleFeeTier2 = slidingScaleFeeTiers.get(1);
        assertThat(slidingScaleFeeTier2.get("lowerBound"), is("13.00"));
        assertThat(slidingScaleFeeTier2.get("upperBound"), is("19.00"));
        assertThat(slidingScaleFeeTier2.get("percentage"), is("0.70"));
        Map<String, String> slidingScaleFeeTier3 = slidingScaleFeeTiers.get(2);
        assertThat(slidingScaleFeeTier3.get("lowerBound"), is("19.00"));
        assertThat(slidingScaleFeeTier3.get("upperBound"), is("23.00"));
        assertThat(slidingScaleFeeTier3.get("percentage"), is("0.73"));
        Map<String, String> slidingScaleFeeTier4 = slidingScaleFeeTiers.get(3);
        assertThat(slidingScaleFeeTier4.get("lowerBound"), is("23.00"));
        assertThat(slidingScaleFeeTier4.get("upperBound"), is(""));
        assertThat(slidingScaleFeeTier4.get("percentage"), is("0.91"));
    }

    private void verifyLicenseeFees(Map<String, Object> feesMap) {
        assertThat(feesMap, hasKey("licenseeFees"));
        Map<String, Object> licenseeFees = (Map<String, Object>) feesMap.get("licenseeFees");

        assertThat(licenseeFees, hasKey("type"));
        assertThat(licenseeFees.get("type").toString(), is("Licensee advice fee"));
        assertThat(licenseeFees, hasKey("feesComponent"));
        List<Map<String, String>> feesComponents = (List<Map<String, String>>) licenseeFees.get("feesComponent");
        assertThat(feesComponents.size(), is(2));
        //Dollar Fee component
        Map<String, String> dollarFeeComponent = feesComponents.get(0);
        assertThat(dollarFeeComponent, hasKey("amount"));
        assertThat(dollarFeeComponent, hasKey("cpiindex"));
        assertThat(dollarFeeComponent, hasKey("label"));
        assertThat(dollarFeeComponent.get("amount"), is("45.00"));
        //assertThat(Boolean.valueOf(dollarFeeComponent.get("cpiindex")),is(false));
        assertThat(dollarFeeComponent.get("label"), is("Dollar fee component"));
        //Percentage fee component
        Map<String, String> percentageFeeComponent = feesComponents.get(1);
        assertThat(percentageFeeComponent, hasKey("termDeposit"));
        assertThat(percentageFeeComponent, hasKey("cash"));
        assertThat(percentageFeeComponent, hasKey("label"));
        assertThat(percentageFeeComponent, hasKey("managedPortfolio"));
        assertThat(percentageFeeComponent, hasKey("managedFund"));
        assertThat(percentageFeeComponent, hasKey("listedSecurity"));
        assertThat(percentageFeeComponent.get("termDeposit"), is("0.22"));
        assertThat(percentageFeeComponent.get("cash"), is("0.33"));
        assertThat(percentageFeeComponent.get("label"), is("Percentage fee component"));
        assertThat(percentageFeeComponent.get("managedPortfolio"), is("0.11"));
        assertThat(percentageFeeComponent.get("managedFund"), is("0.44"));
        assertThat(percentageFeeComponent.get("listedSecurity"), is("0.55"));
    }

    @Test
    public void testAccountWithASIM() {
        ServiceErrors serviceErrors = mock(ServiceErrors.class);

        ApplicationDocumentDetail applicationDocument = createOrgApplicationDocumentWithoutAdviserSettings(null,false,false,serviceErrors);
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(applicationDocument, serviceErrors, UserExperience.ASIM);
        assertThat(clientApplicationDetailsDto.isAsimProfile(), is(true));
        assertThat(clientApplicationDetailsDto.getAccountSettings().getPersonRelations().get(0).getPermissions(), is("No Payments"));
    }

    @Test
    public void testAccountWithSuper() {
        final ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        ApplicationDocumentDetail applicationDocument = createApplicationDocumentDetailWithFees(serviceErrors, AccountStructure.SU,
            ApprovalType.ONLINE);
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(applicationDocument, serviceErrors, UserExperience.ASIM);
        assertThat(clientApplicationDetailsDto.getInvestorAccountType(), is(SUPER_ACCUMULATION.value()));
    }

    @Test
    public void testAccountWithSuperPensionEligibility() {
        final ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        ApplicationDocumentDetail applicationDocument = createApplicationDocumentDetailWithFeesAndPensionEligibility(serviceErrors,
            ApprovalType.ONLINE);
        SuperPensionApplicationDetailsDto clientApplicationDetailsDto
            = (SuperPensionApplicationDetailsDto) clientApplicationDetailsDtoConverterService
            .convert(applicationDocument, serviceErrors, UserExperience.ASIM);
        assertThat(clientApplicationDetailsDto.getInvestorAccountType(), is(SUPER_PENSION.value()));
        assertNotNull(clientApplicationDetailsDto.getPensionEligibility());
        assertThat(clientApplicationDetailsDto.getPensionEligibility().getConditionRelease(), is("condition of release long name for oth"));
        assertThat(clientApplicationDetailsDto.getPensionEligibility().getEligibilityCriteria(),
            is("eligibility criteria long name for unpsv"));
    }

    @Test
    public void testAccountWithSuperPensionEligibility_forDirectPension() {
        final ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        ApplicationDocumentDetail applicationDocument = createApplicationDocumentDetailWithFeesAndPensionEligibility(serviceErrors,
            ApprovalType.ONLINE);
        SuperPensionApplicationDetailsDto clientApplicationDetailsDto
            = (SuperPensionApplicationDetailsDto) clientApplicationDetailsDtoConverterService
            .convert(applicationDocument, serviceErrors, UserExperience.DIRECT);
        assertThat(clientApplicationDetailsDto.getInvestorAccountType(), is(SUPER_PENSION.value()));
        assertNotNull(clientApplicationDetailsDto.getPensionEligibility());
        assertThat(clientApplicationDetailsDto.getPensionEligibility().getConditionRelease(), is("condition of release long name for oth"));
        assertThat(clientApplicationDetailsDto.getPensionEligibility().getEligibilityCriteria(),
            is("eligibility criteria long name for unpsv"));
    }


    @Test
    public void testAccountAuthoriserNoPayments() {
        ServiceErrors serviceErrors = mock(ServiceErrors.class);
        ApplicationDocumentDetail applicationDocument = createApplicationDocumentDetailWithFees(serviceErrors, AccountStructure.I,
            ApprovalType.ONLINE);
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(applicationDocument, serviceErrors, UserExperience.ADVISED);
        String permission = clientApplicationDetailsDto.getAccountSettings().getPersonRelations().get(0).getPermissions();
        assertThat(permission, is("No Payments"));
    }

    @Test
    public void testShareholderRole() throws ParseException{
        ServiceErrors serviceErrors = mock(ServiceErrors.class);
        List<InvestorRole> investorRoles = Arrays.asList(InvestorRole.Shareholder, InvestorRole.Director);
        ApplicationDocumentDetail applicationDocument = createOrgApplicationDocument(serviceErrors, investorRoles);
        CompanyDetailsDto clientApplicationDetailsDto = (CompanyDetailsDto) clientApplicationDetailsDtoConverterService
            .convert(applicationDocument, serviceErrors, UserExperience.ASIM);

        assertThat(clientApplicationDetailsDto.getShareholders().size(), is(0));
        assertThat(clientApplicationDetailsDto.getInvestors().size(), is(1));
    }


    @Test
    public void testControllerOfTrustRole() throws ParseException{
        ServiceErrors serviceErrors = mock(ServiceErrors.class);
        List<InvestorRole> investorRoles = Arrays.asList(InvestorRole.ControllerOfTrust, InvestorRole.Director);
        ApplicationDocumentDetail applicationDocument = createOrgApplicationDocumentForCorporateTrust(serviceErrors, investorRoles);
        CorporateTrustApplicationDetailsDto clientApplicationDetailsDto = (CorporateTrustApplicationDetailsDto) clientApplicationDetailsDtoConverterService
                .convert(applicationDocument, serviceErrors, UserExperience.ADVISED);

        assertThat(clientApplicationDetailsDto.getShareholdersAndMembers().size(), is(0));
        assertThat(clientApplicationDetailsDto.isContainsNominatedInvestors(), is(true));

    }


    @Test
    public void testOfflineApprovalType_withOfflineApprovalAccess() throws Exception {
        final ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        ApplicationDocumentDetail applicationDocument = createApplicationDocumentDetailWithFees(serviceErrors, AccountStructure.SU,
            ApprovalType.OFFLINE);
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(applicationDocument, serviceErrors, UserExperience.ASIM);
        assertEquals(clientApplicationDetailsDto.getApprovalType(), "Offline");
        assertTrue(clientApplicationDetailsDto.isOfflineApprovalAccess());
    }

    @Test
    public void testOnlineApprovalType_withOfflineApprovalAccess() throws Exception {
        final ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        ApplicationDocumentDetail applicationDocument = createApplicationDocumentDetailWithFees(serviceErrors, AccountStructure.SU,
            ApprovalType.ONLINE);
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(applicationDocument, serviceErrors, UserExperience.ASIM);
        assertEquals(clientApplicationDetailsDto.getApprovalType(), "Online");
        assertTrue(clientApplicationDetailsDto.isOfflineApprovalAccess());
    }

    @Test
    public void testWithoutOfflineApprovalAccess() throws Exception {
        final ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        ApplicationDocumentDetail applicationDocument = createApplicationDocumentDetailWithFees(serviceErrors, AccountStructure.SU,
            ApprovalType.ONLINE);
        Broker dealerGroupBroker = getDealerGroupBroker(false, true, null);
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(dealerGroupBroker);
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(applicationDocument, serviceErrors, UserExperience.ASIM);
        assertEquals(clientApplicationDetailsDto.getApprovalType(), "Online");
        assertFalse(clientApplicationDetailsDto.isOfflineApprovalAccess());
    }

    @Test
    public void testDealerGroupName() throws Exception {
        final ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        ApplicationDocumentDetail applicationDocument = createApplicationDocumentDetailWithFees(serviceErrors, AccountStructure.I,
            ApprovalType.ONLINE);
        Broker dealerGroupBroker = getDealerGroupBroker(true, true, "My dealer group");
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(dealerGroupBroker);
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(applicationDocument, serviceErrors, UserExperience.ADVISED);
        assertEquals(clientApplicationDetailsDto.getAdviser().getDealerGroupName(), "My dealer group");
    }

    @Test
    public void testPracticeName() throws Exception {
        final ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        ApplicationDocumentDetail applicationDocument = createApplicationDocumentDetailWithFees(serviceErrors, AccountStructure.I,
            ApprovalType.ONLINE);
        Broker dealerGroupBroker = getDealerGroupBroker(true, true, "My practice");
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(dealerGroupBroker);
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
            .convert(applicationDocument, serviceErrors, UserExperience.ADVISED);
        assertEquals(clientApplicationDetailsDto.getAdviser().getDealerGroupName(), "My practice");
    }

    private Broker getDealerGroupBroker(boolean offlineApprovalAccess, boolean isDealerGroup, String dealerGroupName) {
        Broker dealerGroupBroker = mock(Broker.class);
        when(dealerGroupBroker.getParentKey()).thenReturn(BrokerKey.valueOf("123"));
        when(dealerGroupBroker.isOfflineApproval()).thenReturn(offlineApprovalAccess);

        if (isDealerGroup) {
            when(dealerGroupBroker.getDealerKey()).thenReturn(BrokerKey.valueOf("123"));
        } else {
            when(dealerGroupBroker.getPracticeKey()).thenReturn(BrokerKey.valueOf("123"));
        }

        when(dealerGroupBroker.getPositionName()).thenReturn(dealerGroupName);
        return dealerGroupBroker;
    }

    @Test
    public void convertShouldHaveNominatedMembersIfBenOwner() throws ParseException {
        ServiceErrors serviceErrors = mock(ServiceErrors.class);
        List<InvestorRole> investorRoles = Arrays.asList(InvestorRole.Director, InvestorRole.BeneficialOwner);
        ApplicationDocumentDetail applicationDocument = createOrgApplicationDocument(serviceErrors, investorRoles);
        CompanyDetailsDto clientApplicationDetailsDto = (CompanyDetailsDto) clientApplicationDetailsDtoConverterService
            .convert(applicationDocument, serviceErrors, UserExperience.ADVISED);
        assertThat(clientApplicationDetailsDto.isContainsNominatedInvestors(), is(true));
    }

    @Test
    public void convertShouldHaveCompanySecretaryRole() {
        ServiceErrors serviceErrors = mock(ServiceErrors.class);
        List<InvestorRole> investorRoles = Arrays.asList(InvestorRole.Director, InvestorRole.BeneficialOwner,InvestorRole.Secretary);
        ApplicationDocumentDetail applicationDocument = createOrgApplicationDocumentForCorporateSMSF(serviceErrors, investorRoles);
        CorporateSmsfApplicationDetailsDto clientApplicationDetailsDto = (CorporateSmsfApplicationDetailsDto) clientApplicationDetailsDtoConverterService
                .convert(applicationDocument, serviceErrors, UserExperience.ADVISED);
        assertThat(clientApplicationDetailsDto.getDirectors().get(0).getPersonRoles(), hasItems((InvestorRole.Secretary)));
        verify(personAccountSettingsMapperService,times(1)).mapPersonTaxDetails(any(List.class),any(List.class));
        verify(organisationMapper,times(1)).mapOrganisationTaxDetails(any(List.class),any(List.class));
    }

    @Test
    public void convertShouldHaveCMADetailsForTrust() {
        ServiceErrors serviceErrors = mock(ServiceErrors.class);
        List<InvestorRole> investorRoles = Arrays.asList(InvestorRole.Director);
        ApplicationDocumentDetail applicationDocument = createOrgApplicationDocumentForCorporateTrust(serviceErrors, investorRoles);
        CorporateTrustApplicationDetailsDto clientApplicationDetailsDto = (CorporateTrustApplicationDetailsDto) clientApplicationDetailsDtoConverterService
                .convert(applicationDocument, serviceErrors, UserExperience.ADVISED);

        AccountSettingsDto accountSettingsDto = clientApplicationDetailsDto.getAccountSettings();
        assertNotNull(accountSettingsDto.getPowerOfAttorney());
        assertThat(accountSettingsDto.getPowerOfAttorney(), is("No"));
        assertNotNull(clientApplicationDetailsDto.getTrust().getPersonalInvestmentEntity());
        assertThat(clientApplicationDetailsDto.getTrust().getPersonalInvestmentEntity(),is("Yes"));
        verify(personAccountSettingsMapperService,times(1)).mapPersonTaxDetails(any(List.class),any(List.class));
        verify(organisationMapper,times(1)).mapOrganisationTaxDetails(any(List.class),any(List.class));

    }

    @Test
    public void convertShouldHaveCMADetailsForTrust_Insufficient_BpData() {
        ServiceErrors serviceErrors = mock(ServiceErrors.class);
        List<InvestorRole> investorRoles = Arrays.asList(InvestorRole.Director);
        ApplicationDocumentDetail applicationDocument = createOrgApplicationDocumentForCorporateTrust(serviceErrors, investorRoles);
        List<BPClassList> bpClassLists = new ArrayList<>();
        BPClassListImpl bpClassListPOA = new BPClassListImpl();
        bpClassListPOA.setBpClassifierId(CashManagementAccountType.POWER_OF_ATTORNEY);
        bpClassListPOA.setBpClassIdVal(null);
        bpClassLists.add(bpClassListPOA);
        when(applicationDocument.getAccountClassList()).thenReturn(bpClassLists);
        CorporateTrustApplicationDetailsDto clientApplicationDetailsDto = (CorporateTrustApplicationDetailsDto) clientApplicationDetailsDtoConverterService
                .convert(applicationDocument, serviceErrors, UserExperience.ADVISED);

        AccountSettingsDto accountSettingsDto = clientApplicationDetailsDto.getAccountSettings();
        assertNull(accountSettingsDto.getPowerOfAttorney());
        verify(personAccountSettingsMapperService,times(1)).mapPersonTaxDetails(any(List.class),any(List.class));
        verify(organisationMapper,times(1)).mapOrganisationTaxDetails(any(List.class),any(List.class));

    }

    @Test
    public void convertShouldHaveCMADetailsForTrust_Empty_BpData() {
        ServiceErrors serviceErrors = mock(ServiceErrors.class);
        List<InvestorRole> investorRoles = Arrays.asList(InvestorRole.Director);
        ApplicationDocumentDetail applicationDocument = createOrgApplicationDocumentForCorporateTrust(serviceErrors, investorRoles);
        List<BPClassList> bpClassLists = new ArrayList<>();
        when(applicationDocument.getAccountClassList()).thenReturn(bpClassLists);
        CorporateTrustApplicationDetailsDto clientApplicationDetailsDto = (CorporateTrustApplicationDetailsDto) clientApplicationDetailsDtoConverterService
                .convert(applicationDocument, serviceErrors, UserExperience.ADVISED);

        AccountSettingsDto accountSettingsDto = clientApplicationDetailsDto.getAccountSettings();
        assertNull(accountSettingsDto.getPowerOfAttorney());
        verify(personAccountSettingsMapperService,times(1)).mapPersonTaxDetails(any(List.class),any(List.class));
        verify(organisationMapper,times(1)).mapOrganisationTaxDetails(any(List.class),any(List.class));
        verify(clientApplicationDetailsDtoHelperService,times(1)).getExistingPersonsByCISKey(anyString(),any(List.class), any(ServiceErrors.class));
    }

    @Test
    public void convertShouldHaveCMADetailsForCorporateSMSF() {

       ServiceErrors serviceErrors = mock(ServiceErrors.class);
       List<InvestorRole> investorRoles = Arrays.asList(InvestorRole.Director, InvestorRole.BeneficialOwner);
       ApplicationDocumentDetail applicationDocument = createOrgApplicationDocumentForCorporateSMSF(serviceErrors, investorRoles);
       CorporateSmsfApplicationDetailsDto clientApplicationDetailsDto = (CorporateSmsfApplicationDetailsDto) clientApplicationDetailsDtoConverterService
                    .convert(applicationDocument, serviceErrors, UserExperience.ADVISED);
        AccountSettingsDto accountSettingsDto = clientApplicationDetailsDto.getAccountSettings();
        assertNotNull(accountSettingsDto.getPowerOfAttorney());
        assertThat(accountSettingsDto.getPowerOfAttorney(), is("Yes"));
        verify(clientApplicationDetailsDtoHelperService,times(1)).getExistingPersonsByCISKey(anyString(),any(List.class), any(ServiceErrors.class));
    }

    @Test
    public void convertShouldNotHaveLinkedAccountsForCorporateSMSF() {

        ServiceErrors serviceErrors = mock(ServiceErrors.class);
        List<InvestorRole> investorRoles = Arrays.asList(InvestorRole.Director, InvestorRole.BeneficialOwner);
        ApplicationDocumentDetail applicationDocument = createOrgApplicationDocumentForCorporateSMSF(serviceErrors, investorRoles);
        CorporateSmsfApplicationDetailsDto clientApplicationDetailsDto = (CorporateSmsfApplicationDetailsDto) clientApplicationDetailsDtoConverterService
            .convert(applicationDocument, serviceErrors, UserExperience.ADVISED);
        assertThat(clientApplicationDetailsDto.getLinkedAccounts(), is(EMPTY_LIST));
    }

    @Test
    public void cma_convertShouldHaveParentProductName() {

        ServiceErrors serviceErrors = mock(ServiceErrors.class);
        List<InvestorRole> investorRoles = Arrays.asList(InvestorRole.Director, InvestorRole.BeneficialOwner);
        ApplicationDocumentDetail applicationDocument = createOrgApplicationDocumentForCorporateSMSF(serviceErrors, investorRoles);
        Product mockCMAProduct = createMockCMAProduct();
        when(productIntegrationService.getProductDetail(any(ProductKey.class), any(ServiceErrors.class))).thenReturn(mockCMAProduct);
        CorporateSmsfApplicationDetailsDto clientApplicationDetailsDto = (CorporateSmsfApplicationDetailsDto) clientApplicationDetailsDtoConverterService
            .convert(applicationDocument, serviceErrors, UserExperience.ADVISED);
        assertThat(clientApplicationDetailsDto.getParentProductName(), is("CMA Product"));
    }

    @Test
    public void convertShouldHaveLinkedAccountsForCorporateSMSF() {

        ServiceErrors serviceErrors = mock(ServiceErrors.class);
        List<InvestorRole> investorRoles = Arrays.asList(InvestorRole.Director, InvestorRole.BeneficialOwner);
        ApplicationDocumentDetail applicationDocument = createOrgApplicationDocumentForCorporateSMSFWithLinkedAccounts(serviceErrors, investorRoles);
        CorporateSmsfApplicationDetailsDto clientApplicationDetailsDto = (CorporateSmsfApplicationDetailsDto) clientApplicationDetailsDtoConverterService
            .convert(applicationDocument, serviceErrors, UserExperience.ADVISED);

        assertThat(clientApplicationDetailsDto.getLinkedAccounts().size(), is(2));

        LinkedAccountDto linkedAccountDto = clientApplicationDetailsDto.getLinkedAccounts().get(0);
        assertThat(linkedAccountDto.getAccountNumber(), is("123123123"));
        assertThat(linkedAccountDto.getNickName(), is("Acc1Nick"));
        assertThat(linkedAccountDto.getName(), is("Acc1"));
        assertThat(linkedAccountDto.getBsb(), is("633-633"));
        assertThat(linkedAccountDto.getDirectDebitAmount(), is(new BigDecimal("10.00")));
        assertThat(linkedAccountDto.isPrimary(), is(true));

        LinkedAccountDto linkedAccountDto2 = clientApplicationDetailsDto.getLinkedAccounts().get(1);
        assertThat(linkedAccountDto2.getAccountNumber(), is("123123111"));
        assertThat(linkedAccountDto2.getNickName(), is("Acc2Nick"));
        assertThat(linkedAccountDto2.getName(), is("Acc2"));
        assertThat(linkedAccountDto2.getBsb(), is("633-611"));
        assertThat(linkedAccountDto2.getDirectDebitAmount(), is(new BigDecimal("1.00")));
        assertThat(linkedAccountDto2.isPrimary(), is(false));
    }

    private ApplicationDocumentDetail createOrgApplicationDocumentForCorporateSMSFWithLinkedAccounts(ServiceErrors serviceErrors, List<InvestorRole> investorRoles) {

        ApplicationDocumentDetail appDocDetail = createOrgApplicationDocumentForCorporateSMSF(serviceErrors, investorRoles);
        List<RegisteredAccountImpl> linkedAccounts = new ArrayList<RegisteredAccountImpl>();
        RegisteredAccountImpl linkedAcc1 = new RegisteredAccountImpl();
        linkedAcc1.setAccountNumber("123123123");
        linkedAcc1.setBsb("633633");
        linkedAcc1.setName("Acc1");
        linkedAcc1.setNickName("Acc1Nick");
        linkedAcc1.setInitialDeposit(new BigDecimal("10.00"));
        linkedAcc1.setPrimary(true);
        linkedAccounts.add(linkedAcc1);

        RegisteredAccountImpl linkedAcc2 = new RegisteredAccountImpl();
        linkedAcc2.setAccountNumber("123123111");
        linkedAcc2.setBsb("633611");
        linkedAcc2.setName("Acc2");
        linkedAcc2.setNickName("Acc2Nick");
        linkedAcc2.setInitialDeposit(new BigDecimal("1.00"));
        linkedAcc2.setPrimary(false);
        linkedAccounts.add(linkedAcc2);

        when(appDocDetail.getLinkedAccounts()).thenReturn(linkedAccounts);

        return appDocDetail;
    }

    @Test
    public void testApplicationOpenDate_Advised() throws ParseException {
        ServiceErrors serviceErrors = mock(ServiceErrors.class);
        List<InvestorRole> investorRoles = Arrays.asList(InvestorRole.Director, InvestorRole.BeneficialOwner);
        ApplicationDocumentDetail applicationDocument = createOrgApplicationDocument(serviceErrors, investorRoles);
        CompanyDetailsDto clientApplicationDetailsDto = (CompanyDetailsDto) clientApplicationDetailsDtoConverterService
                .convert(applicationDocument, serviceErrors, UserExperience.ADVISED);
        assertThat(clientApplicationDetailsDto.getApplicationOpenDate(), is(applicationDocument.getApplicationOpenDate()));
        verify(personAccountSettingsMapperService,times(1)).mapPersonTaxDetails(any(List.class),any(List.class));
        verify(organisationMapper,times(1)).mapOrganisationTaxDetails(any(List.class),any(List.class));
        verify(clientApplicationDetailsDtoHelperService,times(1)).getExistingPersonsByCISKey(anyString(),any(List.class), any(ServiceErrors.class));
    }

    @Test
    public void testApplicationOpenDate_Direct() throws ParseException {
        ServiceErrors serviceErrors = mock(ServiceErrors.class);
        List<InvestorRole> investorRoles = Arrays.asList(InvestorRole.Director, InvestorRole.BeneficialOwner);
        ApplicationDocumentDetail applicationDocument = createOrgApplicationDocument(serviceErrors, investorRoles);
        CompanyDetailsDto clientApplicationDetailsDto = (CompanyDetailsDto) clientApplicationDetailsDtoConverterService
                .convert(applicationDocument, serviceErrors, UserExperience.DIRECT);
        assertThat(clientApplicationDetailsDto.getApplicationOpenDate(), is(applicationDocument.getApplicationOpenDate()));
        assertThat(clientApplicationDetailsDto.getParentProductName(), is(""));
    }

    @Test
    @Ignore
    public void convert_shouldCallCRSUtilityForExistingUser() throws Exception {
        clientApplication.setFormData(readJsonFromFile("client_application_form_data_joint_existing.json"));
        ClientKey clientKey = new ClientKey("C760CDDA457EEA4BA402D686CA51D201E611C0480380EB7F");
        List<InvestorRole> personRoles = Arrays.asList(InvestorRole.Owner);

        InvestorDto existingClientDto = new InvestorDto();
        existingClientDto.setKey(clientKey);

        when(clientListDtoService.find(eq(clientKey), any(ServiceErrors.class))).thenReturn(existingClientDto);
        when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(featureToggles);
        when(individualDtoConverter.getPersonRoles(any(IExtendedPersonDetailsForm.class))).thenReturn(personRoles);

        IndividualOrJointApplicationDetailsDto clientApplicationDetailsDto
                = (IndividualOrJointApplicationDetailsDto) clientApplicationDetailsDtoConverterService.convert(clientApplication, null);
        assertThat(clientApplicationDetailsDto.getInvestors().size(), is(2));
        assertThat(clientApplicationDetailsDto.getInvestors().get(1).getPersonRoles(), is(personRoles));
        verify(crsTaxDetailHelperService, times(1)).populateCRSTaxDetailsForIndividual(any(IPersonDetailsForm.class),any(InvestorDto.class));
    }

    @Test
    public void testCorporateTrust_CallCRSUtility() throws ParseException{
        ServiceErrors serviceErrors = mock(ServiceErrors.class);
        List<InvestorRole> investorRoles = Arrays.asList(InvestorRole.ControllerOfTrust, InvestorRole.Director);
        ApplicationDocumentDetail applicationDocument = createOrgApplicationDocumentForCorporateTrust(serviceErrors, investorRoles);
        CorporateTrustApplicationDetailsDto clientApplicationDetailsDto = (CorporateTrustApplicationDetailsDto) clientApplicationDetailsDtoConverterService
                .convert(applicationDocument, serviceErrors, UserExperience.ADVISED);
        verify(personAccountSettingsMapperService,times(1)).mapPersonTaxDetails(any(List.class),any(List.class));
        verify(organisationMapper,times(1)).mapOrganisationTaxDetails(any(List.class),any(List.class));

    }

    @Test
    public void testContributionFees_IDPS() {
        ServiceErrors serviceErrors = mock(ServiceErrors.class);

        ApplicationDocumentDetail applicationDocument = createOrgApplicationDocumentWithoutAdviserSettings(IDPS,true,false,serviceErrors);
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
                .convert(applicationDocument, serviceErrors, UserExperience.ASIM);
        assertNotNull(clientApplicationDetailsDto.getFees());

        Map<String,Map<String,List<Object>>> contributionFees = (Map<String,Map<String,List<Object>>>)clientApplicationDetailsDto.getFees();
        assertNotNull(contributionFees);
        assertNotNull(contributionFees.get("contributionFees"));
        assertEquals(contributionFees.get("contributionFees").get("feesComponent").size(), 2);
        assertEquals(contributionFees.get("contributionFees").get("type"),"Adviser contribution fee");
    }

    @Test
    public void testContributionFees_IDPS_Error() {
        ServiceErrors serviceErrors = mock(ServiceErrors.class);

        ApplicationDocumentDetail applicationDocument = createOrgApplicationDocumentWithoutAdviserSettings(IDPS,true,true,serviceErrors);
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
                .convert(applicationDocument, serviceErrors, UserExperience.ASIM);
        assertNotNull(clientApplicationDetailsDto.getFees());

        Map<String,Map<String,List<Object>>> contributionFees = (Map<String,Map<String,List<Object>>>)clientApplicationDetailsDto.getFees();
        assertNotNull(contributionFees);
        assertNotNull(contributionFees.get("contributionFees"));
        assertEquals(contributionFees.get("contributionFees").get("feesComponent").size(), 2);
        assertEquals(contributionFees.get("contributionFees").get("type"),"Adviser contribution fee");
    }


    @Test
    public void testContributionFees_Super() {
        ServiceErrors serviceErrors = mock(ServiceErrors.class);

        ApplicationDocumentDetail applicationDocument = createOrgApplicationDocumentWithoutAdviserSettings(SUPER, true,false, serviceErrors);
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoConverterService
                .convert(applicationDocument, serviceErrors, UserExperience.ASIM);
        assertNotNull(clientApplicationDetailsDto.getFees());

        Map<String,Map<String,List<Object>>> contributionFees = (Map<String,Map<String,List<Object>>>)clientApplicationDetailsDto.getFees();
        assertNotNull(contributionFees);
        assertNotNull(contributionFees.get("contributionFees"));
        assertEquals(contributionFees.get("contributionFees").get("feesComponent").size(),2);
        assertEquals(contributionFees.get("contributionFees").get("type"),"Adviser contribution fee");
    }


    private ApplicationDocumentDetail createOrgApplicationDocumentWithoutAdviserSettings(String accountType,boolean isContributionFeesIncluded,boolean isError,ServiceErrors serviceErrors) {
        ApplicationDocumentDetailImpl applicationDocument = mock(ApplicationDocumentDetailImpl.class);
        when(applicationDocument.getAccountNumber()).thenReturn("12345");
        List<FeesSchedule> fees = new ArrayList<>();
        FeesSchedule onGoingFees = new FeesScheduleImpl();
        onGoingFees.setType(FeesType.ONGOING_FEE);

        List<FeesComponents> onGoingFeesComponents = new ArrayList<>();
        onGoingFeesComponents.add(createDollarFeesComponent(17, true));
        onGoingFeesComponents.add(createSlidingScaleFeesComponent());

        onGoingFees.setFeesComponents(onGoingFeesComponents);
        fees.add(onGoingFees);

        FeesScheduleImpl licenseeFees = new FeesScheduleImpl();
        licenseeFees.setType(FeesType.LICENSEE_FEE);

        FeesScheduleImpl contributionFees = new FeesScheduleImpl();
        contributionFees.setFeesType(FeesType.CONTRIBUTION_FEE);

        List<FeesComponents> licenseeFeesComponents = new ArrayList<>();
        licenseeFeesComponents.add(createDollarFeesComponent(45, false));
        licenseeFeesComponents.add(createPercentageFeesComponent(0.0011, FeesMiscType.PERCENT_MANAGED_PORTFOLIO));
        licenseeFeesComponents.add(createPercentageFeesComponent(0.0022, FeesMiscType.PERCENT_TERM_DEPOSIT));
        licenseeFeesComponents.add(createPercentageFeesComponent(0.0033, FeesMiscType.PERCENT_CASH));
        licenseeFeesComponents.add(createPercentageFeesComponent(0.0044, FeesMiscType.PERCENT_MANAGED_FUND));
        licenseeFeesComponents.add(createPercentageFeesComponent(0.0055, FeesMiscType.PERCENT_SHARE));

        List<FeesComponents> adviserContributionComponents = new ArrayList<>();
        if(isContributionFeesIncluded){
            if(IDPS.equalsIgnoreCase(accountType)){
                adviserContributionComponents.add(createOneOffFeesComponent(0.001,FeesMiscType.ONEOFF_DEPOSIT));
                adviserContributionComponents.add(createRegularFeesComponent(0.002,FeesMiscType.REGULAR_DEPOSIT));
                if(isError){
                    adviserContributionComponents.add(createRegularFeesComponent(0.002,FeesMiscType.MIN_MAX));
                    adviserContributionComponents.add(createRegularFeesComponent(0.002,FeesMiscType.MIN_MAX));
                }
            }else{
                adviserContributionComponents.add(createOneOffFeesComponent(0.001,FeesMiscType.EMPLOYER_CONTRIBUTION));
                adviserContributionComponents.add(createOneOffFeesComponent(0.002,FeesMiscType.ONEOFF_PERSONAL_CONTRIBUTION));
                adviserContributionComponents.add(createOneOffFeesComponent(0.002,FeesMiscType.ONEOFF_SPOUSE_CONTRIBUTION));
                adviserContributionComponents.add(createRegularFeesComponent(0.002,FeesMiscType.REGULAR_PERSONAL_CONTRIBUTION));
                adviserContributionComponents.add(createRegularFeesComponent(0.002,FeesMiscType.REGULAR_SPOUSE_CONTRIBUTION));
                if(isError){
                    adviserContributionComponents.add(createOneOffFeesComponent(0.001,FeesMiscType.MIN_MAX));
                    adviserContributionComponents.add(createRegularFeesComponent(0.002,FeesMiscType.MIN_MAX));
                }
            }
            contributionFees.setFeesComponents(adviserContributionComponents);
            fees.add(contributionFees);
        }

        licenseeFees.setFeesComponents(licenseeFeesComponents);

        fees.add(licenseeFees);

        FeesScheduleImpl establishmentFess = new FeesScheduleImpl();
        establishmentFess.setFeesType(FeesType.AVSR_ESTAB);
        List<FeesComponents> establishmentFeesComponents = new ArrayList<>();
        establishmentFeesComponents.add(createDollarFeesComponent(-500, false));
        establishmentFess.setFeesComponents(establishmentFeesComponents);
        fees.add(establishmentFess);

        when(applicationDocument.getFees()).thenReturn(fees);


        //Investor Account Settings
        List<PersonDetail> accountSettingsForAllPersons = mock(List.class);
        AccountAuthoriser investorAccountSettings = mock(AccountAuthoriser.class);

        List<AccountAuthoriser> investorAccountSettingsList = new ArrayList<>();
        investorAccountSettingsList.add(investorAccountSettings);
        PersonDetail person = mock(PersonDetail.class);
        when(accountSettingsForAllPersons.isEmpty()).thenReturn(true);
        when(person.getAccountAuthorisationList()).thenReturn(investorAccountSettingsList);
        accountSettingsForAllPersons.add(person);
        when(applicationDocument.getAccountSettingsForAllPersons()).thenReturn(accountSettingsForAllPersons);
        //Portfolio
        List mockedPortfolioList = createMockedPortfolioList(AccountStructure.I);
        applicationDocument.setPortfolio(mockedPortfolioList);
        when(applicationDocument.getPortfolio()).thenReturn(mockedPortfolioList);


        //AdviserKey
        BrokerKey mockAdviserKey = mock(BrokerKey.class);
        applicationDocument.setAdviserKey(mockAdviserKey);
        when(applicationDocument.getAdviserKey()).thenReturn(mockAdviserKey);

        //BrokerService
        BrokerUser mockBroker = createMockBroker();
        when(brokerIntegrationService.getAdviserBrokerUser(mockAdviserKey, serviceErrors)).thenReturn(mockBroker);

        //LinkedAccounts
        when(applicationDocument.getLinkedAccounts()).thenReturn(Collections.EMPTY_LIST);

        //Persons
        List<PersonDetail> mockPersons = createMockPersonList();
        List mockAddress = mock(List.class);
        when(applicationDocument.getPersons()).thenReturn(mockPersons);

        InvestorDto mockInvestor = mock(InvestorDto.class);

        when(mockAddress.isEmpty()).thenReturn(true);
        when(investorDtoConverterForPersonDetail.convertFromPersonDetail(mockPersons.get(0), null,null)).thenReturn(mockInvestor);
        Product mockProduct = createMockProduct();
        when(productIntegrationService.getProductDetail(any(ProductKey.class), any(ServiceErrors.class))).thenReturn(mockProduct);
        return applicationDocument;
    }

    private ApplicationDocumentDetail createOrgApplicationDocumentForCorporateSMSF(ServiceErrors serviceErrors, List<InvestorRole> investorRoles) {
        ApplicationDocumentDetailImpl applicationDocument = mock(ApplicationDocumentDetailImpl.class);
        when(applicationDocument.getAccountNumber()).thenReturn("12345");
        List<FeesSchedule> fees = new ArrayList<>();
        FeesSchedule onGoingFees = new FeesScheduleImpl();
        onGoingFees.setType(FeesType.ONGOING_FEE);

        List<FeesComponents> onGoingFeesComponents = new ArrayList<>();
        onGoingFeesComponents.add(createDollarFeesComponent(17, true));
        onGoingFeesComponents.add(createSlidingScaleFeesComponent());

        onGoingFees.setFeesComponents(onGoingFeesComponents);
        fees.add(onGoingFees);
        when(applicationDocument.getFees()).thenReturn(fees);
        //Adviser Account Settings
        List<AccountAuthoriser> adviserAccountSettingsList = new ArrayList<>();
        AccountAuthoriser adviserAccountSettings = mock(AccountAuthoriser.class);
        when(adviserAccountSettings.getTxnType()).thenReturn(TransactionPermission.Account_Maintenance);
        adviserAccountSettingsList.add(adviserAccountSettings);

        when(applicationDocument.getAdviserAccountSettings()).thenReturn(adviserAccountSettingsList);

        //Investor Account Settings
        List<PersonDetail> accountSettingsForAllPersons = mock(List.class);
        AccountAuthoriser investorAccountSettings = mock(AccountAuthoriser.class);
        when(adviserAccountSettings.getTxnType()).thenReturn(TransactionPermission.Account_Maintenance);
        List<AccountAuthoriser> investorAccountSettingsList = new ArrayList<>();
        investorAccountSettingsList.add(investorAccountSettings);
        PersonDetail person = mock(PersonDetail.class);
        when(accountSettingsForAllPersons.isEmpty()).thenReturn(true);
        when(person.getAccountAuthorisationList()).thenReturn(investorAccountSettingsList);
        accountSettingsForAllPersons.add(person);
        when(applicationDocument.getAccountSettingsForAllPersons()).thenReturn(accountSettingsForAllPersons);
        //Portfolio
        List mockedPortfolioList = createMockedCorporateSMSFPortfolioList();
        applicationDocument.setPortfolio(mockedPortfolioList);
        when(applicationDocument.getPortfolio()).thenReturn(mockedPortfolioList);
        when(applicationDocument.getOrderType()).thenReturn(OrderType.NewCorporateSMSF.getOrderType());


        //AdviserKey
        BrokerKey mockAdviserKey = mock(BrokerKey.class);
        applicationDocument.setAdviserKey(mockAdviserKey);
        when(applicationDocument.getAdviserKey()).thenReturn(mockAdviserKey);

        //BrokerService
        BrokerUser mockBroker = createMockBroker();
        when(brokerIntegrationService.getAdviserBrokerUser(mockAdviserKey, serviceErrors)).thenReturn(mockBroker);

        //LinkedAccounts
        when(applicationDocument.getLinkedAccounts()).thenReturn(Collections.EMPTY_LIST);

        //BPClass Data for CMA
        List<BPClassList> bpClassLists = new ArrayList<BPClassList>();

        BPClassListImpl bpClassListPOA = new BPClassListImpl();
        bpClassListPOA.setBpClassifierId(CashManagementAccountType.POWER_OF_ATTORNEY);
        bpClassListPOA.setBpClassIdVal(CashManagementAccountValues.BP_CLASS_YES);
        bpClassLists.add(bpClassListPOA);

        when(applicationDocument.getAccountClassList()).thenReturn(bpClassLists);

        //Persons
        List<PersonDetail> mockPersons = createMockPersonList();
        List mockAddress = mock(List.class);
        when(applicationDocument.getPersons()).thenReturn(mockPersons);
        InvestorDto mockInvestor = mock(InvestorDto.class);
        when(mockAddress.isEmpty()).thenReturn(true);
        when(mockInvestor.getPrimaryRole()).thenReturn(PersonRelationship.DIRECTOR);

        when(mockInvestor.getPersonRoles()).thenReturn(investorRoles);
        when(investorDtoConverterForPersonDetail.convertFromPersonDetail(mockPersons.get(0), null, new HashMap())).thenReturn(mockInvestor);

        SmsfDto registeredEntityDto = mock(SmsfDto.class);
        when(organizationDtoConverterForApplicationDocument
                .getOrganisationDetailsFromApplicationDocument(any(ApplicationDocumentDetail.class),
                        any(IClientApplicationForm.AccountType.class))).thenReturn(registeredEntityDto);
        Organisation organisation = mock(Organisation.class);
        when(organisation.getFullName()).thenReturn("Test Org SMSF");
        when(organizationDtoConverterForApplicationDocument.getOrganisation(any(List.class), any(IClientApplicationForm.AccountType.class)))
                .thenReturn(organisation);
        Product mockProduct = createMockProduct();
        when(productIntegrationService.getProductDetail(any(ProductKey.class), any(ServiceErrors.class))).thenReturn(mockProduct);

        return applicationDocument;
    }

    private ApplicationDocumentDetail createOrgApplicationDocumentForCorporateTrust(ServiceErrors serviceErrors, List<InvestorRole> investorRoles) {
        ApplicationDocumentDetailImpl applicationDocument = mock(ApplicationDocumentDetailImpl.class);

        when(applicationDocument.getAccountNumber()).thenReturn("12345");
        //Adviser Account Settings
        List<AccountAuthoriser> adviserAccountSettingsList = new ArrayList<>();
        AccountAuthoriser adviserAccountSettings = mock(AccountAuthoriser.class);
        when(adviserAccountSettings.getTxnType()).thenReturn(TransactionPermission.Account_Maintenance);
        adviserAccountSettingsList.add(adviserAccountSettings);

        when(applicationDocument.getAdviserAccountSettings()).thenReturn(adviserAccountSettingsList);

        //Investor Account Settings
        List<PersonDetail> accountSettingsForAllPersons = mock(List.class);
        AccountAuthoriser investorAccountSettings = mock(AccountAuthoriser.class);
        when(adviserAccountSettings.getTxnType()).thenReturn(TransactionPermission.Account_Maintenance);
        List<AccountAuthoriser> investorAccountSettingsList = new ArrayList<>();
        investorAccountSettingsList.add(investorAccountSettings);
        PersonDetail person = mock(PersonDetail.class);
        when(accountSettingsForAllPersons.isEmpty()).thenReturn(true);
        when(person.getAccountAuthorisationList()).thenReturn(investorAccountSettingsList);
        accountSettingsForAllPersons.add(person);
        when(applicationDocument.getAccountSettingsForAllPersons()).thenReturn(accountSettingsForAllPersons);
        //Portfolio
        List mockedPortfolioListForTrust = createMockedCorporateTrustPortfolioList();
        applicationDocument.setPortfolio(mockedPortfolioListForTrust);
        when(applicationDocument.getPortfolio()).thenReturn(mockedPortfolioListForTrust);
        when(applicationDocument.getOrderType()).thenReturn(OrderType.Default.getOrderType());


        //AdviserKey
        BrokerKey mockAdviserKey = mock(BrokerKey.class);
        applicationDocument.setAdviserKey(mockAdviserKey);
        when(applicationDocument.getAdviserKey()).thenReturn(mockAdviserKey);

        //BrokerService
        BrokerUser mockBroker = createMockBroker();
        when(brokerIntegrationService.getAdviserBrokerUser(mockAdviserKey, serviceErrors)).thenReturn(mockBroker);

        //LinkedAccounts
        when(applicationDocument.getLinkedAccounts()).thenReturn(Collections.EMPTY_LIST);

        //Persons
        List<PersonDetail> mockPersons = createMockPersonList();
        List mockAddress = mock(List.class);
        when(applicationDocument.getPersons()).thenReturn(mockPersons);

        //BPClass Data for CMA
        List<BPClassList> bpClassLists = new ArrayList<BPClassList>();
        BPClassListImpl bpClassListPIE = new BPClassListImpl();
        bpClassListPIE.setBpClassifierId(CashManagementAccountType.PERSONAL_INVESTMENT_ENTITY);
        bpClassListPIE.setBpClassIdVal(CashManagementAccountValues.BP_CLASS_YES);

        BPClassListImpl bpClassListPOA = new BPClassListImpl();
        bpClassListPOA.setBpClassifierId(CashManagementAccountType.POWER_OF_ATTORNEY);
        bpClassListPOA.setBpClassIdVal(CashManagementAccountValues.BP_CLASS_NO);

        bpClassLists.add(bpClassListPIE);
        bpClassLists.add(bpClassListPOA);

        when(applicationDocument.getAccountClassList()).thenReturn(bpClassLists);
        when(applicationDocument.getAccountClassList()).thenReturn(bpClassLists);
        InvestorDto mockInvestor = mock(InvestorDto.class);
        when(mockAddress.isEmpty()).thenReturn(true);
        when(mockInvestor.getPrimaryRole()).thenReturn(PersonRelationship.DIRECTOR);
        when(mockInvestor.getPersonRoles()).thenReturn(investorRoles);

        when(investorDtoConverterForPersonDetail.convertFromPersonDetail(mockPersons.get(0),null, new HashMap<String,Boolean>())).thenReturn(mockInvestor);

        TrustDto registeredEntityDto = mock(TrustDto.class);
        when(registeredEntityDto.getPersonalInvestmentEntity()).thenReturn("Yes");
        when(registeredEntityDto.getTrustType()).thenReturn("family");
        when(organizationDtoConverterForApplicationDocument
                .getOrganisationDetailsFromApplicationDocument(any(ApplicationDocumentDetail.class),
                        any(IClientApplicationForm.AccountType.class))).thenReturn(registeredEntityDto);
        Organisation organisation = mock(Organisation.class);
        when(organisation.getFullName()).thenReturn("Test Corp Trust");
        when(organizationDtoConverterForApplicationDocument.getOrganisation(any(List.class), any(IClientApplicationForm.AccountType.class)))
                .thenReturn(organisation);
        Product mockProduct = createMockProduct();
        when(productIntegrationService.getProductDetail(any(ProductKey.class), any(ServiceErrors.class))).thenReturn(mockProduct);

        return applicationDocument;
    }


    private ApplicationDocumentDetail createOrgApplicationDocument(ServiceErrors serviceErrors, List<InvestorRole> investorRoles) throws ParseException {
        ApplicationDocumentDetailImpl applicationDocument = mock(ApplicationDocumentDetailImpl.class);
        when(applicationDocument.getAccountNumber()).thenReturn("12345");
        List<FeesSchedule> fees = new ArrayList<>();
        FeesSchedule onGoingFees = new FeesScheduleImpl();
        onGoingFees.setType(FeesType.ONGOING_FEE);

        List<FeesComponents> onGoingFeesComponents = new ArrayList<>();
        onGoingFeesComponents.add(createDollarFeesComponent(17, true));
        onGoingFeesComponents.add(createSlidingScaleFeesComponent());

        onGoingFees.setFeesComponents(onGoingFeesComponents);
        fees.add(onGoingFees);
        when(applicationDocument.getFees()).thenReturn(fees);
        //Adviser Account Settings
        List<AccountAuthoriser> adviserAccountSettingsList = new ArrayList<>();
        AccountAuthoriser adviserAccountSettings = mock(AccountAuthoriser.class);
        when(adviserAccountSettings.getTxnType()).thenReturn(TransactionPermission.Account_Maintenance);
        adviserAccountSettingsList.add(adviserAccountSettings);

        when(applicationDocument.getAdviserAccountSettings()).thenReturn(adviserAccountSettingsList);

        //Investor Account Settings
        List<PersonDetail> accountSettingsForAllPersons = mock(List.class);
        AccountAuthoriser investorAccountSettings = mock(AccountAuthoriser.class);
        when(adviserAccountSettings.getTxnType()).thenReturn(TransactionPermission.Account_Maintenance);
        List<AccountAuthoriser> investorAccountSettingsList = new ArrayList<>();
        investorAccountSettingsList.add(investorAccountSettings);
        PersonDetail person = mock(PersonDetail.class);
        when(accountSettingsForAllPersons.isEmpty()).thenReturn(true);
        when(person.getAccountAuthorisationList()).thenReturn(investorAccountSettingsList);
        accountSettingsForAllPersons.add(person);
        when(applicationDocument.getAccountSettingsForAllPersons()).thenReturn(accountSettingsForAllPersons);
        //Portfolio
        List mockedPortfolioList = createMockedCompanyPortfolioList();
        applicationDocument.setPortfolio(mockedPortfolioList);
        when(applicationDocument.getPortfolio()).thenReturn(mockedPortfolioList);


        //Application Open Date
        SimpleDateFormat s = new SimpleDateFormat("yyyy-mm-dd");
        when(applicationDocument.getApplicationOpenDate()).thenReturn(s.parse("2016-12-29"));

        //AdviserKey
        BrokerKey mockAdviserKey = mock(BrokerKey.class);
        when(mockAdviserKey.getId()).thenReturn("1234567");
        applicationDocument.setAdviserKey(mockAdviserKey);
        when(applicationDocument.getAdviserKey()).thenReturn(mockAdviserKey);

        //BrokerService
        BrokerUser mockBroker = createMockBroker();
        when(brokerIntegrationService.getAdviserBrokerUser(mockAdviserKey, serviceErrors)).thenReturn(mockBroker);

        //LinkedAccounts
        when(applicationDocument.getLinkedAccounts()).thenReturn(Collections.EMPTY_LIST);

        //Persons
        List<PersonDetail> mockPersons = createMockPersonList();
        List mockAddress = mock(List.class);
        when(applicationDocument.getPersons()).thenReturn(mockPersons);
        InvestorDto mockInvestor = mock(InvestorDto.class);
        when(mockAddress.isEmpty()).thenReturn(true);
        when(mockInvestor.getPrimaryRole()).thenReturn(PersonRelationship.DIRECTOR);

        when(mockInvestor.getPersonRoles()).thenReturn(investorRoles);
        when(investorDtoConverterForPersonDetail.convertFromPersonDetail(mockPersons.get(0), null, new HashMap<String,Boolean>())).thenReturn(mockInvestor);

        CompanyDto registeredEntityDto = mock(CompanyDto.class);
        when(organizationDtoConverterForApplicationDocument
            .getOrganisationDetailsFromApplicationDocument(any(ApplicationDocumentDetail.class),
                any(IClientApplicationForm.AccountType.class))).thenReturn(registeredEntityDto);
        Organisation organisation = mock(Organisation.class);
        when(organisation.getFullName()).thenReturn("Test Org");
        when(organizationDtoConverterForApplicationDocument.getOrganisation(any(List.class), any(IClientApplicationForm.AccountType.class)))
            .thenReturn(organisation);
        Product mockProduct = createMockProduct();
        when(productIntegrationService.getProductDetail(any(ProductKey.class), any(ServiceErrors.class))).thenReturn(mockProduct);

        return applicationDocument;
    }


    private ApplicationDocumentDetail createApplicationDocumentDetailWithFeesAndPensionEligibility(ServiceErrors serviceErrors,
                                                                                                   ApprovalType approvalType) {
        ApplicationDocumentDetail applicationDocument = createApplicationDocumentDetailWithFees(serviceErrors, AccountStructure.SU,
            approvalType);
        when(applicationDocument.getSuperAccountSubType()).thenReturn(AccountSubType.PENSION);
        when(applicationDocument.getPensionEligibility())
            .thenReturn(createPensionEligibility(EligibilityCriteria.UNPSV, ConditionOfRelease.OTHER));
        return applicationDocument;
    }

    private ApplicationDocumentDetail createApplicationDocumentDetailWithFees(ServiceErrors serviceErrors,
                                                                              AccountStructure accountStructure,
                                                                              ApprovalType approvalType) {
        ApplicationDocumentDetailImpl applicationDocument = mock(ApplicationDocumentDetailImpl.class);
        when(applicationDocument.getAccountNumber()).thenReturn("12345");
        List<FeesSchedule> fees = new ArrayList<>();
        FeesSchedule onGoingFees = new FeesScheduleImpl();
        onGoingFees.setType(FeesType.ONGOING_FEE);

        List<FeesComponents> onGoingFeesComponents = new ArrayList<>();
        onGoingFeesComponents.add(createDollarFeesComponent(17, true));
        onGoingFeesComponents.add(createSlidingScaleFeesComponent());

        onGoingFees.setFeesComponents(onGoingFeesComponents);
        onGoingFees.setTransactionType(Arrays.asList(FeesMiscType.PERCENT_MANAGED_FUND, FeesMiscType.PERCENT_MANAGED_PORTFOLIO));
        fees.add(onGoingFees);

        FeesScheduleImpl licenseeFees = new FeesScheduleImpl();
        licenseeFees.setType(FeesType.LICENSEE_FEE);

        List<FeesComponents> licenseeFeesComponents = new ArrayList<>();
        licenseeFeesComponents.add(createDollarFeesComponent(45, false));
        licenseeFeesComponents.add(createPercentageFeesComponent(0.0011, FeesMiscType.PERCENT_MANAGED_PORTFOLIO));
        licenseeFeesComponents.add(createPercentageFeesComponent(0.0022, FeesMiscType.PERCENT_TERM_DEPOSIT));
        licenseeFeesComponents.add(createPercentageFeesComponent(0.0033, FeesMiscType.PERCENT_CASH));
        licenseeFeesComponents.add(createPercentageFeesComponent(0.0044, FeesMiscType.PERCENT_MANAGED_FUND));
        licenseeFeesComponents.add(createPercentageFeesComponent(0.0055, FeesMiscType.PERCENT_SHARE));

        licenseeFees.setFeesComponents(licenseeFeesComponents);

        fees.add(licenseeFees);

        FeesScheduleImpl establishmentFess = new FeesScheduleImpl();
        establishmentFess.setFeesType(FeesType.AVSR_ESTAB);
        List<FeesComponents> establishmentFeesComponents = new ArrayList<>();
        establishmentFeesComponents.add(createDollarFeesComponent(-500, false));
        establishmentFess.setFeesComponents(establishmentFeesComponents);
        fees.add(establishmentFess);

        when(applicationDocument.getFees()).thenReturn(fees);

        //Adviser Account Settings
        List<AccountAuthoriser> adviserAccountSettingsList = new ArrayList<>();
        AccountAuthoriser adviserAccountSettings = mock(AccountAuthoriser.class);
        when(adviserAccountSettings.getTxnType()).thenReturn(TransactionPermission.Account_Maintenance);
        adviserAccountSettingsList.add(adviserAccountSettings);

        when(applicationDocument.getAdviserAccountSettings()).thenReturn(adviserAccountSettingsList);

        //Investor Account Settings
        List<PersonDetail> accountSettingsForAllPersons = mock(List.class);
        AccountAuthoriser investorAccountSettings = mock(AccountAuthoriser.class);
        when(adviserAccountSettings.getTxnType()).thenReturn(TransactionPermission.Account_Maintenance);
        List<AccountAuthoriser> investorAccountSettingsList = new ArrayList<>();
        investorAccountSettingsList.add(investorAccountSettings);
        PersonDetail person = mock(PersonDetail.class);
        when(accountSettingsForAllPersons.isEmpty()).thenReturn(true);
        when(person.getAccountAuthorisationList()).thenReturn(investorAccountSettingsList);
        accountSettingsForAllPersons.add(person);
        when(applicationDocument.getAccountSettingsForAllPersons()).thenReturn(accountSettingsForAllPersons);
        //Portfolio
        List mockedPortfolioList = createMockedPortfolioList(accountStructure);
        applicationDocument.setPortfolio(mockedPortfolioList);
        when(applicationDocument.getPortfolio()).thenReturn(mockedPortfolioList);


        //AdviserKey
        BrokerKey mockAdviserKey = BrokerKey.valueOf("adviserKey");
        applicationDocument.setAdviserKey(mockAdviserKey);
        when(applicationDocument.getAdviserKey()).thenReturn(mockAdviserKey);

        //BrokerService
        BrokerUser mockBroker = createMockBroker();
        when(brokerIntegrationService.getAdviserBrokerUser(mockAdviserKey, serviceErrors)).thenReturn(mockBroker);

        //LinkedAccounts
        when(applicationDocument.getLinkedAccounts()).thenReturn(Collections.EMPTY_LIST);

        //Persons
        List<PersonDetail> mockPersons = createMockPersonList();
        List mockAddress = mock(List.class);
        when(applicationDocument.getPersons()).thenReturn(mockPersons);
        InvestorDto mockInvestor = mock(InvestorDto.class);

        when(mockAddress.isEmpty()).thenReturn(true);
        when(investorDtoConverterForPersonDetail.convertFromPersonDetail(mockPersons.get(0), null, new HashMap<String,Boolean>())).thenReturn(mockInvestor);
        Product mockProduct = createMockProduct();
        when(productIntegrationService.getProductDetail(any(ProductKey.class), any(ServiceErrors.class))).thenReturn(mockProduct);

        when(applicationDocument.getApprovalType()).thenReturn(approvalType);

        if (AccountStructure.SU == accountStructure) {
            when(applicationDocument.getSuperAccountSubType()).thenReturn(AccountSubType.ACCUMULATION);
        }

        return applicationDocument;
    }


    private List<PersonDetail> createMockPersonList() {
        ArrayList<PersonDetail> personDetails = new ArrayList<>();
        PersonDetail mockPerson = mock(PersonDetail.class);
        personDetails.add(mockPerson);
        when(mockPerson.getClientKey()).thenReturn(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("123"));
        when(mockPerson.getCISKey()).thenReturn(CISKey.valueOf("1001"));
        return personDetails;
    }

    private BrokerUser createMockBroker() {
        BrokerUser mockBroker = mock(BrokerUser.class);
        when(mockBroker.getFirstName()).thenReturn("FirstName");
        when(mockBroker.getMiddleName()).thenReturn("MiddleName");
        when(mockBroker.getLastName()).thenReturn("LastName");
        return mockBroker;
    }

    private Product createMockProduct() {
        Product mockProduct = mock(Product.class);
        when(mockProduct.getProductName()).thenReturn("Testing Product");
        return mockProduct;
    }

    private Product createMockCMAProduct() {
        Product mockProduct = mock(Product.class);
        when(mockProduct.getProductName()).thenReturn("Testing Product");
        when(mockProduct.getParentProductName()).thenReturn("CMA Product");
        return mockProduct;
    }

    private List createMockedPortfolioList(AccountStructure accountStructure) {
        List mockedPortfolioList = Mockito.mock(List.class);
        LinkedPortfolioDetails mockPortfolio = createMockPortfolioDetail(accountStructure);
        when(mockedPortfolioList.get(0)).thenReturn(mockPortfolio);
        return mockedPortfolioList;
    }

    private List createMockedCompanyPortfolioList() {
        List mockedPortfolioList = Mockito.mock(List.class);
        LinkedPortfolioDetails mockPortfolio = createCompanyMockPortfolioDetail();
        when(mockedPortfolioList.get(0)).thenReturn(mockPortfolio);
        return mockedPortfolioList;
    }

    private List createMockedCorporateSMSFPortfolioList() {
        List mockedPortfolioList = Mockito.mock(List.class);
        LinkedPortfolioDetails mockPortfolio = createCorporateSMSFMockPortfolioDetail();
        when(mockedPortfolioList.get(0)).thenReturn(mockPortfolio);
        return mockedPortfolioList;
    }

    private List createMockedCorporateTrustPortfolioList() {
        List mockedPortfolioList = Mockito.mock(List.class);
        LinkedPortfolioDetails mockPortfolio = createCorporateTrustMockPortfolioDetail();
        when(mockedPortfolioList.get(0)).thenReturn(mockPortfolio);
        return mockedPortfolioList;
    }

    private LinkedPortfolioDetails createMockPortfolioDetail(AccountStructure accountStructure) {
        LinkedPortfolioDetails mockPortfolio = mock(LinkedPortfolioDetails.class);
        when(mockPortfolio.getAccountType()).thenReturn(accountStructure);
        when(mockPortfolio.getProductId()).thenReturn("1");
        return mockPortfolio;
    }

    private LinkedPortfolioDetails createCompanyMockPortfolioDetail() {
        LinkedPortfolioDetails mockPortfolio = createMockPortfolioDetail(AccountStructure.C);
        return mockPortfolio;
    }

    private LinkedPortfolioDetails createCorporateSMSFMockPortfolioDetail() {
        LinkedPortfolioDetails mockPortfolio = createMockPortfolioDetail(AccountStructure.S);
        return mockPortfolio;
    }

    private LinkedPortfolioDetails createCorporateTrustMockPortfolioDetail() {
        LinkedPortfolioDetails mockPortfolio = createMockPortfolioDetail(AccountStructure.T);
        return mockPortfolio;
    }

    private FeesComponents createPercentageFeesComponent(double factor, FeesMiscType feesMiscType) {
        AnnotatedPercentageFeesComponent percentageFeesComponent = new AnnotatedPercentageFeesComponent();
        ReflectionTestUtils.setField(percentageFeesComponent, "factor", new BigDecimal(factor));
        ReflectionTestUtils.setField(percentageFeesComponent, "feesMiscType", feesMiscType);
        percentageFeesComponent.setFeesComponentType(FeesComponentType.PERCENTAGE_FEE);
        return percentageFeesComponent;
    }

    private FeesComponents createOneOffFeesComponent(double factor, FeesMiscType feesMiscType) {
        OneOffFeesComponent oneOffFeesComponent = new OneOffFeesComponent();
        ReflectionTestUtils.setField(oneOffFeesComponent, "factor", new BigDecimal(factor));
        ReflectionTestUtils.setField(oneOffFeesComponent, "feesMiscType", feesMiscType);
        oneOffFeesComponent.setFeesComponentType(FeesComponentType.ONE_OFF_FEE);
        return oneOffFeesComponent;
    }

    private FeesComponents createRegularFeesComponent(double factor, FeesMiscType feesMiscType) {
        RegularFeesComponent regularFeesComponent = new RegularFeesComponent();
        ReflectionTestUtils.setField(regularFeesComponent, "factor", new BigDecimal(factor));
        ReflectionTestUtils.setField(regularFeesComponent, "feesMiscType", feesMiscType);
        regularFeesComponent.setFeesComponentType(FeesComponentType.REGULAR_FEE);
        return regularFeesComponent;
    }

    private FeesComponents createSlidingScaleFeesComponent() {
        SlidingScaleFeesComponent slidingScaleFeesComponent = new SlidingScaleFeesComponent();
        List<SlidingScaleTiers> tiers = new ArrayList<>(4);
        tiers.add(createFeesTier(0, 13, 0.005f));
        tiers.add(createFeesTier(13, 19, 0.007f));
        tiers.add(createFeesTier(19, 23, 0.0073f));
        tiers.add(createFeesTier(23, 9999999999999L, 0.0091f));
        slidingScaleFeesComponent.setTiers(tiers);
        slidingScaleFeesComponent.setFeesComponentType(FeesComponentType.SLIDING_SCALE_FEE);
        return slidingScaleFeesComponent;
    }

    private SlidingScaleTiers createFeesTier(long lowerBound, long upperBound, float percent) {
        SlidingScaleTiers slidingScaleTiers = new SlidingScaleTiers();
        slidingScaleTiers.setLowerBound(new BigDecimal(lowerBound));
        slidingScaleTiers.setUpperBound(new BigDecimal(upperBound));
        slidingScaleTiers.setPercent(new BigDecimal(percent));
        return slidingScaleTiers;
    }

    private FeesComponents createDollarFeesComponent(int value, boolean cpiIndex) {
        DollarFeesComponent dollarFeesComponent = new DollarFeesComponent();
        dollarFeesComponent.setDollar(BigDecimal.valueOf(value));
        dollarFeesComponent.setCpiindex(cpiIndex);
        dollarFeesComponent.setFeesComponentType(FeesComponentType.DOLLAR_FEE);
        return dollarFeesComponent;
    }

    private PensionEligibility createPensionEligibility(final EligibilityCriteria criteria, final ConditionOfRelease release) {
        return new PensionEligibility() {
            @Override
            public EligibilityCriteria getEligibilityCriteria() {
                return criteria;
            }

            @Override
            public ConditionOfRelease getConditionOfRelease() {
                return release;
            }
        };
    }

    private PersonRelationDto fetchAccountSettingsForAdviser(List<PersonRelationDto> accountSettings){

        return Lambda.selectFirst(accountSettings,new LambdaMatcher<PersonRelationDto>() {
            @Override
            protected boolean matchesSafely(PersonRelationDto personRelationDto) {
                return personRelationDto.isAdviser();
            }
        });
    }

    private void verifyOverseasCountryValues(InvestorDto investorDto) {

        if (CollectionUtils.isNotEmpty(investorDto.getTaxResidenceCountries())) {
            List<TaxResidenceCountriesDto> taxResidenceCountries = investorDto.getTaxResidenceCountries();
            assertThat(taxResidenceCountries.get(0).getTaxExemptionReason(), Is.is("TIN pending"));
            assertThat(taxResidenceCountries.get(0).getTaxResidenceCountry(), Is.is("Albania"));
            assertNull(taxResidenceCountries.get(0).getTin());
            assertThat(taxResidenceCountries.get(1).getTaxExemptionReason(), Is.is("TIN not issued"));
            assertThat(taxResidenceCountries.get(1).getTaxResidenceCountry(), Is.is("Algeria"));
            assertNull(taxResidenceCountries.get(1).getTin());
        }
    }


}
