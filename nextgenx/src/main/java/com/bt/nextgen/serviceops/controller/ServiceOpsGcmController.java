package com.bt.nextgen.serviceops.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainarrangementandiparrangementrelationships.v1.svc0256.ActionCode;
import au.com.westpac.gn.locationmanagement.services.locationmanagement.xsd.retrievepostaladdress.v1.svc0454.AddressType;

import com.bt.nextgen.api.client.model.ClientUpdateKey;
import com.bt.nextgen.api.client.service.CustomerDataDtoService;
import com.bt.nextgen.api.client.service.IPToIPRelationshipsDataDtoService;
import com.bt.nextgen.api.client.service.MaintainArrangementAndRelationshipService;
import com.bt.nextgen.api.client.service.MaintainIpToIpRelationshipDTOService;
import com.bt.nextgen.api.client.service.RetriveIDVDetailsDataDtoService;
import com.bt.nextgen.api.client.service.RetrivePostalAddressDataDtoService;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.web.binding.EnumPropertyEditor;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.serviceops.model.MaintainArrangementAndRelationshipReqModel;
import com.bt.nextgen.serviceops.model.MaintainIpToIpRelationshipReqModel;
import com.bt.nextgen.serviceops.model.RetrieveIDVDetailsReqModel;
import com.bt.nextgen.serviceops.model.RetriveIpToIpRelationshipReqModel;
import com.bt.nextgen.serviceops.model.RetrivePostalAddressReqModel;
import com.bt.nextgen.serviceops.service.ServiceOpsService;
import com.bt.nextgen.userauthority.web.Action;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.util.StringUtil;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@Controller
public class ServiceOpsGcmController {

	private static final Logger logger = LoggerFactory.getLogger(ServiceOpsController.class);

	private static final String CIS_KEY = "cisKey";

	private static final String PERSON_TYPE = "personType";

	private static final String SILO = "silo";

	private static final String COLON = ":";

	private static final String EMPTY = " ";

	@Autowired
	private ServiceOpsService serviceOpsService;

	@Autowired
	private CustomerDataDtoService customerDataDtoService;

	@Autowired
	@Qualifier("maintainArrangementAndRelationship")
	private MaintainArrangementAndRelationshipService maintainArrangementAndRelationshipService;

	@Autowired
	@Qualifier("retriveIPToIPRelationshipDtoService")
	private IPToIPRelationshipsDataDtoService retriveIpToIpRelationshipDtoService;

	@Autowired
	@Qualifier("maintainIpToIpRelationship")
	private MaintainIpToIpRelationshipDTOService maintainIpToIpRelationshipDTOService;

	@Autowired
	@Qualifier("retriveIDVDetailsDtoService")
	private RetriveIDVDetailsDataDtoService retriveIDVDetailsDataDtoService;

	@Autowired
	@Qualifier("retrivePostalAddressDtoService")
	private RetrivePostalAddressDataDtoService retrivePostalAddressService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.registerCustomEditor(Action.class, new EnumPropertyEditor(Action.class));
	}

	 @InitBinder("maintainIpToIpRelationshipReqModel")
     public void maintainIpToIpRelationshipBinder(WebDataBinder binder) {
         binder.setAllowedFields("useCase", "sourcePersonType","sourceCISKey","partyRelType","silo","targetPersonType","targetCISKey",
                 "partyRelStatus","partyRelStartDate","partyRelEndDate","partyRelModNum","versionNumber");
     }
	 @InitBinder("retrieveIDVDetailsReqModel")
     public void retrieveIDVDetailsBinder(WebDataBinder binder) {
         binder.setAllowedFields("cisKey", "silo","personType");
     }
	@RequestMapping(value = "/secure/page/serviceOps/maintainArrangementAndrelationshipReq", method = RequestMethod.GET)
	public String maintainArrangementAndrelationshipReq() {
		if (serviceOpsService.isServiceOpsITSupportRole())
			return View.MAINTAIN_ARRANGEMENT_AND_RELATIONSHIP_REQ;
		else
			return View.ERROR;
	}

	@RequestMapping(value = "/secure/page/serviceOps/gcmHome", method = RequestMethod.GET)
	public String gcmServiceOpsHome() {
		if (serviceOpsService.isServiceOpsITSupportRole())
			return View.GCM_SERVICEOPS_HOME;
		else
			return View.ERROR;
	}

	@RequestMapping(value = "/secure/page/serviceOps/clientDetailsSerch", method = RequestMethod.GET)
	public ModelAndView searchClientDetails(@RequestParam(required = false) String cisKey, @RequestParam(required = false) RoleType roleType,
			@RequestParam(required = false) String[] operationTypes, @RequestParam(required = false) String silo) {

		if (!serviceOpsService.isServiceOpsITSupportRole())
			return new ModelAndView(View.ERROR);

		logger.info("Loading to search {}", cisKey);
		List<RoleType> roleList = new ArrayList<RoleType>();
		roleList.add(RoleType.INDIVIDUAL);
		roleList.add(RoleType.ORGANISATION);
		List<CustomerRawData> customerDataList = new ArrayList<CustomerRawData>();
		CustomerRawData customerRawData = null;

		ModelAndView modelAndView = new ModelAndView(View.CLIENTDETAILS_SEARCH, Attribute.SERVICE_OPS_MODEL, customerDataList).addObject(
				"isRestricted", serviceOpsService.isServiceOpsRestricted()).addObject("roleTypes", roleList);

		if (StringUtil.isNotNullorEmpty(cisKey)) {
			final ClientUpdateKey id = new ClientUpdateKey("", "", cisKey, roleType.name());
			ServiceErrors serviceErrors = new ServiceErrorsImpl();
			customerRawData = customerDataDtoService.retrieve(id, silo, operationTypes, serviceErrors);
			customerDataList.add(customerRawData);
			if (serviceErrors.hasErrors()) {
				modelAndView.addObject("serviceError", serviceErrors.getError("").getReason());
			}
			modelAndView.setViewName(View.GCM_SERVICEOPS_RESPONSE);
			modelAndView.addObject(Attribute.SERVICE_OPS_MODEL, customerDataList);
		}
		return modelAndView;
	}

	@RequestMapping(value = "/secure/page/serviceOps/maintainArrangementAndrelationship", method = RequestMethod.POST)
	public ModelAndView maintainArrangementsAndRelashinship(@ModelAttribute MaintainArrangementAndRelationshipReqModel reqModel,
			HttpServletRequest req, HttpServletResponse res) {
		if (!serviceOpsService.isServiceOpsITSupportRole())
			return new ModelAndView(View.ERROR);
		logger.info("Calling ServiceOpseController.maintainArrangementsAndRelashinship()");		StringBuilder strtDate = new StringBuilder();
		strtDate.append(reqModel.getStartDate()).append(EMPTY).append(reqModel.getHour()).append(COLON).append(reqModel.getMin()).append(COLON)
				.append(reqModel.getSec());
		reqModel.setStartDate(strtDate.toString());		List<CustomerRawData> customerDataList = new ArrayList<CustomerRawData>();
		CustomerRawData customerRawData = null;
		List<RoleType> roleList = new ArrayList<RoleType>();
		roleList.add(RoleType.INDIVIDUAL);
		roleList.add(RoleType.ORGANISATION);
		reqModel.setRequestedAction(ActionCode.MAINTAIN_ARRANGEMENT_AND_RELATIONSHIPS);
		ModelAndView modelAndView = new ModelAndView(View.MAINTAIN_ARRANGEMENT_AND_RELATIONSHIP, Attribute.SERVICE_OPS_MODEL, customerDataList)
				.addObject("isRestricted", serviceOpsService.isServiceOpsRestricted()).addObject("roleTypes", roleList);

		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		customerRawData = maintainArrangementAndRelationshipService.createArrangementAndRelationShip(reqModel, serviceErrors);
		customerDataList.add(customerRawData);
		if (null != customerDataList && !customerDataList.isEmpty()) {
			if (serviceErrors.hasErrors()) {
				logger.info("ServiceOpseController getting serviceError:" + serviceErrors.getError("").getReason());
				modelAndView.addObject("serviceError", serviceErrors.getError("").getReason());
			}
			modelAndView.setViewName(View.GCM_SERVICEOPS_RESPONSE);
			modelAndView.addObject(Attribute.SERVICE_OPS_MODEL, customerDataList);
		}
		return modelAndView;
	}

	@RequestMapping(value = "/secure/page/serviceOps/maintainIpToIpRelationshipReq", method = RequestMethod.GET)
	public String maintainIpToIpRelationship() {
		if (serviceOpsService.isServiceOpsITSupportRole()) {
			return View.MAINTAIN_IP_TO_IP_RELATIONSHIP;
		} else {
			return View.ERROR;
		}
	}

	@RequestMapping(value = "/secure/page/serviceOps/maintainIpToIpRelationship", method = RequestMethod.POST)
	public ModelAndView maintainIpToIpRelationship(
			@ModelAttribute ("maintainIpToIpRelationshipReqModel") MaintainIpToIpRelationshipReqModel reqModel,
			HttpServletRequest req, HttpServletResponse res, BindingResult bindingResult) {
	    if (bindingResult.hasErrors()) {
            logger.info("maintainIpToIpRelationship request contains input errors",  reqModel.toString());
            return new ModelAndView(View.ERROR);
        }		if (!serviceOpsService.isServiceOpsITSupportRole())
			return new ModelAndView(View.ERROR);
		logger.info("Calling ServiceOpseController.maintainIpToIpRelashinship()");
		List<CustomerRawData> customerDataList = new ArrayList<CustomerRawData>();
		CustomerRawData customerRawData = null;
		List<RoleType> roleList = new ArrayList<RoleType>();
		roleList.add(RoleType.INDIVIDUAL);
		roleList.add(RoleType.ORGANISATION);
		// reqModel.setRequestedAction(ActionCode.MAINTAIN_ARRANGEMENT_AND_RELATIONSHIPS);
		ModelAndView modelAndView = new ModelAndView(View.MAINTAIN_IP_TO_IP_RELATIONSHIP, Attribute.SERVICE_OPS_MODEL, customerDataList).addObject(
				"isRestricted", serviceOpsService.isServiceOpsRestricted()).addObject("roleTypes", roleList);

		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		customerRawData = maintainIpToIpRelationshipDTOService.maintainIpToIpRelationship(reqModel, serviceErrors);
		customerDataList.add(customerRawData);
		if (null != customerDataList && !customerDataList.isEmpty()) {
			if (serviceErrors.hasErrors()) {
				logger.info("ServiceOpseController getting serviceError:" + serviceErrors.getError("").getReason());
				modelAndView.addObject("serviceError", serviceErrors.getError("").getReason());
			}
			modelAndView.setViewName(View.GCM_SERVICEOPS_RESPONSE);
			modelAndView.addObject(Attribute.SERVICE_OPS_MODEL, customerDataList);

		}

		return modelAndView;

	}

	@RequestMapping(value = "/secure/page/serviceOps/retriveIpToIpRelationship", method = RequestMethod.GET)
	public ModelAndView retriveIpToIpRelationship(HttpServletRequest req) {

		if (!serviceOpsService.isServiceOpsITSupportRole())
			return new ModelAndView(View.ERROR);

		RetriveIpToIpRelationshipReqModel reqModel = setIpToIpRetriveReq(req);
		List<CustomerRawData> customerDataList = new ArrayList<CustomerRawData>();
		CustomerRawData customerRawData = null;

		ModelAndView modelAndView = new ModelAndView(View.RETRIVE_IP_TO_IP_RELATIONSHIPS, Attribute.SERVICE_OPS_MODEL, customerDataList).addObject(
				"isRestricted", serviceOpsService.isServiceOpsRestricted());

		if (StringUtil.isNotNullorEmpty(reqModel.getCisKey())) {
			ServiceErrors serviceErrors = new ServiceErrorsImpl();
			customerRawData = retriveIpToIpRelationshipDtoService.retrieve(reqModel, serviceErrors);
			customerDataList.add(customerRawData);
			if (serviceErrors.hasErrors()) {
				modelAndView.addObject("serviceError", serviceErrors.getError("").getReason());
			}
			modelAndView.setViewName(View.GCM_SERVICEOPS_RESPONSE);
			modelAndView.addObject(Attribute.SERVICE_OPS_MODEL, customerDataList);
		}
		return modelAndView;
	}
	@RequestMapping(value = "/secure/page/serviceOps/retrieveIDVDetails", method = RequestMethod.GET)
	public ModelAndView retrieveIDVDetails(
			@ModelAttribute ("retrieveIDVDetailsReqModel") RetrieveIDVDetailsReqModel reqModel ,  BindingResult bindingResult) {
	    
	    if (bindingResult.hasErrors()) {
            logger.info("retrieveIDVDetails request contains input errors",  reqModel.toString());
            return new ModelAndView(View.ERROR);
        }		if (!serviceOpsService.isServiceOpsITSupportRole())
			return new ModelAndView(View.ERROR);

		List<CustomerRawData> customerDataList = new ArrayList<CustomerRawData>();
		CustomerRawData customerRawData = null;

		ModelAndView modelAndView = new ModelAndView(View.RETRIVE_IDV_DETAILS, Attribute.SERVICE_OPS_MODEL, customerDataList).addObject(
				"isRestricted", serviceOpsService.isServiceOpsRestricted());

		if (StringUtil.isNotNullorEmpty(reqModel.getCisKey())) {
			ServiceErrors serviceErrors = new ServiceErrorsImpl();
			customerRawData = retriveIDVDetailsDataDtoService.retrieve(reqModel, serviceErrors);
			customerDataList.add(customerRawData);
			if (serviceErrors.hasErrors()) {
				modelAndView.addObject("serviceError", serviceErrors.getError("").getReason());
			}
			modelAndView.setViewName(View.GCM_SERVICEOPS_RESPONSE);
			modelAndView.addObject(Attribute.SERVICE_OPS_MODEL, customerDataList);
		}
		return modelAndView;
	}

	private RetriveIpToIpRelationshipReqModel setIpToIpRetriveReq(HttpServletRequest req) {
		RetriveIpToIpRelationshipReqModel reqModel = new RetriveIpToIpRelationshipReqModel();
		reqModel.setCisKey(req.getParameter(CIS_KEY));
		reqModel.setSilo(req.getParameter(SILO));
		reqModel.setRoleType(req.getParameter(PERSON_TYPE));

		return reqModel;
	}

	@RequestMapping(value = "/secure/page/serviceOps/retrivePostalAddress", method = RequestMethod.GET)
	public ModelAndView retrivePostalAddress(@ModelAttribute RetrivePostalAddressReqModel reqModel, HttpServletRequest req) {

		if (!serviceOpsService.isServiceOpsITSupportRole())
			return new ModelAndView(View.ERROR);

		List<CustomerRawData> customerDataList = new ArrayList<CustomerRawData>();
		CustomerRawData customerRawData = null;

		ModelAndView modelAndView = new ModelAndView(View.RETRIVE_POSTAL_ADDRESS, Attribute.SERVICE_OPS_MODEL, customerDataList)
				.addObject("isRestricted", serviceOpsService.isServiceOpsRestricted()).addObject("addressType", AddressType.values())
				.addObject("addressValidationQasApi", Properties.get("addressValidation.qasApi"));

		if (StringUtil.isNotNullorEmpty(reqModel.getKey())) {
			ServiceErrors serviceErrors = new ServiceErrorsImpl();
			customerRawData = retrivePostalAddressService.retrieve(reqModel, serviceErrors);
			customerDataList.add(customerRawData);
			if (serviceErrors.hasErrors()) {
				modelAndView.addObject("serviceError", serviceErrors.getError("").getReason());
			}
			modelAndView.setViewName(View.GCM_SERVICEOPS_RESPONSE);
			modelAndView.addObject(Attribute.SERVICE_OPS_MODEL, customerDataList);
		}
		return modelAndView;
	}
}
