package com.bt.nextgen.api.saml.service;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.ModifyChannelAccessCredentialResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.saml.model.SamlTokenRefreshDto;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementIntegrationService;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.service.group.customer.groupesb.GroupEsbCustomerCredentialManagementAdapter;
import com.bt.nextgen.test.MockAuthentication;
import com.bt.nextgen.util.SamlUtil;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by F030695 on 18/01/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class SamlTokenRefreshDtoServiceImplTest extends MockAuthentication {

    private final String WEBSEAL_APP_SERVER_ID = "websealAppServerId";
    @InjectMocks
    private SamlTokenRefreshDtoServiceImpl samlTokenRefreshDtoService;

    @Mock
    private CustomerCredentialManagementIntegrationService customerCredentialManagementIntegrationService;

    private SamlToken samlToken;


    @Before
    public void setup() {
        samlToken = new SamlToken(SamlUtil.loadWplSaml());
    }

    @Test
    public void testResponse_successful() {
        mockAuthentication("investor");
        CustomerCredentialManagementInformation updateResponse = getCustomerCredentialUpdateInformation(Level.SUCCESS);
        Mockito.when(customerCredentialManagementIntegrationService.refreshCredential(Mockito.eq(WEBSEAL_APP_SERVER_ID), Mockito.any(ServiceErrors.class))).thenReturn(updateResponse);
        SamlTokenRefreshDto response = samlTokenRefreshDtoService.refreshSamlToken(WEBSEAL_APP_SERVER_ID, new ServiceErrorsImpl());
        Mockito.verify(customerCredentialManagementIntegrationService, times(1)).refreshCredential(Mockito.eq(WEBSEAL_APP_SERVER_ID), Mockito.any(ServiceErrors.class));
        assertTrue(response.isRefreshed());
    }

    @Test
    public void testResponse_failed() {
        mockAuthentication("investor");
        CustomerCredentialManagementInformation updateResponse = getCustomerCredentialUpdateInformation(Level.ERROR);
        Mockito.when(customerCredentialManagementIntegrationService.refreshCredential(Mockito.eq(WEBSEAL_APP_SERVER_ID), Mockito.any(ServiceErrors.class))).thenReturn(updateResponse);
        SamlTokenRefreshDto response = samlTokenRefreshDtoService.refreshSamlToken(WEBSEAL_APP_SERVER_ID,new ServiceErrorsImpl());
        Mockito.verify(customerCredentialManagementIntegrationService, times(1)).refreshCredential(Mockito.eq(WEBSEAL_APP_SERVER_ID), Mockito.any(ServiceErrors.class));
        assertFalse(response.isRefreshed());
    }

    @Test
    public void testNullWebsealAppServerId() {
        mockAuthentication("investor");
        SamlTokenRefreshDto response = samlTokenRefreshDtoService.refreshSamlToken(null,new ServiceErrorsImpl());
        Mockito.verify(customerCredentialManagementIntegrationService, times(0)).refreshCredential(Mockito.any(String.class), Mockito.any(ServiceErrors.class));
        assertFalse(response.isRefreshed());
    }

    @Test
    public void testEmptyWebsealAppServerId() {
        mockAuthentication("investor");
        SamlTokenRefreshDto response = samlTokenRefreshDtoService.refreshSamlToken("",new ServiceErrorsImpl());
        Mockito.verify(customerCredentialManagementIntegrationService, times(0)).refreshCredential(Mockito.any(String.class), Mockito.any(ServiceErrors.class));
        assertFalse(response.isRefreshed());
    }

    private CustomerCredentialManagementInformation getCustomerCredentialUpdateInformation(Level level) {
        ModifyChannelAccessCredentialResponse response = new ModifyChannelAccessCredentialResponse();
        response.setServiceStatus(getServiceStatus(level));
        return new GroupEsbCustomerCredentialManagementAdapter(response);
    }

    private ServiceStatus getServiceStatus(Level level) {
        ServiceStatus serviceStatus = new ServiceStatus();
        StatusInfo statusInfo = new StatusInfo();
        statusInfo.setLevel(level);
        serviceStatus.getStatusInfo().add(statusInfo);
        return serviceStatus;
    }
}
