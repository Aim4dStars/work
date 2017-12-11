package com.bt.panorama.direct.api.email.service;

import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.bt.panorama.direct.api.email.model.PortfolioDetailDto;
import com.bt.panorama.direct.service.group.customer.CustomerCommunicationIntegrationService;
import com.bt.panorama.direct.service.group.customer.groupesb.GroupEsbCustomerCommunicationAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class SendPortfolioDetailsServiceImpl implements SendPortfolioDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(SendPortfolioDetailsServiceImpl.class);

    @Autowired
    private CustomerCommunicationIntegrationService integrationService;

    /**
     * Generate emails to prospect including portfolio details. No need to resend email if there is a failure
     * as per requirement.
     * @param portfolioDetailDto
     * @param serviceErrors
     * @return
     */
    public boolean sendPortfolioDetails(PortfolioDetailDto portfolioDetailDto, ServiceErrors serviceErrors){
        boolean isSuccess = true;

        if(portfolioDetailDto.getEmail() != null  && !portfolioDetailDto.getEmail().isEmpty()) {
            logger.info("Sending email to prospect's email!" );
            isSuccess = sendEmail(portfolioDetailDto, serviceErrors);
        } else {
            isSuccess = false;
        }

        return isSuccess;
    }

    private boolean sendEmail(PortfolioDetailDto portfolioDetailDto, ServiceErrors serviceErrors){
        GroupEsbCustomerCommunicationAdapter result = integrationService.generateEmailCommunication(portfolioDetailDto, serviceErrors);
        if( null != result && !result.getServiceStatus().equals(Attribute.SUCCESS_MESSAGE) ){
            logger.info("Email Sending is failed ! " + portfolioDetailDto.getEmail());
            //log service errors
            logErrors(serviceErrors);
            return false;
        }

        return true;
    }

    private void logErrors(ServiceErrors serviceErrors){
        //TODO - error handling
        if(serviceErrors.hasErrors()) {
            Iterator<ServiceError> serviceError =  serviceErrors.getErrorList().iterator();
            while (serviceError.hasNext())
            {
                ServiceError error = serviceError.next();
                logger.error("SendPortfolioDetailsServiceImpl email service error: " + error.getReason());
            }
        }
    }
}
