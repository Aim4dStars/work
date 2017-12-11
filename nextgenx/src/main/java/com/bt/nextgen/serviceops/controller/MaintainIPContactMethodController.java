package com.bt.nextgen.serviceops.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import com.bt.nextgen.api.client.service.CustomerDataDtoService;
import com.bt.nextgen.api.client.service.MaintainIpContactDtoService;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.serviceops.model.MaintainIPContactMethodModel;
import com.bt.nextgen.serviceops.service.ServiceOpsService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@Controller
public class MaintainIPContactMethodController {
	
	@Autowired
	private ServiceOpsService serviceOpsService;

	@Autowired
	private CustomerDataDtoService customerDataDtoService;
	
	@Autowired
	@Qualifier("maintainipcontactdtoservice")
	private MaintainIpContactDtoService maintainIpContactDtoService;

	private static final Logger logger = LoggerFactory
			.getLogger(MaintainIPContactMethodController.class);
	
	 @InitBinder("maintainIPContactMethodModel")
	    public void cmaintainIpContactMethodBinder(WebDataBinder binder) {
	        binder.setAllowedFields("silo ", "personType","cisKey","requestedAction","addressType","usageId","validityStatus",
	                "priorityLevel","emailAddress","countryCode","areaCode","localNumber","contactMedium");
	    }
	@RequestMapping(value = "/secure/page/serviceOps/maintainIpContactMethodReq", method = RequestMethod.GET)
	public String maintainIpContactMethod() {
		 if (!serviceOpsService.isServiceOpsITSupportRole())
	        {
	            return View.ERROR;
	        }

	        return View.MAINTAIN_IP_CONTACT_METHOD;
}
	
	@RequestMapping(value = "/secure/page/serviceOps/maintainIpContactMethod", method = RequestMethod.POST)
	public ModelAndView maintainIpContactMethod(
			@ModelAttribute ("maintainIPContactMethodModel") MaintainIPContactMethodModel maintainIPContactMethodModel,
			HttpServletRequest req ,  BindingResult bindingResult) {
	    if (bindingResult.hasErrors()) {
            logger.info("maintainIpContactMethod request contains input errors",  maintainIPContactMethodModel.toString());
            return new ModelAndView(View.ERROR);
        }
		if (!serviceOpsService.isServiceOpsITSupportRole())
			return new ModelAndView(View.ERROR);
		List<CustomerRawData> customerDataList = new ArrayList<CustomerRawData>();
		CustomerRawData customerRawData = null;

		ModelAndView modelAndView = new ModelAndView(View.MAINTAIN_IP_CONTACT_METHOD,
				Attribute.SERVICE_OPS_MODEL, customerDataList).addObject(
				"isRestricted", serviceOpsService.isServiceOpsRestricted());

		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		customerRawData = maintainIpContactDtoService.maintain(maintainIPContactMethodModel, serviceErrors);
		customerDataList.add(customerRawData);
		if (serviceErrors.hasErrors()) {
			modelAndView.addObject("serviceError", serviceErrors.getError("")
					.getReason());
		}
		modelAndView.setViewName(View.GCM_SERVICEOPS_RESPONSE);
		modelAndView.addObject(Attribute.SERVICE_OPS_MODEL, customerDataList);
		return modelAndView;
	}
}
