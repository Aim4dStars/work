package com.bt.nextgen.serviceops.repository;

import java.util.List;

/**
 * Created by L091297 on 08/06/2017.
 */

public interface SiloMovementStatusRepository {
	
	public SiloMovementStatus create(SiloMovementStatus siloMovementStatus);

	public SiloMovementStatus update(SiloMovementStatus siloMovementStatus);

	public SiloMovementStatus retrieve(Long oldCisKey);

	public List<SiloMovementStatus> retrieveAll(SiloMovementStatus siloMovementStatus);

}
