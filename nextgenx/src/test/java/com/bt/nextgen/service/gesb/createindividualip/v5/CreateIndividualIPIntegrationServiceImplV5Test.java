package com.bt.nextgen.service.gesb.createindividualip.v5;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
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
import org.springframework.ws.soap.client.SoapFaultClientException;

import au.com.westpac.gn.common.xsd.identifiers.v1.CustomerIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.CreateIndividualIPResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.InvolvedParty;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;

import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.CorrelationIdWrapper;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawDataImpl;
import com.bt.nextgen.serviceops.repository.GcmAuditRepository;
import com.bt.nextgen.util.SamlUtil;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ CustomerRawDataImpl.class })
public class CreateIndividualIPIntegrationServiceImplV5Test {
    
    @InjectMocks
    private CreateIndividualIPIntegrationServiceImplV5 createIndividualIPIntegrationServiceImplV5;

    @Mock
    private CreateIndividualIPResponse response;

    @Mock
    private ServiceStatus serviceStatus;

    @Mock
    private WebServiceProvider provider;

    @Mock
    private BankingAuthorityService userSamlService;

    
    @Mock
    private GcmAuditRepository gcmAuditRepository;
    
    @Mock
    private UserProfileService userProfileService;
    
    private void runCommonMockServices() throws Exception {
        SamlToken samlToken = new SamlToken(SamlUtil.loadSaml());
        when(userSamlService.getSamlToken()).thenReturn(samlToken);
        when(response.getServiceStatus()).thenReturn(serviceStatus);
        Mockito.when(userProfileService.getUserId()).thenReturn("201603884");
        doNothing().when(gcmAuditRepository).logAuditEntry("userId", "reqType", "message");
    }

    private StatusInfo getStatus(Level level) {
        StatusInfo status = new StatusInfo();
        status.setLevel(level);
        return status;
    }

    @Test
    public void createIndividualIPTest() throws Exception {
        CorrelatedResponse correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), response);
        CreateIndividualIPResponse res = new CreateIndividualIPResponse();
        InvolvedParty involvedPartyVal =  new InvolvedParty();
        CustomerIdentifier customerIdentifier = new CustomerIdentifier();
        customerIdentifier.setCustomerNumber("11");
        involvedPartyVal.setCustomerIdentifier(customerIdentifier);
        res.setIndividual(involvedPartyVal);
        ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class);
        res.setServiceStatus(serviceStatus);
        correlatedResponse.setResponseObject(res);      
        PowerMockito.mockStatic(CustomerRawDataImpl.class);
        Mockito.when(CustomerRawDataImpl.getJson(res)).thenReturn("{aaaaa}");
        when(
                provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                        anyObject(), any(ServiceErrors.class))).thenReturn(correlatedResponse);
        when(serviceStatus.getStatusInfo()).thenReturn(Arrays.asList(getStatus(Level.SUCCESS)));
        doNothing().when(gcmAuditRepository).logAuditEntry("userId", "reqType", "message");      
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
         CustomerRawData customerRawData =
                createIndividualIPIntegrationServiceImplV5.create(getCreateIndividualIPReq(), serviceErrors);
        assertThat(customerRawData.getRawResponse(), is("{aaaaa}"));
    }

    @Test
    public void createIndividualIPTestError() throws Exception {
        CorrelatedResponse correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), response);
        CreateIndividualIPResponse res = new CreateIndividualIPResponse();
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
                createIndividualIPIntegrationServiceImplV5.create(getCreateIndividualIPReq(),
                        serviceErrors);
        Assert.assertEquals(null, customerRawData.getRawResponse());
    }

    @Test(expected = JsonProcessingException.class)
    public void createIndividualIPTestThrowException() throws JsonProcessingException {
        CorrelatedResponse correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), response);
        CreateIndividualIPResponse res = new CreateIndividualIPResponse();
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
                createIndividualIPIntegrationServiceImplV5.create(getCreateIndividualIPReq(),
                        serviceErrors)).thenThrow(JsonProcessingException.class);
        CustomerRawData customerRawData = createIndividualIPIntegrationServiceImplV5.create(getCreateIndividualIPReq(), serviceErrors);
    }

    @Test
    public void createIndividualIPTestSoapFaultClientException() throws SoapFaultClientException {
        CorrelatedResponse correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), response);
        CreateIndividualIPResponse res = new CreateIndividualIPResponse();
        ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class);        
        correlatedResponse.setResponseObject(res);
        SoapFaultClientException soapFaultClientException = Mockito.mock(SoapFaultClientException.class);
        when(soapFaultClientException.getFaultCode()).thenReturn(new QName("405"));
        when(soapFaultClientException.getFaultStringOrReason()).thenReturn("Service Error");
        when(serviceStatus.getStatusInfo()).thenReturn(Arrays.asList(getStatus(Level.ERROR)));
        res.setServiceStatus(serviceStatus);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        when(
                provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                        anyObject(), any(ServiceErrors.class))).thenThrow(soapFaultClientException);
        CustomerRawData customerRawData = createIndividualIPIntegrationServiceImplV5.create(getCreateIndividualIPReq(), serviceErrors);
        assertThat(customerRawData.getRawResponse(), is(notNullValue()));
        
    }

    private CreateIndvIPRequest getCreateIndividualIPReq() {
        CreateIndvIPRequest createOrganisationIPReq = new CreateIndvIPRequest();
        Individual individual = new Individual();
        individual.setGender("Female");
        createOrganisationIPReq.setIndividual(individual);
        return createOrganisationIPReq;
    }
}