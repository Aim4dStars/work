/**
 * 
 */
package com.bt.nextgen.service.group.customer.groupesb.retriveidvdetails.v6;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.RetrieveIDVDetailsRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.ObjectFactory;

/**
 * @author L081050
 */
public class RetriveIDVDetailsRequestBuilderV6 {
    private static final Logger LOGGER = LoggerFactory.getLogger(RetriveIDVDetailsIntegrationServiceImplv6.class);

    private RetriveIDVDetailsRequestBuilderV6() {

    }

    public static RetrieveIDVDetailsRequest createIDVDetialsRequest(RetriveIDVDtlRequest request) {
        LOGGER.info("Creating RetrieveIPToIPRelationshipsRequest inside IPToIPRelationshipsRequestBuilderV4");
        final ObjectFactory factory = new ObjectFactory();
        RetrieveIDVDetailsRequest req = factory.createRetrieveIDVDetailsRequest();
        req.getInvolvedPartyIdentifier().add(request.getInvolvedPartyIdentifier().get(0));
        req.setInvolvedPartyType(request.getInvolvedPartyType());
        req.setNumberOfRecords(request.getNumberOfRecords());
        return req;
    }
}
