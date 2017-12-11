/**
 * 
 */
package com.bt.nextgen.api.client.service;

import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.serviceops.repository.ServiceOpsAuditLog;
import com.bt.nextgen.serviceops.repository.ServiceOpsAuditRepositoryImpl;

/**
 * @author l081050
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceOpsAuditServiceImplTest {
    @InjectMocks
    private ServiceOpsAuditServiceImpl serviceOpsAuditServiceImpl;
    @Mock
    private ServiceOpsAuditRepositoryImpl serviceOpsAuditRepositoryImpl;
    @Test
    public void testCreate() {
        ServiceOpsAuditLog serviceOpsAuditLog = new ServiceOpsAuditLog();
        serviceOpsAuditLog.setUserId("L081050");
        serviceOpsAuditLog.setMessage("test");
        serviceOpsAuditLog.setTimeStamp(new Date());
        serviceOpsAuditLog.setAction("test");
        when(serviceOpsAuditRepositoryImpl.create(serviceOpsAuditLog)).thenReturn(serviceOpsAuditLog);
        serviceOpsAuditServiceImpl.createLog("userid", "test", "test");
    }
}
