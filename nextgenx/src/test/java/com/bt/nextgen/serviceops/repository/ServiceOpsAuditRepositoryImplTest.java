/**
 * 
 */
package com.bt.nextgen.serviceops.repository;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bt.nextgen.config.BaseSecureIntegrationTest;

/**
 * @author l081050
 *
 */
public class ServiceOpsAuditRepositoryImplTest extends BaseSecureIntegrationTest{
    @Autowired
    @Qualifier("serviceOpsAudiLogRepository")
    private ServiceOpsAuditRepository serviceOpsAudiLogRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testCreate() {
        ServiceOpsAuditLog serviceOpsAuditLog = new ServiceOpsAuditLog();
        serviceOpsAuditLog.setUserId("L081050");
        serviceOpsAuditLog.setMessage("test");
        serviceOpsAuditLog.setTimeStamp(new Date());
        serviceOpsAuditLog.setAction("test");
        ServiceOpsAuditLog serviceOpsAuditLogRes = serviceOpsAudiLogRepository.create(serviceOpsAuditLog);
        assertTrue(serviceOpsAuditLogRes.getUserId().equals(serviceOpsAuditLogRes.getUserId()));
    }
}
