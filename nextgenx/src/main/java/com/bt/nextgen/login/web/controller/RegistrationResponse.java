package com.bt.nextgen.login.web.controller;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: L053474
 * Date: 25/09/13
 * Time: 9:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class RegistrationResponse implements Serializable{

    private String relayState;
    private String SAMLResponse;
    private String eamPostUrl;

    private boolean showSMS;

    private RegistrationResponse() {

    }

    public static RegistrationResponse buildForNextStep(String relayState, String saml, String eamPostUrl) {
        RegistrationResponse resp = new RegistrationResponse();
        resp.setRelayState(relayState);
        resp.setSAMLResponse(saml);
        resp.setEamPostUrl(eamPostUrl);
        return resp;
    }

    public static RegistrationResponse buildForCurrentStepWithSMS() {
        RegistrationResponse resp = new RegistrationResponse();
        resp.setShowSMS(true);
        return resp;
    }

    public String getRelayState() {
        return relayState;
    }

    public void setRelayState(String relayState) {
        this.relayState = relayState;
    }

    public String getSAMLResponse() {
        return SAMLResponse;
    }

    public void setSAMLResponse(String SAMLResponse) {
        this.SAMLResponse = SAMLResponse;
    }

    public String getEamPostUrl() {
        return eamPostUrl;
    }

    public void setEamPostUrl(String eamPostUrl) {
        this.eamPostUrl = eamPostUrl;
    }

    public boolean isShowSMS() {
        return showSMS;
    }

    public void setShowSMS(boolean showSMS) {
        this.showSMS = showSMS;
    }
}
