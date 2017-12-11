package com.bt.nextgen.serviceops.silomovement.aspect;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.MaintainIDVDetailsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.RetrieveIDVDetailsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.CreateIndividualIPResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainarrangementandiparrangementrelationships.v1.svc0256.MaintainArrangementAndIPArrangementRelationshipsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;

import com.bt.nextgen.api.client.service.MaintainSiloMovementStatusService;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.serviceops.model.SiloMovementReqModel;
import com.bt.nextgen.serviceops.model.SiloMovementStatusModel;
import com.bt.nextgen.serviceops.service.ServiceOpsService;
import com.bt.nextgen.silomovement.exception.SiloMovementException;

/**
 * Created by L091297 on 08/06/2017.
 */

@Aspect
public class SiloMovementStatusAspect {

	private static final Logger logger = LoggerFactory.getLogger(SiloMovementStatusAspect.class);

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
	private static final String SERVICE_256_STATUS_CRT = "MNT256_CRT";
	private static final String SERVICE_256_STATUS_END = "MNT256_END";

	private static SiloMovementStatusModel siloMovementStatusModel;

	//private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	@Autowired
	@Qualifier("maintainSiloMovementStatusService")
	private MaintainSiloMovementStatusService maintainSiloMovementStatusService;

	@Autowired
	private ServiceOpsService serviceOpsService;

	@Before("execution(* com.bt.nextgen.api.client.service.SiloMovementServiceImpl.*(..))")
	public void before(JoinPoint joinPoint) throws ParseException {
		logger.info("Before aspect: " + joinPoint.getSignature().getName());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		String method = joinPoint.getSignature().getName();
		if (SILO_MOVEMENT_METHOD.equalsIgnoreCase(method)) {
			Object[] reqObjs = joinPoint.getArgs();
			for (Object obj : reqObjs) {
				if (null != obj && obj instanceof SiloMovementReqModel) {
					SiloMovementReqModel reqModel = (SiloMovementReqModel) obj;
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
					SiloMovementStatusModel statusModel = new SiloMovementStatusModel();
					statusModel.setDatetimeStart(simpleDateFormat.format(new Date()));
					statusModel.setOldCis(reqModel.getKey());
					statusModel.setFromSilo(reqModel.getFromSilo());
					statusModel.setToSilo(reqModel.getToSilo());
					statusModel.setUserId(username);
					logger.info("SiloMovementStatusModel: " + statusModel.toString());
					siloMovementStatusModel = maintainSiloMovementStatusService.create(statusModel);
				}
			}
		}
	}

	@AfterReturning(pointcut = "execution(* com.bt.nextgen.api.client.service.SiloMovementServiceImpl.execute*(..))", returning = "result")
	public void afterReturning(JoinPoint joinPoint, Object result) throws ParseException {
		logger.info("After returning aspect: " + JoinPoint.METHOD_CALL);
		String method = joinPoint.getSignature().getName();
		SiloMovementStatusModel statusModel = getSiloMovementStatusObjetFromDB();
		if (null != statusModel) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			statusModel.setDatetimeEnd(simpleDateFormat.format(new Date()));
			updateStatus(statusModel, result, method);
			maintainSiloMovementStatusService.update(statusModel);
		}
	}

	private SiloMovementStatusModel getSiloMovementStatusObjetFromDB() {
		SiloMovementStatusModel statusModel = maintainSiloMovementStatusService.retrieve(siloMovementStatusModel.getAppId());
		return statusModel;
	}

	@SuppressWarnings("unchecked")
	private void updateStatus(SiloMovementStatusModel statusModel, Object result, String serviceMethod) {
		List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List = null;
		if (null != result) {
			switch (serviceMethod) {
			case SERVICE_258:
				RetrieveDetailsAndArrangementRelationshipsForIPsResponse response258 = (RetrieveDetailsAndArrangementRelationshipsForIPsResponse) result;
				updateStatusFor258(statusModel, response258);
				break;
			case SERVICE_324:
				RetrieveIDVDetailsResponse response324 = (RetrieveIDVDetailsResponse) result;
				updateStatusFor324(statusModel, response324);
				break;
			case SERVICE_336:
				CreateIndividualIPResponse response336 = (CreateIndividualIPResponse) result;
				updateStatusFor336(statusModel, response336);
				break;
			case SERVICE_325:
				MaintainIDVDetailsResponse response325 = (MaintainIDVDetailsResponse) result;
				updateStatusFor325(statusModel, response325);
				break;
			case SERVICE_256_CRT:
				response256List = (List<MaintainArrangementAndIPArrangementRelationshipsResponse>) result;
				updateStatusFor256Create(statusModel, response256List);
				break;
			case SERVICE_256_END:
				response256List = (List<MaintainArrangementAndIPArrangementRelationshipsResponse>) result;
				updateStatusFor256Delete(statusModel, response256List);
				break;
			default:
				break;
			}
		}

	}

	private void updateStatusFor258(SiloMovementStatusModel statusModel, RetrieveDetailsAndArrangementRelationshipsForIPsResponse response258) {
		if (response258.getServiceStatus().getStatusInfo().get(0).getLevel().equals(Level.SUCCESS)) {
			statusModel.setLastSuccState(SERVICE_258_STATUS);
		} else if (response258.getServiceStatus().getStatusInfo().get(0).getLevel().equals(Level.ERROR)) {
			statusModel.setErrState(SERVICE_258_STATUS);
			statusModel.setErrMsg(response258.getServiceStatus().getStatusInfo().get(0).getDescription());
		}
	}

	private void updateStatusFor324(SiloMovementStatusModel statusModel, RetrieveIDVDetailsResponse response324) {
		if (response324.getServiceStatus().getStatusInfo().get(0).getLevel().equals(Level.SUCCESS)) {
			statusModel.setLastSuccState(SERVICE_258_STATUS);
		} else if (response324.getServiceStatus().getStatusInfo().get(0).getLevel().equals(Level.ERROR)) {
			statusModel.setErrState(SERVICE_258_STATUS);
			statusModel.setErrMsg(response324.getServiceStatus().getStatusInfo().get(0).getDescription());
		}
	}

	private void updateStatusFor336(SiloMovementStatusModel statusModel, CreateIndividualIPResponse response336) {
		if (response336.getServiceStatus().getStatusInfo().get(0).getLevel().equals(Level.SUCCESS)) {
			statusModel.setLastSuccState(SERVICE_336_STATUS);
			statusModel.setNewCis(response336.getIndividual().getInvolvedPartyIdentifier().get(0).getInvolvedPartyId());
		} else if (response336.getServiceStatus().getStatusInfo().get(0).getLevel().equals(Level.ERROR)) {
			statusModel.setErrState(SERVICE_336_STATUS);
			statusModel.setErrMsg(response336.getServiceStatus().getStatusInfo().get(0).getDescription());
		}
	}

	private void updateStatusFor325(SiloMovementStatusModel statusModel, MaintainIDVDetailsResponse response325) {
		if (response325.getServiceStatus().get(0).getStatusInfo().get(0).getLevel().equals(Level.SUCCESS)) {
			statusModel.setLastSuccState(SERVICE_325_STATUS);
		} else if (response325.getServiceStatus().get(0).getStatusInfo().get(0).getLevel().equals(Level.ERROR)) {
			statusModel.setErrState(SERVICE_325_STATUS);
			statusModel.setErrMsg(response325.getServiceStatus().get(0).getStatusInfo().get(0).getDescription());
		}
	}

	@SuppressWarnings("unchecked")
	private void updateStatusFor256Create(SiloMovementStatusModel statusModel, Object result) {
		if (null != result) {
			List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List = (List<MaintainArrangementAndIPArrangementRelationshipsResponse>) result;
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			if (!response256List.isEmpty() && response256List.get(0).getServiceStatus().getStatusInfo().get(0).getLevel().equals(Level.SUCCESS)) {
				statusModel.setLastSuccState(SERVICE_256_STATUS_CRT);
				statusModel.setDatetimeEnd(simpleDateFormat.format(new Date()));
			} else if (!response256List.isEmpty() && response256List.get(0).getServiceStatus().getStatusInfo().get(0).getLevel().equals(Level.ERROR)) {
				statusModel.setErrState(SERVICE_256_STATUS_CRT);
				statusModel.setErrMsg(response256List.get(0).getServiceStatus().getStatusInfo().get(0).getDescription());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void updateStatusFor256Delete(SiloMovementStatusModel statusModel, Object result) {
		if (null != result) {
			List<MaintainArrangementAndIPArrangementRelationshipsResponse> response256List = (List<MaintainArrangementAndIPArrangementRelationshipsResponse>) result;
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			if (!response256List.isEmpty() && response256List.get(0).getServiceStatus().getStatusInfo().get(0).getLevel().equals(Level.SUCCESS)) {
				statusModel.setLastSuccState(SERVICE_256_STATUS_END);
				statusModel.setDatetimeEnd(simpleDateFormat.format(new Date()));
			} else if (!response256List.isEmpty() && response256List.get(0).getServiceStatus().getStatusInfo().get(0).getLevel().equals(Level.ERROR)) {
				statusModel.setErrState(SERVICE_256_STATUS_END);
				statusModel.setErrMsg(response256List.get(0).getServiceStatus().getStatusInfo().get(0).getDescription());
			}
		}
	}

	@Around("execution(* com.bt.nextgen.serviceops.controller.SiloMovementController.siloMovement(..))")
	public ModelAndView swallowException(ProceedingJoinPoint joinPoint) {
		try {
			return (ModelAndView) joinPoint.proceed();
		} catch (Exception e) {
			logger.error("Exception at Around Clause : " + e);
			ModelAndView modelAndView = new ModelAndView(View.SILO_MOVEMENT).addObject("isRestricted", serviceOpsService.isServiceOpsRestricted())
					.addObject("key", SiloMovementStatusAspect.getSiloMovementStatusModel().getAppId());
			return modelAndView;
		} catch (Throwable e) {
			logger.error("Exception at Around Clause : " + e);
			ModelAndView modelAndView = new ModelAndView(View.SILO_MOVEMENT).addObject("isRestricted", serviceOpsService.isServiceOpsRestricted())
					.addObject("key", SiloMovementStatusAspect.getSiloMovementStatusModel().getAppId());
			return modelAndView;
		}
	}

	@AfterThrowing(pointcut = "execution(* com.bt.nextgen.api.client.service.SiloMovementServiceImpl.execute*(..))", throwing = "error")
	public void afterThrowingService(JoinPoint joinPoint, Throwable error) {
		logger.info("After throwing aspect: " + joinPoint.getSignature().getName());
		logger.error(ExceptionUtils.getStackTrace(error));
		String method = joinPoint.getSignature().getName();
		SiloMovementStatusModel statusModel = getSiloMovementStatusObjetFromDB();
		if (null != statusModel) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			statusModel.setDatetimeEnd(simpleDateFormat.format(new Date()));
			if (error instanceof SiloMovementException) {
				SiloMovementException siloMovementException = (SiloMovementException) error;
				statusModel.setErrMsg(siloMovementException.getMessage());
				statusModel.setErrState(siloMovementException.getErrorState());
			} else {
				statusModel.setErrMsg(ExceptionUtils.getStackTrace(error));
				updateStatusForErrorState(statusModel, method);
			}
			maintainSiloMovementStatusService.update(statusModel);
		}
	}

	@SuppressWarnings("unchecked")
	private void updateStatusForErrorState(SiloMovementStatusModel statusModel, String serviceMethod) {
		switch (serviceMethod) {
		case SERVICE_258:
		case SERVICE_324:
		case SILO_MOVEMENT_METHOD:
			statusModel.setErrState(SERVICE_258_STATUS);
			break;
		case SERVICE_336:
			statusModel.setErrState(SERVICE_336_STATUS);
			break;
		case SERVICE_325:
			statusModel.setErrState(SERVICE_325_STATUS);
			break;
		case SERVICE_256_CRT:
			statusModel.setErrState(SERVICE_256_STATUS_CRT);
			break;
		case SERVICE_256_END:
			statusModel.setErrState(SERVICE_256_STATUS_END);
			break;
		default:
			break;
		}

	}

	public static SiloMovementStatusModel getSiloMovementStatusModel() {
		return siloMovementStatusModel;
	}

	public static void setSiloMovementStatusModel(SiloMovementStatusModel siloMovementStatusModel1) {
		siloMovementStatusModel = siloMovementStatusModel1;
	}
}
