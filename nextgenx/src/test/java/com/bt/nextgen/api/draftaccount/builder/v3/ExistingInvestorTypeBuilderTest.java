package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IDirectorDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IExtendedPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm;
import com.bt.nextgen.api.draftaccount.model.form.ITrustForm;
import com.bt.nextgen.api.draftaccount.model.form.ITrusteeDetailsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.btfin.panorama.core.security.integration.domain.InvestorDetail;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import ns.btfin_com.authorityprofile.v1_0.AuthorityTypeType;
import ns.btfin_com.authorityprofile.v1_0.RoleTypeType;
import ns.btfin_com.party.v3_0.CustomerIdentifier;
import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import ns.btfin_com.party.v3_0.InvestmentAccountPartyRoleTypeType;
import ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType;
import ns.btfin_com.product.common.investmentaccount.v2_0.InvestorAuthorityProfileType;
import ns.btfin_com.product.common.investmentaccount.v2_0.InvestorType;
import ns.btfin_com.sharedservices.common.address.v3_0.RegisteredResidentialAddressDetailType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactDetailType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberTypeCode;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberUsageTypeCode;
import ns.btfin_com.sharedservices.common.contact.v1_1.EmailAddressDetailType;
import ns.btfin_com.sharedservices.common.contact.v1_1.EmailUsageTypeCode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.selectFirst;
import static com.bt.nextgen.api.draftaccount.builder.v3.AuthorityTypeMatcher.hasAuthorityType;
import static junit.framework.TestCase.fail;
import static ns.btfin_com.party.v3_0.InvestmentAccountPartyRoleTypeType.ACCOUNT_SERVICER_ROLE;
import static ns.btfin_com.party.v3_0.InvestmentAccountPartyRoleTypeType.CONTACT_PERSON_ROLE;
import static ns.btfin_com.party.v3_0.InvestmentAccountPartyRoleTypeType.PRIMARY_OWNER_ROLE;
import static ns.btfin_com.party.v3_0.InvestmentAccountPartyRoleTypeType.SECONDARY_OWNER_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.COMPANY_BENEFICIAL_OWNER_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.COMPANY_DIRECTOR_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.COMPANY_SHAREHOLDER_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.SMSF_MEMBER_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.TRUST_BENEFICIAL_OWNER_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.TRUST_BENEFICIARY_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.TRUST_RESPONSIBLE_ENTITY_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.TRUST_TRUSTEE_ROLE;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExistingInvestorTypeBuilderTest {

    @Mock
    private IExtendedPersonDetailsForm form;

    @Mock
    private ClientIntegrationService clientIntegrationService;

    @Mock
    private AuthorityTypeBuilder authorityTypeBuilder;

    @Mock
    private InvestorDetail investorDetail;

    @Mock
    private ExistingCustomerIdentifiersBuilder existingCustomerIdentifiersBuilder;

    @Mock
    private AddressTypeBuilder addressTypeBuilder;

    @Mock
    private TaxFieldsBuilder taxFieldsBuilder;

    @InjectMocks
    private ExistingInvestorTypeBuilder builder;

    @Mock
    private FeatureTogglesService featureTogglesService;

    private FeatureToggles featureToggles;

    @Before
    public void setupClientIntegrationService() {
        when(form.getClientKey()).thenReturn(EncodedString.fromPlainText("MY CLIENT KEY").toString());
        when(clientIntegrationService.loadClientDetails(eq(ClientKey.valueOf("MY CLIENT KEY")), any(ServiceErrors.class))).thenReturn(investorDetail);
        when(authorityTypeBuilder.getInvestorAuthorityProfilesType(any(PaymentAuthorityEnum.class))).thenCallRealMethod();
        when(authorityTypeBuilder.getInvestorAuthorityProfileType(any(PaymentAuthorityEnum.class))).thenCallRealMethod();
        when(addressTypeBuilder.getAddressType(any(Address.class), any(RegisteredResidentialAddressDetailType.class))).thenCallRealMethod();
        when(existingCustomerIdentifiersBuilder.buildCustomerIdentifierWithCisKey(anyString())).thenCallRealMethod();
        when(existingCustomerIdentifiersBuilder.buildCustomerIdentifierWithGcmId(anyString())).thenCallRealMethod();
        when(existingCustomerIdentifiersBuilder.buildCustomerIdentifierWithCustomerNumber(anyString())).thenCallRealMethod();
        Address mockResidentialAddress = createAddress(true,"some","address","is","better", "vic", "than","no","address");
        List<Address> addresses = Arrays.asList(mockResidentialAddress);
        when(investorDetail.getAddresses()).thenReturn(addresses);
        Email ignoredEmail = createEmail("ignore", AddressMedium.EMAIL_PRIMARY);
        when(investorDetail.getEmails()).thenReturn(Arrays.asList(ignoredEmail));
        Phone ignoredPhone = createPhone("ignore", AddressMedium.MOBILE_PHONE_PRIMARY);
        when(investorDetail.getPhones()).thenReturn(Arrays.asList(ignoredPhone));
        mockInvestorIdentifiers(investorDetail, "gcmId", "cisKey", "customerNumber");
        mockBrandSiloIdAndRequest(false);
        featureToggles = new FeatureToggles();
        featureToggles.setFeatureToggle("retrieveCrsEnabled", false);
        when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(featureToggles);
    }

    private HttpServletRequest mockBrandSiloIdAndRequest(boolean shouldHaveValue){
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        if(shouldHaveValue){
            when(investorDetail.getBrandSiloId()).thenReturn("BTPL");
        } else {
            when(investorDetail.getBrandSiloId()).thenReturn(null);
        }

        return request;
    }
    private void mockInvestorIdentifiers(InvestorDetail investorDetail, String gcmId, String cisKey, String customerNumber) {
        when(investorDetail.getGcmId()).thenReturn(gcmId);
        when(investorDetail.getCISKey()).thenReturn(CISKey.valueOf(cisKey));
        when(investorDetail.getWestpacCustomerNumber()).thenReturn(customerNumber);
    }

    @Test(expected = IllegalStateException.class)
    public void getInvestorType_failsIfCisKeyIsMissing() throws Exception {
        mockInvestorIdentifiers(investorDetail, "gcmId", null, "customerNumber");
        builder.getInvestorType(form, IClientApplicationForm.AccountType.SUPER_ACCUMULATION);
    }

    @Test(expected = IllegalStateException.class)
    public void getInvestorType_failsIfCustomerNumberIsMissing() throws Exception {
        mockInvestorIdentifiers(investorDetail, "gcmId", "cisKey", null);
        builder.getInvestorType(form, IClientApplicationForm.AccountType.INDIVIDUAL);
    }

    @Test
    public void getInvestorType_result_shouldHaveThreeCustomerIdentifiers() throws Exception {
        String customerNumber = "MY IDENTIFIER";
        String cisKey = "MY CIS KEY";
        String gcmId = "MY GCM ID";
        mockInvestorIdentifiers(investorDetail, gcmId, cisKey, customerNumber);

        InvestorType investorType = builder.getInvestorType(form, IClientApplicationForm.AccountType.INDIVIDUAL);

        CustomerIdentifier gcmIdentifier = getExistingCustomerIdentifier(investorType, gcmId);
        assertThat(gcmIdentifier.getCustomerNumberIdentifier().getCustomerNumber(), is(gcmId));
        assertThat(gcmIdentifier.getCustomerNumberIdentifier().getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.BT_PANORAMA));

        CustomerIdentifier cisKeyIdentifier = getExistingCustomerIdentifier(investorType, cisKey);
        assertThat(cisKeyIdentifier.getCustomerNumberIdentifier().getCustomerNumber(), is(cisKey));
        assertThat(cisKeyIdentifier.getCustomerNumberIdentifier().getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.WESTPAC_LEGACY));

        CustomerIdentifier customerNumberIdentifier = getExistingCustomerIdentifier(investorType, customerNumber);
        assertThat(customerNumberIdentifier.getCustomerNumberIdentifier().getCustomerNumber(), is(customerNumber));
        assertThat(customerNumberIdentifier.getCustomerNumberIdentifier().getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.WESTPAC));
    }

    @Test
    public void getInvestorType_result_shouldHaveResidentialAddress() throws Exception {
        Address residentialAddress = createAddress(true, "234234", "200", "Barangaroo", "Sydney", "NSW", "Australia", "2145", "AUS");
        Address mailingAddress = createAddress(false, "12345", "100", "Sussex", "Melbourne", "VIC", "OZLand", "3000", "OZ");

        List<Address> addresses = Arrays.asList(residentialAddress, mailingAddress);
        when(investorDetail.getAddresses()).thenReturn(addresses);
        InvestorType investorType = builder.getInvestorType(form, IClientApplicationForm.AccountType.INDIVIDUAL);
        RegisteredResidentialAddressDetailType residentialAddressDetailType = investorType.getInvestorDetails().getPartyDetails().getIndividual().getResidentialAddress();
        assertThat(residentialAddressDetailType.getAddressDetail().getStructuredAddressDetail().getAddressTypeDetail().getStandardAddress().getStreetNumber(),is("200"));
        assertThat(residentialAddressDetailType.getAddressDetail().getStructuredAddressDetail().getAddressTypeDetail().getStandardAddress().getStreetName(),is("Barangaroo"));
        assertThat(residentialAddressDetailType.getAddressDetail().getStructuredAddressDetail().getCity(),is("Sydney"));
        assertThat(residentialAddressDetailType.getAddressDetail().getStructuredAddressDetail().getCountryCode(),is("AUS"));
        assertThat(residentialAddressDetailType.getAddressDetail().getStructuredAddressDetail().getState(),is("NSW"));
    }


    @Test
    public void investorTypeShouldHaveEmailPrimaryAddress() throws Exception {
        Email secondaryEmailAddress = createEmail("wrong@email.com", AddressMedium.EMAIL_ADDRESS_SECONDARY);
        Email primaryEmailAddress  = createEmail("primary@email.com", AddressMedium.EMAIL_PRIMARY);
        List<Email> emails = Arrays.asList(secondaryEmailAddress, primaryEmailAddress);
        when(investorDetail.getEmails()).thenReturn(emails);
        InvestorType investorType = builder.getInvestorType(form, IClientApplicationForm.AccountType.INDIVIDUAL);
        EmailAddressDetailType emailAddressDetail = investorType.getInvestorDetails().getEmailAddresses().getEmailAddress().get(0).getEmailAddressDetail().getValue();
        assertThat(emailAddressDetail.getEmailAddress(), is("primary@email.com"));
        assertThat(emailAddressDetail.getEmailUsage().get(0), is(EmailUsageTypeCode.ALL_HOURS));
    }

    private Email createEmail(String emailAddress, AddressMedium type) {
        Email email = mock(Email.class);
        when(email.getEmail()).thenReturn(emailAddress);
        when(email.getType()).thenReturn(type);
        return email;
    }

    private Address createAddress(boolean isDomicile, String modificationSeqNumber, String streetNumber, String streetName, String city, String state, String country, String postCode, String countryCode) {
        Address address = mock(Address.class);
        when(address.isDomicile()).thenReturn(isDomicile);
        when(address.getModificationSeq()).thenReturn(modificationSeqNumber);
        when(address.getStreetNumber()).thenReturn(streetNumber);
        when(address.getStreetName()).thenReturn(streetName);
        when(address.getCity()).thenReturn(city);
        when(address.getState()).thenReturn(state);
        when(address.getCountry()).thenReturn(country);
        when(address.getPostCode()).thenReturn(postCode);
        when(address.getCountryCode()).thenReturn(countryCode);
        when(address.getCountryAbbr()).thenReturn(countryCode);
        when(address.getStateAbbr()).thenReturn(state);
        return address;
    }

    @Test
    public void investorTypeShouldHaveMobile() throws Exception {
        Phone secondaryMobileNumber = createPhone("0498765432", AddressMedium.MOBILE_PHONE_SECONDARY);
        Phone primaryMobileNumber  = createPhone("0412345678", AddressMedium.MOBILE_PHONE_PRIMARY);
        List<Phone> phoneNumbers = Arrays.asList(secondaryMobileNumber, primaryMobileNumber);
        when(investorDetail.getPhones()).thenReturn(phoneNumbers);
        InvestorType investorType = builder.getInvestorType(form, IClientApplicationForm.AccountType.INDIVIDUAL);
        ContactDetailType contactDetail = investorType.getInvestorDetails().getContacts().getContact().get(0).getContactDetail();
        assertThat(contactDetail.getContactNumber().getStandardContactNumber().getAreaCode(), is("4"));
        assertThat(contactDetail.getContactNumber().getStandardContactNumber().getSubscriberNumber(), is("12345678"));
        assertThat(contactDetail.getContactNumberType(), is(ContactNumberTypeCode.MOBILE));
        assertThat(contactDetail.getContactNumberUsage().get(0), is(ContactNumberUsageTypeCode.ALL_HOURS));
    }

    @Test
    public void shouldThrowExplicitExceptionIfInvestorDoesNotHavePrimaryEmail() throws Exception {
        Email secondaryEmailAddress = createEmail("wrong@email.com", AddressMedium.EMAIL_ADDRESS_SECONDARY);
        List<Email> emails = Arrays.asList(secondaryEmailAddress);
        when(investorDetail.getEmails()).thenReturn(emails);
        try {
            builder.getInvestorType(form, IClientApplicationForm.AccountType.INDIVIDUAL);
            fail("Should have thrown exception");
        } catch (IllegalStateException ex) {
            assertThat(ex.getMessage(), is("Client(gcmId=gcmId) must have a primary email address"));
        }
    }

    @Test
    public void shouldThrowExplicitExceptionIfInvestorDoesNotHavePrimaryMobile() throws Exception {
        Phone secondaryPhone = createPhone("WRONG", AddressMedium.MOBILE_PHONE_SECONDARY);
        List<Phone> phones = Arrays.asList(secondaryPhone);
        when(investorDetail.getPhones()).thenReturn(phones);
        try {
            builder.getInvestorType(form, IClientApplicationForm.AccountType.INDIVIDUAL);
            fail("Should have thrown exception");
        } catch (IllegalStateException ex) {
            assertThat(ex.getMessage(), is("Client(gcmId=gcmId) must have a primary mobile phone number"));
        }
    }

    @Test
    public void shouldThrowExplicitExceptionIfInvestorDoesNotHaveResidentialAddress() throws Exception {
        Address mailingAddress = createAddress(false,"some","thing","is","better", "act", "than","nothing","tada");
        List<Address> addresses = Arrays.asList(mailingAddress);
        when(investorDetail.getAddresses()).thenReturn(addresses);
        try {
            builder.getInvestorType(form, IClientApplicationForm.AccountType.INDIVIDUAL);
            fail("Should have thrown exception");
        } catch (IllegalStateException ex) {
            assertThat(ex.getMessage(), is("Client(gcmId=gcmId) must have a residential address"));
        }
    }

    private Phone createPhone(String phoneNumber, AddressMedium type) {
        Phone phone = mock(Phone.class);
        when(phone.getNumber()).thenReturn(phoneNumber);
        when(phone.getType()).thenReturn(type);
        return phone;
    }

    @Test
    public void shouldNotSetAgainRequestAttrIfRequestHasBrandSilo() throws Exception {
        assertNotNull(investorDetail);
        HttpServletRequest request = mockBrandSiloIdAndRequest(true);
        request.setAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO, "BTPL");
        assertThat((String)request.getAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO), is("BTPL"));
        builder.getInvestorType(form, IClientApplicationForm.AccountType.INDIVIDUAL);
        assertThat((String)request.getAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO), is("BTPL"));
    }

    @Test
    public void shouldSetRequestAttrIfHaveBrandSilo() throws Exception {
        assertNotNull(investorDetail);
        HttpServletRequest request = mockBrandSiloIdAndRequest(true);
        assertNull(request.getAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO));
        builder.getInvestorType(form, IClientApplicationForm.AccountType.INDIVIDUAL);
        assertThat((String)request.getAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO), is("BTPL"));
    }

    @Test
    public void shouldNotHaveInRequestAttrIfDoNotHaveBrandSilo() throws Exception {
        HttpServletRequest request = mockBrandSiloIdAndRequest(false);
        assertNull(request.getAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO));//should be null initially
        builder.getInvestorType(form, IClientApplicationForm.AccountType.INDIVIDUAL);
        assertNull(request.getAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO));//if not returned should be null again
    }

    @Test
    public void investorTypeShouldHaveFirstname() throws Exception {
        when(investorDetail.getFirstName()).thenReturn("FIRSTNAME");
        InvestorType investorType = builder.getInvestorType(form, IClientApplicationForm.AccountType.INDIVIDUAL);
        assertThat(investorType.getInvestorDetails().getPartyDetails().getIndividual().getGivenName(), is("FIRSTNAME"));
    }

    @Test
    public void investorTypeShouldHaveLastname() throws Exception {
        when(investorDetail.getLastName()).thenReturn("LASTNAME");
        InvestorType investorType = builder.getInvestorType(form, IClientApplicationForm.AccountType.INDIVIDUAL);
        assertThat(investorType.getInvestorDetails().getPartyDetails().getIndividual().getLastName(), is("LASTNAME"));
    }

    @Test
    public void investorTypeShouldHavePaymentAuthority() throws Exception {
        when(form.getPaymentSetting()).thenReturn(PaymentAuthorityEnum.ALLPAYMENTS);
        when(authorityTypeBuilder.getAuthorityType(any(PaymentAuthorityEnum.class))).thenCallRealMethod();
        InvestorType investorType = builder.getInvestorType(form, IClientApplicationForm.AccountType.INDIVIDUAL);
        List<InvestorAuthorityProfileType> authorityProfile = investorType.getAuthorityProfiles().getAuthorityProfile();
        assertThat(authorityProfile.get(0).getAuthorityType(), is(AuthorityTypeType.FULL_TRANSACTION));
        assertThat(authorityProfile.get(0).getRoleType(), is(RoleTypeType.CLIENT));
        assertThat(authorityProfile.get(1).getAuthorityType(), is(AuthorityTypeType.APPLICATION_MAINTENANCE));
        assertThat(authorityProfile.get(1).getRoleType(), is(RoleTypeType.CLIENT));
    }

   @Test
    public void investorTypeShouldHaveApprovalAuthority() throws Exception {
        when(authorityTypeBuilder.getInvestorAuthorityProfileTypeForApplicationApproval()).thenCallRealMethod();
        InvestorType investorType = builder.getInvestorType(form, IClientApplicationForm.AccountType.INDIVIDUAL);
        List<InvestorAuthorityProfileType> authorityProfile = investorType.getAuthorityProfiles().getAuthorityProfile();
        assertThat(authorityProfile.get(1).getAuthorityType(), is(AuthorityTypeType.APPLICATION_MAINTENANCE));
        assertThat(authorityProfile.get(1).getRoleType(), is(RoleTypeType.CLIENT));
        assertThat(authorityProfile.get(2).getAuthorityType(), is(AuthorityTypeType.APPLICATION_APPROVAL));
        assertThat(authorityProfile.get(2).getRoleType(), is(RoleTypeType.CLIENT));
    }

    @Test
    public void primaryContactInvestorTypeShouldBePrimaryOwner() throws Exception {
        when(form.isPrimaryContact()).thenReturn(true);
        InvestorType investorType = builder.getInvestorType(form, IClientApplicationForm.AccountType.INDIVIDUAL);
        assertThat(investorType.getInvestmentAccountPartyRole(), hasItem(PRIMARY_OWNER_ROLE));
        assertThat(investorType.getInvestmentAccountPartyRole(), hasItem(CONTACT_PERSON_ROLE));
    }

    @Test
    public void secondaryContactInvestorTypeShouldBeSecondaryOwner() throws Exception {
        when(form.isPrimaryContact()).thenReturn(false);
        InvestorType investorType = builder.getInvestorType(form, IClientApplicationForm.AccountType.JOINT);
        assertThat(investorType.getInvestmentAccountPartyRole(), hasItem(SECONDARY_OWNER_ROLE));
    }

    @Test
    public void nonPrimaryContactInvestorTypeShouldNotBePrimaryOwner() throws Exception {
        when(form.isPrimaryContact()).thenReturn(false);
        InvestorType investorType = builder.getInvestorType(form, IClientApplicationForm.AccountType.JOINT);
        assertThat(investorType.getInvestmentAccountPartyRole(), not(hasItem(PRIMARY_OWNER_ROLE)));
    }

    @Test
    public void primaryContactInvestorTypeShouldHaveContactPersonRole() throws Exception {
        when(form.isPrimaryContact()).thenReturn(true);
        InvestorType investorType = builder.getInvestorType(form, IClientApplicationForm.AccountType.JOINT);
        assertThat(investorType.getInvestmentAccountPartyRole(), hasItem(CONTACT_PERSON_ROLE));
    }

    @Test
    public void investorTypeShouldBeAccountOwner() throws Exception {
        InvestorType investorType = builder.getInvestorType(form, IClientApplicationForm.AccountType.INDIVIDUAL_TRUST);
        assertThat(investorType.getInvestmentAccountPartyRole(), hasItem(InvestmentAccountPartyRoleTypeType.ACCOUNT_SERVICER_ROLE));
    }

    @Test
    public void getDirectoryShareholderShouldHaveBenOwnerRole() throws Exception {
        IDirectorDetailsForm directorDetailsForm = mock(IDirectorDetailsForm.class);
        when(directorDetailsForm.isShareholder()).thenReturn(true);
        when(directorDetailsForm.getRole()).thenReturn(IOrganisationForm.OrganisationRole.DIRECTOR);
        when(directorDetailsForm.getClientKey()).thenReturn(EncodedString.fromPlainText("MY CLIENT KEY").toString());
        InvestorType investorType = builder.getDirectorType(directorDetailsForm,  IClientApplicationForm.AccountType.COMPANY);
        assertThat(investorType.getInvestmentAccountPartyRole(), hasItem(InvestmentAccountPartyRoleTypeType.ACCOUNT_SERVICER_ROLE));
        assertThat(investorType.getPartyRoleInRelatedOrganisation(), hasItem(PartyRoleInRelatedOrganisationType.COMPANY_BENEFICIAL_OWNER_ROLE));
    }

    @Test
    public void getDirectorShouldHaveCompanySecretaryRole() throws Exception {
        IDirectorDetailsForm directorDetailsForm = mock(IDirectorDetailsForm.class);
        when(directorDetailsForm.isCompanySecretary()).thenReturn(true);
        when(directorDetailsForm.getRole()).thenReturn(IOrganisationForm.OrganisationRole.DIRECTOR);
        when(directorDetailsForm.getClientKey()).thenReturn(EncodedString.fromPlainText("MY CLIENT KEY").toString());
        InvestorType investorType = builder.getDirectorType(directorDetailsForm, IClientApplicationForm.AccountType.COMPANY);
        assertThat(investorType.getInvestmentAccountPartyRole(), hasItem(InvestmentAccountPartyRoleTypeType.ACCOUNT_SERVICER_ROLE));
        assertThat(investorType.getPartyRoleInRelatedOrganisation(), hasItem(PartyRoleInRelatedOrganisationType.COMPANY_SECRETARY_ROLE));
    }

    @Test
    public void getDirectoryShareholderShouldHaveContactPersonRoleIfPrimaryContact() throws Exception {
        IDirectorDetailsForm directorDetailsForm = mock(IDirectorDetailsForm.class);
        when(directorDetailsForm.isPrimaryContact()).thenReturn(true);
        when(directorDetailsForm.getRole()).thenReturn(IOrganisationForm.OrganisationRole.DIRECTOR);
        when(directorDetailsForm.getClientKey()).thenReturn(EncodedString.fromPlainText("MY CLIENT KEY").toString());
        InvestorType investorType = builder.getDirectorType(directorDetailsForm,  IClientApplicationForm.AccountType.COMPANY);
        assertThat(investorType.getInvestmentAccountPartyRole(), hasItem(InvestmentAccountPartyRoleTypeType.CONTACT_PERSON_ROLE));
    }

    @Test
    public void getDirectorType_ShouldHaveDirectorRole(){
        IDirectorDetailsForm directorDetailsForm = getPersonDetailsForm(IDirectorDetailsForm.class);
        when(directorDetailsForm.getRole()).thenReturn(IOrganisationForm.OrganisationRole.DIRECTOR);

        InvestorType director = builder.getDirectorType(directorDetailsForm, IClientApplicationForm.AccountType.COMPANY);
        assertThat(director.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_DIRECTOR_ROLE));
    }

    @Test
    public void getDirectorType_ShouldHaveDirectorAndMemberRoleIfDirectorIsMember(){
        IDirectorDetailsForm directorDetailsForm = getPersonDetailsForm(IDirectorDetailsForm.class);
        when(directorDetailsForm.isMember()).thenReturn(true);
        when(directorDetailsForm.getRole()).thenReturn(IOrganisationForm.OrganisationRole.DIRECTOR);

        InvestorType director = builder.getDirectorType(directorDetailsForm, IClientApplicationForm.AccountType.COMPANY);
        assertThat(director.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_DIRECTOR_ROLE, SMSF_MEMBER_ROLE));

    }

    @Test
    public void getDirectorType_ShouldHaveBeneficiaryAndBeneficiaryRoleIfDirectorIsBeneficiary(){
        IDirectorDetailsForm directorDetailsForm = getPersonDetailsForm(IDirectorDetailsForm.class);
        when(directorDetailsForm.isBeneficiary()).thenReturn(true);
        when(directorDetailsForm.getRole()).thenReturn(IOrganisationForm.OrganisationRole.DIRECTOR);

        InvestorType director = builder.getDirectorType(directorDetailsForm, IClientApplicationForm.AccountType.COMPANY);

        assertThat(director.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_DIRECTOR_ROLE, TRUST_BENEFICIARY_ROLE));
    }

    @Test
    public void getDirectorType_ShouldHaveDirectorAndShareHolderRoleIfDirectorIsShareHolder(){
        IDirectorDetailsForm directorDetailsForm = getPersonDetailsForm(IDirectorDetailsForm.class);
        when(directorDetailsForm.isShareholder()).thenReturn(true);
        when(directorDetailsForm.getRole()).thenReturn(IOrganisationForm.OrganisationRole.DIRECTOR);

        InvestorType director = builder.getDirectorType(directorDetailsForm, IClientApplicationForm.AccountType.COMPANY);
        assertThat(director.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_DIRECTOR_ROLE, COMPANY_BENEFICIAL_OWNER_ROLE));
    }

    @Test
    public void getTrusteeType_ShouldHaveCompanyBeneficialOwnerRole(){
        ITrusteeDetailsForm trusteeDetailsForm = getPersonDetailsForm(ITrusteeDetailsForm.class);
        when(trusteeDetailsForm.isBeneficialOwner()).thenReturn(true);
        InvestorType director = builder.getTrusteeType(trusteeDetailsForm, IClientApplicationForm.AccountType.INDIVIDUAL_TRUST, ITrustForm.TrustType.FAMILY);
        assertThat(director.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(TRUST_TRUSTEE_ROLE,COMPANY_BENEFICIAL_OWNER_ROLE,TRUST_BENEFICIAL_OWNER_ROLE));
    }

    @Test
    public void getTrusteeType_ShouldHaveCompanyBeneficialOwnerRole_ForShareHolder(){
        ITrusteeDetailsForm trusteeDetailsForm = getPersonDetailsForm(ITrusteeDetailsForm.class);
        when(trusteeDetailsForm.isShareholder()).thenReturn(true);
        InvestorType director = builder.getTrusteeType(trusteeDetailsForm, IClientApplicationForm.AccountType.INDIVIDUAL_TRUST, ITrustForm.TrustType.FAMILY);
        assertThat(director.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(TRUST_TRUSTEE_ROLE,COMPANY_BENEFICIAL_OWNER_ROLE,TRUST_BENEFICIAL_OWNER_ROLE));
    }

    @Test
    public void getDirectorType_ShouldHaveTrustBeneficialOwnerRole(){
        IDirectorDetailsForm directorDetailsForm = getPersonDetailsForm(IDirectorDetailsForm.class);
        when(directorDetailsForm.isControllerOfTrust()).thenReturn(true);
        when(directorDetailsForm.getRole()).thenReturn(IOrganisationForm.OrganisationRole.DIRECTOR);
        InvestorType director = builder.getDirectorType(directorDetailsForm, IClientApplicationForm.AccountType.CORPORATE_TRUST);
        assertThat(director.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_DIRECTOR_ROLE,TRUST_BENEFICIAL_OWNER_ROLE));
    }


    @Test
    public void getDirectorType_ShouldHaveCompanyBeneficialOwnerRole_forShareHolder(){
        IDirectorDetailsForm directorDetailsForm = getPersonDetailsForm(IDirectorDetailsForm.class);
        when(directorDetailsForm.isShareholder()).thenReturn(true);
        when(directorDetailsForm.getRole()).thenReturn(IOrganisationForm.OrganisationRole.DIRECTOR);
        InvestorType director = builder.getDirectorType(directorDetailsForm, IClientApplicationForm.AccountType.NEW_CORPORATE_SMSF);
        assertThat(director.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_DIRECTOR_ROLE,COMPANY_SHAREHOLDER_ROLE));
    }

    @Test
    public void getDirectorType_ShouldHaveDirectorShareHolderAndMemberRoleIfDirectorIsShareHolderAndMember(){
        IDirectorDetailsForm directorDetailsForm = getPersonDetailsForm(IDirectorDetailsForm.class);
        when(directorDetailsForm.isShareholder()).thenReturn(true);
        when(directorDetailsForm.isMember()).thenReturn(true);
        when(directorDetailsForm.getRole()).thenReturn(IOrganisationForm.OrganisationRole.DIRECTOR);

        InvestorType director = builder.getDirectorType(directorDetailsForm, IClientApplicationForm.AccountType.COMPANY);
        assertThat(director.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(COMPANY_DIRECTOR_ROLE, SMSF_MEMBER_ROLE, COMPANY_BENEFICIAL_OWNER_ROLE));
    }


    @Test
    public void getTrusteeType_ShouldHaveMemberRoleIfTrusteeIsMember(){
        ITrusteeDetailsForm trusteeDetailsForm = getPersonDetailsForm(ITrusteeDetailsForm.class);
        when(trusteeDetailsForm.isMember()).thenReturn(true);

        InvestorType director = builder.getTrusteeType(trusteeDetailsForm, IClientApplicationForm.AccountType.INDIVIDUAL_TRUST, ITrustForm.TrustType.FAMILY);
        assertThat(director.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(TRUST_TRUSTEE_ROLE, SMSF_MEMBER_ROLE, TRUST_BENEFICIAL_OWNER_ROLE));
    }

    @Test
    public void getTrusteeType_ShouldHaveBeneficiaryRoleIfTrusteeIsBeneficiary(){
        ITrusteeDetailsForm trusteeDetailsForm = getPersonDetailsForm(ITrusteeDetailsForm.class);
        when(trusteeDetailsForm.isBeneficiary()).thenReturn(true);

        InvestorType director = builder.getTrusteeType(trusteeDetailsForm, IClientApplicationForm.AccountType.INDIVIDUAL_TRUST, ITrustForm.TrustType.FAMILY);
        assertThat(director.getPartyRoleInRelatedOrganisation(), containsInAnyOrder(TRUST_TRUSTEE_ROLE, TRUST_BENEFICIARY_ROLE, TRUST_BENEFICIAL_OWNER_ROLE));
    }

    @Test
    public void getTrusteeType_ShouldHavePrimaryOwnerRoleIfTrusteeIsPrimaryContact(){
        ITrusteeDetailsForm trusteeDetailsForm = getPersonDetailsForm(ITrusteeDetailsForm.class);
        when(trusteeDetailsForm.isPrimaryContact()).thenReturn(true);

        InvestorType director = builder.getTrusteeType(trusteeDetailsForm, IClientApplicationForm.AccountType.INDIVIDUAL_TRUST, ITrustForm.TrustType.FAMILY);
        assertThat(director.getPartyRoleInRelatedOrganisation(), hasItem(TRUST_TRUSTEE_ROLE));
        assertThat(director.getInvestmentAccountPartyRole(), hasItem(ACCOUNT_SERVICER_ROLE));
        assertThat(director.getInvestmentAccountPartyRole(), hasItem(CONTACT_PERSON_ROLE));
    }

    @Test
    public void getTrusteeType_ShouldHaveResponsibleEntityRoleIfTrustIsMIS(){
        ITrusteeDetailsForm trusteeDetailsForm = getPersonDetailsForm(ITrusteeDetailsForm.class);
        when(trusteeDetailsForm.isPrimaryContact()).thenReturn(true);

        InvestorType director = builder.getTrusteeType(trusteeDetailsForm, IClientApplicationForm.AccountType.CORPORATE_TRUST, ITrustForm.TrustType.REGISTERED_MIS);
        assertThat(director.getPartyRoleInRelatedOrganisation(), hasItem(TRUST_RESPONSIBLE_ENTITY_ROLE));
        assertThat(director.getPartyRoleInRelatedOrganisation(), hasItem(TRUST_BENEFICIAL_OWNER_ROLE));
    }

    @Test
    public void getTrusteeType_ShouldHaveAuthorityRoleAsReturnedByAuthorityTypeBuilder(){
        ITrusteeDetailsForm trusteeDetailsForm = getPersonDetailsForm(ITrusteeDetailsForm.class);
        PaymentAuthorityEnum paymentSetting = PaymentAuthorityEnum.NOPAYMENTS;
        when(trusteeDetailsForm.getPaymentSetting()).thenReturn(paymentSetting);
        when(authorityTypeBuilder.getAuthorityType(paymentSetting)).thenReturn(AuthorityTypeType.FULL_TRANSACTION);

        InvestorType director = builder.getTrusteeType(trusteeDetailsForm, IClientApplicationForm.AccountType.CORPORATE_TRUST, ITrustForm.TrustType.FAMILY);
        assertThat(director.getAuthorityProfiles().getAuthorityProfile(), hasAuthorityType(AuthorityTypeType.FULL_TRANSACTION));
    }

    @Test
    public void getTrusteeType_ShouldHaveAuthorityRoleAsApprover(){
        ITrusteeDetailsForm trusteeDetailsForm = getPersonDetailsForm(ITrusteeDetailsForm.class);
        when(trusteeDetailsForm.isApprover()).thenReturn(true);

        InvestorType director = builder.getTrusteeType(trusteeDetailsForm, IClientApplicationForm.AccountType.CORPORATE_TRUST, ITrustForm.TrustType.FAMILY);
        assertThat(director.getAuthorityProfiles().getAuthorityProfile(), hasAuthorityType(AuthorityTypeType.APPLICATION_APPROVAL));
    }


    private <T extends IExtendedPersonDetailsForm> T getPersonDetailsForm(Class<T> formType) {
        T trusteeDetailsForm = mock(formType);
        when(trusteeDetailsForm.getClientKey()).thenReturn(EncodedString.fromPlainText("MY CLIENT KEY").toString());
        return trusteeDetailsForm;
    }

    private CustomerIdentifier getExistingCustomerIdentifier(InvestorType investorType, final String key) {
        return selectFirst(investorType.getInvestorDetails().getCustomerIdentifiers().getCustomerIdentifier(), new LambdaMatcher<CustomerIdentifier>() {
            @Override
            protected boolean matchesSafely(CustomerIdentifier item) {
                return item.getCustomerNumberIdentifier().getCustomerNumber().equals(key);
            }
        });
    }
}
