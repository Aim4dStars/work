package com.bt.nextgen.service.integration.authorisedfund.service;

import com.bt.nextgen.service.integration.authentication.model.TokenIssuer;
import com.bt.nextgen.service.integration.authorisedfund.model.AuthorisedFundDetail;

import java.util.List;

/**
 * Created by L067218 on 6/04/2016.
 */
public interface AuthorisedFundsIntegrationService {


    /**
     * Retrieve Authorised trust details
     * @param gcmId user id
     * @param issuer Issuer of Trust Information
     * @return List<AuthorisedFundDetail>
     */
    public List<AuthorisedFundDetail> loadAuthorisedFunds(String gcmId, TokenIssuer issuer);
}
