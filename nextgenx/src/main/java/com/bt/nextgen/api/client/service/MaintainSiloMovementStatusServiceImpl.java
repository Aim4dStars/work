package com.bt.nextgen.api.client.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.serviceops.model.SiloMovementStatusModel;
import com.bt.nextgen.serviceops.repository.SiloMovementStatus;
import com.bt.nextgen.serviceops.repository.SiloMovementStatusRepository;

@Service("maintainSiloMovementStatusService")
public class MaintainSiloMovementStatusServiceImpl implements MaintainSiloMovementStatusService {

	private static final Logger logger = LoggerFactory.getLogger(MaintainSiloMovementStatusServiceImpl.class);
	
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	private static final String CREATE_METHOD = "create";
	private static final String UPDATE_METHOD = "update";
	
	private static final String DATETIME_APPENDER = " 00:00:00.000";


	@Autowired
	@Qualifier("siloMovementStatusRepositoryImpl")
	private SiloMovementStatusRepository siloMovementStatusRepository;

	@Override
	public SiloMovementStatusModel create(SiloMovementStatusModel reqModel) {
		SiloMovementStatus siloMovementStatus = createSiloMovementStatusRequest(reqModel, CREATE_METHOD);
		siloMovementStatus = siloMovementStatusRepository.create(siloMovementStatus);
		return toSiloMovementStatusModel(siloMovementStatus);
	}

	@Override
	public SiloMovementStatusModel update(SiloMovementStatusModel reqModel) {
		SiloMovementStatus siloMovementStatus = createSiloMovementStatusRequest(reqModel, UPDATE_METHOD);
		siloMovementStatus = siloMovementStatusRepository.update(siloMovementStatus);
		return toSiloMovementStatusModel(siloMovementStatus);
	}

	@Override
	public SiloMovementStatusModel retrieve(Long id) {
		SiloMovementStatus siloMovementStatus = siloMovementStatusRepository.retrieve(id);
		return toSiloMovementStatusModel(siloMovementStatus);
	}

	@Override
	public List<SiloMovementStatusModel> retrieveAll(SiloMovementStatusModel siloMovementStatusModel) {
		List<SiloMovementStatus> siloMovementStatusList = siloMovementStatusRepository.retrieveAll(toSiloMovementStatus(siloMovementStatusModel));
		return toSiloMovementStatusModel(siloMovementStatusList);
	}

	private SiloMovementStatus toSiloMovementStatus(SiloMovementStatusModel siloMovementStatusModel) {
		SiloMovementStatus siloMovementStatus = new SiloMovementStatus();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
		try {

			siloMovementStatus.setId(siloMovementStatusModel.getAppId());
			siloMovementStatus.setDatetimeEnd((null != siloMovementStatusModel.getDatetimeEnd() && !"".equals(siloMovementStatusModel
					.getDatetimeEnd())) ? simpleDateFormat.parse(siloMovementStatusModel.getDatetimeEnd() + DATETIME_APPENDER) : null);
			siloMovementStatus.setDatetimeStart((null != siloMovementStatusModel.getDatetimeStart() && !"".equals(siloMovementStatusModel
					.getDatetimeStart())) ? simpleDateFormat.parse(siloMovementStatusModel.getDatetimeStart() + DATETIME_APPENDER) : null);
			siloMovementStatus.setErrMsg(siloMovementStatusModel.getErrMsg());
			siloMovementStatus.setErrState(siloMovementStatusModel.getErrState());
			siloMovementStatus.setFromSilo(siloMovementStatusModel.getFromSilo());
			siloMovementStatus.setLastSuccState(siloMovementStatusModel.getLastSuccState());
			siloMovementStatus.setNewCis(siloMovementStatusModel.getNewCis());
			siloMovementStatus.setOldCis(siloMovementStatusModel.getOldCis());
			siloMovementStatus.setToSilo(siloMovementStatusModel.getToSilo());
			siloMovementStatus.setUserId(siloMovementStatusModel.getUserId());
		} catch (ParseException e) {
			logger.error("Error parsing the date: " + siloMovementStatusModel.getDatetimeEnd() + " or " + siloMovementStatusModel.getDatetimeStart());
		}
		return siloMovementStatus;

	}

	private SiloMovementStatus createSiloMovementStatusRequest(SiloMovementStatusModel reqModel, String method) {
		SiloMovementStatus siloMovementStatus = method.equals(UPDATE_METHOD) ? siloMovementStatusRepository.retrieve(reqModel.getAppId())
				: new SiloMovementStatus();
		switch (method) {
		case CREATE_METHOD:
			updateStatusForCreate(siloMovementStatus, reqModel);
			break;
		case UPDATE_METHOD:
			siloMovementStatus = updateStatusForUpdate(siloMovementStatus, reqModel);
			break;
		default:
			break;
		}
		return siloMovementStatus;
	}

	private SiloMovementStatus updateStatusForUpdate(SiloMovementStatus siloMovementStatus, SiloMovementStatusModel reqModel) {
		siloMovementStatus.setNewCis(reqModel.getNewCis());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
		try {
			siloMovementStatus.setDatetimeEnd(simpleDateFormat.parse(reqModel.getDatetimeEnd()));
			siloMovementStatus.setErrMsg(reqModel.getErrMsg());
			siloMovementStatus.setErrState(reqModel.getErrState());
			siloMovementStatus.setLastSuccState(reqModel.getLastSuccState());
		} catch (ParseException e) {
			logger.error("Error parsing the date: " + reqModel.getDatetimeEnd());
		}
		return siloMovementStatus;
	}

	private SiloMovementStatus updateStatusForCreate(SiloMovementStatus siloMovementStatus, SiloMovementStatusModel reqModel) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
		try {
			siloMovementStatus.setDatetimeStart(simpleDateFormat.parse(reqModel.getDatetimeStart()));
			siloMovementStatus.setOldCis(reqModel.getOldCis());
			siloMovementStatus.setFromSilo(reqModel.getFromSilo());
			siloMovementStatus.setToSilo(reqModel.getToSilo());
			siloMovementStatus.setUserId(reqModel.getUserId());
		} catch (ParseException e) {
			logger.error("Error parsing the date: " + reqModel.getDatetimeStart());
		}
		return siloMovementStatus;
	}

	private SiloMovementStatusModel toSiloMovementStatusModel(SiloMovementStatus siloMovementStatus) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
		SiloMovementStatusModel siloMovementStatusModel = new SiloMovementStatusModel();
		siloMovementStatusModel.setAppId(siloMovementStatus.getId());
		siloMovementStatusModel.setDatetimeEnd(null != siloMovementStatus.getDatetimeEnd() ? simpleDateFormat.format(siloMovementStatus
				.getDatetimeEnd()) : null);
		siloMovementStatusModel.setDatetimeStart(simpleDateFormat.format(siloMovementStatus.getDatetimeStart()));
		siloMovementStatusModel.setErrMsg(siloMovementStatus.getErrMsg());
		siloMovementStatusModel.setErrState(siloMovementStatus.getErrState());
		siloMovementStatusModel.setFromSilo(siloMovementStatus.getFromSilo());
		siloMovementStatusModel.setLastSuccState(siloMovementStatus.getLastSuccState());
		siloMovementStatusModel.setNewCis(siloMovementStatus.getNewCis());
		siloMovementStatusModel.setOldCis(siloMovementStatus.getOldCis());
		siloMovementStatusModel.setToSilo(siloMovementStatus.getToSilo());
		siloMovementStatusModel.setUserId(siloMovementStatus.getUserId());
		return siloMovementStatusModel;
	}

	private List<SiloMovementStatusModel> toSiloMovementStatusModel(List<SiloMovementStatus> siloMovementStatusList) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
		List<SiloMovementStatusModel> siloMovementStatusModelList = new ArrayList<SiloMovementStatusModel>();
		for (SiloMovementStatus siloMovementStatus : siloMovementStatusList) {
			SiloMovementStatusModel siloMovementStatusModel = new SiloMovementStatusModel();
			siloMovementStatusModel.setAppId(siloMovementStatus.getId());
			siloMovementStatusModel.setDatetimeEnd(null != siloMovementStatus.getDatetimeEnd() ? simpleDateFormat.format(siloMovementStatus
					.getDatetimeEnd()) : null);
			siloMovementStatusModel.setDatetimeStart(simpleDateFormat.format(siloMovementStatus.getDatetimeStart()));
			siloMovementStatusModel.setErrMsg(siloMovementStatus.getErrMsg());
			siloMovementStatusModel.setErrState(siloMovementStatus.getErrState());
			siloMovementStatusModel.setFromSilo(siloMovementStatus.getFromSilo());
			siloMovementStatusModel.setLastSuccState(siloMovementStatus.getLastSuccState());
			siloMovementStatusModel.setNewCis(siloMovementStatus.getNewCis());
			siloMovementStatusModel.setOldCis(siloMovementStatus.getOldCis());
			siloMovementStatusModel.setToSilo(siloMovementStatus.getToSilo());
			siloMovementStatusModel.setUserId(siloMovementStatus.getUserId());
			siloMovementStatusModelList.add(siloMovementStatusModel);
		}
		return siloMovementStatusModelList;
	}
}
