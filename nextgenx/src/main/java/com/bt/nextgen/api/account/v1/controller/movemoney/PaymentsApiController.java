package com.bt.nextgen.api.account.v1.controller.movemoney;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.bt.nextgen.service.*;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.account.v1.model.PaymentDto;
import com.bt.nextgen.api.account.v1.service.PayeeDtoService;
import com.bt.nextgen.api.account.v1.service.PaymentDtoService;
import com.bt.nextgen.api.fees.validation.PaymentDtoErrorMapper;
import com.bt.nextgen.api.safi.facade.TwoFactorAuthenticationServiceImpl;
import com.bt.nextgen.api.safi.model.EventModel;
import com.bt.nextgen.api.transaction.util.TransactionUtil;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.api.operation.Validate;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.repository.Bsb;
import com.bt.nextgen.payments.repository.BsbCodeRepository;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.safi.model.SafiAnalyzeAndChallengeResponse;
import com.bt.nextgen.service.safi.model.SafiAnalyzeRequest;
import com.bt.nextgen.service.safi.model.SafiAuthenticateRequest;
import com.bt.nextgen.service.safi.model.SafiAuthenticateResponse;
import com.bt.nextgen.service.safi.model.SafiChallengeRequest;
import com.bt.nextgen.service.security.converter.HttpRequestConverter;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.bt.nextgen.web.validator.ValidationErrorCode;

import static com.bt.nextgen.web.validator.ValidationErrorCode.INVALID_SMS_CODE;

/**
 * @deprecated Use V2
 */
@Deprecated
@Controller
@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
// Sonar issues fixed in v2
@SuppressWarnings("all")
public class PaymentsApiController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentsApiController.class);

    @Autowired
    @Qualifier("PaymentDtoServiceV1")
    private PaymentDtoService paymentDtoService;

    @Autowired
    @Qualifier("PayeeDtoServiceV1")
    private PayeeDtoService payeeDtoService;

    @Autowired
    private TwoFactorAuthenticationServiceImpl twoFactorAuthenticationService;

    @Autowired
    private PaymentDtoErrorMapper paymentsErrorMapper;

    @Autowired
    private BsbCodeRepository bsbCodeRepository;

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    public CmsService cmsService;

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.PAYMENTS)
    @PreAuthorize("@acctPermissionService.canTransact(#portfolioId, 'account.report.view')")
    public @ResponseBody ApiResponse getPayeesForAccount(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String portfolioId,
            @RequestParam(required = false, value = Attribute.MOVE_MONEY_MODEL) String movemoney) {
        String portfolioIdTest = EncodedString.toPlainText(portfolioId);
        List<ApiSearchCriteria> criteria = new ArrayList<ApiSearchCriteria>();
        criteria.add(
                new ApiSearchCriteria(Attribute.PORTFOLIO_ID, SearchOperation.EQUALS, portfolioIdTest, OperationType.STRING));
        if (movemoney != null) {
            criteria.add(
                    new ApiSearchCriteria(Attribute.MOVE_MONEY_MODEL, SearchOperation.EQUALS, movemoney, OperationType.STRING));
        }
        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, paymentDtoService, criteria).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.CONFIRM_PAYMENT)
    @PreAuthorize("(@acctPermissionService.canTransact(#portfolioId, 'account.payment.linked.create')) OR "
            + "(@acctPermissionService.canTransact(#portfolioId, 'account.payment.anyone.create'))")
    public @ResponseBody KeyedApiResponse<AccountKey> confirmPayment(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String portfolioId, @ModelAttribute PaymentDto paymentDto) {
        String accountId = EncodedString.toPlainText(portfolioId);

        AccountKey key = new AccountKey(accountId);
        paymentDto.setKey(key);

        return new Validate<>(ApiVersion.CURRENT_VERSION, paymentDtoService, paymentsErrorMapper, paymentDto).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.SUBMIT_PAYMENT)
    @PreAuthorize("(@acctPermissionService.canTransact(#portfolioId, 'account.payment.linked.create')) OR "
            + "(@acctPermissionService.canTransact(#portfolioId, 'account.payment.anyone.create'))")
    public @ResponseBody KeyedApiResponse<AccountKey> submitPayment(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String portfolioId, @ModelAttribute PaymentDto paymentDto,
            HttpSession session,
            HttpServletRequest request) {
        AccountKey key = new AccountKey(EncodedString.toPlainText(portfolioId));
        paymentDto.setKey(key);

        String userAgent = request.getHeader("User-Agent");
        if(null != userAgent) {
            String channel = userAgent.toLowerCase().indexOf("mobile") != -1 ? "mobile" : "online";
            Code channelCode = staticIntegrationService.loadCodeByAvaloqId(CodeCategory.PAYMENT_CHANNEL_TYPE, channel, new ServiceErrorsImpl());
            if( null != channelCode && null != channelCode.getCodeId() ) {
                paymentDto.setBusinessChannel(channelCode.getCodeId());
            }
        }
        paymentDto.setClientIp(request.getRemoteAddr());

        KeyedApiResponse response = new Submit<>(ApiVersion.CURRENT_VERSION, paymentDtoService, paymentsErrorMapper, paymentDto)
                .performOperation();
        // Add payments to session for validation
        if (((PaymentDto) response.getData()).getErrors() == null || ((PaymentDto) response.getData()).getErrors().isEmpty()) {
            PaymentDto updatedDto = (PaymentDto) response.getData();
            TransactionUtil.updatePaymentsAmountsToSessionAfterSubmit(updatedDto, session);
        }
        return response;
    }

    @Deprecated
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.SAFI_ANALYZE)
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public AjaxResponse safiAnalyze(@RequestParam(value = Attribute.PAYEE_TYPE, required = false) String payeeType,
            HttpServletRequest request, HttpSession session) {
        try {
            /* Start :: Prepare Event Model for Safi Analyze */
            EventModel eventModel = new EventModel();
            switch (payeeType) {
                case "LINKED":
                    eventModel.setPayeeType(PayeeType.LINKED);
                    eventModel.setClientDefinedEventType("ADD_PAYEE");
                    eventModel.setEventDescription("LINKED");
                    break;
                case "BPAY":
                    eventModel.setPayeeType(PayeeType.BPAY);
                    eventModel.setClientDefinedEventType("ADD_PAYEE");
                    eventModel.setEventDescription("BPAY");
                    break;
                case "PAY_ANYONE":
                    eventModel.setPayeeType(PayeeType.PAY_ANYONE);
                    eventModel.setClientDefinedEventType("ADD_PAYEE");
                    eventModel.setEventDescription("PAY_ANYONE");
                    break;
                default:
                    eventModel.setPayeeType(PayeeType.PAY_ANYONE);
                    eventModel.setClientDefinedEventType("CHANGE_DAILY_LIMIT");
                    eventModel.setEventDescription("CHANGE_DAILY_LIMIT");
                    break;
            }

            HttpRequestParams requestParams = HttpRequestConverter.toHttpRequestParams(request);
            SafiAnalyzeRequest analyzeRequest = new SafiAnalyzeRequest();
            analyzeRequest.setEventModel(eventModel);
            analyzeRequest.setHttpRequestParams(requestParams);

            ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
            SafiAnalyzeAndChallengeResponse analyzeResult = twoFactorAuthenticationService.analyze(analyzeRequest, serviceErrors);

            if (null != analyzeResult) {
                session.setAttribute("safiAnalyzeResult", analyzeResult);
                String deviceNumber = populateDeviceNumber();// Get Device
                // Number & Mask it
                int length = deviceNumber.length();
                deviceNumber = deviceNumber.substring(0, length - 7) + "####" + deviceNumber.substring(length - 3, length);
                if (analyzeResult.getActionCode())
                    return new AjaxResponse(true, deviceNumber + "|" + analyzeResult.getTransactionId());
                else
                    return new AjaxResponse(false, deviceNumber + "|" + analyzeResult.getTransactionId());
            } else {
                logger.info("Safi Analyze response is null.");
            }
        } catch (Exception e) {
            // session.removeAttribute("safiAnalyzeResult");
            logger.error("Safi Analyze Exception", e);
        }
        return new AjaxResponse(false, Attribute.SYSTEM_UNAVAILABLE);
    }

    @Deprecated
    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.SAFI_CHALLENGE)
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody AjaxResponse safiChallenge(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String portfolioId,
            @ModelAttribute PaymentDto paymentDto, HttpSession session, HttpServletRequest request) {
        AccountKey key = new AccountKey(EncodedString.toPlainText(portfolioId));
        paymentDto.setKey(key);
        paymentDto.setOpType(Attribute.ADD);


        HttpRequestParams requestParams = HttpRequestConverter.toHttpRequestParams(request);
        SafiAnalyzeAndChallengeResponse analyzeResult = (SafiAnalyzeAndChallengeResponse) session
                .getAttribute("safiAnalyzeResult");
        if (analyzeResult != null && analyzeResult.getActionCode()) {
            logger.info("analyse result usernames are main{} identificationData {}", analyzeResult.getUserName(),
                    analyzeResult.getIdentificationData().getUserName());

            SafiChallengeRequest challengeRequest = new SafiChallengeRequest(requestParams, analyzeResult);
            SafiAnalyzeAndChallengeResponse challengeResponse = twoFactorAuthenticationService.challenge(challengeRequest);

            if (challengeResponse == null) {
                logger.info("safi challenge request return null.");
                throw new BadRequestException(ApiVersion.CURRENT_VERSION, ValidationErrorCode.SYSTEM_UNAVAILABLE);
            } else if (challengeResponse.getActionCode() == false) {
                return new AjaxResponse(false, "Unable to perform challenge");
            }
        }
        return new AjaxResponse(true, "SAFI challenge request successful.");
    }

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.ADD_PAYEE)
    @PreAuthorize("@acctPermissionService.canTransact(#portfolioId, 'account.payee.view')")
    public @ResponseBody KeyedApiResponse addPayee(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String portfolioId,
            @ModelAttribute PaymentDto paymentDto, @RequestParam(required = false, value = Attribute.SMS_CODE) String smsCode,
            HttpSession session, HttpServletRequest request) throws Exception {

        AccountKey key = new AccountKey(EncodedString.toPlainText(portfolioId));
        SafiAuthenticateResponse authResult = new SafiAuthenticateResponse();
        paymentDto.setKey(key);
        paymentDto.setOpType(Attribute.ADD);
        if(null!=paymentDto && null!= paymentDto.getToPayteeDto()&& null != paymentDto.getToPayteeDto().getPayeeType() && PayeeType.LINKED.toString().equalsIgnoreCase(paymentDto.getToPayteeDto().getPayeeType()) ){
            paymentDto.getToPayteeDto().setManuallyVerifiedFlag("true");
        }

        // validate before authenticating SMS code.
        payeeDtoService.validate(paymentDto, new ServiceErrorsImpl());
        if (paymentDto.getErrors() != null && paymentDto.getErrors().size() > 0) {
            // return if validation failed
            logger.info("validation failed for adding payee.");
            return new KeyedApiResponse(ApiVersion.CURRENT_VERSION, paymentDto.getKey(), paymentDto);
        }
        SafiAnalyzeAndChallengeResponse analyzeResult = (SafiAnalyzeAndChallengeResponse) session
                .getAttribute("analyze-result-" + paymentDto.getTransactionId());
        // TODO - to identify the case where analyzeRequest can be null
        if (analyzeResult != null) {
            // only if action code is true and sms code is not blank
            if (analyzeResult.getActionCode() && StringUtils.isNotBlank(smsCode)) {
                HttpRequestParams requestParams = HttpRequestConverter.toHttpRequestParams(request);
                // matching request transaction id
                if (StringUtils.isNotBlank(analyzeResult.getTransactionId())
                        && analyzeResult.getTransactionId().equals(paymentDto.getTransactionId())) {
                    SafiAuthenticateRequest authenticateRequest = new SafiAuthenticateRequest(requestParams, analyzeResult,
                            smsCode);
                    authResult = twoFactorAuthenticationService.authenticate(authenticateRequest);
                    // TODO - If authentication is failed three time.
                    if (!authResult.isSuccessFlag()) {
                        logger.info("SMS code is mismatched.");
                        return populateErrorCode(paymentDto, authResult);
                    } else {
                        // removing session entry in case of success
                        session.removeAttribute("analyze-result-" + paymentDto.getTransactionId());
                    }
                } else {
                    // in case of transaction id is not same
                    logger.info("Passed transaction id is not valid.");
                    return populateErrorCode(paymentDto, authResult);
                }
            } else if (analyzeResult.getActionCode() && StringUtils.isBlank(smsCode)) {
                // if authentication (getActionCode is true) is required and sms
                // code is blank.
                logger.info("SAFI authentication is required.");
                return populateErrorCode(paymentDto, authResult);
            }
        } else {
            logger.info("SAFI analyze result is null.");
            return populateErrorCode(paymentDto, authResult);
        }
        return new Submit<>(ApiVersion.CURRENT_VERSION, payeeDtoService, paymentsErrorMapper, paymentDto).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.DELETE_PAYEE)
    @PreAuthorize("@acctPermissionService.canTransact(#portfolioId, 'account.payee.view')")
    public @ResponseBody KeyedApiResponse deletePayee(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String portfolioId, @ModelAttribute PaymentDto paymentDto) {

        AccountKey key = new AccountKey(EncodedString.toPlainText(portfolioId));
        paymentDto.setKey(key);
        paymentDto.setOpType(Attribute.DELETE);
        return new Submit<>(ApiVersion.CURRENT_VERSION, payeeDtoService, paymentsErrorMapper, paymentDto).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.UPDATE_PAYEE)
    @PreAuthorize("@acctPermissionService.canTransact(#portfolioId, 'account.payee.view')")
    public @ResponseBody KeyedApiResponse updatePayee(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String portfolioId, @ModelAttribute PaymentDto paymentDto) {
        logger.info("PRM TESTING :: In V1");
        AccountKey key = new AccountKey(EncodedString.toPlainText(portfolioId));
        paymentDto.setKey(key);
        paymentDto.setOpType(Attribute.UPDATE);
        return new Submit<>(ApiVersion.CURRENT_VERSION, payeeDtoService, paymentsErrorMapper, paymentDto).performOperation();

    }

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.VALIDATE_BSB)
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody boolean validateBSB(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String portfolioId,
            @RequestParam(required = false, value = Attribute.BSB_CODE) String bsbCode) {
        Bsb resultBsb = bsbCodeRepository.load(bsbCode);
        if (null != resultBsb && null != resultBsb.getBsbCode())
            return true;
        else
            return false;

    }

    private String populateDeviceNumber() {
        IndividualDetailImpl clientDetail = (IndividualDetailImpl) clientIntegrationService
                .loadClientDetails(userProfileService.getActiveProfile().getClientKey(), new FailFastErrorsImpl());
        if (null != clientDetail) {
            List<Phone> listPhone = clientDetail.getPhones();
            for (Phone phone : listPhone) {
                if (phone.getType().equals(AddressMedium.MOBILE_PHONE_PRIMARY))
                    return phone.getNumber();
            }
        }
        return null;
    }

    private KeyedApiResponse populateErrorCode(PaymentDto paymentDto, SafiAuthenticateResponse authResult) {
        List<ServiceError> errors = new ArrayList<>();
        ServiceError serviceError = new ServiceErrorImpl();
        serviceError.setId(Constants.INVALID_SMS_CODE);

        if (authResult != null && null != authResult.getStatusCode()) {
            serviceError.setReason(cmsService.getContent(authResult.getDisplayMessageCode()));
        }
        else {
            serviceError.setReason(cmsService.getContent(INVALID_SMS_CODE));
        }

        errors.add(serviceError);
        paymentDto.setErrors(errors);
        return new KeyedApiResponse(ApiVersion.CURRENT_VERSION, paymentDto.getKey(), paymentDto);
    }


}
