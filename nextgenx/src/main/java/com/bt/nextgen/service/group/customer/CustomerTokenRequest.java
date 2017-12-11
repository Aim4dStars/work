package com.bt.nextgen.service.group.customer;

import com.btfin.panorama.core.security.saml.SamlToken;

/**
 * Created by l063220 on 5/05/14.
 */
public interface CustomerTokenRequest {
	String getCustomerNumber();
	String getxForwardedHost();
	String getPanNumber();
	SamlToken getToken();
	boolean isForgotPassword();
	String getDeviceId();
    String getzNumber();
	String getDevicePrint();
}
