package com.bt.nextgen.service.safi;

import com.bt.nextgen.api.safi.model.EventModel;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.bt.nextgen.service.prm.service.PrmServiceImpl;
import com.bt.nextgen.service.safi.model.SafiAnalyzeAndChallengeResponse;
import com.bt.nextgen.service.safi.model.SafiAuthenticateResponse;
import com.bt.nextgen.service.safi.model.SafiEventType;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.bt.nextgen.util.SamlUtil;
import com.btfin.panorama.core.security.profile.SafiDeviceIdentifier;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.rsa.csd.ws.ActionCode;
import com.rsa.csd.ws.AnalyzeRequest;
import com.rsa.csd.ws.AnalyzeResponse;
import com.rsa.csd.ws.AnalyzeResponseType;
import com.rsa.csd.ws.AnalyzeType;
import com.rsa.csd.ws.AuthenticateType;
import com.rsa.csd.ws.DeviceData;
import com.rsa.csd.ws.DeviceResult;
import com.rsa.csd.ws.RiskResult;
import com.rsa.csd.ws.TriggeredRule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TwoFactorAuthenticationIntegrationServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(TwoFactorAuthenticationIntegrationServiceImplTest.class);

    @InjectMocks
    private TwoFactorAuthenticationIntegrationServiceImpl twoFactorAuthenticationIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private WebServiceProvider provider;

    @Mock
    private SafiAnalyzeAndChallengeResponse safiResponse;

    @Mock
    private ServiceErrors serviceErrors;

    @Mock
    private SafiAuthenticateResponse response;

    @Mock
    private FeatureTogglesService featureTogglesService;

    @Mock
    private PrmServiceImpl prmService;

    @Mock
    private TwoFactorAuthenticationDocumentService twoFactorAuthenticationDocumentService;

    @Before
    public void setUp() throws Exception {
        serviceErrors = new FailFastErrorsImpl();
        safiResponse = new SafiAnalyzeAndChallengeResponse();
        mockAnalyzeResponse();
        // userProfileService=getProfile(JobRole.ADVISER, "job id 1", "client1");
    }

    @Test
    public void testFeatureToggleForPRM() throws Exception {

        FeatureToggles featureToggles = new FeatureToggles();
        featureToggles.setFeatureToggle("prmNonValueEvents", true);
        when(featureTogglesService.findOne(any(ServiceErrorsImpl.class))).thenReturn(featureToggles);
        SafiDeviceIdentifier safiDeviceIdentifier = new SafiDeviceIdentifier() {
            @Override
            public String getSafiDeviceId() {
                return "1234";
            }
        };
        when(userProfileService.getSafiDeviceIdentifier()).thenReturn(safiDeviceIdentifier);
        SamlToken token = new SamlToken(SamlUtil.loadSaml());

        when(userProfileService.getSamlToken()).thenReturn(token);

        AuthenticateType type = new AuthenticateType();
        // type.setRequest();

        when(twoFactorAuthenticationDocumentService.createAuthenticateRequest(anyString(), any(HttpRequestParams.class), any(SafiAnalyzeAndChallengeResponse.class), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(type);
        SafiAuthenticateResponse response = new SafiAuthenticateResponse();
        response.setSuccessFlag(true);
        when(twoFactorAuthenticationDocumentService.toAuthenticateResultModel(any(CorrelatedResponse.class), any(ServiceErrorsImpl.class))).thenReturn(response);

        response = twoFactorAuthenticationIntegrationService.authenticate("123", getHttpRequestParams(), safiResponse, serviceErrors);

        verify(prmService, Mockito.times(1)).triggerTwoFactorPrmEvent(null);


        featureToggles = new FeatureToggles();
        featureToggles.setFeatureToggle("prmNonValueEvents", false);
        when(featureTogglesService.findOne(any(ServiceErrorsImpl.class))).thenReturn(featureToggles);

        response = twoFactorAuthenticationIntegrationService.authenticate("123", getHttpRequestParams(), safiResponse, serviceErrors);


        verify(prmService, Mockito.times(1)).triggerTwoFactorPrmEvent(null);

    }


    @Test
    public void testAuthenticate() throws Exception {

        when(response.getStatusCode()).thenReturn("SUCCESS");
        TwoFactorAuthenticationIntegrationServiceImpl twoFactorAuthenticationIntegrationServiceImpl = mock(TwoFactorAuthenticationIntegrationServiceImpl.class);
        when(twoFactorAuthenticationIntegrationServiceImpl.authenticate("12344", getHttpRequestParams(), safiResponse, serviceErrors)).thenReturn(response);
        response = twoFactorAuthenticationIntegrationServiceImpl.authenticate("12344", getHttpRequestParams(), safiResponse, serviceErrors);
    }

    @Test
    public void analyze() throws Exception {
        EventModel eventModel = mock(EventModel.class);
        SafiAnalyzeAndChallengeResponse response;
        for (SafiEventType eventType : SafiEventType.values()) {
            when(eventModel.getClientDefinedEventType()).thenReturn(eventType.name());
            response = twoFactorAuthenticationIntegrationService.analyze(eventModel, mock(HttpRequestParams.class), mock(ServiceErrors.class));
            assertEquals(response.getDeviceToken(), "147852");
            assertEquals(response.getActionCode(), false);
        }
    }

    private void mockAnalyzeResponse() {
        AnalyzeType analyzeType = mock(AnalyzeType.class);

        AnalyzeResponse analyzeResponse = mock(AnalyzeResponse.class);
        RiskResult riskResult = mock(RiskResult.class);
        TriggeredRule triggeredRule = mock(TriggeredRule.class);

        DeviceResult deviceResult = mock(DeviceResult.class);
        DeviceData deviceData = mock(DeviceData.class);

        when(triggeredRule.getActionCode()).thenReturn(ActionCode.ALLOW);
        when(riskResult.getTriggeredRule()).thenReturn(triggeredRule);
        when(deviceResult.getDeviceData()).thenReturn(deviceData);
        when(deviceData.getDeviceTokenCookie()).thenReturn("147852");
        when(analyzeResponse.getRiskResult()).thenReturn(riskResult);
        when(analyzeResponse.getDeviceResult()).thenReturn(deviceResult);

        AnalyzeResponseType analyzeResponseType = mock(AnalyzeResponseType.class);
        when(analyzeResponseType.getAnalyzeReturn()).thenReturn(analyzeResponse);


        CorrelatedResponse correlatedResponse = mock(CorrelatedResponse.class);
        when(correlatedResponse.getResponseObject()).thenReturn(analyzeResponseType);

        when(twoFactorAuthenticationDocumentService.createAnalyzeRequest(any(HttpRequestParams.class), anyString(), anyString(),
                anyString(), anyMap(), anyMap(), anyString())).thenReturn(analyzeType);

        when(provider.sendWebServiceWithResponseCallback(anyString(), any(AnalyzeRequest.class))).thenReturn(correlatedResponse);

    }

    private HttpRequestParams getHttpRequestParams() {
        HttpRequestParams requestParams = new HttpRequestParams();
        requestParams.setHttpAccept("text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
        requestParams.setHttpAcceptChars("ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        requestParams.setHttpAcceptEncoding("gzip,deflate");
        requestParams.setHttpAcceptLanguage("en-ua,en;q=0.5");
        requestParams.setHttpReferrer("http://www.cba.com.uk");
        requestParams.setHttpUserAgent("Mozilla/5.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727; InfoPath.1; .NET CLR 3.0.04506.30)");
        requestParams.setHttpOriginatingIpAddress("127.0.0.1");

        return requestParams;
    }


    private UserProfile getProfile(final JobRole role, final String jobId, final String customerId) {
        UserInformationImpl user = new UserInformationImpl();
        user.setClientKey(ClientKey.valueOf(customerId));

        JobProfileImpl job = new JobProfileImpl();
        job.setJobRole(role);
        job.setJob(JobKey.valueOf(jobId));
        UserProfile profile = new UserProfileAdapterImpl(user, job);
        return profile;
    }
}