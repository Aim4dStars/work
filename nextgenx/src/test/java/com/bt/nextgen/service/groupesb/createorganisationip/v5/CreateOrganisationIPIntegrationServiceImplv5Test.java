/**
 * 
 */
package com.bt.nextgen.service.groupesb.createorganisationip.v5;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.ws.soap.client.SoapFaultClientException;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.CreateOrganisationResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.InvolvedPartyName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.InvolvedPartyRole;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.Organisation;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;

import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.CorrelationIdWrapper;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawDataImpl;
import com.bt.nextgen.util.SamlUtil;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author L081050
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ CustomerRawDataImpl.class })
public class CreateOrganisationIPIntegrationServiceImplv5Test {
    @InjectMocks
    private CreateOrganisationIPIntegrationServiceImplv5 createOrganisationIPIntegrationServiceImplv5;

    @Mock
    private CreateOrganisationResponse response;

    @Mock
    private ServiceStatus serviceStatus;

    @Mock
    private WebServiceProvider provider;

    @Mock
    private BankingAuthorityService userSamlService;

    @Mock
    private CustomerRawData customerRawData;

    private void runCommonMockServices() throws Exception {
        SamlToken samlToken = new SamlToken(SamlUtil.loadSaml());
        when(userSamlService.getSamlToken()).thenReturn(samlToken);
        when(response.getServiceStatus()).thenReturn(serviceStatus);
    }

    private StatusInfo getStatus(Level level) {
        StatusInfo status = new StatusInfo();
        status.setLevel(level);
        return status;
    }

    @Test
    public void createorganisationIPTest() throws Exception {
        CorrelatedResponse correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), response);
        CreateOrganisationResponse res = new CreateOrganisationResponse();
        ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class);
        res.setServiceStatus(serviceStatus);
        correlatedResponse.setResponseObject(res);
        PowerMockito.mockStatic(CustomerRawDataImpl.class);
        Mockito.when(CustomerRawDataImpl.getJson(res)).thenReturn("{aaaaa}");
        when(
                provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                        anyObject(), any(ServiceErrors.class))).thenReturn(correlatedResponse);
        when(serviceStatus.getStatusInfo()).thenReturn(Arrays.asList(getStatus(Level.SUCCESS)));

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        CustomerRawData customerRawData =
                createOrganisationIPIntegrationServiceImplv5.createorganisationIP(getCreateOrganisationIPReq(),
                        serviceErrors);
        assertThat(customerRawData.getRawResponse(), is("{aaaaa}"));
    }

    @Test
    public void createorganisationIPTestError() throws Exception {
        CorrelatedResponse correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), response);
        CreateOrganisationResponse res = new CreateOrganisationResponse();
        ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class);
        res.setServiceStatus(serviceStatus);
        correlatedResponse.setResponseObject(res);
        when(serviceStatus.getStatusInfo()).thenReturn(Arrays.asList(getStatus(Level.ERROR)));
        when(
                provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                        anyObject(), any(ServiceErrors.class))).thenReturn(correlatedResponse);
        when(serviceStatus.getStatusInfo()).thenReturn(Arrays.asList(getStatus(Level.ERROR)));

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        CustomerRawData customerRawData =
                createOrganisationIPIntegrationServiceImplv5.createorganisationIP(getCreateOrganisationIPReq(),
                        serviceErrors);
        Assert.assertEquals(null, customerRawData.getRawResponse());
    }

    @Test
    public void createorganisationIPTestThrowException() throws JsonProcessingException {
        CorrelatedResponse correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), response);
        CreateOrganisationResponse res = new CreateOrganisationResponse();
        ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class);
        res.setServiceStatus(serviceStatus);
        correlatedResponse.setResponseObject(res);
        when(serviceStatus.getStatusInfo()).thenReturn(Arrays.asList(getStatus(Level.ERROR)));
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        when(
                provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                        anyObject(), any(ServiceErrors.class))).thenReturn(correlatedResponse);
        when(serviceStatus.getStatusInfo()).thenReturn(Arrays.asList(getStatus(Level.SUCCESS)));
        when(
                createOrganisationIPIntegrationServiceImplv5.createorganisationIP(getCreateOrganisationIPReq(),
                        serviceErrors)).thenThrow(JsonProcessingException.class);
        createOrganisationIPIntegrationServiceImplv5.createorganisationIP(getCreateOrganisationIPReq(), serviceErrors);
    }

    @Test
    public void createorganisationIPTestSoapFaultClientException() throws SoapFaultClientException {
        CorrelatedResponse correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), response);
        CreateOrganisationResponse res = new CreateOrganisationResponse();
        ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class);
        res.setServiceStatus(serviceStatus);
        correlatedResponse.setResponseObject(res);
        SoapFaultClientException soapFaultClientException = Mockito.mock(SoapFaultClientException.class);
        when(soapFaultClientException.getFaultCode()).thenReturn(new QName("405"));
        when(soapFaultClientException.getFaultStringOrReason()).thenReturn("Service Error");
        when(serviceStatus.getStatusInfo()).thenReturn(Arrays.asList(getStatus(Level.ERROR)));
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        when(
                provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                        anyObject(), any(ServiceErrors.class))).thenThrow(soapFaultClientException);
        createOrganisationIPIntegrationServiceImplv5.createorganisationIP(getCreateOrganisationIPReq(), serviceErrors);
    }

    private CreateOrganisationIPReq getCreateOrganisationIPReq() {
        CreateOrganisationIPReq createOrganisationIPReq = new CreateOrganisationIPReq();
        Organisation organisation = new Organisation();
        InvolvedPartyName involvedPartyName = new InvolvedPartyName();
        involvedPartyName.setFullName("Test");
        organisation.setHasForName(involvedPartyName);
        organisation.setLifecycleStatus("Active");
        InvolvedPartyRole InvolvedPartyRole = new InvolvedPartyRole();
        InvolvedPartyRole.setRoleType("Individual");
        organisation.setIsPlayingRole(InvolvedPartyRole);
        createOrganisationIPReq.setOragnaisation(organisation);
        return createOrganisationIPReq;
    }
}
