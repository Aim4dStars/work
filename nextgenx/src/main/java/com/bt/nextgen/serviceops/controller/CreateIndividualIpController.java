package com.bt.nextgen.serviceops.controller;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.servlet.ModelAndView;

import com.bt.nextgen.api.client.service.CreateIndividualIPDtoService;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.serviceops.model.CreateIndividualIPEmailPhoneContactMethodsReqModel;
import com.bt.nextgen.serviceops.model.CreateIndividualIPReqModel;
import com.bt.nextgen.serviceops.service.ServiceOpsService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.util.StringUtil;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@Controller
public class CreateIndividualIpController {
	private static final Logger logger = LoggerFactory.getLogger(CreateIndividualIpController.class);
	@Autowired
	@Qualifier("createindividualipdtoservice")
	private CreateIndividualIPDtoService createIndividualIPDtoService;

	@Autowired
	private ServiceOpsService serviceOpsService;

	@InitBinder("createIndividualIPReqModel")
	public void createIndividualIPBinder(WebDataBinder binder) {
		binder.setAllowedFields("silo", "prefix", "firstName", "lastName", "altName", "gender", "birthDate", "foreignRegistered", "roleType",
				"purposeOfBusinessRelationship", "sourceOfFunds", "sourceOfWealth", "usage", "addresseeNameText", "addressType", "streetNumber",
				"streetName", "streetType", "city", "state", "postCode", "country", "registrationIdentifierNumber",
				"registrationIdentifierNumberType", "hasLoansWithOtherBanks", "middleNames", "preferredName", "alternateName", "isPreferred",
				"employmentType", "occupationCode", "registrationArrangementsRegistrationNumber", "registrationArrangementsRegistrationNumberType",
				"registrationArrangementsCountry", "registrationArrangementsState", "addressLine1", "addressLine2", "addressLine3", "floorNumber",
				"unitNumber", "buildingName");
	}

	@RequestMapping(value = "/secure/page/serviceOps/createIndividualIPReq", method = RequestMethod.GET)
	public String createIndividualIPReq() {

		if (!serviceOpsService.isServiceOpsITSupportRole()) {
			return View.ERROR;
		}

		return View.CREATE_INDIVIDUAL_IP;
	}

	@RequestMapping(value = "/secure/page/serviceOps/createIndividualIP", method = RequestMethod.POST)
	public ModelAndView createIndividualIP(@ModelAttribute CreateIndividualIPReqModel reqModel, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			logger.info("createIndividualIP request contains input errors", reqModel.toString());
			return new ModelAndView(View.ERROR);
		}
		if (!serviceOpsService.isServiceOpsITSupportRole()) {
			return new ModelAndView(View.ERROR);
		}

		return createIndividual(reqModel, null);
	}

	public ModelAndView createIndividualIP(CreateIndividualIPReqModel reqModel,
			CreateIndividualIPEmailPhoneContactMethodsReqModel contactMethodsReqModel, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			logger.info("createIndividualIP request contains input errors", reqModel.toString());
			return new ModelAndView(View.ERROR);
		}
		if (!serviceOpsService.isServiceOpsITSupportRole()) {
			return new ModelAndView(View.ERROR);
		}

		return createIndividual(reqModel, contactMethodsReqModel);
	}

	private ModelAndView createIndividual(CreateIndividualIPReqModel reqModel,
			CreateIndividualIPEmailPhoneContactMethodsReqModel contactMethodsReqModel) {
		List<CustomerRawData> customerDataList = new ArrayList<CustomerRawData>();
		CustomerRawData customerRawData = null;

		ModelAndView modelAndView = new ModelAndView(View.CREATE_INDIVIDUAL_IP, Attribute.SERVICE_OPS_MODEL, customerDataList).addObject(
				"isRestricted", serviceOpsService.isServiceOpsRestricted());

		if (StringUtil.isNotNullorEmpty(reqModel.getFirstName())) {
			ServiceErrors serviceErrors = new ServiceErrorsImpl();
			customerRawData = createIndividualIPDtoService.create(reqModel, contactMethodsReqModel, serviceErrors);
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