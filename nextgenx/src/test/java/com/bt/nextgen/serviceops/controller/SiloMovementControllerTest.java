package com.bt.nextgen.serviceops.controller;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.MaintainIDVDetailsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.RetrieveIDVDetailsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.CreateIndividualIPResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainarrangementandiparrangementrelationships.v1.svc0256.MaintainArrangementAndIPArrangementRelationshipsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;

import com.bt.nextgen.api.client.model.ClientUpdateKey;
import com.bt.nextgen.api.client.service.CustomerDataDtoService;
import com.bt.nextgen.api.client.service.MaintainSiloMovementStatusService;
import com.bt.nextgen.api.client.service.SiloMovementRequestBuilder;
import com.bt.nextgen.api.client.service.SiloMovementService;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.serviceops.model.CreateIndividualIPReqModel;
import com.bt.nextgen.serviceops.model.MaintainArrangementAndRelationshipReqModel;
import com.bt.nextgen.serviceops.model.MaintainIdvDetailReqModel;
import com.bt.nextgen.serviceops.model.RetrieveIDVDetailsReqModel;
import com.bt.nextgen.serviceops.model.SiloMovementReqModel;
import com.bt.nextgen.serviceops.model.SiloMovementStatusModel;
import com.bt.nextgen.serviceops.service.ServiceOpsService;
import com.bt.nextgen.serviceops.silomovement.aspect.SiloMovementStatusAspect;
import com.bt.nextgen.silomovement.exception.SiloMovementException;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

@RunWith(MockitoJUnitRunner.class)
public class SiloMovementControllerTest {

	@InjectMocks
	private SiloMovementController siloMovementController;

	@Mock
	private ServiceOpsGcmSiloController serviceOpsGcmSiloController;

	@Mock
	private ServiceOpsService serviceOpsService;

	@Mock
	private CreateIndividualIpController createIndividualIpController;

	@Mock
	private ServiceOpsGcmController serviceOpsGcmController;

	@Mock
	private SiloMovementService siloMovementService;

	@Mock
	private SiloMovementRequestBuilder requestBuilder;

	@Mock
	private SiloMovementController selfSiloMovementController;

	private final static String ACCOUNT_PREFIX = "NG-003";

	@Mock
	RetrieveDetailsAndArrangementRelationshipsForIPsResponse retrieveAndArrangementRelationshipsForIPsResponse;

	private final static SimpleDateFormat DATEFORMATTER = new SimpleDateFormat("dd-MMM-yyyy");

	@Mock
	@Qualifier("maintainSiloMovementStatusService")
	private MaintainSiloMovementStatusService maintainSiloMovementStatusService;

	@Mock
	BindingResult bindingResult;

	@Mock
	private HttpServletRequest req;

	@Mock
	HttpServletResponse res;

	@Mock
	SiloMovementReqModel siloMovementReqModel;

	@Mock
	RetrieveIDVDetailsReqModel retrieveIDVDetailsReqModel;

	@Mock
	CreateIndividualIPReqModel createIndividualIPReqModel;

	@Mock
	MaintainIdvDetailReqModel maintainIdvDetailReqModel;

	@Mock
	MaintainArrangementAndRelationshipReqModel maintainArrangementAndRelationshipReqModel;

	@Mock
	SiloMovementStatusAspect siloMovementStatusAspect;

	@Mock
	private CustomerDataDtoService customerDataDtoService;

	@Mock
	RetrieveDetailsAndArrangementRelationshipsForIPsResponse response258;

	@Mock
	CreateIndividualIPResponse response336;

	@Mock
	RetrieveIDVDetailsResponse response324;

	@Mock
	MaintainIDVDetailsResponse response325;

	String[] operationTypes = { "all" };

	List<CustomerRawData> customerDataList = new ArrayList<CustomerRawData>();

	List<RoleType> roleList = new ArrayList<RoleType>();

	ServiceErrors serviceErrors;

	ClientUpdateKey id;

	List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List = new ArrayList<MaintainArrangementAndIPArrangementRelationshipsResponse>();

	private String cisKey = "12345678905";

	private RoleType personType = RoleType.INDIVIDUAL;

	private String silo = "WPAC";

	@Mock
	CustomerRawData customerRawData;

	@Mock
	ModelMap modlMap;
	
	@Mock
	WebDataBinder binder;

	@Mock
	List<CustomerRawData> rawDataList;

	@Before
	public void setup() throws Exception {
		when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(true);
		when(siloMovementReqModel.getKey()).thenReturn(cisKey);
		when(siloMovementReqModel.getPersonType()).thenReturn(personType);
		when(siloMovementReqModel.getFromSilo()).thenReturn(silo);

		when(retrieveIDVDetailsReqModel.getCisKey()).thenReturn(cisKey);
		when(retrieveIDVDetailsReqModel.getPersonType()).thenReturn(personType.name());
		when(retrieveIDVDetailsReqModel.getSilo()).thenReturn(silo);

		when(modlMap.get(Attribute.SERVICE_OPS_MODEL)).thenReturn(rawDataList);

		roleList.add(RoleType.INDIVIDUAL);
		roleList.add(RoleType.ORGANISATION);

		customerDataList.add(customerRawData);
		id = new ClientUpdateKey("", "", cisKey, personType.name());
		serviceErrors = new ServiceErrorsImpl();
		
		siloMovementController.siloMovementReqModelBinder(binder);
		siloMovementController.siloMovementStatusModelBinder(binder);
	}

	@Test
	public void testSiloMovementReq() throws SiloMovementException, JsonProcessingException {
		String siloMovementReq = siloMovementController.siloMovementReq();
		assertThat(siloMovementReq, is(View.SILO_MOVEMENT));
	}
	
	@Test
	public void testSiloMovementReqErrorPage() throws SiloMovementException, JsonProcessingException {
		when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(false);
		String siloMovementReq = siloMovementController.siloMovementReq();
		assertThat(siloMovementReq, is(View.ERROR));
	}
	
	@Test
	public void testSiloMovementSuccess() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		ModelAndView siloMovementResponse = siloMovementController.siloMovement(siloMovementReqModel, req, res, bindingResult);
		assertThat(siloMovementResponse.getViewName(), is(View.SILO_MOVEMENT));
	}
	
	@Test
	public void testSiloMovementNonITSupportRoles() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(false);
		ModelAndView siloMovementResponse = siloMovementController.siloMovement(siloMovementReqModel, req, res, bindingResult);
		assertThat(siloMovementResponse.getViewName(), is(View.ERROR));
	}

	@Test
	public void testSiloMovementTracking() {
		List<SiloMovementStatusModel> siloMovementStatusModelList = new ArrayList<SiloMovementStatusModel>();
		siloMovementStatusModelList.add(getSiloMovementStatusModel());
		// when(siloMovementStatusModelList.get(0)).thenReturn(getSiloMovementStatusModel());
		when(maintainSiloMovementStatusService.retrieveAll(getSiloMovementStatusModel())).thenReturn(siloMovementStatusModelList);
		maintainSiloMovementStatusService.create(getSiloMovementStatusModel());
		ModelAndView resultList = siloMovementController.siloMovementTracking(getSiloMovementStatusModel());
		assertNotNull(resultList);
		assertEquals(resultList.getViewName(), View.SILO_MOVEMENT_RESPONSE);
		assertEquals(resultList.getModel().size(), 4);
	}
	
	@Test
	public void testSiloMovementTrackingFailure() {
		when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(false);
		maintainSiloMovementStatusService.create(getSiloMovementStatusModel());
		ModelAndView resultList = siloMovementController.siloMovementTracking(getSiloMovementStatusModel());
		assertEquals(resultList.getViewName(), View.ERROR);
	}

	@Test
	public void testSiloMovementResponse() {
		when(maintainSiloMovementStatusService.retrieve(getSiloMovementStatusModel().getAppId())).thenReturn(getSiloMovementStatusModel());
		maintainSiloMovementStatusService.create(getSiloMovementStatusModel());
		SiloMovementStatusModel result = siloMovementController.bulkupdateResponse(getSiloMovementStatusModel().getAppId());
		assertTrue(result.getAppId().equals(getSiloMovementStatusModel().getAppId()));
	}
	
	@Test(expected=AccessDeniedException.class)
	public void testSiloMovementResponseAccessdenied() {
		when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(false);
		maintainSiloMovementStatusService.create(getSiloMovementStatusModel());
		siloMovementController.bulkupdateResponse(getSiloMovementStatusModel().getAppId());
	}

	private SiloMovementStatusModel getSiloMovementStatusModel() {
		SiloMovementStatusModel siloMovementStatusModel = new SiloMovementStatusModel();
		siloMovementStatusModel.setAppId((long) 1);
		siloMovementStatusModel.setDatetimeEnd("05/07/2017");
		siloMovementStatusModel.setDatetimeStart("05/07/2017");
		siloMovementStatusModel.setErrMsg("");
		siloMovementStatusModel.setErrState("");
		siloMovementStatusModel.setFromSilo("WPAC");
		siloMovementStatusModel.setLastSuccState("MNT256");
		siloMovementStatusModel.setNewCis("32145678901");
		siloMovementStatusModel.setOldCis("12345678905");
		siloMovementStatusModel.setToSilo("BTFG");
		siloMovementStatusModel.setUserId("CS12345");
		return siloMovementStatusModel;
	}
}
