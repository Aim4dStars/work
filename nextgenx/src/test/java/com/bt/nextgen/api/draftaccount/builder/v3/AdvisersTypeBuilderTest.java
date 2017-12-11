package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.model.form.*;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerRole;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import ns.btfin_com.authorityprofile.v1_0.AuthorityTypeType;
import ns.btfin_com.authorityprofile.v1_0.RoleTypeType;
import ns.btfin_com.party.adviser.v3_1.AdviserNumberIssuerType;
import ns.btfin_com.party.adviser.v3_1.AdviserType;
import ns.btfin_com.party.intermediary.v1_1.AuthorityProfileType;
import ns.btfin_com.party.intermediary.v1_1.IntermediaryType;
import ns.btfin_com.party.intermediary.v1_1.OrganisationUnitType;
import ns.btfin_com.party.intermediary.v1_1.PositionIDIssuerType;
import ns.btfin_com.party.intermediary.v1_1.PositionType;
import ns.btfin_com.party.v3_0.IndividualPartyType;
import ns.btfin_com.party.v3_0.IndividualType;
import ns.btfin_com.product.common.investmentaccount.v2_0.AdvisersType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactDetailType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberTypeCode;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactsType;
import ns.btfin_com.sharedservices.common.contact.v1_1.EmailAddressesType;
import ns.btfin_com.sharedservices.common.fee.v1_2.FeeInfoType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdvisersTypeBuilderTest {

    public static final String PRIMARY_EMAIL_ADDR = "adviser@example.com";
    public static final String SECONDARY_EMAIL_ADDR = "wrong@example.com";
    public static final String BUSINESS_PHONE = "0298765432";
    public static final String MOBILE_PHONE = "0458123123";

    @Mock
    private FeesBuilder feesBuilder;

    @Mock
    private AuthorityTypeBuilder authorityTypeBuilder;

    @InjectMocks
    private AdvisersTypeBuilder advisersTypeBuilder;

    private static final String GCM_ID = "456";
    private FeeInfoType[] mockFeesType;
    private IAccountSettingsForm accountSettings;
    private String positionId;
    private IClientApplicationForm form;
    private BrokerUser brokerUser;

    @Before
    public void setUp(){


        mockFeesType = new FeeInfoType[]{ mock(FeeInfoType.class), mock(FeeInfoType.class) };
        Mockito.when(authorityTypeBuilder.getAuthorityType(Mockito.any(PaymentAuthorityEnum.class))).thenReturn(AuthorityTypeType.FULL_TRANSACTION);
        Mockito.when(feesBuilder.getFees(any(IFeesForm.class), any(IClientApplicationForm.AccountType.class) )).thenReturn(mockFeesType);

        accountSettings = mock(IAccountSettingsForm.class);
        when(accountSettings.getPaymentSettingForInvestor(0)).thenReturn(PaymentAuthorityEnum.LINKEDACCOUNTSONLY);
        when(accountSettings.getProfessionalsPayment()).thenReturn(PaymentAuthorityEnum.ALLPAYMENTS);

        form = mock(IClientApplicationForm.class);
        when(form.getAccountSettings()).thenReturn(accountSettings);

        positionId = "MY POSITION ID";

        brokerUser = mockBrokerUser();
    }

    private BrokerUser mockBrokerUser() {
        BrokerUser broker = mock(BrokerUser.class);
        when(broker.getBankReferenceId()).thenReturn(GCM_ID);
        when(broker.getFirstName()).thenReturn("AdviserFirstName");
        when(broker.getLastName()).thenReturn("AdviserLastName");

        Email rightEmail = mock(Email.class);
        when(rightEmail.getEmail()).thenReturn(PRIMARY_EMAIL_ADDR);
        when(rightEmail.getType()).thenReturn(AddressMedium.EMAIL_PRIMARY);

        Email wrongEmail = mock(Email.class);
        when(wrongEmail.getEmail()).thenReturn(SECONDARY_EMAIL_ADDR);
        when(wrongEmail.getType()).thenReturn(AddressMedium.EMAIL_ADDRESS_SECONDARY);
        when(broker.getEmails()).thenReturn(asList(wrongEmail, rightEmail));

        BrokerRole brokerRole = mock(BrokerRole.class);
        when(brokerRole.getRole()).thenReturn(JobRole.ADVISER);
        when(brokerRole.getKey()).thenReturn(BrokerKey.valueOf(positionId));
        when(broker.getRoles()).thenReturn(asList(brokerRole));

        Phone business = businessPhone();
        when(broker.getPhones()).thenReturn(asList(business));

        return broker;
    }

    private Phone businessPhone() {
        Phone phone = mock(Phone.class);
        when(phone.getCountryCode()).thenReturn("+61");
        when(phone.getAreaCode()).thenReturn(BUSINESS_PHONE.substring(0, 2));
        when(phone.getNumber()).thenReturn(BUSINESS_PHONE.substring(2));
        when(phone.getType()).thenReturn(AddressMedium.BUSINESS_TELEPHONE);
        return phone;
    }

    private Phone mobilePhone() {
        Phone phone = mock(Phone.class);
        when(phone.getAreaCode()).thenReturn(MOBILE_PHONE.substring(0, 4));
        when(phone.getNumber()).thenReturn(MOBILE_PHONE.substring(4));
        when(phone.getType()).thenReturn(AddressMedium.MOBILE_PHONE_SECONDARY);
        return phone;
    }

    @Test
    public void getAdviserType_ShouldReturnASingleAdviser() {
        AdvisersType advisersType = advisersTypeBuilder.getAdvisersType(form, brokerUser);
        assertThat(advisersType.getAdviser(), hasSize(1));
    }

    @Test
    public void getAdviserType_ShouldReturnAnAdviserNumberEqualToTheBrokerUsersBankReferenceId() throws Exception {
        AdviserType adviserType = advisersTypeBuilder.getAdvisersType(form, brokerUser).getAdviser().get(0);

        assertEquals(GCM_ID, adviserType.getAdviserNumber());
    }

    @Test
    public void getAdviserType_ShouldReturnAnAdviserNumberIssuerOfWestpac() {
        AdviserType adviserType = advisersTypeBuilder.getAdvisersType(form, brokerUser).getAdviser().get(0);

        assertEquals(AdviserNumberIssuerType.WESTPAC, adviserType.getAdviserNumberIssuer());
    }

    @Test
    public void getAdviserType_ShouldHaveIntermediaryDetails() {
        AdviserType adviserType = advisersTypeBuilder.getAdvisersType(form, brokerUser).getAdviser().get(0);

        IndividualPartyType intermediaryDetails = adviserType.getAdviserDetails().getIntermediaryDetails();
        assertNotNull(intermediaryDetails);
    }

    @Test
    public void getAdviserType_ShouldHaveIndividualWithAdvisersFirstAndLastName() {
        AdviserType adviserType = advisersTypeBuilder.getAdvisersType(form, brokerUser).getAdviser().get(0);

        IndividualPartyType intermediaryDetails = adviserType.getAdviserDetails().getIntermediaryDetails();
        IndividualType individual = intermediaryDetails.getPartyDetails();
        assertNotNull(individual);
        assertThat(individual.getGivenName(), is("AdviserFirstName"));
        assertThat(individual.getLastName(), is("AdviserLastName"));
    }

    @Test
    public void getAdviserType_ShouldOnlyHavePrimaryEmailAddress_InTheIntermediaryDetails() {
        AdviserType adviserType = advisersTypeBuilder.getAdvisersType(form, brokerUser).getAdviser().get(0);

        IndividualPartyType intermediaryDetails = adviserType.getAdviserDetails().getIntermediaryDetails();

        EmailAddressesType emailAddresses = intermediaryDetails.getEmailAddresses();
        assertThat(emailAddresses.getEmailAddress().size(), is(1));
        assertThat(emailAddresses.getEmailAddress().get(0).getEmailAddressDetail().getValue().getEmailAddress(), is(PRIMARY_EMAIL_ADDR));
    }

    @Test
    public void getAdviserType_ShouldHaveNoPhoneContact_IfTheAdviserHasNoBusinessNumber_InTheIntermediaryDetails() {
        Phone mobile = mobilePhone();
        when(brokerUser.getPhones()).thenReturn(asList(mobile));

        AdviserType adviserType = advisersTypeBuilder.getAdvisersType(form, brokerUser).getAdviser().get(0);

        IndividualPartyType intermediaryDetails = adviserType.getAdviserDetails().getIntermediaryDetails();

        assertNull(intermediaryDetails.getContacts());
    }

    @Test
    public void getAdviserType_ShouldHaveOnlyTheBusinessPhone_InTheIntermediaryDetails() {
        Phone mobile = mobilePhone();
        Phone business = businessPhone();
        when(brokerUser.getPhones()).thenReturn(asList(business, mobile));

        AdviserType adviserType = advisersTypeBuilder.getAdvisersType(form, brokerUser).getAdviser().get(0);

        IndividualPartyType intermediaryDetails = adviserType.getAdviserDetails().getIntermediaryDetails();

        ContactsType contacts = intermediaryDetails.getContacts();
        assertThat(contacts.getContact().size(), is(1));

        ContactDetailType firstContactNumber = contacts.getContact().get(0).getContactDetail();
        assertThat(firstContactNumber.getContactNumber().getNonStandardContactNumber(), is(BUSINESS_PHONE));
        assertThat(firstContactNumber.getContactNumberType(), is(ContactNumberTypeCode.PHONE));
    }

    @Test
    public void getAdviserType_ShouldHaveOnePosition_WithTheBrokersPositionId() {
        AdviserType adviserType = advisersTypeBuilder.getAdvisersType(form, brokerUser).getAdviser().get(0);

        IntermediaryType adviserDetails = adviserType.getAdviserDetails();

        List<OrganisationUnitType> organisationUnit = adviserDetails.getOrganisation().getOrganisationUnit();
        List<PositionType> positions = organisationUnit.get(0).getPosition();
        assertThat(positions, hasSize(1));
        assertThat(positions.get(0).getPositionIdentifier().getPositionIDIssuer(), is(PositionIDIssuerType.WESTPAC));
        assertThat(positions.get(0).getPositionIdentifier().getPositionID(), is(positionId));
    }

    @Test
    public void getAdviserType_ShouldHaveOnePosition_WithTheFeesType() {
        AdviserType adviserType = advisersTypeBuilder.getAdvisersType(form, brokerUser).getAdviser().get(0);

        IntermediaryType adviserDetails = adviserType.getAdviserDetails();

        List<OrganisationUnitType> organisationUnit = adviserDetails.getOrganisation().getOrganisationUnit();
        List<PositionType> positions = organisationUnit.get(0).getPosition();
        assertThat(positions.get(0).getFees().getFee(), is(asList(mockFeesType)));
    }

    @Test
    public void getAdviserType_ShouldHaveOnePosition_WithTheAuthorityProfileOfTheAdviser() {
        AdviserType adviserType = advisersTypeBuilder.getAdvisersType(form, brokerUser).getAdviser().get(0);

        IntermediaryType adviserDetails = adviserType.getAdviserDetails();

        List<OrganisationUnitType> organisationUnit = adviserDetails.getOrganisation().getOrganisationUnit();
        List<PositionType> positions = organisationUnit.get(0).getPosition();
        List<AuthorityProfileType> authorityProfile = positions.get(0).getAuthorityProfiles().getAuthorityProfile();
        assertThat(authorityProfile, hasSize(1));
        assertThat(authorityProfile.get(0).getAuthorityType(), is(AuthorityTypeType.FULL_TRANSACTION));
        assertThat(authorityProfile.get(0).getRoleType(), is(RoleTypeType.ADVISER));
    }
}
