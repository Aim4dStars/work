package com.bt.panorama.direct.api.email.service;

import au.com.westpac.gn.communicationmanagement.services.communicationdispatch.xsd.generatecommunicationdetails.v1.svc0019.GenerateCommunicationDetailsResponse;
import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.panorama.direct.api.email.model.PortfolioDetailDto;
import com.bt.panorama.direct.service.group.customer.CustomerCommunicationIntegrationService;
import com.bt.panorama.direct.service.group.customer.groupesb.GroupEsbCustomerCommunicationAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

/**
 * Created by L069552 on 17/06/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class SendPortfolioDetailsServiceImplTest {

    @InjectMocks
    SendPortfolioDetailsServiceImpl sendPortfolioDetailsService;

    @Mock
    CustomerCommunicationIntegrationService integrationService;

    private GroupEsbCustomerCommunicationAdapter groupEsbCustomerCommunicationAdapter;

    ServiceErrors serviceErrors ;

    private PortfolioDetailDto portfolioDetailDto;

    @Before
    public void setUp() throws Exception
    {
        serviceErrors = new ServiceErrorsImpl();
        portfolioDetailDto = new PortfolioDetailDto("URL", "Moderate", "Abc", "abc@test.com", null);
        String communicationResponseXML = "<SVC0019:generateCommunicationDetailsResponse xsi:schemaLocation=\"http://www.westpac.com.au/gn/communicationManagement/services/communicationDispatch/xsd/generateCommunicationDetails/v1/SVC0019/ GenerateCommunicationDetails_v1.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SVC0019=\"http://www.westpac.com.au/gn/communicationManagement/services/communicationDispatch/xsd/generateCommunicationDetails/v1/SVC0019/\" xmlns:sh=\"http://www.westpac.com.au/gn/utility/xsd/statusHandling/v1/\">"
                + "<sh:serviceStatus><sh:statusInfo> <sh:level>Success</sh:level> <sh:code>000,00000</sh:code>"
                + "</sh:statusInfo></sh:serviceStatus>"
                + "</SVC0019:generateCommunicationDetailsResponse>";
        InputStream inputStream = new ByteArrayInputStream(communicationResponseXML.getBytes("UTF-8"));
        GenerateCommunicationDetailsResponse generateCommunicationDetailsResponse = JaxbUtil.unmarshall(inputStream, GenerateCommunicationDetailsResponse.class);

        groupEsbCustomerCommunicationAdapter = new GroupEsbCustomerCommunicationAdapter(generateCommunicationDetailsResponse, serviceErrors);

    }

    @Test
    public void test_sendPortfolioDetails_single_recipient() {

        Mockito.when(integrationService.generateEmailCommunication(portfolioDetailDto, serviceErrors)).thenReturn(groupEsbCustomerCommunicationAdapter);

        assertTrue(sendPortfolioDetailsService.sendPortfolioDetails(portfolioDetailDto, serviceErrors));

    }

}
