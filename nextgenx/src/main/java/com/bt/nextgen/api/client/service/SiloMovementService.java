package com.bt.nextgen.api.client.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindingResult;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.MaintainIDVDetailsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.RetrieveIDVDetailsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.CreateIndividualIPResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainarrangementandiparrangementrelationships.v1.svc0256.MaintainArrangementAndIPArrangementRelationshipsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;

import com.bt.nextgen.serviceops.model.SiloMovementReqModel;
import com.bt.nextgen.silomovement.exception.SiloMovementException;

public interface SiloMovementService {
	public void siloMovement(SiloMovementReqModel reqModel, BindingResult bindingResult, HttpServletRequest req, HttpServletResponse res)
			throws SiloMovementException;

	public List<MaintainArrangementAndIPArrangementRelationshipsResponse> executeService256Create(
			List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List,
			RetrieveDetailsAndArrangementRelationshipsForIPsResponse response258, CreateIndividualIPResponse response336,
			SiloMovementReqModel reqModel, HttpServletRequest req, HttpServletResponse res) throws SiloMovementException;
	public List<MaintainArrangementAndIPArrangementRelationshipsResponse> executeService256Delete(
            List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List,
            RetrieveDetailsAndArrangementRelationshipsForIPsResponse response258, CreateIndividualIPResponse response336,
            SiloMovementReqModel reqModel, HttpServletRequest req, HttpServletResponse res) throws SiloMovementException;

	public MaintainIDVDetailsResponse executeService325(RetrieveIDVDetailsResponse response324, CreateIndividualIPResponse response336, SiloMovementReqModel reqModel, HttpServletRequest req)
			throws SiloMovementException;

	public RetrieveIDVDetailsResponse executeService324(SiloMovementReqModel reqModel, BindingResult bindingResult) throws SiloMovementException;

	public RetrieveDetailsAndArrangementRelationshipsForIPsResponse executeService258(SiloMovementReqModel reqModel) throws SiloMovementException;

	public CreateIndividualIPResponse executeService336(RetrieveDetailsAndArrangementRelationshipsForIPsResponse response258,
			SiloMovementReqModel reqModel, BindingResult bindingResult) throws SiloMovementException;
}
