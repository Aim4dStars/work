package com.bt.nextgen.service.safi;

import com.bt.nextgen.api.safi.model.EventModel;
import com.bt.nextgen.service.safi.model.SafiEventType;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.prm.service.PrmService;
import com.bt.nextgen.service.safi.model.SafiAnalyzeAndChallengeResponse;
import com.bt.nextgen.service.safi.model.SafiAuthenticateResponse;
import com.bt.nextgen.service.security.SmsServiceImpl;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.rsa.csd.ws.*;
import org.apache.commons.lang.StringUtils;
import org.apache.ws.security.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class TwoFactorAuthenticationIntegrationServiceImpl implements TwoFactorAuthenticationIntegrationService {
    @Autowired
    private TwoFactorAuthenticationDocumentService twoFactorAuthenticationDocumentService;

    @Autowired
    private UserProfileService userProfileService;

    @Resource(name = "serverAuthorityService")
    private BankingAuthorityService applicationSamlService;

    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

    @Autowired
    private WebServiceProvider provider;

    @Autowired
    private PrmService prmService;

    @Autowired
    private FeatureTogglesService featureTogglesService;


    /**
     * Orchestrates a SAFI authenticate request to the SAFI backend
     * Authenticates the sms code entered by the user in response to a SAFI challenge.
     * <p>
     * Please note (in no particular order):
     * (x) three incorrect authentication attempts for any analyze will result in the mobile device being suspended at device level
     * (no further challenges/authenticates allowed for the device)
     * (x) After seven incorrect authentication attempts the device will be suspended at the network level. Use service ops unlock mobile to unsuspend.
     *
     * @param smsCode       the sms code as entered by the user
     * @param requestParams Http Request Parameters from the users request used to determine risk
     * @param safiResponse  Result of the corresponding analyze call that was called for this user action that is to be authenticated
     */
    @Override
    public SafiAuthenticateResponse authenticate(String smsCode, HttpRequestParams requestParams, SafiAnalyzeAndChallengeResponse safiResponse, ServiceErrors serviceErrors) throws Exception {
        SafiAuthenticateResponse result = new SafiAuthenticateResponse();
        boolean actionCode = false;
        String sessionId = "";
        String transactionId = "";
        String devicePrint = "";
        String deviceToken = "";
        String customerId = "";
        String samlAssertion = "";
        String safiDeviceId = "";
        String avaloqId = null; //It is not actually used downstream userProfileService.getAvaloqId();

        try {

            try {

                safiDeviceId = userProfileService.getSafiDeviceIdentifier().getSafiDeviceId();
                samlAssertion = getBase64EncodedSamlToken(userProfileService.getSamlToken().getToken());
            } catch (IllegalStateException err) {
                logger.info("User doing authenticate is not logged in - assuming this is registration attempt");
                safiDeviceId = safiResponse.getDeviceId();
                //This is not ideal, please consider revising
                samlAssertion = getBase64EncodedSamlToken(applicationSamlService.getSamlToken().getToken());
            }

            if (safiResponse != null && safiResponse.getIdentificationData() != null) {
                sessionId = safiResponse.getIdentificationData().getSessionId();
                transactionId = safiResponse.getIdentificationData().getTransactionId();

                //XXX Quick fix to support Registration - needs to be reviewed
                if (StringUtils.isBlank(transactionId))
                    transactionId = safiResponse.getTransactionId();

                devicePrint = safiResponse.getDevicePrint();
                deviceToken = safiResponse.getDeviceToken();
                customerId = safiResponse.getIdentificationData().getUserName();
            } else {
                logger.info("Unable to create Authenticate request. One or more missing parameters.safi session {} and safi transactionId {}", sessionId, transactionId);
            }
            // TODO: 2FA SMS event can be trigger from here, snipet can add here


            // TODO: Is this even valid? Why would customerId be blank?
            String safiUsername = StringUtils.isBlank(customerId) ? userProfileService.getGcmId() : customerId;


            AuthenticateType request = twoFactorAuthenticationDocumentService.createAuthenticateRequest(smsCode, requestParams, safiResponse, avaloqId, safiDeviceId, samlAssertion, customerId, safiUsername);
            CorrelatedResponse response = provider.sendWebServiceWithResponseCallback(Attribute.SAFI_KEY, request);
            result = twoFactorAuthenticationDocumentService.toAuthenticateResultModel(response, serviceErrors);

            // TODO: 2FA SMS event can be trigger from here
            if(result.isSuccessFlag()&& featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle(FeatureToggles.PRM_VIEW)) {
				prmService.triggerTwoFactorPrmEvent(null);
			}
        } catch (Exception e) {
            logger.error("Error performing safi authenticate attempt for safi session {} and safi transactionId {}: {}",
                    sessionId, transactionId, e.getMessage(), e);

            result.setSuccessFlag(false);
            result.setStatusCode("ERROR");
            throw e;
        }

        return result;
    }


    /***
     * Orchestrates a SAFI challenge event on the backend.
     * A challenge request generates a SMS code challenge which is sent to the users registered mobile device (in safi).
     *
     * Please note (in no particular order):
     * (a) if multiple challenge calls are made for the same analyze session, then only the last challenge and resulting sms code is valid.
     * (b) if the safi device id is not active, registered, or suspended at device or network levels then this fails.
     *
     * @param safiResponse    Result of the corresponding analyze call that was called for this user action that is to be challeneged
     * @param requestParams Http Request Parameters from the users request used to determine risk
     */
    @Override
    public SafiAnalyzeAndChallengeResponse challenge(SafiAnalyzeAndChallengeResponse safiResponse, HttpRequestParams requestParams, ServiceErrors serviceErrors) {

        boolean actionCode = false;
        SafiAnalyzeAndChallengeResponse result = safiResponse;
        String sessionId = "";
        String transactionId = "";
        try {
            String samlAssertion = getBase64EncodedSamlToken(userProfileService.getSamlToken().getToken());
            String safiDeviceId = userProfileService.getSafiDeviceIdentifier().getSafiDeviceId();
            String avaloqId = userProfileService.getAvaloqId();
            logger.info("Received Authenticated Challenge req params samlAssertion:{}, safiDeviceId:{}, avaloqId:{}", samlAssertion, safiDeviceId, avaloqId);

            if (safiResponse != null && safiResponse.getIdentificationData() != null) {
                sessionId = safiResponse.getIdentificationData().getSessionId();
                transactionId = safiResponse.getIdentificationData().getTransactionId();

            } else {
                logger.info("Unable to create Challenge request. One or more missing parameters.safi session {} and safi transactionId {}", sessionId, transactionId);
            }
            if (StringUtils.isEmpty(samlAssertion) || StringUtils.isEmpty(safiDeviceId) || StringUtils.isEmpty(avaloqId)) {
                logger.error("Unable to create Challenge request. One or more missing parameters. SamlAssertion: {}, safiDeviceId: {}, avaloqId: {}",
                        StringUtils.isEmpty(samlAssertion) ? "Not Provided" : "Provided", safiDeviceId, avaloqId);

                throw new IllegalStateException("Unable to create Challenge request. One or more missing parameters.");
            }

            ChallengeType request = twoFactorAuthenticationDocumentService.createChallengeRequest(safiResponse, requestParams, avaloqId, safiDeviceId, samlAssertion, userProfileService.getGcmId());

            CorrelatedResponse correlatedResponse = provider.sendWebServiceWithResponseCallback(Attribute.SAFI_KEY, request);
            result = twoFactorAuthenticationDocumentService.toChallengeResultModel(correlatedResponse, safiResponse, serviceErrors);
        } catch (Exception e) {
            logger.error("Error performing safi challenge attempt for safi session {} and safi transactionId {}: {}",
                    sessionId, transactionId, e.getMessage(), e);

            actionCode = false;
            result.setStatusCode("ERROR");
            result.setActionCode(actionCode);

        }

        return result;
    }

    public SafiAnalyzeAndChallengeResponse challengeFromNotAuthCtx(SafiAnalyzeAndChallengeResponse safiResponse, HttpRequestParams requestParams, ServiceErrors serviceErrors) {

        SafiAnalyzeAndChallengeResponse result = safiResponse;
        String samlAssertion;

        try {
            samlAssertion = getBase64EncodedSamlToken(applicationSamlService.getSamlToken().getToken());

            String safiDeviceId = safiResponse.getDeviceId();
            String avaloqId = safiResponse.getUserName();

            logger.info("Received Unauthenticated Challenge req params samlAssertion:{}, safiDeviceId:{}, avaloqId:{} clientSessionId:{}, clientTransactionId:{}", samlAssertion, safiDeviceId, avaloqId, safiResponse.getIdentificationData().getClientSessionId(), safiResponse.getIdentificationData().getClientTransactionId());

            if (StringUtils.isEmpty(samlAssertion) || StringUtils.isEmpty(safiDeviceId) || StringUtils.isEmpty(avaloqId)) {
                logger.error("Unable to create Challenge request. One or more missing parameters. SamlAssertion: {}, safiDeviceId: {}, avaloqId: {}",
                        StringUtils.isEmpty(samlAssertion) ? "Not Provided" : "Provided", safiDeviceId, avaloqId);

                throw new IllegalStateException("Unable to create Challenge request. One or more missing parameters.");
            }

            ChallengeType request = twoFactorAuthenticationDocumentService.createChallengeRequest(safiResponse, requestParams, avaloqId, safiDeviceId, samlAssertion, safiResponse.getUserName());
            CorrelatedResponse correlatedResponse = provider.sendWebServiceWithResponseCallback(Attribute.SAFI_KEY, request);
            result = twoFactorAuthenticationDocumentService.toChallengeResultModel(correlatedResponse, safiResponse, serviceErrors);

        } catch (Exception e) {
            boolean actionCode = false;
            result.setStatusCode("ERROR");
            result.setActionCode(actionCode);
            serviceErrors.addError(new ServiceErrorImpl("Error performing safi challenge attempt for safi session {} and safi transactionId {}: {}", new Object[]{safiResponse.getIdentificationData().getClientSessionId(), safiResponse.getIdentificationData().getClientTransactionId(), e}));
        }

        return result;
    }


    /**
     * Orchestrates a safi analyze event on the backend.
     * Generates the required request based on incoming data from presentation layer and produces the appropriate webservice call.
     * Analyze call generates a risk assessment by calling the SAFI risk engine to determine whether the user action can proceed
     * or requires a challenge/authenticate event before the user can continue.
     *
     * @param eventModel    bean containing the user action for safi analyze
     * @param requestParams http request parameters for
     */
    @Override
    public SafiAnalyzeAndChallengeResponse analyze(EventModel eventModel, HttpRequestParams requestParams, ServiceErrors serviceErrors) {
        String devicePrint = "";
        String clientSessionId = "";
        String clientTransactionId = "";

        try {
            devicePrint = eventModel.getDeviceToken();
            clientSessionId = getSessionId();
            clientTransactionId = getTransactionId();
        } catch (Exception e) {
            logger.warn("Unable to determine devicetoken, session, or transactionid for the user during safi analyze call", e);
        }

        final Map<String, String> factMap = createFactMap(eventModel);
        final Map<String, String> eventTypeMap = createEventTypeMap(eventModel.getClientDefinedEventType());

        AnalyzeType request = twoFactorAuthenticationDocumentService.createAnalyzeRequest(requestParams, devicePrint, clientSessionId, clientTransactionId, factMap, eventTypeMap, userProfileService.getGcmId());
        CorrelatedResponse correlatedResponse = provider.sendWebServiceWithResponseCallback(Attribute.SAFI_KEY, request);
        AnalyzeResponseType response = (AnalyzeResponseType) correlatedResponse.getResponseObject();

        SafiAnalyzeAndChallengeResponse safiAnalyzeResult = createAnalyzeResultBeanFromResponse(response, clientTransactionId, devicePrint);

        return safiAnalyzeResult;
    }


    private SafiAnalyzeAndChallengeResponse createAnalyzeResultBeanFromResponse(AnalyzeResponseType response, String clientTransactionId, String devicePrint) {
        AnalyzeResponse responseData = response.getAnalyzeReturn();
        ActionCode action = responseData.getRiskResult().getTriggeredRule().getActionCode();

        SafiAnalyzeAndChallengeResponse result = new SafiAnalyzeAndChallengeResponse();
        result.setIdentificationData(responseData.getIdentificationData());
        result.setTransactionId(clientTransactionId);
        result.setDeviceToken(responseData.getDeviceResult().getDeviceData().getDeviceTokenCookie());
        result.setDevicePrint(devicePrint);

        boolean actionCode = false;
        logger.info("createAnalyzeResultBeanFromResponse - action: {}", action);
        switch (action) {
            case ALLOW:
                actionCode = false;
                break;
            case CHALLENGE:
                actionCode = true;
                break;
            default:
                actionCode = false;
        }

        result.setActionCode(actionCode);

        return result;
    }


    private HashMap<String, String> createFactMap(EventModel eventModel) {
        EventDataList eventDataList = new EventDataList();
        EventData eventData = new EventData();
        ClientDefinedFact clientDefinedFactAccountType = new ClientDefinedFact();

        HashMap facts = new HashMap();

        FactList factList = new FactList();


        if (eventModel.getPayeeType() != null) {
            facts.put("BT_ACCT_TYPE", "IDPS");
        }

        if (eventModel.getClientDefinedEventType() != null && eventModel.getClientDefinedEventType().equalsIgnoreCase("CHANGE_DAILY_LIMIT")) {
            facts.put("BT_PAYEE_TYPE", "ANY");
        }

        return facts;
    }


    private Map<String, String> createEventTypeMap(String eventTypeCode) {
        final Map<String, String> eventTypeMap = new HashMap<>();
        final String eventType = "eventType";
        final String clientDefinedEventType = "clientDefinedEventType";
        final SafiEventType safiEventType = SafiEventType.forEventTypeCode(eventTypeCode);

        switch (safiEventType) {
            case PAYMENT:
            case ADD_PAYEE:
            case USER_DETAILS:
                eventTypeMap.put(eventType, safiEventType.name());
                break;
            case FORGOTTEN_PASSWORD:
                eventTypeMap.put(eventType, "CHANGE_PASSWORD");
                eventTypeMap.put(clientDefinedEventType, safiEventType.name());
                break;
            case CHANGE_DAILY_LIMIT:
            case SUPER_SEARCH_CONSENT:
                eventTypeMap.put(eventType, SafiEventType.CLIENT_DEFINED.name());
                eventTypeMap.put(clientDefinedEventType, safiEventType.name());
                break;
            default:
                eventTypeMap.put(eventType, SafiEventType.CLIENT_DEFINED.name());
        }
        return eventTypeMap;
    }

    public String getSessionId() throws Exception {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();
        return session.getId();
    }

    private String getTransactionId() throws Exception {
        return UUID.randomUUID().toString();
    }


    public TwoFactorAuthenticationDocumentService getTwoFactorAuthenticationDocumentService() {
        return twoFactorAuthenticationDocumentService;
    }


    public void setTwoFactorAuthenticationDocumentService(
            TwoFactorAuthenticationDocumentService twoFactorAuthenticationDocumentService) {
        this.twoFactorAuthenticationDocumentService = twoFactorAuthenticationDocumentService;
    }


    private String getBase64EncodedSamlToken(String token) throws Exception {
        String encodedSamlToken = Base64.encode(token.getBytes());
        return encodedSamlToken;
    }
}
