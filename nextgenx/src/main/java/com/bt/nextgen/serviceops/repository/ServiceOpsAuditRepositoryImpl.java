/**
 * 
 */
package com.bt.nextgen.serviceops.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author l081050
 *
 */
@Repository("serviceOpsAudiLogRepository")
public class ServiceOpsAuditRepositoryImpl implements ServiceOpsAuditRepository{
    private static final Logger logger = LoggerFactory.getLogger(ServiceOpsAuditRepositoryImpl.class);
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(value = "springJpaTransactionManager")
    @Override
    public ServiceOpsAuditLog create(ServiceOpsAuditLog serviceOpsAuditLog) {
        logger.info("Making entry to SERVICEOPS_AUDIT_LOG table");
        ServiceOpsAuditLog sa = serviceOpsAuditLog;
        entityManager.persist(sa);
        entityManager.flush();
        return serviceOpsAuditLog;
    }

}
