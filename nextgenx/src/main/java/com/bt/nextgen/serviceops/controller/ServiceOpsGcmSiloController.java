/**
 * 
 */
package com.bt.nextgen.serviceops.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.bt.nextgen.api.client.service.CustomerDataDtoService;
import com.bt.nextgen.api.client.service.MaintainIdvDetailDtoService;
import com.bt.nextgen.config.AttributeNames;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.serviceops.model.MaintainIdvDetailReqModel;
import com.bt.nextgen.serviceops.service.ServiceOpsService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

/**
 * @author L081050
 * 
 */
@Controller
public class ServiceOpsGcmSiloController {
	@Autowired
	private ServiceOpsService serviceOpsService;

	@Autowired
	private CustomerDataDtoService customerDataDtoService;
	@Autowired
	@Qualifier("maintainidvdetaildtoservice")
	private MaintainIdvDetailDtoService maintainIdvDetailDtoService;

	@RequestMapping(value = "/secure/page/serviceOps/maintainIdvDetailsReq", method = RequestMethod.GET)
	public ModelAndView maintainIdvDetailsReq() {
		List<AttributeNames> optAttribute = Arrays.asList(AttributeNames
				.values());

		if (serviceOpsService.isServiceOpsITSupportRole()) {
			ModelAndView modelAndView = new ModelAndView(
					View.MAINTAIN_IDV_DETAILS).addObject("isRestricted",
					serviceOpsService.isServiceOpsRestricted()).addObject(
					"optAttribute", optAttribute);
			return modelAndView;
		} else {
			ModelAndView modelAndView = new ModelAndView(View.ERROR);
			return modelAndView;
		}
	}

	@RequestMapping(value = "/secure/page/serviceOps/maintainIdvDetails", method = RequestMethod.POST)
	public ModelAndView maintainIdvDetails(
			@ModelAttribute MaintainIdvDetailReqModel reqModel,
			HttpServletRequest req) {

		if (!serviceOpsService.isServiceOpsITSupportRole())
			return new ModelAndView(View.ERROR);
		List<CustomerRawData> customerDataList = new ArrayList<CustomerRawData>();
		CustomerRawData customerRawData = null;

		ModelAndView modelAndView = new ModelAndView(View.MAINTAIN_IDV_DETAILS,
				Attribute.SERVICE_OPS_MODEL, customerDataList).addObject(
				"isRestricted", serviceOpsService.isServiceOpsRestricted());

		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		customerRawData = maintainIdvDetailDtoService.maintain(reqModel,
				serviceErrors);
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
