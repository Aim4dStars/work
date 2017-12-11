package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.AbstractJsonReaderTest;
import com.bt.nextgen.api.draftaccount.model.form.ClientApplicationFormFactory;
import com.bt.nextgen.api.draftaccount.model.form.IAccountSettingsForm;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.ICompanyForm;
import com.bt.nextgen.api.draftaccount.model.form.ILinkedAccountsForm;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import ns.btfin_com.party.v3_0.PartyIdentificationInformationsIndType;
import ns.btfin_com.product.common.investmentaccount.v2_0.OwnershipTypeType;
import ns.btfin_com.product.common.investmentaccount.v2_0.ProductIDIssuerType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.AddressDetailType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.AddressTypeDetailType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.AdvisersType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.ApplicationType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.CashAccountsType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.IndividualType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.InvestmentAccountType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.InvestorsType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.PaymentInstructionsType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.ProcessInvestorApplicationRequestMsgType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.RegisteredResidentialAddressDetailType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.StructuredAddressDetailType;
import ns.btfin_com.sharedservices.common.address.v3_0.NonStandardAddressType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProcessInvestorsRequestMsgTypeBuilderV3Test extends AbstractJsonReaderTest {

    @Mock
    private ExistingInvestorTypeBuilder existingInvestorTypeBuilder;

    @Mock
    private InvestorsTypeBuilder investorsTypeBuilder;

    @Mock
    private InvestmentProductTypeBuilder investmentProductTypeBuilder;

    @Mock
    private PaymentInstructionsBuilder paymentInstructionsBuilder;

    @Mock
    private AdvisersTypeBuilder advisersTypeBuilder;

    @Mock
    private CashAccountsBuilder cashAccountsBuilder;

    @Mock
    private BrokerUser brokerUser;

    @Mock
    private Broker dealer;

    @Mock
    private FeatureTogglesService featureTogglesService;

    @Mock
    private OnboardingApplicationKey key;

    @Mock
    private AddressV2CacheService addressV2CacheService;

    @InjectMocks
    private ProcessInvestorApplicationRequestMsgTypeBuilderV3 requestMsgTypeBuilder;

    private FeatureToggles featureToggles;

    @Before
    public void setUp(){
        when(paymentInstructionsBuilder.getPaymentInstructions(any(ILinkedAccountsForm.class))).thenReturn(new PaymentInstructionsType());
        when(advisersTypeBuilder.getAdvisersType(any(IClientApplicationForm.class), any(BrokerUser.class))).thenReturn(new AdvisersType());
        when(cashAccountsBuilder.getCashAccounts(any(ILinkedAccountsForm.class))).thenReturn(new CashAccountsType());
        when(investorsTypeBuilder.getInvestorsType(any(IClientApplicationForm.class), any(BrokerUser.class), any(Broker.class), any(
            ServiceErrors.class))).thenReturn(new InvestorsType());
        IndividualType individualType = new IndividualType();
        individualType.setResidentialAddress(getRegisteredResidentialAddressDetailType());
        individualType.setPartyIdentificationInformations(new PartyIdentificationInformationsIndType());

        when(brokerUser.getBankReferenceId()).thenReturn("SOME GCM ID");
        when(brokerUser.getFirstName()).thenReturn("AdviserFirstName");
        when(brokerUser.getLastName()).thenReturn("AdviserLastName");
        Email adviserEmail = mock(Email.class);
        when(adviserEmail.getEmail()).thenReturn("adviser@example.com");
        when(brokerUser.getEmails()).thenReturn(Arrays.asList(adviserEmail));
        Phone adviserPhone = mock(Phone.class);
        when(adviserPhone.getCountryCode()).thenReturn("+61");
        when(adviserPhone.getAreaCode()).thenReturn("458");
        when(adviserPhone.getNumber()).thenReturn("123123");
        when(brokerUser.getPhones()).thenReturn(Arrays.asList(adviserPhone));
        featureToggles = new FeatureToggles();
        when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(featureToggles);
    }

    private RegisteredResidentialAddressDetailType getRegisteredResidentialAddressDetailType() {
        RegisteredResidentialAddressDetailType residentialAddress = new RegisteredResidentialAddressDetailType();
        AddressDetailType addressDetail = new AddressDetailType();
        StructuredAddressDetailType structuredAddressDetail = new StructuredAddressDetailType();
        AddressTypeDetailType addressTypeDetail = new AddressTypeDetailType();
        addressTypeDetail.setNonStandardAddress(new NonStandardAddressType());
        structuredAddressDetail.setAddressTypeDetail(addressTypeDetail);
        addressDetail.setStructuredAddressDetail(structuredAddressDetail);
        residentialAddress.setAddressDetail(addressDetail);
        return residentialAddress;
    }

    @Test
    public void individualSmsfShouldBeOfTypeTrust() throws Exception {
        IClientApplicationForm clientApplicationForm = mock(IClientApplicationForm.class);
        when(clientApplicationForm.getAccountType()).thenReturn(IClientApplicationForm.AccountType.INDIVIDUAL_SMSF);
        when(clientApplicationForm.getAdviceType()).thenReturn(IClientApplicationForm.AdviceType.PERSONAL_ADVICE.value());
        when(clientApplicationForm.getApplicationOrigin()).thenReturn(IClientApplicationForm.ApplicationOriginType.BT_PANORAMA.value());
        IAccountSettingsForm acctSettings = Mockito.mock(IAccountSettingsForm.class);
        when(clientApplicationForm.getAccountSettings()).thenReturn(acctSettings);
        when(clientApplicationForm.getAccountSettings().getPowerOfAttorney()).thenReturn(Boolean.TRUE);
        setLinkedAccounts(clientApplicationForm);
        ProcessInvestorApplicationRequestMsgType result = requestMsgTypeBuilder.buildFromForm(clientApplicationForm,
                brokerUser, key, "1222", dealer, new ServiceErrorsImpl());
        assertThat(result.getApplication().getInvestmentAccount().getOwnershipType(), is(OwnershipTypeType.TRUST));
    }

    @Test
    public void corporateSmsfShouldBeOfTypeTrust() throws Exception {
        IClientApplicationForm clientApplicationForm = mock(IClientApplicationForm.class);
        when(clientApplicationForm.getAccountType()).thenReturn(IClientApplicationForm.AccountType.CORPORATE_SMSF);
        when(clientApplicationForm.getAdviceType()).thenReturn(IClientApplicationForm.AdviceType.PERSONAL_ADVICE.value());
        when(clientApplicationForm.getApplicationOrigin()).thenReturn(IClientApplicationForm.ApplicationOriginType.BT_PANORAMA.value());
        IAccountSettingsForm acctSettings = Mockito.mock(IAccountSettingsForm.class);
        when(clientApplicationForm.getAccountSettings()).thenReturn(acctSettings);
        when(clientApplicationForm.getAccountSettings().getPowerOfAttorney()).thenReturn(Boolean.TRUE);
        setLinkedAccounts(clientApplicationForm);
        ProcessInvestorApplicationRequestMsgType result = requestMsgTypeBuilder.buildFromForm(clientApplicationForm,
                brokerUser, key, "1222", dealer, new ServiceErrorsImpl());
        assertThat(result.getApplication().getInvestmentAccount().getOwnershipType(), is(OwnershipTypeType.TRUST));
    }

    private void setLinkedAccounts(IClientApplicationForm clientApplicationForm) {
        ILinkedAccountsForm iLinkedAccountsForm = mock(ILinkedAccountsForm.class);
        when(iLinkedAccountsForm.isEmpty()).thenReturn(true);
        when(clientApplicationForm.getLinkedAccounts()).thenReturn(mock(ILinkedAccountsForm.class));
    }

    @Test
    public void companyShouldBeOfTypeCorporation() throws Exception {
        IClientApplicationForm clientApplicationForm = mock(IClientApplicationForm.class);
        when(clientApplicationForm.getAccountType()).thenReturn(IClientApplicationForm.AccountType.COMPANY);
        when(clientApplicationForm.getAdviceType()).thenReturn(IClientApplicationForm.AdviceType.PERSONAL_ADVICE.value());
        when(clientApplicationForm.getApplicationOrigin()).thenReturn(IClientApplicationForm.ApplicationOriginType.BT_PANORAMA.value());
        ICompanyForm companyDetails = mock(ICompanyForm.class);
        IAccountSettingsForm accountSettings = mock(IAccountSettingsForm.class);
        when(clientApplicationForm.getCompanyDetails()).thenReturn(companyDetails);
        when(companyDetails.getPersonalInvestmentEntity()).thenReturn(Boolean.TRUE);
        when(clientApplicationForm.getCompanyDetails().getPersonalInvestmentEntity()).thenReturn(Boolean.TRUE);
        when(clientApplicationForm.getAccountSettings()).thenReturn(accountSettings);
        when(accountSettings.getPowerOfAttorney()).thenReturn(Boolean.TRUE);
        when(clientApplicationForm.getAccountSettings().getPowerOfAttorney()).thenReturn(Boolean.TRUE);
        setLinkedAccounts(clientApplicationForm);
        ProcessInvestorApplicationRequestMsgType result = requestMsgTypeBuilder.buildFromForm(clientApplicationForm,
                brokerUser, key, "1222", dealer, new ServiceErrorsImpl());
        assertThat(result.getApplication().getInvestmentAccount().getOwnershipType(), is(OwnershipTypeType.CORPORATION));
    }

    @Test
    public void individualTrustShouldBeOfTypeTrust() throws Exception {
        IClientApplicationForm clientApplicationForm = mock(IClientApplicationForm.class);
        when(clientApplicationForm.getAccountType()).thenReturn(IClientApplicationForm.AccountType.INDIVIDUAL_TRUST);
        when(clientApplicationForm.getAdviceType()).thenReturn(IClientApplicationForm.AdviceType.PERSONAL_ADVICE.value());
        setLinkedAccounts(clientApplicationForm);
        when(clientApplicationForm.getApplicationOrigin()).thenReturn(IClientApplicationForm.ApplicationOriginType.BT_PANORAMA.value());
        ProcessInvestorApplicationRequestMsgType result = requestMsgTypeBuilder.buildFromForm(clientApplicationForm,
                brokerUser, key, "1222", dealer, new ServiceErrorsImpl());
        assertThat(result.getApplication().getInvestmentAccount().getOwnershipType(), is(OwnershipTypeType.TRUST));
    }

    @Test
    public void corporateTrustShouldBeOfTypeTrust() throws Exception {
        IClientApplicationForm clientApplicationForm = mock(IClientApplicationForm.class);
        when(clientApplicationForm.getAccountType()).thenReturn(IClientApplicationForm.AccountType.CORPORATE_TRUST);
        when(clientApplicationForm.getAdviceType()).thenReturn(IClientApplicationForm.AdviceType.PERSONAL_ADVICE.value());
        when(clientApplicationForm.getApplicationOrigin()).thenReturn(IClientApplicationForm.ApplicationOriginType.BT_PANORAMA.value());
        setLinkedAccounts(clientApplicationForm);
        ProcessInvestorApplicationRequestMsgType result = requestMsgTypeBuilder.buildFromForm(clientApplicationForm,
                brokerUser, key, "1222", dealer, new ServiceErrorsImpl());
        assertThat(result.getApplication().getInvestmentAccount().getOwnershipType(), is(OwnershipTypeType.TRUST));
    }

    @Test
    public void individualShouldBeOfTypeSingleOwnerAccount() throws Exception {
        IClientApplicationForm clientApplicationForm = mock(IClientApplicationForm.class);
        when(clientApplicationForm.getAccountType()).thenReturn(IClientApplicationForm.AccountType.INDIVIDUAL);
        when(clientApplicationForm.getAdviceType()).thenReturn(IClientApplicationForm.AdviceType.PERSONAL_ADVICE.value());
        when(clientApplicationForm.getApplicationOrigin()).thenReturn(IClientApplicationForm.ApplicationOriginType.BT_PANORAMA.value());
        IAccountSettingsForm acctSettings = Mockito.mock(IAccountSettingsForm.class);
        when(clientApplicationForm.getAccountSettings()).thenReturn(acctSettings);
        when(clientApplicationForm.getAccountSettings().getPowerOfAttorney()).thenReturn(Boolean.TRUE);
        setLinkedAccounts(clientApplicationForm);
        ProcessInvestorApplicationRequestMsgType result = requestMsgTypeBuilder.buildFromForm(clientApplicationForm,
                brokerUser, key, "1222", dealer, new ServiceErrorsImpl());
        assertThat(result.getApplication().getInvestmentAccount().getOwnershipType(), is(OwnershipTypeType.SINGLE_OWNER_ACCOUNT));
    }

    @Test
    public void jointShouldBeOfTypeJointAccount() throws Exception {
        IClientApplicationForm clientApplicationForm = mock(IClientApplicationForm.class);
        when(clientApplicationForm.getAccountType()).thenReturn(IClientApplicationForm.AccountType.JOINT);
        when(clientApplicationForm.getAdviceType()).thenReturn(IClientApplicationForm.AdviceType.PERSONAL_ADVICE.value());
        when(clientApplicationForm.getApplicationOrigin()).thenReturn(IClientApplicationForm.ApplicationOriginType.BT_PANORAMA.value());
        IAccountSettingsForm acctSettings = Mockito.mock(IAccountSettingsForm.class);
        when(clientApplicationForm.getAccountSettings()).thenReturn(acctSettings);
        when(clientApplicationForm.getAccountSettings().getPowerOfAttorney()).thenReturn(Boolean.TRUE);
        setLinkedAccounts(clientApplicationForm);
        ProcessInvestorApplicationRequestMsgType result = requestMsgTypeBuilder.buildFromForm(clientApplicationForm,
                brokerUser, key, "1222", dealer, new ServiceErrorsImpl());
        assertThat(result.getApplication().getInvestmentAccount().getOwnershipType(), is(OwnershipTypeType.JOINT_ACCOUNT));
    }

    @Test
    public void shouldBuildProcessApplicationInvestorsRequestMsgTypeWithAllRequiredInfo() throws IOException {
        ProcessInvestorApplicationRequestMsgType processInvestorApplicationRequestMsgType =
                requestMsgTypeBuilder.buildFromForm(ClientApplicationFormFactory.getNewClientApplicationForm(readJsonFromFile("individual.json")),
                        brokerUser, key, "1222", dealer, new ServiceErrorsImpl());

        ApplicationType application = processInvestorApplicationRequestMsgType.getApplication();
        assertNotNull(application);
        InvestmentAccountType investmentAccount = application.getInvestmentAccount();

        assertThatInvestmentAccountDetailsAreCorrect(investmentAccount);

        assertNotNull(investmentAccount.getAdvisers());
        assertNotNull(investmentAccount.getCashAccounts());
        assertEquals(investmentAccount.getOwnershipType(), OwnershipTypeType.SINGLE_OWNER_ACCOUNT);
        assertNotNull(application.getPaymentInstructions());
    }

    private void assertThatInvestmentAccountDetailsAreCorrect(InvestmentAccountType investmentAccount) {
        assertNotNull(investmentAccount);
        assertThat(investmentAccount.getProductID(), is("1222"));
        assertEquals(ProductIDIssuerType.WESTPAC, investmentAccount.getProductIDIssuer());
        assertThat(investmentAccount.getOwnershipType(), equalTo(OwnershipTypeType.SINGLE_OWNER_ACCOUNT));
        assertThat(investmentAccount.getCorrelationSequenceNumber(), is("0"));
    }
}
