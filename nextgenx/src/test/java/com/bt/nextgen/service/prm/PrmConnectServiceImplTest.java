package com.bt.nextgen.service.prm;

import au.com.westpac.gn.riskmanagement.services.riskmonitoring.xsd.notifyeventforfraudassessment.v1.svc0525.NotifyEventForFraudAssessmentResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.prm.service.PrmGESBConnectServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created by L075208 on 4/10/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class PrmConnectServiceImplTest {

    @InjectMocks PrmGESBConnectServiceImpl prmConnectService;

    private NotifyEventForFraudAssessmentResponse jaxbValidateResponse;

    @Mock
    private WebServiceProvider provider;



    @Test
    public void testSubmitRequest() throws Exception {
//        provider = mock(WebServiceProvider.class);
//
//        jaxbValidateResponse = JaxbUtil.unmarshall("/webservices/response/NotifyEventForFraudAssessment_Response.xml", NotifyEventForFraudAssessmentResponse.class);
//
//        Mockito.doAnswer(new Answer<NotifyEventForFraudAssessmentResponse>() {
//            @Override
//            public NotifyEventForFraudAssessmentResponse answer(InvocationOnMock invocation) throws Throwable {
//                return jaxbValidateResponse;
//            }
//
//        }).when(provider).sendWebServiceWithSecurityHeader(isA(SamlToken.class), anyString(), anyObject());
//
//        PrmDto prmDto = new PrmDto();
//        prmDto.setUserId("userID");
//        prmDto.setClientIp("ClientID");
//        prmDto.setChannelId("ChannelID");
//
//        prmConnectService.submitRequest(prmDto,new ServiceErrorsImpl());

        //assertEquals();



    }

}
