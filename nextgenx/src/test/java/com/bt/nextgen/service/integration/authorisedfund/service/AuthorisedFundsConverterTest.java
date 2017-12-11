package com.bt.nextgen.service.integration.authorisedfund.service;

import com.bt.nextgen.service.integration.authentication.model.TokenIssuer;
import com.bt.nextgen.service.integration.authorisedfund.model.AuthorisedFundDetail;
import ns.btfin_com.party.v3_1.TrustIDIssuerType;
import ns.btfin_com.product.common.investmenttrust.investmenttrustservice.investmenttrustreply.v1_0.*;
import ns.btfin_com.product.common.investmenttrust.investmenttrustservice.investmenttrustrequest.v1_0.InvestmentTrustIdIssuerType;
import ns.btfin_com.product.common.investmenttrust.investmenttrustservice.investmenttrustrequest.v1_0.RetrieveAuthorisedTrustsRequestMsgType;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by L067218 on 21/04/2016.
 */

@RunWith(MockitoJUnitRunner.class)
public class AuthorisedFundsConverterTest {


    @Test
    public void testToRetrieveAuthorisedTrustsRequestMsgType(){
        RetrieveAuthorisedTrustsRequestMsgType request = AuthorisedFundsConverter.toRetrieveAuthorisedTrustsRequestMsgType("12345", TokenIssuer.BGL);
        assertNotNull(request);
        assertEquals(request.getCustomerNumber(),"12345");
        assertEquals(request.getInvestmentTrustIDIssuer(), InvestmentTrustIdIssuerType.BGL);

    }

    @Test
    public void testToRetrieveAuthorisedTrustsResponseWithSuccessResponse(){

        RetrieveAuthorisedTrustsResponseMsgType responseMsgType = new RetrieveAuthorisedTrustsResponseMsgType();
        setSuccessResponseMsgType(responseMsgType);
        List<AuthorisedFundDetail> fundDetailList= AuthorisedFundsConverter.toRetrieveAuthorisedTrustsResponse(responseMsgType);
        assertNotNull(fundDetailList);
        assertEquals(fundDetailList.size(),2);
        assertEquals(fundDetailList.get(0).getOrganisationName(),"btfinancialapitest");
        assertEquals(fundDetailList.get(1).getOrganisationName(),"BglTest");

        assertEquals(fundDetailList.get(0).getAbn(),"11111117111");
        assertEquals(fundDetailList.get(1).getAbn(),"11118917111");


        assertEquals(fundDetailList.get(0).getTrustDetails().getTrustName(),"Bgl Fund");
        assertEquals(fundDetailList.get(0).getTrustDetails().getTrustId(),"8a009fd94b919beb014b96599dd90015");
        assertEquals(fundDetailList.get(0).getTrustDetails().getTrustIdIssuer(),"BGL");
        assertEquals(fundDetailList.get(1).getTrustDetails().getTrustName(),"BGL Training Fund 9");
        assertEquals(fundDetailList.get(1).getTrustDetails().getTrustId(),"8a009fd94b919beb014b94699dd90015");
        assertEquals(fundDetailList.get(1).getTrustDetails().getTrustIdIssuer(),"BGL");

    }

    public void setSuccessResponseMsgType(RetrieveAuthorisedTrustsResponseMsgType responseMsgType){
        responseMsgType.setStatus(StatusCodeType.SUCCESS);
        RetrieveAuthorisedTrustsResponseDetailsType detailsType = new RetrieveAuthorisedTrustsResponseDetailsType();
        RetrieveAuthorisedTrustsResponseDetailType detail = new RetrieveAuthorisedTrustsResponseDetailType();
        RetrieveAuthorisedTrustsDetailsSuccessResponseType successResponse = new RetrieveAuthorisedTrustsDetailsSuccessResponseType();
        List<OrganisationType> investmentTrustDetail = new ArrayList<OrganisationType>();

        OrganisationType orgType1 = new OrganisationType();
        orgType1.setOrganisationName("btfinancialapitest");
        orgType1.setABN("11111117111");
        TrustDetailsType trustDetailsType1 = new TrustDetailsType();
        trustDetailsType1.setTrustIDIssuer(TrustIDIssuerType.BGL);
        trustDetailsType1.setTrustID("8a009fd94b919beb014b96599dd90015");
        trustDetailsType1.setTrustName("Bgl Fund");
        orgType1.setTrustDetails(trustDetailsType1);


        OrganisationType orgType2 = new OrganisationType();
        orgType2.setABN("11118917111");
        orgType2.setOrganisationName("BglTest");
        TrustDetailsType trustDetailsType2 = new TrustDetailsType();
        trustDetailsType2.setTrustIDIssuer(TrustIDIssuerType.BGL);
        trustDetailsType2.setTrustID("8a009fd94b919beb014b94699dd90015");
        trustDetailsType2.setTrustName("BGL Training Fund 9");
        orgType2.setTrustDetails(trustDetailsType2);


        investmentTrustDetail.add(orgType1);
        investmentTrustDetail.add(orgType2);

        successResponse.getInvestmentTrustDetail().addAll(investmentTrustDetail);
        detail.setSuccessResponse(successResponse);
        detailsType.setResponseDetail(detail);
        responseMsgType.setResponseDetails(detailsType);
    }

    @Test(expected=RuntimeException.class)
    public void testToRetrieveAuthorisedTrustsResponseWithErrorResponse() {

        RetrieveAuthorisedTrustsResponseMsgType responseMsgType = new RetrieveAuthorisedTrustsResponseMsgType();
        setErrorResponseMsgType(responseMsgType);
        List<AuthorisedFundDetail> fundDetailList = AuthorisedFundsConverter.toRetrieveAuthorisedTrustsResponse(responseMsgType);
        assertNotNull(fundDetailList);
        assertEquals(fundDetailList.size(),0);
    }

    public void setErrorResponseMsgType(RetrieveAuthorisedTrustsResponseMsgType responseMsgType){
        responseMsgType.setStatus(StatusCodeType.ERROR);
    }
}
