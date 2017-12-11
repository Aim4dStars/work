package com.bt.nextgen.service.onboarding;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.onboarding.btesb.BtEsbOnboardingIntegrationServiceImpl;
import com.bt.nextgen.util.SamlUtil;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.ProvisionMFAMobileDeviceRequestMsgType;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ProvisionMFAMobileDeviceErrorResponsesType;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ProvisionMFAMobileDeviceResponseDetailsType;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ProvisionMFAMobileDeviceResponseMsgType;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ProvisionMFAMobileDeviceSuccessResponseType;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.StatusTypeCode;
import ns.btfin_com.sharedservices.integration.common.response.errorresponsetype.v3.ErrorCodeType;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by L069552 on 14/11/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class BTEsbProvisionMFADeviceServiceTest {

    @InjectMocks
    BtEsbOnboardingIntegrationServiceImpl btEsbOnboardingIntegrationService;

    @Mock
    private BankingAuthorityService userSamlService;

    @Mock
    private BankingAuthorityService serverSamlService;

    @Mock
    private WebServiceProvider provider;

    private ProvisionMFAMobileDeviceResponseMsgType provisionMFAMobileDeviceResponseMsgTypeResp;

    ProvisionMFADeviceRequest provisionMFADeviceRequest;

    @Before
    public void setUp(){
        provisionMFADeviceRequest = new ProvisionMFADeviceRequestModel();
        Map<CustomerNoAllIssuerType, String> customerIdentifiers = new HashMap<>();
        customerIdentifiers.put(CustomerNoAllIssuerType.BT_PANORAMA,"201654479");
        customerIdentifiers.put(CustomerNoAllIssuerType.WESTPAC_LEGACY,null);
        customerIdentifiers.put(CustomerNoAllIssuerType.WESTPAC, null);
        provisionMFADeviceRequest.setCustomerIdentifiers(customerIdentifiers);
        provisionMFADeviceRequest.setCanonicalProductCode("3fb3e732d5c5429d97af392ab18e998b");
        provisionMFADeviceRequest.setPrimaryMobileNumber("61444888448");

        Mockito.doAnswer(new Answer<SamlToken>() {
            @Override
            public SamlToken answer(InvocationOnMock invocation) throws Throwable {
                return new SamlToken(SamlUtil.loadSaml());
            }

        }).when(serverSamlService).getSamlToken();

        Mockito.doAnswer(new Answer<SamlToken>() {
            @Override
            public SamlToken answer(InvocationOnMock invocation) throws Throwable {
                return new SamlToken(SamlUtil.loadSaml());
            }

        }).when(userSamlService).getSamlToken();


    }

    @Test
    public void testProvisionMFADevice_Success(){
        provisionMFAMobileDeviceResponseMsgTypeResp = JaxbUtil.unmarshall("/webservices/response/ProvisionMFAResponse_UT.xml", ProvisionMFAMobileDeviceResponseMsgType.class);
        Mockito.doAnswer(new Answer<ProvisionMFAMobileDeviceResponseMsgType>() {
            @Override
            public ProvisionMFAMobileDeviceResponseMsgType answer(InvocationOnMock invocation) {
                return provisionMFAMobileDeviceResponseMsgTypeResp;
            }

        }).when(provider).sendWebServiceWithSecurityHeader(isA(SamlToken.class), anyString(), anyObject());

        ProvisionMFAMobileDeviceResponse provisionMFAMobileDeviceResponse = btEsbOnboardingIntegrationService.provisionMFADevice(provisionMFADeviceRequest);
        assertNotNull(provisionMFAMobileDeviceResponse);
        assertNotNull(provisionMFAMobileDeviceResponse.getMFAArrangementId());
    }


    @Test
    public void testProvisionMFADevice_Failure(){

        provisionMFAMobileDeviceResponseMsgTypeResp = JaxbUtil.unmarshall("/webservices/response/ProvisionMFAResponse_Errors.xml",ProvisionMFAMobileDeviceResponseMsgType.class);
        Mockito.doAnswer(new Answer<ProvisionMFAMobileDeviceResponseMsgType>() {
            @Override
            public ProvisionMFAMobileDeviceResponseMsgType answer(InvocationOnMock invocation) {
                return provisionMFAMobileDeviceResponseMsgTypeResp;
            }

        }).when(provider).sendWebServiceWithSecurityHeader(isA(SamlToken.class), anyString(), anyObject());
        ProvisionMFAMobileDeviceResponse provisionMFAMobileDeviceResponse = btEsbOnboardingIntegrationService.provisionMFADevice(provisionMFADeviceRequest);
        assertNotNull(provisionMFAMobileDeviceResponse);
        assertNotNull(provisionMFAMobileDeviceResponse.getServiceErrors());
        assertTrue(provisionMFAMobileDeviceResponse.getServiceErrors().hasErrors());
    }


}