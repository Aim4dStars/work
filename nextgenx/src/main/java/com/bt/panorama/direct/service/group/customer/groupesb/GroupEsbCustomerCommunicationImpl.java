package com.bt.panorama.direct.service.group.customer.groupesb;

import au.com.westpac.gn.communicationmanagement.services.communicationdispatch.xsd.generatecommunicationdetails.v1.svc0019.GenerateCommunicationDetailsRequest;
import au.com.westpac.gn.communicationmanagement.services.communicationdispatch.xsd.generatecommunicationdetails.v1.svc0019.GenerateCommunicationDetailsResponse;
import au.com.westpac.gn.communicationmanagement.services.communicationdispatch.xsd.generatecommunicationdetails.v1.svc0019.ObjectFactory;
import com.bt.nextgen.config.WebServiceProviderConfig;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.service.ErrorHandlerUtil;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.bt.panorama.direct.api.email.model.PortfolioDetailDto;
import com.bt.panorama.direct.service.group.customer.CustomerCommunicationIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class GroupEsbCustomerCommunicationImpl implements CustomerCommunicationIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(GroupEsbCustomerCommunicationImpl.class);

    @Autowired
    private WebServiceProvider provider;

    @Resource(name = "serverAuthorityService")
    private BankingAuthorityService userSamlService;

    public GroupEsbCustomerCommunicationAdapter generateEmailCommunication(PortfolioDetailDto portfolioDetailDto, ServiceErrors serviceErrors){
        logger.info("GroupEsbCustomerCommunicationImpl.generateEmailCommunication() : Sending email request");

        GenerateCommunicationDetailsRequest request =  buildRequest(portfolioDetailDto);

        CorrelatedResponse correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(userSamlService.getSamlToken(),
                WebServiceProviderConfig.GROUP_ESB_GENERATE_COMMUNICATION_DETAILS.getConfigName(),
                request,
                serviceErrors);
        GenerateCommunicationDetailsResponse response = (GenerateCommunicationDetailsResponse)correlatedResponse.getResponseObject();
        logger.info("GroupEsbCustomerCommunicationImpl.generateEmailCommunication() : Successfully returning service status.");

        GroupEsbCustomerCommunicationAdapter result = new GroupEsbCustomerCommunicationAdapter(response, serviceErrors);
        if (!result.getServiceLevel().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE))
        {
            ErrorHandlerUtil.parseErrors(response.getServiceStatus(),
                    serviceErrors,
                    ServiceConstants.SERVICE_019,
                    correlatedResponse.getCorrelationIdWrapper());
        }
        return result;
    }

    private GenerateCommunicationDetailsRequest buildRequest(PortfolioDetailDto portfolioDetailDto){
        ObjectFactory of = new ObjectFactory();
        GenerateCommunicationDetailsRequest request =  of.createGenerateCommunicationDetailsRequest();

        request.getCommunication().add(GroupEsbRequestUtils.makeCommunicationObject(portfolioDetailDto));

        return request;
    }
}
