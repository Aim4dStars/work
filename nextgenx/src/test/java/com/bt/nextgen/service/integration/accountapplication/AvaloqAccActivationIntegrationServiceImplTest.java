package com.bt.nextgen.service.integration.accountapplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.accountactivation.AccountApplicationImpl;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationDocumentImpl;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import com.bt.nextgen.service.avaloq.accountactivation.AssociatedPersonImpl;
import com.bt.nextgen.service.avaloq.accountactivation.AvaloqAccActivationIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.btfin.panorama.core.security.integration.customer.CustomerCredentialInformation;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by F030695 on 2/09/2016.
 */

@RunWith(MockitoJUnitRunner.class)
public class AvaloqAccActivationIntegrationServiceImplTest {

    @InjectMocks
    private AvaloqAccActivationIntegrationServiceImpl accActivationIntegrationService;

    @Mock
    private AvaloqExecute avaloqExecute;

    @Mock
    private UserProfileService userProfileService;

    private UserProfile userProfile;

    @Before
    public void setup() {
        userProfile = getUserProfile("123");
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(userProfile);
    }

    @Test
    public void testLoadApplicationForPortfolio_withClientFiltering_withResults() {
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
            Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(getAccountApplication(new String[] {"123", "456", "123"}));
        List<ApplicationDocument> applicationDocuments = accActivationIntegrationService
            .loadAccApplicationForPortfolio(Arrays.asList(getWrapId("1"), getWrapId("2")), userProfile.getJobRole(),userProfile.getClientKey(),new ServiceErrorsImpl());
        assertEquals(applicationDocuments.size(), 2);
    }

    @Test
    public void testLoadApplicationForPortfolio_withClientFiltering_withNoResults() {
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
            Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(getAccountApplication(new String[] {"456", "789", "101"}));
        List<ApplicationDocument> applicationDocuments = accActivationIntegrationService
            .loadAccApplicationForPortfolio(Arrays.asList(getWrapId("1"), getWrapId("2")), userProfileService.getActiveProfile().getJobRole(),userProfileService.getActiveProfile().getClientKey(),new ServiceErrorsImpl());
        assertEquals(applicationDocuments.size(), 0);
    }

    @Test
    public void testLoadApplicationForPortfolio_withNoClientFiltering() {
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
            Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(getAccountApplication(new String[] {"456", "789", "101"}));
        List<ApplicationDocument> applicationDocuments = accActivationIntegrationService
            .loadAccApplicationForPortfolioWithNoFilter(Arrays.asList(getWrapId("1"), getWrapId("2")), new ServiceErrorsImpl());
        assertEquals(applicationDocuments.size(), 3);
    }

    private UserProfile getUserProfile(final String clientId) {
        UserInformation user = getUserInformation(clientId);
        user.setClientKey(ClientKey.valueOf(clientId));
        JobProfile job = getJobProfile();
        CustomerCredentialInformation credential = getCredentialInformation();
        return new UserProfileAdapterImpl(user, job, credential);
    }

    private UserInformation getUserInformation(final String clientId) {
        UserInformation userInfo = mock(UserProfile.class);
        when(userInfo.getClientKey()).thenReturn(ClientKey.valueOf(clientId));
        return userInfo;
    }

    private JobProfile getJobProfile() {
        JobProfileImpl job = new JobProfileImpl();
        job.setJobRole(JobRole.INVESTOR);
        return job;
    }

    private CustomerCredentialInformation getCredentialInformation() {
        CustomerCredentialInformation credential = mock(CustomerCredentialInformation.class);
        when(credential.getBankReferenceId()).thenReturn("12345678");
        return credential;
    }

    private AccountApplicationImpl getAccountApplication(String[] clientIds) {
        AccountApplicationImpl accountApplication = new AccountApplicationImpl();
        accountApplication.setApplication(getApplicationDocuments(clientIds));
        return accountApplication;
    }

    private List<ApplicationDocument> getApplicationDocuments(String ... clientIds) {
        List<ApplicationDocument> applicationDocuments = new ArrayList<>();
        for (String clientId : clientIds) {
            applicationDocuments.add(getApplicationDocument(clientId));
        }
        return applicationDocuments;
    }

    private ApplicationDocument getApplicationDocument(String clientId) {
        ApplicationDocument applicationDocument = new ApplicationDocumentImpl();
        applicationDocument.setPersonDetails(getPersonDetails(clientId));
        return applicationDocument;
    }

    private List<AssociatedPerson> getPersonDetails(String clientId) {
        AssociatedPerson associatedPerson = new AssociatedPersonImpl();
        associatedPerson.setClientKey(ClientKey.valueOf(clientId));
        return Arrays.asList(associatedPerson);
    }

    private WrapAccountIdentifier getWrapId(String bpId) {
        WrapAccountIdentifierImpl wrapAccountIdentifier = new WrapAccountIdentifierImpl();
        wrapAccountIdentifier.setBpId(bpId);
        return wrapAccountIdentifier;
    }
}
