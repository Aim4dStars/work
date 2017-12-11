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
import org.springframework.web.servlet.ModelAndView;

import com.bt.nextgen.api.client.service.MaintainIdvDetailDtoService;
import com.bt.nextgen.api.client.service.RetrivePostalAddressDataDtoService;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawDataImpl;
import com.bt.nextgen.serviceops.model.MaintainIdvDetailReqModel;
import com.bt.nextgen.serviceops.service.ServiceOpsService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

/**
 * @author L081050
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceOpsGcmSiloControllerTest {

	@Mock
	ServiceOpsService serviceOpsService;

	@Mock
	private MaintainIdvDetailDtoService maintainIdvDetailDtoService;
	@Mock
	private RetrivePostalAddressDataDtoService retrivePostalAddressService;
	MockHttpServletRequest request;

	MockHttpServletResponse response;

	@InjectMocks
	private ServiceOpsGcmSiloController serviceOpsGcmController;

	@Test
	public void testMaintainIndividualIdvReq() {
		request = new MockHttpServletRequest();
		MaintainIdvDetailReqModel req = new MaintainIdvDetailReqModel();
		req.setAddressline1("pune");
		req.setCisKey("12345678901");
		Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
				false);
		Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(
				true);
		ModelAndView modelAndView = serviceOpsGcmController
				.maintainIdvDetailsReq();
		assertThat(modelAndView.getViewName(), is(View.MAINTAIN_IDV_DETAILS));
	}

	@Test
	public void testMaintainIndividualIdvReqError() {
		request = new MockHttpServletRequest();
		MaintainIdvDetailReqModel req = new MaintainIdvDetailReqModel();
		req.setAddressline1("pune");
		req.setCisKey("12345678901");
		Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
				true);
		ModelAndView modelAndView = serviceOpsGcmController
				.maintainIdvDetailsReq();
		assertThat(modelAndView.getViewName(), is(View.ERROR));
	}

	@Test
	public void testMaintainIndividualIdv() {
		request = new MockHttpServletRequest();
		MaintainIdvDetailReqModel req = new MaintainIdvDetailReqModel();
		req.setAddressline1("pune");
		req.setCisKey("12345678901");
		req.setAgentName("xyz");
		req.setAgentCisKey("12345678901");
		req.setCity("dfgfgfhdfh");
		req.setCountry("rttyyeyuu");
		req.setDateOfBirth("1980-21-12");
		req.setDocumentType("Passport");
		req.setEmployerName("asdfghjjk");
		req.setExtIdvDate("2010-23-01");
		req.setFirstName("assddghhf");
		req.setLastName("gdgfhfjjfj");
		req.setOptAttrName("rettye,weteryyy,eertyye");
		req.setOptAttrVal("dfdgdggdf,sdfdggg,dfdghd");
		req.setPersonType("Individual");
		req.setPincode("123456");
		req.setSilo("WP");
		req.setState("NSW");
		req.setUsage("UPDATE");

		Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
				false);
		Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(
				true);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerRawData customerRawData = mock(CustomerRawDataImpl.class);
		when(maintainIdvDetailDtoService.maintain(req, serviceErrors))
				.thenReturn(customerRawData);
		HttpServletResponse resp = mock(HttpServletResponse.class);
		ModelAndView modelAndView = serviceOpsGcmController.maintainIdvDetails(
				req, request);
		assertThat(modelAndView.getViewName(), is(View.GCM_SERVICEOPS_RESPONSE));
		assertThat(modelAndView.getModel(), notNullValue());
	}

	@Test
	public void testMaintainIndividualIdvError() {
		request = new MockHttpServletRequest();
		MaintainIdvDetailReqModel req = new MaintainIdvDetailReqModel();
		req.setAddressline1("pune");
		req.setCisKey("12345678901");
		Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
				true);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerRawData customerRawData = mock(CustomerRawDataImpl.class);
		when(maintainIdvDetailDtoService.maintain(req, serviceErrors))
				.thenReturn(customerRawData);
		HttpServletResponse resp = mock(HttpServletResponse.class);
		ModelAndView modelAndView = serviceOpsGcmController.maintainIdvDetails(
				req, request);
		assertThat(modelAndView.getViewName(), is(View.ERROR));
	}

}
