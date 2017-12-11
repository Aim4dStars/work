package com.bt.nextgen.service.groupesb.iptoiprelationships.v4;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrieveiptoiprelationships.v4.svc0260.IndividualTo;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrieveiptoiprelationships.v4.svc0260.InvolvedPartyRelationshipQuery;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrieveiptoiprelationships.v4.svc0260.RetrieveIPToIPRelationshipsRequest;

import com.bt.nextgen.service.group.customer.groupesb.maintainarrangementandiparrangementrelationships.RetriveIPToIPRequest;

@RunWith(MockitoJUnitRunner.class)
public class IPToIPRelationshipsRequestBuilderV4Test {
    @Test
    public void createiPToIPRelationshipsRequest() throws Exception {
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
        RetrieveIPToIPRelationshipsRequest req = IPToIPRelationshipsRequestBuilderV4.createiPToIPRelationshipsRequest(request);
        assertNotNull(req);
        Assert.assertEquals("55555555555", req.getInvolvedPartyRelationshipQuery().get(0).getIndividual().getInvolvedPartyIdentifier().get(0).getInvolvedPartyId());
    }
}
