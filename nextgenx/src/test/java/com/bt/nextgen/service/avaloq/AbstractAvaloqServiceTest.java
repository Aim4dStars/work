package com.bt.nextgen.service.avaloq;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.bt.nextgen.api.notification.model.NotificationDtoKey;
import com.bt.nextgen.api.notification.model.NotificationListDto;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.jms.cacheinvalidation.InvalidationNotification;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.StaticCodeEnumTemplate;
import com.bt.nextgen.service.avaloq.gateway.*;
import com.bt.nextgen.service.avaloq.jms.JmsIntegrationServiceResponseImpl;
import com.bt.nextgen.service.avaloq.staticrole.CacheManagedStaticRoleService;
import com.bt.nextgen.service.request.*;
import com.bt.nextgen.service.request.AvaloqRequest;
import org.junit.Assert;
import org.junit.Test;

import com.bt.nextgen.addressbook.web.model.GenericPayee;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.payments.domain.PayeeType;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

@RunWith(MockitoJUnitRunner.class)
public class AbstractAvaloqServiceTest
{


    @InjectMocks
    CacheManagedStaticRoleService abstractAvaloqIntegrationService;

    @Mock
    private AvaloqExecute avaloqExecute;

    @Mock
    private AvaloqReportService avaloqService;

    @Mock
    private AvaloqRequestRegister requestRegister;

    @Mock
    InvalidationNotification invalidationNotification;

    @Mock
    AvaloqReportRequest jmsRequest;

    @Mock
    ServiceErrors serviceErrors;

    @Mock
    private AvaloqPartialInvalidationRequestRegister partialInvalidationRequestRegister;

    @Mock
    JmsIntegrationServiceResponseImpl jmsResponse;


	public void testAddLinkedAccount() throws Exception
	{
		String portfolioId = "12249";
        GenericPayee account =new GenericPayee();
        account.setPayeeType(PayeeType.LINKED);
        account.setName("mine");
        account.setReference("123456713");
        account.setCode("012012");
        account.setNickname("nickname1");
        account.setLimit(new BigDecimal("5000"));
        account.setPrimary(false);

	}

    //Test case for Partial Update scenario with AvaloqRequest Parameter
    @Test
    public void testInvokeJMSAvaloqRequest() throws Exception {


        com.bt.nextgen.service.request.AvaloqRequest avaloqRequest= new AvaloqReportRequestImpl(StaticCodeEnumTemplate.STATIC_CODES);
        Mockito.when(partialInvalidationRequestRegister.checkRegisterForPartialInvalidationRequest(Mockito.any(AvaloqRequest.class), Mockito.any(InvalidationNotification.class))).thenReturn(true);
        Mockito.when(avaloqService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(jmsResponse);
        Mockito.when(partialInvalidationRequestRegister.updateRegisterWithCorrelationId(invalidationNotification, jmsResponse.getRequestId())).thenReturn(true);
        List list = new ArrayList();
        list.add("Testing123");
        Mockito.when(jmsResponse.getResponseMsgIdList()).thenReturn(list);
        abstractAvaloqIntegrationService.invokeJMS(avaloqRequest,invalidationNotification,serviceErrors);
        Mockito.verify(partialInvalidationRequestRegister).updateRegisterWithCorrelationId(invalidationNotification,jmsResponse.getRequestId());
       Mockito.verify(partialInvalidationRequestRegister).checkRegisterForPartialInvalidationRequest(Mockito.any(AvaloqRequest.class),Mockito.any(InvalidationNotification.class));

    }

    //Test case for Full Update scenario with AvaloqRequest Parameter
    @Test
    public void testNegativeInvokeJMSAvaloqRequest() throws Exception {


        com.bt.nextgen.service.request.AvaloqRequest avaloqRequest= new AvaloqReportRequestImpl(StaticCodeEnumTemplate.STATIC_CODES);
        Mockito.when(partialInvalidationRequestRegister.checkRegisterForPartialInvalidationRequest(Mockito.any(AvaloqRequest.class), Mockito.any(InvalidationNotification.class))).thenReturn(false);
        Mockito.when(avaloqService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(jmsResponse);
        Mockito.when(partialInvalidationRequestRegister.updateRegisterWithCorrelationId(invalidationNotification, jmsResponse.getRequestId())).thenReturn(true);
        List list = new ArrayList();
        list.add("Testing123");
        Mockito.when(jmsResponse.getResponseMsgIdList()).thenReturn(list);
        abstractAvaloqIntegrationService.invokeJMS(avaloqRequest,invalidationNotification,serviceErrors);
        Mockito.verify(partialInvalidationRequestRegister).checkRegisterForPartialInvalidationRequest(Mockito.any(AvaloqRequest.class),Mockito.any(InvalidationNotification.class));
        Mockito.verifyZeroInteractions(partialInvalidationRequestRegister);

    }


    //Test case for Full Update scenario with AvaloqRequest Parameter
    @Test
    public void testInvokeJMSAvaloqRequestFull() throws Exception {


        com.bt.nextgen.service.request.AvaloqRequest avaloqRequest= new AvaloqReportRequestImpl(StaticCodeEnumTemplate.STATIC_CODES);
        Mockito.when(requestRegister.checkRegisterForRequest(Mockito.any(AvaloqRequest.class))).thenReturn(true);
        Mockito.when(avaloqService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(jmsResponse);
        List list = new ArrayList();
        list.add("Testing123");
        Mockito.when(jmsResponse.getResponseMsgIdList()).thenReturn(list);
        boolean response=abstractAvaloqIntegrationService.invokeJMS(avaloqRequest,serviceErrors);
        Assert.assertTrue(response);
        Mockito.verify(requestRegister).checkRegisterForRequest(Mockito.any(AvaloqRequest.class));
    }

    //Test case for Full Update scenario with AvaloqRequest Parameter
    @Test
    public void testNegativeInvokeJMSAvaloqRequestFull() throws Exception {


        com.bt.nextgen.service.request.AvaloqRequest avaloqRequest= new AvaloqReportRequestImpl(StaticCodeEnumTemplate.STATIC_CODES);
        Mockito.when(requestRegister.checkRegisterForRequest(Mockito.any(AvaloqRequest.class))).thenReturn(false);
        Mockito.when(avaloqService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(jmsResponse);
        List list = new ArrayList();
        list.add("Testing123");
        Mockito.when(jmsResponse.getResponseMsgIdList()).thenReturn(list);
        boolean response=abstractAvaloqIntegrationService.invokeJMS(avaloqRequest,serviceErrors);
        Assert.assertFalse(response);
        Mockito.verify(requestRegister).checkRegisterForRequest(Mockito.any(AvaloqRequest.class));
    }




}
