package com.bt.nextgen.core.security;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;

import com.btfin.panorama.core.security.profile.Profile;


public class ProfileBasedSecurityExpressionRoot extends WebSecurityExpressionRoot
{

    private static final Logger logger = LoggerFactory.getLogger(ProfileBasedSecurityExpressionRoot.class);

    protected final Authentication authentication;

    public ProfileBasedSecurityExpressionRoot(Authentication a, FilterInvocation fi)
    {
        super(a, fi);
        this.authentication = a;
    }

    public boolean isValidProfile()
    {

        Profile profile = getProfile();

        if (profile == null)
        {
            logger.warn("User profile is NULL");
            return false;
        }

        //Profile is not valid if "custDefinedLogin" is missing from SAML. Service OP is an exception
        if((!Profile.fromOriginalSecurityContext().hasRole(com.btfin.panorama.core.security.Roles.ROLE_SERVICE_OP)) &&
                StringUtils.isBlank(profile.getCustDefinedLogin()))
        {
            logger.warn("CustDefinedLogin is missing from profile");
            return false;
        }

        //TODO: we may also need check if USER has accepted TnC


        return true;
    }

    public boolean isValidProfileWithRole(String role)
    {

        if (isValidProfile() && hasRole(role))
        {
            return true;
        }

        return false;
    }

    private Profile getProfile()
    {

        try
        {
            return (Profile) authentication.getDetails();
        }
        catch (Exception e)
        {
            logger.error("Error in profile:", e);
        }

        return null;
    }
}