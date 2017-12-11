package com.bt.nextgen.service.integration.authorisedfund.model;

/**
 * Created by L067218 on 6/04/2016.
 */
public interface AuthorisedFundDetail {

    String getOrganisationName();

    void setOrganisationName(String organisationName);

    String getAbn();

    void setAbn(String abn);

    TrustDetails  getTrustDetails();

    void setTrustDetails(TrustDetails trustDetails);
}
