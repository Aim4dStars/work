package com.bt.nextgen.api.investor;

import com.bt.nextgen.api.investor.controller.InvestorApiController;
import com.bt.nextgen.api.safi.facade.TwoFactorAuthenticationServiceImpl;
import com.bt.nextgen.cms.service.CmsService;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.service.safi.model.*;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.*;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.rsa.csd.ws.IdentificationData;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * Created by L072457 on 9/12/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class InvestorApiControllerTest {

    @InjectMocks
    private InvestorApiController investorApiController;

    @Mock
    private UserProfileService profileService;

    @Mock
    private TwoFactorAuthenticationServiceImpl twoFactorAuthenticationService;

    @Mock
    private ClientIntegrationService clientIntegrationService;

    @Mock
    private FeatureTogglesService togglesService;

    @Mock
    private CmsService cmsService;

    @Mock
    private SafiAnalyzeAndChallengeResponse challengeResponse;

    MockHttpServletRequest request;

    MockHttpServletResponse response;

    private SafiAnalyzeAndChallengeResponse analyzeResult;

    @Before
    public void setUp()
    {
        doAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return "CMS-DATA";
            }

        }).when(cmsService).getContent(anyString());
    }

    public SafiAnalyzeAndChallengeResponse createSafiAnalyzeResult()
    {
        SafiAnalyzeAndChallengeResponse result = new SafiAnalyzeAndChallengeResponse();

        IdentificationData identificationData = new IdentificationData();
        identificationData.setClientSessionId("7c8f422:1449b7fb819:-6f56");
        identificationData.setTransactionId("TRX_7c8f422:1449b7fb819:-6f55");

        result.setIdentificationData(identificationData);
        result.setActionCode(true);
        result.setDeviceId("159f9da6-42b5-4aaa-84c6-2335f8fab2b3");
        result.setTransactionId("d8cb6cc3-5e8d-451b-842c-196e7afafa78");
        result.setDevicePrint("version%3D1%26pm%5Ffpua%3Dmozilla%2F5%2E0%20%28windows%20nt%206%2E1%3B%20wow64%29%20applewebkit%2F537%2E36%20%28khtml%2C%20like%20gecko%29%20chrome%2F33%2E0%2E1750%2E146%20safari%2F537%2E36%7C5%2E0%20%28Windows%20NT%206%2E1%3B%20WOW64%29%20AppleWebKit%2F537%2E36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F33%2E0%2E1750%2E146%20Safari%2F537%2E36%7CWin32%26pm%5Ffpsc%3D24%7C1280%7C800%7C760%26pm%5Ffpsw%3D%7Cqt1%7Cqt2%7Cqt3%7Cqt4%7Cqt5%7Cdsw%26pm%5Ffptz%3D10%26pm%5Ffpln%3Dlang%3Den%2DUS%7Csyslang%3D%7Cuserlang%3D%26pm%5Ffpjv%3D1%26pm%5Ffpco%3D1");
        result.setDeviceToken("PMV60we%2BjHxYaQvS7uvXa5L0ipGvnMEoQXniuQAI4oK%2F295Ao3NTZg3HXhEybPW1ZDayhA");

        return result;
    }

    public HttpRequestParams getHttpRequestParams()
    {
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

    @Test
    public void testAnalyze_Valid() throws Exception
    {
        mockSafiAnalyzeAndChallengeResponse();
        MockHttpSession session  = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        UserProfile activeProfile = getActiveProfile();
        when(profileService.getActiveProfile()).thenReturn(activeProfile);
        when(profileService.isInvestor()).thenReturn(Boolean.TRUE);
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrorsImpl.class)))
                .thenReturn(getClientDetail());
        AjaxResponse ajaxResponse = investorApiController.safiAnalyze("USER_DETAILS", request, session);
        assertThat(ajaxResponse.isSuccess(), Is.is(true));
    }

    @Test
    public void testAnalyze_BPay() throws Exception
    {
        mockSafiAnalyzeAndChallengeResponse();
        MockHttpSession session  = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        UserProfile activeProfile = getActiveProfile();
        when(profileService.getActiveProfile()).thenReturn(activeProfile);
        when(profileService.isInvestor()).thenReturn(Boolean.FALSE);
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrorsImpl.class)))
                .thenReturn(getClientDetail());
        when(challengeResponse.getActionCode()).thenReturn(Boolean.FALSE);
        when(twoFactorAuthenticationService.analyze(any(SafiAnalyzeRequest.class),any(ServiceErrors.class))).thenReturn(challengeResponse);
        AjaxResponse ajaxResponse = investorApiController.safiAnalyze("BPAY", request, session);
        assertThat(ajaxResponse.isSuccess(), Is.is(false));
    }

    @Test
    public void testAnalyze_RequestMethodSecure_URL_Valid() throws Exception
    {
        //Request method
        UserProfile activeProfile = getActiveProfile();
        when(profileService.getActiveProfile()).thenReturn(activeProfile);
        when(profileService.isInvestor()).thenReturn(Boolean.TRUE);
        MockHttpServletRequest request = new MockHttpServletRequest(RequestMethod.GET.name(), "/secure/api/v1_0/safiAnalyze");
        request.setParameter("eventType", "Test-clientDefinedEventType");
        MockHttpServletResponse response = new MockHttpServletResponse();

		/* Checking the URL **/
        AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        HttpMessageConverter[] messageConverters =
                {
                        new MappingJackson2HttpMessageConverter()
                };
        annotationMethodHandlerAdapter.setMessageConverters(messageConverters);
        annotationMethodHandlerAdapter.handle(request, response, investorApiController);
        assertThat(response.getStatus(), Is.is(HttpServletResponse.SC_OK));
    }

    @Test
    public void testUpdateCustomerData_InvalidSMSCode() throws Exception {

        doAnswer(new Answer <SafiAuthenticateResponse>()
        {
            @Override
            public SafiAuthenticateResponse answer(InvocationOnMock invocation) throws Throwable
            {
                SafiAuthenticateResponse safiAuthenticateResponse = new SafiAuthenticateResponse();
                safiAuthenticateResponse.setSuccessFlag(false);
                return safiAuthenticateResponse;
            }
        }).when(twoFactorAuthenticationService).authenticate(any(SafiAuthenticateRequest.class));

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("AnalyzeResult", createSafiAnalyzeResult());
        MockHttpServletRequest request = new MockHttpServletRequest();

        SafiAuthenticateResponse safiAuthenticateResponse = Mockito.mock(SafiAuthenticateResponse.class);
        safiAuthenticateResponse.setSuccessFlag(false);
        when(twoFactorAuthenticationService.authenticate(any(SafiAuthenticateRequest.class))).thenReturn(safiAuthenticateResponse);
        UserProfile activeProfile = getActiveProfile();
        when(profileService.getActiveProfile()).thenReturn(activeProfile);
        when(profileService.isInvestor()).thenReturn(Boolean.TRUE);
        AjaxResponse ajaxResponse = investorApiController.verifySmsCode("d8cb6cc3-5e8d-451b-842c-196e7afafa78", "223423", session, request);
        assertNotNull(ajaxResponse);
        assertFalse(ajaxResponse.isSuccess());
    }

    @Test
    public void testAnalyze_2FAVarified() throws Exception
    {
        mockSafiAnalyzeAndChallengeResponse();
        MockHttpSession session  = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        UserProfile activeProfile = getActiveProfile();
        when(profileService.getActiveProfile()).thenReturn(activeProfile);
        when(profileService.isInvestor()).thenReturn(Boolean.TRUE);
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrorsImpl.class)))
                .thenReturn(getClientDetail());
        AjaxResponse ajaxResponse = investorApiController.safiAnalyze("USER_DETAILS", request, session);
        assertThat(ajaxResponse.isSuccess(), Is.is(true));
    }


    private void mockSafiAnalyzeAndChallengeResponse()
    {
        doAnswer(new Answer <SafiAnalyzeAndChallengeResponse>()
        {
            @Override
            public SafiAnalyzeAndChallengeResponse answer(InvocationOnMock invocation) throws Throwable
            {
                SafiAnalyzeAndChallengeResponse safiAnalyzeAndChallengeResponse = new SafiAnalyzeAndChallengeResponse();
                safiAnalyzeAndChallengeResponse.setActionCode(true);
                return safiAnalyzeAndChallengeResponse;
            }

        }).when(twoFactorAuthenticationService).analyze(any(SafiAnalyzeRequest.class), any(ServiceErrorsImpl.class));
    }

    public UserProfile getActiveProfile()
    {
        UserInformation userInfo = new UserInformationImpl();
        userInfo.setClientKey(ClientKey.valueOf("57735"));
        UserProfile activeProfile = new UserProfileAdapterImpl(userInfo, new JobProfileImpl());

        return activeProfile;
    }

    public IndividualDetailImpl getClientDetail()
    {
        List<Address> addresses = new ArrayList<>();
        AddressImpl address = new AddressImpl();
        address.setStreetNumber("33");
        address.setStreetName("pitt");
        address.setSuburb("Perth");
        address.setStateAbbr("WA");
        address.setPostCode("6000");
        address.setDomicile(true);
        address.setStreetType("Street");
        addresses.add(address);
        final IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setFullName("Ankita");
        clientDetail.setClientKey(ClientKey.valueOf("0"));
        clientDetail.setAddresses(addresses);

        List<Phone> phones = new ArrayList<>();
        Phone phone = new Phone() {
            @Override
            public AddressKey getPhoneKey() {
                return null;
            }

            @Override
            public AddressMedium getType() {
                return AddressMedium.MOBILE_PHONE_PRIMARY;
            }

            @Override
            public String getNumber() {
                return "0470258633";
            }

            @Override
            public String getCountryCode() {
                return null;
            }

            @Override
            public String getAreaCode() {
                return null;
            }

            @Override
            public String getModificationSeq() {
                return null;
            }

            @Override
            public boolean isPreferred() {
                return true;
            }

            @Override
            public AddressType getCategory() {
                return null;
            }
        };
        phones.add(phone);
        clientDetail.setPhones(phones);
        return clientDetail;
    }
}
