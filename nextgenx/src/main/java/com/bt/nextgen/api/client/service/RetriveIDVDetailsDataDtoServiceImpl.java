package com.bt.nextgen.api.client.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.InvolvedPartyType;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.retriveidvdetails.v6.RetriveIDVDetailsIntegrationService;
import com.bt.nextgen.service.group.customer.groupesb.retriveidvdetails.v6.RetriveIDVDtlRequest;
import com.bt.nextgen.serviceops.controller.ServiceOpsController;
import com.bt.nextgen.serviceops.model.RetrieveIDVDetailsReqModel;

@Service("retriveIDVDetailsDtoService")
@SuppressWarnings("squid:S1200")
public class RetriveIDVDetailsDataDtoServiceImpl implements
		RetriveIDVDetailsDataDtoService {
	private static final Logger logger = LoggerFactory
			.getLogger(ServiceOpsController.class);

	private static final String SOURCE_SYSTEM = "CIS";

	private static final String PERSON_TYPE = "Individual";

	@Autowired
	@Qualifier("retriveidvdetialsintegrationservicev6")
	private RetriveIDVDetailsIntegrationService retriveIDVDetailsIntegrationService;

	@Override
	public CustomerRawData retrieve(RetrieveIDVDetailsReqModel reqModel,
			ServiceErrors serviceErrors) {
		RetriveIDVDtlRequest request = createretriveIDVDetailsRequest(reqModel);
		logger.info("Calling IPToIPRelationshipsIntegrationService.retrieve");
		CustomerRawData customerData = retriveIDVDetailsIntegrationService
				.retrieveIDVDetails(request, serviceErrors);

		return customerData;
	}

	private RetriveIDVDtlRequest createretriveIDVDetailsRequest(
			RetrieveIDVDetailsReqModel reqModel) {
		logger.info("Calling RetriveIDVDetailsDataDtoServiceImpl.createretriveIDVDetailsRequest");
		RetriveIDVDtlRequest retriveidvRequest = new RetriveIDVDtlRequest();
		List<InvolvedPartyIdentifier> involvedPartyIdentifierList = new ArrayList<InvolvedPartyIdentifier>();
		InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
		involvedPartyIdentifier
				.setIdentificationScheme(IdentificationScheme.CIS_KEY);
		involvedPartyIdentifier.setInvolvedPartyId(reqModel.getCisKey());
		involvedPartyIdentifierList.add(involvedPartyIdentifier);
		retriveidvRequest
				.setInvolvedPartyIdentifier(involvedPartyIdentifierList);
		retriveidvRequest.setNumberOfRecords(new BigInteger("1"));
		if (PERSON_TYPE.equalsIgnoreCase(reqModel.getPersonType())) {
			retriveidvRequest
					.setInvolvedPartyType(InvolvedPartyType.INDIVIDUAL);
		} else {
			retriveidvRequest
					.setInvolvedPartyType(InvolvedPartyType.ORGANISATION);
		}

		return retriveidvRequest;
	}
}
