/**
 * 
 */
package com.bt.nextgen.service.groupesb.iptoiprelationships.v4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrieveiptoiprelationships.v4.svc0260.ObjectFactory;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrieveiptoiprelationships.v4.svc0260.RetrieveIPToIPRelationshipsRequest;

import com.bt.nextgen.service.group.customer.groupesb.maintainarrangementandiparrangementrelationships.RetriveIPToIPRequest;

/**
 * @author L081050
 */
public class IPToIPRelationshipsRequestBuilderV4 {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(IPToIPRelationshipsIntegrationServiceImplv4.class);

	private IPToIPRelationshipsRequestBuilderV4(){
		
	}
	
	public static RetrieveIPToIPRelationshipsRequest createiPToIPRelationshipsRequest(
			RetriveIPToIPRequest request) {
		LOGGER.info("Creating RetrieveIPToIPRelationshipsRequest inside IPToIPRelationshipsRequestBuilderV4");
		final ObjectFactory factory = new ObjectFactory();
		RetrieveIPToIPRelationshipsRequest req = factory
				.createRetrieveIPToIPRelationshipsRequest();
		req.getInvolvedPartyRelationshipQuery().add(
				request.getInvolvedPartyRelationshipQuery().get(0));
		return req;
	}
}
