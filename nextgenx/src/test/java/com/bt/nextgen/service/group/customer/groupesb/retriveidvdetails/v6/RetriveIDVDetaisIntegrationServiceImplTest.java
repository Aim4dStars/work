package com.bt.nextgen.service.group.customer.groupesb.retriveidvdetails.v6;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.InvolvedPartyType;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.RetrieveIDVDetailsResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;

import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawDataImpl;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

@RunWith(MockitoJUnitRunner.class)
public class RetriveIDVDetaisIntegrationServiceImplTest {

    @Mock
    RetriveIDVDetailsIntegrationServiceImplv6 integrationServiceV6;

    @InjectMocks
    RetriveIDVDetaisIntegrationServiceImpl retriveIDVDetaisIntegrationServiceImpl;

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

    @Test
    public void testretrieveIDVDetails() throws JsonProcessingException {
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
        when(integrationServiceV6.retrieveIDVDetails(retriveidvRequest, serviceErrors)).thenReturn(
                new CustomerRawDataImpl(null));
        CustomerRawData customerRawData =
                retriveIDVDetaisIntegrationServiceImpl.retrieveIDVDetails(retriveidvRequest, serviceErrors);
        assertNotNull(customerRawData);
    }

}