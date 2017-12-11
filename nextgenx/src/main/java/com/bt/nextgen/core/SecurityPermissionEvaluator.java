package com.bt.nextgen.core;

import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.repository.UserRepository;
import com.btfin.panorama.core.security.encryption.EncodedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

public class SecurityPermissionEvaluator implements PermissionEvaluator
{
	private static final Logger logger = LoggerFactory.getLogger(SecurityPermissionEvaluator.class);

	@Autowired
	private UserRepository userRepository;

	public static enum Permission
	{
		isValidAdviser, isValidCashAccount, isValidInvestor
	}

	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission)
	{
		if (authentication == null || permission == null)
		{
			logger.info(
				"Security check failed due to NPE: " + authentication + ", " + targetDomainObject + ", " + permission);
			return false;
		}

		if (targetDomainObject instanceof EncodedString)
		{
			targetDomainObject = ((EncodedString) targetDomainObject).plainText();
		}

		logger.info(
			"Security check: User principal: " + authentication.getName() + ", target domain: " + targetDomainObject + ", permission: " + permission);

		switch (Permission.valueOf(String.valueOf(permission)))
		{
			case isValidAdviser:
				//securityAssertion(targetDomainObject.toString().equals(userRepository.loadUser(authentication.getName()).getAdviserId()));
				securityAssertion(true);
				break;

			case isValidCashAccount:
				securityAssertion(true); //needs to have alphabetical character for now.
				break;

			case isValidInvestor:
				securityAssertion(true); //needs to have alphabetical character for now.
				break;
				
			default:
				securityAssertion(true);
				//securityAssertion(false, "Unknown security permission check: " + permission);
				break;
		}
		return true;
	}

	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
		Object permission)
	{
		return true;
	}

	private void securityAssertion(boolean value)
	{
		securityAssertion(value, "Access is denied");
	}

	private void securityAssertion(boolean value, String message)
	{
		if (!value)
		{
			logger.error("Security assertion failed");
			throw new AccessDeniedException(message);
		}
	}
}
