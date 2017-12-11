package com.bt.nextgen.api.draftaccount.builder.v3_JsonSchema;


import com.bt.nextgen.api.client.service.ClientListDtoService;
import com.bt.nextgen.api.draftaccount.AbstractJsonReaderTest;
import com.bt.nextgen.api.draftaccount.builder.ProcessInvestorApplicationRequestMsgTypeBuilder;
import com.bt.nextgen.api.draftaccount.builder.v3.AddressV2CacheService;
import com.bt.nextgen.api.draftaccount.controller.ClientApplicationDtoDeserializer;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.v1.ClientApplicationFormFactoryV1;
import com.bt.nextgen.api.draftaccount.schemas.v1.DirectClientApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.ContactValue;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Customer;
import com.bt.nextgen.config.ApplicationContextProvider;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.product.ProductDetailImpl;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.gesb.locationmanagement.PostalAddress;
import com.bt.nextgen.service.gesb.locationmanagement.v1.LocationManagementIntegrationService;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerRole;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.code.Field;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.integration.domain.InvestorDetail;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ns.btfin_com.authorityprofile.v1_0.AuthorityTypeType;
import ns.btfin_com.authorityprofile.v1_0.RoleTypeType;
import ns.btfin_com.identityverification.v1_1.PartyIdentificationStatusType;
import ns.btfin_com.party.v3_0.CustomerIdentifier;
import ns.btfin_com.party.v3_0.CustomerIdentifiers;
import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import ns.btfin_com.party.v3_0.ForeignCountriesForTaxationType;
import ns.btfin_com.party.v3_0.ForeignCountryForTaxationType;
import ns.btfin_com.party.v3_0.GenderTypeCode;
import ns.btfin_com.party.v3_0.IndividualType;
import ns.btfin_com.party.v3_0.OrganisationType;
import ns.btfin_com.party.v3_0.OrganisationTypeType;
import ns.btfin_com.party.v3_0.TFNRegistrationExemptionType;
import ns.btfin_com.party.v3_0.TFNRegistrationType;
import ns.btfin_com.product.common.cashaccount.v2_0.CashAccountType;
import ns.btfin_com.product.common.cashaccount.v2_0.LinkedFinancialInstitutionType;
import ns.btfin_com.product.common.investmentaccount.v2_0.AccountInvestmentProductType;
import ns.btfin_com.product.common.investmentaccount.v2_0.AdviceTypeType;
import ns.btfin_com.product.common.investmentaccount.v2_0.InvestorAuthorityProfileType;
import ns.btfin_com.product.common.investmentaccount.v2_0.InvestorAuthorityProfilesType;
import ns.btfin_com.product.common.investmentaccount.v2_0.InvestorType;
import ns.btfin_com.product.common.investmentaccount.v2_0.InvolvedPartyDetailsType;
import ns.btfin_com.product.common.investmentaccount.v2_0.OwnershipTypeType;
import ns.btfin_com.product.common.investmentproduct.v1_1.InvestmentCodeIssuerType;
import ns.btfin_com.product.common.investmentproduct.v1_1.ProductType;
import ns.btfin_com.product.common.investmentproduct.v1_1.ServiceOfferCodeIssuerType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.ApplicationType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.InvestmentAccountType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.OBApplicationApprovalType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.OBApplicationOriginType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.OBApplicationTypeType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.PaymentInstructionType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.PaymentInstructionsType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.ProcessInvestorApplicationRequestMsgType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingresponse.v3_0.ProcessInvestorApplicationErrorResponseType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingresponse.v3_0.ProcessInvestorApplicationResponseMsgType;
import ns.btfin_com.product.superannuationretirement.superannuationaccount.v3_0.ConditionsOfReleaseType;
import ns.btfin_com.product.superannuationretirement.superannuationaccount.v3_0.EligibilityTypeType;
import ns.btfin_com.product.superannuationretirement.superannuationaccount.v3_0.SuperAccountType;
import ns.btfin_com.sharedservices.common.address.v3_0.AddressesType;
import ns.btfin_com.sharedservices.common.address.v3_0.RegisteredResidentialAddressDetailType;
import ns.btfin_com.sharedservices.common.address.v3_0.StandardAddressType;
import ns.btfin_com.sharedservices.common.address.v3_0.StructuredAddressDetailType;
import ns.btfin_com.sharedservices.common.contact.v1_1.StandardContactNumberType;
import ns.btfin_com.sharedservices.common.fee.v1_2.FeeClassificationType;
import ns.btfin_com.sharedservices.common.fee.v1_2.FeeFrequencyType;
import ns.btfin_com.sharedservices.common.fee.v1_2.FeeInfoType;
import ns.btfin_com.sharedservices.common.fee.v1_2.FeeSourceType;
import ns.btfin_com.sharedservices.common.payment.v2_1.CreditDebitIndicatorType;
import ns.btfin_com.sharedservices.common.payment.v2_1.FinancialInstitutionAccountType;
import ns.btfin_com.sharedservices.common.payment.v2_1.PaymentAccountType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import util.JaxbValidator;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.bt.nextgen.clients.util.JaxbUtil.marshall;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static ns.btfin_com.authorityprofile.v1_0.AuthorityTypeType.APPLICATION_APPROVAL;
import static ns.btfin_com.authorityprofile.v1_0.AuthorityTypeType.APPLICATION_MAINTENANCE;
import static ns.btfin_com.authorityprofile.v1_0.AuthorityTypeType.FULL_TRANSACTION;
import static ns.btfin_com.authorityprofile.v1_0.AuthorityTypeType.LIMITED_CHANGE;
import static ns.btfin_com.authorityprofile.v1_0.AuthorityTypeType.LIMITED_TRANSACTION;
import static ns.btfin_com.authorityprofile.v1_0.RoleTypeType.CLIENT;
import static ns.btfin_com.party.v3_0.InvestmentAccountPartyRoleTypeType.ACCOUNT_SERVICER_ROLE;
import static ns.btfin_com.party.v3_0.InvestmentAccountPartyRoleTypeType.CONTACT_PERSON_ROLE;
import static ns.btfin_com.party.v3_0.InvestmentAccountPartyRoleTypeType.PRIMARY_OWNER_ROLE;
import static ns.btfin_com.party.v3_0.InvestmentAccountPartyRoleTypeType.SECONDARY_OWNER_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.COMPANY_BENEFICIAL_OWNER_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.COMPANY_DIRECTOR_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.COMPANY_SECRETARY_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.COMPANY_SHAREHOLDER_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.COMPANY_SIGNATORY_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.SMSF_MEMBER_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.SMSF_TRUSTEE_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.TRUST_BENEFICIAL_OWNER_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.TRUST_BENEFICIARY_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.TRUST_TRUSTEE_ROLE;
import static ns.btfin_com.party.v3_0.ReasonForTaxIdentificationNumberExemptionType.TIN_NEVER_ISSUED;
import static ns.btfin_com.party.v3_0.ReasonForTaxIdentificationNumberExemptionType.TIN_PENDING;
import static ns.btfin_com.party.v3_0.ReasonForTaxIdentificationNumberExemptionType.UNDER_AGE;
import static ns.btfin_com.party.v3_0.TFNRegistrationExemptionType.NON_RESIDENT;
import static ns.btfin_com.party.v3_0.TFNRegistrationType.EXEMPT;
import static ns.btfin_com.party.v3_0.TFNRegistrationType.NONE;
import static ns.btfin_com.party.v3_0.TFNRegistrationType.ONE;
import static ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.OnboardingRequestSchemaValidatorTest.ONBOARDING_REQUEST_V3_SCHEMA_PATH;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by F058391 on 9/05/2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ProcessInvestorApplicationRequestMsgTypeBuilderV3_JsonSchemaTest.TestConfig.class)
public class ProcessInvestorApplicationRequestMsgTypeBuilderV3_JsonSchemaTest extends AbstractJsonReaderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessInvestorApplicationRequestMsgTypeBuilderV3_JsonSchemaTest.class);

    @Autowired
    @Qualifier("test")
    UserProfileService userProfileService;

    protected ApplicationType application;

    @Autowired
    private ProcessInvestorApplicationRequestMsgTypeBuilder<ProcessInvestorApplicationRequestMsgType, ProcessInvestorApplicationResponseMsgType, ProcessInvestorApplicationErrorResponseType> requestMsgTypeBuilder;

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @Autowired
    private AssetIntegrationService assetIntegrationService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private LocationManagementIntegrationService addressService;

    @Autowired
    private AddressV2CacheService addressV2CacheService;

    @Autowired
    private FeatureTogglesService featureTogglesService;

    private FeatureToggles featureToggles;
    private InvestorDetail investorDetail;
    private BrokerUser adviser;
    private Broker dealer;
    private ObjectMapper mapper;
    private HttpServletRequest request;

    @Before
    public void setupServices() {

        mapper = new JsonObjectMapper();
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        Mockito.when(applicationContext.getBean("jsonObjectMapper")).thenReturn(mapper);
        ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider(null);
        applicationContextProvider.setApplicationContext(applicationContext);

        dealer = mock(Broker.class);
        when(dealer.getPositionName()).thenReturn("DealerGroupName");

        adviser = mock(BrokerUser.class);
        when(adviser.getCISKey()).thenReturn(CISKey.valueOf("123456789"));
        when(adviser.getBankReferenceId()).thenReturn("SOME GCM ID");
        when(adviser.getFirstName()).thenReturn("AdviserFirstName");
        when(adviser.getLastName()).thenReturn("AdviserLastName");
        Email adviserEmail = mock(Email.class);
        when(adviserEmail.getEmail()).thenReturn("adviser@example.com");
        when(adviserEmail.getType()).thenReturn(AddressMedium.EMAIL_PRIMARY);
        when(adviser.getEmails()).thenReturn(singletonList(adviserEmail));

        BrokerRole role = mock(BrokerRole.class);
        when(role.getRole()).thenReturn(JobRole.ADVISER);
        when(role.getKey()).thenReturn(BrokerKey.valueOf("123456789"));
        when(adviser.getRoles()).thenReturn(singletonList(role));
        mockAdviserBusinessPhone();

        investorDetail = mock(InvestorDetail.class);

        Address address = mock(Address.class);
        when(address.isDomicile()).thenReturn(true);
        when(address.getModificationSeq()).thenReturn("12");
        when(address.getStreetNumber()).thenReturn("200");
        when(address.getStreetName()).thenReturn("Barangaroo");
        when(address.getSuburb()).thenReturn("Sydney");
        when(address.getCountry()).thenReturn("Australia");
        when(address.getPostCode()).thenReturn("2145");
        when(address.getStateAbbr()).thenReturn("NSW");
        when(address.getCountryAbbr()).thenReturn("AU");

        List<Address> addresses = new ArrayList<>();
        addresses.add(address);

        when(clientIntegrationService.loadClientDetails(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(investorDetail);

        Email ignoredEmail = createEmail("someemail@domain.com", AddressMedium.EMAIL_PRIMARY);
        when(investorDetail.getEmails()).thenReturn(singletonList(ignoredEmail));
        Phone primaryPhone = createPhone("0456456456", AddressMedium.MOBILE_PHONE_PRIMARY);
        when(investorDetail.getPhones()).thenReturn(singletonList(primaryPhone));
        when(investorDetail.getFirstName()).thenReturn("First name");
        when(investorDetail.getLastName()).thenReturn("Surname");
        when(investorDetail.getGcmId()).thenReturn("Some GCM ID");
        when(investorDetail.getCISKey()).thenReturn(CISKey.valueOf("CISKey"));
        when(investorDetail.getAddresses()).thenReturn(addresses);
        when(investorDetail.getWestpacCustomerNumber()).thenReturn("WPACNumber");
        mockStaticIntegrationServiceForTitle();
        featureToggles = new FeatureToggles();
        featureToggles.setFeatureToggle("onboardingCMA", false);
        when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(featureToggles);
        createMockRequest();
    }

    private HttpServletRequest createMockRequest(){
        request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        return request;
    }
    private void mockAdviserBusinessPhone() {
        Phone businessPhone = mock(Phone.class);
        when(businessPhone.getCountryCode()).thenReturn("+61");
        when(businessPhone.getAreaCode()).thenReturn("0458");
        when(businessPhone.getNumber()).thenReturn("123123");
        when(businessPhone.getType()).thenReturn(AddressMedium.BUSINESS_TELEPHONE);

        Phone mobilePhone = mock(Phone.class);
        when(mobilePhone.getAreaCode()).thenReturn("02");
        when(mobilePhone.getNumber()).thenReturn("98765432");
        when(mobilePhone.getType()).thenReturn(AddressMedium.MOBILE_PHONE_SECONDARY);


        when(adviser.getPhones()).thenReturn(asList(businessPhone, mobilePhone));
    }

    private Phone createPhone(String phoneNumber, AddressMedium type) {
        Phone phone = mock(Phone.class);
        when(phone.getNumber()).thenReturn(phoneNumber);
        when(phone.getType()).thenReturn(type);
        return phone;
    }

    private Email createEmail(String emailAddress, AddressMedium type) {
        Email email = mock(Email.class);
        when(email.getEmail()).thenReturn(emailAddress);
        when(email.getType()).thenReturn(type);
        return email;
    }

    private void mockStaticIntegrationServiceForTitle() {
        CodeImpl codeImpl = Mockito.mock(CodeImpl.class);
        Field field = mock(Field.class);
        when(codeImpl.getField(anyString())).thenReturn(field);
        when(field.getValue()).thenReturn("Mr");
        when(staticIntegrationService.loadCodeByUserId(eq(CodeCategory.PERSON_TITLE), anyString(), any(ServiceErrors.class))).thenReturn(codeImpl);
    }

    private void mockAssetIntegrationService() {
        String assetId = "assetId";
        Asset asset = mock(Asset.class);
        when(asset.getAssetName()).thenReturn("BT Moderate Portfolio");
        when(asset.getAssetCode()).thenReturn("WFS0586AU");
        when(asset.getAssetId()).thenReturn(assetId);
        when(assetIntegrationService.loadAvailableAssets(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(singletonList(asset));
    }

    private void mockProductIntegrationService() {
        final List<Product> productList =
            asList(createProduct(ProductLevel.OFFER, "Offer Active", "product", false),
                createProduct(ProductLevel.WHITE_LABEL, "BT Invest", "product", false),
                createProduct(ProductLevel.WHITE_LABEL, "BT Super Direct", "productSuper", true),
                createProduct(ProductLevel.OFFER, "Offer Simple", "ServiceOfferCode", false));

        when(productIntegrationService.getDealerGroupProductList(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);
    }

    private Product createProduct(ProductLevel productLevel, String productName, String productCode, boolean isSuper) {
        ProductDetailImpl product = new ProductDetailImpl();
        product.setProductKey(ProductKey.valueOf(productCode));
        product.setProductId(productCode);
        product.setProductName(productName);
        product.setProductLevel(productLevel);
        product.setSuper(isSuper);
        return product;
    }

    protected void validateRequest(String jsonAsString, boolean isDirect) throws Exception {
        Object formData;
        IClientApplicationForm clientApplicationForm;
        if (isDirect) {
            formData = mapper.readValue(jsonAsString, DirectClientApplicationFormData.class);
            clientApplicationForm = ClientApplicationFormFactoryV1.getNewDirectClientApplicationForm((DirectClientApplicationFormData) formData);
        } else {
            formData = mapper.readValue(jsonAsString, OnboardingApplicationFormData.class);
            clientApplicationForm = ClientApplicationFormFactoryV1.getNewClientApplicationForm((OnboardingApplicationFormData) formData);
        }

        validateRequest(clientApplicationForm);

    }

    private void validateRequest(IClientApplicationForm form) throws Exception {
        ProcessInvestorApplicationRequestMsgType processInvestorApplicationRequestMsgType =
            requestMsgTypeBuilder.buildFromForm(form, adviser,
                OnboardingApplicationKey.valueOf(123), "1234", dealer, new ServiceErrorsImpl());
        processInvestorApplicationRequestMsgType.getApplication().setSubmissionID("12");

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshall(baos, ProcessInvestorApplicationRequestMsgType.class, processInvestorApplicationRequestMsgType);

        final String xml = baos.toString();
        // US14695 - we no longer want any extraneous xsi:type attributes polluting our XML for onboarding requests
        //assertFalse(xml.contains("xsi:type"));
        LOGGER.info(xml);

        JaxbValidator<ProcessInvestorApplicationRequestMsgType> validator = new JaxbValidator<>(ONBOARDING_REQUEST_V3_SCHEMA_PATH, ProcessInvestorApplicationRequestMsgType.class);
        validator.validate(processInvestorApplicationRequestMsgType);
        this.application = processInvestorApplicationRequestMsgType.getApplication();
    }

    private void assertAuthorityProfiles(InvestorAuthorityProfilesType authorityProfiles, RoleTypeType roleType, AuthorityTypeType... authTypes) {
        final List<InvestorAuthorityProfileType> profiles = authorityProfiles.getAuthorityProfile();
        assertThat(profiles.size(), is(authTypes.length));
        int i = 0;
        for (InvestorAuthorityProfileType profile : profiles) {
            assertThat(profile.getRoleType(), is(roleType));
            assertThat(profile.getAuthorityType(), is(authTypes[i++]));
        }
    }

    private void assertIndividual(IndividualType individual, String givenName, String lastName, GenderTypeCode gender) {
        assertThat(individual.getGivenName(), is(givenName));
        assertThat(individual.getLastName(), is(lastName));
        assertThat(individual.getGender(), is(gender));
    }

    private void assertIndividual(IndividualType individual, String titlePrefix, String givenName, String lastName, GenderTypeCode gender) {
        assertThat(individual.getTitlePrefix(), is(titlePrefix));
        assertThat(individual.getGivenName(), is(givenName));
        assertThat(individual.getLastName(), is(lastName));
        assertThat(individual.getGender(), is(gender));
    }

    //-----------------Individual------------------------

    @Test
    public void individual() throws Exception {
        String jsonRequest = readJsonStringFromFile("individual.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.INDIVIDUAL));
        assertThat(application.getApplicationOrigin(), is(OBApplicationOriginType.BT_PANORAMA));

        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertThat(investmentAccount.getAdviceType(), is(AdviceTypeType.PERSONAL_ADVICE));

        final List<InvestorType> investors = investmentAccount.getInvestors().getInvestor();
        assertThat(investors.size(), is(1));
        final InvestorType investor = investors.get(0);
        assertThat(investor.getInvestmentAccountPartyRole(), contains(PRIMARY_OWNER_ROLE, CONTACT_PERSON_ROLE));
        assertAuthorityProfiles(investor.getAuthorityProfiles(), CLIENT, LIMITED_CHANGE, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);
        final IndividualType individual = investor.getInvestorDetails().getPartyDetails().getIndividual();
        assertIndividual(individual, "Mr", "Simpson", "Kumar", GenderTypeCode.MALE);
        String unitNumber = investor.getInvestorDetails().getPartyDetails().getIndividual().getResidentialAddress().getAddressDetail().getStructuredAddressDetail().getAddressTypeDetail().getStandardAddress().getUnitNumber();
        assertNull(unitNumber);
        StandardContactNumberType workNumber = individual.getWorkPhoneNumber().getContactNumber().getStandardContactNumber();
        assertThat(workNumber.getCountryCode(), is("61"));
        assertThat(workNumber.getAreaCode(), is("2"));
        assertThat(workNumber.getSubscriberNumber(), is("53252332"));
    }

    @Test
    public void individualWithNewAddressV2() throws Exception {
        featureToggles.setFeatureToggle("onboardingCMA", false);
        String jsonRequest = readJsonStringFromFile("individualWithAddressV2.json");
        mockAddressService();
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.INDIVIDUAL));
        assertThat(application.getApplicationOrigin(), is(OBApplicationOriginType.BT_PANORAMA));

        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertThat(investmentAccount.getAdviceType(), is(AdviceTypeType.PERSONAL_ADVICE));
        assertNull(investmentAccount.getAccountProperties());

        final List<InvestorType> investors = investmentAccount.getInvestors().getInvestor();
        assertThat(investors.size(), is(1));
        final InvestorType investor = investors.get(0);
        assertThat(investor.getInvestmentAccountPartyRole(), contains(PRIMARY_OWNER_ROLE, CONTACT_PERSON_ROLE));
        assertAuthorityProfiles(investor.getAuthorityProfiles(), CLIENT, LIMITED_CHANGE, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);
        final IndividualType individual = investor.getInvestorDetails().getPartyDetails().getIndividual();
        assertIndividual(individual, "Mr", "Test", "smith", GenderTypeCode.MALE);

        RegisteredResidentialAddressDetailType residentialAddress = individual.getResidentialAddress();
        StructuredAddressDetailType structuredAddressDetail = residentialAddress.getAddressDetail().getStructuredAddressDetail();
        validateAddress(structuredAddressDetail);

        StandardAddressType postalAddress = investor.getInvestorDetails().getPostalAddresses().getAddress().get(0).getAddressDetail().getStructuredAddressDetail().getAddressTypeDetail().getStandardAddress();
        assertThat(postalAddress.getStreetName(), is("Pitt"));
        assertThat(postalAddress.getStreetType(), is("ST"));
        assertThat(postalAddress.getStreetNumber(), is("33"));
        assertThat(postalAddress.getFloorNumber(), is("28"));
        assertThat(postalAddress.getUnitNumber(), is("100"));
    }

    @Test
    public void individual_withCMA() throws Exception {
        featureToggles.setFeatureToggle("onboardingCMA", true);
        String jsonRequest = readJsonStringFromFile("individualWithAddressV2.json");
        mockAddressService();
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.INDIVIDUAL));
        assertThat(application.getApplicationOrigin(), is(OBApplicationOriginType.BT_PANORAMA));

        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertThat(investmentAccount.getAdviceType(), is(AdviceTypeType.PERSONAL_ADVICE));
        assertNotNull(investmentAccount.getAccountProperties());
        assertNotNull(investmentAccount.getAccountProperties().getAccountProperty());
    }

    @Test
    public void testBuildFromJson_OptionalLinkedAccountSMSFSubmission() throws Exception {
        String jsonRequest = readJsonStringFromFile("company_linkedaccounts_optional.json");
        mockAddressService();
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.CORPORATE_SMSF));
        assertThat(application.getApplicationOrigin(), is(OBApplicationOriginType.BT_PANORAMA));

        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertThat(investmentAccount.getAdviceType(), is(AdviceTypeType.PERSONAL_ADVICE));
        assertNull(application.getInvestmentAccount().getCashAccounts());

    }

    //---------------------Joint----------------------
    @Test
    public void jointWithOneExisting2LinkedAccount() throws Exception {
        String jsonRequest = readJsonStringFromFile("jointWithOneExisting2LinkedAccount.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.JOINT));
        assertThat(application.getApplicationOrigin(), is(OBApplicationOriginType.BT_PANORAMA));

        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertThat(investmentAccount.getAdviceType(), is(AdviceTypeType.PERSONAL_ADVICE));
        final List<InvestorType> investors = investmentAccount.getInvestors().getInvestor();
        final List<InvestorType> existingInvestors = investmentAccount.getInvestors().getExistingInvestor();
        assertThat(investors.size(), is(1));
        assertThat(existingInvestors.size(), is(1));

        final InvestorType newInvestor = investors.get(0);
        final InvestorType existingInvestor = existingInvestors.get(0);

        assertThat(newInvestor.getInvestmentAccountPartyRole(), containsInAnyOrder(PRIMARY_OWNER_ROLE, CONTACT_PERSON_ROLE));
        assertAuthorityProfiles(newInvestor.getAuthorityProfiles(), CLIENT, LIMITED_CHANGE, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);

        assertThat(existingInvestor.getInvestmentAccountPartyRole(), containsInAnyOrder(SECONDARY_OWNER_ROLE));
        assertAuthorityProfiles(existingInvestor.getAuthorityProfiles(), CLIENT, LIMITED_TRANSACTION, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);

        assertThat(application.getPaymentInstructions().getPaymentInstruction().size(), is(1));

        final PaymentInstructionType paymentInstructionType = application.getPaymentInstructions().getPaymentInstruction().get(0);
        assertThat(paymentInstructionType.getCreditDebitIndicator(), is(CreditDebitIndicatorType.DEBIT));
        assertThat(paymentInstructionType.getPaymentAmount(), is(new BigDecimal("20000.00")));

        final PaymentAccountType debtorAccount = paymentInstructionType.getDebtorAccount();
        final FinancialInstitutionAccountType financialInstitutionAccount = debtorAccount.getFinancialInstitutionAccount();

        assertThat(financialInstitutionAccount.getAccountName(), is("test account"));
        assertThat(financialInstitutionAccount.getAccountNumber(), is("6643632"));
        assertThat(financialInstitutionAccount.getBSB(), is("062-000"));

        final List<LinkedFinancialInstitutionType> linkedFinancialInstitutions = application.getInvestmentAccount().getCashAccounts().getCashAccount().get(0).getLinkedFinancialInstitutions().getLinkedFinancialInstitution();
        assertThat(linkedFinancialInstitutions.size(), is(2));

        final LinkedFinancialInstitutionType financialInstitutionType1 = linkedFinancialInstitutions.get(0);
        assertThat(financialInstitutionType1.getAccountName(), is("test account"));
        assertThat(financialInstitutionType1.getBSB(), is("062-000"));
        assertThat(financialInstitutionType1.getAccountNumber(), is("6643632"));

        final LinkedFinancialInstitutionType financialInstitutionType2 = linkedFinancialInstitutions.get(1);
        assertThat(financialInstitutionType2.getAccountName(), is("test account 2"));
        assertThat(financialInstitutionType2.getBSB(), is("062-000"));
        assertThat(financialInstitutionType2.getAccountNumber(), is("543534"));
    }

    //----------------------------------New SMSF Individual----------------------
    @Test
    public void new_individual_smsf() throws Exception {
        String jsonRequest = readJsonStringFromFile("newIndividualSMSF.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.INDIVIDUAL_NEW_SMSF));
        assertThat(application.getApplicationOrigin(), is(OBApplicationOriginType.BT_PANORAMA));
        assertThat(application.getInvestmentAccount().getOwnershipType(), is(OwnershipTypeType.TRUST));

        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertThat(investmentAccount.getAdviceType(), is(AdviceTypeType.PERSONAL_ADVICE));

        final List<InvestorType> investors = investmentAccount.getInvestors().getInvestor();
        assertThat(investors.size(), is(3));

        final InvestorType smsfOrganization = investors.get(0);
        assertThat(smsfOrganization.getInvestmentAccountPartyRole(), contains(PRIMARY_OWNER_ROLE));
        final OrganisationType organisation = smsfOrganization.getInvestorDetails().getPartyDetails().getOrganisation();
        assertThat(organisation.getOrganisationName(), is("New JSON New Individual SMSF"));
        assertThat(organisation.isRegisteredForGST(), is(true));
        assertThat(organisation.getOrganisationType().name(), is("SMSF"));

        final InvestorType trusteeOne = investors.get(1);
        assertThat(trusteeOne.getInvestmentAccountPartyRole(), contains(ACCOUNT_SERVICER_ROLE, CONTACT_PERSON_ROLE));
        assertAuthorityProfiles(trusteeOne.getAuthorityProfiles(), CLIENT, LIMITED_CHANGE, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);
        final IndividualType individualOne = trusteeOne.getInvestorDetails().getPartyDetails().getIndividual();
        assertThat(individualOne.getGivenName(), is("Kamal"));
        assertThat(individualOne.getLastName(), is("Hassan"));
        assertThat(individualOne.getGender(), is(GenderTypeCode.MALE));

        final InvestorType trusteeTwo = investors.get(2);
        assertThat(trusteeTwo.getInvestmentAccountPartyRole(), contains(ACCOUNT_SERVICER_ROLE));
        assertAuthorityProfiles(trusteeOne.getAuthorityProfiles(), CLIENT, LIMITED_CHANGE, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);
        final IndividualType individualTwo = trusteeTwo.getInvestorDetails().getPartyDetails().getIndividual();
        assertThat(individualTwo.getGivenName(), is("Rajni"));
        assertThat(individualTwo.getLastName(), is("Kant"));
        assertThat(individualTwo.getGender(), is(GenderTypeCode.MALE));


    }

    //----------------------------------New SMSF Corporate----------------------
    @Test
    public void new_corporate_smsf() throws Exception {
        featureToggles.setFeatureToggle("onboardingCMA", false);
        String jsonRequest = readJsonStringFromFile("newCorporateSMSF.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.CORPORATE_NEW_SMSF));
        assertThat(application.getApplicationOrigin(), is(OBApplicationOriginType.BT_PANORAMA));
        assertThat(application.getInvestmentAccount().getOwnershipType(), is(OwnershipTypeType.TRUST));

        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertThat(investmentAccount.getAdviceType(), is(AdviceTypeType.PERSONAL_ADVICE));
        assertNull(investmentAccount.getAccountProperties());//CMA properties should not be set if CMA toggle false

        final List<InvestorType> investors = investmentAccount.getInvestors().getInvestor();
        assertThat(investors.size(), is(4));

        final InvestorType investorOne = investors.get(0);
        assertThat(investorOne.getInvestmentAccountPartyRole(), contains(ACCOUNT_SERVICER_ROLE, CONTACT_PERSON_ROLE));
        assertThat(investorOne.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_DIRECTOR_ROLE, SMSF_MEMBER_ROLE, COMPANY_BENEFICIAL_OWNER_ROLE, COMPANY_SHAREHOLDER_ROLE));
        assertAuthorityProfiles(investorOne.getAuthorityProfiles(), CLIENT, LIMITED_CHANGE, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);
        final IndividualType directorOne = investorOne.getInvestorDetails().getPartyDetails().getIndividual();
        assertThat(directorOne.getGivenName(), is("Dinesh"));
        assertThat(directorOne.getLastName(), is("Kharyal"));
        assertThat(directorOne.getGender(), is(GenderTypeCode.MALE));
        assertThat(directorOne.getCityOfBirth(), is("GRANVILLE"));
        final InvestorType investorTwo = investors.get(1);
        assertThat(investorTwo.getInvestmentAccountPartyRole(), contains(ACCOUNT_SERVICER_ROLE));
        assertThat(investorTwo.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_DIRECTOR_ROLE, SMSF_MEMBER_ROLE, COMPANY_SHAREHOLDER_ROLE, COMPANY_BENEFICIAL_OWNER_ROLE,COMPANY_SECRETARY_ROLE));
        assertAuthorityProfiles(investorTwo.getAuthorityProfiles(), CLIENT, LIMITED_CHANGE, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);
        final IndividualType directorTwo = investorTwo.getInvestorDetails().getPartyDetails().getIndividual();
        assertThat(directorTwo.getGivenName(), is("Gyaneshwar"));
        assertThat(directorTwo.getLastName(), is("Chintamani"));
        assertThat(directorTwo.getGender(), is(GenderTypeCode.MALE));
        assertThat(directorTwo.getCityOfBirth(), is("ATARMAN"));

        final InvestorType companyOrganization = investors.get(2);
        assertThat(companyOrganization.getInvestmentAccountPartyRole(), contains(ACCOUNT_SERVICER_ROLE));
        assertThat(companyOrganization.getPartyRoleInRelatedOrganisation(), contains(SMSF_TRUSTEE_ROLE));
        final OrganisationType companyOrganisation = companyOrganization.getInvestorDetails().getPartyDetails().getOrganisation();
        assertThat(companyOrganisation.getOrganisationName(), is("HOME EASE"));
        assertThat(companyOrganisation.getASICName(), is("HOME EASE"));
        assertThat(companyOrganisation.isRegisteredForGST(), is(false));
        assertThat(companyOrganisation.getOrganisationType(), is(OrganisationTypeType.COMPANY));

        final InvestorType smsfOrganization = investors.get(3);
        assertThat(smsfOrganization.getInvestmentAccountPartyRole(), contains(PRIMARY_OWNER_ROLE));
        final OrganisationType smsfOrganisation = smsfOrganization.getInvestorDetails().getPartyDetails().getOrganisation();
        assertThat(smsfOrganisation.getOrganisationName(), is("New JSON New Corporate"));
        assertThat(smsfOrganisation.isRegisteredForGST(), is(true));
        assertThat(smsfOrganisation.getOrganisationType(), is(OrganisationTypeType.SMSF));

    }

    @Test
    public void new_corporate_smsf_withCMA() throws Exception {
        featureToggles.setFeatureToggle("onboardingCMA", true);
        String jsonRequest = readJsonStringFromFile("newCorporateSMSF.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.CORPORATE_NEW_SMSF));
        assertThat(application.getApplicationOrigin(), is(OBApplicationOriginType.BT_PANORAMA));
        assertThat(application.getInvestmentAccount().getOwnershipType(), is(OwnershipTypeType.TRUST));

        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertThat(investmentAccount.getAdviceType(), is(AdviceTypeType.PERSONAL_ADVICE));
        assertNotNull(investmentAccount.getAccountProperties());
        assertNotNull(investmentAccount.getAccountProperties().getAccountProperty());

    }

    //-----------------------Existing SMSF individual------------------

    @Test
    public void existing_individual_smsf() throws Exception {
        String jsonRequest = readJsonStringFromFile("existingIndividualSMSF.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.INDIVIDUAL_SMSF));
        assertThat(application.getApplicationOrigin(), is(OBApplicationOriginType.BT_PANORAMA));
        assertThat(application.getInvestmentAccount().getOwnershipType(), is(OwnershipTypeType.TRUST));

        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertThat(investmentAccount.getAdviceType(), is(AdviceTypeType.PERSONAL_ADVICE));

        final List<InvestorType> investors = investmentAccount.getInvestors().getInvestor();
        assertThat(investors.size(), is(4));


        final InvestorType smsfOrganization = investors.get(0);
        assertThat(smsfOrganization.getInvestmentAccountPartyRole(), contains(PRIMARY_OWNER_ROLE));
        final OrganisationType smsfOrganisation = smsfOrganization.getInvestorDetails().getPartyDetails().getOrganisation();
        assertThat(smsfOrganisation.getOrganisationName(), is("New Json Existing Individual SMSF"));
        assertThat(smsfOrganisation.isRegisteredForGST(), is(true));
        assertThat(smsfOrganisation.getOrganisationType(), is(OrganisationTypeType.SMSF));


        final InvestorType investorOne = investors.get(1);
        assertThat(investorOne.getInvestmentAccountPartyRole(), contains(ACCOUNT_SERVICER_ROLE, CONTACT_PERSON_ROLE));
        assertThat(investorOne.getPartyRoleInRelatedOrganisation(), contains(SMSF_TRUSTEE_ROLE, COMPANY_BENEFICIAL_OWNER_ROLE, SMSF_MEMBER_ROLE));
        assertAuthorityProfiles(investorOne.getAuthorityProfiles(), CLIENT, LIMITED_CHANGE, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);
        final IndividualType trusteeOne = investorOne.getInvestorDetails().getPartyDetails().getIndividual();
        assertThat(trusteeOne.getGivenName(), is("Trustee"));
        assertThat(trusteeOne.getLastName(), is("One"));
        assertThat(trusteeOne.getGender(), is(GenderTypeCode.MALE));


        final InvestorType investorTwo = investors.get(2);
        assertThat(investorTwo.getInvestmentAccountPartyRole(), contains(ACCOUNT_SERVICER_ROLE));
        assertThat(investorTwo.getPartyRoleInRelatedOrganisation(), contains(SMSF_TRUSTEE_ROLE, COMPANY_BENEFICIAL_OWNER_ROLE));
        assertAuthorityProfiles(investorTwo.getAuthorityProfiles(), CLIENT, LIMITED_TRANSACTION, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);
        final IndividualType trusteeTwo = investorTwo.getInvestorDetails().getPartyDetails().getIndividual();
        assertThat(trusteeTwo.getGivenName(), is("Trustee"));
        assertThat(trusteeTwo.getLastName(), is("Two"));
        assertThat(trusteeTwo.getGender(), is(GenderTypeCode.MALE));

        final InvestorType organization = investors.get(3);
        assertThat(organization.getPartyRoleInRelatedOrganisation(), contains(SMSF_MEMBER_ROLE));
        final IndividualType member = organization.getInvestorDetails().getPartyDetails().getIndividual();
        assertThat(member.getGivenName(), is("MEMBER"));
        assertThat(member.getLastName(), is("ONE"));


    }

    //------------------------Existing smsf corporate---------------------------

    @Test
    public void testExistingCorporateSmsfWithMemberAddressV2() throws Exception {
        mockAddressService();
        String jsonRequest = readJsonStringFromFile("corporateSmsfWithMemberAddressV2.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.CORPORATE_SMSF));
        List<InvestorType> investorTypeList = application.getInvestmentAccount().getInvestors().getInvestor();
        assertThat(investorTypeList.size(), is(4));
        InvolvedPartyDetailsType smsfdetails = investorTypeList.get(1).getInvestorDetails();

        StructuredAddressDetailType organisationRegisteredAddress = smsfdetails.getPartyDetails().getOrganisation().getRegisteredAddress().getAddressDetail().getStructuredAddressDetail();
        validateAddress(organisationRegisteredAddress);

        StructuredAddressDetailType smsfPostalAddres = smsfdetails.getPostalAddresses().getAddress().get(0).getAddressDetail().getStructuredAddressDetail();
        validateAddress(smsfPostalAddres);

        InvolvedPartyDetailsType companyAsTrusteeDetails = investorTypeList.get(2).getInvestorDetails();

        StructuredAddressDetailType companyRegisteredAddress = companyAsTrusteeDetails.getPartyDetails().getOrganisation().getRegisteredAddress().getAddressDetail().getStructuredAddressDetail();
        validateAddress(companyRegisteredAddress);

        StructuredAddressDetailType companyPostalAddres = companyAsTrusteeDetails.getPostalAddresses().getAddress().get(0).getAddressDetail().getStructuredAddressDetail();
        validateAddress(companyPostalAddres);

        InvolvedPartyDetailsType member = investorTypeList.get(3).getInvestorDetails();
        StructuredAddressDetailType shareHolderResidentialAddress = member.getPartyDetails().getIndividual().getResidentialAddress().getAddressDetail().getStructuredAddressDetail();
        validateAddress(shareHolderResidentialAddress);

    }

    @Test
    public void testExistingCorporateSmsfRoles() throws Exception {
        mockAddressService();
        String jsonRequest = readJsonStringFromFile("corporateSmsfWithAdditionalBeneficiaries.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.CORPORATE_SMSF));
        List<InvestorType> investorTypeList = application.getInvestmentAccount().getInvestors().getInvestor();
        assertThat(investorTypeList.size(), is(6));

        InvolvedPartyDetailsType directorOne = investorTypeList.get(0).getInvestorDetails();
        assertThat(directorOne.getPartyDetails().getIndividual().getGivenName(), is("Dir"));
        assertThat(directorOne.getPartyDetails().getIndividual().getLastName(), is("One"));
        assertThat(investorTypeList.get(0).getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_DIRECTOR_ROLE, SMSF_MEMBER_ROLE));

        InvolvedPartyDetailsType directorTwo= investorTypeList.get(1).getInvestorDetails();
        assertThat(directorTwo.getPartyDetails().getIndividual().getGivenName(), is("Dir"));
        assertThat(directorTwo.getPartyDetails().getIndividual().getLastName(), is("Two"));
        assertThat(investorTypeList.get(1).getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_DIRECTOR_ROLE, COMPANY_BENEFICIAL_OWNER_ROLE));

        InvolvedPartyDetailsType company= investorTypeList.get(2).getInvestorDetails();
        assertThat(company.getPartyDetails().getOrganisation().getOrganisationName(), is("Comp 123"));
        assertThat(company.getPartyDetails().getOrganisation().getOrganisationType(), is(OrganisationTypeType.COMPANY));
        assertThat(investorTypeList.get(2).getPartyRoleInRelatedOrganisation(), containsInAnyOrder(SMSF_TRUSTEE_ROLE));

        InvolvedPartyDetailsType smsf= investorTypeList.get(3).getInvestorDetails();
        assertThat(smsf.getPartyDetails().getOrganisation().getOrganisationName(), is("EXISTINGA ROLETEST"));
        assertThat(smsf.getPartyDetails().getOrganisation().getOrganisationType(), is(OrganisationTypeType.SMSF));

        InvolvedPartyDetailsType additionalMember= investorTypeList.get(4).getInvestorDetails();
        assertThat(additionalMember.getPartyDetails().getIndividual().getGivenName(), is("Additional"));
        assertThat(additionalMember.getPartyDetails().getIndividual().getLastName(), is("Member"));
        assertThat(investorTypeList.get(4).getPartyRoleInRelatedOrganisation(), containsInAnyOrder(SMSF_MEMBER_ROLE));

        InvolvedPartyDetailsType additionalShareHolder= investorTypeList.get(5).getInvestorDetails();
        assertThat(additionalShareHolder.getPartyDetails().getIndividual().getGivenName(), is("Additional"));
        assertThat(additionalShareHolder.getPartyDetails().getIndividual().getLastName(), is("Shareholder"));
        assertThat(investorTypeList.get(5).getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_BENEFICIAL_OWNER_ROLE));

    }


    //------------------------Individual trust----------------------------

    @Test
    public void testTrustIndividualWithBeneficiaryOwner() throws Exception {
        setTrustTypeStaticReferenceCode("Discretionary/family trust");
        String jsonRequest = readJsonStringFromFile("trust-individual-family-2.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.INDIVIDUAL_TRUST));
        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertThat(investmentAccount.getAdviceType(), is(AdviceTypeType.PERSONAL_ADVICE));

        final List<InvestorType> investors = investmentAccount.getInvestors().getInvestor();
        assertThat(investors.size(), is(5));
        //Verifying Trustee details
        final InvestorType investor = investors.get(1);
        final IndividualType individual = investor.getInvestorDetails().getPartyDetails().getIndividual();
        assertIndividual(individual, "Deep", "Test", GenderTypeCode.MALE);
        StructuredAddressDetailType structuredAddressType = investor.getInvestorDetails().getPartyDetails().getIndividual().getResidentialAddress().getAddressDetail().getStructuredAddressDetail();
        assertThat(structuredAddressType.getAddressTypeDetail().getStandardAddress().getStreetNumber(), is("33"));
        assertThat(structuredAddressType.getAddressTypeDetail().getStandardAddress().getStreetName(), is("Pitt"));
        assertThat(structuredAddressType.getAddressTypeDetail().getStandardAddress().getStreetType(), is("ST"));
        assertNotNull(investor.getInvestorDetails().getPostalAddresses());
        assertThat(investor.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(TRUST_BENEFICIARY_ROLE, TRUST_TRUSTEE_ROLE, TRUST_BENEFICIAL_OWNER_ROLE));
        //Verifying Beneficiary Details
        final InvestorType benificiary = investors.get(2);
        final IndividualType benificiaryIndividual = benificiary.getInvestorDetails().getPartyDetails().getIndividual();

        assertThat(benificiary.getPartyRoleInRelatedOrganisation(), contains(TRUST_BENEFICIARY_ROLE));
        //Verifying BeneficiaryOwner Details
        final InvestorType benificiaryOwner = investors.get(3);
        final IndividualType benificiaryOwnerIndividual = benificiaryOwner.getInvestorDetails().getPartyDetails().getIndividual();
        assertIndividual(benificiaryOwnerIndividual, "Resp", "Test", GenderTypeCode.MALE);
        assertThat(benificiaryOwner.getPartyRoleInRelatedOrganisation(), contains(TRUST_BENEFICIAL_OWNER_ROLE));
        //Verifying BeneficiaryandBeneficiaryOwner Details
        final InvestorType benificiaryAndBeneficiaryOwner = investors.get(4);
        final IndividualType benificiaryAndBeneficiaryOwnerIndividual = benificiaryAndBeneficiaryOwner.getInvestorDetails().getPartyDetails().getIndividual();
        assertIndividual(benificiaryAndBeneficiaryOwnerIndividual, "BeneResp", "Test", GenderTypeCode.FEMALE);
        assertThat(benificiaryAndBeneficiaryOwner.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(TRUST_BENEFICIAL_OWNER_ROLE, TRUST_BENEFICIARY_ROLE));
    }

    @Test
    public void testTrustIndividualGovtSuper() throws Exception {
        setTrustTypeStaticReferenceCode("Discretionary/family trust");
        String jsonRequest = readJsonStringFromFile("trust_individual_govtsuper.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.INDIVIDUAL_TRUST));
        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertThat(investmentAccount.getAdviceType(), is(AdviceTypeType.PERSONAL_ADVICE));

        final List<InvestorType> investors = investmentAccount.getInvestors().getInvestor();
        assertThat(investors.size(), is(3));
        //Verifying Govt Trust Details
        final InvestorType investor = investors.get(0);
        final OrganisationType organisation = investor.getInvestorDetails().getPartyDetails().getOrganisation();
        assertThat(organisation.getOrganisationName(), is("trustee three submit"));
        assertThat(organisation.getABN(), is("67008617203"));
        assertThat(organisation.getTrustDetails().getTrustRegisteredState(), is("NSW"));
        assertNotNull(organisation.getTrustDetails().getTrustType().getSuperannuationFund());
        assertThat(investors.get(0).getInvestmentAccountPartyRole(), contains(PRIMARY_OWNER_ROLE));
        //Verifying Trustee Details
        final InvestorType trustee = investors.get(1);
        final IndividualType individual = trustee.getInvestorDetails().getPartyDetails().getIndividual();
        assertIndividual(individual, "newTEst", "lastname", GenderTypeCode.MALE);
        StructuredAddressDetailType structuredAddressType = trustee.getInvestorDetails().getPartyDetails().getIndividual().getResidentialAddress().getAddressDetail().getStructuredAddressDetail();
        assertThat(structuredAddressType.getAddressTypeDetail().getStandardAddress().getStreetNumber(), is("33"));
        assertThat(structuredAddressType.getAddressTypeDetail().getStandardAddress().getStreetName(), is("Pitt"));
        assertThat(structuredAddressType.getAddressTypeDetail().getStandardAddress().getStreetType(), is("ST"));
        assertNotNull(trustee.getInvestorDetails().getPostalAddresses());
        assertThat(trustee.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(TRUST_TRUSTEE_ROLE, TRUST_BENEFICIAL_OWNER_ROLE, TRUST_BENEFICIARY_ROLE));
    }

    @Test
    public void testTrustIndividualInvestmentScheme() throws Exception {
        setTrustTypeStaticReferenceCode("Discretionary/family trust");
        String jsonRequest = readJsonStringFromFile("trust_individual_invscheme.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.INDIVIDUAL_TRUST));
        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertThat(investmentAccount.getAdviceType(), is(AdviceTypeType.PERSONAL_ADVICE));

        final List<InvestorType> investors = investmentAccount.getInvestors().getInvestor();
        assertThat(investors.size(), is(2));
        //Verifying InvestmentScheme Details
        final InvestorType investor = investors.get(0);
        final OrganisationType organisation = investor.getInvestorDetails().getPartyDetails().getOrganisation();
        assertThat(organisation.getOrganisationName(), is("TrustRMIS"));
        assertThat(organisation.getTrustDetails().getTrustRegisteredState(), is("NSW"));
        assertNotNull(organisation.getTrustDetails().getTrustType().getInvestmentScheme());
        assertNotNull(organisation.getTrustDetails().getTrustType().getInvestmentScheme().getARSN(), is("089489225"));
        assertThat(investors.get(0).getInvestmentAccountPartyRole(), contains(PRIMARY_OWNER_ROLE));

        //Verifying Trustee Details
        final InvestorType trustee = investors.get(1);
        final IndividualType individual = trustee.getInvestorDetails().getPartyDetails().getIndividual();
        assertIndividual(individual, "Sam", "Anderson", GenderTypeCode.MALE);
    }

    @Test
    public void testIndvTrustWithAddressV2() throws Exception {
        setTrustTypeStaticReferenceCode("Discretionary/family trust");
        mockAddressService();
        featureToggles.setFeatureToggle("onboardingCMA", false);
        String jsonRequest = readJsonStringFromFile("indvTrustWithAddressv2.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.INDIVIDUAL_TRUST));
        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertNull(investmentAccount.getAccountProperties());

        List<InvestorType> investorTypeList = application.getInvestmentAccount().getInvestors().getInvestor();
        assertThat(investorTypeList.size(), is(3));
        InvestorType investor1 = investorTypeList.get(0);

        OrganisationType organisation = investor1.getInvestorDetails().getPartyDetails().getOrganisation();
        assertNotNull(organisation.getTrustDetails().getTrustType().getStandard());

        StructuredAddressDetailType organisationRegisteredAddress = organisation.getRegisteredAddress().getAddressDetail().getStructuredAddressDetail();
        validateAddress(organisationRegisteredAddress);


        InvestorType investor2 = investorTypeList.get(1);
        IndividualType individual = investor2.getInvestorDetails().getPartyDetails().getIndividual();

        RegisteredResidentialAddressDetailType residentialAddress = individual.getResidentialAddress();
        StructuredAddressDetailType structuredAddressDetail = residentialAddress.getAddressDetail().getStructuredAddressDetail();
        validateAddress(structuredAddressDetail);

        StructuredAddressDetailType postalAddress = investor1.getInvestorDetails().getPostalAddresses().getAddress().get(0).getAddressDetail().getStructuredAddressDetail();
        validateAddress(postalAddress);
    }

    @Test
    public void testIndvTrustWithCMA() throws Exception {
        setTrustTypeStaticReferenceCode("Discretionary/family trust");
        mockAddressService();

        featureToggles.setFeatureToggle("onboardingCMA", true);
        String jsonRequest = readJsonStringFromFile("indvTrustWithAddressv2.json");
        validateRequest(jsonRequest, false);
        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.INDIVIDUAL_TRUST));
        assertNotNull(investmentAccount.getAccountProperties());
        assertNotNull(investmentAccount.getAccountProperties().getAccountProperty());
    }


    //-----------------------Trust Corporate------------------

    @Test
    public void testCorporateTrustGovtSuper() throws Exception {
        setTrustTypeStaticReferenceCode("Discretionary/family trust");
        String jsonRequest = readJsonStringFromFile("corporatetrust_govtsuper.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.CORPORATE_TRUST));
        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertThat(investmentAccount.getAdviceType(), is(AdviceTypeType.PERSONAL_ADVICE));

        final List<InvestorType> investors = investmentAccount.getInvestors().getInvestor();
        assertThat(investors.size(), is(7));
        //Verifying CorporateTrustDetails
        final InvestorType trusteeDetails = investmentAccount.getInvestors().getInvestor().get(0);
        final OrganisationType corporateTrutee = trusteeDetails.getInvestorDetails().getPartyDetails().getOrganisation();
        assertThat(trusteeDetails.getInvestmentAccountPartyRole(), containsInAnyOrder(PRIMARY_OWNER_ROLE));
        assertThat(corporateTrutee.getOrganisationName(), is("Trust correlation 1"));
        assertThat(corporateTrutee.getTrustDetails().getTrustType().getSuperannuationFund().getLegislationName(), is("Fund 11"));
        assertNotNull(corporateTrutee.getTrustDetails().getTrustType().getSuperannuationFund());

        //Verifying CompanyTrusteeDetails
        final InvestorType companyTrusteeDetails = investmentAccount.getInvestors().getInvestor().get(1);
        final OrganisationType companyTrustee = companyTrusteeDetails.getInvestorDetails().getPartyDetails().getOrganisation();
        assertThat(companyTrusteeDetails.getInvestmentAccountPartyRole(), containsInAnyOrder(ACCOUNT_SERVICER_ROLE));
        assertThat(companyTrustee.getOrganisationName(), is("Comp corre 123"));
        assertThat(companyTrustee.getASICName(), is("Comp corre 123"));
        assertThat(companyTrustee.getACN(), is("010749961"));


        //Verifying DirectorDetails
        final InvestorType directorDetails = investmentAccount.getInvestors().getInvestor().get(2);
        final IndividualType director = directorDetails.getInvestorDetails().getPartyDetails().getIndividual();
        assertThat(directorDetails.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_DIRECTOR_ROLE, TRUST_BENEFICIARY_ROLE));
        assertIndividual(director, "Deepika", "Test", GenderTypeCode.FEMALE);
        StructuredAddressDetailType structuredAddressType = director.getResidentialAddress().getAddressDetail().getStructuredAddressDetail();
        assertThat(structuredAddressType.getAddressTypeDetail().getStandardAddress().getStreetNumber(), is("275"));
        assertThat(structuredAddressType.getAddressTypeDetail().getStandardAddress().getStreetName(), is("Kent"));
        assertThat(structuredAddressType.getAddressTypeDetail().getStandardAddress().getStreetType(), is("ST"));
        assertNotNull(directorDetails.getInvestorDetails().getPostalAddresses());

        //Verifying Beneficiary Details
        final InvestorType beneficiaryDetails = investmentAccount.getInvestors().getInvestor().get(3);
        final IndividualType beneficiary = beneficiaryDetails.getInvestorDetails().getPartyDetails().getIndividual();
        assertThat(beneficiaryDetails.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(TRUST_BENEFICIARY_ROLE));


        //Verifying Responsible Person Details
        final InvestorType responsiblePersonDetails = investmentAccount.getInvestors().getInvestor().get(4);
        final IndividualType responsiblePerson = responsiblePersonDetails.getInvestorDetails().getPartyDetails().getIndividual();
        assertThat(responsiblePersonDetails.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(TRUST_BENEFICIAL_OWNER_ROLE));
        assertIndividual(responsiblePerson, "Resp", "Test", GenderTypeCode.FEMALE);

        //Verifying BeneficiarynResponsible Person Details
        final InvestorType bennresponsibleDetails = investmentAccount.getInvestors().getInvestor().get(5);
        final IndividualType bennresPerson = bennresponsibleDetails.getInvestorDetails().getPartyDetails().getIndividual();
        assertThat(bennresponsibleDetails.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(TRUST_BENEFICIARY_ROLE, TRUST_BENEFICIAL_OWNER_ROLE));
        assertIndividual(bennresPerson, "Beneresp", "Test", GenderTypeCode.FEMALE);


    }

    @Test
    public void testCorporateTrustOther() throws Exception {
        setTrustTypeStaticReferenceCode("Discretionary/family trust");
        String jsonRequest = readJsonStringFromFile("corporatetrust_other.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.CORPORATE_TRUST));
        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertThat(investmentAccount.getAdviceType(), is(AdviceTypeType.PERSONAL_ADVICE));

        final List<InvestorType> investors = investmentAccount.getInvestors().getInvestor();
        assertThat(investors.size(), is(3));

        //Verifying CorporateTrustDetails
        final InvestorType trusteeDetails = investmentAccount.getInvestors().getInvestor().get(0);
        final OrganisationType corporateTrutee = trusteeDetails.getInvestorDetails().getPartyDetails().getOrganisation();
        assertThat(trusteeDetails.getInvestmentAccountPartyRole(), containsInAnyOrder(PRIMARY_OWNER_ROLE));
        assertThat(corporateTrutee.getOrganisationName(), is("TrustOther"));
        assertThat(corporateTrutee.getTrustDetails().getTrustType().getStandard().getSettlorOfTrust().getOrganisation().getOrganisationName(), is("OrganisationSettlor"));
        assertThat(corporateTrutee.getTrustDetails().getTrustType().getStandard().getTrustOtherDescription(), is("Charitable"));

        //Verifying CompanyTrusteeDetails
        final InvestorType companyTrusteeDetails = investmentAccount.getInvestors().getInvestor().get(1);
        final OrganisationType companyTrustee = companyTrusteeDetails.getInvestorDetails().getPartyDetails().getOrganisation();
        assertThat(companyTrusteeDetails.getInvestmentAccountPartyRole(), containsInAnyOrder(ACCOUNT_SERVICER_ROLE));
        assertThat(companyTrustee.getOrganisationName(), is("TrustCompany"));
        assertThat(companyTrustee.getASICName(), is("TrustCompany"));
        assertThat(companyTrustee.getACN(), is("004085616"));

        //Verifying DirectorDetails
        final InvestorType directorDetails = investmentAccount.getInvestors().getInvestor().get(2);
        final IndividualType director = directorDetails.getInvestorDetails().getPartyDetails().getIndividual();
        assertThat(directorDetails.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_DIRECTOR_ROLE, TRUST_BENEFICIARY_ROLE, COMPANY_BENEFICIAL_OWNER_ROLE));
        assertThat(directorDetails.getInvestmentAccountPartyRole(), containsInAnyOrder(ACCOUNT_SERVICER_ROLE, CONTACT_PERSON_ROLE));
        assertIndividual(director, "FirstTrust", "Last", GenderTypeCode.MALE);
    }

    @Test
    public void testCorporateTrustRegulatedInvestment() throws Exception {
        featureToggles.setFeatureToggle("onboardingCMA", false);
        setTrustTypeStaticReferenceCode("Discretionary/family trust");
        String jsonRequest = readJsonStringFromFile("corporatetrust_regulated.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.CORPORATE_TRUST));
        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertThat(investmentAccount.getAdviceType(), is(AdviceTypeType.PERSONAL_ADVICE));
        assertNull(investmentAccount.getAccountProperties());

        final List<InvestorType> investors = investmentAccount.getInvestors().getInvestor();
        assertThat(investors.size(), is(3));

        //Verifying CorporateTrustDetails
        final InvestorType trusteeDetails = investmentAccount.getInvestors().getInvestor().get(0);
        final OrganisationType corporateTrutee = trusteeDetails.getInvestorDetails().getPartyDetails().getOrganisation();
        assertThat(trusteeDetails.getInvestmentAccountPartyRole(), containsInAnyOrder(PRIMARY_OWNER_ROLE));
        assertThat(corporateTrutee.getOrganisationName(), is("TestRegulated"));
        assertNotNull(corporateTrutee.getTrustDetails().getTrustType().getRegulated().getRegulatorLicensingNumber(), is("2345678"));

        //Verifying CompanyTrusteeDetails
        final InvestorType companyTrusteeDetails = investmentAccount.getInvestors().getInvestor().get(1);
        final OrganisationType companyTrustee = companyTrusteeDetails.getInvestorDetails().getPartyDetails().getOrganisation();
        assertThat(companyTrusteeDetails.getInvestmentAccountPartyRole(), containsInAnyOrder(ACCOUNT_SERVICER_ROLE));
        assertThat(companyTrustee.getOrganisationName(), is("RegulatedCompany"));
        assertThat(companyTrustee.getASICName(), is("RegulatedCompany"));
        assertThat(companyTrustee.getACN(), is("004085616"));

        //Verifying DirectorDetails
        final InvestorType directorDetails = investmentAccount.getInvestors().getInvestor().get(2);
        final IndividualType director = directorDetails.getInvestorDetails().getPartyDetails().getIndividual();
        assertThat(directorDetails.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_DIRECTOR_ROLE, TRUST_BENEFICIARY_ROLE, COMPANY_BENEFICIAL_OWNER_ROLE));
        assertThat(directorDetails.getInvestmentAccountPartyRole(), containsInAnyOrder(ACCOUNT_SERVICER_ROLE, CONTACT_PERSON_ROLE));
        assertIndividual(director, "SamTrust", "Last", GenderTypeCode.MALE);
    }

    @Test
    public void testCorporateTrustRegulated_withCMA() throws Exception {
        featureToggles.setFeatureToggle("onboardingCMA", true);
        setTrustTypeStaticReferenceCode("Discretionary/family trust");
        String jsonRequest = readJsonStringFromFile("corporatetrust_regulated.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.CORPORATE_TRUST));
        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertThat(investmentAccount.getAdviceType(), is(AdviceTypeType.PERSONAL_ADVICE));
        assertNotNull(investmentAccount.getAccountProperties());
        assertNotNull(investmentAccount.getAccountProperties().getAccountProperty());

    }


    private void setTrustTypeStaticReferenceCode(String userId) {
        CodeImpl codeImpl = Mockito.mock(CodeImpl.class);
        when(codeImpl.getName()).thenReturn(userId);
        when(staticIntegrationService.loadCodeByUserId(eq(CodeCategory.TRUST_TYPE_DESC), anyString(), any(ServiceErrors.class))).thenReturn(codeImpl);
    }

    //-----------------------Company----------------------

    @Test
    public void companyWithOneBeneficialOwner() throws Exception {
        String jsonRequest = readJsonStringFromFile("companyWithOneBeneficialOwner.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.COMPANY));
        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertThat(investmentAccount.getAdviceType(), is(AdviceTypeType.PERSONAL_ADVICE));
        final List<InvestorType> investors = application.getInvestmentAccount().getInvestors().getInvestor();
        final List<InvestorType> existingInvestors = application.getInvestmentAccount().getInvestors().getExistingInvestor();


        assertThat(investors.size(), is(3));
        assertThat(existingInvestors.size(), is(3));

        assertThat(investors.get(1).getInvestmentAccountPartyRole(), contains(PRIMARY_OWNER_ROLE));

        assertThat(investors.get(0).getInvestmentAccountPartyRole(), contains(ACCOUNT_SERVICER_ROLE, CONTACT_PERSON_ROLE));
        assertThat(investors.get(0).getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_DIRECTOR_ROLE, COMPANY_BENEFICIAL_OWNER_ROLE));
        assertAuthorityProfiles(investors.get(0).getAuthorityProfiles(), CLIENT, LIMITED_CHANGE, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);

        assertThat(existingInvestors.get(0).getInvestmentAccountPartyRole(), contains(ACCOUNT_SERVICER_ROLE));
        assertThat(existingInvestors.get(0).getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_DIRECTOR_ROLE));
        assertAuthorityProfiles(existingInvestors.get(0).getAuthorityProfiles(), CLIENT, LIMITED_TRANSACTION, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);

        assertThat(existingInvestors.get(1).getInvestmentAccountPartyRole(), contains(ACCOUNT_SERVICER_ROLE));
        assertThat(existingInvestors.get(1).getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_SIGNATORY_ROLE, COMPANY_BENEFICIAL_OWNER_ROLE));
        assertAuthorityProfiles(existingInvestors.get(1).getAuthorityProfiles(), CLIENT, LIMITED_TRANSACTION, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);

        assertThat(existingInvestors.get(2).getInvestmentAccountPartyRole(), contains(ACCOUNT_SERVICER_ROLE));
        assertThat(existingInvestors.get(2).getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_SECRETARY_ROLE));
        assertAuthorityProfiles(existingInvestors.get(2).getAuthorityProfiles(), CLIENT, LIMITED_TRANSACTION, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);


        assertThat(investors.get(2).getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_BENEFICIAL_OWNER_ROLE));
        assertThat(investors.get(2).getInvestorDetails().getCorrelationSequenceNumber(), is("6"));
    }

    @Test
    public void companyWithOneShareholder() throws Exception {
        String jsonRequest = readJsonStringFromFile("companyWithOneShareHolder.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.COMPANY));
        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertThat(investmentAccount.getAdviceType(), is(AdviceTypeType.PERSONAL_ADVICE));
        final List<InvestorType> investors = application.getInvestmentAccount().getInvestors().getInvestor();
        final List<InvestorType> existingInvestors = application.getInvestmentAccount().getInvestors().getExistingInvestor();

        assertThat(investors.size(), is(3));
        assertThat(existingInvestors.size(), is(1));

        assertThat(investors.get(1).getInvestmentAccountPartyRole(), contains(PRIMARY_OWNER_ROLE));

        assertThat(investors.get(0).getInvestmentAccountPartyRole(), contains(ACCOUNT_SERVICER_ROLE));
        assertThat(investors.get(0).getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_DIRECTOR_ROLE, COMPANY_BENEFICIAL_OWNER_ROLE));
        assertAuthorityProfiles(investors.get(0).getAuthorityProfiles(), CLIENT, FULL_TRANSACTION, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);

        assertThat(existingInvestors.get(0).getInvestmentAccountPartyRole(), containsInAnyOrder(CONTACT_PERSON_ROLE, ACCOUNT_SERVICER_ROLE));
        assertThat(existingInvestors.get(0).getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_SIGNATORY_ROLE));
        assertAuthorityProfiles(existingInvestors.get(0).getAuthorityProfiles(), CLIENT, FULL_TRANSACTION, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);

        assertThat(investors.get(2).getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_BENEFICIAL_OWNER_ROLE));
    }

    @Test
    public void companyWithShareHolderAddressV2() throws Exception {

        featureToggles.setFeatureToggle("onboardingCMA", false);
        mockAddressService();
        String jsonRequest = readJsonStringFromFile("companyWithShareholderAddressV2.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.COMPANY));
        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertNull(investmentAccount.getAccountProperties());

        List<InvestorType> investors = application.getInvestmentAccount().getInvestors().getInvestor();

        InvolvedPartyDetailsType companyDetails = investors.get(1).getInvestorDetails();
        StructuredAddressDetailType companyPostalAddress = companyDetails.getPostalAddresses().getAddress().get(0).getAddressDetail().getStructuredAddressDetail();
        RegisteredResidentialAddressDetailType companyRegisteredAddress = companyDetails.getPartyDetails().getOrganisation().getRegisteredAddress();
        validateAddress(companyPostalAddress);
        validateAddress(companyRegisteredAddress.getAddressDetail().getStructuredAddressDetail());

        InvolvedPartyDetailsType shareholder = investors.get(2).getInvestorDetails();
        StructuredAddressDetailType shareHolderResidentialAddress = shareholder.getPartyDetails().getIndividual().getResidentialAddress().getAddressDetail().getStructuredAddressDetail();
        validateAddress(shareHolderResidentialAddress);
    }

    @Test
    public void companyWithCMA() throws Exception {

        featureToggles.setFeatureToggle("onboardingCMA", true);
        mockAddressService();
        String jsonRequest = readJsonStringFromFile("companyWithShareholderAddressV2.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.COMPANY));
        final InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertNotNull(investmentAccount.getAccountProperties());
        assertNotNull(investmentAccount.getAccountProperties().getAccountProperty());

    }

    //---------------Direct Individual tests------------------

    @Test
    public void directIndividual_new_standardAddress_Active() throws Exception {
        mockProductIntegrationService();
        String jsonRequest = readJsonStringFromFile("directIndividual_new_standardAddress_Active.json");
        DirectClientApplicationFormData directClientApplicationFormData = mapper.readValue(jsonRequest, DirectClientApplicationFormData.class);
        decodeContactValue(directClientApplicationFormData);
        IClientApplicationForm clientApplicationForm = ClientApplicationFormFactoryV1.getNewDirectClientApplicationForm(directClientApplicationFormData);
        validateRequest(clientApplicationForm);

        assertThat(application.getApplicationType(), is(OBApplicationTypeType.INDIVIDUAL));
        assertThat(application.getApplicationOrigin(), is(OBApplicationOriginType.WESTPAC_LIVE));


        InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        final List<InvestorType> investors = investmentAccount.getInvestors().getInvestor();
        assertThat(investors.size(), is(1));

        InvestorType investor = investors.get(0);

        assertThat(investor.getInvestorDetails().getTFNRegistration(), is(TFNRegistrationType.EXEMPT));
        assertThat(investor.getInvestorDetails().getTFNRegistrationExemption(), is(TFNRegistrationExemptionType.FINANCE_PROVIDER));

        CustomerIdentifiers customerIdentifiers = investor.getInvestorDetails().getCustomerIdentifiers();
        CustomerIdentifier cisKeyIdentifier = customerIdentifiers.getCustomerIdentifier().get(0);
        assertThat(cisKeyIdentifier.getCustomerNumberIdentifier().getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.WESTPAC_LEGACY));
        assertThat(cisKeyIdentifier.getCustomerNumberIdentifier().getCustomerNumber(), is("235234523523"));

        CustomerIdentifier customerNumberIdentifier = customerIdentifiers.getCustomerIdentifier().get(1);
        assertThat(customerNumberIdentifier.getCustomerNumberIdentifier().getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.WESTPAC));
        assertThat(customerNumberIdentifier.getCustomerNumberIdentifier().getCustomerNumber(), is("21142019"));

        assertThat(investor.getInvestmentAccountPartyRole(), contains(PRIMARY_OWNER_ROLE, CONTACT_PERSON_ROLE));
        assertAuthorityProfiles(investor.getAuthorityProfiles(), CLIENT, FULL_TRANSACTION, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);
        assertThat(investor.getInvestorDetails().getPartyDetails().getIndividual().getPartyIdentificationStatus(), is(PartyIdentificationStatusType.YES));
        assertThat(investmentAccount.getAdviceType(), is(AdviceTypeType.NO_ADVICE));

        List<CashAccountType> cashAccountTypes = investmentAccount.getCashAccounts().getCashAccount();
        assertThat(cashAccountTypes.size(), is(1));

        AddressesType postalAddresses = investor.getInvestorDetails().getPostalAddresses();
        StandardAddressType standardAddress = postalAddresses.getAddress().get(0).getAddressDetail().getStructuredAddressDetail().getAddressTypeDetail().getStandardAddress();
        assertThat(standardAddress.getUnitNumber(), is("9"));
        assertThat(standardAddress.getFloorNumber(), is("90"));
        assertThat(standardAddress.getStreetNumber(), is("438"));
        assertThat(standardAddress.getPropertyName(), is("Ece Arc"));
        assertThat(standardAddress.getStreetName(), is("Market"));
        assertThat(standardAddress.getStreetType(), is("ST"));
    }

    @Test
    public void directIndividual_new_nonStandardAddr() throws Exception {
        mockProductIntegrationService();
        String jsonRequest = readJsonStringFromFile("directIndividual_new_nonStandardAddr.json");
        DirectClientApplicationFormData directClientApplicationFormData = mapper.readValue(jsonRequest, DirectClientApplicationFormData.class);
        decodeContactValue(directClientApplicationFormData);
        IClientApplicationForm clientApplicationForm = ClientApplicationFormFactoryV1.getNewDirectClientApplicationForm(directClientApplicationFormData);
        validateRequest(clientApplicationForm);

        assertThat(application.getApplicationType(), is(OBApplicationTypeType.INDIVIDUAL));
        assertThat(application.getApplicationOrigin(), is(OBApplicationOriginType.WESTPAC_LIVE));


        InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        final List<InvestorType> investors = investmentAccount.getInvestors().getInvestor();
        assertThat(investors.size(), is(1));

        InvestorType investor = investors.get(0);

        AddressesType postalAddresses = investor.getInvestorDetails().getPostalAddresses();
        final List<String> addressDetailLine = postalAddresses.getAddress().get(0).getAddressDetail().getStructuredAddressDetail().getAddressTypeDetail().getNonStandardAddress().getAddressLine();
        assertThat(addressDetailLine.get(0), is("6/64 Gipps Street"));
    }

    @Test
    public void directIndividual_existing_Simple() throws Exception {
        featureToggles.setFeatureToggle("onboardingCMA", false);
        mockProductIntegrationService();
        mockAssetIntegrationService();
        String jsonRequest = readJsonStringFromFile("directIndividual_existing_nonStandardAddr_Simple.json");
        validateRequest(jsonRequest, true);

        assertThat(application.getApplicationType(), is(OBApplicationTypeType.INDIVIDUAL));
        assertThat(application.getApplicationOrigin(), is(OBApplicationOriginType.WESTPAC_LIVE));

        InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertNull(investmentAccount.getAccountProperties());
        List<InvestorType> existingInvestors = investmentAccount.getInvestors().getExistingInvestor();
        assertThat(existingInvestors.size(), is(1));

        InvestorType investor = existingInvestors.get(0);

        CustomerIdentifiers customerIdentifiers = investor.getInvestorDetails().getCustomerIdentifiers();

        CustomerIdentifier gcmIdentifier = customerIdentifiers.getCustomerIdentifier().get(0);
        assertThat(gcmIdentifier.getCustomerNumberIdentifier().getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.BT_PANORAMA));
        assertThat(gcmIdentifier.getCustomerNumberIdentifier().getCustomerNumber(), is("Some GCM ID"));

        CustomerIdentifier cisKeyIdentifier = customerIdentifiers.getCustomerIdentifier().get(1);
        assertThat(cisKeyIdentifier.getCustomerNumberIdentifier().getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.WESTPAC_LEGACY));
        assertThat(cisKeyIdentifier.getCustomerNumberIdentifier().getCustomerNumber(), is("CISKey"));

        CustomerIdentifier customerNumberIdentifier = customerIdentifiers.getCustomerIdentifier().get(2);
        assertThat(customerNumberIdentifier.getCustomerNumberIdentifier().getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.WESTPAC));
        assertThat(customerNumberIdentifier.getCustomerNumberIdentifier().getCustomerNumber(), is("WPACNumber"));

        final AccountInvestmentProductType accountInvestmentProductType = investmentAccount.getInvestmentProduct();
        assertThat(accountInvestmentProductType.getInvestmentCode(), is("assetId"));
        assertThat(accountInvestmentProductType.getInvestmentCodeIssuer(), is(InvestmentCodeIssuerType.AVALOQ));
        assertThat(accountInvestmentProductType.getType(), is(ProductType.MANAGED_PORTFOLIO));

        assertThat(accountInvestmentProductType.getServiceOfferCodeIssuer(), is(ServiceOfferCodeIssuerType.AVALOQ));
        assertThat(accountInvestmentProductType.getServiceOfferCode(), is("ServiceOfferCode"));

        final PaymentInstructionsType paymentInstructions = application.getPaymentInstructions();
        final PaymentInstructionType paymentInstructionType = paymentInstructions.getPaymentInstruction().get(0);
        assertThat(paymentInstructionType.getCreditDebitIndicator(), is(CreditDebitIndicatorType.DEBIT));
        assertThat(paymentInstructionType.getPaymentAmount(), is(new BigDecimal("42341241")));

        final PaymentAccountType debtorAccount = paymentInstructionType.getDebtorAccount();
        final FinancialInstitutionAccountType financialInstitutionAccount = debtorAccount.getFinancialInstitutionAccount();

        assertThat(financialInstitutionAccount.getAccountName(), is("Westpac Choice"));
        assertThat(financialInstitutionAccount.getAccountNumber(), is("750244"));
        assertThat(financialInstitutionAccount.getBSB(), is("732-006"));
    }

    @Test
    public void directIndividual_existing_withCMA() throws Exception {
        featureToggles.setFeatureToggle("onboardingCMA", true);
        mockProductIntegrationService();
        mockAssetIntegrationService();
        String jsonRequest = readJsonStringFromFile("directIndividual_existing_nonStandardAddr_Simple.json");
        validateRequest(jsonRequest, true);

        assertThat(application.getApplicationType(), is(OBApplicationTypeType.INDIVIDUAL));
        assertThat(application.getApplicationOrigin(), is(OBApplicationOriginType.WESTPAC_LIVE));

        InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        assertNull(investmentAccount.getAccountProperties());
    }

    //---------------Super Accumulation------------------

    @Test
    public void superAccumulation_minimalData() throws Exception {
        String jsonRequest = readJsonStringFromFile("superAccumulation.json");
        validateRequest(jsonRequest, false);

        InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        List<InvestorType> investors = investmentAccount.getInvestors().getInvestor();
        assertThat(investors.size(), is(1));
        final InvestorType investor = investors.get(0);
        assertThat(investor.getInvestmentAccountPartyRole(), contains(PRIMARY_OWNER_ROLE, CONTACT_PERSON_ROLE));
        assertAuthorityProfiles(investor.getAuthorityProfiles(), CLIENT, LIMITED_CHANGE, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);

        assertThat(application.getApplicationType(), is(OBApplicationTypeType.SUPERANNUATION));

        assertThat(application.getInvestmentAccount().getOwnershipType(), is(OwnershipTypeType.SINGLE_OWNER_ACCOUNT));

        final SuperAccountType accountPurpose = application.getInvestmentAccount().getAccountPurpose();
        assertNotNull(accountPurpose.getAccountType().getRetirementScheme());

    }

    @Test
    public void superPension_simple() throws Exception {
        String jsonRequest = readJsonStringFromFile("client_application_superpension_form.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.SUPERANNUATION));
        InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        List<InvestorType> investors = investmentAccount.getInvestors().getInvestor();
        assertThat(investors.size(), is(1));
        final InvestorType investor = investors.get(0);

        assertThat(investor.getInvestmentAccountPartyRole(), contains(PRIMARY_OWNER_ROLE, CONTACT_PERSON_ROLE));
        assertAuthorityProfiles(investor.getAuthorityProfiles(), CLIENT, LIMITED_CHANGE, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);
        assertThat(application.getInvestmentAccount().getOwnershipType(), is(OwnershipTypeType.SINGLE_OWNER_ACCOUNT));

        final SuperAccountType accountPurpose = application.getInvestmentAccount().getAccountPurpose();
        assertNotNull(accountPurpose.getAccountType().getPensionFundOrdinary());
        assertThat(accountPurpose.getAccountType().getPensionFundOrdinary().getEligibility(), is(EligibilityTypeType.AGE_65));
        assertThat(investor.getInvestorDetails().getTFNRegistrationExemption(), is(TFNRegistrationExemptionType.PENSIONER_FOR_SUPER));
    }

    @Test
    public void superPension_conditionsofrelease() throws Exception {
        String jsonRequest = readJsonStringFromFile("client_application_superpension_conditonsofrelease_form.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.SUPERANNUATION));
        InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        List<InvestorType> investors = investmentAccount.getInvestors().getInvestor();
        assertThat(investors.size(), is(1));
        final InvestorType investor = investors.get(0);

        assertThat(investor.getInvestmentAccountPartyRole(), contains(PRIMARY_OWNER_ROLE, CONTACT_PERSON_ROLE));
        assertAuthorityProfiles(investor.getAuthorityProfiles(), CLIENT, LIMITED_CHANGE, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);
        assertThat(application.getInvestmentAccount().getOwnershipType(), is(OwnershipTypeType.SINGLE_OWNER_ACCOUNT));

        final SuperAccountType accountPurpose = application.getInvestmentAccount().getAccountPurpose();
        assertNotNull(accountPurpose.getAccountType().getPensionFundOrdinary());
        assertThat(accountPurpose.getAccountType().getPensionFundOrdinary().getEligibility(), is(EligibilityTypeType.UNRESTRICTED_NON_PRESERVE));
        assertThat(accountPurpose.getAccountType().getPensionFundOrdinary().getConditionsOfRelease(), is(ConditionsOfReleaseType.AGE_65));
        assertThat(investor.getInvestorDetails().getTFN().get(0), is("25896314"));
    }


    @Test
    public void testDefaultTFNRegistrationForBeneficiaryWhenCRSEnabled() throws Exception {
        featureToggles.setFeatureToggle("onboardingCMA", true);
        when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(featureToggles);

        setTrustTypeStaticReferenceCode("Discretionary/family trust");
        mockAddressService();
        String jsonRequest = readJsonStringFromFile("trust_indiv_with_beneficiary_2.json");
        validateRequest(jsonRequest, false);
        //
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.INDIVIDUAL_TRUST));
        InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        List<InvestorType> investors = investmentAccount.getInvestors().getInvestor();
        assertThat(investors.size(), is(5));
        assertThat(investors.get(4).getInvestorDetails().getTFNRegistration(), is(NONE));
        assertThat(investors.get(4).getPartyRoleInRelatedOrganisation().contains(TRUST_BENEFICIARY_ROLE), is(true));
        assertThat(investors.get(4).getPartyRoleInRelatedOrganisation().size(), is(1));
    }

    @Test
    public void testWhenForeignRegisteredisNull() throws Exception {
        mockAddressService();
        String jsonRequest = readJsonStringFromFile("individual_existingForeignRegisteredNull.json");
        validateRequest(jsonRequest, false);
        //
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.INDIVIDUAL));
        InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        List<InvestorType> investors = investmentAccount.getInvestors().getInvestor();
        assertThat(investors.size(), is(1));
        assertThat(investors.get(0).getInvestorDetails().getTFNRegistration(), is(EXEMPT));
        assertThat(investors.get(0).getInvestorDetails().getTFNRegistrationExemption(), is(NON_RESIDENT));
        assertThat(investors.get(0).getInvestorDetails().getCountryOfResidenceForTax(), is("DE"));
    }

    @Test
    public void superpension_existinginvestor() throws Exception {
        String jsonRequest = readJsonStringFromFile("superpension_existingidps.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.SUPERANNUATION));
        InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        List<InvestorType> investors = investmentAccount.getInvestors().getExistingInvestor();
        assertThat(investors.size(), is(1));
        final InvestorType investor = investors.get(0);

        assertThat(investor.getInvestmentAccountPartyRole(), containsInAnyOrder(PRIMARY_OWNER_ROLE, CONTACT_PERSON_ROLE));
        assertAuthorityProfiles(investor.getAuthorityProfiles(), CLIENT, LIMITED_TRANSACTION, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);
        assertThat(application.getInvestmentAccount().getOwnershipType(), is(OwnershipTypeType.SINGLE_OWNER_ACCOUNT));

        final SuperAccountType accountPurpose = application.getInvestmentAccount().getAccountPurpose();
        assertNotNull(accountPurpose.getAccountType().getPensionFundOrdinary());
        assertThat(investor.getInvestorDetails().getTFN().size(), is(0));
        assertNotNull(investor.getInvestorDetails().getTFNRegistration());
        assertNull(investor.getInvestorDetails().getTFNRegistrationExemption());

    }

    @Test
    public void individual_existingPanoramaInvestorWithCRS() throws Exception {
        featureToggles.setFeatureToggle("retrieveCrsEnabled", true);
        String jsonRequest = readJsonStringFromFile("individual_existingPanoramaWithCRS.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.INDIVIDUAL));
        InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        List<InvestorType> investors = investmentAccount.getInvestors().getExistingInvestor();
        assertThat(investors.size(), is(1));
        final InvestorType investor = investors.get(0);

        assertThat(investor.getInvestmentAccountPartyRole(), containsInAnyOrder(PRIMARY_OWNER_ROLE, CONTACT_PERSON_ROLE));
        assertAuthorityProfiles(investor.getAuthorityProfiles(), CLIENT, LIMITED_CHANGE, APPLICATION_MAINTENANCE, APPLICATION_APPROVAL);
        assertThat(application.getInvestmentAccount().getOwnershipType(), is(OwnershipTypeType.SINGLE_OWNER_ACCOUNT));

        assertThat(investor.getInvestorDetails().getTFN().size(), is(0));
        assertThat(investor.getInvestorDetails().getTFNRegistration(), is(TFNRegistrationType.EXEMPT));
        assertThat(investor.getInvestorDetails().getTFNRegistrationExemption(), is(TFNRegistrationExemptionType.NORFOLK_ISLAND_RESIDENT));
        assertThat(investor.getInvestorDetails().getCountryOfResidenceForTax(), is("AU"));


    }

    @Test(expected = JsonMappingException.class)
    public void shouldThrowExceptionForAnIncorrectApprovalType() throws Exception {
        String jsonRequest = readJsonStringFromFile("individual_with_incorrect_approval_type.json");
        validateRequest(jsonRequest, false);
    }

    @Test
    public void shouldReturnApprovalTypeForNonDirectApplicationsAsOfflineIfValueIsOffline() throws Exception {
        String jsonRequest = readJsonStringFromFile("newIndividualSMSF.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationApprovalType(), is(OBApplicationApprovalType.OFFLINE));
    }

    @Test
    public void shouldReturnApprovalTypeForNonDirectApplicationsAsOnlineIfValueIsOnline() throws Exception {
        String jsonRequest = readJsonStringFromFile("individual.json");
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationApprovalType(), is(OBApplicationApprovalType.ONLINE));
    }

    @Test
    public void shouldReturnApprovalTypeAsOnlineForDirectEvenIfApprovalTypeIsOffline() throws Exception {
        mockProductIntegrationService();
        mockAssetIntegrationService();
        //approval type value is set as offline for this one intentionally
        String jsonRequest = readJsonStringFromFile("directIndividual_existing_nonStandardAddr_Simple.json");
        validateRequest(jsonRequest, true);

        assertThat(application.getApplicationApprovalType(), is(OBApplicationApprovalType.ONLINE));
    }

    @Test
    public void shouldReturnIsAccountManuallyEnteredForDirect() throws Exception {
        mockProductIntegrationService();
        mockAssetIntegrationService();
        validateRequest(readJsonStringFromFile("directIndividual_existing_manuallyEnteredLinkedAccount.json"), true);
        CashAccountType cashAcc = application.getInvestmentAccount().getCashAccounts().getCashAccount().get(0);
        assertThat(cashAcc.getLinkedFinancialInstitutions().getLinkedFinancialInstitution().get(0).isIsAccountManuallyEntered(), is(true));
    }

    @Test
    public void shouldReturnApprovalTypeAsOnlineForDirectWhenApprovalTypeIsOnline() throws Exception {
        mockProductIntegrationService();
        String jsonRequest = readJsonStringFromFile("directIndividual_new_nonStandardAddr.json");
        DirectClientApplicationFormData directClientApplicationFormData = mapper.readValue(jsonRequest, DirectClientApplicationFormData.class);
        decodeContactValue(directClientApplicationFormData);
        IClientApplicationForm clientApplicationForm = ClientApplicationFormFactoryV1.getNewDirectClientApplicationForm(directClientApplicationFormData);
        validateRequest(clientApplicationForm);

        assertThat(application.getApplicationApprovalType(), is(OBApplicationApprovalType.ONLINE));
    }

    @Test
    public void shouldSetCrsDataForDirectIndividual() throws Exception {
        mockProductIntegrationService();
        String jsonRequest = readJsonStringFromFile("directIndividual_crsData.json");
        DirectClientApplicationFormData directClientApplicationFormData = mapper.readValue(jsonRequest, DirectClientApplicationFormData.class);
        decodeContactValue(directClientApplicationFormData);
        IClientApplicationForm clientApplicationForm = ClientApplicationFormFactoryV1.getNewDirectClientApplicationForm(directClientApplicationFormData);
        validateRequest(clientApplicationForm);

        InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        InvolvedPartyDetailsType investorDetails = investmentAccount.getInvestors().getInvestor().get(0).getInvestorDetails();

        assertThat(investorDetails.getTFNRegistrationExemption(), is(TFNRegistrationExemptionType.NORFOLK_ISLAND_RESIDENT));
        assertThat(investorDetails.getCountryOfResidenceForTax(), is("AU"));
    }


    @Test
    public void shouldSetCrsDataForDirectIndividual_withTaxOption_ProvideLater() throws Exception {
        mockProductIntegrationService();
        String jsonRequest = readJsonStringFromFile("directIndividual_crsData_new.json");
        DirectClientApplicationFormData directClientApplicationFormData = mapper.readValue(jsonRequest, DirectClientApplicationFormData.class);
        decodeContactValue(directClientApplicationFormData);
        IClientApplicationForm clientApplicationForm = ClientApplicationFormFactoryV1.getNewDirectClientApplicationForm(directClientApplicationFormData);
        validateRequest(clientApplicationForm);

        InvestmentAccountType investmentAccount = application.getInvestmentAccount();
        InvolvedPartyDetailsType investorDetails = investmentAccount.getInvestors().getInvestor().get(0).getInvestorDetails();

        assertThat(investorDetails.getTFNRegistration(), is(TFNRegistrationType.NONE));
        assertThat(investorDetails.getCountryOfResidenceForTax(), is("AU"));
    }

    @Test
    public void testBuildFromJsonForSuperAccumulationCRS_withOverseasTIN() throws Exception {
        String jsonRequest = readJsonStringFromFile("client_application_superAccumulation_CRS.json");
        mockAddressService();
        validateRequest(jsonRequest, false);

        assertThat(application.getApplicationType(), is(OBApplicationTypeType.SUPERANNUATION));
        assertThat(application.getInvestmentAccount().getInvestors().getInvestor().size(), is(1));

        InvolvedPartyDetailsType investor = application.getInvestmentAccount().getInvestors().getInvestor().get(0).getInvestorDetails();
        assertThat(investor.getForeignCountriesForTaxation().getForeignCountryForTaxation().size(), is(1));
        assertThat(investor.getTFN().get(0), is("123456782"));
        assertThat(investor.getCountryOfResidenceForTax(), is("AU"));
        assertThat(investor.getTFNRegistration(), is(ONE));

        ForeignCountriesForTaxationType foreignCountriesForTaxationType = investor.getForeignCountriesForTaxation();
        assertThat(foreignCountriesForTaxationType.getForeignCountryForTaxation().size(), is(1));
        ForeignCountryForTaxationType foreignCountryForTaxationType = foreignCountriesForTaxationType.getForeignCountryForTaxation().get(0);
        assertThat(foreignCountryForTaxationType.getCountryCode(), is("AF"));
        assertThat(foreignCountryForTaxationType.getTIN(), is("OVERSEASTIN"));
        assertNull(foreignCountryForTaxationType.getReasonForTaxIdentificationNumberExemption());

    }

    @Test
    public void testBuildFromJsonForJointCRS_withMutipleOverseasTIN() throws Exception {
        String jsonRequest = readJsonStringFromFile("client_application_joint_CRS.json");
        mockAddressService();
        validateRequest(jsonRequest, false);

        assertThat(application.getApplicationType(), is(OBApplicationTypeType.JOINT));
        assertThat(application.getInvestmentAccount().getInvestors().getInvestor().size(), is(2));

        InvolvedPartyDetailsType investorOne = application.getInvestmentAccount().getInvestors().getInvestor().get(0).getInvestorDetails();
        ForeignCountriesForTaxationType foreignTaxCountriesForInvestorOne = investorOne.getForeignCountriesForTaxation();

        assertThat(investorOne.getForeignCountriesForTaxation().getForeignCountryForTaxation().size(), is(4));
        assertThat(investorOne.getTFN().size(), is(0));
        assertThat(investorOne.getCountryOfResidenceForTax(), is("BR"));
        assertThat(investorOne.getTFNRegistration(), is(NONE));

        ForeignCountryForTaxationType foreignCountryOne = foreignTaxCountriesForInvestorOne.getForeignCountryForTaxation().get(0);
        assertThat(foreignCountryOne.getCountryCode(), is("BD"));
        assertNull(foreignCountryOne.getTIN());
        assertThat(foreignCountryOne.getReasonForTaxIdentificationNumberExemption(), is(TIN_NEVER_ISSUED));

        ForeignCountryForTaxationType foreignCountryTwo = foreignTaxCountriesForInvestorOne.getForeignCountryForTaxation().get(1);
        assertThat(foreignCountryTwo.getCountryCode(), is("AR"));
        assertThat(foreignCountryTwo.getTIN(), is("ARGENTINATAXNO"));
        assertNull(foreignCountryTwo.getReasonForTaxIdentificationNumberExemption());

        ForeignCountryForTaxationType foreignCountryThree = foreignTaxCountriesForInvestorOne.getForeignCountryForTaxation().get(2);
        assertThat(foreignCountryThree.getCountryCode(), is("BR"));
        assertNull(foreignCountryThree.getTIN());
        assertThat(foreignCountryThree.getReasonForTaxIdentificationNumberExemption(), is(UNDER_AGE));

        ForeignCountryForTaxationType foreignCountryFour = foreignTaxCountriesForInvestorOne.getForeignCountryForTaxation().get(3);
        assertThat(foreignCountryFour.getCountryCode(), is("KH"));
        assertNull(foreignCountryFour.getTIN());
        assertThat(foreignCountryFour.getReasonForTaxIdentificationNumberExemption(), is(TIN_PENDING));

        InvolvedPartyDetailsType investorTwo = application.getInvestmentAccount().getInvestors().getInvestor().get(1).getInvestorDetails();
        assertNull(investorTwo.getForeignCountriesForTaxation());
        assertThat(investorTwo.getTFN().size(), is(0));
        assertThat(investorTwo.getCountryOfResidenceForTax(), is("AU"));
        assertThat(investorTwo.getTFNRegistration(), is(TFNRegistrationType.EXEMPT));
        assertThat(investorTwo.getTFNRegistrationExemption(), is(TFNRegistrationExemptionType.PENSIONER));

    }

    @Test
    public void testBuildFromJsonForIndividualAdviserContributionFees() throws Exception{
        String jsonRequest = readJsonStringFromFile("clientapplication_individual_advisercontribution.json");
        mockAddressService();
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.INDIVIDUAL));
        List<FeeInfoType> feeInfoTypeList = application.getInvestmentAccount().getAdvisers().getAdviser().get(0).getAdviserDetails().getOrganisation().getOrganisationUnit().get(0).getPosition().get(0).getFees().getFee();
        assertThat(feeInfoTypeList.get(0).getFeeClassification(),is(FeeClassificationType.ESTABLISHMENT));
        assertThat(feeInfoTypeList.get(1).getFeeClassification(),is(FeeClassificationType.LICENSEE_ADVICE));
        assertThat(feeInfoTypeList.get(2).getFeeClassification(),is(FeeClassificationType.ADVISER_CONTRIBUTION));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(0).getPercentageFee().getInvestmentProduct(),is(ProductType.CASH_DEPOSIT));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(0).getPercentageFee().getFeeFrequency(),is(FeeFrequencyType.ONE_OFF));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(0).getPercentageFee().getAppliedFeeRate(),is(new BigDecimal("2.00")));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(1).getPercentageFee().getInvestmentProduct(),is(ProductType.CASH_DEPOSIT));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(1).getPercentageFee().getFeeFrequency(),is(FeeFrequencyType.ONGOING));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(1).getPercentageFee().getAppliedFeeRate(),is(new BigDecimal("3.00")));
    }

    @Test
    public void testBuildFromJsonForSuperAdviserContributionFees() throws Exception {
        String jsonRequest = readJsonStringFromFile("clientapplication_super_advisercontributionfees.json");
        mockAddressService();
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.SUPERANNUATION));
        List<FeeInfoType> feeInfoTypeList = application.getInvestmentAccount().getAdvisers().getAdviser().get(0).getAdviserDetails().getOrganisation().getOrganisationUnit().get(0).getPosition().get(0).getFees().getFee();
        assertThat(feeInfoTypeList.get(0).getFeeClassification(),is(FeeClassificationType.ESTABLISHMENT));
        assertThat(feeInfoTypeList.get(1).getFeeClassification(),is(FeeClassificationType.LICENSEE_ADVICE));
        assertThat(feeInfoTypeList.get(2).getFeeClassification(),is(FeeClassificationType.ADVISER_CONTRIBUTION));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(0).getPercentageFee().getInvestmentProduct(),is(ProductType.SUPERANNUATION_CONTRIBUTION));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(0).getPercentageFee().getFeeSource(),is(FeeSourceType.SELF));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(0).getPercentageFee().getFeeFrequency(),is(FeeFrequencyType.ONE_OFF));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(0).getPercentageFee().getAppliedFeeRate(),is(new BigDecimal("5.00")));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(1).getPercentageFee().getInvestmentProduct(),is(ProductType.SUPERANNUATION_CONTRIBUTION));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(1).getPercentageFee().getFeeSource(),is(FeeSourceType.EMPLOYER));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(1).getPercentageFee().getFeeFrequency(),is(FeeFrequencyType.ONE_OFF));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(1).getPercentageFee().getAppliedFeeRate(),is(new BigDecimal("2.00")));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(2).getPercentageFee().getInvestmentProduct(),is(ProductType.SUPERANNUATION_CONTRIBUTION));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(2).getPercentageFee().getFeeSource(),is(FeeSourceType.SPOUSE));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(2).getPercentageFee().getFeeFrequency(),is(FeeFrequencyType.ONE_OFF));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(2).getPercentageFee().getAppliedFeeRate(),is(new BigDecimal("6.00")));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(3).getPercentageFee().getInvestmentProduct(),is(ProductType.SUPERANNUATION_CONTRIBUTION));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(3).getPercentageFee().getFeeSource(),is(FeeSourceType.SELF));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(3).getPercentageFee().getFeeFrequency(),is(FeeFrequencyType.ONGOING));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(3).getPercentageFee().getAppliedFeeRate(),is(new BigDecimal("6.00")));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(4).getPercentageFee().getInvestmentProduct(),is(ProductType.SUPERANNUATION_CONTRIBUTION));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(4).getPercentageFee().getFeeSource(),is(FeeSourceType.SPOUSE));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(4).getPercentageFee().getFeeFrequency(),is(FeeFrequencyType.ONGOING));
        assertThat(feeInfoTypeList.get(2).getFeeMethod().get(4).getPercentageFee().getAppliedFeeRate(),is(new BigDecimal("7.00")));
    }

    @Test
    public void testBuildFromJsonForPensionAdviserContributionFees() throws Exception {
        String jsonRequest = readJsonStringFromFile("clientapplication_pension_advisercontributionfees.json");
        mockAddressService();
        validateRequest(jsonRequest, false);
        assertThat(application.getApplicationType(), is(OBApplicationTypeType.SUPERANNUATION));
        final SuperAccountType accountPurpose = application.getInvestmentAccount().getAccountPurpose();
        assertNotNull(accountPurpose.getAccountType().getPensionFundOrdinary());
        List<FeeInfoType> feeInfoTypeList = application.getInvestmentAccount().getAdvisers().getAdviser().get(0).getAdviserDetails().getOrganisation().getOrganisationUnit().get(0).getPosition().get(0).getFees().getFee();
        assertThat(feeInfoTypeList.get(0).getFeeClassification(),is(FeeClassificationType.ESTABLISHMENT));
        assertThat(feeInfoTypeList.get(1).getFeeClassification(),is(FeeClassificationType.ONGOING_ADVICE));
        assertThat(feeInfoTypeList.get(2).getFeeClassification(),is(FeeClassificationType.LICENSEE_ADVICE));
        assertThat(feeInfoTypeList.get(3).getFeeClassification(),is(FeeClassificationType.ADVISER_CONTRIBUTION));
        assertThat(feeInfoTypeList.get(3).getFeeMethod().get(0).getPercentageFee().getInvestmentProduct(),is(ProductType.SUPERANNUATION_CONTRIBUTION));
        assertThat(feeInfoTypeList.get(3).getFeeMethod().get(0).getPercentageFee().getFeeSource(),is(FeeSourceType.SELF));
        assertThat(feeInfoTypeList.get(3).getFeeMethod().get(0).getPercentageFee().getFeeFrequency(),is(FeeFrequencyType.ONE_OFF));
        assertThat(feeInfoTypeList.get(3).getFeeMethod().get(0).getPercentageFee().getAppliedFeeRate(),is(new BigDecimal("75.00")));
        assertThat(feeInfoTypeList.get(3).getFeeMethod().get(1).getPercentageFee().getInvestmentProduct(),is(ProductType.SUPERANNUATION_CONTRIBUTION));
        assertThat(feeInfoTypeList.get(3).getFeeMethod().get(1).getPercentageFee().getFeeSource(),is(FeeSourceType.EMPLOYER));
        assertThat(feeInfoTypeList.get(3).getFeeMethod().get(1).getPercentageFee().getFeeFrequency(),is(FeeFrequencyType.ONE_OFF));
        assertThat(feeInfoTypeList.get(3).getFeeMethod().get(1).getPercentageFee().getAppliedFeeRate(),is(new BigDecimal("23.00")));
    }

    @Test
    public void testBuildFromJsonForExistingCorporateSMSFwithCRS() throws Exception {
        String jsonRequest = readJsonStringFromFile("client_application_existingCorporateSMSF_CRS.json");
        mockAddressService();
        validateRequest(jsonRequest, false);

        assertThat(application.getApplicationType(), is(OBApplicationTypeType.CORPORATE_SMSF));
        assertThat(application.getInvestmentAccount().getInvestors().getInvestor().size(), is(6)); //SMSF + COMPANY TRUSTEE+ DIRECTOR + 3 SHAREHOLDERS

        //************* - Director **********
        InvolvedPartyDetailsType director = application.getInvestmentAccount().getInvestors().getInvestor().get(0).getInvestorDetails();
        assertNull(director.getPartyDetails().getOrganisation());
        assertThat(director.getTFN().size(), is(0));
        assertThat(director.getCountryOfResidenceForTax(), is("AU"));
        assertNull(director.getTFNRegistrationExemption());
        assertThat(director.getTFNRegistration(), is(NONE));
        assertThat(director.getPartyDetails().getIndividual().getGivenName(), is("DIRECTOR"));
        assertThat(director.getPartyDetails().getIndividual().getLastName(), is("ONE"));

        ForeignCountriesForTaxationType foreignTaxCountriesForInvestorOne = director.getForeignCountriesForTaxation();
        assertThat(director.getForeignCountriesForTaxation().getForeignCountryForTaxation().size(), is(2));

        ForeignCountryForTaxationType foreignCountryOne = foreignTaxCountriesForInvestorOne.getForeignCountryForTaxation().get(0);
        assertThat(foreignCountryOne.getCountryCode(), is("CV"));
        assertThat(foreignCountryOne.getTIN(), is("DIRTIN"));
        assertNull(foreignCountryOne.getReasonForTaxIdentificationNumberExemption());

        ForeignCountryForTaxationType foreignCountryTwo = foreignTaxCountriesForInvestorOne.getForeignCountryForTaxation().get(1);
        assertThat(foreignCountryTwo.getCountryCode(), is("KH"));
        assertThat(foreignCountryTwo.getTIN(), is("DIRTINTWO"));
        assertNull(foreignCountryTwo.getReasonForTaxIdentificationNumberExemption());

        //************* - Company Trustee **********
        InvolvedPartyDetailsType companyTrustee = application.getInvestmentAccount().getInvestors().getInvestor().get(1).getInvestorDetails();
        assertThat(companyTrustee.getPartyDetails().getOrganisation().getOrganisationName(), is("COMPANY SMSF"));
        assertThat(companyTrustee.getPartyDetails().getOrganisation().getOrganisationType(), is(OrganisationTypeType.COMPANY));
        assertThat(companyTrustee.getTFN().size(), is(0));
        assertThat(companyTrustee.getCountryOfResidenceForTax(), is("AU"));
        assertThat(companyTrustee.getTFNRegistration(), is(TFNRegistrationType.NONE));
        assertNull(companyTrustee.getTFNRegistrationExemption());

        assertThat(companyTrustee.getForeignCountriesForTaxation().getForeignCountryForTaxation().size(), is(3));
        ForeignCountriesForTaxationType foreignTaxCountriesForCompanyTrustee = companyTrustee.getForeignCountriesForTaxation();

        ForeignCountryForTaxationType companyTrusteeFCOne = foreignTaxCountriesForCompanyTrustee.getForeignCountryForTaxation().get(0);
        assertThat(companyTrusteeFCOne.getCountryCode(), is("AX"));
        assertNull(companyTrusteeFCOne.getTIN());
        assertThat(companyTrusteeFCOne.getReasonForTaxIdentificationNumberExemption(), is(TIN_NEVER_ISSUED));

        ForeignCountryForTaxationType companyTrusteeFCTwo = foreignTaxCountriesForCompanyTrustee.getForeignCountryForTaxation().get(1);
        assertThat(companyTrusteeFCTwo.getCountryCode(), is("AT"));
        assertNull(companyTrusteeFCTwo.getTIN());
        assertThat(companyTrusteeFCTwo.getReasonForTaxIdentificationNumberExemption(), is(TIN_PENDING));

        ForeignCountryForTaxationType companyTrusteeFCThree = foreignTaxCountriesForCompanyTrustee.getForeignCountryForTaxation().get(2);
        assertThat(companyTrusteeFCThree.getCountryCode(), is("BE"));
        assertNull(companyTrusteeFCThree.getTIN());
        assertThat(companyTrusteeFCThree.getReasonForTaxIdentificationNumberExemption(), is(TIN_PENDING));

        //************* - SMSF **********
        InvolvedPartyDetailsType smsf = application.getInvestmentAccount().getInvestors().getInvestor().get(2).getInvestorDetails();
        assertThat(smsf.getPartyDetails().getOrganisation().getOrganisationName(), is("OTHER SUBMIT CORPORATE"));
        assertThat(smsf.getPartyDetails().getOrganisation().getOrganisationType(), is(OrganisationTypeType.SMSF));
        assertThat(smsf.getTFN().get(0), is("123456782"));
        assertThat(smsf.getCountryOfResidenceForTax(), is("AU"));
        assertThat(smsf.getTFNRegistration(), is(TFNRegistrationType.ONE));
        assertNull(smsf.getTFNRegistrationExemption());

        assertThat(smsf.getForeignCountriesForTaxation().getForeignCountryForTaxation().size(), is(1));

        ForeignCountryForTaxationType companySMSFFCOne = smsf.getForeignCountriesForTaxation().getForeignCountryForTaxation().get(0);
        assertThat(companySMSFFCOne.getCountryCode(), is("AX"));
        assertThat(companySMSFFCOne.getTIN(), is("SMSFTIN"));
        assertNull(companySMSFFCOne.getReasonForTaxIdentificationNumberExemption());

        //************* - SHAREHOLDER ONE **********
        InvolvedPartyDetailsType shareholderOne = application.getInvestmentAccount().getInvestors().getInvestor().get(3).getInvestorDetails();
        assertThat(shareholderOne.getPartyDetails().getIndividual().getGivenName(), is("NO"));
        assertThat(shareholderOne.getPartyDetails().getIndividual().getMiddleName().get(0), is("ONE"));
        assertThat(shareholderOne.getPartyDetails().getIndividual().getLastName(), is("OVERSEAS"));
        assertNull(shareholderOne.getPartyDetails().getOrganisation());

        assertThat(shareholderOne.getTFN().size(), is(0));
        assertThat(shareholderOne.getCountryOfResidenceForTax(), is("AF"));
        assertThat(shareholderOne.getTFNRegistration(), is(TFNRegistrationType.NONE));
        assertNull(shareholderOne.getTFNRegistrationExemption());

        assertThat(shareholderOne.getForeignCountriesForTaxation().getForeignCountryForTaxation().size(), is(1));

        ForeignCountryForTaxationType shareHolderFCOne = shareholderOne.getForeignCountriesForTaxation().getForeignCountryForTaxation().get(0);
        assertThat(shareHolderFCOne.getCountryCode(), is("AF"));
        assertThat(shareHolderFCOne.getTIN(), is("AFGTIN"));
        assertNull(shareHolderFCOne.getReasonForTaxIdentificationNumberExemption());

        //************* - SHAREHOLDER TWO **********
        InvolvedPartyDetailsType shareholderTWO = application.getInvestmentAccount().getInvestors().getInvestor().get(4).getInvestorDetails();
        assertThat(shareholderTWO.getPartyDetails().getIndividual().getGivenName(), is("NO"));
        assertThat(shareholderTWO.getPartyDetails().getIndividual().getMiddleName().get(0), is("TWO"));
        assertThat(shareholderTWO.getPartyDetails().getIndividual().getLastName(), is("OVERSEAS"));
        assertNull(shareholderTWO.getPartyDetails().getOrganisation());

        assertThat(shareholderTWO.getTFN().size(), is(0));
        assertThat(shareholderTWO.getCountryOfResidenceForTax(), is("VI"));
        assertThat(shareholderTWO.getTFNRegistration(), is(TFNRegistrationType.NONE));
        assertNull(shareholderTWO.getTFNRegistrationExemption());

        assertThat(shareholderTWO.getForeignCountriesForTaxation().getForeignCountryForTaxation().size(), is(2));

        ForeignCountryForTaxationType shareHolderTwoFC1 = shareholderTWO.getForeignCountriesForTaxation().getForeignCountryForTaxation().get(0);
        assertThat(shareHolderTwoFC1.getCountryCode(), is("DZ"));
        assertThat(shareHolderTwoFC1.getTIN(), is("ALGTIN"));
        assertNull(shareHolderTwoFC1.getReasonForTaxIdentificationNumberExemption());

        ForeignCountryForTaxationType shareHolderTwoFC2 = shareholderTWO.getForeignCountriesForTaxation().getForeignCountryForTaxation().get(1);
        assertThat(shareHolderTwoFC2.getCountryCode(), is("VI"));
        assertNull(shareHolderTwoFC2.getTIN());
        assertThat(shareHolderTwoFC2.getReasonForTaxIdentificationNumberExemption(), is(TIN_PENDING));

        //************* - SHAREHOLDER THREE **********
        InvolvedPartyDetailsType shareholderThree = application.getInvestmentAccount().getInvestors().getInvestor().get(5).getInvestorDetails();
        assertThat(shareholderThree.getPartyDetails().getIndividual().getGivenName(), is("YES"));
        assertThat(shareholderThree.getPartyDetails().getIndividual().getLastName(), is("TIN"));
        assertNull(shareholderThree.getPartyDetails().getOrganisation());

        assertThat(shareholderThree.getTFN().size(), is(0));
        assertThat(shareholderThree.getCountryOfResidenceForTax(), is("AU"));
        assertThat(shareholderThree.getTFNRegistration(), is(TFNRegistrationType.NONE));
        assertNull(shareholderThree.getTFNRegistrationExemption());

        assertNull(shareholderThree.getForeignCountriesForTaxation());
    }

    @Test
    public void testBuildFromJsonForDirectSuper_InvestorwithoutTFNsupplied() throws Exception {
        String jsonRequest = readJsonStringFromFile("com/bt/nextgen/api/draftaccount/model/individual-direct-superAccumulation.json");
        mockAddressService();
        mockProductIntegrationService();
        validateRequest(jsonRequest, true);

        assertThat(application.getApplicationType(), is(OBApplicationTypeType.SUPERANNUATION));
        assertThat(application.getApplicationOrigin(), is(OBApplicationOriginType.WESTPAC_LIVE));
        assertThat(application.getInvestmentAccount().getInvestors().getExistingInvestor().size(), is(1));

        InvolvedPartyDetailsType investor = application.getInvestmentAccount().getInvestors().getExistingInvestor().get(0).getInvestorDetails();
        assertNull(investor.getPartyDetails().getOrganisation());
        assertThat(investor.getTFN().size(), is(1));
        assertNull(investor.getCountryOfResidenceForTax());
        assertNull(investor.getTFNRegistrationExemption());
        assertThat(investor.getTFNRegistration(), is(ONE));
        assertThat(investor.getPartyDetails().getIndividual().getGivenName(), is("First name"));
        assertThat(investor.getPartyDetails().getIndividual().getLastName(), is("Surname"));
        assertNull(investor.getForeignCountriesForTaxation());

        assertThat(application.getInvestmentAccount().getOwnershipType(), is(OwnershipTypeType.SINGLE_OWNER_ACCOUNT));
    }

    @Test
    public void testBuildFromJsonForDirectSuperPension_InvestorwithoutTFNsupplied() throws Exception {
        String jsonRequest = readJsonStringFromFile("com/bt/nextgen/api/draftaccount/model/individual-direct-superPension.json");
        mockAddressService();
        validateRequest(jsonRequest, true);

        assertThat(application.getApplicationType(), is(OBApplicationTypeType.SUPERANNUATION));
        assertThat(application.getApplicationOrigin(), is(OBApplicationOriginType.WESTPAC_LIVE));
        assertThat(application.getInvestmentAccount().getInvestors().getExistingInvestor().size(), is(1));

        InvolvedPartyDetailsType investor = application.getInvestmentAccount().getInvestors().getExistingInvestor().get(0).getInvestorDetails();
        assertNull(investor.getPartyDetails().getOrganisation());
        assertThat(investor.getTFN().size(), is(1));
        assertThat(investor.getTFN().get(0), is("11111111"));
        assertNull(investor.getCountryOfResidenceForTax());
        assertNull(investor.getTFNRegistrationExemption());
        assertThat(investor.getTFNRegistration(), is(ONE));
        assertThat(investor.getPartyDetails().getIndividual().getGivenName(), is("First name"));
        assertThat(investor.getPartyDetails().getIndividual().getLastName(), is("Surname"));
        assertNull(investor.getForeignCountriesForTaxation());

        assertThat(application.getInvestmentAccount().getOwnershipType(), is(OwnershipTypeType.SINGLE_OWNER_ACCOUNT));

        final SuperAccountType accountPurpose = application.getInvestmentAccount().getAccountPurpose();
        assertNotNull(accountPurpose.getAccountType().getPensionFundOrdinary());
        assertThat(accountPurpose.getAccountType().getPensionFundOrdinary().getEligibility(), is(EligibilityTypeType.UNRESTRICTED_NON_PRESERVE));
        assertThat(accountPurpose.getAccountType().getPensionFundOrdinary().getConditionsOfRelease(), is(ConditionsOfReleaseType.OTHER));
    }

    // TODO Alefiya, not the best hack but had to do for new direct
    private void decodeContactValue(DirectClientApplicationFormData directClientApplicationFormData) {
        Customer customer = directClientApplicationFormData.getInvestors().get(0);
        final String encodedValue = customer.getMobile().getValue();
        ContactValue value = new ContactValue();
        value.setValid(true);
        value.setValue(EncodedString.toPlainText(encodedValue));

        customer.setMobile(value);
    }

    private void mockAddressService() {
        when(addressV2CacheService.getAddress(anyString(), any(ServiceErrors.class))).thenReturn(getAddress());
    }

    private PostalAddress getAddress() {
        PostalAddress address = new PostalAddress();
        address.setBuildingName("Lennys Place");
        address.setUnitNumber("100");
        address.setFloor("28");
        address.setStreetNumber("33");
        address.setStreetName("Pitt");
        address.setStreetType("St");
        address.setCity("Sydney");
        address.setPostcode("2000");
        address.setState("NSW");
        return address;
    }


    private void validateAddress(StructuredAddressDetailType structuredAddressDetail) {
        assertThat(structuredAddressDetail.getState(), is("NSW"));
        assertThat(structuredAddressDetail.getPostcode(), is("2000"));
        assertThat(structuredAddressDetail.getCity(), is("Sydney"));

        StandardAddressType standardAddress = structuredAddressDetail.getAddressTypeDetail().getStandardAddress();
        assertThat(standardAddress.getStreetName(), is("Pitt"));
        assertThat(standardAddress.getStreetType(), is("ST"));
        assertThat(standardAddress.getStreetNumber(), is("33"));
        assertThat(standardAddress.getFloorNumber(), is("28"));
        assertThat(standardAddress.getUnitNumber(), is("100"));
    }

    @ComponentScan("com.bt.nextgen.api.draftaccount.builder")
    public static class TestConfig {
        @Bean(name = "test")
        public UserProfileService getUserProfileService() {
            return mock(UserProfileService.class);
        }

        @Bean
        public ClientListDtoService getClientListDtoService() {
            return mock(ClientListDtoService.class);
        }

        @Bean
        public BrokerIntegrationService getBrokerIntegrationService() {
            return mock(BrokerIntegrationService.class);
        }

        @Bean
        @Qualifier("avaloqClientIntegrationService")
        public ClientIntegrationService getClientIntegrationService() {
            return mock(ClientIntegrationService.class);
        }

        @Bean
        public StaticIntegrationService getStaticIntegrationService() {
            return mock(StaticIntegrationService.class);
        }

        @Bean
        @Qualifier("avaloqAssetIntegrationService")
        public AssetIntegrationService getAssetIntegrationService() {
            return mock(AssetIntegrationService.class);
        }

        @Bean
        public ProductIntegrationService getProductIntegrationService() {
            return mock(ProductIntegrationService.class);
        }

        @Bean
        public LocationManagementIntegrationService getLocationManagementIntegrationService() {
            return mock(LocationManagementIntegrationService.class);
        }

        @Bean
        public AddressV2CacheService getAddressV2CacheService() {
            return mock(AddressV2CacheService.class);
        }

        @Bean
        public FeatureTogglesService getFeatureTogglesService() {
            return mock(FeatureTogglesService.class);
        }

        @Bean
        public ClientApplicationDtoDeserializer clientApplicationDtoDeserializer() {
            return new ClientApplicationDtoDeserializer();
        }
    }
}
