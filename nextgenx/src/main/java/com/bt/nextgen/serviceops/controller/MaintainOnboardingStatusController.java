package com.bt.nextgen.serviceops.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.bt.nextgen.api.client.service.MaintainOnboardingStatusService;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.repository.OnboardingApplicationStatus;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.serviceops.model.MaintainOnboardingStatusModel;
import com.bt.nextgen.serviceops.service.ServiceOpsService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.util.StringUtil;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
@Controller
public class MaintainOnboardingStatusController {

	private static final Logger logger = LoggerFactory.getLogger(ServiceOpsController.class);
	private static final String MAINTAIN_ONBOARDING_RESPONSE = "maintainOnboardingResponse";
	@Autowired
	private ServiceOpsService serviceOpsService;
	
	@Autowired
	@Qualifier("maintainOnboardingStatusService")
	private MaintainOnboardingStatusService maintainOnboardingStatusService;
	 @RequestMapping(value = "/secure/page/serviceOps/OnBoardingStatus", method = RequestMethod.GET)
	    public ModelAndView  getOnBoardingStatus(@RequestParam(required = false) String cisKey) {

	     if (!serviceOpsService.isServiceOpsITSupportRole()) {
             throw new AccessDeniedException("Access denied: " + MAINTAIN_ONBOARDING_RESPONSE);
         }

	        logger.info("Loading Onboarding status page", cisKey);

	        ModelAndView modelAndView = new ModelAndView(View.ONBOARDING_STATUS);

	        if (StringUtil.isNotNullorEmpty(cisKey)) {
	            ServiceErrors serviceErrors = new ServiceErrorsImpl();
	            if (serviceErrors.hasErrors()) {
	                modelAndView.addObject("serviceError", serviceErrors.getError("").getReason());
	            }
	            Long appId = Long.valueOf(cisKey);
	            MaintainOnboardingStatusModel maintainOnboardingStatusModel = maintainOnboardingStatusService.find(appId);
	           
	            modelAndView.setViewName(View.ONBOARDING_STATUS_RESPONSE);
	            modelAndView.addObject(Attribute.ONBOARDING_STATUS_MODEL, maintainOnboardingStatusModel);
	            modelAndView.addObject("statusType", OnboardingApplicationStatus.values());
	        }
	        return modelAndView;
	   
	    
	}
     @RequestMapping(value = "/secure/page/serviceOps/changeStatus", method = RequestMethod.POST)
        public ModelAndView  changeStatus(HttpServletRequest request) {
         logger.info("Updating Onboarding status");
         if (!serviceOpsService.isServiceOpsITSupportRole()) {
             throw new AccessDeniedException("Access denied: " + MAINTAIN_ONBOARDING_RESPONSE);
         }

            ModelAndView modelAndView = new ModelAndView(View.ONBOARDING_STATUS_RESPONSE);

                Long appId = Long.valueOf(request.getParameter("appId"));
                String status = request.getParameter("status");
                MaintainOnboardingStatusModel maintainOnboardingStatusModel = maintainOnboardingStatusService.find(appId);
                maintainOnboardingStatusModel.getOnBoardingApplication().setStatus(OnboardingApplicationStatus.valueOf(status));
                maintainOnboardingStatusService.update(maintainOnboardingStatusModel,appId);
                MaintainOnboardingStatusModel onboardingStatusModel = maintainOnboardingStatusService.find(appId);
                onboardingStatusModel = updateModel(onboardingStatusModel, status);
                modelAndView.addObject(Attribute.ONBOARDING_STATUS_MODEL, onboardingStatusModel);
                modelAndView.addObject("statusType", OnboardingApplicationStatus.values());
            
            return modelAndView;
       
        
    }
     private MaintainOnboardingStatusModel updateModel(MaintainOnboardingStatusModel maintainOnboardingStatusModel , String status){
         if(null != maintainOnboardingStatusModel.getOnBoardingApplication().getStatus() &&
             status.equalsIgnoreCase(maintainOnboardingStatusModel.getOnBoardingApplication().getStatus().toString())){
             maintainOnboardingStatusModel.setStatusMessage("success"); 
         }
         
         return maintainOnboardingStatusModel;
         
     }

}
