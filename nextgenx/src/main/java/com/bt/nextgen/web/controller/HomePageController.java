package com.bt.nextgen.web.controller;

import com.bt.nextgen.api.userpreference.model.UserPreferenceEnum;
import com.bt.nextgen.core.exception.AvaloqConnectionException;
import com.bt.nextgen.core.repository.UserPreference;
import com.bt.nextgen.core.repository.UserPreferenceKey;
import com.bt.nextgen.core.repository.UserPreferenceRepository;
import com.bt.nextgen.core.security.encryption.DecryptionService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.bt.nextgen.core.session.SessionUtils.ORIGINATING_SYSTEM;
import static com.bt.nextgen.core.web.controller.SpaController.REDIRECT_APP_HOME;
import static com.bt.nextgen.core.web.controller.SpaController.REDIRECT_DIRECT_ONBOARDING_HOME;

@Controller
@SuppressWarnings({"squid:S2092", "checkstyle:com.puppycrawl.tools.checkstyle.checks.whitespace.NoLineWrapCheck"})
public class HomePageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomePageController.class);

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private DecryptionService decryptionService;

    @Autowired
    private FeatureTogglesService togglesService;

    /**
     * This is a central link to the 'home' page
     */
    public static final String SERVICEOP_HOMEPAGE = "/secure/page/serviceOps/home";
    public static final String HOMEPAGE = "/secure/page/home";
    public static final String RESET_PASSWORD = "/public/page/resetTemporaryPassword";
    public static final String LOGOUT = "/public/page/logout";
    public static final String REDIRECT_HOMEPAGE = "redirect:" + HOMEPAGE;
    public static final String REDIRECT_TEMP_PASSWORD = "redirect:" + RESET_PASSWORD;
    public static final String REDIRECT_LOGOUT = "redirect:" + LOGOUT;
    public static final String REDIRECT_SERVICEOP_HOMEPAGE = "redirect:" + SERVICEOP_HOMEPAGE;
    public static final String REDIRECT_ADMIN_HOME = "redirect:/secure/page/admin/home";
    public static final String CLOSEDLOGON = "/public/page/closed";
    public static final String REDIRECT_CLOSED_LOGON = "redirect:" + CLOSEDLOGON + "?closedStatus=closed";
    public static final String HOMEPAGE_DIRECT_ONBOARDING = "/onboard";
    public static final String AVALOQ_CONNECTION_FAILURE_LOGON = "/public/page/serverfailure";
    public static final String REDIRECT_AVALOQ_CONNECTION_FAILURE_LOGON = "redirect:" + AVALOQ_CONNECTION_FAILURE_LOGON;

    public static final String CHANNEL_WESTPAC_LIVE = "WLIVE";

    //Default constructor for ioc
    public HomePageController() {
    }

    public HomePageController(UserProfileService profileService, UserPreferenceRepository userPreferenceRepository, DecryptionService decryptionService, FeatureTogglesService togglesService) {
        this.profileService = profileService;
        this.userPreferenceRepository = userPreferenceRepository;
        this.decryptionService = decryptionService;
        this.togglesService = togglesService;
    }

    /**
     * Support nicer url use, make all urls go to nice places.
     */
    @RequestMapping(value = {"/", "/home", "/public/home", "/secure", "/secure/home", "/secure/page/admin",}, method = RequestMethod.GET)
    public String root(HttpServletRequest request, HttpServletResponse response) {
        boolean isAdmin = false;
        String result = "";
        try {
            isAdmin = profileService.isAdmin();
        } catch (AvaloqConnectionException ace) {
            LOGGER.error("Avaloq connection failure occurred", ace);
            return REDIRECT_AVALOQ_CONNECTION_FAILURE_LOGON;
        }
        LOGGER.info("start of the method");
        if (this.isServiceOperator()) {
            result = REDIRECT_SERVICEOP_HOMEPAGE;
        } else if (isAdmin) {
            result = REDIRECT_ADMIN_HOME;
        } else {
            // Generate cookie for Omniature scraping to generate login metrics (login success).
            // Cookie explicitly set to unsecure so that it can be deleted by js (non-sensitive cookie).
            try {
                setCookies(response, profileService.getActiveProfile().getJobRole().toString());
                result = REDIRECT_APP_HOME;
            } catch (IllegalStateException ise) {
                LOGGER.error("Following GCMID " + profileService.getGcmId() + " is inactive in avaloq ", ise);
                return REDIRECT_CLOSED_LOGON;
            }
        }
        return result;
    }

    /**
     * This is a refactored method to reduce cyclomatic complexity of calling method.
     * This method checks if current logged in user is Service Operator.
     *
     * @return true/false
     */
    private boolean isServiceOperator() {
        try {
            return profileService.isServiceOperator();
        } catch (IllegalStateException ise) {
            return false;
        }
    }

    private void saveInitialAccountToDisplay(String gcmId, String decryptedAccountId) {
        LOGGER.info("Last accessed BSB_ACCOUNT_NUMBER: {}", decryptedAccountId);

        String accountNumber = null;
        try {
            accountNumber = decryptedAccountId.substring(6);
        } catch (Exception e) {
            LOGGER.error("Unable to extract account number from wpl BSB_ACCOUNT_NUMBER", e);
        }

        if (accountNumber != null) {
            LOGGER.info("Last accessed account number:{}.", accountNumber);
            UserPreferenceKey key = new UserPreferenceKey(gcmId, UserPreferenceEnum.LAST_ACCESSED_ACCOUNT.getPreferenceKey());
            UserPreference userPreference = new UserPreference(key, accountNumber);
            userPreferenceRepository.save(userPreference);
        }
    }

    /**
     * Entry point for external channels (Westpac Live)
     *
     * @param accountId         encrypted account id to forward to
     * @param originatingSystem external originating system name, to provide link back out
     */
    @SuppressWarnings({"squid:S00112"})
    @RequestMapping(value = HOMEPAGE, method = RequestMethod.GET)
    public String home(HttpSession session, HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "encrypted_accountnumber", required = false) String accountId, @RequestParam(value = "system_id", required = false) String originatingSystem) throws Exception {

        final boolean featureWPLSSOAccount = togglesService.findOne(new FailFastErrorsImpl())
                                                           .getFeatureToggle("wpl.sso.encryptedaccount");

        if (featureWPLSSOAccount && !StringUtils.isBlank(accountId) && !StringUtils.isBlank(originatingSystem)) {
            String gcmId = profileService.getGcmId();

            // Decrypt account id here.
            String decryptedAccountId = null;
            try {
                decryptedAccountId = decryptionService.aes256Decryption(accountId);
            } catch (Exception e) {
                LOGGER.error("Failed to decrypt account id. Input: {}.", accountId);
            }

            if (decryptedAccountId != null) {
                saveInitialAccountToDisplay(gcmId, decryptedAccountId);
            }
        }

        // If encrypted account id and originating system is provided then request is from external channel
        if (CHANNEL_WESTPAC_LIVE.equalsIgnoreCase(originatingSystem)) {
            session.setAttribute(ORIGINATING_SYSTEM, CHANNEL_WESTPAC_LIVE);
        }

        return root(request, response);
    }

    /**
     * Redirect to SPA on direct onboarding junction
     *
     * @return homepage string
     */
    @RequestMapping(value = HOMEPAGE_DIRECT_ONBOARDING, method = RequestMethod.GET)
    public String directOnboardingHome(HttpServletResponse response) {
        try {
            setCookies(response, profileService.getSamlToken().getCISKey().getId());
            return REDIRECT_DIRECT_ONBOARDING_HOME;
        } catch (IllegalStateException e) {
            LOGGER.error("Error occurred loading home page for CIS key. Error: {}", e);
            return REDIRECT_CLOSED_LOGON;
        }
    }

    private void setCookies(HttpServletResponse response, String user) {
        // Generate cookie for Omniature scraping to generate login metrics (login success).
        // Cookie explicitly set to unsecure so that it can be deleted by js (non-sensitive cookie).
        Cookie loginStatusCookie = new Cookie("LoginStatusCookie", "true");
        loginStatusCookie.setPath("/");
        loginStatusCookie.setSecure(false);
        loginStatusCookie.setHttpOnly(false);
        response.addCookie(loginStatusCookie);

        // Generate cookie for Omniature scraping to generate login metrics (user role)
        Cookie userRoleCookie = new Cookie("s_bt-seg", user);
        userRoleCookie.setPath("/");
        userRoleCookie.setSecure(true);
        loginStatusCookie.setHttpOnly(false);
        response.addCookie(userRoleCookie);
    }
}
