package com.bt.nextgen.service.prm;

import com.bt.nextgen.api.movemoney.v2.model.DailyLimitDto;
import com.bt.nextgen.api.movemoney.v2.model.PayeeDto;
import com.bt.nextgen.api.movemoney.v2.model.PaymentDto;
import com.bt.nextgen.core.security.api.service.PermissionBaseDtoService;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.bt.nextgen.service.prm.pojo.PrmDto;
import com.bt.nextgen.service.prm.service.PrmGESBConnectService;
import com.bt.nextgen.service.prm.service.PrmServiceImpl;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.bt.nextgen.serviceops.model.ServiceOpsModel;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PrmServiceImplTest {

   // private static final Logger logger = LoggerFactory.getLogger(PrmServiceImplTest.class);


    @InjectMocks
    PrmServiceImpl prmService;

    PaymentDto paymentDto = new PaymentDto();

    @Mock
    PrmGESBConnectService prmGESBConnectService;


    @Mock
    ServiceErrors serviceErrors;

    @Mock
    HttpRequestParams requestParams;

    @Mock
    HttpServletRequest request;

    @Mock
    UserProfileService userProfileService;

    @Mock
    ClientIntegrationService clientIntegrationService;

    UserProfile profile;

    @Mock
    PermissionBaseDtoService permissionBaseService;

    @Before
    public void setup() {
        // Payee events Setup
        PayeeDto payeeDto = new PayeeDto();
        payeeDto.setPayeeType(PayeeType.BPAY.name());
        payeeDto.setCode("1234");
        paymentDto.setToPayeeDto(payeeDto);
        paymentDto.setOpType(Attribute.ADD);

        //profile = mock(UserProfile.class);
        profile = getProfile("123456");

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    private ServiceOpsModel getServiceOpsModel() {
        ServiceOpsModel serviceOpsModel = new ServiceOpsModel();
        serviceOpsModel.setCisId("12345");
        return serviceOpsModel;
    }

    private ServiceOpsModel getServiceOpsModelForNullCisKey() {
        ServiceOpsModel serviceOpsModel = new ServiceOpsModel();
        serviceOpsModel.setCisId(null);
        return serviceOpsModel;
    }

    private UserProfile getProfile(final String customerId) {
        UserInformationImpl user = new UserInformationImpl();
        user.setClientKey(ClientKey.valueOf(customerId));
        JobProfile jobProfile = null;
        UserProfile profile = new UserProfileAdapterImpl(user, jobProfile);

        return profile;
    }

    @Test
    public void testTwoFactorEvent() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId("12345");
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        prmService.triggerTwoFactorPrmEvent(null);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));
    }

    @Test
    public void testTwoFactorEventForNullCisKey(){
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(userProfileService.getActiveProfile().getClientKey()).thenReturn(null);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId("12345");
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        prmService.triggerTwoFactorPrmEvent(null);
        Mockito.verify(prmGESBConnectService, never()).submitRequest(Mockito.any(PrmDto.class));
    }

    @Test
    public void testTwoFactorEventCisKeyAsInput(){
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        when(userProfileService.getActiveProfile().getClientKey()).thenReturn(null);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId(null);
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        prmService.triggerTwoFactorPrmEvent("12345");
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));
    }

    @Test
    public void testTriggerRegistrationPrmEventForCisKeyNull() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId(null);
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        prmService.triggerRegistrationPrmEvent();
        Mockito.verify(prmGESBConnectService, Mockito.never()).submitRequest(Mockito.any(PrmDto.class));

    }

    @Test
    public void testTriggerRegistrationPrmEvent() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId("12345");
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        prmService.triggerRegistrationPrmEvent();
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));

    }

    @Test
    public void testTriggerMobileChangeServiceOpsPrmEvent() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId("12345");
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        prmService.triggerMobileChangeServiceOpsPrmEvent(getServiceOpsModel());
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));
    }

    @Test
    public void testTriggerMobileChangeServiceOpsPrmEventCisKeyNull() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId(null);
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        prmService.triggerMobileChangeServiceOpsPrmEvent(getServiceOpsModelForNullCisKey());
        Mockito.verify(prmGESBConnectService, never()).submitRequest(Mockito.any(PrmDto.class));

    }

    @Test
    public void testtriggerPayeeEventsAddBapyVersionTwo() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId("12345");
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        when(permissionBaseService.hasBasicPermission(Mockito.anyString())).thenReturn(true);
        PaymentDto paymentDto = new PaymentDto();
        PayeeDto payeeDto = new PayeeDto();
        payeeDto.setPayeeType("BPAY");
        payeeDto.setAccountId("123456");
        payeeDto.setCode("11111");
        payeeDto.setNickname("Roger");
        paymentDto.setToPayeeDto(payeeDto);
        //testing for payee add event
        paymentDto.setOpType("ADD");
        prmService.triggerPayeeEvents(paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));
    }

    @Test
    public void testtriggerPayeeEventsAddVersionTwo() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId("12345");
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        when(permissionBaseService.hasBasicPermission(Mockito.anyString())).thenReturn(true);
        PaymentDto paymentDto = new PaymentDto();
        PayeeDto payeeDto = new PayeeDto();
        payeeDto.setPayeeType("BPAY");
        payeeDto.setAccountId("123456");
        payeeDto.setCode("11111");
        payeeDto.setAccountName("Roger");
        paymentDto.setToPayeeDto(payeeDto);
        //testing for payee add event
        paymentDto.setOpType("ADD");
        prmService.triggerPayeeEvents(paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));

        Mockito.reset(prmGESBConnectService);
        //payeeType Pay_Anyone
        payeeDto.setPayeeType("PAY_ANYONE");
        paymentDto.setToPayeeDto(payeeDto);
        prmService.triggerPayeeEvents(paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));

        Mockito.reset(prmGESBConnectService);
        //payeeType Linked
        payeeDto.setPayeeType("LINKED");
        paymentDto.setToPayeeDto(payeeDto);
        prmService.triggerPayeeEvents(paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));
    }

    @Test
    public void testtriggerPayeeEventsAddVersionTwoCisKeyIsNull() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId(null);
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        when(permissionBaseService.hasBasicPermission(Mockito.anyString())).thenReturn(true);
        PaymentDto paymentDto = new PaymentDto();
        PayeeDto payeeDto = new PayeeDto();
        payeeDto.setPayeeType("BPAY");
        payeeDto.setAccountId("123456");
        payeeDto.setCode("11111");
        payeeDto.setAccountName("Roger");
        paymentDto.setToPayeeDto(payeeDto);
        //testing for payee add event
        paymentDto.setOpType("ADD");
        prmService.triggerPayeeEvents(paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.never()).submitRequest(Mockito.any(PrmDto.class));

        Mockito.reset(prmGESBConnectService);
        //payeeType Pay_Anyone
        payeeDto.setPayeeType("PAY_ANYONE");
        paymentDto.setToPayeeDto(payeeDto);
        prmService.triggerPayeeEvents(paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.never()).submitRequest(Mockito.any(PrmDto.class));

        Mockito.reset(prmGESBConnectService);
        //payeeType Linked
        payeeDto.setPayeeType("LINKED");
        paymentDto.setToPayeeDto(payeeDto);
        prmService.triggerPayeeEvents(paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.never()).submitRequest(Mockito.any(PrmDto.class));
    }

    @Test
    public void testtriggerPayeeEventsUpdateVersionTwo() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId("12345");
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        when(permissionBaseService.hasBasicPermission(Mockito.anyString())).thenReturn(true);
        PaymentDto paymentDto = new PaymentDto();
        PayeeDto payeeDto = new PayeeDto();
        payeeDto.setPayeeType("BPAY");
        payeeDto.setAccountId("123456");
        payeeDto.setCode("11111");
        payeeDto.setAccountName("Roger");
        paymentDto.setToPayeeDto(payeeDto);
        paymentDto.setOpType("UPDATE");
        prmService.triggerPayeeEvents(paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));

        Mockito.reset(prmGESBConnectService);
        //payeeType Pay_Anyone
        payeeDto.setPayeeType("PAY_ANYONE");
        paymentDto.setToPayeeDto(payeeDto);
        prmService.triggerPayeeEvents(paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));

        Mockito.reset(prmGESBConnectService);
        //payeeType Linked
        payeeDto.setPayeeType("LINKED");
        paymentDto.setToPayeeDto(payeeDto);
        prmService.triggerPayeeEvents(paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));
    }

    @Test
    public void testtriggerPayeeEventsDeleteVersionTwo() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId("12345");
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        when(permissionBaseService.hasBasicPermission(Mockito.anyString())).thenReturn(true);
        PaymentDto paymentDto = new PaymentDto();
        PayeeDto payeeDto = new PayeeDto();
        payeeDto.setPayeeType("BPAY");
        payeeDto.setAccountId("123456");
        payeeDto.setCode("11111");
        payeeDto.setAccountName("Roger");
        paymentDto.setToPayeeDto(payeeDto);
        //testing for payee add event
        paymentDto.setOpType("DELETE");
        prmService.triggerPayeeEvents(paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));

        Mockito.reset(prmGESBConnectService);
        //payeeType Pay_Anyone
        payeeDto.setPayeeType("PAY_ANYONE");
        paymentDto.setToPayeeDto(payeeDto);
        prmService.triggerPayeeEvents(paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));

        Mockito.reset(prmGESBConnectService);
        //payeeType Linked
        payeeDto.setPayeeType("LINKED");
        paymentDto.setToPayeeDto(payeeDto);
        prmService.triggerPayeeEvents(paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));
    }

    @Test
    public void testTriggerPayeeEventsAddVersionOneCisKeyIsNull() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId(null);
        ServiceOpsModel model = new ServiceOpsModel();
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        when(permissionBaseService.hasBasicPermission(Mockito.anyString())).thenReturn(true);
        com.bt.nextgen.api.account.v1.model.PaymentDto paymentDto = new com.bt.nextgen.api.account.v1.model.PaymentDto();
        com.bt.nextgen.api.account.v1.model.PayeeDto payeeDto = new com.bt.nextgen.api.account.v1.model.PayeeDto();
        payeeDto.setPayeeType("BPAY");
        payeeDto.setAccountId("123456");
        payeeDto.setCode("11111");
        payeeDto.setAccountName("Roger");
        paymentDto.setToPayteeDto(payeeDto);
        paymentDto.setOpType("ADD");
        prmService.triggerPayeeEvents(new ServiceErrorsImpl(), paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.never()).submitRequest(Mockito.any(PrmDto.class));

        Mockito.reset(prmGESBConnectService);
        //payeeType Pay_Anyone
        payeeDto.setPayeeType("PAY_ANYONE");
        paymentDto.setToPayteeDto(payeeDto);
        prmService.triggerPayeeEvents(new ServiceErrorsImpl(), paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.never()).submitRequest(Mockito.any(PrmDto.class));

        Mockito.reset(prmGESBConnectService);
        //payeeType Linked
        payeeDto.setPayeeType("LINKED");
        paymentDto.setToPayteeDto(payeeDto);
        prmService.triggerPayeeEvents(new ServiceErrorsImpl(), paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.never()).submitRequest(Mockito.any(PrmDto.class));

    }

    @Test
    public void testTriggerPayeeEventsAddBpayVersionOne() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId("12345");
        ServiceOpsModel model = new ServiceOpsModel();
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        when(permissionBaseService.hasBasicPermission(Mockito.anyString())).thenReturn(true);
        com.bt.nextgen.api.account.v1.model.PaymentDto paymentDto = new com.bt.nextgen.api.account.v1.model.PaymentDto();
        com.bt.nextgen.api.account.v1.model.PayeeDto payeeDto = new com.bt.nextgen.api.account.v1.model.PayeeDto();
        payeeDto.setPayeeType("BPAY");
        payeeDto.setAccountId("123456");
        payeeDto.setCode("11111");
        payeeDto.setNickname("Roger");
        paymentDto.setToPayteeDto(payeeDto);
        paymentDto.setOpType("ADD");
        prmService.triggerPayeeEvents(new ServiceErrorsImpl(), paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));
    }

    @Test
    public void testTriggerPayeeEventsAddVersionOne() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId("12345");
        ServiceOpsModel model = new ServiceOpsModel();
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        when(permissionBaseService.hasBasicPermission(Mockito.anyString())).thenReturn(true);
        com.bt.nextgen.api.account.v1.model.PaymentDto paymentDto = new com.bt.nextgen.api.account.v1.model.PaymentDto();
        com.bt.nextgen.api.account.v1.model.PayeeDto payeeDto = new com.bt.nextgen.api.account.v1.model.PayeeDto();
        payeeDto.setPayeeType("BPAY");
        payeeDto.setAccountId("123456");
        payeeDto.setCode("11111");
        payeeDto.setAccountName("Roger");
        paymentDto.setToPayteeDto(payeeDto);
        paymentDto.setOpType("ADD");
        prmService.triggerPayeeEvents(new ServiceErrorsImpl(), paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));

        Mockito.reset(prmGESBConnectService);
        //payeeType Pay_Anyone
        payeeDto.setPayeeType("PAY_ANYONE");
        paymentDto.setToPayteeDto(payeeDto);
        prmService.triggerPayeeEvents(new ServiceErrorsImpl(), paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));

        Mockito.reset(prmGESBConnectService);
        //payeeType Linked
        payeeDto.setPayeeType("LINKED");
        paymentDto.setToPayteeDto(payeeDto);
        prmService.triggerPayeeEvents(new ServiceErrorsImpl(), paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));

    }

    @Test
    public void testTriggerPayeeEventsUpdateVersionOne() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId("12345");
        ServiceOpsModel model = new ServiceOpsModel();
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        when(permissionBaseService.hasBasicPermission(Mockito.anyString())).thenReturn(true);
        com.bt.nextgen.api.account.v1.model.PaymentDto paymentDto = new com.bt.nextgen.api.account.v1.model.PaymentDto();
        com.bt.nextgen.api.account.v1.model.PayeeDto payeeDto = new com.bt.nextgen.api.account.v1.model.PayeeDto();
        payeeDto.setPayeeType("BPAY");
        payeeDto.setAccountId("123456");
        payeeDto.setCode("11111");
        payeeDto.setAccountName("Roger");
        paymentDto.setToPayteeDto(payeeDto);
        paymentDto.setOpType("UPDATE");
        prmService.triggerPayeeEvents(new ServiceErrorsImpl(), paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));

        Mockito.reset(prmGESBConnectService);
        //payeeType Pay_Anyone
        payeeDto.setPayeeType("PAY_ANYONE");
        paymentDto.setToPayteeDto(payeeDto);
        prmService.triggerPayeeEvents(new ServiceErrorsImpl(), paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));

        Mockito.reset(prmGESBConnectService);
        //payeeType Linked
        payeeDto.setPayeeType("LINKED");
        paymentDto.setToPayteeDto(payeeDto);
        prmService.triggerPayeeEvents(new ServiceErrorsImpl(), paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));

    }

    @Test
    public void testTriggerPayeeEventsDeleteVersionOne() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId("12345");
        ServiceOpsModel model = new ServiceOpsModel();
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        when(permissionBaseService.hasBasicPermission(Mockito.anyString())).thenReturn(true);
        com.bt.nextgen.api.account.v1.model.PaymentDto paymentDto = new com.bt.nextgen.api.account.v1.model.PaymentDto();
        com.bt.nextgen.api.account.v1.model.PayeeDto payeeDto = new com.bt.nextgen.api.account.v1.model.PayeeDto();
        payeeDto.setPayeeType("BPAY");
        payeeDto.setAccountId("123456");
        payeeDto.setCode("11111");
        payeeDto.setAccountName("Roger");
        paymentDto.setToPayteeDto(payeeDto);
        paymentDto.setOpType("DELETE");
        prmService.triggerPayeeEvents(new ServiceErrorsImpl(), paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));

        Mockito.reset(prmGESBConnectService);
        //payeeType Pay_Anyone
        payeeDto.setPayeeType("PAY_ANYONE");
        paymentDto.setToPayteeDto(payeeDto);
        prmService.triggerPayeeEvents(new ServiceErrorsImpl(), paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));

        Mockito.reset(prmGESBConnectService);
        //payeeType Linked
        payeeDto.setPayeeType("LINKED");
        paymentDto.setToPayteeDto(payeeDto);
        prmService.triggerPayeeEvents(new ServiceErrorsImpl(), paymentDto);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));

    }

    @Test
    public void testTriggerAccessUnblockPrmEvent() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId("12345");
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        prmService.triggerAccessUnblockPrmEvent(getServiceOpsModel());
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));
    }

    @Test
    public void testTriggerAccessUnblockPrmEventCisKeyNull() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId(null);
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        prmService.triggerAccessUnblockPrmEvent(getServiceOpsModelForNullCisKey());
        Mockito.verify(prmGESBConnectService, never()).submitRequest(Mockito.any(PrmDto.class));
    }

    @Test
    public void testTriggerForgotPasswordPrmEventForCisKeyNull() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId(null);
        ServiceOpsModel model = new ServiceOpsModel();
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        prmService.triggerForgotPasswordPrmEvent();
        Mockito.verify(prmGESBConnectService, Mockito.never()).submitRequest(Mockito.any(PrmDto.class));

    }

    @Test
    public void testTriggerForgotPasswordPrmEvent() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId("12345");
        ServiceOpsModel model = new ServiceOpsModel();
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        prmService.triggerForgotPasswordPrmEvent();
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));

    }

    @Test
    public void testTriggerLogOffPrmEventForCisKeyNull() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId(null);
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        prmService.triggerLogOffPrmEvent(request ,new ServiceErrorsImpl());
        Mockito.verify(prmGESBConnectService, Mockito.never()).submitRequest(Mockito.any(PrmDto.class));

    }

    @Test
    public void testTriggerLogOffPrmEvent() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId("12345");
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        prmService.triggerLogOffPrmEvent(request ,new ServiceErrorsImpl());
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));

    }

    @Test
    public void testTriggerPaymentLimitChangePrmEventForCisKeyNull(){
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId(null);
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        DailyLimitDto dto = new DailyLimitDto();
        BigDecimal amount = new BigDecimal("1115.37");
        dto.setLimit(amount);
        dto.setPayeeType("Bpay");
        prmService.triggerPaymentLimitChangePrmEvent(dto);
        Mockito.verify(prmGESBConnectService, Mockito.never()).submitRequest(Mockito.any(PrmDto.class));


    }

    @Test
    public void testTriggerPaymentLimitChangePrmEvent(){
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId("12345");
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        DailyLimitDto dto = new DailyLimitDto();
        BigDecimal amount = new BigDecimal("1115.37");
        dto.setLimit(amount);
        dto.setPayeeType("BPAY");
        prmService.triggerPaymentLimitChangePrmEvent(dto);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));


    }

    @Test
    public void testTriggerPaymentLimitChangePrmEventV1ForCisKeyNull(){
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId(null);
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        com.bt.nextgen.api.account.v1.model.DailyLimitDto dto1 = new com.bt.nextgen.api.account.v1.model.DailyLimitDto();
        BigDecimal amount = new BigDecimal("1115.37");
        dto1.setLimit(amount);
        dto1.setPayeeType("BPAY");
        prmService.triggerPaymentLimitChangePrmEvent(dto1);
        Mockito.verify(prmGESBConnectService, Mockito.never()).submitRequest(Mockito.any(PrmDto.class));
    }

    @Test
    public void testTriggerPaymentLimitChangePrmEventV1(){
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId("12345");
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        com.bt.nextgen.api.account.v1.model.DailyLimitDto dto1 = new com.bt.nextgen.api.account.v1.model.DailyLimitDto();
        BigDecimal amount = new BigDecimal("1115.37");
        dto1.setLimit(amount);
        dto1.setPayeeType("PAY_ANYONE");
        prmService.triggerPaymentLimitChangePrmEvent(dto1);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));
    }

    @Test
    public void testTriggerChgPwdPrmEventForCisKeyNull() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(getProfile("123465"));
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId(null);
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        prmService.triggerChgPwdPrmEvent(new ServiceErrorsImpl());
        Mockito.verify(prmGESBConnectService, Mockito.never()).submitRequest(Mockito.any(PrmDto.class));

    }

    @Test
    public void testTriggerChgPwdPrmEvent() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(getProfile("123465"));
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId("12345");
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        prmService.triggerChgPwdPrmEvent(new ServiceErrorsImpl());
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));

    }

    @Test
    public void testTriggerIssuePwdServiceOpsPrmEventForCisKeyNull() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId(null);
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        prmService.triggerIssuePwdServiceOpsPrmEvent(getServiceOpsModelForNullCisKey());
        Mockito.verify(prmGESBConnectService, never()).submitRequest(Mockito.any(PrmDto.class));
    }

    @Test
    public void testTriggerIssuePwdServiceOpsPrmEvent() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId("12345");
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        prmService.triggerIssuePwdServiceOpsPrmEvent(getServiceOpsModel());
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));

    }

    @Test
    public void testTriggerAccessBlockPrmEventCisKeyNull() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId(null);
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        prmService.triggerAccessBlockPrmEvent(getServiceOpsModelForNullCisKey(), true);
        Mockito.verify(prmGESBConnectService, never()).submitRequest(Mockito.any(PrmDto.class));
        prmService.triggerAccessBlockPrmEvent(getServiceOpsModelForNullCisKey(), false);
        Mockito.verify(prmGESBConnectService, never()).submitRequest(Mockito.any(PrmDto.class));

    }

    @Test
    public void testTriggerAccessBlockPrmEvent() {
        requestParams.setHttpOriginatingIpAddress("12345");
        when(request.getRemoteAddr()).thenReturn("123456");
        when(requestParams.getHttpOriginatingIpAddress()).thenReturn("12345");
        when(userProfileService.getActiveProfile()).thenReturn(profile);
        IndividualDetailImpl clientDetail = new IndividualDetailImpl();
        clientDetail.setCisId("12345");
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
        when(userProfileService.isServiceOperator()).thenReturn(true);
        prmService.triggerAccessBlockPrmEvent(getServiceOpsModel(), true);
        Mockito.verify(prmGESBConnectService, Mockito.times(1)).submitRequest(Mockito.any(PrmDto.class));

    }

}