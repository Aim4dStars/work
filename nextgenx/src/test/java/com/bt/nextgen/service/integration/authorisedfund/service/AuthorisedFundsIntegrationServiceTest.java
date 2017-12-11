package com.bt.nextgen.service.integration.authorisedfund.service;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.integration.authentication.model.TokenIssuer;
import com.bt.nextgen.service.integration.authorisedfund.model.AuthorisedFundDetail;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by L067218 on 27/04/2016.
 */
public class AuthorisedFundsIntegrationServiceTest extends BaseSecureIntegrationTest {

    @Autowired
    AuthorisedFundsIntegrationService authorisedFundsIntegrationService;

    @Test
    public void loadAuthorisedFunds()
    {
        List<AuthorisedFundDetail> response = authorisedFundsIntegrationService.loadAuthorisedFunds("12345", TokenIssuer.BGL);

        Assert.assertNotNull(response);

        Assert.assertTrue(response.size() == 13);

        Assert.assertEquals(response.get(0).getAbn(),"11111117111");
        Assert.assertEquals(response.get(0).getOrganisationName(),"btfinancialapitest");
        Assert.assertEquals(response.get(0).getTrustDetails().getTrustId(),"8a009fd94b919beb014b94699dd90015");
        Assert.assertEquals(response.get(0).getTrustDetails().getTrustIdIssuer(),"BGL");
        Assert.assertEquals(response.get(0).getTrustDetails().getTrustName(),"BGL Training Fund 9");

        Assert.assertEquals(response.get(3).getOrganisationName(),"btfinancialapi");
        Assert.assertEquals(response.get(3).getTrustDetails().getTrustId(),"8a009fd94b919beb014b94699dd66015");
        Assert.assertEquals(response.get(3).getTrustDetails().getTrustName(),"BGL Training");
        Assert.assertEquals(response.get(3).getTrustDetails().getTrustIdIssuer(),"BGL");
    }

    @Test
    // customer does not have valid token -- so no funds are returned
    public void loadAuthorisedFundsCustomerNotFound()
    {
        List<AuthorisedFundDetail> response = authorisedFundsIntegrationService.loadAuthorisedFunds("77777", TokenIssuer.BGL);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.size() == 0);
    }

    @Test(expected = RuntimeException.class)
    public void loadAuthorisedFundsWithError()
    {
        List<AuthorisedFundDetail> response = authorisedFundsIntegrationService.loadAuthorisedFunds("66666", TokenIssuer.BGL);
    }
}