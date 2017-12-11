package com.bt.nextgen.serviceops.repository;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by l091297 on 02/06/2017.
 */
@Repository("siloMovementStatusRepositoryImpl")
public class SiloMovementStatusRepositoryImpl implements SiloMovementStatusRepository {

	private static final Logger logger = LoggerFactory.getLogger(SiloMovementStatusRepositoryImpl.class);
private static final String SD_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	private static long ID = 1;


	@PersistenceContext
	private EntityManager entityManager;

	@Transactional(value = "springJpaTransactionManager")
	@Override
	public SiloMovementStatus create(SiloMovementStatus siloMovementStatus) {
		logger.info("Making entry to SILO_MOVEMENT_STATUS table");
		SiloMovementStatus sm = siloMovementStatus;
		entityManager.persist(sm);
		entityManager.flush();
		return siloMovementStatus;
	}

	@Transactional(value = "springJpaTransactionManager")
	@Override
	public SiloMovementStatus update(SiloMovementStatus siloMovementStatus) {
		logger.info("Updating to SILO_MOVEMENT_STATUS table");
		entityManager.merge(siloMovementStatus);
		entityManager.flush();
		return siloMovementStatus;
	}

	@Override
	public SiloMovementStatus retrieve(Long id) {

		logger.info("Retrieving from SILO_MOVEMENT_STATUS table");
		Query query = entityManager.createQuery("from SiloMovementStatus S where ID = :id").setParameter("id", id);
		return (SiloMovementStatus) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SiloMovementStatus> retrieveAll(SiloMovementStatus siloMovementStatus) {
		logger.info("Retrieving from SILO_MOVEMENT_STATUS table");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SD_FORMAT);
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<SiloMovementStatus> cq = cb.createQuery(SiloMovementStatus.class);
		Root<SiloMovementStatus> siloMovementStatusQuery = cq.from(SiloMovementStatus.class);

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (null != siloMovementStatus.getOldCis() && !siloMovementStatus.getOldCis().isEmpty()) {
			predicates.add(cb.equal(siloMovementStatusQuery.get("oldCis"), siloMovementStatus.getOldCis()));
		}
		if (null != siloMovementStatus.getDatetimeEnd() && null != siloMovementStatus.getDatetimeStart()) {
			predicates.add(cb.greaterThanOrEqualTo(siloMovementStatusQuery.<Date> get("datetimeStart"), new Timestamp(siloMovementStatus
					.getDatetimeStart().getTime())));
			
			// Increased end date by one so that we can ignore time from database.  
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(siloMovementStatus.getDatetimeEnd());
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			
			predicates.add(cb.lessThanOrEqualTo(siloMovementStatusQuery.<Date> get("datetimeEnd"), new Timestamp(calendar.getTime().getTime())));
		}
		List<SiloMovementStatus> results = new ArrayList<SiloMovementStatus>();
		if (predicates.isEmpty()) {
			Timestamp startDate = null;
			Timestamp endDate = null;
			Calendar calEndDate = Calendar.getInstance();
			calEndDate.add(Calendar.MONTH, -1);

			Calendar calStartDate = Calendar.getInstance();
			calStartDate.add(Calendar.DAY_OF_MONTH, 1);

			endDate = new Timestamp(null != siloMovementStatus.getDatetimeEnd() ? siloMovementStatus.getDatetimeEnd().getTime() : calStartDate
					.getTime().getTime());
			startDate = new Timestamp(null != siloMovementStatus.getDatetimeStart() ? siloMovementStatus.getDatetimeStart().getTime() : calEndDate
					.getTime().getTime());

			results = entityManager.createQuery(
					"from SiloMovementStatus where datetimeStart BETWEEN to_timestamp('" + simpleDateFormat.format(startDate)
							+ "', 'YYYY-MM-DD HH24:MI:SS.FF') and to_timestamp('" + simpleDateFormat.format(endDate)
							+ "', 'YYYY-MM-DD HH24:MI:SS.FF') ORDER BY id asc").getResultList();
			return results;
		}

		cq.select(siloMovementStatusQuery).where(predicates.toArray(new Predicate[] {}));
		cq.orderBy(cb.asc(siloMovementStatusQuery.get("id")));
		results = entityManager.createQuery(cq).getResultList();
		return results;
	}
}
