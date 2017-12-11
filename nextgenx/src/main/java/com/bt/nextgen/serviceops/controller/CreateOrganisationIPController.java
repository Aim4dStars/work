/**
 * 
 */
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

import com.bt.nextgen.api.client.service.CreateOrganisationIPDataDtoService;
import com.bt.nextgen.api.client.service.CustomerDataDtoService;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.serviceops.model.CreateOraganisationIPReqModel;
import com.bt.nextgen.serviceops.service.ServiceOpsService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

/**
 * @author L081050
 */
@Controller
public class CreateOrganisationIPController {
    private static final Logger logger = LoggerFactory
            .getLogger(CreateOrganisationIPController.class);
    @Autowired
    private ServiceOpsService serviceOpsService;

    @Autowired
    private CustomerDataDtoService customerDataDtoService;

    @Autowired
    @Qualifier("createOrganisationIPDataDtoService")
    private CreateOrganisationIPDataDtoService CreateOrganisationIPDataDtoService;
    
    @InitBinder("createOraganisationIPReqModel")
    public void createOrganisationIPBinder(WebDataBinder binder) {
        binder.setAllowedFields("silo","fullName","personType","isForeignRegistered","registrationNumber","registrationNumberType",
                "isIssuedAtC","isIssuedAtS","startDate","industryCode","priorityLevel","addrspriorityLevel","usage","addresseeNameText",
                "addressType","streetNumber","streetName","streetType","city","state","postCode","country","organisationLegalStructureValue",
                "purposeOfBusinessRelationship","sourceOfFunds","sourceOfWealth","characteristicType","characteristicCode","characteristicValue",
                "frn","frntype","effectiveStartDate");
    }
    @RequestMapping(value = "/secure/page/serviceOps/createOrganisationIPReq", method = RequestMethod.GET)
    public String createOrganisationIPReq() {
        if (serviceOpsService.isServiceOpsITSupportRole()) {
            return View.CREATE_ORGANIGATION_IP;
        } else {
            return View.ERROR;
        }
    }

    @RequestMapping(value = "/secure/page/serviceOps/createOrganisationIP", method = RequestMethod.POST)
    public ModelAndView createOrganisationIP(@ModelAttribute  ("createOraganisationIPReqModel")  CreateOraganisationIPReqModel reqModel,
            HttpServletRequest req , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.info("createOrganisationIP request contains input errors",  reqModel.toString());
            return new ModelAndView(View.ERROR);
        }
        if (!serviceOpsService.isServiceOpsITSupportRole())
            return new ModelAndView(View.ERROR);

        List<CustomerRawData> customerDataList = new ArrayList<CustomerRawData>();
        CustomerRawData customerRawData = null;

        ModelAndView modelAndView =
                new ModelAndView(View.CREATE_ORGANIGATION_IP, Attribute.SERVICE_OPS_MODEL, customerDataList).addObject(
                        "isRestricted", serviceOpsService.isServiceOpsRestricted()).addObject("searchCriteria");

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        customerRawData = CreateOrganisationIPDataDtoService.create(reqModel, serviceErrors);
        customerDataList.add(customerRawData);
        if (serviceErrors.hasErrors()) {
            modelAndView.addObject("serviceError", serviceErrors.getError("").getReason());
        }
        modelAndView.setViewName(View.GCM_SERVICEOPS_RESPONSE);
        modelAndView.addObject(Attribute.SERVICE_OPS_MODEL, customerDataList);
        return modelAndView;
    }
}
