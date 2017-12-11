package com.bt.nextgen.serviceops.aspect;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ws.soap.SoapFaultException;

import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.MaintainIDVDetailsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.RetrieveIDVDetailsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.CreateIndividualIPResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.InvolvedParty;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainarrangementandiparrangementrelationships.v1.svc0256.MaintainArrangementAndIPArrangementRelationshipsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;

import com.bt.nextgen.api.client.service.MaintainSiloMovementStatusService;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.serviceops.model.SiloMovementReqModel;
import com.bt.nextgen.serviceops.model.SiloMovementStatusModel;
import com.bt.nextgen.serviceops.service.ServiceOpsService;
import com.bt.nextgen.serviceops.silomovement.aspect.SiloMovementStatusAspect;
import com.bt.nextgen.silomovement.exception.SiloMovementException;

/**
 * Created by L091297 on 08/06/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class SiloMovementStatusAspectTest {

	private static final Logger logger = LoggerFactory.getLogger(SiloMovementStatusAspectTest.class);

	private static final String SERVICE_258 = "executeService258";
	private static final String SERVICE_324 = "executeService324";
	private static final String SERVICE_336 = "executeService336";
	private static final String SERVICE_325 = "executeService325";
	private static final String SERVICE_256_CRT = "executeService256Create";
	private static final String SERVICE_256_END = "executeService256Delete";
	private static final String SILO_MOVEMENT_METHOD = "siloMovement";

	private static final String SERVICE_258_STATUS = "RTRDET";
	private static final String SERVICE_336_STATUS = "MNT336";
	private static final String SERVICE_325_STATUS = "MNT325";
	private static final String SERVICE_256_CRT_STATUS = "MNT256CRT";
	private static final String SERVICE_256_END_STATUS = "MNT256END";

	@Mock
	private static SiloMovementStatusModel siloMovementStatusModel;

	@InjectMocks
	public SiloMovementStatusAspect siloMovementStatusAspect;

	@Mock
	private ServiceOpsService serviceOpsService;

	JoinPoint jp = Mockito.mock(JoinPoint.class);

	ProceedingJoinPoint pjp = Mockito.mock(ProceedingJoinPoint.class);

	ModelAndView modelAndView = new ModelAndView();

	Signature signature = Mockito.mock(Signature.class);

	@Mock
	SecurityContext securityContext;

	@Mock
	Authentication authentication;

	private String cisKey = "12345678905";

	private String fromSilo = "WPAC";

	private String toSilo = "BTFG";

	private String userId = "CS12345";

	@Mock
	private MaintainSiloMovementStatusService maintainSiloMovementStatusService;

	private List<StatusInfo> statusInfoList = new ArrayList<StatusInfo>();

	@Mock
	StatusInfo statusInfo;

	@Mock
	ServiceStatus serviceStatus;

	@Before
	public void setup() throws Exception {
		statusInfoList.add(statusInfo);
	}

	@Test
	public void testBefore() throws ParseException {
		Object reqObj[] = { getSiloMovementReqModel() };
		when(jp.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn(SILO_MOVEMENT_METHOD);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(siloMovementStatusModel.getOldCis()).thenReturn(cisKey);
		when(authentication.getName()).thenReturn("CS12345");
		when(jp.getArgs()).thenReturn(reqObj);
		when(maintainSiloMovementStatusService.create(getStatusModel())).thenReturn(siloMovementStatusModel);
		siloMovementStatusAspect.before(jp);
	}

	@Test
	public void testAfterReturning258Success() throws ParseException {
		Object reqObj[] = { getSiloMovementReqModel() };
		RetrieveDetailsAndArrangementRelationshipsForIPsResponse result258 = Mockito
				.mock(RetrieveDetailsAndArrangementRelationshipsForIPsResponse.class);

		when(jp.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn(SERVICE_258);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(siloMovementStatusModel.getAppId()).thenReturn((long) 1);
		when(jp.getArgs()).thenReturn(reqObj);
		SiloMovementStatusAspect.setSiloMovementStatusModel(getStatusModel());
		when(maintainSiloMovementStatusService.retrieve(siloMovementStatusModel.getAppId())).thenReturn(siloMovementStatusModel);
		when(maintainSiloMovementStatusService.update(getStatusModel())).thenReturn(siloMovementStatusModel);

		when(result258.getServiceStatus()).thenReturn(serviceStatus);
		when(serviceStatus.getStatusInfo()).thenReturn(statusInfoList);
		when(statusInfo.getLevel()).thenReturn(Level.SUCCESS);

		siloMovementStatusAspect.afterReturning(jp, result258);
	}

	@Test
	public void testAfterReturning258Error() throws ParseException {
		Object reqObj[] = { getSiloMovementReqModel() };
		RetrieveDetailsAndArrangementRelationshipsForIPsResponse result258 = Mockito
				.mock(RetrieveDetailsAndArrangementRelationshipsForIPsResponse.class);

		when(jp.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn(SERVICE_258);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(siloMovementStatusModel.getAppId()).thenReturn((long) 1);
		when(jp.getArgs()).thenReturn(reqObj);
		SiloMovementStatusAspect.setSiloMovementStatusModel(getStatusModel());
		when(maintainSiloMovementStatusService.retrieve(siloMovementStatusModel.getAppId())).thenReturn(siloMovementStatusModel);
		when(maintainSiloMovementStatusService.update(getStatusModel())).thenReturn(siloMovementStatusModel);

		when(result258.getServiceStatus()).thenReturn(serviceStatus);
		when(serviceStatus.getStatusInfo()).thenReturn(statusInfoList);
		when(statusInfo.getLevel()).thenReturn(Level.ERROR);

		siloMovementStatusAspect.afterReturning(jp, result258);
	}

	@Test
	public void testAfterReturning324Success() throws ParseException {
		Object reqObj[] = { getSiloMovementReqModel() };
		RetrieveIDVDetailsResponse result324 = Mockito.mock(RetrieveIDVDetailsResponse.class);

		when(jp.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn(SERVICE_324);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(siloMovementStatusModel.getAppId()).thenReturn((long) 1);
		when(jp.getArgs()).thenReturn(reqObj);
		SiloMovementStatusAspect.setSiloMovementStatusModel(getStatusModel());
		when(maintainSiloMovementStatusService.retrieve(siloMovementStatusModel.getAppId())).thenReturn(siloMovementStatusModel);
		when(maintainSiloMovementStatusService.update(getStatusModel())).thenReturn(siloMovementStatusModel);

		when(result324.getServiceStatus()).thenReturn(serviceStatus);
		when(serviceStatus.getStatusInfo()).thenReturn(statusInfoList);
		when(statusInfo.getLevel()).thenReturn(Level.SUCCESS);

		siloMovementStatusAspect.afterReturning(jp, result324);
	}

	@Test
	public void testAfterReturning324Error() throws ParseException {
		Object reqObj[] = { getSiloMovementReqModel() };
		RetrieveIDVDetailsResponse result324 = Mockito.mock(RetrieveIDVDetailsResponse.class);

		when(jp.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn(SERVICE_324);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(siloMovementStatusModel.getAppId()).thenReturn((long) 1);
		when(jp.getArgs()).thenReturn(reqObj);
		SiloMovementStatusAspect.setSiloMovementStatusModel(getStatusModel());
		when(maintainSiloMovementStatusService.retrieve(siloMovementStatusModel.getAppId())).thenReturn(siloMovementStatusModel);
		when(maintainSiloMovementStatusService.update(getStatusModel())).thenReturn(siloMovementStatusModel);

		when(result324.getServiceStatus()).thenReturn(serviceStatus);
		when(serviceStatus.getStatusInfo()).thenReturn(statusInfoList);
		when(statusInfo.getLevel()).thenReturn(Level.ERROR);

		siloMovementStatusAspect.afterReturning(jp, result324);
	}

	@Test
	public void testAfterReturning336Success() throws ParseException {
		Object reqObj[] = { getSiloMovementReqModel() };
		CreateIndividualIPResponse result336 = Mockito.mock(CreateIndividualIPResponse.class);
		InvolvedParty invvolvedParty = Mockito.mock(InvolvedParty.class);
		when(jp.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn(SERVICE_336);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(siloMovementStatusModel.getAppId()).thenReturn((long) 1);
		when(jp.getArgs()).thenReturn(reqObj);
		SiloMovementStatusAspect.setSiloMovementStatusModel(getStatusModel());
		when(maintainSiloMovementStatusService.retrieve(siloMovementStatusModel.getAppId())).thenReturn(siloMovementStatusModel);
		when(maintainSiloMovementStatusService.update(getStatusModel())).thenReturn(siloMovementStatusModel);

		when(result336.getServiceStatus()).thenReturn(serviceStatus);
		when(serviceStatus.getStatusInfo()).thenReturn(statusInfoList);
		when(statusInfo.getLevel()).thenReturn(Level.SUCCESS);

		List<InvolvedPartyIdentifier> involvedPartyIdentifierList = new ArrayList<InvolvedPartyIdentifier>();
		InvolvedPartyIdentifier involvedPartyIdentifier = Mockito.mock(InvolvedPartyIdentifier.class);
		involvedPartyIdentifierList.add(involvedPartyIdentifier);

		when(result336.getIndividual()).thenReturn(invvolvedParty);
		when(invvolvedParty.getInvolvedPartyIdentifier()).thenReturn(involvedPartyIdentifierList);
		when(involvedPartyIdentifier.getInvolvedPartyId()).thenReturn(cisKey);

		siloMovementStatusAspect.afterReturning(jp, result336);
	}

	@Test
	public void testAfterReturning336Error() throws ParseException {
		Object reqObj[] = { getSiloMovementReqModel() };
		CreateIndividualIPResponse result336 = Mockito.mock(CreateIndividualIPResponse.class);
		InvolvedParty invvolvedParty = Mockito.mock(InvolvedParty.class);
		when(jp.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn(SERVICE_336);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(siloMovementStatusModel.getAppId()).thenReturn((long) 1);
		when(jp.getArgs()).thenReturn(reqObj);
		SiloMovementStatusAspect.setSiloMovementStatusModel(getStatusModel());
		when(maintainSiloMovementStatusService.retrieve(siloMovementStatusModel.getAppId())).thenReturn(siloMovementStatusModel);
		when(maintainSiloMovementStatusService.update(getStatusModel())).thenReturn(siloMovementStatusModel);

		when(result336.getServiceStatus()).thenReturn(serviceStatus);
		when(serviceStatus.getStatusInfo()).thenReturn(statusInfoList);
		when(statusInfo.getLevel()).thenReturn(Level.ERROR);

		List<InvolvedPartyIdentifier> involvedPartyIdentifierList = new ArrayList<InvolvedPartyIdentifier>();
		InvolvedPartyIdentifier involvedPartyIdentifier = Mockito.mock(InvolvedPartyIdentifier.class);
		involvedPartyIdentifierList.add(involvedPartyIdentifier);

		when(result336.getIndividual()).thenReturn(invvolvedParty);
		when(invvolvedParty.getInvolvedPartyIdentifier()).thenReturn(involvedPartyIdentifierList);
		when(involvedPartyIdentifier.getInvolvedPartyId()).thenReturn(cisKey);

		siloMovementStatusAspect.afterReturning(jp, result336);
	}

	@Test
	public void testAfterReturning325Success() throws ParseException {
		Object reqObj[] = { getSiloMovementReqModel() };
		MaintainIDVDetailsResponse result325 = Mockito.mock(MaintainIDVDetailsResponse.class);

		when(jp.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn(SERVICE_325);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(siloMovementStatusModel.getAppId()).thenReturn((long) 1);
		when(jp.getArgs()).thenReturn(reqObj);
		SiloMovementStatusAspect.setSiloMovementStatusModel(getStatusModel());
		when(maintainSiloMovementStatusService.retrieve(siloMovementStatusModel.getAppId())).thenReturn(siloMovementStatusModel);
		when(maintainSiloMovementStatusService.update(getStatusModel())).thenReturn(siloMovementStatusModel);
		List<ServiceStatus> serviceStatusList = new ArrayList<ServiceStatus>();
		serviceStatusList.add(serviceStatus);
		when(result325.getServiceStatus()).thenReturn(serviceStatusList);
		when(serviceStatus.getStatusInfo()).thenReturn(statusInfoList);
		when(statusInfo.getLevel()).thenReturn(Level.SUCCESS);

		siloMovementStatusAspect.afterReturning(jp, result325);
	}

	@Test
	public void testAfterReturning325Error() throws ParseException {
		Object reqObj[] = { getSiloMovementReqModel() };
		MaintainIDVDetailsResponse result325 = Mockito.mock(MaintainIDVDetailsResponse.class);

		when(jp.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn(SERVICE_325);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(siloMovementStatusModel.getAppId()).thenReturn((long) 1);
		when(jp.getArgs()).thenReturn(reqObj);
		SiloMovementStatusAspect.setSiloMovementStatusModel(getStatusModel());
		when(maintainSiloMovementStatusService.retrieve(siloMovementStatusModel.getAppId())).thenReturn(siloMovementStatusModel);
		when(maintainSiloMovementStatusService.update(getStatusModel())).thenReturn(siloMovementStatusModel);
		List<ServiceStatus> serviceStatusList = new ArrayList<ServiceStatus>();
		serviceStatusList.add(serviceStatus);
		when(result325.getServiceStatus()).thenReturn(serviceStatusList);
		when(serviceStatus.getStatusInfo()).thenReturn(statusInfoList);
		when(statusInfo.getLevel()).thenReturn(Level.ERROR);

		siloMovementStatusAspect.afterReturning(jp, result325);
	}

	@Test
	public void testAfterReturning256CreateSuccess() throws ParseException {
		Object reqObj[] = { getSiloMovementReqModel() };
		List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List = new ArrayList<MaintainArrangementAndIPArrangementRelationshipsResponse>();
		MaintainArrangementAndIPArrangementRelationshipsResponse response256 = Mockito
				.mock(MaintainArrangementAndIPArrangementRelationshipsResponse.class);
		response256List.add(response256);

		when(jp.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn(SERVICE_256_CRT);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(siloMovementStatusModel.getAppId()).thenReturn((long) 1);
		when(jp.getArgs()).thenReturn(reqObj);
		SiloMovementStatusAspect.setSiloMovementStatusModel(getStatusModel());
		when(maintainSiloMovementStatusService.retrieve(siloMovementStatusModel.getAppId())).thenReturn(siloMovementStatusModel);
		when(maintainSiloMovementStatusService.update(getStatusModel())).thenReturn(siloMovementStatusModel);

		when(response256.getServiceStatus()).thenReturn(serviceStatus);
		when(serviceStatus.getStatusInfo()).thenReturn(statusInfoList);
		when(statusInfo.getLevel()).thenReturn(Level.SUCCESS);

		siloMovementStatusAspect.afterReturning(jp, response256List);
	}

	@Test
	public void testAfterReturning256CreateError() throws ParseException {
		Object reqObj[] = { getSiloMovementReqModel() };
		List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List = new ArrayList<MaintainArrangementAndIPArrangementRelationshipsResponse>();
		MaintainArrangementAndIPArrangementRelationshipsResponse response256 = Mockito
				.mock(MaintainArrangementAndIPArrangementRelationshipsResponse.class);
		response256List.add(response256);

		when(jp.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn(SERVICE_256_CRT);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(siloMovementStatusModel.getAppId()).thenReturn((long) 1);
		when(jp.getArgs()).thenReturn(reqObj);
		SiloMovementStatusAspect.setSiloMovementStatusModel(getStatusModel());
		when(maintainSiloMovementStatusService.retrieve(siloMovementStatusModel.getAppId())).thenReturn(siloMovementStatusModel);
		when(maintainSiloMovementStatusService.update(getStatusModel())).thenReturn(siloMovementStatusModel);

		when(response256.getServiceStatus()).thenReturn(serviceStatus);
		when(serviceStatus.getStatusInfo()).thenReturn(statusInfoList);
		when(statusInfo.getLevel()).thenReturn(Level.ERROR);

		siloMovementStatusAspect.afterReturning(jp, response256List);
	}

	@Test
	public void testAfterReturning256EndSuccess() throws ParseException {
		Object reqObj[] = { getSiloMovementReqModel() };
		List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List = new ArrayList<MaintainArrangementAndIPArrangementRelationshipsResponse>();
		MaintainArrangementAndIPArrangementRelationshipsResponse response256 = Mockito
				.mock(MaintainArrangementAndIPArrangementRelationshipsResponse.class);
		response256List.add(response256);

		when(jp.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn(SERVICE_256_END);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(siloMovementStatusModel.getAppId()).thenReturn((long) 1);
		when(jp.getArgs()).thenReturn(reqObj);
		SiloMovementStatusAspect.setSiloMovementStatusModel(getStatusModel());
		when(maintainSiloMovementStatusService.retrieve(siloMovementStatusModel.getAppId())).thenReturn(siloMovementStatusModel);
		when(maintainSiloMovementStatusService.update(getStatusModel())).thenReturn(siloMovementStatusModel);

		when(response256.getServiceStatus()).thenReturn(serviceStatus);
		when(serviceStatus.getStatusInfo()).thenReturn(statusInfoList);
		when(statusInfo.getLevel()).thenReturn(Level.SUCCESS);

		siloMovementStatusAspect.afterReturning(jp, response256List);
	}

	@Test
	public void testAfterReturning256EndError() throws ParseException {
		Object reqObj[] = { getSiloMovementReqModel() };
		List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List = new ArrayList<MaintainArrangementAndIPArrangementRelationshipsResponse>();
		MaintainArrangementAndIPArrangementRelationshipsResponse response256 = Mockito
				.mock(MaintainArrangementAndIPArrangementRelationshipsResponse.class);
		response256List.add(response256);

		when(jp.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn(SERVICE_256_END);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(siloMovementStatusModel.getAppId()).thenReturn((long) 1);
		when(jp.getArgs()).thenReturn(reqObj);
		SiloMovementStatusAspect.setSiloMovementStatusModel(getStatusModel());
		when(maintainSiloMovementStatusService.retrieve(siloMovementStatusModel.getAppId())).thenReturn(siloMovementStatusModel);
		when(maintainSiloMovementStatusService.update(getStatusModel())).thenReturn(siloMovementStatusModel);

		when(response256.getServiceStatus()).thenReturn(serviceStatus);
		when(serviceStatus.getStatusInfo()).thenReturn(statusInfoList);
		when(statusInfo.getLevel()).thenReturn(Level.ERROR);

		siloMovementStatusAspect.afterReturning(jp, response256List);
	}

	@Test
	public void testSwallowExceptionSuccess() throws Throwable {
		when(pjp.proceed()).thenReturn(modelAndView);
		ModelAndView modelAndView = siloMovementStatusAspect.swallowException(pjp);
		assertNotNull(modelAndView);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSwallowExceptionSiloMovementException() throws Throwable {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getStatusModel());
		when(serviceOpsService.isServiceOpsRestricted()).thenReturn(true);
		when(pjp.proceed()).thenThrow(SiloMovementException.class);
		ModelAndView modelAndView = siloMovementStatusAspect.swallowException(pjp);
		assertNotNull(modelAndView);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSwallowExceptionThrowable() throws Throwable {
		SiloMovementStatusAspect.setSiloMovementStatusModel(getStatusModel());
		when(serviceOpsService.isServiceOpsRestricted()).thenReturn(true);
		when(pjp.proceed()).thenThrow(Throwable.class);
		ModelAndView modelAndView = siloMovementStatusAspect.swallowException(pjp);
		assertNotNull(modelAndView);
	}

	@Test
	public void testAfterThrowingForSiloMovementException() throws Throwable {
		Exception error = new SiloMovementException(SERVICE_258_STATUS, "Error in service 258 or 324");
		Object reqObj[] = { getSiloMovementReqModel() };
		when(jp.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn(SERVICE_258);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(siloMovementStatusModel.getAppId()).thenReturn((long) 1);
		when(jp.getArgs()).thenReturn(reqObj);

		SiloMovementStatusAspect.setSiloMovementStatusModel(getStatusModel());
		when(maintainSiloMovementStatusService.retrieve(siloMovementStatusModel.getAppId())).thenReturn(siloMovementStatusModel);
		when(maintainSiloMovementStatusService.update(getStatusModel())).thenReturn(siloMovementStatusModel);

		siloMovementStatusAspect.afterThrowingService(jp, error);
	}
	
	@Test
	public void testAfterThrowingForSoapFaultExceptionForService258() throws Throwable {
		Exception error = new SoapFaultException(SERVICE_258_STATUS);
		Object reqObj[] = { getSiloMovementReqModel() };
		when(jp.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn(SERVICE_258);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(siloMovementStatusModel.getAppId()).thenReturn((long) 1);
		when(jp.getArgs()).thenReturn(reqObj);

		SiloMovementStatusAspect.setSiloMovementStatusModel(getStatusModel());
		when(maintainSiloMovementStatusService.retrieve(siloMovementStatusModel.getAppId())).thenReturn(siloMovementStatusModel);
		when(maintainSiloMovementStatusService.update(getStatusModel())).thenReturn(siloMovementStatusModel);

		siloMovementStatusAspect.afterThrowingService(jp, error);
	}
	
	@Test
	public void testAfterThrowingForSoapFaultExceptionForService324() throws Throwable {
		Exception error = new SoapFaultException(SERVICE_258_STATUS);
		Object reqObj[] = { getSiloMovementReqModel() };
		when(jp.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn(SERVICE_324);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(siloMovementStatusModel.getAppId()).thenReturn((long) 1);
		when(jp.getArgs()).thenReturn(reqObj);

		SiloMovementStatusAspect.setSiloMovementStatusModel(getStatusModel());
		when(maintainSiloMovementStatusService.retrieve(siloMovementStatusModel.getAppId())).thenReturn(siloMovementStatusModel);
		when(maintainSiloMovementStatusService.update(getStatusModel())).thenReturn(siloMovementStatusModel);

		siloMovementStatusAspect.afterThrowingService(jp, error);
	}
	
	@Test
	public void testAfterThrowingForSoapFaultExceptionForService336() throws Throwable {
		Exception error = new SoapFaultException(SERVICE_336_STATUS);
		Object reqObj[] = { getSiloMovementReqModel() };
		when(jp.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn(SERVICE_336);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(siloMovementStatusModel.getAppId()).thenReturn((long) 1);
		when(jp.getArgs()).thenReturn(reqObj);

		SiloMovementStatusAspect.setSiloMovementStatusModel(getStatusModel());
		when(maintainSiloMovementStatusService.retrieve(siloMovementStatusModel.getAppId())).thenReturn(siloMovementStatusModel);
		when(maintainSiloMovementStatusService.update(getStatusModel())).thenReturn(siloMovementStatusModel);

		siloMovementStatusAspect.afterThrowingService(jp, error);
	}
	
	@Test
	public void testAfterThrowingForSoapFaultExceptionForService325() throws Throwable {
		Exception error = new SoapFaultException(SERVICE_325_STATUS);
		Object reqObj[] = { getSiloMovementReqModel() };
		when(jp.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn(SERVICE_325);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(siloMovementStatusModel.getAppId()).thenReturn((long) 1);
		when(jp.getArgs()).thenReturn(reqObj);

		SiloMovementStatusAspect.setSiloMovementStatusModel(getStatusModel());
		when(maintainSiloMovementStatusService.retrieve(siloMovementStatusModel.getAppId())).thenReturn(siloMovementStatusModel);
		when(maintainSiloMovementStatusService.update(getStatusModel())).thenReturn(siloMovementStatusModel);

		siloMovementStatusAspect.afterThrowingService(jp, error);
	}
	
	@Test
	public void testAfterThrowingForSoapFaultExceptionForService256Create() throws Throwable {
		Exception error = new SoapFaultException(SERVICE_256_CRT_STATUS);
		Object reqObj[] = { getSiloMovementReqModel() };
		when(jp.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn(SERVICE_256_CRT);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(siloMovementStatusModel.getAppId()).thenReturn((long) 1);
		when(jp.getArgs()).thenReturn(reqObj);

		SiloMovementStatusAspect.setSiloMovementStatusModel(getStatusModel());
		when(maintainSiloMovementStatusService.retrieve(siloMovementStatusModel.getAppId())).thenReturn(siloMovementStatusModel);
		when(maintainSiloMovementStatusService.update(getStatusModel())).thenReturn(siloMovementStatusModel);

		siloMovementStatusAspect.afterThrowingService(jp, error);
	}
	
	@Test
	public void testAfterThrowingForSoapFaultExceptionForService256End() throws Throwable {
		Exception error = new SoapFaultException(SERVICE_256_END_STATUS);
		Object reqObj[] = { getSiloMovementReqModel() };
		when(jp.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn(SERVICE_256_END);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(siloMovementStatusModel.getAppId()).thenReturn((long) 1);
		when(jp.getArgs()).thenReturn(reqObj);

		SiloMovementStatusAspect.setSiloMovementStatusModel(getStatusModel());
		when(maintainSiloMovementStatusService.retrieve(siloMovementStatusModel.getAppId())).thenReturn(siloMovementStatusModel);
		when(maintainSiloMovementStatusService.update(getStatusModel())).thenReturn(siloMovementStatusModel);

		siloMovementStatusAspect.afterThrowingService(jp, error);
	}

	private SiloMovementStatusModel getStatusModel() {
		SiloMovementStatusModel statusModel = new SiloMovementStatusModel();
		statusModel.setDatetimeStart(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
		statusModel.setAppId((long) 1);
		statusModel.setOldCis(cisKey);
		statusModel.setFromSilo(fromSilo);
		statusModel.setToSilo(toSilo);
		statusModel.setUserId(userId);
		return statusModel;
	}

	private SiloMovementReqModel getSiloMovementReqModel() {
		SiloMovementReqModel siloMovementReqModel = new SiloMovementReqModel();
		siloMovementReqModel.setFromSilo(fromSilo);
		siloMovementReqModel.setKey(cisKey);
		siloMovementReqModel.setToSilo("BTFG");
		siloMovementReqModel.setPersonType(RoleType.INDIVIDUAL);
		return siloMovementReqModel;
	}
}
