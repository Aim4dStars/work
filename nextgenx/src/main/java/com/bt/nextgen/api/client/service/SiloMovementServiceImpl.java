package com.bt.nextgen.api.client.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.MaintainIDVDetailsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.RetrieveIDVDetailsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.CreateIndividualIPResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainarrangementandiparrangementrelationships.v1.svc0256.MaintainArrangementAndIPArrangementRelationshipsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyArrangementRole;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;

import com.bt.nextgen.service.group.customer.groupesb.ClientDetailsOperation;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.serviceops.controller.CreateIndividualIpController;
import com.bt.nextgen.serviceops.controller.ServiceOpsController;
import com.bt.nextgen.serviceops.controller.ServiceOpsGcmController;
import com.bt.nextgen.serviceops.controller.ServiceOpsGcmSiloController;
import com.bt.nextgen.serviceops.controller.SiloMovementController;
import com.bt.nextgen.serviceops.model.CreateIndividualIPEmailPhoneContactMethodsReqModel;
import com.bt.nextgen.serviceops.model.CreateIndividualIPReqModel;
import com.bt.nextgen.serviceops.model.MaintainArrangementAndRelationshipReqModel;
import com.bt.nextgen.serviceops.model.MaintainIdvDetailReqModel;
import com.bt.nextgen.serviceops.model.RetrieveIDVDetailsReqModel;
import com.bt.nextgen.serviceops.model.SiloMovementReqModel;
import com.bt.nextgen.silomovement.exception.SiloMovementException;
import com.bt.nextgen.web.controller.cash.util.Attribute;

/**
 * @author L091297
 * 
 *         Do not change the methods' names Aspect is written based on this
 *         methods. Functionality will break down if you will change the
 *         methods' names.
 */
@Service("siloMovementService")
@SuppressWarnings("squid:S1200")
// Single Responsibility Principle
public class SiloMovementServiceImpl implements SiloMovementService {

	private static final Logger logger = LoggerFactory.getLogger(ServiceOpsController.class);

	@Autowired
	private CreateIndividualIpController createIndividualIpController;

	@Autowired
	private ServiceOpsGcmController serviceOpsGcmController;

	@Autowired
	private ServiceOpsGcmSiloController serviceOpsGcmSiloController;

	@Autowired
	private SiloMovementRequestBuilder requestBuilder;

	@Resource
	private SiloMovementController selfSiloMovementController;

	private final static String ACCOUNT_PREFIX = "NG-002";

	@Autowired
	private SiloMovementService selfSiloMovementService;

	@Autowired
	@Qualifier("maintainSiloMovementStatusService")
	private MaintainSiloMovementStatusService maintainSiloMovementStatusService;

	private static final String SERVICE_258_STATUS = "RTRDET";
	private static final String SERVICE_336_STATUS = "MNT336";
	private static final String SERVICE_325_STATUS = "MNT325";
	private static final String SERVICE_256_STATUS_CRT = "MNT256_CRT";
	private static final String SERVICE_256_STATUS_END = "MNT256_END";

	@Override
	public void siloMovement(SiloMovementReqModel reqModel, BindingResult bindingResult, HttpServletRequest req, HttpServletResponse res)
			throws SiloMovementException {

		logger.info("Calling ServiceOpsGCMController.siloMovement()");

		RetrieveDetailsAndArrangementRelationshipsForIPsResponse response258 = null;
		CreateIndividualIPResponse response336 = null;
		RetrieveIDVDetailsResponse response324 = null;
		List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List = new ArrayList<MaintainArrangementAndIPArrangementRelationshipsResponse>();

		req.setAttribute("silo-movement", reqModel.getFromSilo());
		response258 = selfSiloMovementService.executeService258(reqModel);
		response324 = selfSiloMovementService.executeService324(reqModel, bindingResult);
		req.setAttribute("silo-movement", reqModel.getToSilo());
		response336 = selfSiloMovementService.executeService336(response258, reqModel, bindingResult);
		selfSiloMovementService.executeService325(response324, response336, reqModel, req);
		response256List = selfSiloMovementService.executeService256Create(response256List, response258, response336, reqModel, req, res);
		req.setAttribute("silo-movement", reqModel.getFromSilo());
		RetrieveDetailsAndArrangementRelationshipsForIPsResponse updateResponse258 = selfSiloMovementService.executeService258(reqModel);
		response256List = selfSiloMovementService.executeService256Delete(response256List, updateResponse258, response336, reqModel, req, res);

		logger.info("Calling ServiceOpsGCMController.siloMovement() Complete");

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MaintainArrangementAndIPArrangementRelationshipsResponse> executeService256Delete(
			List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List,
			RetrieveDetailsAndArrangementRelationshipsForIPsResponse response258, CreateIndividualIPResponse response336,
			SiloMovementReqModel reqModel, HttpServletRequest req, HttpServletResponse res) throws SiloMovementException {
		if (response336 != null) {
			if (null != response336.getIndividual() && null != response336.getIndividual().getInvolvedPartyIdentifier()
					&& !response336.getIndividual().getInvolvedPartyIdentifier().isEmpty()) {
				if (null != response258 && null != response258.getIndividual() && null != response258.getIndividual().getIsPlayingRoleInArrangement()
						&& !response258.getIndividual().getIsPlayingRoleInArrangement().isEmpty()) {

					for (InvolvedPartyArrangementRole arrangements : response258.getIndividual().getIsPlayingRoleInArrangement()) {
						if (arrangements.getHasForContext().getIsBasedOn().getProductSystemIdentifier().getProductSystemId()
								.equalsIgnoreCase(ACCOUNT_PREFIX)) {
							MaintainArrangementAndRelationshipReqModel reqModel256 = requestBuilder.get256ReqModel(reqModel.getKey(), reqModel
									.getPersonType().name(), arrangements, "delete");
							reqModel256.setSilo(reqModel.getFromSilo());
							ModelAndView modelAndView256 = serviceOpsGcmController.maintainArrangementsAndRelashinship(reqModel256, req, res);
							ModelMap modlMap256 = modelAndView256.getModelMap();
							List<CustomerRawData> customerDataList256 = (List<CustomerRawData>) modlMap256.get(Attribute.SERVICE_OPS_MODEL);
							if (null != customerDataList256 && !customerDataList256.isEmpty()) {
								response256List.add((MaintainArrangementAndIPArrangementRelationshipsResponse) customerDataList256.get(0)
										.getResponseObject());
							}
							logger.info("Response for Service 256 End_Date_IP_AR:" + customerDataList256.get(0).getRawResponse());
						} else {
							throw new SiloMovementException(SERVICE_256_STATUS_END, "No matching usecase for service 256");
						}
					}
				} else {
					throw new SiloMovementException(SERVICE_258_STATUS, "Not able to fetch the arrangement and relationships(258) for "
							+ reqModel.getKey() + " key from " + reqModel.getFromSilo() + " silo.");
				}
			}
		} else {
			throw new SiloMovementException(SERVICE_336_STATUS, "Not able to create the individual.");
		}
		return response256List;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MaintainArrangementAndIPArrangementRelationshipsResponse> executeService256Create(
			List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List,
			RetrieveDetailsAndArrangementRelationshipsForIPsResponse response258, CreateIndividualIPResponse response336,
			SiloMovementReqModel reqModel, HttpServletRequest req, HttpServletResponse res) throws SiloMovementException {
		if (response336 != null) {
			if (null != response336.getIndividual() && null != response336.getIndividual().getInvolvedPartyIdentifier()
					&& !response336.getIndividual().getInvolvedPartyIdentifier().isEmpty()) {

				String cisKey = response336.getIndividual().getInvolvedPartyIdentifier().get(0).getInvolvedPartyId();
				if (null != response258 && null != response258.getIndividual() && null != response258.getIndividual().getIsPlayingRoleInArrangement()
						&& !response258.getIndividual().getIsPlayingRoleInArrangement().isEmpty()) {

					for (InvolvedPartyArrangementRole arrangements : response258.getIndividual().getIsPlayingRoleInArrangement()) {
						if (arrangements.getHasForContext().getIsBasedOn().getProductSystemIdentifier().getProductSystemId()
								.equalsIgnoreCase(ACCOUNT_PREFIX)) {
							MaintainArrangementAndRelationshipReqModel reqModel256 = requestBuilder.get256ReqModel(cisKey, reqModel.getPersonType()
									.name(), arrangements, "create");

							ModelAndView modelAndView256 = serviceOpsGcmController.maintainArrangementsAndRelashinship(reqModel256, req, res);
							ModelMap modlMap256 = modelAndView256.getModelMap();
							List<CustomerRawData> customerDataList256 = (List<CustomerRawData>) modlMap256.get(Attribute.SERVICE_OPS_MODEL);
							if (null != customerDataList256 && !customerDataList256.isEmpty()) {
								response256List.add((MaintainArrangementAndIPArrangementRelationshipsResponse) customerDataList256.get(0)
										.getResponseObject());
							}
							logger.info("Response for Service 256 IP_AR:" + customerDataList256.get(0).getRawResponse());
						} else {
							throw new SiloMovementException(SERVICE_256_STATUS_CRT, "No matching usecase for service 256");
						}
					}
				} else {
					throw new SiloMovementException(SERVICE_258_STATUS, "Not able to fetch the arrangement and relationships(258) for "
							+ reqModel.getKey() + " key from " + reqModel.getFromSilo() + " silo.");
				}
			}
		} else {
			throw new SiloMovementException(SERVICE_336_STATUS, "Not able to create the individual.");
		}
		return response256List;

	}

	@Override
	@SuppressWarnings("unchecked")
	public MaintainIDVDetailsResponse executeService325(RetrieveIDVDetailsResponse response324, CreateIndividualIPResponse response336,
			SiloMovementReqModel reqModel, HttpServletRequest req) throws SiloMovementException {
		MaintainIDVDetailsResponse response325 = null;
		if (null != response324) {
			logger.info("Calling Service 325");
			MaintainIdvDetailReqModel reqModel325 = requestBuilder.get325ReqModel(response324, response336);
			if (null == reqModel325.getCisKey() || StringUtils.isBlank(reqModel325.getCisKey())) {
				throw new SiloMovementException(SERVICE_336_STATUS, "Not able to create CIS key ");
			}
			ModelAndView modelAndView325 = serviceOpsGcmSiloController.maintainIdvDetails(reqModel325, req);
			ModelMap modlMap325 = modelAndView325.getModelMap();
			List<CustomerRawData> customerDataList325 = (List<CustomerRawData>) modlMap325.get(Attribute.SERVICE_OPS_MODEL);

			if (null != customerDataList325 && !customerDataList325.isEmpty()) {
				response325 = (MaintainIDVDetailsResponse) customerDataList325.get(0).getResponseObject();
				logger.info("Response for Service 324:" + response324.toString());
			} else {
				throw new SiloMovementException(SERVICE_325_STATUS, "Not able to maintain Individual Details in " + reqModel.getToSilo() + " silo.");
			}
		} else {
			throw new SiloMovementException(SERVICE_258_STATUS, "Not able to fetch the arrangement and relationships(258) for " + reqModel.getKey()
					+ " key from " + reqModel.getFromSilo() + " silo.");

		}

		return response325;
	}

	@Override
	@SuppressWarnings("unchecked")
	public RetrieveIDVDetailsResponse executeService324(SiloMovementReqModel reqModel, BindingResult bindingResult) throws SiloMovementException {
		RetrieveIDVDetailsResponse response324 = null;
		// Calling service 258 for getting data
		logger.info("Calling Service 324");
		RetrieveIDVDetailsReqModel reqModel324 = requestBuilder.get324ReqModel(reqModel.getKey(), reqModel.getPersonType().name(),
				reqModel.getFromSilo());
		ModelAndView modelAndView258 = serviceOpsGcmController.retrieveIDVDetails(reqModel324, bindingResult);
		ModelMap modlMap258 = modelAndView258.getModelMap();
		List<CustomerRawData> customerDataList324 = (List<CustomerRawData>) modlMap258.get(Attribute.SERVICE_OPS_MODEL);

		if (null != customerDataList324 && !customerDataList324.isEmpty()) {
			response324 = (RetrieveIDVDetailsResponse) customerDataList324.get(0).getResponseObject();
			logger.info("Response for Service 324:" + response324.toString());
		} else {
			throw new SiloMovementException(SERVICE_258_STATUS, "Not able to fetch the arrangement and relationships(258) for " + reqModel.getKey()
					+ " key from " + reqModel.getFromSilo() + " silo.");
		}
		return response324;
	}

	@Override
	@SuppressWarnings("unchecked")
	public RetrieveDetailsAndArrangementRelationshipsForIPsResponse executeService258(SiloMovementReqModel reqModel) throws SiloMovementException {
		RetrieveDetailsAndArrangementRelationshipsForIPsResponse response258 = null;
		// Calling service 258 for getting data
		logger.info("Calling Service 258");
		String[] operationType = getAllCustomerManagementOperation();
		ModelAndView modelAndView258 = serviceOpsGcmController.searchClientDetails(reqModel.getKey(), reqModel.getPersonType(), operationType,
				reqModel.getFromSilo());
		ModelMap modlMap258 = modelAndView258.getModelMap();
		List<CustomerRawData> customerDataList258 = (List<CustomerRawData>) modlMap258.get(Attribute.SERVICE_OPS_MODEL);

		if (null != customerDataList258 && !customerDataList258.isEmpty()) {
			response258 = (RetrieveDetailsAndArrangementRelationshipsForIPsResponse) customerDataList258.get(0).getResponseObject();
			logger.info("Response for Service 258:" + response258.toString());
		} else {
			throw new SiloMovementException(SERVICE_258_STATUS, "Not able to fetch the arrangement and relationships(258) for " + reqModel.getKey()
					+ " key from " + reqModel.getFromSilo() + " silo.");
		}
		return response258;
	}

	private String[] getAllCustomerManagementOperation() {
		ClientDetailsOperation[] operationTypes = ClientDetailsOperation.values();
		String[] operationTypesArray = new String[operationTypes.length];
		for (int i = 0; i < operationTypes.length; i++) {
			operationTypesArray[i] = operationTypes[i].name();
		}
		return operationTypesArray;
	}

	@Override
	@SuppressWarnings("unchecked")
	public CreateIndividualIPResponse executeService336(RetrieveDetailsAndArrangementRelationshipsForIPsResponse response258,
			SiloMovementReqModel reqModel, BindingResult bindingResult) throws SiloMovementException {
		CreateIndividualIPResponse response336 = null;
		if (response258 != null) {
			logger.info("Calling Service 336");
			CreateIndividualIPReqModel reqModel336 = requestBuilder.get336ReqModel(response258, reqModel.getPersonType(), reqModel.getToSilo());
			CreateIndividualIPEmailPhoneContactMethodsReqModel reqModel336EmailPhone = requestBuilder.get336EmailPhoneReqModel(response258,
					reqModel.getPersonType(), reqModel.getToSilo());
			ModelAndView modelAndView336 = createIndividualIpController.createIndividualIP(reqModel336,reqModel336EmailPhone, bindingResult);
			ModelMap modlMap336 = modelAndView336.getModelMap();
			List<CustomerRawData> customerDataList336 = (List<CustomerRawData>) modlMap336.get(Attribute.SERVICE_OPS_MODEL);
			if (null != customerDataList336 && !customerDataList336.isEmpty()) {
				response336 = (CreateIndividualIPResponse) customerDataList336.get(0).getResponseObject();
				logger.info("Response for Service 336:" + response336.toString());
			} else {
				throw new SiloMovementException(SERVICE_336_STATUS, "Not able to create the individual from " + reqModel.getKey() + " key from "
						+ reqModel.getFromSilo() + " silo to " + reqModel.getToSilo());
			}
		} else {
			throw new SiloMovementException(SERVICE_258_STATUS, "Not able to fetch the arrangement and relationships(258) for " + reqModel.getKey()
					+ " key from " + reqModel.getFromSilo() + " silo.");
		}
		return response336;
	}
}
