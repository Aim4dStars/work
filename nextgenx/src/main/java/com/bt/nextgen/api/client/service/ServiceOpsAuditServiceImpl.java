/**
 * 
 */
package com.bt.nextgen.api.client.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.serviceops.repository.ServiceOpsAuditLog;
import com.bt.nextgen.serviceops.repository.ServiceOpsAuditRepository;

/**
 * @author l081050
 *
 */
@Service("serviceOpsAuditService")
public class ServiceOpsAuditServiceImpl implements ServiceOpsAuditService{
    private static final Logger logger = LoggerFactory.getLogger(ServiceOpsAuditServiceImpl.class);
    @Autowired
    @Qualifier("serviceOpsAudiLogRepository")
    private ServiceOpsAuditRepository serviceOpsAudiLogRepository;
    @Override
    public void createLog(String userId, String action, String message) {
        logger.info("Inside serviceOpsAudiLogRepository create method");
        ServiceOpsAuditLog serviceOpsAuditLog = new ServiceOpsAuditLog();
        serviceOpsAuditLog.setUserId(userId);
        serviceOpsAuditLog.setMessage(message);
        serviceOpsAuditLog.setTimeStamp(new Date());
        serviceOpsAuditLog.setAction(action);
        serviceOpsAudiLogRepository.create(serviceOpsAuditLog);
    }

}
