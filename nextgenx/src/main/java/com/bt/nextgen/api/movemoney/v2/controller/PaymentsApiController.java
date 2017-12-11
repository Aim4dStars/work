package com.bt.nextgen.api.movemoney.v2.controller;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.movemoney.v2.model.BsbValidationDto;
import com.bt.nextgen.api.movemoney.v2.model.EndPaymentDto;
import com.bt.nextgen.api.movemoney.v2.model.PaymentDto;
import com.bt.nextgen.api.movemoney.v2.service.PayeeDtoService;
import com.bt.nextgen.api.movemoney.v2.service.PaymentDtoService;
import com.bt.nextgen.api.movemoney.v2.service.SavedPaymentDtoService;
import com.bt.nextgen.api.movemoney.v2.validation.MovemoneyDtoErrorMapper;
import com.bt.nextgen.api.safi.controller.TwoFactorAuthenticationBaseController;
import com.bt.nextgen.api.safi.facade.TwoFactorAuthenticationServiceImpl;
import com.bt.nextgen.api.safi.model.EventModel;
import com.bt.nextgen.api.safi.model.SafiResponseDto;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.SearchByKeyedCriteria;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.api.operation.Update;
import com.bt.nextgen.core.api.operation.Validate;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.repository.Bsb;
import com.bt.nextgen.payments.repository.BsbCodeRepository;
import com.bt.nextgen.payments.web.model.AccountVerificationStatus;
import com.bt.nextgen.payments.web.model.SafiSMSModel;
import com.bt.nextgen.payments.web.model.TwoFactorAccountVerificationKey;
import com.bt.nextgen.payments.web.model.TwoFactorRuleModel;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.rules.AvaloqRulesIntegrationService;
import com.bt.nextgen.service.avaloq.rules.RuleUpdateParams;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.movemoney.PaymentActionType;
import com.bt.nextgen.service.safi.model.SafiAnalyzeAndChallengeResponse;
import com.bt.nextgen.service.safi.model.SafiAnalyzeRequest;
import com.bt.nextgen.service.safi.model.SafiAuthenticateRequest;
import com.bt.nextgen.service.safi.model.SafiAuthenticateResponse;
import com.bt.nextgen.service.safi.model.SafiChallengeRequest;
import com.bt.nextgen.service.security.converter.HttpRequestConverter;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.bt.nextgen.web.validator.ValidationErrorCode;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.bt.nextgen.web.validator.ValidationErrorCode.INVALID_SMS_CODE;

@Controller("PaymentsApiControllerV2")
@RequestMapping(produces = "application/json")
@SuppressWarnings("all")
public class PaymentsApiController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentsApiController.class);

    @Autowired
    private PaymentDtoService paymentDtoService;

    @Autowired
    private PayeeDtoService payeeDtoService;

    @Autowired
    private SavedPaymentDtoService savedPaymentDtoService;

    @Autowired
    private TwoFactorAuthenticationServiceImpl twoFactorAuthenticationService;

    @Autowired
    private MovemoneyDtoErrorMapper movemoneytDtoErrorMapper;

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
    private TwoFactorAuthenticationBaseController twoFactorAuthenticationBaseController;

    @Autowired
    private AvaloqRulesIntegrationService rulesIntegrationService;

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @Value("${api.movemoney.v2.version}")
    private String version;

    private final String ACCOUNT_ID_PARAM = "account-id";

    @RequestMapping(method = RequestMethod.GET, value = "${api.movemoney.v2.uri.paymentPayees}")
    @PreAuthorize("@acctPermissionService.canTransact(#accountId, 'account.report.view')")
    public
    @ResponseBody
    ApiResponse getPayeesForAccount(@PathVariable(ACCOUNT_ID_PARAM) String accountId,
                                    @RequestParam(required = false, value = Attribute.MOVE_MONEY_MODEL) String movemoney) {

        List<ApiSearchCriteria> criteria = new ArrayList<ApiSearchCriteria>();
        if (movemoney != null) {
            criteria.add(
                    new ApiSearchCriteria(Attribute.MOVE_MONEY_MODEL, SearchOperation.EQUALS, movemoney, OperationType.STRING));
        }
        return new SearchByKeyedCriteria<>(version, paymentDtoService, new AccountKey(accountId), criteria).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.movemoney.v2.uri.confirmPayment}")
    @PreAuthorize("(@acctPermissionService.canTransact(#accountId, 'account.payment.linked.create')) OR "
            + "(@acctPermissionService.canTransact(#accountId, 'account.payment.anyone.create'))")
    public
    @ResponseBody
    KeyedApiResponse<AccountKey> confirmPayment(@PathVariable(ACCOUNT_ID_PARAM) String accountId,
                                                @RequestBody PaymentDto paymentDto) {

        paymentDto.setKey(new AccountKey(accountId));
        return new Validate<>(version, paymentDtoService, movemoneytDtoErrorMapper, paymentDto).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.movemoney.v2.uri.submitPayment}")
    @PreAuthorize("(@acctPermissionService.canTransact(#accountId, 'account.payment.linked.create')) OR "
            + "(@acctPermissionService.canTransact(#accountId, 'account.payment.anyone.create'))")
    public
    @ResponseBody
    KeyedApiResponse<AccountKey> submitPayment(@PathVariable(ACCOUNT_ID_PARAM) String accountId,
                                               @RequestBody PaymentDto paymentDto,
                                               HttpServletRequest request) {
        paymentDto.setKey(new AccountKey(accountId));
        String userAgent = request.getHeader("User-Agent");
        if(null != userAgent) {
            String channel = userAgent.toLowerCase().indexOf("mobile") != -1 ? "mobile" : "online";
            Code channelCode = staticIntegrationService.loadCodeByAvaloqId(CodeCategory.PAYMENT_CHANNEL_TYPE, channel, new ServiceErrorsImpl());
            if( null != channelCode && null != channelCode.getCodeId() ) {
                paymentDto.setBusinessChannel(channelCode.getCodeId());
            }
        }
        paymentDto.setClientIp(request.getRemoteAddr());
        return new Submit<>(version, paymentDtoService, movemoneytDtoErrorMapper, paymentDto).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.movemoney.v2.uri.savePayment}")
    @PreAuthorize("(@acctPermissionService.canTransact(#accountId, 'account.payment.linked.create')) OR "
            + "(@acctPermissionService.canTransact(#accountId, 'account.payment.anyone.create'))")
    public
    @ResponseBody
    KeyedApiResponse<AccountKey> savePayment(@PathVariable(ACCOUNT_ID_PARAM) String accountId,
                                             @RequestBody PaymentDto paymentDto) {
        if (StringUtils.isEmpty(accountId)) {
            throw new IllegalArgumentException("Account id is not valid");
        }
        if (StringUtils.isEmpty(paymentDto.getPaymentAction())) {
            throw new IllegalArgumentException("Payment action not valid");
        }
        if (PaymentActionType.fromLabel(paymentDto.getPaymentAction()) == null) {
            throw new IllegalArgumentException("Invalid payment action");
        }
        paymentDto.setKey(new AccountKey(accountId));
        return new Update<>(version, paymentDtoService, movemoneytDtoErrorMapper, paymentDto).performOperation();
    }


    @RequestMapping(method = RequestMethod.POST, value = "${api.movemoney.v2.uri.endPayment}")
    @PreAuthorize("(@acctPermissionService.canTransact(#accountId, 'account.payment.linked.create')) OR "
            + "(@acctPermissionService.canTransact(#accountId, 'account.payment.anyone.create'))")
    public
    @ResponseBody
    KeyedApiResponse<AccountKey> endPayment(@PathVariable(ACCOUNT_ID_PARAM) String accountId,
                                            @RequestBody EndPaymentDto endPaymentDto) {
        endPaymentDto.setKey(new AccountKey(accountId));
        return new Submit<>(version, paymentDtoService, movemoneytDtoErrorMapper, endPaymentDto).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.movemoney.v2.uri.cancelSavedPayment}")
    @PreAuthorize("(@acctPermissionService.canTransact(#accountId, 'account.payment.linked.create')) OR "
            + "(@acctPermissionService.canTransact(#accountId, 'account.payment.anyone.create'))")
    public
    @ResponseBody
    ApiResponse cancelPayment(@PathVariable(ACCOUNT_ID_PARAM) String accountId,
                              @RequestBody PaymentDto paymentDto) {
        if (StringUtils.isEmpty(accountId)) {
            throw new IllegalArgumentException("Account id is not valid");
        }
        if (StringUtils.isEmpty(paymentDto.getPaymentAction())) {
            throw new IllegalArgumentException("Payment action required for cancel save payment");
        }
        if (PaymentActionType.fromLabel(paymentDto.getPaymentAction()) == null) {
            throw new IllegalArgumentException("Invalid payment action");
        }
        paymentDto.setKey(new AccountKey(accountId));
        return new Update<>(version, paymentDtoService, movemoneytDtoErrorMapper, paymentDto).performOperation();
    }


    @Deprecated
    @RequestMapping(method = RequestMethod.GET, value = "${api.movemoney.v2.uri.safiAnalyze}")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public AjaxResponse safiAnalyze(@RequestParam(value = "payeeType", required = false) String payeeType,
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
                if (analyzeResult.getActionCode()) {
                    return new AjaxResponse(true, deviceNumber + "|" + analyzeResult.getTransactionId());
                }
                else {
                    return new AjaxResponse(false, deviceNumber + "|" + analyzeResult.getTransactionId());
                }
            }
            else {
                logger.info("Safi Analyze response is null.");
            }
        }
        catch (Exception e) {
            // session.removeAttribute("safiAnalyzeResult");
            logger.error("Safi Analyze Exception", e);
        }

        return new AjaxResponse(false, Attribute.SYSTEM_UNAVAILABLE);
    }

    @Deprecated
    @RequestMapping(method = RequestMethod.POST, value = "${api.movemoney.v2.uri.safiChallenge}")
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    AjaxResponse safiChallenge(@PathVariable(ACCOUNT_ID_PARAM) String portfolioId,
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
                throw new BadRequestException(version, ValidationErrorCode.SYSTEM_UNAVAILABLE);
            }
            else if (challengeResponse.getActionCode() == false) {
                return new AjaxResponse(false, "Unable to perform challenge");
            }
        }

        return new AjaxResponse(true, "SAFI challenge request successful.");
    }

    @SuppressWarnings("squid:S00112") // Generic exceptions should never be thrown
    @RequestMapping(method = RequestMethod.POST, value = "${api.movemoney.v2.uri.safiVerify}", produces = "application/json")
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    AjaxResponse authenticacteSmsCode(@PathVariable(ACCOUNT_ID_PARAM) String portfolioId, @RequestBody SafiSMSModel smsModel,
                                      HttpSession session, HttpServletRequest request) throws Exception {

        SafiResponseDto safiResponseDto = twoFactorAuthenticationBaseController.safiAuthenticate(smsModel.getTransactionID(), smsModel.getSmsCode(), session, request);

        if (safiResponseDto.isSuccessFlag()) {
            TwoFactorRuleModel ruleModel = (TwoFactorRuleModel) session.getAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER);
            AccountVerificationStatus accountVerificationStatus = ruleModel.getAccountStatusMap().get(new TwoFactorAccountVerificationKey(smsModel.getAccountID(), smsModel.getBsb()));

            if (accountVerificationStatus != null) {
                accountVerificationStatus.setAuthenticationDone(true);
                session.setAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER, ruleModel);
                logger.info("Payment SAFI Auth: Updating rule with id:{} in avaloq.", accountVerificationStatus.getRuleID());
                rulesIntegrationService.updateAvaloqRuleAsync(accountVerificationStatus.getRuleID(), Collections.singletonMap(RuleUpdateParams.STATUS, Boolean.valueOf(true)));
            }
            else {
                logger.error("Unable to find verification status for accoundID:{} in the session", smsModel.getAccountID());
                return new AjaxResponse(false, "Unable to find verification status for accoundId");
            }
        }

        return new AjaxResponse(safiResponseDto.isSuccessFlag(), safiResponseDto.isSuccessFlag() ? "SAFI verification is successful." : safiResponseDto.getErrorMessage());
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.movemoney.v2.uri.addPayee}")
    @PreAuthorize("@acctPermissionService.canTransact(#portfolioId, 'account.payee.view')")
    public
    @ResponseBody
    KeyedApiResponse addPayee(@PathVariable(ACCOUNT_ID_PARAM) String portfolioId,
                              @ModelAttribute PaymentDto paymentDto, @RequestParam(required = false, value = Attribute.SMS_CODE) String smsCode,
                              HttpSession session, HttpServletRequest request) throws Exception {
        logger.info("PRM TESTING :: In V2");
        AccountKey key = new AccountKey(EncodedString.toPlainText(portfolioId));
        SafiAuthenticateResponse authResult = new SafiAuthenticateResponse();
        paymentDto.setKey(key);
        paymentDto.setOpType(Attribute.ADD);
        if(null!=paymentDto && null!= paymentDto.getToPayeeDto()&& null != paymentDto.getToPayeeDto().getPayeeType() && PayeeType.LINKED.toString().equalsIgnoreCase(paymentDto.getToPayeeDto().getPayeeType()) ){
            paymentDto.getToPayeeDto().setManuallyVerifiedFlag("true");
        }

        // validate before authenticating SMS code.
        payeeDtoService.validate(paymentDto, new ServiceErrorsImpl());
        if (paymentDto.getErrors() != null && paymentDto.getErrors().size() > 0) {
            // return if validation failed
            logger.info("validation failed for adding payee.");
            return new KeyedApiResponse(version, paymentDto.getKey(), paymentDto);
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
                    }
                    else {
                        // removing session entry in case of success
                        session.removeAttribute("analyze-result-" + paymentDto.getTransactionId());
                    }
                }
                else {
                    // in case of transaction id is not same
                    logger.info("Passed transaction id is not valid.");
                    return populateErrorCode(paymentDto, authResult);
                }
            }
            else if (analyzeResult.getActionCode() && StringUtils.isBlank(smsCode)) {
                // if authentication (getActionCode is true) is required and sms
                // code is blank.
                logger.info("SAFI authentication is required.");
                return populateErrorCode(paymentDto, authResult);
            }
        }
        else {
            logger.info("SAFI analyze result is null.");
            return populateErrorCode(paymentDto, authResult);
        }

        return new Submit<>(version, payeeDtoService, movemoneytDtoErrorMapper, paymentDto).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.movemoney.v2.uri.addLinkedPayee}")
    @PreAuthorize("@acctPermissionService.canTransact(#portfolioId, 'account.payee.view')")
    public
    @ResponseBody
    KeyedApiResponse addLinkedPayee(@PathVariable(ACCOUNT_ID_PARAM) String portfolioId,
                              @ModelAttribute PaymentDto paymentDto,
                              HttpSession session, HttpServletRequest request) throws Exception {
        AccountKey key = new AccountKey(EncodedString.toPlainText(portfolioId));
        paymentDto.setKey(key);
        paymentDto.setOpType(Attribute.ADD);
        Boolean is2FVerified = (Boolean) session.getAttribute("LINKED_2FA_VERIFIED");
        if(!is2FVerified) {
            setErrors(paymentDto, cmsService.getContent(INVALID_SMS_CODE));
            return new KeyedApiResponse(version, paymentDto.getKey(), paymentDto);
        }

        payeeDtoService.validate(paymentDto, new ServiceErrorsImpl());
        if (paymentDto.getErrors() != null && paymentDto.getErrors().size() > 0) {
            // return if validation failed
            logger.info("validation failed for adding payee.");
            return new KeyedApiResponse(version, paymentDto.getKey(), paymentDto);
        }

        return new Submit<>(version, payeeDtoService, movemoneytDtoErrorMapper, paymentDto).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.movemoney.v2.uri.deletePayee}")
    @PreAuthorize("@acctPermissionService.canTransact(#portfolioId, 'account.payee.view')")
    public
    @ResponseBody
    KeyedApiResponse deletePayee(@PathVariable(ACCOUNT_ID_PARAM) String portfolioId,
                                 @ModelAttribute PaymentDto paymentDto) {

        AccountKey key = new AccountKey(EncodedString.toPlainText(portfolioId));
        paymentDto.setKey(key);
        paymentDto.setOpType(Attribute.DELETE);

        return new Submit<>(version, payeeDtoService, movemoneytDtoErrorMapper, paymentDto).performOperation();

    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.movemoney.v2.uri.updatePayee}")
    @PreAuthorize("@acctPermissionService.canTransact(#portfolioId, 'account.payee.view')")
    public
    @ResponseBody
    KeyedApiResponse updatePayee(@PathVariable(ACCOUNT_ID_PARAM) String portfolioId,
                                 @ModelAttribute PaymentDto paymentDto) {

        logger.info("account/movemoney/accountsandbillers?a=0E875196402897D8CDF9DC87180C0A537BEB53509459B0D6&c=NULL :: In V1");
        AccountKey key = new AccountKey(EncodedString.toPlainText(portfolioId));
        paymentDto.setKey(key);
        paymentDto.setOpType(Attribute.UPDATE);

        return new Submit<>(version, payeeDtoService, movemoneytDtoErrorMapper, paymentDto).performOperation();

    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.movemoney.v2.uri.bsbValidate}")
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    BsbValidationDto validateBSB(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String portfolioId,
                                 @RequestParam(required = false, value = Attribute.BSB_CODE) String bsbCode) {
        BsbValidationDto bsbValidationDto = new BsbValidationDto();
        Bsb resultBsb = bsbCodeRepository.load(bsbCode);
        if (null != resultBsb && null != resultBsb.getBsbCode()) {
            bsbValidationDto.setValid(true);
            bsbValidationDto.setBankName(resultBsb.getBankName());
        } else {
            bsbValidationDto.setValid(false);
        }
        return bsbValidationDto;
    }

    //retrieve saved payments
    @RequestMapping(method = RequestMethod.GET, value = "${api.movemoney.v2.uri.retrievePayments}")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.report.view')")
    @ResponseBody
    public ApiResponse retrieveSavedPayments(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId) {

        if (StringUtils.isEmpty(accountId)) {
            throw new IllegalArgumentException("Account id is not valid");
        }

        final ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria(ACCOUNT_ID_PARAM,
                ApiSearchCriteria.SearchOperation.EQUALS, EncodedString.toPlainText(accountId),
                ApiSearchCriteria.OperationType.STRING);

        final List<ApiSearchCriteria> searchCriteriaList = new ArrayList<>();

        searchCriteriaList.add(accountIdCriteria);

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, savedPaymentDtoService, searchCriteriaList).performOperation();
    }

    private String populateDeviceNumber() {
        IndividualDetailImpl clientDetail = (IndividualDetailImpl) clientIntegrationService
                .loadClientDetails(userProfileService.getActiveProfile().getClientKey(), new FailFastErrorsImpl());
        if (null != clientDetail) {
            List<Phone> listPhone = clientDetail.getPhones();
            for (Phone phone : listPhone) {
                if (phone.getType().equals(AddressMedium.MOBILE_PHONE_PRIMARY)) {
                    return phone.getNumber();
                }
            }
        }
        return null;
    }

    private KeyedApiResponse populateErrorCode(PaymentDto paymentDto, SafiAuthenticateResponse authResult) {
        String reason = null;

        if (authResult != null && null != authResult.getStatusCode()) {
            reason = cmsService.getContent(authResult.getDisplayMessageCode());
        }
        else {
            reason = cmsService.getContent(INVALID_SMS_CODE);
        }

        setErrors(paymentDto, reason);
        return new KeyedApiResponse(version, paymentDto.getKey(), paymentDto);
    }

    private void setErrors(PaymentDto paymentDto, String reason) {
        List<DomainApiErrorDto> errorList = new ArrayList<>();
        DomainApiErrorDto error = new DomainApiErrorDto(Constants.INVALID_SMS_CODE, null, reason, null,
                DomainApiErrorDto.ErrorType.ERROR);
        errorList.add(error);
        paymentDto.setErrors(errorList);
    }
}
