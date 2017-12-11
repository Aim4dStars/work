package com.bt.nextgen.service.onboarding;


import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.OTPPartyDetailsType;

public interface ValidatePartyResponse extends Response {

    /**
     * @return the sessionId
     */
    String getSessionId();

    /**
     * @return the userName
     */
    String getUserName();

    /**
     * @param userName the userName to set
     */
    void setUserName(String userName);

    String getzNumber();

    void setzNumber(String zNumber);

    String getCisKey();

    void setCisKey(String cisKey);

    String getDeviceId();

    String getDeviceToken();

    OTPPartyDetailsType getInvalidPartyDetails();
}
