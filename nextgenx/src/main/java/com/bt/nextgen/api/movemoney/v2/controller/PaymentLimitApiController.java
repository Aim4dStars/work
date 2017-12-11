package com.bt.nextgen.api.movemoney.v2.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.movemoney.v2.model.DailyLimitDto;
import com.bt.nextgen.api.movemoney.v2.service.PaymentLimitDtoService;
import com.bt.nextgen.api.safi.facade.TwoFactorAuthenticationServiceImpl;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.api.validation.ErrorMapper;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.ServiceError;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.safi.model.SafiAnalyzeAndChallengeResponse;
import com.bt.nextgen.service.safi.model.SafiAuthenticateRequest;
import com.bt.nextgen.service.safi.model.SafiAuthenticateResponse;
import com.bt.nextgen.service.security.converter.HttpRequestConverter;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.bt.nextgen.web.validator.ValidationErrorCode;

@Controller("PaymentLimitApiControllerV2")
@RequestMapping(produces = "application/json")
@SuppressWarnings("all")
public class PaymentLimitApiController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentLimitApiController.class);

    @Autowired
    private PaymentLimitDtoService paymentLimitDtoService;

    @Autowired
    private TwoFactorAuthenticationServiceImpl twoFactorAuthenticationFacade;

    @Value("${api.movemoney.v2.version}")
    private String version;

    @RequestMapping(method = RequestMethod.POST, value = "${api.movemoney.v2.uri.submitPaymentLimit}")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.payee.view')")
    public @ResponseBody KeyedApiResponse<AccountKey> submitPaymentLimit(@PathVariable("account-id") String accountId,
            @ModelAttribute DailyLimitDto dailyLimitDto, HttpServletRequest request, HttpSession session) {

        AccountKey key = new AccountKey(EncodedString.toPlainText(accountId));
        dailyLimitDto.setKey(key);

        HttpRequestParams requestParams = HttpRequestConverter.toHttpRequestParams(request);
        SafiAnalyzeAndChallengeResponse analyzeResult = (SafiAnalyzeAndChallengeResponse) session
                .getAttribute("analyze-result-" + dailyLimitDto.getTransactionId());

        if (analyzeResult != null) {

            if (analyzeResult.getActionCode() && StringUtils.isNotBlank(dailyLimitDto.getSmsCode())) {
                SafiAuthenticateRequest safiAuthReq = new SafiAuthenticateRequest(requestParams, analyzeResult,
                        dailyLimitDto.getSmsCode());
                try {
                    SafiAuthenticateResponse authResult = twoFactorAuthenticationFacade.authenticate(safiAuthReq);
                    if (!authResult.isSuccessFlag()) {
                        logger.info("SAFI authentication failed for updating limits.");
                        populateError(dailyLimitDto, Constants.INVALID_SMS_CODE, "");
                        return new KeyedApiResponse(version, dailyLimitDto.getKey(), dailyLimitDto);
                    } else {
                        // removing session entry in case of success
                        session.removeAttribute("analyze-result-" + dailyLimitDto.getTransactionId());
                    }
                } catch (Exception e) {
                    throw new ApiException(version, e);
                }

            } else if (analyzeResult.getActionCode() && StringUtils.isBlank(dailyLimitDto.getSmsCode())) {
                // if authentication (getActionCode is true) is required and sms
                // code is blank.
                logger.info("SAFI authentication failed for updating limits.");
                populateError(dailyLimitDto, ValidationErrorCode.INVALID_SMS_CODE, "");
                return new KeyedApiResponse(version, dailyLimitDto.getKey(), dailyLimitDto);
            }
        } else {
            logger.info("SAFI analyze result is null.");
            populateError(dailyLimitDto, ValidationErrorCode.INVALID_SMS_CODE, "SAFI analyze request not found.");
            return new KeyedApiResponse(version, dailyLimitDto.getKey(), dailyLimitDto);
        }

        return new Submit<>(version, paymentLimitDtoService, new ErrorMapper() {

            @Override
            public List<DomainApiErrorDto> map(List<ValidationError> errors) {
                return new ArrayList<DomainApiErrorDto>();
            }
        }, dailyLimitDto).performOperation();

    }

    private void populateError(DailyLimitDto dailyLimitDto, String id, String reason) {
        List<ServiceError> errors = new ArrayList<>();
        ServiceError serviceError = new ServiceErrorImpl();
        serviceError.setReason(reason);
        serviceError.setId(id);
        errors.add(serviceError);
        dailyLimitDto.setErrors(errors);
    }
}
