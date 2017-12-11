package com.bt.nextgen.service.group.customer.groupesb.retriveidvdetails.v6;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import au.com.westpac.gn.common.xsd.identifiers.v1.CustomerIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.InvolvedPartyType;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.RetrieveIDVDetailsRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.RetrieveIDVDetailsResponse;
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

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CustomerRawDataImpl.class })
public class RetriveIDVDetailsIntegrationServiceImplv6Test {

    @InjectMocks
    private RetriveIDVDetailsIntegrationServiceImplv6 retriveIDVDetailsIntegrationServiceImplv6;

    @Mock
    private WebServiceProvider provider;

    @Mock
    private RetrieveIDVDetailsResponse response;

    @Mock
    private BankingAuthorityService userSamlService;
    @Mock
    private ServiceStatus serviceStatus;
   

    @Mock
    private CustomerRawData customerRawData;

    private void runCommonMockServices() throws Exception {
        SamlToken samlToken = new SamlToken(SamlUtil.loadSaml());
        when(userSamlService.getSamlToken()).thenReturn(samlToken);
        when(response.getServiceStatus()).thenReturn(serviceStatus);
    }

    @Test
    public void testretrieveIDVDetails() throws JsonProcessingException {
        RetrieveIDVDetailsResponse res = new RetrieveIDVDetailsResponse();
        CorrelatedResponse correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), response);

        ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class);
        res.setServiceStatus(serviceStatus);

        PowerMockito.mockStatic(CustomerRawDataImpl.class);
        Mockito.when(CustomerRawDataImpl.getJson(res)).thenReturn("{aaaaa}");

        correlatedResponse.setResponseObject(res);

        RetriveIDVDtlRequest retriveidvRequest = new RetriveIDVDtlRequest();
        List<InvolvedPartyIdentifier> involvedPartyIdentifierList = new ArrayList<InvolvedPartyIdentifier>();
        InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
        involvedPartyIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
        involvedPartyIdentifier.setInvolvedPartyId("12456543215");
        involvedPartyIdentifierList.add(involvedPartyIdentifier);
        retriveidvRequest.setInvolvedPartyIdentifier(involvedPartyIdentifierList);
        retriveidvRequest.setNumberOfRecords(new BigInteger("1"));
        retriveidvRequest.setInvolvedPartyType(InvolvedPartyType.INDIVIDUAL);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        RetrieveIDVDetailsRequest requestPayload =
                RetriveIDVDetailsRequestBuilderV6.createIDVDetialsRequest(retriveidvRequest);
        SamlToken samlToken = new SamlToken(SamlUtil.loadSaml());
        when(userSamlService.getSamlToken()).thenReturn(samlToken);
        when(
                provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                        anyObject(), any(ServiceErrors.class))).thenReturn(correlatedResponse);
        when(serviceStatus.getStatusInfo()).thenReturn(getStatus(Level.SUCCESS));

        CustomerRawData customerRawData =
                retriveIDVDetailsIntegrationServiceImplv6.retrieveIDVDetails(retriveidvRequest, serviceErrors);
        assertThat(customerRawData.getRawResponse(), is("{aaaaa}"));
    }

    @Test
    public void testretrieveIDVDetailsWithError() throws JsonProcessingException {
        RetrieveIDVDetailsResponse res = new RetrieveIDVDetailsResponse();
        CorrelatedResponse correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), response);

        ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class);
        res.setServiceStatus(serviceStatus);

        PowerMockito.mockStatic(CustomerRawDataImpl.class);
        Mockito.when(CustomerRawDataImpl.getJson(res)).thenReturn("{aaaaa}");

        correlatedResponse.setResponseObject(res);

        RetriveIDVDtlRequest retriveidvRequest = new RetriveIDVDtlRequest();
        List<InvolvedPartyIdentifier> involvedPartyIdentifierList = new ArrayList<InvolvedPartyIdentifier>();
        InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
        involvedPartyIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
        involvedPartyIdentifier.setInvolvedPartyId("12456543215");
        involvedPartyIdentifierList.add(involvedPartyIdentifier);
        retriveidvRequest.setInvolvedPartyIdentifier(involvedPartyIdentifierList);
        retriveidvRequest.setNumberOfRecords(new BigInteger("1"));
        retriveidvRequest.setInvolvedPartyType(InvolvedPartyType.INDIVIDUAL);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        RetrieveIDVDetailsRequest requestPayload =
                RetriveIDVDetailsRequestBuilderV6.createIDVDetialsRequest(retriveidvRequest);
        SamlToken samlToken = new SamlToken(SamlUtil.loadSaml());
        when(userSamlService.getSamlToken()).thenReturn(samlToken);
        when(
                provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                        anyObject(), any(ServiceErrors.class))).thenReturn(correlatedResponse);
        when(serviceStatus.getStatusInfo()).thenReturn(getStatus(Level.ERROR));

        CustomerRawData customerRawData =
                retriveIDVDetailsIntegrationServiceImplv6.retrieveIDVDetails(retriveidvRequest, serviceErrors);
        assertThat(customerRawData.getRawResponse(), is("{aaaaa}"));
    }
   
    @Test
    public void testretrieveIDVDetailsThrowException() throws JsonProcessingException {
        RetrieveIDVDetailsResponse res = new RetrieveIDVDetailsResponse();
        CorrelatedResponse correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), response);

        ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class);
        res.setServiceStatus(serviceStatus);

        PowerMockito.mockStatic(CustomerRawDataImpl.class);
        Mockito.when(CustomerRawDataImpl.getJson(res)).thenReturn("{aaaaa}");

        correlatedResponse.setResponseObject(res);

        RetriveIDVDtlRequest retriveidvRequest = new RetriveIDVDtlRequest();
        List<InvolvedPartyIdentifier> involvedPartyIdentifierList = new ArrayList<InvolvedPartyIdentifier>();
        InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
        involvedPartyIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
        involvedPartyIdentifier.setInvolvedPartyId("12456543215");
        involvedPartyIdentifierList.add(involvedPartyIdentifier);
        retriveidvRequest.setInvolvedPartyIdentifier(involvedPartyIdentifierList);
        retriveidvRequest.setNumberOfRecords(new BigInteger("1"));
        retriveidvRequest.setInvolvedPartyType(InvolvedPartyType.INDIVIDUAL);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        RetrieveIDVDetailsRequest requestPayload =
                RetriveIDVDetailsRequestBuilderV6.createIDVDetialsRequest(retriveidvRequest);
        SamlToken samlToken = new SamlToken(SamlUtil.loadSaml());
        when(userSamlService.getSamlToken()).thenReturn(samlToken);
        when(
                provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                        anyObject(), any(ServiceErrors.class))).thenReturn(correlatedResponse);
        when(serviceStatus.getStatusInfo()).thenReturn(getStatus(Level.SUCCESS));
        when(retriveIDVDetailsIntegrationServiceImplv6.retrieveIDVDetails(retriveidvRequest, serviceErrors)).thenThrow(JsonProcessingException.class);
                retriveIDVDetailsIntegrationServiceImplv6.retrieveIDVDetails(retriveidvRequest, serviceErrors);
    }
    @Test
    public void testRetriveIDVDtlRequest(){
        CustomerIdentifier customerIdentifier = new CustomerIdentifier();
        RetriveIDVDtlRequest RetriveIDVDtlRequest = new RetriveIDVDtlRequest();
        RetriveIDVDtlRequest.setCustomerIdentifier(customerIdentifier);
        assertNotNull(RetriveIDVDtlRequest.getCustomerIdentifier());
    }
    private List<StatusInfo> getStatus(Level level) {
        List<StatusInfo>  statusInfo = new ArrayList<StatusInfo>();
        StatusInfo status = new StatusInfo();
        status.setLevel(level);
        statusInfo.add(status);
        return statusInfo;
    }

}
