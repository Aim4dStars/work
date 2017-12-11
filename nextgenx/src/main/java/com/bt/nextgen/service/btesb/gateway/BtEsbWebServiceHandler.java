package com.bt.nextgen.service.btesb.gateway;

import com.btfin.panorama.core.security.avaloq.AvaloqBankingAuthorityService;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.namespace.RuntimeNamespaceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service("btEsbWebServiceHandler")
public class BtEsbWebServiceHandler implements WebServiceHandler {
    private static final Logger logger = LoggerFactory.getLogger(BtEsbWebServiceHandler.class);

    @Autowired
    private WebServiceProvider serviceProvider;

    @Resource(name = "userDetailsService")
    private AvaloqBankingAuthorityService userSamlService;


    private static final Map<String, String> nsMap;

    static {
        nsMap = new HashMap<>();
        nsMap.put("RspNS", "ns://btfin.com/Product/Common/InvestmentTrust/InvestmentTrustService/InvestmentTrustReply/V1_0");
        nsMap.put("RspINS", "ns://btfin.com/Product/Insurance/LifeInsurance/Policy/PolicyService/PolicyReply/V4_2");
        nsMap.put("RspINA", "ns://btfin.com/Product/Common/InvestmentAccount/InvestmentAccountService/InvestmentAccountReply/V1_0");
        nsMap.put("RspSCNS", "ns://btfin.com/Product/SuperannuationRetirement/SuperannuationMatch/SuperannuationMatchService/SuperannuationMatchReply/V1_0");
        nsMap.put("RspSNNS", "ns://btfin.com/Product/SuperannuationRetirement/SuperannuationNotification/SuperannuationNotificationService/SuperannuationNotificationReply/V1_0");
    }


    /**
     * Method to send an btesb gateway request for an annotated class response
     *
     * @param requestPayload The request payload that is being sent to btesb
     * @param serviceErrors  A collector for any errors which may occur
     * @param responseType   The annotated class implementation which the response will be translated into
     *
     * @return T
     */
    @Override
    public <T> T sendToWebServiceAndParseResponseToDomain(String serviceKey, Object requestPayload, Class<T> responseType,
                                                          com.bt.nextgen.service.ServiceErrors serviceErrors) {
        logger.debug("Generating request payload");
        DefaultResponseExtractor<T> defaultResponseExtractor = new DefaultResponseExtractor<>(responseType);
        defaultResponseExtractor.setNamespaceContext(getNsContext());


        T result = serviceProvider.parseSendAndReceiveToDomain(userSamlService.getSamlToken(),
                serviceKey,
                requestPayload,
                defaultResponseExtractor);

        logger.debug("Service errors {}", serviceErrors.getErrors().size());
        //validateResponse(serviceErrors,result);
        return result;
    }

    private RuntimeNamespaceContext getNsContext() {
        RuntimeNamespaceContext nsContext = new RuntimeNamespaceContext(getNamespaceMap());
        return nsContext;
    }

    @Override
    public Map<String, String> getNamespaceMap() {
        return nsMap;
    }
}