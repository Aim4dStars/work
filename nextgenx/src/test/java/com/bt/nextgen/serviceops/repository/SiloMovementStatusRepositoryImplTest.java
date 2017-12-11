package com.bt.nextgen.serviceops.repository;

import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bt.nextgen.config.BaseSecureIntegrationTest;

/**
 * Created by l069679 on 14/02/2017.
 */

public class SiloMovementStatusRepositoryImplTest extends BaseSecureIntegrationTest {

	@Autowired
	@Qualifier("siloMovementStatusRepositoryImpl")
	private SiloMovementStatusRepository siloMovementStatusRepository;

	@PersistenceContext
	private EntityManager entityManager;
	
	@Test
	public void testCreate() {
		SiloMovementStatus siloMovementStatusRequest = getSiloMovementStatus();
		SiloMovementStatus siloMovementStatus = siloMovementStatusRepository.create(siloMovementStatusRequest);
		assertTrue(siloMovementStatus.getOldCis().equals(siloMovementStatusRequest.getOldCis()));
	}

	@Test
	public void testUpdate() {
		SiloMovementStatus siloMovementStatusRequest = getSiloMovementStatus();
		SiloMovementStatus siloMovementStatus2 = siloMovementStatusRepository.create(siloMovementStatusRequest);
		siloMovementStatus2.setErrMsg("Error while updating service 256");
		siloMovementStatus2.setErrState("MNT256_CRT");
		siloMovementStatus2.setLastSuccState(null);
		SiloMovementStatus siloMovementStatus = siloMovementStatusRepository.update(siloMovementStatus2);

		assertTrue(siloMovementStatus.getErrState().equals(siloMovementStatus2.getErrState()));
	}

	@Test
	public void testRetrieve() {
		SiloMovementStatus siloMovementStatusRequest = getSiloMovementStatus();
		SiloMovementStatus siloMovementStatus2 = siloMovementStatusRepository.create(siloMovementStatusRequest);
		SiloMovementStatus siloMovementStatus = siloMovementStatusRepository.retrieve(siloMovementStatus2.getId());
		assertTrue(siloMovementStatus.getOldCis().equals(siloMovementStatusRequest.getOldCis()));
	}

	@Test
	public void testRetrieveAll() {
		SiloMovementStatus siloMovementStatusRequest = getSiloMovementStatus();
		siloMovementStatusRepository.create(siloMovementStatusRequest);
		SiloMovementStatus siloMovementStatus2 = getSiloMovementStatus();
		siloMovementStatusRepository.create(siloMovementStatus2);
		List<SiloMovementStatus> siloMovementStatus = siloMovementStatusRepository.retrieveAll(getSiloMovementStatus());
		assertTrue(!siloMovementStatus.isEmpty());
		assertTrue(siloMovementStatus.get(0).getOldCis().equals(siloMovementStatus2.getOldCis()));
	}
	
	@Test
	public void testRetrieveAllWithParameter() {
		SiloMovementStatus siloMovementStatusRequest = getSiloMovementStatus();
		siloMovementStatusRepository.create(siloMovementStatusRequest);
		SiloMovementStatus siloMovementStatus2 = getSiloMovementStatus();
		siloMovementStatusRepository.create(siloMovementStatus2);
		
		List<SiloMovementStatus> siloMovementStatusResult = siloMovementStatusRepository.retrieveAll(getSiloMovementStatusSettingParameter());
		assertTrue(!siloMovementStatusResult.isEmpty());
		assertTrue(siloMovementStatusResult.get(0).getOldCis().equals(siloMovementStatus2.getOldCis()));
	}
	
	@Test
	@Ignore
	public void testRetrieveAllWithoutParameter() {
		SiloMovementStatus siloMovementStatusRequest = getSiloMovementStatus();
		siloMovementStatusRepository.create(siloMovementStatusRequest);
		SiloMovementStatus siloMovementStatus2 = getSiloMovementStatus();
		siloMovementStatusRepository.create(siloMovementStatus2);
		
		List<SiloMovementStatus> siloMovementStatusResult = siloMovementStatusRepository.retrieveAll(new SiloMovementStatus());
		assertTrue(!siloMovementStatusResult.isEmpty());
		assertTrue(siloMovementStatusResult.get(0).getOldCis().equals(siloMovementStatus2.getOldCis()));
	}

	private SiloMovementStatus getSiloMovementStatus() {
		SiloMovementStatus siloMovementStatus = new SiloMovementStatus();
		siloMovementStatus.setErrMsg("");
		siloMovementStatus.setErrState("");
		siloMovementStatus.setFromSilo("WPAC");
		siloMovementStatus.setLastSuccState("MNT256_END");
		siloMovementStatus.setNewCis("31936320070");
		siloMovementStatus.setOldCis("12345678905");
		siloMovementStatus.setToSilo("BTPL");
		siloMovementStatus.setUserId("CS057462");

		return siloMovementStatus;
	}
	
	private SiloMovementStatus getSiloMovementStatusSettingParameter() {
		SiloMovementStatus siloMovementStatus = new SiloMovementStatus();
		siloMovementStatus.setId((long) 124);
		siloMovementStatus.setErrState("MNT256");
		siloMovementStatus.setFromSilo("WPAC");
		siloMovementStatus.setNewCis("32145678901");
		siloMovementStatus.setOldCis("12345678905");
		siloMovementStatus.setToSilo("BTFG");
		siloMovementStatus.setUserId("CS12345");

		return siloMovementStatus;
	}
}
