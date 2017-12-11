package com.bt.nextgen.login.service;

import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.login.web.controller.RegistrationResponse;
import com.bt.nextgen.login.web.model.CredentialsModel;
import com.bt.nextgen.login.web.model.RegistrationModel;
import com.bt.nextgen.login.web.model.SmsCodeModel;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerTokenRequest;
import com.bt.nextgen.service.safi.model.SafiAnalyzeAndChallengeResponse;
import com.bt.nextgen.service.security.model.HttpRequestParams;

import javax.servlet.http.HttpServletRequest;

public interface RegistrationService {

    SafiAnalyzeAndChallengeResponse validRegistration(SmsCodeModel model, HttpRequestParams requestParams) throws Exception;

    ValidateCredentialsResponse validateParty(CredentialsModel model);

    String createUser(RegistrationModel registrationModel, ServiceErrors errors) throws Exception;

    SamlToken generateUserSAML(CustomerTokenRequest customerTokenRequest) throws Exception;

    RegistrationResponse createRegistrationResponse(CustomerTokenRequest customerTokenRequest, String nextStepUrl, String investorEtpPath, String advisorEtpPath, String appContext) throws Exception;

    RegistrationResponse registrationResponseForOptionalTwoFA(CustomerTokenRequest customerTokenRequest, HttpServletRequest req, String nextStepUrl, String investorEtpPath, String advisorEtpPath, String appContext) throws Exception;

    void updateOnlineRegistrationStatus(boolean isTnCAccepted, ServiceErrors serviceErrors) throws Exception;

    void updateUserTwoFAStatusAsync(String gcmId);
}
