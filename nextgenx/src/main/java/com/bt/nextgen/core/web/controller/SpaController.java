package com.bt.nextgen.core.web.controller;

import com.bt.nextgen.api.profile.v1.model.ProfileDetailsDto;
import com.bt.nextgen.api.profile.v1.service.ProfileDetailsDtoService;
import com.bt.nextgen.core.util.SETTINGS;
import com.bt.nextgen.core.web.util.View;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.apache.commons.lang3.BooleanUtils.toBoolean;

@Controller
public class SpaController {
    public static final String WRONG_APP_HOME = "/secure/app";
    public static final String APP_LOC = "apploc";
    public static final String APP_LAUNCH = "index.html";
    public static final String APP_LAUNCH_NO_KERNEL = "index-nw-standalone.html";
    public static final String APP_HOME = "/secure/app/";
    public static final String REDIRECT_APP_HOME = "redirect:" + APP_HOME;
    public static final String DIRECT_ONBOARDING_HOME = "/onboard/app/";
    public static final String REDIRECT_DIRECT_ONBOARDING_HOME = "redirect:" + DIRECT_ONBOARDING_HOME;
    public static final String LOAD_KERNEL = "loadKernel";

    @Autowired
    @Qualifier("profileDetailsServiceV1")
    private ProfileDetailsDtoService profileService;

    @RequestMapping(value = {APP_HOME, DIRECT_ONBOARDING_HOME}, method = RequestMethod.GET)
    public String showPage(HttpServletRequest request, HttpServletResponse response) {
        profileService.clearProfileCache();
        request.setAttribute(APP_LOC, SETTINGS.WEBCLIENT_RESOURCE_LOCATION.value() + (loadWithKernel(request) ? APP_LAUNCH : APP_LAUNCH_NO_KERNEL));
        return View.SINGLE_PAGE_APPLICATION;
    }

    @RequestMapping(value = {WRONG_APP_HOME}, method = RequestMethod.GET)
    public String getItRight() {
        return REDIRECT_APP_HOME;
    }

    /**
     * Support nicer url use, make all urls go to nice places.
     *
     * @return
     * @deprecated should use the correct url
     */
    @Deprecated
    @RequestMapping(value = {"/spa", "/spa/"}, method = RequestMethod.GET)
    public String app() {
        return REDIRECT_APP_HOME;
    }

    /**
     * This will support a nice name for the direct investor portal
     *
     * @return redirect to the actual location
     */
    @RequestMapping(value = "/public/page/investorpre")
    public String investorSite() {
        return "redirect:" + SETTINGS.MICROSITE_INVESTOR_HOSTING_URL.value();
    }

    private boolean loadWithKernel(HttpServletRequest request) {
        final ProfileDetailsDto profile = profileService.findOne(new ServiceErrorsImpl());
        return toBoolean(request.getParameter(LOAD_KERNEL)) || profile.isIntermediary() || profile.getHasAsimAccounts();
    }
}
