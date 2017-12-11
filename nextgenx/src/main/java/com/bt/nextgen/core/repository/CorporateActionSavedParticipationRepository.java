package com.bt.nextgen.core.repository;


public interface CorporateActionSavedParticipationRepository {
	CorporateActionSavedParticipation find(String oeId, String orderNumber);

	CorporateActionSavedParticipation insert(CorporateActionSavedParticipation participation);

	CorporateActionSavedParticipation update(CorporateActionSavedParticipation participation);

	int delete(CorporateActionSavedParticipation participation);

	int deleteAllExpired(String oeId);
}
