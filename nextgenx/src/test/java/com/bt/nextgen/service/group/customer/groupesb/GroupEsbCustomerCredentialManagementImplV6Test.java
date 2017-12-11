package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v6.svc0310.ModifyChannelAccessCredentialRequest;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v6.svc0310.ModifyChannelAccessCredentialResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.*;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.CorrelationIdWrapper;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.util.SamlUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by L075208 on 19/07/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupEsbCustomerCredentialManagementImplV6Test {

    @InjectMocks
    private GroupEsbCustomerCredentialManagementImplV6 credentialManagement;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private WebServiceProvider provider;

    @Before
    public void setUp() {
        when(userProfileService.getCredentialId(any(ServiceErrors.class))).thenReturn("123456789");
        when(userProfileService.getSamlToken()).thenReturn(getSamlToken(true));
    }


    @Test
    public void testUpdatePPID_successResponse() {
        when(provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                any(ModifyChannelAccessCredentialRequest.class), any(ServiceErrors.class))).thenReturn(getCorrelatedResponse(Level.SUCCESS));
        boolean response = credentialManagement.updatePPID("PPID","CredentialID", new ServiceErrorsImpl());
        assertNotNull(response);
        Assert.assertTrue(response);
    }

    @Test
    public void testUpdatePPID_ErrorResponse(){
        when(provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                any(ModifyChannelAccessCredentialRequest.class), any(ServiceErrors.class))).thenReturn(getCorrelatedResponse(Level.ERROR));
        boolean response = credentialManagement.updatePPID("PPID","credentialID", new ServiceErrorsImpl());
        assertNotNull(response);
        Assert.assertFalse(response);
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
