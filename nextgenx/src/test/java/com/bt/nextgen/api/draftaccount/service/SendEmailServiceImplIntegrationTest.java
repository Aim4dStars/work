package com.bt.nextgen.api.draftaccount.service;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.web.controller.cash.util.Attribute;

public class SendEmailServiceImplIntegrationTest extends BaseSecureIntegrationTest{

	@Autowired
    private SendEmailService sendEmailService;
	
	private final String GCM_ID ="1234567";
	
	private final String ROLE = Attribute.ADVISER;
	
	private static final String SUCCESS = "Success";
	
	@SecureTestContext
    @Test
    @Ignore
    public void testResendRegistrationCodeSendEmail() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        String response = sendEmailService.resendRegistrationEmail(GCM_ID, ROLE, serviceErrors);

        Assert.assertNotNull(response);
        Assert.assertSame(SUCCESS, response);
    }
}
