package com.bt.nextgen.service.gesb.maintainipcontactmethod.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.MaintainIPContactMethodsRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.ObjectFactory;

public class MaintainIpContactRequestBuilderV1 {
	private static final Logger logger = LoggerFactory
			.getLogger(MaintainIpContactRequestBuilderV1.class);

	private MaintainIpContactRequestBuilderV1() {

	}

	public static MaintainIPContactMethodsRequest createIpContactRequest(
			MaintainIpContactRequest request) {
		logger.info("Creating MaintainIPContactMethodsRequest inside public class MaintainIpContactRequestBuilderV1");
		final ObjectFactory factory = new ObjectFactory();
		MaintainIPContactMethodsRequest req = factory.createMaintainIPContactMethodsRequest();
		req.setInvolvedPartyType(request.getInvolvedPartyType());
		req.getHasEmailAddressContactMethod().add(request.getHasEmailAddressContactMethod());
		req.getHasPhoneAddressContactMethod().add(request.getHasPhoneAddressContactMethod());
		req.getInvolvedPartyIdentifier().add(request.getInvolvedPartyIdentifier());
		return req;
	}
}
