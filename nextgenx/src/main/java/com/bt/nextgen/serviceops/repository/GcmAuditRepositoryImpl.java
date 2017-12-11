package com.bt.nextgen.serviceops.repository;

import com.bt.nextgen.draftaccount.repository.ClientApplication;
import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by l069679 on 9/02/2017.
 */
@Repository("gcmAuditRepository")
public class GcmAuditRepositoryImpl implements GcmAuditRepository {

    private static final Logger logger = LoggerFactory.getLogger(GcmAuditRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(value = "springJpaTransactionManager")
    public void logAuditEntry(String userId, String reqType, String reqMsg) {
        logger.info("Making GCM Operation audit log entry to GCM_AUDIT_TRAIL table");
        String silo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getParameter("silo");
        GcmOpsAuditTrail gcmOpsAuditTrail = new GcmOpsAuditTrail(userId,silo,reqType,reqMsg);
        entityManager.persist(gcmOpsAuditTrail);
        entityManager.flush();
    }
}
