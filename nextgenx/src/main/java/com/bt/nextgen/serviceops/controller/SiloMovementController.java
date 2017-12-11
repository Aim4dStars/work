package com.bt.nextgen.serviceops.controller;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bt.nextgen.api.client.service.MaintainSiloMovementStatusService;
import com.bt.nextgen.api.client.service.SiloMovementService;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.serviceops.model.SiloMovementReqModel;
import com.bt.nextgen.serviceops.model.SiloMovementStatusModel;
import com.bt.nextgen.serviceops.service.ServiceOpsService;
import com.bt.nextgen.serviceops.silomovement.aspect.SiloMovementStatusAspect;
import com.bt.nextgen.silomovement.exception.SiloMovementException;

/**
 * Created by L091297 on 08/06/2017.
 */
@SuppressWarnings("squid:S1200")
// Single Responsibility Principle
@Controller("selfSiloMovementController")
public class SiloMovementController {

	private static final Logger logger = LoggerFactory.getLogger(ServiceOpsController.class);

	private static final String SILO_MOVEMENT_RESPONSE = "siloMovementResponse";
	
	@Autowired
	private ServiceOpsService serviceOpsService;

	@Autowired
	@Qualifier("maintainSiloMovementStatusService")
	private MaintainSiloMovementStatusService maintainSiloMovementStatusService;

	@Autowired
	@Qualifier("siloMovementService")
	private SiloMovementService siloMovementService;

	@InitBinder("reqModel")
	public void siloMovementReqModelBinder(WebDataBinder binder) {
		binder.setAllowedFields("key", "fromSilo", "toSilo", "personType");
	}

	@InitBinder("siloMovementStatusModel")
	public void siloMovementStatusModelBinder(WebDataBinder binder) {
		binder.setAllowedFields("appId", "userId", "datetimeStart", "datetimeEnd", "oldCis", "newCis", "fromSilo", "toSilo", "lastSuccState",
				"errState", "errMsg");
	}

	@RequestMapping(value = "/secure/page/serviceOps/siloMovementReq", method = RequestMethod.GET)
	public String siloMovementReq() {
		if (serviceOpsService.isServiceOpsITSupportRole())
			return View.SILO_MOVEMENT;
		else
			return View.ERROR;
	}

	@RequestMapping(value = "/secure/page/serviceOps/siloMovement", method = RequestMethod.POST)
	public ModelAndView siloMovement(@ModelAttribute("reqModel") SiloMovementReqModel reqModel, HttpServletRequest req, HttpServletResponse res,
			BindingResult bindingResult) throws SiloMovementException {

		String view = View.SILO_MOVEMENT;
		if (!serviceOpsService.isServiceOpsITSupportRole()) {
			view = View.ERROR;
			return new ModelAndView(view);
		}
		logger.info("Calling ServiceOpsGCMController.siloMovement()");

		siloMovementService.siloMovement(reqModel, bindingResult, req, res);

		ModelAndView modelAndView = new ModelAndView(view).addObject("isRestricted", serviceOpsService.isServiceOpsRestricted()).addObject("key",
				SiloMovementStatusAspect.getSiloMovementStatusModel().getAppId());

		return modelAndView;
	}

	@RequestMapping(value = "/secure/page/serviceOps/siloMovementTracking", method = RequestMethod.GET)
	public ModelAndView siloMovementTracking(@ModelAttribute("siloMovementStatusModel") SiloMovementStatusModel siloMovementStatusModel) {
		String view = View.SILO_MOVEMENT_RESPONSE;
		ModelAndView modelAndView = new ModelAndView(view);
		if (!serviceOpsService.isServiceOpsITSupportRole()) {
			view = View.ERROR;
			modelAndView.setViewName(view);
			return modelAndView;
		}
		modelAndView.addObject("siloMovementList", maintainSiloMovementStatusService.retrieveAll(siloMovementStatusModel));
		modelAndView.addObject("oldCis", siloMovementStatusModel.getOldCis());
		modelAndView.addObject("datetimeStart", siloMovementStatusModel.getDatetimeStart());
		modelAndView.addObject("datetimeEnd", siloMovementStatusModel.getDatetimeEnd());
		return modelAndView;
	}

	@RequestMapping(value = "/secure/page/serviceOps/siloMovementResponse", method = RequestMethod.POST)
	public @ResponseBody
	SiloMovementStatusModel bulkupdateResponse(@RequestParam Long key) {
		if (!serviceOpsService.isServiceOpsITSupportRole()) {
			throw new AccessDeniedException("Access denied: " + SILO_MOVEMENT_RESPONSE);
		}
		return maintainSiloMovementStatusService.retrieve(key);
	}
}
