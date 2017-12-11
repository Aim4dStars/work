/**
 * 
 */
package com.bt.nextgen.serviceops.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import com.bt.nextgen.api.client.service.CreateOrganisationIPDataDtoService;
import com.bt.nextgen.api.client.service.RetrivePostalAddressDataDtoService;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawDataImpl;
import com.bt.nextgen.serviceops.model.CreateOraganisationIPReqModel;
import com.bt.nextgen.serviceops.service.ServiceOpsService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

/**
 * @author L081050
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateOrganisationIPControllerTest {
    @Mock
    ServiceOpsService serviceOpsService;

    @Mock
    private CreateOrganisationIPDataDtoService CreateOrganisationIPDataDtoService;
    MockHttpServletRequest request;

    MockHttpServletResponse response;

    @Mock
    BindingResult bindingResult;
    @InjectMocks
    private CreateOrganisationIPController createOrganisationIPController;
    @Test
    public void testCreateOrganisationIPReq() {
        Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
                false);
        Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(
                true);
        String view = createOrganisationIPController
                .createOrganisationIPReq();
        assertThat(view, is(View.CREATE_ORGANIGATION_IP));
    }

    @Test
    public void testCreateOrganisationIPReqError() {
        request = new MockHttpServletRequest();
        Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
                true);
        String view = createOrganisationIPController
                .createOrganisationIPReq();
        assertThat(view, is(View.ERROR));
    }

    @Test
    public void testCreateOrganisationIP() {
        request = new MockHttpServletRequest();
        CreateOraganisationIPReqModel req = new CreateOraganisationIPReqModel();
        req.setAddresseeNameText("Test");
        req.setAddressType("test");
        req.setAddrspriorityLevel("Primary");
        req.setCharacteristicCode("test");
        req.setCharacteristicType("test");
        req.setCharacteristicValue("value");
        req.setCity("test");
        req.setCountry("test");
        //req.setEffectiveStartDate("2017-12-12");
        req.setFrn("123343");
        req.setFullName("test");
        req.setFrntype("ACN");
        req.setIndustryCode("wegyfy");
        req.setIsIssuedAtC("test");
        req.setIsForeignRegistered("123444");
        req.setIsIssuedAtS("ghfjjifj");
        req.setOrganisationLegalStructureValue("tyrwq");
        req.setPersonType("ettyry");
        req.setPostCode("123343");
        req.setPriorityLevel("Primary");
        req.setPurposeOfBusinessRelationship("asdf");
        req.setRegistrationNumber("123445455");
        req.setSilo("WPAC");
        req.setSourceOfFunds("qweer");
        req.setSourceOfWealth("qwerrr");
        req.setStartDate("2017-12-12");
        req.setState("NSW");
        req.setStreetName("werttysy");
        req.setStreetNumber("r266376");
        req.setStreetType("werty");
        req.setUsage("erettey");
        Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
                false);
        Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(
                true);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        CustomerRawData customerRawData = mock(CustomerRawDataImpl.class);
        when(CreateOrganisationIPDataDtoService.create(req, serviceErrors))
                .thenReturn(customerRawData);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        ModelAndView modelAndView = createOrganisationIPController.createOrganisationIP(req, request,bindingResult);
        assertThat(modelAndView.getViewName(), is(View.GCM_SERVICEOPS_RESPONSE));
        assertThat(modelAndView.getModel(), notNullValue());
    }
    @Test
    public void testCreateOrganisationIPError() {
        request = new MockHttpServletRequest();
        CreateOraganisationIPReqModel req = new CreateOraganisationIPReqModel();

        Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(
                false);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        CustomerRawData customerRawData = mock(CustomerRawDataImpl.class);
        when(CreateOrganisationIPDataDtoService.create(req, serviceErrors))
                .thenReturn(customerRawData);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        ModelAndView modelAndView = createOrganisationIPController.createOrganisationIP(req, request,bindingResult);
        assertThat(modelAndView.getViewName(), is(View.ERROR));
    }
}
