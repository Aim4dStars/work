package com.bt.nextgen.service.avaloq.additionalservices;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.draftaccount.service.OrderType;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.accountactivation.AccountApplicationImpl;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.accountactivation.AccountApplication;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.accountactivation.ApplicationIdentifier;
import com.bt.nextgen.service.integration.additionalservices.AdditionalServicesIntegrationService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import com.btfin.abs.trxservice.customer.v2_0.CustrRsp;
import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.core.AnyOf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;

@Service
public class AvaloqAdditionalServicesIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements AdditionalServicesIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(AvaloqAdditionalServicesIntegrationServiceImpl.class);

    @Autowired
    private AvaloqExecute avaloqExecute;

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    @Autowired
    private SubscriptionConverter subscriptionConverter;

    @Override
    public ApplicationDocument subscribe(final ServiceErrors serviceErrors, final ApplicationDocument applicationDocument) {
        return new IntegrationSingleOperation<ApplicationDocument>("submitSubscriptions", serviceErrors) {
            @Override
            public ApplicationDocument performOperation() {
                CustrRsp custrRsp = webserviceClient.sendToWebService(subscriptionConverter.createRequest(applicationDocument),
                        AvaloqOperation.CUSTR_REQ2,
                        serviceErrors);
                return subscriptionConverter.parseResponse(custrRsp, applicationDocument.getOrderType(), serviceErrors);
            }
        }.run();

    }

    @Override
    public List<ApplicationDocument> loadApplications(ServiceErrors serviceErrors, WrapAccountIdentifier... wrapAccountList) {
        try {
            List<String> portfolioIdList = Lambda.extract(wrapAccountList, (Lambda.on(WrapAccountIdentifier.class)).getAccountIdentifier());
            AccountApplication applications = avaloqExecute.executeReportRequestToDomain(new AvaloqReportRequest(Template.ACC_ACTIV_STATUS_BP.getName()).forBpList(portfolioIdList),
                    AccountApplicationImpl.class,
                    serviceErrors);
            List<ApplicationDocument> emptyList = Collections.emptyList();
            return CollectionUtils.isEmpty(applications.getApplication()) ? emptyList : applications.getApplication();
        } catch (ServiceException e) {
            logger.error("Exception avaloq service :" + Template.ACC_ACTIV_STATUS_BP.getName(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<ApplicationDocument> loadApplications(ServiceErrors serviceErrors, List<ApplicationIdentifier> identifierList) {
        try {
            //Evaluate the input docIdList
            List<String> docIdList = Lambda.extract(identifierList, Lambda.on(ApplicationIdentifier.class).getDocId());
            AccountApplication applications = avaloqExecute.executeReportRequestToDomain(new AvaloqReportRequest(Template.ACC_ACTIV_STATUS.getName()).forAvokaAppNum(docIdList), AccountApplicationImpl.class, serviceErrors);

            List<ApplicationDocument> applicationDocuments = Lambda.select(applications.getApplication(), Lambda.having(Lambda.on(ApplicationDocument.class).getOrderType(),
                    equalTo(OrderType.FundAdmin.getOrderType())));
            return applicationDocuments;
            /*List<ApplicationDocument> emptyList = Collections.emptyList();
            return CollectionUtils.isEmpty(applications.getApplication()) ? emptyList : applications.getApplication();*/
        } catch (ServiceException e) {
            logger.error("Exception in loading applications for the docId list", e);
            return Collections.emptyList();
        }
    }
}