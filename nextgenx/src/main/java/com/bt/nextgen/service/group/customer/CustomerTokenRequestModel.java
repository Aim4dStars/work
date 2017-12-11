package com.bt.nextgen.service.group.customer;

import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by l063220 on 5/5/14.
 */
public class CustomerTokenRequestModel implements CustomerTokenRequest {

    private static final Logger logger = LoggerFactory.getLogger(CustomerTokenRequestModel.class);

    private String customerNumber;
    private String panNumber;
    private String xForwardedHost;
    private SamlToken token;
    private boolean forgotPassword;
    private String deviceId;
    private String zNumber;
    private String devicePrint;

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getxForwardedHost() {
        return xForwardedHost;
    }

    public void setxForwardedHost(String xForwardedHost) {
        this.xForwardedHost = xForwardedHost;
    }

    public SamlToken getToken() {
        return token;
    }

    public void setToken(SamlToken token) {
        this.token = token;
    }

    public void populateCustomerNumber(String zNumber, String userName) {
        if (StringUtil.isNotNullorEmpty(zNumber)) {
            logger.info("Setting the znumber to customer number in token request.");
            setCustomerNumber(zNumber);
        } else {
            logger.info("Setting the pan number to customer number in token request.");
            setCustomerNumber(userName);
        }
    }

    @Override
    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    @Override
    public boolean isForgotPassword() {
        return forgotPassword;
    }

    public void setForgotPassword(boolean forgotPassword) {
        this.forgotPassword = forgotPassword;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getzNumber() {
        return zNumber;
    }

    public void setzNumber(String zNumber) {
        this.zNumber = zNumber;
    }

    @Override
    public String getDevicePrint() {
        return devicePrint;
    }

    public void setDevicePrint(String devicePrint) {
        this.devicePrint = devicePrint;
    }
}
