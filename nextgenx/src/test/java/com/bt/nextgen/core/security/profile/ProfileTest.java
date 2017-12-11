package com.bt.nextgen.core.security.profile;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.Test;

import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.util.SamlUtil;

public class ProfileTest
{

	@Test
	public void testIsExpiredTrue() throws Exception
	{
		String stringToken = SamlUtil.loadSaml();
		SamlToken token = new SamlToken(stringToken);

		com.btfin.panorama.core.security.profile.Profile profile = new com.btfin.panorama.core.security.profile.Profile(token);
		assertTrue(profile.isExpired());
	}
	
	@Test
	public void testIsExpiredFalse() throws Exception
	{
		String stringToken = SamlUtil.loadSaml("/saml-sample_UT.xml");
		SamlToken token = new SamlToken(stringToken);

		com.btfin.panorama.core.security.profile.Profile profile = new com.btfin.panorama.core.security.profile.Profile(token);
		assertFalse(profile.isExpired());
	}
	
	@Test
	public void testIsValid() throws Exception
	{
		String stringToken = SamlUtil.loadSaml();
		SamlToken token = new SamlToken(stringToken);

		com.btfin.panorama.core.security.profile.Profile profile = new com.btfin.panorama.core.security.profile.Profile(token);
		profile.setCurrentProfileId("12111");
		
		//when samlToken, profileId, emulationId are all null
		boolean isValid = profile.isValid(null, null);
		assertFalse(isValid);

		//when profile id is null and samlToken is not null
		isValid = profile.isValid(stringToken, null);
		assertTrue(isValid);

		//when passed profile id is equal to profile's token profile id
		isValid = profile.isValid(stringToken, "12111");
		assertTrue(isValid);

		//when profile ids are unequal and the current profile id is changed
		isValid = profile.isValid(stringToken, "12110");
		assertFalse(isValid);
		

		//Test for different tokens but GCM ids are not equal
		String newToken = SamlUtil.loadSaml("staff-saml.xml");
		isValid = profile.isValid(newToken, "12111");
		assertFalse(isValid);

		//Test for different tokens but GCM ids and profile ids are unequal
		Field field = SamlToken.class.getDeclaredField("gcmId");
		field.setAccessible(true);
		field.set(token, "CS057462");

		isValid = profile.isValid(newToken, "12111");
		assertTrue(isValid);

		//Test for different tokens but GCM ids and profile ids are equal
		isValid = profile.isValid(newToken, "12110");
		assertTrue(isValid);
		
	}

	public void testNewUserNameDoesUpdateUsername()
	{
		String stringToken = SamlUtil.loadSaml();
		SamlToken token = new SamlToken(stringToken);

		com.btfin.panorama.core.security.profile.Profile profile = new com.btfin.panorama.core.security.profile.Profile(token);
		assertThat(profile.getUserName(), is(not(nullValue())));
		assertThat(profile.getUserName(),is("adviser"));

		profile.setNewUserName("steve");
		assertThat(profile.getUserName(), is("steve"));
	}


	@Test
	public void testNewUsernameUpdate_DoesntGetOverWrittenByOldToken()
	{
		String stringToken = SamlUtil.loadSaml();
		SamlToken token = new SamlToken(stringToken);

		com.btfin.panorama.core.security.profile.Profile profile = new com.btfin.panorama.core.security.profile.Profile(token);
		profile.setNewUserName("steve");

		profile.refreshFrom(new com.btfin.panorama.core.security.profile.Profile(token));
		assertThat(profile.getUserName(), is("steve"));

		profile.refreshFrom(new com.btfin.panorama.core.security.profile.Profile(token));
		assertThat(profile.getUserName(), is("steve"));


	}

	@Test
	public void testNewUsernameUpdate_DoesRemoveUpdateAfterReplacement()
	{
		String stringToken = SamlUtil.loadSaml();
		SamlToken token = new SamlToken(stringToken);

		com.btfin.panorama.core.security.profile.Profile profile = new com.btfin.panorama.core.security.profile.Profile(token);
		assertThat(profile.getUserName(), is(not(nullValue())));
		assertThat(profile.getUserName(),is("adviser"));

		profile.setNewUserName("steve");
		assertThat(profile.getUserName(), is("steve"));


		String newToken = SamlUtil.loadSaml("steve",getAdviserAuthorities(),"123456789");
		profile.refreshFrom(new com.btfin.panorama.core.security.profile.Profile(new SamlToken(newToken)));
		assertThat(profile.getUserName(), is("steve"));

		profile.refreshFrom(new com.btfin.panorama.core.security.profile.Profile(token));
		assertThat(profile.getUserName(), is("adviser"));
	}


	private String[] getAdviserAuthorities()
	{
		String[] adviserAuth = {"adviser"};
		return adviserAuth;
	}

}
