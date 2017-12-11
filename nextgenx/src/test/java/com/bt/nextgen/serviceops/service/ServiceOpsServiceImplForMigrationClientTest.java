package com.bt.nextgen.serviceops.service;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.security.UserRole;
import com.bt.nextgen.core.service.CredentialService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.domain.PhoneImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.group.customer.BankingCustomerIdentifier;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.WrapAccountDetailResponse;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.AddressType;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.userinformation.BankReferenceIdentifier;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.serviceops.model.ServiceOpsModel;
import com.bt.nextgen.serviceops.model.UserAccountStatusModel;
import com.btfin.panorama.core.security.UserAccountStatus;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.panorama.core.security.integration.userprofile.ProfileIntegrationService;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.joda.time.DateTime;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.anyBoolean;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServiceOpsServiceImplForMigrationClientTest {
    public static final String CIS_KEY = "123456";

    @InjectMocks
    private ServiceOpsServiceImpl serviceOpsServiceImpl;

    @Mock
    private UserAccountStatusService userAccountStatusService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private CredentialService credentialService;

    @Mock
    private CmsService cmsService;

    @Mock
    private ClientIntegrationService clientIntegrationService;

    @Mock
    private ProfileIntegrationService profileIntegrationService;

    @Mock
    private UserInformationIntegrationService userInformationIntegrationService;

    @Mock
    private UserAccountStatusModel userAccountStatusModel;

    @Mock
    private UserInformationImpl userInformation;

    @Mock
    private IndividualDetailImpl clientDetail;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    ServiceOpsModel serviceOpsModel;

    @Before
    public void setUp() throws Exception {
        mockClientDetails();
        when(cmsService.getContent("uim0140"))
                .thenReturn("The user's mobile number and email are required before the user record can be created.");
        when(userAccountStatusModel.getUserAccountStatus()).thenReturn(UserAccountStatus.ACTV);
        when(userAccountStatusService.lookupStatus(anyString(), anyString(), anyBoolean())).thenReturn(userAccountStatusModel);
        JobProfile userJobProfile = mock(JobProfile.class);
        when(userJobProfile.getJobRole()).thenReturn(JobRole.ADVISER);
        when(profileIntegrationService
                .loadAvailableJobProfilesForUser(any(BankingCustomerIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(userJobProfile));
        when(credentialService.getPPID(anyString())).thenReturn("0");
        JobProfile jobProfile = mock(JobProfile.class);
        UserProfile userProfile = mock(UserProfile.class);
        when(jobProfile.getJobRole()).thenReturn(JobRole.SERVICE_AND_OPERATION);
        when(userInformation.getUserRoles())
                .thenReturn(Arrays.asList(UserRole.SERVICEOPS_ADMINISTRATOR_BASIC.getRole()));
        when(userProfileService.getAvailableProfiles()).thenReturn(Arrays.asList(jobProfile));
        when(userInformationIntegrationService.getAvailableRoles(any(JobProfile.class), any(ServiceErrors.class)))
                .thenReturn(userInformation);
        when(userProfileService.getActiveProfile()).thenReturn(userProfile);
        when(userProfileService.getActiveProfile().getUserRoles())
                .thenReturn(Arrays.asList(UserRole.SERVICEOPS_ADMINISTRATOR_BASIC.getRole()));
    }

    private void mockClientDetails() {
        when(clientDetail.getFirstName()).thenReturn("FirstName");
        when(clientDetail.getOpenDate()).thenReturn(DateTime.now());
        when(clientDetail.getDateOfBirth()).thenReturn(DateTime.now());
        when(clientDetail.getPhones()).thenReturn(Collections.EMPTY_LIST);
        when(clientDetail.getEmails()).thenReturn(Collections.EMPTY_LIST);
        when(clientDetail.getClientKey()).thenReturn(ClientKey.valueOf("clientId"));
        when(clientDetail.getAddresses()).thenReturn(Collections.EMPTY_LIST);
        when(clientDetail.getCISKey()).thenReturn(CISKey.valueOf("cisKey"));
        when(clientDetail.getWestpacCustomerNumber()).thenReturn("WpacCustNo");
        when(clientDetail.getSafiDeviceId()).thenReturn("safiId");
        when(clientDetail.getCustomerId()).thenReturn("gcmId");
        when(clientDetail.getGcmId()).thenReturn("gcmId");
        when(clientDetail.isRegistrationOnline()).thenReturn(true);
        when(clientIntegrationService.loadClientDetails(any(ClientKey.class), any(ServiceErrorsImpl.class)))
                .thenReturn(clientDetail);
    }

    private List<Phone> getMockPhones() {
        List<Phone> phones = new ArrayList<>();
        Phone phone = new PhoneImpl(AddressKey.valueOf("12345"), AddressMedium.MOBILE_PHONE_PRIMARY, "0488888888", "61",
                "04", null, true, AddressType.ELECTRONIC);
        phones.add(phone);
        return phones;
    }

    private List<Email> getMockEmails() {
        List<Email> emails = new ArrayList<>();
        Email email = new EmailImpl(AddressKey.valueOf("12345"), AddressMedium.EMAIL_PRIMARY, "test@bt.com", null, true,
                AddressType.ELECTRONIC);
        emails.add(email);
        return emails;
    }

    @Test
    public void testGetUserDetail_whenTheUserIsServiceOpsSuperRoleWithoutPhoneAndEmail() throws Exception {
        mockAccountDetailsByGCMForMigratedClient(true);
        when(userInformation.getUserRoles())
                .thenReturn(Arrays.asList(UserRole.SERVICEOPS_ADMINISTRATOR_DG_RESTRICTED.getRole()));
        serviceOpsModel = serviceOpsServiceImpl.getUserDetail("clientId", true, new ServiceErrorsImpl());
        assertNotNull(serviceOpsModel.getInformationMessage());
        assertTrue(serviceOpsModel.isMandatoryDetailMissing());
    }

    @Test
    public void testGetUserDetail_whenTheUserIsServiceOpsSuperRoleWithoutEmail() throws Exception {
        mockAccountDetailsByGCMForMigratedClient(true);
        when(clientDetail.getPhones()).thenReturn(getMockPhones());
        when(userInformation.getUserRoles())
                .thenReturn(Arrays.asList(UserRole.SERVICEOPS_ADMINISTRATOR_DG_RESTRICTED.getRole()));
        serviceOpsModel = serviceOpsServiceImpl.getUserDetail("clientId", true, new ServiceErrorsImpl());
        assertNotNull(serviceOpsModel.getInformationMessage());
        assertTrue(serviceOpsModel.isMandatoryDetailMissing());
    }

    @Test
    public void testGetUserDetail_whenTheUserIsServiceOpsSuperRoleWithoutPhone() throws Exception {
        mockAccountDetailsByGCMForMigratedClient(true);
        when(clientDetail.getEmails()).thenReturn(getMockEmails());
        when(userInformation.getUserRoles())
                .thenReturn(Arrays.asList(UserRole.SERVICEOPS_ADMINISTRATOR_DG_RESTRICTED.getRole()));
        serviceOpsModel = serviceOpsServiceImpl.getUserDetail("clientId", true, new ServiceErrorsImpl());
        assertNotNull(serviceOpsModel.getInformationMessage());
        assertTrue(serviceOpsModel.isMandatoryDetailMissing());}

    @Test
    public void testGetUserDetail_whenTheUserIsServiceOpsSuperRoleWithPhoneAndEMail() throws Exception {
        mockAccountDetailsByGCMForMigratedClient(false);
        when(clientDetail.getPhones()).thenReturn(getMockPhones());
        when(clientDetail.getEmails()).thenReturn(getMockEmails());
        when(userInformation.getUserRoles())
                .thenReturn(Arrays.asList(UserRole.SERVICEOPS_ADMINISTRATOR_DG_RESTRICTED.getRole()));
        serviceOpsModel = serviceOpsServiceImpl.getUserDetail("clientId", true, new ServiceErrorsImpl());
        assertNull(serviceOpsModel.getInformationMessage());
        assertFalse(serviceOpsModel.isMandatoryDetailMissing());
    }

    private void mockAccountDetailsByGCMForMigratedClient(boolean isMigratedClient){
        WrapAccountDetailResponse wrapAccountDetailResponse = mock(WrapAccountDetailResponse.class);
        WrapAccountDetailImpl wrapAccountDetail = mock(WrapAccountDetailImpl.class);
        when(wrapAccountDetail.getMigrationKey()).thenReturn(isMigratedClient ? "BTWRAP12345": null);
        when(wrapAccountDetail.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);
        when(wrapAccountDetail.getAccountKey()).thenReturn(AccountKey.valueOf("1234567881"));
        when(wrapAccountDetail.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        List<WrapAccountDetail> wrapAccounts = new ArrayList<>();
        wrapAccounts.add(wrapAccountDetail);
        when(wrapAccountDetailResponse.getWrapAccountDetails()).thenReturn(wrapAccounts);
        when(accountIntegrationService.loadWrapAccountDetailByGcm(any(BankReferenceIdentifier.class), any(ServiceErrors.class))).thenReturn(wrapAccountDetailResponse);
    }
}