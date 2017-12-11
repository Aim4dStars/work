package com.bt.nextgen.api.client.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.ProductSystemIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.MaintainIDVDetailsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.RetrieveIDVDetailsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.CreateIndividualIPResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.InvolvedParty;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.PriorityLevel;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainarrangementandiparrangementrelationships.v1.svc0256.MaintainArrangementAndIPArrangementRelationshipsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyArrangementRole;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Product;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.ProductArrangement;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;

import com.bt.nextgen.api.client.model.ClientUpdateKey;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.ClientDetailsOperation;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.serviceops.controller.CreateIndividualIpController;
import com.bt.nextgen.serviceops.controller.ServiceOpsGcmController;
import com.bt.nextgen.serviceops.controller.ServiceOpsGcmSiloController;
import com.bt.nextgen.serviceops.model.CreateIndividualIPEmailPhoneContactMethodsReqModel;
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
public class SiloMovementServiceImplTest {

	@InjectMocks
	@Qualifier("siloMovementService")
	private SiloMovementServiceImpl siloMovementService;

	@Mock
	private ServiceOpsGcmSiloController serviceOpsGcmSiloController;

	@Mock
	private ServiceOpsService serviceOpsService;

	@Mock
	private CreateIndividualIpController createIndividualIpController;

	@Mock
	private ServiceOpsGcmController serviceOpsGcmController;

	@Mock
	private SiloMovementRequestBuilder requestBuilder;

	@Mock
	private SiloMovementServiceImpl selfSiloMovementService;

	private final static String ACCOUNT_PREFIX = "NG-003";

	private final static String CORRECT_ACCOUNT_PREFIX = "NG-002";

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

	String[] operationTypes = { "ID", "IP" };

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
		operationTypes = getAllCustomerManagementOperation();
	}

	@Test
	public void testSiloMovementSuccess() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		siloMovementService.siloMovement(siloMovementReqModel, bindingResult, req, res);
	}

	@Test
	public void testExecute258Success() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		ModelAndView modelAndView258 = Mockito.mock(ModelAndView.class);
		when(
				serviceOpsGcmController.searchClientDetails(siloMovementReqModel.getKey(), siloMovementReqModel.getPersonType(), operationTypes,
						siloMovementReqModel.getFromSilo())).thenReturn(modelAndView258);
		when(modelAndView258.getModelMap()).thenReturn(modlMap);
		when(modlMap.get(Attribute.SERVICE_OPS_MODEL)).thenReturn(customerDataList);
		when(customerRawData.getResponseObject()).thenReturn(response258);
		RetrieveDetailsAndArrangementRelationshipsForIPsResponse siloMovementService258Response = siloMovementService
				.executeService258(siloMovementReqModel);
		assertThat(siloMovementService258Response, is(RetrieveDetailsAndArrangementRelationshipsForIPsResponse.class));
	}

	@Test(expected = SiloMovementException.class)
	public void testExecute258Error() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		ModelAndView modelAndView258 = Mockito.mock(ModelAndView.class);
		when(
				serviceOpsGcmController.searchClientDetails(siloMovementReqModel.getKey(), siloMovementReqModel.getPersonType(), operationTypes,
						siloMovementReqModel.getFromSilo())).thenReturn(modelAndView258);
		when(modelAndView258.getModelMap()).thenReturn(modlMap);
		when(modlMap.get(Attribute.SERVICE_OPS_MODEL)).thenReturn(null);
		when(maintainSiloMovementStatusService.retrieve(SiloMovementStatusAspect.getSiloMovementStatusModel().getAppId())).thenReturn(
				getSiloMovementStatusModel());
		siloMovementService.executeService258(siloMovementReqModel);
	}

	@Test
	public void testExecute324Success() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		ModelAndView modelAndView324 = Mockito.mock(ModelAndView.class);
		when(
				requestBuilder.get324ReqModel(siloMovementReqModel.getKey(), siloMovementReqModel.getPersonType().name(),
						siloMovementReqModel.getFromSilo())).thenReturn(retrieveIDVDetailsReqModel);

		when(serviceOpsGcmController.retrieveIDVDetails(retrieveIDVDetailsReqModel, bindingResult)).thenReturn(modelAndView324);
		when(modelAndView324.getModelMap()).thenReturn(modlMap);
		when(modlMap.get(Attribute.SERVICE_OPS_MODEL)).thenReturn(customerDataList);
		when(customerRawData.getResponseObject()).thenReturn(response324);
		RetrieveIDVDetailsResponse siloMovementService324Response = siloMovementService.executeService324(siloMovementReqModel, bindingResult);
		assertThat(siloMovementService324Response, is(RetrieveIDVDetailsResponse.class));
	}

	@Test(expected = SiloMovementException.class)
	public void testExecute324Error() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		ModelAndView modelAndView324 = Mockito.mock(ModelAndView.class);
		when(
				requestBuilder.get324ReqModel(siloMovementReqModel.getKey(), siloMovementReqModel.getPersonType().name(),
						siloMovementReqModel.getFromSilo())).thenReturn(retrieveIDVDetailsReqModel);

		when(serviceOpsGcmController.retrieveIDVDetails(retrieveIDVDetailsReqModel, bindingResult)).thenReturn(modelAndView324);
		when(modelAndView324.getModelMap()).thenReturn(modlMap);
		when(modlMap.get(Attribute.SERVICE_OPS_MODEL)).thenReturn(null);
		when(maintainSiloMovementStatusService.retrieve(SiloMovementStatusAspect.getSiloMovementStatusModel().getAppId())).thenReturn(
				getSiloMovementStatusModel());
		siloMovementService.executeService324(siloMovementReqModel, bindingResult);
		// assertTrue(siloMovementService324Response == null);
	}

	@Test
	public void testExecute336Success() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		ModelAndView modelAndView336 = Mockito.mock(ModelAndView.class);
		CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneContactMethodsReqModel = Mockito.mock(CreateIndividualIPEmailPhoneContactMethodsReqModel.class);//new CreateIndividualIPEmailPhoneContactMethodsReqModel();
		//setEmailAndPhoneContactMethod(emailPhoneContactMethodsReqModel);
		when(requestBuilder.get336ReqModel(response258, siloMovementReqModel.getPersonType(), siloMovementReqModel.getToSilo())).thenReturn(
				createIndividualIPReqModel);
		when(requestBuilder.get336EmailPhoneReqModel(response258, siloMovementReqModel.getPersonType(), siloMovementReqModel.getToSilo())).thenReturn(
				emailPhoneContactMethodsReqModel);
		ModelAndView modelAndView = createIndividualIpController.createIndividualIP(createIndividualIPReqModel,emailPhoneContactMethodsReqModel, bindingResult);
		when(createIndividualIpController.createIndividualIP(createIndividualIPReqModel,emailPhoneContactMethodsReqModel, bindingResult))
				.thenReturn(modelAndView336);
		when(modelAndView336.getModelMap()).thenReturn(modlMap);
		when(modlMap.get(Attribute.SERVICE_OPS_MODEL)).thenReturn(customerDataList);
		when(customerRawData.getResponseObject()).thenReturn(response336);
		CreateIndividualIPResponse siloMovementService336Response = siloMovementService.executeService336(response258, siloMovementReqModel,
				bindingResult);
		assertThat(siloMovementService336Response, is(CreateIndividualIPResponse.class));
	}

	@Test(expected = SiloMovementException.class)
	public void testExecute336Error() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		ModelAndView modelAndView336 = Mockito.mock(ModelAndView.class);
		when(requestBuilder.get336ReqModel(response258, siloMovementReqModel.getPersonType(), siloMovementReqModel.getToSilo())).thenReturn(
				createIndividualIPReqModel);
		when(createIndividualIpController.createIndividualIP(createIndividualIPReqModel,null, bindingResult)).thenReturn(modelAndView336);
		when(modelAndView336.getModelMap()).thenReturn(modlMap);
		when(modlMap.get(Attribute.SERVICE_OPS_MODEL)).thenReturn(null);
		when(maintainSiloMovementStatusService.retrieve(SiloMovementStatusAspect.getSiloMovementStatusModel().getAppId())).thenReturn(
				getSiloMovementStatusModel());
		siloMovementService.executeService336(response258, siloMovementReqModel, bindingResult);
	}

	@Test(expected = SiloMovementException.class)
	public void testExecute336ErrorFor258() throws SiloMovementException, JsonProcessingException {
		when(maintainSiloMovementStatusService.retrieve(SiloMovementStatusAspect.getSiloMovementStatusModel().getAppId())).thenReturn(
				getSiloMovementStatusModel());
		siloMovementService.executeService336(null, siloMovementReqModel, bindingResult);
	}

	@Test
	public void testExecute325Success() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		ModelAndView modelAndView325 = Mockito.mock(ModelAndView.class);
		when(maintainIdvDetailReqModel.getCisKey()).thenReturn("12345678905");
		when(requestBuilder.get325ReqModel(response324, response336)).thenReturn(maintainIdvDetailReqModel);
		when(serviceOpsGcmSiloController.maintainIdvDetails(maintainIdvDetailReqModel, req)).thenReturn(modelAndView325);
		when(modelAndView325.getModelMap()).thenReturn(modlMap);
		when(modlMap.get(Attribute.SERVICE_OPS_MODEL)).thenReturn(customerDataList);
		when(customerRawData.getResponseObject()).thenReturn(response325);
		MaintainIDVDetailsResponse siloMovementService325Response = siloMovementService.executeService325(response324, response336,
				siloMovementReqModel, req);
		assertThat(siloMovementService325Response, is(MaintainIDVDetailsResponse.class));
	}

	@Test(expected = SiloMovementException.class)
	public void testExecute325NewCisKeyFailure() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		ModelAndView modelAndView325 = Mockito.mock(ModelAndView.class);
		when(requestBuilder.get325ReqModel(response324, response336)).thenReturn(maintainIdvDetailReqModel);
		when(serviceOpsGcmSiloController.maintainIdvDetails(maintainIdvDetailReqModel, req)).thenReturn(modelAndView325);
		when(modelAndView325.getModelMap()).thenReturn(modlMap);
		when(modlMap.get(Attribute.SERVICE_OPS_MODEL)).thenReturn(customerDataList);
		when(customerRawData.getResponseObject()).thenReturn(response325);
		MaintainIDVDetailsResponse siloMovementService325Response = siloMovementService.executeService325(response324, response336,
				siloMovementReqModel, req);
		assertThat(siloMovementService325Response, is(MaintainIDVDetailsResponse.class));
	}

	@Test(expected = SiloMovementException.class)
	public void testExecute325CustomerDataListNullCheck() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		ModelAndView modelAndView325 = Mockito.mock(ModelAndView.class);
		when(requestBuilder.get325ReqModel(response324, response336)).thenReturn(maintainIdvDetailReqModel);
		when(serviceOpsGcmSiloController.maintainIdvDetails(maintainIdvDetailReqModel, req)).thenReturn(modelAndView325);
		when(modelAndView325.getModelMap()).thenReturn(modlMap);
		when(modlMap.get(Attribute.SERVICE_OPS_MODEL)).thenReturn(null);
		when(customerRawData.getResponseObject()).thenReturn(response325);
		MaintainIDVDetailsResponse siloMovementService325Response = siloMovementService.executeService325(response324, response336,
				siloMovementReqModel, req);
		assertThat(siloMovementService325Response, is(MaintainIDVDetailsResponse.class));
	}

	@Test(expected = SiloMovementException.class)
	public void testExecute325Error() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		ModelAndView modelAndView325 = Mockito.mock(ModelAndView.class);
		when(requestBuilder.get325ReqModel(response324, response336)).thenReturn(maintainIdvDetailReqModel);

		when(serviceOpsGcmSiloController.maintainIdvDetails(maintainIdvDetailReqModel, req)).thenReturn(modelAndView325);
		when(modelAndView325.getModelMap()).thenReturn(modlMap);
		when(modlMap.get(Attribute.SERVICE_OPS_MODEL)).thenReturn(null);
		when(maintainSiloMovementStatusService.retrieve(SiloMovementStatusAspect.getSiloMovementStatusModel().getAppId())).thenReturn(
				getSiloMovementStatusModel());
		siloMovementService.executeService325(response324, response336, siloMovementReqModel, req);
	}

	@Test(expected = SiloMovementException.class)
	public void testExecute325ErrorFor324() throws SiloMovementException, JsonProcessingException {
		when(maintainSiloMovementStatusService.retrieve(SiloMovementStatusAspect.getSiloMovementStatusModel().getAppId())).thenReturn(
				getSiloMovementStatusModel());
		siloMovementService.executeService325(null, response336, siloMovementReqModel, req);
	}

	@Test
	public void testExecute256CreateSuccess() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List = new ArrayList<MaintainArrangementAndIPArrangementRelationshipsResponse>();
		MaintainArrangementAndIPArrangementRelationshipsResponse response256 = new MaintainArrangementAndIPArrangementRelationshipsResponse();

		ModelAndView modelAndView256 = Mockito.mock(ModelAndView.class);

		InvolvedParty individual = new Individual();
		InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
		involvedPartyIdentifier.setInvolvedPartyId(cisKey);
		individual.getInvolvedPartyIdentifier().add(involvedPartyIdentifier);

		CreateIndividualIPResponse response336 = new CreateIndividualIPResponse();
		response336.setIndividual(individual);

		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual individual258 = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual();
		InvolvedPartyArrangementRole arrangementRole = new InvolvedPartyArrangementRole();
		ProductArrangement productArrangement = new ProductArrangement();
		Product product = new Product();
		ProductSystemIdentifier productSystemIdentifier = new ProductSystemIdentifier();
		productSystemIdentifier.setProductSystemId(CORRECT_ACCOUNT_PREFIX);
		product.setProductSystemIdentifier(productSystemIdentifier);
		productArrangement.setIsBasedOn(product);
		arrangementRole.setHasForContext(productArrangement);
		individual258.getIsPlayingRoleInArrangement().add(arrangementRole);

		RetrieveDetailsAndArrangementRelationshipsForIPsResponse response258 = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
		response258.setIndividual(individual258);

		when(requestBuilder.get256ReqModel(siloMovementReqModel.getKey(), siloMovementReqModel.getPersonType().name(), arrangementRole, "create"))
				.thenReturn(maintainArrangementAndRelationshipReqModel);

		when(serviceOpsGcmController.maintainArrangementsAndRelashinship(maintainArrangementAndRelationshipReqModel, req, res)).thenReturn(
				modelAndView256);
		when(modelAndView256.getModelMap()).thenReturn(modlMap);
		when(modlMap.get(Attribute.SERVICE_OPS_MODEL)).thenReturn(customerDataList);
		when(customerRawData.getResponseObject()).thenReturn(response256);

		List<MaintainArrangementAndIPArrangementRelationshipsResponse> siloMovementService256Response = siloMovementService.executeService256Create(
				response256List, response258, response336, siloMovementReqModel, req, res);
		assertTrue(siloMovementService256Response.toArray().getClass().getComponentType()
				.isAssignableFrom(MaintainArrangementAndIPArrangementRelationshipsResponse.class));
	}

	@Test
	public void testExecute256DeleteSuccess() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List = new ArrayList<MaintainArrangementAndIPArrangementRelationshipsResponse>();
		MaintainArrangementAndIPArrangementRelationshipsResponse response256 = new MaintainArrangementAndIPArrangementRelationshipsResponse();

		ModelAndView modelAndView256 = Mockito.mock(ModelAndView.class);

		InvolvedParty individual = new Individual();
		InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
		involvedPartyIdentifier.setInvolvedPartyId(cisKey);
		individual.getInvolvedPartyIdentifier().add(involvedPartyIdentifier);

		CreateIndividualIPResponse response336 = new CreateIndividualIPResponse();
		response336.setIndividual(individual);

		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual individual258 = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual();
		InvolvedPartyArrangementRole arrangementRole = new InvolvedPartyArrangementRole();
		ProductArrangement productArrangement = new ProductArrangement();
		Product product = new Product();
		ProductSystemIdentifier productSystemIdentifier = new ProductSystemIdentifier();
		productSystemIdentifier.setProductSystemId(CORRECT_ACCOUNT_PREFIX);
		product.setProductSystemIdentifier(productSystemIdentifier);
		productArrangement.setIsBasedOn(product);
		arrangementRole.setHasForContext(productArrangement);
		individual258.getIsPlayingRoleInArrangement().add(arrangementRole);

		RetrieveDetailsAndArrangementRelationshipsForIPsResponse response258 = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
		response258.setIndividual(individual258);

		when(requestBuilder.get256ReqModel(siloMovementReqModel.getKey(), siloMovementReqModel.getPersonType().name(), arrangementRole, "delete"))
				.thenReturn(maintainArrangementAndRelationshipReqModel);

		when(serviceOpsGcmController.maintainArrangementsAndRelashinship(maintainArrangementAndRelationshipReqModel, req, res)).thenReturn(
				modelAndView256);
		when(modelAndView256.getModelMap()).thenReturn(modlMap);
		when(modlMap.get(Attribute.SERVICE_OPS_MODEL)).thenReturn(customerDataList);
		when(customerRawData.getResponseObject()).thenReturn(response256);

		List<MaintainArrangementAndIPArrangementRelationshipsResponse> siloMovementService256Response = siloMovementService.executeService256Delete(
				response256List, response258, response336, siloMovementReqModel, req, res);
		assertTrue(siloMovementService256Response.toArray().getClass().getComponentType()
				.isAssignableFrom(MaintainArrangementAndIPArrangementRelationshipsResponse.class));
	}

	@Test
	public void testExecute256EndErrorFor336Individual() throws SiloMovementException, JsonProcessingException {
		InvolvedParty involvedParty = Mockito.mock(InvolvedParty.class);
		List<InvolvedPartyIdentifier> involvedPartIdentifierList = new ArrayList<InvolvedPartyIdentifier>();
		when(response336.getIndividual()).thenReturn(involvedParty);
		when(involvedParty.getInvolvedPartyIdentifier()).thenReturn(involvedPartIdentifierList);
		List<MaintainArrangementAndIPArrangementRelationshipsResponse> siloMovementService256Response = siloMovementService.executeService256Delete(
				response256List, response258, response336, siloMovementReqModel, req, res);
		assertTrue(siloMovementService256Response.isEmpty());
	}

	@Test(expected = SiloMovementException.class)
	public void testExecute256ErrorForArrangementError() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List = new ArrayList<MaintainArrangementAndIPArrangementRelationshipsResponse>();
		MaintainArrangementAndIPArrangementRelationshipsResponse response256 = new MaintainArrangementAndIPArrangementRelationshipsResponse();

		InvolvedParty individual = new Individual();
		InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
		involvedPartyIdentifier.setInvolvedPartyId(cisKey);
		individual.getInvolvedPartyIdentifier().add(involvedPartyIdentifier);

		CreateIndividualIPResponse response336 = new CreateIndividualIPResponse();
		response336.setIndividual(individual);

		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual individual258 = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual();
		InvolvedPartyArrangementRole arrangementRole = new InvolvedPartyArrangementRole();
		ProductArrangement productArrangement = new ProductArrangement();
		Product product = new Product();
		ProductSystemIdentifier productSystemIdentifier = new ProductSystemIdentifier();
		productSystemIdentifier.setProductSystemId(ACCOUNT_PREFIX);
		product.setProductSystemIdentifier(productSystemIdentifier);
		productArrangement.setIsBasedOn(product);
		arrangementRole.setHasForContext(productArrangement);
		individual258.getIsPlayingRoleInArrangement().add(arrangementRole);

		RetrieveDetailsAndArrangementRelationshipsForIPsResponse response258 = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
		response258.setIndividual(individual258);

		siloMovementService.executeService256Create(response256List, response258, response336, siloMovementReqModel, req, res);
	}

	@Test(expected = SiloMovementException.class)
	public void testExecute256ErrorFor258Error() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List = new ArrayList<MaintainArrangementAndIPArrangementRelationshipsResponse>();

		InvolvedParty individual = new Individual();
		InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
		involvedPartyIdentifier.setInvolvedPartyId(cisKey);
		individual.getInvolvedPartyIdentifier().add(involvedPartyIdentifier);

		CreateIndividualIPResponse response336 = new CreateIndividualIPResponse();
		response336.setIndividual(individual);

		RetrieveDetailsAndArrangementRelationshipsForIPsResponse response258 = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
		response258.setIndividual(null);

		siloMovementService.executeService256Create(response256List, response258, response336, siloMovementReqModel, req, res);
	}

	@Test(expected = SiloMovementException.class)
	public void testExecute256ErrorForIsPlayingRoleInArrangementError() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List = new ArrayList<MaintainArrangementAndIPArrangementRelationshipsResponse>();

		InvolvedParty individual = new Individual();
		InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
		involvedPartyIdentifier.setInvolvedPartyId(cisKey);
		individual.getInvolvedPartyIdentifier().add(involvedPartyIdentifier);

		CreateIndividualIPResponse response336 = new CreateIndividualIPResponse();
		response336.setIndividual(individual);

		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual individual258 = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual();

		RetrieveDetailsAndArrangementRelationshipsForIPsResponse response258 = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
		response258.setIndividual(individual258);

		siloMovementService.executeService256Create(response256List, response258, response336, siloMovementReqModel, req, res);
	}

	@Test(expected = SiloMovementException.class)
	public void testExecute256ErrorFor336Error() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List = new ArrayList<MaintainArrangementAndIPArrangementRelationshipsResponse>();

		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual individual258 = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual();

		RetrieveDetailsAndArrangementRelationshipsForIPsResponse response258 = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
		response258.setIndividual(individual258);

		siloMovementService.executeService256Create(response256List, response258, null, siloMovementReqModel, req, res);
	}

	@Test(expected = SiloMovementException.class)
	public void testExecute256EndErrorForArrangementError() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List = new ArrayList<MaintainArrangementAndIPArrangementRelationshipsResponse>();
		MaintainArrangementAndIPArrangementRelationshipsResponse response256 = new MaintainArrangementAndIPArrangementRelationshipsResponse();

		InvolvedParty individual = new Individual();
		InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
		involvedPartyIdentifier.setInvolvedPartyId(cisKey);
		individual.getInvolvedPartyIdentifier().add(involvedPartyIdentifier);

		CreateIndividualIPResponse response336 = new CreateIndividualIPResponse();
		response336.setIndividual(individual);

		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual individual258 = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual();
		InvolvedPartyArrangementRole arrangementRole = new InvolvedPartyArrangementRole();
		ProductArrangement productArrangement = new ProductArrangement();
		Product product = new Product();
		ProductSystemIdentifier productSystemIdentifier = new ProductSystemIdentifier();
		productSystemIdentifier.setProductSystemId(ACCOUNT_PREFIX);
		product.setProductSystemIdentifier(productSystemIdentifier);
		productArrangement.setIsBasedOn(product);
		arrangementRole.setHasForContext(productArrangement);
		individual258.getIsPlayingRoleInArrangement().add(arrangementRole);

		RetrieveDetailsAndArrangementRelationshipsForIPsResponse response258 = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
		response258.setIndividual(individual258);

		siloMovementService.executeService256Delete(response256List, response258, response336, siloMovementReqModel, req, res);
	}

	@Test(expected = SiloMovementException.class)
	public void testExecute256EndErrorFor258IndividualError() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List = new ArrayList<MaintainArrangementAndIPArrangementRelationshipsResponse>();

		InvolvedParty individual = new Individual();
		InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
		involvedPartyIdentifier.setInvolvedPartyId(cisKey);
		individual.getInvolvedPartyIdentifier().add(involvedPartyIdentifier);

		CreateIndividualIPResponse response336 = new CreateIndividualIPResponse();
		response336.setIndividual(individual);

		RetrieveDetailsAndArrangementRelationshipsForIPsResponse response258 = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
		response258.setIndividual(null);

		siloMovementService.executeService256Delete(response256List, response258, response336, siloMovementReqModel, req, res);
	}

	@Test(expected = SiloMovementException.class)
	public void testExecute256EndErrorForIsPlayingRoleInArrangementError() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List = new ArrayList<MaintainArrangementAndIPArrangementRelationshipsResponse>();

		InvolvedParty individual = new Individual();
		InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
		involvedPartyIdentifier.setInvolvedPartyId(cisKey);
		individual.getInvolvedPartyIdentifier().add(involvedPartyIdentifier);

		CreateIndividualIPResponse response336 = new CreateIndividualIPResponse();
		response336.setIndividual(individual);

		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual individual258 = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual();

		RetrieveDetailsAndArrangementRelationshipsForIPsResponse response258 = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
		response258.setIndividual(individual258);

		siloMovementService.executeService256Delete(response256List, response258, response336, siloMovementReqModel, req, res);
	}

	@Test(expected = SiloMovementException.class)
	public void testExecute256EndErrorFor336Error() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List = new ArrayList<MaintainArrangementAndIPArrangementRelationshipsResponse>();

		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual individual258 = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual();

		RetrieveDetailsAndArrangementRelationshipsForIPsResponse response258 = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
		response258.setIndividual(individual258);

		siloMovementService.executeService256Delete(response256List, response258, null, siloMovementReqModel, req, res);
	}

	@Test(expected = SiloMovementException.class)
	public void testExecute256EndErrorFor258Error() throws SiloMovementException, JsonProcessingException {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getSiloMovementStatusModel());
		List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List = new ArrayList<MaintainArrangementAndIPArrangementRelationshipsResponse>();

		InvolvedParty individual = new Individual();
		InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
		involvedPartyIdentifier.setInvolvedPartyId(cisKey);
		individual.getInvolvedPartyIdentifier().add(involvedPartyIdentifier);

		CreateIndividualIPResponse response336 = new CreateIndividualIPResponse();
		response336.setIndividual(individual);

		siloMovementService.executeService256Delete(response256List, null, response336, siloMovementReqModel, req, res);
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

	private String[] getAllCustomerManagementOperation() {
		ClientDetailsOperation[] operationTypes = ClientDetailsOperation.values();
		String[] operationTypesArray = new String[operationTypes.length];
		for (int i = 0; i < operationTypes.length; i++) {
			operationTypesArray[i] = operationTypes[i].name();
		}
		return operationTypesArray;
	}

	private void setEmailAndPhoneContactMethod(CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneReqModel) {
		emailPhoneReqModel.setPhoneAddressContactAddressee("full name");
		emailPhoneReqModel.setPhoneAddressContactAreaCode("Area code");
		emailPhoneReqModel.setPhoneAddressContactContactInstructions("instructions");
		emailPhoneReqModel.setPhoneAddressContactContactMedium("medium");
		emailPhoneReqModel.setPhoneAddressContactContactMethodId("contactmethod id");
		emailPhoneReqModel.setPhoneAddressContactCountryCode("country code");
		emailPhoneReqModel.setPhoneAddressContactLocalNumber("local number");
		// createIndividualIPRequest.setPhoneAddressContactEndDate(phoneAddressContactMethod.getEndDate());
		emailPhoneReqModel.setPhoneAddressContactFullTelephoneNumber("telephone number");
		emailPhoneReqModel.setPhoneAddressContactIdentificationScheme(IdentificationScheme.CIS_KEY.name());
		emailPhoneReqModel.setPhoneAddressContactPreferredContactTime("preferred time");
		emailPhoneReqModel.setPhoneAddressContactPriorityLevel(PriorityLevel.PRIMARY.name());
		emailPhoneReqModel.setPhoneAddressContactSourceSystem("Source System");
		// createIndividualIPRequest.setPhoneAddressContactStartDate(phoneAddressContactMethod.getStartDate());
		emailPhoneReqModel.setHasPhoneContactMethodUsage("USAge");
		emailPhoneReqModel.setPhoneAddressContactValidityStatus("C");

		emailPhoneReqModel.setEmailAddressContactAddressee("Addressee");
		emailPhoneReqModel.setEmailAddressContactContactMethodId("Contact method id");
		emailPhoneReqModel.setEmailAddressContactEmailAddress("email@email.com");
		// createIndividualIPRequest.setEmailAddressContactEndDate(phoneAddressContactMethod.getEndDate());
		emailPhoneReqModel.setEmailAddressContactIdentificationScheme(IdentificationScheme.CIS_KEY.name());
		emailPhoneReqModel.setHasEmailContactMethodUsage("usage");
		emailPhoneReqModel.setEmailAddressContactPreferredContactTime("contact method");
		emailPhoneReqModel.setEmailAddressContactPriorityLevel(PriorityLevel.PRIMARY.name());
		emailPhoneReqModel.setEmailAddressContactSourceSystem("Source System");
		// createIndividualIPRequest.setEmailAddressContactStartDate(phoneAddressContactMethod.getStartDate());
		emailPhoneReqModel.setEmailAddressContactValidityStatus("C");

	}
}
