package com.bt.nextgen.core.web;

import javax.servlet.http.HttpServletRequest;

import com.btfin.panorama.core.security.saml.SamlSettings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.util.Environment;

import static com.bt.nextgen.core.util.SETTINGS.*;

@Component
public class RequestQuery
{
	private static final Logger logger = LoggerFactory.getLogger(RequestQuery.class);
	public boolean isWebSealRequest()
	{
		return Environment.isProduction()
			|| !getRequest().getHeader(SECURITY_HEADER_XFORWARDHOST.value()).matches(DEV_HOST_CHECK.value());
	}

    /**
     * Will retrieve the header value that give the original host value as send by the client browser.
     *
     * <a href="http://dwgps0026/twiki/bin/view/NextGen/TechnicalWebSeal#Westpac_headers">Twiki - TechnicalWebSeal</a>
     * @return the value from the header
     */
    public String getOriginalHost()
    {
        return getRequest().getHeader(SECURITY_HEADER_XFORWARDHOST.value());
    }

	private HttpServletRequest getRequest()
	{
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}


	public SamlToken getSamlToken()
	{
		String samlString = getRequest().getHeader(SAML_HEADER_WBC.value());
		SamlToken token = new SamlToken(samlString);
		return token;
	}

	public boolean isInvestorOnInvestorSite()
	{
		return getSamlToken().getCredentialGroups().contains(Roles.ROLE_INVESTOR) && getOriginalHost().contains("investor");
	}

	public boolean isAdviserOnAdviserSite()
	{
		return getSamlToken().getCredentialGroups().contains(Roles.ROLE_ADVISER) && getOriginalHost().contains("adviser");
	}


	public boolean isUserAuthenticated()
	{

        String ivUser=getRequest().getHeader(SECURITY_HEADER_USERNAME.value());
        if (StringUtils.isBlank(ivUser) || StringUtils.isEmpty(ivUser))
        {
            return false;
        }
        else
        {
			logger.debug("IV User is :{}",ivUser);
            return !SamlSettings.UNAUTHENTICATED.value().equalsIgnoreCase(ivUser);
        }

	}

}
