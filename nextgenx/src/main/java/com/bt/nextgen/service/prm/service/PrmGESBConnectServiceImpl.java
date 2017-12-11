package com.bt.nextgen.service.prm.service;

import au.com.westpac.gn.riskmanagement.services.riskmonitoring.xsd.notifyeventforfraudassessment.v1.svc0525.NotifyEventForFraudAssessmentRequest;
import au.com.westpac.gn.riskmanagement.services.riskmonitoring.xsd.notifyeventforfraudassessment.v1.svc0525.NotifyEventForFraudAssessmentResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;
import com.bt.nextgen.config.WebServiceProviderConfig;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.service.ErrorHandlerUtil;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.service.prm.pojo.PrmDto;
import com.bt.nextgen.service.prm.util.PrmUtil;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * Created by l081361 on 7/09/2016.
 */

@Service
@EnableAsync
public class PrmGESBConnectServiceImpl implements PrmGESBConnectService {

    private final Logger logger = LoggerFactory.getLogger(PrmGESBConnectServiceImpl.class);

    @Autowired
    private WebServiceProvider provider;

    @Autowired
    @Qualifier("serverAuthorityService")
    private BankingAuthorityService userSamlService;

    @Override
    @Async
    public Future<Void> submitRequest(PrmDto prmDto){
        ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();

        submitRequestPrimary(prmDto, serviceErrors);

        if (serviceErrors.hasErrors()) {
            logger.error("Failed to submit PRM events: {}", serviceErrors.getErrorMessagesForScreenDisplay());
        }

        return  null;
    }

    public void submitRequestPrimary(PrmDto prmDto, ServiceErrors serviceErrors) {

        NotifyEventForFraudAssessmentRequest notifyEventForFraudAssessmentRequest =  PrmUtil.getGESBRequest(prmDto);
        CorrelatedResponse correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(userSamlService.getSamlToken(), WebServiceProviderConfig.GROUP_ESB_PRM.getConfigName(), notifyEventForFraudAssessmentRequest, serviceErrors);

        if(null != correlatedResponse) {

            NotifyEventForFraudAssessmentResponse response = (NotifyEventForFraudAssessmentResponse) correlatedResponse.getResponseObject();

            boolean isFailure = false;

            if (null != response && null != response.getServiceStatus()) {
                for (StatusInfo statusInfo : response.getServiceStatus().getStatusInfo()) {
                    if (!statusInfo.getLevel().toString().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
                        isFailure = true;
                    }
                }
            }

            if (isFailure) {
                ErrorHandlerUtil.parseErrors(response.getServiceStatus(),
                        serviceErrors,
                        ServiceConstants.SERVICE_0525,
                        correlatedResponse.getCorrelationIdWrapper());
            }
        }
    }
}
