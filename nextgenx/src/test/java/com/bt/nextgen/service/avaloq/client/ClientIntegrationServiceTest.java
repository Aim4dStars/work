package com.bt.nextgen.service.avaloq.client;

import com.avaloq.abs.bb.fld_def.TextFld;
import com.bt.nextgen.api.client.service.ClientUpdateCategory;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.service.AvaloqReportServiceImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperServiceImpl;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.domain.existingclient.ClientHolder;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.GenericClient;
import com.bt.nextgen.service.request.AvaloqRequest;
import com.btfin.panorama.service.integration.account.AccountSecurityIntegrationService;
import com.btfin.abs.reportservice.reportrequest.v1_0.ParamList;
import com.btfin.abs.trxservice.person.v1_0.PersonReq;
import com.btfin.abs.trxservice.person.v1_0.PersonRsp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;

import static com.bt.nextgen.service.avaloq.userinformation.JobRole.INVESTOR;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientIntegrationServiceTest {

    @Spy
    @InjectMocks
    private ClientIntegrationServiceImpl clientIntegrationService;

    @Mock
    private AvaloqReportServiceImpl avaloqReportRequest;

    @Mock
    private AvaloqExecute avaloqExecute;

    @Mock
    private ClientUpdateConverter clientUpdateConverter;

    @Mock
    private AccountSecurityIntegrationService accountIntegrationService;

    @Mock
    private ClientIntegrationServiceImpl clientIntegrationService1;

    @Mock
    private AvaloqGatewayHelperServiceImpl webserviceClient;

    @Mock
    private UserProfileService profileService;

    @Mock
    private UserProfile userProfile;


    private AbstractGenericClientImpl abstractHolder;

    @Before
    public void setup() throws Exception {
        when(avaloqExecute.executeReportRequestToDomain(any(AvaloqReportRequest.class), eq(ClientHolder.class), any(ServiceErrors.class)))
                .thenReturn(new ClientHolder());

        final ClassPathResource genericClient = new ClassPathResource("/webservices/response/PersonDetailsInvestorResp.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(genericClient.getInputStream()));
        DefaultResponseExtractor<AbstractGenericClientImpl> defaultResponseExtractor = new DefaultResponseExtractor<>(AbstractGenericClientImpl.class);
        abstractHolder = defaultResponseExtractor.extractData(content);
        when(avaloqReportRequest.executeReportRequestToDomain(any(AvaloqRequest.class), any(Class.class), any(ServiceErrors.class)))
                .thenReturn(abstractHolder);
        when(profileService.getActiveProfile()).thenReturn(userProfile);
    }

    @Test
    public void testUpdateDeviceStatus() throws Exception {
        PersonReq personReq = mock(PersonReq.class);
        when(clientUpdateConverter.createClientDetailsUpdateRequest(any(GenericClient.class), eq(ClientUpdateCategory.DEVICE_STATUS), any(ServiceErrors.class))).thenReturn(personReq);
        clientIntegrationService.updateDeviceStatus(ClientKey.valueOf("157365"), true, new ServiceErrorsImpl());
        Mockito.verify(webserviceClient, times(1)).sendToWebService(eq(personReq), eq(AvaloqOperation.PERSON_REQ), any(ServiceErrors.class));
        Mockito.verify(clientUpdateConverter, times(1)).createClientDetailsUpdateResponse(any(PersonRsp.class), any(GenericClient.class), any(ClientUpdateCategory.class), any(ServiceErrors.class));
    }

    @Test
    public void testUpdatePPID() throws Exception {
        PersonReq personReq = mock(PersonReq.class);
        when(clientUpdateConverter.createUpdatePPIDRequest(any(GenericClient.class), any(String.class), any(ServiceErrors.class))).thenReturn(personReq);
        clientIntegrationService.updatePPID(ClientKey.valueOf("78910"), "PPID", new ServiceErrorsImpl());
        Mockito.verify(webserviceClient, times(1)).sendToWebService(eq(personReq), eq(AvaloqOperation.PERSON_REQ), any(ServiceErrors.class));
    }

    @Test
    public void testLoadClientShouldConvertSearchStringBeforePassingToAvaloq_withOneWord() throws Exception {
        clientIntegrationService.loadClientsForExistingClientSearch(new ServiceErrorsImpl(), "te");
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(argThat(containsSearchString("te%")), eq(ClientHolder.class), any(ServiceErrors.class));
    }

    @Test
    public void testLoadClientShouldConvertSearchStringBeforePassingToAvaloq_withTwoWords() throws Exception {
        clientIntegrationService.loadClientsForExistingClientSearch(new ServiceErrorsImpl(), "First   Last");
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(argThat(containsSearchString("First% Last%")), eq(ClientHolder.class), any(ServiceErrors.class));
    }

    private LambdaMatcher<AvaloqReportRequest> containsSearchString(final String expectedSearchString) {
        return new LambdaMatcher<AvaloqReportRequest>() {
            @Override
            protected boolean matchesSafely(AvaloqReportRequest reportRequest) {
                ParamList paramList = (ParamList) ReflectionTestUtils.invokeGetterMethod(reportRequest, "getParamList");
                String actualText = ((TextFld) paramList.getParam().get(0).getVal()).getVal();
                return actualText.equals(expectedSearchString);
            }
        };
    }

    @Test
    public void testUpdateRegisterOnlineGeneric_NewRegistration() throws Exception {
        when(avaloqReportRequest.executeReportRequestToDomain(any(AvaloqRequest.class), any(Class.class), any(ServiceErrors.class)))
                .thenReturn(abstractHolder);

        when(clientUpdateConverter.createClientDetailsUpdateResponse(any(com.btfin.abs.trxservice.person.v1_0.PersonRsp.class),
                any(GenericClient.class), eq(ClientUpdateCategory.REGISTER_ONLINE), any(ServiceErrors.class))).thenReturn(abstractHolder);
        when(userProfile.getJobRole()).thenReturn(INVESTOR);
        clientIntegrationService.updateRegisterOnline(ClientKey.valueOf("157365"), JobRole.INVESTOR, new ServiceErrorsImpl());
        Mockito.verify(clientUpdateConverter).createClientDetailsUpdateRequest(any(GenericClient.class),
                eq(ClientUpdateCategory.REGISTER_ONLINE), any(ServiceErrors.class));

    }

    @Test
    public void testUpdateRegisterOnlineGeneric_AlreadyRegistered() throws Exception {
        final ClassPathResource genericClient = new ClassPathResource("/webservices/response/genericclient/PersonDetailsInvestorResp.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(genericClient.getInputStream()));
        DefaultResponseExtractor<AbstractGenericClientImpl> defaultResponseExtractor = new DefaultResponseExtractor<>(AbstractGenericClientImpl.class);
        abstractHolder = defaultResponseExtractor.extractData(content);
        when(avaloqReportRequest.executeReportRequestToDomain(any(AvaloqRequest.class), any(Class.class), any(ServiceErrors.class)))
                .thenReturn(abstractHolder);

        when(clientUpdateConverter.createClientDetailsUpdateResponse(any(com.btfin.abs.trxservice.person.v1_0.PersonRsp.class),
                any(GenericClient.class), eq(ClientUpdateCategory.REGISTER_ONLINE), any(ServiceErrors.class))).thenReturn(abstractHolder);
        when(userProfile.getJobRole()).thenReturn(INVESTOR);
        when(profileService.getActiveProfile()).thenReturn(userProfile);when(profileService.getActiveProfile()).thenReturn(userProfile);
        GenericClient genericClientResp = clientIntegrationService.updateRegisterOnline(ClientKey.valueOf("157365"), userProfile.getJobRole(),new ServiceErrorsImpl());

        assertThat(genericClientResp.getModificationSeq(), is("3"));

    }

    @Test
    public void testUpdateRegisterOnlineGeneric_updateLegalEntities() throws Exception {
        when(avaloqReportRequest.executeReportRequestToDomain(any(AvaloqRequest.class), any(Class.class), any(ServiceErrors.class)))
                .thenReturn(abstractHolder);
        when(userProfile.getJobRole()).thenReturn(INVESTOR);
        clientIntegrationService.updateRegisterOnline(ClientKey.valueOf("157365"),JobRole.INVESTOR, new ServiceErrorsImpl());
        Mockito.verify(clientIntegrationService, times(1)).updateLegalEntities(any(GenericClient.class), any(ServiceErrors.class));

    }
}
