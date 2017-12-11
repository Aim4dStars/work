package com.bt.nextgen.service.groupesb.iptoiprelationships.v4;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrieveiptoiprelationships.v4.svc0260.IndividualTo;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrieveiptoiprelationships.v4.svc0260.InvolvedPartyRelationshipQuery;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrieveiptoiprelationships.v4.svc0260.RetrieveIPToIPRelationshipsResponse;
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
import com.bt.nextgen.service.group.customer.groupesb.maintainarrangementandiparrangementrelationships.RetriveIPToIPRequest;
import com.bt.nextgen.util.SamlUtil;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CustomerRawDataImpl.class })
public class IPToIPRelationshipsIntegrationServiceImplv4Test {
    @InjectMocks
    private IPToIPRelationshipsIntegrationServiceImplv4 iptoipRelationshipsIntegrationServiceImplv4;

    @Mock
    private RetrieveIPToIPRelationshipsResponse response;

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
    public void retrieveIpToIpRelationshipsInformation() throws Exception {
        CorrelatedResponse correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), response);
        RetrieveIPToIPRelationshipsResponse res = new RetrieveIPToIPRelationshipsResponse();
        ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class);
        res.setServiceStatus(serviceStatus);
        correlatedResponse.setResponseObject(res);

        PowerMockito.mockStatic(CustomerRawDataImpl.class);
        Mockito.when(CustomerRawDataImpl.getJson(res)).thenReturn("{aaaaa}");
        RetriveIPToIPRequest request = new RetriveIPToIPRequest();
        List<InvolvedPartyRelationshipQuery> involvedPartyRelationshipQueryList =
                new ArrayList<InvolvedPartyRelationshipQuery>();
        InvolvedPartyRelationshipQuery involvedPartyRelationshipQuery = new InvolvedPartyRelationshipQuery();
        InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
        involvedPartyIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
        involvedPartyIdentifier.setSourceSystem("CIS");
        involvedPartyIdentifier.setInvolvedPartyId("55555555555");
        IndividualTo individualTo = new IndividualTo();
        individualTo.getInvolvedPartyIdentifier().add(involvedPartyIdentifier);
        involvedPartyRelationshipQuery.setIndividual(individualTo);

        involvedPartyRelationshipQueryList.add(involvedPartyRelationshipQuery);
        request.setInvolvedPartyRelationshipQuery(involvedPartyRelationshipQueryList);

        when(
                provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                        anyObject(), any(ServiceErrors.class))).thenReturn(correlatedResponse);
        when(serviceStatus.getStatusInfo()).thenReturn(Arrays.asList(getStatus(Level.SUCCESS)));

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        CustomerRawData customerRawData =
                iptoipRelationshipsIntegrationServiceImplv4.retrieveIpToIpRelationshipsInformation(request,
                        serviceErrors);
        assertThat(customerRawData.getRawResponse(), is("{aaaaa}"));
    }
    @Test
    public void retrieveIpToIpRelationshipsInformationError() throws Exception {
        CorrelatedResponse correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), response);
        RetrieveIPToIPRelationshipsResponse res = new RetrieveIPToIPRelationshipsResponse();
        ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class);
        res.setServiceStatus(serviceStatus);
        correlatedResponse.setResponseObject(res);

        PowerMockito.mockStatic(CustomerRawDataImpl.class);
        RetriveIPToIPRequest request = new RetriveIPToIPRequest();
        when(serviceStatus.getStatusInfo()).thenReturn(Arrays.asList(getStatus(Level.ERROR)));
        List<InvolvedPartyRelationshipQuery> involvedPartyRelationshipQueryList =
                new ArrayList<InvolvedPartyRelationshipQuery>();
        InvolvedPartyRelationshipQuery involvedPartyRelationshipQuery = new InvolvedPartyRelationshipQuery();
        InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
        involvedPartyIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
        involvedPartyIdentifier.setSourceSystem("CIS");
        involvedPartyIdentifier.setInvolvedPartyId("55555555555");
        IndividualTo individualTo = new IndividualTo();
        individualTo.getInvolvedPartyIdentifier().add(involvedPartyIdentifier);
        involvedPartyRelationshipQuery.setIndividual(individualTo);

        involvedPartyRelationshipQueryList.add(involvedPartyRelationshipQuery);
        request.setInvolvedPartyRelationshipQuery(involvedPartyRelationshipQueryList);

        when(
                provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                        anyObject(), any(ServiceErrors.class))).thenReturn(correlatedResponse);
        when(serviceStatus.getStatusInfo()).thenReturn(Arrays.asList(getStatus(Level.SUCCESS)));

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        CustomerRawData customerRawData =
                iptoipRelationshipsIntegrationServiceImplv4.retrieveIpToIpRelationshipsInformation(request,
                        serviceErrors);
        Assert.assertEquals(null, customerRawData.getRawResponse());
    }
}
