package com.bt.nextgen.api.account.v1.controller.movemoney;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.account.v1.model.DailyLimitDto;
import com.bt.nextgen.api.account.v1.service.PaymentLimitDtoService;
import com.bt.nextgen.api.safi.facade.TwoFactorAuthenticationServiceImpl;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
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

/**
 * @deprecated Use V2
 */
@Deprecated
@Controller
@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
// Sonar issues fixed in v2
@SuppressWarnings("all")
public class PaymentLimitApiController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentLimitApiController.class);
    @Autowired
    @Qualifier("PaymentLimitDtoServiceV1")
    private PaymentLimitDtoService paymentLimitDtoService;

    @Autowired
    private TwoFactorAuthenticationServiceImpl twoFactorAuthenticationFacade;

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.SUBMIT_PAYMENT_LIMIT)
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#portfolioId, 'account.payee.view')")
    public @ResponseBody KeyedApiResponse<AccountKey> submitPaymentLimit(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String portfolioId,
            @ModelAttribute DailyLimitDto dailyLimitDto, HttpServletRequest request, HttpSession session) {

        AccountKey key = new AccountKey(EncodedString.toPlainText(portfolioId));
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
                        return new KeyedApiResponse(ApiVersion.CURRENT_VERSION, dailyLimitDto.getKey(), dailyLimitDto);
                    } else {
                        // removing session entry in case of success
                        session.removeAttribute("analyze-result-" + dailyLimitDto.getTransactionId());
                    }
                } catch (Exception e) {
                    throw new ApiException(ApiVersion.CURRENT_VERSION, e);
                }

            } else if (analyzeResult.getActionCode() && StringUtils.isBlank(dailyLimitDto.getSmsCode())) {
                // if authentication (getActionCode is true) is required and sms
                // code is blank.
                logger.info("SAFI authentication failed for updating limits.");
                populateError(dailyLimitDto, ValidationErrorCode.INVALID_SMS_CODE, "");
                return new KeyedApiResponse(ApiVersion.CURRENT_VERSION, dailyLimitDto.getKey(), dailyLimitDto);
            }
        } else {
            logger.info("SAFI analyze result is null.");
            populateError(dailyLimitDto, ValidationErrorCode.INVALID_SMS_CODE, "SAFI analyze request not found.");
            return new KeyedApiResponse(ApiVersion.CURRENT_VERSION, dailyLimitDto.getKey(), dailyLimitDto);
        }

        return new Submit<>(ApiVersion.CURRENT_VERSION, paymentLimitDtoService, new ErrorMapper() {

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
