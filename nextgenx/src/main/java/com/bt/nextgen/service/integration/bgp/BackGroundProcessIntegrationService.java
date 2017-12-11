package com.bt.nextgen.service.integration.bgp;

import com.bt.nextgen.service.ServiceErrors;
import org.joda.time.DateTime;

import java.util.List;

public interface BackGroundProcessIntegrationService {

    List<BackGroundProcess> getBackGroundProcesses(ServiceErrors serviceErrors);

    DateTime getCurrentTime(ServiceErrors serviceErrors);

}
