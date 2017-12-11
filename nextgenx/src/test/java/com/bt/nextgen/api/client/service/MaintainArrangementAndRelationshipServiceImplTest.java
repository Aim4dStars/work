package com.bt.nextgen.api.client.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainarrangementandiparrangementrelationships.v1.svc0256.ActionCode;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainarrangementandiparrangementrelationships.v1.svc0256.MaintainArrangementAndIPArrangementRelationshipsResponse;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawDataImpl;
import com.bt.nextgen.service.group.customer.groupesb.maintainarrangementandiparrangementrelationships.ArrangementAndRelationshipManagementRequest;
import com.bt.nextgen.service.group.maintainarrangementandiparrangementrelationships.MaintainArrangementAndRelationshipIntegrationService;
import com.bt.nextgen.serviceops.model.MaintainArrangementAndRelationshipReqModel;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

@RunWith(MockitoJUnitRunner.class)
public class MaintainArrangementAndRelationshipServiceImplTest {

    private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    @InjectMocks
    private MaintainArrangementAndRelationshipServiceImpl maintainArrangementAndRelationshipService;

    @Mock
    private MaintainArrangementAndRelationshipIntegrationService maintainArrangementAndRelationshipIntegrationService;

    @Test
    public void testCreateArrangementAndRelationShipUseCase1() throws JsonProcessingException {
        MaintainArrangementAndRelationshipReqModel input = new MaintainArrangementAndRelationshipReqModel();
        ServiceErrors serviceError = new ServiceErrorsImpl();
        String endDate = formatter.format(new Date());
        input.setAccountNumber("1234567");
        input.setEndDate(endDate);
        input.setBsbNumber("12579");
        input.setUseCase("ipar");
        input.setHour("10");
        input.setMin("10");
        input.setSec("10");
        input.setCisKey("555555");
        input.setHasArrangementRole(true);
        input.setLifecycleStatus("Restricted");
        input.setLifecycleStatusReason("adv");
        input.setPanNumber("BBIOP1256790");
        input.setPersonType("individual");
        input.setProductCpc("4587879");
        input.setVersionNumberIpAr("1");
        input.setRequestedAction(ActionCode.MAINTAIN_ARRANGEMENT_AND_RELATIONSHIPS);
        input.setSilo("WPAC");
        input.setVersionNumberAr("1");
        input.setVersionNumberIpAr("1");
        CustomerRawData customerRawData = new CustomerRawDataImpl("aaadf");

        when(
                maintainArrangementAndRelationshipIntegrationService.createArrangementAndRelationShip(
                        any(ArrangementAndRelationshipManagementRequest.class), any(ServiceErrors.class))).thenReturn(
                customerRawData);
        customerRawData =
                maintainArrangementAndRelationshipService.createArrangementAndRelationShip(input, serviceError);
        assertThat(customerRawData.getRawResponse(), CoreMatchers.equalTo("\"aaadf\""));
        assertThat(input.getHour(),CoreMatchers.equalTo("10"));
        assertThat(input.getMin(),CoreMatchers.equalTo("10"));
        assertThat(input.getSec(),CoreMatchers.equalTo("10"));
        assertThat(input.getSilo(),CoreMatchers.equalTo("WPAC"));
        assertTrue(input.getRequestedAction().equals(ActionCode.MAINTAIN_ARRANGEMENT_AND_RELATIONSHIPS));
        assertThat(input.getEndDate(),CoreMatchers.equalTo(endDate));
        assertThat(input.getLifecycleStatus(),CoreMatchers.equalTo("Restricted"));
        assertThat(input.isHasArrangementRole(),CoreMatchers.is(true));
    }
    
    @Test
    public void testCreateArrangementAndRelationShipUseCase1NullCheck() throws JsonProcessingException {
        MaintainArrangementAndRelationshipReqModel input = new MaintainArrangementAndRelationshipReqModel();
        ServiceErrors serviceError = new ServiceErrorsImpl();
        String endDate = formatter.format(new Date());
        input.setAccountNumber(null);
        input.setEndDate(null);
        input.setBsbNumber(null);
        input.setUseCase("ipar");
        input.setHour(null);
        input.setMin(null);
        input.setSec(null);
        input.setCisKey(null);
        input.setLifecycleStatus(null);
        input.setLifecycleStatusReason(null);
        input.setPanNumber(null);
        input.setPersonType(null);
        input.setProductCpc(null);
        input.setVersionNumberIpAr(null);
        input.setRequestedAction(null);
        input.setSilo(null);
        input.setVersionNumberAr(null);
        input.setVersionNumberIpAr(null);
        CustomerRawData customerRawData = new CustomerRawDataImpl("aaadf");

        when(
                maintainArrangementAndRelationshipIntegrationService.createArrangementAndRelationShip(
                        any(ArrangementAndRelationshipManagementRequest.class), any(ServiceErrors.class))).thenReturn(
                customerRawData);
        customerRawData =
                maintainArrangementAndRelationshipService.createArrangementAndRelationShip(input, serviceError);
        assertThat(customerRawData.getRawResponse(), CoreMatchers.equalTo("\"aaadf\""));
        assertThat(input.getHour(),CoreMatchers.equalTo(""));
        assertThat(input.getMin(),CoreMatchers.equalTo(""));
        assertThat(input.getSec(),CoreMatchers.equalTo(""));
        assertThat(input.getSilo(),CoreMatchers.equalTo(""));
        assertTrue(input.getRequestedAction().equals(ActionCode.MAINTAIN_ARRANGEMENT_AND_RELATIONSHIPS));
        assertThat(input.getEndDate(),CoreMatchers.equalTo(""));
        assertThat(input.getLifecycleStatus(),CoreMatchers.equalTo(""));
        assertThat(input.isHasArrangementRole(),CoreMatchers.is(false));
    }

    @Test
    public void testCreateArrangementAndRelationShipUseCase2() throws JsonProcessingException {
        MaintainArrangementAndRelationshipReqModel input = new MaintainArrangementAndRelationshipReqModel();
        ServiceErrors serviceError = new ServiceErrorsImpl();
        input.setUseCase("ipsar");
        input.setHour("10");
        input.setMin("10");
        input.setSec("10");
        CustomerRawData customerRawData = new CustomerRawDataImpl("aaa");
        when(
                maintainArrangementAndRelationshipIntegrationService.createArrangementAndRelationShip(
                        any(ArrangementAndRelationshipManagementRequest.class), any(ServiceErrors.class))).thenReturn(
                customerRawData);

        customerRawData =
                maintainArrangementAndRelationshipService.createArrangementAndRelationShip(input, serviceError);
        assertThat(customerRawData.getRawResponse(), CoreMatchers.equalTo("\"aaa\""));

    }

    @Test
    public void testCreateArrangementAndRelationShipUseCase3() throws JsonProcessingException {
        MaintainArrangementAndRelationshipReqModel input = new MaintainArrangementAndRelationshipReqModel();
        ServiceErrors serviceError = new ServiceErrorsImpl();
        input.setUseCase("iparthirdparty");
        input.setHour("10");
        input.setMin("10");
        input.setSec("10");
        CustomerRawData customerRawData = new CustomerRawDataImpl("abcd");
        when(
                maintainArrangementAndRelationshipIntegrationService.createArrangementAndRelationShip(
                        any(ArrangementAndRelationshipManagementRequest.class), any(ServiceErrors.class))).thenReturn(
                customerRawData);

        customerRawData =
                maintainArrangementAndRelationshipService.createArrangementAndRelationShip(input, serviceError);
        assertThat(customerRawData.getRawResponse(), CoreMatchers.equalTo("\"abcd\""));

    }

    @Test
    public void testCreateArrangementAndRelationShipUseCase4() throws JsonProcessingException {
        MaintainArrangementAndRelationshipReqModel input = new MaintainArrangementAndRelationshipReqModel();
        ServiceErrors serviceError = new ServiceErrorsImpl();
        input.setUseCase("enddateiparsol");
        input.setHour("10");
        input.setMin("10");
        input.setSec("10");
        CustomerRawData customerRawData = new CustomerRawDataImpl("endDateiparsol");
        when(
                maintainArrangementAndRelationshipIntegrationService.createArrangementAndRelationShip(
                        any(ArrangementAndRelationshipManagementRequest.class), any(ServiceErrors.class))).thenReturn(
                customerRawData);

        customerRawData =
                maintainArrangementAndRelationshipService.createArrangementAndRelationShip(input, serviceError);
        assertThat(customerRawData.getRawResponse(), CoreMatchers.equalTo("\"endDateiparsol\""));

    }

    @Test
    public void testCreateArrangementAndRelationShipUseCase5() throws JsonProcessingException {
        MaintainArrangementAndRelationshipReqModel input = new MaintainArrangementAndRelationshipReqModel();
        ServiceErrors serviceError = new ServiceErrorsImpl();
        input.setUseCase("enddateipsar");
        input.setHour("10");
        input.setMin("10");
        input.setSec("10");
        CustomerRawData customerRawData = new CustomerRawDataImpl("endDateiparsol");
        when(
                maintainArrangementAndRelationshipIntegrationService.createArrangementAndRelationShip(
                        any(ArrangementAndRelationshipManagementRequest.class), any(ServiceErrors.class))).thenReturn(
                customerRawData);

        customerRawData =
                maintainArrangementAndRelationshipService.createArrangementAndRelationShip(input, serviceError);
        assertThat(customerRawData.getRawResponse(), CoreMatchers.equalTo("\"endDateiparsol\""));

    }

    @Test
    public void testCreateArrangementAndRelationShipUseCase6() throws JsonProcessingException {
        MaintainArrangementAndRelationshipReqModel input = new MaintainArrangementAndRelationshipReqModel();
        input.setStartDate(formatter.format(new Date()));
        input.setEndDate(formatter.format(new Date()));
        ServiceErrors serviceError = new ServiceErrorsImpl();
        input.setUseCase("enddateiparthirdparty");
        input.setHour("10");
        input.setMin("10");
        input.setSec("10");
        CustomerRawData customerRawData = new CustomerRawDataImpl("end date");
        when(
                maintainArrangementAndRelationshipIntegrationService.createArrangementAndRelationShip(
                        any(ArrangementAndRelationshipManagementRequest.class), any(ServiceErrors.class))).thenReturn(
                customerRawData);

        customerRawData =
                maintainArrangementAndRelationshipService.createArrangementAndRelationShip(input, serviceError);
        assertThat(customerRawData.getRawResponse(), CoreMatchers.equalTo("\"end date\""));

    }
}
