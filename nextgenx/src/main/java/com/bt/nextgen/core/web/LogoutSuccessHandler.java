package com.bt.nextgen.core.web;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.bt.nextgen.core.util.SETTINGS.SECURITY_LOGOUT_REDIRECT_SUCCESS;

/**
 * This class takes care of redirecting to the correct logout url
 */
public class LogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler
{
    private static final Logger logger = LoggerFactory.getLogger(LogoutSuccessHandler.class);
    private static final String ERROR = "ERROR";

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
		throws IOException, ServletException
	{
        logger.debug("LogoutSuccessHandler Called...");
        response.sendRedirect(response.encodeRedirectURL(buildRedirectUrl(request)));
	}

    private String buildRedirectUrl(HttpServletRequest request) {
        String error = request.getParameter(ERROR);

        String redirectUrl = SECURITY_LOGOUT_REDIRECT_SUCCESS.value();
        if (StringUtils.isNotBlank(error)) {
            redirectUrl += "&" + ERROR + "=" + error;
        }
        return redirectUrl;
    }
}
