
package com.bt.nextgen.service.integration.authorisedfund.service;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.integration.authentication.model.TokenIssuer;
import com.bt.nextgen.service.integration.authorisedfund.model.AuthorisedFundDetail;
import ns.btfin_com.product.common.investmenttrust.investmenttrustservice.investmenttrustreply.v1_0.RetrieveAuthorisedTrustsResponseMsgType;
import ns.btfin_com.product.common.investmenttrust.investmenttrustservice.investmenttrustrequest.v1_0.RetrieveAuthorisedTrustsRequestMsgType;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Created by L067218 on 14/04/2016.
 */

@Ignore // Ignoring temporarily. We may have to switch to a full fledged integration test to get value.
@RunWith(MockitoJUnitRunner.class)
public class AuthorisedFundsServiceTest {

    @InjectMocks
    AuthorisedFundsIntegrationServiceImpl authorisedFundsIntegrationService = new AuthorisedFundsIntegrationServiceImpl();

    @Mock
    private BankingAuthorityService userSamlService;

    @Mock
    private WebServiceProvider provider;

    @Test
    public void testGetAuthorisedFundsForSuccess()
    {
        RetrieveAuthorisedTrustsResponseMsgType jaxbValidateResponse = JaxbUtil.unmarshall("/webservices/response/InvestmentTrustResponse_UT.xml", RetrieveAuthorisedTrustsResponseMsgType.class);

        when(userSamlService.getSamlToken()).thenReturn(new SamlToken(""));
        when(provider.sendWebServiceWithSecurityHeader(any(SamlToken.class), anyString(),
                any(RetrieveAuthorisedTrustsRequestMsgType.class)))
                .thenReturn(jaxbValidateResponse);

        List<AuthorisedFundDetail> response = authorisedFundsIntegrationService.loadAuthorisedFunds("12345", TokenIssuer.BGL);

        assertThat(response, notNullValue());
        Assert.assertEquals(response.size(),13);
        Assert.assertEquals(response.get(0).getAbn(),"11111117111");
        Assert.assertEquals(response.get(0).getOrganisationName(),"btfinancialapitest");
        Assert.assertEquals(response.get(0).getTrustDetails().getTrustId(),"8a009fd94b919beb014b94699dd90015");
        Assert.assertEquals(response.get(0).getTrustDetails().getTrustIdIssuer(),"BGL");
        Assert.assertEquals(response.get(0).getTrustDetails().getTrustName(),"BGL Training Fund 9");

        Assert.assertEquals(response.get(12).getOrganisationName(),"btfinancialapitest");
        Assert.assertEquals(response.get(12).getTrustDetails().getTrustId(),"8a009fd94b919beb014b94699dd90069");
        Assert.assertEquals(response.get(12).getTrustDetails().getTrustName(),"BGL Training Fund New");
        Assert.assertEquals(response.get(12).getTrustDetails().getTrustIdIssuer(),"BGL");

        Assert.assertEquals(response.get(9).getOrganisationName(),"btfinancialapitest");
        Assert.assertEquals(response.get(9).getTrustDetails().getTrustId(),"8a009fd94b919beb896b94699dd90015");
        Assert.assertEquals(response.get(9).getTrustDetails().getTrustName(),"BGL Training");
        Assert.assertEquals(response.get(9).getTrustDetails().getTrustIdIssuer(),"BGL");

    }
}

