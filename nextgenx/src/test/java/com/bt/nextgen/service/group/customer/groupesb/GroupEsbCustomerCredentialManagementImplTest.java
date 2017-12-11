package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.ModifyChannelAccessCredentialRequest;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.ModifyChannelAccessCredentialResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ProviderErrorDetail;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusDetail;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.CorrelationIdWrapper;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.util.SamlUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ws.soap.client.SoapFaultClientException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GroupEsbCustomerCredentialManagementImplTest {

    @InjectMocks
    private GroupEsbCustomerCredentialManagementImpl credentialManagement;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private WebServiceProvider provider;
    private final String WEBSEAL_APP_SERVER_ID = "websealAppServerId";

    @Before
    public void setUp() {
        when(userProfileService.getCredentialId(any(ServiceErrors.class))).thenReturn("123456789");
        when(userProfileService.getSamlToken()).thenReturn(getSamlToken(true));
    }

    @Test
    public void testRefreshCredential_successResponse() {
        when(provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
            any(ModifyChannelAccessCredentialRequest.class), any(ServiceErrors.class))).thenReturn(getCorrelatedResponse(Level.SUCCESS));
        CustomerCredentialManagementInformation response = credentialManagement.refreshCredential(WEBSEAL_APP_SERVER_ID, new ServiceErrorsImpl());
        assertEquals(response.getServiceLevel(), "SUCCESS");
    }

    @Test
    public void testRefreshCredential_errorResponse() {
        when(provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
            any(ModifyChannelAccessCredentialRequest.class), any(ServiceErrors.class))).thenReturn(getCorrelatedResponse(Level.ERROR));
        CustomerCredentialManagementInformation response = credentialManagement.refreshCredential(WEBSEAL_APP_SERVER_ID, new ServiceErrorsImpl());
        assertEquals(response.getServiceLevel(), "ERROR");
        assertEquals(response.getServiceStatusErrorCode(), "ERR-001");
    }

    @Test
    public void testRefreshCredential_warningResponse() {
        when(provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                any(ModifyChannelAccessCredentialRequest.class), any(ServiceErrors.class))).thenReturn(getCorrelatedResponse(Level.WARNING));
        CustomerCredentialManagementInformation response = credentialManagement.refreshCredential(WEBSEAL_APP_SERVER_ID, new ServiceErrorsImpl());
        assertEquals(response.getServiceLevel(), "WARNING");
    }

    @Test
    public void testRefreshCredential_noCredentialId() {
        when(userProfileService.getCredentialId(any(ServiceErrors.class))).thenReturn("");
        CustomerCredentialManagementInformation response = credentialManagement.refreshCredential(WEBSEAL_APP_SERVER_ID, new ServiceErrorsImpl());
        assertEquals(response, null);
    }

    @Test
    public void testRefreshCredential_noWestpacCustomerNumber() {
        when(userProfileService.getSamlToken()).thenReturn(getSamlToken(false));
        CustomerCredentialManagementInformation response = credentialManagement.refreshCredential(WEBSEAL_APP_SERVER_ID, new ServiceErrorsImpl());
        assertEquals(response, null);
    }

    @Test
    public void testRefreshCredential_noWebsealAppServerId() {
        when(userProfileService.getSamlToken()).thenReturn(getSamlToken(true));
        CustomerCredentialManagementInformation response = credentialManagement.refreshCredential("", new ServiceErrorsImpl());
        assertEquals(response, null);
    }

    @Test
    public void testRefreshCredential_soapException() {
        SoapFaultClientException sfe = mock(SoapFaultClientException.class);
        when(provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
            any(ModifyChannelAccessCredentialRequest.class), any(ServiceErrors.class))).thenThrow(sfe);
        CustomerCredentialManagementInformation response = credentialManagement.refreshCredential(WEBSEAL_APP_SERVER_ID, new ServiceErrorsImpl());
        assertEquals(response, null);
    }

    private SamlToken getSamlToken(boolean hasWestpacCustomerNumber) {
        SamlToken samlToken = new SamlToken(SamlUtil.loadWplSaml());

        if (!hasWestpacCustomerNumber) {
            samlToken.setBankDefinedLogin(null);
        }
        return samlToken;
    }

    private CorrelatedResponse getCorrelatedResponse(Level level) {
        return new CorrelatedResponse(new CorrelationIdWrapper(), getResponseObject(level));
    }

    private ModifyChannelAccessCredentialResponse getResponseObject(Level level) {
        ModifyChannelAccessCredentialResponse responseObject = new ModifyChannelAccessCredentialResponse();
        responseObject.setServiceStatus(getServiceStatus(level));
        return responseObject;
    }

    private ServiceStatus getServiceStatus(Level level) {
        ServiceStatus serviceStatus = new ServiceStatus();
        StatusInfo statusInfo = new StatusInfo();
        statusInfo.setLevel(level);
        statusInfo.setCode("123");
        statusInfo.setDescription("There was a failure.");
        statusInfo.getStatusDetail().add(getStatusDetail());
        serviceStatus.getStatusInfo().add(statusInfo);
        return serviceStatus;
    }

    private StatusDetail getStatusDetail() {
        StatusDetail statusDetail = new StatusDetail();
        statusDetail.getProviderErrorDetail().add(getProviderErrorDetail());
        return statusDetail;
    }

    private ProviderErrorDetail getProviderErrorDetail() {
        ProviderErrorDetail providerErrorDetail = new ProviderErrorDetail();
        providerErrorDetail.setProviderErrorCode("ERR-001");
        return providerErrorDetail;
    }
}
