package com.bt.nextgen.serviceops.service;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.util.SamlUtil;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class ResetPasswordServiceIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	private ResetPasswordService resetPasswordService;

	@Before
    public void setup()
    {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("adviser", "", Roles.ROLE_SERVICE_OP.name());
		Profile dummyProfile = new Profile(new SamlToken(SamlUtil.loadSaml()));
		authentication.setDetails(dummyProfile);
		SecurityContextHolder.getContext().setAuthentication(authentication);
    }
	
	@Test
	public void testResetPassword() 
	{
		String result = resetPasswordService.resetPassword("201602352", "gcmId", new ServiceErrorsImpl());
		Assert.assertThat(result, Is.is("PW3TEK"));
	}

}
