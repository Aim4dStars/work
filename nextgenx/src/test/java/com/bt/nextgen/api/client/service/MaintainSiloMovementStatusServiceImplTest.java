/**
 * 
 */
package com.bt.nextgen.api.client.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.serviceops.model.SiloMovementStatusModel;
import com.bt.nextgen.serviceops.repository.SiloMovementStatus;
import com.bt.nextgen.serviceops.repository.SiloMovementStatusRepositoryImpl;

/**
 * @author l081050
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class MaintainSiloMovementStatusServiceImplTest {
	@InjectMocks
	private MaintainSiloMovementStatusServiceImpl maintainSiloMovementStatusService;
	@Mock
	private SiloMovementStatusRepositoryImpl siloMovementStatusRepository;

	@Test
	public void testCreate() {
		SiloMovementStatus siloMovementStatus = new SiloMovementStatus();
		siloMovementStatus.setDatetimeEnd(new Date());
		siloMovementStatus.setDatetimeStart(new Date());
		SiloMovementStatusModel siloMovementStatusModel = new SiloMovementStatusModel();
		siloMovementStatusModel.setFromSilo("WPAC");
		siloMovementStatusModel.setToSilo("BTPL");
		siloMovementStatusModel.setOldCis("12345678905");
		siloMovementStatusModel.setDatetimeEnd("2017-11-11 11:11:11.111");
		siloMovementStatusModel.setDatetimeStart("2017-11-11 11:11:11.111");
		when(siloMovementStatusRepository.create(any(SiloMovementStatus.class))).thenReturn(siloMovementStatus);

		SiloMovementStatusModel statusModel = maintainSiloMovementStatusService.create(siloMovementStatusModel);
		assertNotNull(statusModel);
	}

	@Test
	public void testUpdate() {
		SiloMovementStatus siloMovementStatus = new SiloMovementStatus();
		siloMovementStatus.setDatetimeEnd(new Date());
		siloMovementStatus.setDatetimeStart(new Date());
		siloMovementStatus.setNewCis("12345678905");
		SiloMovementStatusModel siloMovementStatusModel = new SiloMovementStatusModel();
		siloMovementStatusModel.setFromSilo("WPAC");
		siloMovementStatusModel.setAppId(new Long(12344));
		siloMovementStatusModel.setToSilo("BTPL");
		siloMovementStatusModel.setOldCis("12345678905");
		siloMovementStatusModel.setDatetimeEnd("2017-11-11 11:11:11.111");
		siloMovementStatusModel.setDatetimeStart("2017-11-11 11:11:11.111");
		siloMovementStatusModel.setNewCis("12345678905");
		when(siloMovementStatusRepository.retrieve(any(Long.class))).thenReturn(siloMovementStatus);
		when(siloMovementStatusRepository.update(any(SiloMovementStatus.class))).thenReturn(siloMovementStatus);

		SiloMovementStatusModel statusModel = maintainSiloMovementStatusService.update(siloMovementStatusModel);
		assertNotNull(statusModel);
	}

	@Test
	public void testRetrive() {
		List<SiloMovementStatus> SiloMovementStatusList = new ArrayList<SiloMovementStatus>();
		SiloMovementStatus siloMovementStatus = new SiloMovementStatus();
		siloMovementStatus.setDatetimeEnd(new Date());
		siloMovementStatus.setDatetimeStart(new Date());
		SiloMovementStatusList.add(siloMovementStatus);
		SiloMovementStatusModel siloMovementStatusModel = new SiloMovementStatusModel();
		siloMovementStatusModel.setAppId(new Long(12344));
		siloMovementStatusModel.setFromSilo("WPAC");
		siloMovementStatusModel.setToSilo("BTPL");
		siloMovementStatusModel.setOldCis("12345678905");
		siloMovementStatusModel.setDatetimeEnd("2017-11-11 11:11:11.111");
		siloMovementStatusModel.setDatetimeStart("2017-11-11 11:11:11.111");

		when(siloMovementStatusRepository.retrieveAll(any(SiloMovementStatus.class))).thenReturn(SiloMovementStatusList);

		List<SiloMovementStatusModel> list = maintainSiloMovementStatusService.retrieveAll(siloMovementStatusModel);
		assertNotNull(list);
	}
}
