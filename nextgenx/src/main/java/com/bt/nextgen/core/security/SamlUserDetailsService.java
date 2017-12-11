package com.bt.nextgen.core.security;


import com.btfin.panorama.core.security.profile.Profile;
import org.opensaml.saml2.core.Response;
import org.opensaml.xml.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import static com.bt.nextgen.core.security.AuthorityUtil.grantAuthorities;

/**
 * Using the saml token, query what ever you have to to fill in the user details
 */
public class SamlUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken>
{
	private static final Logger logger = LoggerFactory.getLogger(SamlUserDetailsService.class);
	private Validator<Response> samlValidator;
	private boolean samlValidationEnabled = true;

	public void setValidator(Validator<Response> samlValidator)
	{
		this.samlValidator = samlValidator;
	}

	public void setSamlValidationEnabled(boolean samlValidationEnabled)
	{
		this.samlValidationEnabled = samlValidationEnabled;
	}

	@SuppressWarnings("squid:MethodCyclomaticComplexity ")
	@Override public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException
	{
		Profile profile = (Profile) token.getDetails();

		if (!Profile.class.isAssignableFrom(token.getDetails().getClass()))
		{
			throw new BadCredentialsException("We need a profile on the authentication token");
		}

		if (!ProfileValidator.isValid(profile))
		{
			throw new BadCredentialsException("Invalid profile");
		}

		if (samlValidationEnabled)
		{
			//TODO validation?
			//			samlValidator.validate(parseSaml(token));
		}

        return new User(profile.getNameId(), "UNKNOWN", grantAuthorities(profile.getRoles()));
	}
}
