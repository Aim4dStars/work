package com.bt.panorama.direct.api.email.controller;

import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.panorama.direct.api.email.model.PortfolioDetailDto;
import com.bt.panorama.direct.api.email.service.SendPortfolioDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(method = RequestMethod.GET, value = { "/public/direct", "/secure/direct" }, produces = "application/json")
public class SendPortfolioDetailApiController {

    private static final Logger logger = LoggerFactory.getLogger(SendPortfolioDetailApiController.class);

    @Autowired
    private SendPortfolioDetailsService sendPortfolioDetailsService;

    @RequestMapping(method = RequestMethod.POST, value = "/sendEmail", produces = "application/json")
    public
    @ResponseBody
    AjaxResponse emailPortfolioDetails(@ModelAttribute PortfolioDetailDto portfolioDetailDto){
        if(portfolioDetailDto.getEmail() != null && !portfolioDetailDto.getEmail().isEmpty()
                && portfolioDetailDto.getCustomerName() != null && !portfolioDetailDto.getCustomerName().isEmpty()) {
            ServiceErrors serviceErrors = new ServiceErrorsImpl();

            boolean isSent = sendPortfolioDetailsService.sendPortfolioDetails(portfolioDetailDto, serviceErrors);
            if( isSent ) {
                logger.info("Email generated successfully.");
                return new AjaxResponse(true, "success");
            } else {
                logger.info("Email Sending failed.");
                return new AjaxResponse(false, serviceErrors.getErrorList());
            }
        } else {
            logger.info("Mandatory parameter is missing.");
            return new AjaxResponse(false, "Mandatory parameter is missing");
        }

    }
}
