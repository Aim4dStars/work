package com.bt.nextgen.api.corporateaction.v1.service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionPersistenceDto;
import com.bt.nextgen.service.ServiceErrors;
import org.springframework.stereotype.Service;


@Service
public class CorporateActionRoaPersistenceDtoServiceImpl implements CorporateActionRoaPersistenceDtoService {
	/**
	 * Generate ROA
	 *
	 * @param corporateActionPersistenceDto the complete CorporateActionPersistenceDto object
	 * @param serviceErrors                 the service errors
	 * @return corporateActionPersistenceDto
	 */
	@Override
	public CorporateActionPersistenceDto submit(CorporateActionPersistenceDto corporateActionPersistenceDto, ServiceErrors serviceErrors) {
		//	return generateROAs(corporateActionPersistenceDto, persistenceDto);
		return corporateActionPersistenceDto;
	}

	// TODO: send to Avaloq for generation once the Avaloq piece is done
	// TODO: send to Notification system
	//	private CorporateActionPersistenceDto generateROAs(CorporateActionPersistenceDto corporateActionPersistenceDto,
	//													   CorporateActionPersistenceDto persistenceDto) {
	//		return persistenceDto;
	//	}
}
