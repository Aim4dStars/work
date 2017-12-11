package com.bt.nextgen.api.safi.facade;

import com.bt.nextgen.core.exception.ServiceException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.safi.TwoFactorAuthenticationIntegrationService;
import com.bt.nextgen.service.safi.model.*;
import com.bt.nextgen.service.security.service.TwoFactorAuthenticationHttpRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.MapBindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Component("twoFactorAuthenticationService")
public class TwoFactorAuthenticationServiceImpl {

    @Autowired
    private TwoFactorAuthenticationIntegrationService twoFactorAuthenticationService;

    @Autowired
    private TwoFactorAuthenticationHttpRequestValidator twoFactorAuthenticationHttpRequestValidator;

    @Autowired
    protected UserProfileService profileService;

    @Autowired
    private HttpSession session;

    public SafiAnalyzeAndChallengeResponse analyze(TwoFactorAuthenticationBasicRequest safiAnalyzeRequest) {
        return analyze(safiAnalyzeRequest, new ServiceErrorsImpl());
    }

    public SafiAnalyzeAndChallengeResponse analyze(TwoFactorAuthenticationBasicRequest safiAnalyzeRequest, ServiceErrors serviceErrors) {
        SafiAnalyzeRequest request = (SafiAnalyzeRequest) safiAnalyzeRequest;
        SafiAnalyzeAndChallengeResponse result = twoFactorAuthenticationService.analyze(request.getEventModel(), request.getRequestParams(), serviceErrors);

        Map<String, String> map = new HashMap<>();
        MapBindingResult err = new MapBindingResult(map, HttpServletRequest.class.getName());
        twoFactorAuthenticationHttpRequestValidator.validate(request, err);

        if (!err.getAllErrors().isEmpty()) {
            serviceErrors.addError(new ServiceErrorImpl("One or more invalid request headers are present"));
            throw new IllegalArgumentException("One or more invalid request headers are present");
        }

        return result;
    }


    public SafiAnalyzeAndChallengeResponse challenge(TwoFactorAuthenticationBasicRequest safiChallengeRequest) {
        return challenge(safiChallengeRequest, new ServiceErrorsImpl());
    }

    public SafiAnalyzeAndChallengeResponse challengeFromNotAuthCtx(TwoFactorAuthenticationBasicRequest safiChallengeRequest) {

        SafiChallengeRequest request = (SafiChallengeRequest) safiChallengeRequest;

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        SafiAnalyzeAndChallengeResponse result = twoFactorAuthenticationService.challengeFromNotAuthCtx(request.getAnalyzeResult(), request.getRequestParams(), errors);

        if (errors.hasErrors()) {
            ServiceException serviceException = new ServiceException(String.format("SAFI challenge call has failed: %s", errors.getErrorMessagesForScreenDisplay()));
            serviceException.setServiceErrors(errors);
            throw serviceException;
        }

        return result;
    }

    public SafiAnalyzeAndChallengeResponse challenge(TwoFactorAuthenticationBasicRequest safiChallengeRequest, ServiceErrors serviceErrors) {
        //test
        SafiChallengeRequest request = (SafiChallengeRequest) safiChallengeRequest;
        SafiAnalyzeAndChallengeResponse result = twoFactorAuthenticationService.challenge(request.getAnalyzeResult(), request.getRequestParams(), serviceErrors);
        return result;
    }

    @SuppressWarnings("squid:S00112") // Generic exceptions should never be thrown
    public SafiAuthenticateResponse authenticate(SafiAuthenticateRequest safiAuthenticateRequest) throws Exception {
        return authenticate(safiAuthenticateRequest, new FailFastErrorsImpl());
    }

    @SuppressWarnings("squid:S00112") // Generic exceptions should never be thrown
    public SafiAuthenticateResponse authenticate(SafiAuthenticateRequest safiAuthenticateRequest, ServiceErrors serviceErrors)
            throws Exception {
        SafiAuthenticateResponse result = twoFactorAuthenticationService.authenticate(safiAuthenticateRequest.getSmsCode(),
                safiAuthenticateRequest.getRequestParams(), safiAuthenticateRequest.getAnalyzeResult(), serviceErrors);
        return result;
    }

    /**
     * @param safiAuthenticateRequest
     * @return
     */
    @SuppressWarnings({"squid:S00112", "squid:S1172"})
    // Generic exceptions should never be thrown, Unused method parameters should be removed
    public SafiAuthenticateResponse authenticate(SafiAuthenticateRequest safiAuthenticateRequest, String encodedSamlToken, ServiceErrors serviceErrors) throws Exception {
        safiAuthenticateRequest.getRequestParams();
        SafiAuthenticateResponse result = twoFactorAuthenticationService.authenticate(safiAuthenticateRequest.getSmsCode(), safiAuthenticateRequest.getRequestParams(), safiAuthenticateRequest.getAnalyzeResult(), serviceErrors);
        return result;
    }

    public boolean is2FAVerified(String eventType) {
        if (profileService.isInvestor()) {
            Boolean is2FARequired = (Boolean) session.getAttribute(eventType + "_2FA_VERIFIED");
            if (is2FARequired != null) {
                return is2FARequired;
            }
            else {
                return false;
            }
        }
        return true;
    }
}
