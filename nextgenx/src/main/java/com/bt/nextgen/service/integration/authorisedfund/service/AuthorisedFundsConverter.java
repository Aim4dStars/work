package com.bt.nextgen.service.integration.authorisedfund.service;

import com.bt.nextgen.service.integration.authentication.model.TokenIssuer;
import com.bt.nextgen.service.integration.authorisedfund.model.*;
import ns.btfin_com.product.common.investmenttrust.investmenttrustservice.investmenttrustreply.v1_0.*;
import ns.btfin_com.product.common.investmenttrust.investmenttrustservice.investmenttrustreply.v1_0.OrganisationType;
import ns.btfin_com.product.common.investmenttrust.investmenttrustservice.investmenttrustrequest.v1_0.InvestmentTrustIdIssuerType;
import ns.btfin_com.product.common.investmenttrust.investmenttrustservice.investmenttrustrequest.v1_0.RetrieveAuthorisedTrustsRequestMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L067218 on 6/04/2016.
 */
public class AuthorisedFundsConverter
{
    private static final Logger logger = LoggerFactory.getLogger(com.bt.nextgen.service.integration.authorisedfund.service.AuthorisedFundsConverter.class);


    public static RetrieveAuthorisedTrustsRequestMsgType toRetrieveAuthorisedTrustsRequestMsgType(String customerNumber, TokenIssuer investmentTrustIDIssuer)
    {
        RetrieveAuthorisedTrustsRequestMsgType requestBuilder = new RetrieveAuthorisedTrustsRequestMsgType();
        requestBuilder.setCustomerNumber(customerNumber);
        requestBuilder.setInvestmentTrustIDIssuer(InvestmentTrustIdIssuerType.fromValue(investmentTrustIDIssuer.name()));
        return requestBuilder;
    }

    public static List<AuthorisedFundDetail> toRetrieveAuthorisedTrustsResponse(RetrieveAuthorisedTrustsResponseMsgType responseMsgType)
    {
        List<AuthorisedFundDetail> fundList = new ArrayList<AuthorisedFundDetail>();
        setFundList(fundList,responseMsgType);
        return fundList;
    }

    public static void setFundList(List<AuthorisedFundDetail> fundList, RetrieveAuthorisedTrustsResponseMsgType responseMsgType) throws RuntimeException
    {
        if(responseMsgType.getStatus().equals(StatusCodeType.SUCCESS))
        {
            RetrieveAuthorisedTrustsDetailsSuccessResponseType responseType = responseMsgType.getResponseDetails().getResponseDetail().getSuccessResponse();
            for(OrganisationType organisationType : responseType.getInvestmentTrustDetail()){
                AuthorisedFundDetail fundDetail = new AuthorisedFundDetailImpl();
                fundDetail.setOrganisationName(organisationType.getOrganisationName());
                fundDetail.setAbn(organisationType.getABN());
                TrustDetails trustdetails= new TrustDetailsImpl();
                trustdetails.setTrustId(organisationType.getTrustDetails().getTrustID());
                trustdetails.setTrustName(organisationType.getTrustDetails().getTrustName());
                trustdetails.setTrustIdIssuer(organisationType.getTrustDetails().getTrustIDIssuer().value());
                fundDetail.setTrustDetails(trustdetails);
                fundList.add(fundDetail);
            }
        }
        else if (responseMsgType.getStatus().equals(StatusCodeType.ERROR)
                && "CustomerNumberNotFound".equals(responseMsgType.getResponseDetails().getResponseDetail().getErrorResponse().getSubCode()))
        {
            // No funds for this customer because customer token is not present in icc
            logger.debug("token not found for customer -- authorised fund api not called");
        }
        else
        {
            throw new RuntimeException("authorised funds could not retrieved successfully");
        }
    }
}