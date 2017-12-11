package com.bt.nextgen.core.web.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.btfin.panorama.service.client.status.ServiceStatus;

import java.util.List;

/**
 * Created by F058391 on 22/03/2017.
 */
public class ApplicationServiceStatusDto extends BaseDto {
    private final String serviceName;
    private final String serviceStatus;
    private final List<ServiceStatus> serviceStatuses;

    public ApplicationServiceStatusDto(String serviceName, String serviceStatus, List<ServiceStatus> serviceStatuses) {
        this.serviceName = serviceName;
        this.serviceStatus = serviceStatus;
        this.serviceStatuses = serviceStatuses;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public List<ServiceStatus> getCacheStatuses() {
        return serviceStatuses;
    }
}
