package com.bt.nextgen.service.integration.authorisedfund.service;

import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.btesb.gateway.WebServiceHandler;
import com.bt.nextgen.service.integration.authentication.model.TokenIssuer;
import com.bt.nextgen.service.integration.authorisedfund.model.AuthorisedFundDetail;
import com.bt.nextgen.service.integration.authorisedfund.model.AuthorisedFundDetailHolder;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import ns.btfin_com.product.common.investmenttrust.investmenttrustservice.investmenttrustreply.v1_0.StatusCodeType;
import ns.btfin_com.product.common.investmenttrust.investmenttrustservice.investmenttrustrequest.v1_0.RetrieveAuthorisedTrustsRequestMsgType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("S00112")
@Service
public class AuthorisedFundsIntegrationServiceImpl implements AuthorisedFundsIntegrationService
{
    private static final Logger logger = LoggerFactory.getLogger(AuthorisedFundsIntegrationServiceImpl.class);

    @Autowired
    @Qualifier("btEsbWebServiceHandler")
    private WebServiceHandler webServiceHandler;


    @Override
    public List<AuthorisedFundDetail> loadAuthorisedFunds(String customerNumber, TokenIssuer investmentTrustIDIssuer) {

        RetrieveAuthorisedTrustsRequestMsgType request = AuthorisedFundsConverter.toRetrieveAuthorisedTrustsRequestMsgType(customerNumber, investmentTrustIDIssuer);

        AuthorisedFundDetailHolder fundHolder = null;
        fundHolder = webServiceHandler.sendToWebServiceAndParseResponseToDomain(Attribute.INVESTMENT_TRUST_KEY, request, AuthorisedFundDetailHolder.class, new ServiceErrorsImpl());

        if (StringUtils.isNotEmpty(fundHolder.getStatus()) && fundHolder.getStatus().equals(StatusCodeType.ERROR.value())
                && StringUtils.isNotEmpty(fundHolder.getStatusSubcode()) && "CustomerNumberNotFound".equals(fundHolder.getStatusSubcode()))
        {
            // No funds for this customer because customer token is not present in icc
            logger.debug("token not found for customer -- authorised fund api not called");
        }
        else if (StringUtils.isNotEmpty(fundHolder.getStatus()) && fundHolder.getStatus().equals(StatusCodeType.ERROR.value()))
        {
            //throw new RuntimeException("authorised funds could not retrieved successfully");
            ServiceErrorImpl error = new ServiceErrorImpl();
            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            error.setReason("Authorised funds could not retrieved successfully");
            serviceErrors.addError(error);
//            return new ArrayList<>();
        }

        return fundHolder.getAuthorisedFundDetailsList();
    }
}