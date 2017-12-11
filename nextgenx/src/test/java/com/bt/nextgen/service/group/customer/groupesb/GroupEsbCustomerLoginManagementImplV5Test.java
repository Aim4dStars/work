package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.RetrieveChannelAccessCredentialResponse;
import com.bt.nextgen.clients.util.JaxbUtil;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.security.profile.UserProfileServiceSpringImpl;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.login.util.SamlUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.group.customer.CredentialRequestModel;
import ns.private_btfin_com.avaloq.avaloqgateway.avaloqgatewayresponse.v1_0.AvaloqGatewayResponseDetailsType;
import ns.private_btfin_com.avaloq.avaloqgateway.avaloqgatewayresponse.v1_0.AvaloqGatewayResponseMsgType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;

/**
 * Created by L075208 on 29/07/2016.
 */
@RunWith(MockitoJUnitRunner.class)
@Ignore
public class GroupEsbCustomerLoginManagementImplV5Test {

    @Mock
    private BankingAuthorityService userSamlService;

    @Mock
    private WebServiceProvider provider;

    private GroupEsbCustomerLoginManagementImpl gesbclmService;

    private AvaloqGatewayResponseMsgType response;

    private RetrieveChannelAccessCredentialResponse retrieveChannelAccessCredentialResponse;

    private RetrieveChannelAccessCredentialResponse retrieveChannelAccessCredentialListResponse;

    private CorrelatedResponse correlatedResponse;

    private CredentialRequestModel credentialRequestModel;

    private InputStream stream;

    private InputStream listStream;

    @Mock
    private UserProfileService profileService;

    private static final String responseXML = "<out:retrieveChannelAccessCredentialResponse xmlns:io=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:io2=\"http://www.westpac.com.au/gn/utility/xsd/statusHandling/v1/\" xmlns:io3=\"http://www.westpac.com.au/gn/utility/xsd/esbHeader/v4/\" " +
            "xmlns:out=\"http://www.westpac.com.au/gn/channelManagement/services/credentialManagement/xsd/retrieveChannelAccessCredential/v4/SVC0311/\" " +
            "xmlns:out2=\"http://www.westpac.com.au/gn/common/xsd/identifiers/v1/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"> " +
            "<sh:serviceStatus xmlns:EAM=\"http://v1.0.eam.entity.olb.westpac.com.au\" xmlns:MVC=\"http://www.westpac.com.au/gn/MediationService/MVC0172/v03\" " +
            "xmlns:sh=\"http://www.westpac.com.au/gn/utility/xsd/statusHandling/v1/\"> <sh:statusInfo> <sh:level>Success</sh:level> <sh:code>000,00000</sh:code> " +
            "</sh:statusInfo> <sh:statusInfo> <sh:level>Warning</sh:level> <sh:code>480,00001</sh:code> " +
            "<sh:description>ReferenceDataLookup returned empty value,ElementName=brand,ElementValue=wpac,Category=BrandSiloCode</sh:description> <sh:referenceId>EAM</sh:referenceId> </sh:statusInfo> " +
            "</sh:serviceStatus> <userCredential> <credentialType>ONL</credentialType> <credentialGroup> <credentialGroupType>bt-investor</credentialGroupType> </credentialGroup> <channel> " +
            "<channelType>Online</channelType> </channel> <userName> <userId>010000705</userId> <userAlias>aUsername</userAlias> </userName> <internalIdentifier> " +
            "<out2:credentialId>44b2aafa-3490-11e3-888b-deadbeef90fe</out2:credentialId> </internalIdentifier> <lastUsedDate>2013-11-13</lastUsedDate> <lastUsedTime>05:23:06Z</lastUsedTime> " +
            "<lifecycleStatus> <status>MIG</status> <startTimestamp>2013-10-14T05:19:58Z</startTimestamp> </lifecycleStatus> <sourceSystem>EAM</sourceSystem> </userCredential><userCredential> " +
            "<credentialType>ONL</credentialType> <credentialGroup> <credentialGroupType>bt-adviser</credentialGroupType> </credentialGroup> <channel> <channelType>Online</channelType> </channel> " +
            "<userName> <userId>30981</userId> <userAlias>dealergroup</userAlias> </userName> <internalIdentifier> <out2:credentialId>44b2aafa-3490-11e3-888b-deadbeef90fe</out2:credentialId> " +
            "</internalIdentifier> <lastUsedDate>2013-11-13</lastUsedDate> <lastUsedTime>05:23:06Z</lastUsedTime> <lifecycleStatus> <status>Active</status> <startTimestamp>2013-10-14T05:19:58Z</startTimestamp> " +
            "</lifecycleStatus> <sourceSystem>EAM</sourceSystem> </userCredential></out:retrieveChannelAccessCredentialResponse>";


    @Before
    public  void setUp()throws Exception{
        profileService = new UserProfileServiceSpringImpl();
        gesbclmService = new GroupEsbCustomerLoginManagementImpl();
        credentialRequestModel = new CredentialRequestModel();
        credentialRequestModel.setBankReferenceId("30981");

        provider = mock(WebServiceProvider.class);
        ReflectionTestUtils.setField(gesbclmService, "provider", provider);
        userSamlService = mock(BankingAuthorityService.class);
        ReflectionTestUtils.setField(gesbclmService, "userSamlService", userSamlService);
        profileService = mock(UserProfileService.class);
        ReflectionTestUtils.setField(gesbclmService, "profileService", profileService);
        Mockito.when(profileService.getGcmId()).thenReturn("30981");

        response = AvaloqObjectFactory.getResponseGatewayObjectFactory().createAvaloqGatewayResponseMsgType();
        AvaloqGatewayResponseDetailsType detail = AvaloqObjectFactory.getResponseGatewayObjectFactory()
                .createAvaloqGatewayResponseDetailsType();//AvaloqGatewaySuccessResponseType
        response.setResponseDetails(detail);

        stream = new ByteArrayInputStream(responseXML.getBytes("UTF-8"));

        retrieveChannelAccessCredentialResponse =  JaxbUtil.unmarshall(stream, RetrieveChannelAccessCredentialResponse.class);
        stream.close();

        Mockito.doAnswer(new Answer<CorrelatedResponse>() {
            @Override
            public CorrelatedResponse answer(InvocationOnMock invocation) throws Throwable {
                return correlatedResponse;
            }

        }).when(provider).sendWebServiceWithSecurityHeaderAndResponseCallback(isA(SamlToken.class), anyString(), anyObject(), any(ServiceErrorsImpl.class));


        Mockito.doAnswer(new Answer<SamlToken>() {
            @Override
            public SamlToken answer(InvocationOnMock invocation) throws Throwable {
                return 	new SamlToken(SamlUtil.loadSaml());
            }

        }).when(userSamlService).getSamlToken();
    }

    @Test
    public void testGetPPID() throws  Exception{

        ServiceErrors errors = null;
       String PPID = gesbclmService.getPPID(credentialRequestModel,errors);
        Assert.assertNotNull(PPID);


    }
}
