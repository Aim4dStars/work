package com.bt.nextgen.core.security;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

public class RolesTest
{
	@Test
	public void testFromRawName() throws Exception
	{
		Assert.assertThat(com.btfin.panorama.core.security.Roles.fromRawName("bt-adviser"), Is.is(com.btfin.panorama.core.security.Roles.ROLE_ADVISER));
		Assert.assertThat(com.btfin.panorama.core.security.Roles.fromRawName("bt-investor"), Is.is(com.btfin.panorama.core.security.Roles.ROLE_INVESTOR));
		Assert.assertThat(com.btfin.panorama.core.security.Roles.fromRawName(" bt-admin"), Is.is(com.btfin.panorama.core.security.Roles.ROLE_ADMIN));
		Assert.assertThat(com.btfin.panorama.core.security.Roles.fromRawName("bt-admin "), Is.is(com.btfin.panorama.core.security.Roles.ROLE_ADMIN));
		Assert.assertThat(com.btfin.panorama.core.security.Roles.fromRawName("ngUsers "), Is.is(com.btfin.panorama.core.security.Roles.ROLE_SERVICE_OP));
		Assert.assertThat(com.btfin.panorama.core.security.Roles.fromRawName("bt-ngTrusteeUsers "), Is.is(com.btfin.panorama.core.security.Roles.ROLE_TRUSTEE));
		Assert.assertThat(com.btfin.panorama.core.security.Roles.fromRawName("ngTrusteeUsers "), Is.is(com.btfin.panorama.core.security.Roles.ROLE_TRUSTEE));
		Assert.assertThat(com.btfin.panorama.core.security.Roles.fromRawName("ngtrusteeusers "), Is.is(com.btfin.panorama.core.security.Roles.ROLE_TRUSTEE));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidRoleName() throws Exception
	{
		com.btfin.panorama.core.security.Roles.fromRawName("INVALID_NAME");
	}
}
