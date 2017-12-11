package com.bt.nextgen.api.client.service;

import java.util.List;

import com.bt.nextgen.serviceops.model.SiloMovementStatusModel;

public interface MaintainSiloMovementStatusService {

	public SiloMovementStatusModel create(SiloMovementStatusModel reqModel);

	public SiloMovementStatusModel update(SiloMovementStatusModel reqModel);

	public SiloMovementStatusModel retrieve(Long id);

	public List<SiloMovementStatusModel> retrieveAll(SiloMovementStatusModel siloMovementStatusModel);
}
