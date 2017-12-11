package com.bt.nextgen.core.repository;

import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository("corporateActionDraftParticipationRepository")
public class CorporateActionDraftParticipationRepositoryImpl implements CorporateActionSavedParticipationRepository {
	//	private static final Logger logger = LoggerFactory.getLogger(CorporateActionDraftParticipationRepositoryImp.class);

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Return CorporateActionDraftElection object for the specified account
	 *
	 * @param oeId        the OE ID the CA options saved against
	 * @param orderNumber the CA ID
	 * @return the CorporateActionDraftElection for the account
	 */
	@Override
	public CorporateActionSavedParticipation find(String oeId, String orderNumber) {
		return entityManager.find(CorporateActionDraftParticipationImpl.class, new CorporateActionSavedParticipationKey(oeId, orderNumber));
	}

	/**
	 * Retrieve saved CA options that has expired
	 *
	 * @param oeId       the organisation ID the CA options saved against
	 * @param expiryDate the expiry date
	 * @return a list of CorporateActionDraftElection
	 */
	private List<CorporateActionSavedParticipation> findAllExpired(String oeId, DateTime expiryDate) {
		final TypedQuery<CorporateActionSavedParticipation> query = entityManager
				.createQuery(
						"SELECT a FROM CorporateActionDraftParticipationImpl a WHERE a.key.oeId = :oeId AND a.expiryDate < :expiryDate",
						CorporateActionSavedParticipation.class);

		query.setParameter("oeId", oeId);
		query.setParameter("expiryDate", expiryDate.toDate());

		return query.getResultList();
	}

	/**
	 * Insert CorporateActionDraftElection
	 *
	 * @param participation the CA selected option object to save
	 * @return the same object passed to this method
	 */
	@Override
	@Transactional(value = "springJpaTransactionManager")
	public CorporateActionSavedParticipation insert(CorporateActionSavedParticipation participation) {
		entityManager.persist(participation);
		entityManager.flush();
		return participation;
	}

	@Override
	@Transactional(value = "springJpaTransactionManager")
	public int delete(CorporateActionSavedParticipation participation) {
		CorporateActionDraftParticipationImpl ref = entityManager.getReference(CorporateActionDraftParticipationImpl.class,
				participation.getKey());

		deleteDraftAccountElections(ref.getKey().getOeId(), ref.getKey().getOrderNumber());
		deleteDraftAccounts(ref.getKey().getOeId(), ref.getKey().getOrderNumber());
		deleteDraftParticipation(ref.getKey().getOeId(), ref.getKey().getOrderNumber());

		// entityManager.remove(ref);

		entityManager.flush();

		return 1;
	}

	/**
	 * Update a specific corporate action selected option
	 *
	 * @param participation
	 * @return the same object passed to this method
	 */
	@Override
	@Transactional(value = "springJpaTransactionManager")
	public CorporateActionSavedParticipation update(CorporateActionSavedParticipation participation) {
		entityManager.merge(participation);
		entityManager.flush();
		return participation;
	}

	/**
	 * Delete old/expired saved elections for house-keeping
	 *
	 * @param oeId the oe ID the CA options saved against
	 * @return number of records deleted
	 */
	@Override
	@Transactional(value = "springJpaTransactionManager")
	public int deleteAllExpired(String oeId) {
		final DateTime currentDate = new DateTime();
		final DateTime expiryDate = currentDate.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);

		// Unable to automatically cascade delete for some reason, hence removing each one manually
		List<CorporateActionSavedParticipation> expiredDraftParticipations =
				(List<CorporateActionSavedParticipation>) findAllExpired(oeId, expiryDate);
		int deleteCount = 0;

		if (expiredDraftParticipations != null) {
			for (CorporateActionSavedParticipation participation : expiredDraftParticipations) {
				deleteDraftAccountElections(participation.getKey().getOeId(), participation.getKey().getOrderNumber());
				deleteDraftAccounts(participation.getKey().getOeId(), participation.getKey().getOrderNumber());
				deleteDraftParticipation(participation.getKey().getOeId(), participation.getKey().getOrderNumber());
				deleteCount++;
			}
		}

		entityManager.flush();

		return deleteCount;
	}

	private int deleteDraftParticipation(String oeId, String orderNumber) {
		final Query query = entityManager
				.createQuery(
						"DELETE FROM CorporateActionDraftParticipationImpl a WHERE a.key.oeId = :oeId AND a.key.orderNumber = :orderNumber");

		query.setParameter("oeId", oeId);
		query.setParameter("orderNumber", orderNumber);

		return query.executeUpdate();
	}

	private int deleteDraftAccounts(String oeId, String orderNumber) {
		final Query query = entityManager
				.createQuery(
						"DELETE FROM CorporateActionDraftAccountImpl a WHERE a.key.oeId = :oeId AND a.key.orderNumber = :orderNumber");

		query.setParameter("oeId", oeId);
		query.setParameter("orderNumber", orderNumber);

		return query.executeUpdate();
	}

	private int deleteDraftAccountElections(String oeId, String orderNumber) {
		final Query query = entityManager
				.createQuery(
						"DELETE FROM CorporateActionDraftAccountElectionImpl a WHERE a.key.oeId = :oeId AND a.key.orderNumber = :orderNumber");

		query.setParameter("oeId", oeId);
		query.setParameter("orderNumber", orderNumber);

		return query.executeUpdate();
	}
}
