/**
 * 
 */
package com.bt.nextgen.api.client.service;

import com.bt.nextgen.serviceops.repository.ServiceOpsAuditLog;

/**
 * @author l081050
 *
 */
public interface ServiceOpsAuditService {
    
    public void createLog(String userId, String action, String message);

}
