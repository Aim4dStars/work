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

import au.com.westpac.gn.locationmanagement.services.locationmanagement.xsd.retrievepostaladdress.v1.svc0454.AddressType;

import com.bt.nextgen.api.client.model.ClientUpdateKey;
import com.bt.nextgen.api.client.service.CustomerDataDtoService;
import com.bt.nextgen.api.client.service.IPToIPRelationshipsDataDtoService;
import com.bt.nextgen.api.client.service.MaintainArrangementAndRelationshipService;
import com.bt.nextgen.api.client.service.MaintainIdvDetailDtoService;
import com.bt.nextgen.api.client.service.MaintainIpToIpRelationshipDTOService;
import com.bt.nextgen.api.client.service.RetriveIDVDetailsDataDtoService;
import com.bt.nextgen.api.client.service.RetrivePostalAddressDataDtoService;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawDataImpl;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.serviceops.model.MaintainArrangementAndRelationshipReqModel;
import com.bt.nextgen.serviceops.model.MaintainIdvDetailReqModel;
import com.bt.nextgen.serviceops.model.MaintainIpToIpRelationshipReqModel;
import com.bt.nextgen.serviceops.model.RetrieveIDVDetailsReqModel;
import com.bt.nextgen.serviceops.model.RetriveIpToIpRelationshipReqModel;
import com.bt.nextgen.serviceops.model.RetrivePostalAddressReqModel;
import com.bt.nextgen.serviceops.service.ServiceOpsService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@RunWith(MockitoJUnitRunner.class)
public class ServiceOpsGcmControllerTest {
	@Mock
	CustomerDataDtoService customerDataDtoService;
	@Mock
	RetriveIDVDetailsDataDtoService retriveIDVDetailsDataDtoService;

	@Mock
	IPToIPRelationshipsDataDtoService retriveIpToIpRelationshipDtoService;

	@Mock
	ServiceOpsService serviceOpsService;

	@Mock
	private MaintainIpToIpRelationshipDTOService maintainIpToIpRelationshipDTOService;

	@Mock
	private MaintainArrangementAndRelationshipService maintainArrangementAndRelationshipService;
	@Mock
	private MaintainIdvDetailDtoService maintainIdvDetailDtoService;
	@Mock
	private RetrivePostalAddressDataDtoService retrivePostalAddressService;
	MockHttpServletRequest request;

	MockHttpServletResponse response;

	@Mock
    BindingResult bindingResult;
	
	@InjectMocks
	private ServiceOpsGcmController serviceOpsGcmController;

	@Test
	public void testRetrivePostalAddress() {
		RetrivePostalAddressReqModel req = new RetrivePostalAddressReqModel();
		req.setAddressType(AddressType.D);
		req.setKey("12345678901");
		Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
				false);
		Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(
				true);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerRawData customerRawData = mock(CustomerRawDataImpl.class);
		when(retrivePostalAddressService.retrieve(req, serviceErrors))
				.thenReturn(customerRawData);
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();

		ModelAndView modelAndView = serviceOpsGcmController
				.retrivePostalAddress(req, request);
		assertThat(modelAndView.getViewName(), is(View.GCM_SERVICEOPS_RESPONSE));
		assertThat(modelAndView.getModel(), notNullValue());
	}

	@Test
	public void testRetrivePostalAddressError() {
		RetrivePostalAddressReqModel req = new RetrivePostalAddressReqModel();
		req.setAddressType(AddressType.D);
		req.setKey("12345678901");
		Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
				true);
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();

		ModelAndView modelAndView = serviceOpsGcmController
				.retrivePostalAddress(req, request);
		assertThat(modelAndView.getViewName(), is(View.ERROR));
		assertThat(modelAndView.getModel(), notNullValue());
	}

	@Test
	public void testRetrivePostalAddressReq() {
		RetrivePostalAddressReqModel req = new RetrivePostalAddressReqModel();
		req.setAddressType(AddressType.D);
		Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
				false);
		Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(
				true);
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();

		ModelAndView modelAndView = serviceOpsGcmController
				.retrivePostalAddress(req, request);
		assertThat(modelAndView.getViewName(), is(View.RETRIVE_POSTAL_ADDRESS));
		assertThat(modelAndView.getModel(), notNullValue());
	}

	@Test
	public void testMaintainIpToIpRelationship() {
		Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
				false);
		Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(
				true);
		String view = serviceOpsGcmController.maintainIpToIpRelationship();
		assertThat(view, is(View.MAINTAIN_IP_TO_IP_RELATIONSHIP));
	}

	@Test
	public void testMaintainIpToIpRelationshipError() {
		Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
				true);
		String view = serviceOpsGcmController.maintainIpToIpRelationship();
		assertThat(view, is(View.ERROR));
	}

	@Test
	public void testGcmServiceOpsHome() {
		Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
				false);
		Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(
				true);
		String view = serviceOpsGcmController.gcmServiceOpsHome();
		assertThat(view, is(View.GCM_SERVICEOPS_HOME));
	}

	@Test
	public void testGcmServiceOpsHomeError() {
		Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
				true);
		String view = serviceOpsGcmController.gcmServiceOpsHome();
		assertThat(view, is(View.ERROR));
	}

	@Test
	public void testMaintainArrangementAndrelationshipReq() {
		Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
				false);
		Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(
				true);
		String view = serviceOpsGcmController
				.maintainArrangementAndrelationshipReq();
		assertThat(view, is(View.MAINTAIN_ARRANGEMENT_AND_RELATIONSHIP_REQ));
	}

	@Test
	public void testMaintainArrangementAndrelationshipReqError() {
		Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
				true);
		String view = serviceOpsGcmController
				.maintainArrangementAndrelationshipReq();
		assertThat(view, is(View.ERROR));
	}

	
	@Test
	public void testretrieveIDVDetailsWithError() {
		RetrieveIDVDetailsReqModel req = new RetrieveIDVDetailsReqModel();
		req.setCisKey("55555");
		req.setPersonType("individual");
		req.setSilo("WPAC");
		Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
				false);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerRawData customerRawData = mock(CustomerRawDataImpl.class);
		when(retriveIDVDetailsDataDtoService.retrieve(req, serviceErrors))
				.thenReturn(customerRawData);
		HttpServletResponse resp = mock(HttpServletResponse.class);
		ModelAndView modelAndView = serviceOpsGcmController
				.retrieveIDVDetails(req,bindingResult);
		assertThat(modelAndView.getViewName(), is(View.ERROR));
		assertThat(modelAndView.getModel(), notNullValue());
	}

	@Test
	public void testretrieveIDVDetails() {
		RetrieveIDVDetailsReqModel req = new RetrieveIDVDetailsReqModel();
		req.setCisKey("55555");
		req.setPersonType("individual");
		req.setSilo("WPAC");
		Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
				false);
		Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(
				true);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerRawData customerRawData = mock(CustomerRawDataImpl.class);
		when(retriveIDVDetailsDataDtoService.retrieve(req, serviceErrors))
				.thenReturn(customerRawData);
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();

		ModelAndView modelAndView = serviceOpsGcmController
				.retrieveIDVDetails(req,bindingResult);
		assertThat(modelAndView.getViewName(), is(View.GCM_SERVICEOPS_RESPONSE));
		assertThat(modelAndView.getModel(), notNullValue());
	}

	@Test
	public void testretrieveIptoip() {
		RetriveIpToIpRelationshipReqModel req = new RetriveIpToIpRelationshipReqModel();
		req.setCisKey("12345556662");
		req.setRoleType("Individual");
		req.setSilo("WPAC");
		request = new MockHttpServletRequest();
		request.setParameter("cisKey", "12345556662");
		request.setParameter("personType", "Individual");
		request.setParameter("silo", "WPAC");
		Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
				false);
		Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(
				true);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerRawData customerRawData = mock(CustomerRawDataImpl.class);
		when(retriveIpToIpRelationshipDtoService.retrieve(req, serviceErrors))
				.thenReturn(customerRawData);
		HttpServletResponse resp = mock(HttpServletResponse.class);
		ModelAndView modelAndView = serviceOpsGcmController
				.retriveIpToIpRelationship(request);
		assertThat(modelAndView.getViewName(), is(View.GCM_SERVICEOPS_RESPONSE));
		assertThat(modelAndView.getModel(), notNullValue());
	}

	@Test
	public void testretrieveIptoipWithError() {
		RetriveIpToIpRelationshipReqModel req = new RetriveIpToIpRelationshipReqModel();
		req.setCisKey("12345556662");
		req.setRoleType("Individual");
		req.setSilo("WPAC");
		request = new MockHttpServletRequest();
		request.setParameter("cisKey", "12345556662");
		request.setParameter("personType", "Individual");
		request.setParameter("silo", "WPAC");
		Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(
				false);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerRawData customerRawData = mock(CustomerRawDataImpl.class);
		when(retriveIpToIpRelationshipDtoService.retrieve(req, serviceErrors))
				.thenReturn(customerRawData);
		HttpServletResponse resp = mock(HttpServletResponse.class);
		ModelAndView modelAndView = serviceOpsGcmController
				.retriveIpToIpRelationship(request);
		assertThat(modelAndView.getViewName(), is(View.ERROR));
		assertThat(modelAndView.getModel(), notNullValue());
	}

	@Test
	public void testmaintainArrangementsAndRelashinship() {
		request = new MockHttpServletRequest();
		MaintainArrangementAndRelationshipReqModel req = new MaintainArrangementAndRelationshipReqModel();
		req.setBsbNumber("12555");
		req.setCisKey("55555");
		req.setLifecycleStatusReason("aa");
		req.setPersonType("individual");
		req.setAccountNumber("1234567");
		req.setSilo("WPAC");
		req.setStartDate("10 Feb 2017");
		req.setVersionNumberAr("7");
		Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
				false);
		Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(
				true);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerRawData customerRawData = mock(CustomerRawDataImpl.class);
		when(
				maintainArrangementAndRelationshipService
						.createArrangementAndRelationShip(req, serviceErrors))
				.thenReturn(customerRawData);
		HttpServletResponse resp = mock(HttpServletResponse.class);
		ModelAndView modelAndView = serviceOpsGcmController
				.maintainArrangementsAndRelashinship(req, request, resp);
		assertThat(modelAndView.getViewName(), is(View.GCM_SERVICEOPS_RESPONSE));
		assertThat(modelAndView.getModel(), notNullValue());
	}

	@Test
	public void testmaintainArrangementsAndRelashinshipWithError() {
		MaintainArrangementAndRelationshipReqModel req = new MaintainArrangementAndRelationshipReqModel();
		req.setBsbNumber("12555");
		req.setCisKey("55555");
		req.setLifecycleStatusReason("aa");
		req.setPersonType("individual");
		req.setAccountNumber("1234567");
		req.setSilo("WPAC");
		req.setStartDate("10 Feb 2017");
		req.setVersionNumberAr("7");
		Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
				false);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerRawData customerRawData = mock(CustomerRawDataImpl.class);
		when(
				maintainArrangementAndRelationshipService
						.createArrangementAndRelationShip(req, serviceErrors))
				.thenReturn(customerRawData);
		HttpServletResponse resp = mock(HttpServletResponse.class);
		ModelAndView modelAndView = serviceOpsGcmController
				.maintainArrangementsAndRelashinship(req, request, resp);
		assertThat(modelAndView.getViewName(), is(View.ERROR));
		assertThat(modelAndView.getModel(), notNullValue());
	}

	@Test
	public void testmaintainIpToIpRelashinship() {
		MaintainIpToIpRelationshipReqModel req = new MaintainIpToIpRelationshipReqModel();
		req.setUseCase("Add");
		req.setTargetCISKey("55555");
		req.setSourceCISKey("55555");
		req.setPartyRelStatus("BOE");
		req.setPartyRelType("Active");
		req.setTargetPersonType("individual");
		req.setSourcePersonType("individual");
		req.setPartyRelModNum("1234567");
		req.setSilo("WPAC");
		req.setPartyRelStartDate("10 Feb 2017");
		req.setPartyRelEndDate("10 Feb 2017");
		Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
				false);
		Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(
				true);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerRawData customerRawData = mock(CustomerRawDataImpl.class);
		when(
				maintainIpToIpRelationshipDTOService
						.maintainIpToIpRelationship(req, serviceErrors))
				.thenReturn(customerRawData);
		HttpServletResponse resp = mock(HttpServletResponse.class);
		ModelAndView modelAndView = serviceOpsGcmController
				.maintainIpToIpRelationship(req, request, resp,bindingResult);
		assertThat(modelAndView.getViewName(), is(View.GCM_SERVICEOPS_RESPONSE));
		assertThat(modelAndView.getModel(), notNullValue());
	}

	@Test
	public void testClientSearch() {

		Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
				false);
		Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(
				true);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerRawData customerRawData = mock(CustomerRawDataImpl.class);
		String[] data = { "test" };
		ClientUpdateKey id = new ClientUpdateKey("", "", "12344455566",
				"individual");
		when(customerDataDtoService.retrieve(id, "WPAC", data, serviceErrors))
				.thenReturn(customerRawData);
		HttpServletResponse resp = mock(HttpServletResponse.class);
		ModelAndView modelAndView = serviceOpsGcmController
				.searchClientDetails("12344455566", RoleType.INDIVIDUAL, data,
						"WPAC");
		assertThat(modelAndView.getViewName(), is(View.GCM_SERVICEOPS_RESPONSE));
		assertThat(modelAndView.getModel(), notNullValue());
	}

	@Test
	public void testmaintainIpToIpRelashinshipWithError() {
		MaintainIpToIpRelationshipReqModel req = new MaintainIpToIpRelationshipReqModel();
		req.setUseCase("Add");
		req.setTargetCISKey("55555");
		req.setSourceCISKey("55555");
		req.setPartyRelStatus("BOE");
		req.setPartyRelType("Active");
		req.setTargetPersonType("individual");
		req.setSourcePersonType("individual");
		req.setPartyRelModNum("1234567");
		req.setSilo("WPAC");
		req.setPartyRelStartDate("10 Feb 2017");
		req.setPartyRelEndDate("10 Feb 2017");
		Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
				false);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerRawData customerRawData = mock(CustomerRawDataImpl.class);
		when(
				maintainIpToIpRelationshipDTOService
						.maintainIpToIpRelationship(req, serviceErrors))
				.thenReturn(customerRawData);
		HttpServletResponse resp = mock(HttpServletResponse.class);
		ModelAndView modelAndView = serviceOpsGcmController
				.maintainIpToIpRelationship(req, request, resp,bindingResult);
		assertThat(modelAndView.getViewName(), is(View.ERROR));
		assertThat(modelAndView.getModel(), notNullValue());
	}
}