package com.bt.nextgen.service.gesb.maintainidvdetail.v5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.MaintainIDVDetailsRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.ObjectFactory;

public class MaintainIdvDetailRequestBuilderV5 {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MaintainIdvDetailRequestBuilderV5.class);

	private MaintainIdvDetailRequestBuilderV5() {

	}

	public static MaintainIDVDetailsRequest createIDVDetialsRequest(
			MaintainIdvRequest request) {
		LOGGER.info("Creating MaintainIDVDetailsRequest inside MaintainIdvDetailRequestBuilderV5");
		final ObjectFactory factory = new ObjectFactory();
		MaintainIDVDetailsRequest req = factory
				.createMaintainIDVDetailsRequest();
		req.setRequestAction(request.getRequestAction());
		req.setIdentityVerificationAssessment(request
				.getIdentityVerificationAssessment());
		return req;
	}
}
