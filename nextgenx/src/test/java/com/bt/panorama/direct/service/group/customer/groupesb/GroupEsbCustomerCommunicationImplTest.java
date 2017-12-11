package com.bt.panorama.direct.service.group.customer.groupesb;

import au.com.westpac.gn.communicationmanagement.services.communicationdispatch.xsd.generatecommunicationdetails.v1.svc0019.GenerateCommunicationDetailsResponse;
import com.bt.nextgen.clients.util.JaxbUtil;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.CorrelationIdWrapper;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.util.SamlUtil;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.bt.panorama.direct.api.email.model.PortfolioDetailDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;

/**
 * Created by L069552 on 16/06/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupEsbCustomerCommunicationImplTest {

    @InjectMocks
    GroupEsbCustomerCommunicationImpl groupEsbCustomerCommunication;

    @Mock
    private WebServiceProvider provider;

    @Mock
    private BankingAuthorityService userSamlService;

    private PortfolioDetailDto portfolioDetailDto;

    @Before
    public void setUp()
    {
        portfolioDetailDto = new PortfolioDetailDto("URL", "Moderate", "Abc", "abc@test.com", null);
        Mockito.doAnswer(new Answer <SamlToken>()
        {
            @Override
            public SamlToken answer(InvocationOnMock invocation) throws Throwable
            {
                return new SamlToken(SamlUtil.loadSaml());
            }

        }).when(userSamlService).getSamlToken();
    }

    @Test
    public void testSendCommunicationMail() throws Exception {

        String communicationResponseXML = "<SVC0019:generateCommunicationDetailsResponse "
                + "xsi:schemaLocation=\"http://www.westpac.com.au/gn/communicationManagement/services/communicationDispatch/xsd/generateCommunicationDetails/v1/SVC0019/ GenerateCommunicationDetails_v1.xsd\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SVC0019=\"http://www.westpac.com.au/gn/communicationManagement/services/communicationDispatch/xsd/generateCommunicationDetails/v1/SVC0019/\" "
                + "xmlns:sh=\"http://www.westpac.com.au/gn/utility/xsd/statusHandling/v1/\">"
                + "<sh:serviceStatus><sh:statusInfo> <sh:level>Success</sh:level> <sh:code>000,00000</sh:code>"
                + "</sh:statusInfo></sh:serviceStatus>"
                + "</SVC0019:generateCommunicationDetailsResponse>";
        InputStream inputStream = new ByteArrayInputStream(communicationResponseXML.getBytes("UTF-8"));
        GenerateCommunicationDetailsResponse generateCommunicationDetailsResponse = JaxbUtil.unmarshall(inputStream, GenerateCommunicationDetailsResponse.class);


        final CorrelatedResponse correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(),
                generateCommunicationDetailsResponse);

        Mockito.doAnswer(new Answer<CorrelatedResponse>() {
            @Override
            public CorrelatedResponse answer(InvocationOnMock invocation) throws Throwable {
                return correlatedResponse;
            }

        })
                .when(provider)
                .sendWebServiceWithSecurityHeaderAndResponseCallback(isA(SamlToken.class),
                        anyString(),
                        anyObject(),
                        any(ServiceErrorsImpl.class));

        GroupEsbCustomerCommunicationAdapter groupEsbCustomerCommunicationAdapter = groupEsbCustomerCommunication.generateEmailCommunication(portfolioDetailDto, new ServiceErrorsImpl());

        assertNotNull(groupEsbCustomerCommunicationAdapter);

        assertEquals(groupEsbCustomerCommunicationAdapter.getServiceLevel(), (Attribute.SUCCESS_MESSAGE));


    }
    @Test
    public void test_generateCommunication_error_response()throws Exception{

        String errorCommunicationResponseXml = "<SVC0019:generateCommunicationDetailsResponse "+
                "xsi:schemaLocation=\"http://www.westpac.com.au/gn/communicationManagement/services/communicationDispatch/xsd/generateCommunicationDetails/v1/SVC0019/ GenerateCommunicationDetails_v1.xsd\" "+
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
                "xmlns:SVC0019=\"http://www.westpac.com.au/gn/communicationManagement/services/communicationDispatch/xsd/generateCommunicationDetails/v1/SVC0019/\" xmlns:sh=\"http://www.westpac.com.au/gn/utility/xsd/statusHandling/v1/\" xmlns:id=\"http://www.westpac.com.au/gn/common/xsd/identifiers/v1/\" "+
                "xmlns:cso=\"http://www.westpac.com.au/gn/communicationManagement/services/communicationDispatch/common/xsd/v1/\">"+
                                                "<sh:serviceStatus>"+
                                                "<sh:statusInfo>" +
                                                "<sh:level>Error</sh:level>"+
                                                "<!-- Error code Provided by ESB-->" +
                                                "<sh:code>300,00018</sh:code>"+
                                                "<sh:description>Duplicate message</sh:description>" +
                                                "<sh:referenceId>1234TBD</sh:referenceId>"+
                                                        "<sh:statusDetail>"+
                                                "<sh:mediationName>TBD</sh:mediationName>" +
                                                "<sh:providerErrorDetail>" +
                                                "<sh:providerErrorCode>4</sh:providerErrorCode>" +
                                                        "</sh:providerErrorDetail>" +
                                                        "</sh:statusDetail>" +
                                                        "</sh:statusInfo>" +
                                                        "</sh:serviceStatus>" +
                                                        "<SVC0019:communication referenceId=\"1234TBD\">" +
                                                        "<cso:internalIdentifier >" +
                                                        "<id:communicationId>LK-001.05192e62-55bf-4849-9bcb-fa25a366ea56.123456789AB.123456789</id:communicationId>" +
                                                        "</cso:internalIdentifier>" +
                                                "</SVC0019:communication>" +
                                                "</SVC0019:generateCommunicationDetailsResponse>";

        InputStream inputStream = new ByteArrayInputStream(errorCommunicationResponseXml.getBytes("UTF-8"));
        GenerateCommunicationDetailsResponse generateCommunicationDetailsResponse = JaxbUtil.unmarshall(inputStream, GenerateCommunicationDetailsResponse.class);


        final CorrelatedResponse correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(),
                generateCommunicationDetailsResponse);

        Mockito.doAnswer(new Answer<CorrelatedResponse>() {
            @Override
            public CorrelatedResponse answer(InvocationOnMock invocation) throws Throwable {
                return correlatedResponse;
            }

        })
                .when(provider)
                .sendWebServiceWithSecurityHeaderAndResponseCallback(isA(SamlToken.class),
                        anyString(),
                        anyObject(),
                        any(ServiceErrorsImpl.class));

        GroupEsbCustomerCommunicationAdapter groupEsbCustomerCommunicationAdapter = groupEsbCustomerCommunication.generateEmailCommunication(portfolioDetailDto, new ServiceErrorsImpl());

        assertNotNull(groupEsbCustomerCommunicationAdapter);

        assertEquals(groupEsbCustomerCommunicationAdapter.getServiceLevel(), (Attribute.ERROR_MESSAGE));

    }
}
