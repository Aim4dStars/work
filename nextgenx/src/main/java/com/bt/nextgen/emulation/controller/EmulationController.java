package com.bt.nextgen.emulation.controller;

import com.bt.nextgen.core.security.EmulationAuthenticationDetailsSource;
import com.bt.nextgen.core.security.EmulationRequestInfo;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.bt.nextgen.core.security.AuthorityUtil.grantAuthorities;

@Controller
/**
 * Service emulation requests through service operator screens 
 */
public class EmulationController {

    private static final Logger logger = LoggerFactory.getLogger(EmulationController.class);

    public static final String REDIRECT_SERVICE_OPS_HOME = "redirect:/secure/page/serviceOps/home";
    public static final String REDIRECT_SERVICE_OPS_ROOT = "redirect:/secure/page/serviceOps/";
    public static final String REDIRECT_SECURE_APP = "redirect:/secure/app";

    @Autowired
    private EmulationAuthenticationDetailsSource samlAuthenticationBuilder;

    @Autowired
    private UserProfileService userProfileService;


    @RequestMapping(value = "/secure/page/serviceOps/startEmulation", method = RequestMethod.GET)
    /**
     *
     * @param profileId profile_id of the user/job to perform emulation for
     * @param gcmId gcm_id of the person to perform emulation for
     * @param redirectAttributes
     * @return
     */
    public String startEmulation(@RequestParam("emulating") String profileId, @RequestParam String gcmId, @RequestHeader("iv-user") String principal, RedirectAttributes redirectAttributes) {
        logger.info("Start of emulation. Loading emulation target profile {} for user {} into Spring security session.", profileId, gcmId);
        Authentication emulatedUserAuth = buildAuthenticationObject(profileId, gcmId, principal);
        SecurityContextHolder.getContext().setAuthentication(emulatedUserAuth);

        String currentProfileId = userProfileService.getActiveProfile().getProfileId();
        String emulatedProfileId = userProfileService.getBaseProfile().getActiveProfile().getProfileId();
        logger.info("Emulation started - Current profile id: {}, Emulation profile id: {}", currentProfileId, emulatedProfileId);

        return REDIRECT_SECURE_APP;
    }


    /**
     * Retrieve the current Spring Authentication object
     *
     * @return Spring Authentication object
     */
    private Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }


    @RequestMapping(value = "/secure/page/serviceOps/stopEmulation", method = RequestMethod.GET)
    /**
     * Terminate emulation mode and return to service ops details page - If the parameters "op=search" are given, then return to search screen instead
     * @param op if the value "search" is provided, redirect back to the search screen instead of the user details page
     * @return
     */
    public String stopEmulation(@RequestParam(value = "op", required = false) String op) {
        Authentication currentAuth = getCurrentAuthentication();

        Profile profile = (Profile) currentAuth.getDetails();
        String clientKey = profile.getActiveProfile().getClientKey().getId();

        logger.info("Stopping emulation for emulated user with gcm_id {} and profile_id {}, and then redirecting back to {} screen",
                userProfileService.getActiveProfile().getProfileId(), userProfileService.getGcmId(), (StringUtils.isEmpty(op) ? "user detail" : "search"));

        Authentication originalAuth = Profile.getOriginalAuth();
        SecurityContextHolder.getContext().setAuthentication(originalAuth);

        String redirectUrl = REDIRECT_SERVICE_OPS_HOME;

        if (!StringUtils.isEmpty(op) && op.equalsIgnoreCase("search")) {
            logger.debug("Send user back to the service op search page");
        } else {
            if (profile != null && !StringUtils.isEmpty(clientKey)) {
                // Generate the URL for the service ops detail page for the emulated user
                EncodedString encodedProfileId = EncodedString.fromPlainText(clientKey);
                String hashedProfileId = encodedProfileId.toString();
                redirectUrl = REDIRECT_SERVICE_OPS_ROOT + hashedProfileId + "/detail";
            }
        }

        return redirectUrl;
    }


    /**
     * TODO: Probabaly belongs in a utility class.......
     * <p>
     * Generate Spring Authentication object for emulated user.
     * The original authentication object for the service operator is set as part of the authorities of the emulated user.<br>
     * The Profile for the emulated user is only partially generated here with minimal detail.
     * During the login process the Profile will self populate.
     *
     * @param profileId profile_id of the user/job to perform emulation for
     * @param gcmId     gcm_id of the person to perform emulation for
     * @return Spring authentication object which should be set into the Security context to initiate emulation
     * @throws AuthenticationException
     */
    private Authentication buildAuthenticationObject(String profileId, String gcmId, String principal) throws AuthenticationException {
        UsernamePasswordAuthenticationToken emulatedUserToken;
        Authentication currentAuth = getCurrentAuthentication();

        EmulationRequestInfo emulationRequest = new EmulationRequestInfo();
        emulationRequest.setGcmId(gcmId);
        emulationRequest.setProfileId(profileId);

        //TODO: Generate profile
        Profile profile = samlAuthenticationBuilder.buildDetails(emulationRequest);

        if (principal == null) {
            logger.warn("{} header not found in request.", "iv-user");
        }

        UserDetails emulatedUser = new User(StringUtils.defaultIfBlank(principal, profile.getGcmId()), "UNKNOWN", grantAuthorities(profile.getRoles()));

        Collection<? extends GrantedAuthority> emulatedAuthorities = emulatedUser.getAuthorities();

        GrantedAuthority origAuthority = new SwitchUserGrantedAuthority("ROLE_SERVICE_OP", currentAuth);

        List<GrantedAuthority> totalNewAuthorities = new ArrayList<GrantedAuthority>(emulatedAuthorities);
        totalNewAuthorities.add(origAuthority);

        emulatedUserToken = new UsernamePasswordAuthenticationToken(emulatedUser, emulatedUser.getPassword(), totalNewAuthorities);
        emulatedUserToken.setDetails(profile);

        return emulatedUserToken;
    }
}
