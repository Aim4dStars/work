package com.bt.nextgen.core;

import java.util.List;

/**
 * Interface to get Cache status and external services status(OffThreadImplementation).
 */
public interface IServiceStatus {
    List<com.btfin.panorama.service.client.status.ServiceStatus> getServiceStatus();
    boolean checkCacheStatus();
}
