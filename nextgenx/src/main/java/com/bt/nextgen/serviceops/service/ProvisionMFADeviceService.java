package com.bt.nextgen.serviceops.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.serviceops.model.ProvisionMFARequestData;

/**
 * Created by L069552 on 6/11/17.
 */
public interface ProvisionMFADeviceService {

    boolean provisionMFADevice(ProvisionMFARequestData provisionMFARequestData, ServiceErrors serviceErrors);
}