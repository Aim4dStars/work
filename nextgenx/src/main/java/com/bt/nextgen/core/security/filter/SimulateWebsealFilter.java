package com.bt.nextgen.core.security.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.bt.nextgen.core.security.HttpHeadersRequestWrapper;
import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.profile.Profile;
import com.bt.nextgen.login.util.SamlUtil;
import com.bt.nextgen.util.Environment;

import static com.bt.nextgen.core.util.SETTINGS.*;

/**
 * This class allows us to run a username/password authentication, when the saml is missing. Should only be used in dev.
 */
public class SimulateWebsealFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(SimulateWebsealFilter.class);
    private static final Logger headerLogger = LoggerFactory.getLogger(SimulateWebsealFilter.class.getCanonicalName() + ".headers");

    private String defaultUrl;

    public SimulateWebsealFilter() {
    }

    public void setDefaultUrl(String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        if (Environment.notProduction()) {
            if (missingWebsealHeaders(request) && SAML_SIMULATE.isTrue()) {
                logger.info("Webseal headers missing, will assume direct access and simulate headers");
                request = addSimulatedHeaders(request);
            }
            logRequestHeaders(request);
        }
        chain.doFilter(request, res);
    }

    private HttpServletRequest addSimulatedHeaders(HttpServletRequest request) {
        HttpHeadersRequestWrapper wrappedRequest = new HttpHeadersRequestWrapper(request);

        RequestContext context = new RequestContext(request);

        wrappedRequest.addCustomHeader(SECURITY_HEADER_USERNAME.value(), context.username);
        chooseSamlSource(wrappedRequest, context);

        return wrappedRequest;
    }

    private HttpHeadersRequestWrapper chooseSamlSource(HttpHeadersRequestWrapper wrapped, RequestContext context) {

        if (context.currentlyAuthenticating()) {
            // create tokens from templates
            switch (context.getRole()) {
                case "ngtrusteeusers":
                    wrapped.addCustomHeader(SAML_HEADER_WBC.value(), loadSaml(context, "trustee-saml.xml"));
                    break;
                case "service":
                    wrapped.addCustomHeader(SAML_HEADER_INTERNAL.value(), loadSaml(context, "staff-saml.xml"));
                    break;
                case "wpl-direct":
                    wrapped.addCustomHeader(SAML_HEADER_WBC.value(), loadSaml(context, "direct-saml.xml"));
                    break;
                case "adviser":
                case "investor":
                default:
                    wrapped.addCustomHeader(SAML_HEADER_WBC.value(), loadSaml(context, "saml-sample.xml"));
                    break;
            }
            return wrapped;
        }

        if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null) {
            wrapped.addCustomHeader(SAML_HEADER_WBC.value(), loadSaml(context, "unauthenticated-saml.xml"));
            return wrapped;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String headerName;
        if (authentication.getAuthorities().contains(Roles.ROLE_SERVICE_OP.AUTHORITY)) {
            headerName = SAML_HEADER_INTERNAL.value();
        } else {
            headerName = SAML_HEADER_WBC.value();
        }

        // Manually create the profile if we are emulating
        if (!(authentication instanceof UsernamePasswordAuthenticationToken)) {
            wrapped.addCustomHeader(headerName, Profile.fromAuthentication(authentication).getToken().getToken());
        }

        return wrapped;
    }

    private void logRequestHeaders(HttpServletRequest request) {
        if (headerLogger.isDebugEnabled()) {
            Enumeration headers = request.getHeaderNames();
            while (headers != null && headers.hasMoreElements()) {
                String header = (String) headers.nextElement();
                headerLogger.debug(" {} : {}", header, request.getHeader(header));
            }
        }
    }

    private String loadSaml(RequestContext context, String filename) {
        // usernames should have 3 parts name_role_gcmid
        String[] usernameParts = context.usernameParts;
        if (usernameParts.length > 3) {
            // handle usernames that have underscores in the gcm id eg bob_adviser_invst_mgr_bas
            Object[] newUsernameParts = ArrayUtils.add(Arrays.copyOfRange(usernameParts, 0, 2), StringUtils.join(Arrays.copyOfRange(usernameParts, 2, usernameParts.length), '_'));
            return String.format(SamlUtil.loadSaml(filename), newUsernameParts);
        }
        return String.format(SamlUtil.loadSaml(filename), context.usernameParts);
    }

    private boolean missingWebsealHeaders(HttpServletRequest req) {
        boolean missingUsernameHeader = req.getHeader(SECURITY_HEADER_USERNAME.value()) == null;

        if (missingUsernameHeader) {
            logger.warn("The header {} was missing, I hope this is dev", SECURITY_HEADER_USERNAME.value());
        }

        boolean samlTokenFound = false;
        for (String headerName : getSamlHeaderNames()) {
            samlTokenFound |= StringUtils.isEmpty(req.getHeader(headerName));
        }

        return missingUsernameHeader || !samlTokenFound;
    }

    private String[] getSamlHeaderNames() {
        return SAML_HEADER.value().split(",");
    }
}
